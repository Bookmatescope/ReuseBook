# 接口文档

> 负责人：陈东楷（需求+文档）

本节记录 Alpha 冲刺已完成并可对外演示的接口，覆盖认证、书籍、购物车、媒体四大模块。默认 Base URL 为 `http://localhost:8081`。

---

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

### 1.3 刷新 Token `POST /api/auth/refresh`

- **请求体**
  ```json
  {
    "token": "<现有有效 token>"
  }
  ```
- **响应 200 OK**
  ```json
  {
    "token": "<新签发 token>",
    "profile": { ... }
  }
  ```
- **失败**：`401 UNAUTHORIZED`（Token 无效或已过期）

### 1.4 查询资料 `GET /api/auth/profile`

- **请求头**：`Authorization: Bearer <token>`
- **响应 200 OK**：返回 `UserProfile`。
- **失败**：`401 UNAUTHORIZED`（缺失/非法 Token）。

---

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

- **响应 200 OK**：返回该 ISBN 下所有二手书条目数组。

### 2.3 书籍列表 `GET /api/books`

- **响应 200 OK**
  ```json
  [
    {
      "id": "uuid",
      "isbn": "9787302671491",
      "title": "算法导论",
      "author": "CLRS",
      "description": "经典教材",
      "price": 35.00,
      "condition": "九成新",
      "sellerEmail": "seller@reusebook.cn",
      "createdAt": "2025-11-25T10:00:00Z"
    }
  ]
  ```

### 2.4 书籍详情 `GET /api/books/{id}`

- **路径参数**：`id`（UUID）
- **响应 200 OK**：返回单本书籍 `BookResponse`。
- **失败**：`404 NOT_FOUND`（书籍不存在）。

### 2.5 书籍搜索 `GET /api/books/search`

- **查询参数**：`keyword`（可选，模糊匹配标题、作者、ISBN）
- **响应 200 OK**：返回匹配的书籍数组，若无关键字则返回全量列表。
  ```json
  [
    {
      "id": "uuid",
      "isbn": "9787302671491",
      "title": "算法导论",
      "author": "CLRS",
      "description": "经典教材",
      "price": 35.00,
      "condition": "九成新",
      "sellerEmail": "seller@reusebook.cn",
      "createdAt": "2025-11-25T10:00:00Z"
    }
  ]
  ```

### 2.6 创建书籍 `POST /api/books`

- **请求体**
  ```json
  {
    "isbn": "9787302671491",
    "title": "算法导论",
    "author": "CLRS",
    "description": "无批注",
    "price": 25.0,
    "condition": "九成新",
    "sellerEmail": "seller@reusebook.cn"
  }
  ```
- **响应 201 Created**：返回 `BookResponse`。

---

## 3. 用户模块 `/api/user`

### 3.1 获取个人资料 `GET /api/user/profile`

- **请求头**：`Authorization: Bearer <token>`
- **响应 200 OK**
  ```json
  {
    "id": "uuid",
    "email": "user@reusebook.cn",
    "nickname": "用户昵称",
    "createdAt": "2025-11-24T12:00:00Z"
  }
  ```

### 3.2 更新个人资料 `PUT /api/user/profile`

- **请求头**：`Authorization: Bearer <token>`
- **请求体**
  ```json
  {
    "nickname": "新昵称"
  }
  ```
- **响应 200 OK**：返回更新后的 `UserProfile`。

### 3.3 获取收货地址列表 `GET /api/user/addresses`

- **请求头**：`Authorization: Bearer <token>`
- **响应 200 OK**
  ```json
  [
    {
      "id": "address-uuid",
      "recipientName": "张三",
      "phone": "13800138000",
      "province": "广东省",
      "city": "深圳市",
      "district": "南山区",
      "detailAddress": "科技园路1号",
      "isDefault": true
    }
  ]
  ```

### 3.4 添加收货地址 `POST /api/user/addresses`

- **请求头**：`Authorization: Bearer <token>`
- **请求体**
  ```json
  {
    "recipientName": "张三",
    "phone": "13800138000",
    "province": "广东省",
    "city": "深圳市",
    "district": "南山区",
    "detailAddress": "科技园路1号",
    "isDefault": true
  }
  ```
- **响应 201 Created**：返回新创建的 `AddressResponse`。

