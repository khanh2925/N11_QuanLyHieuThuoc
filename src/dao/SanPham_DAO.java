package dao;

import java.sql.*;
import java.util.ArrayList;

import connectDB.connectDB;
import entity.SanPham;
import enums.DuongDung;
import entity.LoaiSanPham;
import entity.DonViTinh;

public class SanPham_DAO {

    public SanPham_DAO() {}

    /** Lấy toàn bộ sản phẩm */
    public ArrayList<SanPham> getAllSanPham() {
        ArrayList<SanPham> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaSanPham, TenSanPham, MaLoaiSanPham, SoDangKy, HoatChat, HamLuong, " +
                     "HangSanXuat, XuatXu, MaDonViTinh, MaDuongDung, GiaNhap, GiaBan, HinhAnh, " +
                     "QuyCachDongGoi, KeBanSanPham, HoatDong " +
                     "FROM SanPham";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String maSP   = rs.getString("MaSanPham");
                String ten    = rs.getString("TenSanPham");

                // LoaiSanPham: chỉ set mã (không bắt buộc phải có tên)
                String maLSP  = rs.getString("MaLoaiSanPham");
                LoaiSanPham lsp = null;
                if (maLSP != null) {
                    lsp = new LoaiSanPham();
                    try { lsp.setMaLoaiSanPham(maLSP); } catch (IllegalArgumentException ignore) {}
                }

                String soDK   = rs.getString("SoDangKy");
                String hoatChat = rs.getString("HoatChat");
                String hamLuong = rs.getString("HamLuong");
                String hangSX = rs.getString("HangSanXuat");
                String xuatXu = rs.getString("XuatXu");

                // DonViTinh: chỉ set mã
                String maDVT  = rs.getString("MaDonViTinh");
                DonViTinh dvt = null;
                if (maDVT != null) {
                    dvt = new DonViTinh();
                    try { dvt.setMaDonViTinh(maDVT); } catch (IllegalArgumentException ignore) {}
                }

                // DuongDung: enum từ VARCHAR
                String ddStr  = rs.getString("MaDuongDung");
                DuongDung dd  = null;
                if (ddStr != null) {
                    try { dd = DuongDung.valueOf(ddStr); } catch (IllegalArgumentException ignore) {}
                }

                double giaNhap = rs.getDouble("GiaNhap");
                double giaBan  = rs.getDouble("GiaBan");
                String hinhAnh = rs.getString("HinhAnh");
                String quyCach = rs.getString("QuyCachDongGoi");
                String keBan   = rs.getString("KeBanSanPham");
                boolean hoatDong = rs.getBoolean("HoatDong");

                SanPham sp = new SanPham(maSP, ten, lsp, soDK, hoatChat, hamLuong, hangSX, xuatXu,
                                         dvt, dd, giaNhap, giaBan, hinhAnh, quyCach, keBan, hoatDong);
                ds.add(sp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Thêm sản phẩm */
    public boolean createSanPham(SanPham sp) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "INSERT INTO SanPham " +
                     "(MaSanPham, TenSanPham, MaLoaiSanPham, SoDangKy, HoatChat, HamLuong, " +
                     " HangSanXuat, XuatXu, MaDonViTinh, MaDuongDung, GiaNhap, GiaBan, HinhAnh, " +
                     " QuyCachDongGoi, KeBanSanPham, HoatDong) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sp.getMaSanPham());
            ps.setString(2, sp.getTenSanPham());
            ps.setString(3, sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().getMaLoaiSanPham() : null);
            ps.setString(4, sp.getSoDangKy());
            ps.setString(5, sp.getHoatChat());
            ps.setString(6, sp.getHamLuong());
            ps.setString(7, sp.getHangSanXuat());
            ps.setString(8, sp.getXuatXu());
            ps.setString(9, sp.getDonViTinh() != null ? sp.getDonViTinh().getMaDonViTinh() : null);
            ps.setString(10, sp.getDuongDung() != null ? sp.getDuongDung().name() : null); // ENUM -> VARCHAR
            ps.setDouble(11, sp.getGiaNhap());
            ps.setDouble(12, sp.getGiaBan());
            ps.setString(13, sp.getHinhAnh());
            ps.setString(14, sp.getQuyCachDongGoi());
            ps.setString(15, sp.getKeBanSanPham());
            ps.setBoolean(16, sp.isHoatDong());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // PK/FK/constraint...
        }
        return false;
    }

