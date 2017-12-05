package com.github.boot.framework.web.result;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

/**
 * @Description 全局返回结果
 * @author cjh
 * @version 1.0
 * @date：2017年3月14日 下午7:32:05
 */
@JsonPropertyOrder({"code", "message", "userId", "timestamp", "data"})
public class Result implements Serializable{

	private static final long serialVersionUID = -5759964467525426508L;

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
	 * 请求接口不存在
	 */
	public static final int API_NOT_EXIST = 40004;

	/**
	 * 无效请求方法
	 */
	public static final int INVALID_REQUEST_METHOD = 40005;

	/**
	 * 用户账号被锁定
	 */
	public static final int ACCOUNT_LOCKED = 40006;

	/**
	 * 无效的签名
	 */
	public static final int INVALID_SIGN = 40007;

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
	 * 响应结果状态
	 */
	private int status;

	/**
	 * 响应结果信息
	 */
	private String message;

	/**
	 * 用户ID
	 */
	private Object userId;

	/**
	 * 响应时间戳
	 */
	private Long timestamp;

	/**
	 * 响应结果数据
	 */
	private Object data;

	public Result(){}

	public Result(int status, String message){
		this.status = status;
		this.message = message;
	}

	public static Result success(){
		return new Result(SUCCESS, "请求成功");
	}

	public static Result locked(){
		return new Result(ACCOUNT_LOCKED, "用户被锁定");
	}

	public static Result unauthorized(){
		return new Result(NOT_OAUTH, "用户未登录");
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getUserId() {
		return userId;
	}

	public void setUserId(Object userId) {
		this.userId = userId;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
