package dao;

import java.sql.*;
import java.util.ArrayList;

import connectDB.connectDB;
import entity.QuyCachDongGoi;
import entity.DonViTinh;
import entity.SanPham;
import enums.DuongDung;
import enums.LoaiSanPham;

public class QuyCachDongGoi_DAO {

    public QuyCachDongGoi_DAO() {}

    /** Lấy tất cả quy cách đóng gói với thông tin chi tiết (JOIN 3 bảng) */
    public ArrayList<QuyCachDongGoi> layTatCaQuyCachDongGoi() {
        ArrayList<QuyCachDongGoi> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql =
            "SELECT qc.MaQuyCach, qc.HeSoQuyDoi, qc.TiLeGiam, qc.DonViGoc, " +
            "       sp.MaSanPham, sp.TenSanPham, sp.LoaiSanPham, sp.SoDangKy, sp.DuongDung, sp.GiaNhap, sp.HinhAnh, sp.KeBanSanPham, sp.HoatDong, " +
            "       dvt.MaDonViTinh, dvt.TenDonViTinh " +
            "FROM QuyCachDongGoi qc " +
            "JOIN SanPham sp ON qc.MaSanPham = sp.MaSanPham " +
            "JOIN DonViTinh dvt ON qc.MaDonViTinh = dvt.MaDonViTinh";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String maQC = rs.getString("MaQuyCach");
                try {
                    DonViTinh dvt = new DonViTinh(
                        rs.getString("MaDonViTinh"),
                        rs.getString("TenDonViTinh")
                    );

                    // Enum LoaiSanPham
                    LoaiSanPham loai = null;
                    String loaiSPStr = rs.getString("LoaiSanPham");
                    if (loaiSPStr != null && !loaiSPStr.isBlank()) {
                        try { loai = LoaiSanPham.valueOf(loaiSPStr.trim().toUpperCase()); }
                        catch (IllegalArgumentException e) {
                            System.err.println("LoaiSanPham không hợp lệ cho MaQuyCach " + maQC + ": " + loaiSPStr);
                        }
                    }

                    // Enum DuongDung
                    DuongDung dd = null;
                    String duongDungStr = rs.getString("DuongDung");
                    if (duongDungStr != null && !duongDungStr.isBlank()) {
                        try { dd = DuongDung.valueOf(duongDungStr.trim().toUpperCase()); }
                        catch (IllegalArgumentException e) {
                            System.err.println("DuongDung không hợp lệ cho MaQuyCach " + maQC + ": " + duongDungStr);
                        }
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

                    QuyCachDongGoi qc = new QuyCachDongGoi(
                        maQC, dvt, sp,
                        rs.getInt("HeSoQuyDoi"),
                        rs.getDouble("TiLeGiam"),
                        rs.getBoolean("DonViGoc")
                    );
                    ds.add(qc);

                } catch (IllegalArgumentException e) {
                    System.err.println("Lỗi dữ liệu không hợp lệ (MaQuyCach " + maQC + "): " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Sinh mã quy cách mới (dạng QC-000001) */
    public String taoMaQuyCach() {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "SELECT TOP 1 MaQuyCach FROM QuyCachDongGoi WHERE MaQuyCach LIKE 'QC-%' ORDER BY MaQuyCach DESC";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String lastMa = rs.getString("MaQuyCach"); // ví dụ QC-000123
                if (lastMa != null && lastMa.matches("^QC-\\d{6}$")) {
                    int lastNum = Integer.parseInt(lastMa.substring(3)); // bỏ QC-
                    return String.format("QC-%06d", lastNum + 1);
                }
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        return "QC-000001";
    }

    /** Thêm quy cách */
    public boolean themQuyCachDongGoi(QuyCachDongGoi q) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "INSERT INTO QuyCachDongGoi (MaQuyCach, MaSanPham, MaDonViTinh, HeSoQuyDoi, TiLeGiam, DonViGoc) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, q.getMaQuyCach());
            ps.setString(2, q.getSanPham().getMaSanPham());
            ps.setString(3, q.getDonViTinh().getMaDonViTinh());
            ps.setInt(4, q.getHeSoQuyDoi());
            ps.setDouble(5, q.getTiLeGiam());
            ps.setBoolean(6, q.isDonViGoc());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Cập nhật quy cách */
    public boolean capNhatQuyCachDongGoi(QuyCachDongGoi q) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "UPDATE QuyCachDongGoi SET MaSanPham = ?, MaDonViTinh = ?, HeSoQuyDoi = ?, TiLeGiam = ?, DonViGoc = ? WHERE MaQuyCach = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, q.getSanPham().getMaSanPham());
            ps.setString(2, q.getDonViTinh().getMaDonViTinh());
            ps.setInt(3, q.getHeSoQuyDoi());
            ps.setDouble(4, q.getTiLeGiam());
            ps.setBoolean(5, q.isDonViGoc());
            ps.setString(6, q.getMaQuyCach());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
