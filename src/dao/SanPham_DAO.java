package dao;

import java.sql.*;
import java.util.ArrayList;

import connectDB.connectDB;
import entity.SanPham;
import enums.LoaiSanPham;
import enums.DuongDung;

public class SanPham_DAO {

    public SanPham_DAO() {}

    /** 🔹 Lấy toàn bộ sản phẩm */
    public ArrayList<SanPham> layTatCaSanPham() {
        ArrayList<SanPham> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaSanPham, TenSanPham, LoaiSanPham, SoDangKy, DuongDung, " +
                     "GiaNhap, GiaBan, HinhAnh, KeBanSanPham, HoatDong FROM SanPham";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String maSP = rs.getString("MaSanPham");
                String ten = rs.getString("TenSanPham");

                LoaiSanPham loai = null;
                String loaiStr = rs.getString("LoaiSanPham");
                if (loaiStr != null) {
                    try { loai = LoaiSanPham.valueOf(loaiStr); } catch (IllegalArgumentException ignore) {}
                }

                String soDK = rs.getString("SoDangKy");
                DuongDung dd = null;
                String duongDungStr = rs.getString("DuongDung");
                if (duongDungStr != null) {
                    try { dd = DuongDung.valueOf(duongDungStr); } catch (IllegalArgumentException ignore) {}
                }

                double giaNhap = rs.getDouble("GiaNhap");
                String hinhAnh = rs.getString("HinhAnh");
                String keBan = rs.getString("KeBanSanPham");
                boolean hoatDong = rs.getBoolean("HoatDong");

                SanPham sp = new SanPham(maSP, ten, loai, soDK, dd, giaNhap, hinhAnh, keBan, hoatDong);
                ds.add(sp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** 🔹 Thêm sản phẩm mới */
    public boolean themSanPham(SanPham sp) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "INSERT INTO SanPham (MaSanPham, TenSanPham, LoaiSanPham, SoDangKy, DuongDung, " +
                     "GiaNhap, GiaBan, HinhAnh, KeBanSanPham, HoatDong) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

    /** 🔹 Cập nhật thông tin sản phẩm */
    public boolean capNhatSanPham(SanPham sp) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "UPDATE SanPham SET TenSanPham=?, LoaiSanPham=?, SoDangKy=?, DuongDung=?, " +
                     "GiaNhap=?, GiaBan=?, HinhAnh=?, KeBanSanPham=?, HoatDong=? WHERE MaSanPham=?";

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

    /** 🔹 Xóa sản phẩm theo mã */
    public boolean xoaSanPham(String maSanPham) {
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

    /** 🔹 Lấy sản phẩm theo mã */
    public SanPham laySanPhamTheoMa(String maSanPham) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT * FROM SanPham WHERE MaSanPham = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSanPham);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LoaiSanPham loai = null;
                    String loaiStr = rs.getString("LoaiSanPham");
                    if (loaiStr != null) {
                       try { loai = LoaiSanPham.valueOf(loaiStr); } catch (IllegalArgumentException ignore) {}
                    }

                    DuongDung dd = null;
                    String duongDungStr = rs.getString("DuongDung");
                    if (duongDungStr != null) {
                        try { dd = DuongDung.valueOf(duongDungStr); } catch (IllegalArgumentException ignore) {}
                    }

                    return new SanPham(
                        rs.getString("MaSanPham"),
                        rs.getString("TenSanPham"),
                        loai,
                        rs.getString("SoDangKy"),
                        dd,
                        rs.getDouble("GiaNhap"),
                        rs.getString("HinhAnh"),
                        rs.getString("KeBanSanPham"),
                        rs.getBoolean("HoatDong")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 🔹 Tìm kiếm sản phẩm theo tên hoặc mã. */
    public ArrayList<SanPham> timKiemSanPham(String keyword) {
        ArrayList<SanPham> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        
        String searchTerm = (keyword == null) ? "" : keyword.trim();
        String sql = "SELECT * FROM SanPham WHERE TenSanPham LIKE ? OR MaSanPham LIKE ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + searchTerm + "%");
            ps.setString(2, "%" + searchTerm + "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LoaiSanPham loai = null;
                    String loaiStr = rs.getString("LoaiSanPham");
                    if (loaiStr != null) {
                       try { loai = LoaiSanPham.valueOf(loaiStr); } catch (IllegalArgumentException ignore) {}
                    }

                    DuongDung dd = null;
                    String duongDungStr = rs.getString("DuongDung");
                    if (duongDungStr != null) {
                        try { dd = DuongDung.valueOf(duongDungStr); } catch (IllegalArgumentException ignore) {}
                    }

                    SanPham sp = new SanPham(
                        rs.getString("MaSanPham"),
                        rs.getString("TenSanPham"),
                        loai,
                        rs.getString("SoDangKy"),
                        dd,
                        rs.getDouble("GiaNhap"),
                        rs.getString("HinhAnh"),
                        rs.getString("KeBanSanPham"),
                        rs.getBoolean("HoatDong")
                    );
                    ds.add(sp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
    
    /** 🔹 Lấy danh sách sản phẩm theo loại. */
    public ArrayList<SanPham> laySanPhamTheoLoai(LoaiSanPham loaiSP) {
        ArrayList<SanPham> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT * FROM SanPham WHERE LoaiSanPham = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, loaiSP.name());
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DuongDung dd = null;
                    String duongDungStr = rs.getString("DuongDung");
                    if (duongDungStr != null) {
                        try { dd = DuongDung.valueOf(duongDungStr); } catch (IllegalArgumentException ignore) {}
                    }

                    SanPham sp = new SanPham(
                        rs.getString("MaSanPham"),
                        rs.getString("TenSanPham"),
                        loaiSP,
                        rs.getString("SoDangKy"),
                        dd,
                        rs.getDouble("GiaNhap"),
                        rs.getString("HinhAnh"),
                        rs.getString("KeBanSanPham"),
                        rs.getBoolean("HoatDong")
                    );
                    ds.add(sp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** 🔹 Tìm sản phẩm theo số đăng ký (dùng trong bán hàng) */
    public SanPham timTheoSoDangKy(String soDangKy) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT * FROM SanPham WHERE SoDangKy = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, soDangKy);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LoaiSanPham loai = null;
                    try { loai = LoaiSanPham.valueOf(rs.getString("LoaiSanPham")); } catch (Exception ignore) {}
                    DuongDung dd = null;
                    try { dd = DuongDung.valueOf(rs.getString("DuongDung")); } catch (Exception ignore) {}

                    return new SanPham(
                        rs.getString("MaSanPham"),
                        rs.getString("TenSanPham"),
                        loai,
                        rs.getString("SoDangKy"),
                        dd,
                        rs.getDouble("GiaNhap"),
                        rs.getString("HinhAnh"),
                        rs.getString("KeBanSanPham"),
                        rs.getBoolean("HoatDong")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}