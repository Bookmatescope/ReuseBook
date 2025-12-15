import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';

const API_BASE = 'http://localhost:8081/api';

/**
 * CartPage 购物车页面
 * 
 * 功能:
 * 1. 展示购物车商品列表
 * 2. 支持删除商品
 * 3. 实时计算总价
 * 4. 跳转到结算页面
 * 
 * 注：二手书每本唯一，无需数量加减功能
 */
export default function CartPage() {
  const navigate = useNavigate();
  const [cartItems, setCartItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedItems, setSelectedItems] = useState(new Set());

  // 获取登录Token
  const getAuthToken = () => localStorage.getItem('token');
  const getUserEmail = () => localStorage.getItem('email');
  const isLoggedIn = () => !!getAuthToken();

  useEffect(() => {
    if (!isLoggedIn()) {
      navigate('/login');
      return;
    }
    fetchCartItems();
  }, []);

  const fetchCartItems = async () => {
    try {
      setLoading(true);
      const email = getUserEmail();
      const res = await fetch(`${API_BASE}/cart/items?buyerEmail=${encodeURIComponent(email)}`, {
        headers: {
          'Authorization': `Bearer ${getAuthToken()}`
        }
      });
      if (!res.ok) throw new Error('获取购物车失败');
      const data = await res.json();
      setCartItems(data);
      // 默认全选
      setSelectedItems(new Set(data.map(item => item.id)));
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const removeItem = async (cartItemId) => {
    if (!confirm('确定要移除这本书吗？')) return;
    try {
      const res = await fetch(`${API_BASE}/cart/items/${cartItemId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${getAuthToken()}`
        }
      });
      if (!res.ok) throw new Error('删除失败');
      setCartItems(items => items.filter(item => item.id !== cartItemId));
      setSelectedItems(prev => {
        const newSet = new Set(prev);
        newSet.delete(cartItemId);
        return newSet;
      });
    } catch (err) {
      setError(err.message);
    }
  };

  const toggleSelectItem = (itemId) => {
    setSelectedItems(prev => {
      const newSet = new Set(prev);
      if (newSet.has(itemId)) {
        newSet.delete(itemId);
      } else {
        newSet.add(itemId);
      }
      return newSet;
    });
  };

  const toggleSelectAll = () => {
    if (selectedItems.size === cartItems.length) {
      setSelectedItems(new Set());
    } else {
      setSelectedItems(new Set(cartItems.map(item => item.id)));
    }
  };

  const handleCheckout = () => {
    if (selectedItems.size === 0) {
      alert('请选择要结算的书籍');
      return;
    }
    // 将选中的商品ID存储到localStorage
    localStorage.setItem('checkoutItems', JSON.stringify([...selectedItems]));
    navigate('/checkout');
  };

  // 计算选中商品的总价
  const selectedTotal = cartItems
    .filter(item => selectedItems.has(item.id))
    .reduce((sum, item) => sum + (item.unitPrice || 0), 0);

  if (loading) {
    return (
      <div className="page-container cart-page">
        <p className="loading">加载中...</p>
      </div>
    );
  }

  return (
    <div className="page-container cart-page">
      <header className="cart-header-title">
        <h1>🛒 我的购物车</h1>
        {cartItems.length > 0 && (
          <span className="cart-count">{cartItems.length} 本书</span>
        )}
      </header>

      {error && <div className="error-banner">{error}</div>}

      {cartItems.length === 0 ? (
        <div className="cart-empty-state">
          <div className="empty-illustration">
            <span className="empty-icon">📚</span>
          </div>
          <h2>购物车空空如也</h2>
          <p>快去发现心仪的二手书吧！</p>
          <Link to="/" className="action-btn action-btn-primary">
            🔍 浏览书籍
          </Link>
        </div>
      ) : (
        <div className="cart-content">
          {/* 全选栏 */}
          <div className="cart-select-bar">
            <label className="select-all-label">
              <input
                type="checkbox"
                checked={selectedItems.size === cartItems.length && cartItems.length > 0}
                onChange={toggleSelectAll}
                className="checkbox-input"
              />
              <span className="checkbox-custom"></span>
              <span>全选</span>
            </label>
          </div>

          {/* 购物车列表 */}
          <div className="cart-items-list">
            {cartItems.map(item => (
              <div 
                key={item.id} 
                className={`cart-item-card ${selectedItems.has(item.id) ? 'selected' : ''}`}
              >
                <label className="item-select">
                  <input
                    type="checkbox"
                    checked={selectedItems.has(item.id)}
                    onChange={() => toggleSelectItem(item.id)}
                    className="checkbox-input"
                  />
                  <span className="checkbox-custom"></span>
                </label>

                <div className="item-content">
                  <div className="item-main">
                    <Link to={`/books/${item.bookId}`} className="item-title">
                      {item.bookTitle}
                    </Link>
                    {item.condition && (
                      <span className="item-condition">{item.condition}</span>
                    )}
                    {item.meetupLocation && (
                      <p className="item-location">
                        <span className="location-icon">📍</span>
                        {item.meetupLocation}
                      </p>
                    )}
                  </div>
                  
                  <div className="item-right">
                    <div className="item-price">
                      <span className="price-label">价格</span>
                      <span className="price-value">¥{(item.unitPrice || 0).toFixed(2)}</span>
                    </div>
                    <button 
                      className="item-remove-btn"
                      onClick={() => removeItem(item.id)}
                      title="移除"
                    >
                      <span className="remove-icon">×</span>
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/* 结算栏 */}
          <div className="cart-checkout-bar">
            <div className="checkout-info">
              <div className="selected-info">
                已选 <strong>{selectedItems.size}</strong> 本
              </div>
              <div className="total-info">
                <span className="total-label">合计：</span>
                <span className="total-amount">¥{selectedTotal.toFixed(2)}</span>
              </div>
            </div>
            <button 
              className="checkout-btn"
              onClick={handleCheckout}
              disabled={selectedItems.size === 0}
            >
              去结算
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
