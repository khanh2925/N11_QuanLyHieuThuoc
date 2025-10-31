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

    private final Connection con;
    private final ChiTietHoaDon_DAO chiTietHoaDonDAO;

    public ChiTietPhieuTra_DAO() {
        this.con = connectDB.getConnection();
        this.chiTietHoaDonDAO = new ChiTietHoaDon_DAO();
    }

    public List<ChiTietPhieuTra> timKiemChiTietBangMaPhieuTra(String maPhieuTra) {
        List<ChiTietPhieuTra> danhSachChiTiet = new ArrayList<>();
        // SỬA LỖI 1: Đổi tên cột LyDoTra -> LyDoChiTiet
        String sql = "SELECT * FROM ChiTietPhieuTra WHERE MaPhieuTra = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maPhieuTra);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String maHoaDon = rs.getString("MaHoaDon");
                    String maSanPham = rs.getString("MaSanPham");
                    String lyDoChiTiet = rs.getString("LyDoChiTiet");
                    int soLuong = rs.getInt("SoLuong");
                    int trangThai = rs.getInt("TrangThai");

                    ChiTietHoaDon cthd = chiTietHoaDonDAO.timKiemChiTietHoaDonBangMa(maHoaDon, maSanPham);
                    if (cthd != null) {
                        PhieuTra pt = new PhieuTra();
                        pt.setMaPhieuTra(maPhieuTra);

                        ChiTietPhieuTra ctpt = new ChiTietPhieuTra(pt, cthd, lyDoChiTiet, soLuong, trangThai);
                        danhSachChiTiet.add(ctpt);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return danhSachChiTiet;
    }


    public boolean themChiTietPhieuTra(ChiTietPhieuTra ctpt) {
        // SỬA LỖI 1: Đổi tên cột LyDoTra -> LyDoChiTiet
        String sql = "INSERT INTO ChiTietPhieuTra (MaPhieuTra, MaHoaDon, MaSanPham, LyDoChiTiet, SoLuong, ThanhTienHoan, TrangThai) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, ctpt.getPhieuTra().getMaPhieuTra());
            stmt.setString(2, ctpt.getChiTietHoaDon().getHoaDon().getMaHoaDon());
            stmt.setString(3, ctpt.getChiTietHoaDon().getSanPham().getMaSanPham());
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


    public boolean capNhatTrangThaiChiTiet(String maPhieuTra, String maHoaDon, String maSanPham, int trangThaiMoi) {
        String sql = "UPDATE ChiTietPhieuTra SET TrangThai = ? WHERE MaPhieuTra = ? AND MaHoaDon = ? AND MaSanPham = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, trangThaiMoi);
            stmt.setString(2, maPhieuTra);
            stmt.setString(3, maHoaDon);
            stmt.setString(4, maSanPham);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}