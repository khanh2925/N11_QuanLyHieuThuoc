package dao;

import connectDB.connectDB;
import entity.BangGia;
import entity.NhanVien;
import entity.ChiTietBangGia;
import entity.SanPham; // Giữ lại nếu các lớp entity khác cần

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BangGia_DAO {

    public BangGia_DAO() {}

    /** 🔹 Lấy tất cả bảng giá (Giữ nguyên) */
    public List<BangGia> layTatCaBangGia() {
        List<BangGia> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "SELECT * FROM BangGia ORDER BY NgayApDung DESC";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(taoBangGiaTuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy danh sách bảng giá: " + e.getMessage());
        }
        return ds;
    }

    /** 🔹 Lấy bảng giá đang hoạt động (Giữ nguyên) */
    public BangGia layBangGiaDangHoatDong() {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "SELECT * FROM BangGia WHERE HoatDong = 1";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return taoBangGiaTuResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy bảng giá đang hoạt động: " + e.getMessage());
        }
        return null;
    }

    /** 🔹 Tìm bảng giá theo mã (Giữ nguyên) */
    public BangGia timBangGiaTheoMa(String maBangGia) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "SELECT * FROM BangGia WHERE MaBangGia = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBangGia);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return taoBangGiaTuResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi tìm bảng giá theo mã: " + e.getMessage());
        }
        return null;
    }

    /** 🔹 Thêm bảng giá mới (Giữ nguyên) */
    public boolean themBangGia(BangGia bg) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = """
            INSERT INTO BangGia (MaBangGia, MaNhanVien, TenBangGia, NgayApDung, HoatDong)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bg.getMaBangGia());
            ps.setString(2, bg.getNhanVien().getMaNhanVien());
            ps.setString(3, bg.getTenBangGia());
            ps.setDate(4, Date.valueOf(bg.getNgayApDung()));
            ps.setBoolean(5, bg.isHoatDong());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi thêm bảng giá: " + e.getMessage());
        }
        return false;
    }

    /** 🔹 Cập nhật bảng giá (Giữ nguyên) */
    public boolean capNhatBangGia(BangGia bg) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = """
            UPDATE BangGia
            SET MaNhanVien=?, TenBangGia=?, NgayApDung=?, HoatDong=?
            WHERE MaBangGia=?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bg.getNhanVien().getMaNhanVien());
            ps.setString(2, bg.getTenBangGia());
            ps.setDate(3, Date.valueOf(bg.getNgayApDung()));
            ps.setBoolean(4, bg.isHoatDong());
            ps.setString(5, bg.getMaBangGia());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi cập nhật bảng giá: " + e.getMessage());
        }
        return false;
    }

    /** 🔹 Hủy kích hoạt tất cả bảng giá khác khi bật bảng giá mới (Giữ nguyên) */
    public boolean huyHoatDongTatCaTruBangGia(String maBangGia) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "UPDATE BangGia SET HoatDong = 0 WHERE MaBangGia <> ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBangGia);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi hủy hoạt động các bảng giá khác: " + e.getMessage());
        }
        return false;
    }

    /** 🔹 Xóa bảng giá (Giữ nguyên) */
    public boolean xoaBangGia(String maBangGia) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "DELETE FROM BangGia WHERE MaBangGia = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBangGia);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi xóa bảng giá: " + e.getMessage());
        }
        return false;
    }

    /** 🔹 Lấy danh sách chi tiết bảng giá theo mã bảng giá (ĐÃ SỬA) */
    public List<ChiTietBangGia> layChiTietTheoMaBangGia(String maBangGia) {
        List<ChiTietBangGia> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        // 💡 ĐÃ SỬA SQL: Loại bỏ join SanPham và chỉ lấy các trường định giá (GiaTu, GiaDen, TiLe)
        String sql = """
            SELECT MaBangGia, GiaTu, GiaDen, TiLe
            FROM ChiTietBangGia
            WHERE MaBangGia = ?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBangGia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BangGia bg = new BangGia(maBangGia);
                    // 💡 ĐÃ SỬA: Dùng constructor ChiTietBangGia không có SanPham
                    ChiTietBangGia ct = new ChiTietBangGia(
                        bg, 
                        rs.getDouble("GiaTu"),
                        rs.getDouble("GiaDen"),
                        rs.getDouble("TiLe")
                    );
                    ds.add(ct);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy chi tiết bảng giá: " + e.getMessage());
        }
        return ds;
    }

    /** 🔹 Thêm chi tiết bảng giá (ĐÃ SỬA) */
    public boolean themChiTietBangGia(ChiTietBangGia ct) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        // 💡 ĐÃ SỬA SQL: Loại bỏ MaSanPham
        String sql = "INSERT INTO ChiTietBangGia (MaBangGia, GiaTu, GiaDen, TiLe) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ct.getBangGia().getMaBangGia());
            // ps.setString(2, ct.getSanPham().getMaSanPham()); // ĐÃ XÓA
            ps.setDouble(2, ct.getGiaTu());
            ps.setDouble(3, ct.getGiaDen());
            ps.setDouble(4, ct.getTiLe());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi thêm chi tiết bảng giá: " + e.getMessage());
        }
        return false;
    }

    /** 🔹 Xóa toàn bộ chi tiết của một bảng giá (Giữ nguyên) */
    public boolean xoaTatCaChiTiet(String maBangGia) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "DELETE FROM ChiTietBangGia WHERE MaBangGia = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBangGia);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi xóa chi tiết bảng giá: " + e.getMessage());
        }
        return false;
    }

    // ========================= TIỆN ÍCH =========================
    private BangGia taoBangGiaTuResultSet(ResultSet rs) throws SQLException {
        String ma = rs.getString("MaBangGia");
        String ten = rs.getString("TenBangGia");
        LocalDate ngay = rs.getDate("NgayApDung").toLocalDate();
        boolean hoatDong = rs.getBoolean("HoatDong");

        NhanVien nv = new NhanVien(rs.getString("MaNhanVien"));
        return new BangGia(ma, nv, ten, ngay, hoatDong);
    }

    /** 🔹 Sinh mã bảng giá tự động (theo format BG-yyyyMMdd-xxxx) (Giữ nguyên) */
    public String taoMaBangGia() {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String today = java.time.LocalDate.now().toString().replaceAll("-", "");
        String prefix = "BG-" + today + "-";
        String sql = "SELECT MAX(MaBangGia) AS MaCuoi FROM BangGia WHERE MaBangGia LIKE ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getString("MaCuoi") != null) {
                    String last = rs.getString("MaCuoi");
                    int num = Integer.parseInt(last.substring(last.lastIndexOf("-") + 1));
                    return prefix + String.format("%04d", num + 1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prefix + "0001";
    }
}