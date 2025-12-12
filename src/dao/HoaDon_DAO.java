package dao;

import database.connectDB;
import entity.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_DAO {

    private final NhanVien_DAO nhanVienDAO;
    private final KhachHang_DAO khachHangDAO;
    private final ChiTietHoaDon_DAO chiTietHoaDonDAO;
    private final QuyCachDongGoi_DAO quyCachDongGoiDAO;
    private final KhuyenMai_DAO khuyenMaiDAO;

    public HoaDon_DAO() {
        this.nhanVienDAO = new NhanVien_DAO();
        this.khachHangDAO = new KhachHang_DAO();
        this.chiTietHoaDonDAO = new ChiTietHoaDon_DAO();
        this.quyCachDongGoiDAO = new QuyCachDongGoi_DAO();
        this.khuyenMaiDAO = new KhuyenMai_DAO();
    }


    public HoaDon timHoaDonTheoMa(String maHD) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connectDB.getInstance();
            con = connectDB.getConnection();

            String sql = "SELECT * FROM HoaDon WHERE MaHoaDon = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maHD);
            rs = stmt.executeQuery();

            HoaDon hd = new HoaDon();

            String maNV = "";
            String maKH = "";
            LocalDate ngayLap = null;
            String maKM = "";
            double tongTien = 0.0;
            boolean thuocKeDon = false;

            if (rs.next()) {
                maNV = rs.getString("MaNhanVien");
                maKH = rs.getString("MaKhachHang");
                ngayLap = rs.getDate("NgayLap").toLocalDate();
                maKM = rs.getString("MaKM");
                tongTien = rs.getDouble("TongThanhToan");
                thuocKeDon = rs.getBoolean("ThuocKeDon");

                try {
                    var setTongTien = HoaDon.class.getDeclaredField("tongTien");
                    setTongTien.setAccessible(true);
                    setTongTien.set(hd, tongTien);
                } catch (Exception ignore) {}
            } else {
                return null; // Không tìm thấy thì trả về null ngay
            }

            // Đóng ResultSet và Statement ngay tại đây để giải phóng kết nối
            // trước khi gọi các DAO con (vì các DAO con cũng dùng chung connect)
            rs.close();
            stmt.close();

            // Bây giờ mới gọi các hàm tìm kiếm khác (an toàn vì stmt cũ đã đóng)
            NhanVien nhanVien = nhanVienDAO.timNhanVienTheoMa(maNV);
            KhachHang khachHang = khachHangDAO.timKhachHangTheoMa(maKH);
            KhuyenMai khuyenMai = khuyenMaiDAO.timKhuyenMaiTheoMa(maKM);
            List<ChiTietHoaDon> dsCT = chiTietHoaDonDAO.layDanhSachChiTietTheoMaHD(maHD);

            hd.setMaHoaDon(maHD);
            hd.setNhanVien(nhanVien);
            hd.setKhachHang(khachHang);
            hd.setNgayLap(ngayLap);
            hd.setKhuyenMai(khuyenMai);
            hd.setDanhSachChiTiet(dsCT);
            hd.setThuocKeDon(thuocKeDon);

            return hd;
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tìm hóa đơn theo mã: " + e.getMessage());
        } 
        // Lưu ý: Không đóng 'con' ở đây nếu dùng Singleton connection
        return null;
    }

    /** 📜 Lấy toàn bộ hóa đơn (ĐÃ SỬA LỖI BUSY CONNECTION) */
    public List<HoaDon> layTatCaHoaDon() {
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

        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy danh sách hóa đơn: " + e.getMessage());
        }
        // Không cần finally close rs/st ở đây vì đã close ở giữa rồi
        return dsHD;
    }

    // ... (Giữ nguyên hàm themHoaDon và taoMaHoaDon) ...
    public boolean themHoaDon(HoaDon hd) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmtHD = null;
        PreparedStatement stmtCTHD = null;
        PreparedStatement stmtUpdateTon = null;

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

            for (ChiTietHoaDon cthd : hd.getDanhSachChiTiet()) {
                stmtCTHD.setString(1, hd.getMaHoaDon());
                stmtCTHD.setString(2, cthd.getLoSanPham().getMaLo());
                stmtCTHD.setString(3, cthd.getDonViTinh().getMaDonViTinh());
                stmtCTHD.setDouble(4, cthd.getSoLuong());
                stmtCTHD.setDouble(5, cthd.getGiaBan());
                stmtCTHD.setDouble(6, cthd.getThanhTien());
                if (cthd.getKhuyenMai() != null) stmtCTHD.setString(7, cthd.getKhuyenMai().getMaKM()); else stmtCTHD.setNull(7, Types.CHAR);
                stmtCTHD.addBatch();

                QuyCachDongGoi qc = quyCachDongGoiDAO.timQuyCachTheoSanPhamVaDonVi(cthd.getLoSanPham().getSanPham().getMaSanPham(), cthd.getDonViTinh().getMaDonViTinh());
                if (qc == null) throw new SQLException("Không tìm thấy quy cách đóng gói");
                double soLuongBanBase = cthd.getSoLuong() * qc.getHeSoQuyDoi();

                stmtUpdateTon.setDouble(1, soLuongBanBase);
                stmtUpdateTon.setString(2, cthd.getLoSanPham().getMaLo());
                stmtUpdateTon.setDouble(3, soLuongBanBase);
                if (stmtUpdateTon.executeUpdate() == 0) throw new SQLException("Tồn kho không đủ");
            }
            stmtCTHD.executeBatch();
            con.commit();
            return true;
        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ignore) {}
            return false;
        } finally {
            try {
                if (stmtHD != null) stmtHD.close();
                if (stmtCTHD != null) stmtCTHD.close();
                if (stmtUpdateTon != null) stmtUpdateTon.close();
                if (con != null) con.setAutoCommit(true);
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