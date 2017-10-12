package com.github.boot.framework.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * @Description Redis配置
 * @author cjh
 * @version 1.0
 * @date：2017年3月15日 下午6:29:49
 */
@Configuration
public class RedisConfigure{

	@Value("${redis.nodes:127.0.0.1:6379}")
	private String nodes;

	@Bean
	public RedissonClient redissonClient(){
		String[] nodeArr = nodes.split(",");
		Config config = new Config();
		if(nodeArr.length == 1){
			config.useSingleServer().setAddress(nodes);
		}else{
			config.useClusterServers().setScanInterval(2000).addNodeAddress(nodeArr);
		}
		return Redisson.create(config);
	}

}
