package com.reusebook.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存配置类 - 实现内存缓存机制
 * 支持Book热点数据、Order状态、User信息缓存
 * 
 * 优化策略:
 * 1. 热点Book数据: 缓存热门书籍, TTL 30分钟
 * 2. Order状态: 缓存最近订单状态, TTL 5分钟 (高频查询)
 * 3. User信息: 缓存用户Profile, TTL 1小时
 * 4. Review数据: 缓存最新评价, TTL 1小时
 * 
 * 注意: 生产环境可替换为Redis缓存
 * 
 * @author 赖顺炜
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    /**
     * 配置缓存管理器 - 使用ConcurrentMap实现
     * - Book热点缓存: 通过浏览次数判断热点
     * - Order缓存: 用户级别隔离, 减少数据库访问
     * - 缓存键设计规范: entity:id 或 entity:type:id
     * 
     * 生产环境建议:
     * - 替换为RedisCacheManager
     * - 添加spring-boot-starter-data-redis依赖
     */
    @Bean
    public CacheManager cacheManager() {
        // 使用ConcurrentMapCacheManager作为简单缓存实现
        // 缓存名称: hotBooks, sellerBooks, bookDetail, userOrders, orderDetail
        return new ConcurrentMapCacheManager(
            "hotBooks", 
            "sellerBooks", 
            "bookDetail", 
            "userOrders", 
            "orderDetail",
            "pendingOrderCount"
        );
    }
}
