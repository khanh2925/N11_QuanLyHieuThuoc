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

    public PhieuTra_DAO() {}

    // ===== Lấy 1 phiếu trả theo mã (kèm chi tiết) =====
    public PhieuTra timKiemPhieuTraBangMa(String maPhieuTra) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaPhieuTra, NgayLap, MaNhanVien, MaKhachHang, TongTienHoan, DaDuyet " +
                     "FROM PhieuTra WHERE MaPhieuTra = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPhieuTra);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String maNV = rs.getString("MaNhanVien");
                    String maKH = rs.getString("MaKhachHang");
                    LocalDate ngayLap = rs.getDate("NgayLap").toLocalDate();
                    boolean daDuyet = rs.getBoolean("DaDuyet");
                    // double tongTienHoanDB = rs.getBigDecimal("TongTienHoan") == null ? 0
                    //         : rs.getBigDecimal("TongTienHoan").doubleValue();

                    // nạp NV, KH
                    NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
                    KhachHang_DAO khachHangDAO = new KhachHang_DAO();
                    NhanVien nv = nhanVienDAO.getNhanVienTheoMa(maNV);
                    KhachHang kh = khachHangDAO.getKhachHangTheoMa(maKH);

                    // nạp chi tiết
                    ChiTietPhieuTra_DAO ctDAO = new ChiTietPhieuTra_DAO();
                    List<ChiTietPhieuTra> chiTietList = ctDAO.timKiemChiTietBangMaPhieuTra(maPhieuTra);

                    // dựng entity (constructor sẽ tự cập nhật tổng tiền hoàn)
                    return new PhieuTra(maPhieuTra, kh, nv, ngayLap, daDuyet, chiTietList);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== Lấy tất cả phiếu trả (kèm chi tiết) =====
    public List<PhieuTra> layTatCaPhieuTra() {
        List<PhieuTra> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaPhieuTra FROM PhieuTra ORDER BY NgayLap DESC, MaPhieuTra DESC";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String maPT = rs.getString("MaPhieuTra");
                PhieuTra pt = timKiemPhieuTraBangMa(maPT); // tái dùng hàm trên để kèm chi tiết
                if (pt != null) ds.add(pt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    // ===== Thêm phiếu trả + chi tiết (transaction) =====
    public boolean themPhieuTra(PhieuTra pt) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        // Lấy tổng tiền hoàn từ entity (đã tự tính theo chi tiết hợp lệ)
        double tongTienHoan = pt.getTongTienHoan();

        String sqlPT = "INSERT INTO PhieuTra " +
                "(MaPhieuTra, NgayLap, MaNhanVien, MaKhachHang, TongTienHoan, DaDuyet) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        // Giữ nguyên cú pháp bảng chi tiết hiện tại của bạn
        String sqlCT = "INSERT INTO ChiTietPhieuTra " +
                "(MaPhieuTra, MaHoaDon, MaSanPham, LyDoTra, SoLuong, ThanhTienHoan, TrangThai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            con.setAutoCommit(false);

            // 1) Insert header
            try (PreparedStatement ps = con.prepareStatement(sqlPT)) {
                ps.setString(1, pt.getMaPhieuTra());
                ps.setDate(2, Date.valueOf(pt.getNgayLap()));
                ps.setString(3, pt.getNhanVien().getMaNhanVien());
                ps.setString(4, pt.getKhachHang().getMaKhachHang());
                // DECIMAL(18,2) -> BigDecimal
                if (Double.isNaN(tongTienHoan)) tongTienHoan = 0;
                ps.setBigDecimal(5, java.math.BigDecimal.valueOf(Math.round(tongTienHoan * 100.0) / 100.0));
                ps.setBoolean(6, pt.isDaDuyet());
                ps.executeUpdate();
            }

            // 2) Insert details
            try (PreparedStatement psCT = con.prepareStatement(sqlCT)) {
                for (ChiTietPhieuTra ct : pt.getChiTietPhieuTraList()) {
                    psCT.setString(1, pt.getMaPhieuTra());
                    psCT.setString(2, ct.getChiTietHoaDon().getHoaDon().getMaHoaDon());
                    psCT.setString(3, ct.getChiTietHoaDon().getSanPham().getMaSanPham());
                    psCT.setString(4, ct.getLyDoChiTiet());
                    psCT.setInt(5, ct.getSoLuong());
                    psCT.setBigDecimal(6, java.math.BigDecimal.valueOf(
                            Math.round(ct.getThanhTienHoan() * 100.0) / 100.0));

                    // Nếu cột TrangThai của bảng chi tiết là BIT -> dùng setBoolean.
                    // Nếu cột TrangThai là NVARCHAR -> đổi thành setString tùy schema bạn đang dùng.
                    // Ở đây ưu tiên BIT:
                    psCT.setBoolean(7, ct.isHoanTien()); // hoặc ct.isTrangThai() nếu bạn đặt tên vậy
                    psCT.addBatch();
                }
                psCT.executeBatch();
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

    // ===== Cập nhật trạng thái đã duyệt (BIT) =====
    public boolean capNhatTrangThai(String maPhieuTra, boolean daDuyetMoi) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "UPDATE PhieuTra SET DaDuyet = ? WHERE MaPhieuTra = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, daDuyetMoi);
            ps.setString(2, maPhieuTra);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== Cập nhật tổng tiền theo chi tiết (sync lại nếu chi tiết đổi) =====
    public boolean capNhatTongTienTheoChiTiet(String maPhieuTra) {
        PhieuTra pt = timKiemPhieuTraBangMa(maPhieuTra);
        if (pt == null) return false;

        double sum = pt.getTongTienHoan();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "UPDATE PhieuTra SET TongTienHoan = ? WHERE MaPhieuTra = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, java.math.BigDecimal.valueOf(Math.round(sum * 100.0) / 100.0));
            ps.setString(2, maPhieuTra);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== Tạo mã PTxxxxxx (đúng CK_PT_Ma) =====
    public String taoMaPhieuTra() {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String prefix = "PT";

        String sql = "SELECT MAX(MaPhieuTra) AS MaxMa FROM PhieuTra WHERE MaPhieuTra LIKE 'PT%%%%%%'";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String lastID = rs.getString("MaxMa");
                if (lastID != null) {
                    int lastNum = Integer.parseInt(lastID.substring(prefix.length()));
                    return String.format("%s%06d", prefix, lastNum + 1);
                }
            }
            return prefix + "000001";
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            return prefix + "000001";
        }
    }
}