package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
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

import dao.MemberDAO;
import model.Member;

public class MemberRegistrationScreen extends JDialog {

    private JTextField txtEnrollment, txtFirstName, txtLastName, txtEmail, txtPhone;
    private JComboBox<String> cbGender; // New combo box
    private JTextArea txtAddress;
    private JButton btnSubmit, btnCancel;
    private MemberDAO memberDAO;

    public MemberRegistrationScreen(JFrame parentFrame) {
        super(parentFrame, "Register New Member | Vidyasetu LMS", true);
        setSize(550, 650); // Slightly taller to fit the new field
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());

        memberDAO = new MemberDAO();

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(e -> dispose(), escapeKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(25, 118, 210));
        headerPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
        JLabel lblHeader = new JLabel("Register Library Member");
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

        txtEnrollment = new JTextField();
        txtEnrollment.setEditable(false);
        txtEnrollment.setBackground(new Color(240, 240, 240));

        String generatedNo = memberDAO.generateNextEnrollmentNo();
        if (generatedNo == null) {
            JOptionPane.showMessageDialog(parentFrame,
                    "CRITICAL ERROR: Cannot connect to the database to generate an Enrollment Number. Please check your database server.",
                    "Connection Failure",
                    JOptionPane.ERROR_MESSAGE);
            dispose(); // Close the window safely
            return;
        }
        txtEnrollment.setText(generatedNo);

        txtEnrollment.getAccessibleContext().setAccessibleName(
                "Auto-generated Enrollment Number: " + txtEnrollment.getText() + ". This field is read-only.");
        JLabel lblEnrollment = new JLabel("Enrollment No:");
        lblEnrollment.setFont(labelFont);
        lblEnrollment.setLabelFor(txtEnrollment);

        txtFirstName = new JTextField();
        txtFirstName.setFont(inputFont);
        JLabel lblFirstName = new JLabel("First Name:");
        lblFirstName.setFont(labelFont);
        lblFirstName.setLabelFor(txtFirstName);

        txtLastName = new JTextField();
        txtLastName.setFont(inputFont);
        JLabel lblLastName = new JLabel("Last Name:");
        lblLastName.setFont(labelFont);
        lblLastName.setLabelFor(txtLastName);

        txtEmail = new JTextField();
        txtEmail.setFont(inputFont);
        JLabel lblEmail = new JLabel("Email Address:");
        lblEmail.setFont(labelFont);
        lblEmail.setLabelFor(txtEmail);

        txtPhone = new JTextField();
        txtPhone.setFont(inputFont);
        JLabel lblPhone = new JLabel("Phone No:");
        lblPhone.setFont(labelFont);
        lblPhone.setLabelFor(txtPhone);

        String[] genders = { "Select Gender", "Male", "Female", "Other" };
        cbGender = new JComboBox<>(genders);
        cbGender.setFont(inputFont);
        cbGender.getAccessibleContext().setAccessibleDescription("Select your gender from the dropdown");
        JLabel lblGender = new JLabel("Gender:");
        lblGender.setFont(labelFont);
        lblGender.setLabelFor(cbGender);

        txtAddress = new JTextArea(3, 20);
        txtAddress.setFont(inputFont);
        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);

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
        lblAddress.setFont(labelFont);
        lblAddress.setLabelFor(txtAddress);

        formPanel.add(lblEnrollment);
        formPanel.add(txtEnrollment);
        formPanel.add(lblFirstName);
        formPanel.add(txtFirstName);
        formPanel.add(lblLastName);
        formPanel.add(txtLastName);
        formPanel.add(lblEmail);
        formPanel.add(txtEmail);
        formPanel.add(lblPhone);
        formPanel.add(txtPhone);
        formPanel.add(lblGender); // Add Gender to form
        formPanel.add(cbGender); // Add Gender combo to form
        formPanel.add(lblAddress);
        formPanel.add(addressScroll);

        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setBackground(Color.WHITE);
        formWrapper.add(formPanel, BorderLayout.NORTH);
        add(formWrapper, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(new Color(245, 245, 245));

        btnCancel = new JButton("Cancel");
        btnCancel.setFont(labelFont);
        btnCancel.setBackground(new Color(211, 47, 47));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.addActionListener(e -> dispose());

        btnSubmit = new JButton("Register Member");
        btnSubmit.setFont(labelFont);
        btnSubmit.setBackground(new Color(76, 175, 80));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.addActionListener(e -> performValidation());

        buttonPanel.add(btnSubmit);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void performValidation() {
        String enrollment = txtEnrollment.getText().trim();
        String fName = txtFirstName.getText().trim();
        String lName = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String gender = cbGender.getSelectedItem().toString(); // Get Gender
        String address = txtAddress.getText().trim();

        StringBuilder errors = new StringBuilder();

        if (fName.isEmpty()) {
            errors.append("- First Name is required.\n");
        } else if (!fName.matches("^[a-zA-Z]+$")) {
            errors.append("- First Name must contain only letters.\n");
        }

        if (lName.isEmpty()) {
            errors.append("- Last Name is required.\n");
        } else if (!lName.matches("^[a-zA-Z]+$")) {
            errors.append("- Last Name must contain only letters.\n");
        }

        if (email.isEmpty()) {
            errors.append("- Email Address is required.\n");
        } else if (email.contains(" ") || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]+$")) {
            errors.append("- Email Address format is invalid (e.g., name@domain.com).\n");
        }

        if (phone.isEmpty()) {
            errors.append("- Phone Number is required.\n");
        } else if (phone.length() != 10 || !phone.matches("\\d{10}")) {
            errors.append("- Phone Number must be exactly 10 digits.\n");
        }

        if (gender.equals("Select Gender")) {
            errors.append("- Please select a Gender.\n"); // Validate Gender
        }

        if (address.isEmpty()) {
            errors.append("- Address is required.\n");
        }

        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this,
                    "Please fix the following errors before registering:\n\n" + errors.toString(),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Member newMember = new Member();
        newMember.setEnrollmentNo(enrollment);
        newMember.setFirstName(fName);
        newMember.setLastName(lName);
        newMember.setEmail(email);
        newMember.setPhone(phone);
        newMember.setGender(gender); // Set Gender before saving
        newMember.setAddress(address);
        newMember.setStatus("Active");

        boolean isRegistered = memberDAO.addMember(newMember);

        if (isRegistered) {
            JOptionPane.showMessageDialog(this, "Member Registered Successfully!\nEnrollment Number: " + enrollment,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Database Error: Could not register member. The Email might already exist.",
                    "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}