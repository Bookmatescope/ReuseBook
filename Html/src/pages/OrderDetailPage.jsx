import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

const API_BASE = 'http://localhost:8081/api';

// 订单状态映射
const STATUS_MAP = {
  PENDING: { label: '待确认', color: '#ff9800', next: 'CONFIRMED', nextLabel: '确认订单' },
  CONFIRMED: { label: '已确认', color: '#2196f3', next: 'MEETUP', nextLabel: '开始面交' },
  MEETUP: { label: '面交中', color: '#9c27b0', next: 'COMPLETED', nextLabel: '确认完成' },
  COMPLETED: { label: '已完成', color: '#4caf50', next: null },
  CANCELLED: { label: '已取消', color: '#9e9e9e', next: null }
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

  if(loading) return (<div className="page-container"><p>加载中...</p></div>);
  if(error) return (<div className="page-container"><p className="error-message">{error}</p></div>);
  if(!order) return (<div className="page-container"><p>找不到该订单</p></div>);

  const statusInfo = STATUS_MAP[order.status] || { label: order.status, color: '#666' };

  return (
    <div className="page-container">
      <button className="btn-secondary" onClick={() => navigate(-1)}>返回</button>
      <h1>订单详情</h1>
      <div className="order-detail-card">
        <div className="order-meta">
          <div>订单号：{order.id}</div>
          <div>
            状态：<span style={{ color: statusInfo.color, fontWeight: 'bold' }}>{statusInfo.label}</span>
          </div>
          <div>合计：¥{order.totalAmount.toFixed(2)}</div>
          <div>下单时间：{new Date(order.createdAt).toLocaleString('zh-CN')}</div>
        </div>

        {/* 面交信息提示 */}
        {(order.status === 'CONFIRMED' || order.status === 'MEETUP') && (
          <div className="meetup-info">
            <h3>📍 面交信息</h3>
            <p>请联系卖家确认面交时间和地点</p>
            <p className="meetup-tip">交易时请当面验货，确认无误后再完成订单</p>
          </div>
        )}

        <div className="order-items">
          <h3>商品清单</h3>
          {order.items.map(item => (
            <div key={item.bookId} className="order-item">
              <div className="order-item-title">{item.bookTitle}</div>
              <div className="order-item-meta">数量：{item.quantity}  单价：¥{item.price.toFixed(2)}</div>
            </div>
          ))}
        </div>

        {/* 操作按钮 */}
        <div className="order-actions">
          {statusInfo.next && (
            <button 
              className="btn-primary" 
              onClick={() => updateStatus(statusInfo.next)}
              disabled={updating}
            >
              {updating ? '处理中...' : statusInfo.nextLabel}
            </button>
          )}
          {order.status !== 'COMPLETED' && order.status !== 'CANCELLED' && (
            <button 
              className="btn-danger" 
              onClick={cancelOrder}
              disabled={updating}
            >
              取消订单
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
