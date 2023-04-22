package ru.nsu.shelbogashev.tdgserver.config;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.OxmSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import ru.nsu.shelbogashev.tdgserver.model.ws.Lobby;

import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.database}")
    Integer redisDatabase;

    @Value("${spring.data.redis.password}")
    String password;

    @Value("${spring.data.redis.host}")
    String host;

    @Value("${spring.data.redis.port}")
    Integer port;

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);

        if (!password.trim().isEmpty()) {
            redisStandaloneConfiguration.setPassword(password);
        }

        if (redisDatabase > 0) {
            redisStandaloneConfiguration.setDatabase(redisDatabase);
        }

        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public <F> RedisTemplate<String, F> redisTemplate() {

        final RedisTemplate<String, F> redisTemplate = new RedisTemplate<>();

        JedisConnectionFactory factory = jedisConnectionFactory();
        Objects.requireNonNull(factory.getPoolConfig()).setMaxTotal(30);
        Objects.requireNonNull(factory.getPoolConfig()).setMinIdle(10);
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setKeySerializer(StringRedisSerializer.UTF_8);
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    @Bean
    public <F, S> HashOperations<F, String, S> hashOperations(RedisTemplate<F, S> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    @Bean
    public <F, S> SetOperations<F, S> setOperations(RedisTemplate<F, S> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        redisConnectionFactory.getConnection().serverCommands().flushDb();
        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig())
                .build();
    }

    private static final String LOCK_NAME = "lock";


}

