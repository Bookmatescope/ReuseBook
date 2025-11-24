import { useForm } from 'react-hook-form';

export default function AuthForm({ mode = 'register', onSubmit, isSubmitting, defaultValues = {} }) {
  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm({ defaultValues });

  const renderPasswordHint = () => (
    <p className="field-hint">密码需包含至少 8 位字符，建议包含字母和数字</p>
  );

  return (
    <form className="auth-form" onSubmit={handleSubmit(onSubmit)} noValidate>
      <div className="field">
        <label htmlFor="email">邮箱</label>
        <input
          id="email"
          type="email"
          placeholder="name@example.com"
          {...register('email', { required: '请输入邮箱地址' })}
        />
        {errors.email ? <p className="field-error">{errors.email.message}</p> : null}
      </div>

      {mode === 'register' ? (
        <div className="field">
          <label htmlFor="nickname">昵称</label>
          <input
            id="nickname"
            placeholder="书海拾贝队"
            {...register('nickname', {
              required: '请输入昵称',
              minLength: { value: 2, message: '昵称至少 2 个字符' }
            })}
          />
          {errors.nickname ? <p className="field-error">{errors.nickname.message}</p> : null}
        </div>
      ) : null}

      <div className="field">
        <label htmlFor="password">密码</label>
        <input
          id="password"
          type="password"
          placeholder="至少 8 位"
          {...register('password', {
            required: '请输入密码',
            minLength: { value: 8, message: '密码长度不能少于 8 位' }
          })}
        />
        {errors.password ? <p className="field-error">{errors.password.message}</p> : renderPasswordHint()}
      </div>

      {mode === 'register' ? (
        <div className="field">
          <label htmlFor="confirmPassword">确认密码</label>
          <input
            id="confirmPassword"
            type="password"
            placeholder="再次输入密码"
            {...register('confirmPassword', {
              required: '请再次输入密码',
              minLength: { value: 8, message: '密码长度不能少于 8 位' }
            })}
          />
          {errors.confirmPassword ? (
            <p className="field-error">{errors.confirmPassword.message}</p>
          ) : (
            <p className="field-hint">确保两次输入一致</p>
          )}
        </div>
      ) : null}

      <button type="submit" className="primary-btn" disabled={isSubmitting}>
        {isSubmitting ? '提交中...' : mode === 'register' ? '注册' : '登录'}
      </button>
    </form>
  );
}
