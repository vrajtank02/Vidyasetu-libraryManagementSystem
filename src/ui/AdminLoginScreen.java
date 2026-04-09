package ui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import dao.AdminDAO;
import model.Admin;

public class AdminLoginScreen extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtHiddenPass;
    private JTextField txtVisiblePass;
    private JPanel passPanel;
    private CardLayout clPass;
    private JCheckBox chkShowPassword;
    private JButton btnLogin, btnRegister;

    public AdminLoginScreen() {
        setTitle("Admin Login | Vidyasetu");
        setSize(450, 300); // Smaller, more focused window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // This is the main entry point, so closing it exits the app
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 2, 10, 20)); // 4 rows, generous 20px vertical spacing
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        Font standardFont = new Font("SansSerif", Font.PLAIN, 14);
        Font boldFont = new Font("SansSerif", Font.BOLD, 14);

        txtUsername = new JTextField();
        txtUsername.setFont(standardFont);
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(boldFont);
        lblUsername.setLabelFor(txtUsername);

        txtHiddenPass = new JPasswordField();
        txtHiddenPass.setFont(standardFont);

        txtVisiblePass = new JTextField();
        txtVisiblePass.setFont(standardFont);

        clPass = new CardLayout();
        passPanel = new JPanel(clPass);
        passPanel.add(txtHiddenPass, "hidden");
        passPanel.add(txtVisiblePass, "visible");

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(boldFont);
        lblPassword.setLabelFor(txtHiddenPass);

        chkShowPassword = new JCheckBox("Show Password");
        chkShowPassword.setFont(standardFont);
        chkShowPassword.getAccessibleContext().setAccessibleDescription("Toggle to show or hide password");

        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtVisiblePass.setText(new String(txtHiddenPass.getPassword()));
                clPass.show(passPanel, "visible");
                lblPassword.setLabelFor(txtVisiblePass);
            } else {
                txtHiddenPass.setText(txtVisiblePass.getText());
                clPass.show(passPanel, "hidden");
                lblPassword.setLabelFor(txtHiddenPass);
            }
        });

        btnLogin = new JButton("Login");
        btnLogin.setFont(boldFont);
        btnLogin.setBackground(new Color(41, 128, 185)); // Standard Blue
        btnLogin.setForeground(Color.WHITE);
        btnLogin.addActionListener(e -> performLogin());

        btnRegister = new JButton("Register New Admin");
        btnRegister.setFont(boldFont);
        btnRegister.setBackground(new Color(46, 204, 113)); // Standard Green
        btnRegister.setForeground(Color.WHITE);
        btnRegister.addActionListener(e -> openRegistrationScreen());

        mainPanel.add(lblUsername);
        mainPanel.add(txtUsername);
        mainPanel.add(lblPassword);
        mainPanel.add(passPanel);
        mainPanel.add(new JLabel(""));
        mainPanel.add(chkShowPassword);
        mainPanel.add(btnRegister);
        mainPanel.add(btnLogin);

        add(mainPanel);
    }

private void performLogin() {
        String user = txtUsername.getText().trim();
        String pass = chkShowPassword.isSelected() ? txtVisiblePass.getText() : new String(txtHiddenPass.getPassword());

        StringBuilder errors = new StringBuilder();

        if (user.isEmpty()) {
            errors.append("- Username is required.\n");
        } else if (user.contains(" ")) {
            errors.append("- Username cannot contain spaces.\n");
        }

        if (pass.isEmpty()) {
            errors.append("- Password is required.\n");
        } else {
            if (pass.length() < 8) errors.append("- Password must be at least 8 characters long.\n");
            if (pass.contains(" ")) errors.append("- Password cannot contain spaces.\n");
        }

        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this, "Please fix the following errors before logging in:\n\n" + errors.toString(), "Validation Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        AdminDAO adminDAO = new AdminDAO();
        Admin loggedInAdmin = adminDAO.verifyLogin(user, pass);

        if (loggedInAdmin != null) {
            JOptionPane.showMessageDialog(this, "Login Successful! Welcome to Vidyasetu.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            MainDashboard dashboard = new MainDashboard(loggedInAdmin);
            dashboard.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openRegistrationScreen() {
        // Opens the registration screen on top of the login screen
        AdminRegistrationScreen regScreen = new AdminRegistrationScreen();
        regScreen.setVisible(true);
    }
}