CREATE DATABASE IF NOT EXISTS library_db CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE library_db;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS authors;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE authors (
  author_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  country VARCHAR(30) NOT NULL,
  CONSTRAINT uq_author_name_country UNIQUE (name, country)
) ENGINE=InnoDB;

CREATE TABLE books (
  book_id INT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL,
  author_id INT NOT NULL,
  genre VARCHAR(30) NOT NULL,
  total_copies INT NOT NULL,
  available_copies INT NOT NULL,
  CONSTRAINT fk_books_author FOREIGN KEY (author_id) REFERENCES authors(author_id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT ck_books_total CHECK (total_copies >= 0),
  CONSTRAINT ck_books_available CHECK (available_copies >= 0 AND available_copies <= total_copies),
  INDEX ix_books_title (title),
  INDEX ix_books_genre (genre)
) ENGINE=InnoDB;

CREATE TABLE members (
  member_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  city VARCHAR(50) NOT NULL,
  join_date DATE NOT NULL,
  email VARCHAR(120) NULL,
  CONSTRAINT uq_members_email UNIQUE (email)
) ENGINE=InnoDB;

CREATE TABLE loans (
  loan_id INT PRIMARY KEY AUTO_INCREMENT,
  book_id INT NOT NULL,
  member_id INT NOT NULL,
  issue_date DATE NOT NULL,
  due_date DATE NOT NULL,
  return_date DATE NULL,
  fine_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  CONSTRAINT fk_loans_book FOREIGN KEY (book_id) REFERENCES books(book_id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_loans_member FOREIGN KEY (member_id) REFERENCES members(member_id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT ck_loans_dates CHECK (due_date >= issue_date),
  CONSTRAINT ck_loans_fine CHECK (fine_amount >= 0),
  INDEX ix_loans_active (return_date, due_date),
  INDEX ix_loans_member (member_id),
  INDEX ix_loans_book (book_id)
) ENGINE=InnoDB;

INSERT INTO authors (author_id, name, country) VALUES
(1,'Meera Nair','India'),(2,'John Carter','UK'),(3,'Kavya Reddy','India'),
(4,'Sophie Laurent','France'),(5,'Hiro Tanaka','Japan');

INSERT INTO books (book_id, title, author_id, genre, total_copies, available_copies) VALUES
(1,'Monsoon Tales',1,'Fiction',5,5),(2,'The Silent Clock',2,'Mystery',3,3),
(3,'Code of Honor',3,'Fiction',4,4),(4,'Midnight Express',2,'Mystery',2,2),
(5,'City of Lights',4,'Romance',3,3),(6,'River Songs',1,'Poetry',2,2),
(7,'Startup Diaries',3,'Business',6,6),(8,'Paper Boats',1,'Fiction',4,4);

INSERT INTO members (member_id, name, city, join_date) VALUES
(1,'Arjun','Chennai','2026-01-10'),(2,'Bhuvana','Madurai','2026-02-14'),
(3,'Chandru','Chennai','2026-03-01'),(4,'Devi','Coimbatore','2026-04-20'),
(5,'Elango','Trichy','2026-05-11');

INSERT INTO loans (loan_id, book_id, member_id, issue_date, due_date, return_date, fine_amount) VALUES
(1,1,1,'2026-09-01','2026-09-15','2026-09-10',0.00),
(2,2,1,'2026-09-03','2026-09-17','2026-09-20',15.00),
(3,3,2,'2026-09-05','2026-09-19','2026-09-18',0.00),
(4,2,3,'2026-09-07','2026-09-21',NULL,0.00),
(5,4,2,'2026-09-08','2026-09-22',NULL,0.00),
(6,7,3,'2026-09-10','2026-09-24','2026-09-25',5.00),
(7,1,4,'2026-09-12','2026-09-26','2026-09-24',0.00),
(8,5,4,'2026-09-14','2026-09-28',NULL,0.00),
(9,6,1,'2026-09-15','2026-09-29',NULL,0.00),
(10,3,3,'2026-09-16','2026-09-30','2026-09-29',0.00);

-- Keep the assignment's original total-copy counts and derive current availability from open loans.
UPDATE books b SET available_copies = total_copies - (
  SELECT COUNT(*) FROM loans l WHERE l.book_id = b.book_id AND l.return_date IS NULL
);
