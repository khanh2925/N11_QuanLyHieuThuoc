package dao;

import java.sql.*;
import java.util.ArrayList;

import connectDB.connectDB;
import entity.SanPham;
import entity.DonViTinh;
import enums.LoaiSanPham;
import enums.DuongDung;

public class SanPham_DAO {

    public SanPham_DAO() {}

    /** Lấy toàn bộ sản phẩm */
    public ArrayList<SanPham> getAllSanPham() {
        ArrayList<SanPham> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaSanPham, TenSanPham, MaLoaiSanPham, SoDangKy, " +
                     "MaDonViTinh, MaDuongDung, GiaNhap, GiaBan, HinhAnh, KeBanSanPham, HoatDong " +
                     "FROM SanPham";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String maSP   = rs.getString("MaSanPham");
                String ten    = rs.getString("TenSanPham");

                // LoaiSanPham (ENUM theo VARCHAR lưu name())
                String lspStr = rs.getString("MaLoaiSanPham");
                LoaiSanPham loai = null;
                if (lspStr != null) {
                    try { loai = LoaiSanPham.valueOf(lspStr); } catch (IllegalArgumentException ignore) {}
                }

                String soDK   = rs.getString("SoDangKy");

                // DonViTinh: chỉ set mã (tránh đụng validate tên/mô tả)
                String maDVT  = rs.getString("MaDonViTinh");
                DonViTinh dvt = null;
                if (maDVT != null) {
                    dvt = new DonViTinh();
                    try { dvt.setMaDonViTinh(maDVT); } catch (IllegalArgumentException ignore) {}
                }

                // DuongDung (ENUM theo VARCHAR lưu name())
                String ddStr  = rs.getString("MaDuongDung");
                DuongDung dd  = null;
                if (ddStr != null) {
                    try { dd = DuongDung.valueOf(ddStr); } catch (IllegalArgumentException ignore) {}
                }

                double giaNhap = rs.getDouble("GiaNhap");
                double giaBan  = rs.getDouble("GiaBan");
                String hinhAnh = rs.getString("HinhAnh");
                String keBan   = rs.getString("KeBanSanPham");
                boolean hoatDong = rs.getBoolean("HoatDong");

                SanPham sp = new SanPham(maSP, ten, loai, soDK, dvt, dd, giaNhap, giaBan, hinhAnh, keBan, hoatDong);
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
                     "(MaSanPham, TenSanPham, MaLoaiSanPham, SoDangKy, MaDonViTinh, MaDuongDung, " +
                     " GiaNhap, GiaBan, HinhAnh, KeBanSanPham, HoatDong) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sp.getMaSanPham());
            ps.setString(2, sp.getTenSanPham());
            ps.setString(3, sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().name() : null);
            ps.setString(4, sp.getSoDangKy());
            ps.setString(5, sp.getDonViTinh() != null ? sp.getDonViTinh().getMaDonViTinh() : null);
            ps.setString(6, sp.getDuongDung() != null ? sp.getDuongDung().name() : null);
            ps.setDouble(7, sp.getGiaNhap());
            ps.setDouble(8, sp.getGiaBan());
            ps.setString(9, sp.getHinhAnh());
            ps.setString(10, sp.getKeBanSanPham());
            ps.setBoolean(11, sp.isHoatDong());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace(); // trùng PK/FK/constraint...
        }
        return false;
    }

    /** Cập nhật sản phẩm theo mã */
    public boolean updateSanPham(SanPham sp) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "UPDATE SanPham SET TenSanPham=?, MaLoaiSanPham=?, SoDangKy=?, " +
                     "MaDonViTinh=?, MaDuongDung=?, GiaNhap=?, GiaBan=?, HinhAnh=?, KeBanSanPham=?, HoatDong=? " +
                     "WHERE MaSanPham=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sp.getTenSanPham());
            ps.setString(2, sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().name() : null);
            ps.setString(3, sp.getSoDangKy());
            ps.setString(4, sp.getDonViTinh() != null ? sp.getDonViTinh().getMaDonViTinh() : null);
            ps.setString(5, sp.getDuongDung() != null ? sp.getDuongDung().name() : null);
            ps.setDouble(6, sp.getGiaNhap());
            ps.setDouble(7, sp.getGiaBan());
            ps.setString(8, sp.getHinhAnh());
            ps.setString(9, sp.getKeBanSanPham());
            ps.setBoolean(10, sp.isHoatDong());
            ps.setString(11, sp.getMaSanPham());

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
            e.printStackTrace(); // nếu bị ràng buộc FK (CTHoaDon/Nhap...) thì DB báo ở đây
        }
        return false;
    }

    /** Lấy 1 sản phẩm theo mã */
    public SanPham getSanPhamTheoMa(String maSanPham) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaSanPham, TenSanPham, MaLoaiSanPham, SoDangKy, " +
                     "MaDonViTinh, MaDuongDung, GiaNhap, GiaBan, HinhAnh, KeBanSanPham, HoatDong " +
                     "FROM SanPham WHERE MaSanPham = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSanPham);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String ten    = rs.getString("TenSanPham");

                    String lspStr = rs.getString("MaLoaiSanPham");
                    LoaiSanPham loai = null;
                    if (lspStr != null) {
                        try { loai = LoaiSanPham.valueOf(lspStr); } catch (IllegalArgumentException ignore) {}
                    }

                    String soDK   = rs.getString("SoDangKy");

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
                    String keBan   = rs.getString("KeBanSanPham");
                    boolean hoatDong = rs.getBoolean("HoatDong");

                    return new SanPham(maSanPham, ten, loai, soDK, dvt, dd, giaNhap, giaBan, hinhAnh, keBan, hoatDong);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // không thấy
    }

    /** Tìm kiếm theo tên (LIKE, không phân biệt hoa thường nếu DB collation hỗ trợ) */
    public ArrayList<SanPham> searchSanPhamTheoTen(String keyword) {
        ArrayList<SanPham> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaSanPham, TenSanPham, MaLoaiSanPham, SoDangKy, " +
                     "MaDonViTinh, MaDuongDung, GiaNhap, GiaBan, HinhAnh, KeBanSanPham, HoatDong " +
                     "FROM SanPham WHERE TenSanPham LIKE ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + (keyword == null ? "" : keyword.trim()) + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maSP   = rs.getString("MaSanPham");
                    String ten    = rs.getString("TenSanPham");

                    String lspStr = rs.getString("MaLoaiSanPham");
                    LoaiSanPham loai = null;
                    if (lspStr != null) {
                        try { loai = LoaiSanPham.valueOf(lspStr); } catch (IllegalArgumentException ignore) {}
                    }

                    String soDK   = rs.getString("SoDangKy");

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
                    String keBan   = rs.getString("KeBanSanPham");
                    boolean hoatDong = rs.getBoolean("HoatDong");

                    ds.add(new SanPham(maSP, ten, loai, soDK, dvt, dd, giaNhap, giaBan, hinhAnh, keBan, hoatDong));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
}
