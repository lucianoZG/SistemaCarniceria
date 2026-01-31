# 🥩 Butcher Shop Management System (Full Stack)

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-DB-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Frontend-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)

> **Status:** Completed & Functional. Ready for deployment.

## 📖 Overview

A comprehensive **Full Stack Solution** designed to digitize a retail meat business. The system serves two distinct purposes:
1.  **Management Dashboard:** For Admins and Employees to track stock, manage sales, and generate reports.
2.  **E-commerce Portal:** For **Customers** to browse products, add items to a **Shopping Cart**, and place orders online.

This system replaces manual/paper-based workflows with a centralized digital platform, allowing business owners to track stock in real-time, manage employee sales, and generate historical reports. It was built with a strong focus on **Relational Database Design** and **Business Logic integrity**.

---

## 💻 Tech Stack

* **Backend:** Java 17, Spring Boot (Web, Data JPA).
* **Frontend:** Thymeleaf (Server-Side Rendering), HTML5, CSS3, Bootstrap.
* **Database:** MySQL (Relational persistence).
* **Build Tool:** Maven.
* **Version Control:** Git.

---

## 📸 Screenshots

| Login Screen | Top 5 products and sales by day of the week dashboards |
| :---: | :---: |
| <img src="assets/login-screen.png" width="400" alt="Login"> | <img src="assets/dashboards.png" width="400" alt="Dashboards"> |

| Sales Interface | Reporting |
| :---: | :---: |
| <img src="assets/sales.png" width="400" alt="Sales"> | <img src="assets/report.png" width="400" alt="Reports"> |

| Shopping cart (client) | 
| :---: |
| <img src="assets/shopping-cart.png" width="400" alt="Shopping-cart"> |

---

## 🚀 Key Features

### 1. Inventory & Stock Control
* **Real-time Tracking:** Automatically deducts stock (in kg/units) upon every sale.
* **Product Management:** CRUD operations for meat cuts, prices per kg, and categories.
* **Low Stock Alerts:** Visual indicators when specific products are running low.

### 2. Sales Management (Point of Sale)
* **Cart System:** Allows adding multiple products to a single transaction.
* **Dynamic Calculation:** Automatically calculates total prices based on weight and current price per kg.
* **Sales History:** Detailed log of all transactions, filtered by date or employee.

### 3. User & Security
The system implements **Spring Security** to enforce strict isolation between user types:

* **ROLE_ADMIN** Access to the Back-office (Inventory, Reports, All Orders). Cannot access personal client carts.
* **ROLE_CUSTOMER:** Exclusive access to the Storefront and personal Shopping Cart. Restricted from viewing internal business data.

---

## 🗂 Database Structure (ER Diagram)

The system relies on a robust relational database schema to ensure data consistency.

| <img src="assets/diagram.png" width="400" alt="Diagram"> |

* **Entities:** `Product`, `Inventory`, `User`, `Sale`, `SaleDetail`, `Employee`.
* **Relationships:**
    * One-to-Many: `Product` ➡️ `SaleDetail`
    * One-to-Many: `User` ➡️ `Sale`
    * Many-to-Many: `Sale` ↔️ `Product` (handled via `SaleDetail` for historical price accuracy).
    * Many-to-Many: `Product` ↔️ `Inventory`

---

## ⚙️ How to Run Locally

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/lucianoZG/tu-repo-carniceria.git](https://github.com/lucianoZG/tu-repo-carniceria.git)
    ```
2.  **Configure Database:**
    * Ensure MySQL is running.
    * Create a schema named `butchershop_db`.
    * Configure environmental variables with your MYSQL credentials (url, username, password and database name).
3.  **Run the App:**
    ```bash
    mvn spring-boot:run
    ```
4.  **Access:**
    * Go to `http://localhost:8080` in your browser.

---

## 🔮 Future Improvements (Roadmap)
* [ ] **Dockerization:** Containerize the app and database for easier deployment.
* [ ] **SonarQube Integration:** Implement static code analysis for code quality.
* [ ] **Charts:** Integrate Chart.js for visual sales reporting.

---

## 📩 Contact

**Luciano Rafael Zanni Giuliano**
* **LinkedIn:** [linkedin.com/in/lucianozannig](https://www.linkedin.com/in/lucianozannig)
* **Email:** lucianozannig@gmail.com
