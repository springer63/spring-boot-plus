package com.github.boot.framework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 数据工具类
 * @author ChenJianhui
 */
public class DataUtils {
	
	/**
	 * 指定长度数字生成器
	 * @param length 要生成的数字字符串的长度
	 * @return
	 */
	public static String numRandomGenerator(int length){
		Long num = (long) ((Math.random() * 9 + 1) * Math.pow(10, length-1));
		return num.toString();
	}
	
	/**
	 * 指定长度字符串生成器
	 * @param length 表示生成字符串的长度
	 * @return
	 */
	public static String strRandomGenerator(int length) {
	    String base = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnopqrstuvwxyz23456789";   
	    Random random = new Random();   
	    StringBuffer sb = new StringBuffer();   
	    for (int i = 0; i < length; i++) {   
	        int number = random.nextInt(base.length());   
	        sb.append(base.charAt(number));   
	    }   
	    return sb.toString();   
	 }
	
	public static String generateDrawCode(int length) {
	    String character = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";   
	    Random random = new Random();   
	    StringBuffer sb = new StringBuffer();   
	    for (int i = 0; i < 3; i++) {   
	        int index = random.nextInt(character.length());   
	        sb.append(character.charAt(index));   
	    }   
	    String number = "0123456789";
	    for (int i = 0; i < 5; i++) {   
	        int index = random.nextInt(number.length());   
	        sb.append(number.charAt(index));   
	    }
	    String character2 = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";
	    sb.append(character2.charAt(random.nextInt(character2.length())));   
	    return sb.toString();   
	 }

	/**
	 * 使用md5算法进行加密 
	 */
	public static String md5(String src){
		try {
			StringBuffer buffer = new StringBuffer();
			char[] chars = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
			byte[] bytes = src.getBytes();
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] targ = md.digest(bytes);
			for(byte b: targ){
				buffer.append(chars[(b >> 4) & 0x0F]);
				buffer.append(chars[b & 0x0F]);
			}
			return buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null ;
	}
	
	/**
	 * 深度复制,复制的整个对象图
	 */
	public static Serializable deeplyCopy(Serializable src){
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(src);
			oos.close();
			baos.close();
			byte[] bytes = baos.toByteArray();
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Serializable copy = (Serializable) ois.readObject();
			ois.close();
			bais.close();
			return copy ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null ;
	}
	
	/**
	 * 判断字符串是否为数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){  
	    Pattern pattern = Pattern.compile("^[0-9]*$");  
	    return pattern.matcher(str).matches();     
	}  
	
	/**
	 * 随机生成在[min, max]区间内的随机整数
	 * @param min
	 * @param max
	 * @return
	 */
	public static int randomInt(int min, int max){
		Random random = new Random();
	    int num = random.nextInt(max-min+1) + min ;
	    return num;
	}
	
    /**
     * 获取UUID字符串(不带横杠)
     * @return
     */
    public static String uuidStr(){
    	return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }
    
    /** 
     * 除去数组中的空值和签名参数
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, Object> paraFilter(Map<String, Object> sArray) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (sArray == null || sArray.size() <= 0) {
            return result;
        }
        for (String key : sArray.keySet()) {
        	if(sArray.get(key) != null){
	            String value = sArray.get(key).toString();
	            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
	                || key.equalsIgnoreCase("sign_type") || key.equalsIgnoreCase("signature")) {
	                continue;
	            }
	            result.put(key, value);
        	}
        }
        return result;
    }
        
    /** 
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, Object> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key).toString();
            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        return prestr;
    }
    /** 
     * 功能：生成签名结果
    * @param sArray 要签名的数组
    * @param key 安全校验码
    * @return 签名结果字符串
    */
	public static String buildMysign(Map<String,Object> sArray, String key) {
		String prestr = createLinkString(sArray);  //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		prestr = prestr + key;                     //把拼接后的字符串再与安全校验码直接连接起来(key 双方确定好的秘钥)
		System.out.println(prestr);
		String signature = md5(prestr);
		return signature;
	}

	public static String leftPaddingZero(int num,int length){
		String numStr = String.valueOf(num);
		if(numStr.length() < length){
			for(int i= 0 ;i <= length - numStr.length() ; i++){
				numStr = "0"+numStr;
			}
		}
		return numStr;
	}
	public static String generateSN(Long id) {
		Random random = new Random(id);
		return leftPaddingZero(random.nextInt(1000000),6) + leftPaddingZero(random.nextInt(10000),4);
	}
	
}
