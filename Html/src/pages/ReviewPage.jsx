import { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';

const API_BASE = 'http://localhost:8081/api';

// 评分文字描述
const RATING_TEXT = {
  1: '非常差',
  2: '较差',
  3: '一般',
  4: '满意',
  5: '非常满意'
};

/**
 * 评价页面 - 为已完成订单添加评价
 */
export default function ReviewPage() {
  const { orderId } = useParams();
  const navigate = useNavigate();
  const [order, setOrder] = useState(null);
  const [rating, setRating] = useState(5);
  const [hoverRating, setHoverRating] = useState(0);
  const [content, setContent] = useState('');
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

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

      setSuccess(true);
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <div className="page-container review-page"><p className="loading">加载中...</p></div>;
  if (error && !order) return <div className="page-container review-page"><p className="error-message">{error}</p></div>;
  if (!order) return <div className="page-container review-page"><p>订单不存在</p></div>;

  // 评价提交成功页面
  if (success) {
    return (
      <div className="page-container review-page">
        <div className="review-success">
          <div className="success-animation">
            <div className="success-star">⭐</div>
          </div>
          <h1>感谢您的评价！</h1>
          <p className="success-message">您的评价将帮助其他用户更好地了解这本书</p>
          <div className="success-rating">
            <div className="stars-display">
              {[1, 2, 3, 4, 5].map(star => (
                <span key={star} className={`star-icon ${rating >= star ? 'filled' : ''}`}>★</span>
              ))}
            </div>
            <span className="rating-label">{RATING_TEXT[rating]}</span>
          </div>
          <div className="success-actions">
            <button 
              className="action-btn action-btn-primary"
              onClick={() => navigate(`/orders/${orderId}`)}
            >
              📦 返回订单详情
            </button>
            <button 
              className="action-btn action-btn-secondary"
              onClick={() => navigate('/orders')}
            >
              📋 查看全部订单
            </button>
          </div>
        </div>
      </div>
    );
  }

  const displayRating = hoverRating || rating;

  return (
    <div className="page-container review-page">
      <header className="review-header">
        <Link to={`/orders/${orderId}`} className="back-link">← 返回订单详情</Link>
        <h1>评价订单</h1>
      </header>

      {error && <div className="error-banner">{error}</div>}

      <div className="review-container">
        {/* 订单摘要卡片 */}
        <div className="order-summary-card">
          <div className="summary-header">
            <span className="summary-icon">📦</span>
            <h3>订单信息</h3>
          </div>
          <div className="summary-content">
            <div className="summary-row">
              <span className="label">订单号</span>
              <span className="value">{order.id.slice(0, 12)}...</span>
            </div>
            <div className="summary-row">
              <span className="label">订单金额</span>
              <span className="value price">¥{order.totalAmount.toFixed(2)}</span>
            </div>
            <div className="summary-items">
              {order.items?.map(item => (
                <div key={item.bookId} className="summary-item">
                  <span className="item-name">{item.bookTitle}</span>
                  <span className="item-qty">× {item.quantity}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* 评价表单 */}
        <div className="review-form-card">
          <div className="form-header">
            <span className="form-icon">⭐</span>
            <h3>您的评价</h3>
          </div>

          {/* 星级评分 */}
          <div className="rating-selector">
            <label className="section-label">评分</label>
            <div className="stars-container">
              <div className="stars-row">
                {[1, 2, 3, 4, 5].map(star => (
                  <button
                    key={star}
                    type="button"
                    className={`star-btn ${displayRating >= star ? 'active' : ''}`}
                    onClick={() => setRating(star)}
                    onMouseEnter={() => setHoverRating(star)}
                    onMouseLeave={() => setHoverRating(0)}
                  >
                    <span className="star-icon">★</span>
                  </button>
                ))}
              </div>
              <div className="rating-info">
                <span className="rating-number">{displayRating}</span>
                <span className="rating-text">{RATING_TEXT[displayRating]}</span>
              </div>
            </div>
          </div>

          {/* 评价内容 */}
          <div className="content-input">
            <label className="section-label" htmlFor="content">评价内容（选填）</label>
            <div className="textarea-wrapper">
              <textarea
                id="content"
                value={content}
                onChange={(e) => setContent(e.target.value)}
                placeholder="分享您的购书体验，帮助其他用户做出更好的选择..."
                maxLength={500}
                rows={5}
              />
              <div className="textarea-footer">
                <span className="char-count">{content.length}/500</span>
              </div>
            </div>
          </div>

          {/* 快捷评价标签 */}
          <div className="quick-tags">
            <label className="section-label">快捷评价</label>
            <div className="tags-list">
              {['书籍品相好', '卖家态度好', '面交方便', '物超所值', '描述准确'].map(tag => (
                <button
                  key={tag}
                  type="button"
                  className={`tag-btn ${content.includes(tag) ? 'selected' : ''}`}
                  onClick={() => {
                    if (content.includes(tag)) {
                      setContent(content.replace(tag, '').trim());
                    } else {
                      setContent(prev => prev ? `${prev} ${tag}` : tag);
                    }
                  }}
                >
                  {tag}
                </button>
              ))}
            </div>
          </div>

          {/* 提交按钮 */}
          <button
            className="submit-btn"
            onClick={handleSubmit}
            disabled={submitting}
          >
            {submitting ? (
              <>
                <span className="btn-spinner"></span>
                提交中...
              </>
            ) : (
              <>
                <span className="btn-icon">✨</span>
                提交评价
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
}
