package src;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate; // <-- Added this missing import
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors; 
import src.LibraryException; 
import src.LibraryException.FileOperationException; 

public class LibraryGUI extends JFrame {
    private ArrayList<Book> books;
    private ArrayList<Member> members;
    private ArrayList<Transaction> transactions;
    private final String bookFile = "data/books.txt";
    private final String memberFile = "data/members.txt";
    private final String transactionFile = "data/transactions.txt";
    
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    // --- FIX: Store table models as class fields ---
    private DefaultTableModel bookTableModel;
    private DefaultTableModel memberTableModel;
    private DefaultTableModel transactionTableModel;
    // --- END FIX ---

    // Color scheme
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color ACCENT_COLOR = new Color(46, 204, 113);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color TEXT_COLOR = new Color(44, 62, 80);

    public LibraryGUI() {
        
        // --- FIX: Handle FileOperationException during data loading ---
        try {
            books = FileHandler.readFromFile(bookFile);
        } catch (FileOperationException e) { 
            showMessage("Warning: Could not load book data. Starting with empty list. Error: " + e.getMessage(), 
                        "Data Load Error", JOptionPane.WARNING_MESSAGE);
            books = new ArrayList<>();
        }
        
        try {
             members = FileHandler.readFromFile(memberFile);
        } catch (FileOperationException e) { 
             showMessage("Warning: Could not load member data. Starting with empty list. Error: " + e.getMessage(), 
                        "Data Load Error", JOptionPane.WARNING_MESSAGE);
            members = new ArrayList<>();
        }
        
        try {
            transactions = FileHandler.readFromFile(transactionFile);
        } catch (FileOperationException e) { 
            showMessage("Warning: Could not load transaction data. Starting with empty list. Error: " + e.getMessage(), 
                        "Data Load Error", JOptionPane.WARNING_MESSAGE);
            transactions = new ArrayList<>();
        }
        // --- END FIX ---

        setTitle("Library Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        
        // Initialize all main panels
        mainPanel.add(createDashboardPanel(), "Dashboard");
        mainPanel.add(createBookPanel(), "BookManagement");
        mainPanel.add(createMemberPanel(), "MemberManagement");
        mainPanel.add(createTransactionPanel(), "TransactionManagement");

        add(mainPanel);
        setVisible(true);
    }
    
    // --- Utility Methods ---
    
    // --- FIX: Update the saveData method to throw the checked exception ---
    // This allows the calling method to handle the error (e.g., roll back a change)
    private void saveData() throws FileOperationException {
        try {
            FileHandler.saveToFile(books, bookFile);
            FileHandler.saveToFile(members, memberFile);
            FileHandler.saveToFile(transactions, transactionFile);
        } catch (FileOperationException e) { 
            // Re-throw the exception so the UI method can catch it and show a message
            throw e;
        }
    }
    // --- END FIX ---

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    // Helper to create a styled button
    private JButton createStyledButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }
    
