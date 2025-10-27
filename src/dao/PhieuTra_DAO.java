package dao;

import connectDB.connectDB;
import entity.ChiTietPhieuTra;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuTra;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PhieuTra_DAO {

    public PhieuTra_DAO() {
    }

    /**
     * Tìm kiếm một phiếu trả trong CSDL dựa vào mã phiếu.
     */
    public PhieuTra timKiemPhieuTraBangMa(String maPhieuTra) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connectDB.getInstance();
            con = connectDB.getConnection();
            // Khởi tạo các DAO cần thiết cục bộ
            NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
            KhachHang_DAO khachHangDAO = new KhachHang_DAO();
            ChiTietPhieuTra_DAO chiTietPhieuTraDAO = new ChiTietPhieuTra_DAO();

            String sql = "SELECT * FROM PhieuTra WHERE MaPhieuTra = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maPhieuTra);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String maNV = rs.getString("MaNhanVien");
                String maKH = rs.getString("MaKhachHang");
                LocalDate ngayLap = rs.getDate("NgayLapPhieu").toLocalDate();
                boolean trangThaiDB = rs.getBoolean("TrangThai");

                NhanVien nv = nhanVienDAO.getNhanVienTheoMa(maNV);
                KhachHang kh = khachHangDAO.getKhachHangTheoMa(maKH);

                String trangThaiEntity = trangThaiDB ? "Đã xử lý" : "Chờ duyệt";
                PhieuTra pt = new PhieuTra(maPhieuTra, kh, nv, ngayLap, trangThaiEntity);

                List<ChiTietPhieuTra> chiTietList = chiTietPhieuTraDAO.timKiemChiTietBangMaPhieuTra(maPhieuTra);
                pt.setChiTietPhieuTraList(chiTietList);

                return pt;
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
     * Lấy tất cả các phiếu trả từ cơ sở dữ liệu.
     */
    public List<PhieuTra> layTatCaPhieuTra() {
        List<PhieuTra> danhSachPT = new ArrayList<>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            connectDB.getInstance();
            con = connectDB.getConnection();
            
            String sql = "SELECT MaPhieuTra FROM PhieuTra ORDER BY NgayLapPhieu DESC";
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                String maPT = rs.getString("MaPhieuTra");
                PhieuTra pt = timKiemPhieuTraBangMa(maPT); // Tái sử dụng phương thức trong cùng lớp
                if (pt != null) {
                    danhSachPT.add(pt);
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
        return danhSachPT;
    }

    /**
     * Tạo một phiếu trả hàng mới và các chi tiết của nó (sử dụng transaction).
     */
    public boolean themPhieuTra(PhieuTra pt) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmtPhieuTra = null;
        PreparedStatement stmtChiTiet = null;

        try {
            con.setAutoCommit(false); // Bắt đầu transaction

            // 1. Thêm vào bảng PhieuTra
            String sqlPhieuTra = "INSERT INTO PhieuTra (MaPhieuTra, NgayLapPhieu, MaNhanVien, MaKhachHang, TongTienHoan, TrangThai) " +
                                 "VALUES (?, ?, ?, ?, ?, ?)";
            stmtPhieuTra = con.prepareStatement(sqlPhieuTra);
            stmtPhieuTra.setString(1, pt.getMaPhieuTra());
            stmtPhieuTra.setDate(2, Date.valueOf(pt.getNgayLap()));
            stmtPhieuTra.setString(3, pt.getNhanVien().getMaNhanVien());
            stmtPhieuTra.setString(4, pt.getKhachHang().getMaKhachHang());
            stmtPhieuTra.setDouble(5, pt.getTongTienHoan());
            boolean trangThaiDB = pt.getTrangThai().equalsIgnoreCase("Đã xử lý");
            stmtPhieuTra.setBoolean(6, trangThaiDB);
            stmtPhieuTra.executeUpdate();

            // 2. Thêm các dòng vào bảng ChiTietPhieuTra
            String sqlChiTiet = "INSERT INTO ChiTietPhieuTra (MaPhieuTra, MaHoaDon, MaSanPham, LyDoTra, SoLuong, ThanhTienHoan, TrangThai) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?)";
            stmtChiTiet = con.prepareStatement(sqlChiTiet);
            for (ChiTietPhieuTra ctpt : pt.getChiTietPhieuTraList()) {
                stmtChiTiet.setString(1, pt.getMaPhieuTra());
                stmtChiTiet.setString(2, ctpt.getChiTietHoaDon().getHoaDon().getMaHoaDon());
                stmtChiTiet.setString(3, ctpt.getChiTietHoaDon().getSanPham().getMaSanPham());
                stmtChiTiet.setString(4, ctpt.getLyDoChiTiet());
                stmtChiTiet.setInt(5, ctpt.getSoLuong());
                stmtChiTiet.setDouble(6, ctpt.getThanhTienHoan());
                String trangThaiChiTietDB = ctpt.isTrangThai() ? "Đã xử lý" : "Chờ duyệt";
                stmtChiTiet.setString(7, trangThaiChiTietDB);
                stmtChiTiet.addBatch();
            }
            stmtChiTiet.executeBatch();

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
                if (stmtPhieuTra != null) stmtPhieuTra.close();
                if (stmtChiTiet != null) stmtChiTiet.close();
                if (con != null) con.setAutoCommit(true); // Luôn trả lại trạng thái auto-commit
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Cập nhật trạng thái của một phiếu trả.
     */
    public boolean capNhatTrangThai(String maPhieuTra, String trangThaiMoi) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmt = null;

        try {
            String sql = "UPDATE PhieuTra SET TrangThai = ? WHERE MaPhieuTra = ?";
            stmt = con.prepareStatement(sql);
            boolean trangThaiDB = trangThaiMoi.equalsIgnoreCase("Đã xử lý");
            stmt.setBoolean(1, trangThaiDB);
            stmt.setString(2, maPhieuTra);
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
     * Tạo mã phiếu trả tự động.
     */
    public String taoMaPhieuTra() {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        String newID = null;
        String prefix = "PT";

        try {
            String sql = "SELECT MAX(MaPhieuTra) FROM PhieuTra WHERE MaPhieuTra LIKE ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, prefix + "%");
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                String lastID = rs.getString(1);
                if (lastID != null) {
                    int lastNumber = Integer.parseInt(lastID.substring(prefix.length()));
                    newID = String.format("%s%06d", prefix, lastNumber + 1);
                } else {
                    newID = prefix + "000001";
                }
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            newID = prefix + "000001"; // Trả về mã đầu tiên nếu có lỗi
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return newID;
    }
}