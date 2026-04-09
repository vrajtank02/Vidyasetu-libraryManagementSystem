package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import dao.AdminDAO;
import model.Admin;

public class AdminRegistrationScreen extends JDialog {

    private JTextField txtFirstName, txtLastName, txtUsername, txtEmail, txtMobile;
    private JPasswordField txtHiddenPass, txtHiddenConfirmPass;
    private JTextField txtVisiblePass, txtVisibleConfirmPass;
    private JPanel passPanel, confirmPassPanel;
    private CardLayout clPass, clConfirmPass;
    private JCheckBox chkShowPassword;
    private JComboBox<String> cbGender;
    private JTextArea txtAddress;
    private JButton btnSubmit, btnCancel;

    public AdminRegistrationScreen() {
        setTitle("Admin Registration | Vidyasetu");
        setSize(550, 700);
        setModal(true);
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(e -> dispose(), escapeKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(11, 2, 10, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        Font standardFont = new Font("SansSerif", Font.PLAIN, 14);
        Font boldFont = new Font("SansSerif", Font.BOLD, 14);

        txtFirstName = new JTextField();
        txtFirstName.setFont(standardFont);
        JLabel lblFirstName = new JLabel("First Name:");
        lblFirstName.setFont(boldFont);
        lblFirstName.setLabelFor(txtFirstName);

        txtLastName = new JTextField();
        txtLastName.setFont(standardFont);
        JLabel lblLastName = new JLabel("Last Name:");
        lblLastName.setFont(boldFont);
        lblLastName.setLabelFor(txtLastName);

        txtUsername = new JTextField();
        txtUsername.setFont(standardFont);
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(boldFont);
        lblUsername.setLabelFor(txtUsername);

        txtHiddenPass = new JPasswordField();
        txtHiddenPass.setFont(standardFont);
        txtHiddenPass.getAccessibleContext().setAccessibleDescription("Enter your password");

        txtVisiblePass = new JTextField();
        txtVisiblePass.setFont(standardFont);
        txtVisiblePass.getAccessibleContext().setAccessibleDescription("Enter your password");

        clPass = new CardLayout();
        passPanel = new JPanel(clPass);
        passPanel.add(txtHiddenPass, "hidden");
        passPanel.add(txtVisiblePass, "visible");

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(boldFont);
        lblPassword.setLabelFor(txtHiddenPass);

        txtHiddenConfirmPass = new JPasswordField();
        txtHiddenConfirmPass.setFont(standardFont);
        txtHiddenConfirmPass.getAccessibleContext().setAccessibleDescription("Confirm your password");

        txtVisibleConfirmPass = new JTextField();
        txtVisibleConfirmPass.setFont(standardFont);
        txtVisibleConfirmPass.getAccessibleContext().setAccessibleDescription("Confirm your password");

        clConfirmPass = new CardLayout();
        confirmPassPanel = new JPanel(clConfirmPass);
        confirmPassPanel.add(txtHiddenConfirmPass, "hidden");
        confirmPassPanel.add(txtVisibleConfirmPass, "visible");

        JLabel lblConfirmPassword = new JLabel("Confirm Password:");
        lblConfirmPassword.setFont(boldFont);
        lblConfirmPassword.setLabelFor(txtHiddenConfirmPass);

        chkShowPassword = new JCheckBox("Show Password");
        chkShowPassword.setFont(standardFont);
        chkShowPassword.getAccessibleContext().setAccessibleDescription("Toggle to show or hide passwords");

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

        txtEmail = new JTextField("Yourname@domain.com");
        txtEmail.setFont(standardFont);
        txtEmail.setForeground(Color.GRAY);

        txtEmail.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (txtEmail.getText().equals("Yourname@domain.com") &&
                        e.getKeyCode() != KeyEvent.VK_TAB &&
                        e.getKeyCode() != KeyEvent.VK_SHIFT) {
                    txtEmail.setText("");
                    txtEmail.setForeground(Color.BLACK);
                }
            }
        });

