package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import db.DatabaseConnection;
import model.Member;

public class MemberDAO {

    public boolean addMember(Member member) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return false;

        // Added gender to the INSERT query
        String query = "INSERT INTO members (enrollment_no, first_name, last_name, email, phone, gender, address, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, member.getEnrollmentNo());
            pstmt.setString(2, member.getFirstName());
            pstmt.setString(3, member.getLastName());
            pstmt.setString(4, member.getEmail());
            pstmt.setString(5, member.getPhone());
            pstmt.setString(6, member.getGender()); // Set the gender
            pstmt.setString(7, member.getAddress());
            pstmt.setString(8, member.getStatus() != null ? member.getStatus() : "Active");

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return members;

        String query = "SELECT * FROM members";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Member member = new Member(
                        rs.getInt("member_id"),
                        rs.getString("enrollment_no"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("gender"),
                        rs.getString("address"),
                        rs.getString("status"));
                members.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    public boolean updateMember(Member member) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return false;

        String query = "UPDATE members SET enrollment_no = ?, first_name = ?, last_name = ?, email = ?, phone = ?, gender = ?, address = ?, status = ? WHERE member_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, member.getEnrollmentNo());
            pstmt.setString(2, member.getFirstName());
            pstmt.setString(3, member.getLastName());
            pstmt.setString(4, member.getEmail());
            pstmt.setString(5, member.getPhone());
            pstmt.setString(6, member.getGender());
            pstmt.setString(7, member.getAddress());
            pstmt.setString(8, member.getStatus());
            pstmt.setInt(9, member.getMemberId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Soft delete: Changes status to 'Inactive' instead of permanently deleting
    public boolean deactivateMember(int memberId) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return false;

        String query = "UPDATE members SET status = 'Inactive' WHERE member_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, memberId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Member> getActiveMembers() {
        List<Member> members = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return members;

        String query = "SELECT * FROM members WHERE status = 'Active'";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Member member = new Member(
                        rs.getInt("member_id"),
                        rs.getString("enrollment_no"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("gender"),
                        rs.getString("address"),
                        rs.getString("status"));
                members.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    public boolean reactivateMember(int memberId) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return false;

        String query = "UPDATE members SET status = 'Active' WHERE member_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, memberId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Member> getInactiveMembers() {
        List<Member> members = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return members;

        String query = "SELECT * FROM members WHERE status = 'Inactive'";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Member member = new Member(
                        rs.getInt("member_id"),
                        rs.getString("enrollment_no"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("gender"),
                        rs.getString("address"),
                        rs.getString("status"));
                members.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    public String generateNextEnrollmentNo() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null)
            return null;

        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        int startYear = (month >= 4) ? year : year - 1;
        int endYear = startYear + 1;

        String fyPrefix = String.format("VS%02d%02d", startYear % 100, endYear % 100);

        String query = "SELECT enrollment_no FROM members WHERE enrollment_no LIKE ? ORDER BY enrollment_no DESC LIMIT 1";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, fyPrefix + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String lastNo = rs.getString("enrollment_no");
                    try {
                        String sequenceStr = lastNo.substring(fyPrefix.length());
                        int sequence = Integer.parseInt(sequenceStr);
                        return fyPrefix + String.format("%04d", sequence + 1);
                    } catch (Exception e) {
                        return fyPrefix + "0001"; // Fallback if parsing fails
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fyPrefix + "0001";
    }
    public Member getMemberByEnrollmentNo(String enrollmentNo) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        String query = "SELECT * FROM members WHERE enrollment_no = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, enrollmentNo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Member(
                        rs.getInt("member_id"), rs.getString("enrollment_no"),
                        rs.getString("first_name"), rs.getString("last_name"),
                        rs.getString("email"), rs.getString("phone"),
                        rs.getString("gender"), rs.getString("address"),
                        rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<Member> searchMembers(String keyword) {
        List<Member> members = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return members;

        String searchPattern = "%" + keyword + "%";
        String query = "SELECT * FROM members WHERE enrollment_no LIKE ? OR first_name LIKE ? OR last_name LIKE ? OR email LIKE ? OR phone LIKE ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            pstmt.setString(5, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Member member = new Member(
                        rs.getInt("member_id"), rs.getString("enrollment_no"),
                        rs.getString("first_name"), rs.getString("last_name"),
                        rs.getString("email"), rs.getString("phone"),
                        rs.getString("gender"), rs.getString("address"),
                        rs.getString("status")
                    );
                    members.add(member);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }
}