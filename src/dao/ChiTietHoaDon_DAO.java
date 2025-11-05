package dao;

import connectDB.connectDB;
import entity.ChiTietHoaDon;
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
    
    public ChiTietHoaDon_DAO() {
        this.loSanPhamDAO = new LoSanPham_DAO();
        this.khuyenMaiDAO = new KhuyenMai_DAO();
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
            
            // SỬA SQL: Lấy thêm MaKM
            String sql = "SELECT MaLo, MaKM, SoLuong, GiaBan FROM ChiTietHoaDon WHERE MaHoaDon = ? AND MaLo = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maHD);
            stmt.setString(2, maLo); 
            rs = stmt.executeQuery();

            if (rs.next()) {
                double soLuong = rs.getDouble("SoLuong"); // Sửa kiểu dữ liệu
                double giaBan = rs.getDouble("GiaBan");
                String maKM = rs.getString("MaKM");
                
                HoaDon hd = new HoaDon();
                hd.setMaHoaDon(maHD);

                LoSanPham lo = loSanPhamDAO.timLoTheoMa(maLo);
                KhuyenMai km = null;
                if (maKM != null) km = khuyenMaiDAO.timKhuyenMaiTheoMa(maKM);
                
                if (lo != null) {
                    return new ChiTietHoaDon(hd, lo, soLuong, giaBan, km); 
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

            // SỬA SQL: Lấy thêm MaKM
            String sql = "SELECT MaLo, MaKM, SoLuong, GiaBan FROM ChiTietHoaDon WHERE MaHoaDon = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maHD);
            rs = stmt.executeQuery();
            
            HoaDon hd = new HoaDon();
            hd.setMaHoaDon(maHD);
            
            while (rs.next()) {
                String maLo = rs.getString("MaLo");
                String maKM = rs.getString("MaKM");
                double soLuong = rs.getDouble("SoLuong");
                double giaBan = rs.getDouble("GiaBan");

                LoSanPham lo = loSanPhamDAO.timLoTheoMa(maLo);
                KhuyenMai km = null;
                if (maKM != null) km = khuyenMaiDAO.timKhuyenMaiTheoMa(maKM);
                
                if (lo != null) {
                    ChiTietHoaDon cthd = new ChiTietHoaDon(hd, lo, soLuong, giaBan, km);
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
    // [Placeholder: Implement the rest of the original methods here]
}