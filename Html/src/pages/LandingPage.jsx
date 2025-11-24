// 着陆页：向新用户展示项目价值并提供注册/登录入口
import { Link } from 'react-router-dom';

export default function LandingPage() {
  return (
    <div className="landing">
      <section className="hero">
        <h1>书海拾贝 · 让每本书继续旅程</h1>
        <p>加入校园二手书交易平台，快速发布、搜索与分享你的书籍。</p>
        <div className="actions">
          <Link className="primary-btn" to="/register">
            立即注册
          </Link>
          <Link className="secondary-btn" to="/login">
            已有账号？登录
          </Link>
        </div>
      </section>
    </div>
  );
}
