import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

/**
 * 订单创建页面 - 从购物车结算到创建订单
 */
export default function CheckoutPage() {
  const navigate = useNavigate();
  const [cartItems, setCartItems] = useState([]);
  const [addresses, setAddresses] = useState([]);
  const [selectedAddressId, setSelectedAddressId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    // 模拟从购物车和地址接口获取数据
    const mockCart = [
      { id: 'book-1', title: '深入理解计算机系统', author: 'Randal E. Bryant', price: 35.60, quantity: 1, condition: '九成新' },
      { id: 'book-2', title: 'JavaScript高级程序设计', author: 'Nicholas C. Zakas', price: 28.00, quantity: 1, condition: '全新' },
    ];
    const mockAddresses = [
      { id: 'addr-1', recipientName: '张三', phone: '138****1234', province: '福建省', city: '福州市', district: '闽侯县', detailAddress: '福州大学旗山校区', isDefault: true },
      { id: 'addr-2', recipientName: '李四', phone: '139****5678', province: '福建省', city: '福州市', district: '鼓楼区', detailAddress: '五一广场', isDefault: false },
    ];
    setTimeout(() => {
      setCartItems(mockCart);
      setAddresses(mockAddresses);
      setSelectedAddressId(mockAddresses.find(a => a.isDefault)?.id || mockAddresses[0]?.id);
      setLoading(false);
    }, 300);
  }, []);

  const totalAmount = cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0);

  const handleSubmit = async () => {
    if (!selectedAddressId) {
      alert('请选择收货地址');
      return;
    }
    setSubmitting(true);
    // 模拟创建订单
    setTimeout(() => {
      alert('订单创建成功！');
      navigate('/orders');
    }, 500);
  };

  if (loading) {
    return <div className="page-container"><p>加载中...</p></div>;
  }

  return (
    <div className="page-container">
      <h1>确认订单</h1>

      {/* 收货地址选择 */}
      <section className="checkout-section">
        <h2>收货地址</h2>
        <div className="address-list">
          {addresses.map(addr => (
            <div
              key={addr.id}
              className={`address-card ${selectedAddressId === addr.id ? 'selected' : ''}`}
              onClick={() => setSelectedAddressId(addr.id)}
            >
              <div className="address-info">
                <span className="recipient">{addr.recipientName}</span>
                <span className="phone">{addr.phone}</span>
                {addr.isDefault && <span className="default-tag">默认</span>}
              </div>
              <div className="address-detail">
                {addr.province} {addr.city} {addr.district} {addr.detailAddress}
              </div>
            </div>
          ))}
        </div>
        <button className="btn-secondary" onClick={() => navigate('/addresses')}>
          管理收货地址
        </button>
      </section>

      {/* 商品列表 */}
      <section className="checkout-section">
        <h2>商品清单</h2>
        <div className="order-items">
          {cartItems.map(item => (
            <div key={item.id} className="order-item">
              <div className="item-info">
                <h3>{item.title}</h3>
                <p className="author">{item.author}</p>
                <span className="condition">{item.condition}</span>
              </div>
              <div className="item-price">
                <span className="price">¥{item.price.toFixed(2)}</span>
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
          <span>运费</span>
          <span>免运费</span>
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
