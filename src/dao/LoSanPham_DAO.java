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
    public ArrayList<LoSanPham> layTatCaLoSanPham() {
        ArrayList<LoSanPham> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaLo, HanSuDung, SoLuongNhap, SoLuongTon, MaSanPham FROM LoSanPham";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String maLo = rs.getString("MaLo");
                LocalDate hsd = rs.getDate("HanSuDung").toLocalDate();
                int soLuongNhap = rs.getInt("SoLuongNhap");
                int soLuongTon = rs.getInt("SoLuongTon");
                String maSP = rs.getString("MaSanPham");

                SanPham sp = new SanPham();
                try { sp.setMaSanPham(maSP); } catch (IllegalArgumentException ignore) {}

                LoSanPham lo = new LoSanPham(maLo, hsd, soLuongNhap, soLuongTon, sp);
                ds.add(lo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Thêm lô sản phẩm */
    public boolean themLoSanPham(LoSanPham lo) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "INSERT INTO LoSanPham (MaLo, HanSuDung, SoLuongNhap, SoLuongTon, MaSanPham) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, lo.getMaLo());
            ps.setDate(2, Date.valueOf(lo.getHanSuDung()));
            ps.setInt(3, lo.getSoLuongNhap()); 
            ps.setInt(4, lo.getSoLuongTon());   
            ps.setString(5, lo.getSanPham() != null ? lo.getSanPham().getMaSanPham() : null);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Cập nhật lô sản phẩm (theo MaLo) */
    public boolean capNhatLoSanPham(LoSanPham lo) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "UPDATE LoSanPham SET HanSuDung = ?, SoLuongNhap = ?, SoLuongTon = ?, MaSanPham = ? "
                   + "WHERE MaLo = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(lo.getHanSuDung()));
            ps.setInt(2, lo.getSoLuongNhap()); // Thêm SoLuongNhap
            ps.setInt(3, lo.getSoLuongTon());
            ps.setString(4, lo.getSanPham() != null ? lo.getSanPham().getMaSanPham() : null);
            ps.setString(5, lo.getMaLo());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Xóa lô sản phẩm theo mã */
    public boolean xoaLoSanPham(String maLo) {
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
    public LoSanPham timLoSanPhamTheoMa(String maLo) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaLo, HanSuDung, SoLuongNhap, SoLuongTon, MaSanPham "
                   + "FROM LoSanPham WHERE MaLo = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maLo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LocalDate hsd = rs.getDate("HanSuDung").toLocalDate();
                    // SỬA LỖI 2: Đọc cả hai cột
                    int soLuongNhap = rs.getInt("SoLuongNhap");
                    int soLuongTon = rs.getInt("SoLuongTon");
                    String maSP = rs.getString("MaSanPham");

                    SanPham sp = new SanPham();
                    try { sp.setMaSanPham(maSP); } catch (IllegalArgumentException ignore) {}

                    return new LoSanPham(maLo, hsd, soLuongNhap, soLuongTon, sp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}