//package dao;
//
//import connectDB.connectDB;
//<<<<<<< HEAD
//import entity.*;
//=======
//import entity.ChiTietHoaDon;
//import entity.DonViTinh;
//import entity.HoaDon;
//import entity.KhuyenMai;
//import entity.LoSanPham; 
//>>>>>>> khanh
//
//import java.sql.*;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ChiTietHoaDon_DAO {
//<<<<<<< HEAD
//=======
//    
//    private final LoSanPham_DAO loSanPhamDAO;
//    private final KhuyenMai_DAO khuyenMaiDAO;
//    private final DonViTinh_DAO donViTinhDAO; // ✅ Bổ sung DonViTinh_DAO
//    
//    public ChiTietHoaDon_DAO() {
//        this.loSanPhamDAO = new LoSanPham_DAO();
//        this.khuyenMaiDAO = new KhuyenMai_DAO();
//        // ✅ Khởi tạo DonViTinh_DAO (giả định tồn tại)
//        this.donViTinhDAO = new DonViTinh_DAO(); 
//    }
//>>>>>>> khanh
//
//    // ============================================================
//    // 🔍 Lấy 1 chi tiết hóa đơn theo mã (KHÔNG LỒNG DAO)
//    // ============================================================
//    public ChiTietHoaDon timKiemChiTietHoaDonBangMa(String maHD, String maLo) {
//<<<<<<< HEAD
//
//        String sql = """
//                SELECT 
//                    cthd.SoLuong AS SLHD,
//                    cthd.GiaBan,
//                    cthd.MaKM,
//                    cthd.MaDonViTinh,
//
//                    lo.MaLo,
//                    lo.HanSuDung,
//                    lo.SoLuongTon,
//                    sp.MaSanPham,
//                    sp.TenSanPham,
//
//                    dvt.TenDonViTinh,
//
//                    km.TenKM,
//                    km.GiaTri,
//                    km.HinhThuc
//                FROM ChiTietHoaDon cthd
//                JOIN LoSanPham lo ON lo.MaLo = cthd.MaLo
//                JOIN SanPham sp ON sp.MaSanPham = lo.MaSanPham
//                LEFT JOIN DonViTinh dvt ON dvt.MaDonViTinh = cthd.MaDonViTinh
//                LEFT JOIN KhuyenMai km ON km.MaKM = cthd.MaKM
//                WHERE cthd.MaHoaDon = ? AND cthd.MaLo = ?
//                """;
//
//        try (Connection con = connectDB.getConnection();
//             PreparedStatement stmt = con.prepareStatement(sql)) {
//
//=======
//        Connection con = null;
//        PreparedStatement stmt = null;
//        ResultSet rs = null;
//        
//        try {
//            connectDB.getInstance();
//            con = connectDB.getConnection();
//            
//            // ✅ SỬA SQL: Lấy thêm MaDonViTinh
//            String sql = "SELECT MaLo, MaKM, SoLuong, GiaBan, MaDonViTinh FROM ChiTietHoaDon WHERE MaHoaDon = ? AND MaLo = ?";
//            stmt = con.prepareStatement(sql);
//>>>>>>> khanh
//            stmt.setString(1, maHD);
//            stmt.setString(2, maLo);
//
//            ResultSet rs = stmt.executeQuery();
//
//            if (rs.next()) {
//<<<<<<< HEAD
//
//                // ========================
//                // 🔹 Tạo HoaDon
//                // ========================
//                HoaDon hd = new HoaDon();
//                hd.setMaHoaDon(maHD);
//
//                // ========================
//                // 🔹 Tạo Sản phẩm
//                // ========================
//                SanPham sp = new SanPham();
//                sp.setMaSanPham(rs.getString("MaSanPham"));
//                sp.setTenSanPham(rs.getString("TenSanPham"));
//
//                // ========================
//                // 🔹 Tạo Lô (LoSanPham)
//                // ========================
//                LoSanPham lo = new LoSanPham(rs.getString("MaLo"));
//                lo.setHanSuDung(rs.getDate("HanSuDung").toLocalDate());
//                lo.setSoLuongTon(rs.getInt("SoLuongTon"));
//                lo.setSanPham(sp);
//
//                // ========================
//                // 🔹 Tạo ĐVT
//                // ========================
//                DonViTinh dvt = null;
//                if (rs.getString("MaDonViTinh") != null) {
//                    dvt = new DonViTinh(
//                            rs.getString("MaDonViTinh"),
//                            rs.getString("TenDonViTinh")
//                    );
//=======
//                int soLuong = rs.getInt("SoLuong"); 
//                double giaBan = rs.getDouble("GiaBan");
//                String maKM = rs.getString("MaKM");
//                String maDVT = rs.getString("MaDonViTinh"); // ✅ Lấy MaDonViTinh
//                
//                HoaDon hd = new HoaDon();
//                hd.setMaHoaDon(maHD);
//
//                LoSanPham lo = loSanPhamDAO.timLoTheoMa(maLo);
//                KhuyenMai km = null;
//                if (maKM != null) km = khuyenMaiDAO.timKhuyenMaiTheoMa(maKM);
//                
//                // ✅ Load DonViTinh
//                DonViTinh donViTinh = null;
//                if (maDVT != null) donViTinh = donViTinhDAO.timDonViTinhTheoMa(maDVT);
//                
//                if (lo != null) {
//                    // ✅ Cập nhật constructor với DonViTinh
//                    return new ChiTietHoaDon(hd, lo, soLuong, giaBan, km, donViTinh); 
//>>>>>>> khanh
//                }
//
//                // ========================
//                // 🔹 Tạo khuyến mãi
//                // ========================
//                KhuyenMai km = null;
//                if (rs.getString("MaKM") != null) {
//                    km = new KhuyenMai();
//                    km.setMaKM(rs.getString("MaKM"));
//                    km.setTenKM(rs.getString("TenKM"));
//                    km.setGiaTri(rs.getDouble("GiaTri"));
//                    km.setHinhThuc(
//                            enums.HinhThucKM.valueOf(rs.getString("HinhThuc"))
//                    );
//                }
//
//                // ========================
//                // 🔹 Tạo ChiTietHoaDon
//                // ========================
//                return new ChiTietHoaDon(
//                        hd,
//                        lo,
//                        rs.getDouble("SLHD"),
//                        rs.getDouble("GiaBan"),
//                        km,
//                        dvt
//                );
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    // ============================================================
//    // 🔍 Lấy DS chi tiết hóa đơn theo mã (KHÔNG LỒNG DAO)
//    // ============================================================
//    public List<ChiTietHoaDon> layDanhSachChiTietTheoMaHD(String maHD) {
//
//        List<ChiTietHoaDon> ds = new ArrayList<>();
//
//        String sql = """
//                SELECT 
//                    cthd.SoLuong AS SLHD,
//                    cthd.GiaBan,
//                    cthd.MaKM,
//                    cthd.MaDonViTinh,
//
//                    lo.MaLo,
//                    lo.HanSuDung,
//                    lo.SoLuongTon,
//                    sp.MaSanPham,
//                    sp.TenSanPham,
//
//                    dvt.TenDonViTinh,
//
//                    km.TenKM,
//                    km.GiaTri,
//                    km.HinhThuc
//                FROM ChiTietHoaDon cthd
//                JOIN LoSanPham lo ON lo.MaLo = cthd.MaLo
//                JOIN SanPham sp ON sp.MaSanPham = lo.MaSanPham
//                LEFT JOIN DonViTinh dvt ON dvt.MaDonViTinh = cthd.MaDonViTinh
//                LEFT JOIN KhuyenMai km ON km.MaKM = cthd.MaKM
//                WHERE cthd.MaHoaDon = ?
//                ORDER BY lo.MaLo
//                """;
//
//        try (Connection con = connectDB.getConnection();
//             PreparedStatement stmt = con.prepareStatement(sql)) {
//
//<<<<<<< HEAD
//=======
//            // ✅ SỬA SQL: Lấy thêm MaDonViTinh
//            String sql = "SELECT MaLo, MaKM, SoLuong, GiaBan, MaDonViTinh FROM ChiTietHoaDon WHERE MaHoaDon = ?";
//            stmt = con.prepareStatement(sql);
//>>>>>>> khanh
//            stmt.setString(1, maHD);
//            ResultSet rs = stmt.executeQuery();
//
//            // Bộ khung hóa đơn
//            HoaDon hd = new HoaDon();
//            hd.setMaHoaDon(maHD);
//<<<<<<< HEAD
//
//            while (rs.next()) {
//
//                SanPham sp = new SanPham();
//                sp.setMaSanPham(rs.getString("MaSanPham"));
//                sp.setTenSanPham(rs.getString("TenSanPham"));
//
//                LoSanPham lo = new LoSanPham(rs.getString("MaLo"));
//                lo.setHanSuDung(rs.getDate("HanSuDung").toLocalDate());
//                lo.setSoLuongTon(rs.getInt("SoLuongTon"));
//                lo.setSanPham(sp);
//
//                DonViTinh dvt = null;
//                if (rs.getString("MaDonViTinh") != null) {
//                    dvt = new DonViTinh(
//                            rs.getString("MaDonViTinh"),
//                            rs.getString("TenDonViTinh")
//                    );
//=======
//            
//            while (rs.next()) {
//                String maLo = rs.getString("MaLo");
//                String maKM = rs.getString("MaKM");
//                int soLuong = rs.getInt("SoLuong");
//                double giaBan = rs.getDouble("GiaBan");
//                String maDVT = rs.getString("MaDonViTinh"); // ✅ Lấy MaDonViTinh
//
//                LoSanPham lo = loSanPhamDAO.timLoTheoMa(maLo);
//                KhuyenMai km = null;
//                if (maKM != null) km = khuyenMaiDAO.timKhuyenMaiTheoMa(maKM);
//                
//                // ✅ Load DonViTinh
//                DonViTinh donViTinh = null;
//                if (maDVT != null) donViTinh = donViTinhDAO.timDonViTinhTheoMa(maDVT);
//                
//                if (lo != null) {
//                    // ✅ Cập nhật constructor với DonViTinh
//                    ChiTietHoaDon cthd = new ChiTietHoaDon(hd, lo, soLuong, giaBan, km, donViTinh);
//                    danhSachChiTiet.add(cthd);
//>>>>>>> khanh
//                }
//
//                KhuyenMai km = null;
//                if (rs.getString("MaKM") != null) {
//                    km = new KhuyenMai();
//                    km.setMaKM(rs.getString("MaKM"));
//                    km.setTenKM(rs.getString("TenKM"));
//                    km.setGiaTri(rs.getDouble("GiaTri"));
//                    km.setHinhThuc(enums.HinhThucKM.valueOf(rs.getString("HinhThuc")));
//                }
//
//                ds.add(new ChiTietHoaDon(
//                        hd,
//                        lo,
//                        rs.getDouble("SLHD"),
//                        rs.getDouble("GiaBan"),
//                        km,
//                        dvt
//                ));
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return ds;
//    }
//<<<<<<< HEAD
//}
//=======
//}
//>>>>>>> khanh
//---------------------------------------------------------------------------------------------------
package dao;

