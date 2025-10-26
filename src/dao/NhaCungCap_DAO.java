package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connectDB.connectDB;
import entity.NhaCungCap;

public class NhaCungCap_DAO {

    public NhaCungCap_DAO() {}

    /** Lấy toàn bộ nhà cung cấp */
    public ArrayList<NhaCungCap> getAllNhaCungCap() {
        ArrayList<NhaCungCap> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaNhaCungCap, TenNhaCungCap, SoDienThoai, DiaChi FROM NhaCungCap";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String ma = rs.getString("MaNhaCungCap");
                String ten = rs.getString("TenNhaCungCap");
                String sdt = rs.getString("SoDienThoai");
                String diaChi = rs.getString("DiaChi");
                ds.add(new NhaCungCap(ma, ten, sdt, diaChi));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Thêm nhà cung cấp */
    public boolean createNhaCungCap(NhaCungCap ncc) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "INSERT INTO NhaCungCap (MaNhaCungCap, TenNhaCungCap, SoDienThoai, DiaChi) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, ncc.getMaNhaCungCap());
            stmt.setString(2, ncc.getTenNhaCungCap());
            stmt.setString(3, ncc.getSoDienThoai());
            stmt.setString(4, ncc.getDiaChi());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Cập nhật nhà cung cấp  */
    public boolean updateNhaCungCap(NhaCungCap ncc) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "UPDATE NhaCungCap SET TenNhaCungCap = ?, SoDienThoai = ?, DiaChi = ? WHERE MaNhaCungCap = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, ncc.getTenNhaCungCap());
            stmt.setString(2, ncc.getSoDienThoai());
            stmt.setString(3, ncc.getDiaChi());
            stmt.setString(4, ncc.getMaNhaCungCap());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Tìm kiếm nhà cung cấp theo mã  */
    public NhaCungCap getNhaCungCapTheoMa(String maNCC) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaNhaCungCap, TenNhaCungCap, SoDienThoai, DiaChi FROM NhaCungCap WHERE MaNhaCungCap = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maNCC);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String ten = rs.getString("TenNhaCungCap");
                    String sdt = rs.getString("SoDienThoai");
                    String diaChi = rs.getString("DiaChi");
                    return new NhaCungCap(maNCC, ten, sdt, diaChi);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // không tìm thấy
    }

   
    /** Xóa nhà cung cấp theo mã */
    public boolean deleteNhaCungCap(String maNCC) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "DELETE FROM NhaCungCap WHERE MaNhaCungCap = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maNCC);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return false;
    }
    


}
