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
        String sql = "SELECT * FROM ChiTietPhieuTra WHERE MaPhieuTra = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maPhieuTra);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String maHoaDon = rs.getString("MaHoaDon");
                    String maSanPham = rs.getString("MaSanPham");
                    String lyDoTra = rs.getString("LyDoTra");
                    int soLuong = rs.getInt("SoLuong");
                    // Đọc trạng thái kiểu NVARCHAR từ DB
                    String trangThaiDB = rs.getString("TrangThai");

                    // Chuyển đổi NVARCHAR -> boolean cho entity
                    boolean trangThaiEntity = trangThaiDB != null && trangThaiDB.equalsIgnoreCase("Đã xử lý");

                    // Lấy đối tượng ChiTietHoaDon từ DAO tương ứng
                    ChiTietHoaDon cthd = chiTietHoaDonDAO.timKiemChiTietHoaDonBangMa(maHoaDon, maSanPham);
                    if (cthd != null) {
                        // Tạo đối tượng PhieuTra tạm chỉ với mã để đưa vào constructor
                        PhieuTra pt = new PhieuTra();
                        pt.setMaPhieuTra(maPhieuTra);

                        ChiTietPhieuTra ctpt = new ChiTietPhieuTra(pt, cthd, lyDoTra, soLuong, trangThaiEntity);
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
        String sql = "INSERT INTO ChiTietPhieuTra (MaPhieuTra, MaHoaDon, MaSanPham, LyDoTra, SoLuong, ThanhTienHoan, TrangThai) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, ctpt.getPhieuTra().getMaPhieuTra());
            stmt.setString(2, ctpt.getChiTietHoaDon().getHoaDon().getMaHoaDon());
            stmt.setString(3, ctpt.getChiTietHoaDon().getSanPham().getMaSanPham());
            stmt.setString(4, ctpt.getLyDoChiTiet());
            stmt.setInt(5, ctpt.getSoLuong());
            stmt.setDouble(6, ctpt.getThanhTienHoan());

            // Chuyển đổi boolean -> NVARCHAR cho DB
            String trangThaiDB = ctpt.isTrangThai() ? "Đã xử lý" : "Chờ xử lý";
            stmt.setString(7, trangThaiDB);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean capNhatTrangThaiChiTiet(String maPhieuTra, String maHoaDon, String maSanPham, String trangThaiMoi) {
        String sql = "UPDATE ChiTietPhieuTra SET TrangThai = ? WHERE MaPhieuTra = ? AND MaHoaDon = ? AND MaSanPham = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, trangThaiMoi);
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