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
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import dao.AdminDAO;
import model.Admin;

public class ManageAdminsScreen extends JDialog {

    private JTable table;
    private DefaultTableModel tableModel;
    private AdminDAO adminDAO;
    private JComboBox<String> cbFilter;
    private int currentLoggedInAdminId;

    public ManageAdminsScreen(JFrame parentFrame, int loggedInAdminId) {
        super(parentFrame, "Manage System Admins | Vidyasetu LMS", true);
        this.currentLoggedInAdminId = loggedInAdminId;
        setSize(1000, 600);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());

        adminDAO = new AdminDAO();

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(e -> dispose(), escapeKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 118, 210)); // Deep Blue
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblHeader = new JLabel("System Administrators");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFocusable(true);
        headerPanel.add(lblHeader, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setOpaque(false);

        JLabel lblFilter = new JLabel("Filter View: ");
        lblFilter.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblFilter.setForeground(Color.WHITE);

        String[] filters = { "Active Admins", "Inactive Admins", "All Admins" };
        cbFilter = new JComboBox<>(filters);
        cbFilter.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblFilter.setLabelFor(cbFilter);
        cbFilter.addActionListener(e -> loadTableData());

        filterPanel.add(lblFilter);
        filterPanel.add(cbFilter);
        headerPanel.add(filterPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = { "Admin ID", "Username", "First Name", "Last Name", "Mobile No", "Email", "Status" };
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

        table.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                Collections.singleton(KeyStroke.getKeyStroke("TAB")));
        table.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                Collections.singleton(KeyStroke.getKeyStroke("shift TAB")));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        loadTableData();

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        bottomPanel.setBackground(Color.WHITE);
        Font btnFont = new Font("SansSerif", Font.BOLD, 14);

        JButton btnToggleStatus = new JButton("Toggle Active/Inactive Status");
        btnToggleStatus.setFont(btnFont);
        btnToggleStatus.setBackground(new Color(255, 152, 0)); // Orange
        btnToggleStatus.setForeground(Color.WHITE);
        btnToggleStatus.addActionListener(e -> toggleSelectedAdminStatus());

        JButton btnClose = new JButton("Close");
        btnClose.setFont(btnFont);
        btnClose.setBackground(new Color(211, 47, 47)); // Red
        btnClose.setForeground(Color.WHITE);
        btnClose.addActionListener(e -> dispose());

        bottomPanel.add(btnToggleStatus);
        bottomPanel.add(btnClose);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        String selectedFilter = (String) cbFilter.getSelectedItem();
        List<Admin> admins;

        if (selectedFilter.equals("Active Admins")) {
            admins = adminDAO.getActiveAdmins();
        } else if (selectedFilter.equals("Inactive Admins")) {
            admins = adminDAO.getInactiveAdmins();
        } else {
            admins = adminDAO.getAllAdmins();
        }

        for (Admin a : admins) {
            Object[] rowData = {
                    a.getAdminId(),
                    a.getUsername(),
                    a.getFirstName(),
                    a.getLastName(),
                    a.getMobileNo(),
                    a.getEmail(),
                    a.getStatus()
            };
            tableModel.addRow(rowData);
        }

        if (tableModel.getRowCount() == 0) {
            table.getAccessibleContext().setAccessibleName("Table is empty. No admins found for this filter.");
        } else {
            table.getAccessibleContext().setAccessibleName(
                    "Admins Data Table with " + tableModel.getRowCount() + " rows. Use arrow keys to select.");
        }
    }

    private void toggleSelectedAdminStatus() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an admin from the table.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int targetAdminId = (int) tableModel.getValueAt(selectedRow, 0);
        String targetUsername = (String) tableModel.getValueAt(selectedRow, 1);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 6);

        // Business Rule: Prevent self-deactivation
        if (targetAdminId == currentLoggedInAdminId) {
            JOptionPane.showMessageDialog(this, "Security Block: You cannot change your own active status.",
                    "Action Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (currentStatus.equals("Active")) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to DEACTIVATE admin account: " + targetUsername
                            + "?\nThey will no longer be able to log in.",
                    "Confirm Deactivation", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (adminDAO.deactivateAdmin(targetAdminId)) {
                    JOptionPane.showMessageDialog(this, "Admin account deactivated.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadTableData();
                } else {
                    JOptionPane.showMessageDialog(this, "Database Error.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to REACTIVATE admin account: " + targetUsername + "?",
                    "Confirm Reactivation", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (adminDAO.reactivateAdmin(targetAdminId)) {
                    JOptionPane.showMessageDialog(this, "Admin account reactivated.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadTableData();
                } else {
                    JOptionPane.showMessageDialog(this, "Database Error.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}