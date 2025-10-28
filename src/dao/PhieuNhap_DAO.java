package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List; // Vẫn dùng List cho kiểu trả về và dsChiTiet

import connectDB.connectDB;
import entity.ChiTietPhieuNhap;
import entity.LoSanPham;
import entity.NhaCungCap;
import entity.NhanVien;
import entity.PhieuNhap;

public class PhieuNhap_DAO {

    public PhieuNhap_DAO() {
    }

    /**
     * Lấy danh sách tất cả phiếu nhập (chế độ xem tóm tắt, không kèm chi tiết).
     * Dùng để hiển thị danh sách.
     * Đã đổi tên từ layTatCaPhieuNhap.
     */
    public List<PhieuNhap> layDanhSachPhieuNhap() {
        List<PhieuNhap> dsPhieuNhap = new ArrayList<>();
        // Theo form SanPham_DAO: Lấy connection bên trong
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT pn.MaPhieuNhap, pn.NgayNhap, pn.TongTien, " +
                     "nv.MaNhanVien, nv.TenNhanVien, " +
                     "ncc.MaNhaCungCap, ncc.TenNhaCungCap " +
                     "FROM PhieuNhap pn " +
                     "JOIN NhanVien nv ON pn.MaNhanVien = nv.MaNhanVien " +
                     "JOIN NhaCungCap ncc ON pn.MaNhaCungCap = ncc.MaNhaCungCap " +
                     "ORDER BY pn.NgayNhap DESC";

        // Theo form SanPham_DAO: Dùng try-with-resources
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                NhanVien nv = new NhanVien(rs.getString("MaNhanVien"), rs.getString("TenNhanVien"));
                
                NhaCungCap ncc = new NhaCungCap();
                ncc.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
                ncc.setTenNhaCungCap(rs.getString("TenNhaCungCap"));

                PhieuNhap pn = new PhieuNhap();
                pn.setMaPhieuNhap(rs.getString("MaPhieuNhap"));
                pn.setNgayNhap(rs.getDate("NgayNhap").toLocalDate());
                pn.setNhanVien(nv);
                pn.setNhaCungCap(ncc);
                
                // Dùng setter chuẩn đã thêm vào entity PhieuNhap
                pn.setTongTien(rs.getDouble("TongTien"));

                dsPhieuNhap.add(pn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) { // Bắt các lỗi khác từ setter nếu có
            e.printStackTrace();
        }
        // try-with-resources tự đóng stmt và rs
        return dsPhieuNhap;
    }

    /**
     * Tìm một phiếu nhập theo mã, bao gồm tất cả chi tiết phiếu nhập.
     * @param maPhieuNhap Mã phiếu nhập cần tìm.
     * @return PhieuNhap (đầy đủ chi tiết) hoặc null nếu không tìm thấy.
     */
    public PhieuNhap timPhieuNhapTheoMa(String maPhieuNhap) {
        PhieuNhap pn = null;
        // Theo form SanPham_DAO: Lấy connection bên trong
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT pn.NgayNhap, pn.TongTien, " +
                     "nv.MaNhanVien, nv.TenNhanVien, " +
                     "ncc.MaNhaCungCap, ncc.TenNhaCungCap " +
                     "FROM PhieuNhap pn " +
                     "JOIN NhanVien nv ON pn.MaNhanVien = nv.MaNhanVien " +
                     "JOIN NhaCungCap ncc ON pn.MaNhaCungCap = ncc.MaNhaCungCap " +
                     "WHERE pn.MaPhieuNhap = ?";

        // Theo form SanPham_DAO: Dùng try-with-resources
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maPhieuNhap);
            
