package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import connectDB.connectDB;
import entity.NhanVien;

public class NhanVien_DAO {

    public NhanVien_DAO() {}

    /** Lấy tất cả nhân viên */
    public ArrayList<NhanVien> getAllNhanVien() {
        ArrayList<NhanVien> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "SELECT * FROM NhanVien";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String maNV = rs.getString("MaNhanVien");
                String tenNV = rs.getString("TenNhanVien");
                boolean gioiTinh = rs.getBoolean("GioiTinh");
                Date d = rs.getDate("NgaySinh");
                LocalDate ngaySinh = (d != null) ? d.toLocalDate() : null;
                String sdt = rs.getString("SoDienThoai");
                String diaChi = rs.getString("DiaChi");
                boolean quanLy = rs.getBoolean("QuanLy");
                String caLam = rs.getString("CaLam");
                boolean trangThai = rs.getBoolean("TrangThai");

                NhanVien nv = new NhanVien(
                    maNV, tenNV, gioiTinh,
                    ngaySinh, sdt, diaChi,
                    quanLy, caLam, trangThai
                );
                ds.add(nv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Thêm nhân viên (không còn MaTaiKhoan vì đã nằm trong TaiKhoan) */
    public boolean createNhanVien(NhanVien nv) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "INSERT INTO NhanVien (MaNhanVien, TenNhanVien, GioiTinh, NgaySinh, SoDienThoai, DiaChi, QuanLy, CaLam, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, nv.getMaNhanVien());
            stmt.setString(2, nv.getTenNhanVien());
            stmt.setBoolean(3, nv.isGioiTinh());
            stmt.setDate(4, nv.getNgaySinh() != null ? Date.valueOf(nv.getNgaySinh()) : null);
            stmt.setString(5, nv.getSoDienThoai());
            stmt.setString(6, nv.getDiaChi());
            stmt.setBoolean(7, nv.isQuanLy());
            stmt.setString(8, nv.getCaLam());
            stmt.setBoolean(9, nv.isTrangThai());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi thêm nhân viên: " + e.getMessage());
        }
        return false;
    }

    /** Cập nhật nhân viên */
    public boolean updateNhanVien(NhanVien nv) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "UPDATE NhanVien SET TenNhanVien=?, GioiTinh=?, NgaySinh=?, SoDienThoai=?, DiaChi=?, QuanLy=?, CaLam=?, TrangThai=? WHERE MaNhanVien=?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, nv.getTenNhanVien());
            stmt.setBoolean(2, nv.isGioiTinh());
            stmt.setDate(3, nv.getNgaySinh() != null ? Date.valueOf(nv.getNgaySinh()) : null);
            stmt.setString(4, nv.getSoDienThoai());
            stmt.setString(5, nv.getDiaChi());
            stmt.setBoolean(6, nv.isQuanLy());
            stmt.setString(7, nv.getCaLam());
            stmt.setBoolean(8, nv.isTrangThai());
            stmt.setString(9, nv.getMaNhanVien());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi cập nhật nhân viên: " + e.getMessage());
        }
        return false;
    }

    /** Lấy nhân viên theo mã */
    public NhanVien getNhanVienTheoMa(String maNV) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "SELECT * FROM NhanVien WHERE MaNhanVien=?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maNV);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String tenNV = rs.getString("TenNhanVien");
                    boolean gioiTinh = rs.getBoolean("GioiTinh");
                    Date d = rs.getDate("NgaySinh");
                    LocalDate ngaySinh = (d != null) ? d.toLocalDate() : null;
                    String sdt = rs.getString("SoDienThoai");
                    String diaChi = rs.getString("DiaChi");
                    boolean quanLy = rs.getBoolean("QuanLy");
                    String caLam = rs.getString("CaLam");
                    boolean trangThai = rs.getBoolean("TrangThai");

                    return new NhanVien(
                        maNV, tenNV, gioiTinh,
                        ngaySinh, sdt, diaChi,
                        quanLy, caLam, trangThai
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy nhân viên theo mã: " + e.getMessage());
        }
        return null;
    }

    /** Cập nhật trạng thái làm việc */
    public boolean updateTrangThai(String maNV, boolean trangThai) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "UPDATE NhanVien SET TrangThai=? WHERE MaNhanVien=?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setBoolean(1, trangThai);
            stmt.setString(2, maNV);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi cập nhật trạng thái: " + e.getMessage());
        }
        return false;
    }
}
