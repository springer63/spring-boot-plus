package com.github.boot.framework.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.BeanWrapperImpl;


/**
 * 反射工具类
 * 提供访问私有变量, 获取泛型类型 Class, 提取集合中元素属性等 Utils 函数
 * @author ChenJianhui
 */
public class ReflectionUtils {


	/**
	 * 判断一个类是否实现了某个接口
	 * @param target
	 * @param interfaceClass
	 * @return
	 */
	public static boolean isImplement(Class<?> target, Class<?> interfaceClass){
		for (Class<?> i : target.getInterfaces()){
			if(i == interfaceClass){
				return true;
			}
			if(i.getInterfaces().length > 0){
				isImplement(i, interfaceClass);
			}
		}
		Class<?> superclass = target.getSuperclass();
		if(superclass != null){
			return isImplement(superclass, interfaceClass);
		}
		return false;
	}
	
	/**
	 * 将反射时的 "检查异常" 转换为 "运行时异常"
	 * @return
	 */
	public static IllegalArgumentException convertToUncheckedException(Exception ex){
		if(ex instanceof IllegalAccessException || ex instanceof IllegalArgumentException
				|| ex instanceof NoSuchMethodException){
			throw new IllegalArgumentException("反射异常", ex);
		}else{
			throw new IllegalArgumentException(ex);
		}
	}
	
	/**
	 * 通过反射, 获得定义 Class 时声明的父类的泛型参数的类型
	 * 如: public EmployeeDao extends BaseDao<Employee, String>
	 * @param clazz
	 * @param index
	 * @return
	 */
	public static Class<?> getSuperClassGenricType(Class<?> clazz, int index){
		Type genType = clazz.getGenericSuperclass();
		if(!(genType instanceof ParameterizedType)){
			return Object.class;
		}
		Type [] params = ((ParameterizedType)genType).getActualTypeArguments();
		if(index >= params.length || index < 0){
			return Object.class;
		}
		if(!(params[index] instanceof Class)){
			return Object.class;
		}
		return (Class<?>) params[index];
	}
	
	
	/**
	 * 循环向上转型, 获取对象的 DeclaredMethod
	 * @param object
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 */
	public static Method getDeclaredMethod(Object object, String methodName, Class<?>[] parameterTypes){
		for(Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()){
			try {
				return superClass.getDeclaredMethod(methodName, parameterTypes);
			} catch (NoSuchMethodException e) {
				//Method 不在当前类定义, 继续向上转型
			}
		}
		return null;
	}
	
	/**
	 * 使 私有的Field变为可访问
	 * @param field
	 */
	public static void makeAccessible(Field field){
		if(!Modifier.isPublic(field.getModifiers())){
			field.setAccessible(true);
		}
	}
	
	/**
	 * 循环向上转型, 获取对象的 DeclaredField
	 * @param object
	 * @param filedName
	 * @return
	 */
	public static Field getDeclaredField(Object object, String filedName){
		for(Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()){
			try {
				return superClass.getDeclaredField(filedName);
			} catch (NoSuchFieldException e) {
				//Field 不在当前类定义, 继续向上转型
			}
		}
		return null;
	}
	
