// 根组件：集中配置路由以及延迟加载的页面组件
import { Navigate, Route, Routes } from 'react-router-dom';
import { Suspense, lazy } from 'react';
import AuthLayout from './components/forms/AuthLayout.jsx';
import LandingPage from './pages/LandingPage.jsx';

const RegisterPage = lazy(() => import('./pages/RegisterPage.jsx'));
const LoginPage = lazy(() => import('./pages/LoginPage.jsx'));
const UploadPage = lazy(() => import('./pages/UploadPage.jsx'));

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
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Suspense>
  );
}
