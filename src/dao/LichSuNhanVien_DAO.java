	package dao;

import connectDB.connectDB;
import entity.HoaDon;
import entity.PhieuHuy;
import entity.PhieuTra;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 🔍 DAO chuyên dùng để tra cứu LỊCH SỬ bán / trả / huỷ theo NHÂN VIÊN
 *
 * Ý tưởng:
 *  - Pha 1: Query danh sách MÃ chứng từ (HoaDon / PhieuTra / PhieuHuy) theo MaNhanVien + khoảng ngày
 *  - Pha 2: Dùng các DAO sẵn có (HoaDon_DAO, PhieuTra_DAO, PhieuHuy_DAO) để load entity đầy đủ
 *
 * Ưu điểm: tránh join nặng + tái sử dụng logic đã có trong các DAO khác
 */
public class LichSuNhanVien_DAO {

    private final HoaDon_DAO hoaDonDAO;

    public LichSuNhanVien_DAO() {
        this.hoaDonDAO = new HoaDon_DAO();
    }

    // ========================================================================
    // 1️ LỊCH SỬ BÁN HÀNG (Hóa đơn) THEO NHÂN VIÊN
    // ========================================================================

    /**
     * Lấy danh sách Hóa đơn do 1 nhân viên lập trong khoảng ngày (có thể null).
     *
     * @param maNhanVien Mã nhân viên
     * @param tuNgay     Ngày bắt đầu (có thể null)
     * @param denNgay    Ngày kết thúc (có thể null)
     */
    public List<HoaDon> layLichSuBanTheoNhanVien(String maNhanVien, LocalDate tuNgay, LocalDate denNgay) {
        List<String> danhSachMa = new ArrayList<>();
        List<HoaDon> ketQua = new ArrayList<>();

        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        StringBuilder sql = new StringBuilder("""
                SELECT MaHoaDon, NgayLap
                FROM HoaDon
                WHERE MaNhanVien = ?
                """);

        // Thêm điều kiện ngày nếu có
        if (tuNgay != null && denNgay != null) {
            sql.append(" AND NgayLap BETWEEN ? AND ? ");
        } else if (tuNgay != null) {
            sql.append(" AND NgayLap >= ? ");
        } else if (denNgay != null) {
            sql.append(" AND NgayLap <= ? ");
        }

        sql.append(" ORDER BY NgayLap DESC, MaHoaDon DESC ");

        try {
            ps = con.prepareStatement(sql.toString());
            int idx = 1;
            ps.setString(idx++, maNhanVien);

            if (tuNgay != null && denNgay != null) {
                ps.setDate(idx++, Date.valueOf(tuNgay));
                ps.setDate(idx++, Date.valueOf(denNgay));
            } else if (tuNgay != null) {
                ps.setDate(idx++, Date.valueOf(tuNgay));
            } else if (denNgay != null) {
                ps.setDate(idx++, Date.valueOf(denNgay));
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                danhSachMa.add(rs.getString("MaHoaDon"));
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy mã hóa đơn lịch sử NV: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (Exception ignored) {}
            try {
                if (ps != null) ps.close();
            } catch (Exception ignored) {}
        }

        // Pha 2: dùng HoaDon_DAO để load entity đầy đủ
        for (String maHD : danhSachMa) {
            HoaDon hd = hoaDonDAO.timHoaDonTheoMa(maHD);
            if (hd != null) {
                ketQua.add(hd);
            }
        }

        return ketQua;
    }

}
