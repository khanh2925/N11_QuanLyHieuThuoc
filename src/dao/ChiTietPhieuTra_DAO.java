package dao;

import database.connectDB;
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

	public ChiTietPhieuTra_DAO() {
	}

	// ============================================================
	// 🔍 Lấy danh sách chi tiết phiếu trả theo mã phiếu trả (OPTIMIZED - dùng JOIN)
	// ============================================================
	public List<ChiTietPhieuTra> timKiemChiTietBangMaPhieuTra(String maPhieuTra) {

		List<ChiTietPhieuTra> ds = new ArrayList<>();

		String sql = """
				SELECT
				    ctp.MaHoaDon, ctp.MaLo, ctp.SoLuong, ctp.ThanhTienHoan,
				    ctp.LyDoChiTiet, ctp.TrangThai,
				    ctp.MaDonViTinh AS MaDonViTinhCT,

				    -- ChiTietHoaDon
				    cthd.GiaBan, cthd.SoLuong AS SoLuongHD,
				    cthd.MaDonViTinh AS MaDonViTinhHD, cthd.MaKM, cthd.ThanhTien AS ThanhTienHD,

				    -- LoSanPham
				    lo.HanSuDung, lo.SoLuongTon,
				    sp.MaSanPham, sp.TenSanPham,

				    -- DonViTinh
				    dvt.TenDonViTinh,

				    -- KhuyenMai
				    km.TenKM, km.GiaTri, km.HinhThuc
				FROM ChiTietPhieuTra ctp
				LEFT JOIN ChiTietHoaDon cthd
				    ON  ctp.MaHoaDon   = cthd.MaHoaDon
				    AND ctp.MaLo       = cthd.MaLo
				    AND ctp.MaDonViTinh = cthd.MaDonViTinh
				LEFT JOIN LoSanPham lo
				    ON lo.MaLo = ctp.MaLo
				LEFT JOIN SanPham sp
				    ON sp.MaSanPham = lo.MaSanPham
				LEFT JOIN DonViTinh dvt
				    ON dvt.MaDonViTinh = ctp.MaDonViTinh
				LEFT JOIN KhuyenMai km
				    ON km.MaKM = cthd.MaKM
				WHERE ctp.MaPhieuTra = ?
				ORDER BY ctp.MaLo
				""";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, maPhieuTra);
			rs = ps.executeQuery();

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
				if (rs.getString("MaDonViTinhCT") != null) {
					dvt = new DonViTinh();
					dvt.setMaDonViTinh(rs.getString("MaDonViTinhCT"));
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
				ChiTietHoaDon cthd = new ChiTietHoaDon(hd, lo, rs.getInt("SoLuongHD"), dvt, rs.getDouble("GiaBan"),
						km);

				// ========== Phiếu trả ==========
				PhieuTra pt = new PhieuTra();
				pt.setMaPhieuTra(maPhieuTra);

				// ========== ChiTietPhieuTra ==========
				ChiTietPhieuTra ctpt = new ChiTietPhieuTra(pt, cthd, rs.getString("LyDoChiTiet"),
						rs.getInt("SoLuong"), rs.getInt("TrangThai"));
				ctpt.setDonViTinh(dvt);
				ds.add(ctpt);
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
	// ➕ Thêm mới 1 chi tiết phiếu trả
	// ============================================================
	public boolean themChiTietPhieuTra(ChiTietPhieuTra ctpt) {
		String sql = """
				    INSERT INTO ChiTietPhieuTra
				    (MaPhieuTra, MaHoaDon, MaLo, LyDoChiTiet, SoLuong, ThanhTienHoan, TrangThai, MaDonViTinh)
				    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
				""";

		Connection con = connectDB.getConnection();
		PreparedStatement stmt = null;

		try {
			stmt = con.prepareStatement(sql);
			stmt.setString(1, ctpt.getPhieuTra().getMaPhieuTra());
			stmt.setString(2, ctpt.getChiTietHoaDon().getHoaDon().getMaHoaDon());
			stmt.setString(3, ctpt.getChiTietHoaDon().getLoSanPham().getMaLo());
			stmt.setString(4, ctpt.getLyDoChiTiet());
			stmt.setInt(5, ctpt.getSoLuong());
			stmt.setDouble(6, ctpt.getThanhTienHoan());
			stmt.setInt(7, ctpt.getTrangThai());
			stmt.setString(8, ctpt.getDonViTinh() != null ? ctpt.getDonViTinh().getMaDonViTinh()
					: ctpt.getChiTietHoaDon().getDonViTinh().getMaDonViTinh());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception ignored) {
			}
			// ❗ KHÔNG đóng connection (singleton)
		}
	}

	// ============================================================
	// 🔄 Cập nhật trạng thái của 1 chi tiết phiếu trả
	// ============================================================
	public boolean capNhatTrangThaiChiTiet(String maPhieuTra, String maHoaDon, String maLo, String maDonViTinh,
			int trangThaiMoi) {
		String sql = """
				    UPDATE ChiTietPhieuTra
				    SET TrangThai = ?
				    WHERE MaPhieuTra = ? AND MaHoaDon = ? AND MaLo = ? AND MaDonViTinh = ?
				""";

		Connection con = connectDB.getConnection();
		PreparedStatement stmt = null;

		try {
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, trangThaiMoi);
			stmt.setString(2, maPhieuTra);
			stmt.setString(3, maHoaDon);
			stmt.setString(4, maLo);
			stmt.setString(5, maDonViTinh);

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception ignored) {
			}
			// ❗ KHÔNG đóng connection (singleton)
		}
	}

	// ============================================================
	// 🔢 Tính tổng số lượng đã trả của 1 sản phẩm theo mã HĐ + mã lô (OPTIMIZED -
	// dùng JOIN)
	// ============================================================
	public double tongSoLuongDaTra(String maHD, String maLo) {
		String sql = """
				    SELECT SUM(ct.SoLuong * qc.HeSoQuyDoi) AS TongGoc
				    FROM ChiTietPhieuTra ct
				    JOIN LoSanPham lo
				        ON lo.MaLo = ct.MaLo
				    JOIN QuyCachDongGoi qc
				        ON qc.MaDonViTinh = ct.MaDonViTinh
				       AND qc.MaSanPham   = lo.MaSanPham
				    WHERE ct.MaHoaDon = ?
				      AND ct.MaLo     = ?
				""";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, maHD);
			ps.setString(2, maLo);
			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getDouble("TongGoc");
			}
		} catch (SQLException e) {
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
		return 0;
	}

}
