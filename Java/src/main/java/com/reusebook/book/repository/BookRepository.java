package com.reusebook.book.repository;

import com.reusebook.book.model.Book;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 书籍仓储接口 - 支持缓存和查询优化
 * 
 * 缓存策略:
 * - 热点书籍: 按viewCount排序的热门书籍缓存，TTL 30分钟
 * - 卖家书籍: 按卖家维度缓存，支持精确清空
 * - 单个书籍: 详情缓存，减少重复查询
 * 
 * SQL优化建议:
 * - 创建索引: seller_id, status, created_at, viewCount
 * - 使用分页避免全表扫描
 * - JOIN预加载关联数据，避免N+1问题
 * - 考虑使用Elasticsearch进行全文搜索优化
 * 
 * @author 赖顺炜
 */
public interface BookRepository {

    Book save(Book book);

    Optional<Book> findById(UUID id);

    List<Book> findAll();

    List<Book> findByIsbn(String isbn);
    
    /**
     * 查询热点书籍
     * 优化: 分页查询避免一次性加载大量数据
     * @param limit 限制数量
     */
    List<Book> findHotBooks(int limit);
    
    /**
     * 查询卖家发布的书籍
     * 优化: 按卖家ID维度缓存，支持更新时清空
     */
    List<Book> findBySellerId(UUID sellerId);
    
    /**
     * 搜索书籍 - 全文搜索优化
     * 优化建议: 使用Elasticsearch替代LIKE查询
     */
    List<Book> searchBooks(String keyword);
}
