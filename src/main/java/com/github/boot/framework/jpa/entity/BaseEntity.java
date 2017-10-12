package com.github.boot.framework.jpa.entity;


import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Entity基类
 * @author ChenJianhui
 */
@MappedSuperclass
public class BaseEntity<T> implements Comparable<T>, Serializable{

	private static final long serialVersionUID = 5516293384661833901L;

	@Override
	public int hashCode() {
		StringBuffer buffer = new StringBuffer();
		try{
			for (Field f : this.getClass().getDeclaredFields()){
			    if(f.getAnnotation(Id.class) == null){
			        continue;
                }
				f.setAccessible(true);
				Object thisValue = f.get(this);
				if(thisValue == null){
					return super.hashCode();
				}
				buffer.append(thisValue);
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return buffer.toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null)	return false;
		try{
			for (Field f : this.getClass().getDeclaredFields()){
                if(f.getAnnotation(Id.class) == null){
                    continue;
                }
				f.setAccessible(true);
				Object thisValue = f.get(this);
				Object otherValue = f.get(obj);
				if(thisValue == null || otherValue == null){
					return super.equals(obj);
				}
				if(!thisValue.equals(otherValue)){
					return false;
				}
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public int compareTo(T o) {
		if(o == null){
			return 0;
		}
		if(this == o){
			return 1;
		}
		try{
			for (Field f : this.getClass().getDeclaredFields()){
                if(f.getAnnotation(Id.class) == null){
                    continue;
                }
				f.setAccessible(true);
				Object thisValue = f.get(this);
				Object otherValue = f.get(this);
				if(thisValue == null || otherValue == null){
					return 0;
				}
				if(thisValue instanceof Number){
					Number num1 = (Number) thisValue;
					Number num2 = (Number) otherValue;
					if(num1.intValue() - num2.intValue() <= 0){
						return 0;
					}
				}
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return 1;
	}
}