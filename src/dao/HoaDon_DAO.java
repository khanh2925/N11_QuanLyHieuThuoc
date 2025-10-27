package dao;

import connectDB.connectDB;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_DAO {

    public HoaDon_DAO() {
    }

    public HoaDon timHoaDonTheoMa(String maHD) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connectDB.getInstance();
            con = connectDB.getConnection();
            // Khởi tạo các DAO cần thiết cục bộ
            NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
            KhachHang_DAO khachHangDAO = new KhachHang_DAO();
            ChiTietHoaDon_DAO chiTietHoaDonDAO = new ChiTietHoaDon_DAO();

            String sql = "SELECT * FROM HoaDon WHERE MaHoaDon = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maHD);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String maKH = rs.getString("MaKhachHang");
                LocalDate ngayLap = rs.getDate("NgayLap").toLocalDate();
                String maNV = rs.getString("MaNhanVien");
                boolean thuocTheoDon = rs.getBoolean("ThuocTheoDon");

                NhanVien nv = nhanVienDAO.timKiemNhanVienBangMa(maNV);
                 KhachHang kh = khachHangDAO.timKiemKhachHangBangMa(maKH); // Entity HoaDon chỉ cần mã KH

                // KhuyenMai là null vì DB không có
                HoaDon hd = new HoaDon(maHD, maKH, ngayLap, nv, null, thuocTheoDon);

                List<ChiTietHoaDon> dsChiTiet = chiTietHoaDonDAO.layDanhSachChiTietTheoMaHD(maHD);
                hd.setChiTietHoaDonList(dsChiTiet);

                return hd;
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
     * Lấy tất cả các hóa đơn từ cơ sở dữ liệu.
     */
    public List<HoaDon> layTatCaHoaDon() {
        List<HoaDon> dsHD = new ArrayList<>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            connectDB.getInstance();
            con = connectDB.getConnection();

            String sql = "SELECT MaHoaDon FROM HoaDon ORDER BY NgayLap DESC";
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                // Tái sử dụng phương thức tìm theo mã trong cùng lớp
                HoaDon hd = timHoaDonTheoMa(rs.getString("MaHoaDon"));
                if (hd != null) {
                    dsHD.add(hd);
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
        return dsHD;
    }

    /**
     * Thêm một hóa đơn mới vào CSDL (sử dụng transaction).
     */
    public boolean themHoaDon(HoaDon hd) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmtHD = null;
        PreparedStatement stmtCTHD = null;

        try {
            con.setAutoCommit(false); // Bắt đầu transaction

            // 1. Thêm Hóa Đơn
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

            // 2. Thêm Chi Tiết Hóa Đơn
            String sqlChiTiet = "INSERT INTO ChiTietHoaDon (MaHoaDon, MaSanPham, SoLuong, GiaBan) VALUES (?, ?, ?, ?)";
            stmtCTHD = con.prepareStatement(sqlChiTiet);
            for (ChiTietHoaDon cthd : hd.getChiTietHoaDonList()) {
                stmtCTHD.setString(1, hd.getMaHoaDon());
                stmtCTHD.setString(2, cthd.getSanPham().getMaSanPham());
                stmtCTHD.setInt(3, (int) cthd.getSoLuong());
                stmtCTHD.setDouble(4, cthd.getGiaBan());
                stmtCTHD.addBatch();
            }
            stmtCTHD.executeBatch();

            con.commit(); // Hoàn tất transaction
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback(); // Hoàn tác nếu có lỗi
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (stmtHD != null) stmtHD.close();
                if (stmtCTHD != null) stmtCTHD.close();
                if (con != null) con.setAutoCommit(true); // Luôn trả lại auto-commit
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Tạo mã hóa đơn tự động theo ngày.
     */
    public String taoMaHoaDon() {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String prefix = "HD-" + dateString + "-";
            String sql = "SELECT COUNT(*) FROM HoaDon WHERE MaHoaDon LIKE ?";
            
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
        return "HD-" + dateString + "-0001";
    }
}