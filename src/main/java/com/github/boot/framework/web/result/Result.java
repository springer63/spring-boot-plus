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
	public static final int NOT_OAUTH = 40000;

	/**
	 * 无权限访问
	 */
	public static final int PERMISSION_DENIED = 40001;

	/**
	 * 非法请求
	 */
	public static final int ILLEGAL_REQUEST = 40002;

	/**
	 * 无效的请求参数
	 */
	public static final int INVALID_PARAM = 40003;

	/**
	 * 无效的签名
	 */
	private static final int INVALID_SIGN = 40005;

	/**
	 * 请求接口不存在
	 */
	public static final int API_NOT_EXIST = 40004;

	/**
	 * 无效请求方法
	 */
	public static final int INVALID_REQUEST_METHOD = 40005;

	/**
	 * 用户被锁定
	 */
	public static final int ACCOUNT_LOCKED = 40006;

	/**
	 * 系统异常
	 */
	public static final int SYSTEM_ERROR = 50000;

	/**
	 * 系统繁忙
	 */
	public static final int SYSTEM_BUSY = 50001;

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
	private Long userId;

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

	public static Result success(){
		return new Result(SUCCESS, "request success");
	}

	public static Result locked(){
		return new Result(ACCOUNT_LOCKED, "用户被锁定");
	}

	public static Result unauthorized(){
		return new Result(NOT_OAUTH, "请先登录！");
	}

	public static Result permissionDenied(){
		return new Result(PERMISSION_DENIED, "无访问权限");
	}

	public static Result illagalRequest(){
		return new Result(ILLEGAL_REQUEST, "非法请求");
	}

	public static Result invalidSign() {
		return new Result(INVALID_SIGN, "无效的签名");
	}

	public static Result invalidParam(){
		return new Result(INVALID_PARAM, "参数校验不通过");
	}

	public static Result notFound(){
		return new Result(API_NOT_EXIST, "请求地址错误");
	}

	public static Result systemError(){
		return new Result(SYSTEM_ERROR, "服务器错误");
	}

	public static Result systemBusy() {
		return new Result(SYSTEM_BUSY, "操作过于频繁，请稍后重试");
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Object getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
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

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
