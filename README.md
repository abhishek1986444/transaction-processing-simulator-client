
# Transaction System Simulation Client

Transaction Processing Simulator  | Java, JDBC, MySQL
Developed a Java-based project that simulates transaction-processing workflows using mock transaction data, implementing request handling, transaction validation, database interaction, and error handling to demonstrate concepts relevant to real-world backend transaction systems.


The client provides:

* User login using username and password
* OTP-based login verification
* JWT-based authentication
* Login session validation
* Payment request submission
* Payment status checking
* Local SQLite transaction storage
* Transaction record lookup
* Logout functionality

The client communicates with the server using:

```text
https://localhost:9090
```

---


# Disclaimer

This project is developed for educational, learning, and demonstration purposes only. It is a simulation of a transaction processing system and is not intended for use as a real banking, payment, or financial transaction system.

No real financial transactions or real monetary funds should be processed through this application. The security mechanisms, authentication, database design, and transaction-processing logic are implemented as part of the learning and demonstration scope of the project and should not be considered production-ready financial infrastructure.

AI tools were used during the development of this project for purposes such as coding assistance, debugging, documentation, and learning. .


--- 

# 1. Requirements

Install the following before running the client:

* Java 11 or later
* Maven 3.8+
* Transaction System Simulation Server
* SQLite JDBC dependency
* A registered user account on the server
* Email/OTP service configured on the server

Verify the installations:

```bash
java -version
mvn -version
```

Example:

```text
java version "17.x.x"
Apache Maven 3.x.x
```

---

# 2. Project Structure

The client project should contain:

```text
client/
│
├── pom.xml
│
└── src/
    └── main/
        └── java/
            └── com/
                └── example/
                    └── version1/
                        └── client/
                            │
                            ├── SimpleClient.java
                            │
                            └── finalmodule/
                                └── DatabaseManager.java
```

The SQLite database file is created automatically at runtime:

```text
banking_client.db
```

---

# 3. Server Prerequisite

The client requires the Transaction System Simulation Server to be running before the client is started.

The client is configured to use:

```text
https://localhost:9090
```

Make sure the server is running and listening on port `9090`.

The client configuration is located in:

```java
private static final String BASE_URL =
        "https://localhost:9090";
```

If your server uses another host or port, update `BASE_URL` in:

```text
SimpleClient.java
```

For example:

```java
private static final String BASE_URL =
        "https://localhost:9090";
```

---

# 4. SQLite Configuration

The client uses SQLite for local transaction storage.

The database connection is configured in:

```text
DatabaseManager.java
```

The JDBC URL is:

```java
private static final String URL =
        "jdbc:sqlite:banking_client.db";
```

Therefore, the database file is:

```text
banking_client.db
```

No separate SQLite server needs to be installed or started.

The database and table are created automatically when the client starts.

The client calls:

```java
DatabaseManager.createTables();
```

before login.

---

# 5. SQLite Dependency

Make sure the SQLite JDBC driver is included in `pom.xml`.

Example:

```xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.46.1.0</version>
</dependency>
```

The exact version can be changed to the version used by your project.

After adding or changing dependencies, run:

```bash
mvn clean install
```

---

# 6. Jackson Dependency

The client uses Jackson to create and parse JSON requests and responses.

The required Jackson dependency should also be present in `pom.xml`.

Example:

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.17.2</version>
</dependency>
```

If the project already contains Jackson dependencies, do not add duplicate versions.

---

# 7. Setup

Clone or copy the client project into a local directory.

Navigate to the project:

```bash
cd client
```

Verify that `pom.xml` exists:

```bash
ls
```

You should see:

```text
pom.xml
src
```

On Windows:

```cmd
dir
```

---

# 8. Configure the Server URL

Open:

```text
src/main/java/com/example/version1/client/SimpleClient.java
```

Find:

```java
private static final String BASE_URL =
        "https://localhost:9090";
```

Keep this value if the server is running locally on port `9090`.

If the server is running somewhere else, change it accordingly.

Example:

```java
private static final String BASE_URL =
        "https://192.168.1.100:9090";
