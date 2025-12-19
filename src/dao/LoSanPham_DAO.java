package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List; // 💡 Bổ sung import List
import java.util.Map;

import database.connectDB;
import entity.LoSanPham;
import entity.SanPham;
import enums.LoaiSanPham;
import entity.ChiTietPhieuHuy;

public class LoSanPham_DAO {

	// CACHE LAYER
	private static List<LoSanPham> cacheAllLoSanPham = null;

	public LoSanPham_DAO() {
	}

	/** Xóa cache để buộc load lại dữ liệu mới từ DB */
	public static void clearCache() {
		cacheAllLoSanPham = null;
	}

	/** Lấy toàn bộ lô sản phẩm */
	public ArrayList<LoSanPham> layTatCaLoSanPham() {
		// 1. Kiểm tra cache
		if (cacheAllLoSanPham != null && !cacheAllLoSanPham.isEmpty()) {
			return new ArrayList<>(cacheAllLoSanPham);
		}

		ArrayList<LoSanPham> danhSach = new ArrayList<>();

		Connection con = connectDB.getConnection();

		String sql = """
				SELECT
					lo.MaLo, lo.HanSuDung, lo.SoLuongTon,
					sp.MaSanPham, sp.TenSanPham, sp.LoaiSanPham
				FROM LoSanPham lo
				LEFT JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
				""";

		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				String maLo = rs.getString("MaLo");
				LocalDate hanSuDung = rs.getDate("HanSuDung").toLocalDate();
				int soLuongTon = rs.getInt("SoLuongTon");
				String maSP = rs.getString("MaSanPham");

				SanPham sp = new SanPham();
				try {
					sp.setMaSanPham(maSP);
					sp.setTenSanPham(rs.getString("TenSanPham"));
					String loaiStr = rs.getString("LoaiSanPham");
					if (loaiStr != null) {
						sp.setLoaiSanPham(enums.LoaiSanPham.valueOf(loaiStr));
					}
				} catch (Exception ignore) {
				}

				danhSach.add(new LoSanPham(maLo, hanSuDung, soLuongTon, sp));
			}

		} catch (SQLException e) {
			System.err.println("Lỗi lấy danh sách lô sản phẩm: " + e.getMessage());
		}

		// 3. Update Cache
		cacheAllLoSanPham = new ArrayList<>(danhSach);

		return danhSach;
	}

	/** Thêm mới lô sản phẩm */
	public boolean themLoSanPham(LoSanPham lo) {

		Connection con = connectDB.getConnection();

		String sql = """
				    INSERT INTO LoSanPham (MaLo, HanSuDung, SoLuongTon, MaSanPham)
				    VALUES (?, ?, ?, ?)
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, lo.getMaLo());
			stmt.setDate(2, Date.valueOf(lo.getHanSuDung()));
			stmt.setInt(3, lo.getSoLuongTon());
			stmt.setString(4, lo.getSanPham() != null ? lo.getSanPham().getMaSanPham() : null);
			boolean result = stmt.executeUpdate() > 0;
			if (result && cacheAllLoSanPham != null) {
				cacheAllLoSanPham.add(0, lo);
			}
			return result;
		} catch (SQLException e) {
			System.err.println("Lỗi thêm lô sản phẩm: " + e.getMessage());
		}
		return false;
	}

	/** Cập nhật thông tin lô sản phẩm */
	public boolean capNhatLoSanPham(LoSanPham lo) {

		Connection con = connectDB.getConnection();

		String sql = """
				    UPDATE LoSanPham
				    SET HanSuDung=?, SoLuongTon=?, MaSanPham=?
				    WHERE MaLo=?
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setDate(1, Date.valueOf(lo.getHanSuDung()));
			stmt.setInt(2, lo.getSoLuongTon());
			stmt.setString(3, lo.getSanPham() != null ? lo.getSanPham().getMaSanPham() : null);
			stmt.setString(4, lo.getMaLo());
			boolean result = stmt.executeUpdate() > 0;

			// ✅ Cập nhật cache trực tiếp
			if (result && cacheAllLoSanPham != null) {
				for (int i = 0; i < cacheAllLoSanPham.size(); i++) {
					if (cacheAllLoSanPham.get(i).getMaLo().equals(lo.getMaLo())) {
						cacheAllLoSanPham.set(i, lo);
						break;
					}
				}
			}
			return result;
		} catch (SQLException e) {
			System.err.println("Lỗi cập nhật lô sản phẩm: " + e.getMessage());
		}
		return false;
	}

	/** Xóa lô sản phẩm theo mã */
	public boolean xoaLoSanPham(String maLo) {

		Connection con = connectDB.getConnection();

		String sql = "DELETE FROM LoSanPham WHERE MaLo=?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maLo);
			boolean result = stmt.executeUpdate() > 0;

			// ✅ Xóa khỏi cache
			if (result && cacheAllLoSanPham != null) {
				cacheAllLoSanPham.removeIf(l -> l.getMaLo().equals(maLo));
			}
			return result;
		} catch (SQLException e) {
			System.err.println("Lỗi xóa lô sản phẩm: " + e.getMessage());
		}
		return false;
	}

	/** Tìm lô sản phẩm chính xác theo mã (OPTIMIZED - dùng JOIN) */
	public LoSanPham timLoTheoMa(String maLo) {
		// 1. Check Cache
		if (cacheAllLoSanPham != null) {
			for (LoSanPham lo : cacheAllLoSanPham) {
				if (lo.getMaLo().equals(maLo)) {
					return lo;
				}
			}
		}

		Connection con = connectDB.getConnection();

		// ✅ OPTIMIZED: Dùng JOIN thay vì gọi SanPham_DAO riêng
		String sql = """
				SELECT
					lo.MaLo, lo.HanSuDung, lo.SoLuongTon,
					sp.MaSanPham, sp.TenSanPham, sp.LoaiSanPham, sp.SoDangKy,
					sp.DuongDung, sp.GiaNhap, sp.HinhAnh, sp.KeBanSanPham, sp.HoatDong
				FROM LoSanPham lo
				LEFT JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
				WHERE lo.MaLo = ?
				""";

		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = con.prepareStatement(sql);
			stmt.setString(1, maLo);
			rs = stmt.executeQuery();

			if (rs.next()) {
				// ========== TẠO SẢN PHẨM TỪ RESULTSET ==========
				SanPham sp = null;
				if (rs.getString("MaSanPham") != null) {
					sp = new SanPham();
					sp.setMaSanPham(rs.getString("MaSanPham"));
					sp.setTenSanPham(rs.getString("TenSanPham"));
					sp.setGiaNhap(rs.getDouble("GiaNhap"));

					String loaiStr = rs.getString("LoaiSanPham");
					if (loaiStr != null) {
						try {
							sp.setLoaiSanPham(LoaiSanPham.valueOf(loaiStr.trim().toUpperCase()));
						} catch (Exception ignore) {
						}
					}

					sp.setSoDangKy(rs.getString("SoDangKy"));
					sp.setHinhAnh(rs.getString("HinhAnh"));
					sp.setKeBanSanPham(rs.getString("KeBanSanPham"));
					sp.setHoatDong(rs.getBoolean("HoatDong"));

					String ddStr = rs.getString("DuongDung");
					if (ddStr != null) {
						try {
							sp.setDuongDung(enums.DuongDung.valueOf(ddStr.trim().toUpperCase()));
						} catch (Exception ignore) {
						}
					}
				}

				// ========== TẠO LÔ SẢN PHẨM ==========
				LocalDate hanSuDung = null;
				if (rs.getDate("HanSuDung") != null) {
					hanSuDung = rs.getDate("HanSuDung").toLocalDate();
				}
				int soLuongTon = rs.getInt("SoLuongTon");

				return new LoSanPham(maLo, hanSuDung, soLuongTon, sp);
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi tìm lô sản phẩm theo mã: " + e.getMessage());
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
			// ❗ KHÔNG đóng connection (singleton)
		}
		return null;
	}

	// 💡 HÀM BỔ SUNG: LẤY DANH SÁCH LÔ THEO MÃ SẢN PHẨM
	/**
	 * 🔹 Lấy danh sách lô đang có tồn kho và chưa hết hạn, sắp xếp theo HSD tăng
	 * dần (cũ nhất lên đầu)
	 */
	public List<LoSanPham> layDanhSachLoTheoMaSanPham(String maSanPham) {
		List<LoSanPham> danhSach = new ArrayList<>();

		Connection con = connectDB.getConnection();

		// Chỉ lấy lô còn tồn (> 0) và chưa hết hạn (>= GETDATE())
		String sql = """
				    SELECT MaLo, HanSuDung, SoLuongTon, MaSanPham
				    FROM LoSanPham
				    WHERE MaSanPham = ?
				      AND SoLuongTon > 0
				      AND HanSuDung >= GETDATE()
				    ORDER BY HanSuDung ASC
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maSanPham);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String maLo = rs.getString("MaLo");
					LocalDate hanSuDung = rs.getDate("HanSuDung").toLocalDate();
					int soLuongTon = rs.getInt("SoLuongTon");
					String maSP = rs.getString("MaSanPham");

					SanPham sp = new SanPham(maSP);
					danhSach.add(new LoSanPham(maLo, hanSuDung, soLuongTon, sp));
				}
			}
		} catch (SQLException e) {
			System.err.println("Lỗi lấy danh sách lô theo mã sản phẩm: " + e.getMessage());
		}
		return danhSach;
	}

	/** Tìm lô có hạn sử dụng sắp hết (cũ nhất) theo mã sản phẩm */
	public LoSanPham timLoGanHetHanTheoSanPham(String maSanPham) {

		Connection con = connectDB.getConnection();

		String sql = """
				    SELECT TOP 1 MaLo, HanSuDung, SoLuongTon, MaSanPham
				    FROM LoSanPham
				    WHERE MaSanPham = ?
				      AND HanSuDung >= GETDATE()
				      AND SoLuongTon > 0
				    ORDER BY HanSuDung ASC
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maSanPham);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String maLo = rs.getString("MaLo");
					LocalDate hanSuDung = rs.getDate("HanSuDung").toLocalDate();
					int soLuongTon = rs.getInt("SoLuongTon");
					String maSP = rs.getString("MaSanPham");

					SanPham sp = new SanPham();
					try {
						sp.setMaSanPham(maSP);
					} catch (IllegalArgumentException ignore) {
					}

					return new LoSanPham(maLo, hanSuDung, soLuongTon, sp);

				}
			}
		} catch (SQLException e) {
			System.err.println("Lỗi tìm lô gần hết hạn: " + e.getMessage());
		}
		return null;
	}

	/** Lấy lô kế tiếp (hạn tiếp theo) nếu lô hiện tại đã hết hàng */
	public LoSanPham timLoKeTiepTheoSanPham(String maSanPham, LocalDate hanSuDungHienTai) {

		Connection con = connectDB.getConnection();

		String sql = """
				    SELECT TOP 1 MaLo, HanSuDung, SoLuongTon, MaSanPham
				    FROM LoSanPham
				    WHERE MaSanPham = ?
				      AND HanSuDung > ?
				      AND HanSuDung >= GETDATE()
				      AND SoLuongTon > 0
				    ORDER BY HanSuDung ASC
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maSanPham);
			stmt.setDate(2, Date.valueOf(hanSuDungHienTai));

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String maLo = rs.getString("MaLo");
					LocalDate hanSuDung = rs.getDate("HanSuDung").toLocalDate();
					int soLuongTon = rs.getInt("SoLuongTon");
					String maSP = rs.getString("MaSanPham");

					SanPham sp = new SanPham();
					try {
						sp.setMaSanPham(maSP);
					} catch (IllegalArgumentException ignore) {
					}

					return new LoSanPham(maLo, hanSuDung, soLuongTon, sp);
				}
			}
		} catch (SQLException e) {
			System.err.println("Lỗi tìm lô kế tiếp: " + e.getMessage());
		}
		return null;
	}

	/** 🔹 Tính số lượng tồn thực tế (ĐÃ SỬA CHỈ TRỪ CÁC GIAO DỊCH CHỜ DUYỆT) */
	public int tinhSoLuongTonThucTe(String maLo) {

		Connection con = connectDB.getConnection();

		// Hằng số trạng thái
		final int CTPH_CHO_DUYET = ChiTietPhieuHuy.CHO_DUYET;
		final int CTPT_CHO_DUYET = 0;

		// Công thức: Tồn Kho (tại cột) - SUM(SL Chờ Duyệt PhieuHuy) - SUM(SL Chờ Duyệt
		// PhieuTra)
		String sql = """
				    SELECT
				        lo.SoLuongTon
				        - COALESCE(
				            (SELECT SUM(ctph.SoLuongHuy) FROM ChiTietPhieuHuy ctph
				             WHERE ctph.MaLo = lo.MaLo AND ctph.TrangThai = ?), 0)
				        - COALESCE(
				            (SELECT SUM(ctpt.SoLuong) FROM ChiTietPhieuTra ctpt
				             WHERE ctpt.MaLo = lo.MaLo AND ctpt.TrangThai = ?), 0)
				    AS SoLuongTonKhảDụng
				    FROM LoSanPham lo
				    WHERE lo.MaLo = ?
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			// Tham số 1: Trạng thái Chờ duyệt của Phiếu Hủy (1)
			stmt.setInt(1, CTPH_CHO_DUYET);
			// Tham số 2: Trạng thái Chờ duyệt của Phiếu Trả (0)
			stmt.setInt(2, CTPT_CHO_DUYET);
			// Tham số 3: Mã Lô
			stmt.setString(3, maLo);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int tonKhảDụng = rs.getInt("SoLuongTonKhảDụng");
					return Math.max(0, tonKhảDụng);
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi tính số lượng tồn thực tế: " + e.getMessage());
		}
		return 0;
	}

	public String taoMaLoTuDong() {
		String sql = "SELECT TOP 1 MaLo FROM LoSanPham WHERE MaLo LIKE 'LO-%' ORDER BY MaLo DESC";

		try (Connection con = connectDB.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			if (rs.next()) {
				String lastMaLo = rs.getString("MaLo"); // Ví dụ: LO-098907
				int lastNumber = Integer.parseInt(lastMaLo.substring(3)); // 98707
				int nextNumber = lastNumber + 1;
				return String.format("LO-%06d", nextNumber); // LO-098908
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Nếu chưa có lô nào → bắt đầu từ LO-000001
		return "LO-000001";
	}

	/** Hằng số số ngày cảnh báo gần hết hạn - áp dụng cho mọi loại sản phẩm */
	private static final int SO_NGAY_CANH_BAO = 90;

	/** Tìm các lô ĐÃ HẾT HẠN theo loại sản phẩm */
	public List<LoSanPham> timLoDaHetHanTheoLoai(LoaiSanPham loaiSanPham) {
		List<LoSanPham> danhSach = new ArrayList<>();

		if (loaiSanPham == null) {
			return danhSach;
		}

		// Logic hết hạn: HSD < today
		// (origin/khoi logic was kept, HEAD logic relied on missing method)
		Connection con = connectDB.getConnection();

		// Logic hết hạn: HSD < today
		String sql = """
				SELECT L.MaLo, L.HanSuDung, L.SoLuongTon, L.MaSanPham
				FROM LoSanPham L
				JOIN SanPham SP ON L.MaSanPham = SP.MaSanPham
				WHERE SP.LoaiSanPham = ?
				  AND L.HanSuDung < CAST(GETDATE() AS DATE)
				  AND L.SoLuongTon > 0
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, loaiSanPham.name()); // VD: THUC_PHAM_BO_SUNG

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String maLo = rs.getString("MaLo");
					LocalDate hanSuDung = rs.getDate("HanSuDung").toLocalDate();
					int soLuongTon = rs.getInt("SoLuongTon");
					String maSP = rs.getString("MaSanPham");

					SanPham sp = new SanPham(maSP);

					danhSach.add(new LoSanPham(maLo, hanSuDung, soLuongTon, sp));
				}
			}

		} catch (SQLException e) {
			System.err.println("Lỗi tìm lô gần hết hạn theo loại sản phẩm: " + e.getMessage());
		}

		return danhSach;
	}

	/** Thống kê số lô GẦN HẾT HẠN theo loại sản phẩm (0 < HSD - today <= 90) */
	public Map<LoaiSanPham, Integer> thongKeSoLoDaHetHanTheoHSDTheoLoai() {
		Map<LoaiSanPham, Integer> map = new LinkedHashMap<>();
		for (LoaiSanPham l : LoaiSanPham.values())
			map.put(l, 0);

		Connection con = connectDB.getConnection();

		// Logic gần hết hạn: 0 < (HSD - today) <= 90
		String sql = """
				    SELECT SP.LoaiSanPham, COUNT(*) AS SoLo
				    FROM LoSanPham L
				    JOIN SanPham SP ON L.MaSanPham = SP.MaSanPham
				    WHERE L.SoLuongTon > 0
				      AND L.HanSuDung > CAST(GETDATE() AS DATE)
				      AND L.HanSuDung <= DATEADD(DAY, 90, CAST(GETDATE() AS DATE))
				    GROUP BY SP.LoaiSanPham
				""";

		try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				String loaiStr = rs.getString("LoaiSanPham");
				int soLo = rs.getInt("SoLo");
				try {
					LoaiSanPham loai = LoaiSanPham.valueOf(loaiStr.trim().toUpperCase());
					map.put(loai, soLo);
				} catch (Exception ignore) {
				}
			}

		} catch (SQLException e) {
			System.err.println("❌ Lỗi thống kê số lô gần hết hạn theo loại: " + e.getMessage());
		}

		return map;
	}

	/**
	 * ✅ Lấy danh sách lô sắp hết hạn sử dụng
	 * Logic: 0 < (HSD - ngày hiện tại) <= 90
	 * (tức là today < HSD <= today + 90)
	 */
	public List<LoSanPham> layDanhSachLoSPToiHanSuDung() {
		List<LoSanPham> danhSach = new ArrayList<>();

		Connection con = connectDB.getConnection();

		// Logic sắp hết hạn: 0 < (HSD - today) <= 90
		String sql = """
				    SELECT L.MaLo, L.HanSuDung, L.SoLuongTon, L.MaSanPham
				    FROM LoSanPham L
				    JOIN SanPham SP ON L.MaSanPham = SP.MaSanPham
				    WHERE L.SoLuongTon > 0
				      AND L.HanSuDung > CAST(GETDATE() AS DATE)
				      AND L.HanSuDung <= DATEADD(DAY, 90, CAST(GETDATE() AS DATE))
				    ORDER BY L.HanSuDung ASC
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				String maLo = rs.getString("MaLo");
				LocalDate hanSuDung = rs.getDate("HanSuDung").toLocalDate();
				int soLuongTon = rs.getInt("SoLuongTon");
				String maSP = rs.getString("MaSanPham");

				SanPham sp = new SanPham(maSP);

				danhSach.add(new LoSanPham(maLo, hanSuDung, soLuongTon, sp));
			}

		} catch (SQLException e) {
			System.err.println("❌ Lỗi lấy danh sách lô gần hết hạn sử dụng: " + e.getMessage());
		}

		return danhSach;
	}

	/**
	 * ✅ Lấy danh sách lô sản phẩm đã hết hạn (bao gồm cả thông tin sản phẩm đầy đủ)
	 * Logic: ngày hiện tại > hạn sử dụng
	 */
	public List<LoSanPham> layDanhSachLoSPDaHetHan() {
		List<LoSanPham> danhSach = new ArrayList<>();

		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		// Lấy lô đã hết hạn với thông tin sản phẩm đầy đủ
		String sql = """
				    SELECT L.MaLo, L.HanSuDung, L.SoLuongTon,
				           SP.MaSanPham, SP.TenSanPham, SP.LoaiSanPham, SP.GiaNhap
				    FROM LoSanPham L
				    JOIN SanPham SP ON L.MaSanPham = SP.MaSanPham
				    WHERE L.SoLuongTon > 0
				      AND L.HanSuDung < CAST(GETDATE() AS DATE)
				    ORDER BY L.HanSuDung ASC
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				String maLo = rs.getString("MaLo");
				LocalDate hanSuDung = rs.getDate("HanSuDung").toLocalDate();
				int soLuongTon = rs.getInt("SoLuongTon");
				String maSP = rs.getString("MaSanPham");

				SanPham sp = new SanPham();
				try {
					sp.setMaSanPham(maSP);
					sp.setTenSanPham(rs.getString("TenSanPham"));
					sp.setGiaNhap(rs.getDouble("GiaNhap"));
					String loaiStr = rs.getString("LoaiSanPham");
					if (loaiStr != null) {
						sp.setLoaiSanPham(LoaiSanPham.valueOf(loaiStr.trim().toUpperCase()));
					}
				} catch (Exception ignore) {
				}

				danhSach.add(new LoSanPham(maLo, hanSuDung, soLuongTon, sp));
			}

		} catch (SQLException e) {
			System.err.println("❌ Lỗi lấy danh sách lô sản phẩm đã hết hạn: " + e.getMessage());
		}

		return danhSach;
	}

	/** ✅ Tìm lô sản phẩm theo keyword (Mã lô hoặc Mã SP) */
	public List<LoSanPham> timLoSanPhamTheoKeyword(String keyword) {
		List<LoSanPham> danhSach = new ArrayList<>();

		Connection con = connectDB.getConnection();

		String sql = """
				SELECT
					lo.MaLo, lo.HanSuDung, lo.SoLuongTon,
					sp.MaSanPham, sp.TenSanPham, sp.LoaiSanPham
				FROM LoSanPham lo
				LEFT JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
				WHERE LOWER(lo.MaLo) LIKE LOWER(?) OR LOWER(sp.MaSanPham) LIKE LOWER(?) OR LOWER(sp.TenSanPham) LIKE LOWER(?)
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			String query = "%" + keyword + "%";
			stmt.setString(1, query);
			stmt.setString(2, query);
			stmt.setString(3, query);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String maLo = rs.getString("MaLo");
					LocalDate hanSuDung = rs.getDate("HanSuDung").toLocalDate();
					int soLuongTon = rs.getInt("SoLuongTon");
					String maSP = rs.getString("MaSanPham");

					SanPham sp = new SanPham();
					try {
						sp.setMaSanPham(maSP);
						sp.setTenSanPham(rs.getString("TenSanPham"));
						String loaiStr = rs.getString("LoaiSanPham");
						if (loaiStr != null) {
							sp.setLoaiSanPham(enums.LoaiSanPham.valueOf(loaiStr));
						}
					} catch (Exception ignore) {
					}

					danhSach.add(new LoSanPham(maLo, hanSuDung, soLuongTon, sp));
				}
			}
		} catch (SQLException e) {
			System.err.println("Lỗi tìm lô theo keyword: " + e.getMessage());
		}
		return danhSach;
	}

	/**
	 * ✅ Kiểm tra lô có sắp hết hạn hay không
	 * Logic: 0 < (HSD - today) <= 90
	 */
	public boolean kiemTraLoToiHan(LoSanPham lo) {
		if (lo == null || lo.getSanPham() == null)
			return false;
		LocalDate today = LocalDate.now();
		LocalDate hanSuDung = lo.getHanSuDung();
		LocalDate ngayCanhBaoMax = today.plusDays(SO_NGAY_CANH_BAO);
		// 0 < (HSD - today) <= 90 → today < HSD <= today + 90
		return hanSuDung.isAfter(today) && !hanSuDung.isAfter(ngayCanhBaoMax);
	}

}