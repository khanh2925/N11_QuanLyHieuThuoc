package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;
import java.util.ArrayList;

import connectDB.connectDB;
import entity.LoSanPham;
import entity.SanPham;

public class LoSanPham_DAO {

    public LoSanPham_DAO() {}

    /** Lấy toàn bộ lô sản phẩm */
    public ArrayList<LoSanPham> getAllLoSanPham() {
        ArrayList<LoSanPham> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaLo, HanSuDung, SoLuongTon, MaSanPham FROM LoSanPham";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String maLo = rs.getString("MaLo");
                LocalDate hsd = rs.getDate("HanSuDung").toLocalDate();
                int soLuong = rs.getInt("SoLuong");
                String maSP = rs.getString("MaSanPham");

                // Tạo SanPham tối thiểu (chỉ set mã)
                SanPham sp = new SanPham();
                try { sp.setMaSanPham(maSP); } catch (IllegalArgumentException ignore) {}

                LoSanPham lo = new LoSanPham(maLo, hsd, soLuong, sp);
                ds.add(lo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Thêm lô sản phẩm */
    public boolean createLoSanPham(LoSanPham lo) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "INSERT INTO LoSanPham (MaLo, HanSuDung, SoLuongTon, MaSanPham) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, lo.getMaLo());
            ps.setDate(2, Date.valueOf(lo.getHanSuDung()));
            ps.setInt(3, lo.getSoLuongTon());
            ps.setString(4, lo.getSanPham() != null ? lo.getSanPham().getMaSanPham() : null);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Cập nhật lô sản phẩm (theo MaLo) */
    public boolean updateLoSanPham(LoSanPham lo) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "UPDATE LoSanPham SET HanSuDung = ?, SoLuongTon = ?, MaSanPham = ? "
                   + "WHERE MaLo = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(lo.getHanSuDung()));
            ps.setInt(2, lo.getSoLuongTon());
            ps.setString(3, lo.getSanPham() != null ? lo.getSanPham().getMaSanPham() : null);
            ps.setString(4, lo.getMaLo());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Xóa lô sản phẩm theo mã */
    public boolean deleteLoSanPham(String maLo) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "DELETE FROM LoSanPham WHERE MaLo = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maLo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return false;
    }

    /** Lấy 1 lô sản phẩm theo mã lô (chính xác) */
    public LoSanPham getLoSanPhamTheoMa(String maLo) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaLo, HanSuDung, SoLuongTon, MaSanPham "
                   + "FROM LoSanPham WHERE MaLo = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maLo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LocalDate hsd = rs.getDate("HanSuDung").toLocalDate();
                    int soLuong = rs.getInt("SoLuongTon");
                    String maSP = rs.getString("MaSanPham");

                    SanPham sp = new SanPham();
                    try { sp.setMaSanPham(maSP); } catch (IllegalArgumentException ignore) {}

                    return new LoSanPham(maLo, hsd, soLuong, sp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}