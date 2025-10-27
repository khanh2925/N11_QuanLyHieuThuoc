package dao;

import connectDB.connectDB;
import entity.NhanVien;
import entity.TaiKhoan;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NhanVien_DAO {
    private final Connection con;
    private final TaiKhoan_DAO taiKhoanDAO;

    public NhanVien_DAO() {
        this.con = connectDB.getConnection();
        this.taiKhoanDAO = new TaiKhoan_DAO();
    }

    public NhanVien timKiemNhanVienBangMa(String maNhanVien) {
        String sql = "SELECT * FROM NhanVien WHERE MaNhanVien = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maNhanVien);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String tenNV = rs.getString("TenNhanVien");
                    boolean gioiTinh = rs.getBoolean("GioiTinh");

                    Date ngaySinhSQL = rs.getDate("NgaySinh");
                    LocalDate ngaySinh = (ngaySinhSQL != null) ? ngaySinhSQL.toLocalDate() : null;

                    String sdt = rs.getString("SoDienThoai");
                    String diaChi = rs.getString("DiaChi");
                    boolean quanLy = rs.getBoolean("ChucVu"); // Đổi tên biến để khớp với entity
                    boolean trangThai = rs.getBoolean("TrangThai");
                    String maTK = rs.getString("MaTaiKhoan");

                    TaiKhoan tk = taiKhoanDAO.getTaiKhoanTheoMa(maTK);
                    String caLam = "SANG"; 

                    return new NhanVien(maNhanVien, tenNV, gioiTinh, ngaySinh, sdt, diaChi, quanLy, tk, caLam, trangThai);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<NhanVien> timKiemTatCaNhanVien() {
        List<NhanVien> danhSachNV = new ArrayList<>();
        String sql = "SELECT MaNhanVien FROM NhanVien";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String maNV = rs.getString("MaNhanVien");
                NhanVien nv = timKiemNhanVienBangMa(maNV);
                if (nv != null) {
                    danhSachNV.add(nv);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachNV;
    }
}