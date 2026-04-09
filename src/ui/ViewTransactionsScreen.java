package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import dao.TransactionDAO;
import model.Transaction;

public class ViewTransactionsScreen extends JDialog {

    private JTable table;
    private DefaultTableModel tableModel;
    private TransactionDAO transactionDAO;
    private int filterMemberId;

    public ViewTransactionsScreen(Window parentWindow, int memberId, String contextTitle) {
        super(parentWindow, "Transaction Records | Vidyasetu LMS", Dialog.ModalityType.APPLICATION_MODAL);
        this.filterMemberId = memberId;
        this.transactionDAO = new TransactionDAO();

        setSize(900, 500);
        setLocationRelativeTo(parentWindow);
        setLayout(new BorderLayout());

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(e -> dispose(), escapeKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(25, 118, 210));
        headerPanel.setBorder(new EmptyBorder(15, 0, 15, 0));

        JLabel lblHeader = new JLabel(contextTitle);
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);
        add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = { "Tx ID", "Copy ID", "Member ID", "Issue Date", "Issued By", "Return Date",
                "Returned To", "Fine (Rs)", "Status" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getAccessibleContext().setAccessibleName(contextTitle + " Table");

        table.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                Collections.singleton(KeyStroke.getKeyStroke("TAB")));
        table.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                Collections.singleton(KeyStroke.getKeyStroke("shift TAB")));

        loadTransactionData();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        bottomPanel.setBackground(Color.WHITE);
        Font btnFont = new Font("SansSerif", Font.BOLD, 14);

        JButton btnClose = new JButton("Close");
        btnClose.setFont(btnFont);
        btnClose.setBackground(new Color(211, 47, 47));
        btnClose.setForeground(Color.WHITE);
        btnClose.addActionListener(e -> dispose());

        bottomPanel.add(btnClose);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadTransactionData() {
        List<Transaction> transactions;

        if (filterMemberId == -1) {
            transactions = transactionDAO.getAllTransactions();
        } else {
            transactions = transactionDAO.getTransactionsByMember(filterMemberId);
        }

        for (Transaction tx : transactions) {
            String issueDate = tx.getIssueDate() != null ? tx.getIssueDate().toString() : "Error";
            String issuedBy = String.valueOf(tx.getIssueAdminId());

            String returnDate = (tx.getReturnDate() != null) ? tx.getReturnDate().toString() : "Pending";
            String returnedTo = (tx.getReturnAdminId() == 0) ? "N/A" : String.valueOf(tx.getReturnAdminId());
            String fineAmount = (tx.getReturnDate() == null) ? "N/A" : String.format("%.2f", tx.getFineAmount());
            String status = (tx.getReturnDate() == null) ? "Currently Issued" : "Returned";

            Object[] rowData = {
                    tx.getTransactionId(),
                    tx.getCopyId(),
                    tx.getMemberId(), // Added Member ID column since we might be viewing all
                    issueDate,
                    issuedBy,
                    returnDate,
                    returnedTo,
                    fineAmount,
                    status
            };
            tableModel.addRow(rowData);
        }
if (tableModel.getRowCount() == 0) {
    table.getAccessibleContext().setAccessibleName("Table is empty. No data available.");
} else {
    table.getAccessibleContext().setAccessibleName("Data table with " + tableModel.getRowCount() + " rows. Use arrow keys to select a row.");
}

    }
}
