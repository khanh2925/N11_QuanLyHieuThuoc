package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connectDB.connectDB;
import entity.NhaCungCap;

public class NhaCungCap_DAO {

    public NhaCungCap_DAO() {}
    
    public List<NhaCungCap> getAllNhaCungCap() {
        List<NhaCungCap> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaNhaCungCap, TenNhaCungCap, SoDienThoai, DiaChi FROM NhaCungCap ORDER BY MaNhaCungCap";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String ma = rs.getString("MaNhaCungCap");
                String ten = rs.getString("TenNhaCungCap");
                String sdt = rs.getString("SoDienThoai");
                String dia = rs.getString("DiaChi");
                ds.add(new NhaCungCap(ma, ten, sdt, dia));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean createNhaCungCap(NhaCungCap ncc) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "INSERT INTO NhaCungCap (MaNhaCungCap, TenNhaCungCap, SoDienThoai, DiaChi) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ncc.getMaNhaCungCap());
            ps.setString(2, ncc.getTenNhaCungCap());
            ps.setString(3, ncc.getSoDienThoai());
            ps.setString(4, ncc.getDiaChi());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // trùng khóa, vi phạm unique phone (nếu DB có), ...
            e.printStackTrace();
            return false;
        }
    }
    
    // update
    public boolean updateNhaCungCap(NhaCungCap ncc) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "UPDATE NhaCungCap " +
                     "SET TenNhaCungCap = ?, SoDienThoai = ?, DiaChi = ? " +
                     "WHERE MaNhaCungCap = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ncc.getTenNhaCungCap());
            ps.setString(2, ncc.getSoDienThoai());
            ps.setString(3, ncc.getDiaChi());
            ps.setString(4, ncc.getMaNhaCungCap());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // có thể do unique phone hoặc lỗi khác
            return false;
        }
    }


    /** Sinh mã NCC theo pattern NCC-xxx (3 chữ số) hoặc NCC-000001 nếu bạn muốn 6 chữ số, tùy sửa SQL */
    public String generateId() {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        // Nếu mã của bạn là NCC-001, NCC-002... dùng SUBSTRING sau ký tự thứ 5.
        String sql = "SELECT MAX(CAST(SUBSTRING(MaNhaCungCap, 5, 10) AS INT)) AS MaxNum " +
                     "FROM NhaCungCap WHERE MaNhaCungCap LIKE 'NCC-%'";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int next = 1;
            if (rs.next()) {
                next = rs.getInt("MaxNum") + 1;
            }
            return String.format("NCC-%03d", next); // ví dụ NCC-021
        } catch (SQLException e) {
            e.printStackTrace();
            return "NCC-001";
        }
    }
}