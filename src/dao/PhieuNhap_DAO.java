package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import connectDB.connectDB;
import entity.NhaCungCap;
import entity.NhanVien;
import entity.PhieuNhap;

public class PhieuNhap_DAO {

    public PhieuNhap_DAO() {
    }
    /** Lấy tất cả phiếu nhập cùng thông tin nhân viên và nhà cung cấp */
    public List<PhieuNhap> layTatCaPhieuNhap() {
        List<PhieuNhap> dsPhieuNhap = new ArrayList<>();
        String sql = "SELECT pn.MaPhieuNhap, pn.NgayNhap, pn.TongTien, " +
                     "nv.MaNhanVien, nv.TenNhanVien, " +
                     "ncc.MaNhaCungCap, ncc.TenNhaCungCap " +
                     "FROM PhieuNhap pn " +
                     "JOIN NhanVien nv ON pn.MaNhanVien = nv.MaNhanVien " +
                     "JOIN NhaCungCap ncc ON pn.MaNhaCungCap = ncc.MaNhaCungCap " +
                     "ORDER BY pn.NgayNhap DESC";

        try (Connection con = connectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Tạo đối tượng NhanVien
                NhanVien nv = new NhanVien();
                nv.setMaNhanVien(rs.getString("MaNhanVien"));
                nv.setTenNhanVien(rs.getString("TenNhanVien"));
                
                // Tạo đối tượng NhaCungCap
                NhaCungCap ncc = new NhaCungCap();
                ncc.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
                ncc.setTenNhaCungCap(rs.getString("TenNhaCungCap"));

                // Tạo đối tượng PhieuNhap
                PhieuNhap pn = new PhieuNhap();
                pn.setMaPhieuNhap(rs.getString("MaPhieuNhap"));
                pn.setNgayNhap(rs.getDate("NgayNhap").toLocalDate());
                pn.setTongTien(rs.getDouble("TongTien"));
                pn.setNhanVien(nv);
                pn.setNhaCungCap(ncc);

                dsPhieuNhap.add(pn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsPhieuNhap;
    }
}