package dao;

import connectDB.connectDB;
import entity.NhanVien;

import java.sql.*;

public class NhanVien_DAO {

    /** Tìm nhân viên theo mã (ví dụ NV2025010001) */
    public NhanVien findById(String maNhanVien) throws SQLException {
        NhanVien nv = null;
        Connection con = connectDB.getConnection();

        // Dùng đúng tên cột như trong ảnh bạn chụp
        final String sql =
            "SELECT MaNhanVien, TenNhanVien, GioiTinh, NgaySinh, SoDienThoai, DiaChi, TrangThai " +
            "FROM dbo.NhanVien WHERE MaNhanVien = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nv = new NhanVien();
                    nv.setMaNhanVien(rs.getString("MaNhanVien"));
                    nv.setTenNhanVien(rs.getString("TenNhanVien"));

                    // GioiTinh: BIT -> boolean -> Boolean
                    boolean gt = rs.getBoolean("GioiTinh"); // 0/1 trong SQL Server
                    nv.setGioiTinh(gt);

                    Date ns = rs.getDate("NgaySinh");
                    if (ns != null) nv.setNgaySinh(ns.toLocalDate());

                    nv.setSoDienThoai(rs.getString("SoDienThoai"));
                    nv.setDiaChi(rs.getString("DiaChi"));

                    // Nếu entity có setter TrangThai, mở dòng dưới:
                    // nv.setTrangThai(rs.getBoolean("TrangThai"));
                }
            }
        }
        return nv;
    }
}
