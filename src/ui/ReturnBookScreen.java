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
import java.time.temporal.ChronoUnit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import dao.TransactionDAO;
import db.DatabaseConnection;

public class ReturnBookScreen extends JDialog {

    private JTextField txtCopyId;
    private JButton btnSearch, btnConfirmReturn, btnCancel;
    private JLabel lblMemberDetails, lblBookDetails, lblIssueDate, lblFineAmount;
    private JComboBox<String> cbReturnCondition;
    private JPanel detailsPanel;

    private int currentAdminId;
    private int activeTransactionId = -1;
    private double calculatedFine = 0.0;

    // Standard Library Rules
    private static final int ALLOWED_DAYS = 14;
    private static final double FINE_PER_DAY = 5.0;

    public ReturnBookScreen(JFrame parentFrame, int adminId) {
        super(parentFrame, "Process Book Return | Vidyasetu LMS", true);
        this.currentAdminId = adminId;

        setSize(600, 500);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(e -> dispose(), escapeKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(25, 118, 210)); // Deep Blue
        headerPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
        JLabel lblHeader = new JLabel("Process Book Return");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblHeader.setForeground(Color.WHITE);
lblHeader.setFocusable(true);        headerPanel.add(lblHeader);
        add(headerPanel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        searchPanel.setBackground(Color.WHITE);

        Font labelFont = new Font("SansSerif", Font.BOLD, 16);
        Font dataFont = new Font("SansSerif", Font.PLAIN, 16);

        JLabel lblSearch = new JLabel("Copy ID (Barcode):");
        lblSearch.setFont(labelFont);

        txtCopyId = new JTextField(15);
        txtCopyId.setFont(dataFont);
        lblSearch.setLabelFor(txtCopyId);

        btnSearch = new JButton("Search");
        btnSearch.setFont(labelFont);
        btnSearch.setBackground(new Color(33, 150, 243)); // Blue
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> fetchTransactionDetails());

        txtCopyId.addActionListener(e -> fetchTransactionDetails());

        searchPanel.add(lblSearch);
        searchPanel.add(txtCopyId);
        searchPanel.add(btnSearch);

        detailsPanel = new JPanel(new GridLayout(5, 2, 10, 15)); // Increased to 5 rows for the condition dropdown
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 40, 20, 40),
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        "Transaction Summary")));
        detailsPanel.setBackground(new Color(250, 250, 250));
        detailsPanel.setVisible(false);

        lblMemberDetails = new JLabel("-");
        lblMemberDetails.setFont(dataFont);
        lblBookDetails = new JLabel("-");
        lblBookDetails.setFont(dataFont);
        lblIssueDate = new JLabel("-");
        lblIssueDate.setFont(dataFont);

        lblFineAmount = new JLabel("-");
        lblFineAmount.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblFineAmount.setForeground(new Color(211, 47, 47)); // Red

        String[] conditions = { "Good Condition", "Damaged", "Lost" };
        cbReturnCondition = new JComboBox<>(conditions);
        cbReturnCondition.setFont(dataFont);

        detailsPanel.add(new JLabel("Issued To:")).setFont(labelFont);
        detailsPanel.add(lblMemberDetails);

        detailsPanel.add(new JLabel("Book Title:")).setFont(labelFont);
        detailsPanel.add(lblBookDetails);

        detailsPanel.add(new JLabel("Issue Date:")).setFont(labelFont);
        detailsPanel.add(lblIssueDate);

        detailsPanel.add(new JLabel("Late Fine (Rs):")).setFont(labelFont);
        detailsPanel.add(lblFineAmount);

        JLabel lblCondition = new JLabel("Return Condition:");
        lblCondition.setFont(labelFont);
        lblCondition.setLabelFor(cbReturnCondition);
        detailsPanel.add(lblCondition);
        detailsPanel.add(cbReturnCondition);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(Color.WHITE);
        centerWrapper.add(searchPanel, BorderLayout.NORTH);
        centerWrapper.add(detailsPanel, BorderLayout.CENTER);
        add(centerWrapper, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(new Color(245, 245, 245));

        btnCancel = new JButton("Cancel");
        btnCancel.setFont(labelFont);
        btnCancel.setBackground(new Color(211, 47, 47)); // Red
        btnCancel.setForeground(Color.WHITE);
        btnCancel.addActionListener(e -> dispose());

        btnConfirmReturn = new JButton("Confirm Return");
        btnConfirmReturn.setFont(labelFont);
        btnConfirmReturn.setBackground(new Color(76, 175, 80)); // Green
        btnConfirmReturn.setForeground(Color.WHITE);
        btnConfirmReturn.setEnabled(false); // Disabled until details are verified
        btnConfirmReturn.addActionListener(e -> processReturn());

        buttonPanel.add(btnConfirmReturn);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void fetchTransactionDetails() {
        String copyIdStr = txtCopyId.getText().trim();
        if (copyIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Copy ID.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int copyId = Integer.parseInt(copyIdStr);
            Connection conn = DatabaseConnection.getConnection();

            String query = "SELECT t.transaction_id, t.issue_date, b.title, m.first_name, m.last_name, m.enrollment_no "
                    +
                    "FROM transactions t " +
                    "JOIN book_copies c ON t.copy_id = c.copy_id " +
                    "JOIN books b ON c.book_id = b.book_id " +
                    "JOIN members m ON t.member_id = m.member_id " +
                    "WHERE t.copy_id = ? AND t.return_date IS NULL";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, copyId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                activeTransactionId = rs.getInt("transaction_id");
                Date issueDateSql = rs.getDate("issue_date");
                String bookTitle = rs.getString("title");
                String memberName = rs.getString("first_name") + " " + rs.getString("last_name");
                String enrollmentNo = rs.getString("enrollment_no");

                LocalDate issueDate = issueDateSql.toLocalDate();
                LocalDate today = LocalDate.now();

                long daysKept = ChronoUnit.DAYS.between(issueDate, today);
                if (daysKept > ALLOWED_DAYS) {
                    calculatedFine = (daysKept - ALLOWED_DAYS) * FINE_PER_DAY;
                } else {
                    calculatedFine = 0.0;
                }

                lblMemberDetails.setText(enrollmentNo + " - " + memberName);
                lblBookDetails.setText(bookTitle);
                lblIssueDate.setText(issueDate.toString() + " (" + daysKept + " days ago)");
                lblFineAmount.setText(String.format("%.2f", calculatedFine));

                cbReturnCondition.setSelectedIndex(0);

                if (calculatedFine > 0) {
                    btnConfirmReturn.setText("Collect Fine & Return");
                } else {
                    btnConfirmReturn.setText("Confirm Return");
                }

                detailsPanel.setVisible(true);
                btnConfirmReturn.setEnabled(true);

                cbReturnCondition.requestFocusInWindow();

            } else {
                JOptionPane.showMessageDialog(this, "No active issued record found for Copy ID: " + copyId, "Not Found",
                        JOptionPane.INFORMATION_MESSAGE);
                resetDetails();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Copy ID must be a number.", "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
            resetDetails();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void processReturn() {
        if (activeTransactionId == -1)
            return;

        int copyId = Integer.parseInt(txtCopyId.getText().trim());
        Date returnDate = Date.valueOf(LocalDate.now());

        String selectedCondition = cbReturnCondition.getSelectedItem().toString();
        String dbStatusForCopy = "Available";

        if (selectedCondition.equals("Damaged")) {
            dbStatusForCopy = "Damaged";
        } else if (selectedCondition.equals("Lost")) {
            dbStatusForCopy = "Lost";
        }

        TransactionDAO dao = new TransactionDAO();
        boolean success = dao.returnBook(activeTransactionId, currentAdminId, returnDate, calculatedFine, copyId,
                dbStatusForCopy);

        if (success) {
            String message = "Return Successful!\nFine Collected: Rs. " + calculatedFine;
            if (!dbStatusForCopy.equals("Available")) {
                message += "\nBook Copy " + copyId + " marked as: " + dbStatusForCopy;
            }

            JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
            resetDetails();
            txtCopyId.setText("");
            txtCopyId.requestFocusInWindow(); // Move focus back to start for the next book
        } else {
            JOptionPane.showMessageDialog(this, "Database error during return process.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetDetails() {
        activeTransactionId = -1;
        calculatedFine = 0.0;
        detailsPanel.setVisible(false);
        btnConfirmReturn.setEnabled(false);
        btnConfirmReturn.setText("Confirm Return");
    }
}