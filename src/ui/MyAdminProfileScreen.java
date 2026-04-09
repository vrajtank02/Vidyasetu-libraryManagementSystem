package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import dao.AdminDAO;
import model.Admin;

public class MyAdminProfileScreen extends JDialog {

    private JTextField txtUsername, txtFirstName, txtLastName, txtEmail, txtMobile;
    private JComboBox<String> cbGender;
    private JTextArea txtAddress;
    private JButton btnUpdate, btnCancel;

    private Admin currentAdmin;

    public MyAdminProfileScreen(JFrame parentFrame, Admin admin) {
        super(parentFrame, "My Admin Profile | Vidyasetu LMS", true);
        this.currentAdmin = admin;

        setSize(550, 600);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(e -> dispose(), escapeKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(25, 118, 210));
        headerPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
        JLabel lblHeader = new JLabel("My Admin Profile Details");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFocusable(true);
        headerPanel.add(lblHeader);
        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 20));
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        formPanel.setBackground(Color.WHITE);

        Font labelFont = new Font("SansSerif", Font.BOLD, 14);
        Font inputFont = new Font("SansSerif", Font.PLAIN, 14);

        txtUsername = new JTextField(currentAdmin.getUsername());
        txtUsername.setFont(inputFont);
        txtUsername.setEditable(false);
        txtUsername.setBackground(new Color(240, 240, 240));
        JLabel lblUsername = new JLabel("Username (Unchangeable):");
        lblUsername.setFont(labelFont);
        lblUsername.setLabelFor(txtUsername);

        txtFirstName = new JTextField(currentAdmin.getFirstName());
        txtFirstName.setFont(inputFont);
        JLabel lblFirstName = new JLabel("First Name:");
        lblFirstName.setFont(labelFont);
        lblFirstName.setLabelFor(txtFirstName);

        txtLastName = new JTextField(currentAdmin.getLastName());
        txtLastName.setFont(inputFont);
        JLabel lblLastName = new JLabel("Last Name:");
        lblLastName.setFont(labelFont);
        lblLastName.setLabelFor(txtLastName);

        txtEmail = new JTextField(currentAdmin.getEmail());
        txtEmail.setFont(inputFont);
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(labelFont);
        lblEmail.setLabelFor(txtEmail);

        txtMobile = new JTextField(currentAdmin.getMobileNo());
        txtMobile.setFont(inputFont);
        JLabel lblMobile = new JLabel("Mobile No:");
        lblMobile.setFont(labelFont);
        lblMobile.setLabelFor(txtMobile);

        String[] genders = { "Male", "Female", "Other" };
        cbGender = new JComboBox<>(genders);
        cbGender.setFont(inputFont);
        if (currentAdmin.getGender() != null) {
            cbGender.setSelectedItem(currentAdmin.getGender());
        }
        JLabel lblGender = new JLabel("Gender:");
        lblGender.setFont(labelFont);
        lblGender.setLabelFor(cbGender);

        txtAddress = new JTextArea(currentAdmin.getAddress(), 3, 20);
        txtAddress.setFont(inputFont);
        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);
        JScrollPane addressScroll = new JScrollPane(txtAddress);
        JLabel lblAddress = new JLabel("Address:");
        lblAddress.setFont(labelFont);
        lblAddress.setLabelFor(txtAddress);

        formPanel.add(lblUsername);
        formPanel.add(txtUsername);
        formPanel.add(lblFirstName);
        formPanel.add(txtFirstName);
        formPanel.add(lblLastName);
        formPanel.add(txtLastName);
        formPanel.add(lblEmail);
        formPanel.add(txtEmail);
        formPanel.add(lblMobile);
        formPanel.add(txtMobile);
        formPanel.add(lblGender);
        formPanel.add(cbGender);
        formPanel.add(lblAddress);
        formPanel.add(addressScroll);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(new Color(245, 245, 245));

        btnCancel = new JButton("Cancel");
        btnCancel.setFont(labelFont);
        btnCancel.setBackground(new Color(211, 47, 47));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.addActionListener(e -> dispose());

        btnUpdate = new JButton("Save Profile");
        btnUpdate.setFont(labelFont);
        btnUpdate.setBackground(new Color(76, 175, 80));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(e -> updateProfile());

        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateProfile() {
        String fName = txtFirstName.getText().trim();
        String lName = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String mobile = txtMobile.getText().trim();
        String gender = cbGender.getSelectedItem().toString();
        String address = txtAddress.getText().trim();

        if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || mobile.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentAdmin.setFirstName(fName);
        currentAdmin.setLastName(lName);
        currentAdmin.setEmail(email);
        currentAdmin.setMobileNo(mobile);
        currentAdmin.setGender(gender);
        currentAdmin.setAddress(address);

        AdminDAO dao = new AdminDAO();
        if (dao.updateAdmin(currentAdmin)) {
            JOptionPane.showMessageDialog(this,
                    "Profile updated successfully! Some changes will reflect on next login.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Database error. Could not update profile.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}