	/**
	 * 直接调用对象方法, 而忽略修饰符(private, protected)
	 * @param object
	 * @param methodName
	 * @param parameterTypes
	 * @param parameters
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 */
	public static Object invokeMethod(Object object, String methodName, Class<?> [] parameterTypes,
			Object [] parameters) throws InvocationTargetException{
		Method method = getDeclaredMethod(object, methodName, parameterTypes);
		if(method == null){
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + object + "]");
		}
		method.setAccessible(true);
		try {
			return method.invoke(object, parameters);
		} catch(IllegalAccessException e) {
		   e.printStackTrace();
		} 
		return null;
	}
	
	/**
	 * 直接设置对象属性值, 忽略 private/protected 修饰符, 也不经过 setter
	 * @param object
	 * @param fieldName
	 * @param value
	 */
	public static void setFieldValue(Object object, String fieldName, Object value){
		Field field = getDeclaredField(object, fieldName);
		if (field == null){
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
		}
		makeAccessible(field);
		try {
			field.set(object, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 直接读取对象的属性值, 忽略 private/protected 修饰符, 也不经过 getter
	 * @param object
	 * @param fieldName
	 * @return
	 */
	public static Object getFieldValue(Object object, String fieldName){
		Field field = getDeclaredField(object, fieldName);
		if (field == null){
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
		}
		makeAccessible(field);
		Object result = null;
		try {
			result = field.get(object);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 拷贝对象属性（不包含值为空的属性）
	 * @param dest
	 * @param source
	 * @throws Exception
	 */
	public static void copyProperties(Object dest, Object source){
		if(dest == null || source == null){
			return;
		}
		for (Field f : source.getClass().getDeclaredFields()) {
			if(! Modifier.isFinal(f.getModifiers())){
				Object value = ReflectionUtils.getFieldValue(source, f.getName());
				if(value != null && ReflectionUtils.getDeclaredField(dest, f.getName()) != null){
					ReflectionUtils.setFieldValue(dest, f.getName(), value);
				}
			}
		}
	}

	/**
	 * 获取类申明的所有属性
	 * @param beanClass
	 * @return
	 */
	public static Field[] getAllDeclaredFields(Class<?> beanClass){
		HashMap<String , Field> fieldMap  = new HashMap<String , Field>();
		addFields(beanClass, fieldMap);
		Field[] array = new Field[fieldMap.size()];
		fieldMap.values().toArray(array);
		return array;
	}

	private static void addFields(Class<?> beanClass, HashMap<String , Field> fieldMap){
		for (Field field : beanClass.getDeclaredFields()) {
			if(!fieldMap.containsKey(field.getName())){
				fieldMap.put(field.getName() , field);
			}
		}
		Class<?> superclass = beanClass.getSuperclass();
		if(superclass != null && superclass != Object.class){
			addFields(superclass, fieldMap);
		}
	}

	/**
	 * 获取一个类的所有属性
	 * @param beanClass
	 * @return
	 */
	public static PropertyDescriptor[] getProperties(Class<?> beanClass) {
		HashMap<String , PropertyDescriptor> propertyMap  = new HashMap<String , PropertyDescriptor>();
		addProperties(beanClass, propertyMap);
		PropertyDescriptor[] array = new PropertyDescriptor[propertyMap.size()];
		propertyMap.values().toArray(array);
		return array;
	}

	private static void addProperties(Class<?> beanClass, HashMap<String , PropertyDescriptor> propertyMap){
		for (Method method : beanClass.getMethods()) {
			String methodName = method.getName();
			if(!methodName.startsWith("get") && !methodName.startsWith("set")){
				continue;
			}
			String upperCaseName = methodName.substring(3);
			if(upperCaseName.equals("Class")){
				continue;
			}
			char[] chars  = upperCaseName.toCharArray();
			chars[0] = Character.toLowerCase(chars[0]);
			String propertyName = new String(chars);
			try {
				PropertyDescriptor property = propertyMap.get(propertyName);
				if(property == null){
					property = new PropertyDescriptor(propertyName , beanClass , null , null);
					propertyMap.put(propertyName, property);
				}
				if(methodName.startsWith("get")){
					if(property.getReadMethod() == null){
						property.setReadMethod(method);
					}
				}else{
					if(property.getWriteMethod() == null){
						property.setWriteMethod(method);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		Class<?> superclass = beanClass.getSuperclass();
		if(superclass != null && superclass != Object.class){
			addProperties(superclass, propertyMap);
		}
	}

	/**
	 * 根据属性获取属性
	 * @param beanClass
	 * @param name
	 * @return
	 */
	public static Field findField(Class<?> beanClass, String name) {
		HashMap<String , Field> fieldMap  = new HashMap<String , Field>();
		addFields(beanClass, fieldMap);
		return fieldMap.get(name);
	}

	/**
	 * 获取包装类型
	 * @param type
	 * @return
	 */
	public static Class<?> getBoxedType(Class<?> type){
		if(type == char.class){
			return Character.class;
		}
		if(type == boolean.class){
			return Boolean.class;
		}
		if(type == double.class){
			return Double.class;
		}
		if(type == double.class){
			return Double.class;
		}
		if(type == long.class){
			return Long.class;
		}
		if(type == int.class){
			return Integer.class;
		}
		if(type == short.class){
			return Short.class;
		}
		if(type == byte.class){
			return Byte.class;
		}
		return type;
	}

	/**
	 * 获取拆箱类型
	 * @param type
	 * @return
	 */
	public static Class<?> getUnboxedType(Class<?> type){
		if(type == Character.class){
			return char.class;
		}
		if(type == Boolean.class){
			return boolean.class;
		}
		if(type == Double.class){
			return double.class;
		}
		if(type == Double.class){
			return double.class;
		}
		if(type == Long.class){
			return long.class;
		}
		if(type == Integer.class){
			return int.class;
		}
		if(type == Short.class){
			return short.class;
		}
		if(type == Byte.class){
			return byte.class;
		}
		return type;
	}

	/**
	 * 判断一个类是否是否复杂类型
	 * @param type
	 * @return
	 */
	public static boolean isComplexType(Class<?> type) {
		if (type.isPrimitive()) {
			return false;
		}
		if(getUnboxedType(type).isPrimitive()){
			return false;
		}
		if (type == String.class) {
			return false;
		}
		if (type == Date.class) {
			return false;
		}
		if (type.isArray()) {
			return false;
		}
		if(type.isEnum()){
			return false;
		}
		return true;
	}

	/**
	 * 讲一个对象转成字符串
	 * @param object
	 * @return
	 */
    public static String toString(Object object) {
		if(!isComplexType(object.getClass())){
			return object.toString();
		}
		StringBuffer buffer = new StringBuffer();
		BeanWrapperImpl wrapper = new BeanWrapperImpl(object);
		for (PropertyDescriptor pd : wrapper.getPropertyDescriptors()){
			if(pd.getReadMethod().getName().equals("getClass")){
				continue;
			}
			Object propertyValue = wrapper.getPropertyValue(pd.getName());
			if(propertyValue == null){
				continue;
			}
			buffer.append(toString(propertyValue));
		}
		return buffer.toString();
    }
}

