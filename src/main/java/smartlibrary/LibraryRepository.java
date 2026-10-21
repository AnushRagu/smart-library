package smartlibrary;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** All SQL lives here; UI code never assembles SQL from user input. */
public class LibraryRepository {
    public static final BigDecimal FINE_PER_DAY = new BigDecimal("5.00");
    private static final int LOAN_DAYS = 14;

    public List<String> getGenres() throws SQLException {
        List<String> values = new ArrayList<>();
        try (Connection c = Database.getConnection(); Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT DISTINCT genre FROM books ORDER BY genre")) {
            while (r.next()) values.add(r.getString(1));
        }
        return values;
    }

    public List<Object[]> getAuthors() throws SQLException {
        return rows("SELECT author_id, name, country FROM authors ORDER BY name", null);
    }

    public List<Object[]> getBooks(String search, String genre) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT b.book_id,b.title,a.name AS author,b.genre,b.total_copies,b.available_copies FROM books b JOIN authors a ON a.author_id=b.author_id WHERE 1=1");
        List<Object> args = new ArrayList<>();
        if (search != null && !search.trim().isEmpty()) { sql.append(" AND (b.title LIKE ? OR a.name LIKE ?)"); args.add("%" + search.trim() + "%"); args.add("%" + search.trim() + "%"); }
        if (genre != null && !genre.equals("All genres")) { sql.append(" AND b.genre=?"); args.add(genre); }
        sql.append(" ORDER BY b.title");
        return rows(sql.toString(), args);
    }

    public List<Object[]> getMembers() throws SQLException {
        return rows("SELECT member_id,name,city,join_date,email FROM members ORDER BY name", null);
    }

    public List<Object[]> getLoans(String view) throws SQLException {
        String condition;
        if ("Overdue Loans".equals(view)) condition = "WHERE l.return_date IS NULL AND l.due_date < CURDATE()";
        else if ("Active Loans".equals(view)) condition = "WHERE l.return_date IS NULL";
        else condition = "";
        String sql = "SELECT l.loan_id,m.name AS member,b.title,l.issue_date,l.due_date,l.return_date, " +
                "CASE WHEN l.return_date IS NULL THEN GREATEST(DATEDIFF(CURDATE(),l.due_date),0)*" + FINE_PER_DAY +
                " ELSE l.fine_amount END AS fine FROM loans l JOIN members m ON m.member_id=l.member_id " +
                "JOIN books b ON b.book_id=l.book_id " + condition + " ORDER BY l.due_date,l.loan_id";
        return rows(sql, null);
    }

    public int[] getDashboardStats() throws SQLException {
        int[] values = new int[5];
        String sql = "SELECT (SELECT COUNT(*) FROM books),(SELECT COALESCE(SUM(available_copies),0) FROM books)," +
                "(SELECT COUNT(*) FROM members),(SELECT COUNT(*) FROM loans WHERE return_date IS NULL)," +
                "(SELECT COUNT(*) FROM loans WHERE return_date IS NULL AND due_date<CURDATE())";
        try (Connection c=Database.getConnection(); Statement s=c.createStatement(); ResultSet r=s.executeQuery(sql)) {
            if (r.next()) for (int i=0;i<values.length;i++) values[i]=r.getInt(i+1);
        }
        return values;
    }

    private List<Object[]> rows(String sql, List<Object> args) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        try (Connection c=Database.getConnection(); PreparedStatement p=c.prepareStatement(sql)) {
            if (args != null) for (int i=0;i<args.size();i++) p.setObject(i+1,args.get(i));
            try (ResultSet r=p.executeQuery()) {
                ResultSetMetaData m=r.getMetaData(); int count=m.getColumnCount();
                while (r.next()) { Object[] row=new Object[count]; for(int i=0;i<count;i++) row[i]=r.getObject(i+1); result.add(row); }
            }
        }
        return result;
    }

    public void saveAuthor(Integer id, String name, String country) throws SQLException {
        String sql=id==null ? "INSERT INTO authors(name,country) VALUES(?,?)" : "UPDATE authors SET name=?,country=? WHERE author_id=?";
        try(Connection c=Database.getConnection(); PreparedStatement p=c.prepareStatement(sql)) {
            p.setString(1,name); p.setString(2,country); if(id!=null)p.setInt(3,id); p.executeUpdate();
        }
    }
    public void deleteAuthor(int id) throws SQLException { executeDelete("DELETE FROM authors WHERE author_id=?",id); }

    public void saveBook(Integer id, String title, int authorId, String genre, int total, int available) throws SQLException {
        String sql=id==null ? "INSERT INTO books(title,author_id,genre,total_copies,available_copies) VALUES(?,?,?,?,?)" : "UPDATE books SET title=?,author_id=?,genre=?,total_copies=?,available_copies=? WHERE book_id=?";
        try(Connection c=Database.getConnection(); PreparedStatement p=c.prepareStatement(sql)) {
            p.setString(1,title);p.setInt(2,authorId);p.setString(3,genre);p.setInt(4,total);p.setInt(5,available);if(id!=null)p.setInt(6,id);p.executeUpdate();
        }
    }
    public void deleteBook(int id) throws SQLException { executeDelete("DELETE FROM books WHERE book_id=?",id); }

    public void saveMember(Integer id, String name, String city, LocalDate joinDate, String email) throws SQLException {
        String sql=id==null ? "INSERT INTO members(name,city,join_date,email) VALUES(?,?,?,?)" : "UPDATE members SET name=?,city=?,join_date=?,email=? WHERE member_id=?";
        try(Connection c=Database.getConnection(); PreparedStatement p=c.prepareStatement(sql)) {
            p.setString(1,name);p.setString(2,city);p.setDate(3,Date.valueOf(joinDate));
            if(email==null||email.trim().isEmpty())p.setNull(4,Types.VARCHAR);else p.setString(4,email.trim());
            if(id!=null)p.setInt(5,id);p.executeUpdate();
        }
    }
    public void deleteMember(int id) throws SQLException { executeDelete("DELETE FROM members WHERE member_id=?",id); }
    private void executeDelete(String sql,int id) throws SQLException {
        try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement(sql)){p.setInt(1,id);p.executeUpdate();}
    }

    /** Locks the selected book row while checking and consuming its available copy. */
    public void issueBook(int bookId,int memberId) throws SQLException {
        try(Connection c=Database.getConnection()) {
            c.setAutoCommit(false);
            try {
                try(PreparedStatement p=c.prepareStatement("SELECT available_copies FROM books WHERE book_id=? FOR UPDATE")) {
                    p.setInt(1,bookId); try(ResultSet r=p.executeQuery()) { if(!r.next())throw new SQLException("Book not found."); if(r.getInt(1)<1)throw new SQLException("No available copies of this book."); }
                }
                try(PreparedStatement p=c.prepareStatement("SELECT member_id FROM members WHERE member_id=? FOR UPDATE")) {
                    p.setInt(1,memberId);try(ResultSet r=p.executeQuery()){if(!r.next())throw new SQLException("Member not found.");}
                }
                try(PreparedStatement p=c.prepareStatement("SELECT COUNT(*) FROM loans WHERE member_id=? AND return_date IS NULL")) {
                    p.setInt(1,memberId);try(ResultSet r=p.executeQuery()){r.next();if(r.getInt(1)>=5)throw new SQLException("A member can have at most 5 active loans.");}
                }
                try(PreparedStatement p=c.prepareStatement("INSERT INTO loans(book_id,member_id,issue_date,due_date) VALUES(?,?,CURDATE(),DATE_ADD(CURDATE(),INTERVAL ? DAY))")) {
                    p.setInt(1,bookId);p.setInt(2,memberId);p.setInt(3,LOAN_DAYS);p.executeUpdate();
                }
                try(PreparedStatement p=c.prepareStatement("UPDATE books SET available_copies=available_copies-1 WHERE book_id=? AND available_copies>0")) {
                    p.setInt(1,bookId);if(p.executeUpdate()!=1)throw new SQLException("No available copies of this book.");
                }
                c.commit();
            } catch(SQLException ex) { c.rollback(); throw ex; }
        }
    }

    /** Calculates the assessed fine and restores stock atomically; a loan can be returned once. */
    public BigDecimal returnLoan(int loanId) throws SQLException {
        try(Connection c=Database.getConnection()) {
            c.setAutoCommit(false);
            try {
                int bookId; BigDecimal fine;
                try(PreparedStatement p=c.prepareStatement("SELECT book_id,due_date FROM loans WHERE loan_id=? AND return_date IS NULL FOR UPDATE")) {
                    p.setInt(1,loanId);try(ResultSet r=p.executeQuery()){if(!r.next())throw new SQLException("Loan is missing or has already been returned.");bookId=r.getInt(1);}
                }
                try(PreparedStatement p=c.prepareStatement("SELECT GREATEST(DATEDIFF(CURDATE(),due_date),0)*? FROM loans WHERE loan_id=?")) {
                    p.setBigDecimal(1,FINE_PER_DAY);p.setInt(2,loanId);try(ResultSet r=p.executeQuery()){r.next();fine=r.getBigDecimal(1);}
                }
                try(PreparedStatement p=c.prepareStatement("UPDATE loans SET return_date=CURDATE(),fine_amount=? WHERE loan_id=? AND return_date IS NULL")) {
                    p.setBigDecimal(1,fine);p.setInt(2,loanId);if(p.executeUpdate()!=1)throw new SQLException("Loan was already returned.");
                }
                try(PreparedStatement p=c.prepareStatement("UPDATE books SET available_copies=available_copies+1 WHERE book_id=? AND available_copies<total_copies")) {
                    p.setInt(1,bookId);if(p.executeUpdate()!=1)throw new SQLException("Copy count could not be restored; transaction cancelled.");
                }
                c.commit();return fine;
            } catch(SQLException ex) { c.rollback();throw ex; }
        }
    }
}
