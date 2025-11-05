package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import connectDB.connectDB;
import entity.KhachHang;

public class KhachHang_DAO {

    public KhachHang_DAO() {}

    /** 🔹 Lấy toàn bộ khách hàng */
    public ArrayList<KhachHang> layTatCaKhachHang() {
        ArrayList<KhachHang> danhSach = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT * FROM KhachHang";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                danhSach.add(taoKhachHangTuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy danh sách khách hàng: " + e.getMessage());
        }
        return danhSach;
    }

    /** 🔹 Thêm khách hàng mới */
    public boolean themKhachHang(KhachHang kh) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = """
            INSERT INTO KhachHang (MaKhachHang, TenKhachHang, GioiTinh, SoDienThoai, NgaySinh, HoatDong, DiemTichLuy)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, kh.getMaKhachHang());
            stmt.setString(2, kh.getTenKhachHang());
            stmt.setBoolean(3, kh.isGioiTinh());
            stmt.setString(4, kh.getSoDienThoai());
            stmt.setDate(5, kh.getNgaySinh() != null ? Date.valueOf(kh.getNgaySinh()) : null);
            stmt.setBoolean(6, kh.isHoatDong());
            stmt.setDouble(7, kh.getDiemTichLuy());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi thêm khách hàng: " + e.getMessage());
        }
        return false;
    }

    /** 🔹 Cập nhật thông tin khách hàng */
    public boolean capNhatKhachHang(KhachHang kh) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = """
            UPDATE KhachHang
            SET TenKhachHang=?, GioiTinh=?, SoDienThoai=?, NgaySinh=?, HoatDong=?, DiemTichLuy=?
            WHERE MaKhachHang=?
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, kh.getTenKhachHang());
            stmt.setBoolean(2, kh.isGioiTinh());
            stmt.setString(3, kh.getSoDienThoai());
            stmt.setDate(4, kh.getNgaySinh() != null ? Date.valueOf(kh.getNgaySinh()) : null);
            stmt.setBoolean(5, kh.isHoatDong());
            stmt.setDouble(6, kh.getDiemTichLuy());
            stmt.setString(7, kh.getMaKhachHang());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi cập nhật khách hàng: " + e.getMessage());
        }
        return false;
    }

    /** 🔹 Xóa khách hàng */
    public boolean xoaKhachHang(String maKhachHang) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "DELETE FROM KhachHang WHERE MaKhachHang=?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maKhachHang);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi xóa khách hàng: " + e.getMessage());
        }
        return false;
    }

    /** 🔹 Tìm khách hàng theo mã / tên / SĐT (LIKE gần đúng) */
    public ArrayList<KhachHang> timKhachHang(String tuKhoa) {
        ArrayList<KhachHang> danhSach = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = """
            SELECT * FROM KhachHang
            WHERE MaKhachHang LIKE ?
               OR TenKhachHang LIKE ?
               OR SoDienThoai LIKE ?
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            String key = "%" + tuKhoa.trim() + "%";
            stmt.setString(1, key);
            stmt.setString(2, key);
            stmt.setString(3, key);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(taoKhachHangTuResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi tìm khách hàng: " + e.getMessage());
        }
        return danhSach;
    }

    /** 🔹 Tìm khách hàng đang hoạt động */
    public ArrayList<KhachHang> timKhachHangHoatDong() {
        ArrayList<KhachHang> danhSach = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT * FROM KhachHang WHERE HoatDong = 1";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                danhSach.add(taoKhachHangTuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi tìm khách hàng hoạt động: " + e.getMessage());
        }
        return danhSach;
    }

    /** 🔹 Tìm khách hàng có điểm tích lũy ≥ mức chỉ định */
    public ArrayList<KhachHang> timKhachHangTheoDiemTichLuy(double diemToiThieu) {
        ArrayList<KhachHang> danhSach = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT * FROM KhachHang WHERE DiemTichLuy >= ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setDouble(1, diemToiThieu);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(taoKhachHangTuResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi tìm khách hàng theo điểm tích lũy: " + e.getMessage());
        }
        return danhSach;
    }
    /** 🔹 Tìm khách hàng chính xác theo mã (dùng cho các DAO khác) */
    public KhachHang timKhachHangTheoMa(String maKhachHang) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "SELECT * FROM KhachHang WHERE MaKhachHang = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maKhachHang);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return taoKhachHangTuResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi tìm khách hàng theo mã: " + e.getMessage());
        }

        return null; // Không tìm thấy
    }


    /** 🔹 Hàm tiện ích: Tạo đối tượng KhachHang từ ResultSet */
    private KhachHang taoKhachHangTuResultSet(ResultSet rs) throws SQLException {
        String ma = rs.getString("MaKhachHang");
        String ten = rs.getString("TenKhachHang");
        boolean gt = rs.getBoolean("GioiTinh");
        String sdt = rs.getString("SoDienThoai");
        Date d = rs.getDate("NgaySinh");
        LocalDate ns = (d != null) ? d.toLocalDate() : null;
        boolean hoatDong = rs.getBoolean("HoatDong");
        double diem = rs.getDouble("DiemTichLuy");

        KhachHang kh = new KhachHang(ma, ten, gt, sdt, ns);
        kh.setHoatDong(hoatDong);
        kh.setDiemTichLuy(diem);
        return kh;
    }
}
