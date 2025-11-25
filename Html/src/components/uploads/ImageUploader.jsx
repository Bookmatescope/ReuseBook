// 图片上传组件：封装文件选择、预览与上传状态
import { useCallback, useMemo, useState } from 'react';

export default function ImageUploader({
  title = '封面图片上传',
  subtitle = '支持 PNG/JPEG/WebP，大小不超过 5MB',
  onUploaded
}) {
  const [file, setFile] = useState(null);
  const [category, setCategory] = useState('book-cover');
  const [previewUrl, setPreviewUrl] = useState('');
  const [uploading, setUploading] = useState(false);
  const [message, setMessage] = useState(null);

  const acceptTypes = 'image/png,image/jpeg,image/webp';

  const handleFileChange = useCallback((event) => {
    const selected = event.target.files?.[0];
    if (!selected) {
      setFile(null);
      setPreviewUrl('');
      return;
    }
    setFile(selected);
    setPreviewUrl(URL.createObjectURL(selected));
    setMessage(null);
  }, []);

  const fileInfo = useMemo(() => {
    if (!file) return null;
    const sizeInKb = (file.size / 1024).toFixed(1);
    return `${file.name} · ${sizeInKb}KB`;
  }, [file]);

  const handleUpload = useCallback(async () => {
    if (!file) {
      setMessage({ type: 'error', text: '请先选择要上传的图片' });
      return;
    }
    const formData = new FormData();
    formData.append('file', file);
    if (category) {
      formData.append('category', category);
    }
    setUploading(true);
    setMessage(null);
    try {
      const response = await fetch('/api/uploads/images', {
        method: 'POST',
        body: formData
      });
      if (!response.ok) {
        const body = await response.json().catch(() => ({}));
        throw new Error(body.message || '上传失败，请稍后重试');
      }
      const result = await response.json();
      setMessage({ type: 'success', text: `上传成功，访问地址：${result.url}` });
      if (typeof onUploaded === 'function') {
        onUploaded(result);
      }
    } catch (error) {
      setMessage({ type: 'error', text: error.message });
    } finally {
      setUploading(false);
    }
  }, [file, category]);

  return (
    <div className="uploader-card">
      <header>
        <h2>{title}</h2>
        <p>{subtitle}</p>
      </header>
      <div className="uploader-field">
        <label htmlFor="category">图片分类</label>
        <input
          id="category"
          value={category}
          onChange={(event) => setCategory(event.target.value)}
          placeholder="book-cover"
        />
      </div>
      <div className="uploader-field">
        <label htmlFor="file">选择图片</label>
        <input id="file" type="file" accept={acceptTypes} onChange={handleFileChange} />
        {fileInfo ? <p className="field-hint">{fileInfo}</p> : null}
      </div>
      {previewUrl ? (
        <div className="uploader-preview">
          <img src={previewUrl} alt="预览" />
        </div>
      ) : null}
      <button className="primary-btn" onClick={handleUpload} disabled={uploading}>
        {uploading ? '上传中...' : '上传图片'}
      </button>
      {message ? (
        <div className={`alert ${message.type === 'error' ? 'error' : 'success'}`}>{message.text}</div>
      ) : null}
    </div>
  );
}
