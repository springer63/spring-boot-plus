package com.github.boot.framework.util.http;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.*;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.DefaultCookieSpec;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * HTTP工具类
 *
 * @author chenjianhui
 */
public class HttpUtils {

    private final static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * 核心线程处理数
     */
    //private static final int CORE_THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * 核心线程处理数
     */
    //private static final int MAX_THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 100;

    /**
     * 默认字符集
     */
    public final static String DEFALUT_ENCODING = "UTF-8";

    /**
     * 缓冲区大小
     */
    public final static int BUFFER_SIZE = 1024;

    /**
     * http最大连接数
     */
    private static final int MAX_CONNECTIONS = 50000;

    /**
     * 同主机最大http连接数
     */
    private static final int MAX_PER_ROUTE = 100;

    /**
     * 最大从连接池获取连接的最大等待时间，单位毫秒
     */
    private static final int MAX_CONNECTION_REQUEST_TIMEOUT = 30000;

    /**
     * tcp连接建立timeout，单位毫秒
     */
    private static final int MAX_CONNECTION_TIMEOUT = 30000;

    /**
     * 读取数据最大等待时间，单位毫秒
     */
    private static final int MAX_SOCKET_READ_TIMEOUT = 30000;

    /**
     * HTTP请求客户端
     */
    private static CloseableHttpClient httpClient;

    /**
     * Cookie存储器
     */
    private volatile static CookieStore cookieStore;

    /**
     * 请求对象
     */
    private HttpRequestBase request;

    /**
     * Post, put请求的参数
     */
    private EntityBuilder builder;

    /**
     * get, delete请求的参数
     */
    private URIBuilder uriBuilder;

    /**
     * 请求类型1-post, 2-get, 3-put, 4-delete
     */
    private int type;

