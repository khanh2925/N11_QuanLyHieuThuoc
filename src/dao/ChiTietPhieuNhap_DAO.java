package dao;

import connectDB.connectDB;
import entity.ChiTietPhieuNhap;
import entity.DonViTinh;
import entity.LoSanPham;
import entity.PhieuNhap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietPhieuNhap_DAO {

    private final LoSanPham_DAO loSanPhamDAO;
    private final DonViTinh_DAO donViTinhDAO; // 💡 KHAI BÁO DAO ĐVT

    public ChiTietPhieuNhap_DAO() {
        this.loSanPhamDAO = new LoSanPham_DAO();
        this.donViTinhDAO = new DonViTinh_DAO(); // 💡 KHỞI TẠO DAO ĐVT
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

            // 💡 SỬA SQL: Thêm MaDonViTinh và tính lại thành tiền
            String sql = "SELECT MaLo, MaDonViTinh, SoLuongNhap, DonGiaNhap FROM ChiTietPhieuNhap WHERE MaPhieuNhap = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maPhieuNhap);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String maLo = rs.getString("MaLo");
                String maDVT = rs.getString("MaDonViTinh"); // 💡 ĐỌC MA ĐVT
                int soLuongNhap = rs.getInt("SoLuongNhap");
                double donGiaNhap = rs.getDouble("DonGiaNhap");

                LoSanPham lo = loSanPhamDAO.timLoTheoMa(maLo);
                DonViTinh dvt = donViTinhDAO.timDonViTinhTheoMa(maDVT); // 💡 LẤY ĐỐI TƯỢNG ĐVT

                if (lo != null && dvt != null) {
                    PhieuNhap pn = new PhieuNhap();
                    pn.setMaPhieuNhap(maPhieuNhap);
                    // 💡 TRUYỀN ĐẦY ĐỦ THAM SỐ
                    ChiTietPhieuNhap ctpn = new ChiTietPhieuNhap(pn, lo, dvt, soLuongNhap, donGiaNhap); 
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