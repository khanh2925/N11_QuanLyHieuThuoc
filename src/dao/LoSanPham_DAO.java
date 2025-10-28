package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;
import java.util.ArrayList;

import connectDB.connectDB;
import entity.LoSanPham;
import entity.SanPham;

public class LoSanPham_DAO {

    public LoSanPham_DAO() {}

    /** Lấy toàn bộ lô sản phẩm */
    public ArrayList<LoSanPham> layTatCaLoSanPham() {
        ArrayList<LoSanPham> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaLo, HanSuDung, SoLuongNhap, SoLuongTon, MaSanPham FROM LoSanPham";

        SanPham_DAO sanPhamDAO = new SanPham_DAO(); // Khởi tạo DAO sản phẩm

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String maLo = rs.getString("MaLo");
                LocalDate hsd = rs.getDate("HanSuDung").toLocalDate();
                int soLuongNhap = rs.getInt("SoLuongNhap");
                int soLuongTon = rs.getInt("SoLuongTon");
                String maSP = rs.getString("MaSanPham");

                SanPham sp = sanPhamDAO.getSanPhamTheoMa(maSP); // Lấy SP đầy đủ
                if (sp == null) {
                    System.err.println("Cảnh báo: Không tìm thấy sản phẩm mã " + maSP + " cho lô " + maLo);
                    sp = new SanPham(maSP); // Tạo tạm
                    sp.setTenSanPham("SP Không Tồn Tại");
                }

                LoSanPham lo = new LoSanPham(maLo, hsd, soLuongNhap, soLuongTon, sp);
                ds.add(lo);
            }
        } catch (SQLException | IllegalArgumentException e) { // Bắt cả lỗi entity
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
            ps.setInt(4, lo.getSoLuongTon()); // Đảm bảo ghi cả SL tồn ban đầu
            ps.setString(5, lo.getSanPham() != null ? lo.getSanPham().getMaSanPham() : null);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi thêm lô sản phẩm: " + e.getMessage());
        } catch (NullPointerException e) {
             System.err.println("Lỗi NullPointerException khi thêm lô: SanPham bị null.");
        }
        return false;
    }

    /** Cập nhật lô sản phẩm (theo MaLo) */
    public boolean capNhatLoSanPham(LoSanPham lo) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        // Cập nhật HSD, SL Nhập, SL Tồn, Mã SP
        String sql = "UPDATE LoSanPham SET HanSuDung = ?, SoLuongNhap = ?, SoLuongTon = ?, MaSanPham = ? "
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
        } catch (NullPointerException e) {
             System.err.println("Lỗi NullPointerException khi cập nhật lô: SanPham bị null.");
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
             System.err.println("Lỗi khi xóa lô sản phẩm (có thể do đang được tham chiếu): " + e.getMessage());
        }
        return false;
    }

    /** Lấy 1 lô sản phẩm theo mã lô (chính xác), bao gồm thông tin sản phẩm đầy đủ */
    public LoSanPham timLoSanPhamTheoMa(String maLo) {
        LoSanPham lo = null;
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT HanSuDung, SoLuongNhap, SoLuongTon, MaSanPham "
                   + "FROM LoSanPham WHERE MaLo = ?";
        SanPham_DAO sanPhamDAO = new SanPham_DAO();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maLo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LocalDate hsd = rs.getDate("HanSuDung").toLocalDate();
                    int soLuongNhap = rs.getInt("SoLuongNhap");
                    int soLuongTon = rs.getInt("SoLuongTon");
                    String maSP = rs.getString("MaSanPham");

                    SanPham sp = sanPhamDAO.getSanPhamTheoMa(maSP); // Lấy SP đầy đủ
                     if (sp == null) {
                        System.err.println("Cảnh báo: Không tìm thấy sản phẩm mã " + maSP + " cho lô " + maLo);
                        sp = new SanPham(maSP);
                        sp.setTenSanPham("SP Không Tồn Tại");
                    }

                    lo = new LoSanPham(maLo, hsd, soLuongNhap, soLuongTon, sp);
                }
            }
        } catch (SQLException | IllegalArgumentException e) { // Bắt cả lỗi entity
            e.printStackTrace();
        }
        return lo;
    }

    // *** THÊM PHƯƠNG THỨC TẠO MÃ LÔ ***
    /**
     * Tạo mã lô sản phẩm tự động (LO-xxxxxx).
     * @return Mã lô mới.
     */
    public String taoMaLo() {
        String newID = "LO-000001"; // Mã mặc định
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "SELECT MAX(MaLo) FROM LoSanPham WHERE MaLo LIKE 'LO-%'";

        try (PreparedStatement stmt = con.prepareStatement(sql); // Dùng PreparedStatement cho an toàn
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String lastID = rs.getString(1);
                if (lastID != null && lastID.startsWith("LO-") && lastID.length() > 3) {
                    try {
                        int lastNumber = Integer.parseInt(lastID.substring(3)); // Bỏ "LO-"
                        newID = String.format("LO-%06d", lastNumber + 1);
                    } catch (NumberFormatException nfe) {
                        System.err.println("Lỗi khi phân tích mã lô cuối cùng: " + lastID);
                    }
                }
                // Nếu lastID là null hoặc không đúng định dạng, newID vẫn là "LO-000001"
            }
        } catch (SQLException e) {
            e.printStackTrace();
             // Giữ nguyên newID = "LO-000001" nếu có lỗi SQL
        }
        return newID;
    }
    // *************************************
}
