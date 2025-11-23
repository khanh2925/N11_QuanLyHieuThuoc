package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connectDB.connectDB;
import entity.NhaCungCap;

public class NhaCungCap_DAO {

    public NhaCungCap_DAO() {}

    /** 🔹 Lấy toàn bộ nhà cung cấp */
    public List<NhaCungCap> layTatCaNhaCungCap() {
        List<NhaCungCap> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = """
            SELECT MaNhaCungCap, TenNhaCungCap, SoDienThoai, DiaChi, Email, HoatDong
            FROM NhaCungCap
            ORDER BY MaNhaCungCap
        """;

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                NhaCungCap ncc = new NhaCungCap(
                        rs.getString("MaNhaCungCap"),
                        rs.getString("TenNhaCungCap"),
                        rs.getString("SoDienThoai"),
                        rs.getString("DiaChi"),
                        rs.getString("Email")
                );
                ncc.setHoatDong(rs.getBoolean("HoatDong"));
                ds.add(ncc);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy danh sách nhà cung cấp: " + e.getMessage());
        }
        return ds;
    }

    /** 🔹 Thêm nhà cung cấp mới */
    public boolean themNhaCungCap(NhaCungCap ncc) {
        connectDB.getInstance();
        String sql = """
            INSERT INTO NhaCungCap (MaNhaCungCap, TenNhaCungCap, SoDienThoai, DiaChi, Email, HoatDong)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ncc.getMaNhaCungCap());
            ps.setString(2, ncc.getTenNhaCungCap());
            ps.setString(3, ncc.getSoDienThoai());
            ps.setString(4, ncc.getDiaChi());
            ps.setString(5, ncc.getEmail());
            ps.setBoolean(6, ncc.isHoatDong());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi thêm nhà cung cấp: " + e.getMessage());
            return false;
        }
    }

    /** 🔹 Cập nhật nhà cung cấp */
    public boolean capNhatNhaCungCap(NhaCungCap ncc) {
        connectDB.getInstance();
        String sql = """
            UPDATE NhaCungCap
            SET TenNhaCungCap=?, SoDienThoai=?, DiaChi=?, Email=?, HoatDong=?
            WHERE MaNhaCungCap=?
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ncc.getTenNhaCungCap());
            ps.setString(2, ncc.getSoDienThoai());
            ps.setString(3, ncc.getDiaChi());
            ps.setString(4, ncc.getEmail());
            ps.setBoolean(5, ncc.isHoatDong());
            ps.setString(6, ncc.getMaNhaCungCap());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi cập nhật nhà cung cấp: " + e.getMessage());
        }
        return false;
    }

    /** 🔹 Sinh mã tự động NCC-yyyyMMdd-xxxx */
    public String taoMaTuDong() {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = """
            SELECT MAX(RIGHT(MaNhaCungCap, 4)) AS SoCuoi
            FROM NhaCungCap
            WHERE MaNhaCungCap LIKE 'NCC-%'
        """;
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int so = 1;
            if (rs.next()) so = rs.getInt("SoCuoi") + 1;

            String ngay = java.time.LocalDate.now().toString().replaceAll("-", "");
            return String.format("NCC-%s-%04d", ngay, so);
        } catch (SQLException e) {
            System.err.println("❌ Lỗi sinh mã nhà cung cấp: " + e.getMessage());
            return "NCC-" + System.currentTimeMillis();
        }
    }

    public NhaCungCap timNhaCungCapTheoMaHoacSDT(String keyword) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        
        // Tìm kiếm chính xác theo Mã hoặc SĐT
        String sql = "SELECT * FROM NhaCungCap WHERE MaNhaCungCap = ? OR SoDienThoai = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, keyword);
            stmt.setString(2, keyword);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    NhaCungCap ncc = new NhaCungCap(
                            rs.getString("MaNhaCungCap"),
                            rs.getString("TenNhaCungCap"),
                            rs.getString("SoDienThoai"),
                            rs.getString("DiaChi"),
                            rs.getString("Email")
                    );
                    ncc.setHoatDong(rs.getBoolean("HoatDong"));
                    return ncc;
                }
            }
        } catch (Exception e) {
            // Bắt Exception chung (bao gồm cả SQLException và IllegalArgumentException)
            System.err.println("❌ Lỗi timNhaCungCapTheoMaHoacSDT: " + e.getMessage());
        }
        return null; // không tìm thấy
    }
}
