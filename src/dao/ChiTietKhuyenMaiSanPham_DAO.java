package dao;

import connectDB.connectDB;
import entity.ChiTietKhuyenMaiSanPham;
import entity.KhuyenMai;
import entity.SanPham;
import enums.HinhThucKM;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietKhuyenMaiSanPham_DAO {

    public ChiTietKhuyenMaiSanPham_DAO() {
    }

    public List<ChiTietKhuyenMaiSanPham> timKiemChiTietKhuyenMaiSanPhamBangMa(String maKM) {
        List<ChiTietKhuyenMaiSanPham> ds = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connectDB.getInstance();
            con = connectDB.getConnection();
            SanPham_DAO sanPhamDAO = new SanPham_DAO(); // Khởi tạo DAO cục bộ
            KhuyenMai_DAO khuyenMaiDAO = new KhuyenMai_DAO(); // Khởi tạo DAO cục bộ

            String sql = "SELECT * FROM ChiTietKhuyenMaiSanPham WHERE MaKM = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maKM);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                String maSP = rs.getString("MaSanPham");

                SanPham sp = sanPhamDAO.getSanPhamTheoMa(maSP);
                KhuyenMai km = khuyenMaiDAO.timKhuyenMaiTheoMa(maKM);

                if (sp != null && km != null) {
                    ds.add(new ChiTietKhuyenMaiSanPham(sp, km));
                }
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
        return ds;
    }

    public boolean themChiTietKhuyenMaiSanPham(ChiTietKhuyenMaiSanPham ctkm) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmt = null;

        try {
            String sql = "INSERT INTO ChiTietKhuyenMaiSanPham (MaKM, MaSanPham, GiamGia) VALUES (?, ?, ?)";
            stmt = con.prepareStatement(sql);
            
            KhuyenMai km = ctkm.getKhuyenMai();
            stmt.setString(1, km.getMaKM());
            stmt.setString(2, ctkm.getSanPham().getMaSanPham());

            double giamGia = 0;
            if (km.getHinhThuc() == HinhThucKM.GIAM_GIA_PHAN_TRAM || km.getHinhThuc() == HinhThucKM.GIAM_GIA_TIEN) {
                giamGia = km.getGiaTri();
            }
            stmt.setDouble(3, giamGia);

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

    public boolean xoaChiTietKhuyenMaiSanPham(String maKM, String maSP) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmt = null;

        try {
            String sql = "DELETE FROM ChiTietKhuyenMaiSanPham WHERE MaKM = ? AND MaSanPham = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maKM);
            stmt.setString(2, maSP);
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