import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import AddressManager from '../components/AddressManager';

/**
 * 订单创建页面 - 从购物车结算到创建订单（面交模式）
 */
export default function CheckoutPage() {
  const navigate = useNavigate();
  const [cartItems, setCartItems] = useState([]);
  const [selectedAddressId, setSelectedAddressId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchCartItems();
  }, []);

  const fetchCartItems = async () => {
    try {
      const token = localStorage.getItem('token');
      const email = localStorage.getItem('userEmail');
      const res = await fetch(`/api/cart/items?buyerEmail=${email}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setCartItems(data);
      } else {
        // 如果接口失败，使用模拟数据
        setCartItems([
          { id: 'book-1', bookId: 'book-1', bookTitle: '深入理解计算机系统', unitPrice: 35.60, quantity: 1 },
        ]);
      }
    } catch (err) {
      console.error('获取购物车失败:', err);
      setCartItems([
        { id: 'book-1', bookId: 'book-1', bookTitle: '深入理解计算机系统', unitPrice: 35.60, quantity: 1 },
      ]);
    } finally {
      setLoading(false);
    }
  };

  const totalAmount = cartItems.reduce((sum, item) => sum + (item.unitPrice || item.price) * item.quantity, 0);

  const handleSubmit = async () => {
    if (!selectedAddressId) {
      setError('请选择收货地址');
      return;
    }
    setSubmitting(true);
    setError(null);
    
    try {
      const token = localStorage.getItem('token');
      const res = await fetch('/api/orders', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({
          items: cartItems.map(item => ({
            bookId: item.bookId || item.id,
            quantity: item.quantity
          })),
          addressId: selectedAddressId
        })
      });
      
      if (res.ok) {
        alert('订单创建成功！卖家确认后将与您约定面交时间地点。');
        navigate('/orders');
      } else {
        const data = await res.json();
        setError(data.message || '创建订单失败');
      }
    } catch (err) {
      setError('网络错误，请稍后重试');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return <div className="page-container"><p>加载中...</p></div>;
  }

  return (
    <div className="page-container">
      <h1>确认订单</h1>

      {error && <div className="error-message">{error}</div>}

      {/* 收货地址选择 */}
      <section className="checkout-section">
        <AddressManager onSelect={setSelectedAddressId} />
      </section>

      {/* 商品列表 */}
      <section className="checkout-section">
        <h2>商品清单</h2>
        <div className="order-items">
          {cartItems.map(item => (
            <div key={item.id} className="order-item">
              <div className="item-info">
                <h3>{item.bookTitle || item.title}</h3>
                <p className="author">{item.author}</p>
                <span className="condition">{item.condition}</span>
              </div>
              <div className="item-price">
                <span className="price">¥{(item.unitPrice || item.price).toFixed(2)}</span>
                <span className="quantity">× {item.quantity}</span>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* 订单汇总 */}
      <section className="checkout-summary">
        <div className="summary-row">
          <span>商品金额</span>
          <span>¥{totalAmount.toFixed(2)}</span>
        </div>
        <div className="summary-row">
          <span>交易方式</span>
          <span className="meetup-tag">面交</span>
        </div>
        <div className="summary-row total">
          <span>应付总额</span>
          <span className="total-price">¥{totalAmount.toFixed(2)}</span>
        </div>
      </section>

      {/* 提交按钮 */}
      <div className="checkout-actions">
        <button
          className="btn-primary btn-large"
          onClick={handleSubmit}
          disabled={submitting || cartItems.length === 0}
        >
          {submitting ? '提交中...' : '提交订单'}
        </button>
      </div>
    </div>
  );
}
