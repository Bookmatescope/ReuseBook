// 登录页面：处理凭证提交与 Token 持久化
import { useCallback, useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import AuthForm from '../components/forms/AuthForm.jsx';

export default function LoginPage() {
  const [error, setError] = useState(null);
  const [isSubmitting, setSubmitting] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const [prefillEmail, setPrefillEmail] = useState('');

  useEffect(() => {
    // 若注册页跳转带上邮箱则自动填入
    if (location.state?.email) {
      setPrefillEmail(location.state.email);
    }
  }, [location.state]);

  const handleLogin = useCallback(
    async (values) => {
      setSubmitting(true);
      setError(null);

      try {
        const response = await fetch('/api/auth/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            email: values.email,
            password: values.password
          })
        });

        if (!response.ok) {
          // 登录失败提示尽量展示后端返回信息
          const body = await response.json().catch(() => ({}));
          throw new Error(body.message || '登录失败，请检查账号或密码');
        }

        const data = await response.json();
        // 将 token 与用户信息存入本地，便于后续接口鉴权
        localStorage.setItem('token', data.token);
        localStorage.setItem('email', values.email);
        localStorage.setItem('username', data.profile?.nickname || data.profile?.username || values.email);
        localStorage.setItem('reusebook_token', data.token);
        localStorage.setItem('reusebook_profile', JSON.stringify(data.profile));
        // 触发登录状态变更事件，通知导航栏更新
        window.dispatchEvent(new Event('loginStateChange'));
        navigate('/', { replace: true });
      } catch (err) {
        setError(err.message);
      } finally {
        setSubmitting(false);
      }
    },
    [navigate]
  );

  return (
    <section>
      <header className="page-header">
        <h2>欢迎回来</h2>
        <p>使用注册邮箱登录，继续探索书海。</p>
      </header>
      {error ? <div className="alert error">{error}</div> : null}
      <AuthForm
        mode="login"
        onSubmit={handleLogin}
        isSubmitting={isSubmitting}
        defaultValues={{ email: prefillEmail }}
      />
    </section>
  );
}
