package dao;

import connectDB.connectDB;
import entity.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_DAO {

    private final NhanVien_DAO nhanVienDAO;
    private final KhachHang_DAO khachHangDAO;
    private final ChiTietHoaDon_DAO chiTietHoaDonDAO;
    private final QuyCachDongGoi_DAO quyCachDongGoiDAO;
    private final KhuyenMai_DAO khuyenMaiDAO;

    public HoaDon_DAO() {
        this.nhanVienDAO = new NhanVien_DAO();
        this.khachHangDAO = new KhachHang_DAO();
        this.chiTietHoaDonDAO = new ChiTietHoaDon_DAO();
        this.quyCachDongGoiDAO = new QuyCachDongGoi_DAO();
        this.khuyenMaiDAO = new KhuyenMai_DAO();
    }

    /** 🔍 Tìm hóa đơn theo mã (load đầy đủ chi tiết, nhân viên, khách hàng, KM) */
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
                boolean thuocKeDon = rs.getBoolean("ThuocKeDon");
                String maKM = rs.getString("MaKM");

                // Lấy nhân viên & khách hàng
                NhanVien nhanVien = nhanVienDAO.timNhanVienTheoMa(maNV);
                KhachHang khachHang = khachHangDAO.timKhachHangTheoMa(maKH);

                // KM hóa đơn (nếu có)
                KhuyenMai kmHoaDon = null;
                if (maKM != null && !maKM.isBlank()) {
                    kmHoaDon = khuyenMaiDAO.timKhuyenMaiTheoMa(maKM);
                }

                // 🔹 Load danh sách chi tiết hóa đơn
                List<ChiTietHoaDon> dsCT = chiTietHoaDonDAO.layDanhSachChiTietTheoMaHD(maHD);

                // ✅ Tạo hóa đơn đầy đủ
                HoaDon hd = new HoaDon(maHD, nhanVien, khachHang, ngayLap, kmHoaDon, dsCT, thuocKeDon);

                // Nếu muốn dùng đúng số tiền đã lưu DB (phòng khi rule KM thay đổi):
                double tongThanhToan = rs.getDouble("TongThanhToan");
                double soTienGiamKM = rs.getDouble("SoTienGiamKhuyenMai");
                hd.capNhatDuLieuHoaDon();

                return hd;
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tìm hóa đơn theo mã: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
            } catch (SQLException ignore) {
            }
        }
        return null;
    }

    /** 📜 Lấy toàn bộ hóa đơn */
    public List<HoaDon> layTatCaHoaDon() {
        List<HoaDon> dsHD = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection(); // 👈 KHÔNG đưa vào try-with-resources

        Statement st = null;
        ResultSet rs = null;

        try {
            st = con.createStatement();
            rs = st.executeQuery("SELECT MaHoaDon FROM HoaDon ORDER BY NgayLap DESC");

            while (rs.next()) {
                String maHD = rs.getString("MaHoaDon");
                HoaDon hd = timHoaDonTheoMa(maHD);
                if (hd != null)
                    dsHD.add(hd);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy danh sách hóa đơn: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (st != null)
                    st.close();
                // ❌ KHÔNG được con.close();
            } catch (SQLException ignore) {
            }
        }

        return dsHD;
    }

    /** ➕ Thêm hóa đơn mới */
    public boolean themHoaDon(HoaDon hd) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmtHD = null;
        PreparedStatement stmtCTHD = null;
        PreparedStatement stmtUpdateTon = null;

        try {
            con.setAutoCommit(false); // bắt đầu transaction

            // 1. Tính lại tổng tiền + KM hóa đơn
            hd.capNhatDuLieuHoaDon(); // sẽ gọi luôn capNhatTongThanhToan bên trong

            double tongThanhToan = hd.getTongThanhToan();
            double soTienGiamKM = hd.getSoTienGiamKhuyenMai();
            KhuyenMai kmHD = hd.getKhuyenMai();

            // 2. Thêm hóa đơn (KHÔNG còn cột TongTien, DiemSuDung)
            String sqlHD = """
                    INSERT INTO HoaDon (MaHoaDon, NgayLap, MaNhanVien, MaKhachHang,
                                        TongThanhToan, MaKM, SoTienGiamKhuyenMai, ThuocKeDon)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """;
            stmtHD = con.prepareStatement(sqlHD);
            stmtHD.setString(1, hd.getMaHoaDon());
            stmtHD.setDate(2, Date.valueOf(hd.getNgayLap()));
            stmtHD.setString(3, hd.getNhanVien().getMaNhanVien());
            stmtHD.setString(4, hd.getKhachHang().getMaKhachHang());
            stmtHD.setDouble(5, tongThanhToan);

            if (kmHD != null) {
                stmtHD.setString(6, kmHD.getMaKM());
            } else {
                stmtHD.setNull(6, Types.CHAR);
            }

            stmtHD.setDouble(7, soTienGiamKM);
            stmtHD.setBoolean(8, hd.isThuocKeDon());
            stmtHD.executeUpdate();

            // 3. Thêm chi tiết hóa đơn
            String sqlCT = """
                    INSERT INTO ChiTietHoaDon (MaHoaDon, MaLo, MaDonViTinh, SoLuong, GiaBan, ThanhTien, MaKM)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """;
            stmtCTHD = con.prepareStatement(sqlCT);

            // 4. Chuẩn bị lệnh update tồn kho (SoLuongTon đang là đơn vị gốc)
            String sqlUpdateTon = """
                    UPDATE LoSanPham
                    SET SoLuongTon = SoLuongTon - ?
                    WHERE MaLo = ? AND SoLuongTon >= ?
                    """;
            stmtUpdateTon = con.prepareStatement(sqlUpdateTon);

            for (ChiTietHoaDon cthd : hd.getDanhSachChiTiet()) {
                double soLuongBan = cthd.getSoLuong();   // theo đơn vị bán
                double giaBanRow  = cthd.getGiaBan();    // GIÁ GỐC / 1 đơn vị
                double thanhTienRow = cthd.getThanhTien(); // ✅ ĐÃ GIẢM KM SẢN PHẨM

                stmtCTHD.setString(1, hd.getMaHoaDon());
                stmtCTHD.setString(2, cthd.getLoSanPham().getMaLo());
                stmtCTHD.setString(3, cthd.getDonViTinh().getMaDonViTinh());
                stmtCTHD.setDouble(4, soLuongBan);
                stmtCTHD.setDouble(5, giaBanRow);
                stmtCTHD.setDouble(6, thanhTienRow);

                KhuyenMai km = cthd.getKhuyenMai();
                if (km != null) {
                    stmtCTHD.setString(7, km.getMaKM());
                } else {
                    stmtCTHD.setNull(7, Types.CHAR);
                }

                stmtCTHD.addBatch();

                // ==== TÍNH SỐ LƯỢNG BASE ĐỂ TRỪ TỒN ====
                String maLo = cthd.getLoSanPham().getMaLo();
                String maSP = cthd.getLoSanPham().getSanPham().getMaSanPham();
                String maDVT = cthd.getDonViTinh().getMaDonViTinh();

                // Lấy quy cách để biết hệ số quy đổi
                QuyCachDongGoi qc = quyCachDongGoiDAO.timQuyCachTheoSanPhamVaDonVi(maSP, maDVT);
                if (qc == null) {
                    throw new SQLException("Không tìm thấy quy cách đóng gói cho SP=" + maSP + ", DVT=" + maDVT);
                }

                int heSo = qc.getHeSoQuyDoi(); // ví dụ: 1 hộp = 100 viên => heSo = 100
                double soLBan = cthd.getSoLuong(); // bán bao nhiêu hộp/vỉ/...
                double soLuongBanBase = soLBan * heSo;

                // ==== TRỪ TỒN KHO ====
                stmtUpdateTon.setDouble(1, soLuongBanBase);
                stmtUpdateTon.setString(2, maLo);
                stmtUpdateTon.setDouble(3, soLuongBanBase);

                int affected = stmtUpdateTon.executeUpdate();
                if (affected == 0) {
                    // Không đủ hàng hoặc MaLo không hợp lệ -> rollback toàn bộ
                    throw new SQLException(
                            "Tồn kho không đủ cho lô " + maLo + " (cần " + soLuongBanBase + " đơn vị gốc)");
                }
            }

            stmtCTHD.executeBatch();

            con.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi thêm hóa đơn: " + e.getMessage());
            try {
                if (con != null)
                    con.rollback();
            } catch (SQLException ignore) {
            }
            return false;
        } finally {
            try {
                if (stmtHD != null)
                    stmtHD.close();
                if (stmtCTHD != null)
                    stmtCTHD.close();
                if (stmtUpdateTon != null)
                    stmtUpdateTon.close();
                if (con != null)
                    con.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
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
            System.err.println("❌ Lỗi tạo mã hóa đơn: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
            } catch (SQLException ignore) {
            }
        }

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "HD-" + today + "-0001";
    }

    /**
     * 🔍 Tìm danh sách hóa đơn theo số điện thoại khách hàng
     * (Dùng cho dialog chọn hóa đơn, chỉ load danh sách rồi gọi timHoaDonTheoMa)
     */
    public List<HoaDon> timHoaDonTheoSoDienThoai(String soDienThoai) {
        List<HoaDon> dsHD = new ArrayList<>();

        String sql = """
                SELECT hd.MaHoaDon, hd.NgayLap
                FROM HoaDon hd
                JOIN KhachHang kh ON hd.MaKhachHang = kh.MaKhachHang
                JOIN NhanVien nv ON hd.MaNhanVien = nv.MaNhanVien
                WHERE kh.SoDienThoai = ?
                ORDER BY hd.NgayLap DESC
                """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, soDienThoai);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    String maHD = rs.getString("MaHoaDon");
                    HoaDon hd = timHoaDonTheoMa(maHD);
                    if (hd != null)
                        dsHD.add(hd);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi tìm hóa đơn theo SĐT: " + e.getMessage());
            e.printStackTrace();
        }

        return dsHD;
    }
}
