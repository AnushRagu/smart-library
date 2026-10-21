# Smart Library Management System — Demo and Viva Guide

## Before the demo

1. Start MySQL and confirm the `library_db` database is available.
2. Import `sql/schema_seed.sql`. **This resets the four project tables**, so do not rerun it when you need to preserve live library data.
3. Import `sql/add_sample_data.sql` to add the expanded demo set without resetting the base scenario.
4. Set `LIBRARY_DB_USER` and `LIBRARY_DB_PASSWORD` if your local MySQL credentials differ from the defaults.
5. From the project folder, run `./run.sh`. The app compiles its Java source and opens the Swing window.

After both SQL files are imported, the demo database contains 20 authors, 50 titles, 50 members, and 200 loan records. It includes returned loans, active loans, overdue loans, and currently available copies. Counts can differ after changes in a live database.

## Suggested demonstration (about 5 minutes)

1. **Dashboard:** Explain the five live totals: book titles, available copies, members, active loans, and overdue loans.
2. **Books:** Search for `Monsoon`, then show the genre filter and the total versus available copy counts. Mention that the query joins books to authors.
3. **Authors and Members:** Show the existing tables and the add/edit/delete controls. Explain that foreign keys prevent deleting records already referenced by books or loans.
4. **Issue a book:** In Loans & Reports, choose Active Loans, select Issue Book, and choose an available title and a member. Show the new loan and the decrease in available stock.
5. **Return the book:** Select that loan and choose Return Selected Loan. Show that stock is restored, the return date is recorded, and the loan remains visible in Loan History.
6. **Overdue report:** Choose Overdue Loans and explain that overdue status is calculated from the database's current date. The fine is ₹5 per late day.

For a repeatable demo, use a book with at least one available copy. If you need a completely clean scenario again, re-import the seed script only after deciding that resetting the tables is acceptable.

## Viva points

### Java and OOP

- **Encapsulation:** UI state and service dependencies are held in private fields. `Database` is a non-instantiable connection factory.
- **Inheritance:** `MainFrame` extends Swing's `JFrame`. The table model is an anonymous subclass of `DefaultTableModel`.
- **Polymorphism:** The table model overrides `isCellEditable`; Swing event callbacks are supplied through the `ActionListener` interface.
- **Abstraction and separation:** `MainFrame` presents the UI, `LibraryService` validates business inputs, `LibraryRepository` owns SQL, and `Database` creates JDBC connections. The JDBC and Swing APIs also provide framework abstractions.
- **Constructors:** `MainFrame` receives a `LibraryService`; `LibraryService` receives a `LibraryRepository`. This is constructor-based dependency injection.
- **Collections:** The repository uses `List`/`ArrayList` for query results and genre values; Swing combo boxes hold author, book, and member choices.
- **Exception handling:** SQL operations declare `SQLException`; JDBC resources use try-with-resources. Issue and return roll back on SQL failures. The UI catches failures and shows a dialog.

### Database and DBMS

- **Relationships:** One author can have many books. One book can appear in many loans. One member can have many loans. `loans` is the associative transaction table linking books and members over time.
- **Keys and constraints:** Every table has an auto-increment primary key. Foreign keys protect references. `NOT NULL`, uniqueness, and `CHECK` constraints enforce valid records. Delete rules are `RESTRICT` for referenced authors, books, and members.
- **CRUD and joins:** The app creates, reads, updates, and deletes authors, books, and members. Catalogue, active loan, overdue loan, and history views use joins to show related names and titles.
- **Prepared statements:** User-provided search text and form values are bound parameters, not concatenated into SQL.
- **Aggregates:** The dashboard uses `COUNT` and `SUM`; the assignment query file demonstrates `GROUP BY`, `HAVING`, inner joins, and left joins.
- **Transactions:** Issuing a book locks the book row, checks availability and the member's active-loan limit, inserts the loan, decrements stock, and commits as one transaction. Returning locks the open loan, calculates and stores any fine, restores stock, and commits. SQL failures cause rollback.

### Extra feature details

- Loan term: 14 days.
- Fine: ₹5 for each day after the due date.
- An unreturned loan's current fine is calculated through today's database date. When it is returned, the assessed fine is stored in `loans.fine_amount`.
- The Loan History view includes active and returned records, with current or assessed fine amounts.

## Honest test report for presentation

- The five Java classes compiled successfully with JDK 21 and the included Connector/J driver.
- The MySQL schema/seed script and the 15 supplied scenario queries were run successfully in the local MySQL environment.
- The live Swing UI was opened against the seeded database. Dashboard counts and the Books catalogue were observed; dashboard, search, active/overdue views, and issue/return flow were exercised during the project work.
- There is no automated unit-test suite in this project. Do not describe the manual demo as automated test coverage.
