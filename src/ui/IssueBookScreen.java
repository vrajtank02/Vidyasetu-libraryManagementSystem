package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import dao.MemberDAO;
import dao.TransactionDAO;
import db.DatabaseConnection;
import model.Member;
import model.Transaction;

public class IssueBookScreen extends JDialog {

    private JTextField txtCopyId, txtEnrollmentNo;
    private JButton btnVerify, btnIssue, btnCancel;
    private JLabel lblBookDetails, lblMemberDetails;
    private JPanel detailsPanel;

    private int currentAdminId;
    private int verifiedCopyId = -1;
    private int verifiedMemberId = -1;

    public IssueBookScreen(JFrame parentFrame, int adminId) {
        super(parentFrame, "Issue Book | Vidyasetu LMS", true);
        this.currentAdminId = adminId;

        setSize(600, 450);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(e -> dispose(), escapeKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(25, 118, 210));
        headerPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
        JLabel lblHeader = new JLabel("Issue Book to Member");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFocusable(true);
        headerPanel.add(lblHeader);
        add(headerPanel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 15));
        inputPanel.setBorder(new EmptyBorder(20, 40, 10, 40));
        inputPanel.setBackground(Color.WHITE);

        Font labelFont = new Font("SansSerif", Font.BOLD, 16);
        Font dataFont = new Font("SansSerif", Font.PLAIN, 16);

        txtEnrollmentNo = new JTextField();
        txtEnrollmentNo.setFont(dataFont);
        JLabel lblEnrollment = new JLabel("Enrollment No:");
        lblEnrollment.setFont(labelFont);
        lblEnrollment.setLabelFor(txtEnrollmentNo);

        txtCopyId = new JTextField();
        txtCopyId.setFont(dataFont);
        JLabel lblCopyId = new JLabel("Copy ID (Barcode):");
        lblCopyId.setFont(labelFont);
        lblCopyId.setLabelFor(txtCopyId);

        inputPanel.add(lblEnrollment);
        inputPanel.add(txtEnrollmentNo);
        inputPanel.add(lblCopyId);
        inputPanel.add(txtCopyId);

