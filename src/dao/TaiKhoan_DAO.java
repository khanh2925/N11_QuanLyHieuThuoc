/**
 * @author Quốc Khánh cute
 * @version 1.0
 * @since Oct 26, 2025
 *
 * Mô tả: Lớp này được tạo bởi Quốc Khánh vào ngày Oct 26, 2025.
 */
package dao;

import java.sql.*;
import java.util.ArrayList;
import connectDB.connectDB;
import entity.TaiKhoan;

public class TaiKhoan_DAO {

    public TaiKhoan_DAO() {}

    /**
     * Lấy toàn bộ danh sách tài khoản trong hệ thống.
     */
    public ArrayList<TaiKhoan> getAllTaiKhoan() {
        ArrayList<TaiKhoan> dsTK = new ArrayList<>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            connectDB.getInstance();
            con = connectDB.getConnection();
            if (con == null)
                throw new SQLException("Không thể kết nối cơ sở dữ liệu!");

            String sql = "SELECT MaTaiKhoan, TenDangNhap, MatKhau FROM TaiKhoan";
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String maTK = rs.getString("MaTaiKhoan");
                String tenDN = rs.getString("TenDangNhap");
                String matKhau = rs.getString("MatKhau");
                dsTK.add(new TaiKhoan(maTK, tenDN, matKhau));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return dsTK;
    }

    /**
     * Tạo mới một tài khoản.
     */
    public boolean createTaiKhoan(TaiKhoan tk) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmt = null;

        try {
            String sql = "INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau) VALUES (?, ?, ?)";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, tk.getMaTaiKhoan());
            stmt.setString(2, tk.getTenDangNhap());
            stmt.setString(3, tk.getMatKhau());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE"))
                System.err.println("❌ Lỗi: Tên đăng nhập đã tồn tại trong hệ thống!");
            else
                e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Cập nhật mật khẩu cho tài khoản.
     */
    public boolean updateMatKhau(String maTaiKhoan, String matKhauMoi) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmt = null;

        try {
            String sql = "UPDATE TaiKhoan SET MatKhau = ? WHERE MaTaiKhoan = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, matKhauMoi);
            stmt.setString(2, maTaiKhoan);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Xóa tài khoản theo mã.
     */
    public boolean deleteTaiKhoan(String maTaiKhoan) throws SQLException {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "DELETE FROM TaiKhoan WHERE MaTaiKhoan = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maTaiKhoan);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("FOREIGN KEY"))
                throw new RuntimeException("❌ Không thể xóa: tài khoản đang được gán cho nhân viên!");
            throw e;
        }
    }

    /**
     * Kiểm tra đăng nhập (login)
     * @return TaiKhoan nếu đúng, null nếu sai
     */
    public TaiKhoan login(String tenDangNhap, String matKhau) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ? AND MatKhau = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, tenDangNhap);
            stmt.setString(2, matKhau);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String maTK = rs.getString("MaTaiKhoan");
                String tenDN = rs.getString("TenDangNhap");
                String mk = rs.getString("MatKhau");
                return new TaiKhoan(maTK, tenDN, mk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Kiểm tra xem tên đăng nhập đã tồn tại chưa.
     */
    public boolean isUsernameExists(String tenDangNhap) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT 1 FROM TaiKhoan WHERE TenDangNhap = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, tenDangNhap);
            rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Lấy tài khoản theo mã (dùng cho NhanVien DAO khi join)
     */
    public TaiKhoan getTaiKhoanTheoMa(String maTaiKhoan) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM TaiKhoan WHERE MaTaiKhoan = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maTaiKhoan);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String tenDN = rs.getString("TenDangNhap");
                String mk = rs.getString("MatKhau");
                return new TaiKhoan(maTaiKhoan, tenDN, mk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
