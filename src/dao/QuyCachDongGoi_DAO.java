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

        String sql = "SELECT qc.MaQuyCach, qc.HeSoQuyDoi, qc.TiLeGiam, qc.DonViGoc, " +
                     "sp.MaSanPham, sp.TenSanPham, sp.LoaiSanPham, sp.SoDangKy, sp.DuongDung, sp.GiaNhap, sp.HinhAnh, sp.KeBanSanPham, sp.HoatDong, " +
                     "dvt.MaDonViTinh, dvt.TenDonViTinh, dvt.MoTa " +
                     "FROM QuyCachDongGoi qc " +
                     "JOIN SanPham sp ON qc.MaSanPham = sp.MaSanPham " +
                     "JOIN DonViTinh dvt ON qc.MaDonViTinh = dvt.MaDonViTinh";
        
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String maQC = rs.getString("MaQuyCach");
                try {
                    DonViTinh dvt = new DonViTinh(rs.getString("MaDonViTinh"), rs.getString("TenDonViTinh"), rs.getString("MoTa"));

                    // ===== SỬA ĐỔI TẠI ĐÂY: Xử lý Enum trực tiếp bằng valueOf =====
                    
                    // 1. Chuyển đổi LoaiSanPham
                    LoaiSanPham loai = null;
                    String loaiSPStr = rs.getString("LoaiSanPham");
                    if (loaiSPStr != null && !loaiSPStr.trim().isEmpty()) {
                        try {
                            // Sử dụng valueOf(), cần đảm bảo chuỗi khớp chính xác tên hằng số (viết hoa)
                            loai = LoaiSanPham.valueOf(loaiSPStr.trim().toUpperCase());
                        } catch (IllegalArgumentException e) {
                            System.err.println("Giá trị LoaiSanPham không hợp lệ từ CSDL cho MaQuyCach '" + maQC + "': '" + loaiSPStr + "'");
                            // Có thể gán giá trị mặc định hoặc bỏ qua bản ghi này tùy nghiệp vụ
                        }
                    }

                    // 2. Chuyển đổi DuongDung
                    DuongDung dd = null;
                    String duongDungStr = rs.getString("DuongDung");
                    if (duongDungStr != null && !duongDungStr.trim().isEmpty()) {
                        try {
                             // Sử dụng valueOf(), cần đảm bảo chuỗi khớp chính xác tên hằng số (viết hoa)
                            dd = DuongDung.valueOf(duongDungStr.trim().toUpperCase());
                        } catch (IllegalArgumentException e) {
                            System.err.println("Giá trị DuongDung không hợp lệ từ CSDL cho MaQuyCach '" + maQC + "': '" + duongDungStr + "'");
                        }
                    }

                    // =========================================================

                    SanPham sp = new SanPham(
                        rs.getString("MaSanPham"), 
                        rs.getString("TenSanPham"),
                        loai, // Sử dụng biến enum đã được chuyển đổi
                        rs.getString("SoDangKy"),
                        dd,   // Sử dụng biến enum đã được chuyển đổi
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
                    // Bắt lỗi validation từ các hàm khởi tạo của Entity (ví dụ HeSoQuyDoi sai)
                    System.err.println("Lỗi dữ liệu không hợp lệ từ CSDL cho MaQuyCach '" + maQC + "': " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Lỗi kết nối hoặc câu SQL
        }
        return ds;
    }

    /** Tạo mã quy cách mới */
    public String taoMaQuyCach() {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "SELECT TOP 1 MaQuyCach FROM QuyCachDongGoi ORDER BY MaQuyCach DESC";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String lastMa = rs.getString("MaQuyCach");
                // Đảm bảo chuỗi có dạng QCxxxxxx trước khi cắt và parse
                if (lastMa != null && lastMa.matches("^QC\\d{6}$")) {
                     int lastNum = Integer.parseInt(lastMa.substring(2));
                     return String.format("QC%06d", lastNum + 1);
                }
            }
        } catch (SQLException | NumberFormatException e) { // Bắt thêm NumberFormatException
            e.printStackTrace();
        }
        return "QC000001"; // Mã mặc định nếu có lỗi hoặc chưa có bản ghi nào
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
            e.printStackTrace(); // Log lỗi SQL
            // Có thể kiểm tra mã lỗi SQL để biết chi tiết (vd: vi phạm khóa ngoại, unique constraint)
            // if (e.getErrorCode() == ...) { ... }
        }
        return false;
    }

    /** Cập nhật quy cách (theo khóa chính MaQuyCach) */
    public boolean capNhatQuyCachDongGoi(QuyCachDongGoi q) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        // Cập nhật cả MaSanPham và MaDonViTinh để phòng trường hợp thay đổi
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