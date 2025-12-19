package dao;

import database.connectDB;
import entity.*;
import enums.HinhThucKM;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_DAO {
    // ============ CACHE LAYER ============
    // Cache toàn bộ hóa đơn (dùng chung toàn ứng dụng)
    private static List<HoaDon> cacheAllHoaDon = null;

    public HoaDon_DAO() {
    }

    // ============================================================
    // 🔍 Tìm hóa đơn theo mã (OPTIMIZED - dùng JOIN)
    // ============================================================
    public HoaDon timHoaDonTheoMa(String maHD) {
        String sql = """
                SELECT
                	hd.MaHoaDon, hd.NgayLap, hd.TongThanhToan, hd.SoTienGiamKhuyenMai, hd.ThuocKeDon,
                	nv.MaNhanVien, nv.TenNhanVien, nv.QuanLy, nv.CaLam,
                	kh.MaKhachHang, kh.TenKhachHang, kh.GioiTinh, kh.SoDienThoai, kh.NgaySinh, kh.HoatDong,
                    km.MaKM, km.TenKM, km.GiaTri AS GiaTriKM, km.HinhThuc AS HinhThucKM,
                    km.KhuyenMaiHoaDon, km.DieuKienApDungHoaDon, km.SoLuongKhuyenMai,
                    km.NgayBatDau, km.NgayKetThuc, km.TrangThai AS TrangThaiKM
                FROM HoaDon hd
                JOIN NhanVien nv ON hd.MaNhanVien = nv.MaNhanVien
                JOIN KhachHang kh ON hd.MaKhachHang = kh.MaKhachHang
                LEFT JOIN KhuyenMai km ON hd.MaKM = km.MaKM
                WHERE hd.MaHoaDon = ?
                """;

        Connection con = connectDB.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, maHD);
            rs = ps.executeQuery();

            if (rs.next()) {
                // ========== TẠO NHANVIEN ==========
                NhanVien nv = new NhanVien();
                nv.setMaNhanVien(rs.getString("MaNhanVien"));
                nv.setTenNhanVien(rs.getString("TenNhanVien"));
                nv.setQuanLy(rs.getBoolean("QuanLy"));
                nv.setCaLam(rs.getInt("CaLam"));

                // ========== TẠO KHACHHANG ==========
                KhachHang kh = new KhachHang();
                kh.setMaKhachHang(rs.getString("MaKhachHang"));
                kh.setTenKhachHang(rs.getString("TenKhachHang"));
                kh.setGioiTinh(rs.getBoolean("GioiTinh"));
                kh.setSoDienThoai(rs.getString("SoDienThoai"));
                if (rs.getDate("NgaySinh") != null) {
                    kh.setNgaySinh(rs.getDate("NgaySinh").toLocalDate());
                }
                kh.setHoatDong(rs.getBoolean("HoatDong"));

                // ========== TẠO KHUYENMAI (nếu có) ==========
                KhuyenMai km = null;
                if (rs.getString("MaKM") != null) {
                    km = new KhuyenMai();
                    km.setMaKM(rs.getString("MaKM"));
                    km.setTenKM(rs.getString("TenKM"));
                    km.setGiaTri(rs.getDouble("GiaTriKM"));
                    km.setKhuyenMaiHoaDon(rs.getBoolean("KhuyenMaiHoaDon"));
                    km.setDieuKienApDungHoaDon(rs.getDouble("DieuKienApDungHoaDon"));
                    km.setSoLuongKhuyenMai(rs.getInt("SoLuongKhuyenMai"));
                    if (rs.getString("HinhThucKM") != null) {
                        km.setHinhThuc(HinhThucKM.valueOf(rs.getString("HinhThucKM")));
                    }
                    if (rs.getDate("NgayBatDau") != null) {
                        km.setNgayBatDau(rs.getDate("NgayBatDau").toLocalDate());
                    }
                    if (rs.getDate("NgayKetThuc") != null) {
                        km.setNgayKetThuc(rs.getDate("NgayKetThuc").toLocalDate());
                    }
                    km.setTrangThai(rs.getBoolean("TrangThaiKM"));
                }

                // ========== TẠO HOADON ==========
                LocalDate ngayLap = rs.getDate("NgayLap").toLocalDate();
                double tongTien = rs.getDouble("TongThanhToan");
                boolean thuocKeDon = rs.getBoolean("ThuocKeDon");

                HoaDon hd = new HoaDon();
                hd.setMaHoaDon(maHD);
                hd.setNhanVien(nv);
                hd.setKhachHang(kh);
                hd.setNgayLap(ngayLap);
                hd.setKhuyenMai(km);
                hd.setThuocKeDon(thuocKeDon);

                // Set tongTien bằng reflection
                try {
                    var setTongTien = HoaDon.class.getDeclaredField("tongTien");
                    setTongTien.setAccessible(true);
                    setTongTien.set(hd, tongTien);
                } catch (Exception ignore) {
                }

                // Đóng rs, ps trước khi gọi layChiTietHoaDon
                rs.close();
                ps.close();

                // ========== LẤY CHI TIẾT HÓA ĐƠN ==========
                List<ChiTietHoaDon> dsCT = layChiTietHoaDon(maHD);
                hd.setDanhSachChiTiet(dsCT);

                return hd;
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tìm hóa đơn theo mã: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ignored) {
            }
            try {
                if (ps != null)
                    ps.close();
            } catch (Exception ignored) {
            }
            // ❗ KHÔNG đóng connection (singleton)
        }
        return null;
    }

    // ============================================================
    // 📜 Lấy chi tiết hóa đơn (OPTIMIZED - dùng JOIN)
    // ============================================================
    private List<ChiTietHoaDon> layChiTietHoaDon(String maHD) {
        List<ChiTietHoaDon> ds = new ArrayList<>();

        String sql = """
                SELECT
                	ct.MaLo, ct.SoLuong, ct.GiaBan, ct.ThanhTien,
                	ct.MaDonViTinh, dvt.TenDonViTinh,
                	lo.HanSuDung, lo.SoLuongTon,
                	sp.MaSanPham, sp.TenSanPham, sp.GiaNhap,
                	km.MaKM, km.TenKM, km.GiaTri, km.HinhThuc
                FROM ChiTietHoaDon ct
                LEFT JOIN DonViTinh dvt ON ct.MaDonViTinh = dvt.MaDonViTinh
                LEFT JOIN LoSanPham lo ON ct.MaLo = lo.MaLo
                LEFT JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
                LEFT JOIN KhuyenMai km ON ct.MaKM = km.MaKM
                WHERE ct.MaHoaDon = ?
                ORDER BY ct.MaLo
                """;

        Connection con = connectDB.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, maHD);
            rs = ps.executeQuery();

            HoaDon hd = new HoaDon();
            hd.setMaHoaDon(maHD);

            while (rs.next()) {
                // ========== TẠO SẢN PHẨM ==========
                SanPham sp = null;
                if (rs.getString("MaSanPham") != null) {
                    sp = new SanPham();
                    sp.setMaSanPham(rs.getString("MaSanPham"));
                    sp.setTenSanPham(rs.getString("TenSanPham"));
                    sp.setGiaNhap(rs.getDouble("GiaNhap"));
                }

                // ========== TẠO LÔ SẢN PHẨM ==========
                LoSanPham lo = new LoSanPham();
                lo.setMaLo(rs.getString("MaLo"));
                if (rs.getDate("HanSuDung") != null) {
                    lo.setHanSuDung(rs.getDate("HanSuDung").toLocalDate());
                }
                lo.setSoLuongTon(rs.getInt("SoLuongTon"));
                lo.setSanPham(sp);

                // ========== TẠO ĐƠN VỊ TÍNH ==========
                DonViTinh dvt = null;
                if (rs.getString("MaDonViTinh") != null) {
                    dvt = new DonViTinh();
                    dvt.setMaDonViTinh(rs.getString("MaDonViTinh"));
                    dvt.setTenDonViTinh(rs.getString("TenDonViTinh"));
                }

                // ========== TẠO KHUYẾN MÃI (nếu có) ==========
                KhuyenMai km = null;
                if (rs.getString("MaKM") != null) {
                    km = new KhuyenMai();
                    km.setMaKM(rs.getString("MaKM"));
                    km.setTenKM(rs.getString("TenKM"));
                    km.setGiaTri(rs.getDouble("GiaTri"));
                    if (rs.getString("HinhThuc") != null) {
                        km.setHinhThuc(HinhThucKM.valueOf(rs.getString("HinhThuc")));
                    }
                }

                // ========== TẠO CHI TIẾT HÓA ĐƠN ==========
                ChiTietHoaDon cthd = new ChiTietHoaDon(hd, lo, rs.getDouble("SoLuong"), dvt, rs.getDouble("GiaBan"),
                        km);
                ds.add(cthd);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ignored) {
            }
            try {
                if (ps != null)
                    ps.close();
            } catch (Exception ignored) {
            }
            // ❗ KHÔNG đóng connection (singleton)
        }

        return ds;
    }

    // ============================================================
    // 📜 Lấy tất cả hóa đơn (OPTIMIZED - dùng JOIN, CÓ CACHE)
    // ============================================================
    public List<HoaDon> layTatCaHoaDon() {
        // Nếu cache đã có dữ liệu → Return cache (clone để tránh modify trực tiếp)
        if (cacheAllHoaDon != null && !cacheAllHoaDon.isEmpty()) {
            return new ArrayList<>(cacheAllHoaDon);
        }

        // Cache rỗng → Query DB và lưu vào cache
        List<HoaDon> dsHD = new ArrayList<>();

        String sql = """
                SELECT
                	hd.MaHoaDon, hd.NgayLap, hd.TongThanhToan, hd.SoTienGiamKhuyenMai, hd.ThuocKeDon,
                	nv.MaNhanVien, nv.TenNhanVien, nv.QuanLy, nv.CaLam,
                	kh.MaKhachHang, kh.TenKhachHang, kh.GioiTinh, kh.SoDienThoai, kh.NgaySinh, kh.HoatDong,
                    km.MaKM, km.TenKM, km.GiaTri AS GiaTriKM, km.HinhThuc AS HinhThucKM,
                    km.KhuyenMaiHoaDon, km.DieuKienApDungHoaDon, km.SoLuongKhuyenMai,
                    km.NgayBatDau, km.NgayKetThuc, km.TrangThai AS TrangThaiKM
                FROM HoaDon hd
                JOIN NhanVien nv ON hd.MaNhanVien = nv.MaNhanVien
                JOIN KhachHang kh ON hd.MaKhachHang = kh.MaKhachHang
                LEFT JOIN KhuyenMai km ON hd.MaKM = km.MaKM
                ORDER BY hd.NgayLap DESC, hd.MaHoaDon DESC
                """;

        Connection con = connectDB.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        // Tạm lưu danh sách hóa đơn (chưa có chi tiết)
        List<HoaDonTemp> tempList = new ArrayList<>();

        try {
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                // ========== TẠO NHANVIEN ==========
                NhanVien nv = new NhanVien();
                nv.setMaNhanVien(rs.getString("MaNhanVien"));
                nv.setTenNhanVien(rs.getString("TenNhanVien"));
                nv.setQuanLy(rs.getBoolean("QuanLy"));
                nv.setCaLam(rs.getInt("CaLam"));

                // ========== TẠO KHACHHANG ==========
                KhachHang kh = new KhachHang();
                kh.setMaKhachHang(rs.getString("MaKhachHang"));
                kh.setTenKhachHang(rs.getString("TenKhachHang"));
                kh.setGioiTinh(rs.getBoolean("GioiTinh"));
                kh.setSoDienThoai(rs.getString("SoDienThoai"));
                if (rs.getDate("NgaySinh") != null) {
                    kh.setNgaySinh(rs.getDate("NgaySinh").toLocalDate());
                }
                kh.setHoatDong(rs.getBoolean("HoatDong"));

                // ========== TẠO KHUYENMAI (nếu có) ==========
                KhuyenMai km = null;
                if (rs.getString("MaKM") != null) {
                    km = new KhuyenMai();
                    km.setMaKM(rs.getString("MaKM"));
                    km.setTenKM(rs.getString("TenKM"));
                    km.setGiaTri(rs.getDouble("GiaTriKM"));
                    km.setKhuyenMaiHoaDon(rs.getBoolean("KhuyenMaiHoaDon"));
                    km.setDieuKienApDungHoaDon(rs.getDouble("DieuKienApDungHoaDon"));
                    km.setSoLuongKhuyenMai(rs.getInt("SoLuongKhuyenMai"));
                    if (rs.getString("HinhThucKM") != null) {
                        km.setHinhThuc(HinhThucKM.valueOf(rs.getString("HinhThucKM")));
                    }
                    if (rs.getDate("NgayBatDau") != null) {
                        km.setNgayBatDau(rs.getDate("NgayBatDau").toLocalDate());
                    }
                    if (rs.getDate("NgayKetThuc") != null) {
                        km.setNgayKetThuc(rs.getDate("NgayKetThuc").toLocalDate());
                    }
                    km.setTrangThai(rs.getBoolean("TrangThaiKM"));
                }

                // ========== LƯU TẠM ==========
                HoaDonTemp temp = new HoaDonTemp();
                temp.maHD = rs.getString("MaHoaDon");
                temp.ngayLap = rs.getDate("NgayLap").toLocalDate();
                temp.tongTien = rs.getDouble("TongThanhToan");
                temp.thuocKeDon = rs.getBoolean("ThuocKeDon");
                temp.nv = nv;
                temp.kh = kh;
                temp.km = km;
                tempList.add(temp);
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy danh sách hóa đơn: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ignored) {
            }
            try {
                if (ps != null)
                    ps.close();
            } catch (Exception ignored) {
            }
            // ❗ KHÔNG đóng connection (singleton)
        }

        // Sau khi đóng ResultSet, lấy chi tiết cho từng hóa đơn
        for (HoaDonTemp temp : tempList) {
            List<ChiTietHoaDon> dsCT = layChiTietHoaDon(temp.maHD);

            HoaDon hd = new HoaDon();
            hd.setMaHoaDon(temp.maHD);
            hd.setNhanVien(temp.nv);
            hd.setKhachHang(temp.kh);
            hd.setNgayLap(temp.ngayLap);
            hd.setKhuyenMai(temp.km);
            hd.setThuocKeDon(temp.thuocKeDon);
            hd.setDanhSachChiTiet(dsCT);

            // Set tongTien bằng reflection
            try {
                var setTongTien = HoaDon.class.getDeclaredField("tongTien");
                setTongTien.setAccessible(true);
                setTongTien.set(hd, temp.tongTien);
            } catch (Exception ignore) {
            }

            dsHD.add(hd);
        }

        // Lưu vào cache
        cacheAllHoaDon = dsHD;

        return new ArrayList<>(dsHD); // Clone để tránh modify cache
    }

    // Class tạm để lưu thông tin hóa đơn
    private static class HoaDonTemp {
        String maHD;
        LocalDate ngayLap;
        double tongTien;
        boolean thuocKeDon;
        NhanVien nv;
        KhachHang kh;
        KhuyenMai km;
    }

    // ============================================================
    // ➕ Thêm hóa đơn
    // ============================================================
    public boolean themHoaDon(HoaDon hd) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmtHD = null;
        PreparedStatement stmtCTHD = null;
        PreparedStatement stmtUpdateTon = null;
        PreparedStatement stmtQuyDoi = null;

        try {
            con.setAutoCommit(false);
            hd.capNhatDuLieuHoaDon();

            double tongThanhToan = hd.getTongThanhToan();
            double soTienGiamKM = hd.getSoTienGiamKhuyenMai();
            KhuyenMai kmHD = hd.getKhuyenMai();

            String sqlHD = "INSERT INTO HoaDon (MaHoaDon, NgayLap, MaNhanVien, MaKhachHang, TongThanhToan, MaKM, SoTienGiamKhuyenMai, ThuocKeDon) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            stmtHD = con.prepareStatement(sqlHD);
            stmtHD.setString(1, hd.getMaHoaDon());
            stmtHD.setDate(2, Date.valueOf(hd.getNgayLap()));
            stmtHD.setString(3, hd.getNhanVien().getMaNhanVien());
            stmtHD.setString(4, hd.getKhachHang().getMaKhachHang());
            stmtHD.setDouble(5, tongThanhToan);
            if (kmHD != null)
                stmtHD.setString(6, kmHD.getMaKM());
            else
                stmtHD.setNull(6, Types.CHAR);
            stmtHD.setDouble(7, soTienGiamKM);
            stmtHD.setBoolean(8, hd.isThuocKeDon());
            stmtHD.executeUpdate();

            String sqlCT = "INSERT INTO ChiTietHoaDon (MaHoaDon, MaLo, MaDonViTinh, SoLuong, GiaBan, ThanhTien, MaKM) VALUES (?, ?, ?, ?, ?, ?, ?)";
            stmtCTHD = con.prepareStatement(sqlCT);

            String sqlUpdateTon = "UPDATE LoSanPham SET SoLuongTon = SoLuongTon - ? WHERE MaLo = ? AND SoLuongTon >= ?";
            stmtUpdateTon = con.prepareStatement(sqlUpdateTon);

            // Query để lấy hệ số quy đổi
            String sqlQuyDoi = "SELECT HeSoQuyDoi FROM QuyCachDongGoi WHERE MaSanPham = ? AND MaDonViTinh = ?";
            stmtQuyDoi = con.prepareStatement(sqlQuyDoi);

            for (ChiTietHoaDon cthd : hd.getDanhSachChiTiet()) {
                stmtCTHD.setString(1, hd.getMaHoaDon());
                stmtCTHD.setString(2, cthd.getLoSanPham().getMaLo());
                stmtCTHD.setString(3, cthd.getDonViTinh().getMaDonViTinh());
                stmtCTHD.setDouble(4, cthd.getSoLuong());
                stmtCTHD.setDouble(5, cthd.getGiaBan());
                stmtCTHD.setDouble(6, cthd.getThanhTien());
                if (cthd.getKhuyenMai() != null)
                    stmtCTHD.setString(7, cthd.getKhuyenMai().getMaKM());
                else
                    stmtCTHD.setNull(7, Types.CHAR);
                stmtCTHD.addBatch();

                // Lấy hệ số quy đổi trực tiếp từ DB
                stmtQuyDoi.setString(1, cthd.getLoSanPham().getSanPham().getMaSanPham());
                stmtQuyDoi.setString(2, cthd.getDonViTinh().getMaDonViTinh());
                ResultSet rsQD = stmtQuyDoi.executeQuery();
                double heSoQuyDoi = 1;
                if (rsQD.next()) {
                    heSoQuyDoi = rsQD.getDouble("HeSoQuyDoi");
                }
                rsQD.close();

                double soLuongBanBase = cthd.getSoLuong() * heSoQuyDoi;

                stmtUpdateTon.setDouble(1, soLuongBanBase);
                stmtUpdateTon.setString(2, cthd.getLoSanPham().getMaLo());
                stmtUpdateTon.setDouble(3, soLuongBanBase);
                if (stmtUpdateTon.executeUpdate() == 0)
                    throw new SQLException("Tồn kho không đủ");
            }
            stmtCTHD.executeBatch();
            con.commit();

            // ✅ Cập nhật cache: Thêm hóa đơn mới vào đầu danh sách
            if (cacheAllHoaDon != null) {
                cacheAllHoaDon.add(0, hd); // Thêm vào đầu (mới nhất)
            }

            return true;
        } catch (SQLException e) {
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
                if (stmtQuyDoi != null)
                    stmtQuyDoi.close();
                con.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
        }
    }

    // ============================================================
    // 🧾 Tạo mã hóa đơn
    // ============================================================
    public String taoMaHoaDon() {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.prepareStatement("SELECT COUNT(*) FROM HoaDon WHERE MaHoaDon LIKE ?");
            String prefix = "HD-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
            stmt.setString(1, prefix + "%");
            rs = stmt.executeQuery();
            if (rs.next())
                return String.format("%s%04d", prefix, rs.getInt(1) + 1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ignored) {
            }
            try {
                if (stmt != null)
                    stmt.close();
            } catch (Exception ignored) {
            }
        }
        return "HD-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-0001";
    }

    // ============================================================
    // 🔍 Tìm hóa đơn theo SĐT (OPTIMIZED - dùng JOIN)
    // ============================================================
    public List<HoaDon> timHoaDonTheoSoDienThoai(String soDienThoai) {
        List<HoaDon> dsHD = new ArrayList<>();

        String sql = """
                SELECT
                	hd.MaHoaDon, hd.NgayLap, hd.TongThanhToan, hd.SoTienGiamKhuyenMai, hd.ThuocKeDon,
                	nv.MaNhanVien, nv.TenNhanVien, nv.QuanLy, nv.CaLam,
                	kh.MaKhachHang, kh.TenKhachHang, kh.GioiTinh, kh.SoDienThoai, kh.NgaySinh, kh.HoatDong,
                    km.MaKM, km.TenKM, km.GiaTri AS GiaTriKM, km.HinhThuc AS HinhThucKM,
                    km.KhuyenMaiHoaDon, km.DieuKienApDungHoaDon, km.SoLuongKhuyenMai,
                    km.NgayBatDau, km.NgayKetThuc, km.TrangThai AS TrangThaiKM
                FROM HoaDon hd
                JOIN NhanVien nv ON hd.MaNhanVien = nv.MaNhanVien
                JOIN KhachHang kh ON hd.MaKhachHang = kh.MaKhachHang
                LEFT JOIN KhuyenMai km ON hd.MaKM = km.MaKM
                WHERE kh.SoDienThoai = ?
                ORDER BY hd.NgayLap DESC
                """;

        Connection con = connectDB.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        // Tạm lưu danh sách hóa đơn (chưa có chi tiết)
        List<HoaDonTemp> tempList = new ArrayList<>();

        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, soDienThoai);
            rs = ps.executeQuery();

            while (rs.next()) {
                // ========== TẠO NHANVIEN ==========
                NhanVien nv = new NhanVien();
                nv.setMaNhanVien(rs.getString("MaNhanVien"));
                nv.setTenNhanVien(rs.getString("TenNhanVien"));
                nv.setQuanLy(rs.getBoolean("QuanLy"));
                nv.setCaLam(rs.getInt("CaLam"));

                // ========== TẠO KHACHHANG ==========
                KhachHang kh = new KhachHang();
                kh.setMaKhachHang(rs.getString("MaKhachHang"));
                kh.setTenKhachHang(rs.getString("TenKhachHang"));
                kh.setGioiTinh(rs.getBoolean("GioiTinh"));
                kh.setSoDienThoai(rs.getString("SoDienThoai"));
                if (rs.getDate("NgaySinh") != null) {
                    kh.setNgaySinh(rs.getDate("NgaySinh").toLocalDate());
                }
                kh.setHoatDong(rs.getBoolean("HoatDong"));

                // ========== TẠO KHUYENMAI (nếu có) ==========
                KhuyenMai km = null;
                if (rs.getString("MaKM") != null) {
                    km = new KhuyenMai();
                    km.setMaKM(rs.getString("MaKM"));
                    km.setTenKM(rs.getString("TenKM"));
                    km.setGiaTri(rs.getDouble("GiaTriKM"));
                    km.setKhuyenMaiHoaDon(rs.getBoolean("KhuyenMaiHoaDon"));
                    km.setDieuKienApDungHoaDon(rs.getDouble("DieuKienApDungHoaDon"));
                    km.setSoLuongKhuyenMai(rs.getInt("SoLuongKhuyenMai"));
                    if (rs.getString("HinhThucKM") != null) {
                        km.setHinhThuc(HinhThucKM.valueOf(rs.getString("HinhThucKM")));
                    }
                    if (rs.getDate("NgayBatDau") != null) {
                        km.setNgayBatDau(rs.getDate("NgayBatDau").toLocalDate());
                    }
                    if (rs.getDate("NgayKetThuc") != null) {
                        km.setNgayKetThuc(rs.getDate("NgayKetThuc").toLocalDate());
                    }
                    km.setTrangThai(rs.getBoolean("TrangThaiKM"));
                }

                // ========== LƯU TẠM ==========
                HoaDonTemp temp = new HoaDonTemp();
                temp.maHD = rs.getString("MaHoaDon");
                temp.ngayLap = rs.getDate("NgayLap").toLocalDate();
                temp.tongTien = rs.getDouble("TongThanhToan");
                temp.thuocKeDon = rs.getBoolean("ThuocKeDon");
                temp.nv = nv;
                temp.kh = kh;
                temp.km = km;
                tempList.add(temp);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi tìm hóa đơn theo SĐT: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ignored) {
            }
            try {
                if (ps != null)
                    ps.close();
            } catch (Exception ignored) {
            }
        }

        // Sau khi đóng ResultSet, lấy chi tiết cho từng hóa đơn
        for (HoaDonTemp temp : tempList) {
            List<ChiTietHoaDon> dsCT = layChiTietHoaDon(temp.maHD);

            HoaDon hd = new HoaDon();
            hd.setMaHoaDon(temp.maHD);
            hd.setNhanVien(temp.nv);
            hd.setKhachHang(temp.kh);
            hd.setNgayLap(temp.ngayLap);
            hd.setKhuyenMai(temp.km);
            hd.setThuocKeDon(temp.thuocKeDon);
            hd.setDanhSachChiTiet(dsCT);

            // Set tongTien bằng reflection
            try {
                var setTongTien = HoaDon.class.getDeclaredField("tongTien");
                setTongTien.setAccessible(true);
                setTongTien.set(hd, temp.tongTien);
            } catch (Exception ignore) {
            }

            dsHD.add(hd);
        }

        return dsHD;
    }

    // ========== PHẦN THỐNG KÊ CHO DASHBOARD ==========

    /**
     * Lấy tổng doanh thu theo tháng và năm
     * 
     * @param thang Tháng (1-12)
     * @param nam   Năm (VD: 2024, 2025)
     * @return Tổng doanh thu trong tháng đó
     */
    public double layDoanhThuTheoThang(int thang, int nam) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = """
                SELECT COALESCE(SUM(TongThanhToan), 0) AS TongDoanhThu
                FROM HoaDon
                WHERE MONTH(NgayLap) = ? AND YEAR(NgayLap) = ?
                """;

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);

            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("TongDoanhThu");
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy doanh thu theo tháng: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ignored) {
            }
            try {
                if (stmt != null)
                    stmt.close();
            } catch (Exception ignored) {
            }
        }
        return 0;
    }

    /**
     * 🔄 Force refresh cache - Xóa cache và load lại từ DB Dùng khi cần đồng bộ dữ
     * liệu real-time (VD: sau khi import data)
     */
    public void refreshCache() {
        cacheAllHoaDon = null;
        layTatCaHoaDon(); // Load lại ngay
    }

    /**
     * Đếm số hóa đơn theo tháng và năm
     * 
     * @param thang Tháng (1-12)
     * @param nam   Năm
     * @return Số lượng hóa đơn
     */
    public int demSoHoaDonTheoThang(int thang, int nam) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = """
                SELECT COUNT(*) AS SoLuong
                FROM HoaDon
                WHERE MONTH(NgayLap) = ? AND YEAR(NgayLap) = ?
                """;

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);

            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("SoLuong");
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi đếm số hóa đơn theo tháng: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ignored) {
            }
            try {
                if (stmt != null)
                    stmt.close();
            } catch (Exception ignored) {
            }
        }
        return 0;
    }

}