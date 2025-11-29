package com.reusebook.book.repository;

import com.reusebook.book.model.Book;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 书籍仓储接口 - 支持缓存和分页查询优化
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
     * 缓存热点书籍查询
     * 优化: 分页查询避免一次性加载大量数据
     */
    @Cacheable(value = "hotBooks", unless = "#result.isEmpty()")
    Page<Book> findHotBooks(Pageable pageable);
    
    /**
     * 缓存卖家发布的书籍
     * 优化: 按卖家ID维度缓存，支持更新时清空
     */
    @Cacheable(value = "sellerBooks", key = "#sellerId")
    Page<Book> findBySellerId(Long sellerId, Pageable pageable);
    
    /**
     * 缓存单个书籍详情
     * 优化: 预加载关联数据，避免N+1查询
     */
    @Cacheable(value = "bookDetail", key = "#id")
    Book findByIdWithDetails(UUID id);
    
    /**
     * 搜索书籍 - 全文搜索优化
     * 优化建议: 使用Elasticsearch替代LIKE查询
     */
    Page<Book> searchBooks(String keyword, Pageable pageable);
}
