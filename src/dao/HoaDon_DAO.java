/**
 * @author Quốc Khánh cute
 * @version 1.0
 * @since Oct 26, 2025
 *
 * Mô tả: Lớp này được tạo bởi Quốc Khánh vào ngày Oct 26, 2025.
 */
package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import connectDB.connectDB;
import entity.HoaDon;
import entity.NhanVien;
import entity.KhuyenMai;

/**
 * @author:
 * @version: 1.0
 * @created: Oct 2025
 *
 * DAO cho bảng HoaDon
 * - Không cho phép sửa hoặc xóa hóa đơn sau khi đã lập.
 * - Chỉ thêm mới và đọc dữ liệu.
 */
public class HoaDon_DAO {

    /**
     * Lấy toàn bộ danh sách hóa đơn trong hệ thống.
     */
    public ArrayList<HoaDon> docTuBang() {
        ArrayList<HoaDon> dsHoaDon = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = connectDB.getConnection();
            if (con == null || con.isClosed()) {
                throw new SQLException("Kết nối cơ sở dữ liệu không khả dụng!");
            }

            String sql = """
                SELECT hd.MaHoaDon, hd.MaKhachHang, hd.NgayLap, hd.MaNhanVien,
                       hd.ThuocTheoDon, km.MaKM, km.TenKM,
                       nv.TenNhanVien
                FROM HoaDon hd
                JOIN NhanVien nv ON hd.MaNhanVien = nv.MaNhanVien
                LEFT JOIN KhuyenMai km ON hd.MaKM = km.MaKM
                ORDER BY hd.NgayLap DESC
            """;

            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHoaDon(rs.getString("MaHoaDon"));
                hd.setMaKhachHang(rs.getString("MaKhachHang"));
                hd.setNgayLap(rs.getDate("NgayLap").toLocalDate());
                hd.setThuocTheoDon(rs.getBoolean("ThuocTheoDon"));

                // Tạo đối tượng nhân viên đơn giản (chỉ cần mã & tên)
                NhanVien nv = new NhanVien();
                nv.setMaNhanVien(rs.getString("MaNhanVien"));
                nv.setTenNhanVien(rs.getString("TenNhanVien"));
                hd.setNhanVien(nv);

                // Nếu có khuyến mãi
                String maKM = rs.getString("MaKM");
                if (maKM != null) {
                    KhuyenMai km = new KhuyenMai();
                    km.setMaKM(maKM);
                    km.setTenKM(rs.getString("TenKM"));
                    hd.setKhuyenMai(km);
                }

                dsHoaDon.add(hd);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return dsHoaDon;
    }

