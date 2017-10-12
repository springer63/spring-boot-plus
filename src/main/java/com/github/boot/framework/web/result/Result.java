package com.github.boot.framework.web.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @Description 全局返回结果
 * @author cjh
 * @version 1.0
 * @date：2017年3月14日 下午7:32:05
 */
@JsonPropertyOrder({"code", "message", "handleTime", "userId", "data"})
public class Result implements Serializable{

	private static final long serialVersionUID = -5759964467525426508L;

	private static final Logger logger = LoggerFactory.getLogger(Result.class);

	//**********************系统全局错误码**************************

	/**
	 * 请求成功状态码
	 */
	public static final String SUCCESS = "0";

	/**
	 *  未授权（未登录）
	 */
	public static final String NOT_OAUTH = "97";

	/**
	 * 用户被锁定
	 */
	public static final String USER_LOCKED = "40006";

	/**
	 * 无权限访问
	 */
	public static final String PERMISSION_DENIED = "40001";

	/**
	 * 非法请求
	 */
	public static final String ILLEGAL_REQUEST = "40002";

	/**
	 * 无效的请求参数
	 */
	public static final String INVALID_PARAM = "40003";

	/**
	 * 请求接口不存在
	 */
	public static final String API_NOT_EXIST = "40004";

	/**
	 * 无效请求方法
	 */
	public static final String INVALID_REQUEST_METHOD = "40005";

	/**
	 * 系统异常
	 */
	public static final String SYSTEM_ERROR = "100";

	/**
	 * 系统繁忙
	 */
	public static final String SYSTEM_BUSY = "101";
	private static final String INVALID_SIGN = "40006";

	//**************************************************************

	/**
	 * 错误码
	 */
	@JsonProperty(value = "code")
	private String errcode;

	/**
	 * 错误信息
	 */
	@JsonProperty(value = "message")
	private String errmsg;

	private Object userId;

	private Long handleTime;

	/**
	 * 返回数据
	 */
	@JsonProperty(value = "datas")
	private Object data;

	public Result(){}

	public Result(String code, String msg){
		this.errcode = code;
		this.errmsg = msg;
	}

	public static Result success(){
		return new Result(SUCCESS, "request success");
	}

	public static Result locked(){
		return new Result(USER_LOCKED, "用户被锁定");
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

	public String getErrcode() {
		return errcode;
	}

	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
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
			return new ResultJsonSerializer().writeValueAsString(this);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			return "request error";
		}
	}

	public void setHandleTime(Long handleTime) {
		this.handleTime = handleTime;
	}

	public Long getHandleTime() {
		return handleTime;
	}
}
