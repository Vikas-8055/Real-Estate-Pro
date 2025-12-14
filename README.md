# 🏠 RealEstate Pro - Property Management System

A comprehensive web-based real estate management platform built with Spring Boot that connects property owners, real estate agents, buyers, renters, and administrators on a unified platform.

## 📋 Project Overview

RealEstate Pro streamlines the real estate transaction process by implementing a multi-tier approval workflow where properties flow from creation through administrative review before 
becoming visible to potential buyers and renters. The platform features advanced search capabilities, favorites management, viewing request coordination, and application processing workflows.

## 🚀 Key Features

- **Multi-Role Architecture**: Support for Admin, Owner, Agent, Buyer, and Renter roles
- **Property Management**: Complete CRUD operations with admin approval workflow
- **Advanced Search & Filtering**: Search by location, property type, price range, and more
- **Favorites System**: Universal bookmarking for all users
- **Viewing Requests**: Schedule and manage property tours with approval workflow
- **Application Processing**: Submit and track rental/purchase applications
- **Real-time Status Tracking**: Monitor property, viewing, and application statuses
- **Role-Based Access Control (RBAC)**: Secure session-based authentication

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot 3.x
- **ORM**: Hibernate/JPA
- **Database**: MySQL 8.x
- **Build Tool**: Maven
- **Server**: Embedded Tomcat

### Frontend
- **Template Engine**: Thymeleaf
- **Styling**: HTML5, CSS3
- **Scripting**: JavaScript

### Architecture
- **Pattern**: MVC (Model-View-Controller)
- **Data Access**: Repository Pattern with Spring Data JPA
- **Security**: Session-based authentication with RBAC

## 👥 User Roles & Capabilities

| Role | Key Features |
|------|-------------|
| **Admin** | Property approval/rejection, user management, platform oversight |
| **Owner** | List properties, manage listings, approve viewing requests & applications |
| **Agent** | Represent owners/buyers/renters, manage all transaction workflows |
| **Buyer** | Browse properties, request viewings, submit purchase applications |
| **Renter** | Browse properties, request viewings, submit rental applications |

## 📊 System Workflows

### Property Approval Workflow
```
Owner/Agent creates property → PENDING → Admin reviews → APPROVED/REJECTED
```

### Viewing Request Workflow
```
Buyer/Renter requests viewing → PENDING → Owner/Agent approves/disapproves
```

### Application Workflow
```
Buyer/Renter submits application → PENDING → Owner/Agent accepts/rejects
```

## 🗃️ Database Schema

### Core Tables
- **users**: User accounts with roles and authentication
- **properties**: Property listings with status tracking
- **viewing_requests**: Property viewing appointments
- **applications**: Rental/purchase applications
- **favorites**: User-property bookmarking

### Relationships
- User → Property (One-to-Many)
- Property → Viewing Requests (One-to-Many)
- Property → Applications (One-to-Many)
- User ↔ Favorites ↔ Property (Many-to-Many)

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- MySQL 8.x
- Maven 3.6+

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/realestate-pro.git
cd realestate-pro
```

2. **Configure database**
```properties
# Update src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/realestate
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. **Build the project**
```bash
mvn clean install
```

4. **Run the application**
```bash
mvn spring-boot:run
```

5. **Access the application**
```
http://localhost:8080
```

## 📁 Project Structure
```
realestate-management/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── realestate/
│   │   │           └── management/
│   │   │               ├── ManagementApplication.java
│   │   │               ├── controller/
│   │   │               │   ├── AdminController.java
│   │   │               │   ├── ApplicationController.java
│   │   │               │   ├── AuthController.java
│   │   │               │   ├── DashboardController.java
│   │   │               │   ├── FavoriteController.java
│   │   │               │   ├── HomeController.java
│   │   │               │   ├── PropertyController.java
│   │   │               │   └── ViewingController.java
│   │   │               ├── service/
│   │   │               │   ├── ApplicationService.java
│   │   │               │   ├── FavoriteService.java
│   │   │               │   ├── PropertyService.java
│   │   │               │   ├── UserService.java
│   │   │               │   └── ViewingService.java
│   │   │               ├── dao/
│   │   │               │   ├── ApplicationDao.java
│   │   │               │   ├── FavoriteDao.java
│   │   │               │   ├── PropertyDao.java
│   │   │               │   ├── UserDao.java
│   │   │               │   └── ViewingDao.java
│   │   │               └── model/
│   │   │                   ├── Application.java
│   │   │                   ├── Favorite.java
│   │   │                   ├── Property.java
│   │   │                   ├── PropertyViewing.java
│   │   │                   └── User.java
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── css/
│   │       │   │   └── style.css
│   │       │   ├── js/
│   │       │   │   └── script.js
│   │       │   └── images/
│   │       │       └── logo.png
│   │       ├── templates/
│   │       │   ├── admin/
│   │       │   │   ├── dashboard.html
│   │       │   │   ├── pending-properties.html
│   │       │   │   └── users.html
│   │       │   ├── applications/
│   │       │   │   ├── my-applications.html
│   │       │   │   ├── received.html
│   │       │   │   └── submit.html
│   │       │   ├── auth/
│   │       │   │   ├── login.html
│   │       │   │   └── register.html
│   │       │   ├── property/
│   │       │   │   ├── create.html
│   │       │   │   ├── details.html
│   │       │   │   ├── edit.html
│   │       │   │   ├── list.html
│   │       │   │   └── my-properties.html
│   │       │   ├── viewings/
│   │       │   │   ├── my-viewings.html
│   │       │   │   ├── request.html
│   │       │   │   └── requests.html
│   │       │   ├── dashboard.html
│   │       │   ├── favorites.html
│   │       │   └── home.html
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── realestate/
│                   └── management/
│                       └── ManagementApplicationTests.java
├── target/
│   ├── classes/
│   ├── generated-sources/
│   └── realestate-management-0.0.1-SNAPSHOT.jar
├── .gitignore
├── pom.xml
├── README.md
└── mvnw

## 🔐 Security Features

- **Session-Based Authentication**: Secure user session management
- **Role-Based Access Control**: Controller-level permission checks
- **Input Validation**: Multi-layer validation (HTML5, JavaScript, Spring)
- **SQL Injection Prevention**: Parameterized JPA queries
- **Email Validation**: Gmail-only registration restriction


## 🎯 Future Enhancements

- [ ] Photo upload and gallery system
- [ ] Integrated messaging between users
- [ ] Email notifications for status changes
- [ ] Password encryption with BCrypt
- [ ] Spring Security integration
- [ ] Advanced analytics dashboard
- [ ] Mobile responsive design improvements
- [ ] Payment gateway integration

## 👨‍💻 Author

**Vikas Meneni**
- Course: Enterprise Software Design 
- Institution: Northeastern University
- Date: December 2025

## 📝 License

This project was developed as part of an academic course requirement.

## Acknowledgments

- Spring Boot Documentation
- Thymeleaf Template Engine
- Hibernate/JPA Framework
- MySQL Database



