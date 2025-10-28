package dao;

import connectDB.connectDB;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.NhanVien;
// Giả định bạn có entity KhuyenMai
import entity.KhuyenMai;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_DAO {

    public HoaDon_DAO() {
    }

    /**
     * Tìm hóa đơn theo mã.
     * @param maHD Mã hóa đơn cần tìm.
     * @return Đối tượng HoaDon hoặc null nếu không tìm thấy.
     */
    public HoaDon timHoaDonTheoMa(String maHD) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT * FROM HoaDon WHERE MaHoaDon = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maHD);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String maKH = rs.getString("MaKhachHang");
                    LocalDate ngayLap = rs.getDate("NgayLap").toLocalDate();
                    String maNV = rs.getString("MaNhanVien");
                    boolean thuocTheoDon = rs.getBoolean("ThuocTheoDon");

                    NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
                    ChiTietHoaDon_DAO chiTietHoaDonDAO = new ChiTietHoaDon_DAO();

                    NhanVien nv = nhanVienDAO.getNhanVienTheoMa(maNV);

                    HoaDon hd = new HoaDon(maHD, maKH, ngayLap, nv, null, thuocTheoDon);

                    List<ChiTietHoaDon> dsChiTiet = chiTietHoaDonDAO.layDanhSachChiTietTheoMaHD(maHD);
                    hd.setChiTietHoaDonList(dsChiTiet);

                    return hd;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy tất cả các hóa đơn từ cơ sở dữ liệu.
     * Form: Trả về ArrayList.
     */
    public ArrayList<HoaDon> layTatCaHoaDon() {
        ArrayList<HoaDon> dsHD = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaHoaDon FROM HoaDon ORDER BY NgayLap DESC";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                HoaDon hd = timHoaDonTheoMa(rs.getString("MaHoaDon"));
                if (hd != null) {
                    dsHD.add(hd);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsHD;
    }


    public boolean themHoaDon(HoaDon hd) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        
        PreparedStatement stmtHD = null;
        PreparedStatement stmtCTHD = null;
        try {
            con.setAutoCommit(false);

            String sqlHoaDon = "INSERT INTO HoaDon (MaHoaDon, MaKhachHang, NgayLap, MaNhanVien, ThuocTheoDon, TongTien) " +
                               "VALUES (?, ?, ?, ?, ?, ?)";
            stmtHD = con.prepareStatement(sqlHoaDon);
            stmtHD.setString(1, hd.getMaHoaDon());
            stmtHD.setString(2, hd.getMaKhachHang());
            stmtHD.setDate(3, Date.valueOf(hd.getNgayLap()));
            stmtHD.setString(4, hd.getNhanVien().getMaNhanVien());
            stmtHD.setBoolean(5, hd.isThuocTheoDon());
            stmtHD.setDouble(6, hd.getTongTien());
            stmtHD.executeUpdate();

            String sqlChiTiet = "INSERT INTO ChiTietHoaDon (MaHoaDon, MaSanPham, MaKM, SoLuong, GiaBan) VALUES (?, ?, ?, ?, ?)";
            stmtCTHD = con.prepareStatement(sqlChiTiet);
            
            for (ChiTietHoaDon cthd : hd.getChiTietHoaDonList()) {
                stmtCTHD.setString(1, hd.getMaHoaDon());
                stmtCTHD.setString(2, cthd.getSanPham().getMaSanPham());

                if (cthd.getKhuyenMai() != null) {
                    stmtCTHD.setString(3, cthd.getKhuyenMai().getMaKM());
                } else {
                    stmtCTHD.setNull(3, java.sql.Types.CHAR);
                }
                
                stmtCTHD.setInt(4, (int) cthd.getSoLuong());
                stmtCTHD.setDouble(5, cthd.getGiaBan());
                
                stmtCTHD.addBatch();
            }
            stmtCTHD.executeBatch();

            con.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (stmtHD != null) stmtHD.close();
                if (stmtCTHD != null) stmtCTHD.close();
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Tạo mã hóa đơn mới theo định dạng HD-YYYYMMDD-XXXX.
     * @return Một chuỗi mã hóa đơn duy nhất trong ngày.
     */
    public String taoMaHoaDon() {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        
        String dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "HD-" + dateString + "-";
        String sql = "SELECT COUNT(*) FROM HoaDon WHERE MaHoaDon LIKE ?";

        // Form: Sử dụng try-with-resources
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, prefix + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return String.format("%s%04d", prefix, count + 1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return prefix + "0001";
    }
}