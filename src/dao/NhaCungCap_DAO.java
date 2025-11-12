package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connectDB.connectDB;
import entity.NhaCungCap;

public class NhaCungCap_DAO {

    public NhaCungCap_DAO() {}

    /** 🔹 Lấy toàn bộ nhà cung cấp */
    public List<NhaCungCap> layTatCaNhaCungCap() {
        List<NhaCungCap> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = """
            SELECT MaNhaCungCap, TenNhaCungCap, SoDienThoai, DiaChi, Email, HoatDong
            FROM NhaCungCap
            ORDER BY MaNhaCungCap
        """;

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                NhaCungCap ncc = new NhaCungCap(
                        rs.getString("MaNhaCungCap"),
                        rs.getString("TenNhaCungCap"),
                        rs.getString("SoDienThoai"),
                        rs.getString("DiaChi"),
                        rs.getString("Email")
                );
                ncc.setHoatDong(rs.getBoolean("HoatDong"));
                ds.add(ncc);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy danh sách nhà cung cấp: " + e.getMessage());
        }
        return ds;
    }

    /** 🔹 Thêm nhà cung cấp mới */
    public boolean themNhaCungCap(NhaCungCap ncc) {
        connectDB.getInstance();
        String sql = """
            INSERT INTO NhaCungCap (MaNhaCungCap, TenNhaCungCap, SoDienThoai, DiaChi, Email, HoatDong)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ncc.getMaNhaCungCap());
            ps.setString(2, ncc.getTenNhaCungCap());
            ps.setString(3, ncc.getSoDienThoai());
            ps.setString(4, ncc.getDiaChi());
            ps.setString(5, ncc.getEmail());
            ps.setBoolean(6, ncc.isHoatDong());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi thêm nhà cung cấp: " + e.getMessage());
            return false;
        }
    }

    /** 🔹 Cập nhật nhà cung cấp */
    public boolean capNhatNhaCungCap(NhaCungCap ncc) {
        connectDB.getInstance();
        String sql = """
            UPDATE NhaCungCap
            SET TenNhaCungCap=?, SoDienThoai=?, DiaChi=?, Email=?, HoatDong=?
            WHERE MaNhaCungCap=?
        """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ncc.getTenNhaCungCap());
            ps.setString(2, ncc.getSoDienThoai());
            ps.setString(3, ncc.getDiaChi());
            ps.setString(4, ncc.getEmail());
            ps.setBoolean(5, ncc.isHoatDong());
            ps.setString(6, ncc.getMaNhaCungCap());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi cập nhật nhà cung cấp: " + e.getMessage());
        }
        return false;
    }

    /** * 🔹 Tìm nhà cung cấp theo mã (ĐÃ SỬA LỖI)
     */
    public NhaCungCap timNhaCungCapTheoMa(String maNCC) { 
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        // ✅ SỬA 1: Bổ sung Email và HoatDong vào câu SQL
        String sql = "SELECT TenNhaCungCap, SoDienThoai, DiaChi, Email, HoatDong FROM NhaCungCap WHERE MaNhaCungCap = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maNCC);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String ten = rs.getString("TenNhaCungCap");
                    String sdt = rs.getString("SoDienThoai");
                    String diaChi = rs.getString("DiaChi");
                    
                    // ✅ SỬA 2: Đọc Email và HoatDong từ ResultSet
                    String email = rs.getString("Email");
                    boolean hoatDong = rs.getBoolean("HoatDong");

                    // ✅ SỬA 3: Truyền đúng biến 'email' vào constructor
                    NhaCungCap ncc = new NhaCungCap(maNCC, ten, sdt, diaChi, email);
                    
                    // ✅ SỬA 4: Cập nhật trạng thái hoạt động
                    ncc.setHoatDong(hoatDong);
                    
                    return ncc;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
             e.printStackTrace(); // Lỗi từ constructor NhaCungCap (ví dụ nếu mã NCC sai regex)
        }
        return null; // không tìm thấy
    }

    /** 🔹 Sinh mã tự động NCC-yyyyMMdd-xxxx */
    public String taoMaTuDong() {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        
        // ✅ SỬA 5: Sửa logic tạo mã tự động để khớp với định dạng ngày
        // Ví dụ: NCC-20251105-0001
        String today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "NCC-" + today + "-";
        
        // Lấy số lớn nhất TRONG NGÀY HÔM NAY
        String sql = "SELECT MAX(RIGHT(MaNhaCungCap, 4)) AS SoCuoi FROM NhaCungCap WHERE MaNhaCungCap LIKE ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, prefix + "%"); // Tìm các mã trong ngày
            
            try (ResultSet rs = ps.executeQuery()) {
                int so = 1;
                if (rs.next()) {
                    // Dùng getString và parseInt để tránh lỗi null
                    String soCuoi = rs.getString("SoCuoi");
                    if (soCuoi != null) {
                        so = Integer.parseInt(soCuoi) + 1;
                    }
                }
                return prefix + String.format("%04d", so);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi sinh mã nhà cung cấp: " + e.getMessage());
            // Fallback nếu có lỗi
            return "NCC-" + today + "-0001"; 
        }
    }
    public NhaCungCap timNhaCungCapTheoMaHoacSDT(String keyword) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        
        // Tìm kiếm chính xác theo Mã hoặc SĐT
        String sql = "SELECT * FROM NhaCungCap WHERE MaNhaCungCap = ? OR SoDienThoai = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, keyword);
            stmt.setString(2, keyword);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    NhaCungCap ncc = new NhaCungCap(
                            rs.getString("MaNhaCungCap"),
                            rs.getString("TenNhaCungCap"),
                            rs.getString("SoDienThoai"),
                            rs.getString("DiaChi"),
                            rs.getString("Email")
                    );
                    ncc.setHoatDong(rs.getBoolean("HoatDong"));
                    return ncc;
                }
            }
        } catch (Exception e) {
            // Bắt Exception chung (bao gồm cả SQLException và IllegalArgumentException)
            System.err.println("❌ Lỗi timNhaCungCapTheoMaHoacSDT: " + e.getMessage());
        }
        return null; // không tìm thấy
    }
}