package com.github.boot.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class ServletUtils {

	private static Logger log = LoggerFactory.getLogger(ServletUtils.class);
	
	public static final String TEXT_TYPE = "text/plain";
	public static final String JSON_TYPE = "application/json";
	public static final String XML_TYPE = "text/xml";
	public static final String HTML_TYPE = "text/html";
	public static final String JS_TYPE = "text/javascript";
	public static final String EXCEL_TYPE = "application/vnd.ms-excel";
	public static final String JPEG_TYPE = "image/jpeg";

	public static final String AUTHENTICATION_HEADER = "Authorization";

	public static final long ONE_YEAR_SECONDS = 60 * 60 * 24 * 365;

	public static void setCookie(HttpServletResponse response, String name, String value){
		response.addCookie(new Cookie(name, value));
	}

	public static void setExpiresHeader(HttpServletResponse response, long expiresSeconds) {
		// Http 1.0 header
		response.setDateHeader("Expires", System.currentTimeMillis() + expiresSeconds * 1000);
		// Http 1.1 header
		response.setHeader("Cache-Control", "private, max-age=" + expiresSeconds);
	}

	public static void setDisableCacheHeader(HttpServletResponse response) {
		// Http 1.0 header
		response.setDateHeader("Expires", 1L);
		response.addHeader("Pragma", "no-cache");
		// Http 1.1 header
		response.setHeader("Cache-Control", "no-cache, no-store, max-age=0");
	}

	public static void setLastModifiedHeader(HttpServletResponse response, long lastModifiedDate) {
		response.setDateHeader("Last-Modified", lastModifiedDate);
	}

	public static void setEtag(HttpServletResponse response, String etag) {
		response.setHeader("ETag", etag);
	}

	public static boolean checkIfModifiedSince(HttpServletRequest request, HttpServletResponse response,
			long lastModified) {
		long ifModifiedSince = request.getDateHeader("If-Modified-Since");
		if ((ifModifiedSince != -1) && (lastModified < ifModifiedSince + 1000)) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return false;
		}
		return true;
	}

	public static boolean checkIfNoneMatchEtag(HttpServletRequest request, HttpServletResponse response, String etag) {
		String headerValue = request.getHeader("If-None-Match");
		if (headerValue != null) {
			boolean conditionSatisfied = false;
			if (!"*".equals(headerValue)) {
				StringTokenizer commaTokenizer = new StringTokenizer(headerValue, ",");
				while (!conditionSatisfied && commaTokenizer.hasMoreTokens()) {
					String currentToken = commaTokenizer.nextToken();
					if (currentToken.trim().equals(etag)) {
						conditionSatisfied = true;
					}
				}
			} else {
				conditionSatisfied = true;
			}
			if (conditionSatisfied) {
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				response.setHeader("ETag", etag);
				return false;
			}
		}
		return true;
	}

	public static void setFileDownloadHeader(HttpServletResponse response, String fileName) {
		try {
			String encodedfileName = new String(fileName.getBytes(), "utf-8");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedfileName + "\"");
		} catch (UnsupportedEncodingException e) {
		}
	}

	public static Map<String, Object> getParametersStartingWith(ServletRequest request, String prefix) {
		Assert.notNull(request, "Request must not be null");
		Enumeration<?> paramNames = request.getParameterNames();
		Map<String, Object> params = new TreeMap<>();
		if (prefix == null) {
			prefix = "";
		}
		while (paramNames != null && paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			if ("".equals(prefix) || paramName.startsWith(prefix)) {
				String unprefixed = paramName.substring(prefix.length());
				String[] values = request.getParameterValues(paramName);
				if (values == null || values.length == 0) {
					// Do nothing, no values found at all.
				} else if (values.length > 1) {
					params.put(unprefixed, values);
				} else {
					params.put(unprefixed, values[0]);
				}
			}
		}
		return params;
	}
	
	 /**
	  * 在很多应用下都可能有需要将用户的真实IP记录下来，这时就要获得用户的真实IP地址，在JSP里，获取客户端的IP地
	  * 址的方法是：request.getRemoteAddr()，这种方法在大部分情况下都是有效的。但是在通过了Apache,Squid等
	  * 反向代理软件就不能获取到客户端的真实IP地址了。
	  * 但是在转发请求的HTTP头信息中，增加了X－FORWARDED－FOR信息。用以跟踪原有的客户端IP地址和原来客户端请求的服务器地址。
	  * @param request
	  * @return
	  */
	public static String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ValidUtils.isValid(ip)) {
			ip = ip.split(",")[0];
		}
		log.info("client ip :" + ip);
		return ip;
	}
	
	/**
	 * 获取Servlet容器对应上下文URL
	 * @param request
	 * @return
	 */
	public static String getContextUrl(HttpServletRequest request){
		StringBuffer url = request.getRequestURL();
		if("/".equals(request.getContextPath())){
			return url.delete(url.length() - request.getRequestURI().length(), url.length()).append(request.getContextPath()).toString();  
		}else{
			return url.delete(url.length() - request.getRequestURI().length(), url.length()).append(request.getContextPath()).append("/").toString();  
		}
	}

	/**
	 * 获取客户端系统平台
	 * @param request
	 * @return
	 */
	public static String getClientPlatform(HttpServletRequest request){
		String userAgent = request.getHeader("User-Agent").toLowerCase();
		return getClientPlatform(userAgent);
	}

	/**
	 * 获取客户端系统平台
	 * @param userAgent
	 * @return
	 */
	public static String getClientPlatform(String userAgent){
		if(!ValidUtils.isValid(userAgent)){
			return "Unknown";
		}
		userAgent = userAgent.toLowerCase();
		if(userAgent.contains("ios")){
			return "iOS";
		}
		if(userAgent.contains("android")){
			return "Android";
		}
		if(userAgent.contains("windows")){
			return "Windows";
		}
		return "Other";
	}

	/**
	 * 输出文件流
	 * @param filename
	 * @param bytes
	 * @throws IOException
	 */
	public static ResponseEntity<byte[]> getFileResponse(String filename, byte[] bytes){
		HttpHeaders headers = new HttpHeaders();
		try{
			filename = new String(filename.getBytes("UTF-8"), "ISO8859-1");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		headers.setContentType(new MediaType(MediaType.APPLICATION_OCTET_STREAM, Charset.forName("UTF-8")));
		headers.setContentLength(bytes.length);
		headers.setContentDispositionFormData("attachment", filename);
		return new ResponseEntity<>(bytes, headers, HttpStatus.CREATED);
	}

}