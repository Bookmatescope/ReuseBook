import { useState, useEffect } from 'react';

const API_BASE = 'http://localhost:8081/api';

export default function CartPage() {
  const [cartItems, setCartItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [buyerEmail] = useState('test@reusebook.cn'); // 模拟已登录用户

  useEffect(() => {
    fetchCartItems();
  }, []);

  const fetchCartItems = async () => {
    try {
      setLoading(true);
      const res = await fetch(`${API_BASE}/cart/items?buyerEmail=${encodeURIComponent(buyerEmail)}`);
      if (!res.ok) throw new Error('获取购物车失败');
      const data = await res.json();
      setCartItems(data);
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
        headers: { 'Content-Type': 'application/json' },
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
    try {
      const res = await fetch(`${API_BASE}/cart/items/${cartItemId}`, {
        method: 'DELETE',
      });
      if (!res.ok) throw new Error('删除失败');
      setCartItems(items => items.filter(item => item.id !== cartItemId));
    } catch (err) {
      setError(err.message);
    }
  };

  const totalAmount = cartItems.reduce((sum, item) => sum + item.subtotal, 0);

  if (loading) {
    return (
      <div className="cart-page">
        <div className="container">
          <p className="loading">加载中...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="cart-page">
      <div className="container">
        <h1>我的购物车</h1>

        {error && <p className="error-message">{error}</p>}

        {cartItems.length === 0 ? (
          <div className="cart-empty">
            <p>购物车空空如也</p>
            <a href="/books" className="btn btn-primary">去选购书籍</a>
          </div>
        ) : (
          <>
            <div className="cart-list">
              {cartItems.map(item => (
                <div key={item.id} className="cart-item">
                  <div className="cart-item-info">
                    <h3 className="cart-item-title">{item.bookTitle}</h3>
                    <p className="cart-item-price">¥{item.unitPrice.toFixed(2)}</p>
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
                    <span>¥{item.subtotal.toFixed(2)}</span>
                  </div>
                  <button 
                    className="cart-item-remove"
                    onClick={() => removeItem(item.id)}
                  >
                    删除
                  </button>
                </div>
              ))}
            </div>

            <div className="cart-summary">
              <div className="cart-total">
                <span>合计：</span>
                <strong>¥{totalAmount.toFixed(2)}</strong>
              </div>
              <button className="btn btn-primary checkout-btn">
                去结算 ({cartItems.length} 件)
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
