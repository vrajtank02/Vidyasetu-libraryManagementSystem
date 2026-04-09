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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import dao.BookDAO;
import model.Book;

public class AddBookScreen extends JDialog {

    private JTextField txtIsbn, txtTitle, txtAuthor, txtPublisher, txtDate, txtCopies;
    private JButton btnSubmit, btnCancel;

    public AddBookScreen(JFrame parentFrame) {
        super(parentFrame, "Add New Book | Vidyasetu LMS", true);
        setSize(500, 600);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(e -> dispose(), escapeKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(25, 118, 210)); // Deep Blue
        headerPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
        JLabel lblHeader = new JLabel("Register New Book");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFocusable(true);
        headerPanel.add(lblHeader);
        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 20));
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        formPanel.setBackground(Color.WHITE);

        Font labelFont = new Font("SansSerif", Font.BOLD, 14);
        Font inputFont = new Font("SansSerif", Font.PLAIN, 14);

        txtIsbn = createTextField(inputFont, "Enter ISBN");
        txtTitle = createTextField(inputFont, "Enter Book Title");
        txtAuthor = createTextField(inputFont, "Enter Author Name");
        txtPublisher = createTextField(inputFont, "Enter Publisher Name");
        txtDate = createTextField(inputFont, "Enter Publication Date in YYYY-MM-DD format");
        txtCopies = createTextField(inputFont, "Enter number of physical copies to add");

        formPanel.add(createLabel("ISBN:", labelFont, txtIsbn));
        formPanel.add(txtIsbn);

        formPanel.add(createLabel("Book Title:", labelFont, txtTitle));
        formPanel.add(txtTitle);

        formPanel.add(createLabel("Author:", labelFont, txtAuthor));
        formPanel.add(txtAuthor);

        formPanel.add(createLabel("Publisher:", labelFont, txtPublisher));
        formPanel.add(txtPublisher);

        formPanel.add(createLabel("Publication Date (YYYY-MM-DD):", labelFont, txtDate));
        formPanel.add(txtDate);

        formPanel.add(createLabel("Number of Copies:", labelFont, txtCopies));
        formPanel.add(txtCopies);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        btnCancel = new JButton("Cancel");
        btnCancel.setFont(labelFont);
        btnCancel.setBackground(new Color(211, 47, 47)); // Red
        btnCancel.setForeground(Color.WHITE);
        btnCancel.addActionListener(e -> dispose());

        btnSubmit = new JButton("Save Book");
        btnSubmit.setFont(labelFont);
        btnSubmit.setBackground(new Color(76, 175, 80)); // Green for success action
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.addActionListener(e -> validateAndSave());

        buttonPanel.add(btnSubmit);
        buttonPanel.add(btnCancel);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text, Font font, JTextField targetField) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setLabelFor(targetField);
        return label;
    }

    private JTextField createTextField(Font font, String accessibleDesc) {
        JTextField field = new JTextField();
        field.setFont(font);
        field.getAccessibleContext().setAccessibleName(accessibleDesc);
        return field;
    }

    private void validateAndSave() {
        String isbn = txtIsbn.getText().trim();
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String publisher = txtPublisher.getText().trim();
        String dateStr = txtDate.getText().trim();
        String copiesStr = txtCopies.getText().trim();

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
        if (title.isEmpty())
            errors.append("- Title is required.\n");

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
                pubDate = Date.valueOf(dateStr); // Requires strict YYYY-MM-DD
            } catch (IllegalArgumentException e) {
                errors.append("- Date must be in valid YYYY-MM-DD format.\n");
            }
        }

        int numberOfCopies = 0;
        if (copiesStr.isEmpty()) {
            errors.append("- Number of Copies is required.\n");
        } else {
            try {
                numberOfCopies = Integer.parseInt(copiesStr);
                if (numberOfCopies <= 0) {
                    errors.append("- Number of copies must be at least 1.\n");
                }
            } catch (NumberFormatException e) {
                errors.append("- Number of copies must be a valid number.\n");
            }
        }

        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this,
                    "Please fix the following errors:\n\n" + errors.toString(),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Book newBook = new Book();
        newBook.setIsbn(cleanIsbn);
        newBook.setTitle(title);
        newBook.setAuthor(author);
        newBook.setPublisher(publisher);
        newBook.setPublicationDate(pubDate);

        BookDAO bookDAO = new BookDAO();
        boolean success = bookDAO.addBook(newBook, numberOfCopies);

        if (success) {
            JOptionPane.showMessageDialog(this, "Book added successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Close the form
        } else {
            JOptionPane.showMessageDialog(this,
                    "Database Error: Could not add book. ISBN or Title might already exist.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}