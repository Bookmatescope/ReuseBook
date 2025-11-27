import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

/**
 * 个人中心页面
 */
export default function ProfilePage() {
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);
  const [editing, setEditing] = useState(false);
  const [nickname, setNickname] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // 模拟获取用户信息
    const mockProfile = {
      id: 'user-1',
      email: 'student@fzu.edu.cn',
      nickname: '书友小明',
      createdAt: '2025-11-20T10:00:00Z'
    };
    setTimeout(() => {
      setProfile(mockProfile);
      setNickname(mockProfile.nickname);
      setLoading(false);
    }, 300);
  }, []);

  const handleSave = () => {
    // 模拟保存
    setProfile({ ...profile, nickname });
    setEditing(false);
    alert('保存成功！');
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  if (loading) {
    return <div className="page-container"><p>加载中...</p></div>;
  }

  return (
    <div className="page-container">
      <h1>个人中心</h1>

      {/* 用户信息卡片 */}
      <section className="profile-card">
        <div className="profile-header">
          <div className="avatar">
            {profile.nickname.charAt(0)}
          </div>
          <div className="profile-info">
            {editing ? (
              <input
                type="text"
                value={nickname}
                onChange={(e) => setNickname(e.target.value)}
                className="nickname-input"
              />
            ) : (
              <h2>{profile.nickname}</h2>
            )}
            <p className="email">{profile.email}</p>
          </div>
          {editing ? (
            <div className="edit-actions">
              <button className="btn-primary" onClick={handleSave}>保存</button>
              <button className="btn-secondary" onClick={() => { setEditing(false); setNickname(profile.nickname); }}>取消</button>
            </div>
          ) : (
            <button className="btn-secondary" onClick={() => setEditing(true)}>编辑</button>
          )}
        </div>
        <p className="join-date">加入时间：{new Date(profile.createdAt).toLocaleDateString('zh-CN')}</p>
      </section>

      {/* 功能入口 */}
      <section className="profile-menu">
        <div className="menu-item" onClick={() => navigate('/orders')}>
          <span className="menu-icon">📦</span>
          <span className="menu-text">我的订单</span>
          <span className="menu-arrow">›</span>
        </div>
        <div className="menu-item" onClick={() => navigate('/addresses')}>
          <span className="menu-icon">📍</span>
          <span className="menu-text">收货地址</span>
          <span className="menu-arrow">›</span>
        </div>
        <div className="menu-item" onClick={() => navigate('/publish')}>
          <span className="menu-icon">📚</span>
          <span className="menu-text">我发布的书籍</span>
          <span className="menu-arrow">›</span>
        </div>
        <div className="menu-item" onClick={() => navigate('/cart')}>
          <span className="menu-icon">🛒</span>
          <span className="menu-text">购物车</span>
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
