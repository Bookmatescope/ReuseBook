import { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';

const API_BASE = 'http://localhost:8081/api';

// 订单状态映射
const STATUS_MAP = {
  PENDING: { label: '待确认', color: '#ff9800', bgColor: '#fff3e0', next: 'CONFIRMED', nextLabel: '确认订单', icon: '⏳' },
  CONFIRMED: { label: '已确认', color: '#2196f3', bgColor: '#e3f2fd', next: 'MEETUP', nextLabel: '开始面交', icon: '✅' },
  MEETUP: { label: '面交中', color: '#9c27b0', bgColor: '#f3e5f5', next: 'COMPLETED', nextLabel: '确认完成', icon: '🤝' },
  COMPLETED: { label: '已完成', color: '#4caf50', bgColor: '#e8f5e9', next: null, icon: '🎉' },
  CANCELLED: { label: '已取消', color: '#9e9e9e', bgColor: '#fafafa', next: null, icon: '❌' }
};

export default function OrderDetailPage(){
  const { id } = useParams();
  const navigate = useNavigate();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [updating, setUpdating] = useState(false);

  useEffect(() => {
    fetchOrder();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const fetchOrder = async () => {
    try{
      setLoading(true);
      const res = await fetch(`${API_BASE}/orders/${id}`, {
        headers: { 'Authorization': `Bearer ${localStorage.getItem('token') || ''}` }
      });
      if(!res.ok) throw new Error('获取订单详情失败');
      const data = await res.json();
      setOrder(data);
    }catch(err){
      setError(err.message);
    }finally{
      setLoading(false);
    }
  };

  const updateStatus = async (newStatus) => {
    try {
      setUpdating(true);
      const res = await fetch(`${API_BASE}/orders/${id}/status`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token') || ''}`
        },
        body: JSON.stringify({ status: newStatus })
      });
      if (!res.ok) throw new Error('更新状态失败');
      const data = await res.json();
      setOrder(data);
    } catch (err) {
      alert(err.message);
    } finally {
      setUpdating(false);
    }
  };

  const cancelOrder = async () => {
    if (!confirm('确定要取消此订单吗？')) return;
    await updateStatus('CANCELLED');
  };

  if(loading) return (<div className="page-container order-detail-page"><p className="loading">加载中...</p></div>);
  if(error) return (<div className="page-container order-detail-page"><p className="error-message">{error}</p></div>);
  if(!order) return (<div className="page-container order-detail-page"><p>找不到该订单</p></div>);

  const statusInfo = STATUS_MAP[order.status] || { label: order.status, color: '#666', bgColor: '#f5f5f5', icon: '📦' };

  return (
    <div className="page-container order-detail-page">
      <header className="detail-header">
        <Link to="/orders" className="back-link">← 返回订单列表</Link>
        <h1>订单详情</h1>
      </header>

      <div className="order-detail-card">
        {/* 订单状态区域 */}
        <div className="order-status-banner" style={{ background: statusInfo.bgColor }}>
          <span className="status-icon">{statusInfo.icon}</span>
          <div className="status-info">
            <span className="status-label" style={{ color: statusInfo.color }}>{statusInfo.label}</span>
            <span className="status-time">下单时间：{new Date(order.createdAt).toLocaleString('zh-CN')}</span>
          </div>
        </div>

        {/* 订单基本信息 */}
        <div className="order-info-section">
          <div className="info-row">
            <span className="info-label">订单号</span>
            <span className="info-value order-number">{order.id}</span>
          </div>
          <div className="info-row">
            <span className="info-label">订单金额</span>
            <span className="info-value price">¥{order.totalAmount.toFixed(2)}</span>
          </div>
          <div className="info-row">
            <span className="info-label">交易方式</span>
            <span className="info-value">🤝 面交</span>
          </div>
        </div>

        {/* 面交信息提示 */}
        {(order.status === 'CONFIRMED' || order.status === 'MEETUP') && (
          <div className="meetup-notice">
            <div className="notice-header">
              <span className="notice-icon">📍</span>
              <h3>面交信息</h3>
            </div>
            <p className="notice-text">请联系卖家确认面交时间和地点</p>
            <p className="notice-tip">💡 交易时请当面验货，确认无误后再完成订单</p>
          </div>
        )}

        {/* 商品清单 */}
        <div className="order-items-section">
          <h3>📦 商品清单</h3>
          <div className="items-list">
            {order.items.map(item => (
              <div key={item.bookId} className="item-card">
                <div className="item-main">
                  <h4 className="item-title">{item.bookTitle}</h4>
                  <p className="item-quantity">数量：{item.quantity}</p>
                </div>
                <div className="item-price">
                  <span className="unit-price">¥{item.price.toFixed(2)}</span>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* 操作按钮区域 */}
        <div className="order-action-bar">
          {statusInfo.next && (
            <button 
              className="action-btn action-btn-primary" 
              onClick={() => updateStatus(statusInfo.next)}
              disabled={updating}
            >
              {updating ? (
                <>
                  <span className="btn-spinner"></span>
                  处理中...
                </>
              ) : (
                <>
                  {statusInfo.next === 'CONFIRMED' && '✅ '}
                  {statusInfo.next === 'MEETUP' && '🤝 '}
                  {statusInfo.next === 'COMPLETED' && '🎉 '}
                  {statusInfo.nextLabel}
                </>
              )}
            </button>
          )}
          {order.status === 'COMPLETED' && (
            <button 
              className="action-btn action-btn-success"
              onClick={() => navigate(`/orders/${order.id}/review`)}
            >
              ⭐ 评价此订单
            </button>
          )}
          {order.status !== 'COMPLETED' && order.status !== 'CANCELLED' && (
            <button 
              className="action-btn action-btn-danger" 
              onClick={cancelOrder}
              disabled={updating}
            >
              ❌ 取消订单
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