        detailsPanel = new JPanel(new GridLayout(2, 1, 5, 10));
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 40, 20, 40),
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        "Verification Details")));
        detailsPanel.setBackground(new Color(250, 250, 250));

        lblMemberDetails = new JLabel("Member: Pending Verification...");
        lblMemberDetails.setFont(labelFont);
        lblMemberDetails.setForeground(Color.DARK_GRAY);
        lblMemberDetails.setFocusable(true);

        lblBookDetails = new JLabel("Book: Pending Verification...");
        lblBookDetails.setFont(labelFont);
        lblBookDetails.setForeground(Color.DARK_GRAY);
        lblBookDetails.setFocusable(true);

        detailsPanel.add(lblMemberDetails);
        detailsPanel.add(lblBookDetails);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(Color.WHITE);
        centerWrapper.add(inputPanel, BorderLayout.NORTH);
        centerWrapper.add(detailsPanel, BorderLayout.CENTER);
        add(centerWrapper, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(new Color(245, 245, 245));

        btnVerify = new JButton("Verify Details");
        btnVerify.setFont(labelFont);
        btnVerify.setBackground(new Color(33, 150, 243)); // Blue
        btnVerify.setForeground(Color.WHITE);
        btnVerify.addActionListener(e -> verifyInputs());

        btnCancel = new JButton("Cancel");
        btnCancel.setFont(labelFont);
        btnCancel.setBackground(new Color(211, 47, 47)); // Red
        btnCancel.setForeground(Color.WHITE);
        btnCancel.addActionListener(e -> dispose());

        btnIssue = new JButton("Confirm Issue");
        btnIssue.setFont(labelFont);
        btnIssue.setBackground(new Color(76, 175, 80)); // Green
        btnIssue.setForeground(Color.WHITE);
        btnIssue.setEnabled(false);
        btnIssue.addActionListener(e -> processIssue());

        buttonPanel.add(btnVerify);
        buttonPanel.add(btnIssue);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void verifyInputs() {
        String enrollmentNo = txtEnrollmentNo.getText().trim();
        String copyIdStr = txtCopyId.getText().trim();

        if (enrollmentNo.isEmpty() || copyIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Enrollment No and Copy ID.", "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean memberValid = false;
        boolean bookValid = false;

        MemberDAO memberDAO = new MemberDAO();
        Member member = memberDAO.getMemberByEnrollmentNo(enrollmentNo);

        if (member == null) {
            lblMemberDetails.setText("Member: NOT FOUND");
            lblMemberDetails.setForeground(new Color(211, 47, 47)); // Red
            lblMemberDetails.getAccessibleContext().setAccessibleName("Error: Member not found.");
        } else if (!member.getStatus().equalsIgnoreCase("Active")) {
            lblMemberDetails
                    .setText("Member: " + member.getFirstName() + " " + member.getLastName() + " (ACCOUNT INACTIVE)");
            lblMemberDetails.setForeground(new Color(211, 47, 47));
            lblMemberDetails.getAccessibleContext().setAccessibleName("Error: Member account is inactive.");
        } else {
            lblMemberDetails.setText("Member: " + member.getFirstName() + " " + member.getLastName() + " (Verified)");
            lblMemberDetails.setForeground(new Color(76, 175, 80)); // Green
            lblMemberDetails.getAccessibleContext()
                    .setAccessibleName("Verified Member: " + member.getFirstName() + " " + member.getLastName());
            verifiedMemberId = member.getMemberId();
            memberValid = true;
        }

        try {
            int copyId = Integer.parseInt(copyIdStr);
            Connection conn = DatabaseConnection.getConnection();
            String query = "SELECT b.title, c.status FROM book_copies c JOIN books b ON c.book_id = b.book_id WHERE c.copy_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, copyId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String title = rs.getString("title");
                String status = rs.getString("status");

                if (!status.equalsIgnoreCase("Available")) {
                    lblBookDetails.setText("Book: " + title + " (CURRENTLY " + status.toUpperCase() + ")");
                    lblBookDetails.setForeground(new Color(211, 47, 47));
                    lblBookDetails.getAccessibleContext().setAccessibleName("Error: Book is currently " + status);
                } else {
                    lblBookDetails.setText("Book: " + title + " (Available)");
                    lblBookDetails.setForeground(new Color(76, 175, 80));
                    lblBookDetails.getAccessibleContext().setAccessibleName("Verified Book: " + title);
                    verifiedCopyId = copyId;
                    bookValid = true;
                }
            } else {
                lblBookDetails.setText("Book: INVALID COPY ID");
                lblBookDetails.setForeground(new Color(211, 47, 47));
                lblBookDetails.getAccessibleContext().setAccessibleName("Error: Invalid physical copy ID.");
            }
        } catch (NumberFormatException ex) {
            lblBookDetails.setText("Book: COPY ID MUST BE A NUMBER");
            lblBookDetails.setForeground(new Color(211, 47, 47));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        lblMemberDetails.requestFocusInWindow();

        if (memberValid && bookValid) {
            btnIssue.setEnabled(true);
            btnVerify.setEnabled(false);
            txtEnrollmentNo.setEditable(false);
            txtCopyId.setEditable(false);
        } else {
            btnIssue.setEnabled(false);
        }
    }

    private void processIssue() {
        Transaction tx = new Transaction();
        tx.setCopyId(verifiedCopyId);
        tx.setMemberId(verifiedMemberId);
        tx.setIssueAdminId(currentAdminId);
        tx.setIssueDate(Date.valueOf(LocalDate.now()));

        TransactionDAO dao = new TransactionDAO();
        if (dao.issueBook(tx)) {
            JOptionPane.showMessageDialog(this, "Book successfully issued!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Database error while issuing.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}