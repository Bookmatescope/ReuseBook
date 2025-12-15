// 导航栏组件：显示登录/注册按钮或已登录用户信息
import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';

/**
 * Navbar 导航栏组件
 * - 未登录时：显示注册和登录按钮
 * - 已登录时：显示用户信息和退出按钮
 */
export default function Navbar() {
  const [user, setUser] = useState(null);
  const navigate = useNavigate();

  // 从 localStorage 获取用户昵称
  const getUserInfo = () => {
    const token = localStorage.getItem('token');
    const profileStr = localStorage.getItem('reusebook_profile');
    if (token && profileStr) {
      try {
        const profile = JSON.parse(profileStr);
        return { nickname: profile.nickname || profile.email || '用户' };
      } catch {
        return null;
      }
    }
    return null;
  };

  // 检查登录状态
  useEffect(() => {
    const userInfo = getUserInfo();
    if (userInfo) {
      setUser(userInfo);
    }
  }, []);

  // 监听登录状态变化
  useEffect(() => {
    const handleStorageChange = () => {
      const userInfo = getUserInfo();
      if (userInfo) {
        setUser(userInfo);
      } else {
        setUser(null);
      }
    };

    window.addEventListener('storage', handleStorageChange);
    // 自定义事件用于同页面登录状态更新
    window.addEventListener('loginStateChange', handleStorageChange);
    
    return () => {
      window.removeEventListener('storage', handleStorageChange);
      window.removeEventListener('loginStateChange', handleStorageChange);
    };
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('reusebook_token');
    localStorage.removeItem('reusebook_profile');
    setUser(null);
    window.dispatchEvent(new Event('loginStateChange'));
    navigate('/');
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <Link to="/" className="navbar-brand">
          📚 书海拾贝
        </Link>
        
        <div className="navbar-links">
          <Link to="/" className="nav-link">书籍市场</Link>
          <Link to="/publish" className="nav-link">发布书籍</Link>
          {user && <Link to="/cart" className="nav-link">🛒 购物车</Link>}
          {user && <Link to="/orders" className="nav-link">我的订单</Link>}
        </div>

        <div className="navbar-auth">
          {user ? (
            <div className="user-menu">
              <Link to="/profile" className="user-info">
                <span className="user-avatar">👤</span>
                <span className="user-name">{user.nickname}</span>
              </Link>
              <button onClick={handleLogout} className="btn-logout">
                退出
              </button>
            </div>
          ) : (
            <div className="auth-buttons">
              <Link to="/login" className="btn-login">登录</Link>
              <Link to="/register" className="btn-register">注册</Link>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
}
