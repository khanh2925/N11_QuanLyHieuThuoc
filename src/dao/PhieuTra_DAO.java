package dao;

import connectDB.connectDB; // Đảm bảo tên class connectDB đúng
import entity.ChiTietPhieuTra;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuTra;

import java.sql.*;
import java.time.LocalDate;
// import java.time.format.DateTimeFormatter; // Không cần cho PhieuTra_DAO này
import java.util.ArrayList;
import java.util.List;

public class PhieuTra_DAO {

    public PhieuTra_DAO() {
    }

    /**
     * Tìm kiếm một phiếu trả trong CSDL dựa vào mã phiếu, bao gồm cả chi tiết.
     */
    public PhieuTra timKiemPhieuTraBangMa(String maPhieuTra) {
        PhieuTra pt = null;
        // Theo form: Lấy connection bên trong method
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT NgayLap, MaNhanVien, MaKhachHang, DaDuyet, TongTienHoan " + // Sửa: NgayLap, DaDuyet
                     "FROM PhieuTra WHERE MaPhieuTra = ?";

        // Theo form: Dùng try-with-resources
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maPhieuTra);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LocalDate ngayLap = rs.getDate("NgayLap").toLocalDate(); // Sửa: NgayLap
                    String maNV = rs.getString("MaNhanVien");
                    String maKH = rs.getString("MaKhachHang");
                    boolean daDuyet = rs.getBoolean("DaDuyet"); // Sửa: DaDuyet (boolean)
                    double tongTienHoanDB = rs.getDouble("TongTienHoan"); // Đọc tổng tiền từ DB

                    // Khởi tạo DAO phụ thuộc cục bộ (giả định tên method tiếng Việt)
                    NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
                    KhachHang_DAO khachHangDAO = new KhachHang_DAO();
                    ChiTietPhieuTra_DAO chiTietPhieuTraDAO = new ChiTietPhieuTra_DAO();

                    NhanVien nv = nhanVienDAO.getNhanVienTheoMa(maNV); 
                    KhachHang kh = khachHangDAO.getKhachHangTheoMa(maKH); // Giả định tên method

                    // Tạo PhieuTra với thông tin cơ bản
                    pt = new PhieuTra();
                    pt.setMaPhieuTra(maPhieuTra);
                    pt.setNgayLap(ngayLap);
                    pt.setNhanVien(nv);
                    pt.setKhachHang(kh);
                    pt.setDaDuyet(daDuyet); // Gán trạng thái boolean

