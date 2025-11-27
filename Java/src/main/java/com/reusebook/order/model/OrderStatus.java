package com.reusebook.order.model;

/**
 * 订单状态枚举（面交模式）
 */
public enum OrderStatus {
    /** 待确认 - 买家下单后等待卖家确认 */
    PENDING,
    /** 已确认 - 卖家确认订单，约定面交 */
    CONFIRMED,
    /** 面交中 - 双方约定见面交易 */
    MEETUP,
    /** 已完成 - 交易成功 */
    COMPLETED,
    /** 已取消 - 订单取消 */
    CANCELLED
}
