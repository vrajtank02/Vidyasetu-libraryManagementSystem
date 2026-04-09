package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import dao.BookDAO;
import model.BookCopy;

public class ManageCopiesScreen extends JDialog {

    private JTable table;
    private DefaultTableModel tableModel;
    private BookDAO bookDAO;
    private int currentBookId;

    public ManageCopiesScreen(JDialog parentDialog, int bookId, String bookTitle) {
        super(parentDialog, "Manage Physical Copies", true);
        this.currentBookId = bookId;
        this.bookDAO = new BookDAO();

        setSize(700, 500);
        setLocationRelativeTo(parentDialog);
        setLayout(new BorderLayout());
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(e -> dispose(), escapeKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(25, 118, 210));
        headerPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
        JLabel lblHeader = new JLabel("Copies for: " + bookTitle);
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);
        add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = { "Copy ID", "Current Status" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("SansSerif", Font.PLAIN, 16));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 16));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getAccessibleContext()
                .setAccessibleName("Physical Copies Data Table. Select a row to change its status.");
        table.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                Collections.singleton(KeyStroke.getKeyStroke("TAB")));
        table.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                Collections.singleton(javax.swing.KeyStroke.getKeyStroke("shift TAB")));

        loadCopiesData();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        bottomPanel.setBackground(Color.WHITE);
        Font btnFont = new Font("SansSerif", Font.BOLD, 14);

        JButton btnMarkLost = new JButton("Mark as Lost");
        btnMarkLost.setFont(btnFont);
        btnMarkLost.setBackground(new Color(211, 47, 47)); // Red
        btnMarkLost.setForeground(Color.WHITE);
        btnMarkLost.addActionListener(e -> changeStatus("Lost"));

        JButton btnMarkDamaged = new JButton("Mark as Damaged");
        btnMarkDamaged.setFont(btnFont);
        btnMarkDamaged.setBackground(new Color(243, 156, 18)); // Orange warning color
        btnMarkDamaged.setForeground(Color.WHITE);
        btnMarkDamaged.addActionListener(e -> changeStatus("Damaged"));

        JButton btnMarkAvailable = new JButton("Mark as Available");
        btnMarkAvailable.setFont(btnFont);
        btnMarkAvailable.setBackground(new Color(76, 175, 80)); // Green
        btnMarkAvailable.setForeground(Color.WHITE);
        btnMarkAvailable.addActionListener(e -> changeStatus("Available"));

        JButton btnClose = new JButton("Close");
        btnClose.setFont(btnFont);
        btnClose.addActionListener(e -> dispose());

        bottomPanel.add(btnMarkLost);
        bottomPanel.add(btnMarkDamaged);
        bottomPanel.add(btnMarkAvailable);
        bottomPanel.add(btnClose);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadCopiesData() {
        tableModel.setRowCount(0);
        List<BookCopy> copies = bookDAO.getCopiesByBookId(currentBookId);
        for (BookCopy copy : copies) {
            tableModel.addRow(new Object[] { copy.getCopyId(), copy.getStatus() });
        }
        if (tableModel.getRowCount() == 0) {
            table.getAccessibleContext().setAccessibleName("Table is empty. No data available.");
        } else {
            table.getAccessibleContext().setAccessibleName(
                    "Data table with " + tableModel.getRowCount() + " rows. Use arrow keys to select a row.");
        }

    }

    private void changeStatus(String newStatus) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a copy from the list first.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int copyId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 1);

        if (currentStatus.equalsIgnoreCase("Issued")) {
            JOptionPane.showMessageDialog(this,
                    "SECURITY BLOCK: You cannot manually change the status of an 'Issued' book. It must be processed through the Returns system.",
                    "Action Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (currentStatus.equalsIgnoreCase(newStatus)) {
            JOptionPane.showMessageDialog(this, "The book is already marked as " + newStatus + ".", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Change status of Copy ID " + copyId + " to '" + newStatus + "'?",
                "Confirm Status Change", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (bookDAO.updateCopyStatus(copyId, newStatus)) {
                JOptionPane.showMessageDialog(this, "Status updated successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadCopiesData();
            } else {
                JOptionPane.showMessageDialog(this, "Database error. Could not update status.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}