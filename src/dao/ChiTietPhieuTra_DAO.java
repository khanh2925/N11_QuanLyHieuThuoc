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
        // 💡 SỬA SQL: Dùng MaLo thay vì MaSanPham (để khớp với ChiTietHoaDon)
        String sql = "SELECT MaHoaDon, MaLo, LyDoChiTiet, SoLuong, TrangThai FROM ChiTietPhieuTra WHERE MaPhieuTra = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maPhieuTra);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String maHoaDon = rs.getString("MaHoaDon");
                    String maLo = rs.getString("MaLo"); // 💡 ĐỌC MA LÔ
                    String lyDoChiTiet = rs.getString("LyDoChiTiet");
                    int soLuong = rs.getInt("SoLuong");
                    int trangThai = rs.getInt("TrangThai");

                    // 💡 TÌM KIẾM THEO MA LÔ
                    ChiTietHoaDon cthd = chiTietHoaDonDAO.timKiemChiTietHoaDonBangMa(maHoaDon, maLo);
                    if (cthd != null) {
                        PhieuTra pt = new PhieuTra();
                        pt.setMaPhieuTra(maPhieuTra);

                        ChiTietPhieuTra ctpt = new ChiTietPhieuTra(pt, cthd, lyDoChiTiet, soLuong, trangThai);
                        // Cập nhật lại thành tiền hoàn (vì constructor đã gọi capNhatThanhTienHoan)
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


    public boolean themChiTietPhieuTra(ChiTietPhieuTra ctpt) {
        // 💡 SỬA SQL: Dùng MaLo thay vì MaSanPham
        String sql = "INSERT INTO ChiTietPhieuTra (MaPhieuTra, MaHoaDon, MaLo, LyDoChiTiet, SoLuong, ThanhTienHoan, TrangThai) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, ctpt.getPhieuTra().getMaPhieuTra());
            stmt.setString(2, ctpt.getChiTietHoaDon().getHoaDon().getMaHoaDon());
            stmt.setString(3, ctpt.getChiTietHoaDon().getLoSanPham().getMaLo()); // 💡 GÁN MA LÔ
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


    public boolean capNhatTrangThaiChiTiet(String maPhieuTra, String maHoaDon, String maLo, int trangThaiMoi) { // 💡 SỬA THAM SỐ
        // 💡 SỬA SQL: Dùng MaLo thay vì MaSanPham
        String sql = "UPDATE ChiTietPhieuTra SET TrangThai = ? WHERE MaPhieuTra = ? AND MaHoaDon = ? AND MaLo = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, trangThaiMoi);
            stmt.setString(2, maPhieuTra);
            stmt.setString(3, maHoaDon);
            stmt.setString(4, maLo); // 💡 GÁN MA LÔ

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}