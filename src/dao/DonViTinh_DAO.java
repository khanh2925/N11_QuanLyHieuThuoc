package dao;

import java.sql.*;
import java.util.ArrayList;
import connectDB.connectDB;
import entity.DonViTinh;

public class DonViTinh_DAO {

    public DonViTinh_DAO() {}

    public ArrayList<DonViTinh> getAllDonViTinh() {
        ArrayList<DonViTinh> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaDonViTinh, TenDonViTinh, MoTa FROM DonViTinh";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String ma = rs.getString("MaDonViTinh");
                String ten = rs.getString("TenDonViTinh");
                String moTa = rs.getString("MoTa");
                ds.add(new DonViTinh(ma, ten, moTa));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Tìm theo mã */
    public DonViTinh getDonViTinhTheoMa(String maDVT) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaDonViTinh, TenDonViTinh, MoTa FROM DonViTinh WHERE MaDonViTinh = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maDVT);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String ten = rs.getString("TenDonViTinh");
                    String moTa = rs.getString("MoTa");
                    return new DonViTinh(maDVT, ten, moTa);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Thêm */
    public boolean createDonViTinh(DonViTinh dvt) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "INSERT INTO DonViTinh VALUES (?, ?, ?)";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, dvt.getMaDonViTinh());
            stmt.setString(2, dvt.getTenDonViTinh());
            stmt.setString(3, dvt.getMoTa());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Sửa */
    public boolean updateDonViTinh(DonViTinh dvt) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "UPDATE DonViTinh SET TenDonViTinh = ?, MoTa = ? WHERE MaDonViTinh = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, dvt.getTenDonViTinh());
            stmt.setString(2, dvt.getMoTa());
            stmt.setString(3, dvt.getMaDonViTinh());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Xóa */
    public boolean deleteDonViTinh(String maDVT) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "DELETE FROM DonViTinh WHERE MaDonViTinh = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maDVT);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
