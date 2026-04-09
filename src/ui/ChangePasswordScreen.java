package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import dao.AdminDAO;

public class ChangePasswordScreen extends JDialog {

    private JPasswordField txtHiddenPass, txtHiddenConfirmPass;
    private JTextField txtVisiblePass, txtVisibleConfirmPass;
    private JPanel passPanel, confirmPassPanel;
    private CardLayout clPass, clConfirmPass;
    private JCheckBox chkShowPassword;
    private JButton btnSave, btnCancel;

    private int currentAdminId;

    public ChangePasswordScreen(JFrame parentFrame, int adminId) {
        super(parentFrame, "Change Password | Vidyasetu LMS", true);
        this.currentAdminId = adminId;

        setSize(450, 350);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(e -> dispose(), escapeKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(25, 118, 210)); // Deep Blue
        headerPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
        JLabel lblHeader = new JLabel("Change Account Password");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);
        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 25));
        formPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        formPanel.setBackground(Color.WHITE);

        Font labelFont = new Font("SansSerif", Font.BOLD, 14);
        Font inputFont = new Font("SansSerif", Font.PLAIN, 14);

        txtHiddenPass = new JPasswordField();
        txtHiddenPass.setFont(inputFont);
        txtVisiblePass = new JTextField();
        txtVisiblePass.setFont(inputFont);

        clPass = new CardLayout();
        passPanel = new JPanel(clPass);
        passPanel.add(txtHiddenPass, "hidden");
        passPanel.add(txtVisiblePass, "visible");

        JLabel lblPassword = new JLabel("New Password:");
        lblPassword.setFont(labelFont);
        lblPassword.setLabelFor(txtHiddenPass);

        txtHiddenConfirmPass = new JPasswordField();
        txtHiddenConfirmPass.setFont(inputFont);
        txtVisibleConfirmPass = new JTextField();
        txtVisibleConfirmPass.setFont(inputFont);

        clConfirmPass = new CardLayout();
        confirmPassPanel = new JPanel(clConfirmPass);
        confirmPassPanel.add(txtHiddenConfirmPass, "hidden");
        confirmPassPanel.add(txtVisibleConfirmPass, "visible");

        JLabel lblConfirmPassword = new JLabel("Confirm Password:");
        lblConfirmPassword.setFont(labelFont);
        lblConfirmPassword.setLabelFor(txtHiddenConfirmPass);

        chkShowPassword = new JCheckBox("Show Passwords");
        chkShowPassword.setFont(inputFont);
        chkShowPassword.setBackground(Color.WHITE);

        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtVisiblePass.setText(new String(txtHiddenPass.getPassword()));
                txtVisibleConfirmPass.setText(new String(txtHiddenConfirmPass.getPassword()));
                clPass.show(passPanel, "visible");
                clConfirmPass.show(confirmPassPanel, "visible");
                lblPassword.setLabelFor(txtVisiblePass);
                lblConfirmPassword.setLabelFor(txtVisibleConfirmPass);
            } else {
                txtHiddenPass.setText(txtVisiblePass.getText());
                txtHiddenConfirmPass.setText(txtVisibleConfirmPass.getText());
                clPass.show(passPanel, "hidden");
                clConfirmPass.show(confirmPassPanel, "hidden");
                lblPassword.setLabelFor(txtHiddenPass);
                lblConfirmPassword.setLabelFor(txtHiddenConfirmPass);
            }
        });

        formPanel.add(lblPassword);
        formPanel.add(passPanel);
        formPanel.add(lblConfirmPassword);
        formPanel.add(confirmPassPanel);
        formPanel.add(new JLabel("")); // Empty spacer
        formPanel.add(chkShowPassword);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(new Color(245, 245, 245));

        btnCancel = new JButton("Cancel");
        btnCancel.setFont(labelFont);
        btnCancel.setBackground(new Color(211, 47, 47)); // Red
        btnCancel.setForeground(Color.WHITE);
        btnCancel.addActionListener(e -> dispose());

        btnSave = new JButton("Update Password");
        btnSave.setFont(labelFont);
        btnSave.setBackground(new Color(76, 175, 80)); // Green
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> processPasswordChange());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void processPasswordChange() {
        String pass = chkShowPassword.isSelected() ? txtVisiblePass.getText() : new String(txtHiddenPass.getPassword());
        String confirmPass = chkShowPassword.isSelected() ? txtVisibleConfirmPass.getText()
                : new String(txtHiddenConfirmPass.getPassword());

        StringBuilder errors = new StringBuilder();

        if (pass.isEmpty()) {
            errors.append("- Password is required.\n");
        } else {
            if (pass.length() < 8)
                errors.append("- Password must be at least 8 characters long.\n");
            if (pass.contains(" "))
                errors.append("- Password cannot contain spaces.\n");
        }

        if (confirmPass.isEmpty()) {
            errors.append("- Confirm Password is required.\n");
        } else if (!pass.isEmpty() && !pass.equals(confirmPass)) {
            errors.append("- Passwords do not match.\n");
        }

        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this, "Please fix the following errors:\n\n" + errors.toString(),
                    "Validation Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        AdminDAO dao = new AdminDAO();
        if (dao.changePassword(currentAdminId, pass)) {
            JOptionPane.showMessageDialog(this, "Password updated successfully! Please use it on your next login.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Database error. Could not change password.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}