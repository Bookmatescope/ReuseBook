import { useCallback, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthForm from '../components/forms/AuthForm.jsx';

export default function RegisterPage() {
  const [error, setError] = useState(null);
  const [isSubmitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  const handleRegister = useCallback(
    async (values) => {
      if (values.password !== values.confirmPassword) {
        setError('两次输入的密码不一致');
        return;
      }

      setSubmitting(true);
      setError(null);

      try {
        const response = await fetch('/api/auth/register', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            email: values.email,
            password: values.password,
            nickname: values.nickname
          })
        });

        if (!response.ok) {
          const body = await response.json().catch(() => ({}));
          throw new Error(body.message || '注册失败，请稍后重试');
        }

        navigate('/login', { replace: true, state: { email: values.email } });
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
        <h2>创建账号</h2>
        <p>欢迎加入书海拾贝社区，一起分享与传递知识。</p>
      </header>
      {error ? <div className="alert error">{error}</div> : null}
      <AuthForm mode="register" onSubmit={handleRegister} isSubmitting={isSubmitting} />
    </section>
  );
}
