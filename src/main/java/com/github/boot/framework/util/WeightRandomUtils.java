package com.github.boot.framework.util;

import java.util.List;
import java.util.Random;  

/**
 * 带权重的随机算法， 使用于抽奖类型
 * @author cjh
 */
public class WeightRandomUtils {
    
	/**
	 * 根据权重随机抽取一个对象
	 * 如果categories为null或长度为0，或者累计的weight小于0,返回null
	 * @param items
	 * @return
	 */
	public static <T> T random(List<Item<T>> items){
		Random random = new Random();
		int weightSum = 0;
		for (Item<T> wc : items) {
			weightSum += wc.getWeight();
		}
		int n = random.nextInt(weightSum);
		int m = 0;
		Item<T> value = null;
		for (Item<T> wc : items) {
			if (m <= n && n < m + wc.getWeight()) {
				value = wc;
				break;
			}
			m += wc.getWeight();
		}
		return value.target;
	}

	/**
	 * 带权重的对象
	 * @author cjh
	 */
	public static class Item<T> {

		/**
		 * 对象权重
		 */
		private int weight;

		/**
		 * 对象值
		 */
		private T target;

		public Item(T target, Integer weight) {
			this.weight = weight;
			this.target = target;
		}

		public Integer getWeight() {
			return weight;
		}

		public void setWeight(Integer weight) {
			this.weight = weight;
		}

		public T getTarget() {
			return target;
		}

		public void setTarget(T target) {
			this.target = target;
		}
	}
}

