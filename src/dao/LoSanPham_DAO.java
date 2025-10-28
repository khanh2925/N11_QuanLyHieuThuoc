package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

import connectDB.connectDB;
import entity.LoSanPham;
import entity.SanPham;

public class LoSanPham_DAO {

    private final SanPham_DAO sanPhamDAO; // 💡 THÊM THAM CHIẾU ĐẾN SanPham_DAO

    public LoSanPham_DAO() {
        // 💡 KHỞI TẠO SANPHAM_DAO
        this.sanPhamDAO = new SanPham_DAO(); 
    }

    /** Lấy toàn bộ lô sản phẩm */
    public ArrayList<LoSanPham> layTatCaLoSanPham() {
        ArrayList<LoSanPham> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaLo, HanSuDung, SoLuongNhap, SoLuongTon, MaSanPham FROM LoSanPham";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String maLo = rs.getString("MaLo");
                LocalDate hsd = rs.getDate("HanSuDung").toLocalDate();
                int soLuongNhap = rs.getInt("SoLuongNhap");
                int soLuongTon = rs.getInt("SoLuongTon");
                String maSP = rs.getString("MaSanPham");

                // 💡 GỌI DAO ĐỂ TẢI SẢN PHẨM ĐẦY ĐỦ
                SanPham sp = sanPhamDAO.laySanPhamTheoMa(maSP); 

                if (sp != null) { // Chỉ thêm nếu sản phẩm liên quan tồn tại
                    LoSanPham lo = new LoSanPham(maLo, hsd, soLuongNhap, soLuongTon, sp);
                    ds.add(lo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Thêm lô sản phẩm */
    public boolean themLoSanPham(LoSanPham lo) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "INSERT INTO LoSanPham (MaLo, HanSuDung, SoLuongNhap, SoLuongTon, MaSanPham) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, lo.getMaLo());
            ps.setDate(2, Date.valueOf(lo.getHanSuDung()));
            ps.setInt(3, lo.getSoLuongNhap());
            ps.setInt(4, lo.getSoLuongTon());
            ps.setString(5, lo.getSanPham() != null ? lo.getSanPham().getMaSanPham() : null);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // lỗi PK / FK sẽ in ra ở đây
        }
        return false;
    }

    /** Cập nhật lô sản phẩm */
    public boolean capNhatLoSanPham(LoSanPham lo) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "UPDATE LoSanPham "
                   + "SET HanSuDung = ?, SoLuongNhap = ?, SoLuongTon = ?, MaSanPham = ? "
                   + "WHERE MaLo = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(lo.getHanSuDung()));
            ps.setInt(2, lo.getSoLuongNhap());
            ps.setInt(3, lo.getSoLuongTon());
            ps.setString(4, lo.getSanPham() != null ? lo.getSanPham().getMaSanPham() : null);
            ps.setString(5, lo.getMaLo());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Xóa lô sản phẩm theo mã */
    public boolean xoaLoSanPham(String maLo) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "DELETE FROM LoSanPham WHERE MaLo = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maLo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // nếu có FK sẽ báo lỗi ở đây
        }
        return false;
    }

    /** Lấy 1 lô sản phẩm theo mã lô (chính xác) */
    public LoSanPham layLoTheoMa(String maLo) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaLo, HanSuDung, SoLuongNhap, SoLuongTon, MaSanPham "
                   + "FROM LoSanPham WHERE MaLo = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maLo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LocalDate hsd = rs.getDate("HanSuDung").toLocalDate();
                    int soLuongNhap = rs.getInt("SoLuongNhap");
                    int soLuongTon = rs.getInt("SoLuongTon");
                    String maSP = rs.getString("MaSanPham");

                    // 🟢 BƯỚC SỬA LỖI: Lấy đối tượng SanPham đầy đủ
                    SanPham sp = sanPhamDAO.laySanPhamTheoMa(maSP); 
                    
                    if (sp != null) { // Đảm bảo tìm thấy sản phẩm
                        return new LoSanPham(maLo, hsd, soLuongNhap, soLuongTon, sp);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 🔎 Lấy lô có hạn CŨ NHẤT (HSD nhỏ nhất) của 1 sản phẩm, ưu tiên còn tồn */
    public LoSanPham layLoCuNhat(String maSanPham) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        LoSanPham lo = null;

        String sql = """
            SELECT TOP 1 MaLo, HanSuDung, SoLuongNhap, SoLuongTon, MaSanPham
            FROM LoSanPham
            WHERE MaSanPham = ? AND SoLuongTon > 0
            ORDER BY HanSuDung ASC
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSanPham);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String maLo = rs.getString("MaLo");
                    LocalDate hsd = rs.getDate("HanSuDung").toLocalDate();
                    int soLuongNhap = rs.getInt("SoLuongNhap");
                    int soLuongTon = rs.getInt("SoLuongTon");
                    String maSP = rs.getString("MaSanPham");

                    // 🟢 BƯỚC SỬA LỖI: Lấy đối tượng SanPham đầy đủ
                    SanPham sp = sanPhamDAO.laySanPhamTheoMa(maSP); 
                    
                    if (sp != null) { // Đảm bảo tìm thấy sản phẩm
                        lo = new LoSanPham(maLo, hsd, soLuongNhap, soLuongTon, sp);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lo;
    }
    
    /** Lấy lô kế tiếp (hạn gần nhất còn hàng, sau khi lô hiện tại hết) */
    public LoSanPham layLoKeTiep(String maSanPham, LocalDate hanHienTai) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = """
            SELECT TOP 1 MaLo, HanSuDung, SoLuongNhap, SoLuongTon, MaSanPham
            FROM LoSanPham
            WHERE MaSanPham = ? AND SoLuongTon > 0 AND HanSuDung > ?
            ORDER BY HanSuDung ASC
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSanPham);
            ps.setDate(2, java.sql.Date.valueOf(hanHienTai));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String maLo = rs.getString("MaLo");
                LocalDate hanSuDung = rs.getDate("HanSuDung").toLocalDate();
                int soLuongTon = rs.getInt("SoLuongTon");
                int soLuongNhap = rs.getInt("SoLuongNhap");
                String maSP = rs.getString("MaSanPham");

                // 🟢 BƯỚC SỬA LỖI: Lấy đối tượng SanPham đầy đủ
                SanPham sp = sanPhamDAO.laySanPhamTheoMa(maSP); 
                
                if (sp != null) { // Đảm bảo tìm thấy sản phẩm
                    return new LoSanPham(maLo, hanSuDung, soLuongNhap, soLuongTon, sp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}