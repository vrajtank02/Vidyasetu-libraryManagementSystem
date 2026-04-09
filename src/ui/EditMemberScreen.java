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

public class EditMemberScreen extends JDialog {

    private JTextField txtEnrollment, txtFirstName, txtLastName, txtEmail, txtPhone;
    private JComboBox<String> cbGender;
    private JTextArea txtAddress;
    private JButton btnUpdate, btnCancel;
    private Member currentMember;
    private MemberDAO memberDAO;

    public EditMemberScreen(JDialog parentDialog, Member memberToEdit) {
        super(parentDialog, "Edit Member Details | Vidyasetu LMS", true);
        this.currentMember = memberToEdit;
        this.memberDAO = new MemberDAO();

        setSize(550, 650);
        setLocationRelativeTo(parentDialog);
        setLayout(new BorderLayout());

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(e -> dispose(), escapeKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(25, 118, 210));
        headerPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
        JLabel lblHeader = new JLabel("Update Member Information");
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

        txtEnrollment = new JTextField(currentMember.getEnrollmentNo());
        txtEnrollment.setFont(inputFont);
        txtEnrollment.setEditable(false);
        txtEnrollment.setBackground(new Color(240, 240, 240));
        txtEnrollment.getAccessibleContext()
                .setAccessibleName("Enrollment Number " + currentMember.getEnrollmentNo() + ", Read only.");
        JLabel lblEnrollment = new JLabel("Enrollment No:");
        lblEnrollment.setFont(labelFont);
        lblEnrollment.setLabelFor(txtEnrollment);

        txtFirstName = new JTextField(currentMember.getFirstName());
        txtFirstName.setFont(inputFont);
        JLabel lblFirstName = new JLabel("First Name:");
        lblFirstName.setFont(labelFont);
        lblFirstName.setLabelFor(txtFirstName);

        txtLastName = new JTextField(currentMember.getLastName());
        txtLastName.setFont(inputFont);
        JLabel lblLastName = new JLabel("Last Name:");
        lblLastName.setFont(labelFont);
        lblLastName.setLabelFor(txtLastName);

        txtEmail = new JTextField(currentMember.getEmail());
        txtEmail.setFont(inputFont);
        JLabel lblEmail = new JLabel("Email Address:");
        lblEmail.setFont(labelFont);
        lblEmail.setLabelFor(txtEmail);

        txtPhone = new JTextField(currentMember.getPhone());
        txtPhone.setFont(inputFont);
        JLabel lblPhone = new JLabel("Phone No:");
        lblPhone.setFont(labelFont);
        lblPhone.setLabelFor(txtPhone);

        String[] genders = { "Select Gender", "Male", "Female", "Other" };
        cbGender = new JComboBox<>(genders);
        cbGender.setFont(inputFont);
        if (currentMember.getGender() != null) {
            cbGender.setSelectedItem(currentMember.getGender());
        }
        JLabel lblGender = new JLabel("Gender:");
        lblGender.setFont(labelFont);
        lblGender.setLabelFor(cbGender);

        txtAddress = new JTextArea(currentMember.getAddress(), 3, 20);
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
        formPanel.add(lblGender);
        formPanel.add(cbGender);
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

        btnUpdate = new JButton("Update Member");
        btnUpdate.setFont(labelFont);
        btnUpdate.setBackground(new Color(76, 175, 80));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(e -> performValidationAndUpdate());

        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void performValidationAndUpdate() {
        String fName = txtFirstName.getText().trim();
        String lName = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
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

        if (email.isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]+$"))
            errors.append("- Email Address format is invalid.\n");

        if (phone.isEmpty() || phone.length() != 10 || !phone.matches("\\d{10}"))
            errors.append("- Phone Number must be exactly 10 digits.\n");

        if (gender.equals("Select Gender"))
            errors.append("- Please select a Gender.\n");
        if (address.isEmpty())
            errors.append("- Address is required.\n");

        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this, "Please fix the following errors:\n\n" + errors.toString(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentMember.setFirstName(fName);
        currentMember.setLastName(lName);
        currentMember.setEmail(email);
        currentMember.setPhone(phone);
        currentMember.setGender(gender);
        currentMember.setAddress(address);

        if (memberDAO.updateMember(currentMember)) {
            JOptionPane.showMessageDialog(this, "Member Details Updated Successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Close dialog on success
        } else {
            JOptionPane.showMessageDialog(this, "Database Error: Could not update member.", "Update Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}