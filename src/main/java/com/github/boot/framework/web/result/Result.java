package com.github.boot.framework.web.result;

import com.github.boot.framework.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @Description 全局返回结果
 * @author cjh
 * @version 1.0
 * @date：2017年3月14日 下午7:32:05
 */
public final class Result<T> implements Serializable{

	private static final long serialVersionUID = -5759964467525426508L;

	private static final Logger logger = LoggerFactory.getLogger(Result.class);

	//**********************系统全局错误码**************************

	/**
	 * 请求成功状态码
	 */
	public static final int SUCCESS = 0;

	/**
	 *  未授权（未登录）
	 */
	public static final int NOT_OAUTH = 50000;

	/**
	 * 无权限访问
	 */
	public static final int PERMISSION_DENIED = 50001;

	/**
	 * 非法请求
	 */
	public static final int ILLEGAL_REQUEST = 50002;

	/**
	 * 无效的请求参数
	 */
	public static final int INVALID_PARAM = 50003;

	/**
	 * 无效的签名
	 */
	private static final int INVALID_SIGN = 50005;

	/**
	 * 请求接口不存在
	 */
	public static final int API_NOT_EXIST = 50004;

	/**
	 * 无效请求方法
	 */
	public static final int INVALID_REQUEST_METHOD = 50005;

	/**
	 * 用户被锁定
	 */
	public static final int ACCOUNT_LOCKED = 50006;

	/**
	 * 系统异常
	 */
	public static final int SYSTEM_ERROR = 50007;

	/**
	 * 系统繁忙
	 */
	public static final int SYSTEM_BUSY = 50007;

	//**************************************************************

	/**
	 * 错误码
	 */
	private int code;

	/**
	 * 错误信息
	 */
	private String message;

	/**
	 * 用户ID
	 */
	private Object userId;

	/**
	 * 时间戳
	 */
	private Long timestamp;

	/**
	 * 返回数据
	 */
	private T data;

	public Result(){}

	public Result(int code, String msg){
		this.code = code;
		this.message = msg;
	}

	public static <T> Result<T> success(){
		return new Result<>(SUCCESS, "success");
	}

	public static <T> Result<T> locked(){
		return new Result<>(ACCOUNT_LOCKED, "用户被锁定");
	}

	public static <T> Result<T> unauthorized(){
		return new Result<>(NOT_OAUTH, "请先登录！");
	}

	public static <T> Result<T> permissionDenied(){
		return new Result<>(PERMISSION_DENIED, "无访问权限");
	}

	public static <T> Result<T> illagalRequest(){
		return new Result<>(ILLEGAL_REQUEST, "非法请求");
	}

	public static <T> Result<T> invalidSign() {
		return new Result<>(INVALID_SIGN, "无效的签名");
	}

	public static <T> Result<T> invalidParam(){
		return new Result<>(INVALID_PARAM, "参数校验不通过");
	}

	public static <T> Result<T> notFound(){
		return new Result<>(API_NOT_EXIST, "请求地址错误");
	}

	public static <T> Result<T> systemError(){
		return new Result<>(SYSTEM_ERROR, "服务器错误");
	}

	public static <T> Result<T> systemBusy() {
		return new Result<>(SYSTEM_BUSY, "操作过于频繁，请稍后重试");
	}

	public T getData() {
		return data;
	}

	public Result<T> setData(T data) {
		this.data = data;
		return this;
	}

	public Object getUserId() {
		return userId;
	}

	public void setUserId(Object userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		try {
			return JsonUtils.toJson(this);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "request error";
		}
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public int getCode() {
		return code;
	}

	public Result<T> setCode(int code) {
		this.code = code;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public Result<T> setMessage(String message) {
		this.message = message;
		return this;
	}

}
