# Secure School Management System (Secure SMS)

## Overview
The Secure School Management System is a robust Java-based web application designed to streamline student registrations and academic workflows. It features a comprehensive Results Management Module to automate grading, reporting, and exam result lifecycles.

## 🛠️ Tech Stack
* **Backend:** Java 17, Spring Boot 3.2.2
* **Database:** H2 Database (In-Memory for development) via Spring Data JPA
* **Security:** Spring Security 6
* **Frontend:** Thymeleaf, Bootstrap 5.3.0
* **Tooling:** Maven, Lombok

## ✨ Core Modules & Features
* **Premium User Interface:** A modern, high-end design featuring glassmorphism, dynamic micro-animations, and vibrant gradients for an exceptional user experience.
* **Strict Role-Based Dashboards:** Isolated, secure entry points and dashboard views for different personnel (Admin, Teacher, Secretary).
* **User & Role Management:** Secure authentication with distinct roles including `SECRETARY`, `TEACHER`, `DOS` (Director of Studies), `HEADTEACHER`, and `SUPER_DOS`.
* **Student Dashboard:** A user-friendly UI to view recent student registrations and enroll new students with their registration numbers, names, and gender.
* **Results & Grading Engine:** Automated grade and points calculation based on dynamic grading scales.
* **Exam Workflow:** A strict state-machine workflow for exam results: `DRAFT` ➔ `SUBMITTED` ➔ `APPROVED / RETURNED` ➔ `LOCKED`
* **Reporting:** Automated generation of individual student report cards and class-wide broadsheets with calculated rankings and positions.

## 🚀 Local Setup & Installation

### Prerequisites
* Java 17
* Maven

### Running the Application
1. Clone the repository:
   ```bash
   git clone <your-repository-url>
   ```
2. Navigate to the project root directory:
   ```bash
   cd school-system
   ```
3. Build the project using Maven:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## 💻 Accessing the System

* **Main Dashboard:** http://localhost:8080/
* **Default Login Credentials:**
  * **Username:** `admin`
  * **Password:** `admin`

### H2 Database Console
* **URL:** http://localhost:8080/h2-console
* **Driver Class:** `org.h2.Driver`
* **JDBC URL:** `jdbc:h2:mem:schoolsystem`
* **Username:** `sa`
* **Password:** `password`

## 🔮 Future Roadmap (Recommended Updates)
While the core functionality is robust, the following improvements would make the application even better:
* **Global CSS / SCSS Architecture:** Extract the premium inline styles into a standardized stylesheet (or use SCSS) to ensure consistent aesthetics across all views seamlessly.
* **Thymeleaf Layout Dialect:** Implement layout decorators (e.g., `nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect`) to prevent duplication of the sidebar and topbar across the 10+ HTML files.
* **Dark Mode Toggle:** The current UI has dark mode elements, but a fully reactive Light/Dark theme toggle using CSS variables and local storage would enhance the premium feel.
* **Persistent Database:** Migrate from the H2 In-Memory database to a production-ready database like PostgreSQL or MySQL.
* **API Extraction:** Separate the frontend and backend by turning the Spring Boot app into a pure REST API, paving the way for a React/Next.js frontend.
