package dao;

import database.connectDB;
import entity.KhuyenMai;
import enums.HinhThucKM;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMai_DAO {

	public KhuyenMai_DAO() {
	}

	// CACHE LAYER
	private static List<KhuyenMai> cacheAllKhuyenMai = null;

	/** 🔹 Tìm khuyến mãi theo mã */
	public KhuyenMai timKhuyenMaiTheoMa(String maKM) {
		// 1. Check Cache
		if (cacheAllKhuyenMai != null) {
			for (KhuyenMai km : cacheAllKhuyenMai) {
				if (km.getMaKM().equals(maKM)) {
					return km;
				}
			}
		}

		try {

			Connection con = connectDB.getConnection();
			String sql = "SELECT * FROM KhuyenMai WHERE MaKM = ?";
			try (PreparedStatement stmt = con.prepareStatement(sql)) {
				stmt.setString(1, maKM);
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						return taoKhuyenMaiTuResultSet(rs);
					}
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi tìm khuyến mãi theo mã: " + e.getMessage());
		}
		return null;
	}

	/** 🔹 Lấy tất cả khuyến mãi */
	public List<KhuyenMai> layTatCaKhuyenMai() {
		// Check Cache
		if (cacheAllKhuyenMai != null) {
			return new ArrayList<>(cacheAllKhuyenMai);
		}

		List<KhuyenMai> ds = new ArrayList<>();
		try {

			Connection con = connectDB.getConnection();
			String sql = "SELECT * FROM KhuyenMai ORDER BY NgayBatDau DESC";
			try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
				while (rs.next()) {
					ds.add(taoKhuyenMaiTuResultSet(rs));
				}
				// Save to Cache
				cacheAllKhuyenMai = new ArrayList<>(ds);
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi lấy tất cả khuyến mãi: " + e.getMessage());
		}
		return ds;
	}

	/** 🔹 Thêm khuyến mãi */
	public boolean themKhuyenMai(KhuyenMai km) {

		Connection con = connectDB.getConnection();
		String sql = """
				    INSERT INTO KhuyenMai (MaKM, TenKM, NgayBatDau, NgayKetThuc, TrangThai, KhuyenMaiHoaDon,
				                           HinhThuc, GiaTri, DieuKienApDungHoaDon, SoLuongKhuyenMai)
				    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			ganGiaTriKhuyenMai(stmt, km);
			boolean result = stmt.executeUpdate() > 0;

			// ✅ Update Cache
			if (result && cacheAllKhuyenMai != null) {
				cacheAllKhuyenMai.add(0, km);
			}
			return result;
		} catch (SQLException e) {
			System.err.println("❌ Lỗi thêm khuyến mãi: " + e.getMessage());
		}
		return false;
	}

	/** 🔹 Cập nhật khuyến mãi */
	public boolean capNhatKhuyenMai(KhuyenMai km) {

		Connection con = connectDB.getConnection();
		String sql = """
				    UPDATE KhuyenMai
				    SET TenKM=?, NgayBatDau=?, NgayKetThuc=?, TrangThai=?, KhuyenMaiHoaDon=?,
				        HinhThuc=?, GiaTri=?, DieuKienApDungHoaDon=?, SoLuongKhuyenMai=?
				    WHERE MaKM=?
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, km.getTenKM());
			stmt.setDate(2, Date.valueOf(km.getNgayBatDau()));
			stmt.setDate(3, Date.valueOf(km.getNgayKetThuc()));
			stmt.setBoolean(4, km.isTrangThai());
			stmt.setBoolean(5, km.isKhuyenMaiHoaDon());
			stmt.setString(6, km.getHinhThuc().name());
			stmt.setDouble(7, km.getGiaTri());
			stmt.setDouble(8, km.getDieuKienApDungHoaDon());
			stmt.setInt(9, km.getSoLuongKhuyenMai());
			stmt.setString(10, km.getMaKM());
			boolean result = stmt.executeUpdate() > 0;

			// ✅ Update Cache directly
			if (result && cacheAllKhuyenMai != null) {
				for (int i = 0; i < cacheAllKhuyenMai.size(); i++) {
					if (cacheAllKhuyenMai.get(i).getMaKM().equals(km.getMaKM())) {
						cacheAllKhuyenMai.set(i, km);
						break;
					}
				}
			}
			return result;
		} catch (SQLException e) {
			System.err.println("❌ Lỗi cập nhật khuyến mãi: " + e.getMessage());
		}
		return false;
	}

	/** 🔹 Giảm số lượng khuyến mãi sau khi áp dụng */
	public boolean giamSoLuong(String maKM) {
		String sql = "UPDATE KhuyenMai SET SoLuongKhuyenMai = SoLuongKhuyenMai - 1 WHERE MaKM = ? AND SoLuongKhuyenMai > 0";
		try {

			Connection con = connectDB.getConnection();
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setString(1, maKM);
				boolean result = ps.executeUpdate() > 0;

				// ✅ Update Cache (decrease quantity by 1)
				if (result && cacheAllKhuyenMai != null) {
					for (KhuyenMai km : cacheAllKhuyenMai) {
						if (km.getMaKM().equals(maKM)) {
							km.setSoLuongKhuyenMai(km.getSoLuongKhuyenMai() - 1);
							break;
						}
					}
				}
				return result;
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi giảm số lượng khuyến mãi: " + e.getMessage());
		}
		return false;
	}

	/** 🔹 Lấy danh sách khuyến mãi đang hoạt động */
	public List<KhuyenMai> layKhuyenMaiDangHoatDong() {
		// 💡 Use Cache Logic
		if (cacheAllKhuyenMai != null) {
			List<KhuyenMai> ds = new ArrayList<>();
			LocalDate today = LocalDate.now();
			for (KhuyenMai km : cacheAllKhuyenMai) {
				// TrangThai = 1
				// AND GETDATE() BETWEEN NgayBatDau AND NgayKetThuc
				// AND SoLuongKhuyenMai > 0
				if (km.isTrangThai() &&
						(today.isEqual(km.getNgayBatDau()) || today.isAfter(km.getNgayBatDau())) &&
						(today.isEqual(km.getNgayKetThuc()) || today.isBefore(km.getNgayKetThuc())) &&
						km.getSoLuongKhuyenMai() > 0) {
					ds.add(km);
				}
			}
			return ds;
		}

		List<KhuyenMai> ds = new ArrayList<>();
		String sql = """
				    SELECT * FROM KhuyenMai
				    WHERE TrangThai = 1
				      AND GETDATE() BETWEEN NgayBatDau AND NgayKetThuc
				      AND SoLuongKhuyenMai > 0
				    ORDER BY NgayBatDau DESC
				""";
		try {

			Connection con = connectDB.getConnection();
			try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
				while (rs.next())
					ds.add(taoKhuyenMaiTuResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi lấy khuyến mãi đang hoạt động: " + e.getMessage());
		}
		return ds;
	}

	// =================== TIỆN ÍCH ===================

	private KhuyenMai taoKhuyenMaiTuResultSet(ResultSet rs) throws SQLException {
		String maKM = rs.getString("MaKM");
		String tenKM = rs.getString("TenKM");
		LocalDate ngayBatDau = rs.getDate("NgayBatDau").toLocalDate();
		LocalDate ngayKetThuc = rs.getDate("NgayKetThuc").toLocalDate();
		boolean trangThai = rs.getBoolean("TrangThai");
		boolean kmHoaDon = rs.getBoolean("KhuyenMaiHoaDon");
		HinhThucKM hinhThuc = null;
		String hinhThucStr = rs.getString("HinhThuc");
		if (hinhThucStr != null && !hinhThucStr.isBlank()) {
			try {
				hinhThuc = HinhThucKM.valueOf(hinhThucStr.trim().toUpperCase());
			} catch (Exception ignore) {
			}
		}
		double giaTri = rs.getDouble("GiaTri");
		double dieuKien = rs.getDouble("DieuKienApDungHoaDon");
		int soLuongKhuyenMai = rs.getInt("SoLuongKhuyenMai");
		return new KhuyenMai(maKM, tenKM, ngayBatDau, ngayKetThuc, trangThai, kmHoaDon, hinhThuc, giaTri, dieuKien,
				soLuongKhuyenMai);
	}

	/** 🔹 Sinh mã tự động theo định dạng: KM-yyyyMMdd-xxxx */
	public String taoMaKhuyenMai() {

		Connection con = connectDB.getConnection();

		LocalDate homNay = LocalDate.now();
		String ngay = homNay.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
		String prefix = "KM-" + ngay + "-";

		String sql = "SELECT MAX(MaKM) AS MaLonNhat FROM KhuyenMai WHERE MaKM LIKE ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, prefix + "%");
			try (ResultSet rs = ps.executeQuery()) {
				int soThuTu = 1;
				if (rs.next() && rs.getString("MaLonNhat") != null) {
					String maMax = rs.getString("MaLonNhat");
					String[] parts = maMax.split("-");
					if (parts.length == 3) {
						soThuTu = Integer.parseInt(parts[2].trim()) + 1;
					}
				}
				return prefix + String.format("%04d", soThuTu);
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi sinh mã khuyến mãi: " + e.getMessage());
			return prefix + "0001";
		}
	}

	private void ganGiaTriKhuyenMai(PreparedStatement stmt, KhuyenMai km) throws SQLException {
		stmt.setString(1, km.getMaKM());
		stmt.setString(2, km.getTenKM());
		stmt.setDate(3, Date.valueOf(km.getNgayBatDau()));
		stmt.setDate(4, Date.valueOf(km.getNgayKetThuc()));
		stmt.setBoolean(5, km.isTrangThai());
		stmt.setBoolean(6, km.isKhuyenMaiHoaDon());
		stmt.setString(7, km.getHinhThuc().name());
		stmt.setDouble(8, km.getGiaTri());
		stmt.setDouble(9, km.getDieuKienApDungHoaDon());
		stmt.setInt(10, km.getSoLuongKhuyenMai());
	}

	/** 🔹 Xóa khuyến mãi theo mã */
	public boolean xoaKhuyenMai(String maKM) {

		Connection con = connectDB.getConnection();

		String sql = "DELETE FROM KhuyenMai WHERE MaKM = ?";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maKM);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("❌ Lỗi xóa khuyến mãi: " + e.getMessage());
		}
		return false;
	}

}
