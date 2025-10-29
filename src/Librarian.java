package src;
import java.io.Serializable;

public class Librarian implements Serializable {
    private String id;
    private String name;
    private String password;

    public Librarian(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean login(String enteredId, String enteredPassword) {
        return this.id.equals(enteredId) && this.password.equals(enteredPassword);
    }

    @Override
    public String toString() {
        return "Librarian ID: " + id + " | Name: " + name;
    }
}