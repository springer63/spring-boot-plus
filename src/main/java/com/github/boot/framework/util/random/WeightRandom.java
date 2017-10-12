package com.github.boot.framework.util.random;

import java.util.List;
import java.util.Random;  

/**
 * 带权重的随机算法， 使用于抽奖类型
 * @author cjh
 */
public class WeightRandom {  
    
	/**
	 * 根据权重随机抽取一个对象
	 * 如果categorys为null或长度为0，或者累计的weight小于0,返回null
	 * @param categorys
	 * @return
	 */
	public static WeightCategory randomByWeight(List<WeightCategory> categorys){
		Random random = new Random();
		Integer weightSum = 0;
		if(categorys != null && categorys.size() > 0){
			for (WeightCategory wc : categorys) {
				weightSum += wc.getWeight();
			}
		}
		if (weightSum <= 0) {
			return null;
		}
		Integer n = random.nextInt(weightSum);
		Integer m = 0;
		WeightCategory value = null;
		for (WeightCategory wc : categorys) {
			if (m <= n && n < m + wc.getWeight()) {
				value = wc;
				break;
			}
			m += wc.getWeight();
		}
		return value;
	}

}

