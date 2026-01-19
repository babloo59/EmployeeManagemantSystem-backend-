# 🧑‍💼 Employee Management System – Backend

This repository contains the **Backend (Spring Boot)** for the **Employee Management System (EMS)**.  
It provides secure **REST APIs** with **JWT authentication**, **role-based authorization**, and full support for **Admin, Manager, and Employee workflows**.

---

## 🔗 Frontend Repository

👉 **Frontend (React.js)**  
🔗 https://github.com/babloo59/EmployeeManagemantSystem-frontend-
---

## 🚀 Features

### 🔐 Authentication & Security
- JWT-based authentication
- Role-based authorization (ADMIN / MANAGER / EMPLOYEE)
- Token expiry validation
- Force password change on first login
- Secure password hashing using BCrypt

### 👤 Role-based Capabilities

#### 👑 Admin
- Approve / Reject user registrations
- Manage employees & managers
- Assign tasks
- Approve / Reject leave requests
- Approve / Reject task reports

#### 🧑‍💼 Manager
- View & manage assigned employees
- Assign tasks to employees
- Approve / Reject employee leaves
- Approve / Reject employee reports
- View team reports only (not own)

#### 👷 Employee
- Login after admin approval
- View assigned tasks
- Accept tasks
- Submit task reports
- Apply for leave
- View own leave & report status

---

## 🛠️ Tech Stack

- Java 17+
- Spring Boot
- Spring Security
- JWT (JSON Web Token)
- Spring Data JPA (Hibernate)
- MySQL
- Lombok
- Maven

---

## 📁 Project Structure

```
backend/
├── src/main/java/com/bk/ems3/
│ ├── controller/
│ ├── service/
│ ├── repository/
│ ├── model/
│ ├── dto/
│ ├── security/
│ └── exception/
├── src/main/resources/
│ └── application.properties
├── pom.xml
└── README.md
```
---

## ⚙️ Configuration

### 1️⃣ Database Configuration

Update `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ems3?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect

server.port=8080
```
## 🔐 Security Configuration

- JWT filter validates token on every request
- Roles enforced using `hasAuthority`
- Passwords stored using `BCryptPasswordEncoder`
- Public APIs:
  - `/api/auth/login`
  - `/api/auth/register`

## 🔁 Authentication Flow

- User registers → status `PENDING`
- Admin approves user → status `ACTIVE`
- User logs in
- JWT token generated
- If `firstLogin = true` → force password change
- After password update → re-login required

## 📌 Important APIs

### 🔑 Auth
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/change-password`

### 👑 Admin

- `GET /api/admin/pending-users`
- `PUT /api/admin/approve/{id}`
- `PUT /api/admin/reject/{id}`
- `POST /api/admin/assign-task`
- `GET /api/admin/pending-reports`

### 🧑‍💼 Manager
- `GET /api/manager/pending-tasks`
- `GET /api/manager/pending-reports`
- `PUT /api/manager/approve-report/{id}`
- `PUT /api/manager/reject-report/{id}`

### 👷 Employee
- `GET /api/employee/my-tasks`
- `PUT /api/employee/accept-task/{id}`
- `POST /api/employee/submit-report`
- `POST /api/employee/apply-leave`
---
## ▶️ Running the Application

### 1️⃣ Clone Repository
```bash
git clone https://github.com/your-username/employee-management-system-backend.git
cd employee-management-system-backend
```
### 2️⃣ Build Project
```cmd
mvn clean install
```
### 3️⃣ Run Application
```cmd
mvn spring-boot:run
```

Backend will start at:
```link
http://localhost:8080
```
## 🧪 Testing

- Use Postman for API testing
- Include header:
- Authorization: Bearer <JWT_TOKEN>

## ⚠️ Notes

- MySQL must be running before starting backend
- Use Java 17 or above
- Do not expose JWT secret in production
- Enable CORS for frontend (`http://localhost:3000`)

## 📌 Future Enhancements

- Email notifications
- Forgot password via email
- Pagination & filtering
- Audit logs
- Deployment on Render / Railway / AWS

## 👨‍💻 Author

- Babloo Kumar
- Computer Science Engineer
- Java + Spring Boot | React Full Stack Developer