                    // Lấy danh sách chi tiết (dùng đúng tên method tiếng Việt)
                    List<ChiTietPhieuTra> chiTietList = chiTietPhieuTraDAO.timKiemChiTietBangMaPhieuTra(maPhieuTra);
                    pt.setChiTietPhieuTraList(chiTietList);
                    // Entity PhieuTra sẽ tự tính lại tongTienHoan khi set list chi tiết
                    // Không cần gán tongTienHoanDB đọc từ DB nữa
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // try-with-resources tự đóng stmt, rs
        return pt;
    }

    /**
     * Lấy tất cả các phiếu trả từ cơ sở dữ liệu (chế độ xem tóm tắt).
     */
    public List<PhieuTra> layTatCaPhieuTra() {
        List<PhieuTra> danhSachPT = new ArrayList<>();
        // Theo form: Lấy connection bên trong method
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        // Lấy thông tin cần thiết cho view tóm tắt
        String sql = "SELECT pt.MaPhieuTra, pt.NgayLap, pt.TongTienHoan, pt.DaDuyet, " +
                     "nv.MaNhanVien, nv.TenNhanVien, " +
                     "kh.MaKhachHang, kh.TenKhachHang " +
                     "FROM PhieuTra pt " +
                     "JOIN NhanVien nv ON pt.MaNhanVien = nv.MaNhanVien " +
                     "JOIN KhachHang kh ON pt.MaKhachHang = kh.MaKhachHang " +
                     "ORDER BY pt.NgayLap DESC"; // Sửa: NgayLap

        // Theo form: Dùng try-with-resources
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNhanVien(rs.getString("MaNhanVien"));
                nv.setTenNhanVien(rs.getString("TenNhanVien"));

                KhachHang kh = new KhachHang();
                kh.setMaKhachHang(rs.getString("MaKhachHang"));
                kh.setTenKhachHang(rs.getString("TenKhachHang"));

                PhieuTra pt = new PhieuTra();
                pt.setMaPhieuTra(rs.getString("MaPhieuTra"));
                pt.setNgayLap(rs.getDate("NgayLap").toLocalDate());
                pt.setNhanVien(nv);
                pt.setKhachHang(kh);
                pt.setDaDuyet(rs.getBoolean("DaDuyet"));
                
                // Vì không load chi tiết, set tổng tiền trực tiếp từ DB
                pt.setTongTienHoan(rs.getDouble("TongTienHoan"));

                danhSachPT.add(pt);
            }
        } catch (Exception e) { // Bắt cả SQLException và lỗi từ setter
            e.printStackTrace();
        }
        // try-with-resources tự đóng stmt, rs
        return danhSachPT;
    }

    /**
     * Tạo một phiếu trả hàng mới và các chi tiết của nó (sử dụng transaction).
     */
    public boolean themPhieuTra(PhieuTra pt) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        
        PreparedStatement stmtPhieuTra = null;
        PreparedStatement stmtChiTiet = null;

        try {
            con.setAutoCommit(false);

            String sqlPhieuTra = "INSERT INTO PhieuTra (MaPhieuTra, NgayLap, MaNhanVien, MaKhachHang, TongTienHoan, DaDuyet) " +
                                 "VALUES (?, ?, ?, ?, ?, ?)";
            stmtPhieuTra = con.prepareStatement(sqlPhieuTra);
            stmtPhieuTra.setString(1, pt.getMaPhieuTra());
            stmtPhieuTra.setDate(2, Date.valueOf(pt.getNgayLap())); // Sửa: NgayLap
            stmtPhieuTra.setString(3, pt.getNhanVien().getMaNhanVien());
            stmtPhieuTra.setString(4, pt.getKhachHang().getMaKhachHang());
            stmtPhieuTra.setDouble(5, pt.getTongTienHoan()); // Lấy từ entity đã tính
            stmtPhieuTra.setBoolean(6, pt.isDaDuyet()); // Sửa: DaDuyet (boolean)
            stmtPhieuTra.executeUpdate();


            String sqlChiTiet = "INSERT INTO ChiTietPhieuTra (MaPhieuTra, MaHoaDon, MaSanPham, LyDoChiTiet, SoLuong, ThanhTienHoan, TrangThai) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?)";
            stmtChiTiet = con.prepareStatement(sqlChiTiet);
            
            // Giả định ChiTietPhieuTra có các getter cần thiết
            for (ChiTietPhieuTra ctpt : pt.getChiTietPhieuTraList()) {
                stmtChiTiet.setString(1, pt.getMaPhieuTra());
                stmtChiTiet.setString(2, ctpt.getChiTietHoaDon().getHoaDon().getMaHoaDon());
                stmtChiTiet.setString(3, ctpt.getChiTietHoaDon().getSanPham().getMaSanPham());
                stmtChiTiet.setString(4, ctpt.getLyDoChiTiet()); 
                stmtChiTiet.setInt(5, ctpt.getSoLuong());
                stmtChiTiet.setDouble(6, ctpt.getThanhTienHoan());
                stmtChiTiet.setInt(7, ctpt.getTrangThai());
                stmtChiTiet.addBatch();
            }
            stmtChiTiet.executeBatch();

            con.commit();
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
                if (stmtPhieuTra != null) stmtPhieuTra.close();
                if (stmtChiTiet != null) stmtChiTiet.close();
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Cập nhật trạng thái duyệt của một phiếu trả.
     * Đổi tham số thành boolean cho phù hợp với entity và schema.
     */
    public boolean capNhatTrangThai(String maPhieuTra, boolean daDuyetMoi) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        
        String sql = "UPDATE PhieuTra SET DaDuyet = ? WHERE MaPhieuTra = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setBoolean(1, daDuyetMoi);
            stmt.setString(2, maPhieuTra);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Tạo mã phiếu trả tự động (PTxxxxxx).
     */
    public String taoMaPhieuTra() {
        String newID = "PT000001"; 
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        
        String sql = "SELECT MAX(MaPhieuTra) FROM PhieuTra WHERE MaPhieuTra LIKE 'PT%'"; 

        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                String lastID = rs.getString(1);
                if (lastID != null && lastID.length() > 2) {
                    try {
                        int lastNumber = Integer.parseInt(lastID.substring(2)); // Bỏ "PT"
                        newID = String.format("PT%06d", lastNumber + 1);
                    } catch (NumberFormatException nfe) {
                        System.err.println("Lỗi khi phân tích mã phiếu trả cuối cùng: " + lastID);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newID;
    }
}