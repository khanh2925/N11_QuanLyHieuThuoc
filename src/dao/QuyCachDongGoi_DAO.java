package dao;

import java.sql.*;
import java.util.ArrayList;

import connectDB.connectDB;
import entity.QuyCachDongGoi;
import entity.DonViTinh;
import entity.SanPham;

public class QuyCachDongGoi_DAO {

    public QuyCachDongGoi_DAO() {}

    /** Lấy tất cả quy cách đóng gói */
    public ArrayList<QuyCachDongGoi> getAllQuyCachDongGoi() {
        ArrayList<QuyCachDongGoi> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaSanPham, MaDonViTinh, HeSoQuyDoi, TiLeGiam, DonViGoc FROM QuyCachDongGoi";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String maSP  = rs.getString("MaSanPham");
                String maDVT = rs.getString("MaDonViTinh");
                int heSo     = rs.getInt("HeSoQuyDoi");
                double tlg   = rs.getDouble("TiLeGiam");
                boolean goc  = rs.getBoolean("DonViGoc");

                SanPham sp = new SanPham();
                try { sp.setMaSanPham(maSP); } catch (IllegalArgumentException ignore) {}

                DonViTinh dvt = new DonViTinh();
                try { dvt.setMaDonViTinh(maDVT); } catch (IllegalArgumentException ignore) {}

                ds.add(new QuyCachDongGoi(dvt, sp, heSo, tlg, goc));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Lấy DS quy cách theo mã sản phẩm */
    public ArrayList<QuyCachDongGoi> getQuyCachTheoSanPham(String maSanPham) {
        ArrayList<QuyCachDongGoi> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaSanPham, MaDonViTinh, HeSoQuyDoi, TiLeGiam, DonViGoc " +
                     "FROM QuyCachDongGoi WHERE MaSanPham = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSanPham);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maDVT = rs.getString("MaDonViTinh");
                    int heSo     = rs.getInt("HeSoQuyDoi");
                    double tlg   = rs.getDouble("TiLeGiam");
                    boolean goc  = rs.getBoolean("DonViGoc");

                    SanPham sp = new SanPham();
                    try { sp.setMaSanPham(maSanPham); } catch (IllegalArgumentException ignore) {}

                    DonViTinh dvt = new DonViTinh();
                    try { dvt.setMaDonViTinh(maDVT); } catch (IllegalArgumentException ignore) {}

                    ds.add(new QuyCachDongGoi(dvt, sp, heSo, tlg, goc));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Lấy 1 quy cách theo khóa kép (MaSanPham + MaDonViTinh) */
    public QuyCachDongGoi getQuyCachTheoKhoa(String maSanPham, String maDonViTinh) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaSanPham, MaDonViTinh, HeSoQuyDoi, TiLeGiam, DonViGoc " +
                     "FROM QuyCachDongGoi WHERE MaSanPham = ? AND MaDonViTinh = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSanPham);
            ps.setString(2, maDonViTinh);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int heSo     = rs.getInt("HeSoQuyDoi");
                    double tlg   = rs.getDouble("TiLeGiam");
                    boolean goc  = rs.getBoolean("DonViGoc");

                    SanPham sp = new SanPham();
                    try { sp.setMaSanPham(maSanPham); } catch (IllegalArgumentException ignore) {}

                    DonViTinh dvt = new DonViTinh();
                    try { dvt.setMaDonViTinh(maDonViTinh); } catch (IllegalArgumentException ignore) {}

                    return new QuyCachDongGoi(dvt, sp, heSo, tlg, goc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // không thấy
    }

    /** Thêm quy cách */
    public boolean createQuyCachDongGoi(QuyCachDongGoi q) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "INSERT INTO QuyCachDongGoi (MaSanPham, MaDonViTinh, HeSoQuyDoi, TiLeGiam, DonViGoc) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, q.getSanPham() != null ? q.getSanPham().getMaSanPham() : null);
            ps.setString(2, q.getDonViTinh() != null ? q.getDonViTinh().getMaDonViTinh() : null);
            ps.setInt(3, q.getHeSoQuyDoi());
            ps.setDouble(4, q.getTiLeGiam());
            ps.setBoolean(5, q.isDonViGoc());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace(); // trùng PK kép / FK...
        }
        return false;
    }

    /** Cập nhật quy cách (theo khóa kép) */
    public boolean updateQuyCachDongGoi(QuyCachDongGoi q) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "UPDATE QuyCachDongGoi SET HeSoQuyDoi = ?, TiLeGiam = ?, DonViGoc = ? " +
                     "WHERE MaSanPham = ? AND MaDonViTinh = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, q.getHeSoQuyDoi());
            ps.setDouble(2, q.getTiLeGiam());
            ps.setBoolean(3, q.isDonViGoc());
            ps.setString(4, q.getSanPham() != null ? q.getSanPham().getMaSanPham() : null);
            ps.setString(5, q.getDonViTinh() != null ? q.getDonViTinh().getMaDonViTinh() : null);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Xóa quy cách theo khóa kép */
    public boolean deleteQuyCachDongGoi(String maSanPham, String maDonViTinh) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "DELETE FROM QuyCachDongGoi WHERE MaSanPham = ? AND MaDonViTinh = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSanPham);
            ps.setString(2, maDonViTinh);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace(); // nếu bị tham chiếu ở nơi khác -> DB ném lỗi ở đây
        }
        return false;
    }

    /** Tìm quy cách là ĐƠN VỊ GỐC của 1 sản phẩm (nếu có) */
    public QuyCachDongGoi getDonViGocCuaSanPham(String maSanPham) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaSanPham, MaDonViTinh, HeSoQuyDoi, TiLeGiam, DonViGoc " +
                     "FROM QuyCachDongGoi WHERE MaSanPham = ? AND DonViGoc = 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSanPham);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String maDVT = rs.getString("MaDonViTinh");
                    int heSo     = rs.getInt("HeSoQuyDoi");
                    double tlg   = rs.getDouble("TiLeGiam");
                    boolean goc  = rs.getBoolean("DonViGoc");

                    SanPham sp = new SanPham();
                    try { sp.setMaSanPham(maSanPham); } catch (IllegalArgumentException ignore) {}

                    DonViTinh dvt = new DonViTinh();
                    try { dvt.setMaDonViTinh(maDVT); } catch (IllegalArgumentException ignore) {}

                    return new QuyCachDongGoi(dvt, sp, heSo, tlg, goc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // không có đơn vị gốc
    }
}