import connectDB.connectDB;
import entity.ChiTietHoaDon;
import entity.DonViTinh;
import entity.HoaDon;
import entity.KhuyenMai;
import entity.LoSanPham; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDon_DAO {
    
    private final LoSanPham_DAO loSanPhamDAO;
    private final KhuyenMai_DAO khuyenMaiDAO;
    private final DonViTinh_DAO donViTinhDAO; // ✅ Bổ sung DonViTinh_DAO
    
    public ChiTietHoaDon_DAO() {
        this.loSanPhamDAO = new LoSanPham_DAO();
        this.khuyenMaiDAO = new KhuyenMai_DAO();
        // ✅ Khởi tạo DonViTinh_DAO (giả định tồn tại)
        this.donViTinhDAO = new DonViTinh_DAO(); 
    }

    /** * Tìm chi tiết hóa đơn theo mã HD và Mã Lô.
     */
    public ChiTietHoaDon timKiemChiTietHoaDonBangMa(String maHD, String maLo) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            connectDB.getInstance();
            con = connectDB.getConnection();
            
            // ✅ SỬA SQL: Lấy thêm MaDonViTinh
            String sql = "SELECT MaLo, MaKM, SoLuong, GiaBan, MaDonViTinh FROM ChiTietHoaDon WHERE MaHoaDon = ? AND MaLo = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maHD);
            stmt.setString(2, maLo); 
            rs = stmt.executeQuery();

            if (rs.next()) {
                int soLuong = rs.getInt("SoLuong"); 
                double giaBan = rs.getDouble("GiaBan");
                String maKM = rs.getString("MaKM");
                String maDVT = rs.getString("MaDonViTinh"); // ✅ Lấy MaDonViTinh
                
                HoaDon hd = new HoaDon();
                hd.setMaHoaDon(maHD);

                LoSanPham lo = loSanPhamDAO.timLoTheoMa(maLo);
                KhuyenMai km = null;
                if (maKM != null) km = khuyenMaiDAO.timKhuyenMaiTheoMa(maKM);
                
                // ✅ Load DonViTinh
                DonViTinh donViTinh = null;
                if (maDVT != null) donViTinh = donViTinhDAO.timDonViTinhTheoMa(maDVT);
                
                if (lo != null) {
                    // ✅ Cập nhật constructor với DonViTinh
                    return new ChiTietHoaDon(hd, lo, soLuong, giaBan, km, donViTinh); 
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /** * Lấy danh sách chi tiết theo Mã Hóa Đơn.
     */
    public List<ChiTietHoaDon> layDanhSachChiTietTheoMaHD(String maHD) {
        List<ChiTietHoaDon> danhSachChiTiet = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connectDB.getInstance();
            con = connectDB.getConnection();

            // ✅ SỬA SQL: Lấy thêm MaDonViTinh
            String sql = "SELECT MaLo, MaKM, SoLuong, GiaBan, MaDonViTinh FROM ChiTietHoaDon WHERE MaHoaDon = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maHD);
            rs = stmt.executeQuery();
            
            HoaDon hd = new HoaDon();
            hd.setMaHoaDon(maHD);
            
            while (rs.next()) {
                String maLo = rs.getString("MaLo");
                String maKM = rs.getString("MaKM");
                int soLuong = rs.getInt("SoLuong");
                double giaBan = rs.getDouble("GiaBan");
                String maDVT = rs.getString("MaDonViTinh"); // ✅ Lấy MaDonViTinh

                LoSanPham lo = loSanPhamDAO.timLoTheoMa(maLo);
                KhuyenMai km = null;
                if (maKM != null) km = khuyenMaiDAO.timKhuyenMaiTheoMa(maKM);
                
                // ✅ Load DonViTinh
                DonViTinh donViTinh = null;
                if (maDVT != null) donViTinh = donViTinhDAO.timDonViTinhTheoMa(maDVT);
                
                if (lo != null) {
                    // ✅ Cập nhật constructor với DonViTinh
                    ChiTietHoaDon cthd = new ChiTietHoaDon(hd, lo, soLuong, giaBan, km, donViTinh);
                    danhSachChiTiet.add(cthd);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return danhSachChiTiet;
    }
}