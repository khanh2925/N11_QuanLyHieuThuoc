package dao;

import connectDB.connectDB;
import entity.LoSanPham;
import entity.SanPham;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class LoSanPham_DAO {

    private final Connection con;
    private final SanPham_DAO sanPhamDAO;

    public LoSanPham_DAO() {
        this.con = connectDB.getConnection();
        this.sanPhamDAO = new SanPham_DAO();
    }

    public LoSanPham timKiemLoSanPhamBangMa(String maLo) {
        String sql = "SELECT * FROM LoSanPham WHERE MaLo = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maLo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LocalDate hanSuDung = rs.getDate("HanSuDung").toLocalDate();
                    int soLuongTon = rs.getInt("SoLuongTon");
                    String maSanPham = rs.getString("MaSanPham");

                    // Lấy thông tin sản phẩm liên quan
                    SanPham sp = sanPhamDAO.findSanPhamById(maSanPham);
                    LocalDate ngaySanXuatDefault = LocalDate.of(1970, 1, 1);

                    return new LoSanPham(maLo, ngaySanXuatDefault, hanSuDung, soLuongTon, sp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<LoSanPham> timKiemTatCaSanPham() {
        List<LoSanPham> danhSachLo = new ArrayList<>();
        String sql = "SELECT * FROM LoSanPham";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String maLo = rs.getString("MaLo");
                LocalDate hanSuDung = rs.getDate("HanSuDung").toLocalDate();
                int soLuongTon = rs.getInt("SoLuongTon");
                String maSanPham = rs.getString("MaSanPham");

                SanPham sp = sanPhamDAO.findSanPhamById(maSanPham);
                LocalDate ngaySanXuatDefault = LocalDate.of(1970, 1, 1);

                LoSanPham lo = new LoSanPham(maLo, ngaySanXuatDefault, hanSuDung, soLuongTon, sp);
                danhSachLo.add(lo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachLo;
    }
    public List<LoSanPham> tiemKiemSanPhamBangMa(String maSP) {
        List<LoSanPham> danhSachLo = new ArrayList<>();
        String sql = "SELECT * FROM LoSanPham WHERE MaSanPham = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maSP);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String maLo = rs.getString("MaLo");
                    LocalDate hanSuDung = rs.getDate("HanSuDung").toLocalDate();
                    int soLuongTon = rs.getInt("SoLuongTon");

                    SanPham sp = sanPhamDAO.findSanPhamById(maSP);
                    LocalDate ngaySanXuatDefault = LocalDate.of(1970, 1, 1);

                    LoSanPham lo = new LoSanPham(maLo, ngaySanXuatDefault, hanSuDung, soLuongTon, sp);
                    danhSachLo.add(lo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachLo;
    }

    public boolean themLoSanPham(LoSanPham lo) {
        // Câu lệnh SQL không có NgaySanXuat vì không tồn tại trong DB
        String sql = "INSERT INTO LoSanPham (MaLo, HanSuDung, SoLuongTon, MaSanPham) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, lo.getMaLo());
            stmt.setDate(2, Date.valueOf(lo.getHanSuDung()));
            stmt.setInt(3, lo.getSoLuong());
            stmt.setString(4, lo.getSanPham().getMaSanPham());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean capNhatLoSanPham(LoSanPham lo) {
        String sql = "UPDATE LoSanPham SET HanSuDung = ?, SoLuongTon = ?, MaSanPham = ? WHERE MaLo = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(lo.getHanSuDung()));
            stmt.setInt(2, lo.getSoLuong());
            stmt.setString(3, lo.getSanPham().getMaSanPham());
            stmt.setString(4, lo.getMaLo());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean xoaLoSanPham(String maLo) {
        String sql = "DELETE FROM LoSanPham WHERE MaLo = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maLo);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}