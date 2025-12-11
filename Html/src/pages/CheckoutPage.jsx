import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';

const API_BASE = 'http://localhost:8081/api';

/**
 * CheckoutPage 结算页面
 * 
 * 功能:
 * 1. 展示待结算的购物车商品
 * 2. 显示面交地点信息
 * 3. 创建订单
 * 4. 跳转到订单页面
 * 
 * 面交模式:
 * - 无需支付，提交订单后等待卖家确认
 * - 约定面交时间地点完成交易
 * 
 * @author 杨浩 - Day7 完善
 */
export default function CheckoutPage() {
  const navigate = useNavigate();
  const [cartItems, setCartItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [orderSuccess, setOrderSuccess] = useState(false);
  const [createdOrderId, setCreatedOrderId] = useState(null);

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
      
      // 获取选中的商品ID
      const checkoutItemsStr = localStorage.getItem('checkoutItems');
      const checkoutItems = checkoutItemsStr ? JSON.parse(checkoutItemsStr) : [];

      const email = getUserEmail();
      const res = await fetch(`${API_BASE}/cart/items?buyerEmail=${encodeURIComponent(email)}`, {
        headers: { 'Authorization': `Bearer ${getAuthToken()}` }
      });
      
      if (!res.ok) throw new Error('获取购物车失败');
      
      const data = await res.json();
      
      // 如果有选中的商品，只显示选中的；否则显示全部
      if (checkoutItems.length > 0) {
        setCartItems(data.filter(item => checkoutItems.includes(item.id)));
      } else {
        setCartItems(data);
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const totalAmount = cartItems.reduce(
    (sum, item) => sum + (item.unitPrice || item.price || 0) * (item.quantity || 1), 
    0
  );

  const handleSubmit = async () => {
    if (cartItems.length === 0) {
      setError('购物车为空');
      return;
    }

    setSubmitting(true);
    setError(null);
    
    try {
      // 构建订单项列表
      const orderItems = cartItems.map(item => ({
        bookId: item.bookId,
        quantity: item.quantity || 1
      }));

      // 创建订单（面交模式，无需地址）
      const res = await fetch(`${API_BASE}/orders`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${getAuthToken()}`
        },
        body: JSON.stringify({
          items: orderItems,
          addressId: null
        })
      });

      if (!res.ok) {
        const errData = await res.json().catch(() => ({}));
        throw new Error(errData.message || '订单创建失败');
      }

      const orderData = await res.json();
      
      // 清空购物车中的这些商品
      for (const item of cartItems) {
        await fetch(`${API_BASE}/cart/items/${item.id}`, {
          method: 'DELETE',
          headers: { 'Authorization': `Bearer ${getAuthToken()}` }
        });
      }

      // 清空选中状态
      localStorage.removeItem('checkoutItems');
      
      setOrderSuccess(true);
      setCreatedOrderId(orderData.id);
    } catch (err) {
      setError(err.message || '网络错误，请稍后重试');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="page-container checkout-page">
        <p className="loading">加载中...</p>
      </div>
    );
  }

  // 订单创建成功页面
  if (orderSuccess) {
    return (
      <div className="page-container checkout-page">
        <div className="order-success">
          <div className="success-animation">
            <div className="success-checkmark">
              <div className="check-icon">
                <span className="icon-line line-tip"></span>
                <span className="icon-line line-long"></span>
                <div className="icon-circle"></div>
                <div className="icon-fix"></div>
              </div>
            </div>
          </div>
          
          <h1 className="success-title">🎉 订单提交成功！</h1>
          <p className="success-message">
            订单已创建，等待卖家确认后将与您约定面交时间地点。
          </p>
          
          <div className="order-info-card">
            <div className="order-info-header">
              <span className="order-label">订单号</span>
              <span className="order-id">{createdOrderId}</span>
            </div>
            <div className="order-info-detail">
              <div className="info-item">
                <span className="info-label">商品数量</span>
                <span className="info-value">{cartItems.length} 件</span>
              </div>
              <div className="info-item">
                <span className="info-label">订单金额</span>
                <span className="info-value price">¥{totalAmount.toFixed(2)}</span>
              </div>
              <div className="info-item">
                <span className="info-label">交易方式</span>
                <span className="info-value meetup">🤝 面交</span>
              </div>
            </div>
          </div>

          <div className="success-timeline">
            <h3>📋 交易流程</h3>
            <div className="timeline">
              <div className="timeline-item active">
                <div className="timeline-dot"></div>
                <div className="timeline-content">
                  <span className="timeline-title">提交订单</span>
                  <span className="timeline-desc">已完成</span>
                </div>
              </div>
              <div className="timeline-item">
                <div className="timeline-dot"></div>
                <div className="timeline-content">
                  <span className="timeline-title">卖家确认</span>
                  <span className="timeline-desc">等待中...</span>
                </div>
              </div>
              <div className="timeline-item">
                <div className="timeline-dot"></div>
                <div className="timeline-content">
                  <span className="timeline-title">约定面交</span>
                  <span className="timeline-desc">待进行</span>
                </div>
              </div>
              <div className="timeline-item">
                <div className="timeline-dot"></div>
                <div className="timeline-content">
                  <span className="timeline-title">交易完成</span>
                  <span className="timeline-desc">待进行</span>
                </div>
              </div>
            </div>
          </div>

          <div className="success-actions">
            <button 
              className="btn btn-primary btn-large"
              onClick={() => navigate('/orders')}
            >
              📦 查看我的订单
            </button>
            <button 
              className="btn btn-secondary"
              onClick={() => navigate('/')}
            >
              🏠 返回首页
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="page-container checkout-page">
      <header className="checkout-header">
        <Link to="/cart">← 返回购物车</Link>
        <h1>确认订单</h1>
      </header>

      {error && <div className="error-message">{error}</div>}

      {cartItems.length === 0 ? (
        <div className="cart-empty">
          <p>没有待结算的商品</p>
          <Link to="/cart" className="btn btn-primary">返回购物车</Link>
        </div>
      ) : (
        <>
          {/* 商品列表 */}
          <section className="checkout-section">
            <h2>📦 商品清单 ({cartItems.length}件)</h2>
            <div className="order-items">
              {cartItems.map(item => (
                <div key={item.id} className="order-item">
                  <div className="item-info">
                    <h3>{item.bookTitle || item.title}</h3>
                    <p className="item-condition">{item.condition}</p>
                  </div>
                  <div className="item-meetup">
                    {item.meetupLocation && (
                      <span className="meetup-location">📍 {item.meetupLocation}</span>
                    )}
                  </div>
                  <div className="item-price">
                    <span className="price">¥{(item.unitPrice || item.price || 0).toFixed(2)}</span>
                    <span className="quantity">× {item.quantity || 1}</span>
                  </div>
                </div>
              ))}
            </div>
          </section>

          {/* 面交说明 */}
          <section className="checkout-section meetup-info">
            <h2>🤝 面交说明</h2>
            <div className="meetup-tips">
              <p>• 本平台采用<strong>面交模式</strong>，无需在线支付</p>
              <p>• 提交订单后，等待卖家确认</p>
              <p>• 卖家确认后，请联系卖家约定具体面交时间和地点</p>
              <p>• 面交时请当面验货，确认无误后完成交易</p>
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
              <span className="meetup-tag">🤝 面交</span>
            </div>
            <div className="summary-row total">
              <span>预计总额</span>
              <span className="total-price">¥{totalAmount.toFixed(2)}</span>
            </div>
          </section>

          {/* 提交按钮 */}
          <div className="checkout-actions">
            <button
              className="btn btn-primary btn-large"
              onClick={handleSubmit}
              disabled={submitting || cartItems.length === 0}
            >
              {submitting ? '提交中...' : `提交订单 (${cartItems.length}件)`}
            </button>
            <p className="submit-tip">点击提交后，订单将发送给卖家确认</p>
          </div>
        </>
      )}
    </div>
  );
}
