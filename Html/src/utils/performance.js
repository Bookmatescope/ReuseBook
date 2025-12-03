/**
 * 前端性能优化配置
 * 
 * 优化内容:
 * 1. 图片懒加载 (Lazy Loading)
 * 2. 虚拟滚动 (Virtual Scrolling)
 * 3. 防抖和节流 (Debounce & Throttle)
 * 4. 组件记忆化 (React.memo, useMemo)
 * 5. 事件委托 (Event Delegation)
 * 6. CSS优化
 * 
 * @author 刘霆浩
 */

/**
 * 防抖函数 - 用于搜索框、输入框等高频事件
 * 优化: 避免频繁调用API导致浏览器卡顿
 * 
 * 使用场景:
 * - 搜索框输入
 * - 窗口resize事件
 * - 自动保存
 */
export function debounce(func, wait = 300) {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
}

/**
 * 节流函数 - 用于滚动、鼠标移动等持续触发的事件
 * 优化: 确保回调在指定时间间隔内最多执行一次
 * 
 * 使用场景:
 * - 页面滚动加载
 * - 计算元素位置
 */
export function throttle(func, limit = 100) {
  let inThrottle;
  return function(...args) {
    if (!inThrottle) {
      func.apply(this, args);
      inThrottle = true;
      setTimeout(() => inThrottle = false, limit);
    }
  };
}

/**
 * 图片懒加载 Hook
 * 优化: 仅加载可见区域的图片，减少初始加载
 * 
 * 使用:
 * const ref = useIntersectionObserver(onVisible);
 * <img ref={ref} data-src="url" alt="" />
 */
export function useIntersectionObserver(callback) {
  const elementRef = React.useRef(null);

  React.useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          callback(entry);
          observer.unobserve(entry.target);
        }
      },
      {
        rootMargin: '50px' // 提前50px开始加载
      }
    );

    if (elementRef.current) {
      observer.observe(elementRef.current);
    }

    return () => observer.disconnect();
  }, [callback]);

  return elementRef;
}

/**
 * 虚拟滚动组件 - 用于大列表优化
 * 优化: 只渲染可见的列表项，避免DOM节点过多
 * 
 * 使用场景:
 * - 书籍列表 (几千本书籍)
 * - 订单历史列表
 */
export function useVirtualScroll(items, itemHeight, containerHeight) {
  const [scrollTop, setScrollTop] = React.useState(0);
  
  const startIndex = Math.floor(scrollTop / itemHeight);
  const endIndex = Math.ceil((scrollTop + containerHeight) / itemHeight);
  
  // 额外预加载区域，避免快速滚动时出现白屏
  const visibleRange = {
    start: Math.max(0, startIndex - 5),
    end: Math.min(items.length, endIndex + 5)
  };

  const visibleItems = items.slice(visibleRange.start, visibleRange.end);
  const offsetY = visibleRange.start * itemHeight;

  return {
    visibleItems,
    offsetY,
    totalHeight: items.length * itemHeight,
    handleScroll: (e) => setScrollTop(e.target.scrollTop)
  };
}

/**
 * 缓存请求结果 - 减少重复API调用
 * 优化: 相同URL的请求在指定时间内使用缓存
 */
class RequestCache {
  constructor(ttl = 5 * 60 * 1000) { // 5分钟TTL
    this.cache = new Map();
    this.ttl = ttl;
  }

  set(key, value) {
    this.cache.set(key, {
      value,
      timestamp: Date.now()
    });
  }

  get(key) {
    const item = this.cache.get(key);
    if (!item) return null;

    const isExpired = Date.now() - item.timestamp > this.ttl;
    if (isExpired) {
      this.cache.delete(key);
      return null;
    }

    return item.value;
  }

  clear() {
    this.cache.clear();
  }
}

export const requestCache = new RequestCache();

/**
 * 性能监控工具
 * 优化: 跟踪关键性能指标
 * 
 * 使用:
 * - First Contentful Paint (FCP)
 * - Largest Contentful Paint (LCP)
 * - Cumulative Layout Shift (CLS)
 */
export function usePerformanceMonitoring() {
  React.useEffect(() => {
    // Web Vitals 监控
    if ('PerformanceObserver' in window) {
      // LCP 监控
      const lcpObserver = new PerformanceObserver((list) => {
        const entries = list.getEntries();
        const lastEntry = entries[entries.length - 1];
        console.log('LCP:', lastEntry.renderTime || lastEntry.loadTime);
      });

      // CLS 监控
      const clsObserver = new PerformanceObserver((list) => {
        for (const entry of list.getEntries()) {
          if (!entry.hadRecentInput) {
            console.log('CLS:', entry.value);
          }
        }
      });

      try {
        lcpObserver.observe({ entryTypes: ['largest-contentful-paint'] });
        clsObserver.observe({ entryTypes: ['layout-shift'] });
      } catch (e) {
        console.warn('Performance observer not supported:', e);
      }

      return () => {
        lcpObserver.disconnect();
        clsObserver.disconnect();
      };
    }
  }, []);
}

/**
 * CSS 性能优化建议
 * 
 * 1. 使用 CSS Grid/Flexbox 替代浮动布局
 *    - 避免频繁重排
 * 
 * 2. 使用 transform 和 opacity 动画
 *    - 这些属性动画不触发重排
 *    - 使用 will-change: transform 提示浏览器
 * 
 * 3. 使用 contain 属性隔离样式影响范围
 *    - contain: content 避免元素修改影响外部
 * 
 * 4. 避免深层级选择器
 *    - 不使用: .container .item .sub-item .text
 *    - 改为: .item-text
 */
export const performanceOptimizations = `
/* CSS 性能优化 */

/* 1. 虚拟滚动容器 - 使用contain隔离 */
.virtual-scroll-container {
  contain: content;
  overflow-y: auto;
  height: 500px;
}

/* 2. 列表项 - 避免flex深层嵌套 */
.book-card {
  display: grid;
  grid-template-columns: 120px 1fr;
  gap: 16px;
  will-change: transform;
}

/* 3. 动画优化 - 使用transform替代top/left */
.book-card:hover {
  transform: translateY(-4px);
  transition: transform 0.2s ease-out;
}

/* 4. 图片懒加载 - 避免布局抖动 */
.book-image {
  aspect-ratio: 3/4;
  background-color: #f0f0f0;
  object-fit: cover;
}

/* 5. 减少重排 - 使用grid布局 */
.books-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 16px;
  contain: layout;
}
`;
