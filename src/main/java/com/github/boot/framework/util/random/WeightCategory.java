package com.github.boot.framework.util.random;

/**
 * 带权重的对象
 * @author cjh
 */
public class WeightCategory {
	
	/**
	 * 对象ID
	 */
	private Long id;
	
	/**
	 * 对象权重
	 */
	private Integer weight;
	
	/**
	 * 对象值
	 */
	private Object target;
	
	public WeightCategory(Long id, Object target, Integer weight) {
		this.id = id;
		this.weight = weight;
		this.target = target;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Integer getWeight() {
		return weight;
	}
	
	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	
	public Object getTarget() {
		return target;
	}
	
	public void setTarget(Object target) {
		this.target = target;
	}
	

}