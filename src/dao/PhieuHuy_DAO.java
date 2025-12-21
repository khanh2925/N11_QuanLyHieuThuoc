package dao;

import database.connectDB;
import entity.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PhieuHuy_DAO {

	// CACHE LAYER
	private static List<PhieuHuy> cacheAllPhieuHuy = null;

	/**
	 * Xóa cache để load lại dữ liệu mới từ DB
	 */
	public void clearCache() {
		cacheAllPhieuHuy = null;
	}

	public PhieuHuy_DAO() {
	}

	// ============================================================
	// 📜 Lấy tất cả phiếu huỷ (OPTIMIZED - dùng JOIN, CÓ CACHE)
	// ============================================================
	public List<PhieuHuy> layTatCaPhieuHuy() {
		// 1. Kiểm tra cache
		if (cacheAllPhieuHuy != null && !cacheAllPhieuHuy.isEmpty()) {
			return new ArrayList<>(cacheAllPhieuHuy);
		}

		// 2. Nếu không có cache -> Query DB với JOIN
		List<PhieuHuy> list = new ArrayList<>();

		Connection con = connectDB.getConnection();

		String sql = """
				SELECT
					ph.MaPhieuHuy, ph.NgayLapPhieu, ph.TrangThai, ph.TongTien,
					nv.MaNhanVien, nv.TenNhanVien, nv.QuanLy, nv.CaLam
				FROM PhieuHuy ph
				LEFT JOIN NhanVien nv ON ph.MaNhanVien = nv.MaNhanVien
				ORDER BY ph.NgayLapPhieu DESC, ph.MaPhieuHuy DESC
				""";

		PreparedStatement ps = null;
		ResultSet rs = null;

		// Tạm lưu danh sách phiếu huỷ (chưa có chi tiết)
		List<PhieuHuy> headers = new ArrayList<>();

		try {
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();

			while (rs.next()) {
				// ========== TẠO NHANVIEN ==========
				NhanVien nv = null;
				if (rs.getString("MaNhanVien") != null) {
					nv = new NhanVien();
					nv.setMaNhanVien(rs.getString("MaNhanVien"));
					nv.setTenNhanVien(rs.getString("TenNhanVien"));
					nv.setQuanLy(rs.getBoolean("QuanLy"));
					nv.setCaLam(rs.getInt("CaLam"));
				}

				// ========== LƯU TẠM ==========
				PhieuHuy ph = new PhieuHuy(rs.getString("MaPhieuHuy"),
						rs.getDate("NgayLapPhieu").toLocalDate(),
						nv,
						rs.getBoolean("TrangThai"));
				headers.add(ph);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException ignored) {
			}
			try {
				if (ps != null)
					ps.close();
			} catch (SQLException ignored) {
			}
			// ❗ KHÔNG đóng connection (singleton)
		}

		// 2.2. Sau khi đóng ResultSet, lấy chi tiết cho từng phiếu
		for (PhieuHuy ph : headers) {
			ph.setChiTietPhieuHuyList(layChiTietPhieuHuy(ph.getMaPhieuHuy()));
			ph.capNhatTongTienTheoChiTiet();
			list.add(ph);
		}

		// 3. Lưu vào cache
		cacheAllPhieuHuy = list;

		return new ArrayList<>(list);
	}

	// ============================================================
	// 📜 Lấy chi tiết phiếu huỷ (OPTIMIZED - dùng JOIN)
	// ============================================================
	private List<ChiTietPhieuHuy> layChiTietPhieuHuy(String maPhieuHuy) {
		List<ChiTietPhieuHuy> ds = new ArrayList<>();

		String sql = """
				SELECT
					ct.MaLo, ct.SoLuongHuy, ct.DonGiaNhap, ct.LyDoChiTiet, ct.ThanhTien, ct.TrangThai,
					ct.MaDonViTinh, dvt.TenDonViTinh,
					lo.HanSuDung, lo.SoLuongTon,
					sp.MaSanPham, sp.TenSanPham, sp.GiaNhap
				FROM ChiTietPhieuHuy ct
				LEFT JOIN DonViTinh dvt ON ct.MaDonViTinh = dvt.MaDonViTinh
				LEFT JOIN LoSanPham lo ON ct.MaLo = lo.MaLo
				LEFT JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
				WHERE ct.MaPhieuHuy = ?
				ORDER BY ct.MaLo
				""";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, maPhieuHuy);
			rs = ps.executeQuery();

			PhieuHuy ph = new PhieuHuy();
			ph.setMaPhieuHuy(maPhieuHuy);

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

				// ========== TẠO CHI TIẾT PHIẾU HUỶ ==========
				ChiTietPhieuHuy ct = new ChiTietPhieuHuy(ph, lo, rs.getInt("SoLuongHuy"), rs.getDouble("DonGiaNhap"),
						rs.getString("LyDoChiTiet"), dvt, rs.getInt("TrangThai"));
				ds.add(ct);
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
	// 🔔 Đếm số phiếu hủy chưa duyệt (cho Dashboard)
	// ============================================================
	public int demPhieuHuyChuaDuyet() {
		String sql = "SELECT COUNT(*) AS SoLuong FROM PhieuHuy WHERE TrangThai = 0";

		Connection con = connectDB.getConnection();
		Statement st = null;
		ResultSet rs = null;

		try {
			st = con.createStatement();
			rs = st.executeQuery(sql);

			if (rs.next()) {
				return rs.getInt("SoLuong");
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi đếm phiếu hủy chưa duyệt: " + e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ignored) {
			}
			try {
				if (st != null)
					st.close();
			} catch (Exception ignored) {
			}
		}

		return 0;
	}

	/**
	 * Tính tổng tiền hủy hàng theo tháng (cho biểu đồ)
	 * 
	 * @param thang Tháng (1-12)
	 * @param nam   Năm
	 * @return Tổng tiền hàng bị hủy
	 */
	public double tinhTongTienHuyTheoThang(int thang, int nam) {
		String sql = """
				SELECT COALESCE(SUM(TongTien), 0) AS TongTienHuy
				FROM PhieuHuy
				WHERE MONTH(NgayLapPhieu) = ? AND YEAR(NgayLapPhieu) = ?
				""";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setInt(1, thang);
			ps.setInt(2, nam);
			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getDouble("TongTienHuy");
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi tính tổng tiền hủy theo tháng: " + e.getMessage());
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

		return 0;
	}

	// ============================================================
	// 🔍 Lấy phiếu huỷ theo mã (OPTIMIZED - dùng JOIN)
	// ============================================================
	public PhieuHuy layTheoMa(String maPhieuHuy) {
		// 1. Kiểm tra cache
		if (cacheAllPhieuHuy != null) {
			for (PhieuHuy ph : cacheAllPhieuHuy) {
				if (ph.getMaPhieuHuy().equals(maPhieuHuy)) {
					return ph;
				}
			}
		}

		Connection con = connectDB.getConnection();

		String sql = """
				SELECT
					ph.MaPhieuHuy, ph.NgayLapPhieu, ph.TrangThai, ph.TongTien,
					nv.MaNhanVien, nv.TenNhanVien, nv.QuanLy, nv.CaLam
				FROM PhieuHuy ph
				LEFT JOIN NhanVien nv ON ph.MaNhanVien = nv.MaNhanVien
				WHERE ph.MaPhieuHuy = ?
				""";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, maPhieuHuy);
			rs = ps.executeQuery();

			if (rs.next()) {
				// ========== TẠO NHANVIEN ==========
				NhanVien nv = null;
				if (rs.getString("MaNhanVien") != null) {
					nv = new NhanVien();
					nv.setMaNhanVien(rs.getString("MaNhanVien"));
					nv.setTenNhanVien(rs.getString("TenNhanVien"));
					nv.setQuanLy(rs.getBoolean("QuanLy"));
					nv.setCaLam(rs.getInt("CaLam"));
				}

				LocalDate ngay = rs.getDate("NgayLapPhieu").toLocalDate();
				boolean trangThai = rs.getBoolean("TrangThai");

				// Đóng rs, ps trước khi gọi layChiTietPhieuHuy
				rs.close();
				ps.close();

				PhieuHuy ph = new PhieuHuy(maPhieuHuy, ngay, nv, trangThai);
				ph.setChiTietPhieuHuyList(layChiTietPhieuHuy(maPhieuHuy));
				ph.capNhatTongTienTheoChiTiet();
				return ph;
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
		}
		return null;
	}

	// ============================================================
	// 📜 Lấy danh sách chi tiết theo mã phiếu (public method)
	// ============================================================
	public List<ChiTietPhieuHuy> layChiTietTheoMaPhieu(String maPhieuHuy) {
		return layChiTietPhieuHuy(maPhieuHuy);
	}

	// ============================================================
	// ➕ Thêm phiếu huỷ + chi tiết (Transaction) + TRỪ TỒN KHO
	// ============================================================
	public boolean themPhieuHuy(PhieuHuy ph) {

		Connection con = connectDB.getConnection();

		if (ph.getChiTietPhieuHuyList() != null) {
			ph.capNhatTongTienTheoChiTiet();
		}

		String sqlPH = "INSERT INTO PhieuHuy (MaPhieuHuy, NgayLapPhieu, MaNhanVien, TrangThai, TongTien) VALUES (?, ?, ?, ?, ?)";

		// ✅ Thêm cột MaDonViTinh
		String sqlCT = "INSERT INTO ChiTietPhieuHuy (MaPhieuHuy, MaLo, SoLuongHuy, LyDoChiTiet, DonGiaNhap, ThanhTien, MaDonViTinh, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		// ✅ SQL trừ tồn kho
		String sqlTruTon = "UPDATE LoSanPham SET SoLuongTon = SoLuongTon - ? WHERE MaLo = ?";

		try {
			con.setAutoCommit(false);

			// 1️⃣ Thêm header
			try (PreparedStatement ps = con.prepareStatement(sqlPH)) {
				ps.setString(1, ph.getMaPhieuHuy());
				ps.setDate(2, java.sql.Date.valueOf(ph.getNgayLapPhieu()));
				ps.setString(3, ph.getNhanVien() != null ? ph.getNhanVien().getMaNhanVien() : null);
				ps.setBoolean(4, ph.isTrangThai());
				ps.setDouble(5, ph.getTongTien());
				ps.executeUpdate();
			}

			// 2️⃣ Thêm chi tiết
			try (PreparedStatement psCT = con.prepareStatement(sqlCT)) {
				for (ChiTietPhieuHuy ct : ph.getChiTietPhieuHuyList()) {
					psCT.setString(1, ph.getMaPhieuHuy());
					psCT.setString(2, ct.getLoSanPham().getMaLo());
					psCT.setInt(3, ct.getSoLuongHuy());
					psCT.setString(4, ct.getLyDoChiTiet());
					psCT.setDouble(5, ct.getDonGiaNhap());
					psCT.setDouble(6, ct.getThanhTien());

					// ✅ Thêm MaDonViTinh (từ ItemHuyHang.quyCachGoc)
					String maDonViTinh = null;
					if (ct.getDonViTinh() != null) {
						maDonViTinh = ct.getDonViTinh().getMaDonViTinh();
					}
					psCT.setString(7, maDonViTinh);

					psCT.setInt(8, ct.getTrangThai());
					psCT.addBatch();
				}
				psCT.executeBatch();
			}

			// 3️⃣ ✅ TRỪ TỒN KHO ngay khi tạo phiếu hủy
			try (PreparedStatement psTon = con.prepareStatement(sqlTruTon)) {
				for (ChiTietPhieuHuy ct : ph.getChiTietPhieuHuyList()) {
					psTon.setInt(1, ct.getSoLuongHuy());
					psTon.setString(2, ct.getLoSanPham().getMaLo());
					psTon.addBatch();
				}
				psTon.executeBatch();
			}

			con.commit();

			// ✅ Update Cache PhieuHuy: Thêm vào đầu danh sách
			if (cacheAllPhieuHuy != null) {
				cacheAllPhieuHuy.add(0, ph);
			}

			// ✅ Xóa cache LoSanPham vì đã thay đổi tồn kho
			LoSanPham_DAO.clearCache();

			return true;

		} catch (SQLException e) {
			System.err.println("❌ Lỗi thêm phiếu hủy: " + e.getMessage());
			e.printStackTrace();
			try {
				con.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return false;
		} finally {
			try {
				con.setAutoCommit(true);
			} catch (SQLException ignored) {
			}
		}
	}

	public boolean capNhatTrangThai(String maPhieuHuy, boolean trangThaiMoi) {

		Connection con = connectDB.getConnection();

		String sql = "UPDATE PhieuHuy SET TrangThai = ? WHERE MaPhieuHuy = ?";
		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setBoolean(1, trangThaiMoi);
			ps.setString(2, maPhieuHuy);
			boolean result = ps.executeUpdate() > 0;

			// ✅ Cập nhật cache trực tiếp
			if (result && cacheAllPhieuHuy != null) {
				for (PhieuHuy ph : cacheAllPhieuHuy) {
					if (ph.getMaPhieuHuy().equals(maPhieuHuy)) {
						ph.setTrangThai(trangThaiMoi);
						break;
					}
				}
			}
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception ignored) {
			}
		}
	}

	/**
	 * 🔹 (Tuỳ chọn) Tính lại tổng tiền trên entity – KHÔNG cập nhật DB vì không có
	 * cột để lưu
	 */
	public Double tinhTongTienTheoChiTiet(String maPhieuHuy) {
		PhieuHuy ph = layTheoMa(maPhieuHuy);
		if (ph == null)
			return null;
		ph.capNhatTongTienTheoChiTiet();
		return ph.getTongTien();
	}

	/**
	 * 🔹 Tạo mã tự động PH-yyyyMMdd-xxxx (độ dài 16 ký tự khớp CHECK + CHAR(16))
	 */
	public String taoMaPhieuHuy() {

		Connection con = connectDB.getConnection();
		String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String prefix = "PH-" + date + "-";

		String sql = "SELECT COUNT(*) FROM PhieuHuy WHERE MaPhieuHuy LIKE ?";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, prefix + "%");
			rs = ps.executeQuery();
			int count = rs.next() ? rs.getInt(1) : 0;
			return String.format("%s%04d", prefix, count + 1);
		} catch (SQLException e) {
			e.printStackTrace();
			return prefix + "0001";
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
	}

	// ============================================================
	// 🗑️ Xoá phiếu huỷ (xoá cả chi tiết)
	// ============================================================
	public boolean xoa(String maPhieuHuy) {

		Connection con = connectDB.getConnection();

		String sqlCT = "DELETE FROM ChiTietPhieuHuy WHERE MaPhieuHuy = ?";
		String sqlPH = "DELETE FROM PhieuHuy WHERE MaPhieuHuy = ?";

		try {
			con.setAutoCommit(false);

			try (PreparedStatement ps1 = con.prepareStatement(sqlCT);
					PreparedStatement ps2 = con.prepareStatement(sqlPH)) {

				ps1.setString(1, maPhieuHuy);
				ps1.executeUpdate();

				ps2.setString(1, maPhieuHuy);
				ps2.executeUpdate();
			}

			con.commit();

			// ✅ Xóa khỏi cache
			if (cacheAllPhieuHuy != null) {
				cacheAllPhieuHuy.removeIf(ph -> ph.getMaPhieuHuy().equals(maPhieuHuy));
			}

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				con.rollback();
			} catch (SQLException ignored) {
			}
			return false;
		} finally {
			try {
				con.setAutoCommit(true);
			} catch (SQLException ignored) {
			}
		}
	}

	public boolean checkTrangThai(String maPhieuHuy) {
		List<ChiTietPhieuHuy> ds = layChiTietPhieuHuy(maPhieuHuy);

		for (ChiTietPhieuHuy ct : ds) {
			if (ct.getTrangThai() == ChiTietPhieuHuy.CHO_DUYET) { // 1 = Chờ duyệt
				return false;
			}
		}
		return true;
	}

	/** update DB nếu đủ điều kiện */
	public boolean capNhatTrangThaiPhieuHuy(String maPhieuHuy) {
		if (checkTrangThai(maPhieuHuy)) {
			return capNhatTrangThai(maPhieuHuy, true);
		}
		return false;
	}

	// Đếm số PH của nhân viên đã lập trong ngày hiện tại.
	public int demSoPhieuHuyHomNayCuaNhanVien(String maNhanVien) {

		Connection con = connectDB.getConnection();

		String sql = """
				SELECT COUNT(*) AS SoLuong
				FROM PhieuHuy
				WHERE MaNhanVien = ?
				  AND CAST(NgayLapPhieu AS DATE) = CAST(GETDATE() AS DATE)
				""";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, maNhanVien);
			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt("SoLuong");
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi đếm số phiếu huỷ hôm nay của nhân viên: " + e.getMessage());
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

		return 0;
	}

}