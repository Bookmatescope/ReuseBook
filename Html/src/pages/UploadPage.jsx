// 图片上传页面：提供独立入口方便运营同学快速上传封面
import ImageUploader from '../components/uploads/ImageUploader.jsx';

export default function UploadPage() {
  return (
    <div className="page upload-page">
      <div className="page-header">
        <h2>上传图书封面</h2>
        <p>将本地图片一键上传至平台，获取可分享的访问链接。</p>
      </div>
      <ImageUploader />
    </div>
  );
}
