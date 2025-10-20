package dao;

import connectDB.connectDB;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class PhieuHuy_DAO {

    // View cho danh sách header
    public static class PhieuHuyView {
        private final String maPhieuHuy;
        private final LocalDate ngayLap;
        private final String tenNhanVien;
        private final int soDongChiTiet;
        private final int tongSoLuongHuy;
        private boolean trangThai;
        public PhieuHuyView(String ma, LocalDate ngay, String nv, int dong, int tongSL, boolean trangThai) {
            this.maPhieuHuy = ma; 
            this.ngayLap = ngay; 
            this.tenNhanVien = nv;
            this.soDongChiTiet = dong; 
            this.tongSoLuongHuy = tongSL;
            this.trangThai = trangThai;
        }
        public String getMaPhieuHuy(){return maPhieuHuy;}
        public LocalDate getNgayLap(){return ngayLap;}
        public String getTenNhanVien(){return tenNhanVien;}
        public int getSoDongChiTiet(){return soDongChiTiet;}
        public int getTongSoLuongHuy(){return tongSoLuongHuy;}
        public boolean getTrangThai() {return trangThai;}
    }

    /** Lấy danh sách phiếu hủy (header) */
    public List<PhieuHuyView> findAll() throws SQLException {
        List<PhieuHuyView> out = new ArrayList<>();
        Connection con = connectDB.getConnection();
        final String sql =
            "SELECT ph.MaPhieuHuy, ph.NgayLapPhieu, nv.TenNhanVien, " +
            "       COUNT(ct.MaLo) AS SoDongChiTiet, ISNULL(SUM(ct.SoLuongHuy),0) AS TongSoLuongHuy, ph.TrangThai " +
            "FROM PhieuHuy ph " +
            "LEFT JOIN NhanVien nv ON nv.MaNhanVien = ph.MaNhanVien " +
            "LEFT JOIN ChiTietPhieuHuy ct ON ct.MaPhieuHuy = ph.MaPhieuHuy " +
            "GROUP BY ph.MaPhieuHuy, ph.NgayLapPhieu, nv.TenNhanVien, ph.TrangThai " +
            "ORDER BY ph.NgayLapPhieu DESC, ph.MaPhieuHuy DESC";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Date d = rs.getDate("NgayLapPhieu");
                out.add(new PhieuHuyView(
                    rs.getString("MaPhieuHuy"),
                    d != null ? d.toLocalDate() : null,
                    rs.getString("TenNhanVien"),
                    rs.getInt("SoDongChiTiet"),
                    rs.getInt("TongSoLuongHuy"),
                    rs.getBoolean("TrangThai")));
            }
        }
        return out;
    }
}
