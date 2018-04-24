package com.github.boot.framework.util.http;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.DefaultCookieSpec;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;

/**
 * HTTP工具类
 *
 * @author chenjianhui
 */
public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static final HttpUtils DEFAULT_INSTANCE = new HttpUtils();

    /**
     * 默认字符集
     */
    public final static String DEFAULT_ENCODING = "UTF-8";

    /**
     * http最大连接数
     */
    private static final int MAX_CONNECTIONS = 5000;

    /**
     * 链接到同主机最大http连接数
     */
    private static final int MAX_PER_ROUTE = 1000;

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
     * JSON转换工具对象
     */
    private static ObjectMapper objMapper;

    /**
     * XML转换对象
     */
    private static XmlMapper xmlMapper;

    /**
     * HTTP请求客户端
     */
    private CloseableHttpClient httpClient;

    /**
     * Cookie存储器
     */
    private CookieStore cookieStore;

    private ConnectionSocketFactory plainSF;

    private SSLContext sslContext;

    private ConnectionConfig connConfig;

    private SocketConfig socketConfig;

    private LayeredConnectionSocketFactory sslSF;

    private HttpUtils(){
        init(null, null);
    }

    private HttpUtils(String keyStorePath, String keyStorePass){
        init(keyStorePath, keyStorePass);
    }

    /**
     * 获取默认实例
     * @return
     */
    public static HttpUtils getInstance(){
        return DEFAULT_INSTANCE;
    }

    /**
     * 根据证书获取实例
     * @param keyStorePath
     * @param keyStorePass
     * @return
     */
    public static HttpUtils getInstance(String keyStorePath, String keyStorePass){
        return new HttpUtils(keyStorePath, keyStorePass);
    }

    /**
     * 初始化HttpClient
     * @param keyStorePath 证书地址
     * @param keyStorePass 证书密钥
     */
    private void init(String keyStorePath, String keyStorePass) {
        cookieStore = new BasicCookieStore();
        CookieSpecProvider easySpecProvider = (HttpContext context) -> new DefaultCookieSpec() {
            @Override
            public void validate(Cookie cookie, CookieOrigin origin)
                    throws MalformedCookieException {
            }
        };
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();
        plainSF = new PlainConnectionSocketFactory();
        registryBuilder.register("http", plainSF);
        if(keyStorePass != null && keyStorePath != null){
            sslContext = buildCustomSSLContext(keyStorePath, keyStorePass);
        }else{
            sslContext = buildDefaultSSLContext();
        }
        sslSF = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        registryBuilder.register("https", sslSF);
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registryBuilder.build());
        connManager.setMaxTotal(MAX_CONNECTIONS);
        connManager.setDefaultMaxPerRoute(MAX_PER_ROUTE);
        connConfig = ConnectionConfig.custom().setCharset(Charset.forName(DEFAULT_ENCODING)).build();
        socketConfig = SocketConfig.custom().setSoTimeout(100000).build();
        connManager.setDefaultConnectionConfig(connConfig);
        connManager.setDefaultSocketConfig(socketConfig);
        Registry<CookieSpecProvider> r = RegistryBuilder.<CookieSpecProvider> create()
                .register(CookieSpecs.DEFAULT, new DefaultCookieSpecProvider())
                .register("easy", easySpecProvider).build();
        BasicCookieStore cookieStore = new BasicCookieStore();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(MAX_SOCKET_READ_TIMEOUT).
                setConnectionRequestTimeout(MAX_CONNECTION_REQUEST_TIMEOUT).setConnectTimeout(MAX_CONNECTION_TIMEOUT).build();
        httpClient = HttpClientBuilder.create().setRetryHandler(new StandardHttpRequestRetryHandler(0, false))
                .setDefaultRequestConfig(requestConfig).setDefaultCookieStore(cookieStore).setDefaultCookieSpecRegistry(r).setConnectionManager(connManager).build();
    }

    /**
     * 设置信任自签名证书
     * @param keyStorePath      密钥库路径
     * @param keyStorePass      密钥库密码
     * @return
     */
    private SSLContext buildCustomSSLContext(String keyStorePath, String keyStorePass){
        InputStream in = null;
        try {
            KeyStore trustStore =  KeyStore.getInstance("PKCS12");
            in = HttpUtils.class.getClassLoader().getResourceAsStream(keyStorePath);
            trustStore.load(in, keyStorePass.toCharArray());
            sslContext = SSLContexts.custom().loadKeyMaterial(trustStore, keyStorePass.toCharArray()).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return sslContext;
    }

    /**
     * 设置信任所有签名证书
     * @return
     */
    private SSLContext buildDefaultSSLContext(){
        try {
            TrustManager manager = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }
            };
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{manager}, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sslContext;
    }

    /**
     * 创建request请求
     * @param request
     * @param httpUtils
     * @return
     */
    private RequestWrap create(HttpRequestBase request, HttpUtils httpUtils) {
        return new RequestWrap(request, cookieStore, objMapper, xmlMapper, httpUtils);
    }

    /**
     * 创建post请求
     *
     * @param url 请求地址
     * @return
     */
    public RequestWrap post(String url) {
        return create(new HttpPost(url), this);
    }

    /**
     * 创建get请求(不要再URL后面拼接参数)
     *
     * @param url 请求地址
     * @return
     */
    public RequestWrap get(String url) {
        return create(new HttpGet(url), this);
    }

    /**
     * 创建put请求
     *
     * @param url 请求地址
     * @return
     */
    public RequestWrap put(String url) {
        return create(new HttpPut(url), this);
    }

    /**
     * 创建delete请求
     *
     * @param url 请求地址
     * @return
     */
    public RequestWrap delete(String url) {
        return create(new HttpDelete(url), this);
    }

    /**
     * 创建post请求
     *
     * @param uri 请求地址
     * @return
     */
    public RequestWrap post(URI uri) {
        return create(new HttpPost(uri), this);
    }

    /**
     * 创建get请求
     *
     * @param uri 请求地址
     * @return
     */
    public RequestWrap get(URI uri) {
        return create(new HttpGet(uri), this);
    }

    /**
     * 执行请求
     *
     * @return
     */
    public ResponseWrap execute(RequestWrap request) {
        request.settingRequest();
        long startTime = System.currentTimeMillis();
        try {
            HttpClientContext context = HttpClientContext.create();
            context.setCookieStore(cookieStore);
            CloseableHttpResponse response = httpClient.execute(request.getRequest(), context);
            return new ResponseWrap(request.getRequest(), response, context, objMapper, xmlMapper);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            logger.info("请求接口：【{}】 耗时 {}", request.getURI(), System.currentTimeMillis() - startTime);
        }
    }

    static {
        objMapper = new ObjectMapper();
        //设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        objMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        objMapper.enable(Feature.ALLOW_COMMENTS);
        objMapper.enable(Feature.ALLOW_SINGLE_QUOTES);

        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        xmlMapper = new XmlMapper(module);
        //设置序列化不包含Java对象中为空的属性
        xmlMapper.setSerializationInclusion(Include.NON_DEFAULT);
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

}
