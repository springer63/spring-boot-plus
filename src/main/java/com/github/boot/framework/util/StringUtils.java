package com.github.boot.framework.util;

import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 字符串工具类
 *
 * @author chenjianhui
 * @date 2017/03/15
 */
public class StringUtils {
	
	/**
	 * 将字符串转换成数组,按照tag分割
	 */
	public static String[] str2Arr(String str,String tag){
		if(ValidUtils.isValid(str)){
			return str.split(tag);
		}
		return null ;
	}

	/**
	 * 判断在values数组中是否含有指定value字符串
	 */
	public static boolean contains(String[] values, String value) {
		if(ValidUtils.isValid(values)){
			for(String s : values){
				if(s.equals(value)){
					return true ;
				}
			}
		}
		return false;
	}

	/**
	 * 将数组变换成字符串,使用","号分割
	 */
	public static String arr2Str(Object[] arr) {
		String temp = "" ;
		if(ValidUtils.isValid(arr)){
			for(Object s : arr){
				temp = temp + s + "," ;
			}
			return temp.substring(0,temp.length() - 1);
		}
		return temp;
	}
	
	/**
	 * 获得字符串的描述信息
	 * @param str
	 * @return
	 */
	public static String getDescString(String str){
		if(str != null && str.trim().length() > 30){
			return str.substring(0,30);
		}
		return str ;
	}
	
	/**
	 * 判断字符串是否为空
	 * @param value
	 * @return
	 */
	public static boolean isEmpty(String value) {
		return value == null || value.trim().length() == 0;
	}
	
	/**
	 * 给定字符串和给定长度，如果字符串不足给定长度，前补0
	 * @param str
	 * @param len
	 * @return
	 */
	public static String leftPad(String str, int len) {
		StringBuffer buff = new StringBuffer();
		int length = str.length();
		int diff = len - length;
		for (int i = 0; i < diff; i++) {
			buff.append("0");
		}
		buff.append(str);
		return buff.toString();
	}

	/**
	 * 将参数数组转化成Json字符串
	 * @param params
	 * @return
	 */
	public static String arr2Json(Object[] params) {
		StringBuffer buf = new StringBuffer();
		for (Object obj : params) {
			if (obj instanceof HttpSession || obj instanceof HttpServletRequest || obj instanceof BindingResult || obj instanceof HttpServletResponse || obj instanceof MultipartFile) {
				continue;
			} else {
				try {
					buf.append(JsonUtils.toJson(obj) + " ");
				} catch (Exception e) {
					continue;
				}
			}
		}
		return buf.toString();
	}
	
	/**
	 * 过滤特殊字符     
	 * @param str
	 * @return
	 * @throws PatternSyntaxException
	 */
	public static String strFilter(String str) throws PatternSyntaxException {
		String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
	
	/**
	 * 过滤Emoji表情
	 * @param
	 * @return
	 */
	public static String eraseEmojis(String input) {
		if (ValidUtils.isValid(input)) {
			input = input.replaceAll("[^\\u0000-\\uFFFF]", "*");
		}
		return input;
	}
	
    
	/**
	 * 判断是否为中文
	 * @return
	 */
	public static boolean isChinese(String string) {
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if ((c < 0x4e00) || c > 0x9FA5) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 给定数值和给定长度，如果字符串不足给定长度，前补0
	 * @param num
	 * @param length
	 * @return
	 */
	public static String leftPadNum(long num, int length){
		return String.format("%0" + length+ "d", num);
	}

	/**
	 * 将驼峰式命名转换成下划线式命名
	 * @param s 输入
	 * @return 输出
	 */
	public static String toUnderlineName(String s) {
		if (s == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		boolean upperCase = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			boolean nextUpperCase = true;
			if (i < (s.length() - 1)) {
				nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
			}
			if ((i >= 0) && Character.isUpperCase(c)) {
				if (!upperCase || !nextUpperCase) {
					if (i > 0){
						sb.append('_');
					}
				}
				upperCase = true;
			} else {
				upperCase = false;
			}
			sb.append(Character.toLowerCase(c));
		}
		return sb.toString();
	}

	/**
	 * 将下划线式命名转换成驼峰式命名
	 * @param s 输入
	 * @return 输出
	 */
	public static String toCamelCase(String s) {
		if (s == null) {
			return null;
		}
		s = s.toLowerCase();
		StringBuilder sb = new StringBuilder(s.length());
		boolean upperCase = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '_') {
				upperCase = true;
			} else if (upperCase) {
				sb.append(Character.toUpperCase(c));
				upperCase = false;
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * 将下划线式命名转换成驼峰式命名, 且首字母大写
	 * @param s 输入
	 * @return 输出
	 */
	public static String toCapitalizeCamelCase(String s) {
		if (s == null) {
			return null;
		}
		s = toCamelCase(s);
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
	
	/**
	 * Unicode转中文
	 * @param dataStr
	 * @return
	 */
    public static String decodeUnicode(final String dataStr) {     
       int start = 0;     
       int end = 0;     
       final StringBuffer buffer = new StringBuffer();     
       while (start > -1) {     
           end = dataStr.indexOf("\\u", start + 2);     
           String charStr = "";     
           if (end == -1) {     
               charStr = dataStr.substring(start + 2, dataStr.length());     
           } else {     
               charStr = dataStr.substring(start + 2, end);     
           }     
           char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。     
           buffer.append(new Character(letter).toString());     
           start = end;     
       }     
       return buffer.toString();     
    }  
    
}
