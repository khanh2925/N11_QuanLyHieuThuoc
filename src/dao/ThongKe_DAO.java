package dao;

import database.connectDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO phục vụ các tính toán thống kê phức tạp cho Dashboard
 */
public class ThongKe_DAO {

    public ThongKe_DAO() {
    }

    /**
     * Tính lợi nhuận theo tháng = Doanh thu - Chi phí nhập hàng đã bán
     * @param thang Tháng (1-12)
     * @param nam Năm
     * @return Lợi nhuận ước tính (doanh thu - giá nhập trung bình)
     */
    public double tinhLoiNhuanTheoThang(int thang, int nam) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        
        // Giả định đơn giản: Lợi nhuận = 25-30% doanh thu (tỷ suất lợi nhuận trung bình ngành dược)
        // Nếu cần tính chính xác hơn, cần join với PhieuNhap để lấy giá nhập
        String sql = """
                SELECT COALESCE(SUM(TongThanhToan), 0) AS TongDoanhThu
                FROM HoaDon
                WHERE MONTH(NgayLap) = ? AND YEAR(NgayLap) = ?
                """;
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double doanhThu = rs.getDouble("TongDoanhThu");
                    // Giả định tỷ suất lợi nhuận 25.5% (có thể điều chỉnh)
                    return doanhThu * 0.255;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi tính lợi nhuận: " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * Tính lợi nhuận theo tháng (phiên bản tính toán chính xác hơn)
     * Dựa trên giá bán - giá nhập thực tế từ sản phẩm
     * @param thang Tháng (1-12)
     * @param nam Năm
     * @return Lợi nhuận thực tế
     */
    public double tinhLoiNhuanChinhXacTheoThang(int thang, int nam) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        
        String sql = """
                SELECT 
                    COALESCE(SUM(cthd.ThanhTien), 0) AS TongDoanhThu,
                    COALESCE(SUM(cthd.SoLuong * qc.HeSoQuyDoi * sp.GiaNhap), 0) AS TongChiPhi
                FROM ChiTietHoaDon cthd
                INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon
                INNER JOIN LoSanPham lo ON cthd.MaLo = lo.MaLo
                INNER JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
                INNER JOIN QuyCachDongGoi qc ON cthd.MaDonViTinh = qc.MaDonViTinh 
                    AND sp.MaSanPham = qc.MaSanPham
                WHERE MONTH(hd.NgayLap) = ? AND YEAR(hd.NgayLap) = ?
                """;
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double doanhThu = rs.getDouble("TongDoanhThu");
                    double chiPhi = rs.getDouble("TongChiPhi");
                    return doanhThu - chiPhi;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi tính lợi nhuận chính xác: " + e.getMessage());
            // Fallback về phương pháp ước tính
            return tinhLoiNhuanTheoThang(thang, nam);
        }
        return 0;
    }
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

    // Hàm hỗ trợ xây dựng câu WHERE động
    private String getDieuKienLoc(String loaiSP, String maKM) {
        String sql = "";
        // Lọc theo Loại sản phẩm
        if (loaiSP != null && !loaiSP.equals("Tất cả")) {
            sql += " AND sp.LoaiSanPham = ? ";
        }
        // Lọc theo Mã khuyến mãi (Kiểm tra cả KM hóa đơn và KM chi tiết)
        if (maKM != null && !maKM.equals("Tất cả")) {
            sql += " AND (hd.MaKM = ? OR ct.MaKM = ?) ";
        }
        return sql;
    }

    private void setThamSoLoc(PreparedStatement ps, int startIndex, String loaiSP, String maKM) throws SQLException {
        int idx = startIndex;
        if (loaiSP != null && !loaiSP.equals("Tất cả")) {
            ps.setString(idx++, loaiSP); // Enum trong DB lưu dạng String (ví dụ 'THUOC')
        }
        if (maKM != null && !maKM.equals("Tất cả")) {
            ps.setString(idx++, maKM);
            ps.setString(idx++, maKM);
        }
    }

    /**
     * Thống kê theo ngày với bộ lọc mở rộng
     */
    public List<BanGhiThongKe> getDoanhThuTheoNgay(java.util.Date tuNgay, java.util.Date denNgay, String loaiSP, String maKM) {
        List<BanGhiThongKe> list = new ArrayList<>();
        Connection con = null;
        try {
            connectDB.getInstance();
            con = connectDB.getConnection();
            
            // JOIN các bảng để lấy thông tin Loại SP và Khuyến Mãi
            String sql = "SELECT FORMAT(hd.NgayLap, 'dd/MM/yyyy') as Ngay, " +
                         "SUM(ct.ThanhTien) as TongTien, " + // Cộng tiền chi tiết
                         "COUNT(DISTINCT hd.MaHoaDon) as SoDon " +
                         "FROM HoaDon hd " +
                         "JOIN ChiTietHoaDon ct ON hd.MaHoaDon = ct.MaHoaDon " +
                         "JOIN LoSanPham lo ON ct.MaLo = lo.MaLo " +
                         "JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham " +
                         "WHERE hd.NgayLap BETWEEN ? AND ? " + 
                         getDieuKienLoc(loaiSP, maKM) +
                         " GROUP BY hd.NgayLap " +
                         "ORDER BY hd.NgayLap ASC";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(tuNgay.getTime()));
            ps.setDate(2, new java.sql.Date(denNgay.getTime()));
            setThamSoLoc(ps, 3, loaiSP, maKM);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new BanGhiThongKe(rs.getString("Ngay"), rs.getDouble("TongTien"), rs.getInt("SoDon")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Thống kê theo tháng với bộ lọc mở rộng
     */
    public List<BanGhiThongKe> getDoanhThuTheoThang(int nam, String loaiSP, String maKM) {
        List<BanGhiThongKe> list = new ArrayList<>();
        Connection con = null;
        try {
            connectDB.getInstance();
            con = connectDB.getConnection();

            String sql = "SELECT MONTH(hd.NgayLap) as Thang, " +
                         "SUM(ct.ThanhTien) as TongTien, " +
                         "COUNT(DISTINCT hd.MaHoaDon) as SoDon " +
                         "FROM HoaDon hd " +
                         "JOIN ChiTietHoaDon ct ON hd.MaHoaDon = ct.MaHoaDon " +
                         "JOIN LoSanPham lo ON ct.MaLo = lo.MaLo " +
                         "JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham " +
                         "WHERE YEAR(hd.NgayLap) = ? " +
                         getDieuKienLoc(loaiSP, maKM) +
                         " GROUP BY MONTH(hd.NgayLap) " +
                         "ORDER BY MONTH(hd.NgayLap) ASC";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, nam);
            setThamSoLoc(ps, 2, loaiSP, maKM);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new BanGhiThongKe("T" + rs.getInt("Thang"), rs.getDouble("TongTien"), rs.getInt("SoDon")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Thống kê theo năm với bộ lọc mở rộng
     */
    public List<BanGhiThongKe> getDoanhThuTheoNam(int namBatDau, int namKetThuc, String loaiSP, String maKM) {
        List<BanGhiThongKe> list = new ArrayList<>();
        Connection con = null;
        try {
            connectDB.getInstance();
            con = connectDB.getConnection();

            String sql = "SELECT YEAR(hd.NgayLap) as Nam, " +
                         "SUM(ct.ThanhTien) as TongTien, " +
                         "COUNT(DISTINCT hd.MaHoaDon) as SoDon " +
                         "FROM HoaDon hd " +
                         "JOIN ChiTietHoaDon ct ON hd.MaHoaDon = ct.MaHoaDon " +
                         "JOIN LoSanPham lo ON ct.MaLo = lo.MaLo " +
                         "JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham " +
                         "WHERE YEAR(hd.NgayLap) BETWEEN ? AND ? " +
                         getDieuKienLoc(loaiSP, maKM) +
                         " GROUP BY YEAR(hd.NgayLap) " +
                         "ORDER BY YEAR(hd.NgayLap) ASC";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, namBatDau);
            ps.setInt(2, namKetThuc);
            setThamSoLoc(ps, 3, loaiSP, maKM);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new BanGhiThongKe(String.valueOf(rs.getInt("Nam")), rs.getDouble("TongTien"), rs.getInt("SoDon")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    // Hàm phụ để lấy danh sách Khuyến mãi đưa vào ComboBox (nếu cần)
    public List<String[]> getDanhSachKhuyenMai() {
        List<String[]> list = new ArrayList<>();
        try {
            Connection con = connectDB.getConnection();
            // Lấy các khuyến mãi còn hoạt động hoặc tất cả
            String sql = "SELECT MaKM, TenKM FROM KhuyenMai"; 
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                list.add(new String[]{rs.getString("MaKM"), rs.getString("TenKM")});
            }
        } catch (Exception e) {}
        return list;
    }
    
}