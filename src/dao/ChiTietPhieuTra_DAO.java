package dao;

import connectDB.connectDB;
import entity.ChiTietHoaDon;
import entity.ChiTietPhieuTra;
import entity.DonViTinh;
import entity.HoaDon;
import entity.KhuyenMai;
import entity.LoSanPham;
import entity.PhieuTra;
import entity.SanPham;
import enums.HinhThucKM;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietPhieuTra_DAO {

	private final ChiTietHoaDon_DAO chiTietHoaDonDAO;

	public ChiTietPhieuTra_DAO() {
		this.chiTietHoaDonDAO = new ChiTietHoaDon_DAO();
	}

	// ============================================================
	// 🔍 Lấy danh sách chi tiết phiếu trả theo mã phiếu trả
	// ============================================================
    public List<ChiTietPhieuTra> timKiemChiTietBangMaPhieuTra(String maPhieuTra) {

        List<ChiTietPhieuTra> ds = new ArrayList<>();

        String sql = """
                SELECT
                    ctp.MaHoaDon, ctp.MaLo, ctp.SoLuong, ctp.ThanhTienHoan,
                    ctp.LyDoChiTiet, ctp.TrangThai,

                    -- ChiTietHoaDon
                    cthd.GiaBan, cthd.SoLuong AS SoLuongHD,
                    cthd.MaDonViTinh, cthd.MaKM, cthd.ThanhTien AS ThanhTienHD,

                    -- LoSanPham
                    lo.HanSuDung, lo.SoLuongTon,
                    sp.MaSanPham, sp.TenSanPham,

                    -- DonViTinh
                    dvt.TenDonViTinh,

                    -- KhuyenMai
                    km.TenKM, km.GiaTri, km.HinhThuc
                FROM ChiTietPhieuTra ctp
                LEFT JOIN ChiTietHoaDon cthd
                    ON ctp.MaHoaDon = cthd.MaHoaDon AND ctp.MaLo = cthd.MaLo
                LEFT JOIN LoSanPham lo
                    ON lo.MaLo = ctp.MaLo
                LEFT JOIN SanPham sp
                    ON sp.MaSanPham = lo.MaSanPham
                LEFT JOIN DonViTinh dvt
                    ON dvt.MaDonViTinh = cthd.MaDonViTinh
                LEFT JOIN KhuyenMai km
                    ON km.MaKM = cthd.MaKM
                WHERE ctp.MaPhieuTra = ?
                ORDER BY ctp.MaLo
                """;

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maPhieuTra);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    // ========== TẠO HÓA ĐƠN ==========
                    HoaDon hd = new HoaDon();
                    hd.setMaHoaDon(rs.getString("MaHoaDon"));

                    // ========== TẠO SẢN PHẨM ==========
                    SanPham sp = null;
                    if (rs.getString("MaSanPham") != null) {
                        sp = new SanPham();
                        sp.setMaSanPham(rs.getString("MaSanPham"));
                        sp.setTenSanPham(rs.getString("TenSanPham"));
                    }

                    // ========== TẠO LÔ ==========
                    LoSanPham lo = new LoSanPham();
                    lo.setMaLo(rs.getString("MaLo"));
                    if (rs.getDate("HanSuDung") != null)
                        lo.setHanSuDung(rs.getDate("HanSuDung").toLocalDate());
                    lo.setSoLuongTon(rs.getInt("SoLuongTon"));
                    lo.setSanPham(sp);

                    // ========== ĐƠN VỊ TÍNH ==========
                    DonViTinh dvt = null;
                    if (rs.getString("MaDonViTinh") != null) {
                        dvt = new DonViTinh();
                        dvt.setMaDonViTinh(rs.getString("MaDonViTinh"));
                        dvt.setTenDonViTinh(rs.getString("TenDonViTinh"));
                    }

                    // ========== KHUYẾN MÃI ==========
                    KhuyenMai km = null;
                    if (rs.getString("MaKM") != null) {
                        km = new KhuyenMai();
                        km.setMaKM(rs.getString("MaKM"));
                        km.setTenKM(rs.getString("TenKM"));
                        km.setGiaTri(rs.getDouble("GiaTri"));
                        km.setHinhThuc(HinhThucKM.valueOf(rs.getString("HinhThuc")));
                    }

                    // ========== ChiTietHoaDon ==========
                    ChiTietHoaDon cthd = new ChiTietHoaDon(
                            hd,
                            lo,
                            rs.getDouble("SoLuongHD"),   // null → 0
                            rs.getDouble("GiaBan"),       // null → 0
                            km,
                            dvt
                    );

                    // ========== Phiếu trả ==========
                    PhieuTra pt = new PhieuTra();
                    pt.setMaPhieuTra(maPhieuTra);

                    // ========== ChiTietPhieuTra ==========
                    ChiTietPhieuTra ctpt = new ChiTietPhieuTra(
                            pt,
                            cthd,
                            rs.getString("LyDoChiTiet"),
                            rs.getInt("SoLuong"),
                            rs.getInt("TrangThai")
                    );

                    ds.add(ctpt);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

	// ============================================================
	// ➕ Thêm mới 1 chi tiết phiếu trả
	// ============================================================
	public boolean themChiTietPhieuTra(ChiTietPhieuTra ctpt) {
		String sql = """
				    INSERT INTO ChiTietPhieuTra
				    (MaPhieuTra, MaHoaDon, MaLo, LyDoChiTiet, SoLuong, ThanhTienHoan, TrangThai)
				    VALUES (?, ?, ?, ?, ?, ?, ?)
				""";

		try (Connection con = connectDB.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, ctpt.getPhieuTra().getMaPhieuTra());
			stmt.setString(2, ctpt.getChiTietHoaDon().getHoaDon().getMaHoaDon());
			stmt.setString(3, ctpt.getChiTietHoaDon().getLoSanPham().getMaLo());
			stmt.setString(4, ctpt.getLyDoChiTiet());
			stmt.setInt(5, ctpt.getSoLuong());
			stmt.setDouble(6, ctpt.getThanhTienHoan());
			stmt.setInt(7, ctpt.getTrangThai());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// ============================================================
	// 🔄 Cập nhật trạng thái của 1 chi tiết phiếu trả
	// ============================================================
	public boolean capNhatTrangThaiChiTiet(String maPhieuTra, String maHoaDon, String maLo, int trangThaiMoi) {
		String sql = """
				    UPDATE ChiTietPhieuTra
				    SET TrangThai = ?
				    WHERE MaPhieuTra = ? AND MaHoaDon = ? AND MaLo = ?
				""";

		try (Connection con = connectDB.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setInt(1, trangThaiMoi);
			stmt.setString(2, maPhieuTra);
			stmt.setString(3, maHoaDon);
			stmt.setString(4, maLo);

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// ============================================================
	// 🔢 Tính tổng số lượng đã trả của 1 sản phẩm theo mã HĐ + mã lô
	// ============================================================
	public static double tongSoLuongDaTra(String maHD, String maLo) {
		double tong = 0;
		String sql = """
				    SELECT SUM(SoLuong)
				    FROM ChiTietPhieuTra
				    WHERE MaHoaDon = ? AND MaLo = ? AND TrangThai IN (0,1,2)
				""";

		try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, maHD);
			ps.setString(2, maLo);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					tong = rs.getDouble(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return tong;
	}
}
