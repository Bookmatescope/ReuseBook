// 书籍发布页面：整合 ISBN 查询、图文表单与上传组件
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import ImageUploader from '../components/uploads/ImageUploader.jsx';

const conditionOptions = ['全新', '九九新', '九成新', '八成新', '七成新'];

export default function PublishPage() {
  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
    watch
  } = useForm({
    defaultValues: {
      isbn: '',
      sellerEmail: '',
      title: '',
      author: '',
      price: '',
      condition: '九成新',
      description: '',
      coverUrl: '',
      meetupLocation: ''
    }
  });

  const [isbnInfo, setIsbnInfo] = useState(null);
  const [isbnLoading, setIsbnLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [feedback, setFeedback] = useState(null);

  const isbnValue = watch('isbn');

  const handleLookup = async () => {
    if (!isbnValue || isbnValue.length < 10) {
      setFeedback({ type: 'error', text: '请先输入 10-13 位 ISBN' });
      return;
    }
    setIsbnLoading(true);
    setFeedback(null);
    try {
      const response = await fetch(`/api/books/isbn/${isbnValue}/info`);
      if (!response.ok) {
        throw new Error('未找到对应图书，请确认 ISBN 是否正确');
      }
      const data = await response.json();
      setIsbnInfo(data);
      // 自动填充表单字段
      setValue('title', data.title || '');
      setValue('author', data.author || '');
      // 如果有封面图，自动填入
      if (data.coverUrl) {
        setValue('coverUrl', data.coverUrl);
      }
      setFeedback({ type: 'success', text: 'ISBN 查询成功，信息已自动填充' });
    } catch (error) {
      setIsbnInfo(null);
      setFeedback({ type: 'error', text: error.message });
    } finally {
      setIsbnLoading(false);
    }
  };

  const onSubmit = async (values) => {
    setSubmitting(true);
    setFeedback(null);
    try {
      const response = await fetch('/api/books', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          isbn: values.isbn,
          sellerEmail: values.sellerEmail,
          price: Number(values.price),
          condition: values.condition,
          title: values.title,
          author: values.author,
          description: values.description,
          meetupLocation: values.meetupLocation
        })
      });
      if (!response.ok) {
        const body = await response.json().catch(() => ({}));
        throw new Error(body.message || '发布失败，请稍后重试');
      }
      const created = await response.json();
      setFeedback({ type: 'success', text: `发布成功，记录编号：${created.id || created.isbn}` });
    } catch (error) {
      setFeedback({ type: 'error', text: error.message });
    } finally {
      setSubmitting(false);
    }
  };

  const handleUploadSuccess = (result) => {
    setValue('coverUrl', result.url);
    setFeedback({ type: 'success', text: '封面上传成功，可复制链接用于书籍展示。' });
  };

  return (
    <div className="page publish-page">
      <div className="page-header">
        <h2>发布二手图书</h2>
        <p>填写 ISBN、售价与成色信息，几分钟内即可上架到书海拾贝。</p>
      </div>

      <div className="publish-layout">
        <form className="publish-form" onSubmit={handleSubmit(onSubmit)}>
          <section>
            <div className="section-header">
              <h3>基础信息</h3>
              <p>支持通过 ISBN 自动补全标题与作者。</p>
            </div>
            <div className="field">
              <label htmlFor="isbn">ISBN</label>
              <div className="isbn-row">
                <input
                  id="isbn"
                  placeholder="9787302671491"
                  {...register('isbn', {
                    required: '请输入 ISBN',
                    minLength: { value: 10, message: '至少 10 位' }
                  })}
                />
                <button type="button" className="ghost-btn" onClick={handleLookup} disabled={isbnLoading}>
                  {isbnLoading ? '查询中...' : '自动补全'}
                </button>
              </div>
              {errors.isbn ? <p className="field-error">{errors.isbn.message}</p> : null}
            </div>

            <div className="form-grid">
              <div className="field">
                <label htmlFor="title">书名</label>
                <input
                  id="title"
                  placeholder="书名"
                  {...register('title', { required: '请输入书名' })}
                />
                {errors.title ? <p className="field-error">{errors.title.message}</p> : null}
              </div>
              <div className="field">
                <label htmlFor="author">作者</label>
                <input id="author" placeholder="作者" {...register('author')} />
              </div>
            </div>

            <div className="field">
              <label htmlFor="sellerEmail">联系邮箱</label>
              <input
                id="sellerEmail"
                type="email"
                placeholder="seller@example.com"
                {...register('sellerEmail', { required: '请输入联系人邮箱' })}
              />
              {errors.sellerEmail ? <p className="field-error">{errors.sellerEmail.message}</p> : null}
            </div>
          </section>

          <section>
            <div className="section-header">
              <h3>交易信息</h3>
              <p>设定价格、成色与补充描述，帮助买家快速了解。</p>
            </div>
            <div className="form-grid">
              <div className="field">
                <label htmlFor="price">价格（元）</label>
                <input
                  id="price"
                  type="number"
                  step="0.01"
                  min="0.01"
                  placeholder="15.00"
                  {...register('price', { required: '请输入价格', min: { value: 0.01, message: '至少 0.01 元' } })}
                />
                {errors.price ? <p className="field-error">{errors.price.message}</p> : null}
              </div>
              <div className="field">
                <label htmlFor="condition">成色</label>
                <select id="condition" {...register('condition', { required: '请选择成色' })}>
                  {conditionOptions.map((option) => (
                    <option key={option} value={option}>
                      {option}
                    </option>
                  ))}
                </select>
                {errors.condition ? <p className="field-error">{errors.condition.message}</p> : null}
              </div>
            </div>

            <div className="field">
              <label htmlFor="meetupLocation">面交地址</label>
              <input
                id="meetupLocation"
                placeholder="例如：图书馆一楼、西门食堂门口"
                {...register('meetupLocation', { required: '请输入面交地址' })}
              />
              {errors.meetupLocation ? <p className="field-error">{errors.meetupLocation.message}</p> : null}
            </div>

            <div className="field">
              <label htmlFor="description">描述</label>
              <textarea
                id="description"
                rows="4"
                placeholder="例如：无批注，包含配套光盘"
                {...register('description')}
              />
            </div>

            <div className="field">
              <label htmlFor="coverUrl">封面链接</label>
              <input
                id="coverUrl"
                placeholder="https://..."
                {...register('coverUrl')}
              />
              <p className="field-hint">可通过右侧上传组件自动填充。</p>
            </div>
          </section>

          <button type="submit" className="primary-btn" disabled={submitting}>
            {submitting ? '发布中...' : '提交上架'}
          </button>
          {feedback ? (
            <div className={`alert ${feedback.type === 'error' ? 'error' : 'success'}`}>{feedback.text}</div>
          ) : null}
        </form>

        <aside className="publish-side">
          <div className="info-card isbn-result-card">
            <h4>📚 ISBN 解析结果</h4>
            {isbnInfo ? (
              <div className="isbn-result-content">
                {isbnInfo.coverUrl && (
                  <div className="isbn-cover">
                    <img src={isbnInfo.coverUrl} alt={isbnInfo.title} />
                  </div>
                )}
                <ul className="isbn-details">
                  <li>
                    <span className="label">书名</span>
                    <span className="value">{isbnInfo.title || '—'}</span>
                  </li>
                  <li>
                    <span className="label">作者</span>
                    <span className="value">{isbnInfo.author || '—'}</span>
                  </li>
                  <li>
                    <span className="label">出版社</span>
                    <span className="value">{isbnInfo.publisher || '—'}</span>
                  </li>
                  <li>
                    <span className="label">出版时间</span>
                    <span className="value">{isbnInfo.pubdate || '—'}</span>
                  </li>
                  {isbnInfo.pages && (
                    <li>
                      <span className="label">页数</span>
                      <span className="value">{isbnInfo.pages}</span>
                    </li>
                  )}
                  {isbnInfo.price && (
                    <li>
                      <span className="label">定价</span>
                      <span className="value">{isbnInfo.price}</span>
                    </li>
                  )}
                  {isbnInfo.binding && (
                    <li>
                      <span className="label">装帧</span>
                      <span className="value">{isbnInfo.binding}</span>
                    </li>
                  )}
                </ul>
                {isbnInfo.summary && (
                  <div className="isbn-summary">
                    <span className="label">内容简介</span>
                    <p>{isbnInfo.summary.length > 200 ? isbnInfo.summary.substring(0, 200) + '...' : isbnInfo.summary}</p>
                  </div>
                )}
              </div>
            ) : (
              <div className="isbn-empty">
                <div className="isbn-empty-icon">🔍</div>
                <p>输入 ISBN 后点击"自动补全"查询图书信息</p>
                <p className="hint">支持 10 位或 13 位 ISBN 编码</p>
              </div>
            )}
          </div>
          <ImageUploader
            title="上传封面"
            subtitle="支持 PNG/JPEG/WebP，大小 5MB 内"
            onUploaded={handleUploadSuccess}
          />
        </aside>
      </div>
    </div>
  );
}