### 3.5 更新收货地址 `PUT /api/user/addresses/{addressId}`

- **请求头**：`Authorization: Bearer <token>`
- **响应 200 OK**：返回更新后的 `AddressResponse`。
- **失败**：`404 NOT_FOUND`（地址不存在）、`403 FORBIDDEN`（无权操作）。

### 3.6 删除收货地址 `DELETE /api/user/addresses/{addressId}`

- **请求头**：`Authorization: Bearer <token>`
- **响应 204 No Content**
- **失败**：`404 NOT_FOUND`（地址不存在）。

---

## 4. 购物车模块 `/api/cart`

### 3.1 添加商品 `POST /api/cart/items`

- **请求体**
  ```json
  {
    "bookId": "uuid",
    "buyerEmail": "buyer@reusebook.cn",
    "quantity": 1
  }
  ```
- **响应 201 Created**
  ```json
  {
    "id": "cart-item-uuid",
    "bookId": "uuid",
    "bookTitle": "算法导论",
    "buyerEmail": "buyer@reusebook.cn",
    "unitPrice": 35.00,
    "quantity": 1,
    "subtotal": 35.00,
    "addedAt": "2025-11-25T15:00:00Z"
  }
  ```
- **说明**：同一书籍重复添加会累加数量。
- **失败**：`404 NOT_FOUND`（书籍不存在）。

### 3.2 查询购物车 `GET /api/cart/items?buyerEmail={email}`

- **响应 200 OK**：返回该买家所有购物车条目数组。

### 3.3 更新数量 `PATCH /api/cart/items/{cartItemId}`

- **请求体**
  ```json
  {
    "quantity": 3
  }
  ```
- **响应 200 OK**：返回更新后的 `CartItemResponse`。
- **失败**：`404 NOT_FOUND`（条目不存在）。

### 4.4 删除条目 `DELETE /api/cart/items/{cartItemId}`

- **响应 204 No Content**
- **失败**：`404 NOT_FOUND`（条目不存在）。

---

## 5. 订单模块 `/api/orders`

### 5.1 创建订单 `POST /api/orders`

- **请求头**：`Authorization: Bearer <token>`
- **请求体**
  ```json
  {
    "items": [
      { "bookId": "book-uuid-1", "quantity": 1 },
      { "bookId": "book-uuid-2", "quantity": 2 }
    ],
    "addressId": "address-uuid"
  }
  ```
- **响应 201 Created**
  ```json
  {
    "id": "order-uuid",
    "items": [
      {
        "bookId": "book-uuid-1",
        "bookTitle": "算法导论",
        "price": 35.00,
        "quantity": 1
      }
    ],
    "addressId": "address-uuid",
    "totalAmount": 35.00,
    "status": "PENDING",
    "createdAt": "2025-11-26T10:00:00Z"
  }
  ```
- **失败**：`400 BAD_REQUEST`（订单项为空）、`404 NOT_FOUND`（书籍不存在）。

### 5.2 获取订单列表 `GET /api/orders`

- **请求头**：`Authorization: Bearer <token>`
- **响应 200 OK**：返回当前用户所有订单数组。

### 5.3 获取订单详情 `GET /api/orders/{orderId}`

- **请求头**：`Authorization: Bearer <token>`
- **响应 200 OK**：返回 `OrderResponse`。
- **失败**：`404 NOT_FOUND`（订单不存在）、`403 FORBIDDEN`（无权查看）。

---

## 6. 媒体模块 `/api/uploads`

### 6.1 图片上传 `POST /api/uploads/images`

- **请求类型**：`multipart/form-data`
  - `file`：必填，PNG/JPEG/WebP，最大 5MB
  - `category`：选填，分桶目录（例如 `book-cover`）
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
- **失败**：`400 BAD_REQUEST`（缺少文件、超出大小、类型错误）。

---

## 7. 通用说明

| 字段 | 说明 |
|------|------|
| `createdAt` / `addedAt` | ISO-8601 UTC 时间，由后端生成 |
| `price` / `unitPrice` | 以元为单位，保留两位小数 |
| `token` | HMAC-SHA256 签名，7 天有效期，支持刷新 |

- 所有请求返回 `application/json`，除上传接口外无需特殊 Header。
- 异常响应统一格式：`{"code": "BUSINESS_ERROR", "message": "具体原因"}`。
