package smartlibrary;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/** Validates application rules before delegating persistence to the repository. */
public class LibraryService {
    private final LibraryRepository repository;
    public LibraryService(LibraryRepository repository) { this.repository=repository; }
    public LibraryRepository repository() { return repository; }

    public void saveAuthor(Integer id,String name,String country) throws SQLException {
        required(name,"Author name");required(country,"Country");repository.saveAuthor(id,name.trim(),country.trim());
    }
    public void saveBook(Integer id,String title,int authorId,String genre,int total,int available) throws SQLException {
        required(title,"Title");required(genre,"Genre");if(total<1)throw new IllegalArgumentException("Total copies must be at least 1.");
        int borrowed=total-available;if(available<0||borrowed<0)throw new IllegalArgumentException("Total copies cannot be less than the copies currently on loan.");
        repository.saveBook(id,title.trim(),authorId,genre.trim(),total,available);
    }
    public void saveMember(Integer id,String name,String city,LocalDate joined,String email) throws SQLException {
        required(name,"Member name");required(city,"City");if(joined==null)throw new IllegalArgumentException("Join date is required.");
        if(email!=null&&!email.trim().isEmpty()&&!email.trim().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+"))throw new IllegalArgumentException("Enter a valid email address or leave it blank.");
        repository.saveMember(id,name.trim(),city.trim(),joined,email);
    }
    public void issue(int bookId,int memberId) throws SQLException { repository.issueBook(bookId,memberId); }
    private void required(String value,String label) { if(value==null||value.trim().isEmpty())throw new IllegalArgumentException(label+" is required."); }
}
