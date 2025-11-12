package dao;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import connectDB.connectDB;
import entity.LoSanPham;
import entity.SanPham;
import entity.ChiTietPhieuHuy;

public class LoSanPham_DAO {

    // 💡 SỬA: Thêm SanPham_DAO để lấy thông tin sản phẩm đầy đủ
    private final SanPham_DAO sanPhamDAO;

    public LoSanPham_DAO() {
        // 💡 SỬA: Khởi tạo SanPham_DAO
        this.sanPhamDAO = new SanPham_DAO();
    }

    /** Lấy toàn bộ lô sản phẩm */
    public ArrayList<LoSanPham> layTatCaLoSanPham() {
        ArrayList<LoSanPham> danhSach = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaLo, HanSuDung, SoLuongTon, MaSanPham FROM LoSanPham";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String maLo = rs.getString("MaLo");
                LocalDate hanSuDung = rs.getDate("HanSuDung").toLocalDate();
                int soLuongTon = rs.getInt("SoLuongTon");
                String maSP = rs.getString("MaSanPham");

                // 💡 SỬA: Lấy đầy đủ thông tin sản phẩm
                SanPham sp = sanPhamDAO.laySanPhamTheoMa(maSP);
                if (sp == null) { // Xử lý nếu sản phẩm không tìm thấy
                    sp = new SanPham();
                    try { sp.setMaSanPham(maSP); } catch (IllegalArgumentException ignore) {}
                }

                danhSach.add(new LoSanPham(maLo, hanSuDung, soLuongTon, sp));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách lô sản phẩm: " + e.getMessage());
        }
        return danhSach;
    }

    /** Thêm mới lô sản phẩm */
    public boolean themLoSanPham(LoSanPham lo) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = """
            INSERT INTO LoSanPham (MaLo, HanSuDung, SoLuongTon, MaSanPham)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, lo.getMaLo());
            stmt.setDate(2, Date.valueOf(lo.getHanSuDung()));
            stmt.setInt(3, lo.getSoLuongTon());
            stmt.setString(4, lo.getSanPham() != null ? lo.getSanPham().getMaSanPham() : null);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm lô sản phẩm: " + e.getMessage());
        }
        return false;
    }

    /** Cập nhật thông tin lô sản phẩm */
    public boolean capNhatLoSanPham(LoSanPham lo) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = """
            UPDATE LoSanPham
            SET HanSuDung=?, SoLuongTon=?, MaSanPham=?
            WHERE MaLo=?
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(lo.getHanSuDung()));
            stmt.setInt(2, lo.getSoLuongTon());
            stmt.setString(3, lo.getSanPham() != null ? lo.getSanPham().getMaSanPham() : null);
            stmt.setString(4, lo.getMaLo());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật lô sản phẩm: " + e.getMessage());
        }
        return false;
    }

    /** Xóa lô sản phẩm theo mã */
    public boolean xoaLoSanPham(String maLo) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "DELETE FROM LoSanPham WHERE MaLo=?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maLo);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa lô sản phẩm: " + e.getMessage());
        }
        return false;
    }

    /** Tìm lô sản phẩm chính xác theo mã */
    public LoSanPham timLoTheoMa(String maLo) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = """
            SELECT MaLo, HanSuDung, SoLuongTon, MaSanPham
            FROM LoSanPham
            WHERE MaLo = ?
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maLo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LocalDate hanSuDung = rs.getDate("HanSuDung").toLocalDate();
                    int soLuongTon = rs.getInt("SoLuongTon"); 
                    String maSP = rs.getString("MaSanPham");

                    // 💡 SỬA: Lấy đầy đủ thông tin sản phẩm
                    SanPham sp = sanPhamDAO.laySanPhamTheoMa(maSP);
                    if (sp == null) {
                        sp = new SanPham();
                        try { sp.setMaSanPham(maSP); } catch (IllegalArgumentException ignore) {}
                    }
                    
                    return new LoSanPham(maLo, hanSuDung, soLuongTon, sp);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm lô sản phẩm theo mã: " + e.getMessage());
        }
        return null;
    }

    /** Tìm lô có hạn sử dụng sắp hết (cũ nhất) theo mã sản phẩm */
    public LoSanPham timLoGanHetHanTheoSanPham(String maSanPham) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = """
            SELECT TOP 1 MaLo, HanSuDung, SoLuongTon, MaSanPham
            FROM LoSanPham
            WHERE MaSanPham = ?
              AND HanSuDung >= GETDATE()
              AND SoLuongTon > 0
            ORDER BY HanSuDung ASC
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maSanPham);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String maLo = rs.getString("MaLo");
                    LocalDate hanSuDung = rs.getDate("HanSuDung").toLocalDate();
                    int soLuongTon = rs.getInt("SoLuongTon");
                    String maSP = rs.getString("MaSanPham");

                    // 💡 SỬA: Lấy đầy đủ thông tin sản phẩm
                    SanPham sp = sanPhamDAO.laySanPhamTheoMa(maSP);
                     if (sp == null) {
                        sp = new SanPham();
                        try { sp.setMaSanPham(maSP); } catch (IllegalArgumentException ignore) {}
                    }

                    return new LoSanPham(maLo, hanSuDung, soLuongTon, sp);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm lô gần hết hạn: " + e.getMessage());
        }
        return null;
    }

    /** Lấy lô kế tiếp (hạn tiếp theo) nếu lô hiện tại đã hết hàng */
    public LoSanPham timLoKeTiepTheoSanPham(String maSanPham, LocalDate hanSuDungHienTai) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = """
            SELECT TOP 1 MaLo, HanSuDung, SoLuongTon, MaSanPham
            FROM LoSanPham
            WHERE MaSanPham = ?
              AND HanSuDung > ?
              AND HanSuDung >= GETDATE()
              AND SoLuongTon > 0
            ORDER BY HanSuDung ASC
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maSanPham);
            stmt.setDate(2, Date.valueOf(hanSuDungHienTai));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String maLo = rs.getString("MaLo");
                    LocalDate hanSuDung = rs.getDate("HanSuDung").toLocalDate();
                    int soLuongTon = rs.getInt("SoLuongTon");
                    String maSP = rs.getString("MaSanPham");

                    // 💡 SỬA: Lấy đầy đủ thông tin sản phẩm
                    SanPham sp = sanPhamDAO.laySanPhamTheoMa(maSP);
                     if (sp == null) {
                        sp = new SanPham();
                        try { sp.setMaSanPham(maSP); } catch (IllegalArgumentException ignore) {}
                    }

                    return new LoSanPham(maLo, hanSuDung, soLuongTon, sp);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm lô kế tiếp: " + e.getMessage());
        }
        return null;
    }

    /** Tính số lượng tồn thực tế (nhập - bán + trả - hủy + nhập lại) */
    public int tinhSoLuongTonThucTe(String maLo) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = """
            SELECT 
                COALESCE(SUM(ctn.SoLuongNhap), 0)
              - COALESCE(SUM(cth.SoLuong), 0)
              + COALESCE(SUM(CASE WHEN ctpt.TrangThai = 1 THEN ctpt.SoLuong ELSE 0 END), 0)
              - COALESCE(SUM(CASE WHEN ctph.TrangThai = ? THEN ctph.SoLuongHuy ELSE 0 END), 0)
              + COALESCE(SUM(CASE WHEN ctph.TrangThai = ? THEN ctph.SoLuongHuy ELSE 0 END), 0)
              AS SoLuongTon
            FROM LoSanPham lo
            LEFT JOIN ChiTietPhieuNhap ctn ON lo.MaLo = ctn.MaLo
            LEFT JOIN ChiTietHoaDon cth ON lo.MaLo = cth.MaLo
            LEFT JOIN ChiTietPhieuTra ctpt ON lo.MaLo = ctpt.MaLo
            LEFT JOIN ChiTietPhieuHuy ctph ON lo.MaLo = ctph.MaLo
            WHERE lo.MaLo = ?
            GROUP BY lo.MaLo
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, ChiTietPhieuHuy.DA_HUY);
            stmt.setInt(2, ChiTietPhieuHuy.NHAP_LAI_KHO);
            stmt.setString(3, maLo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("SoLuongTon");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tính số lượng tồn thực tế: " + e.getMessage());
        }
        return 0;
    }
    public String taoMaLoTuDong() {
        String sql = "SELECT TOP 1 MaLo FROM LoSanPham WHERE MaLo LIKE 'LO-%' ORDER BY MaLo DESC";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String lastMaLo = rs.getString("MaLo"); // Ví dụ: LO-098907
                int lastNumber = Integer.parseInt(lastMaLo.substring(3)); // 98707
                int nextNumber = lastNumber + 1;
                return String.format("LO-%06d", nextNumber); // LO-098908
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Nếu chưa có lô nào → bắt đầu từ LO-000001
        return "LO-000001";
    }
}