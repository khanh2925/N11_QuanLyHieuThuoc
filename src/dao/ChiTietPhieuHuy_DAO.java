package dao;

import connectDB.connectDB;
import entity.ChiTietPhieuHuy;
import entity.LoSanPham;
import entity.PhieuHuy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietPhieuHuy_DAO {

    public ChiTietPhieuHuy_DAO() {
    }


    public List<ChiTietPhieuHuy> timKiemChiTietPhieuHuyBangMa(String maPhieuHuy) {
        List<ChiTietPhieuHuy> danhSachChiTiet = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connectDB.getInstance();
            con = connectDB.getConnection();
            LoSanPham_DAO loSanPhamDAO = new LoSanPham_DAO(); // Khởi tạo DAO cục bộ

            String sql = "SELECT * FROM ChiTietPhieuHuy WHERE MaPhieuHuy = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maPhieuHuy);
            rs = stmt.executeQuery();
            
            PhieuHuy ph = new PhieuHuy();
            ph.setMaPhieuHuy(maPhieuHuy);

            while (rs.next()) {
                int soLuongHuy = rs.getInt("SoLuongHuy");
                String lyDo = rs.getString("LyDoChiTiet");
                String maLo = rs.getString("MaLo");

                LoSanPham lo = loSanPhamDAO.getLoSanPhamTheoMa(maLo);
                if (lo != null) {
                    ChiTietPhieuHuy ct = new ChiTietPhieuHuy(ph, soLuongHuy, lyDo, lo, lo.getSanPham().getGiaNhap());
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
     * Thêm một dòng chi tiết mới vào phiếu hủy và cập nhật tồn kho (sử dụng transaction).
     */
    public boolean themChiTietPhieuHuy(ChiTietPhieuHuy ct) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmtInsert = null;
        PreparedStatement stmtUpdate = null;

        try {
            con.setAutoCommit(false); // Bắt đầu transaction

            String sqlInsert = "INSERT INTO ChiTietPhieuHuy (MaPhieuHuy, MaLo, SoLuongHuy, LyDoChiTiet) VALUES (?, ?, ?, ?)";
            stmtInsert = con.prepareStatement(sqlInsert);
            stmtInsert.setString(1, ct.getPhieuHuy().getMaPhieuHuy());
            stmtInsert.setString(2, ct.getLoSanPham().getMaLo());
            stmtInsert.setInt(3, ct.getSoLuongHuy());
            stmtInsert.setString(4, ct.getLyDoChiTiet());
            stmtInsert.executeUpdate();

            String sqlUpdate = "UPDATE LoSanPham SET SoLuongTon = SoLuongTon - ? WHERE MaLo = ?";
            stmtUpdate = con.prepareStatement(sqlUpdate);
            stmtUpdate.setInt(1, ct.getSoLuongHuy());
            stmtUpdate.setString(2, ct.getLoSanPham().getMaLo());
            stmtUpdate.executeUpdate();

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
                if (stmtInsert != null) stmtInsert.close();
                if (stmtUpdate != null) stmtUpdate.close();
                if (con != null) con.setAutoCommit(true); // Luôn trả lại trạng thái mặc định
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Xóa một dòng chi tiết khỏi phiếu hủy và hoàn lại tồn kho (sử dụng transaction).
     */
    public boolean xoaChiTietPhieuHuy(ChiTietPhieuHuy ct) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmtDelete = null;
        PreparedStatement stmtUpdate = null;
        
        try {
            con.setAutoCommit(false); // Bắt đầu transaction

            String sqlDelete = "DELETE FROM ChiTietPhieuHuy WHERE MaPhieuHuy = ? AND MaLo = ?";
            stmtDelete = con.prepareStatement(sqlDelete);
            stmtDelete.setString(1, ct.getPhieuHuy().getMaPhieuHuy());
            stmtDelete.setString(2, ct.getLoSanPham().getMaLo());
            int rowsAffected = stmtDelete.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Xóa chi tiết phiếu hủy thất bại, không tìm thấy dòng nào.");
            }

            String sqlUpdate = "UPDATE LoSanPham SET SoLuongTon = SoLuongTon + ? WHERE MaLo = ?";
            stmtUpdate = con.prepareStatement(sqlUpdate);
            stmtUpdate.setInt(1, ct.getSoLuongHuy());
            stmtUpdate.setString(2, ct.getLoSanPham().getMaLo());
            stmtUpdate.executeUpdate();

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
                if (stmtDelete != null) stmtDelete.close();
                if (stmtUpdate != null) stmtUpdate.close();
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}