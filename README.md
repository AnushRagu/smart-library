# Smart Library Management System

A desktop end-semester project using Java Swing, JDBC, and MySQL. It implements the Scenario 3 Library Management tables and workflows, with automated overdue tracking, late fines, dashboard counts, search, and loan reports.

## Features

- Manage authors, books, and members (add, edit, delete).
- Search books by title/author, filter by genre, and see total and available copies.
- Issue a book to a member, recording the issue date and a 14-day due date.
- Return a loan safely; available copies and the late fine update in the same database transaction.
- View active loans, overdue loans, and full loan history with current/assessed fine.
- Dashboard totals for catalogue, availability, members, active loans, and overdue loans.
- Assignment query examples Q1–Q15 are in `sql/scenario_queries.sql`.

## Requirements

- JDK 8 or newer (JDK 17 recommended).
- MySQL 8 or newer.
- The included MySQL Connector/J JAR. JDBC is Java's database connectivity API; Connector/J is the MySQL JDBC driver.

No Maven, web server, framework, Python, or external service is used.

## Set up the database

1. Start MySQL.
2. Run `sql/schema_seed.sql` using MySQL Workbench or the MySQL command line. It creates `library_db` and imports the exact authors, books, members, and sample loans from Scenario 3. The script resets the four project tables when rerun, so back up any library data you want to keep first. Open seeded loans will show as overdue relative to today's date; that is intentional so the overdue screen can be demonstrated immediately.
3. Create a MySQL user for the app or use your local account. The app defaults to `localhost:3306`, database `library_db`, user `root`, and a blank password. Set environment variables to override these:

   - `LIBRARY_DB_URL` (default `jdbc:mysql://localhost:3306/library_db?serverTimezone=UTC`)
   - `LIBRARY_DB_USER` (default `root`)
   - `LIBRARY_DB_PASSWORD` (default blank)

## Build and run

From this project directory:

```sh
export LIBRARY_DB_USER="root"
export LIBRARY_DB_PASSWORD="your-mysql-password"
./run.sh
```

The launcher uses the included Connector/J JAR by default. Set `MYSQL_CONNECTOR_JAR` only if you want to use another copy.

If you installed Java with Homebrew on Apple Silicon, add its keg-only JDK to this terminal's path before running:

```sh
export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"
```

On Windows, compile and run with the equivalent classpath:

```bat
set MYSQL_CONNECTOR_JAR=C:\path\to\mysql-connector-j.jar
set LIBRARY_DB_USER=root
set LIBRARY_DB_PASSWORD=your-mysql-password
mkdir out
javac -encoding UTF-8 -cp "%MYSQL_CONNECTOR_JAR%" -d out src\main\java\smartlibrary\*.java
java -cp "out;%MYSQL_CONNECTOR_JAR%" smartlibrary.Main
```

The UI opens only after it can connect to MySQL. If connection fails, check that MySQL is running, the schema was imported, the driver path is correct, and the credentials are valid.

## OOP and DBMS points for the viva

- `MainFrame` handles the Swing presentation; `LibraryService` validates user actions; `LibraryRepository` contains SQL and transaction boundaries; `Database` owns JDBC connection creation.
- The schema has primary keys, foreign keys, `NOT NULL`, `CHECK`, and uniqueness constraints. The one-to-many relationships match Scenario 3.
- Queries use `PreparedStatement` for user input. Issue/return operations use transactions and row locks so copies cannot be over-issued or double-returned.
- `available_copies` is maintained transactionally and constrained to `0 <= available_copies <= total_copies`.
- Fine rate is ₹5 per day late. An open overdue loan shows the fine accrued through today; a returned loan stores the assessed fine.
- Overdue means an unreturned loan whose due date is before the current database date.

## Demo path

1. Open Dashboard and explain the live counts.
2. Open Books, search for “Monsoon”, then add a book or author if desired.
3. Issue an available book to a member. Show it in Active Loans and observe the available-copy count decrease.
4. Return it. Show the loan in History and the copy count increase. If the due date has passed, show the calculated fine.
5. Open Overdue Loans and explain that the date comparison is calculated from the database's current date.

The sample data has a fixed 2026-09 timeline from the supplied answer key, so four unreturned loans are overdue when first run on 2026-10-21. This makes the overdue feature visible without changing the assignment records.

## Project layout

```text
src/main/java/smartlibrary/   Java application
sql/schema_seed.sql           Schema and Scenario 3 sample data
sql/scenario_queries.sql      Assignment queries Q1–Q15
run.sh                        Compile and launch on macOS/Linux
```
# smart-library
