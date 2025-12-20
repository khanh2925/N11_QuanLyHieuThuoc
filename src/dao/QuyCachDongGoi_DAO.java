package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import database.connectDB;
import entity.QuyCachDongGoi;
import entity.DonViTinh;
import entity.SanPham;
import enums.DuongDung;
import enums.LoaiSanPham;

public class QuyCachDongGoi_DAO {

	public QuyCachDongGoi_DAO() {
	}

	// CACHE LAYER
	private static List<QuyCachDongGoi> cacheAllQuyCach = null;

	/** Lấy tất cả quy cách đóng gói với thông tin chi tiết (JOIN 3 bảng) */
	public ArrayList<QuyCachDongGoi> layTatCaQuyCachDongGoi() {
		// Check Cache
		if (cacheAllQuyCach != null) {
			return new ArrayList<>(cacheAllQuyCach);
		}

		ArrayList<QuyCachDongGoi> ds = new ArrayList<>();

		Connection con = connectDB.getConnection();

		String sql = "SELECT qc.MaQuyCach, qc.HeSoQuyDoi, qc.TiLeGiam, qc.DonViGoc, qc.TrangThai, "
				+ "       sp.MaSanPham, sp.TenSanPham, sp.LoaiSanPham, sp.SoDangKy, sp.DuongDung, sp.GiaNhap, sp.HinhAnh, sp.KeBanSanPham, sp.HoatDong, "
				+ "       dvt.MaDonViTinh, dvt.TenDonViTinh " + "FROM QuyCachDongGoi qc "
				+ "JOIN SanPham sp ON qc.MaSanPham = sp.MaSanPham "
				+ "JOIN DonViTinh dvt ON qc.MaDonViTinh = dvt.MaDonViTinh";

		try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {

			while (rs.next()) {
				String maQC = rs.getString("MaQuyCach");
				try {
					DonViTinh dvt = new DonViTinh(rs.getString("MaDonViTinh"), rs.getString("TenDonViTinh"));

					// Enum LoaiSanPham
					LoaiSanPham loai = null;
					String loaiSPStr = rs.getString("LoaiSanPham");
					if (loaiSPStr != null && !loaiSPStr.isBlank()) {
						try {
							loai = LoaiSanPham.valueOf(loaiSPStr.trim().toUpperCase());
						} catch (IllegalArgumentException e) {
							System.err.println("LoaiSanPham không hợp lệ cho MaQuyCach " + maQC + ": " + loaiSPStr);
						}
					}

					// Enum DuongDung
					DuongDung dd = null;
					String duongDungStr = rs.getString("DuongDung");
					if (duongDungStr != null && !duongDungStr.isBlank()) {
						try {
							dd = DuongDung.valueOf(duongDungStr.trim().toUpperCase());
						} catch (IllegalArgumentException e) {
							System.err.println("DuongDung không hợp lệ cho MaQuyCach " + maQC + ": " + duongDungStr);
						}
					}

					SanPham sp = new SanPham(rs.getString("MaSanPham"), rs.getString("TenSanPham"), loai,
							rs.getString("SoDangKy"), dd, rs.getDouble("GiaNhap"), rs.getString("HinhAnh"),
							rs.getString("KeBanSanPham"), rs.getBoolean("HoatDong"));

					QuyCachDongGoi qc = new QuyCachDongGoi(maQC, dvt, sp, rs.getInt("HeSoQuyDoi"),
							rs.getDouble("TiLeGiam"), rs.getBoolean("DonViGoc"), rs.getBoolean("TrangThai"));
					ds.add(qc);

				} catch (IllegalArgumentException e) {
					System.err.println("Lỗi dữ liệu không hợp lệ (MaQuyCach " + maQC + "): " + e.getMessage());
				}
			}
			// Save to Cache
			cacheAllQuyCach = new ArrayList<>(ds);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ds;
	}

	/** Sinh mã quy cách mới (dạng QC-000001) */
	public String taoMaQuyCach() {

		Connection con = connectDB.getConnection();
		String sql = "SELECT TOP 1 MaQuyCach FROM QuyCachDongGoi WHERE MaQuyCach LIKE 'QC-%' ORDER BY MaQuyCach DESC";
		try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			if (rs.next()) {
				String lastMa = rs.getString("MaQuyCach"); // ví dụ QC-000123
				if (lastMa != null && lastMa.matches("^QC-\\d{6}$")) {
					int lastNum = Integer.parseInt(lastMa.substring(3)); // bỏ QC-
					return String.format("QC-%06d", lastNum + 1);
				}
			}
		} catch (SQLException | NumberFormatException e) {
			e.printStackTrace();
		}
		return "QC-000001";
	}

