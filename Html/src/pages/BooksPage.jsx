// 书籍列表页面：展示全部二手书，支持搜索与分页
import { useEffect, useState, useMemo } from 'react';
import { Link } from 'react-router-dom';
import '../styles.css';

const API_BASE = 'http://localhost:8081';
const PAGE_SIZE = 8;

export default function BooksPage() {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [keyword, setKeyword] = useState('');
  const [currentPage, setCurrentPage] = useState(1);

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

  // 前端搜索过滤
  const filtered = useMemo(() => {
    if (!keyword.trim()) return books;
    const kw = keyword.toLowerCase();
    return books.filter(
      (b) =>
        b.title?.toLowerCase().includes(kw) ||
        b.author?.toLowerCase().includes(kw) ||
        b.isbn?.includes(kw)
    );
  }, [books, keyword]);

  // 分页计算
  const totalPages = Math.ceil(filtered.length / PAGE_SIZE);
  const paged = useMemo(() => {
    const start = (currentPage - 1) * PAGE_SIZE;
    return filtered.slice(start, start + PAGE_SIZE);
  }, [filtered, currentPage]);

  // 重置页码当搜索变化
  useEffect(() => {
    setCurrentPage(1);
  }, [keyword]);

  if (loading) return <div className="loading">加载中...</div>;
  if (error) return <div className="error-message">{error}</div>;

  return (
    <div className="books-page">
      <header className="books-header">
        <h1>📚 二手书市场</h1>
        <Link to="/publish" className="btn btn-primary">发布二手书</Link>
      </header>

      <div className="search-bar">
        <input
          type="search"
          placeholder="搜索书名、作者或 ISBN..."
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
        />
      </div>

      {paged.length === 0 ? (
        <p className="empty-hint">
          {keyword ? '未找到匹配的书籍' : '暂无书籍，快来发布第一本吧！'}
        </p>
      ) : (
        <>
          <ul className="book-list">
            {paged.map((book) => (
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

          {totalPages > 1 && (
            <div className="pagination">
              <button
                disabled={currentPage === 1}
                onClick={() => setCurrentPage((p) => p - 1)}
              >
                上一页
              </button>
              <span>
                {currentPage} / {totalPages}
              </span>
              <button
                disabled={currentPage === totalPages}
                onClick={() => setCurrentPage((p) => p + 1)}
              >
                下一页
              </button>
            </div>
          )}
        </>
      )}

      <footer className="page-footer">
        <Link to="/">← 返回首页</Link>
      </footer>
    </div>
  );
}
