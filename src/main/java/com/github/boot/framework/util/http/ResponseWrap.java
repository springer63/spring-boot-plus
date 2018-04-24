package com.github.boot.framework.util.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.http.*;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;

/**
 * ResponseWrap类，用来对响应结果的处理
 * @author ChenJianhui
 */
public class ResponseWrap {
    private Logger logger = LoggerFactory.getLogger(ResponseWrap.class);
     
    private HttpResponse response;
    private HttpEntity entity;
    private HttpRequestBase request;
    private HttpClientContext context;
    private ObjectMapper objMapper;
    private XmlMapper xmlMapper;

    public ResponseWrap(HttpRequestBase request, HttpResponse response, HttpClientContext context, ObjectMapper mapper, XmlMapper xmlMapper){
        this.response = response;
        this.request = request;
        this.context = context;
        this.objMapper = mapper;
        this.xmlMapper = xmlMapper;
        this.entity = response.getEntity();
        if(entity == null) {
            this.entity = new BasicHttpEntity();
        }
    }
     
 
    /**
     * 终止请求
     */
    public void abort(){
        request.abort();
    }
     
    /**
     * 获取重定向的地址
     * @return
     */
    public List<URI> getRedirectLocations(){
        return context.getRedirectLocations();
    }
     
    /**
     * 关闭连接
     */
    public void shutdown(){
       request.releaseConnection();
    }
     
    /**
     * 获取响应内容为String,默认编码为 "UTF-8"
     * @return
     */
    public String getString() {
        return getString(Consts.UTF_8);
    }
     
    /**
     * 获取响应内容为String
     * @param defaultCharset 指定编码
     * @return
     */
    public String getString(Charset defaultCharset) {
        try {
            String result = EntityUtils.toString(entity, defaultCharset);
            logger.info("请求[{}],返回[{}]", request.getURI().toString(), result);
            return result;
        } catch (ParseException | IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            request.releaseConnection();
        }
    }
     
    /**
     * 获取响应的类型
     * @return
     */
    public Header getContentType() {
        return entity.getContentType();
    }
     
    /**
     * 获取响应编码,如果是文本的话
     * @return
     */
    public Charset getCharset() {
         ContentType contentType = ContentType.get(entity);
         if(contentType == null) {
             return null;
         }
         return contentType.getCharset();
    }
     
    /**
     * 获取响应内容为字节数组
     * @return
     */
    public byte[] getByteArray() {
        try {
            byte[] bytes = EntityUtils.toByteArray(entity);
            logger.info("请求[{}],返回[{}]", request.getURI().toString(), new String(bytes));
            return bytes;
        } catch (ParseException | IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            request.releaseConnection();
        }
    }
     
    /**
     * 获取所有Header
     * @return
     */
    public Header[] getAllHeaders() {
        return response.getAllHeaders();
    }
     
    /**
     * 获取知道名称的Header列表
     * @return
     */
    public Header[] getHeaders(String name) {
        return response.getHeaders(name);
    }
     
    /**
     * 获取响应状态码
     * @return
     */
    public int getStatusCode(){
        return response.getStatusLine().getStatusCode();
    }
     
    /**
     * 移除指定name的Header列表
     * @param name
     */
    public void removeHeaders(String name){
        response.removeHeaders(name);
    }

    /**
     * 是否存在指定name的Header
     * @param name
     * @return
     */
    public boolean containsHeader(String name){
        return response.containsHeader(name);
    }
     
    /**
     * 获取Header的迭代器
     * @return
     */
    public HeaderIterator headerIterator(){
        return response.headerIterator();
    }
 
    /**
     * 获取协议版本信息
     * @return
     */
    public ProtocolVersion getProtocolVersion(){
        return response.getProtocolVersion();
    }
     
    /**
     * 获取Cookie列表
     * @return
     */
    public List<Cookie> getCookies(){
        return context.getCookieStore().getCookies();
    }
     
    /**
     * 获取InputStream,需要手动关闭流
     * @return
     */
    public InputStream getInputStream(){
        try {
            return entity.getContent();
        } catch (IllegalStateException | IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
     
    /**
     * 获取BufferedReader
     * @return
     */
    public BufferedReader getBufferedReader(){
        return new BufferedReader(new InputStreamReader(getInputStream(), getCharset()));
    }
     
    /**
     * 响应内容写入到文件
     * @param filePth 路径
     */
    public void transferTo(String filePth) {
        transferTo(new File(filePth));
    }
     
    /**
     * 响应内容写入到文件
     * @param file
     */
    public void transferTo(File file) {
        try(FileOutputStream fileOutputStream = new FileOutputStream(file)){
            transferTo(fileOutputStream);
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            request.releaseConnection();
        }
    }
     
    /**
     * 写入到OutputStream,并不会关闭OutputStream
     * @param outputStream OutputStream
     */
    public void transferTo(OutputStream outputStream) {
        try {
            entity.writeTo(outputStream);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
     
    /**
     * 获取JSON对象
     * @param clazz
     * @return
     */
    public <T> T getJson(Class<T> clazz) {
        try {
            return objMapper.readValue(getByteArray(), clazz);
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    /**
     * 获取JSON对象
     * @param clazz
     * @return
     */
    public <T> T getXml(Class<T> clazz) {
        try {
            return xmlMapper.readValue(getByteArray(), clazz);
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 获取JSON节点对象
     * @return
     */
    public JsonNode getJsonNode(){
        try {
            return objMapper.readTree(getByteArray());
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
 
    /**
     * 把Json转换成List
     * @param clazz
     * @return
     */
    public <T> List<T> getJsonList(Class<?> clazz) {
        try {
            return objMapper.readValue(getByteArray(), new TypeReference<List<T>>() {});
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
     
}