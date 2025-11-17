package dao;

import connectDB.connectDB;
import entity.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDon_DAO {

    // ============================================================
    // 🔍 Lấy 1 chi tiết hóa đơn theo mã (KHÔNG LỒNG DAO)
    // ============================================================
    public ChiTietHoaDon timKiemChiTietHoaDonBangMa(String maHD, String maLo) {

        String sql = """
                SELECT 
                    cthd.SoLuong AS SLHD,
                    cthd.GiaBan,
                    cthd.MaKM,
                    cthd.MaDonViTinh,

                    lo.MaLo,
                    lo.HanSuDung,
                    lo.SoLuongTon,
                    sp.MaSanPham,
                    sp.TenSanPham,

                    dvt.TenDonViTinh,

                    km.TenKM,
                    km.GiaTri,
                    km.HinhThuc
                FROM ChiTietHoaDon cthd
                JOIN LoSanPham lo ON lo.MaLo = cthd.MaLo
                JOIN SanPham sp ON sp.MaSanPham = lo.MaSanPham
                LEFT JOIN DonViTinh dvt ON dvt.MaDonViTinh = cthd.MaDonViTinh
                LEFT JOIN KhuyenMai km ON km.MaKM = cthd.MaKM
                WHERE cthd.MaHoaDon = ? AND cthd.MaLo = ?
                """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maHD);
            stmt.setString(2, maLo);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                // ========================
                // 🔹 Tạo HoaDon
                // ========================
                HoaDon hd = new HoaDon();
                hd.setMaHoaDon(maHD);

                // ========================
                // 🔹 Tạo Sản phẩm
                // ========================
                SanPham sp = new SanPham();
                sp.setMaSanPham(rs.getString("MaSanPham"));
                sp.setTenSanPham(rs.getString("TenSanPham"));

                // ========================
                // 🔹 Tạo Lô (LoSanPham)
                // ========================
                LoSanPham lo = new LoSanPham(rs.getString("MaLo"));
                lo.setHanSuDung(rs.getDate("HanSuDung").toLocalDate());
                lo.setSoLuongTon(rs.getInt("SoLuongTon"));
                lo.setSanPham(sp);

                // ========================
                // 🔹 Tạo ĐVT
                // ========================
                DonViTinh dvt = null;
                if (rs.getString("MaDonViTinh") != null) {
                    dvt = new DonViTinh(
                            rs.getString("MaDonViTinh"),
                            rs.getString("TenDonViTinh")
                    );
                }

                // ========================
                // 🔹 Tạo khuyến mãi
                // ========================
                KhuyenMai km = null;
                if (rs.getString("MaKM") != null) {
                    km = new KhuyenMai();
                    km.setMaKM(rs.getString("MaKM"));
                    km.setTenKM(rs.getString("TenKM"));
                    km.setGiaTri(rs.getDouble("GiaTri"));
                    km.setHinhThuc(
                            enums.HinhThucKM.valueOf(rs.getString("HinhThuc"))
                    );
                }

                // ========================
                // 🔹 Tạo ChiTietHoaDon
                // ========================
                return new ChiTietHoaDon(
                        hd,
                        lo,
                        rs.getDouble("SLHD"),
                        rs.getDouble("GiaBan"),
                        km,
                        dvt
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // ============================================================
    // 🔍 Lấy DS chi tiết hóa đơn theo mã (KHÔNG LỒNG DAO)
    // ============================================================
    public List<ChiTietHoaDon> layDanhSachChiTietTheoMaHD(String maHD) {

        List<ChiTietHoaDon> ds = new ArrayList<>();

        String sql = """
                SELECT 
                    cthd.SoLuong AS SLHD,
                    cthd.GiaBan,
                    cthd.MaKM,
                    cthd.MaDonViTinh,

                    lo.MaLo,
                    lo.HanSuDung,
                    lo.SoLuongTon,
                    sp.MaSanPham,
                    sp.TenSanPham,

                    dvt.TenDonViTinh,

                    km.TenKM,
                    km.GiaTri,
                    km.HinhThuc
                FROM ChiTietHoaDon cthd
                JOIN LoSanPham lo ON lo.MaLo = cthd.MaLo
                JOIN SanPham sp ON sp.MaSanPham = lo.MaSanPham
                LEFT JOIN DonViTinh dvt ON dvt.MaDonViTinh = cthd.MaDonViTinh
                LEFT JOIN KhuyenMai km ON km.MaKM = cthd.MaKM
                WHERE cthd.MaHoaDon = ?
                ORDER BY lo.MaLo
                """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maHD);
            ResultSet rs = stmt.executeQuery();

            // Bộ khung hóa đơn
            HoaDon hd = new HoaDon();
            hd.setMaHoaDon(maHD);

            while (rs.next()) {

                SanPham sp = new SanPham();
                sp.setMaSanPham(rs.getString("MaSanPham"));
                sp.setTenSanPham(rs.getString("TenSanPham"));

                LoSanPham lo = new LoSanPham(rs.getString("MaLo"));
                lo.setHanSuDung(rs.getDate("HanSuDung").toLocalDate());
                lo.setSoLuongTon(rs.getInt("SoLuongTon"));
                lo.setSanPham(sp);

                DonViTinh dvt = null;
                if (rs.getString("MaDonViTinh") != null) {
                    dvt = new DonViTinh(
                            rs.getString("MaDonViTinh"),
                            rs.getString("TenDonViTinh")
                    );
                }

                KhuyenMai km = null;
                if (rs.getString("MaKM") != null) {
                    km = new KhuyenMai();
                    km.setMaKM(rs.getString("MaKM"));
                    km.setTenKM(rs.getString("TenKM"));
                    km.setGiaTri(rs.getDouble("GiaTri"));
                    km.setHinhThuc(enums.HinhThucKM.valueOf(rs.getString("HinhThuc")));
                }

                ds.add(new ChiTietHoaDon(
                        hd,
                        lo,
                        rs.getDouble("SLHD"),
                        rs.getDouble("GiaBan"),
                        km,
                        dvt
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }
}
