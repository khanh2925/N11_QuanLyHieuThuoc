package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

import connectDB.connectDB;
import entity.KhachHang;

public class KhachHang_DAO {

    public KhachHang_DAO() {}

    /** Lấy toàn bộ khách hàng */
    public ArrayList<KhachHang> getAllKhachHang() {
        ArrayList<KhachHang> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaKhachHang, TenKhachHang, GioiTinh, SoDienThoai, NgaySinh FROM KhachHang";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String ma = rs.getString("MaKhachHang");
                String ten = rs.getString("TenKhachHang");
                boolean gt = rs.getBoolean("GioiTinh");
                String sdt = rs.getString("SoDienThoai");
                Date d = rs.getDate("NgaySinh");
                LocalDate ns = (d != null) ? d.toLocalDate() : null;

                ds.add(new KhachHang(ma, ten, gt, sdt, ns));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Thêm khách hàng */
    public boolean createKhachHang(KhachHang kh) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "INSERT INTO KhachHang (MaKhachHang, TenKhachHang, GioiTinh, SoDienThoai, NgaySinh) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, kh.getMaKhachHang());
            stmt.setString(2, kh.getTenKhachHang());
            stmt.setBoolean(3, kh.isGioiTinh());
            stmt.setString(4, kh.getSoDienThoai());
            stmt.setDate(5, kh.getNgaySinh() != null ? Date.valueOf(kh.getNgaySinh()) : null);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Cập nhật */
    public boolean updateKhachHang(KhachHang kh) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "UPDATE KhachHang SET TenKhachHang=?, GioiTinh=?, SoDienThoai=?, NgaySinh=? "
                   + "WHERE MaKhachHang=?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, kh.getTenKhachHang());
            stmt.setBoolean(2, kh.isGioiTinh());
            stmt.setString(3, kh.getSoDienThoai());
            stmt.setDate(4, kh.getNgaySinh() != null ? Date.valueOf(kh.getNgaySinh()) : null);
            stmt.setString(5, kh.getMaKhachHang());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Xóa */
    public boolean deleteKhachHang(String maKhachHang) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "DELETE FROM KhachHang WHERE MaKhachHang = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maKhachHang);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return false;
    }

    /**  Tìm theo mã */
    public KhachHang getKhachHangTheoMa(String maKhachHang) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaKhachHang, TenKhachHang, GioiTinh, SoDienThoai, NgaySinh "
                   + "FROM KhachHang WHERE MaKhachHang = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maKhachHang);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String ten = rs.getString("TenKhachHang");
                    boolean gt = rs.getBoolean("GioiTinh");
                    String sdt = rs.getString("SoDienThoai");
                    Date d = rs.getDate("NgaySinh");
                    LocalDate ns = (d != null) ? d.toLocalDate() : null;

                    return new KhachHang(maKhachHang, ten, gt, sdt, ns);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}