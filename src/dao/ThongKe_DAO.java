package dao;

import database.connectDB;
import java.sql.*;

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
    
    //Data Transfer Object (DTO) 
    public static class ThongKeHoaDonNgay {
        private final int soHoaDon;
        private final double tongTien;

        public ThongKeHoaDonNgay(int soHoaDon, double tongTien) {
            this.soHoaDon = soHoaDon;
            this.tongTien = tongTien;
        }

        public int getSoHoaDon() {
            return soHoaDon;
        }

        public double getTongTien() {
            return tongTien;
        }
    }
    

    public ThongKeHoaDonNgay thongKeHoaDonHomNayCuaNhanVien(String maNhanVien) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = """
            SELECT 
                COUNT(*) AS SoHoaDon,
                COALESCE(SUM(TongThanhToan), 0) AS TongTien
            FROM HoaDon
            WHERE MaNhanVien = ?
              AND CAST(NgayLap AS DATE) = CAST(GETDATE() AS DATE)
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int soHoaDon = rs.getInt("SoHoaDon");
                    double tongTien = rs.getDouble("TongTien");
                    return new ThongKeHoaDonNgay(soHoaDon, tongTien);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi thống kê hoá đơn hôm nay: " + e.getMessage());
        }

        return new ThongKeHoaDonNgay(0, 0);
    }

    
}
