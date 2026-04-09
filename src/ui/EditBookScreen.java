package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.sql.Date;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import dao.BookDAO;
import model.Book;

public class EditBookScreen extends JDialog {

    private JTextField txtIsbn, txtTitle, txtAuthor, txtPublisher, txtDate;
    private JButton btnSave, btnCancel;
    private int currentBookId;

    public EditBookScreen(JDialog parentDialog, Book bookToEdit) {
        super(parentDialog, "Edit Book Details", true);
        setSize(500, 500);
        setLocationRelativeTo(parentDialog);
        setLayout(new BorderLayout());
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(e -> dispose(), escapeKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        this.currentBookId = bookToEdit.getBookId();

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(25, 118, 210));
        headerPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
        JLabel lblHeader = new JLabel("Update Book Information");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFocusable(true);
        headerPanel.add(lblHeader);
        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 20));
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        formPanel.setBackground(Color.WHITE);

        Font labelFont = new Font("SansSerif", Font.BOLD, 14);
        Font inputFont = new Font("SansSerif", Font.PLAIN, 14);

        txtIsbn = new JTextField(bookToEdit.getIsbn());
        txtTitle = new JTextField(bookToEdit.getTitle());
        txtAuthor = new JTextField(bookToEdit.getAuthor());
        txtPublisher = new JTextField(bookToEdit.getPublisher());
        txtDate = new JTextField(bookToEdit.getPublicationDate().toString());

        txtIsbn.setFont(inputFont);
        txtTitle.setFont(inputFont);
        txtAuthor.setFont(inputFont);
        txtPublisher.setFont(inputFont);
        txtDate.setFont(inputFont);

        JLabel lblIsbn = new JLabel("ISBN:");
        lblIsbn.setFont(labelFont);
        lblIsbn.setLabelFor(txtIsbn);

        JLabel lblTitle = new JLabel("Title:");
        lblTitle.setFont(labelFont);
        lblTitle.setLabelFor(txtTitle);

        JLabel lblAuthor = new JLabel("Author:");
        lblAuthor.setFont(labelFont);
        lblAuthor.setLabelFor(txtAuthor);

        JLabel lblPublisher = new JLabel("Publisher:");
        lblPublisher.setFont(labelFont);
        lblPublisher.setLabelFor(txtPublisher);

        JLabel lblDate = new JLabel("Pub Date (YYYY-MM-DD):");
        lblDate.setFont(labelFont);
        lblDate.setLabelFor(txtDate);

        formPanel.add(lblIsbn);
        formPanel.add(txtIsbn);
        formPanel.add(lblTitle);
        formPanel.add(txtTitle);
        formPanel.add(lblAuthor);
        formPanel.add(txtAuthor);
        formPanel.add(lblPublisher);
        formPanel.add(txtPublisher);
        formPanel.add(lblDate);
        formPanel.add(txtDate);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(new Color(245, 245, 245));

        btnSave = new JButton("Update Book");
        btnSave.setFont(labelFont);
        btnSave.setBackground(new Color(76, 175, 80));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> validateAndUpdate());
        btnCancel = new JButton("Cancel");
        btnCancel.setFont(labelFont);
        btnCancel.setBackground(new Color(211, 47, 47));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void validateAndUpdate() {
        String isbn = txtIsbn.getText().trim();
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String publisher = txtPublisher.getText().trim();
        String dateStr = txtDate.getText().trim();

        StringBuilder errors = new StringBuilder();
        String cleanIsbn = isbn.replace("-", "").trim();

        if (cleanIsbn.isEmpty()) {
            errors.append("- ISBN is required.\n");
        } else if (cleanIsbn.length() != 10 && cleanIsbn.length() != 13) {
            errors.append("- ISBN must be exactly 10 or 13 digits long (excluding hyphens).\n");
        } else if (!cleanIsbn.matches("^[0-9]{9}[0-9Xx]$") && !cleanIsbn.matches("^[0-9]{13}$")) {
            errors.append(
                    "- ISBN format is invalid. It must contain only numbers (and optionally 'X' at the end of a 10-digit ISBN).\n");
        }

        if (title.isEmpty()) {
            errors.append("- Title is required.\n");
        }

        if (author.isEmpty()) {
            errors.append("- Author is required.\n");
        } else if (!author.matches("^[a-zA-Z\\s.]+$")) {
            errors.append("- Author name must contain only letters and spaces.\n");
        }

        if (publisher.isEmpty()) {
            errors.append("- Publisher is required.\n");
        } else if (publisher.matches(".*\\d.*")) {
            errors.append("- Publisher name cannot contain numbers.\n");
        } else if (!publisher.matches("^[a-zA-Z\\s.,'&\\-]+$")) {
            errors.append("- Publisher name can only contain letters, spaces, and basic punctuation (& , . - ').\n");
        }
        Date pubDate = null;
        if (dateStr.isEmpty()) {
            errors.append("- Publication Date is required.\n");
        } else {
            try {
                pubDate = Date.valueOf(dateStr);
            } catch (IllegalArgumentException e) {
                errors.append("- Date must be in valid YYYY-MM-DD format.\n");
            }
        }

        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this,
                    "Please fix the following errors:\n\n" + errors.toString(),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Book updatedBook = new Book(currentBookId, cleanIsbn, title, author, publisher, pubDate);

        BookDAO bookDAO = new BookDAO();
        if (bookDAO.updateBook(updatedBook)) {
            JOptionPane.showMessageDialog(this, "Book updated successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update book. The ISBN might already belong to another book.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}