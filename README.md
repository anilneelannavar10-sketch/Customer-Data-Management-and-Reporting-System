# 👥 Customer Data Management & Reporting System

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)
![HTML5](https://img.shields.io/badge/HTML5-E34F26?logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?logo=css3&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?logo=javascript&logoColor=black)
![License](https://img.shields.io/badge/License-MIT-green)

A full-stack customer management application: a **Java** backend (JDBC +
a lightweight built-in HTTP server) exposing a REST API backed by **MySQL**,
with a **HTML/CSS/JavaScript** web interface for data entry, search, and
reporting.

## ✨ Features

- ➕ **Data entry** — add new customer records through a web form
- ✏️ **Record management** — edit and delete existing customers
- ✅ **Validation** — required fields and email format checked server-side (`CustomerDAO`)
- 🔍 **Search** — live search by name, email, or city
- 📊 **Reporting** — summary dashboard (total/active/inactive counts, customers by city) built from SQL aggregation
- 🌐 **REST API** — clean JSON endpoints consumed by the frontend

## 🗂️ Project Structure

```
customer_management_system/
├── src/main/java/com/customerapp/
│   ├── Main.java                  # starts the HTTP server
│   ├── model/Customer.java        # customer data model
│   ├── dao/CustomerDAO.java       # JDBC + SQL: CRUD, search, validation, reporting
│   ├── db/DatabaseConnection.java # MySQL connection setup
│   └── server/
│       ├── ApiHandler.java        # REST API routes (/api/customers)
│       ├── StaticFileHandler.java # serves the web/ frontend
│       └── JsonUtil.java          # small JSON parsing helper
├── web/
│   ├── index.html                 # UI: form, table, report dashboard
│   ├── style.css
│   └── script.js                  # fetch() calls to the REST API
├── sql/
│   ├── schema.sql                 # table definition + seed data
│   └── queries.sql                # reporting queries (filter, update, summarize)
├── LICENSE
└── README.md
```

## 🏗️ Architecture

```
Browser (HTML/CSS/JS)
        │  fetch() → JSON
        ▼
Java HTTP Server (com.sun.net.httpserver)
        │
        ├── StaticFileHandler   → serves index.html / style.css / script.js
        └── ApiHandler          → REST endpoints
                │
                ▼
        CustomerDAO (JDBC + SQL)
                │
                ▼
             MySQL
```

No external web framework (Spring, etc.) is used — the REST API is built
directly on Java's built-in `HttpServer`, which keeps the project dependency-free
except for the MySQL JDBC driver. This was a deliberate choice to keep the
project runnable with just `javac`/`java` and demonstrate the underlying
HTTP + JDBC concepts directly.

## 🚀 Getting Started

### Prerequisites
- JDK 17+
- MySQL 8.0+
- MySQL Connector/J (JDBC driver) — [download here](https://dev.mysql.com/downloads/connector/j/)

### 1. Set up the database
```bash
mysql -u root -p < sql/schema.sql
```

### 2. Configure the connection
Edit `src/main/java/com/customerapp/db/DatabaseConnection.java` and set your
MySQL username/password.

### 3. Add the JDBC driver to your classpath
Download `mysql-connector-j-<version>.jar` and place it in a `lib/` folder
at the project root.

### 4. Compile
```bash
mkdir build
javac -cp "lib/*" -d build $(find src -name "*.java")
```

### 5. Run
```bash
java -cp "build:lib/*" com.customerapp.Main
```
(On Windows, use `;` instead of `:` in the classpath.)

### 6. Open the app
Go to **http://localhost:8080** in your browser.

## 🔌 REST API Reference

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/customers` | List all customers |
| GET | `/api/customers?q=smith` | Search customers |
| POST | `/api/customers` | Add a new customer |
| PUT | `/api/customers/{id}` | Update a customer |
| DELETE | `/api/customers/{id}` | Delete a customer |
| GET | `/api/customers/report` | Summary report (counts, by-city breakdown) |

## 🧪 SQL Operations Demonstrated
See `sql/queries.sql` for the full set:
- Filtering (`WHERE`, `LIKE` for search)
- Updating records (`UPDATE ... WHERE`)
- Aggregation & summarizing (`GROUP BY`, `COUNT`)
- Date-based trend reporting (`DATE_FORMAT`, `INTERVAL`)

## 🛠️ Tech Stack
Java (JDBC, `com.sun.net.httpserver`) · MySQL · SQL · HTML5 · CSS3 · JavaScript (fetch API)

## 🔮 Future Improvements
- Migrate to Spring Boot for a more production-ready structure
- Add pagination for large customer lists
- Add authentication/authorization
- Replace the hand-rolled JSON parser with Jackson/Gson
- Add unit tests for `CustomerDAO` validation logic

## 📄 License
This project is licensed under the [MIT License](LICENSE).
