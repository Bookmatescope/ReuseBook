import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const API_BASE = 'http://localhost:8081/api';

/**
 * ProfilePage 个人中心页面
 * 
 * 功能:
 * 1. 展示用户基本信息（头像、昵称、邮箱）
 * 2. 支持编辑昵称
 * 3. 功能入口（订单、地址、发布、购物车）
 * 4. 统计数据展示
 * 5. 退出登录
 * 
 * @author 杨浩 - Day7 完善
 */
export default function ProfilePage() {
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);
  const [stats, setStats] = useState({ orders: 0, published: 0, reviews: 0 });
  const [editing, setEditing] = useState(false);
  const [nickname, setNickname] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  // 获取登录Token
  const getAuthToken = () => localStorage.getItem('token');
  const isLoggedIn = () => !!getAuthToken();

  useEffect(() => {
    if (!isLoggedIn()) {
      navigate('/login');
      return;
    }
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      setLoading(true);
      const res = await fetch(`${API_BASE}/auth/profile`, {
        headers: {
          'Authorization': `Bearer ${getAuthToken()}`
        }
      });
      
      if (!res.ok) {
        if (res.status === 401) {
          localStorage.removeItem('token');
          navigate('/login');
          return;
        }
        throw new Error('获取用户信息失败');
      }
      
      const data = await res.json();
      setProfile(data);
      setNickname(data.nickname);

      // 获取统计数据
      fetchStats();
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const fetchStats = async () => {
    try {
      // 获取订单数
      const ordersRes = await fetch(`${API_BASE}/orders`, {
        headers: { 'Authorization': `Bearer ${getAuthToken()}` }
      });
      const orders = ordersRes.ok ? await ordersRes.json() : [];

      // 获取发布的书籍数
      const booksRes = await fetch(`${API_BASE}/books/my`, {
        headers: { 'Authorization': `Bearer ${getAuthToken()}` }
      });
      const books = booksRes.ok ? await booksRes.json() : [];

      setStats({
        orders: orders.length,
        published: books.length,
        reviews: 0 // 暂不统计
      });
    } catch (err) {
      console.error('获取统计数据失败:', err);
    }
  };

  const handleSave = async () => {
    if (!nickname.trim()) {
      alert('昵称不能为空');
      return;
    }

    try {
      setSaving(true);
      const res = await fetch(`${API_BASE}/user/profile`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${getAuthToken()}`
        },
        body: JSON.stringify({ nickname })
      });

      if (!res.ok) throw new Error('保存失败');
      
      const updated = await res.json();
      setProfile(updated);
      setEditing(false);
      alert('保存成功！');
    } catch (err) {
      alert(err.message);
    } finally {
      setSaving(false);
    }
  };

  const handleLogout = () => {
    if (confirm('确定要退出登录吗？')) {
      localStorage.removeItem('token');
      navigate('/login');
    }
  };

  if (loading) {
    return <div className="page-container"><p className="loading">加载中...</p></div>;
  }

  if (error) {
    return <div className="page-container"><p className="error-message">{error}</p></div>;
  }

  return (
    <div className="page-container profile-page">
      <h1>个人中心</h1>

      {/* 用户信息卡片 */}
      <section className="profile-card">
        <div className="profile-header">
          <div className="avatar">
            {profile?.nickname?.charAt(0) || '?'}
          </div>
          <div className="profile-info">
            {editing ? (
              <div className="edit-nickname">
                <input
                  type="text"
                  value={nickname}
                  onChange={(e) => setNickname(e.target.value)}
                  className="nickname-input"
                  placeholder="请输入昵称"
                  maxLength={20}
                />
              </div>
            ) : (
              <h2 className="user-nickname">{profile?.nickname}</h2>
            )}
            <p className="email">{profile?.email}</p>
          </div>
          {editing ? (
            <div className="edit-actions">
              <button 
                className="profile-btn profile-btn-primary" 
                onClick={handleSave}
                disabled={saving}
              >
                {saving ? '保存中...' : '保存'}
              </button>
              <button 
                className="profile-btn profile-btn-secondary" 
                onClick={() => { setEditing(false); setNickname(profile?.nickname); }}
              >
                取消
              </button>
            </div>
          ) : (
            <button className="profile-btn profile-btn-edit" onClick={() => setEditing(true)}>
              ✏️ 编辑
            </button>
          )}
        </div>
        <p className="join-date">
          🗓️ 加入时间：{new Date(profile?.createdAt).toLocaleDateString('zh-CN')}
        </p>
      </section>

      {/* 统计数据 */}
      <section className="profile-stats">
        <div className="stat-item" onClick={() => navigate('/orders')}>
          <span className="stat-value">{stats.orders}</span>
          <span className="stat-label">订单</span>
        </div>
        <div className="stat-item" onClick={() => navigate('/publish')}>
          <span className="stat-value">{stats.published}</span>
          <span className="stat-label">发布</span>
        </div>
        <div className="stat-item">
          <span className="stat-value">{stats.reviews}</span>
          <span className="stat-label">评价</span>
        </div>
      </section>

      {/* 功能入口 */}
      <section className="profile-menu">
        <div className="menu-item" onClick={() => navigate('/orders')}>
          <span className="menu-icon">📦</span>
          <span className="menu-text">我的订单</span>
          <span className="menu-arrow">›</span>
        </div>
        <div className="menu-item" onClick={() => navigate('/cart')}>
          <span className="menu-icon">🛒</span>
          <span className="menu-text">购物车</span>
          <span className="menu-arrow">›</span>
        </div>
        <div className="menu-item" onClick={() => navigate('/publish')}>
          <span className="menu-icon">📚</span>
          <span className="menu-text">发布书籍</span>
          <span className="menu-arrow">›</span>
        </div>
        <div className="menu-item" onClick={() => navigate('/books')}>
          <span className="menu-icon">🔍</span>
          <span className="menu-text">浏览书籍</span>
          <span className="menu-arrow">›</span>
        </div>
      </section>

      {/* 退出登录 */}
      <button className="btn-danger logout-btn" onClick={handleLogout}>
        退出登录
      </button>
    </div>
  );
}
