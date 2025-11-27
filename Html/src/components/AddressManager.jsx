import { useState, useEffect } from 'react';

/**
 * 收货地址管理组件
 */
export default function AddressManager({ onSelect }) {
  const [addresses, setAddresses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  const [form, setForm] = useState({
    recipientName: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detailAddress: '',
    isDefault: false
  });

  useEffect(() => {
    fetchAddresses();
  }, []);

  const fetchAddresses = async () => {
    try {
      const token = localStorage.getItem('token');
      const res = await fetch('/api/user/addresses', {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setAddresses(data);
        // 自动选择默认地址
        const defaultAddr = data.find(a => a.isDefault);
        if (defaultAddr) {
          setSelectedId(defaultAddr.id);
          onSelect?.(defaultAddr.id);
        }
      }
    } catch (error) {
      console.error('获取地址列表失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem('token');
      const res = await fetch('/api/user/addresses', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify(form)
      });
      if (res.ok) {
        setShowForm(false);
        setForm({
          recipientName: '',
          phone: '',
          province: '',
          city: '',
          district: '',
          detailAddress: '',
          isDefault: false
        });
        fetchAddresses();
      }
    } catch (error) {
      console.error('添加地址失败:', error);
    }
  };

  const handleSelect = (id) => {
    setSelectedId(id);
    onSelect?.(id);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('确定删除该地址吗？')) return;
    try {
      const token = localStorage.getItem('token');
      await fetch(`/api/user/addresses/${id}`, {
        method: 'DELETE',
        headers: { Authorization: `Bearer ${token}` }
      });
      fetchAddresses();
    } catch (error) {
      console.error('删除地址失败:', error);
    }
  };

  if (loading) {
    return <div className="address-loading">加载地址中...</div>;
  }

  return (
    <div className="address-manager">
      <div className="address-header">
        <h3>收货地址</h3>
        <button 
          className="btn-add-address"
          onClick={() => setShowForm(!showForm)}
        >
          {showForm ? '取消' : '+ 新增地址'}
        </button>
      </div>

      {showForm && (
        <form className="address-form" onSubmit={handleSubmit}>
          <div className="form-row">
            <input
              type="text"
              placeholder="收件人姓名"
              value={form.recipientName}
              onChange={e => setForm({...form, recipientName: e.target.value})}
              required
            />
            <input
              type="tel"
              placeholder="手机号码"
              value={form.phone}
              onChange={e => setForm({...form, phone: e.target.value})}
              required
            />
          </div>
          <div className="form-row">
            <input
              type="text"
              placeholder="省份"
              value={form.province}
              onChange={e => setForm({...form, province: e.target.value})}
              required
            />
            <input
              type="text"
              placeholder="城市"
              value={form.city}
              onChange={e => setForm({...form, city: e.target.value})}
              required
            />
            <input
              type="text"
              placeholder="区县"
              value={form.district}
              onChange={e => setForm({...form, district: e.target.value})}
              required
            />
          </div>
          <input
            type="text"
            placeholder="详细地址"
            value={form.detailAddress}
            onChange={e => setForm({...form, detailAddress: e.target.value})}
            required
            className="full-width"
          />
          <label className="checkbox-label">
            <input
              type="checkbox"
              checked={form.isDefault}
              onChange={e => setForm({...form, isDefault: e.target.checked})}
            />
            设为默认地址
          </label>
          <button type="submit" className="btn-submit">保存地址</button>
        </form>
      )}

      <div className="address-list">
        {addresses.length === 0 ? (
          <p className="no-address">暂无收货地址，请添加</p>
        ) : (
          addresses.map(addr => (
            <div 
              key={addr.id} 
              className={`address-card ${selectedId === addr.id ? 'selected' : ''}`}
              onClick={() => handleSelect(addr.id)}
            >
              <div className="address-info">
                <div className="address-name">
                  <span className="recipient">{addr.recipientName}</span>
                  <span className="phone">{addr.phone}</span>
                  {addr.isDefault && <span className="default-tag">默认</span>}
                </div>
                <div className="address-detail">
                  {addr.province} {addr.city} {addr.district} {addr.detailAddress}
                </div>
              </div>
              <button 
                className="btn-delete"
                onClick={(e) => {
                  e.stopPropagation();
                  handleDelete(addr.id);
                }}
              >
                删除
              </button>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
