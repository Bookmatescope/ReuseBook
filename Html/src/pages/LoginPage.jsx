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
          const body = await response.json().catch(() => ({}));
          throw new Error(body.message || '登录失败，请检查账号或密码');
        }

        const data = await response.json();
        localStorage.setItem('reusebook_token', data.token);
        localStorage.setItem('reusebook_profile', JSON.stringify(data.profile));
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
