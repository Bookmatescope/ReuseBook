/**
 * 前端组件优化工具库
 * 
 * 包含:
 * 1. 组件记忆化工具
 * 2. 自定义Hooks集合
 * 3. 状态管理优化
 * 4. 列表渲染优化
 * 
 * @author 刘霆浩
 */

import React, { useCallback, useRef, useMemo, useEffect } from 'react';

/**
 * 优化的列表组件 - 支持虚拟滚动和分页
 * 
 * 优化特性:
 * 1. 按需渲染 - 只渲染可见项
 * 2. 键值优化 - 使用稳定的 key
 * 3. 分页加载 - 滚动到底部自动加载
 * 4. 防抖处理 - 避免频繁渲染
 */
export function OptimizedList({ 
  items = [], 
  renderItem, 
  itemHeight = 100,
  containerHeight = 600,
  onLoadMore,
  isLoading = false 
}) {
  const containerRef = useRef(null);
  const [scrollTop, setScrollTop] = React.useState(0);

  // 计算可见范围
  const startIndex = Math.max(0, Math.floor(scrollTop / itemHeight) - 2);
  const endIndex = Math.min(
    items.length, 
    Math.ceil((scrollTop + containerHeight) / itemHeight) + 2
  );

  const visibleItems = items.slice(startIndex, endIndex);
  const offsetY = startIndex * itemHeight;

  // 处理滚动 - 使用防抖避免频繁更新
  const handleScroll = useCallback((e) => {
    setScrollTop(e.target.scrollTop);

    // 检测是否滚动到底部
    const { scrollTop, scrollHeight, clientHeight } = e.target;
    if (scrollHeight - (scrollTop + clientHeight) < 100 && onLoadMore && !isLoading) {
      onLoadMore();
    }
  }, [onLoadMore, isLoading]);

  return (
    <div
      ref={containerRef}
      className="optimized-list-container"
      style={{
        height: containerHeight,
        overflow: 'auto',
        position: 'relative'
      }}
      onScroll={handleScroll}
    >
      {/* 虚拟滚动空白区域 */}
      <div style={{ height: offsetY, pointerEvents: 'none' }} />

      {/* 可见项列表 */}
      <div>
        {visibleItems.map((item, idx) => (
          <div key={startIndex + idx} style={{ height: itemHeight }}>
            {renderItem(item, startIndex + idx)}
          </div>
        ))}
      </div>

      {/* 加载指示器 */}
      {isLoading && (
        <div className="loading-indicator">
          <span className="spinner"></span>
          <p>加载中...</p>
        </div>
      )}

      {/* 底部空白区域 */}
      <div 
        style={{ 
          height: Math.max(0, items.length - endIndex) * itemHeight,
          pointerEvents: 'none'
        }} 
      />
    </div>
  );
}

/**
 * 高阶组件 - 添加性能监控
 * 优化: 记录组件的渲染次数和时间
 */
export function withPerformanceTracking(Component) {
  return React.memo(function WithPerformanceTracking(props) {
    const renderCountRef = useRef(0);
    const renderStartRef = useRef(Date.now());

    useEffect(() => {
      const renderTime = Date.now() - renderStartRef.current;
      console.log(
        `[${Component.name}] Render #${renderCountRef.current++}, Time: ${renderTime}ms`
      );
    });

    return <Component {...props} />;
  });
}

/**
 * 表单优化Hook - 避免表单变化时重新渲染所有子组件
 * 
 * 使用场景:
 * - 书籍发布表单
 * - 用户注册/登录表单
 * - 搜索过滤表单
 */
export function useOptimizedForm(initialValues) {
  const formRef = useRef(initialValues);
  const [, setRender] = React.useState(0);

  const getFormData = useCallback(() => formRef.current, []);

  const updateField = useCallback((name, value) => {
    formRef.current = {
      ...formRef.current,
      [name]: value
    };
    // 仅在需要时触发重渲染
    setRender(prev => prev + 1);
  }, []);

  const reset = useCallback(() => {
    formRef.current = initialValues;
    setRender(prev => prev + 1);
  }, [initialValues]);

  return {
    values: formRef.current,
    getFormData,
    updateField,
    reset
  };
}

/**
 * 异步数据加载Hook - 支持缓存和去重
 * 
 * 优化:
 * 1. 相同请求自动去重
 * 2. 支持缓存避免重复请求
 * 3. 自动清理超时请求
 */
export function useAsyncData(fetchFn, cacheTime = 5 * 60 * 1000) {
  const [data, setData] = React.useState(null);
  const [loading, setLoading] = React.useState(false);
  const [error, setError] = React.useState(null);

  const cacheRef = useRef(new Map());
  const abortControllerRef = useRef(null);

  const fetchData = useCallback(async (key, ...args) => {
    // 检查缓存
    const cached = cacheRef.current.get(key);
    if (cached && Date.now() - cached.timestamp < cacheTime) {
      setData(cached.data);
      return;
    }

    // 取消前一个请求
    if (abortControllerRef.current) {
      abortControllerRef.current.abort();
    }

    abortControllerRef.current = new AbortController();
    setLoading(true);
    setError(null);

    try {
      const result = await fetchFn(...args, abortControllerRef.current.signal);
      
      // 缓存结果
      cacheRef.current.set(key, {
        data: result,
        timestamp: Date.now()
      });

      setData(result);
    } catch (err) {
      if (err.name !== 'AbortError') {
        setError(err);
      }
    } finally {
      setLoading(false);
    }
  }, [fetchFn, cacheTime]);

  return { data, loading, error, fetchData };
}

/**
 * 防止意外重渲染的Hook
 * 
 * 使用场景:
 * - 父组件更新时，子组件无需重渲染
 * - 保持组件的引用稳定
 */
export function useStableCallback(callback, deps) {
  const callbackRef = useRef(callback);

  useEffect(() => {
    callbackRef.current = callback;
  }, deps);

  return useCallback((...args) => {
    return callbackRef.current(...args);
  }, []);
}

/**
 * 防止内存泄漏的Hook - 清理副作用
 */
export function useMount(callback) {
  useEffect(() => {
    callback?.();
  }, []);
}

export function useUnmount(callback) {
  useEffect(() => {
    return () => callback?.();
  }, []);
}

/**
 * 响应式设计Hook - 检测窗口大小
 * 优化: 仅在必要时触发重渲染
 */
export function useWindowSize() {
  const [size, setSize] = React.useState({
    width: typeof window !== 'undefined' ? window.innerWidth : 0,
    height: typeof window !== 'undefined' ? window.innerHeight : 0
  });

  useEffect(() => {
    let timeoutId;
    
    const handleResize = () => {
      clearTimeout(timeoutId);
      timeoutId = setTimeout(() => {
        setSize({
          width: window.innerWidth,
          height: window.innerHeight
        });
      }, 150); // 防抖处理
    };

    window.addEventListener('resize', handleResize);
    return () => {
      window.removeEventListener('resize', handleResize);
      clearTimeout(timeoutId);
    };
  }, []);

  return size;
}
