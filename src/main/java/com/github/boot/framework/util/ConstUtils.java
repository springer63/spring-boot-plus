package com.github.boot.framework.util;

/**
 * 常量工具类
 * @author ChenJianhui
 */
public class ConstUtils {

	public static final String 	SYSTEM_PROFILE_KEY = "xdiamond.project.profile";

	/**
	 * 自动登录Token
	 */
	public static final String TOKEN_NAME = "TOKEN";

	/**
	 * session中存放的请求标识的key值
	 */
	public static final String SESSION_API_REQUESTID = "sessionApiRequestId";
	
	/**
	 * session中存放的User对象的key值
	 */
	public static final String SESSION_USER = "profile";//"sessionUser";
	
	/**
	 * session中存放的用户权限的key值
	 */
	public static final String SESSION_USER_ALLOW_RIGHT = "allowRight";
	
	/**
	 * 系统包含的所有的权限
	 */
	public static final String APP_ALL_RIGHT_MAP = "allRightsMap";

	/**
	 * 系统没有权限控制的请求URL
	 */
	public static final String APP_ALLOW_URLS = "allowUrls";


    public static final String SESSION_USER_ID = "sessionUserId";
}
