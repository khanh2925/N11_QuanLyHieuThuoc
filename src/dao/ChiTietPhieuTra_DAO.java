package dao;

import connectDB.connectDB;
import entity.ChiTietHoaDon;
import entity.ChiTietPhieuTra;
import entity.PhieuTra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietPhieuTra_DAO {

    private final ChiTietHoaDon_DAO chiTietHoaDonDAO;

    public ChiTietPhieuTra_DAO() {
        this.chiTietHoaDonDAO = new ChiTietHoaDon_DAO();
    }

    // ============================================================
    // 🔍 Lấy danh sách chi tiết phiếu trả theo mã phiếu trả
    // ============================================================
    public List<ChiTietPhieuTra> timKiemChiTietBangMaPhieuTra(String maPhieuTra) {
        List<ChiTietPhieuTra> danhSachChiTiet = new ArrayList<>();
        String sql = """
            SELECT MaHoaDon, MaLo, LyDoChiTiet, SoLuong, TrangThai
            FROM ChiTietPhieuTra
            WHERE MaPhieuTra = ?
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maPhieuTra);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String maHoaDon = rs.getString("MaHoaDon");
                    String maLo = rs.getString("MaLo");
                    String lyDoChiTiet = rs.getString("LyDoChiTiet");
                    int soLuong = rs.getInt("SoLuong");
                    int trangThai = rs.getInt("TrangThai");

                    // Tìm chi tiết hóa đơn tương ứng (theo mã HĐ + mã lô)
                    ChiTietHoaDon cthd = chiTietHoaDonDAO.timKiemChiTietHoaDonBangMa(maHoaDon, maLo);
                    if (cthd != null) {
                        PhieuTra pt = new PhieuTra();
                        pt.setMaPhieuTra(maPhieuTra);

                        ChiTietPhieuTra ctpt = new ChiTietPhieuTra(pt, cthd, lyDoChiTiet, soLuong, trangThai);
                        ctpt.capNhatThanhTienHoan();
                        danhSachChiTiet.add(ctpt);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return danhSachChiTiet;
    }

    // ============================================================
    // ➕ Thêm mới 1 chi tiết phiếu trả
    // ============================================================
    public boolean themChiTietPhieuTra(ChiTietPhieuTra ctpt) {
        String sql = """
            INSERT INTO ChiTietPhieuTra
            (MaPhieuTra, MaHoaDon, MaLo, LyDoChiTiet, SoLuong, ThanhTienHoan, TrangThai)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, ctpt.getPhieuTra().getMaPhieuTra());
            stmt.setString(2, ctpt.getChiTietHoaDon().getHoaDon().getMaHoaDon());
            stmt.setString(3, ctpt.getChiTietHoaDon().getLoSanPham().getMaLo());
            stmt.setString(4, ctpt.getLyDoChiTiet());
            stmt.setInt(5, ctpt.getSoLuong());
            stmt.setDouble(6, ctpt.getThanhTienHoan());
            stmt.setInt(7, ctpt.getTrangThai());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ============================================================
    // 🔄 Cập nhật trạng thái của 1 chi tiết phiếu trả
    // ============================================================
    public boolean capNhatTrangThaiChiTiet(String maPhieuTra, String maHoaDon, String maLo, int trangThaiMoi) {
        String sql = """
            UPDATE ChiTietPhieuTra
            SET TrangThai = ?
            WHERE MaPhieuTra = ? AND MaHoaDon = ? AND MaLo = ?
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, trangThaiMoi);
            stmt.setString(2, maPhieuTra);
            stmt.setString(3, maHoaDon);
            stmt.setString(4, maLo);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ============================================================
    // 🔢 Tính tổng số lượng đã trả của 1 sản phẩm theo mã HĐ + mã lô
    // ============================================================
    public static double tongSoLuongDaTra(String maHD, String maLo) {
        double tong = 0;
        String sql = """
            SELECT SUM(SoLuong)
            FROM ChiTietPhieuTra
            WHERE MaHoaDon = ? AND MaLo = ? AND TrangThai IN (0,1,2)
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maHD);
            ps.setString(2, maLo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    tong = rs.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tong;
    }
}
