# RevPay – Console Based Digital Payment System

RevPay is a Java-based console application that simulates a secure digital payment platform similar to PayTM/PhonePe.  
It supports **Personal** and **Business** accounts with wallet management, invoices, loans, transactions, and notifications.

---

## 🚀 Features

### Personal Account
- User registration & login (email/phone)
- Secure password + transaction PIN authentication
- Wallet management (add / withdraw money)
- Send & receive money
- View transaction history
- Receive notifications

### Business Account
- Business registration with profile details
- Wallet management
- Create & manage invoices
- Accept invoice payments
- Apply for business loans
- Repay EMIs
- Receive payments from customers
- Business-specific notifications

---

## 🔐 Security
- Password hashing using **BCrypt**
- Transaction PIN stored separately (hashed)
- Account lock after multiple failed attempts
- PIN lock after multiple failures
- Database-level transaction integrity
- Wallet row locking for concurrency safety

---

## 🧱 Tech Stack
- Java 17
- JDBC
- PostgreSQL
- Maven
- BCrypt (jbcrypt)
- JUnit (basic tests)

---

## 🗄 Database Design
- Users
- Wallets
- Transactions
- Cards
- Business Profiles
- Invoices
- Loans
- Notifications

Refer to **ERD.png** for complete schema.

---

## ▶️ How to Run

### Prerequisites
- Java 17+
- PostgreSQL
- Maven

### Steps
1. Clone repository
git clone https://github.com/<your-username>/revpay.git

2. Create database in PostgreSQL
CREATE DATABASE revpay;

3. Update DB creadentials in
DBConnection.java

4. Build Project
mvn clean install

5. Run Application
Run RevPayApp.java 
 
   

