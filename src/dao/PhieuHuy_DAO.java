package dao;

import connectDB.connectDB;
import entity.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PhieuHuy_DAO {

    /** 🔹 Lấy tất cả phiếu huỷ (kèm chi tiết, entity tự tính tongTien) */
    public List<PhieuHuy> layTatCaPhieuHuy() {
        List<PhieuHuy> list = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
        ChiTietPhieuHuy_DAO chiTietDAO = new ChiTietPhieuHuy_DAO();

        // ✅ KHÔNG còn cột TongTienHuy trong SELECT
        String sql = """
            SELECT MaPhieuHuy, NgayLapPhieu, MaNhanVien, TrangThai
            FROM PhieuHuy
            ORDER BY NgayLapPhieu DESC, MaPhieuHuy DESC
        """;

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String ma = rs.getString("MaPhieuHuy");
                LocalDate ngay = rs.getDate("NgayLapPhieu").toLocalDate();
                String maNV = rs.getString("MaNhanVien");
                boolean trangThai = rs.getBoolean("TrangThai");

                // Lấy nhân viên theo mã (lấy phần tử đầu nếu có)
                NhanVien nv = null;
                ArrayList<NhanVien> dsNV = nhanVienDAO.timNhanVien(maNV);
                if (!dsNV.isEmpty()) nv = dsNV.get(0);

                PhieuHuy ph = new PhieuHuy(ma, ngay, nv, trangThai);
                ph.setChiTietPhieuHuyList(chiTietDAO.timKiemChiTietPhieuHuyBangMa(ma));
                // Entity tự tính tongTien
                ph.capNhatTongTienTheoChiTiet();

                list.add(ph);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** 🔹 Lấy phiếu huỷ theo mã (kèm chi tiết, entity tự tính tongTien) */
    public PhieuHuy layTheoMa(String maPhieuHuy) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
        ChiTietPhieuHuy_DAO chiTietDAO = new ChiTietPhieuHuy_DAO();

        String sql = """
            SELECT MaPhieuHuy, NgayLapPhieu, MaNhanVien, TrangThai
            FROM PhieuHuy WHERE MaPhieuHuy = ?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPhieuHuy);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LocalDate ngay = rs.getDate("NgayLapPhieu").toLocalDate();
                    String maNV = rs.getString("MaNhanVien");
                    boolean trangThai = rs.getBoolean("TrangThai");

                    NhanVien nv = null;
                    ArrayList<NhanVien> dsNV = nhanVienDAO.timNhanVien(maNV);
                    if (!dsNV.isEmpty()) nv = dsNV.get(0);

                    PhieuHuy ph = new PhieuHuy(maPhieuHuy, ngay, nv, trangThai);
                    ph.setChiTietPhieuHuyList(chiTietDAO.timKiemChiTietPhieuHuyBangMa(maPhieuHuy));
                    ph.capNhatTongTienTheoChiTiet(); // tính trên entity
                    return ph;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 🔹 Lấy danh sách chi tiết theo mã phiếu */
    public List<ChiTietPhieuHuy> layChiTietTheoMaPhieu(String maPhieuHuy) {
        return new ChiTietPhieuHuy_DAO().timKiemChiTietPhieuHuyBangMa(maPhieuHuy);
    }

    /** 🔹 Thêm phiếu huỷ + chi tiết (Transaction) – KHÔNG lưu TongTienHuy vì bảng không có cột này */
    public boolean themPhieuHuy(PhieuHuy ph) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        // Entity có thể tự tính tongTien để hiển thị, nhưng KHÔNG lưu xuống bảng PhieuHuy
        if (ph.getChiTietPhieuHuyList() != null) {
            ph.capNhatTongTienTheoChiTiet();
        }

        // Chỉ có 4 cột theo schema
        String sqlPH = "INSERT INTO PhieuHuy (MaPhieuHuy, NgayLapPhieu, MaNhanVien, TrangThai) VALUES (?, ?, ?, ?)";

        // Giữ nguyên cấu trúc bảng chi tiết như bạn đang dùng
        String sqlCT = "INSERT INTO ChiTietPhieuHuy (MaPhieuHuy, MaLo, SoLuongHuy, LyDoChiTiet, DonGiaNhap, ThanhTien, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            con.setAutoCommit(false);

            // 1️⃣ Thêm header (không có TongTienHuy)
            try (PreparedStatement ps = con.prepareStatement(sqlPH)) {
                ps.setString(1, ph.getMaPhieuHuy());
                ps.setDate(2, java.sql.Date.valueOf(ph.getNgayLapPhieu()));
                ps.setString(3, ph.getNhanVien() != null ? ph.getNhanVien().getMaNhanVien() : null);
                ps.setBoolean(4, ph.isTrangThai());
                ps.executeUpdate();
            }

            // 2️⃣ Thêm chi tiết
            try (PreparedStatement psCT = con.prepareStatement(sqlCT)) {
                for (ChiTietPhieuHuy ct : ph.getChiTietPhieuHuyList()) {
                    psCT.setString(1, ph.getMaPhieuHuy());
                    psCT.setString(2, ct.getLoSanPham().getMaLo());
                    psCT.setInt(3, ct.getSoLuongHuy());
                    psCT.setString(4, ct.getLyDoChiTiet());
                    psCT.setDouble(5, ct.getDonGiaNhap());
                    psCT.setDouble(6, ct.getThanhTien());
                    psCT.setInt(7, ct.getTrangThai()); // 1=chờ, 2=đã huỷ, 3=nhập lại kho (ví dụ)
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

    /** 🔹 Cập nhật trạng thái phiếu (true=đã duyệt, false=chờ duyệt) */
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
   


    /** 🔹 (Tuỳ chọn) Tính lại tổng tiền trên entity – KHÔNG cập nhật DB vì không có cột để lưu */
    public Double tinhTongTienTheoChiTiet(String maPhieuHuy) {
        PhieuHuy ph = layTheoMa(maPhieuHuy);
        if (ph == null) return null;
        ph.capNhatTongTienTheoChiTiet();
        return ph.getTongTien();
    }

    /** 🔹 Tạo mã tự động PH-yyyyMMdd-xxxx (độ dài 16 ký tự khớp CHECK + CHAR(16)) */
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

    /** 🔹 Xoá phiếu huỷ (xoá cả chi tiết) */
    public boolean xoa(String maPhieuHuy) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sqlCT = "DELETE FROM ChiTietPhieuHuy WHERE MaPhieuHuy = ?";
        String sqlPH = "DELETE FROM PhieuHuy WHERE MaPhieuHuy = ?";

        try {
            con.setAutoCommit(false);

            try (PreparedStatement ps1 = con.prepareStatement(sqlCT);
                 PreparedStatement ps2 = con.prepareStatement(sqlPH)) {

                ps1.setString(1, maPhieuHuy);
                ps1.executeUpdate();

                ps2.setString(1, maPhieuHuy);
                ps2.executeUpdate();
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try { con.rollback(); } catch (SQLException ignored) {}
            return false;
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }
}
