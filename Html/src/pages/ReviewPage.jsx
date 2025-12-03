import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

const API_BASE = 'http://localhost:8081/api';

/**
 * 评价页面 - 为已完成订单添加评价
 */
export default function ReviewPage() {
  const { orderId } = useParams();
  const navigate = useNavigate();
  const [order, setOrder] = useState(null);
  const [rating, setRating] = useState(5);
  const [content, setContent] = useState('');
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchOrder();
  }, [orderId]);

  const fetchOrder = async () => {
    try {
      const res = await fetch(`${API_BASE}/orders/${orderId}`, {
        headers: { 'Authorization': `Bearer ${localStorage.getItem('token') || ''}` }
      });
      if (!res.ok) throw new Error('订单不存在');
      const data = await res.json();
      setOrder(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async () => {
    if (rating < 1 || rating > 5) {
      alert('请选择评分');
      return;
    }

    setSubmitting(true);
    try {
      const res = await fetch(`${API_BASE}/reviews`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token') || ''}`
        },
        body: JSON.stringify({
          orderId,
          rating,
          content
        })
      });

      if (!res.ok) {
        const data = await res.json();
        throw new Error(data.message || '提交失败');
      }

      alert('感谢您的评价！');
      navigate(`/orders/${orderId}`);
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <div className="page-container"><p>加载中...</p></div>;
  if (error) return <div className="page-container"><p className="error-message">{error}</p></div>;
  if (!order) return <div className="page-container"><p>订单不存在</p></div>;

  return (
    <div className="page-container">
      <button className="btn-secondary" onClick={() => navigate(-1)}>返回</button>
      <h1>评价订单</h1>

      <div className="review-form">
        <div className="order-summary">
          <h3>订单信息</h3>
          <p>订单号：{order.id.slice(0, 12)}...</p>
          <p>金额：¥{order.totalAmount.toFixed(2)}</p>
          <div className="order-items">
            {order.items?.map(item => (
              <div key={item.bookId} className="item">
                {item.bookTitle} × {item.quantity}
              </div>
            ))}
          </div>
        </div>

        <div className="review-content">
          <h3>您的评价</h3>

          {/* 评分选择 */}
          <div className="rating-section">
            <label>评分</label>
            <div className="star-rating">
              {[1, 2, 3, 4, 5].map(star => (
                <button
                  key={star}
                  className={`star ${rating >= star ? 'filled' : ''}`}
                  onClick={() => setRating(star)}
                  type="button"
                >
                  ★
                </button>
              ))}
            </div>
            <span className="rating-text">{rating} 星</span>
          </div>

          {/* 评价内容 */}
          <div className="content-section">
            <label htmlFor="content">评价内容（可选）</label>
            <textarea
              id="content"
              value={content}
              onChange={(e) => setContent(e.target.value)}
              placeholder="分享您的购书体验（0-500字）"
              maxLength={500}
              rows={6}
            />
            <p className="char-count">{content.length}/500</p>
          </div>

          {/* 提交按钮 */}
          <button
            className="btn-primary btn-large"
            onClick={handleSubmit}
            disabled={submitting}
          >
            {submitting ? '提交中...' : '提交评价'}
          </button>
        </div>
      </div>
    </div>
  );
}
