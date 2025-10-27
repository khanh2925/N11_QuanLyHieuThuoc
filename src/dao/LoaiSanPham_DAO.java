package dao;

import java.sql.*;
import java.util.ArrayList;
import connectDB.connectDB;
import entity.LoaiSanPham;

public class LoaiSanPham_DAO {

    public LoaiSanPham_DAO() {}


    public ArrayList<LoaiSanPham> getAllLoaiSanPham() {
        ArrayList<LoaiSanPham> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaLoaiSanPham, TenLoaiSanPham, MoTa FROM LoaiSanPham";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String ma = rs.getString("MaLoaiSanPham");
                String ten = rs.getString("TenLoaiSanPham");
                String moTa = rs.getString("MoTa");
                ds.add(new LoaiSanPham(ma, ten, moTa));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Tìm theo mã */
    public LoaiSanPham getLoaiSanPhamTheoMa(String maLSP) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaLoaiSanPham, TenLoaiSanPham, MoTa FROM LoaiSanPham WHERE MaLoaiSanPham = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maLSP);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String ten = rs.getString("TenLoaiSanPham");
                    String moTa = rs.getString("MoTa");
                    return new LoaiSanPham(maLSP, ten, moTa);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Thêm */
    public boolean createLoaiSanPham(LoaiSanPham lsp) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "INSERT INTO LoaiSanPham VALUES (?, ?, ?)";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, lsp.getMaLoaiSanPham());
            stmt.setString(2, lsp.getTenLoaiSanPham());
            stmt.setString(3, lsp.getMoTa());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /** Sửa */
    public boolean updateLoaiSanPham(LoaiSanPham lsp) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "UPDATE LoaiSanPham SET TenLoaiSanPham = ?, MoTa = ? WHERE MaLoaiSanPham = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, lsp.getTenLoaiSanPham());
            stmt.setString(2, lsp.getMoTa());
            stmt.setString(3, lsp.getMaLoaiSanPham());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /** Xóa */
    public boolean deleteLoaiSanPham(String maLSP) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "DELETE FROM LoaiSanPham WHERE MaLoaiSanPham = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maLSP);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
