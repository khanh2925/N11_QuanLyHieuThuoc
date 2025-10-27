package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import connectDB.connectDB;
import entity.NhanVien;
import entity.TaiKhoan;

public class NhanVien_DAO {

    private TaiKhoan_DAO tkDAO = new TaiKhoan_DAO();

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
                Boolean gioiTinh = rs.getBoolean("GioiTinh");
                Date d = rs.getDate("NgaySinh");
                LocalDate ngaySinh = (d != null) ? d.toLocalDate() : null;
                String sdt = rs.getString("SoDienThoai");
                String diaChi = rs.getString("DiaChi");
                Boolean quanLy = rs.getBoolean("QuanLy");
                String maTK = rs.getString("MaTaiKhoan");
                String caLam = rs.getString("CaLam");
                boolean trangThai = rs.getBoolean("TrangThai");

                TaiKhoan tk = tkDAO.getTaiKhoanTheoMa(maTK);

                NhanVien nv = new NhanVien(maNV, tenNV, gioiTinh,
                        ngaySinh, sdt, diaChi, quanLy, tk, caLam, trangThai);
                ds.add(nv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Thêm nhân viên */
    public boolean createNhanVien(NhanVien nv) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "INSERT INTO NhanVien VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, nv.getMaNhanVien());
            stmt.setString(2, nv.getTenNhanVien());
            stmt.setBoolean(3, nv.isGioiTinh());
            stmt.setDate(4, nv.getNgaySinh() != null ? Date.valueOf(nv.getNgaySinh()) : null);
            stmt.setString(5, nv.getSoDienThoai());
            stmt.setString(6, nv.getDiaChi());
            stmt.setBoolean(7, nv.isQuanLy());
            stmt.setString(8, nv.getTaiKhoan().getMaTaiKhoan());
            stmt.setString(9, nv.getCaLam());
            stmt.setBoolean(10, nv.isTrangThai());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    /** Cập nhật nhân viên */
    public boolean updateNhanVien(NhanVien nv) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "UPDATE NhanVien SET TenNhanVien=?, GioiTinh=?, NgaySinh=?, SoDienThoai=?, DiaChi=?, QuanLy=?, MaTaiKhoan=?, CaLam=?, TrangThai=? WHERE MaNhanVien=?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, nv.getTenNhanVien());
            stmt.setBoolean(2, nv.isGioiTinh());
            stmt.setDate(3, nv.getNgaySinh() != null ? Date.valueOf(nv.getNgaySinh()) : null);
            stmt.setString(4, nv.getSoDienThoai());
            stmt.setString(5, nv.getDiaChi());
            stmt.setBoolean(6, nv.isQuanLy());
            stmt.setString(7, nv.getTaiKhoan().getMaTaiKhoan());
            stmt.setString(8, nv.getCaLam());
            stmt.setBoolean(9, nv.isTrangThai());
            stmt.setString(10, nv.getMaNhanVien());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }



    /** Lấy theo mã */
    public NhanVien getNhanVienTheoMa(String maNV) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "SELECT * FROM NhanVien WHERE MaNhanVien=?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maNV);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String ten = rs.getString("TenNhanVien");
                    Boolean gt = rs.getBoolean("GioiTinh");
                    Date d = rs.getDate("NgaySinh");
                    LocalDate ns = (d != null) ? d.toLocalDate() : null;
                    String sdt = rs.getString("SoDienThoai");
                    String dc = rs.getString("DiaChi");
                    Boolean ql = rs.getBoolean("QuanLy");
                    String maTK = rs.getString("MaTaiKhoan");
                    String cl = rs.getString("CaLam");
                    boolean tt = rs.getBoolean("TrangThai");

                    TaiKhoan tk = tkDAO.getTaiKhoanTheoMa(maTK);
                    return new NhanVien(maNV, ten, gt, ns, sdt, dc, ql, tk, cl, tt);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

   

    /** Cập nhật trạng thái */
    public boolean updateTrangThai(String maNV, boolean tt) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "UPDATE NhanVien SET TrangThai=? WHERE MaNhanVien=?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setBoolean(1, tt);
            stmt.setString(2, maNV);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}