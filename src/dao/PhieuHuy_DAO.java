package dao;

import connectDB.connectDB; // Sửa lại import cho đúng tên class của bạn
import entity.ChiTietPhieuHuy;
import entity.LoSanPham;
import entity.NhanVien;
import entity.PhieuHuy;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class PhieuHuy_DAO {
    
    public PhieuHuy_DAO() {
    }

    /**
     * Lấy danh sách tất cả các phiếu hủy từ cơ sở dữ liệu.
     * @return Danh sách các đối tượng PhieuHuy.
     */
    public List<PhieuHuy> layTatCaPhieuHuy() {
        List<PhieuHuy> danhSachPhieuHuy = new ArrayList<>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            connectDB.getInstance();
            con = connectDB.getConnection();
            NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
            ChiTietPhieuHuy_DAO chiTietPhieuHuyDAO = new ChiTietPhieuHuy_DAO();

            String sql = "SELECT * FROM PhieuHuy ORDER BY NgayLapPhieu DESC";
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String maPhieuHuy = rs.getString("MaPhieuHuy");
                LocalDate ngayLapPhieu = rs.getDate("NgayLapPhieu").toLocalDate();
                boolean trangThai = rs.getBoolean("TrangThai");
                String maNhanVien = rs.getString("MaNhanVien");

                NhanVien nv = nhanVienDAO.timKiemNhanVienBangMa(maNhanVien);
                PhieuHuy ph = new PhieuHuy(maPhieuHuy, ngayLapPhieu, nv, trangThai);

                List<ChiTietPhieuHuy> chiTietList = chiTietPhieuHuyDAO.timKiemChiTietPhieuHuyBangMa(maPhieuHuy);
                ph.setChiTietPhieuHuyList(chiTietList);

                danhSachPhieuHuy.add(ph);
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
        return danhSachPhieuHuy;
    }

    /**
     * Lấy danh sách chi tiết của một phiếu hủy dựa vào mã phiếu.
     * @param maPhieuHuy Mã của phiếu hủy cần lấy chi tiết.
     * @return Danh sách các đối tượng ChiTietPhieuHuy.
     */
    public List<ChiTietPhieuHuy> layChiTietTheoMaPhieu(String maPhieuHuy) {
        List<ChiTietPhieuHuy> danhSachChiTiet = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            connectDB.getInstance();
            con = connectDB.getConnection();
            LoSanPham_DAO loSanPhamDAO = new LoSanPham_DAO();
            
            String sql = "SELECT * FROM ChiTietPhieuHuy WHERE MaPhieuHuy = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maPhieuHuy);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                String maLo = rs.getString("MaLo");
                int soLuongHuy = rs.getInt("SoLuongHuy");
                String lyDo = rs.getString("LyDoChiTiet");

                LoSanPham lo = loSanPhamDAO.timKiemLoSanPhamBangMa(maLo);
                if (lo != null) {
                    // Constructor của ChiTietPhieuHuy yêu cầu PhieuHuy, nhưng ở đây ta chỉ có mã
                    // Ta sẽ tạm thời truyền null để lấy danh sách chi tiết
                    ChiTietPhieuHuy ct = new ChiTietPhieuHuy(null, soLuongHuy, lyDo, lo, lo.getSanPham().getGiaNhap());
                    danhSachChiTiet.add(ct);
                }
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
        return danhSachChiTiet;
    }

    /**
     * Tạo mới một phiếu hủy (sử dụng transaction).
     */
    public boolean themPhieuHuy(PhieuHuy ph) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmtPhieuHuy = null;
        PreparedStatement stmtChiTiet = null;
        PreparedStatement stmtUpdate = null;
        
        try {
            con.setAutoCommit(false); // Bắt đầu transaction

            String sqlPhieuHuy = "INSERT INTO PhieuHuy (MaPhieuHuy, NgayLapPhieu, MaNhanVien, TrangThai) VALUES (?, ?, ?, ?)";
            stmtPhieuHuy = con.prepareStatement(sqlPhieuHuy);
            stmtPhieuHuy.setString(1, ph.getMaPhieuHuy());
            stmtPhieuHuy.setDate(2, Date.valueOf(ph.getNgayLapPhieu()));
            stmtPhieuHuy.setString(3, ph.getNhanVien().getMaNhanVien());
            stmtPhieuHuy.setBoolean(4, ph.isTrangThai());
            stmtPhieuHuy.executeUpdate();

            String sqlChiTiet = "INSERT INTO ChiTietPhieuHuy (MaPhieuHuy, MaLo, SoLuongHuy, LyDoChiTiet) VALUES (?, ?, ?, ?)";
            String sqlUpdateSoLuong = "UPDATE LoSanPham SET SoLuongTon = SoLuongTon - ? WHERE MaLo = ?";
            stmtChiTiet = con.prepareStatement(sqlChiTiet);
            stmtUpdate = con.prepareStatement(sqlUpdateSoLuong);

            for (ChiTietPhieuHuy ct : ph.getChiTietPhieuHuyList()) {
                stmtChiTiet.setString(1, ph.getMaPhieuHuy());
                stmtChiTiet.setString(2, ct.getLoSanPham().getMaLo());
                stmtChiTiet.setInt(3, ct.getSoLuongHuy());
                stmtChiTiet.setString(4, ct.getLyDoChiTiet());
                stmtChiTiet.addBatch();

                stmtUpdate.setInt(1, ct.getSoLuongHuy());
                stmtUpdate.setString(2, ct.getLoSanPham().getMaLo());
                stmtUpdate.addBatch();
            }
            stmtChiTiet.executeBatch();
            stmtUpdate.executeBatch();

            con.commit(); // Hoàn tất transaction
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
                if (stmtPhieuHuy != null) stmtPhieuHuy.close();
                if (stmtChiTiet != null) stmtChiTiet.close();
                if (stmtUpdate != null) stmtUpdate.close();
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Cập nhật trạng thái của phiếu hủy.
     */
    public boolean capNhatTrangThai(String maPhieuHuy, boolean trangThaiMoi) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmt = null;

        try {
            String sql = "UPDATE PhieuHuy SET TrangThai = ? WHERE MaPhieuHuy = ?";
            stmt = con.prepareStatement(sql);
            stmt.setBoolean(1, trangThaiMoi);
            stmt.setString(2, maPhieuHuy);
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
     * Tạo mã phiếu hủy tự động.
     */
    public String taoMaPhieuHuy() {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String prefix = "PH-" + dateString + "-";
            String sql = "SELECT COUNT(*) FROM PhieuHuy WHERE MaPhieuHuy LIKE ?";
            
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
        return "PH-" + dateString + "-0001";
    }
}