// 主布局组件：包含导航栏和页面内容
import { Outlet } from 'react-router-dom';
import Navbar from './Navbar.jsx';

/**
 * MainLayout 主布局组件
 * 为所有需要导航栏的页面提供统一布局
 */
export default function MainLayout({ children }) {
  return (
    <div className="main-layout">
      <Navbar />
      <main className="main-content">
        {children || <Outlet />}
      </main>
    </div>
  );
}