```

---

# 9. Build the Client

From the client project directory, run:

```bash
mvn clean package
```

Maven will:

1. Remove previous build files.
2. Download required dependencies.
3. Compile the Java source code.
4. Run the configured tests, if any.
5. Package the application.

A successful build should finish with:

```text
BUILD SUCCESS
```

---

# 10. Run the Client

If the project is configured with the Maven Exec Plugin, run:

```bash
mvn exec:java
```

If the project uses a different main-class configuration, run the configured Maven command.

The main class is:

```text
com.example.version1.client.SimpleClient
```

If you need to run it directly after compilation, use:

```bash
java -cp "target/classes:<dependencies>" \
com.example.version1.client.SimpleClient
```

On Windows, the classpath separator is `;` instead of `:`.

Using Maven is recommended because Maven automatically handles the project's dependencies.

---

# 11. Start-Up Sequence

The recommended startup sequence is:

```text
1. Start MySQL
        |
        v
2. Start Transaction Server
        |
        v
3. Verify https://localhost:9090
        |
        v
4. Open Client Project
        |
        v
5. Run mvn clean package
        |
        v
6. Run mvn exec:java
        |
        v
7. Enter Username and Password
        |
        v
8. Enter OTP
        |
        v
9. JWT received
        |
        v
10. User Menu
```

The transaction server must be started **before** the client.

---

# 12. First Client Run

When the client is started for the first time, it executes:

```java
DatabaseManager.createTables();
```

SQLite creates:

```text
banking_client.db
```

and the following table:

```text
transactions
```

The client does not require manual database creation.

The expected project directory after the first successful run is:

```text
client/
│
├── banking_client.db
├── pom.xml
│
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── example/
│                   └── version1/
│                       └── client/
│                           ├── SimpleClient.java
│                           │
│                           └── finalmodule/
│                               └── DatabaseManager.java
│
└── target/
```

---

# 13. Login

Start the client:

```bash
mvn exec:java
```

The client displays:

```text
================================
       BANKING CLIENT
================================
Username:
Password:
```

Enter the credentials of a registered user.

The client sends:

```text
POST /login
```

If login is accepted, an OTP is sent to the user's email.

The client displays:

```text
OTP has been sent to your email.
Enter OTP:
```

Enter the received OTP.

The client sends:

```text
POST /login/verify-otp
```

After successful verification, the server returns a JWT.

The client then verifies the session using:

```text
POST /login/status
```

---

# 14. Successful Login

After successful authentication, the client displays:

```text
Login successful.
JWT received.
```

It then checks the login status.

A successful session allows access to:

```text
================================
          USER MENU
================================
1. Make Payment
2. Transaction History
3. Check Transaction Record
4. Logout
```

---

# 15. Make Payment

Select:

```text
1
```

The client asks for:

```text
Enter receiver username:
Enter amount:
Enter payment password:
Enter your account ID:
Enter receiver account ID:
```

The client sends:

```text
POST /payment/pay
```

using the JWT.

If accepted, the server returns a request ID.

The client stores the payment locally in:

```text
banking_client.db
```

---

# 16. Payment Status

After the payment request is submitted, the client waits for processing.

It then sends:

```text
POST /payment/status
```

using:

```text
request_id
username
JWT
```

The returned transaction information is used to update the local SQLite record.

The flow is:

```text
Payment Details
      |
      v
POST /payment/pay
      |
      v
Request ID
      |
      v
Save to SQLite
      |
      v
Wait
      |
      v
POST /payment/status
      |
      v
