import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

const API_BASE = 'http://localhost:8081/api';

export default function OrdersPage(){
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try{
      setLoading(true);
      const res = await fetch(`${API_BASE}/orders`, {
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

  if(loading) return (<div className="page-container"><p>加载中...</p></div>);

  return (
    <div className="page-container">
      <h1>我的订单</h1>
      {error && <p className="error-message">{error}</p>}
      {orders.length === 0 ? (
        <div className="empty">暂无订单，去购物吧！</div>
      ) : (
        <div className="orders-list">
          {orders.map(o => (
            <Link to={`/orders/${o.id}`} key={o.id} className="order-card">
              <div className="order-row">
                <div>
                  <div className="order-id">订单号：{o.id}</div>
                  <div className="order-date">{new Date(o.createdAt).toLocaleString('zh-CN')}</div>
                </div>
                <div style={{textAlign: 'right'}}>
                  <div className="order-amount">合计：¥{o.totalAmount.toFixed(2)}</div>
                  <div className="order-status">状态：{o.status}</div>
                </div>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
