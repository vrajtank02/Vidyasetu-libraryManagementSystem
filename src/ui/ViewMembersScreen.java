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

import dao.MemberDAO;
import model.Member;

public class ViewMembersScreen extends JDialog {

    private JTable table;
    private DefaultTableModel tableModel;
    private MemberDAO memberDAO;
    private JComboBox<String> cbFilter;

    public ViewMembersScreen(JFrame parentFrame) {
        super(parentFrame, "Member Directory | Vidyasetu LMS", true);
        setSize(1150, 600); // Made slightly wider to fit the new button
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());

        memberDAO = new MemberDAO();

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(e -> dispose(), escapeKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 118, 210)); // Deep Blue
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblHeader = new JLabel("Library Members Directory");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFocusable(true);
        headerPanel.add(lblHeader, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setOpaque(false);
        JLabel lblFilter = new JLabel("Filter View: ");
        lblFilter.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblFilter.setForeground(Color.WHITE);
        lblFilter.setFocusable(true);

        String[] filters = { "Active Members", "Inactive Members", "All Members" };
        cbFilter = new JComboBox<>(filters);
        cbFilter.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblFilter.setLabelFor(cbFilter);
        cbFilter.addActionListener(e -> loadTableData());

        filterPanel.add(lblFilter);
        filterPanel.add(cbFilter);
        headerPanel.add(filterPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = { "ID", "Enrollment no", "First Name", "Last Name", "Email", "Phone", "Gender", "Status" };
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

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        bottomPanel.setBackground(Color.WHITE);
        Font btnFont = new Font("SansSerif", Font.BOLD, 14);

        JButton btnViewHistory = new JButton("View Transaction History");
        btnViewHistory.setFont(btnFont);
        btnViewHistory.setBackground(new Color(156, 39, 176)); // Purple
        btnViewHistory.setForeground(Color.WHITE);
        btnViewHistory.addActionListener(e -> viewMemberTransactions());

        JButton btnEdit = new JButton("Edit Selected Member");
        btnEdit.setFont(btnFont);
        btnEdit.setBackground(new Color(33, 150, 243)); // Blue
        btnEdit.setForeground(Color.WHITE);
        btnEdit.addActionListener(e -> editSelectedMember());

        JButton btnToggleStatus = new JButton("Toggle Active/Inactive Status");
        btnToggleStatus.setFont(btnFont);
        btnToggleStatus.setBackground(new Color(255, 152, 0)); // Orange
        btnToggleStatus.setForeground(Color.WHITE);
        btnToggleStatus.addActionListener(e -> toggleSelectedMemberStatus());

        JButton btnClose = new JButton("Close");
        btnClose.setFont(btnFont);
        btnClose.setBackground(new Color(211, 47, 47)); // Red
        btnClose.setForeground(Color.WHITE);
        btnClose.addActionListener(e -> dispose());

        bottomPanel.add(btnViewHistory);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnToggleStatus);
        bottomPanel.add(btnClose);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        String selectedFilter = (String) cbFilter.getSelectedItem();
        List<Member> members;

        if (selectedFilter.equals("Active Members")) {
            members = memberDAO.getActiveMembers();
        } else if (selectedFilter.equals("Inactive Members")) {
            members = memberDAO.getInactiveMembers();
        } else {
            members = memberDAO.getAllMembers();
        }

        for (Member m : members) {
            Object[] rowData = {
                    m.getMemberId(),
                    m.getEnrollmentNo(),
                    m.getFirstName(),
                    m.getLastName(),
                    m.getEmail(),
                    m.getPhone(),
                    m.getGender(),
                    m.getStatus()
            };
            tableModel.addRow(rowData);
        }

        if (tableModel.getRowCount() == 0) {
            table.getAccessibleContext().setAccessibleName("Table is empty. No members found for this filter.");
        } else {
            table.getAccessibleContext().setAccessibleName(
                    "Members Data Table with " + tableModel.getRowCount() + " rows. Use arrow keys to select.");
        }
    }

    private void viewMemberTransactions() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member from the table to view their history.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int memberId = (int) tableModel.getValueAt(selectedRow, 0);
        String enrollment = (String) tableModel.getValueAt(selectedRow, 1);
        String firstName = (String) tableModel.getValueAt(selectedRow, 2);
        String lastName = (String) tableModel.getValueAt(selectedRow, 3);

        String contextTitle = "Transactions for " + enrollment + " - " + firstName + " " + lastName;

        ViewTransactionsScreen dialog = new ViewTransactionsScreen(this, memberId, contextTitle);
        dialog.setVisible(true);
    }

    private void toggleSelectedMemberStatus() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member from the table to change their status.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int memberId = (int) tableModel.getValueAt(selectedRow, 0);
        String enrollment = (String) tableModel.getValueAt(selectedRow, 1);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 7);

        if (currentStatus.equals("Active")) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to DEACTIVATE member " + enrollment
                            + "?\nThey will not be able to issue books.",
                    "Confirm Deactivation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (memberDAO.deactivateMember(memberId)) {
                    JOptionPane.showMessageDialog(this, "Member successfully deactivated.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadTableData();
                } else {
                    JOptionPane.showMessageDialog(this, "Database Error.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to REACTIVATE member " + enrollment + "?", "Confirm Reactivation",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (memberDAO.reactivateMember(memberId)) {
                    JOptionPane.showMessageDialog(this, "Member successfully reactivated.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadTableData();
                } else {
                    JOptionPane.showMessageDialog(this, "Database Error.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void editSelectedMember() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member from the table to edit.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int memberId = (int) tableModel.getValueAt(selectedRow, 0);

        Member memberToEdit = null;
        for (Member m : memberDAO.getAllMembers()) {
            if (m.getMemberId() == memberId) {
                memberToEdit = m;
                break;
            }
        }

        if (memberToEdit != null) {
            EditMemberScreen dialog = new EditMemberScreen(this, memberToEdit);
            dialog.setVisible(true);
            loadTableData();
        }
    }
}