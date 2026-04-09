package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DatabaseConnection;
import model.Admin;

public class AdminDAO {

    public Admin verifyLogin(String username, String password) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        String query = "SELECT * FROM admins WHERE username = ? AND password = ? AND status = 'Active'";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Admin admin = new Admin();
                    admin.setAdminId(rs.getInt("admin_id"));
                    admin.setUsername(rs.getString("username"));
                    admin.setFirstName(rs.getString("first_name"));
                    admin.setLastName(rs.getString("last_name"));
                    return admin;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean addAdmin(Admin admin) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return false;

        String sql = "INSERT INTO admins (username, password, first_name, last_name, mobile_no, email, gender, address, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, admin.getUsername());
            pstmt.setString(2, admin.getPassword());
            pstmt.setString(3, admin.getFirstName());
            pstmt.setString(4, admin.getLastName());
            pstmt.setString(5, admin.getMobileNo());
            pstmt.setString(6, admin.getEmail());
            pstmt.setString(7, admin.getGender());
            pstmt.setString(8, admin.getAddress());
            pstmt.setString(9, admin.getStatus() != null ? admin.getStatus() : "Active");

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Admin> getAllAdmins() {
        List<Admin> admins = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return admins;

        String query = "SELECT * FROM admins";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Admin admin = new Admin();
                admin.setAdminId(rs.getInt("admin_id"));
                admin.setUsername(rs.getString("username"));
                admin.setFirstName(rs.getString("first_name"));
                admin.setLastName(rs.getString("last_name"));
                admin.setMobileNo(rs.getString("mobile_no"));
                admin.setEmail(rs.getString("email"));
                admin.setGender(rs.getString("gender"));
                admin.setAddress(rs.getString("address"));
                admin.setStatus(rs.getString("status"));

                admins.add(admin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admins;
    }

    public boolean updateAdmin(Admin admin) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return false;

        String query = "UPDATE admins SET username = ?, first_name = ?, last_name = ?, mobile_no = ?, email = ?, gender = ?, address = ?, status = ? WHERE admin_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, admin.getUsername());
            pstmt.setString(2, admin.getFirstName());
            pstmt.setString(3, admin.getLastName());
            pstmt.setString(4, admin.getMobileNo());
            pstmt.setString(5, admin.getEmail());
            pstmt.setString(6, admin.getGender());
            pstmt.setString(7, admin.getAddress());
            pstmt.setString(8, admin.getStatus());
            pstmt.setInt(9, admin.getAdminId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean changePassword(int adminId, String newPassword) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return false;

        String query = "UPDATE admins SET password = ? WHERE admin_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, adminId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deactivateAdmin(int adminId) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return false;

        String query = "UPDATE admins SET status = 'Inactive' WHERE admin_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, adminId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean reactivateAdmin(int adminId) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return false;

        String query = "UPDATE admins SET status = 'Active' WHERE admin_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, adminId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Admin> getActiveAdmins() {
        List<Admin> admins = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return admins;

        String query = "SELECT * FROM admins WHERE status = 'Active'";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Admin admin = new Admin();
                admin.setAdminId(rs.getInt("admin_id"));
                admin.setUsername(rs.getString("username"));
                admin.setFirstName(rs.getString("first_name"));
                admin.setLastName(rs.getString("last_name"));
                admin.setMobileNo(rs.getString("mobile_no"));
                admin.setEmail(rs.getString("email"));
                admin.setGender(rs.getString("gender"));
                admin.setAddress(rs.getString("address"));
                admin.setStatus(rs.getString("status"));

                admins.add(admin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admins;
    }

    public List<Admin> getInactiveAdmins() {
        List<Admin> admins = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return admins;

        String query = "SELECT * FROM admins WHERE status = 'Inactive'";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Admin admin = new Admin();
                admin.setAdminId(rs.getInt("admin_id"));
                admin.setUsername(rs.getString("username"));
                admin.setFirstName(rs.getString("first_name"));
                admin.setLastName(rs.getString("last_name"));
                admin.setMobileNo(rs.getString("mobile_no"));
                admin.setEmail(rs.getString("email"));
                admin.setGender(rs.getString("gender"));
                admin.setAddress(rs.getString("address"));
                admin.setStatus(rs.getString("status"));

                admins.add(admin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admins;
    }
}