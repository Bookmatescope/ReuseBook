import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

const API_BASE = 'http://localhost:8081/api';

// 订单状态配置
const STATUS_CONFIG = {
  ALL: { label: '全部', color: '#666' },
  PENDING: { label: '待确认', color: '#ff9800' },
  CONFIRMED: { label: '已确认', color: '#2196f3' },
  MEETUP: { label: '面交中', color: '#9c27b0' },
  COMPLETED: { label: '已完成', color: '#4caf50' },
  CANCELLED: { label: '已取消', color: '#9e9e9e' }
};

export default function OrdersPage(){
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('ALL');

  useEffect(() => {
    fetchOrders();
  }, [activeTab]);

  const fetchOrders = async () => {
    try{
      setLoading(true);
      const url = activeTab === 'ALL' 
        ? `${API_BASE}/orders`
        : `${API_BASE}/orders?status=${activeTab}`;
      const res = await fetch(url, {
        headers: { 'Authorization': `Bearer ${localStorage.getItem('token') || ''}` }
      });
      if(!res.ok) throw new Error('获取订单失败');
      const data = await res.json();
      setOrders(data);
    }catch(err){
      setError(err.message);
    }finally{
      setLoading(false);
    }
  };

  const getStatusStyle = (status) => {
    const config = STATUS_CONFIG[status] || STATUS_CONFIG.ALL;
    return { color: config.color, fontWeight: 'bold' };
  };

  const getStatusLabel = (status) => {
    return STATUS_CONFIG[status]?.label || status;
  };

  return (
    <div className="page-container">
      <h1>我的订单</h1>
      
      {/* 状态筛选标签 */}
      <div className="order-tabs">
        {Object.entries(STATUS_CONFIG).map(([key, { label }]) => (
          <button
            key={key}
            className={`tab-btn ${activeTab === key ? 'active' : ''}`}
            onClick={() => setActiveTab(key)}
          >
            {label}
          </button>
        ))}
      </div>

      {error && <p className="error-message">{error}</p>}
      
      {loading ? (
        <div className="loading">加载中...</div>
      ) : orders.length === 0 ? (
        <div className="empty">暂无订单</div>
      ) : (
        <div className="orders-list">
          {orders.map(o => (
            <Link to={`/orders/${o.id}`} key={o.id} className="order-card">
              <div className="order-row">
                <div>
                  <div className="order-id">订单号：{o.id.slice(0, 8)}...</div>
                  <div className="order-date">{new Date(o.createdAt).toLocaleString('zh-CN')}</div>
                  <div className="order-items-count">{o.items?.length || 0} 件商品</div>
                </div>
                <div style={{textAlign: 'right'}}>
                  <div className="order-amount">¥{o.totalAmount.toFixed(2)}</div>
                  <div className="order-status" style={getStatusStyle(o.status)}>
                    {getStatusLabel(o.status)}
                  </div>
                </div>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
