# Vidyasetu Library Management System (LMS)

Vidyasetu LMS is a comprehensive desktop application designed to automate and streamline library administrative tasks. It is built entirely from scratch using Core Java Swing for the frontend and MySQL for the backend.

---

## Key Features

- **Interactive Dashboard:** View real-time library metrics, including total books, available physical copies, and active members at a glance.

- **Book Management:** Detailed cataloging system tracking ISBN, author, publisher, and publication dates.

- **Physical Inventory Tracking:** Goes beyond basic title tracking by managing individual physical copies of books using unique Copy IDs.

- **Member Management:** Secure, enrollment-based student registration with the ability to toggle active/inactive status to restrict access if needed.

- **Transaction Engine:** A robust and seamless flow for issuing and returning books.

- **Automated Fine System:** Automatically calculates late return fines based on strict library rules and real-time date logic.

- **Search Functionality:** Instantly look up books or members using parameters (Title, ISBN, Enrollment No, Phone, etc.).

- **Admin Panel:** Secure system access with admin profile management and password change functionality.

- **Inclusive Design:** Built with accessibility as a fundamental requirement. The entire interface is 100% navigable via keyboard shortcuts and is natively compatible with screen readers (like NVDA).

---

## Tech Stack

- **Language:** Java (JDK 8+)
- **GUI Framework:** Java Swing & AWT
- **Database:** MySQL
- **Connectivity:** JDBC (Java Database Connectivity)

---

## Setup & Installation

1. Import and run the `Vidyasetu_db_schema.sql` file in your MySQL environment to create the database schema.

2. Open `src/db/DatabaseConnection.java` and update the database username and password with your local MySQL credentials.

3. Import the project into your preferred IDE (such as VS Code, IntelliJ IDEA, or Eclipse).

4. Add the `MySQL Connector/J` JAR file to your project's build path/dependencies.

5. Run `App.java` to launch the application.

---

## Notes

- Ensure MySQL server is running before launching the application.
- Use proper JDBC driver version compatible with your JDK.