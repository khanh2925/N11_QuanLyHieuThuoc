package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import connectDB.connectDB;
import entity.NhanVien;
import entity.TaiKhoan;

public class TaiKhoan_DAO {

    public TaiKhoan_DAO() {}
    
    /**
     * Lấy toàn bộ danh sách tài khoản trong hệ thống kèm thông tin Nhân viên.
     */
    public ArrayList<TaiKhoan> getAllTaiKhoan() {
        ArrayList<TaiKhoan> dsTK = new ArrayList<>();
        connectDB.getInstance();
        
        String sql = "SELECT tk.MaTaiKhoan, tk.TenDangNhap, tk.MatKhau, " +
                     "nv.MaNhanVien, nv.TenNhanVien, nv.GioiTinh, nv.NgaySinh, nv.SoDienThoai, nv.DiaChi, nv.QuanLy, nv.CaLam, nv.TrangThai " +
                     "FROM TaiKhoan tk JOIN NhanVien nv ON tk.MaNhanVien = nv.MaNhanVien";
        
        try (Connection con = connectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                NhanVien nv = new NhanVien(
                    rs.getString("MaNhanVien"), 
                    rs.getString("TenNhanVien"), 
                    rs.getBoolean("GioiTinh"),
                    (rs.getDate("NgaySinh") != null) ? rs.getDate("NgaySinh").toLocalDate() : null,
                    rs.getString("SoDienThoai"), 
                    rs.getString("DiaChi"),
                    rs.getBoolean("QuanLy"), 
                    rs.getString("CaLam"), 
                    rs.getBoolean("TrangThai")
                );
                
                TaiKhoan tk = new TaiKhoan(
                    rs.getString("MaTaiKhoan"), 
                    rs.getString("TenDangNhap"), 
                    rs.getString("MatKhau"), 
                    nv
                );
                dsTK.add(tk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsTK;
    }

    /**
     * Tạo mới một tài khoản. (Giữ nguyên)
     */
    public boolean createTaiKhoan(TaiKhoan tk) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmt = null;
        try {
            String sql = "INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau, MaNhanVien) VALUES (?, ?, ?, ?)";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, tk.getMaTaiKhoan());
            stmt.setString(2, tk.getTenDangNhap());
            stmt.setString(3, tk.getMatKhau());
            stmt.setString(4, tk.getNhanVien().getMaNhanVien());
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
     * Cập nhật mật khẩu cho tài khoản. (Giữ nguyên)
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
     * Xóa tài khoản theo mã. (Giữ nguyên)
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

    @Deprecated
    public TaiKhoan login(String tenDangNhap, String matKhau) {
        return dangNhap(tenDangNhap, matKhau);
    }
    
    /**
     * Kiểm tra đăng nhập (login)
     * @return TaiKhoan kèm NhanVien nếu đúng, null nếu sai
     */
    public TaiKhoan dangNhap(String tenDangNhap, String matKhau) {
        TaiKhoan tk = null;
        String sql = "SELECT tk.MaTaiKhoan, tk.TenDangNhap, tk.MatKhau, " +
                     "nv.MaNhanVien, nv.TenNhanVien, nv.GioiTinh, nv.NgaySinh, nv.SoDienThoai, nv.DiaChi, nv.QuanLy, nv.CaLam, nv.TrangThai " +
                     "FROM TaiKhoan tk JOIN NhanVien nv ON tk.MaNhanVien = nv.MaNhanVien " +
                     "WHERE tk.TenDangNhap=? AND tk.MatKhau=?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tenDangNhap);
            ps.setString(2, matKhau);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    NhanVien nv = new NhanVien(
                        rs.getString("MaNhanVien"),
                        rs.getString("TenNhanVien"),
                        rs.getBoolean("GioiTinh"),
                        (rs.getDate("NgaySinh") != null) ? rs.getDate("NgaySinh").toLocalDate() : null,
                        rs.getString("SoDienThoai"),
                        rs.getString("DiaChi"),
                        rs.getBoolean("QuanLy"),
                        rs.getString("CaLam"),
                        rs.getBoolean("TrangThai")
                    );

                    tk = new TaiKhoan(
                        rs.getString("MaTaiKhoan"),
                        rs.getString("TenDangNhap"),
                        rs.getString("MatKhau"),
                        nv
                    );
                    System.out.println(tk);
                }

            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi đăng nhập (TaiKhoan_DAO): " + e.getMessage());
        }
        return tk;
    }

    /**
     * Kiểm tra xem tên đăng nhập đã tồn tại chưa. (Giữ nguyên)
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
     * Lấy tài khoản theo mã kèm thông tin Nhân viên.
     */
    public TaiKhoan getTaiKhoanTheoMa(String maTaiKhoan) {
        TaiKhoan tk = null;
        
        String sql = "SELECT tk.MaTaiKhoan, tk.TenDangNhap, tk.MatKhau, " +
                     "nv.MaNhanVien, nv.TenNhanVien, nv.GioiTinh, nv.NgaySinh, nv.SoDienThoai, nv.DiaChi, nv.QuanLy, nv.CaLam, nv.TrangThai " +
                     "FROM TaiKhoan tk JOIN NhanVien nv ON tk.MaNhanVien = nv.MaNhanVien WHERE tk.MaTaiKhoan = ?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maTaiKhoan);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    NhanVien nv = new NhanVien(
                        rs.getString("MaNhanVien"), 
                        rs.getString("TenNhanVien"), 
                        rs.getBoolean("GioiTinh"),
                        (rs.getDate("NgaySinh") != null) ? rs.getDate("NgaySinh").toLocalDate() : null,
                        rs.getString("SoDienThoai"), 
                        rs.getString("DiaChi"),
                        rs.getBoolean("QuanLy"), 
                        rs.getString("CaLam"), 
                        rs.getBoolean("TrangThai")
                    );
                    
                    tk = new TaiKhoan(
                        rs.getString("MaTaiKhoan"), 
                        rs.getString("TenDangNhap"), 
                        rs.getString("MatKhau"), 
                        nv
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tk;
    }
    
    /**
     * Cập nhật Tên đăng nhập và Mật khẩu
     */
    public boolean updateTaiKhoan(TaiKhoan tk) {
        if (tk == null || tk.getMaTaiKhoan() == null) {
            System.err.println("❌ Lỗi cập nhật: Đối tượng tài khoản hoặc mã tài khoản là null.");
            return false;
        }
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "UPDATE TaiKhoan SET TenDangNhap = ?, MatKhau = ? WHERE MaTaiKhoan = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, tk.getTenDangNhap());
            stmt.setString(2, tk.getMatKhau()); // Mật khẩu đã được cập nhật trong dialog
            stmt.setString(3, tk.getMaTaiKhoan());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                 System.err.println("❌ Lỗi: Tên đăng nhập '" + tk.getTenDangNhap() + "' đã tồn tại!");
            } else {
                System.err.println("❌ Lỗi cập nhật tài khoản: " + e.getMessage());
            }
            return false;
        }
    }
}