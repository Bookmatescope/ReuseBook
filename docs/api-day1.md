# 接口文档（Day 1 版本）

> 负责人：陈东楷（需求+文档）

本节记录 Alpha Day 1 已完成并可对外演示的接口，覆盖认证、书籍、媒体三大模块。默认 Base URL 为 `http://localhost:8081`（Spring Boot 默认端口）。

## 1. 认证模块 `/api/auth`

### 1.1 注册 `POST /api/auth/register`

- **请求体**
  ```json
  {
    "email": "tester@reusebook.cn",
    "nickname": "测试同学",
    "password": "Password#123"
  }
  ```
- **响应 201 Created**
  ```json
  {
    "id": "d1b2f5a4-...",
    "email": "tester@reusebook.cn",
    "nickname": "测试同学",
    "createdAt": "2025-11-24T12:01:00Z"
  }
  ```
- **失败**：`409 CONFLICT`（邮箱重复）、`400 BAD_REQUEST`（字段缺失/格式错误）

### 1.2 登录 `POST /api/auth/login`

- **请求体**
  ```json
  {
    "email": "tester@reusebook.cn",
    "password": "Password#123"
  }
  ```
- **响应 200 OK**
  ```json
  {
    "token": "eyJhbGciOi...",
    "profile": {
      "id": "d1b2f5a4-...",
      "email": "tester@reusebook.cn",
      "nickname": "测试同学",
      "createdAt": "2025-11-24T12:01:00Z"
    }
  }
  ```
- **失败**：`401 UNAUTHORIZED`（邮箱或密码错误）

### 1.3 查询资料 `GET /api/auth/profile`

- **请求头**：`Authorization: Bearer <token>`（来自登录接口）
- **响应 200 OK**：返回 `UserProfile`。
- **失败**：`401 UNAUTHORIZED`（缺失/非法 Token）。

## 2. 书籍模块 `/api/books`

### 2.1 ISBN 元数据 `GET /api/books/isbn/{isbn}/info`

- **路径参数**：`isbn`（必填，10/13 位数字）
- **响应 200 OK**
  ```json
  {
    "isbn": "9787302671491",
    "title": "算法导论",
    "author": "Thomas H. Cormen",
    "publisher": "机械工业出版社",
    "publishedAt": "2023-08-01"
  }
  ```
- **失败**：`404 NOT_FOUND` 未收录。

### 2.2 ISBN 上架列表 `GET /api/books/isbn/{isbn}/listings`

- **响应 200 OK**
  ```json
  [
    {
      "id": "1",
      "isbn": "9787302671491",
      "title": "算法导论",
      "seller": "测试同学",
      "price": 25.0,
      "condition": "九成新",
      "createdAt": "2025-11-24T12:40:00Z"
    }
  ]
  ```

### 2.3 创建书籍 `POST /api/books`

- **请求体**
  ```json
  {
    "isbn": "9787302671491",
    "title": "算法导论",
    "seller": "测试同学",
    "price": 25.0,
    "condition": "九成新",
    "description": "无批注，附带防尘袋"
  }
  ```
- **响应 201 Created**：返回 `BookResponse`，包含自动生成的 `id` 与 `createdAt`。

## 3. 媒体模块 `/api/uploads`

### 3.1 图片上传 `POST /api/uploads/images`

- **请求类型**：`multipart/form-data`
  - `file`：必填，PNG/JPEG/WebP，最大 5MB
  - `category`：选填，用于在本地目录中分桶（例如 `book-cover`）
- **响应 200 OK**
  ```json
  {
    "filename": "9a3a7d1c-...png",
    "url": "/uploads/book-cover/9a3a7d1c-...png",
    "size": 123456,
    "category": "book-cover",
    "uploadedAt": "2025-11-24T13:02:00Z"
  }
  ```
- **失败**：`400 BAD_REQUEST`（缺少文件、超出大小、类型错误），`500 INTERNAL_SERVER_ERROR`（磁盘写入异常）。

## 4. 通用说明

| 字段 | 说明 |
|------|------|
| `createdAt` | ISO-8601 UTC 时间，由后端生成 |
| `price` | 以元为单位的浮点数，前端提交前需校验两位小数 |
| `token` | HMAC-SHA256 + Base64URL，自行实现，暂未设置过期时间 |

- 所有请求返回 `application/json`，除上传接口外无需特殊 Header。
- 异常响应统一格式：`{"code": "BUSINESS_ERROR", "message": "具体原因"}`（详见 `BusinessException` 处理逻辑）。
- 未来计划：Day2 将扩展书籍列表/搜索与更丰富的错误码。
