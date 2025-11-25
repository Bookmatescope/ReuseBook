// 书籍列表页面：展示全部二手书
import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import '../styles.css';

const API_BASE = 'http://localhost:8081';

export default function BooksPage() {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetch(`${API_BASE}/api/books`)
      .then((res) => {
        if (!res.ok) throw new Error('加载书籍列表失败');
        return res.json();
      })
      .then(setBooks)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="loading">加载中...</div>;
  if (error) return <div className="error-message">{error}</div>;

  return (
    <div className="books-page">
      <header className="books-header">
        <h1>📚 二手书市场</h1>
        <Link to="/publish" className="btn btn-primary">发布二手书</Link>
      </header>

      {books.length === 0 ? (
        <p className="empty-hint">暂无书籍，快来发布第一本吧！</p>
      ) : (
        <ul className="book-list">
          {books.map((book) => (
            <li key={book.id} className="book-card">
              <Link to={`/books/${book.id}`} className="book-link">
                <h3 className="book-title">{book.title}</h3>
                <p className="book-author">{book.author}</p>
                <p className="book-price">¥{book.price?.toFixed(2)}</p>
                <span className="book-condition">{book.condition}</span>
              </Link>
            </li>
          ))}
        </ul>
      )}

      <footer className="page-footer">
        <Link to="/">← 返回首页</Link>
      </footer>
    </div>
  );
}
