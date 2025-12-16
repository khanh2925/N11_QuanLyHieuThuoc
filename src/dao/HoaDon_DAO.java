package dao;

import database.connectDB;
import entity.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_DAO {
    // ============ CACHE LAYER ============
    // Cache toàn bộ hóa đơn (dùng chung toàn ứng dụng)
    private static List<HoaDon> cacheAllHoaDon = null;
    private final ChiTietHoaDon_DAO chiTietHoaDonDAO;

    public HoaDon_DAO() {
        this.chiTietHoaDonDAO = new ChiTietHoaDon_DAO();
    }


    public HoaDon timHoaDonTheoMa(String maHD) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connectDB.getInstance();
            con = connectDB.getConnection();

            String sql = """
                SELECT 
                    hd.MaHoaDon, hd.NgayLap, hd.TongThanhToan, hd.ThuocKeDon,
                    -- NhanVien
                    nv.MaNhanVien, nv.TenNhanVien, nv.QuanLy, nv.CaLam,
                    -- KhachHang
                    kh.MaKhachHang, kh.TenKhachHang, kh.GioiTinh, kh.SoDienThoai, kh.NgaySinh, kh.HoatDong,
                    -- KhuyenMai
                    km.MaKM, km.TenKM, km.GiaTri, km.HinhThuc
                FROM HoaDon hd
                LEFT JOIN NhanVien nv ON hd.MaNhanVien = nv.MaNhanVien
                LEFT JOIN KhachHang kh ON hd.MaKhachHang = kh.MaKhachHang
                LEFT JOIN KhuyenMai km ON hd.MaKM = km.MaKM
                WHERE hd.MaHoaDon = ?
                """;
                
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maHD);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                return null; // Không tìm thấy
            }

            // ========== TẠO NHANVIEN TỪ RESULTSET ==========
            NhanVien nhanVien = new NhanVien();
            nhanVien.setMaNhanVien(rs.getString("MaNhanVien"));
            nhanVien.setTenNhanVien(rs.getString("TenNhanVien"));
            nhanVien.setQuanLy(rs.getBoolean("QuanLy"));
            nhanVien.setCaLam(rs.getInt("CaLam"));

            // ========== TẠO KHACHHANG TỪ RESULTSET ==========
            KhachHang khachHang = new KhachHang();
            khachHang.setMaKhachHang(rs.getString("MaKhachHang"));
            khachHang.setTenKhachHang(rs.getString("TenKhachHang"));
            khachHang.setGioiTinh(rs.getBoolean("GioiTinh"));
            khachHang.setSoDienThoai(rs.getString("SoDienThoai"));
            java.sql.Date ngaySinhKH = rs.getDate("NgaySinh");
            if (ngaySinhKH != null) {
                khachHang.setNgaySinh(ngaySinhKH.toLocalDate());
            }
            khachHang.setHoatDong(rs.getBoolean("HoatDong"));

            // ========== KHUYẾN MÃI ==========
            KhuyenMai khuyenMai = null;
            if (rs.getString("MaKM") != null) {
                khuyenMai = new KhuyenMai();
                khuyenMai.setMaKM(rs.getString("MaKM"));
                khuyenMai.setTenKM(rs.getString("TenKM"));
                khuyenMai.setGiaTri(rs.getDouble("GiaTri"));
                // HinhThuc có thể cần xử lý enum nếu cần
            }

            // ========== TẠO HOADON TỪ RESULTSET ==========
            LocalDate ngayLap = rs.getDate("NgayLap").toLocalDate();
            double tongTien = rs.getDouble("TongThanhToan");
            boolean thuocKeDon = rs.getBoolean("ThuocKeDon");

            // Đóng rs, stmt trước khi gọi layDanhSachChiTietTheoMaHD
            rs.close();
            stmt.close();

            // ========== LẤY CHI TIẾT HÓA ĐƠN ==========
            List<ChiTietHoaDon> dsCT = chiTietHoaDonDAO.layDanhSachChiTietTheoMaHD(maHD);

            HoaDon hd = new HoaDon();
            hd.setMaHoaDon(maHD);
            hd.setNhanVien(nhanVien);
            hd.setKhachHang(khachHang);
            hd.setNgayLap(ngayLap);
            hd.setKhuyenMai(khuyenMai);
            hd.setDanhSachChiTiet(dsCT);
            hd.setThuocKeDon(thuocKeDon);

            // Set tongTien bằng reflection như code cũ
            try {
                var setTongTien = HoaDon.class.getDeclaredField("tongTien");
                setTongTien.setAccessible(true);
                setTongTien.set(hd, tongTien);
            } catch (Exception ignore) {}

            return hd;
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tìm hóa đơn theo mã: " + e.getMessage());
        } 
        // Lưu ý: Không đóng 'con' ở đây nếu dùng Singleton connection
        return null;
    }

    /** 📜 Lấy toàn bộ hóa đơn (CÓ CACHE - TỐI ƯU) */
    public List<HoaDon> layTatCaHoaDon() {
        // Nếu cache đã có dữ liệu → Return cache (clone để tránh modify trực tiếp)
        if (cacheAllHoaDon != null && !cacheAllHoaDon.isEmpty()) {
            return new ArrayList<>(cacheAllHoaDon);
        }
        
        // Cache rỗng → Query DB và lưu vào cache
        List<HoaDon> dsHD = new ArrayList<>();
        List<String> dsMaHD = new ArrayList<>(); // Bước 1: Lưu tạm mã vào đây

        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        Statement st = null;
        ResultSet rs = null;

        try {
            st = con.createStatement();
            rs = st.executeQuery("SELECT MaHoaDon FROM HoaDon ORDER BY NgayLap DESC");

            // 1. Chỉ lấy danh sách MÃ HÓA ĐƠN trước
            while (rs.next()) {
                dsMaHD.add(rs.getString("MaHoaDon"));
            }
            
            // Đóng ngay ResultSet và Statement để giải phóng kết nối
            rs.close();
            st.close();

            // 2. Bây giờ mới dùng vòng lặp để lấy chi tiết từng hóa đơn
            // Lúc này kết nối đã rảnh tay, không bị lỗi nested query
            for (String maHD : dsMaHD) {
                HoaDon hd = timHoaDonTheoMa(maHD);
                if (hd != null) {
                    dsHD.add(hd);
                }
            }
            // Lưu vào cache để lần sau không cần query nữa
            cacheAllHoaDon = dsHD;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy danh sách hóa đơn: " + e.getMessage());
        }
        // Không cần finally close rs/st ở đây vì đã close ở giữa rồi
        return new ArrayList<>(dsHD); // Clone để tránh modify cache
    }

  
    public boolean themHoaDon(HoaDon hd) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmtHD = null;
        PreparedStatement stmtCTHD = null;
        PreparedStatement stmtUpdateTon = null;
        PreparedStatement stmtQC = null;

        try {
            con.setAutoCommit(false);
            hd.capNhatDuLieuHoaDon();

            double tongThanhToan = hd.getTongThanhToan();
            double soTienGiamKM = hd.getSoTienGiamKhuyenMai();
            KhuyenMai kmHD = hd.getKhuyenMai();

            String sqlHD = "INSERT INTO HoaDon (MaHoaDon, NgayLap, MaNhanVien, MaKhachHang, TongThanhToan, MaKM, SoTienGiamKhuyenMai, ThuocKeDon) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            stmtHD = con.prepareStatement(sqlHD);
            stmtHD.setString(1, hd.getMaHoaDon());
            stmtHD.setDate(2, Date.valueOf(hd.getNgayLap()));
            stmtHD.setString(3, hd.getNhanVien().getMaNhanVien());
            stmtHD.setString(4, hd.getKhachHang().getMaKhachHang());
            stmtHD.setDouble(5, tongThanhToan);
            if (kmHD != null) stmtHD.setString(6, kmHD.getMaKM()); else stmtHD.setNull(6, Types.CHAR);
            stmtHD.setDouble(7, soTienGiamKM);
            stmtHD.setBoolean(8, hd.isThuocKeDon());
            stmtHD.executeUpdate();

            String sqlCT = "INSERT INTO ChiTietHoaDon (MaHoaDon, MaLo, MaDonViTinh, SoLuong, GiaBan, ThanhTien, MaKM) VALUES (?, ?, ?, ?, ?, ?, ?)";
            stmtCTHD = con.prepareStatement(sqlCT);

            String sqlUpdateTon = "UPDATE LoSanPham SET SoLuongTon = SoLuongTon - ? WHERE MaLo = ? AND SoLuongTon >= ?";
            stmtUpdateTon = con.prepareStatement(sqlUpdateTon);

            // ✅ Query quy cách trong cùng transaction
            String sqlQC = "SELECT HeSoQuyDoi FROM QuyCachDongGoi WHERE MaSanPham = ? AND MaDonViTinh = ?";
            stmtQC = con.prepareStatement(sqlQC);

            for (ChiTietHoaDon cthd : hd.getDanhSachChiTiet()) {
                stmtCTHD.setString(1, hd.getMaHoaDon());
                stmtCTHD.setString(2, cthd.getLoSanPham().getMaLo());
                stmtCTHD.setString(3, cthd.getDonViTinh().getMaDonViTinh());
                stmtCTHD.setDouble(4, cthd.getSoLuong());
                stmtCTHD.setDouble(5, cthd.getGiaBan());
                stmtCTHD.setDouble(6, cthd.getThanhTien());
                if (cthd.getKhuyenMai() != null) stmtCTHD.setString(7, cthd.getKhuyenMai().getMaKM()); else stmtCTHD.setNull(7, Types.CHAR);
                stmtCTHD.addBatch();

                // ✅ Lấy hệ số quy đổi từ trong transaction
                stmtQC.setString(1, cthd.getLoSanPham().getSanPham().getMaSanPham());
                stmtQC.setString(2, cthd.getDonViTinh().getMaDonViTinh());
                ResultSet rsQC = stmtQC.executeQuery();
                
                double heSoQuyDoi = 1.0;
                if (rsQC.next()) {
                    heSoQuyDoi = rsQC.getDouble("HeSoQuyDoi");
                } else {
                    rsQC.close();
                    throw new SQLException("Không tìm thấy quy cách đóng gói");
                }
                rsQC.close();
                
                double soLuongBanBase = cthd.getSoLuong() * heSoQuyDoi;

                stmtUpdateTon.setDouble(1, soLuongBanBase);
                stmtUpdateTon.setString(2, cthd.getLoSanPham().getMaLo());
                stmtUpdateTon.setDouble(3, soLuongBanBase);
                if (stmtUpdateTon.executeUpdate() == 0) throw new SQLException("Tồn kho không đủ");
            }
            stmtCTHD.executeBatch();
            con.commit();
            
            // ✅ Cập nhật cache: Thêm hóa đơn mới vào đầu danh sách
            if (cacheAllHoaDon != null) {
                cacheAllHoaDon.add(0, hd); // Thêm vào đầu (mới nhất)
            }
            
            return true;
        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ignore) {}
            return false;
        } finally {
            try {
                if (stmtHD != null) stmtHD.close();
                if (stmtCTHD != null) stmtCTHD.close();
                if (stmtUpdateTon != null) stmtUpdateTon.close();
                if (stmtQC != null) stmtQC.close();
            } catch (SQLException ignore) {}
        }
    }
    
    public String taoMaHoaDon() {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("SELECT COUNT(*) FROM HoaDon WHERE MaHoaDon LIKE ?")) {
            String prefix = "HD-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
            stmt.setString(1, prefix + "%");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return String.format("%s%04d", prefix, rs.getInt(1) + 1);
        } catch (SQLException e) { e.printStackTrace(); }
        return "HD-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-0001";
    }

    /** 🔍 Tìm hóa đơn theo SĐT (ĐÃ SỬA LỖI BUSY CONNECTION) */
    public List<HoaDon> timHoaDonTheoSoDienThoai(String soDienThoai) {
        List<HoaDon> dsHD = new ArrayList<>();
        List<String> dsMaHD = new ArrayList<>(); // Bước 1: Lưu mã

        String sql = """
                SELECT hd.MaHoaDon
                FROM HoaDon hd
                JOIN KhachHang kh ON hd.MaKhachHang = kh.MaKhachHang
                WHERE kh.SoDienThoai = ?
                ORDER BY hd.NgayLap DESC
                """;

        try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, soDienThoai);
            
            // 1. Lấy danh sách mã trước
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dsMaHD.add(rs.getString("MaHoaDon"));
                }
            } // rs tự đóng ở đây nhờ try-with-resources
            
            // ps tự đóng ở đây
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi tìm hóa đơn theo SĐT: " + e.getMessage());
        }

        // 2. Duyệt danh sách mã để lấy chi tiết (Kết nối đã rảnh)
        for (String maHD : dsMaHD) {
            HoaDon hd = timHoaDonTheoMa(maHD);
            if (hd != null) {
                dsHD.add(hd);
            }
        }

        return dsHD;
    }
    // ========== PHẦN THỐNG KÊ CHO DASHBOARD ==========
    
    /**
     * Lấy tổng doanh thu theo tháng và năm
     * @param thang Tháng (1-12)
     * @param nam Năm (VD: 2024, 2025)
     * @return Tổng doanh thu trong tháng đó
     */
    public double layDoanhThuTheoThang(int thang, int nam) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        
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
                    return rs.getDouble("TongDoanhThu");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy doanh thu theo tháng: " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * 🔄 Force refresh cache - Xóa cache và load lại từ DB
     * Dùng khi cần đồng bộ dữ liệu real-time (VD: sau khi import data)
     */
    public void refreshCache() {
        cacheAllHoaDon = null;
        layTatCaHoaDon(); // Load lại ngay
    }
    /**
     * Đếm số hóa đơn theo tháng và năm
     * @param thang Tháng (1-12)
     * @param nam Năm
     * @return Số lượng hóa đơn
     */
    public int demSoHoaDonTheoThang(int thang, int nam) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        
        String sql = """
                SELECT COUNT(*) AS SoLuong
                FROM HoaDon
                WHERE MONTH(NgayLap) = ? AND YEAR(NgayLap) = ?
                """;
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("SoLuong");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi đếm số hóa đơn theo tháng: " + e.getMessage());
        }
        return 0;
    }
    
    
}