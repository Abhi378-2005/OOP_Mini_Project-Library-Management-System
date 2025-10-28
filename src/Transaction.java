package src;
import java.io.Serializable;
import java.time.LocalDate;

public class Transaction implements Serializable {
    private String transactionId;
    private String memberId;
    private String bookId;
    private LocalDate issueDate;
    private LocalDate returnDate;
    private boolean isReturned;

    public Transaction(String transactionId, String memberId, String bookId) {
        this.transactionId = transactionId;
        this.memberId = memberId;
        this.bookId = bookId;
        this.issueDate = LocalDate.now();
        this.returnDate = null;
        this.isReturned = false;
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public String getMemberId() { return memberId; }
    public String getBookId() { return bookId; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isReturned() { return isReturned; }

    // Mark transaction as returned
    public void markAsReturned() {
        this.isReturned = true;
        this.returnDate = LocalDate.now();
    }

    @Override
    public String toString() {
        return "Transaction ID: " + transactionId +
               " | Member ID: " + memberId +
               " | Book ID: " + bookId +
               " | Issued: " + issueDate +
               (isReturned ? " | Returned: " + returnDate : " | Not Returned");
    }
}