package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import database.connectDB;
import entity.NhaCungCap;

public class NhaCungCap_DAO {

	// ============ CACHE LAYER ============
	// Cache toàn bộ nhà cung cấp (dùng chung toàn ứng dụng)
	private static List<NhaCungCap> cacheAllNhaCungCap = null;

	public NhaCungCap_DAO() {
	}

	/** 📜 Lấy toàn bộ nhà cung cấp (CÓ CACHE - TỐI ƯU) */
	public List<NhaCungCap> layTatCaNhaCungCap() {
		// Nếu cache đã có dữ liệu → Return cache (clone để tránh modify trực tiếp)
		if (cacheAllNhaCungCap != null && !cacheAllNhaCungCap.isEmpty()) {
			return new ArrayList<>(cacheAllNhaCungCap);
		}

		// Cache rỗng → Query DB và lưu vào cache
		List<NhaCungCap> ds = new ArrayList<>();

		Connection con = connectDB.getConnection();

		String sql = """
				    SELECT MaNhaCungCap, TenNhaCungCap, SoDienThoai, DiaChi, Email, HoatDong
				    FROM NhaCungCap
				    ORDER BY MaNhaCungCap DESC
				""";

		try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {

			while (rs.next()) {
				NhaCungCap ncc = new NhaCungCap(rs.getString("MaNhaCungCap"), rs.getString("TenNhaCungCap"),
						rs.getString("SoDienThoai"), rs.getString("DiaChi"), rs.getString("Email"));
				ncc.setHoatDong(rs.getBoolean("HoatDong"));
				ds.add(ncc);
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi lấy danh sách nhà cung cấp: " + e.getMessage());
		}

		// Lưu vào cache để lần sau không cần query nữa
		cacheAllNhaCungCap = ds;

		return new ArrayList<>(ds); // Clone để tránh modify cache
	}

	/** 🔹 Thêm nhà cung cấp mới */
	public boolean themNhaCungCap(NhaCungCap ncc) {

		String sql = """
				    INSERT INTO NhaCungCap (MaNhaCungCap, TenNhaCungCap, SoDienThoai, DiaChi, Email, HoatDong)
				    VALUES (?, ?, ?, ?, ?, ?)
				""";

		try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, ncc.getMaNhaCungCap());
			ps.setString(2, ncc.getTenNhaCungCap());
			ps.setString(3, ncc.getSoDienThoai());
			ps.setString(4, ncc.getDiaChi());
			ps.setString(5, ncc.getEmail());
			ps.setBoolean(6, ncc.isHoatDong());

			boolean success = ps.executeUpdate() > 0;

			// ✅ Cập nhật cache: Thêm NCC mới vào đầu danh sách
			if (success && cacheAllNhaCungCap != null) {
				cacheAllNhaCungCap.add(0, ncc);
			}

			return success;
		} catch (SQLException e) {
			System.err.println("❌ Lỗi thêm nhà cung cấp: " + e.getMessage());
			return false;
		}
	}

	/** 🔹 Cập nhật nhà cung cấp */
	public boolean capNhatNhaCungCap(NhaCungCap ncc) {

		String sql = """
				    UPDATE NhaCungCap
				    SET TenNhaCungCap=?, SoDienThoai=?, DiaChi=?, Email=?, HoatDong=?
				    WHERE MaNhaCungCap=?
				""";

		try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, ncc.getTenNhaCungCap());
			ps.setString(2, ncc.getSoDienThoai());
			ps.setString(3, ncc.getDiaChi());
			ps.setString(4, ncc.getEmail());
			ps.setBoolean(5, ncc.isHoatDong());
			ps.setString(6, ncc.getMaNhaCungCap());

			boolean result = ps.executeUpdate() > 0;

			// ✅ Cập nhật cache trực tiếp
			if (result && cacheAllNhaCungCap != null) {
				for (int i = 0; i < cacheAllNhaCungCap.size(); i++) {
					if (cacheAllNhaCungCap.get(i).getMaNhaCungCap().equals(ncc.getMaNhaCungCap())) {
						cacheAllNhaCungCap.set(i, ncc);
						break;
					}
				}
			}
			return result;
		} catch (SQLException e) {
			System.err.println("❌ Lỗi cập nhật nhà cung cấp: " + e.getMessage());
		}
		return false;
	}

	/** 🔹 Sinh mã tự động NCC-yyyyMMdd-xxxx */
	public String taoMaTuDong() {

		Connection con = connectDB.getConnection();
		String sql = """
				    SELECT MAX(RIGHT(MaNhaCungCap, 4)) AS SoCuoi
				    FROM NhaCungCap
				    WHERE MaNhaCungCap LIKE 'NCC-%'
				""";
		try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			int so = 1;
			if (rs.next())
				so = rs.getInt("SoCuoi") + 1;

			String ngay = java.time.LocalDate.now().toString().replaceAll("-", "");
			return String.format("NCC-%s-%04d", ngay, so);
		} catch (SQLException e) {
			System.err.println("❌ Lỗi sinh mã nhà cung cấp: " + e.getMessage());
			return "NCC-" + System.currentTimeMillis();
		}
	}

	public NhaCungCap timNhaCungCapTheoMaHoacSDT(String keyword) {

		Connection con = connectDB.getConnection();

		// Tìm kiếm chính xác theo Mã hoặc SĐT
		String sql = "SELECT * FROM NhaCungCap WHERE MaNhaCungCap = ? OR SoDienThoai = ?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, keyword);
			stmt.setString(2, keyword);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					NhaCungCap ncc = new NhaCungCap(rs.getString("MaNhaCungCap"), rs.getString("TenNhaCungCap"),
							rs.getString("SoDienThoai"), rs.getString("DiaChi"), rs.getString("Email"));
					ncc.setHoatDong(rs.getBoolean("HoatDong"));
					return ncc;
				}
			}
		} catch (Exception e) {
			// Bắt Exception chung (bao gồm cả SQLException và IllegalArgumentException)
			System.err.println("❌ Lỗi timNhaCungCapTheoMaHoacSDT: " + e.getMessage());
		}
		return null; // không tìm thấy
	}

	/** 🔍 Tách khu vực (tỉnh/thành phố) từ địa chỉ */
	private String getKhuVucFromDiaChi(String diaChi) {
		if (diaChi == null || diaChi.isBlank())
			return "Không xác định";

		// Cắt sau dấu phẩy cuối
		if (diaChi.contains(",")) {
			return diaChi.substring(diaChi.lastIndexOf(",") + 1).trim();
		}

		return diaChi.trim(); // địa chỉ không có dấu phẩy
	}

	/** 🔎 Tìm kiếm nâng cao cho giao diện TraCuuNhaCungCap */
	public List<NhaCungCap> timKiemNCC(String keyword, String khuVuc, String trangThai, String tieuChi) {

		List<NhaCungCap> ds = new ArrayList<>();

		Connection con = connectDB.getConnection();

		String sql = """
				    SELECT MaNhaCungCap, TenNhaCungCap, SoDienThoai, DiaChi, Email, HoatDong
				    FROM NhaCungCap
				    WHERE 1 = 1
				""";

		// Keyword
		if (!keyword.isEmpty()) {
			sql += """
					    AND (MaNhaCungCap LIKE ?
					         OR TenNhaCungCap LIKE ?
					         OR SoDienThoai LIKE ?
					         OR Email LIKE ?)
					""";
		}

		// Khu vực -- bỏ LIKE, sẽ lọc sau khi lấy danh sách
		boolean filterKhuVuc = !khuVuc.equals("Tất cả");

		// Trạng thái
		if (trangThai.equals("Đang hợp tác"))
			sql += " AND HoatDong = 1 ";
		if (trangThai.equals("Ngừng hợp tác"))
			sql += " AND HoatDong = 0 ";

		// Sắp xếp
		if (tieuChi.equals("Tên A-Z"))
			sql += " ORDER BY TenNhaCungCap ASC ";
		else if (tieuChi.equals("Mới nhất"))
			sql += " ORDER BY MaNhaCungCap DESC ";
		else
			sql += " ORDER BY MaNhaCungCap ";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			int idx = 1;

			if (!keyword.isEmpty()) {
				String kw = "%" + keyword + "%";
				ps.setString(idx++, kw);
				ps.setString(idx++, kw);
				ps.setString(idx++, kw);
				ps.setString(idx++, kw);
			}

			if (!khuVuc.equals("Tất cả")) {
				ps.setString(idx++, "%" + khuVuc + "%");
			}

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {

				String diaChi = rs.getString("DiaChi");
				String khuVucNCC = getKhuVucFromDiaChi(diaChi);

				// ❗ Nếu có lọc theo khu vực → bỏ NCC không khớp khu vực
				if (filterKhuVuc && !khuVucNCC.equalsIgnoreCase(khuVuc)) {
					continue;
				}

				NhaCungCap ncc = new NhaCungCap(rs.getString("MaNhaCungCap"), rs.getString("TenNhaCungCap"),
						rs.getString("SoDienThoai"), diaChi, rs.getString("Email"));
				ncc.setHoatDong(rs.getBoolean("HoatDong"));
				ds.add(ncc);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ds;
	}

	/**
	 * 🔄 Force refresh cache - Xóa cache và load lại từ DB
	 * Dùng khi cần đồng bộ dữ liệu real-time (VD: sau khi import data)
	 */
	public void refreshCache() {
		cacheAllNhaCungCap = null;
		layTatCaNhaCungCap(); // Load lại ngay
	}

}
