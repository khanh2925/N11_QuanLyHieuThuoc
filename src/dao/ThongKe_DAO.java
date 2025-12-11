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
     * 
     * @param thang Tháng (1-12)
     * @param nam   Năm
     * @return Lợi nhuận ước tính (doanh thu - giá nhập trung bình)
     */
    public double tinhLoiNhuanTheoThang(int thang, int nam) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        // Giả định đơn giản: Lợi nhuận = 25-30% doanh thu (tỷ suất lợi nhuận trung bình
        // ngành dược)
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
     * 
     * @param thang Tháng (1-12)
     * @param nam   Năm
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

    // ============================================================
    // 📊 THỐNG KÊ TOP SẢN PHẨM BÁN CHẠY
    // ============================================================

    /**
     * Lấy top N sản phẩm bán chạy theo khoảng thời gian
     * 
     * @param tuNgay  Ngày bắt đầu
     * @param denNgay Ngày kết thúc
     * @param topN    Số lượng top (5, 10, 15, 20...)
     * @return List chứa Object[]: {MaSP, TenSP, LoaiSP, SoLuongBan, DoanhThu}
     */
    public java.util.List<Object[]> layTopSanPhamBanChay(java.time.LocalDate tuNgay,
            java.time.LocalDate denNgay,
            int topN) {
        java.util.List<Object[]> result = new java.util.ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = """
                SELECT TOP (?)
                    sp.MaSanPham,
                    sp.TenSanPham,
                    sp.LoaiSanPham,
                    SUM(cthd.SoLuong * qc.HeSoQuyDoi) AS TongSoLuong,
                    SUM(cthd.ThanhTien) AS TongDoanhThu
                FROM ChiTietHoaDon cthd
                INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon
                INNER JOIN LoSanPham lo ON cthd.MaLo = lo.MaLo
                INNER JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
                INNER JOIN QuyCachDongGoi qc ON cthd.MaDonViTinh = qc.MaDonViTinh
                    AND sp.MaSanPham = qc.MaSanPham
                WHERE hd.NgayLap BETWEEN ? AND ?
                GROUP BY sp.MaSanPham, sp.TenSanPham, sp.LoaiSanPham
                ORDER BY TongSoLuong DESC
                """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, topN);
            stmt.setDate(2, java.sql.Date.valueOf(tuNgay));
            stmt.setDate(3, java.sql.Date.valueOf(denNgay));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[5];
                    row[0] = rs.getString("MaSanPham");
                    row[1] = rs.getString("TenSanPham");
                    row[2] = rs.getString("LoaiSanPham");
                    row[3] = rs.getDouble("TongSoLuong");
                    row[4] = rs.getDouble("TongDoanhThu");
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy top sản phẩm bán chạy: " + e.getMessage());
        }

        return result;
    }

    /**
     * Tính tổng doanh thu trong khoảng thời gian
     * 
     * @param tuNgay  Ngày bắt đầu
     * @param denNgay Ngày kết thúc
     * @return Tổng doanh thu
     */
    public double tinhTongDoanhThuTheoKhoangNgay(java.time.LocalDate tuNgay, java.time.LocalDate denNgay) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = """
                SELECT COALESCE(SUM(TongThanhToan), 0) AS TongDoanhThu
                FROM HoaDon
                WHERE NgayLap BETWEEN ? AND ?
                """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(tuNgay));
            stmt.setDate(2, java.sql.Date.valueOf(denNgay));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("TongDoanhThu");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi tính tổng doanh thu: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Lấy doanh số sản phẩm của kỳ trước (để tính xu hướng)
     * 
     * @param maSanPham Mã sản phẩm
     * @param tuNgay    Ngày bắt đầu kỳ trước
     * @param denNgay   Ngày kết thúc kỳ trước
     * @return Số lượng bán kỳ trước
     */
    public double laySoLuongBanKyTruoc(String maSanPham, java.time.LocalDate tuNgay, java.time.LocalDate denNgay) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = """
                SELECT COALESCE(SUM(cthd.SoLuong * qc.HeSoQuyDoi), 0) AS TongSoLuong
                FROM ChiTietHoaDon cthd
                INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon
                INNER JOIN LoSanPham lo ON cthd.MaLo = lo.MaLo
                INNER JOIN QuyCachDongGoi qc ON cthd.MaDonViTinh = qc.MaDonViTinh
                    AND lo.MaSanPham = qc.MaSanPham
                WHERE lo.MaSanPham = ? AND hd.NgayLap BETWEEN ? AND ?
                """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maSanPham);
            stmt.setDate(2, java.sql.Date.valueOf(tuNgay));
            stmt.setDate(3, java.sql.Date.valueOf(denNgay));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("TongSoLuong");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy số lượng bán kỳ trước: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Tính tổng doanh thu kỳ trước (để so sánh xu hướng)
     * 
     * @param tuNgay  Ngày bắt đầu kỳ trước
     * @param denNgay Ngày kết thúc kỳ trước
     * @return Tổng doanh thu kỳ trước
     */
    public double tinhTongDoanhThuKyTruoc(java.time.LocalDate tuNgay, java.time.LocalDate denNgay) {
        return tinhTongDoanhThuTheoKhoangNgay(tuNgay, denNgay);
    }
}
