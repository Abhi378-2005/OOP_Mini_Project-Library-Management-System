package src;
import java.io.Serializable;
import java.util.ArrayList;

public class Member implements Serializable {
    private String id;
    private String name;
    private ArrayList<String> borrowedBooks = new ArrayList<>();

    public Member(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public ArrayList<String> getBorrowedBooks() { return borrowedBooks; }

    public void borrowBook(String bookId) { borrowedBooks.add(bookId); }
    public void returnBook(String bookId) { borrowedBooks.remove(bookId); }

    @Override
    public String toString() {
        return id + " | " + name + " | Borrowed: " + borrowedBooks.toString();
    }
}