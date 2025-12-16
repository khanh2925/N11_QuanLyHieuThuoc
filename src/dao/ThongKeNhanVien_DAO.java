package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import database.connectDB;

public class ThongKeNhanVien_DAO {

    public static class KetQuaThongKe {
        public double tongDoanhSo = 0;
        public int soHoaDon = 0;
        public int soPhieuTra = 0;
        public double tongTienTra = 0;
        public int soPhieuHuy = 0;

        public double getGiaTriTrungBinh() {
            return soHoaDon == 0 ? 0 : tongDoanhSo / soHoaDon;
        }

        public double getTyLeHoanTra() {
            return tongDoanhSo == 0 ? 0 : (tongTienTra / tongDoanhSo) * 100;
        }
    }

    /**
     * Lấy thống kê cho nhân viên cụ thể
     * @param tuNgay Ngày bắt đầu
     * @param denNgay Ngày kết thúc
     * @param maNhanVien Mã nhân viên đang đăng nhập (Bắt buộc)
     * @param caLam Ca làm việc (0: Tất cả, 1: Sáng, 2: Chiều, 3: Tối)
     */
public KetQuaThongKe getThongKe(java.util.Date tuNgay, java.util.Date denNgay, String maNhanVien, int caLam) {
    KetQuaThongKe kq = new KetQuaThongKe();
    connectDB.getInstance();
    Connection con = connectDB.getConnection();

    java.sql.Date sqlTuNgay = new java.sql.Date(tuNgay.getTime());
    java.sql.Date sqlDenNgay = new java.sql.Date(denNgay.getTime());

    // 1. Xử lý điều kiện SQL động
    String sqlNhanVien = (maNhanVien != null) ? " AND nv.MaNhanVien = ? " : "";
    String sqlCaLam = (caLam > 0) ? " AND nv.CaLam = ? " : "";

    try {
        // --- A. Thống kê DOANH SỐ & SỐ HÓA ĐƠN ---
        String sqlBanHang = "SELECT COUNT(*) as SoDon, COALESCE(SUM(hd.TongThanhToan), 0) as DoanhThu " +
                            "FROM HoaDon hd " +
                            "JOIN NhanVien nv ON hd.MaNhanVien = nv.MaNhanVien " +
                            "WHERE hd.NgayLap BETWEEN ? AND ? " + 
                            sqlNhanVien + sqlCaLam;

        PreparedStatement psBan = con.prepareStatement(sqlBanHang);
        int index = 1;
        psBan.setDate(index++, sqlTuNgay);
        psBan.setDate(index++, sqlDenNgay);
        if (maNhanVien != null) psBan.setString(index++, maNhanVien); // Chỉ set nếu khác null
        if (caLam > 0) psBan.setInt(index++, caLam);

        ResultSet rsBan = psBan.executeQuery();
        if (rsBan.next()) {
            kq.soHoaDon = rsBan.getInt("SoDon");
            kq.tongDoanhSo = rsBan.getDouble("DoanhThu");
        }
        
        // --- B. Thống kê TRẢ HÀNG ---
        String sqlTraHang = "SELECT COUNT(*) as SoPhieu, COALESCE(SUM(pt.TongTienHoan), 0) as TienTra " +
                            "FROM PhieuTra pt " +
                            "JOIN NhanVien nv ON pt.MaNhanVien = nv.MaNhanVien " +
                            "WHERE pt.NgayLap BETWEEN ? AND ? " + 
                            sqlNhanVien + sqlCaLam;
                            
        PreparedStatement psTra = con.prepareStatement(sqlTraHang);
        index = 1;
        psTra.setDate(index++, sqlTuNgay);
        psTra.setDate(index++, sqlDenNgay);
        if (maNhanVien != null) psTra.setString(index++, maNhanVien);
        if (caLam > 0) psTra.setInt(index++, caLam);

        ResultSet rsTra = psTra.executeQuery();
        if (rsTra.next()) {
            kq.soPhieuTra = rsTra.getInt("SoPhieu");
            kq.tongTienTra = rsTra.getDouble("TienTra");
        }

        // --- C. Thống kê HỦY HÀNG ---
        String sqlHuy = "SELECT COUNT(*) as SoPhieuHuy " +
                        "FROM PhieuHuy ph " +
                        "JOIN NhanVien nv ON ph.MaNhanVien = nv.MaNhanVien " +
                        "WHERE ph.NgayLapPhieu BETWEEN ? AND ? " + 
                        sqlNhanVien + sqlCaLam;

        PreparedStatement psHuy = con.prepareStatement(sqlHuy);
        index = 1;
        psHuy.setDate(index++, sqlTuNgay);
        psHuy.setDate(index++, sqlDenNgay);
        if (maNhanVien != null) psHuy.setString(index++, maNhanVien);
        if (caLam > 0) psHuy.setInt(index++, caLam);

        ResultSet rsHuy = psHuy.executeQuery();
        if (rsHuy.next()) {
            kq.soPhieuHuy = rsHuy.getInt("SoPhieuHuy");
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return kq;
}
    public List<String[]> getDanhSachNhanVien() {
        List<String[]> list = new ArrayList<>();
        try {
            connectDB.getInstance();
            Connection con = connectDB.getConnection();
            String sql = "SELECT MaNhanVien, TenNhanVien FROM NhanVien";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                list.add(new String[]{rs.getString("MaNhanVien"), rs.getString("TenNhanVien")});
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    

    public static class ThongKeChiTietNV {
        public String maNV;
        public String tenNV;
        public int soHoaDon;
        public double doanhThu;
        public int soPhieuTra;
        public double tienTra;
        public int soPhieuHuy;

        public ThongKeChiTietNV(String maNV, String tenNV, int soHoaDon, double doanhThu, int soPhieuTra, double tienTra, int soPhieuHuy) {
            this.maNV = maNV;
            this.tenNV = tenNV;
            this.soHoaDon = soHoaDon;
            this.doanhThu = doanhThu;
            this.soPhieuTra = soPhieuTra;
            this.tienTra = tienTra;
            this.soPhieuHuy = soPhieuHuy;
        }
        
        public double getThucThu() {
            return doanhThu - tienTra;
        }
    }

    /**
     * Lấy danh sách thống kê của TẤT CẢ nhân viên trong khoảng thời gian
     * Dùng cho bảng thống kê của Quản lý
     */
    public List<ThongKeChiTietNV> getThongKeDanhSachNhanVien(java.util.Date tuNgay, java.util.Date denNgay) {
        List<ThongKeChiTietNV> list = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        java.sql.Date sqlTu = new java.sql.Date(tuNgay.getTime());
        java.sql.Date sqlDen = new java.sql.Date(denNgay.getTime());

        // Kỹ thuật: Sử dụng LEFT JOIN với các bảng con (Derived Tables) để gom nhóm dữ liệu
        String sql = """
            SELECT 
                nv.MaNhanVien, 
                nv.TenNhanVien,
                ISNULL(Ban.SoDon, 0) AS SoDon,
                ISNULL(Ban.DoanhThu, 0) AS DoanhThu,
                ISNULL(Tra.SoPhieuTra, 0) AS SoPhieuTra,
                ISNULL(Tra.TienTra, 0) AS TienTra,
                ISNULL(Huy.SoPhieuHuy, 0) AS SoPhieuHuy
            FROM NhanVien nv
            -- 1. Thống kê Bán hàng
            LEFT JOIN (
                SELECT MaNhanVien, COUNT(*) AS SoDon, SUM(TongThanhToan) AS DoanhThu
                FROM HoaDon 
                WHERE NgayLap BETWEEN ? AND ?
                GROUP BY MaNhanVien
            ) Ban ON nv.MaNhanVien = Ban.MaNhanVien
            -- 2. Thống kê Trả hàng
            LEFT JOIN (
                SELECT MaNhanVien, COUNT(*) AS SoPhieuTra, SUM(TongTienHoan) AS TienTra
                FROM PhieuTra 
                WHERE NgayLap BETWEEN ? AND ?
                GROUP BY MaNhanVien
            ) Tra ON nv.MaNhanVien = Tra.MaNhanVien
            -- 3. Thống kê Hủy hàng
            LEFT JOIN (
                SELECT MaNhanVien, COUNT(*) AS SoPhieuHuy
                FROM PhieuHuy 
                WHERE NgayLapPhieu BETWEEN ? AND ?
                GROUP BY MaNhanVien
            ) Huy ON nv.MaNhanVien = Huy.MaNhanVien
            
            -- Chỉ lấy nhân viên đang hoạt động hoặc có phát sinh giao dịch
            WHERE nv.TrangThai = 1 OR Ban.SoDon > 0 OR Tra.SoPhieuTra > 0
            ORDER BY DoanhThu DESC
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            // Set tham số cho Ban
            ps.setDate(1, sqlTu);
            ps.setDate(2, sqlDen);
            // Set tham số cho Tra
            ps.setDate(3, sqlTu);
            ps.setDate(4, sqlDen);
            // Set tham số cho Huy
            ps.setDate(5, sqlTu);
            ps.setDate(6, sqlDen);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ThongKeChiTietNV row = new ThongKeChiTietNV(
                        rs.getString("MaNhanVien"),
                        rs.getString("TenNhanVien"),
                        rs.getInt("SoDon"),
                        rs.getDouble("DoanhThu"),
                        rs.getInt("SoPhieuTra"),
                        rs.getDouble("TienTra"),
                        rs.getInt("SoPhieuHuy")
                    );
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
}