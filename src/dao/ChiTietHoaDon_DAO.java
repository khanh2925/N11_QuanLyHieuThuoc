package dao;

import connectDB.connectDB;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.LoSanPham; // 💡 Cần import LoSanPham
import entity.SanPham; // Vẫn cần SanPham để tạo LoSanPham cho các DAO khác (nếu có)

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDon_DAO {
    
    // 💡 KHAI BÁO THÊM DAO ĐỂ TẢI ĐỐI TƯỢNG LO SẢN PHẨM ĐẦY ĐỦ
    private final LoSanPham_DAO loSanPhamDAO;
    
    public ChiTietHoaDon_DAO() {
        this.loSanPhamDAO = new LoSanPham_DAO(); // 💡 Khởi tạo LoSanPham_DAO
    }

    /** * Tìm chi tiết hóa đơn theo mã HD và Mã Lô. 
     * (Giả định bảng ChiTietHoaDon có cột MaLo)
     */
    public ChiTietHoaDon timKiemChiTietHoaDonBangMa(String maHD, String maLo) { // 💡 Sửa tham số thành MaLo
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            connectDB.getInstance();
            con = connectDB.getConnection();
            
            // 💡 SỬA SQL: Tìm kiếm theo MaLo (thay vì MaSanPham)
            String sql = "SELECT MaLo, SoLuong, GiaBan FROM ChiTietHoaDon WHERE MaHoaDon = ? AND MaLo = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maHD);
            stmt.setString(2, maLo); 
            rs = stmt.executeQuery();

            if (rs.next()) {
                int soLuong = rs.getInt("SoLuong");
                double giaBan = rs.getDouble("GiaBan");

                HoaDon hd = new HoaDon();
                hd.setMaHoaDon(maHD);

                // 💡 LẤY ĐỐI TƯỢNG LO SẢN PHẨM QUA DAO
                LoSanPham lo = loSanPhamDAO.layLoTheoMa(maLo); 
                
                if (lo != null) {
                    // 💡 TRUYỀN LO SẢN PHẨM VÀO CONSTRUCTOR
                    return new ChiTietHoaDon(hd, lo, soLuong, giaBan, null); 
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
     * (Giả định bảng ChiTietHoaDon có cột MaLo)
     */
    public List<ChiTietHoaDon> layDanhSachChiTietTheoMaHD(String maHD) {
        List<ChiTietHoaDon> danhSachChiTiet = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connectDB.getInstance();
            con = connectDB.getConnection();

            // 💡 SỬA SQL: Chọn MaLo, SoLuong, GiaBan
            String sql = "SELECT MaLo, SoLuong, GiaBan FROM ChiTietHoaDon WHERE MaHoaDon = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maHD);
            rs = stmt.executeQuery();
            
            HoaDon hd = new HoaDon();
            hd.setMaHoaDon(maHD);
            
            while (rs.next()) {
                String maLo = rs.getString("MaLo"); // 💡 ĐỌC MA LO
                int soLuong = rs.getInt("SoLuong");
                double giaBan = rs.getDouble("GiaBan");

                // 💡 LẤY ĐỐI TƯỢNG LO SẢN PHẨM QUA DAO
                LoSanPham lo = loSanPhamDAO.layLoTheoMa(maLo);

                if (lo != null) {
                    // 💡 TRUYỀN LO SẢN PHẨM VÀO CONSTRUCTOR
                    ChiTietHoaDon cthd = new ChiTietHoaDon(hd, lo, soLuong, giaBan, null);
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