        txtEmail.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (txtEmail.getText().trim().isEmpty()) {
                    txtEmail.setForeground(Color.GRAY);
                    txtEmail.setText("Yourname@domain.com");
                }
            }
        });

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(boldFont);
        lblEmail.setLabelFor(txtEmail);

        txtMobile = new JTextField();
        txtMobile.setFont(standardFont);
        txtMobile.getAccessibleContext().setAccessibleDescription("Enter your 10 digit mobile number");
        JLabel lblMobile = new JLabel("Mobile No:");
        lblMobile.setFont(boldFont);
        lblMobile.setLabelFor(txtMobile);

        String[] genders = { "Select Gender", "Male", "Female", "Other" };
        cbGender = new JComboBox<>(genders);
        cbGender.setFont(standardFont);
        cbGender.getAccessibleContext().setAccessibleDescription("Select your gender from the dropdown");
        JLabel lblGender = new JLabel("Gender:");
        lblGender.setFont(boldFont);
        lblGender.setLabelFor(cbGender);

        txtAddress = new JTextArea(3, 20);
        txtAddress.setFont(standardFont);
        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);
        txtAddress.getAccessibleContext().setAccessibleDescription("Enter your full residential address");

        txtAddress.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    if (e.isShiftDown()) {
                        txtAddress.transferFocusBackward();
                    } else {
                        txtAddress.transferFocus();
                    }
                    e.consume();
                }
            }
        });

        JScrollPane addressScroll = new JScrollPane(txtAddress);
        JLabel lblAddress = new JLabel("Address:");
        lblAddress.setFont(boldFont);
        lblAddress.setLabelFor(txtAddress);

        btnCancel = new JButton("Cancel");
        btnCancel.setFont(boldFont);
        btnCancel.setBackground(new Color(231, 76, 60)); // Standard red for cancel
        btnCancel.setForeground(Color.WHITE);
        btnCancel.addActionListener(e -> dispose()); // Closes the window

        btnSubmit = new JButton("Submit");
        btnSubmit.setFont(boldFont);
        btnSubmit.setBackground(new Color(41, 128, 185)); // Standard blue for submit
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.addActionListener(e -> performValidation());

        mainPanel.add(lblFirstName);
        mainPanel.add(txtFirstName);
        mainPanel.add(lblLastName);
        mainPanel.add(txtLastName);
        mainPanel.add(lblUsername);
        mainPanel.add(txtUsername);
        mainPanel.add(lblPassword);
        mainPanel.add(passPanel);
        mainPanel.add(lblConfirmPassword);
        mainPanel.add(confirmPassPanel);
        mainPanel.add(new JLabel(""));
        mainPanel.add(chkShowPassword);
        mainPanel.add(lblEmail);
        mainPanel.add(txtEmail);
        mainPanel.add(lblMobile);
        mainPanel.add(txtMobile);
        mainPanel.add(lblGender);
        mainPanel.add(cbGender);
        mainPanel.add(lblAddress);
        mainPanel.add(addressScroll);
        mainPanel.add(btnCancel);
        mainPanel.add(btnSubmit);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(mainPanel, BorderLayout.NORTH);
        add(wrapperPanel);
    }

    private void performValidation() {
        String fName = txtFirstName.getText().trim();
        String lName = txtLastName.getText().trim();
        String user = txtUsername.getText().trim();

        String pass = chkShowPassword.isSelected() ? txtVisiblePass.getText() : new String(txtHiddenPass.getPassword());
        String confirmPass = chkShowPassword.isSelected() ? txtVisibleConfirmPass.getText()
                : new String(txtHiddenConfirmPass.getPassword());

        String email = txtEmail.getText().trim();
        String mobile = txtMobile.getText().trim();
        String gender = cbGender.getSelectedItem().toString();
        String address = txtAddress.getText().trim();

        StringBuilder errors = new StringBuilder();

        if (fName.isEmpty())
            errors.append("- First Name is required.\n");
        else if (!fName.matches("^[a-zA-Z]+$"))
            errors.append("- First Name must contain only letters.\n");

        if (lName.isEmpty())
            errors.append("- Last Name is required.\n");
        else if (!lName.matches("^[a-zA-Z]+$"))
            errors.append("- Last Name must contain only letters.\n");

        if (user.isEmpty()) {
            errors.append("- Username is required.\n");
        } else if (user.contains(" ")) {
            errors.append("- Username cannot contain spaces.\n");
        }

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
        if (email.isEmpty() || email.equals("Yourname@domain.com"))
            errors.append("- Email Address is required.\n");
        else if (email.contains(" ") || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]+$"))
            errors.append("- Email Address format is invalid. It should be in yourname@domain.com.\n");

        if (mobile.isEmpty())
            errors.append("- Mobile Number is required.\n");
        else if (mobile.length() != 10 || !mobile.matches("\\d{10}"))
            errors.append("- Mobile Number must be exactly 10 digits.\n");

        if (gender.equals("Select Gender"))
            errors.append("- Please select a Gender.\n");

        if (address.isEmpty())
            errors.append("- Address is required.\n");

        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this,
                    "Please fix the following errors:\n\n" + errors.toString(),
                    "Validation Error:",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        Admin newAdmin = new Admin();
        newAdmin.setUsername(user);
        newAdmin.setPassword(pass);
        newAdmin.setFirstName(fName);
        newAdmin.setLastName(lName);
        newAdmin.setMobileNo(mobile);
        newAdmin.setEmail(email);
        newAdmin.setGender(gender);
        newAdmin.setAddress(address);
        newAdmin.setStatus("Active");
        AdminDAO adminDAO = new AdminDAO();
        boolean isRegistered = adminDAO.addAdmin(newAdmin);

        if (isRegistered) {
            JOptionPane.showMessageDialog(this, "Admin Registered Successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Database Error: Could not register admin. Username or Email might already exist.",
                    "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}