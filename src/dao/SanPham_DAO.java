package dao;

import connectDB.connectDB;
import entity.LoaiSanPham;
import entity.SanPham;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SanPham_DAO {

    private final Connection con;

    public SanPham_DAO() {
		this.con = null;
    }


    public SanPham findSanPhamById(String maSanPham) {
		return null;
    }
    public List<SanPham> timKiemFullSanPham() {
        List<SanPham> danhSachSP = new ArrayList<>();
        String sql = "SELECT * FROM SanPham";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String maSP = rs.getString("MaSanPham");
                SanPham sp = findSanPhamById(maSP);
                if (sp != null) {
                    danhSachSP.add(sp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachSP;
    }

    public boolean addSanPham(SanPham sp) {
        String sql = "INSERT INTO SanPham (MaSanPham, TenSanPham, MaLoaiSanPham, SoDangKy, HoatChat, HamLuong, " +
                     "HangSanXuat, XuatXu, DuongDung, GiaNhap, GiaBan, HinhAnh, KeBanSanPham, HoatDong) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, sp.getMaSanPham());
            stmt.setString(2, sp.getTenSanPham());
            stmt.setString(3, sp.getLoaiSanPham().getMaLoaiSanPham());
            stmt.setString(4, sp.getSoDangKy());
            stmt.setString(5, sp.getHoatChat());
            stmt.setString(6, sp.getHamLuong());
            stmt.setString(7, sp.getHangSanXuat());
            stmt.setString(8, sp.getXuatXu());
            stmt.setString(9, sp.getDuongDung().toString()); // Giả sử DuongDung là Enum
            stmt.setDouble(10, sp.getGiaNhap());
            stmt.setDouble(11, sp.getGiaBan());
            stmt.setString(12, sp.getHinhAnh());
            stmt.setString(13, sp.getKeBanSanPham());
            stmt.setBoolean(14, sp.isHoatDong());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean capNhatSanPham(SanPham sp) {
        String sql = "UPDATE SanPham SET TenSanPham = ?, MaLoaiSanPham = ?, SoDangKy = ?, HoatChat = ?, " +
                     "HamLuong = ?, HangSanXuat = ?, XuatXu = ?, DuongDung = ?, GiaNhap = ?, GiaBan = ?, " +
                     "HinhAnh = ?, KeBanSanPham = ?, HoatDong = ? WHERE MaSanPham = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, sp.getTenSanPham());
            stmt.setString(2, sp.getLoaiSanPham().getMaLoaiSanPham());
            stmt.setString(3, sp.getSoDangKy());
            stmt.setString(4, sp.getHoatChat());
            stmt.setString(5, sp.getHamLuong());
            stmt.setString(6, sp.getHangSanXuat());
            stmt.setString(7, sp.getXuatXu());
            stmt.setString(8, sp.getDuongDung().toString());
            stmt.setDouble(9, sp.getGiaNhap());
            stmt.setDouble(10, sp.getGiaBan());
            stmt.setString(11, sp.getHinhAnh());
            stmt.setString(12, sp.getKeBanSanPham());
            stmt.setBoolean(13, sp.isHoatDong());
            stmt.setString(14, sp.getMaSanPham());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean capNhatHoatDong(String maSanPham, boolean hoatDong) {
        String sql = "UPDATE SanPham SET HoatDong = ? WHERE MaSanPham = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setBoolean(1, hoatDong);
            stmt.setString(2, maSanPham);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}