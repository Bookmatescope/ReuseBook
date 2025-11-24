import { NavLink, Outlet } from 'react-router-dom';

export default function AuthLayout() {
  return (
    <div className="auth-layout">
      <header className="auth-header">
        <h1>ReuseBook</h1>
        <p>分享书海，传递知识</p>
      </header>
      <div className="auth-content">
        <nav className="auth-nav">
          <NavLink to="/register" className={({ isActive }) => (isActive ? 'active' : '')}>
            注册新账号
          </NavLink>
          <NavLink to="/login" className={({ isActive }) => (isActive ? 'active' : '')}>
            登录已有账号
          </NavLink>
        </nav>
        <main className="auth-main">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
