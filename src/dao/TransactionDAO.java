package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DatabaseConnection;
import model.Transaction;

public class TransactionDAO {

    public boolean issueBook(Transaction transaction) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return false;

        String insertQuery = "INSERT INTO transactions (copy_id, member_id, issue_admin_id, issue_date) VALUES (?, ?, ?, ?)";
        String updateCopyQuery = "UPDATE book_copies SET status = 'Issued' WHERE copy_id = ?";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt1 = conn.prepareStatement(insertQuery)) {
                pstmt1.setInt(1, transaction.getCopyId());
                pstmt1.setInt(2, transaction.getMemberId());
                pstmt1.setInt(3, transaction.getIssueAdminId());
                pstmt1.setDate(4, transaction.getIssueDate());
                pstmt1.executeUpdate();
            }

            try (PreparedStatement pstmt2 = conn.prepareStatement(updateCopyQuery)) {
                pstmt2.setInt(1, transaction.getCopyId());
                pstmt2.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean returnBook(int transactionId, int returnAdminId, Date returnDate, double fineAmount, int copyId,
            String returnStatus) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return false;

        String updateTxQuery = "UPDATE transactions SET return_admin_id = ?, return_date = ?, fine_amount = ? WHERE transaction_id = ?";

        String updateCopyQuery = "UPDATE book_copies SET status = ? WHERE copy_id = ?";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt1 = conn.prepareStatement(updateTxQuery)) {
                pstmt1.setInt(1, returnAdminId);
                pstmt1.setDate(2, returnDate);
                pstmt1.setDouble(3, fineAmount);
                pstmt1.setInt(4, transactionId);
                pstmt1.executeUpdate();
            }

            try (PreparedStatement pstmt2 = conn.prepareStatement(updateCopyQuery)) {
                pstmt2.setString(1, returnStatus);
                pstmt2.setInt(2, copyId);
                pstmt2.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return transactions;

        String query = "SELECT * FROM transactions";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(rs.getInt("transaction_id"));
                transaction.setCopyId(rs.getInt("copy_id"));
                transaction.setMemberId(rs.getInt("member_id"));
                transaction.setIssueAdminId(rs.getInt("issue_admin_id"));
                transaction.setReturnAdminId(rs.getInt("return_admin_id"));
                transaction.setIssueDate(rs.getDate("issue_date"));
                transaction.setReturnDate(rs.getDate("return_date"));
                transaction.setFineAmount(rs.getDouble("fine_amount"));
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public List<Transaction> getTransactionsByMember(int memberId) {
        List<Transaction> transactions = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return transactions;

        String query = "SELECT * FROM transactions WHERE member_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setTransactionId(rs.getInt("transaction_id"));
                    transaction.setCopyId(rs.getInt("copy_id"));
                    transaction.setMemberId(rs.getInt("member_id"));
                    transaction.setIssueAdminId(rs.getInt("issue_admin_id"));
                    transaction.setReturnAdminId(rs.getInt("return_admin_id"));
                    transaction.setIssueDate(rs.getDate("issue_date"));
                    transaction.setReturnDate(rs.getDate("return_date"));
                    transaction.setFineAmount(rs.getDouble("fine_amount"));
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
}