    /**
     * 请求的相关配置
     */
    private Builder requestConfig;


    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(MAX_CONNECTIONS);
        cm.setDefaultMaxPerRoute(MAX_PER_ROUTE);
        cookieStore = new BasicCookieStore();
        CookieSpecProvider easySpecProvider = new CookieSpecProvider() {
            @Override
            public CookieSpec create(HttpContext context) {
                return new DefaultCookieSpec() {
                    @Override
                    public void validate(Cookie cookie, CookieOrigin origin)
                            throws MalformedCookieException {
                    }
                };
            }
        };
        Registry<CookieSpecProvider> r = RegistryBuilder.<CookieSpecProvider> create()
                .register(CookieSpecs.DEFAULT, new DefaultCookieSpecProvider())
                .register("easy", easySpecProvider).build();
        BasicCookieStore cookieStore = new BasicCookieStore();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(MAX_SOCKET_READ_TIMEOUT).
                setConnectionRequestTimeout(MAX_CONNECTION_REQUEST_TIMEOUT).setConnectTimeout(MAX_CONNECTION_TIMEOUT).build();
        httpClient = HttpClientBuilder.create().setRetryHandler(new StandardHttpRequestRetryHandler(0, false))
                .setDefaultRequestConfig(requestConfig).setDefaultCookieStore(cookieStore).setDefaultCookieSpecRegistry(r).setConnectionManager(cm).build();
        //ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE_THREAD_POOL_SIZE, MAX_THREAD_POOL_SIZE, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    private HttpUtils(HttpRequestBase request) {
        this.request = request;
        this.requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT);
        if (request instanceof HttpPost) {
            this.type = 1;
            this.builder = EntityBuilder.create().setParameters(new ArrayList<NameValuePair>());
        } else if (request instanceof HttpGet) {
            this.type = 2;
            this.uriBuilder = new URIBuilder(request.getURI());
        } else if (request instanceof HttpPut) {
            this.type = 3;
            this.builder = EntityBuilder.create().setParameters(new ArrayList<NameValuePair>());
        } else if (request instanceof HttpDelete) {
            this.type = 4;
            this.uriBuilder = new URIBuilder();
        }
    }

    public static String readStream(InputStream in, String encoding) {
        if (in == null) {
            return null;
        }
        try {
            InputStreamReader inReader = null;
            if (encoding == null) {
                inReader = new InputStreamReader(in, DEFALUT_ENCODING);
            } else {
                inReader = new InputStreamReader(in, encoding);
            }
            char[] buffer = new char[BUFFER_SIZE];
            int readLen = 0;
            StringBuffer sb = new StringBuffer();
            while ((readLen = inReader.read(buffer)) != -1) {
                sb.append(buffer, 0, readLen);
            }
            inReader.close();
            return sb.toString();
        } catch (IOException e) {
            logger.error("读取返回内容出错", e);
        }
        return null;
    }


    private static HttpUtils create(HttpRequestBase request) {
        return new HttpUtils(request);
    }

    /**
     * 创建post请求
     *
     * @param url 请求地址
     * @return
     */
    public static HttpUtils post(String url) {
        return create(new HttpPost(url));
    }

    /**
     * 创建get请求(不要再URL后面拼接参数)
     *
     * @param url 请求地址
     * @return
     */
    public static HttpUtils get(String url) {
        return create(new HttpGet(url));
    }

    /**
     * 创建put请求
     *
     * @param url 请求地址
     * @return
     */
    public static HttpUtils put(String url) {
        return create(new HttpPut(url));
    }

    /**
     * 创建delete请求
     *
     * @param url 请求地址
     * @return
     */
    public static HttpUtils delete(String url) {
        return create(new HttpDelete(url));
    }

    /**
     * 创建post请求
     *
     * @param uri 请求地址
     * @return
     */
    public static HttpUtils post(URI uri) {
        return create(new HttpPost(uri));
    }

    /**
     * 创建get请求
     *
     * @param uri 请求地址
     * @return
     */
    public static HttpUtils get(URI uri) {
        return create(new HttpGet(uri));
    }

    /**
     * 添加参数
     *
     * @param parameters
     * @return
     */
    public HttpUtils setParameters(final NameValuePair... parameters) {
        if (builder != null) {
            builder.setParameters(parameters);
        } else {
            uriBuilder.setParameters(Arrays.asList(parameters));
        }
        return this;
    }

    /**
     * 添加参数
     *
     * @param name
     * @param value
     * @return
     */
    public HttpUtils addParameter(final String name, final String value) {
        if (builder != null) {
            builder.getParameters().add(new BasicNameValuePair(name, value));
        } else {
            uriBuilder.addParameter(name, value);
        }
        return this;
    }

    /**
     * 添加参数
     *
     * @param parameters
     * @return
     */
    public HttpUtils addParameters(final NameValuePair... parameters) {
        if (builder != null) {
            builder.getParameters().addAll(Arrays.asList(parameters));
        } else {
            uriBuilder.addParameters(Arrays.asList(parameters));
        }
        return this;
    }

    /**
     * 设置请求参数,会覆盖之前的参数
     *
     * @param parameters
     * @return
     */
    public HttpUtils setParameters(final Map<String, String> parameters) {
        NameValuePair[] values = new NameValuePair[parameters.size()];
        int i = 0;

        for (Entry<String, String> parameter : parameters.entrySet()) {
            values[i++] = new BasicNameValuePair(parameter.getKey(), parameter.getValue());
        }
        setParameters(values);
        return this;
    }

    /**
     * 设置请求参数,会覆盖之前的参数
     *
     * @param file
     * @return
     */
    public HttpUtils setParameter(final File file) {
        if (builder != null) {
            builder.setFile(file);
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }

    /**
     * 设置请求参数,会覆盖之前的参数
     *
     * @param binary
     * @return
     */
    public HttpUtils setParameter(final byte[] binary) {
        if (builder != null) {
            builder.setBinary(binary);
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }

    /**
     * 设置请求参数,会覆盖之前的参数
     *
     * @param serializable
     * @return
     */
    public HttpUtils setParameter(final Serializable serializable) {
        if (builder != null) {
            builder.setSerializable(serializable);
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }

    /**
     * 设置参数为Json对象
     *
     * @param parameter 参数对象
     * @return
     */
    public HttpUtils setParameterJson(final Object parameter) {
        if (builder != null) {
            try {
                setContentType(ContentType.APPLICATION_JSON);
                builder.setBinary(objMapper.writeValueAsBytes(parameter));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }

    /**
     * 设置参数为Json对象
     *
     * @param parameter 参数对象
     * @return
     */
    public HttpUtils setParameterXml(final Object parameter) {
        if (builder != null) {
            try {
                setContentType(ContentType.APPLICATION_XML);
                builder.setBinary(xmlMapper.writeValueAsBytes(parameter));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }

    /**
     * 设置请求参数,会覆盖之前的参数
     *
     * @param stream
     * @return
     */
    public HttpUtils setParameter(final InputStream stream) {
        if (builder != null) {
            builder.setStream(stream);
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }

    /**
     * 设置请求参数,会覆盖之前的参数
     *
     * @param text
     * @return
     */
    public HttpUtils setParameter(final String text) {
        if (builder != null) {
            builder.setText(text);
        } else {
            uriBuilder.setParameters(URLEncodedUtils.parse(text, Consts.UTF_8));
        }
        return this;
    }

    /**
     * 设置内容编码
     *
     * @param encoding
     * @return
     */
    public HttpUtils setContentEncoding(final String encoding) {
        if (builder != null)
            builder.setContentEncoding(encoding);
        return this;
    }

    /**
     * 设置ContentType
     *
     * @param contentType
     * @return
     */
    public HttpUtils setContentType(ContentType contentType) {
        if (builder != null)
            builder.setContentType(contentType);
        return this;
    }

    /**
     * 设置ContentType
     *
     * @param mimeType
     * @param charset  内容编码
     * @return
     */
    public HttpUtils setContentType(final String mimeType, final Charset charset) {
        if (builder != null)
            builder.setContentType(ContentType.create(mimeType, charset));
        return this;
    }

    /**
     * 添加参数
     *
     * @param parameters
     * @return
     */
    public HttpUtils addParameters(Map<String, String> parameters) {
        List<NameValuePair> values = new ArrayList<>(parameters.size());
        for (Entry<String, String> parameter : parameters.entrySet()) {
            values.add(new BasicNameValuePair(parameter.getKey(), parameter.getValue()));
        }
        if (builder != null) {
            builder.getParameters().addAll(values);
        } else {
            uriBuilder.addParameters(values);
        }
        return this;
    }

    /**
     * 添加Header
     *
     * @param name
     * @param value
     * @return
     */
    public HttpUtils addHeader(String name, String value) {
        request.addHeader(name, value);
        return this;
    }

    /**
     * 添加Header
     *
     * @param headers
     * @return
     */
    public HttpUtils addHeaders(Map<String, String> headers) {
        for (Entry<String, String> header : headers.entrySet()) {
            request.addHeader(header.getKey(), header.getValue());
        }
        return this;
    }

    /**
     * 设置Header,会覆盖所有之前的Header
     *
     * @param headers
     * @return
     */
    public HttpUtils setHeaders(Map<String, String> headers) {
        Header[] headerArray = new Header[headers.size()];
        int i = 0;
        for (Entry<String, String> header : headers.entrySet()) {
            headerArray[i++] = new BasicHeader(header.getKey(), header.getValue());
        }
        request.setHeaders(headerArray);
        return this;
    }

    /**
     * 设置Header,会覆盖所有之前的Header
     *
     * @param headers
     * @return
     */
    public HttpUtils setHeaders(Header[] headers) {
        request.setHeaders(headers);
        return this;
    }

    /**
     * 获取所有Header
     *
     * @return
     */
    public Header[] getAllHeaders() {
        return request.getAllHeaders();
    }

    /**
     * 移除指定name的Header列表
     *
     * @param name
     */
    public HttpUtils removeHeaders(String name) {
        request.removeHeaders(name);
        return this;
    }

    /**
     * 移除指定的Header
     *
     * @param header
     */
    public HttpUtils removeHeader(Header header) {
        request.removeHeader(header);
        return this;
    }

    /**
     * 移除指定的Header
     *
     * @param name
     * @param value
     */
    public HttpUtils removeHeader(String name, String value) {
        request.removeHeader(new BasicHeader(name, value));
        return this;
    }

    /**
     * 是否存在指定name的Header
     *
     * @param name
     * @return
     */
    public boolean containsHeader(String name) {
        return request.containsHeader(name);
    }

    /**
     * 获取Header的迭代器
     *
     * @return
     */
    public HeaderIterator headerIterator() {
        return request.headerIterator();
    }

    /**
     * 获取协议版本信息
     *
     * @return
     */
    public ProtocolVersion getProtocolVersion() {
        return request.getProtocolVersion();
    }

    /**
     * 获取请求Url
     *
     * @return
     */
    public URI getURI() {
        return request.getURI();
    }

    /**
     * 设置请求Url
     *
     * @return
     */
    public HttpUtils setURI(URI uri) {
        request.setURI(uri);
        return this;
    }

    /**
     * 设置请求Url
     *
     * @return
     */
    public HttpUtils setURI(String uri) {
        return setURI(URI.create(uri));
    }

    /**
     * 设置一个CookieStore
     *
     * @param cookieStore
     * @return
     */
    public HttpUtils SetCookieStore(CookieStore cookieStore) {
        if (cookieStore == null)
            return this;
        HttpUtils.cookieStore = cookieStore;
        return this;
    }

    /**
     * 添加Cookie
     *
     * @param cookies
     * @return
     */
    public HttpUtils addCookie(Cookie... cookies) {
        if (cookies == null)
            return this;
        for (int i = 0; i < cookies.length; i++) {
            cookieStore.addCookie(cookies[i]);
        }
        return this;
    }

    /**
     * 设置Socket超时时间,单位:ms
     *
     * @param socketTimeout
     * @return
     */
    public HttpUtils setSocketTimeout(int socketTimeout) {
        requestConfig.setSocketTimeout(socketTimeout);
        return this;
    }

    /**
     * 设置连接超时时间,单位:ms
     *
     * @param connectTimeout
     * @return
     */
    public HttpUtils setConnectTimeout(int connectTimeout) {
        requestConfig.setConnectTimeout(connectTimeout);
        return this;
    }

    /**
     * 设置请求超时时间,单位:ms
     *
     * @param connectionRequestTimeout
     * @return
     */
    public HttpUtils setConnectionRequestTimeout(int connectionRequestTimeout) {
        requestConfig.setConnectionRequestTimeout(connectionRequestTimeout);
        return this;
    }

    /**
     * 执行请求
     *
     * @return
     */
    public ResponseWrap execute() {
        settingRequest();
        long startTime = System.currentTimeMillis();
        try {
            HttpClientContext context = HttpClientContext.create();
            context.setCookieStore(cookieStore);
            CloseableHttpResponse response = httpClient.execute(request, context);
            //HttpRequestFutureTask<HttpResponse> task = requestExeService.execute(request, context, responseHandler);
            return new ResponseWrap(httpClient, request, response, context, objMapper, xmlMapper);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            logger.info("请求接口：【{}】 耗时 {}", request.getURI(), System.currentTimeMillis() - startTime);
        }
    }

    /**
     * 关闭连接
     */
    public void shutdown() {
        request.releaseConnection();
    }

    private void settingRequest() {
        URI uri = null;
        if (uriBuilder != null) {
            try {
                uri = uriBuilder.build();
            } catch (URISyntaxException e) {
                logger.warn(e.getMessage(), e);
            }
        }
        HttpEntity httpEntity = null;
        switch (type) {
            case 1:
                httpEntity = builder.build();
                if (httpEntity.getContentLength() > 0)
                    ((HttpPost) request).setEntity(builder.build());
                break;

            case 2:
                HttpGet get = ((HttpGet) request);
                get.setURI(uri);
                break;

            case 3:
                httpEntity = builder.build();
                if (httpEntity.getContentLength() > 0)
                    ((HttpPut) request).setEntity(httpEntity);
                break;

            case 4:
                HttpDelete delete = ((HttpDelete) request);
                delete.setURI(uri);
                break;
        }
        request.setConfig(requestConfig.build());
    }

    private static ObjectMapper objMapper;

    private static XmlMapper xmlMapper;

    static {
        objMapper = new ObjectMapper();
        objMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);//设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        objMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        objMapper.enable(Feature.ALLOW_COMMENTS);
        objMapper.enable(Feature.ALLOW_SINGLE_QUOTES);

        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        xmlMapper = new XmlMapper(module);
        xmlMapper.setSerializationInclusion(Include.NON_DEFAULT);//设置序列化不包含Java对象中为空的属性
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

}
