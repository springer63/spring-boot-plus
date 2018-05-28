package com.github.boot.framework.config;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.boot.framework.support.serializer.KryoFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redis配置
 * @author cjh
 * @version 1.0
 * @date 2017/3/15 18:29:49
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
		config.setCodec(new CacheKryoCodec());
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
		config.setCodec(new CacheKryoCodec());
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
		config.setCodec(new CacheKryoCodec());
		config.useClusterServers().setScanInterval(2000).addNodeAddress(node.split(","));
		return Redisson.create(config);
	}

	public static class CacheKryoCodec implements Codec {

		private static final Logger logger = LoggerFactory.getLogger(RedisConfigure.class);

		private static final KryoFactory kryoFactory = KryoFactory.getFactory();

		private final Decoder<Object> decoder = (buf, state) -> {
            Kryo kryo = null;
            try {
                kryo = kryoFactory.getKryo();
                return kryo.readClassAndObject(new Input(new ByteBufInputStream(buf)));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return null;
            } finally {
                if (kryo != null) {
                    kryoFactory.returnKryo(kryo);
                }
            }
        };

		private final Encoder encoder = in -> {
            Kryo kryo = null;
            ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
            try {
                ByteBufOutputStream baos = new ByteBufOutputStream(out);
                Output output = new Output(baos);
                kryo = kryoFactory.getKryo();
                kryo.writeClassAndObject(output, in);
                output.close();
                return baos.buffer();
            } catch (Exception e) {
                out.release();
                throw new RuntimeException(e);
            } finally {
                if (kryo != null) {
                    kryoFactory.returnKryo(kryo);
                }
            }
        };

		@Override
		public Decoder<Object> getMapValueDecoder() {
			return this.decoder;
		}

		@Override
		public Encoder getMapValueEncoder() {
			return this.encoder;
		}

		@Override
		public Decoder<Object> getMapKeyDecoder() {
			return this.decoder;
		}

		@Override
		public Encoder getMapKeyEncoder() {
			return this.encoder;
		}

		@Override
		public Decoder<Object> getValueDecoder() {
			return this.decoder;
		}

		@Override
		public Encoder getValueEncoder() {
			return this.encoder;
		}
	}

}
