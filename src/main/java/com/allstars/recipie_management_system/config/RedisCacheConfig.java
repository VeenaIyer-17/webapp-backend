package com.allstars.recipie_management_system.config;

import com.allstars.recipie_management_system.entity.Recipie;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisCacheConfig extends CachingConfigurerSupport implements CachingConfigurer {

    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Value("${redis.timeout.secs:1}")
    private int redisTimeoutInSecs;

    @Value("${redis.socket.timeout.secs:1}")
    private int redisSocketTimeoutInSecs;

    @Value("${spring.redis.ttl.minutes:10}")
    private int redisDataTTL;

    @Value("${spring.redis.password}")
    private String password;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {

        final SocketOptions socketOptions = SocketOptions.builder().connectTimeout(Duration.ofSeconds(redisSocketTimeoutInSecs)).build();

        final ClientOptions clientOptions = ClientOptions.builder().socketOptions(socketOptions).build();

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(redisTimeoutInSecs)).clientOptions(clientOptions).build();
//        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
//        serverConfig.setPassword(password);

        final LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(sentinelConfig(),clientConfig);
        lettuceConnectionFactory.setValidateConnection(true);

        return lettuceConnectionFactory;
//        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
//                .master(redisProperties.getSentinel().getMaster());
//        redisProperties.getSentinel().getNodes().forEach(s -> sentinelConfig.sentinel(s, Integer.valueOf(redisProperties.getPort())));
//        sentinelConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));
//        return new LettuceConnectionFactory(sentinelConfig);
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(Jackson2JsonRedisSerializer<Recipie> customJackson2JsonRedisSerializer) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<Object, Object>();
        ObjectMapper om = new ObjectMapper();
//        Jackson2JsonRedisSerializer<Recipie> customJackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Recipie>(Recipie.class);
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        customJackson2JsonRedisSerializer.setObjectMapper(om);
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setDefaultSerializer(customJackson2JsonRedisSerializer);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(customJackson2JsonRedisSerializer);
        return redisTemplate;
    }


    @Bean
    public RedisCacheManager redisCacheManager(LettuceConnectionFactory lettuceConnectionFactory,Jackson2JsonRedisSerializer<Recipie> customRedisSerializer) {

        /**
         * If we want to use JSON Serialized with own object mapper then use the below config snippet
         */
         RedisCacheConfiguration redisCacheConfiguration =
         RedisCacheConfiguration.defaultCacheConfig().disableCachingNullValues()
         .entryTtl(Duration.ofMinutes(redisDataTTL)).serializeValuesWith(RedisSerializationContext.SerializationPair
         .fromSerializer(customRedisSerializer));

        redisCacheConfiguration.usePrefix();

        RedisCacheManager redisCacheManager = RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(lettuceConnectionFactory)
                .cacheDefaults(redisCacheConfiguration).build();

        redisCacheManager.setTransactionAware(true);
        return redisCacheManager;
    }

    @Bean
    public Jackson2JsonRedisSerializer<Recipie> customRedisSerializer(){
        Jackson2JsonRedisSerializer<Recipie> customJackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Recipie>(Recipie.class);
        ObjectMapper om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        customJackson2JsonRedisSerializer.setObjectMapper(om);
        return customJackson2JsonRedisSerializer;
    }

    public @Bean RedisSentinelConfiguration sentinelConfig() {
       RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration().master("mymaster").sentinel( redisHost, 26379);
        redisSentinelConfiguration.setPassword(RedisPassword.of(password));
       return redisSentinelConfiguration;
    }
}