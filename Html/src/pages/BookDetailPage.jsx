// 书籍详情页面：展示单本书籍完整信息并支持加入购物车
import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import '../styles.css';

const API_BASE = 'http://localhost:8081';

export default function BookDetailPage() {
  const { id } = useParams();
  const [book, setBook] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [cartMsg, setCartMsg] = useState(null);

  useEffect(() => {
    fetch(`${API_BASE}/api/books/${id}`)
      .then((res) => {
        if (!res.ok) throw new Error('书籍不存在');
        return res.json();
      })
      .then(setBook)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [id]);

  const handleAddToCart = async () => {
    // 简化演示：使用固定买家邮箱（后续接入登录状态）
    const buyerEmail = prompt('请输入您的邮箱以添加到购物车：');
    if (!buyerEmail) return;

    try {
      const res = await fetch(`${API_BASE}/api/cart/items`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ bookId: id, buyerEmail, quantity: 1 }),
      });
      if (!res.ok) {
        const err = await res.json();
        throw new Error(err.message || '添加失败');
      }
      setCartMsg('✅ 已加入购物车');
    } catch (err) {
      setCartMsg(`❌ ${err.message}`);
    }
  };

  if (loading) return <div className="loading">加载中...</div>;
  if (error) return <div className="error-message">{error}</div>;

  return (
    <div className="book-detail-page">
      <header className="detail-header">
        <Link to="/books">← 返回列表</Link>
      </header>

      <article className="book-detail">
        <h1>{book.title}</h1>
        <p className="meta">
          <span>作者：{book.author}</span>
          <span>ISBN：{book.isbn}</span>
        </p>
        <p className="price">¥{book.price?.toFixed(2)}</p>
        <p className="condition">品相：{book.condition}</p>
        <p className="description">{book.description || '暂无描述'}</p>
        <p className="seller">卖家：{book.sellerEmail}</p>

        {/* 面交地址信息 */}
        {book.meetupLocation && (
          <div className="meetup-location">
            <h3>📍 面交地点</h3>
            <p>{book.meetupLocation}</p>
            <p className="meetup-tip">下单后请与卖家约定具体面交时间</p>
          </div>
        )}

        <button className="btn btn-primary" onClick={handleAddToCart}>
          🛒 加入购物车
        </button>
        {cartMsg && <p className="cart-msg">{cartMsg}</p>}
      </article>

      <footer className="page-footer">
        <Link to="/">← 返回首页</Link>
      </footer>
    </div>
  );
}
