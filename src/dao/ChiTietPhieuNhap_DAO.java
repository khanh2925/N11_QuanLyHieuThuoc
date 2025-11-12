package dao;

import connectDB.connectDB;
import entity.ChiTietPhieuNhap;
import entity.DonViTinh;
import entity.LoSanPham;
import entity.PhieuNhap;
import entity.SanPham; // 💡 THÊM IMPORT

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate; // 💡 THÊM IMPORT
import java.util.ArrayList;
import java.util.List;

public class ChiTietPhieuNhap_DAO {

    // ✅ Không cần khai báo các DAO khác nữa vì chúng ta sẽ dùng JOIN
    // private final LoSanPham_DAO loSanPhamDAO;
    // private final DonViTinh_DAO donViTinhDAO;

    public ChiTietPhieuNhap_DAO() {
        // this.loSanPhamDAO = new LoSanPham_DAO();
        // this.donViTinhDAO = new DonViTinh_DAO(); 
    }

    /**
     * Lấy danh sách chi tiết của một phiếu nhập dựa vào mã phiếu.
     * ✅ Đã sửa lỗi N+1 Query và lỗi "Connection is closed" bằng cách dùng JOIN.
     */
    public List<ChiTietPhieuNhap> timKiemChiTietPhieuNhapBangMa(String maPhieuNhap) {
        List<ChiTietPhieuNhap> dsChiTiet = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connectDB.getInstance();
            con = connectDB.getConnection();

            // 💡 SỬA SQL: Dùng JOIN để lấy tất cả dữ liệu trong 1 lần
            String sql = "SELECT " +
                         "    ct.SoLuongNhap, ct.DonGiaNhap, " +
                         "    lo.MaLo, lo.HanSuDung, lo.SoLuongTon, " +
                         "    sp.MaSanPham, sp.TenSanPham, " +
                         "    dvt.MaDonViTinh, dvt.TenDonViTinh " +
                         "FROM " +
                         "    ChiTietPhieuNhap ct " +
                         "JOIN " +
                         "    LoSanPham lo ON ct.MaLo = lo.MaLo " +
                         "JOIN " +
                         "    SanPham sp ON lo.MaSanPham = sp.MaSanPham " +
                         "JOIN " +
                         "    DonViTinh dvt ON ct.MaDonViTinh = dvt.MaDonViTinh " +
                         "WHERE " +
                         "    ct.MaPhieuNhap = ?";
                         
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maPhieuNhap);
            rs = stmt.executeQuery();

            while (rs.next()) {
                
                // 1. Tạo SanPham
                SanPham sp = new SanPham();
                sp.setMaSanPham(rs.getString("MaSanPham"));
                sp.setTenSanPham(rs.getString("TenSanPham"));
                // (Bạn có thể set thêm các thuộc tính khác của SanPham nếu cần)

                // 2. Tạo LoSanPham
                LoSanPham lo = new LoSanPham();
                lo.setMaLo(rs.getString("MaLo"));
                lo.setHanSuDung(rs.getDate("HanSuDung").toLocalDate());
                lo.setSoLuongTon(rs.getInt("SoLuongTon")); // Lấy SoLuongTon từ DB
                lo.setSanPham(sp); // Gán SanPham vào Lô

                // 3. Tạo DonViTinh
                DonViTinh dvt = new DonViTinh();
                dvt.setMaDonViTinh(rs.getString("MaDonViTinh"));
                dvt.setTenDonViTinh(rs.getString("TenDonViTinh"));

                // 4. Tạo đối tượng PhieuNhap (chỉ cần mã để liên kết)
                PhieuNhap pn = new PhieuNhap();
                pn.setMaPhieuNhap(maPhieuNhap);

                // 5. Lấy thông tin ChiTietPhieuNhap
                int soLuongNhap = rs.getInt("SoLuongNhap");
                double donGiaNhap = rs.getDouble("DonGiaNhap");

                // 6. Tạo ChiTietPhieuNhap
                // Constructor này sẽ tự động tính thành tiền
                ChiTietPhieuNhap ctpn = new ChiTietPhieuNhap(pn, lo, dvt, soLuongNhap, donGiaNhap); 
                dsChiTiet.add(ctpn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                // Không đóng 'con' ở đây nếu bạn dùng connectDB Singleton
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return dsChiTiet;
    }
}