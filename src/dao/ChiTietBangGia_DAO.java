package dao;

import connectDB.connectDB;
import entity.BangGia;
import entity.ChiTietBangGia;
import entity.SanPham;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietBangGia_DAO {

    public ChiTietBangGia_DAO() {}

    /** 🔹 Lấy danh sách chi tiết bảng giá theo mã bảng giá */
    public List<ChiTietBangGia> layChiTietTheoMaBangGia(String maBangGia) {
        List<ChiTietBangGia> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "SELECT * FROM ChiTietBangGia WHERE MaBangGia = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBangGia);
            try (ResultSet rs = ps.executeQuery()) {
                BangGia_DAO bangGiaDAO = new BangGia_DAO();
                SanPham_DAO sanPhamDAO = new SanPham_DAO();

                while (rs.next()) {
                    BangGia bg = bangGiaDAO.timBangGiaTheoMa(maBangGia);
                    SanPham sp = sanPhamDAO.laySanPhamTheoMa(rs.getString("MaSanPham"));
                    double giaTu = rs.getDouble("GiaTu");
                    double giaDen = rs.getDouble("GiaDen");
                    double tiLe = rs.getDouble("TiLe");

                    if (bg != null && sp != null) {
                        ds.add(new ChiTietBangGia(bg, sp, giaTu, giaDen, tiLe));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy chi tiết bảng giá: " + e.getMessage());
        }
        return ds;
    }

    /** 🔹 Thêm chi tiết bảng giá mới */
    public boolean themChiTietBangGia(ChiTietBangGia ctbg) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = """
            INSERT INTO ChiTietBangGia (MaBangGia, MaSanPham, GiaTu, GiaDen, TiLe)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ctbg.getBangGia().getMaBangGia());
            ps.setString(2, ctbg.getSanPham().getMaSanPham());
            ps.setDouble(3, ctbg.getGiaTu());
            ps.setDouble(4, ctbg.getGiaDen());
            ps.setDouble(5, ctbg.getTiLe());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi thêm chi tiết bảng giá: " + e.getMessage());
        }
        return false;
    }

    /** 🔹 Cập nhật chi tiết bảng giá (sửa giá trị hoặc tỉ lệ) */
    public boolean capNhatChiTietBangGia(ChiTietBangGia ctbg) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = """
            UPDATE ChiTietBangGia
            SET GiaTu=?, GiaDen=?, TiLe=?
            WHERE MaBangGia=? AND MaSanPham=?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, ctbg.getGiaTu());
            ps.setDouble(2, ctbg.getGiaDen());
            ps.setDouble(3, ctbg.getTiLe());
            ps.setString(4, ctbg.getBangGia().getMaBangGia());
            ps.setString(5, ctbg.getSanPham().getMaSanPham());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi cập nhật chi tiết bảng giá: " + e.getMessage());
        }
        return false;
    }

    /** 🔹 Xóa chi tiết bảng giá (khi gỡ sản phẩm khỏi bảng giá) */
    public boolean xoaChiTietBangGia(String maBangGia, String maSanPham) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "DELETE FROM ChiTietBangGia WHERE MaBangGia=? AND MaSanPham=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBangGia);
            ps.setString(2, maSanPham);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi xóa chi tiết bảng giá: " + e.getMessage());
        }
        return false;
    }

    /** 🔹 Xóa toàn bộ chi tiết của 1 bảng giá (khi xóa bảng giá chính) */
    public boolean xoaChiTietTheoMaBangGia(String maBangGia) {
        connectDB.getInstance();
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
