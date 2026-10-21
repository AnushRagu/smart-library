-- Optional Scenario 3-compatible expansion for an already initialized library_db.
-- Adds 15 authors, 42 books, 45 members, and 190 varied loan records (IDs 6–200).
-- Safe to rerun against the original Scenario 3 seed: fixed sample IDs are ignored if present.
USE library_db;

INSERT IGNORE INTO authors (author_id, name, country) VALUES
(6,'Ana Moreno','Spain'),(7,'Amina Yusuf','Kenya'),(8,'David Okafor','Nigeria'),
(9,'Elena Petrova','Russia'),(10,'Gabriel Santos','Brazil'),(11,'Hannah Brooks','USA'),
(12,'Ishan Mehta','India'),(13,'Jun Park','South Korea'),(14,'Laila Haddad','Lebanon'),
(15,'Mateo Silva','Chile'),(16,'Noor Rahman','Bangladesh'),(17,'Olivia Chen','Singapore'),
(18,'Peter Novak','Czechia'),(19,'Rina Sato','Japan'),(20,'Samuel Mensah','Ghana');

INSERT IGNORE INTO books (book_id, title, author_id, genre, total_copies, available_copies) VALUES
(9,'The Lantern Archive',6,'Fiction',4,4),(10,'Ashes of Tomorrow',7,'Fiction',5,5),
(11,'Paper Kingdom',8,'Mystery',4,4),(12,'Tidekeepers',9,'Fiction',3,3),
(13,'The Map Room',10,'Mystery',5,5),(14,'Thread of Gold',11,'Romance',4,4),
(15,'Orchard of Stars',12,'Poetry',3,3),(16,'The Glass Gardener',13,'Fiction',6,6),
(17,'Small Hours at North Pier',14,'Mystery',4,4),(18,'A City Remembered',15,'History',5,5),
(19,'The Last Observatory',16,'Science Fiction',4,4),(20,'Stone and Sparrow',17,'Fiction',3,3),
(21,'Letters from the Coast',18,'Romance',5,5),(22,'The Winter Index',19,'Mystery',4,4),
(23,'A House of Rain',20,'Fiction',6,6),(24,'Emberline',6,'Science Fiction',4,4),
(25,'Beyond the Banyan',7,'Fiction',5,5),(26,'The Quiet Equation',8,'Science',3,3),
(27,'Mosaic of the Moon',9,'Poetry',4,4),(28,'The Indigo Ledger',10,'Mystery',5,5),
(29,'A Field Guide to Elsewhere',11,'Travel',4,4),(30,'The Clockmaker’s Garden',12,'Fantasy',6,6),
(31,'Saltwind Stories',13,'Fiction',3,3),(32,'The Ninth Window',14,'Mystery',5,5),
(33,'A Brief Atlas of Rain',15,'Travel',4,4),(34,'The Orchard Keeper’s Map',16,'Fiction',5,5),
(35,'Night Train to Juniper',17,'Romance',4,4),(36,'The Borrowed Constellation',18,'Science Fiction',6,6),
(37,'The Museum of Small Things',19,'Fiction',3,3),(38,'River Without Bridges',20,'Poetry',5,5),
(39,'The Cedar House',6,'Mystery',4,4),(40,'Notes from the Blue Room',7,'Fiction',5,5),
(41,'The Cartographer’s Promise',8,'Adventure',6,6),(42,'A Season of Copper',9,'Historical Fiction',4,4),
(43,'The Orchard at Dawn',10,'Romance',3,3),(44,'Midwinter Dispatches',11,'Mystery',5,5),
(45,'The Secret Life of Maps',12,'Nonfiction',4,4),(46,'The Paper Astronomer',13,'Science Fiction',6,6),
(47,'Where the Hills Begin',14,'Travel',5,5),(48,'The Memory of Seeds',15,'Fiction',4,4),
(49,'A Lantern for Every Harbor',16,'Poetry',3,3),(50,'The Unfinished Library',17,'Mystery',5,5);