    /** Cập nhật sản phẩm */
    public boolean updateSanPham(SanPham sp) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "UPDATE SanPham SET " +
                     "TenSanPham=?, MaLoaiSanPham=?, SoDangKy=?, HoatChat=?, HamLuong=?, " +
                     "HangSanXuat=?, XuatXu=?, MaDonViTinh=?, MaDuongDung=?, GiaNhap=?, GiaBan=?, " +
                     "HinhAnh=?, QuyCachDongGoi=?, KeBanSanPham=?, HoatDong=? " +
                     "WHERE MaSanPham=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sp.getTenSanPham());
            ps.setString(2, sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().getMaLoaiSanPham() : null);
            ps.setString(3, sp.getSoDangKy());
            ps.setString(4, sp.getHoatChat());
            ps.setString(5, sp.getHamLuong());
            ps.setString(6, sp.getHangSanXuat());
            ps.setString(7, sp.getXuatXu());
            ps.setString(8, sp.getDonViTinh() != null ? sp.getDonViTinh().getMaDonViTinh() : null);
            ps.setString(9, sp.getDuongDung() != null ? sp.getDuongDung().name() : null); // ENUM -> VARCHAR
            ps.setDouble(10, sp.getGiaNhap());
            ps.setDouble(11, sp.getGiaBan());
            ps.setString(12, sp.getHinhAnh());
            ps.setString(13, sp.getQuyCachDongGoi());
            ps.setString(14, sp.getKeBanSanPham());
            ps.setBoolean(15, sp.isHoatDong());
            ps.setString(16, sp.getMaSanPham());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Xóa sản phẩm theo mã */
    public boolean deleteSanPham(String maSanPham) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "DELETE FROM SanPham WHERE MaSanPham = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSanPham);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // FK ràng buộc từ chi tiết hóa đơn/nhập kho...
        }
        return false;
    }

    /** Lấy 1 sản phẩm theo mã */
    public SanPham getSanPhamTheoMa(String maSanPham) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaSanPham, TenSanPham, MaLoaiSanPham, SoDangKy, HoatChat, HamLuong, " +
                     "HangSanXuat, XuatXu, MaDonViTinh, MaDuongDung, GiaNhap, GiaBan, HinhAnh, " +
                     "QuyCachDongGoi, KeBanSanPham, HoatDong " +
                     "FROM SanPham WHERE MaSanPham = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSanPham);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String ten    = rs.getString("TenSanPham");

                    String maLSP  = rs.getString("MaLoaiSanPham");
                    LoaiSanPham lsp = null;
                    if (maLSP != null) {
                        lsp = new LoaiSanPham();
                        try { lsp.setMaLoaiSanPham(maLSP); } catch (IllegalArgumentException ignore) {}
                    }

                    String soDK   = rs.getString("SoDangKy");
                    String hoatChat = rs.getString("HoatChat");
                    String hamLuong = rs.getString("HamLuong");
                    String hangSX = rs.getString("HangSanXuat");
                    String xuatXu = rs.getString("XuatXu");

                    String maDVT  = rs.getString("MaDonViTinh");
                    DonViTinh dvt = null;
                    if (maDVT != null) {
                        dvt = new DonViTinh();
                        try { dvt.setMaDonViTinh(maDVT); } catch (IllegalArgumentException ignore) {}
                    }

                    String ddStr  = rs.getString("MaDuongDung");
                    DuongDung dd  = null;
                    if (ddStr != null) {
                        try { dd = DuongDung.valueOf(ddStr); } catch (IllegalArgumentException ignore) {}
                    }

                    double giaNhap = rs.getDouble("GiaNhap");
                    double giaBan  = rs.getDouble("GiaBan");
                    String hinhAnh = rs.getString("HinhAnh");
                    String quyCach = rs.getString("QuyCachDongGoi");
                    String keBan   = rs.getString("KeBanSanPham");
                    boolean hoatDong = rs.getBoolean("HoatDong");

                    return new SanPham(maSanPham, ten, lsp, soDK, hoatChat, hamLuong, hangSX, xuatXu,
                                       dvt, dd, giaNhap, giaBan, hinhAnh, quyCach, keBan, hoatDong);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // không thấy
    }
}