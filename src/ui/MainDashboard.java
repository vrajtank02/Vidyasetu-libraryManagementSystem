package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import dao.DashboardDAO;
import model.Admin;

public class MainDashboard extends JFrame {

    private Admin currentAdmin;
    private JTabbedPane tabbedPane;
    private JLabel lblDateTime;

    private JLabel lblValTotalBooks;
    private JLabel lblValTotalCopies;
    private JLabel lblValActiveMembers;
    private JLabel lblValAvailableCopies;
    private JLabel lblValIssuedCopies;
    private JLabel lblValActiveAdmins;

    public MainDashboard(Admin admin) {
        this.currentAdmin = admin;

        setTitle("Home | Vidyasetu LMS");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setupGlobalTabSwitching();

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 118, 210));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        String fullName = currentAdmin.getFirstName() + " " + currentAdmin.getLastName();
        JLabel lblWelcome = new JLabel("Welcome, " + fullName + "!");
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblWelcome.setForeground(Color.WHITE);
        makeLabelAccessible(lblWelcome, "Welcome, " + fullName + "!");

        lblDateTime = new JLabel();
        lblDateTime.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblDateTime.setForeground(Color.WHITE);
        startClock();
        makeLabelAccessible(lblDateTime, "");

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLogout.setBackground(new Color(211, 47, 47));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.getAccessibleContext().setAccessibleName("Logout");
        btnLogout.addActionListener(e -> performLogout());

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightHeader.setOpaque(false);
        rightHeader.add(lblDateTime);
        rightHeader.add(btnLogout);

        headerPanel.add(lblWelcome, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 16));

        tabbedPane.addTab("Dashboard", createDashboardPanel());
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_D);

        tabbedPane.addTab("Books", createBooksPanel());
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_B);

        tabbedPane.addTab("Members", createMembersPanel());
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_M);

        tabbedPane.addTab("Transactions", createTransactionsPanel());
        tabbedPane.setMnemonicAt(3, KeyEvent.VK_T);

        tabbedPane.addTab("Admin Panel", createAdminPanel());
        tabbedPane.setMnemonicAt(4, KeyEvent.VK_A);

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 0) {
                refreshDashboardMetrics();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);

        refreshDashboardMetrics();
    }

    private void setupGlobalTabSwitching() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_TAB && e.isControlDown()) {
                    int currentTab = tabbedPane.getSelectedIndex();
                    int totalTabs = tabbedPane.getTabCount();

                    if (e.isShiftDown()) {
                        tabbedPane.setSelectedIndex((currentTab - 1 + totalTabs) % totalTabs);
                    } else {
                        tabbedPane.setSelectedIndex((currentTab + 1) % totalTabs);
                    }

                    tabbedPane.requestFocusInWindow();
                    return true;
                }
                return false;
            }
        });
    }

    private void makeLabelAccessible(JLabel label, String accessibleName) {
        label.setFocusable(true);
        if (!accessibleName.isEmpty()) {
            label.getAccessibleContext().setAccessibleName(accessibleName);
        }

        Border emptyBorder = new EmptyBorder(2, 4, 2, 4);
        Border focusBorder = BorderFactory.createLineBorder(Color.WHITE, 2);

        label.setBorder(emptyBorder);

        label.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                label.setBorder(focusBorder);
            }

            @Override
            public void focusLost(FocusEvent e) {
                label.setBorder(emptyBorder);
            }
        });
    }

    private void startClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy | hh:mm:ss a");
        Timer timer = new Timer(1000, e -> {
            String currentTime = LocalDateTime.now().format(formatter);
            lblDateTime.setText(currentTime);
            lblDateTime.getAccessibleContext().setAccessibleName("Current Time: " + currentTime);
        });
        timer.start();
    }

    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new AdminLoginScreen().setVisible(true);
        }
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(245, 245, 245));

        JPanel metricsPanel = new JPanel(new GridLayout(2, 3, 25, 25));
        metricsPanel.setBackground(new Color(245, 245, 245));

        lblValTotalBooks = createValueLabel();
        lblValTotalCopies = createValueLabel();
        lblValActiveMembers = createValueLabel();
        lblValAvailableCopies = createValueLabel();
        lblValIssuedCopies = createValueLabel();
        lblValActiveAdmins = createValueLabel();

        metricsPanel.add(createMetricCard("Total Books in library:", lblValTotalBooks, new Color(33, 150, 243)));
        metricsPanel.add(createMetricCard("Total Physical Copies:", lblValTotalCopies, new Color(156, 39, 176)));
        metricsPanel.add(createMetricCard("Available Copies:", lblValAvailableCopies, new Color(0, 150, 136)));
        metricsPanel.add(createMetricCard("Issued Copies:", lblValIssuedCopies, new Color(255, 152, 0)));
        metricsPanel.add(createMetricCard("Active Members", lblValActiveMembers, new Color(76, 175, 80)));
        metricsPanel.add(createMetricCard("System Admins", lblValActiveAdmins, new Color(96, 125, 139)));

        panel.add(metricsPanel, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("0", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 48));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JPanel createMetricCard(String title, JLabel lblValue, Color bgColor) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 2),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        card.add(lblValue);
        card.add(lblTitle);

        card.setFocusable(true);

        card.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.BLACK, 4),
                        new EmptyBorder(18, 18, 18, 18)));
                card.getAccessibleContext().setAccessibleName(title + ", " + lblValue.getText());
            }

            @Override
            public void focusLost(FocusEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(bgColor.darker(), 2),
                        new EmptyBorder(20, 20, 20, 20)));
            }
        });

        return card;
    }

    private void refreshDashboardMetrics() {
        DashboardDAO dashDAO = new DashboardDAO();

        lblValTotalBooks.setText(String.valueOf(dashDAO.getTotalBookTitles()));
        lblValTotalCopies.setText(String.valueOf(dashDAO.getTotalPhysicalCopies()));
        lblValActiveMembers.setText(String.valueOf(dashDAO.getActiveMembers()));
        lblValAvailableCopies.setText(String.valueOf(dashDAO.getAvailableCopies()));
        lblValIssuedCopies.setText(String.valueOf(dashDAO.getIssuedCopies()));
        lblValActiveAdmins.setText(String.valueOf(dashDAO.getActiveAdmins()));
    }

    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        panel.setBackground(Color.WHITE);

        Font btnFont = new Font("SansSerif", Font.PLAIN, 16);

        JButton btnAdd = new JButton("Add Book");
        btnAdd.setFont(btnFont);
        btnAdd.addActionListener(e -> new AddBookScreen(MainDashboard.this).setVisible(true));

        JButton btnViewAll = new JButton("View All Books");
        btnViewAll.setFont(btnFont);
        btnViewAll.addActionListener(e -> new ViewBooksScreen(MainDashboard.this).setVisible(true));

        JButton btnSearch = new JButton("Search Book");
        btnSearch.setFont(btnFont);
        btnSearch.addActionListener(e -> new SearchBooksScreen(MainDashboard.this).setVisible(true));

        panel.add(btnAdd);
        panel.add(btnViewAll);
        panel.add(btnSearch);

        return panel;
    }

    private JPanel createMembersPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        panel.setBackground(Color.WHITE);
        Font btnFont = new Font("SansSerif", Font.PLAIN, 16);

        JButton btnRegister = new JButton("Register Member");
        btnRegister.setFont(btnFont);
        btnRegister.addActionListener(e -> new MemberRegistrationScreen(MainDashboard.this).setVisible(true));

        JButton btnViewAll = new JButton("View All Members");
        btnViewAll.setFont(btnFont);
        btnViewAll.addActionListener(e -> new ViewMembersScreen(MainDashboard.this).setVisible(true));

        JButton btnSearch = new JButton("Search Member");
        btnSearch.setFont(btnFont);
        btnSearch.addActionListener(e -> new SearchMembersScreen(MainDashboard.this).setVisible(true));

        panel.add(btnRegister);
        panel.add(btnViewAll);
        panel.add(btnSearch);

        return panel;
    }

    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        panel.setBackground(Color.WHITE);
        Font btnFont = new Font("SansSerif", Font.PLAIN, 16);

        JButton btnIssue = new JButton("Issue Book");
        btnIssue.setFont(btnFont);
        btnIssue.addActionListener(
                e -> new IssueBookScreen(MainDashboard.this, currentAdmin.getAdminId()).setVisible(true));

        JButton btnReturn = new JButton("Return Book & Collect Fine");
        btnReturn.setFont(btnFont);
        btnReturn.addActionListener(
                e -> new ReturnBookScreen(MainDashboard.this, currentAdmin.getAdminId()).setVisible(true));
        JButton btnViewAll = new JButton("View All Transactions");
        btnViewAll.setFont(btnFont);
        btnViewAll.addActionListener(
                e -> new ViewTransactionsScreen(MainDashboard.this, -1, "All Historical Transactions")
                        .setVisible(true));
        panel.add(btnIssue);
        panel.add(btnReturn);
        panel.add(btnViewAll);

        return panel;
    }

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        panel.setBackground(Color.WHITE);
        Font btnFont = new Font("SansSerif", Font.PLAIN, 16);

        JButton btnProfile = new JButton("My Admin Profile");
        btnProfile.setFont(btnFont);
        btnProfile.addActionListener(e -> new MyAdminProfileScreen(MainDashboard.this, currentAdmin).setVisible(true));

        JButton btnManageAdmins = new JButton("Manage System Admins");
        btnManageAdmins.setFont(btnFont);
        btnManageAdmins.addActionListener(
                e -> new ManageAdminsScreen(MainDashboard.this, currentAdmin.getAdminId()).setVisible(true));

        JButton btnAddAdmin = new JButton("Add New Admin");
        btnAddAdmin.setFont(btnFont);
        btnAddAdmin.addActionListener(e -> {
            AdminRegistrationScreen regScreen = new AdminRegistrationScreen();
            regScreen.setLocationRelativeTo(this); // Centers it over the dashboard
            regScreen.setVisible(true);
        });

        JButton btnChangePassword = new JButton("Change Password");
        btnChangePassword.setFont(btnFont);
        btnChangePassword.addActionListener(
                e -> new ChangePasswordScreen(MainDashboard.this, currentAdmin.getAdminId()).setVisible(true));

        panel.add(btnProfile);
        panel.add(btnManageAdmins);
        panel.add(btnAddAdmin);
        panel.add(btnChangePassword);

        return panel;
    }
}