	/** Thêm quy cách */
	public boolean themQuyCachDongGoi(QuyCachDongGoi q) {

		Connection con = connectDB.getConnection();
		String sql = "INSERT INTO QuyCachDongGoi (MaQuyCach, MaSanPham, MaDonViTinh, HeSoQuyDoi, TiLeGiam, DonViGoc, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, q.getMaQuyCach());
			ps.setString(2, q.getSanPham().getMaSanPham());
			ps.setString(3, q.getDonViTinh().getMaDonViTinh());
			ps.setInt(4, q.getHeSoQuyDoi());
			ps.setDouble(5, q.getTiLeGiam());
			ps.setBoolean(6, q.isDonViGoc());
			ps.setBoolean(7, q.isTrangThai());
			boolean result = ps.executeUpdate() > 0;

			// ✅ Update Cache
			if (result && cacheAllQuyCach != null) {
				cacheAllQuyCach.add(q);
			}
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/** Cập nhật quy cách */
	public boolean capNhatQuyCachDongGoi(QuyCachDongGoi q) {

		Connection con = connectDB.getConnection();
		String sql = "UPDATE QuyCachDongGoi SET MaSanPham = ?, MaDonViTinh = ?, HeSoQuyDoi = ?, TiLeGiam = ?, DonViGoc = ?, TrangThai = ? WHERE MaQuyCach = ?";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, q.getSanPham().getMaSanPham());
			ps.setString(2, q.getDonViTinh().getMaDonViTinh());
			ps.setInt(3, q.getHeSoQuyDoi());
			ps.setDouble(4, q.getTiLeGiam());
			ps.setBoolean(5, q.isDonViGoc());
			ps.setBoolean(6, q.isTrangThai());
			ps.setString(7, q.getMaQuyCach());
			boolean result = ps.executeUpdate() > 0;

			// ✅ Update Cache directly
			if (result && cacheAllQuyCach != null) {
				for (int i = 0; i < cacheAllQuyCach.size(); i++) {
					if (cacheAllQuyCach.get(i).getMaQuyCach().equals(q.getMaQuyCach())) {
						cacheAllQuyCach.set(i, q);
						break;
					}
				}
			}
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/** 🔹 Tìm Quy Cách Đóng Gói Gốc (donViGoc = 1) theo mã sản phẩm */
	public QuyCachDongGoi timQuyCachGocTheoSanPham(String maSanPham) {
		// 1. Check Cache First
		if (cacheAllQuyCach != null) {
			for (QuyCachDongGoi qc : cacheAllQuyCach) {
				if (qc.getSanPham().getMaSanPham().equals(maSanPham) && qc.isDonViGoc()) {
					return qc;
				}
			}
		}

		String sql = "SELECT qc.*, dvt.TenDonViTinh " + "FROM QuyCachDongGoi qc "
				+ "JOIN DonViTinh dvt ON qc.MaDonViTinh = dvt.MaDonViTinh "
				+ "WHERE qc.MaSanPham = ? AND qc.DonViGoc = 1";

		try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, maSanPham);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					DonViTinh dvt = new DonViTinh(rs.getString("MaDonViTinh"), rs.getString("TenDonViTinh"));

					// SanPham (chỉ cần mã để tham chiếu)
					SanPham sp = new SanPham();
					sp.setMaSanPham(maSanPham);

					return new QuyCachDongGoi(rs.getString("MaQuyCach"), dvt, sp, rs.getInt("HeSoQuyDoi"),
							rs.getDouble("TiLeGiam"), rs.getBoolean("DonViGoc"), rs.getBoolean("TrangThai"));
				}
			}
		} catch (Exception e) {
			System.err.println("❌ Lỗi tìm quy cách gốc cho SP " + maSanPham + ": " + e.getMessage());
		}
		return null;
	}

	// ✅✅✅ HÀM MỚI ĐƯỢC THÊM VÀO ✅✅✅
	/** 🔹 Lấy danh sách quy cách đóng gói (kèm ĐVT) theo mã sản phẩm */
	public ArrayList<QuyCachDongGoi> layDanhSachQuyCachTheoSanPham(String maSanPham) {
		// 1. Check Cache
		if (cacheAllQuyCach != null) {
			ArrayList<QuyCachDongGoi> ds = new ArrayList<>();
			for (QuyCachDongGoi qc : cacheAllQuyCach) {
				if (qc.getSanPham().getMaSanPham().equals(maSanPham)) {
					ds.add(qc);
				}
			}
			// Sort by HeSoQuyDoi (as in original SQL)
			ds.sort((o1, o2) -> Integer.compare(o1.getHeSoQuyDoi(), o2.getHeSoQuyDoi()));
			return ds;
		}

		ArrayList<QuyCachDongGoi> ds = new ArrayList<>();

		String sql = "SELECT qc.*, dvt.TenDonViTinh " + "FROM QuyCachDongGoi qc "
				+ "JOIN DonViTinh dvt ON qc.MaDonViTinh = dvt.MaDonViTinh " + "WHERE qc.MaSanPham = ? "
				+ "ORDER BY qc.HeSoQuyDoi ASC"; // Sắp xếp Đơn vị gốc lên đầu

		try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, maSanPham);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					DonViTinh dvt = new DonViTinh(rs.getString("MaDonViTinh"), rs.getString("TenDonViTinh"));

					SanPham sp = new SanPham(maSanPham);

					QuyCachDongGoi qc = new QuyCachDongGoi(rs.getString("MaQuyCach"), dvt, sp, rs.getInt("HeSoQuyDoi"),
							rs.getDouble("TiLeGiam"), rs.getBoolean("DonViGoc"), rs.getBoolean("TrangThai"));
					ds.add(qc);
				}
			}
		} catch (Exception e) {
			System.err.println("❌ Lỗi lấy danh sách quy cách cho SP " + maSanPham + ": " + e.getMessage());
		}
		return ds;
	}

	/** 🔹 Tìm quy cách đóng gói theo mã sản phẩm + mã đơn vị tính */
	public QuyCachDongGoi timQuyCachTheoSanPhamVaDonVi(String maSanPham, String maDonViTinh) {
		// 1. Check Cache
		if (cacheAllQuyCach != null) {
			for (QuyCachDongGoi qc : cacheAllQuyCach) {
				if (qc.getSanPham().getMaSanPham().equals(maSanPham)
						&& qc.getDonViTinh().getMaDonViTinh().equals(maDonViTinh)) {
					return qc;
				}
			}
		}

		Connection con = connectDB.getConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT qc.*, dvt.TenDonViTinh " + "FROM QuyCachDongGoi qc "
				+ "JOIN DonViTinh dvt ON qc.MaDonViTinh = dvt.MaDonViTinh "
				+ "WHERE qc.MaSanPham = ? AND qc.MaDonViTinh = ?";

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, maSanPham);
			ps.setString(2, maDonViTinh);

			rs = ps.executeQuery();
			if (rs.next()) {
				DonViTinh dvt = new DonViTinh(rs.getString("MaDonViTinh"), rs.getString("TenDonViTinh"));

				// SanPham chỉ cần mã (vì SP đầy đủ bạn đã có ở chỗ khác)
				SanPham sp = new SanPham();
				sp.setMaSanPham(maSanPham);

				return new QuyCachDongGoi(rs.getString("MaQuyCach"), dvt, sp, rs.getInt("HeSoQuyDoi"),
						rs.getDouble("TiLeGiam"), rs.getBoolean("DonViGoc"), rs.getBoolean("TrangThai"));
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi tìm quy cách SP=" + maSanPham + ", DVT=" + maDonViTinh + ": " + e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				// ❌ TUYỆT ĐỐI KHÔNG con.close() Ở ĐÂY
			} catch (SQLException ignore) {
			}
		}

		return null;
	}

	/** 🔹 Xóa quy cách đóng gói */
	public boolean xoaQuyCachDongGoi(String maQuyCach) {

		Connection con = connectDB.getConnection();
		String sql = "DELETE FROM QuyCachDongGoi WHERE MaQuyCach = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maQuyCach);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}