    // Helper to create a common header panel
    private JPanel createHeaderPanel(String title) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton dashButton = createStyledButton("Dashboard", ACCENT_COLOR);
        dashButton.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));
        
        JButton bookButton = createStyledButton("Books", SECONDARY_COLOR);
        bookButton.addActionListener(e -> {
            updateBookTable(); // Refresh book data when switching
            cardLayout.show(mainPanel, "BookManagement");
        });

        JButton memberButton = createStyledButton("Members", SECONDARY_COLOR);
        memberButton.addActionListener(e -> {
            updateMemberTable(); // Refresh member data when switching
            cardLayout.show(mainPanel, "MemberManagement");
        });

        JButton transButton = createStyledButton("Transactions", SECONDARY_COLOR);
        transButton.addActionListener(e -> {
            updateTransactionTable(); // Refresh transaction data when switching
            cardLayout.show(mainPanel, "TransactionManagement");
        });
        
        buttonPanel.add(dashButton);
        buttonPanel.add(bookButton);
        buttonPanel.add(memberButton);
        buttonPanel.add(transButton);
        
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        return headerPanel;
    }

    // --- Dashboard Panel ---
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createHeaderPanel("Library Dashboard"), BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        // Stat 1: Total Books
        contentPanel.add(createStatCard("Total Books", String.valueOf(books.size()), PRIMARY_COLOR));
        // Stat 2: Available Books
        long availableBooks = books.stream().filter(b -> !b.isIssued()).count();
        contentPanel.add(createStatCard("Available Books", String.valueOf(availableBooks), ACCENT_COLOR));
        // Stat 3: Total Members
        contentPanel.add(createStatCard("Total Members", String.valueOf(members.size()), SECONDARY_COLOR));
        // Stat 4: Active Transactions
        long activeTrans = transactions.stream().filter(t -> !t.isReturned()).count();
        contentPanel.add(createStatCard("Books Issued", String.valueOf(activeTrans), DANGER_COLOR));
        
        // --- FIX: Add a refresh button to dashboard ---
        JButton refreshButton = createStyledButton("Refresh Stats", PRIMARY_COLOR);
        refreshButton.addActionListener(e -> {
            // Re-create the dashboard panel to get new stats and show it
            mainPanel.remove(0); // Remove old dashboard
            mainPanel.add(createDashboardPanel(), "Dashboard", 0); // Add new one at the same index
            cardLayout.show(mainPanel, "Dashboard");
        });
        
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(BACKGROUND_COLOR);
        footer.add(refreshButton);
        panel.add(footer, BorderLayout.SOUTH);
        // --- END FIX ---

        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 1), 
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);
        card.add(titleLabel, BorderLayout.NORTH);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }

    // --- Book Management Panel ---
    private JPanel createBookPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createHeaderPanel("Book Management"), BorderLayout.NORTH);
        
        // Table setup
        String[] columnNames = {"ID", "Title", "Author", "Status"};
        // --- FIX: Use the class field ---
        bookTableModel = new DefaultTableModel(columnNames, 0);
        JTable bookTable = new JTable(bookTableModel);
        // --- END FIX ---
        JScrollPane scrollPane = new JScrollPane(bookTable);
        
        // Initial table load
        updateBookTable();
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        controlsPanel.setBackground(BACKGROUND_COLOR.darker());
        
        JButton addButton = createStyledButton("Add New Book", ACCENT_COLOR);
        // --- FIX: Call method without parameter ---
        addButton.addActionListener(e -> showAddBookDialog());
        
        JButton deleteButton = createStyledButton("Remove Book", DANGER_COLOR);
        deleteButton.addActionListener(e -> deleteBook(bookTable));
        
        JButton searchButton = createStyledButton("Search Books", SECONDARY_COLOR);
        searchButton.addActionListener(e -> searchBook());
        // --- END FIX ---
        
        controlsPanel.add(addButton);
        controlsPanel.add(deleteButton);
        controlsPanel.add(searchButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(controlsPanel, BorderLayout.SOUTH);
        return panel;
    }

    // --- FIX: Method now uses class field, no parameter ---
    private void updateBookTable() {
        bookTableModel.setRowCount(0); // Clear existing rows
        for (Book b : books) {
            bookTableModel.addRow(new Object[]{
                b.getId(), 
                b.getTitle(), 
                b.getAuthor(), 
                b.isIssued() ? "Issued" : "Available"
            });
        }
    }
    
    private void showAddBookDialog() {
    // --- END FIX ---
        JTextField idField = new JTextField(10);
        JTextField titleField = new JTextField(10);
        JTextField authorField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Book ID (e.g., B001):"));
        panel.add(idField);
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);

        int result = JOptionPane.showConfirmDialog(this, panel, 
                 "Add New Book", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            Book newBook = null;
            try {
                String id = idField.getText().trim();
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                
                if (id.isEmpty() || title.isEmpty() || author.isEmpty()) {
                    throw new LibraryException.InvalidInputException("All fields must be filled.");
                }
                
                if (books.stream().anyMatch(b -> b.getId().equals(id))) {
                    throw new LibraryException.DuplicateIdException(id, "Book");
                }
                
                newBook = new Book(id, title, author);
                books.add(newBook);
                saveData(); // Attempt to save
                
                updateBookTable();
                showMessage("Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (LibraryException | FileOperationException e) {
                // --- FIX: Rollback logic ---
                // If save fails or input is invalid, remove the book that was added
                if (newBook != null) {
                    books.remove(newBook);
                }
                showMessage("Book Add Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteBook(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String bookId = (String) bookTableModel.getValueAt(selectedRow, 0);
            
            Book bookToRemove = books.stream()
                .filter(b -> b.getId().equals(bookId))
                .findFirst().orElse(null);

            if (bookToRemove != null) {
                if (bookToRemove.isIssued()) {
                    showMessage("Cannot remove book with ID " + bookId + " as it is currently issued.", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                try {
                    books.remove(bookToRemove);
                    saveData(); // Attempt to save
                    
                    updateBookTable();
                    showMessage("Book " + bookId + " removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                } catch (FileOperationException e) {
                    // --- FIX: Rollback logic ---
                    // If save fails, add the book back to the list
                    books.add(bookToRemove);
                    showMessage("Delete Failed: Could not save changes. Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            showMessage("Please select a book to remove.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void searchBook() {
    // --- FIX: Use class field ---
        String query = JOptionPane.showInputDialog(this, "Enter ID, Title, or Author to search:", "Search Books", JOptionPane.PLAIN_MESSAGE);
        if (query != null && !query.trim().isEmpty()) {
            String lowerQuery = query.trim().toLowerCase();
            bookTableModel.setRowCount(0);
            
            int foundCount = 0;
            for (Book b : books) {
                if (b.getId().toLowerCase().contains(lowerQuery) || 
                    b.getTitle().toLowerCase().contains(lowerQuery) || 
                    b.getAuthor().toLowerCase().contains(lowerQuery)) {
                    
                    bookTableModel.addRow(new Object[]{
                        b.getId(), 
                        b.getTitle(), 
                        b.getAuthor(), 
                        b.isIssued() ? "Issued" : "Available"
                    });
                    foundCount++;
                }
            }
            if (foundCount == 0) {
                showMessage("No books found matching the query.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                updateBookTable(); // Restore full list if nothing found
            }
        } else {
            updateBookTable(); // Restore full list if search is cancelled or empty
        }
    }
    // --- END FIX ---
    
    // --- Member Management Panel ---
    private JPanel createMemberPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createHeaderPanel("Member Management"), BorderLayout.NORTH);
        
        // Table setup
        String[] columnNames = {"ID", "Name", "Books Borrowed"};
        // --- FIX: Use the class field ---
        memberTableModel = new DefaultTableModel(columnNames, 0);
        JTable memberTable = new JTable(memberTableModel);
        // --- END FIX ---
        JScrollPane scrollPane = new JScrollPane(memberTable);
        
        // Initial table load
        updateMemberTable();
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        controlsPanel.setBackground(BACKGROUND_COLOR.darker());
        
        JButton addButton = createStyledButton("Add New Member", ACCENT_COLOR);
        // --- FIX: Call method without parameter ---
        addButton.addActionListener(e -> showAddMemberDialog());
        // --- END FIX ---
        
        JButton viewBooksButton = createStyledButton("View Borrowed Books", SECONDARY_COLOR);
        viewBooksButton.addActionListener(e -> viewBorrowedBooks(memberTable));
        
        controlsPanel.add(addButton);
        controlsPanel.add(viewBooksButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(controlsPanel, BorderLayout.SOUTH);
        return panel;
    }
    
    // --- FIX: Method now uses class field, no parameter ---
    private void updateMemberTable() {
        memberTableModel.setRowCount(0); // Clear existing rows
        for (Member m : members) {
            memberTableModel.addRow(new Object[]{
                m.getId(), 
                m.getName(), 
                m.getBorrowedBooks().size()
            });
        }
    }
    
    private void showAddMemberDialog() {
    // --- END FIX ---
        JTextField idField = new JTextField(10);
        JTextField nameField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Member ID (e.g., M001):"));
        panel.add(idField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);

        int result = JOptionPane.showConfirmDialog(this, panel, 
                 "Add New Member", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            Member newMember = null;
            try {
                String id = idField.getText().trim();
                String name = nameField.getText().trim();
                
                if (id.isEmpty() || name.isEmpty()) {
                    throw new LibraryException.InvalidInputException("All fields must be filled.");
                }
                
                if (members.stream().anyMatch(m -> m.getId().equals(id))) {
                    throw new LibraryException.DuplicateIdException(id, "Member");
                }
                
                newMember = new Member(id, name);
                members.add(newMember);
                saveData(); // Attempt to save
                
                updateMemberTable();
                showMessage("Member added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (LibraryException | FileOperationException e) {
                 // --- FIX: Rollback logic ---
                if (newMember != null) {
                    members.remove(newMember);
                }
                showMessage("Member Add Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void viewBorrowedBooks(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            // --- FIX: Use class field ---
            String memberId = (String) memberTableModel.getValueAt(selectedRow, 0);
            // --- END FIX ---
            
            Member member = members.stream()
                .filter(m -> m.getId().equals(memberId))
                .findFirst().orElse(null);
            
            if (member != null) {
                // FIXED: Now uses the imported Collectors to join the list of book titles
                String borrowedList = member.getBorrowedBooks().stream()
                    .map(bookId -> {
                        Book b = books.stream().filter(book -> book.getId().equals(bookId)).findFirst().orElse(null);
                        return b != null ? b.getTitle() + " (ID: " + b.getId() + ")" : "Unknown Book (ID: " + bookId + ")";
                    })
                    .collect(Collectors.joining("\n"));
                
                if (borrowedList.isEmpty()) {
                    borrowedList = "This member has no books currently issued.";
                }
                
                showMessage("Books Borrowed by " + member.getName() + " (ID: " + memberId + "):\n\n" + borrowedList, 
                            "Borrowed Books", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            showMessage("Please select a member to view their borrowed books.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    // --- Transaction Management Panel ---
    private JPanel createTransactionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createHeaderPanel("Transaction Management"), BorderLayout.NORTH);
        
        // Table setup
        String[] columnNames = {"ID", "Member ID", "Book ID", "Issue Date", "Return Date", "Status"};
        // --- FIX: Use the class field ---
        transactionTableModel = new DefaultTableModel(columnNames, 0);
        JTable transactionTable = new JTable(transactionTableModel);
        // --- END FIX ---
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        
        // Initial table load
        updateTransactionTable();
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        controlsPanel.setBackground(BACKGROUND_COLOR.darker());
        
        JButton issueButton = createStyledButton("Issue Book", ACCENT_COLOR);
        // --- FIX: Call method without parameter ---
        issueButton.addActionListener(e -> issueBook());
        
        JButton returnButton = createStyledButton("Return Book", DANGER_COLOR);
        returnButton.addActionListener(e -> returnBook(transactionTable));
        
        JButton filterButton = createStyledButton("Filter", SECONDARY_COLOR);
        filterButton.addActionListener(e -> filterTransactions());
        // --- END FIX ---
        
        controlsPanel.add(issueButton);
        controlsPanel.add(returnButton);
        controlsPanel.add(filterButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(controlsPanel, BorderLayout.SOUTH);
        return panel;
    }

    // --- FIX: Method now uses class field, no parameter ---
    private void updateTransactionTable() {
        transactionTableModel.setRowCount(0);
        // Display newest transactions first
        for (int i = transactions.size() - 1; i >= 0; i--) {
            Transaction t = transactions.get(i);
            transactionTableModel.addRow(new Object[]{
                t.getTransactionId(),
                t.getMemberId(),
                t.getBookId(),
                t.getIssueDate(),
                t.getReturnDate() != null ? t.getReturnDate() : "N/A",
                t.isReturned() ? "Returned" : "Active"
            });
        }
    }

    private void issueBook() {
    // --- END FIX ---
        JTextField memberIdField = new JTextField(10);
        JTextField bookIdField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Member ID:"));
        panel.add(memberIdField);
        panel.add(new JLabel("Book ID:"));
        panel.add(bookIdField);

        int result = JOptionPane.showConfirmDialog(this, panel, 
                 "Issue Book", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Transaction newTrans = null;
            Book book = null;
            Member member = null;
            String bookId = ""; // Store bookId for rollback
            
            try {
                String memberId = memberIdField.getText().trim();
                bookId = bookIdField.getText().trim();
                
                if (memberId.isEmpty() || bookId.isEmpty()) {
                    throw new LibraryException.InvalidInputException("Member ID and Book ID must be filled.");
                }
                
                member = members.stream()
                    .filter(m -> m.getId().equals(memberId))
                    .findFirst().orElseThrow(() -> new LibraryException.MemberNotFoundException(memberId));

                book = books.stream()
                    .filter(b -> b.getId().equals(bookId))
                    .findFirst().orElseThrow(() -> new LibraryException.BookNotFoundException(bookId));
                
                if (book.isIssued()) {
                    throw new LibraryException.BookAlreadyIssuedException(bookId);
                }
                
                // Perform the transaction
                book.issueBook();
                member.borrowBook(bookId);
                
                String transId = "T" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
                newTrans = new Transaction(transId, memberId, bookId);
                transactions.add(newTrans);

                saveData(); // Attempt to save all changes
                
                // --- FIX: Call no-arg methods ---
                updateBookTable();
                updateMemberTable();
                updateTransactionTable();
                // --- END FIX ---
                showMessage("Book Issued Successfully! Transaction ID: " + newTrans.getTransactionId(), "Success", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (LibraryException | FileOperationException e) {
                // --- FIX: Rollback logic ---
                if (newTrans != null) {
                    transactions.remove(newTrans);
                }
                if (book != null && book.isIssued()) { // Check if state was changed
                    book.returnBook(); // Revert book status
                }
                if (member != null && member.getBorrowedBooks().contains(bookId)) {
                    member.returnBook(bookId); // Revert member's borrowed list
                }
                showMessage("Issue Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void returnBook(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showMessage("Please select an active transaction to mark as returned.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // --- FIX: Use class field ---
        String transId = (String) transactionTableModel.getValueAt(selectedRow, 0);
        // --- END FIX ---
        
        Transaction transaction = null;
        Book book = null;
        Member member = null;
        
        try {
            transaction = transactions.stream()
                .filter(t -> t.getTransactionId().equals(transId))
                .findFirst().orElse(null);
            
            if (transaction == null || transaction.isReturned()) {
                 showMessage("Selected transaction is either not found or already returned.", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            
            String bookId = transaction.getBookId();
            String memberId = transaction.getMemberId();
            
            // Update models
            book = books.stream()
                .filter(b -> b.getId().equals(bookId))
                .findFirst().orElseThrow(() -> new LibraryException.BookNotFoundException(bookId));
            
            member = members.stream()
                .filter(m -> m.getId().equals(memberId))
                .findFirst().orElseThrow(() -> new LibraryException.MemberNotFoundException(memberId));

            // Perform return
            book.returnBook();
            member.returnBook(bookId);
            transaction.markAsReturned();

            saveData(); // Attempt to save all changes
            
            // --- FIX: Call no-arg methods ---
            updateBookTable();
            updateMemberTable();
            updateTransactionTable();
            // --- END FIX ---
            showMessage("Book returned successfully! Transaction updated.", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (LibraryException | FileOperationException e) {
            // --- FIX: Rollback logic ---
            if (transaction != null && transaction.isReturned()) {
                transaction.undoReturn(); // Requires a new method in Transaction.java
            }
            if (book != null && !book.isIssued()) {
                book.issueBook();
            }
            if (member != null && transaction != null && !member.getBorrowedBooks().contains(transaction.getBookId())) {
                member.borrowBook(transaction.getBookId());
            }
            showMessage("Return Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterTransactions() {
    // --- FIX: Use class field ---
        String[] options = {"Show All", "Show Active", "Show Returned"};
        int choice = JOptionPane.showOptionDialog(this, 
            "Select filter:", "Filter Transactions",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, 
            null, options, options[0]);
        
        transactionTableModel.setRowCount(0);
        // Display newest first
        for (int i = transactions.size() - 1; i >= 0; i--) {
        // --- END FIX ---
            Transaction t = transactions.get(i);
            boolean include = false;
            switch (choice) {
                case -1: // User closed dialog
                case 0: // All
                    include = true;
                    break;
                case 1: // Active
                    include = !t.isReturned();
                    break;
                case 2: // Returned
                    include = t.isReturned();
                    break;
            }
            
            if (include) {
                transactionTableModel.addRow(new Object[]{
                    t.getTransactionId(),
                    t.getMemberId(),
                    t.getBookId(),
                    t.getIssueDate(),
                    t.getReturnDate() != null ? t.getReturnDate() : "N/A",
                    t.isReturned() ? "Returned" : "Active"
                });
            }
        }
    }
}