    /**
     * Lấy thông tin 1 hóa đơn theo mã.
     */
    public HoaDon layHoaDonTheoMa(String maHoaDon) {
        HoaDon hoaDon = null;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = connectDB.getConnection();
            if (con == null || con.isClosed()) {
                throw new SQLException("Kết nối cơ sở dữ liệu không khả dụng!");
            }

            String sql = """
                SELECT hd.MaHoaDon, hd.MaKhachHang, hd.NgayLap, hd.MaNhanVien,
                       hd.ThuocTheoDon, km.MaKM, km.TenKM,
                       nv.TenNhanVien
                FROM HoaDon hd
                JOIN NhanVien nv ON hd.MaNhanVien = nv.MaNhanVien
                LEFT JOIN KhuyenMai km ON hd.MaKM = km.MaKM
                WHERE hd.MaHoaDon = ?
            """;

            stmt = con.prepareStatement(sql);
            stmt.setString(1, maHoaDon);
            rs = stmt.executeQuery();

            if (rs.next()) {
                hoaDon = new HoaDon();
                hoaDon.setMaHoaDon(rs.getString("MaHoaDon"));
                hoaDon.setMaKhachHang(rs.getString("MaKhachHang"));
                hoaDon.setNgayLap(rs.getDate("NgayLap").toLocalDate());
                hoaDon.setThuocTheoDon(rs.getBoolean("ThuocTheoDon"));

                NhanVien nv = new NhanVien();
                nv.setMaNhanVien(rs.getString("MaNhanVien"));
                nv.setTenNhanVien(rs.getString("TenNhanVien"));
                hoaDon.setNhanVien(nv);

                String maKM = rs.getString("MaKM");
                if (maKM != null) {
                    KhuyenMai km = new KhuyenMai();
                    km.setMaKM(maKM);
                    km.setTenKM(rs.getString("TenKM"));
                    hoaDon.setKhuyenMai(km);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return hoaDon;
    }
    public ArrayList<HoaDon> layHoaDonTheoSoDienThoai(String soDienThoai) {
        ArrayList<HoaDon> dsHoaDon = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = connectDB.getConnection();
            if (con == null || con.isClosed()) {
                throw new SQLException("Kết nối cơ sở dữ liệu không khả dụng!");
            }

            String sql = """
                SELECT hd.MaHoaDon, hd.MaKhachHang, hd.NgayLap, hd.MaNhanVien,
                       hd.ThuocTheoDon, km.MaKM, km.TenKM,
                       nv.TenNhanVien, kh.TenKhachHang, kh.SoDienThoai
                FROM HoaDon hd
                JOIN NhanVien nv ON hd.MaNhanVien = nv.MaNhanVien
                JOIN KhachHang kh ON hd.MaKhachHang = kh.MaKhachHang
                LEFT JOIN KhuyenMai km ON hd.MaKM = km.MaKM
                WHERE kh.SoDienThoai = ?
                ORDER BY hd.NgayLap DESC
            """;

            stmt = con.prepareStatement(sql);
            stmt.setString(1, soDienThoai);
            rs = stmt.executeQuery();

            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHoaDon(rs.getString("MaHoaDon"));
                hd.setMaKhachHang(rs.getString("MaKhachHang"));
                hd.setNgayLap(rs.getDate("NgayLap").toLocalDate());
                hd.setThuocTheoDon(rs.getBoolean("ThuocTheoDon"));

                // Nhân viên lập hóa đơn
                NhanVien nv = new NhanVien();
                nv.setMaNhanVien(rs.getString("MaNhanVien"));
                nv.setTenNhanVien(rs.getString("TenNhanVien"));
                hd.setNhanVien(nv);

                // Khuyến mãi (nếu có)
                String maKM = rs.getString("MaKM");
                if (maKM != null) {
                    KhuyenMai km = new KhuyenMai();
                    km.setMaKM(maKM);
                    km.setTenKM(rs.getString("TenKM"));
                    hd.setKhuyenMai(km);
                }

                dsHoaDon.add(hd);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return dsHoaDon;
    }
    
    /**
     * Thêm mới 1 hóa đơn (khi lập hóa đơn bán hàng).
     */
    public boolean themHoaDon(HoaDon hoaDon) {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = connectDB.getConnection();
            if (con == null || con.isClosed()) {
                throw new SQLException("Kết nối cơ sở dữ liệu không khả dụng!");
            }

            String sql = """
                INSERT INTO HoaDon (MaHoaDon, NgayLap, MaNhanVien, MaKhachHang, MaKM, ThuocTheoDon)
                VALUES (?, ?, ?, ?, ?, ?)
            """;

            stmt = con.prepareStatement(sql);
            stmt.setString(1, hoaDon.getMaHoaDon());
            stmt.setDate(2, Date.valueOf(hoaDon.getNgayLap()));
            stmt.setString(3, hoaDon.getNhanVien().getMaNhanVien());
            stmt.setString(4, hoaDon.getMaKhachHang());

            if (hoaDon.getKhuyenMai() != null)
                stmt.setString(5, hoaDon.getKhuyenMai().getMaKM());
            else
                stmt.setNull(5, Types.NVARCHAR);

            stmt.setBoolean(6, hoaDon.isThuocTheoDon());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
