package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.sql.Date;
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
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import dao.BookDAO;
import model.Book;

public class ViewBooksScreen extends JDialog {

    private JTable table;
    private DefaultTableModel tableModel;
    private BookDAO bookDAO;

    public ViewBooksScreen(JFrame parentFrame) {
        super(parentFrame, "Books | Vidyasetu LMS", true);
        setSize(1000, 600); // Made slightly wider to fit all 8 columns comfortably
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());

        bookDAO = new BookDAO();

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,
                0);
        getRootPane().registerKeyboardAction(e -> dispose(), escapeKeyStroke,
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(25, 118, 210));
        headerPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
        JLabel lblHeader = new JLabel("Library Catalog & Inventory");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);
        add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = { "Book ID", "ISBN", "Title", "Author", "Publisher", "Publication Date", "Total Copies",
                "Available copies" };
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
        table.getAccessibleContext().setAccessibleName(
                "Books Catalog Data Table. Use arrow keys to select a row to Edit or Manage Copies.");

        table.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                Collections.singleton(KeyStroke.getKeyStroke("TAB")));
        table.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                Collections.singleton(KeyStroke.getKeyStroke("shift TAB")));

        loadTableData();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        bottomPanel.setBackground(Color.WHITE);
        Font btnFont = new Font("SansSerif", Font.BOLD, 14);

        JButton btnEdit = new JButton("Edit Selected Book");
        btnEdit.setFont(btnFont);
        btnEdit.setBackground(new Color(33, 150, 243));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.addActionListener(e -> editSelectedBook());

        JButton btnManageCopies = new JButton("Manage Physical Copies");
        btnManageCopies.setFont(btnFont);
        btnManageCopies.setBackground(new Color(255, 152, 0));
        btnManageCopies.setForeground(Color.WHITE);
        btnManageCopies.addActionListener(e -> manageSelectedBookCopies());

        JButton btnClose = new JButton("Close");
        btnClose.setFont(btnFont);
        btnClose.setBackground(new Color(211, 47, 47));
        btnClose.setForeground(Color.WHITE);
        btnClose.addActionListener(e -> dispose());

        bottomPanel.add(btnEdit);
        bottomPanel.add(btnManageCopies);
        bottomPanel.add(btnClose);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private String formatISBN(String isbn) {
        if (isbn == null)
            return "";

        if (isbn.length() == 13) {
            return isbn.substring(0, 3) + "-" +
                    isbn.substring(3, 4) + "-" +
                    isbn.substring(4, 8) + "-" +
                    isbn.substring(8, 12) + "-" +
                    isbn.substring(12);
        } else if (isbn.length() == 10) {
            return isbn.substring(0, 1) + "-" +
                    isbn.substring(1, 5) + "-" +
                    isbn.substring(5, 9) + "-" +
                    isbn.substring(9);
        }

        return isbn;
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        List<Book> books = bookDAO.getAllBooks();
        for (Book b : books) {
            Object[] rowData = {
                    b.getBookId(),
                    formatISBN(b.getIsbn()),
                    b.getTitle(),
                    b.getAuthor(),
                    b.getPublisher(),
                    b.getPublicationDate(),
                    b.getTotalCopies(),
                    b.getAvailableCopies()
            };
            tableModel.addRow(rowData);
        }
        if (tableModel.getRowCount() == 0) {
            table.getAccessibleContext().setAccessibleName("Table is empty. No data available.");
        } else {
            table.getAccessibleContext().setAccessibleName(
                    "Data table with " + tableModel.getRowCount() + " rows. Use arrow keys to select a row.");
        }

    }

    private void editSelectedBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book from the table to edit.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String isbn = (String) tableModel.getValueAt(selectedRow, 1);
        String title = (String) tableModel.getValueAt(selectedRow, 2);
        String author = (String) tableModel.getValueAt(selectedRow, 3);
        String publisher = (String) tableModel.getValueAt(selectedRow, 4);
        Date pubDate = (Date) tableModel.getValueAt(selectedRow, 5);

        Book bookToEdit = new Book(id, isbn, title, author, publisher, pubDate);

        EditBookScreen editScreen = new EditBookScreen(this, bookToEdit);
        editScreen.setVisible(true);

        loadTableData(); // Refreshes grid after the edit saves
    }

    private void manageSelectedBookCopies() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book from the table to manage its copies.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookId = (int) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 2);

        ManageCopiesScreen copiesScreen = new ManageCopiesScreen(this, bookId, title);
        copiesScreen.setVisible(true);

        loadTableData();
    }
}