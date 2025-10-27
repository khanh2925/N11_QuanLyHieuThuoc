/**
 * @author Quốc Khánh cute
 * @version 1.0
 * @since Oct 26, 2025
 *
 * Mô tả: Lớp này được tạo bởi Quốc Khánh vào ngày Oct 26, 2025.
 */
package dao;

import java.sql.*;
import java.util.ArrayList;
import connectDB.connectDB;
import entity.ChiTietPhieuNhap;
import entity.PhieuNhap;
import entity.LoSanPham;

/**
 * @author:
 * @version: 1.1
 * @created: Oct 2025
 * 
 * DAO cho bảng ChiTietPhieuNhap
 *  - Không cho phép cập nhật hoặc xóa dữ liệu sau khi đã ghi vào DB.
 *  - Chỉ được thêm mới khi tạo phiếu nhập.
 */
public class ChiTietPhieuNhap_DAO {

    /**
     * Đọc toàn bộ chi tiết phiếu nhập trong hệ thống.
     */
    public ArrayList<ChiTietPhieuNhap> docTuBang() {
        ArrayList<ChiTietPhieuNhap> dsChiTiet = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = connectDB.getConnection();
            if (con == null || con.isClosed()) {
                throw new SQLException("Kết nối cơ sở dữ liệu không khả dụng!");
            }

            String sql = """
                SELECT ctpn.MaPhieuNhap, ctpn.MaLo, ctpn.SoLuongNhap, ctpn.DonGiaNhap,
                       pn.NgayNhap, ncc.TenNhaCungCap, lsp.MaSanPham, sp.TenSanPham
                FROM ChiTietPhieuNhap ctpn
                JOIN PhieuNhap pn ON ctpn.MaPhieuNhap = pn.MaPhieuNhap
                JOIN NhaCungCap ncc ON pn.MaNhaCungCap = ncc.MaNhaCungCap
                JOIN LoSanPham lsp ON ctpn.MaLo = lsp.MaLo
                JOIN SanPham sp ON lsp.MaSanPham = sp.MaSanPham
            """;

            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                PhieuNhap phieuNhap = new PhieuNhap(rs.getString("MaPhieuNhap"));
                LoSanPham lo = new LoSanPham(rs.getString("MaLo"));
                int soLuong = rs.getInt("SoLuongNhap");
                double donGia = rs.getDouble("DonGiaNhap");

                dsChiTiet.add(new ChiTietPhieuNhap(phieuNhap, lo, soLuong, donGia));
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
        return dsChiTiet;
    }

    /**
     * Lấy danh sách chi tiết theo mã phiếu nhập cụ thể.
     */
    public ArrayList<ChiTietPhieuNhap> layChiTietPhieuNhapTheoMaPhieu(String maPhieuNhap) {
        ArrayList<ChiTietPhieuNhap> dsChiTiet = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = connectDB.getConnection();
            if (con == null || con.isClosed()) {
                throw new SQLException("Kết nối cơ sở dữ liệu không khả dụng!");
            }

            String sql = """
                SELECT MaPhieuNhap, MaLo, SoLuongNhap, DonGiaNhap
                FROM ChiTietPhieuNhap
                WHERE MaPhieuNhap = ?
            """;

            stmt = con.prepareStatement(sql);
            stmt.setString(1, maPhieuNhap);
            rs = stmt.executeQuery();

            while (rs.next()) {
                PhieuNhap phieuNhap = new PhieuNhap(rs.getString("MaPhieuNhap"));
                LoSanPham lo = new LoSanPham(rs.getString("MaLo"));
                int soLuong = rs.getInt("SoLuongNhap");
                double donGia = rs.getDouble("DonGiaNhap");

                dsChiTiet.add(new ChiTietPhieuNhap(phieuNhap, lo, soLuong, donGia));
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
        return dsChiTiet;
    }

    /**
     * Thêm mới một chi tiết phiếu nhập (khi tạo phiếu nhập mới).
     * Không cho phép sửa hoặc xóa dữ liệu sau khi đã lưu.
     */
    public boolean themChiTietPhieuNhap(ChiTietPhieuNhap chiTiet) {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = connectDB.getConnection();
            if (con == null || con.isClosed()) {
                throw new SQLException("Kết nối cơ sở dữ liệu không khả dụng!");
            }

            String sql = """
                INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaLo, SoLuongNhap, DonGiaNhap)
                VALUES (?, ?, ?, ?)
            """;
            stmt = con.prepareStatement(sql);
            stmt.setString(1, chiTiet.getPhieuNhap().getMaPhieuNhap());
            stmt.setString(2, chiTiet.getLoSanPham().getMaLo());
            stmt.setInt(3, chiTiet.getSoLuongNhap());
            stmt.setDouble(4, chiTiet.getDonGiaNhap());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
