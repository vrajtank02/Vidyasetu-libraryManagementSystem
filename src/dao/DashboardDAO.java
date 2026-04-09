package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import db.DatabaseConnection;

public class DashboardDAO {

    // A reusable helper method to execute COUNT queries
    private int getCount(String query) {
        int count = 0;
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return 0;

        try (PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count; // Connection remains open for the next dashboard card!
    }

    public int getTotalBookTitles() {
        return getCount("SELECT COUNT(*) FROM books");
    }

    public int getTotalPhysicalCopies() {
        return getCount("SELECT COUNT(*) FROM book_copies");
    }

    public int getActiveMembers() {
        return getCount("SELECT COUNT(*) FROM members WHERE status = 'Active'");
    }

    public int getAvailableCopies() {
        return getCount("SELECT COUNT(*) FROM book_copies WHERE status = 'Available'");
    }

    public int getIssuedCopies() {
        return getCount("SELECT COUNT(*) FROM book_copies WHERE status = 'Issued'");
    }

    public int getActiveAdmins() {
        return getCount("SELECT COUNT(*) FROM admins WHERE status = 'Active'");
    }
}