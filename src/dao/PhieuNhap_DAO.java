package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import connectDB.connectDB;
import entity.NhaCungCap;
import entity.NhanVien;
import entity.PhieuNhap;

public class PhieuNhap_DAO {

    public PhieuNhap_DAO() {}

    /** Lấy tất cả phiếu nhập */
    public List<PhieuNhap> getAllPhieuNhap() {
        List<PhieuNhap> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaPhieuNhap, NgayNhap, MaNhaCungCap, MaNhanVien, TongTien FROM PhieuNhap";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String maPN = rs.getString("MaPhieuNhap");
                Date d = rs.getDate("NgayNhap");
                LocalDate ngayNhap = (d != null) ? d.toLocalDate() : null;
                String maNCC = rs.getString("MaNhaCungCap");
                String maNV = rs.getString("MaNhanVien");
                double tongTien = rs.getDouble("TongTien");

                NhaCungCap ncc = new NhaCungCap(maNCC);
                NhanVien nv = new NhanVien(maNV);

                PhieuNhap pn = new PhieuNhap(maPN, ngayNhap, ncc, nv, tongTien);
                ds.add(pn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Thêm phiếu nhập mới */
    public boolean createPhieuNhap(PhieuNhap pn) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "INSERT INTO PhieuNhap (MaPhieuNhap, NgayNhap, MaNhaCungCap, MaNhanVien, TongTien) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pn.getMaPhieuNhap());
            ps.setDate(2, pn.getNgayNhap() != null ? Date.valueOf(pn.getNgayNhap()) : null);
            ps.setString(3, pn.getNhaCungCap() != null ? pn.getNhaCungCap().getMaNhaCungCap() : null);
            ps.setString(4, pn.getNhanVien() != null ? pn.getNhanVien().getMaNhanVien() : null);
            ps.setDouble(5, pn.getTongTien());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Cập nhật phiếu nhập */
    public boolean updatePhieuNhap(PhieuNhap pn) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "UPDATE PhieuNhap SET NgayNhap=?, MaNhaCungCap=?, MaNhanVien=?, TongTien=? WHERE MaPhieuNhap=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, pn.getNgayNhap() != null ? Date.valueOf(pn.getNgayNhap()) : null);
            ps.setString(2, pn.getNhaCungCap() != null ? pn.getNhaCungCap().getMaNhaCungCap() : null);
            ps.setString(3, pn.getNhanVien() != null ? pn.getNhanVien().getMaNhanVien() : null);
            ps.setDouble(4, pn.getTongTien());
            ps.setString(5, pn.getMaPhieuNhap());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Xóa phiếu nhập */
    public boolean deletePhieuNhap(String maPhieuNhap) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "DELETE FROM PhieuNhap WHERE MaPhieuNhap = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPhieuNhap);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Tìm phiếu nhập theo mã */
    public PhieuNhap getPhieuNhapTheoMa(String maPhieuNhap) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaPhieuNhap, NgayNhap, MaNhaCungCap, MaNhanVien, TongTien "
                   + "FROM PhieuNhap WHERE MaPhieuNhap = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPhieuNhap);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String maPN = rs.getString("MaPhieuNhap");
                    Date d = rs.getDate("NgayNhap");
                    LocalDate ngayNhap = (d != null) ? d.toLocalDate() : null;
                    String maNCC = rs.getString("MaNhaCungCap");
                    String maNV = rs.getString("MaNhanVien");
                    double tongTien = rs.getDouble("TongTien");

                    NhaCungCap ncc = new NhaCungCap(maNCC);
                    NhanVien nv = new NhanVien(maNV);

                    return new PhieuNhap(maPN, ngayNhap, ncc, nv, tongTien);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Tìm theo khoảng ngày nhập (lọc báo cáo) */
    public List<PhieuNhap> searchByDateRange(LocalDate from, LocalDate to) {
        List<PhieuNhap> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        StringBuilder sql = new StringBuilder("SELECT MaPhieuNhap, NgayNhap, MaNhaCungCap, MaNhanVien, TongTien FROM PhieuNhap WHERE 1=1 ");
        if (from != null) sql.append("AND NgayNhap >= ? ");
        if (to != null) sql.append("AND NgayNhap <= ? ");
        sql.append("ORDER BY NgayNhap DESC");

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int index = 1;
            if (from != null) ps.setDate(index++, Date.valueOf(from));
            if (to != null) ps.setDate(index++, Date.valueOf(to));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maPN = rs.getString("MaPhieuNhap");
                    Date d = rs.getDate("NgayNhap");
                    LocalDate ngayNhap = (d != null) ? d.toLocalDate() : null;
                    String maNCC = rs.getString("MaNhaCungCap");
                    String maNV = rs.getString("MaNhanVien");
                    double tongTien = rs.getDouble("TongTien");

                    NhaCungCap ncc = new NhaCungCap(maNCC);
                    NhanVien nv = new NhanVien(maNV);

                    ds.add(new PhieuNhap(maPN, ngayNhap, ncc, nv, tongTien));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
}
