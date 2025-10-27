package dao;

import connectDB.connectDB;
import entity.ChiTietPhieuNhap;
import entity.LoSanPham;
import entity.PhieuNhap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietPhieuNhap_DAO {

    public ChiTietPhieuNhap_DAO() {
    }

    /**
     * Lấy danh sách chi tiết của một phiếu nhập dựa vào mã phiếu.
     */
    public List<ChiTietPhieuNhap> timKiemChiTietPhieuNhapBangMa(String maPhieuNhap) {
        List<ChiTietPhieuNhap> dsChiTiet = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connectDB.getInstance();
            con = connectDB.getConnection();
            LoSanPham_DAO loSanPhamDAO = new LoSanPham_DAO(); // Khởi tạo DAO cục bộ

            String sql = "SELECT * FROM ChiTietPhieuNhap WHERE MaPhieuNhap = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maPhieuNhap);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String maLo = rs.getString("MaLo");
                int soLuongNhap = rs.getInt("SoLuongNhap");
                double donGiaNhap = rs.getDouble("DonGiaNhap");

                LoSanPham lo = loSanPhamDAO.timKiemLoSanPhamBangMa(maLo);

                if (lo != null) {
                    PhieuNhap pn = new PhieuNhap();
                    pn.setMaPhieuNhap(maPhieuNhap);
                    ChiTietPhieuNhap ctpn = new ChiTietPhieuNhap(pn, lo, soLuongNhap, donGiaNhap);
                    dsChiTiet.add(ctpn);
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
        return dsChiTiet;
    }
}