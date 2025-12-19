package dao;

import database.connectDB;
import entity.BangGia;
import entity.ChiTietBangGia;
import entity.SanPham;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietBangGia_DAO {

    public ChiTietBangGia_DAO() {
    }

    /**
     * 🔹 Lấy danh sách chi tiết bảng giá theo mã bảng giá (OPTIMIZED - tránh gọi
     * DAO lồng)
     */
    public List<ChiTietBangGia> layChiTietTheoMaBangGia(String maBangGia) {
        List<ChiTietBangGia> ds = new ArrayList<>();

        Connection con = connectDB.getConnection();
        String sql = "SELECT MaBangGia, GiaTu, GiaDen, TiLe FROM ChiTietBangGia WHERE MaBangGia = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBangGia);
            try (ResultSet rs = ps.executeQuery()) {
                // ✅ OPTIMIZED: Tạo BangGia object trực tiếp từ mã, tránh gọi DAO lồng
                BangGia bg = new BangGia(maBangGia);

                while (rs.next()) {
                    double giaTu = rs.getDouble("GiaTu");
                    double giaDen = rs.getDouble("GiaDen");
                    double tiLe = rs.getDouble("TiLe");

                    // Dùng constructor không có SanPham
                    ds.add(new ChiTietBangGia(bg, giaTu, giaDen, tiLe));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy chi tiết bảng giá: " + e.getMessage());
        }
        return ds;
    }

    /**
     * 🔹 Lấy chi tiết bảng giá theo Khoảng giá (MỚI - Dùng để tìm tỉ lệ cho
     * SanPham)
     */
    public ChiTietBangGia timChiTietTheoKhoangGia(String maBangGia, double giaNhap) {

        Connection con = connectDB.getConnection();
        // SQL: Tìm kiếm tỉ lệ áp dụng nếu gia nhập nằm trong khoảng [GiaTu, GiaDen]
        String sql = "SELECT GiaTu, GiaDen, TiLe FROM ChiTietBangGia WHERE MaBangGia = ? AND ? BETWEEN GiaTu AND GiaDen";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBangGia);
            ps.setDouble(2, giaNhap);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BangGia bg = new BangGia(maBangGia);

                    double giaTu = rs.getDouble("GiaTu");
                    double giaDen = rs.getDouble("GiaDen");
                    double tiLe = rs.getDouble("TiLe");

                    // Trả về ChiTietBangGia với tỉ lệ tương ứng
                    return new ChiTietBangGia(bg, giaTu, giaDen, tiLe);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi tìm chi tiết bảng giá theo khoảng giá: " + e.getMessage());
        }
        return null;
    }

    /** 🔹 Thêm chi tiết bảng giá mới (ĐÃ SỬA) */
    public boolean themChiTietBangGia(ChiTietBangGia ctbg) {

        Connection con = connectDB.getConnection();
        // ĐÃ SỬA: Loại bỏ MaSanPham
        String sql = """
                    INSERT INTO ChiTietBangGia (MaBangGia, GiaTu, GiaDen, TiLe)
                    VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ctbg.getBangGia().getMaBangGia());
            ps.setDouble(2, ctbg.getGiaTu());
            ps.setDouble(3, ctbg.getGiaDen());
            ps.setDouble(4, ctbg.getTiLe());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi thêm chi tiết bảng giá: " + e.getMessage());
        }
        return false;
    }

    /** 🔹 Cập nhật chi tiết bảng giá (sửa giá trị hoặc tỉ lệ) (ĐÃ SỬA) */
    public boolean capNhatChiTietBangGia(ChiTietBangGia ctbg, double giaTuCu, double giaDenCu) {

        Connection con = connectDB.getConnection();
        // Dùng GiaTuCu và GiaDenCu để định danh bản ghi
        String sql = """
                    UPDATE ChiTietBangGia
                    SET GiaTu=?, GiaDen=?, TiLe=?
                    WHERE MaBangGia=? AND GiaTu=? AND GiaDen=?
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, ctbg.getGiaTu());
            ps.setDouble(2, ctbg.getGiaDen());
            ps.setDouble(3, ctbg.getTiLe());
            ps.setString(4, ctbg.getBangGia().getMaBangGia());
            ps.setDouble(5, giaTuCu); // Dùng GiaTu cũ để tìm bản ghi
            ps.setDouble(6, giaDenCu); // Dùng GiaDen cũ để tìm bản ghi
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi cập nhật chi tiết bảng giá: " + e.getMessage());
        }
        return false;
    }

    /** 🔹 Xóa chi tiết bảng giá (Theo khoảng giá) (ĐÃ SỬA) */
    public boolean xoaChiTietBangGia(String maBangGia, double giaTu, double giaDen) {

        Connection con = connectDB.getConnection();
        // Xóa dựa trên MaBangGia và Khoảng giá
        String sql = "DELETE FROM ChiTietBangGia WHERE MaBangGia=? AND GiaTu=? AND GiaDen=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBangGia);
            ps.setDouble(2, giaTu);
            ps.setDouble(3, giaDen);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi xóa chi tiết bảng giá: " + e.getMessage());
        }
        return false;
    }

    /**
     * 🔹 Xóa toàn bộ chi tiết của 1 bảng giá (khi xóa bảng giá chính) (Giữ nguyên)
     */
    public boolean xoaChiTietTheoMaBangGia(String maBangGia) {

        Connection con = connectDB.getConnection();
        String sql = "DELETE FROM ChiTietBangGia WHERE MaBangGia=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBangGia);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi xóa chi tiết theo mã bảng giá: " + e.getMessage());
        }
        return false;
    }
}