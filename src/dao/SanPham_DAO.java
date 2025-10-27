package dao;

import java.sql.*;
import java.util.ArrayList;

import connectDB.connectDB;
import entity.SanPham;
import enums.LoaiSanPham;
import enums.DuongDung;

public class SanPham_DAO {

    public SanPham_DAO() {}

    /** Lấy toàn bộ sản phẩm */
    public ArrayList<SanPham> getAllSanPham() {
        ArrayList<SanPham> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaSanPham, TenSanPham, LoaiSanPham, SoDangKy, DuongDung, " +
                     "GiaNhap, GiaBan, HinhAnh, KeBanSanPham, HoatDong " +
                     "FROM SanPham";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String maSP   = rs.getString("MaSanPham");
                String ten    = rs.getString("TenSanPham");

                // LoaiSanPham (ENUM theo VARCHAR lưu name())
                LoaiSanPham loai = null;
                String lspStr = rs.getString("LoaiSanPham");
                if (lspStr != null) {
                    try { loai = LoaiSanPham.valueOf(lspStr); } catch (IllegalArgumentException ignore) {}
                }

                String soDK   = rs.getString("SoDangKy");

                // DuongDung (ENUM theo VARCHAR lưu name())
                DuongDung dd  = null;
                String ddStr  = rs.getString("DuongDung");
                if (ddStr != null) {
                    try { dd = DuongDung.valueOf(ddStr); } catch (IllegalArgumentException ignore) {}
                }

                double giaNhap = rs.getDouble("GiaNhap");
                double giaBan  = rs.getDouble("GiaBan");
                String hinhAnh = rs.getString("HinhAnh");
                String keBan   = rs.getString("KeBanSanPham");
                boolean hoatDong = rs.getBoolean("HoatDong");

                SanPham sp = new SanPham(maSP, ten, loai, soDK, dd, giaNhap, giaBan, hinhAnh, keBan, hoatDong);
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
                     "(MaSanPham, TenSanPham, LoaiSanPham, SoDangKy, DuongDung, " +
                     " GiaNhap, GiaBan, HinhAnh, KeBanSanPham, HoatDong) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sp.getMaSanPham());
            ps.setString(2, sp.getTenSanPham());
            ps.setString(3, sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().name() : null);
            ps.setString(4, sp.getSoDangKy());
            ps.setString(5, sp.getDuongDung() != null ? sp.getDuongDung().name() : null);
            ps.setDouble(6, sp.getGiaNhap());
            ps.setDouble(7, sp.getGiaBan());
            ps.setString(8, sp.getHinhAnh());
            ps.setString(9, sp.getKeBanSanPham());
            ps.setBoolean(10, sp.isHoatDong());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Cập nhật sản phẩm */
    public boolean updateSanPham(SanPham sp) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "UPDATE SanPham SET TenSanPham=?, LoaiSanPham=?, SoDangKy=?, " +
                     "DuongDung=?, GiaNhap=?, GiaBan=?, HinhAnh=?, KeBanSanPham=?, HoatDong=? " +
                     "WHERE MaSanPham=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sp.getTenSanPham());
            ps.setString(2, sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().name() : null);
            ps.setString(3, sp.getSoDangKy());
            ps.setString(4, sp.getDuongDung() != null ? sp.getDuongDung().name() : null);
            ps.setDouble(5, sp.getGiaNhap());
            ps.setDouble(6, sp.getGiaBan());
            ps.setString(7, sp.getHinhAnh());
            ps.setString(8, sp.getKeBanSanPham());
            ps.setBoolean(9, sp.isHoatDong());
            ps.setString(10, sp.getMaSanPham());

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
            e.printStackTrace();
        }
        return false;
    }

    /** Lấy 1 sản phẩm theo mã */
    public SanPham getSanPhamTheoMa(String maSanPham) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaSanPham, TenSanPham, LoaiSanPham, SoDangKy, DuongDung, " +
                     "GiaNhap, GiaBan, HinhAnh, KeBanSanPham, HoatDong " +
                     "FROM SanPham WHERE MaSanPham = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSanPham);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String ten    = rs.getString("TenSanPham");

                    LoaiSanPham loai = null;
                    String lspStr = rs.getString("LoaiSanPham");
                    if (lspStr != null) {
                        try { loai = LoaiSanPham.valueOf(lspStr); } catch (IllegalArgumentException ignore) {}
                    }

                    String soDK   = rs.getString("SoDangKy");

                    DuongDung dd  = null;
                    String ddStr  = rs.getString("DuongDung");
                    if (ddStr != null) {
                        try { dd = DuongDung.valueOf(ddStr); } catch (IllegalArgumentException ignore) {}
                    }

                    double giaNhap = rs.getDouble("GiaNhap");
                    double giaBan  = rs.getDouble("GiaBan");
                    String hinhAnh = rs.getString("HinhAnh");
                    String keBan   = rs.getString("KeBanSanPham");
                    boolean hoatDong = rs.getBoolean("HoatDong");

                    return new SanPham(maSanPham, ten, loai, soDK, dd, giaNhap, giaBan, hinhAnh, keBan, hoatDong);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Tìm kiếm theo tên (LIKE) */
    public ArrayList<SanPham> searchSanPhamTheoTen(String keyword) {
        ArrayList<SanPham> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaSanPham, TenSanPham, LoaiSanPham, SoDangKy, DuongDung, " +
                     "GiaNhap, GiaBan, HinhAnh, KeBanSanPham, HoatDong " +
                     "FROM SanPham WHERE TenSanPham LIKE ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + (keyword == null ? "" : keyword.trim()) + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maSP   = rs.getString("MaSanPham");
                    String ten    = rs.getString("TenSanPham");

                    LoaiSanPham loai = null;
                    String lspStr = rs.getString("LoaiSanPham");
                    if (lspStr != null) {
                        try { loai = LoaiSanPham.valueOf(lspStr); } catch (IllegalArgumentException ignore) {}
                    }

                    String soDK   = rs.getString("SoDangKy");

                    DuongDung dd  = null;
                    String ddStr  = rs.getString("DuongDung");
                    if (ddStr != null) {
                        try { dd = DuongDung.valueOf(ddStr); } catch (IllegalArgumentException ignore) {}
                    }

                    double giaNhap = rs.getDouble("GiaNhap");
                    double giaBan  = rs.getDouble("GiaBan");
                    String hinhAnh = rs.getString("HinhAnh");
                    String keBan   = rs.getString("KeBanSanPham");
                    boolean hoatDong = rs.getBoolean("HoatDong");

                    ds.add(new SanPham(maSP, ten, loai, soDK, dd, giaNhap, giaBan, hinhAnh, keBan, hoatDong));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Lấy danh sách sản phẩm với quy cách nhỏ nhất (HeSoQuyDoi thấp nhất) */
    /** Lấy danh sách sản phẩm với quy cách nhỏ nhất (HeSoQuyDoi thấp nhất, join DonViTinh) */
    public ArrayList<Object[]> getSanPhamKemQuyCachNhoNhat() {
        ArrayList<Object[]> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql =
            "SELECT sp.MaSanPham, sp.TenSanPham, sp.LoaiSanPham, sp.SoDangKy, sp.DuongDung, " +
            "       sp.GiaNhap, sp.GiaBan, sp.HinhAnh, sp.KeBanSanPham, sp.HoatDong, " +
            "       dvt.TenDonViTinh AS TenDonViTinh, qc.HeSoQuyDoi " +
            "FROM SanPham sp " +
            "OUTER APPLY ( " +
            "    SELECT TOP 1 q.MaDonViTinh, q.HeSoQuyDoi " +
            "    FROM QuyCachDongGoi q " +
            "    WHERE q.MaSanPham = sp.MaSanPham " +
            "    ORDER BY q.HeSoQuyDoi ASC " +
            ") qc " +
            "LEFT JOIN DonViTinh dvt ON qc.MaDonViTinh = dvt.MaDonViTinh " +
            "ORDER BY sp.MaSanPham";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String maSP      = rs.getString("MaSanPham");
                String ten       = rs.getString("TenSanPham");
                String loai      = rs.getString("LoaiSanPham");
                String soDK      = rs.getString("SoDangKy");
                String duongDung = rs.getString("DuongDung");
                double giaNhap   = rs.getDouble("GiaNhap");
                double giaBan    = rs.getDouble("GiaBan");
                String hinhAnh   = rs.getString("HinhAnh");
                String keBan     = rs.getString("KeBanSanPham");
                boolean hoatDong = rs.getBoolean("HoatDong");
                String donViTinh = rs.getString("TenDonViTinh");

                ds.add(new Object[]{
                    hinhAnh, maSP, ten, loai, soDK,
                    duongDung == null ? "" : duongDung,
                    giaNhap, giaBan,
                    donViTinh != null ? donViTinh : "", // hiển thị “Viên/Vỉ/Hộp”
                    keBan,
                    hoatDong ? "Đang kinh doanh" : "Ngừng bán"
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

}
