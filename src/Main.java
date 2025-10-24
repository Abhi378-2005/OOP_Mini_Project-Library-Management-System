package src;

import javax.swing.UIManager; // <-- FIXED: Added missing import

public class Main {
    public static void main(String[] args) {
        // Set look and feel to system default for better appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Start with login screen
        new LoginScreen();
    }
}

