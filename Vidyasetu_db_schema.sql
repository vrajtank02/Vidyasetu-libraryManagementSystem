CREATE DATABASE vidyasetu_db;
USE vidyasetu_db;

-- 1. Admins Table
CREATE TABLE admins (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    mobile_no VARCHAR(15) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    gender VARCHAR(20) NOT NULL,
    address TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'Active'
);

-- 2. Books Table 
CREATE TABLE books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL UNIQUE,
    author VARCHAR(255) NOT NULL,
    publisher VARCHAR(255) NOT NULL,
    publication_date DATE NOT NULL
);

-- 3. Book Copies Table (The Physical Items - 1 row per physical book)
CREATE TABLE book_copies (
    copy_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    status VARCHAR(20) DEFAULT 'Available',
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE RESTRICT
);

-- 4. Members Table 
CREATE TABLE members (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    enrollment_no VARCHAR(50) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(15) NOT NULL,
    address TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'Active'
);

-- 5. Transactions Table 
CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    copy_id INT NOT NULL,
    member_id INT NOT NULL,
    issue_admin_id INT NOT NULL,
    return_admin_id INT,           -- Leaves empty until returned
    issue_date DATE NOT NULL,
    return_date DATE,              -- Leaves empty until returned
    fine_amount DECIMAL(10, 2) DEFAULT 0.00,
    FOREIGN KEY (copy_id) REFERENCES book_copies(copy_id) ON DELETE RESTRICT,
    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE RESTRICT,
    FOREIGN KEY (issue_admin_id) REFERENCES admins(admin_id) ON DELETE RESTRICT,
    FOREIGN KEY (return_admin_id) REFERENCES admins(admin_id) ON DELETE RESTRICT
);
-- Default Super Admin Profile 
INSERT INTO admins (username, password, first_name, last_name, mobile_no, email, gender, address) 
VALUES ('SuperUser1234', 'SU_LMS_1234', 'Vraj', 'Tank', '9876543210', 'admin.vidyasetu@gmail.com', 'Male', 'Junagadh, gujarat -362001');