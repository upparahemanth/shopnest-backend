# 🛍️ ShopNest Backend

A full-featured E-Commerce REST API built with Spring Boot.

## 🚀 Tech Stack

| Technology | Purpose |
|---|---|
| Spring Boot 3.1.5 | Backend Framework |
| Spring Security + JWT | Authentication |
| Spring Data JPA | Database ORM |
| MySQL | Database |
| Razorpay | Payment Gateway |
| Gmail SMTP | Email Notifications |
| Swagger/OpenAPI | API Documentation |
| Lombok | Boilerplate Reduction |

## 📦 Features

- ✅ JWT Authentication (Register/Login)
- ✅ Role-based Access Control (Admin/Customer)
- ✅ Product Management with Pagination & Search
- ✅ Category Management
- ✅ Cart Management
- ✅ Order Placement with Stock Management
- ✅ Razorpay Payment Integration
- ✅ Email Notifications (Welcome, Order, Payment)
- ✅ Swagger API Documentation
- ✅ Global Exception Handling

## 🗂️ Project Structure
src/main/java/com/shopnest/
├── config/          # Security, Swagger, CORS
├── controller/      # REST Controllers
├── dto/             # Request/Response DTOs
├── entity/          # JPA Entities
├── enums/           # Role, OrderStatus
├── exception/       # Global Exception Handler
├── repository/      # JPA Repositories
├── security/        # JWT Filter, Utils
├── service/         # Service Interfaces
└── serviceImpl/     # Service Implementations
## ⚙️ Setup & Installation

### Prerequisites
- Java 17+
- MySQL 8+
- Maven

### Steps

1. Clone the repository
```bash
git clone https://github.com/upparahemanth/shopnest-backend.git
cd shopnest-backend
```

2. Configure application properties
```bash
cp src/main/resources/application.properties.example \
   src/main/resources/application.properties
```

3. Update `application.properties` with your credentials:
- MySQL username/password
- JWT secret key
- Razorpay API keys
- Gmail SMTP credentials

4. Create MySQL database
```sql
CREATE DATABASE shopnest;
```

5. Run the application
```bash
mvn spring-boot:run
```

6. Access Swagger UI
http://localhost:8080/swagger-ui/index.html

## 🔌 API Endpoints

### Auth
| Method | URL | Access |
|---|---|---|
| POST | /api/auth/register | Public |
| POST | /api/auth/login | Public |

### Products
| Method | URL | Access |
|---|---|---|
| GET | /api/products | Public |
| GET | /api/products/{id} | Public |
| GET | /api/products/search | Public |
| POST | /api/admin/products | Admin |
| PUT | /api/admin/products/{id} | Admin |
| DELETE | /api/admin/products/{id} | Admin |

### Cart
| Method | URL | Access |
|---|---|---|
| GET | /api/cart | Customer |
| POST | /api/cart/add | Customer |
| PUT | /api/cart/update/{id} | Customer |
| DELETE | /api/cart/remove/{id} | Customer |

### Orders
| Method | URL | Access |
|---|---|---|
| POST | /api/orders/place | Customer |
| GET | /api/orders/my-orders | Customer |
| GET | /api/admin/orders | Admin |
| PUT | /api/admin/orders/{id}/status | Admin |

### Payments
| Method | URL | Access |
|---|---|---|
| POST | /api/payments/initiate/{orderId} | Customer |
| POST | /api/payments/verify | Customer |

## 📧 Email Notifications
- 🎉 Welcome email on registration
- ✅ Order confirmation after placing order
- 📦 Order status updates
- 💳 Payment success confirmation

## 👤 Author
Uppara Hemanth — [GitHub](https://github.com/upparahemanth)

