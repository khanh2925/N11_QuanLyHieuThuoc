package dao;

import connectDB.connectDB;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.SanPham;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDon_DAO {
    public ChiTietHoaDon_DAO() {
    }
    public ChiTietHoaDon timKiemChiTietHoaDonBangMa(String maHD, String maSP) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            connectDB.getInstance();
            con = connectDB.getConnection();
            SanPham_DAO sanPhamDAO = new SanPham_DAO();
            
            String sql = "SELECT * FROM ChiTietHoaDon WHERE MaHoaDon = ? AND MaSanPham = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maHD);
            stmt.setString(2, maSP);
            rs = stmt.executeQuery();

            if (rs.next()) {
                int soLuong = rs.getInt("SoLuong");
                double giaBan = rs.getDouble("GiaBan");

                HoaDon hd = new HoaDon();
                hd.setMaHoaDon(maHD);

                SanPham sp = sanPhamDAO.findSanPhamById(maSP);

                return new ChiTietHoaDon(hd, sp, soLuong, giaBan, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                // Không đóng Connection ở đây để giữ kết nối chung
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<ChiTietHoaDon> layDanhSachChiTietTheoMaHD(String maHD) {
        List<ChiTietHoaDon> danhSachChiTiet = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connectDB.getInstance();
            con = connectDB.getConnection();
            SanPham_DAO sanPhamDAO = new SanPham_DAO();

            String sql = "SELECT * FROM ChiTietHoaDon WHERE MaHoaDon = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maHD);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                String maSP = rs.getString("MaSanPham");
                int soLuong = rs.getInt("SoLuong");
                double giaBan = rs.getDouble("GiaBan");

                HoaDon hd = new HoaDon();
                hd.setMaHoaDon(maHD);
                SanPham sp = sanPhamDAO.findSanPhamById(maSP);

                if (sp != null) {
                    ChiTietHoaDon cthd = new ChiTietHoaDon(hd, sp, soLuong, giaBan, null);
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

    public boolean themChiTietHoaDon(ChiTietHoaDon cthd) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmt = null;
        
        try {
            String sql = "INSERT INTO ChiTietHoaDon (MaHoaDon, MaSanPham, SoLuong, GiaBan) VALUES (?, ?, ?, ?)";
            stmt = con.prepareStatement(sql);
            
            stmt.setString(1, cthd.getHoaDon().getMaHoaDon());
            stmt.setString(2, cthd.getSanPham().getMaSanPham());
            stmt.setInt(3, (int) cthd.getSoLuong());
            stmt.setDouble(4, cthd.getGiaBan());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}