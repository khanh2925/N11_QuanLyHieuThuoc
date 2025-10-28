package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connectDB.connectDB;
import entity.DonViTinh;
import entity.QuyCachDongGoi;
import entity.SanPham;

public class QuyCachDongGoi_DAO {

    public QuyCachDongGoi_DAO() {}

    // ===== Helper tạo entity từ ResultSet (inline, không tách mapping class) =====
    private QuyCachDongGoi fromRS(ResultSet rs) throws SQLException {
        String maQC  = rs.getString("MaQuyCach");
        String maSP  = rs.getString("MaSanPham");
        String maDVT = rs.getString("MaDonViTinh");
        int heSo     = rs.getInt("HeSoQuyDoi");
        double tlg   = rs.getDouble("TiLeGiam");
        boolean goc  = rs.getBoolean("DonViGoc");

        SanPham sp = new SanPham();
        try { sp.setMaSanPham(maSP); } catch (IllegalArgumentException ignore) {}

        DonViTinh dvt = new DonViTinh();
        try { dvt.setMaDonViTinh(maDVT); } catch (IllegalArgumentException ignore) {}

        return new QuyCachDongGoi(maQC, dvt, sp, heSo, tlg, goc);
    }

    // ===== Lấy tất cả quy cách =====
    public List<QuyCachDongGoi> getAll() {
        List<QuyCachDongGoi> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaQuyCach, MaSanPham, MaDonViTinh, HeSoQuyDoi, TiLeGiam, DonViGoc " +
                     "FROM QuyCachDongGoi ORDER BY MaSanPham, DonViGoc DESC, MaQuyCach";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) ds.add(fromRS(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    // ===== Lấy DS quy cách theo sản phẩm =====
    public List<QuyCachDongGoi> getBySanPham(String maSanPham) {
        List<QuyCachDongGoi> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaQuyCach, MaSanPham, MaDonViTinh, HeSoQuyDoi, TiLeGiam, DonViGoc " +
                     "FROM QuyCachDongGoi WHERE MaSanPham = ? " +
                     "ORDER BY DonViGoc DESC, HeSoQuyDoi, MaQuyCach";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSanPham);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ds.add(fromRS(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    // ===== Lấy 1 quy cách theo PK =====
    public QuyCachDongGoi getById(String maQuyCach) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaQuyCach, MaSanPham, MaDonViTinh, HeSoQuyDoi, TiLeGiam, DonViGoc " +
                     "FROM QuyCachDongGoi WHERE MaQuyCach = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maQuyCach);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return fromRS(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== Lấy 1 quy cách theo khóa kép (unique) =====
    public QuyCachDongGoi getByComposite(String maSanPham, String maDonViTinh) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaQuyCach, MaSanPham, MaDonViTinh, HeSoQuyDoi, TiLeGiam, DonViGoc " +
                     "FROM QuyCachDongGoi WHERE MaSanPham = ? AND MaDonViTinh = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSanPham);
            ps.setString(2, maDonViTinh);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return fromRS(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== Thêm mới (dùng PK MaQuyCach) =====
    public boolean create(QuyCachDongGoi q) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "INSERT INTO QuyCachDongGoi " +
                "(MaQuyCach, MaSanPham, MaDonViTinh, HeSoQuyDoi, TiLeGiam, DonViGoc) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, q.getMaQuyCach());
            ps.setString(2, q.getSanPham().getMaSanPham());
            ps.setString(3, q.getDonViTinh().getMaDonViTinh());
            ps.setInt(4, q.getHeSoQuyDoi());
            ps.setDouble(5, q.getTiLeGiam());
            ps.setBoolean(6, q.isDonViGoc());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // Có thể do trùng MaQuyCach, trùng (MaSanPham, MaDonViTinh), vi phạm CK/ FK
            e.printStackTrace();
            return false;
        }
    }

    // ===== Cập nhật theo PK =====
    public boolean updateById(QuyCachDongGoi q) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "UPDATE QuyCachDongGoi " +
                     "SET MaSanPham = ?, MaDonViTinh = ?, HeSoQuyDoi = ?, TiLeGiam = ?, DonViGoc = ? " +
                     "WHERE MaQuyCach = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, q.getSanPham().getMaSanPham());
            ps.setString(2, q.getDonViTinh().getMaDonViTinh());
            ps.setInt(3, q.getHeSoQuyDoi());
            ps.setDouble(4, q.getTiLeGiam());
            ps.setBoolean(5, q.isDonViGoc());
            ps.setString(6, q.getMaQuyCach());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // Có thể dính unique (MaSanPham, MaDonViTinh)
            e.printStackTrace();
            return false;
        }
    }

    // ===== Xóa theo PK =====
    public boolean deleteById(String maQuyCach) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "DELETE FROM QuyCachDongGoi WHERE MaQuyCach = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maQuyCach);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // Nếu đang bị tham chiếu (FK) nơi khác, DB sẽ báo lỗi
            e.printStackTrace();
            return false;
        }
    }

    // ===== Tìm quy cách là ĐƠN VỊ GỐC của 1 sản phẩm (nếu có) =====
    public QuyCachDongGoi getDonViGocCuaSanPham(String maSanPham) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT TOP 1 MaQuyCach, MaSanPham, MaDonViTinh, HeSoQuyDoi, TiLeGiam, DonViGoc " +
                     "FROM QuyCachDongGoi WHERE MaSanPham = ? AND DonViGoc = 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSanPham);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return fromRS(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // không có đơn vị gốc
    }

    // ===== Gợi ý tạo mã QCxxxxxx =====
    public String taoMaQuyCach() {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String prefix = "QC";

        String sql = "SELECT MAX(MaQuyCach) AS MaxQC FROM QuyCachDongGoi WHERE MaQuyCach LIKE 'QC%%%%%%'";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String last = rs.getString("MaxQC");
                if (last != null) {
                    int num = Integer.parseInt(last.substring(prefix.length()));
                    return String.format("%s%06d", prefix, num + 1);
                }
            }
            return prefix + "000001";
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            return prefix + "000001";
        }
    }
}
