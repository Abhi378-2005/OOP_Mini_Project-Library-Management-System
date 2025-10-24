package src;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import src.LibraryException.FileOperationException; // Import the specific checked exception

public class LoginScreen extends JFrame {
    private ArrayList<Librarian> librarians;
    private final String librarianFile = "data/librarians.txt";
    
    // UI Components
    private JTextField idField;
    private JPasswordField passwordField;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color ACCENT_COLOR = new Color(46, 204, 113);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color TEXT_COLOR = new Color(44, 62, 80);

    public LoginScreen() {
        
        // --- FIX: Handle FileOperationException during data loading ---
        try {
            librarians = FileHandler.readFromFile(librarianFile);
        } catch (FileOperationException e) { 
            // Show a warning and initialize list
            JOptionPane.showMessageDialog(this, 
                "Warning: Could not load librarian data. Starting with empty list. Error: " + e.getMessage(), 
                "Data Load Error", 
                JOptionPane.WARNING_MESSAGE);
            librarians = new ArrayList<>();
        }
        // --- END FIX ---
        
        // Create default librarian if none exists
        if (librarians.isEmpty()) {
            librarians.add(new Librarian("admin", "Administrator", "admin123"));
            librarians.add(new Librarian("lib001", "John Doe", "password"));
            
            // --- FIX: Handle FileOperationException during data saving ---
            try {
                FileHandler.saveToFile(librarians, librarianFile);
            } catch (FileOperationException e) {
                JOptionPane.showMessageDialog(this, 
                    "Critical Error: Failed to save default librarian data. Error: " + e.getMessage(), 
                    "Save Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            // --- END FIX ---
        }
        
        setTitle("Library Management System - Login");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(500, 100));
        headerPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for centering
        
        JLabel titleLabel = new JLabel("Librarian Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        // Login Panel (Center)
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBackground(BACKGROUND_COLOR);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(50, 80, 50, 80));
        
        // --- ID Field ---
        JLabel idLabel = new JLabel("Librarian ID:");
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        idLabel.setForeground(TEXT_COLOR);
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        idField = new JTextField(15);
        idField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        idField.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR.brighter(), 1));
        idField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        // --- Password Field ---
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passwordLabel.setForeground(TEXT_COLOR);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField = new JPasswordField(15);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        passwordField.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR.brighter(), 1));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        // --- Login Button ---
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBackground(ACCENT_COLOR);
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Action Listener for Login
        loginButton.addActionListener(e -> {
            String id = idField.getText().trim();
            String password = new String(passwordField.getPassword());
            attemptLogin(id, password);
        });
        
        // Layout components in loginPanel
        loginPanel.add(idLabel);
        loginPanel.add(Box.createVerticalStrut(5));
        loginPanel.add(idField);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(passwordLabel);
        loginPanel.add(Box.createVerticalStrut(5));
        loginPanel.add(passwordField);
        loginPanel.add(Box.createVerticalStrut(30));
        loginPanel.add(loginButton);
        loginPanel.add(Box.createVerticalStrut(40)); // Spacer
        
        // Info Panel for Default Credentials
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(BACKGROUND_COLOR);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.lightGray));
        
        JLabel infoTitle = new JLabel("Default Credentials (Librarian):");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        infoTitle.setForeground(TEXT_COLOR);
        infoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel info1 = new JLabel("ID: admin | Password: admin123");
        info1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        info1.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel info2 = new JLabel("ID: lib001 | Password: password");
        info2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        info2.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoPanel.add(infoTitle);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(info1);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(info2);
        
        loginPanel.add(infoPanel);
        
        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(BACKGROUND_COLOR);
        footerPanel.setPreferredSize(new Dimension(500, 50));
        
        JLabel footerLabel = new JLabel("© 2025 Library Management System");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(TEXT_COLOR);
        
        footerPanel.add(footerLabel);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        setVisible(true);
    }
    
    private void attemptLogin(String id, String password) {
        boolean loggedIn = false;
        Librarian currentLibrarian = null;

        for (Librarian lib : librarians) {
            if (lib.login(id, password)) {
                loggedIn = true;
                currentLibrarian = lib;
                break;
            }
        }

        if (loggedIn) {
            // Launch main application
            JOptionPane.showMessageDialog(this, 
                "Welcome, " + currentLibrarian.getName() + "!", 
                "Login Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Launch the main GUI and close the login screen
            new LibraryGUI(); 
            this.dispose(); 
        } else {
            // Clear password field on failure
            passwordField.setText("");
            JOptionPane.showMessageDialog(this, 
                "Invalid ID or Password. Please try again.", 
                "Login Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
