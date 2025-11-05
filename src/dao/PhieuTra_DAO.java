package dao;

import connectDB.connectDB;
import entity.ChiTietPhieuTra;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuTra;

import java.sql.*;
import java.time.LocalDate;
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

                    NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
                    KhachHang_DAO khachHangDAO = new KhachHang_DAO();
                    ArrayList<NhanVien> nv = nhanVienDAO.timNhanVien(maNV);
                    ArrayList<KhachHang> kh = khachHangDAO.timKhachHang(maKH);

                    ChiTietPhieuTra_DAO ctDAO = new ChiTietPhieuTra_DAO();
                    List<ChiTietPhieuTra> chiTietList = ctDAO.timKiemChiTietBangMaPhieuTra(maPhieuTra);

                    return new PhieuTra(maPhieuTra, null, null, ngayLap, daDuyet, chiTietList);
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
                PhieuTra pt = timKiemPhieuTraBangMa(maPT);
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

        double tongTienHoan = pt.getTongTienHoan();

        String sqlPT = "INSERT INTO PhieuTra " +
                "(MaPhieuTra, NgayLap, MaNhanVien, MaKhachHang, TongTienHoan, DaDuyet) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        // ⬇️ CẬP NHẬT: dùng MaLo thay vì MaSanPham
        String sqlCT = "INSERT INTO ChiTietPhieuTra " +
                "(MaPhieuTra, MaHoaDon, MaLo, LyDoChiTiet, SoLuong, ThanhTienHoan, TrangThai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            con.setAutoCommit(false);

            // 1) Insert header
            try (PreparedStatement ps = con.prepareStatement(sqlPT)) {
                ps.setString(1, pt.getMaPhieuTra());
                ps.setDate(2, Date.valueOf(pt.getNgayLap()));
                ps.setString(3, pt.getNhanVien().getMaNhanVien());
                ps.setString(4, pt.getKhachHang().getMaKhachHang());
                ps.setBigDecimal(5, java.math.BigDecimal.valueOf(Math.round(tongTienHoan * 100.0) / 100.0));
                ps.setBoolean(6, pt.isDaDuyet());
                ps.executeUpdate();
            }

            // 2) Insert details
            try (PreparedStatement psCT = con.prepareStatement(sqlCT)) {
                for (ChiTietPhieuTra ct : pt.getChiTietPhieuTraList()) {
                    psCT.setString(1, pt.getMaPhieuTra());
                    psCT.setString(2, ct.getChiTietHoaDon().getHoaDon().getMaHoaDon());
                    psCT.setString(3, ct.getChiTietHoaDon().getLoSanPham().getMaLo()); // ✅ MaLo
                    psCT.setString(4, ct.getLyDoChiTiet());
                    psCT.setInt(5, ct.getSoLuong());
                    psCT.setBigDecimal(6, java.math.BigDecimal.valueOf(
                            Math.round(ct.getThanhTienHoan() * 100.0) / 100.0));
                    // Nếu cột TrangThai là BIT
                    psCT.setInt(7, ct.getTrangThai());
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

    // ===== Cập nhật trạng thái đã duyệt =====
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

    // ===== Tạo mã PTxxxxxx =====
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
