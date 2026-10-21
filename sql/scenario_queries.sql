USE library_db;

-- Q1 Fiction titles and copy counts
SELECT title, total_copies AS copies FROM books WHERE genre = 'Fiction' ORDER BY book_id;
-- Q2 Authors from India
SELECT name, country FROM authors WHERE country = 'India';
-- Q3 Books with more than 3 copies, most first
SELECT title, total_copies AS copies FROM books WHERE total_copies > 3 ORDER BY total_copies DESC, title;
-- Q4 Members in Chennai
SELECT name, city FROM members WHERE city = 'Chennai';
-- Q5 Unreturned loans
SELECT loan_id, book_id, member_id, due_date FROM loans WHERE return_date IS NULL ORDER BY loan_id;
-- Q6 Book title with author
SELECT b.title, a.name AS author FROM books b JOIN authors a ON b.author_id = a.author_id ORDER BY b.book_id;
-- Q7 Loans issued after 2026-09-10
SELECT loan_id, issue_date FROM loans WHERE issue_date > '2026-09-10' ORDER BY issue_date;
-- Q8 Titles beginning with M
SELECT title FROM books WHERE title LIKE 'M%' ORDER BY title;
-- Q9 Active loans ordered by due date
SELECT m.name AS member, b.title, l.due_date FROM loans l
JOIN members m ON l.member_id = m.member_id JOIN books b ON l.book_id = b.book_id
WHERE l.return_date IS NULL ORDER BY l.due_date;
-- Q10 Books per author, including authors with none
SELECT a.name, COUNT(b.book_id) AS book_count FROM authors a LEFT JOIN books b ON a.author_id = b.author_id
GROUP BY a.author_id, a.name ORDER BY book_count DESC, a.name;
-- Q11 Loans per member, including members with none
SELECT m.name, COUNT(l.loan_id) AS loans_taken FROM members m LEFT JOIN loans l ON m.member_id = l.member_id
GROUP BY m.member_id, m.name ORDER BY loans_taken DESC, m.name;
-- Q12 Late returns and days late
SELECT m.name AS member, b.title, DATEDIFF(l.return_date, l.due_date) AS days_late FROM loans l
JOIN members m ON l.member_id = m.member_id JOIN books b ON l.book_id = b.book_id
WHERE l.return_date > l.due_date ORDER BY days_late DESC;
-- Q13 Books never borrowed
SELECT b.title FROM books b LEFT JOIN loans l ON b.book_id = l.book_id WHERE l.loan_id IS NULL;
-- Q14 Books borrowed more than once
SELECT b.title, COUNT(*) AS times_borrowed FROM books b JOIN loans l ON b.book_id = l.book_id
GROUP BY b.book_id, b.title HAVING COUNT(*) > 1 ORDER BY times_borrowed DESC, b.title;
-- Q15 Authors whose books have at least 3 loans
SELECT a.name, COUNT(l.loan_id) AS total_loans FROM authors a
JOIN books b ON a.author_id = b.author_id JOIN loans l ON b.book_id = l.book_id
GROUP BY a.author_id, a.name HAVING COUNT(l.loan_id) >= 3 ORDER BY total_loans DESC, a.name;
