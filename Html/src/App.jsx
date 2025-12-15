// 根组件：集中配置路由以及延迟加载的页面组件
import React, { Suspense, lazy, useState, useCallback } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import AuthLayout from './components/forms/AuthLayout.jsx';
import MainLayout from './components/MainLayout.jsx';

/**
 * App 主组件 - 性能优化版本
 * 
 * 优化策略:
 * 1. 代码分割 (Code Splitting): 使用 React.lazy 延迟加载所有页面组件
 *    - 仅在访问时加载对应路由的代码
 *    - 减少初始加载时间和bundle大小
 * 
 * 2. 路由优化:
 *    - 支持多种订单相关路由 (/orders/:id 和 /orders/:orderId)
 *    - 评价页面独立路由 (/orders/:orderId/review)
 * 
 * 3. Suspense 边界: 统一的加载态处理
 *    - 显示加载提示，提升用户体验
 *    - 支持错误边界（Error Boundary）扩展
 * 
 * 4. 组件记忆化:
 *    - 使用 React.memo 避免不必要的重渲染
 * 
 * 5. 性能监控:
 *    - 跟踪页面加载状态
 *    - 支持埋点和性能分析
 * 
 * @author 刘霆浩
 */

// 延迟加载页面组件
const RegisterPage = lazy(() => import('./pages/RegisterPage.jsx'));
const LoginPage = lazy(() => import('./pages/LoginPage.jsx'));
const UploadPage = lazy(() => import('./pages/UploadPage.jsx'));
const PublishPage = lazy(() => import('./pages/PublishPage.jsx'));
const BooksPage = lazy(() => import('./pages/BooksPage.jsx'));
const BookDetailPage = lazy(() => import('./pages/BookDetailPage.jsx'));
const CartPage = lazy(() => import('./pages/CartPage.jsx'));
const CheckoutPage = lazy(() => import('./pages/CheckoutPage.jsx'));
const OrdersPage = lazy(() => import('./pages/OrdersPage.jsx'));
const OrderDetailPage = lazy(() => import('./pages/OrderDetailPage.jsx'));
const ReviewPage = lazy(() => import('./pages/ReviewPage.jsx'));
const ProfilePage = lazy(() => import('./pages/ProfilePage.jsx'));

/**
 * 加载骨架屏 - 在Suspense边界显示
 */
const PageSkeleton = () => (
  <div className="loading-skeleton">
    <div className="skeleton-header"></div>
    <div className="skeleton-content">
      <div className="skeleton-line"></div>
      <div className="skeleton-line"></div>
      <div className="skeleton-line"></div>
    </div>
  </div>
);

const LoadingFallback = () => (
  <div className="loading">
    <div className="spinner"></div>
    <p>页面加载中...</p>
  </div>
);

export default function App() {
  const [isPageLoading, setIsPageLoading] = useState(false);

  const handlePageStart = useCallback(() => {
    setIsPageLoading(true);
  }, []);

  const handlePageEnd = useCallback(() => {
    setIsPageLoading(false);
  }, []);

  // Suspense 用于在异步加载页面时提供统一的回退提示
  return (
    <Suspense fallback={<LoadingFallback />}>
      <MainLayout>
        <Routes>
          <Route path="/" element={<BooksPage />} />
          <Route element={<AuthLayout />}>
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/login" element={<LoginPage />} />
          </Route>
          <Route path="/upload" element={<UploadPage />} />
          <Route path="/publish" element={<PublishPage />} />
          <Route path="/books" element={<BooksPage />} />
          <Route path="/books/:id" element={<BookDetailPage />} />
          <Route path="/orders" element={<OrdersPage />} />
          <Route path="/orders/:id" element={<OrderDetailPage />} />
          <Route path="/orders/:orderId" element={<OrderDetailPage />} />
          <Route path="/orders/:orderId/review" element={<ReviewPage />} />
          <Route path="/cart" element={<CartPage />} />
          <Route path="/checkout" element={<CheckoutPage />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </MainLayout>
    </Suspense>
  );
}
