import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

const API_BASE = 'http://localhost:8081/api';

export default function OrderDetailPage(){
  const { id } = useParams();
  const navigate = useNavigate();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

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

  if(loading) return (<div className="page-container"><p>加载中...</p></div>);
  if(error) return (<div className="page-container"><p className="error-message">{error}</p></div>);
  if(!order) return (<div className="page-container"><p>找不到该订单</p></div>);

  return (
    <div className="page-container">
      <button className="btn-secondary" onClick={() => navigate(-1)}>返回</button>
      <h1>订单详情</h1>
      <div className="order-detail-card">
        <div className="order-meta">
          <div>订单号：{order.id}</div>
          <div>状态：{order.status}</div>
          <div>合计：¥{order.totalAmount.toFixed(2)}</div>
          <div>下单时间：{new Date(order.createdAt).toLocaleString('zh-CN')}</div>
        </div>

        <div className="order-items">
          {order.items.map(item => (
            <div key={item.bookId} className="order-item">
              <div className="order-item-title">{item.bookTitle}</div>
              <div className="order-item-meta">数量：{item.quantity}  单价：¥{item.price.toFixed(2)}</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
