package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import database.connectDB;

public class ThongKe_DAO {

    public static class BanGhiThongKe {
        public String thoiGian;
        public double doanhThu;
        public int soLuongDon;

        public BanGhiThongKe(String thoiGian, double doanhThu, int soLuongDon) {
            this.thoiGian = thoiGian;
            this.doanhThu = doanhThu;
            this.soLuongDon = soLuongDon;
        }
    }

    // --- HÀM TRỢ GIÚP TẠO QUERY ĐỘNG ---
    private String getDieuKienLoc(String loaiSP, String maKM, String aliasSP, String aliasKM_HD, String aliasKM_CT) {
        StringBuilder sql = new StringBuilder();
        if (loaiSP != null && !loaiSP.equals("Tất cả")) {
            sql.append(" AND ").append(aliasSP).append(".LoaiSanPham = ? ");
        }
        if (maKM != null && !maKM.equals("Tất cả")) {
            sql.append(" AND (").append(aliasKM_HD).append(" = ? OR ").append(aliasKM_CT).append(" = ?) ");
        }
        return sql.toString();
    }

    private int setThamSoLoc(PreparedStatement ps, int idx, String loaiSP, String maKM) throws SQLException {
        if (loaiSP != null && !loaiSP.equals("Tất cả")) {
            ps.setString(idx++, loaiSP);
        }
        if (maKM != null && !maKM.equals("Tất cả")) {
            ps.setString(idx++, maKM);
            ps.setString(idx++, maKM);
        }
        return idx;
    }

    /**
     * TỔNG HỢP DỮ LIỆU: BÁN - TRẢ (Net Revenue)
     * type: 1=Ngày, 2=Tháng, 3=Năm
     */
    private List<BanGhiThongKe> getThongKeChung(int type, Object p1, Object p2, String loaiSP, String maKM) {
        List<BanGhiThongKe> list = new ArrayList<>();
        
        String selectTime, whereTimeHD, whereTimePT, groupBy;

        if (type == 1) { // Ngày
            selectTime = "FORMAT(Temp.Ngay, 'dd/MM/yyyy')";
            whereTimeHD = "hd.NgayLap BETWEEN ? AND ?";
            whereTimePT = "pt.NgayLap BETWEEN ? AND ?";
            groupBy = "Temp.Ngay";
        } else if (type == 2) { // Tháng
            selectTime = "'T' + CAST(MONTH(Temp.Ngay) AS VARCHAR)";
            whereTimeHD = "YEAR(hd.NgayLap) = ?";
            whereTimePT = "YEAR(pt.NgayLap) = ?";
            groupBy = "MONTH(Temp.Ngay)";
        } else { // Năm
            selectTime = "CAST(YEAR(Temp.Ngay) AS VARCHAR)";
            whereTimeHD = "YEAR(hd.NgayLap) BETWEEN ? AND ?";
            whereTimePT = "YEAR(pt.NgayLap) BETWEEN ? AND ?";
            groupBy = "YEAR(Temp.Ngay)";
        }

        String sql = 
            "SELECT " + selectTime + " as ThoiGian, " +
            "SUM(Temp.ThanhTien) as DoanhThu, " +
            "COUNT(DISTINCT Temp.MaHoaDon) as SoDon " + 
            "FROM ( " +
            // --- PHẦN 1: HÓA ĐƠN ---
            "   SELECT hd.NgayLap as Ngay, ct.ThanhTien, hd.MaHoaDon " + 
            "   FROM HoaDon hd " +
            "   JOIN ChiTietHoaDon ct ON hd.MaHoaDon = ct.MaHoaDon " +
            "   JOIN LoSanPham lo ON ct.MaLo = lo.MaLo " +
            "   JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham " +
            "   WHERE " + whereTimeHD + getDieuKienLoc(loaiSP, maKM, "sp", "hd.MaKM", "ct.MaKM") +
            "   UNION ALL " +
            // --- PHẦN 2: PHIẾU TRẢ (DOANH THU ÂM) ---
            "   SELECT pt.NgayLap as Ngay, -ctpt.ThanhTienHoan, NULL as MaHoaDon " + 
            "   FROM PhieuTra pt " +
            "   JOIN ChiTietPhieuTra ctpt ON pt.MaPhieuTra = ctpt.MaPhieuTra " +
            "   JOIN LoSanPham lo ON ctpt.MaLo = lo.MaLo " +
            "   JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham " +
            "   LEFT JOIN HoaDon hd ON ctpt.MaHoaDon = hd.MaHoaDon " +
            "   WHERE " + whereTimePT + getDieuKienLoc(loaiSP, maKM, "sp", "hd.MaKM", "NULL") +
            ") as Temp " +
            "GROUP BY " + groupBy + " " +
            "ORDER BY " + groupBy + " ASC";

        // Sử dụng try-with-resources để tự động đóng kết nối
        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            int idx = 1;
            // Tham số thời gian Hóa Đơn
            if (type == 1) {
                ps.setDate(idx++, new java.sql.Date(((java.util.Date)p1).getTime()));
                ps.setDate(idx++, new java.sql.Date(((java.util.Date)p2).getTime()));
            } else {
                ps.setInt(idx++, (int)p1);
                if (type == 3) ps.setInt(idx++, (int)p2);
            }
            idx = setThamSoLoc(ps, idx, loaiSP, maKM);
            
            // Tham số thời gian Phiếu Trả
            if (type == 1) {
                ps.setDate(idx++, new java.sql.Date(((java.util.Date)p1).getTime()));
                ps.setDate(idx++, new java.sql.Date(((java.util.Date)p2).getTime()));
            } else {
                ps.setInt(idx++, (int)p1);
                if (type == 3) ps.setInt(idx++, (int)p2);
            }
            setThamSoLoc(ps, idx, loaiSP, maKM);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new BanGhiThongKe(rs.getString("ThoiGian"), rs.getDouble("DoanhThu"), rs.getInt("SoDon")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<BanGhiThongKe> getDoanhThuTheoNgay(java.util.Date tu, java.util.Date den, String loaiSP, String maKM) {
        return getThongKeChung(1, tu, den, loaiSP, maKM);
    }

    public List<BanGhiThongKe> getDoanhThuTheoThang(int nam, String loaiSP, String maKM) {
        return getThongKeChung(2, nam, null, loaiSP, maKM);
    }

    public List<BanGhiThongKe> getDoanhThuTheoNam(int namTu, int namDen, String loaiSP, String maKM) {
        return getThongKeChung(3, namTu, namDen, loaiSP, maKM);
    }

    public List<String[]> getDanhSachKhuyenMai() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT MaKM, TenKM FROM KhuyenMai WHERE TrangThai = 1"; // Chỉ lấy KM đang hoạt động
        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while(rs.next()) list.add(new String[]{rs.getString("MaKM"), rs.getString("TenKM")});
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}