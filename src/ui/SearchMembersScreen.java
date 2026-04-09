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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import dao.MemberDAO;
import model.Member;

public class SearchMembersScreen extends JDialog {

    private JTextField txtSearch;
    private JButton btnSearch;
    private JTable table;
    private DefaultTableModel tableModel;
    private MemberDAO memberDAO;

    public SearchMembersScreen(JFrame parentFrame) {
        super(parentFrame, "Search Members | Vidyasetu LMS", true);
        setSize(1150, 650);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());

        memberDAO = new MemberDAO();

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(e -> dispose(), escapeKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(25, 118, 210));
        headerPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
        JLabel lblHeader = new JLabel("Search Library Members");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFocusable(true);
        headerPanel.add(lblHeader);
        topPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        searchBarPanel.setBackground(Color.WHITE);

        Font inputFont = new Font("SansSerif", Font.PLAIN, 16);
        JLabel lblSearch = new JLabel("Enter Keyword (Name, Enrollment, Phone):");
        lblSearch.setFont(new Font("SansSerif", Font.BOLD, 16));

        txtSearch = new JTextField(25);
        txtSearch.setFont(inputFont);
        lblSearch.setLabelFor(txtSearch);

        btnSearch = new JButton("Search");
        btnSearch.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnSearch.setBackground(new Color(33, 150, 243));
        btnSearch.setForeground(Color.WHITE);

        btnSearch.addActionListener(e -> executeSearch());
        txtSearch.addActionListener(e -> executeSearch()); // Enter key support

        searchBarPanel.add(lblSearch);
        searchBarPanel.add(txtSearch);
        searchBarPanel.add(btnSearch);

        topPanel.add(searchBarPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        String[] columnNames = { "ID", "Enrollment No", "First Name", "Last Name", "Email", "Phone", "Gender",
                "Status" };
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
        scrollPane.setBorder(new EmptyBorder(10, 20, 20, 20));
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        bottomPanel.setBackground(Color.WHITE);
        Font btnFont = new Font("SansSerif", Font.BOLD, 14);

        JButton btnViewHistory = new JButton("View Transaction History");
        btnViewHistory.setFont(btnFont);
        btnViewHistory.setBackground(new Color(156, 39, 176));
        btnViewHistory.setForeground(Color.WHITE);
        btnViewHistory.addActionListener(e -> viewMemberTransactions());

        JButton btnEdit = new JButton("Edit Selected Member");
        btnEdit.setFont(btnFont);
        btnEdit.setBackground(new Color(33, 150, 243));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.addActionListener(e -> editSelectedMember());

        JButton btnClose = new JButton("Close");
        btnClose.setFont(btnFont);
        btnClose.setBackground(new Color(211, 47, 47));
        btnClose.setForeground(Color.WHITE);
        btnClose.addActionListener(e -> dispose());

        bottomPanel.add(btnViewHistory);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnClose);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void executeSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a keyword to search.", "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        tableModel.setRowCount(0);
        List<Member> members = memberDAO.searchMembers(keyword);

        if (members.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No members found matching '" + keyword + "'.", "No Results",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Member m : members) {
                Object[] rowData = {
                        m.getMemberId(), m.getEnrollmentNo(), m.getFirstName(), m.getLastName(),
                        m.getEmail(), m.getPhone(), m.getGender(), m.getStatus()
                };
                tableModel.addRow(rowData);
            }
            table.requestFocusInWindow();
            if (tableModel.getRowCount() == 0) {
                table.getAccessibleContext().setAccessibleName("Table is empty. ");
            } else {
                table.getAccessibleContext().setAccessibleName(
                        "Data Table with " + tableModel.getRowCount() + " rows. Use arrow keys to select.");
            }

        }
    }

    private void viewMemberTransactions() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int memberId = (int) tableModel.getValueAt(selectedRow, 0);
        String enrollment = (String) tableModel.getValueAt(selectedRow, 1);
        String contextTitle = "Transactions for " + enrollment;
        new ViewTransactionsScreen(this, memberId, contextTitle).setVisible(true);
    }

    private void editSelectedMember() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int memberId = (int) tableModel.getValueAt(selectedRow, 0);
        Member memberToEdit = memberDAO.getAllMembers().stream().filter(m -> m.getMemberId() == memberId).findFirst()
                .orElse(null);
        if (memberToEdit != null) {
            new EditMemberScreen(this, memberToEdit).setVisible(true);
            executeSearch(); // Refresh list after editing
        }
    }
}