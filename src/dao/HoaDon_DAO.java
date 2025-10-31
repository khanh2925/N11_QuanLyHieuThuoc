package dao;

import connectDB.connectDB;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.NhanVien;
import entity.KhuyenMai;
import entity.LoSanPham;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_DAO {

    private final NhanVien_DAO nhanVienDAO;
    private final ChiTietHoaDon_DAO chiTietHoaDonDAO;

    public HoaDon_DAO() {
        this.nhanVienDAO = new NhanVien_DAO();
        this.chiTietHoaDonDAO = new ChiTietHoaDon_DAO();
    }

    /** 🔍 Tìm hóa đơn theo mã (load đầy đủ chi tiết, nhân viên) */
    public HoaDon timHoaDonTheoMa(String maHD) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connectDB.getInstance();
            con = connectDB.getConnection();

            String sql = "SELECT * FROM HoaDon WHERE MaHoaDon = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maHD);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String maNV = rs.getString("MaNhanVien");
                String maKH = rs.getString("MaKhachHang");
                LocalDate ngayLap = rs.getDate("NgayLap").toLocalDate();
                double tongTien = rs.getDouble("TongTien");
                boolean thuocTheoDon = rs.getBoolean("ThuocTheoDon");

                // Lấy nhân viên
                NhanVien nv = nhanVienDAO.getNhanVienTheoMa(maNV);

                // Tạo hóa đơn
                HoaDon hd = new HoaDon(maHD, maKH, ngayLap, nv, null, thuocTheoDon);

                // 💡 Gán tổng tiền đọc từ DB (bằng setter package-private)
                try {
                    var setTongTien = HoaDon.class.getDeclaredMethod("setTongTien", double.class);
                    setTongTien.setAccessible(true);
                    setTongTien.invoke(hd, tongTien);
                } catch (Exception ex) {
                    System.err.println("⚠ Không thể gán tongTien: " + ex.getMessage());
                }

                // 🔹 Load danh sách chi tiết hóa đơn (đã có MaLo)
                List<ChiTietHoaDon> dsCT = chiTietHoaDonDAO.layDanhSachChiTietTheoMaHD(maHD);
                hd.setChiTietHoaDonList(dsCT);

                return hd;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException ignore) {}
        }
        return null;
    }

    /** 📜 Lấy toàn bộ hóa đơn (gọi lại hàm trên để nạp chi tiết đầy đủ) */
    public List<HoaDon> layTatCaHoaDon() {
        List<HoaDon> dsHD = new ArrayList<>();
        try (Connection con = connectDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT MaHoaDon FROM HoaDon ORDER BY NgayLap DESC")) {

            while (rs.next()) {
                HoaDon hd = timHoaDonTheoMa(rs.getString("MaHoaDon"));
                if (hd != null) dsHD.add(hd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsHD;
    }

    /** ➕ Thêm hóa đơn mới (đúng thứ tự cột theo script SQL) */
    public boolean themHoaDon(HoaDon hd) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmtHD = null;
        PreparedStatement stmtCTHD = null;

        try {
            con.setAutoCommit(false); // Bắt đầu transaction

            // 🔹 1️⃣ Tính tổng tiền từ chi tiết (vì getTongTien() là dẫn suất)
            double tongTien = hd.getTongTien();

            // 🔹 2️⃣ Thêm hóa đơn — đúng thứ tự như script
            String sqlHD = "INSERT INTO HoaDon (MaHoaDon, NgayLap, MaNhanVien, MaKhachHang, TongTien, ThuocTheoDon) " +
                           "VALUES (?, ?, ?, ?, ?, ?)";
            stmtHD = con.prepareStatement(sqlHD);
            stmtHD.setString(1, hd.getMaHoaDon());
            stmtHD.setDate(2, Date.valueOf(hd.getNgayLap()));
            stmtHD.setString(3, hd.getNhanVien().getMaNhanVien());
            stmtHD.setString(4, hd.getMaKhachHang());
            stmtHD.setDouble(5, tongTien);
            stmtHD.setBoolean(6, hd.isThuocTheoDon());
            stmtHD.executeUpdate();

            // 🔹 3️⃣ Thêm chi tiết hóa đơn
            String sqlCT = "INSERT INTO ChiTietHoaDon (MaHoaDon, MaLo, MaKM, SoLuong, GiaBan) VALUES (?, ?, ?, ?, ?)";
            stmtCTHD = con.prepareStatement(sqlCT);

            for (ChiTietHoaDon cthd : hd.getChiTietHoaDonList()) {
                stmtCTHD.setString(1, hd.getMaHoaDon());
                stmtCTHD.setString(2, cthd.getLoSanPham().getMaLo());

                KhuyenMai km = cthd.getKhuyenMai();
                if (km != null) stmtCTHD.setString(3, km.getMaKM());
                else stmtCTHD.setNull(3, Types.VARCHAR);

                stmtCTHD.setDouble(4, cthd.getSoLuong());
                stmtCTHD.setDouble(5, cthd.getGiaBan());
                stmtCTHD.addBatch();
            }
            stmtCTHD.executeBatch();

            con.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback();
            } catch (SQLException ignore) {}
            return false;
        } finally {
            try {
                if (stmtHD != null) stmtHD.close();
                if (stmtCTHD != null) stmtCTHD.close();
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException ignore) {}
        }
    }

    /** 🧾 Tạo mã hóa đơn tự động theo ngày */
    public String taoMaHoaDon() {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String prefix = "HD-" + today + "-";
            String sql = "SELECT COUNT(*) FROM HoaDon WHERE MaHoaDon LIKE ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, prefix + "%");
            rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return String.format("%s%04d", prefix, count + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException ignore) {}
        }

        // Nếu lỗi, trả mã cơ bản
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "HD-" + today + "-0001";
    }
}
