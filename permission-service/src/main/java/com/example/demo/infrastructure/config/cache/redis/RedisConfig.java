package com.example.demo.infrastructure.config.cache.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import io.lettuce.core.api.StatefulConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean("redis-properties")
    @ConditionalOnMissingBean(name = "redis-properties")
    @ConfigurationProperties(prefix = "microservice-redis")
    public RedisProperties redisProperties() {
        return new RedisProperties();
    }


    @Bean("microservice-redis-cache")
    @ConditionalOnMissingBean(name = "microservice-redis-cache")
    public RedisConnectionFactory redisConnectionFactory(
            @Qualifier("redis-properties") RedisProperties redisProperties) {

        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisProperties.getHost());
        configuration.setPort(redisProperties.getPort());
        configuration.setDatabase(redisProperties.getDatabase());

        GenericObjectPoolConfig<StatefulConnection<?, ?>> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(redisProperties.getLettuce().getPool().getMaxActive());
        poolConfig.setMaxIdle(redisProperties.getLettuce().getPool().getMaxIdle());
        poolConfig.setMinIdle(redisProperties.getLettuce().getPool().getMinIdle());

        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(redisProperties.getTimeout()))
                .poolConfig(poolConfig)
                .build();

        return new LettuceConnectionFactory(configuration, clientConfig);
    }

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(
            @Qualifier("microservice-redis-cache")
            RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setHashKeySerializer(RedisSerializer.string());

        redisTemplate.setValueSerializer(RedisSerializer.json());
        redisTemplate.setHashValueSerializer(RedisSerializer.json());

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

}
