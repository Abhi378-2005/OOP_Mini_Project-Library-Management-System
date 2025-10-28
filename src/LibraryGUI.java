package src;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors; // <-- FIXED: Added this missing import
import src.LibraryException; // Changed to simple import to avoid nested class issues
import src.LibraryException.FileOperationException; // Explicitly import the nested class

public class LibraryGUI extends JFrame {
    private ArrayList<Book> books;
    private ArrayList<Member> members;
    private ArrayList<Transaction> transactions;
    private final String bookFile = "data/books.txt";
    private final String memberFile = "data/members.txt";
    private final String transactionFile = "data/transactions.txt";
    
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
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
            members = FileHandler.readFromFile(memberFile);
            transactions = FileHandler.readFromFile(transactionFile);
        } catch (FileOperationException e) { 
            // Display a warning and initialize lists to prevent NullPointerExceptions
            showMessage("Warning: Could not load data from file. Starting with empty data. Error: " + e.getMessage(), 
                        "Data Load Error", 
                        JOptionPane.WARNING_MESSAGE);
            books = new ArrayList<>();
            members = new ArrayList<>();
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
    
    // --- FIX: Update the saveData method to handle the checked exception ---
    private void saveData() {
        try {
            FileHandler.saveToFile(books, bookFile);
            FileHandler.saveToFile(members, memberFile);
            FileHandler.saveToFile(transactions, transactionFile);
        } catch (FileOperationException e) { 
            showMessage("Critical Error: Failed to save data to file. Changes may be lost. Error: " + e.getMessage(), 
                        "Save Error", 
                        JOptionPane.ERROR_MESSAGE);
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
        button.setForeground(Color.BLACK);
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
        bookButton.addActionListener(e -> cardLayout.show(mainPanel, "BookManagement"));

        JButton memberButton = createStyledButton("Members", SECONDARY_COLOR);
        memberButton.addActionListener(e -> cardLayout.show(mainPanel, "MemberManagement"));

        JButton transButton = createStyledButton("Transactions", SECONDARY_COLOR);
        transButton.addActionListener(e -> cardLayout.show(mainPanel, "TransactionManagement"));
        
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
        
        panel.add(contentPanel, BorderLayout.CENTER);
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
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable bookTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        
        // Initial table load
        updateBookTable(model);
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        controlsPanel.setBackground(BACKGROUND_COLOR.darker());
        
        JButton addButton = createStyledButton("Add New Book", ACCENT_COLOR);
        addButton.addActionListener(e -> showAddBookDialog(model));
        
        JButton deleteButton = createStyledButton("Remove Book", DANGER_COLOR);
        deleteButton.addActionListener(e -> deleteBook(bookTable, model));
        
        JButton searchButton = createStyledButton("Search Books", SECONDARY_COLOR);
        searchButton.addActionListener(e -> searchBook(model));
        
        controlsPanel.add(addButton);
        controlsPanel.add(deleteButton);
        controlsPanel.add(searchButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(controlsPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void updateBookTable(DefaultTableModel model) {
        model.setRowCount(0); // Clear existing rows
        for (Book b : books) {
            model.addRow(new Object[]{
                b.getId(), 
                b.getTitle(), 
                b.getAuthor(), 
                b.isIssued() ? "Issued" : "Available"
            });
        }
    }
    
    private void showAddBookDialog(DefaultTableModel model) {
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
                
                Book newBook = new Book(id, title, author);
                books.add(newBook);
                saveData(); // Calls the method that now includes try-catch
                updateBookTable(model);
                showMessage("Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (LibraryException e) {
                showMessage("Book Add Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteBook(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String bookId = (String) model.getValueAt(selectedRow, 0);
            
            Book bookToRemove = books.stream()
                .filter(b -> b.getId().equals(bookId))
                .findFirst().orElse(null);

            if (bookToRemove != null) {
                if (bookToRemove.isIssued()) {
                    showMessage("Cannot remove book with ID " + bookId + " as it is currently issued.", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                books.remove(bookToRemove);
                saveData(); // Calls the method that now includes try-catch
                updateBookTable(model);
                showMessage("Book " + bookId + " removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            showMessage("Please select a book to remove.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void searchBook(DefaultTableModel model) {
        String query = JOptionPane.showInputDialog(this, "Enter ID, Title, or Author to search:", "Search Books", JOptionPane.PLAIN_MESSAGE);
        if (query != null && !query.trim().isEmpty()) {
            String lowerQuery = query.trim().toLowerCase();
            model.setRowCount(0);
            
            for (Book b : books) {
                if (b.getId().toLowerCase().contains(lowerQuery) || 
                    b.getTitle().toLowerCase().contains(lowerQuery) || 
                    b.getAuthor().toLowerCase().contains(lowerQuery)) {
                    
                    model.addRow(new Object[]{
                        b.getId(), 
                        b.getTitle(), 
                        b.getAuthor(), 
                        b.isIssued() ? "Issued" : "Available"
                    });
                }
            }
            if (model.getRowCount() == 0) {
                showMessage("No books found matching the query.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                updateBookTable(model); // Restore full list if nothing found
            }
        } else {
            updateBookTable(model); // Restore full list if search is cancelled or empty
        }
    }
    
    // --- Member Management Panel ---
    private JPanel createMemberPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createHeaderPanel("Member Management"), BorderLayout.NORTH);
        
        // Table setup
        String[] columnNames = {"ID", "Name", "Books Borrowed"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable memberTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(memberTable);
        
        // Initial table load
        updateMemberTable(model);
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        controlsPanel.setBackground(BACKGROUND_COLOR.darker());
        
        JButton addButton = createStyledButton("Add New Member", ACCENT_COLOR);
        addButton.addActionListener(e -> showAddMemberDialog(model));
        
        JButton viewBooksButton = createStyledButton("View Borrowed Books", SECONDARY_COLOR);
        viewBooksButton.addActionListener(e -> viewBorrowedBooks(memberTable));
        
        controlsPanel.add(addButton);
        controlsPanel.add(viewBooksButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(controlsPanel, BorderLayout.SOUTH);
        return panel;
    }
    
    private void updateMemberTable(DefaultTableModel model) {
        model.setRowCount(0); // Clear existing rows
        for (Member m : members) {
            model.addRow(new Object[]{
                m.getId(), 
                m.getName(), 
                m.getBorrowedBooks().size()
            });
        }
    }
    
    private void showAddMemberDialog(DefaultTableModel model) {
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
            try {
                String id = idField.getText().trim();
                String name = nameField.getText().trim();
                
                if (id.isEmpty() || name.isEmpty()) {
                    throw new LibraryException.InvalidInputException("All fields must be filled.");
                }
                
                if (members.stream().anyMatch(m -> m.getId().equals(id))) {
                    throw new LibraryException.DuplicateIdException(id, "Member");
                }
                
                Member newMember = new Member(id, name);
                members.add(newMember);
                saveData(); // Calls the method that now includes try-catch
                updateMemberTable(model);
                showMessage("Member added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (LibraryException e) {
                showMessage("Member Add Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void viewBorrowedBooks(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String memberId = (String) table.getValueAt(selectedRow, 0);
            
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
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable transactionTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        
        // Initial table load
        updateTransactionTable(model);
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        controlsPanel.setBackground(BACKGROUND_COLOR.darker());
        
        JButton issueButton = createStyledButton("Issue Book", ACCENT_COLOR);
        issueButton.addActionListener(e -> issueBook(model));
        
        JButton returnButton = createStyledButton("Return Book", DANGER_COLOR);
        returnButton.addActionListener(e -> returnBook(transactionTable, model));
        
        JButton filterButton = createStyledButton("Filter", SECONDARY_COLOR);
        filterButton.addActionListener(e -> filterTransactions(model));
        
        controlsPanel.add(issueButton);
        controlsPanel.add(returnButton);
        controlsPanel.add(filterButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(controlsPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void updateTransactionTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Transaction t : transactions) {
            model.addRow(new Object[]{
                t.getTransactionId(),
                t.getMemberId(),
                t.getBookId(),
                t.getIssueDate(),
                t.getReturnDate() != null ? t.getReturnDate() : "N/A",
                t.isReturned() ? "Returned" : "Active"
            });
        }
    }

    private void issueBook(DefaultTableModel model) {
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
            try {
                String memberId = memberIdField.getText().trim();
                String bookId = bookIdField.getText().trim();
                
                if (memberId.isEmpty() || bookId.isEmpty()) {
                    throw new LibraryException.InvalidInputException("Member ID and Book ID");
                }
                
                Member member = members.stream()
                    .filter(m -> m.getId().equals(memberId))
                    .findFirst().orElseThrow(() -> new LibraryException.MemberNotFoundException(memberId));

                Book book = books.stream()
                    .filter(b -> b.getId().equals(bookId))
                    .findFirst().orElseThrow(() -> new LibraryException.BookNotFoundException(bookId));
                
                if (book.isIssued()) {
                    throw new LibraryException.BookAlreadyIssuedException(bookId);
                }
                
                // Perform the transaction
                book.issueBook();
                member.borrowBook(bookId);
                
                String transId = "T" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
                Transaction newTrans = new Transaction(transId, memberId, bookId);
                transactions.add(newTrans);

                saveData(); // Calls the method that now includes try-catch
                // Update other tables manually since they are in different CardLayout panels
                updateBookTable((DefaultTableModel)((JTable)((JScrollPane)mainPanel.getComponent(1)).getViewport().getView()).getModel());
                updateMemberTable((DefaultTableModel)((JTable)((JScrollPane)mainPanel.getComponent(2)).getViewport().getView()).getModel());
                updateTransactionTable(model);
                showMessage("Book Issued Successfully! Transaction ID: " + transId, "Success", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (LibraryException e) {
                showMessage("Issue Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void returnBook(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showMessage("Please select an active transaction to mark as returned.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String transId = (String) model.getValueAt(selectedRow, 0);
        String bookId = (String) model.getValueAt(selectedRow, 2);
        String memberId = (String) model.getValueAt(selectedRow, 1);

        try {
            Transaction transaction = transactions.stream()
                .filter(t -> t.getTransactionId().equals(transId))
                .findFirst().orElse(null);
            
            if (transaction == null || transaction.isReturned()) {
                 showMessage("Selected transaction is either not found or already returned.", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            
            // Update models
            Book book = books.stream()
                .filter(b -> b.getId().equals(bookId))
                .findFirst().orElseThrow(() -> new LibraryException.BookNotFoundException(bookId)); // Should not happen if data is consistent
            
            Member member = members.stream()
                .filter(m -> m.getId().equals(memberId))
                .findFirst().orElseThrow(() -> new LibraryException.MemberNotFoundException(memberId)); // Should not happen

            // Perform return
            book.returnBook();
            member.returnBook(bookId);
            transaction.markAsReturned();

            saveData(); // Calls the method that now includes try-catch
            // Update other tables manually
            updateBookTable((DefaultTableModel)((JTable)((JScrollPane)mainPanel.getComponent(1)).getViewport().getView()).getModel());
            updateMemberTable((DefaultTableModel)((JTable)((JScrollPane)mainPanel.getComponent(2)).getViewport().getView()).getModel());
            updateTransactionTable(model);
            showMessage("Book returned successfully! Transaction updated.", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (LibraryException e) {
            showMessage("Return Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterTransactions(DefaultTableModel model) {
        String[] options = {"Show All", "Show Active", "Show Returned"};
        int choice = JOptionPane.showOptionDialog(this, 
            "Select filter:", "Filter Transactions",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, 
            null, options, options[0]);
        
        model.setRowCount(0);
        for (Transaction t : transactions) {
            boolean include = false;
            switch (choice) {
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
                model.addRow(new Object[]{
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
