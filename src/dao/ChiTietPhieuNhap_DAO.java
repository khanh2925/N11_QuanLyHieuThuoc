package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.connectDB;
import entity.ChiTietPhieuNhap;
import entity.DonViTinh;
import entity.LoSanPham;
import entity.SanPham;
import enums.LoaiSanPham;

public class ChiTietPhieuNhap_DAO {

    // ============================================================
    // 📦 CACHE - Lưu chi tiết phiếu nhập theo mã phiếu
    // ============================================================
    private static Map<String, List<ChiTietPhieuNhap>> cacheChiTietByPhieu = new HashMap<>();

    public ChiTietPhieuNhap_DAO() {
    }

    /**
     * Lấy danh sách chi tiết phiếu nhập theo mã phiếu nhập.
     * Sử dụng JOIN để lấy luôn thông tin Lô, Sản Phẩm, Đơn Vị Tính
     * để tránh gọi DAO lồng nhau gây lỗi đóng kết nối.
     */
    public List<ChiTietPhieuNhap> timKiemChiTietPhieuNhapBangMa(String maPhieuNhap) {
        List<ChiTietPhieuNhap> dsChiTiet = new ArrayList<>();

        Connection con = connectDB.getConnection();

        // Câu lệnh JOIN lấy đầy đủ thông tin cần thiết để hiển thị lên GUI
        String sql = "SELECT " +
                "   ct.MaPhieuNhap, ct.SoLuongNhap, ct.DonGiaNhap, ct.ThanhTien, " +
                "   lo.MaLo, lo.HanSuDung, lo.SoLuongTon, " +
                "   sp.MaSanPham, sp.TenSanPham, sp.LoaiSanPham, " +
                "   dvt.MaDonViTinh, dvt.TenDonViTinh " +
                "FROM ChiTietPhieuNhap ct " +
                "JOIN LoSanPham lo ON ct.MaLo = lo.MaLo " +
                "JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham " +
                "JOIN DonViTinh dvt ON ct.MaDonViTinh = dvt.MaDonViTinh " +
                "WHERE ct.MaPhieuNhap = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maPhieuNhap);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // 1. Tạo đối tượng Sản Phẩm (chỉ cần các thông tin cơ bản để hiển thị)
                    SanPham sp = new SanPham();
                    sp.setMaSanPham(rs.getString("MaSanPham"));
                    sp.setTenSanPham(rs.getString("TenSanPham"));

                    String loaiStr = rs.getString("LoaiSanPham");
                    if (loaiStr != null) {
                        try {
                            sp.setLoaiSanPham(LoaiSanPham.valueOf(loaiStr));
                        } catch (Exception e) {
                        }
                    }

                    // 2. Tạo đối tượng Lô Sản Phẩm
                    LoSanPham lo = new LoSanPham();
                    lo.setMaLo(rs.getString("MaLo"));
                    lo.setHanSuDung(rs.getDate("HanSuDung").toLocalDate());
                    lo.setSoLuongTon(rs.getInt("SoLuongTon"));
                    lo.setSanPham(sp); // Gắn sản phẩm vào lô

                    // 3. Tạo đối tượng Đơn Vị Tính
                    DonViTinh dvt = new DonViTinh();
                    dvt.setMaDonViTinh(rs.getString("MaDonViTinh"));
                    dvt.setTenDonViTinh(rs.getString("TenDonViTinh"));

                    // 4. Tạo đối tượng Chi Tiết Phiếu Nhập
                    ChiTietPhieuNhap ct = new ChiTietPhieuNhap();
                    // Lưu ý: Không cần setPhieuNhap ở đây để tránh vòng lặp vô tận nếu in ra,
                    // hoặc có thể set new PhieuNhap(maPhieuNhap) nếu cần.

                    ct.setLoSanPham(lo);
                    ct.setDonViTinh(dvt);
                    ct.setSoLuongNhap(rs.getInt("SoLuongNhap"));
                    ct.setDonGiaNhap(rs.getDouble("DonGiaNhap"));
                    ct.capNhatThanhTien();

                    dsChiTiet.add(ct);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // KHÔNG ĐƯỢC ĐÓNG CONNECTION (con.close()) Ở ĐÂY VÌ ĐANG DÙNG SINGLETON

        return dsChiTiet;
    }
}