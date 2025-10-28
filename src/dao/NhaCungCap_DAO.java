package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connectDB.connectDB;
import entity.NhaCungCap;

public class NhaCungCap_DAO {

    public NhaCungCap_DAO() {}

    /** Lấy toàn bộ nhà cung cấp */
    public ArrayList<NhaCungCap> layTatCaNhaCungCap() { // Đổi tên phương thức
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
                // Sử dụng constructor đầy đủ
                ds.add(new NhaCungCap(ma, ten, sdt, diaChi));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
             e.printStackTrace(); // Lỗi từ constructor NhaCungCap
        }
        return ds;
    }

    /** Thêm nhà cung cấp */
    public boolean themNhaCungCap(NhaCungCap ncc) { // Đổi tên phương thức
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
             System.err.println("Lỗi khi thêm nhà cung cấp (có thể do trùng mã): " + e.getMessage());
            // e.printStackTrace();
        }
        return false;
    }

    /** Cập nhật nhà cung cấp  */
    public boolean capNhatNhaCungCap(NhaCungCap ncc) { // Đổi tên phương thức
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
    public NhaCungCap timNhaCungCapTheoMa(String maNCC) { // Đổi tên phương thức
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT TenNhaCungCap, SoDienThoai, DiaChi FROM NhaCungCap WHERE MaNhaCungCap = ?"; // Bỏ MaNCC khỏi SELECT

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
        } catch (IllegalArgumentException e) {
             e.printStackTrace(); // Lỗi từ constructor NhaCungCap
        }
        return null; // không tìm thấy
    }

    // *** THÊM: Tìm theo SĐT ***
     /** Tìm kiếm nhà cung cấp theo số điện thoại (chính xác) */
    public NhaCungCap timNhaCungCapTheoSDT(String soDienThoai) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        NhaCungCap ncc = null;
        String sql = "SELECT MaNhaCungCap, TenNhaCungCap, DiaChi FROM NhaCungCap WHERE SoDienThoai = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, soDienThoai);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String ma = rs.getString("MaNhaCungCap");
                    String ten = rs.getString("TenNhaCungCap");
                    String diaChi = rs.getString("DiaChi");
                    ncc = new NhaCungCap(ma, ten, soDienThoai, diaChi);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
             e.printStackTrace();
        }
        return ncc;
    }
     // *** THÊM: Tìm theo Mã hoặc SĐT ***
     /**
     * Tìm kiếm nhà cung cấp theo Mã hoặc Số Điện Thoại.
     * Ưu tiên tìm theo Mã trước.
     * @param keyword Mã NCC (NCC-xxx) hoặc SĐT (10 số bắt đầu bằng 0)
     * @return NhaCungCap nếu tìm thấy, null nếu không.
     */
     public NhaCungCap timNhaCungCapTheoMaHoacSDT(String keyword) {
         if (keyword == null || keyword.trim().isEmpty()) {
             return null;
         }
         keyword = keyword.trim();

         // Thử tìm theo mã trước
         NhaCungCap ncc = timNhaCungCapTheoMa(keyword);
         if (ncc != null) {
             return ncc;
         }

         // Nếu không thấy theo mã, thử tìm theo SĐT
         // Chỉ tìm theo SĐT nếu keyword trông giống SĐT
         if (keyword.matches("^0\\d{9}$")) {
              ncc = timNhaCungCapTheoSDT(keyword);
              if (ncc != null) {
                 return ncc;
              }
         }

         // Không tìm thấy theo cả hai cách
         return null;
     }

    /** Xóa nhà cung cấp theo mã */
    public boolean xoaNhaCungCap(String maNCC) { 
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "DELETE FROM NhaCungCap WHERE MaNhaCungCap = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maNCC);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
             System.err.println("Lỗi khi xóa nhà cung cấp (có thể đang được tham chiếu): " + e.getMessage());
            // e.printStackTrace();
        }
        return false;
    }
}
