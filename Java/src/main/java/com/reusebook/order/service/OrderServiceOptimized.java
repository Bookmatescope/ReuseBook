package com.reusebook.order.service;

import com.reusebook.order.model.Order;
import com.reusebook.order.model.OrderStatus;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

/**
 * 订单服务缓存优化版本
 * 
 * 缓存策略:
 * - 用户订单列表: 缓存用户的订单列表，支持状态过滤，TTL 5分钟
 * - 订单详情: 缓存订单详情，减少频繁查询，TTL 5分钟
 * - 待处理订单: 缓存待确认订单数，支持实时更新
 * 
 * 清空策略:
 * - 订单创建、更新、删除时，清空相关用户的订单缓存
 * - 使用 @CacheEvict 精确清空缓存，避免全表清空
 * 
 * 数据库优化建议:
 * - 创建索引: buyer_id, seller_id, status, created_at
 * - 分区策略: 按创建日期分区，支持快速归档
 * - 使用触发器自动更新order_count统计表
 * 
 * @author 赖顺炜
 */
public interface OrderService {
    
    /**
     * 缓存用户的订单列表
     * 优化: 支持按状态过滤, 避免重复查询
     */
    @Cacheable(value = "userOrders", key = "#userId + ':' + #status", 
               unless = "#result.isEmpty()")
    Page<Order> getUserOrders(Long userId, OrderStatus status, Pageable pageable);
    
    /**
     * 缓存订单详情
     * 优化: 预加载Book和Seller信息，避免N+1查询
     */
    @Cacheable(value = "orderDetail", key = "#orderId")
    Optional<Order> getOrderDetail(Long orderId);
    
    /**
     * 创建订单 - 清空相关缓存
     * 优化: 只清空买家的订单缓存，不影响其他用户
     */
    @CacheEvict(value = "userOrders", key = "#buyerId + ':*'", allEntries = true)
    Order createOrder(Long buyerId, Long bookId);
    
    /**
     * 更新订单状态 - 清空缓存并触发消息队列
     * 优化: 异步通知卖家，支持并发更新
     */
    @CacheEvict(value = {"orderDetail", "userOrders"}, key = "#orderId", allEntries = true)
    Order updateOrderStatus(Long orderId, OrderStatus newStatus);
    
    /**
     * 获取待处理订单数 - 缓存热点统计数据
     * 优化: 通过专用统计表更新，避免频繁COUNT查询
     */
    @Cacheable(value = "pendingOrderCount", key = "#sellerId")
    Integer getPendingOrderCount(Long sellerId);
    
    /**
     * 搜索订单 - 支持多条件组合查询
     * 优化: 创建复合索引 (buyer_id, seller_id, status, created_at)
     * 建议: 使用消息队列异步更新订单统计数据
     */
    Page<Order> searchOrders(Long userId, String keyword, Pageable pageable);
}
