package dao;

import connectDB.connectDB;
import entity.NhaCungCap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NhaCungCap_DAO {
    private final Connection con;

    public NhaCungCap_DAO() {
        this.con = connectDB.getConnection();
    }

    public NhaCungCap findNhaCungCapById(String maNCC) {
        String sql = "SELECT * FROM NhaCungCap WHERE MaNhaCungCap = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maNCC);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String tenNCC = rs.getString("TenNhaCungCap");
                    String sdt = rs.getString("SoDienThoai");
                    String diaChi = rs.getString("DiaChi");
                    return new NhaCungCap(maNCC, tenNCC, sdt, diaChi);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}