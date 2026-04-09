package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DatabaseConnection;
import model.Book;
import model.BookCopy;

public class BookDAO {

    public boolean addBook(Book book, int numberOfCopies) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return false;

        String insertBookQuery = "INSERT INTO books (isbn, title, author, publisher, publication_date) VALUES (?, ?, ?, ?, ?)";
        String insertCopyQuery = "INSERT INTO book_copies (book_id, status) VALUES (?, 'Available')";

        try {
            conn.setAutoCommit(false);

            int generatedBookId = -1;
            try (PreparedStatement pstmt = conn.prepareStatement(insertBookQuery,
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, book.getIsbn());
                pstmt.setString(2, book.getTitle());
                pstmt.setString(3, book.getAuthor());
                pstmt.setString(4, book.getPublisher());
                pstmt.setDate(5, book.getPublicationDate());
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedBookId = rs.getInt(1);
                    }
                }
            }

            if (generatedBookId != -1) {
                try (PreparedStatement copyStmt = conn.prepareStatement(insertCopyQuery)) {
                    for (int i = 0; i < numberOfCopies; i++) {
                        copyStmt.setInt(1, generatedBookId);
                        copyStmt.addBatch(); // Batch processing is highly efficient
                    }
                    copyStmt.executeBatch();
                }
            }

            conn.commit(); // Save everything
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null)
                    conn.rollback(); // Cancel if error occurs
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null)
                    conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return books;

        // NEW SQL QUERY: Uses subqueries to count the copies live from the database
        String query = "SELECT b.*, " +
                "(SELECT COUNT(*) FROM book_copies c WHERE c.book_id = b.book_id) AS total_copies, " +
                "(SELECT COUNT(*) FROM book_copies c WHERE c.book_id = b.book_id AND c.status = 'Available') AS available_copies "
                +
                "FROM books b";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getInt("book_id"));
                book.setIsbn(rs.getString("isbn"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setPublisher(rs.getString("publisher"));
                book.setPublicationDate(rs.getDate("publication_date"));

                // Fetch the new columns
                book.setTotalCopies(rs.getInt("total_copies"));
                book.setAvailableCopies(rs.getInt("available_copies"));

                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public boolean updateBook(Book book) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return false;

        String query = "UPDATE books SET isbn = ?, title = ?, author = ?, publisher = ?, publication_date = ? WHERE book_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, book.getIsbn());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());
            pstmt.setString(4, book.getPublisher());
            pstmt.setDate(5, book.getPublicationDate());
            pstmt.setInt(6, book.getBookId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addBookCopy(int bookId) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return false;

        String query = "INSERT INTO book_copies (book_id, status) VALUES (?, 'Available')";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, bookId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCopyStatus(int copyId, String status) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return false;

        String query = "UPDATE book_copies SET status = ? WHERE copy_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, copyId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<BookCopy> getCopiesByBookId(int bookId) {
        List<model.BookCopy> copies = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return copies;

        String query = "SELECT * FROM book_copies WHERE book_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BookCopy copy = new BookCopy(
                            rs.getInt("copy_id"),
                            rs.getInt("book_id"),
                            rs.getString("status"));
                    copies.add(copy);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return copies;
    }

    public List<BookCopy> getAvailableCopies(int bookId) {
        List<BookCopy> availableCopies = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return availableCopies;

        String query = "SELECT * FROM book_copies WHERE book_id = ? AND status = 'Available'";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BookCopy copy = new BookCopy(
                            rs.getInt("copy_id"),
                            rs.getInt("book_id"),
                            rs.getString("status"));
                    availableCopies.add(copy);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return availableCopies;
    }

    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return books;

        String searchPattern = "%" + keyword + "%";

        String query = "SELECT b.*, " +
                "(SELECT COUNT(*) FROM book_copies c WHERE c.book_id = b.book_id) AS total_copies, " +
                "(SELECT COUNT(*) FROM book_copies c WHERE c.book_id = b.book_id AND c.status = 'Available') AS available_copies "
                +
                "FROM books b " +
                "WHERE b.title LIKE ? OR b.author LIKE ? OR b.isbn LIKE ? OR b.publisher LIKE ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book();
                    book.setBookId(rs.getInt("book_id"));
                    book.setIsbn(rs.getString("isbn"));
                    book.setTitle(rs.getString("title"));
                    book.setAuthor(rs.getString("author"));
                    book.setPublisher(rs.getString("publisher"));
                    book.setPublicationDate(rs.getDate("publication_date"));
                    book.setTotalCopies(rs.getInt("total_copies"));
                    book.setAvailableCopies(rs.getInt("available_copies"));

                    books.add(book);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
}