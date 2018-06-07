package com.github.boot.framework.util.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author chenjianhui
 * @create 2018/01/26
 **/
public class RequestWrap {

    private static final Logger logger = LoggerFactory.getLogger(RequestWrap.class);

    public static final int HTTP_METHOD_POST = 1;

    public static final int HTTP_METHOD_GET = 2;

    public static final int HTTP_METHOD_PUT = 3;

    public static final int HTTP_METHOD_DELETE = 4;

    /**
     * 请求方法 1-post, 2-get, 3-put, 4-delete
     */
    private int method;

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
     * Cookie持久化对象
     */
    private CookieStore cookieStore;

    /**
     * 请求的相关配置
     */
    private RequestConfig.Builder config;

    /**
     * JSON转换类
     */
    private ObjectMapper objMapper;

    /**
     * XML转换类
     */
    private XmlMapper xmlMapper;

    /**
     * HTTP请求执行对象
     */
    private HttpUtils httpUtils;

    public RequestWrap(HttpRequestBase request, CookieStore cookieStore, ObjectMapper objMapper, XmlMapper xmlMapper, HttpUtils httpUtils){
        this.httpUtils = httpUtils;
        this.request = request;
        this.cookieStore = cookieStore;
        this.xmlMapper = xmlMapper;
        this.objMapper = objMapper;
        this.config = RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT);
        if (request instanceof HttpPost) {
            this.method = HTTP_METHOD_POST;
            this.builder = EntityBuilder.create().setParameters(new ArrayList<>());
        } else if (request instanceof HttpGet) {
            this.method = HTTP_METHOD_GET;
            this.uriBuilder = new URIBuilder(request.getURI());
        } else if (request instanceof HttpPut) {
            this.method = HTTP_METHOD_PUT;
            this.builder = EntityBuilder.create().setParameters(new ArrayList<>());
        } else if (request instanceof HttpDelete) {
            this.method = HTTP_METHOD_DELETE;
            this.uriBuilder = new URIBuilder();
        }

    }

    /**
     * 添加参数
     *
     * @param parameters
     * @return
     */
    public RequestWrap setParameters(final NameValuePair... parameters) {
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
    public RequestWrap addParameter(final String name, final String value) {
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
    public RequestWrap addParameters(final NameValuePair... parameters) {
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
    public RequestWrap setParameters(final Map<String, String> parameters) {
        NameValuePair[] values = new NameValuePair[parameters.size()];
        int i = 0;

        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
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
    public RequestWrap setParameter(final File file) {
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
    public RequestWrap setParameter(final byte[] binary) {
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
    public RequestWrap setParameter(final Serializable serializable) {
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
    public RequestWrap setParameterJson(final Object parameter) {
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
    public RequestWrap setParameterXml(final Object parameter) {
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
    public RequestWrap setParameter(final InputStream stream) {
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
    public RequestWrap setParameter(final String text) {
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
    public RequestWrap setContentEncoding(final String encoding) {
        if (builder != null){
            builder.setContentEncoding(encoding);
        }
        return this;
    }

    /**
     * 设置ContentType
     *
     * @param contentType
     * @return
     */
    public RequestWrap setContentType(ContentType contentType) {
        if (builder != null){
            builder.setContentType(contentType);
        }
        return this;
    }

    /**
     * 设置ContentType
     *
     * @param mimeType
     * @param charset  内容编码
     * @return
     */
    public RequestWrap setContentType(final String mimeType, final Charset charset) {
        if (builder != null){
            builder.setContentType(ContentType.create(mimeType, charset));
        }
        return this;
    }

    /**
     * 添加参数
     *
     * @param parameters
     * @return
     */
    public RequestWrap addParameters(Map<String, String> parameters) {
        List<NameValuePair> values = new ArrayList<>(parameters.size());
        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
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
    public RequestWrap addHeader(String name, String value) {
        request.addHeader(name, value);
        return this;
    }

    /**
     * 添加Header
     *
     * @param headers
     * @return
     */
    public RequestWrap addHeaders(Map<String, String> headers) {
        for (Map.Entry<String, String> header : headers.entrySet()) {
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
    public RequestWrap setHeaders(Map<String, String> headers) {
        Header[] headerArray = new Header[headers.size()];
        int i = 0;
        for (Map.Entry<String, String> header : headers.entrySet()) {
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
    public RequestWrap setHeaders(Header[] headers) {
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
    public RequestWrap removeHeaders(String name) {
        request.removeHeaders(name);
        return this;
    }

    /**
     * 移除指定的Header
     *
     * @param header
     */
    public RequestWrap removeHeader(Header header) {
        request.removeHeader(header);
        return this;
    }

    /**
     * 移除指定的Header
     *
     * @param name
     * @param value
     */
    public RequestWrap removeHeader(String name, String value) {
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
    public RequestWrap setURI(URI uri) {
        request.setURI(uri);
        return this;
    }

    /**
     * 设置请求Url
     *
     * @return
     */
    public RequestWrap setURI(String uri) {
        return setURI(URI.create(uri));
    }

    /**
     * 添加Cookie
     *
     * @param cookies
     * @return
     */
    public RequestWrap addCookie(Cookie... cookies) {
        if (cookies == null){
            return this;
        }
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
    public RequestWrap setSocketTimeout(int socketTimeout) {
        config.setSocketTimeout(socketTimeout);
        return this;
    }

    /**
     * 设置连接超时时间,单位:ms
     *
     * @param connectTimeout
     * @return
     */
    public RequestWrap setConnectTimeout(int connectTimeout) {
        config.setConnectTimeout(connectTimeout);
        return this;
    }

    /**
     * 设置请求超时时间,单位:ms
     *
     * @param connectionRequestTimeout
     * @return
     */
    public RequestWrap setConnectionRequestTimeout(int connectionRequestTimeout) {
        config.setConnectionRequestTimeout(connectionRequestTimeout);
        return this;
    }

    /**
     * 关闭连接
     */
    public void shutdown() {
        request.releaseConnection();
    }

    public void settingRequest() {
        URI uri = null;
        if (uriBuilder != null) {
            try {
                uri = uriBuilder.build();
            } catch (URISyntaxException e) {
                logger.warn(e.getMessage(), e);
            }
        }
        HttpEntity httpEntity = null;
        switch (method) {
            case HTTP_METHOD_POST:
                httpEntity = builder.build();
                if (httpEntity.getContentLength() > 0){
                    ((HttpPost) request).setEntity(builder.build());
                }
                break;

            case HTTP_METHOD_GET:
                HttpGet get = ((HttpGet) request);
                get.setURI(uri);
                break;

            case HTTP_METHOD_PUT:
                httpEntity = builder.build();
                if (httpEntity.getContentLength() > 0){
                    ((HttpPut) request).setEntity(httpEntity);
                }
                break;

            case HTTP_METHOD_DELETE:
                HttpDelete delete = ((HttpDelete) request);
                delete.setURI(uri);
                break;
            default:
                break;
        }
        request.setConfig(config.build());
    }

    public HttpRequestBase getRequest() {
        return this.request;
    }

    public HttpUtils getHttpUtils() {
        return httpUtils;
    }

    public void setHttpUtils(HttpUtils httpUtils) {
        this.httpUtils = httpUtils;
    }

    public ResponseWrap execute(){
        return httpUtils.execute(this);
    }
}