            try(ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    NhanVien nv = new NhanVien(rs.getString("MaNhanVien"), rs.getString("TenNhanVien"));
                    
                    NhaCungCap ncc = new NhaCungCap();
                    ncc.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
                    ncc.setTenNhaCungCap(rs.getString("TenNhaCungCap"));

                    pn = new PhieuNhap();
                    pn.setMaPhieuNhap(maPhieuNhap);
                    pn.setNgayNhap(rs.getDate("NgayNhap").toLocalDate());
                    pn.setNhanVien(nv);
                    pn.setNhaCungCap(ncc);

                    // Lấy danh sách chi tiết
                    ChiTietPhieuNhap_DAO ctpnDAO = new ChiTietPhieuNhap_DAO();
                    // Gọi đúng tên phương thức tiếng Việt của ChiTietPhieuNhap_DAO
                    List<ChiTietPhieuNhap> dsChiTiet = ctpnDAO.timKiemChiTietPhieuNhapBangMa(maPhieuNhap);
                    pn.setChiTietPhieuNhapList(dsChiTiet); 
                    // Entity tự tính tổng tiền khi set list chi tiết
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // try-with-resources tự đóng stmt và rs
        return pn;
    }

    /**
     * Thêm một phiếu nhập mới (sử dụng transaction).
     * Bao gồm việc thêm PhieuNhap, LoSanPham, và ChiTietPhieuNhap.
     * @param pn Đối tượng PhieuNhap chứa đầy đủ thông tin.
     * @return true nếu thêm thành công, false nếu thất bại.
     */
    public boolean themPhieuNhap(PhieuNhap pn) {
        // Theo form SanPham_DAO: Lấy connection bên trong
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        
        // Transaction cần quản lý thủ công -> giữ nguyên cấu trúc try-catch-finally
        PreparedStatement stmtPhieuNhap = null;
        PreparedStatement stmtLoSanPham = null;
        PreparedStatement stmtChiTiet = null;
        
        try {
            con.setAutoCommit(false); // Bắt đầu transaction

            // 1. Thêm PhieuNhap
            String sqlPhieuNhap = "INSERT INTO PhieuNhap (MaPhieuNhap, NgayNhap, MaNhaCungCap, MaNhanVien, TongTien) " +
                                  "VALUES (?, ?, ?, ?, ?)";
            stmtPhieuNhap = con.prepareStatement(sqlPhieuNhap);
            stmtPhieuNhap.setString(1, pn.getMaPhieuNhap());
            stmtPhieuNhap.setDate(2, Date.valueOf(pn.getNgayNhap()));
            stmtPhieuNhap.setString(3, pn.getNhaCungCap().getMaNhaCungCap());
            stmtPhieuNhap.setString(4, pn.getNhanVien().getMaNhanVien());
            stmtPhieuNhap.setDouble(5, pn.getTongTien()); // Lấy tổng tiền đã tính từ entity
            stmtPhieuNhap.executeUpdate();

            // 2. Thêm LoSanPham
            String sqlLoSanPham = "INSERT INTO LoSanPham (MaLo, HanSuDung, SoLuongNhap, SoLuongTon, MaSanPham) " +
                                  "VALUES (?, ?, ?, ?, ?)";
            stmtLoSanPham = con.prepareStatement(sqlLoSanPham);

            // 3. Thêm ChiTietPhieuNhap
            String sqlChiTiet = "INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaLo, SoLuongNhap, DonGiaNhap) " +
                                "VALUES (?, ?, ?, ?)";
            stmtChiTiet = con.prepareStatement(sqlChiTiet);

            for (ChiTietPhieuNhap ctpn : pn.getChiTietPhieuNhapList()) {
                LoSanPham lo = ctpn.getLoSanPham();

                // Batch LoSanPham
                stmtLoSanPham.setString(1, lo.getMaLo());
                stmtLoSanPham.setDate(2, Date.valueOf(lo.getHanSuDung()));
                stmtLoSanPham.setInt(3, lo.getSoLuongNhap());
                stmtLoSanPham.setInt(4, lo.getSoLuongTon()); // SoLuongTon = SoLuongNhap khi mới tạo
                stmtLoSanPham.setString(5, lo.getSanPham().getMaSanPham());
                stmtLoSanPham.addBatch();

                // Batch ChiTietPhieuNhap
                stmtChiTiet.setString(1, pn.getMaPhieuNhap());
                stmtChiTiet.setString(2, lo.getMaLo());
                stmtChiTiet.setInt(3, ctpn.getSoLuongNhap());
                stmtChiTiet.setDouble(4, ctpn.getDonGiaNhap());
                stmtChiTiet.addBatch();
            }

            stmtLoSanPham.executeBatch(); 
            stmtChiTiet.executeBatch();   

            con.commit(); // Hoàn tất transaction
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback(); 
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (stmtPhieuNhap != null) stmtPhieuNhap.close();
                if (stmtLoSanPham != null) stmtLoSanPham.close();
                if (stmtChiTiet != null) stmtChiTiet.close();
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Tạo mã phiếu nhập tự động (PNxxxxxxx).
     * @return Mã phiếu nhập mới.
     */
    public String taoMaPhieuNhap() {
        // Theo form SanPham_DAO: Lấy connection bên trong
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        
        String sql = "SELECT TOP 1 MaPhieuNhap FROM PhieuNhap ORDER BY MaPhieuNhap DESC";

        // Theo form SanPham_DAO: Dùng try-with-resources
        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String maCuoi = rs.getString("MaPhieuNhap"); 
                String soCuoi = maCuoi.substring(2); 
                int soMoi = Integer.parseInt(soCuoi) + 1; 
                return String.format("PN%07d", soMoi); 
            } else {
                return "PN0000001"; // Phiếu đầu tiên
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // try-with-resources tự đóng stmt và rs
        
        return "PN0000001"; // Trả về mã đầu tiên nếu có lỗi
    }
    public List<PhieuNhap> timKiemPhieuNhap(String keyword, java.util.Date tuNgay, java.util.Date denNgay) {
        List<PhieuNhap> dsPhieuNhap = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT pn.MaPhieuNhap, pn.NgayNhap, pn.TongTien, " +
                     "nv.MaNhanVien, nv.TenNhanVien, " +
                     "ncc.MaNhaCungCap, ncc.TenNhaCungCap " +
                     "FROM PhieuNhap pn " +
                     "JOIN NhanVien nv ON pn.MaNhanVien = nv.MaNhanVien " +
                     "JOIN NhaCungCap ncc ON pn.MaNhaCungCap = ncc.MaNhaCungCap " +
                     "WHERE (pn.MaPhieuNhap LIKE ? OR ncc.TenNhaCungCap LIKE ? OR nv.TenNhanVien LIKE ?) " +
                     "AND pn.NgayNhap BETWEEN ? AND ?";

        try {
            stmt = con.prepareStatement(sql);
            String keywordParam = "%" + keyword + "%";
            stmt.setString(1, keywordParam);
            stmt.setString(2, keywordParam);
            stmt.setString(3, keywordParam);
            stmt.setDate(4, new java.sql.Date(tuNgay.getTime()));
            stmt.setDate(5, new java.sql.Date(denNgay.getTime()));
            
            rs = stmt.executeQuery();

            while (rs.next()) {
                NhanVien nv = new NhanVien(rs.getString("MaNhanVien"));
                nv.setTenNhanVien(rs.getString("TenNhanVien"));
                
                NhaCungCap ncc = new NhaCungCap(rs.getString("MaNhaCungCap"));
                ncc.setTenNhaCungCap(rs.getString("TenNhaCungCap"));

                PhieuNhap pn = new PhieuNhap();
                pn.setMaPhieuNhap(rs.getString("MaPhieuNhap"));
                pn.setNgayNhap(rs.getDate("NgayNhap").toLocalDate());
                pn.setNhanVien(nv);
                pn.setNhaCungCap(ncc);
                pn.setTongTien(rs.getDouble("TongTien"));
                
                dsPhieuNhap.add(pn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return dsPhieuNhap;
    }
}