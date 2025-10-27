package dao;

import connectDB.connectDB;
import entity.KhuyenMai;
import enums.HinhThucKM;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMai_DAO {
    
    public KhuyenMai_DAO() {
    }

    /**
     * Tìm kiếm một chương trình khuyến mãi dựa vào mã.
     * @param maKM Mã khuyến mãi cần tìm.
     * @return Đối tượng KhuyenMai nếu tìm thấy, null nếu không.
     */
    public KhuyenMai timKhuyenMaiTheoMa(String maKM) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connectDB.getInstance();
            con = connectDB.getConnection();
            
            String sql = "SELECT * FROM KhuyenMai WHERE MaKM = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maKM);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                String tenKM = rs.getString("TenKM");
                LocalDate ngayBatDau = rs.getDate("NgayBatDau").toLocalDate();
                LocalDate ngayKetThuc = rs.getDate("NgayKetThuc").toLocalDate();
                boolean trangThai = rs.getBoolean("TrangThai");
                boolean kmHoaDon = rs.getBoolean("KhuyenMaiHoaDon");
                HinhThucKM hinhThuc = HinhThucKM.valueOf(rs.getString("HinhThucKM"));
                double giaTri = rs.getDouble("GiaTri");
                String dieuKien = rs.getString("DieuKienApDungHoaDon");
                int slToiThieu = rs.getInt("SoLuongToiThieu");
                int slTangThem = rs.getInt("SoLuongTangThem");

                return new KhuyenMai(maKM, tenKM, ngayBatDau, ngayKetThuc, trangThai, kmHoaDon,
                        hinhThuc, giaTri, dieuKien, slToiThieu, slTangThem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Lấy tất cả các chương trình khuyến mãi.
     * @return Danh sách các đối tượng KhuyenMai.
     */
    public List<KhuyenMai> layTatCaKhuyenMai() {
        List<KhuyenMai> ds = new ArrayList<>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            connectDB.getInstance();
            con = connectDB.getConnection();

            String sql = "SELECT MaKM FROM KhuyenMai ORDER BY NgayBatDau DESC";
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                // Tái sử dụng phương thức tìm theo mã để tránh lặp code
                KhuyenMai km = timKhuyenMaiTheoMa(rs.getString("MaKM"));
                if (km != null) {
                    ds.add(km);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ds;
    }

    /**
     * Thêm một chương trình khuyến mãi mới.
     * @param km Đối tượng KhuyenMai cần thêm.
     * @return true nếu thành công, false nếu thất bại.
     */
    public boolean themKhuyenMai(KhuyenMai km) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmt = null;

        try {
            String sql = "INSERT INTO KhuyenMai (MaKM, TenKM, NgayBatDau, NgayKetThuc, TrangThai, KhuyenMaiHoaDon, " +
                         "HinhThucKM, GiaTri, DieuKienApDungHoaDon, SoLuongToiThieu, SoLuongTangThem) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, km.getMaKM());
            stmt.setString(2, km.getTenKM());
            stmt.setDate(3, Date.valueOf(km.getNgayBatDau()));
            stmt.setDate(4, Date.valueOf(km.getNgayKetThuc()));
            stmt.setBoolean(5, km.isTrangThai());
            stmt.setBoolean(6, km.isKhuyenMaiHoaDon());
            stmt.setString(7, km.getHinhThuc().name()); // Chuyển Enum sang String để lưu
            stmt.setDouble(8, km.getGiaTri());
            stmt.setString(9, km.getDieuKienApDungHoaDon());
            stmt.setInt(10, km.getSoLuongToiThieu());
            stmt.setInt(11, km.getSoLuongTangThem());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Tạo mã khuyến mãi tự động theo ngày.
     * @return Mã khuyến mãi mới có dạng KM-yyyymmdd-xxxx.
     */
    public String taoMaKhuyenMai() {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String prefix = "KM-" + dateString + "-";
            String sql = "SELECT COUNT(*) FROM KhuyenMai WHERE MaKM LIKE ?";
            
            stmt = con.prepareStatement(sql);
            stmt.setString(1, prefix + "%");
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1);
                return String.format("%s%04d", prefix, count + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // Trả về mã đầu tiên trong ngày nếu có lỗi
        String dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "KM-" + dateString + "-0001";
    }
}