INSERT IGNORE INTO members (member_id, name, city, join_date, email) VALUES
(6,'Farah Iqbal','Pune','2026-01-12','farah.iqbal@example.org'),
(7,'Nikhil Rao','Bengaluru','2026-01-18','nikhil.rao@example.org'),
(8,'Mira Joseph','Kochi','2026-01-24','mira.joseph@example.org'),
(9,'Rohan Das','Kolkata','2026-02-02','rohan.das@example.org'),
(10,'Ananya Bose','Kolkata','2026-02-08','ananya.bose@example.org'),
(11,'Kabir Shah','Ahmedabad','2026-02-14','kabir.shah@example.org'),
(12,'Leela Menon','Thiruvananthapuram','2026-02-21','leela.menon@example.org'),
(13,'Dev Patel','Surat','2026-03-01','dev.patel@example.org'),
(14,'Sana Khan','Hyderabad','2026-03-07','sana.khan@example.org'),
(15,'Ira Kulkarni','Pune','2026-03-13','ira.kulkarni@example.org'),
(16,'Vikram Sen','Delhi','2026-03-19','vikram.sen@example.org'),
(17,'Tara Nambiar','Kochi','2026-03-25','tara.nambiar@example.org'),
(18,'Aditya Ghosh','Kolkata','2026-04-01','aditya.ghosh@example.org'),
(19,'Zoya Mirza','Lucknow','2026-04-07','zoya.mirza@example.org'),
(20,'Neel Desai','Ahmedabad','2026-04-13','neel.desai@example.org'),
(21,'Pooja Iyer','Chennai','2026-04-19','pooja.iyer@example.org'),
(22,'Armaan Ali','Jaipur','2026-04-25','armaan.ali@example.org'),
(23,'Diya Roy','Bhubaneswar','2026-05-02','diya.roy@example.org'),
(24,'Kiran Thomas','Kochi','2026-05-08','kiran.thomas@example.org'),
(25,'Maya Kapoor','Delhi','2026-05-14','maya.kapoor@example.org'),
(26,'Siddharth Jain','Jaipur','2026-05-20','siddharth.jain@example.org'),
(27,'Nandita Paul','Guwahati','2026-05-26','nandita.paul@example.org'),
(28,'Omar Siddiqui','Hyderabad','2026-06-02','omar.siddiqui@example.org'),
(29,'Rhea Banerjee','Kolkata','2026-06-08','rhea.banerjee@example.org'),
(30,'Aarav Bhat','Bengaluru','2026-06-14','aarav.bhat@example.org'),
(31,'Meher Sethi','Chandigarh','2026-06-20','meher.sethi@example.org'),
(32,'Eshan Mukherjee','Kolkata','2026-06-26','eshan.mukherjee@example.org'),
(33,'Lavanya Pillai','Chennai','2026-07-03','lavanya.pillai@example.org'),
(34,'Rehan Qureshi','Bhopal','2026-07-09','rehan.qureshi@example.org'),
(35,'Saira Dutta','Guwahati','2026-07-15','saira.dutta@example.org'),
(36,'Manav Khanna','Delhi','2026-07-21','manav.khanna@example.org'),
(37,'Aditi Kulkarni','Pune','2026-07-27','aditi.kulkarni@example.org'),
(38,'Yusuf Fernandes','Goa','2026-08-03','yusuf.fernandes@example.org'),
(39,'Nisha Rangan','Coimbatore','2026-08-09','nisha.rangan@example.org'),
(40,'Pranav Suri','Chandigarh','2026-08-15','pranav.suri@example.org'),
(41,'Amara George','Kochi','2026-08-21','amara.george@example.org'),
(42,'Dhruv Malhotra','Jaipur','2026-08-27','dhruv.malhotra@example.org'),
(43,'Sonal Chawla','Delhi','2026-09-03','sonal.chawla@example.org'),
(44,'Ibrahim Sheikh','Hyderabad','2026-09-09','ibrahim.sheikh@example.org'),
(45,'Ritika Sanyal','Kolkata','2026-09-15','ritika.sanyal@example.org'),
(46,'Karthik Balan','Madurai','2026-09-21','karthik.balan@example.org'),
(47,'Anika D’Souza','Mumbai','2026-09-27','anika.dsouza@example.org'),
(48,'Samarjeet Gill','Amritsar','2026-10-03','samarjeet.gill@example.org'),
(49,'Ayesha Noor','Lucknow','2026-10-09','ayesha.noor@example.org'),
(50,'Ravi Prasad','Visakhapatnam','2026-10-15','ravi.prasad@example.org');

-- Generate dated circulation records relative to the database date so the demo always
-- contains returned loans, overdue loans, and currently borrowed books.
INSERT IGNORE INTO loans (loan_id, book_id, member_id, issue_date, due_date, return_date, fine_amount)
WITH RECURSIVE sequence_numbers (n) AS (
  SELECT 11
  UNION ALL
  SELECT n + 1 FROM sequence_numbers WHERE n < 200
), planned_loans AS (
  SELECT n,
         9 + MOD(n - 11, 42) AS book_id,
         6 + MOD((n - 11) * 11, 45) AS member_id,
         CASE WHEN MOD(n, 4) = 0 AND MOD(n, 8) = 0 THEN 6
              WHEN MOD(n, 4) = 0 THEN 45 + MOD(n * 11, 90)
              ELSE 45 + MOD(n * 7, 150) END AS age_days,
         MOD(n * 5, 16) AS late_days,
         MOD(n, 4) = 0 AS is_active
  FROM sequence_numbers
), dated_loans AS (
  SELECT n, book_id, member_id, age_days, late_days, is_active,
         DATE_SUB(CURDATE(), INTERVAL age_days DAY) AS issue_date
  FROM planned_loans
)
SELECT n, book_id, member_id, issue_date,
       DATE_ADD(issue_date, INTERVAL 14 DAY),
       CASE WHEN is_active THEN NULL
            ELSE DATE_ADD(issue_date, INTERVAL (14 + late_days) DAY) END,
       CASE WHEN is_active THEN 0.00 ELSE late_days * 5.00 END
FROM dated_loans;

-- Recompute inventory from all open loans, including the original Scenario 3 rows.
UPDATE books b SET available_copies = total_copies - (
  SELECT COUNT(*) FROM loans l WHERE l.book_id = b.book_id AND l.return_date IS NULL
);
