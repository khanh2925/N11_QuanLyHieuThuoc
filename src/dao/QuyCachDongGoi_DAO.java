package dao;

import java.sql.*;
import java.util.ArrayList;

import connectDB.connectDB; // Đảm bảo tên class connectDB đúng
import entity.QuyCachDongGoi;
import entity.DonViTinh;
import entity.SanPham;

public class QuyCachDongGoi_DAO {

    public QuyCachDongGoi_DAO() {}

    /** Lấy tất cả quy cách đóng gói */
    public ArrayList<QuyCachDongGoi> layTatCaQuyCachDongGoi() {
        ArrayList<QuyCachDongGoi> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaQuyCach, MaSanPham, MaDonViTinh, HeSoQuyDoi, TiLeGiam, DonViGoc FROM QuyCachDongGoi";
        
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String maQC = rs.getString("MaQuyCach"); // Lấy MaQuyCach
                String maSP  = rs.getString("MaSanPham");
                String maDVT = rs.getString("MaDonViTinh");
                int heSo     = rs.getInt("HeSoQuyDoi");
                double tlg   = rs.getDouble("TiLeGiam");
                boolean goc  = rs.getBoolean("DonViGoc");

                SanPham sp = new SanPham();
                try { sp.setMaSanPham(maSP); } catch (IllegalArgumentException ignore) {}

                DonViTinh dvt = new DonViTinh();
                try { dvt.setMaDonViTinh(maDVT); 
                } catch (IllegalArgumentException ignore) {}

                QuyCachDongGoi qc = new QuyCachDongGoi(maQC, dvt, sp, heSo, tlg, goc);
                ds.add(qc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
             e.printStackTrace();
        }
        return ds;
    }

    /** Lấy danh sách quy cách theo mã sản phẩm */
    public ArrayList<QuyCachDongGoi> timQuyCachTheoMaSanPham(String maSanPham) {
        ArrayList<QuyCachDongGoi> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaQuyCach, MaDonViTinh, HeSoQuyDoi, TiLeGiam, DonViGoc " +
                     "FROM QuyCachDongGoi WHERE MaSanPham = ?";
                     
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSanPham);

            try (ResultSet rs = ps.executeQuery()) {
                SanPham sp = new SanPham();
                try { sp.setMaSanPham(maSanPham); } catch (IllegalArgumentException ignore) { }

                while (rs.next()) {
                    String maQC = rs.getString("MaQuyCach");
                    String maDVT = rs.getString("MaDonViTinh");
                    int heSo     = rs.getInt("HeSoQuyDoi");
                    double tlg   = rs.getDouble("TiLeGiam");
                    boolean goc  = rs.getBoolean("DonViGoc");

                    DonViTinh dvt = new DonViTinh();
                    try { dvt.setMaDonViTinh(maDVT); } catch (IllegalArgumentException ignore) { /* Bỏ qua lỗi */}

                    QuyCachDongGoi qc = new QuyCachDongGoi(maQC, dvt, sp, heSo, tlg, goc);
                    ds.add(qc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
             e.printStackTrace();
        }
        return ds;
    }

    /** Lấy 1 quy cách theo khóa kép (MaSanPham + MaDonViTinh) */
    public QuyCachDongGoi timQuyCachTheoKhoa(String maSanPham, String maDonViTinh) {
        QuyCachDongGoi qc = null;
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaQuyCach, HeSoQuyDoi, TiLeGiam, DonViGoc " +
                     "FROM QuyCachDongGoi WHERE MaSanPham = ? AND MaDonViTinh = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSanPham);
            ps.setString(2, maDonViTinh);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String maQC = rs.getString("MaQuyCach"); // Lấy MaQuyCach
                    int heSo     = rs.getInt("HeSoQuyDoi");
                    double tlg   = rs.getDouble("TiLeGiam");
                    boolean goc  = rs.getBoolean("DonViGoc");

                    SanPham sp = new SanPham();
                    try { sp.setMaSanPham(maSanPham); } catch (IllegalArgumentException ignore) {}

                    DonViTinh dvt = new DonViTinh();
                    try { dvt.setMaDonViTinh(maDonViTinh); } catch (IllegalArgumentException ignore) {}

                    qc = new QuyCachDongGoi(maQC, dvt, sp, heSo, tlg, goc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
             e.printStackTrace();
        }
        return qc;
    }

    /** Thêm quy cách */
    public boolean themQuyCachDongGoi(QuyCachDongGoi q) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "INSERT INTO QuyCachDongGoi (MaQuyCach, MaSanPham, MaDonViTinh, HeSoQuyDoi, TiLeGiam, DonViGoc) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        // Theo form: Dùng try-with-resources
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, q.getMaQuyCach()); // Thêm MaQuyCach
            ps.setString(2, q.getSanPham() != null ? q.getSanPham().getMaSanPham() : null);
            ps.setString(3, q.getDonViTinh() != null ? q.getDonViTinh().getMaDonViTinh() : null);
            ps.setInt(4, q.getHeSoQuyDoi());
            ps.setDouble(5, q.getTiLeGiam());
            ps.setBoolean(6, q.isDonViGoc());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
             e.printStackTrace(); 
        } catch (NullPointerException e) {
              e.printStackTrace();
        }
        return false;
    }

    /** Cập nhật quy cách (theo khóa kép MaSanPham + MaDonViTinh) */
    public boolean capNhatQuyCachDongGoi(QuyCachDongGoi q) {
        // Theo form: Lấy connection trong method
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
        } catch (NullPointerException e) {
             System.err.println("Lỗi NullPointerException khi cập nhật quy cách: Sản phẩm hoặc Đơn vị tính bị null.");
             // e.printStackTrace();
        }
        return false;
    }

    /** Xóa quy cách theo khóa kép (MaSanPham + MaDonViTinh) */
    public boolean xoaQuyCachDongGoi(String maSanPham, String maDonViTinh) {
        // Theo form: Lấy connection trong method
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "DELETE FROM QuyCachDongGoi WHERE MaSanPham = ? AND MaDonViTinh = ?";

        // Theo form: Dùng try-with-resources
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSanPham);
            ps.setString(2, maDonViTinh);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
             e.printStackTrace(); 
        }
        return false;
    }

    /** Tìm quy cách là ĐƠN VỊ GỐC của 1 sản phẩm (nếu có) */
    public QuyCachDongGoi timDonViGocCuaSanPham(String maSanPham) {
        QuyCachDongGoi qc = null;
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaQuyCach, MaDonViTinh, HeSoQuyDoi, TiLeGiam, DonViGoc " +
                     "FROM QuyCachDongGoi WHERE MaSanPham = ? AND DonViGoc = 1"; // DonViGoc = 1 (true)

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSanPham);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { 
                    String maQC = rs.getString("MaQuyCach");
                    String maDVT = rs.getString("MaDonViTinh");
                    int heSo     = rs.getInt("HeSoQuyDoi");
                    double tlg   = rs.getDouble("TiLeGiam");
                    boolean goc  = rs.getBoolean("DonViGoc");

                    SanPham sp = new SanPham();
                    try { sp.setMaSanPham(maSanPham); } catch (IllegalArgumentException ignore) {}

                    DonViTinh dvt = new DonViTinh();
                    try { dvt.setMaDonViTinh(maDVT); } catch (IllegalArgumentException ignore) {}

                    qc = new QuyCachDongGoi(maQC, dvt, sp, heSo, tlg, goc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
             e.printStackTrace();
        }
        return qc; 
    }
}