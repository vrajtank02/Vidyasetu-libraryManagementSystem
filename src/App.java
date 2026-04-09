import javax.swing.SwingUtilities;

import ui.AdminLoginScreen;

public class App {
    public static void main(String[] args) {
        // Run the GUI creation on the Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> {
            AdminLoginScreen loginScreen = new AdminLoginScreen();
            loginScreen.setVisible(true);
        });
    }
}