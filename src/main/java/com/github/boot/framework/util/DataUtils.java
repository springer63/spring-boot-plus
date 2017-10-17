package com.github.boot.framework.util;

import java.io.*;
import java.security.MessageDigest;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 数据工具类
 * @author ChenJianhui
 */
public class DataUtils {

	private final static Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+");
	
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
	    return NUMBER_PATTERN.matcher(str).matches();
	}  
	
	/**
	 * 随机生成在[min, max]区间内的随机整数
	 * @param min
	 * @param max
	 * @return
	 */
	public static int randomInt(int min, int max){
		Random random = new Random();
	    int num = random.nextInt(max - min + 1) + min ;
	    return num;
	}
	
    /**
     * 获取UUID字符串(不带横杠)
     * @return
     */
    public static String uuidStr(){
    	return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }
    

}
