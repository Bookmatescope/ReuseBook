import { Navigate, Route, Routes } from 'react-router-dom';
import { Suspense, lazy } from 'react';
import AuthLayout from './components/forms/AuthLayout.jsx';
import LandingPage from './pages/LandingPage.jsx';

const RegisterPage = lazy(() => import('./pages/RegisterPage.jsx'));
const LoginPage = lazy(() => import('./pages/LoginPage.jsx'));

export default function App() {
  return (
    <Suspense fallback={<div className="loading">页面加载中...</div>}>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route element={<AuthLayout />}>
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/login" element={<LoginPage />} />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Suspense>
  );
}
