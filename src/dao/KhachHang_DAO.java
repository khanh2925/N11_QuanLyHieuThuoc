package dao;

import connectDB.connectDB;
import entity.KhachHang;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KhachHang_DAO {
    private final Connection con;

    public KhachHang_DAO() {
        this.con = connectDB.getConnection();
    }

    public KhachHang timKiemKhachHangBangMa(String maKH) {
        if (maKH == null || maKH.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT * FROM KhachHang WHERE MaKhachHang = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maKH);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String tenKH = rs.getString("TenKhachHang");
                    String sdt = rs.getString("SoDienThoai");
                    String diaChi = rs.getString("DiaChi");
                    int diem = rs.getInt("DiemTichLuy");
                    boolean gioiTinh = rs.getBoolean("GioiTinh");
                    return new KhachHang(maKH, tenKH, gioiTinh, diaChi, null, diem);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}