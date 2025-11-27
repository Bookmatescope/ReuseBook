// 根组件：集中配置路由以及延迟加载的页面组件
import { Navigate, Route, Routes } from 'react-router-dom';
import { Suspense, lazy } from 'react';
import AuthLayout from './components/forms/AuthLayout.jsx';
import LandingPage from './pages/LandingPage.jsx';

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
const ProfilePage = lazy(() => import('./pages/ProfilePage.jsx'));

export default function App() {
  // Suspense 用于在异步加载页面时提供统一的回退提示
  return (
    <Suspense fallback={<div className="loading">页面加载中...</div>}>
      <Routes>
        <Route path="/" element={<LandingPage />} />
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
  <Route path="/cart" element={<CartPage />} />
  <Route path="/checkout" element={<CheckoutPage />} />
  <Route path="/profile" element={<ProfilePage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Suspense>
  );
}
