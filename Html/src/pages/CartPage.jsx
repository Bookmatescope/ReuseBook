import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const API_BASE = 'http://localhost:8081/api';

/**
 * CartPage 购物车页面
 * 
 * 功能:
 * 1. 展示购物车商品列表
 * 2. 支持修改商品数量
 * 3. 支持删除商品
 * 4. 实时计算总价
 * 5. 跳转到结算页面
 * 
 * @author 杨浩 - Day7 完善
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

  const updateQuantity = async (cartItemId, newQuantity) => {
    if (newQuantity < 1) return;
    try {
      const res = await fetch(`${API_BASE}/cart/items/${cartItemId}`, {
        method: 'PATCH',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${getAuthToken()}`
        },
        body: JSON.stringify({ quantity: newQuantity }),
      });
      if (!res.ok) throw new Error('更新数量失败');
      const updated = await res.json();
      setCartItems(items => items.map(item => 
        item.id === cartItemId ? updated : item
      ));
    } catch (err) {
      setError(err.message);
    }
  };

  const removeItem = async (cartItemId) => {
    if (!confirm('确定要删除这件商品吗？')) return;
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
      alert('请选择要结算的商品');
      return;
    }
    // 将选中的商品ID存储到localStorage
    localStorage.setItem('checkoutItems', JSON.stringify([...selectedItems]));
    navigate('/checkout');
  };

  // 计算选中商品的总价
  const selectedTotal = cartItems
    .filter(item => selectedItems.has(item.id))
    .reduce((sum, item) => sum + item.subtotal, 0);

  if (loading) {
    return (
      <div className="cart-page">
        <p className="loading">加载中...</p>
      </div>
    );
  }

  return (
    <div className="cart-page">
      <h1>🛒 我的购物车</h1>

        {error && <p className="error-message">{error}</p>}

        {cartItems.length === 0 ? (
          <div className="cart-empty">
            <div className="empty-icon">🛒</div>
            <p>购物车空空如也</p>
            <a href="/books" className="btn btn-primary">去选购书籍</a>
          </div>
        ) : (
          <>
            {/* 全选 */}
            <div className="cart-header">
              <label className="select-all">
                <input
                  type="checkbox"
                  checked={selectedItems.size === cartItems.length}
                  onChange={toggleSelectAll}
                />
                <span>全选</span>
              </label>
              <span className="header-info">商品信息</span>
              <span className="header-price">单价</span>
              <span className="header-quantity">数量</span>
              <span className="header-subtotal">小计</span>
              <span className="header-action">操作</span>
            </div>

            <div className="cart-list">
              {cartItems.map(item => (
                <div key={item.id} className={`cart-item ${selectedItems.has(item.id) ? 'selected' : ''}`}>
                  <input
                    type="checkbox"
                    checked={selectedItems.has(item.id)}
                    onChange={() => toggleSelectItem(item.id)}
                    className="item-checkbox"
                  />
                  <div className="cart-item-info">
                    <h3 className="cart-item-title">{item.bookTitle}</h3>
                    {item.meetupLocation && (
                      <p className="cart-item-location">📍 {item.meetupLocation}</p>
                    )}
                  </div>
                  <div className="cart-item-price">
                    <span>¥{item.unitPrice.toFixed(2)}</span>
                  </div>
                  <div className="cart-item-quantity">
                    <button 
                      className="qty-btn"
                      onClick={() => updateQuantity(item.id, item.quantity - 1)}
                      disabled={item.quantity <= 1}
                    >
                      −
                    </button>
                    <span className="qty-value">{item.quantity}</span>
                    <button 
                      className="qty-btn"
                      onClick={() => updateQuantity(item.id, item.quantity + 1)}
                    >
                      +
                    </button>
                  </div>
                  <div className="cart-item-subtotal">
                    <span className="subtotal-price">¥{item.subtotal.toFixed(2)}</span>
                  </div>
                  <button 
                    className="cart-item-remove"
                    onClick={() => removeItem(item.id)}
                    title="删除"
                  >
                    🗑️
                  </button>
                </div>
              ))}
            </div>

            {/* 结算栏 */}
            <div className="cart-footer">
              <div className="cart-summary">
                <span className="selected-count">
                  已选 <strong>{selectedItems.size}</strong> 件
                </span>
                <span className="cart-total">
                  合计：<strong className="total-price">¥{selectedTotal.toFixed(2)}</strong>
                </span>
              </div>
              <button 
                className="btn btn-primary checkout-btn"
                onClick={handleCheckout}
                disabled={selectedItems.size === 0}
              >
                去结算
              </button>
            </div>
          </>
        )}
    </div>
  );
}
