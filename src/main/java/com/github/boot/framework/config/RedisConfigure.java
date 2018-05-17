package com.github.boot.framework.config;

import org.apache.commons.lang3.ArrayUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
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

	@Value("${redis.mode:single}")
	private String mode;

	@Value("${redis.node:127.0.0.1:6379}")
	private String node;

	/**
	 * 单例模式
	 * @return
	 */
	@Bean
	@ConditionalOnExpression("'${redis.mode}'=='single'")
	public RedissonClient redisSingleClient(){
		Config config = new Config();
		config.useSingleServer().setAddress(node);
		return Redisson.create(config);
	}

	/**
	 * 主从模式
	 * @return
	 */
	@Bean
	@ConditionalOnExpression("'${redis.mode}'=='master-slave'")
	public RedissonClient redissonClient(){
		Config config = new Config();
		String[] nodes = node.split(",");
		String master = nodes[0];
		String[] slaves = ArrayUtils.subarray(nodes, 1, nodes.length);
		config.useMasterSlaveServers().setMasterAddress(master).addSlaveAddress(slaves);
		return Redisson.create(config);
	}

	/**
	 * 集群模式
	 * @return
	 */
	@Bean
	@ConditionalOnExpression("'${redis.mode}'=='cluster'")
	public RedissonClient redisClusterClient(){
		Config config = new Config();
		config.useClusterServers().setScanInterval(2000).addNodeAddress(node.split(","));
		return Redisson.create(config);
	}

}
