// 书籍详情页面：展示单本书籍完整信息并支持加入购物车
import { useEffect, useState } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import '../styles.css';

const API_BASE = 'http://localhost:8081';

/**
 * BookDetailPage 书籍详情页面
 * 
 * 功能:
 * 1. 展示书籍完整信息（标题、作者、ISBN、价格、品相、描述）
 * 2. 显示卖家面交地点
 * 3. 支持加入购物车
 * 4. 显示书籍评价列表
 * 5. 支持直接下单购买
 * 
 * @author 杨浩 - Day7 完善
 */
export default function BookDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [book, setBook] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [cartMsg, setCartMsg] = useState(null);
  const [isAddingToCart, setIsAddingToCart] = useState(false);

  // 获取登录用户信息
  const getAuthToken = () => localStorage.getItem('token');
  const getUserEmail = () => localStorage.getItem('email');
  const isLoggedIn = () => !!getAuthToken();

  useEffect(() => {
    Promise.all([
      fetch(`${API_BASE}/api/books/${id}`).then(res => {
        if (!res.ok) throw new Error('书籍不存在');
        return res.json();
      }),
      fetch(`${API_BASE}/api/reviews/book/${id}`).then(res => {
        if (!res.ok) return [];
        return res.json();
      }).catch(() => [])
    ])
      .then(([bookData, reviewsData]) => {
        setBook(bookData);
        setReviews(reviewsData);
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [id]);

  const handleAddToCart = async () => {
    if (!isLoggedIn()) {
      if (confirm('请先登录后再添加购物车。是否前往登录？')) {
        navigate('/login');
      }
      return;
    }

    setIsAddingToCart(true);
    setCartMsg(null);

    try {
      const res = await fetch(`${API_BASE}/api/cart/items`, {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${getAuthToken()}`
        },
        body: JSON.stringify({ bookId: id, buyerEmail: getUserEmail(), quantity: 1 }),
      });
      if (!res.ok) {
        const err = await res.json();
        throw new Error(err.message || '添加失败');
      }
      setCartMsg({ type: 'success', text: '✅ 已加入购物车' });
    } catch (err) {
      setCartMsg({ type: 'error', text: `❌ ${err.message}` });
    } finally {
      setIsAddingToCart(false);
    }
  };

  const handleBuyNow = async () => {
    if (!isLoggedIn()) {
      if (confirm('请先登录后再购买。是否前往登录？')) {
        navigate('/login');
      }
      return;
    }

    try {
      // 先加入购物车，再跳转结算
      const res = await fetch(`${API_BASE}/api/cart/items`, {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${getAuthToken()}`
        },
        body: JSON.stringify({ bookId: id, buyerEmail: getUserEmail(), quantity: 1 }),
      });
      if (!res.ok) {
        const err = await res.json();
        throw new Error(err.message || '添加失败');
      }
      navigate('/checkout');
    } catch (err) {
      setCartMsg({ type: 'error', text: `❌ ${err.message}` });
    }
  };

  // 计算平均评分
  const averageRating = reviews.length > 0 
    ? (reviews.reduce((sum, r) => sum + r.rating, 0) / reviews.length).toFixed(1)
    : null;

  if (loading) return <div className="loading">加载中...</div>;
  if (error) return <div className="error-message">{error}</div>;

  return (
    <div className="book-detail-page">
      <header className="detail-header" style={{ marginTop: '1rem' }}>
        <Link to="/books">← 返回列表</Link>
      </header>

      <article className="book-detail">
        {/* 书籍封面 */}
        {book.coverUrl && (
          <div className="book-cover">
            <img src={book.coverUrl} alt={book.title} />
          </div>
        )}

        <div className="book-info">
          <h1>{book.title}</h1>
          <p className="meta">
            <span>作者：{book.author}</span>
            <span>ISBN：{book.isbn}</span>
          </p>
          
          <div className="price-section">
            <span className="price">¥{book.price?.toFixed(2)}</span>
            <span className="condition-badge">{book.condition}</span>
          </div>

          {/* 评分展示 */}
          {averageRating && (
            <div className="rating-summary">
              <span className="stars">{'★'.repeat(Math.round(averageRating))}</span>
              <span className="rating-value">{averageRating}</span>
              <span className="review-count">({reviews.length}条评价)</span>
            </div>
          )}

          <p className="description">{book.description || '暂无描述'}</p>
          
          {/* 卖家信息 */}
          <div className="seller-info">
            <span className="seller-label">卖家：</span>
            <span className="seller-name">{book.sellerNickname || book.sellerEmail}</span>
          </div>

          {/* 面交地址信息 */}
          {book.meetupLocation && (
            <div className="meetup-location">
              <h3>📍 面交地点</h3>
              <p className="location-text">{book.meetupLocation}</p>
              <p className="meetup-tip">💡 下单后请与卖家约定具体面交时间</p>
            </div>
          )}

          {/* 操作按钮 */}
          <div className="action-buttons">
            <button 
              className="btn btn-secondary" 
              onClick={handleAddToCart}
              disabled={isAddingToCart}
            >
              {isAddingToCart ? '添加中...' : '🛒 加入购物车'}
            </button>
            <button 
              className="btn btn-primary" 
              onClick={handleBuyNow}
            >
              💰 立即购买
            </button>
          </div>

          {cartMsg && (
            <p className={`cart-msg ${cartMsg.type}`}>{cartMsg.text}</p>
          )}
        </div>
      </article>

      {/* 评价区域 */}
      <section className="reviews-section">
        <h2>用户评价 ({reviews.length})</h2>
        {reviews.length === 0 ? (
          <p className="no-reviews">暂无评价</p>
        ) : (
          <ul className="reviews-list">
            {reviews.map((review) => (
              <li key={review.id} className="review-item">
                <div className="review-header">
                  <span className="reviewer">{review.reviewerNickname || '匿名用户'}</span>
                  <span className="review-rating">
                    {'★'.repeat(review.rating)}{'☆'.repeat(5 - review.rating)}
                  </span>
                  <span className="review-date">
                    {new Date(review.createdAt).toLocaleDateString('zh-CN')}
                  </span>
                </div>
                <p className="review-content">{review.content}</p>
              </li>
            ))}
          </ul>
        )}
      </section>

      <footer className="page-footer">
        <Link to="/">← 返回首页</Link>
      </footer>
    </div>
  );
}
