package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import database.connectDB;
import entity.DonViTinh;

public class DonViTinh_DAO {

    public DonViTinh_DAO() {}

    /** 🔹 Lấy toàn bộ đơn vị tính */
    public List<DonViTinh> layTatCaDonViTinh() {
        List<DonViTinh> ds = new ArrayList<>();
        connectDB.getInstance();
        String sql = "SELECT MaDonViTinh, TenDonViTinh FROM DonViTinh ORDER BY MaDonViTinh";

        try (Connection con = connectDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(new DonViTinh(
                        rs.getString("MaDonViTinh"),
                        rs.getString("TenDonViTinh")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy danh sách đơn vị tính: " + e.getMessage());
        }
        return ds;
    }

    /** 🔹 Thêm đơn vị tính */
    public boolean themDonViTinh(DonViTinh dvt) {
        connectDB.getInstance();
        String sql = "INSERT INTO DonViTinh (MaDonViTinh, TenDonViTinh) VALUES (?, ?)";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dvt.getMaDonViTinh());
            ps.setString(2, dvt.getTenDonViTinh());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi thêm đơn vị tính: " + e.getMessage());
            return false;
        }
    }

    /** 🔹 Cập nhật tên đơn vị tính */
    public boolean capNhatDonViTinh(DonViTinh dvt) {
        connectDB.getInstance();
        String sql = "UPDATE DonViTinh SET TenDonViTinh=? WHERE MaDonViTinh=?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dvt.getTenDonViTinh());
            ps.setString(2, dvt.getMaDonViTinh());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi cập nhật đơn vị tính: " + e.getMessage());
            return false;
        }
    }

    /** 🔹 Xóa đơn vị tính */
    public boolean xoaDonViTinh(String maDonViTinh) {
        connectDB.getInstance();
        String sql = "DELETE FROM DonViTinh WHERE MaDonViTinh=?";
        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maDonViTinh);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("FK"))
                System.err.println("❌ Không thể xóa: Đơn vị tính đang được sử dụng trong sản phẩm khác!");
            else
                System.err.println("❌ Lỗi xóa đơn vị tính: " + e.getMessage());
            return false;
        }
    }
    /** 🔹 Tìm đơn vị tính theo mã */
    public DonViTinh timDonViTinhTheoMa(String maDonViTinh) {
        connectDB.getInstance();
        String sql = "SELECT MaDonViTinh, TenDonViTinh FROM DonViTinh WHERE MaDonViTinh = ?";
        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maDonViTinh);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new DonViTinh(
                        rs.getString("MaDonViTinh"),
                        rs.getString("TenDonViTinh")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi tìm đơn vị tính theo mã: " + e.getMessage());
        }
        return null;
    }

    /** 🔹 Sinh mã tự động theo định dạng DVT-xxx */
    public String taoMaTuDong() {
        connectDB.getInstance();
        String sql = "SELECT MAX(CAST(SUBSTRING(MaDonViTinh, 5, 3) AS INT)) AS SoCuoi FROM DonViTinh";
        try (Connection con = connectDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int so = 1;
            if (rs.next()) so = rs.getInt("SoCuoi") + 1;
            return String.format("DVT-%03d", so);
        } catch (SQLException e) {
            System.err.println("❌ Lỗi sinh mã tự động: " + e.getMessage());
            return "DVT-001";
        }
    }
}
