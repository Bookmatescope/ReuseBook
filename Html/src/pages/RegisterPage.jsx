// 注册页面：封装注册表单与状态提示逻辑
import { useCallback, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthForm from '../components/forms/AuthForm.jsx';

export default function RegisterPage() {
  const [error, setError] = useState(null);
  const [isSubmitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  const handleRegister = useCallback(
    async (values) => {
      // 前端先行校验两次密码一致性
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
          // 尝试解析后端错误信息，否则给出兜底文案
          const body = await response.json().catch(() => ({}));
          throw new Error(body.message || '注册失败，请稍后重试');
        }

        // 注册成功后跳转登录页并透传邮箱
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
