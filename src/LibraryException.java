package src;

/**
 * Custom exception class for Library Management System
 * Demonstrates Exception Handling as per OOP requirements
 */
public class LibraryException extends Exception {
    
    public LibraryException(String message) {
        super(message);
    }
    
    public LibraryException(String message, Throwable cause) {
        super(message, cause);
    }
    
    // Specific exception types
    public static class BookNotFoundException extends LibraryException {
        public BookNotFoundException(String bookId) {
            super("Book with ID '" + bookId + "' not found!");
        }
    }
    
    public static class MemberNotFoundException extends LibraryException {
        public MemberNotFoundException(String memberId) {
            super("Member with ID '" + memberId + "' not found!");
        }
    }
    
    public static class BookAlreadyIssuedException extends LibraryException {
        public BookAlreadyIssuedException(String bookId) {
            super("Book with ID '" + bookId + "' is already issued!");
        }
    }
    
    public static class BookNotIssuedException extends LibraryException {
        public BookNotIssuedException(String bookId) {
            super("Book with ID '" + bookId + "' is not currently issued!");
        }
    }
    
    public static class DuplicateIdException extends LibraryException {
        public DuplicateIdException(String id, String type) {
            super(type + " with ID '" + id + "' already exists!");
        }
    }
    
    public static class InvalidInputException extends LibraryException {
        public InvalidInputException(String field) {
            super("Invalid input for field: " + field);
        }
    }
    
    public static class FileOperationException extends LibraryException {
        public FileOperationException(String operation, Throwable cause) {
            super("Error during " + operation + " operation", cause);
        }
    }
}