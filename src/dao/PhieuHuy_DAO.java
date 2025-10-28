package dao;

import connectDB.connectDB;
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

    // ========== LẤY TẤT CẢ PHIẾU HỦY (KÈM CHI TIẾT) ==========
    public List<PhieuHuy> layTatCaPhieuHuy() {
        List<PhieuHuy> list = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
        ChiTietPhieuHuy_DAO chiTietDAO = new ChiTietPhieuHuy_DAO();

        String sql = "SELECT MaPhieuHuy, NgayLapPhieu, MaNhanVien, TongTienHuy, TrangThai " +
                     "FROM PhieuHuy ORDER BY NgayLapPhieu DESC, MaPhieuHuy DESC";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String ma = rs.getString("MaPhieuHuy");
                LocalDate ngay = rs.getDate("NgayLapPhieu").toLocalDate();
                String maNV = rs.getString("MaNhanVien");
                boolean trangThai = rs.getBoolean("TrangThai");
                // double tongTienHuy = rs.getBigDecimal("TongTienHuy").doubleValue(); // header có nhưng entity sẽ tự sync sau khi set chi tiết

                NhanVien nv = nhanVienDAO.getNhanVienTheoMa(maNV);

                PhieuHuy ph = new PhieuHuy(ma, ngay, nv, trangThai);
                List<ChiTietPhieuHuy> cts = chiTietDAO.timKiemChiTietPhieuHuyBangMa(ma);
                ph.setChiTietPhieuHuyList(cts); // entity tự capNhatTongTienTheoChiTiet()

                list.add(ph);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ========== LẤY 1 PHIẾU HỦY THEO MÃ ==========
    public PhieuHuy layTheoMa(String maPhieuHuy) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
        ChiTietPhieuHuy_DAO chiTietDAO = new ChiTietPhieuHuy_DAO();

        String sql = "SELECT MaPhieuHuy, NgayLapPhieu, MaNhanVien, TongTienHuy, TrangThai " +
                     "FROM PhieuHuy WHERE MaPhieuHuy = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPhieuHuy);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String ma = rs.getString("MaPhieuHuy");
                    LocalDate ngay = rs.getDate("NgayLapPhieu").toLocalDate();
                    String maNV = rs.getString("MaNhanVien");
                    boolean trangThai = rs.getBoolean("TrangThai");
                    // double tongTienHuy = rs.getBigDecimal("TongTienHuy").doubleValue();

                    NhanVien nv = nhanVienDAO.getNhanVienTheoMa(maNV);
                    PhieuHuy ph = new PhieuHuy(ma, ngay, nv, trangThai);
                    ph.setChiTietPhieuHuyList(chiTietDAO.timKiemChiTietPhieuHuyBangMa(ma));
                    return ph;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ========== LẤY CHI TIẾT (WRAPPER) ==========
    public List<ChiTietPhieuHuy> layChiTietTheoMaPhieu(String maPhieuHuy) {
        ChiTietPhieuHuy_DAO chiTietDAO = new ChiTietPhieuHuy_DAO();
        return chiTietDAO.timKiemChiTietPhieuHuyBangMa(maPhieuHuy);
    }

    // ========== THÊM PHIẾU HỦY + CHI TIẾT (TRANSACTION) ==========
    public boolean themPhieuHuy(PhieuHuy ph) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        // tính lại tổng tiền từ chi tiết để ghi vào cột TongTienHuy
        double tongTien = 0;
        if (ph.getChiTietPhieuHuyList() != null) {
            for (ChiTietPhieuHuy ct : ph.getChiTietPhieuHuyList()) {
                tongTien += ct.getThanhTien();
            }
        }
        tongTien = Math.round(tongTien * 100.0) / 100.0;

        String sqlPH = "INSERT INTO PhieuHuy (MaPhieuHuy, NgayLapPhieu, MaNhanVien, TongTienHuy, TrangThai) " +
                       "VALUES (?, ?, ?, ?, ?)";
        String sqlCT = "INSERT INTO ChiTietPhieuHuy (MaPhieuHuy, MaLo, SoLuongHuy, LyDoChiTiet) " +
                       "VALUES (?, ?, ?, ?)";
        String sqlLo = "UPDATE LoSanPham SET SoLuongTon = SoLuongTon - ? WHERE MaLo = ?";

        try {
            con.setAutoCommit(false);

            // 1) header
            try (PreparedStatement ps = con.prepareStatement(sqlPH)) {
                ps.setString(1, ph.getMaPhieuHuy());
                ps.setDate(2, Date.valueOf(ph.getNgayLapPhieu()));
                ps.setString(3, ph.getNhanVien().getMaNhanVien());
                ps.setBigDecimal(4, java.math.BigDecimal.valueOf(tongTien));
                ps.setBoolean(5, ph.isTrangThai());
                ps.executeUpdate();
            }

            // 2) details + trừ tồn
            try (PreparedStatement psCT = con.prepareStatement(sqlCT);
                 PreparedStatement psUpd = con.prepareStatement(sqlLo)) {

                LoSanPham_DAO loDAO = new LoSanPham_DAO();

                for (ChiTietPhieuHuy ct : ph.getChiTietPhieuHuyList()) {
                    // check tồn trước khi trừ
                    LoSanPham lo = loDAO.layLoTheoMa(ct.getLoSanPham().getMaLo());
                    if (lo == null) throw new SQLException("Không tìm thấy lô: " + ct.getLoSanPham().getMaLo());
                    if (ct.getSoLuongHuy() < 0) throw new SQLException("Số lượng hủy âm ở lô: " + lo.getMaLo());
                    if (lo.getSoLuongTon() < ct.getSoLuongHuy())
                        throw new SQLException("Tồn kho không đủ để hủy. Lô " + lo.getMaLo() +
                                " tồn " + lo.getSoLuongTon() + " < số hủy " + ct.getSoLuongHuy());

                    // insert chi tiết
                    psCT.setString(1, ph.getMaPhieuHuy());
                    psCT.setString(2, lo.getMaLo());
                    psCT.setInt(3, ct.getSoLuongHuy());
                    psCT.setString(4, ct.getLyDoChiTiet());
                    psCT.addBatch();

                    // update tồn
                    psUpd.setInt(1, ct.getSoLuongHuy());
                    psUpd.setString(2, lo.getMaLo());
                    psUpd.addBatch();
                }

                psCT.executeBatch();
                psUpd.executeBatch();
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    // ========== CẬP NHẬT TRẠNG THÁI ==========
    public boolean capNhatTrangThai(String maPhieuHuy, boolean trangThaiMoi) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "UPDATE PhieuHuy SET TrangThai = ? WHERE MaPhieuHuy = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, trangThaiMoi);
            ps.setString(2, maPhieuHuy);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========== CẬP NHẬT TỔNG TIỀN TỪ CHI TIẾT (SYNC LẠI) ==========
    public boolean capNhatTongTienTheoChiTiet(String maPhieuHuy) {
        PhieuHuy ph = layTheoMa(maPhieuHuy);
        if (ph == null) return false;

        double sum = 0;
        if (ph.getChiTietPhieuHuyList() != null) {
            for (ChiTietPhieuHuy ct : ph.getChiTietPhieuHuyList()) sum += ct.getThanhTien();
        }
        sum = Math.round(sum * 100.0) / 100.0;

        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "UPDATE PhieuHuy SET TongTienHuy = ? WHERE MaPhieuHuy = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, java.math.BigDecimal.valueOf(sum));
            ps.setString(2, maPhieuHuy);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========== TẠO MÃ PH-yyyymmdd-xxxx ==========
    public String taoMaPhieuHuy() {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "PH-" + date + "-";

        String sql = "SELECT COUNT(*) FROM PhieuHuy WHERE MaPhieuHuy LIKE ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                int count = rs.next() ? rs.getInt(1) : 0;
                return String.format("%s%04d", prefix, count + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return prefix + "0001";
        }
    }

    // ========== XOÁ (LƯU Ý FK CHI TIẾT) ==========
    public boolean xoa(String maPhieuHuy) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "DELETE FROM PhieuHuy WHERE MaPhieuHuy = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPhieuHuy);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // Nếu có FK từ ChiTietPhieuHuy, cần xoá chi tiết trước
            e.printStackTrace();
            return false;
        }
    }
}