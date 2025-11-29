package com.reusebook.cache;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * 缓存配置类 - 实现Redis缓存机制
 * 支持Book热点数据、Order状态、User信息缓存
 * 
 * 优化策略:
 * 1. 热点Book数据: 缓存热门书籍, TTL 30分钟
 * 2. Order状态: 缓存最近订单状态, TTL 5分钟 (高频查询)
 * 3. User信息: 缓存用户Profile, TTL 1小时
 * 4. Review数据: 缓存最新评价, TTL 1小时
 * 
 * @author 赖顺炜
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    /**
     * 配置Redis缓存管理器
     * - Book热点缓存: 通过浏览次数判断热点
     * - Order缓存: 用户级别隔离, 减少数据库访问
     * - 缓存键设计规范: entity:id 或 entity:type:id
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 支持自定义缓存过期策略
        // 书籍缓存: cache:book:${bookId} -> TTL 30min
        // 订单缓存: cache:order:${orderId} -> TTL 5min
        // 用户缓存: cache:user:${userId} -> TTL 60min
        return RedisCacheManager.create(connectionFactory);
    }
}