Update SQLite
```

---

# 17. Logout

To end the session, select:

```text
4
```

The client sends:

```text
POST /logout
```

with the JWT and username.

After successful logout, the client clears the JWT:

```java
token = null;
```

---

# 18. SQLite Schema

The client creates the following table:

```sql
CREATE TABLE IF NOT EXISTS transactions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    request_id TEXT UNIQUE NOT NULL,
    transaction_id TEXT,
    from_username TEXT,
    to_username TEXT,
    from_account TEXT,
    to_account TEXT,
    amount REAL,
    status TEXT,
    reason TEXT,
    created_at TEXT
);
```

### Columns

| Column           | Type    | Description               |
| ---------------- | ------- | ------------------------- |
| `id`             | INTEGER | Local record ID           |
| `request_id`     | TEXT    | Unique payment request ID |
| `transaction_id` | TEXT    | Server transaction ID     |
| `from_username`  | TEXT    | Sender username           |
| `to_username`    | TEXT    | Receiver username         |
| `from_account`   | TEXT    | Sender account            |
| `to_account`     | TEXT    | Receiver account          |
| `amount`         | REAL    | Payment amount            |
| `status`         | TEXT    | Current payment status    |
| `reason`         | TEXT    | Failure/rejection reason  |
| `created_at`     | TEXT    | Record creation time      |

---

# 19. SQLite Transaction Lifecycle

When the payment request is accepted, the client creates a local record:

```text
request_id
from_username
to_username
from_account
to_account
amount
status
created_at
```

After the payment is processed, the client updates:

```text
transaction_id
status
reason
```

The record is identified using:

```text
request_id
```

This allows the client to maintain a local history of payment requests and their latest known status.

---

# 20. API Endpoints

| Method | Endpoint            | Purpose                      | Authentication |
| ------ | ------------------- | ---------------------------- | -------------- |
| POST   | `/login`            | Login with username/password | No             |
| POST   | `/login/verify-otp` | Verify OTP and obtain JWT    | No             |
| POST   | `/login/status`     | Validate user session        | JWT            |
| POST   | `/payment/pay`      | Submit payment               | JWT            |
| POST   | `/payment/status`   | Check payment status         | JWT            |
| POST   | `/logout`           | Logout user                  | JWT            |

---

# 21. HTTPS

The client communicates with the server using:

```text
HTTPS
```

The configured server URL is:

```text
https://localhost:9090
```

The client creates an `SSLContext` for the HTTPS connection.

The current implementation trusts server certificates without normal certificate validation.

> This configuration is suitable for the local development environment only. Production deployments should use proper certificate validation and a trusted certificate authority.

---

# 22. Troubleshooting

### `Connection refused`

Make sure the server is running:

```text
https://localhost:9090
```

Check:

```text
SimpleClient.java
```

and verify:

```java
private static final String BASE_URL =
        "https://localhost:9090";
```

---

### `SSLHandshakeException`

Verify that:

* The server is running with HTTPS.
* Port `9090` is correct.
* The server SSL configuration is enabled.
* The client is connecting using `https://`, not `http://`.

---

### `mvn: command not found`

Install Maven and verify:

```bash
mvn -version
```

---

### `java: command not found`

Install Java and verify:

```bash
java -version
```

---

### SQLite database not created

Check that:

* The application has write permission in the project directory.
* The SQLite JDBC dependency exists in `pom.xml`.
* `DatabaseManager.createTables()` is executed.
* The client reaches the database initialization stage.

The expected file is:

```text
banking_client.db
```

---

### OTP verification failed

Verify:

* The username is correct.
* The OTP has not expired.
* The OTP was entered correctly.
* The server's email/OTP service is running.

---

### Payment request failed

Verify:

* The user is logged in.
* The JWT is valid.
* The receiver username exists.
* The sender account is valid.
* The receiver account is valid.
* The payment password is correct.
* The payment amount is valid.
* The transaction server is running.

---

# 23. Quick Start

For a normal local development setup:

### Terminal 1 — Start the Transaction Server

Start the Transaction System Simulation Server using its configured Maven command.

Verify that it is available on:

```text
https://localhost:9090
```

### Terminal 2 — Start the Client

```bash
cd client
```

Build:

```bash
mvn clean package
```

Run:

```bash
mvn exec:java
```

Then:

```text
1. Enter username
2. Enter password
3. Enter OTP received by email
4. Wait for login-status verification
5. Select Make Payment or Logout
```

The local SQLite database:

```text
banking_client.db
```

will be created automatically.

---

# 24. Complete Client Flow

```text
                    START
                      |
                      v
              Start Server First
                      |
                      v
              Start Java Client
                      |
                      v
             Username + Password
                      |
                      v
                 /login
                      |
                      v
                 OTP Sent
                      |
                      v
               Enter OTP
                      |
                      v
             /login/verify-otp
                      |
                      v
                    JWT
                      |
                      v
               /login/status
                      |
                      v
                USER MENU
                 /      \
                /        \
               v          v
        Make Payment     Logout
               |
               v
         /payment/pay
               |
               v
          Request ID
               |
               v
       Save SQLite Record
               |
               v
       /payment/status
               |
               v
       Update SQLite Record
```

---

# 25. Summary

The Transaction System Simulation Client provides a command-line interface for interacting with the transaction server.

The client handles:

```text
Authentication
     |
     +-- Username / Password
     +-- OTP Verification
     +-- JWT
     +-- Session Validation
     
Payments
     |
     +-- Submit Payment
     +-- Request ID
     +-- Payment Status
     
Local Storage
     |
     +-- SQLite
     +-- Transaction Records
     +-- Status Updates
     
Session
     |
     +-- Logout
```

The client uses **HTTPS for communication with the server** and **SQLite for local transaction record storage**.
