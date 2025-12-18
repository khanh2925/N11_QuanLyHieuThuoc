package dao;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import database.connectDB;
import entity.NhanVien;

public class NhanVien_DAO {

	// ============ CACHE LAYER ============
	// Cache toàn bộ nhân viên (dùng chung toàn ứng dụng)
	private static List<NhanVien> cacheAllNhanVien = null;

	public NhanVien_DAO() {
	}

	/** 📜 Lấy toàn bộ nhân viên (CÓ CACHE - TỐI ƯU) */
	public ArrayList<NhanVien> layTatCaNhanVien() {
		// Nếu cache đã có dữ liệu → Return cache (clone để tránh modify trực tiếp)
		if (cacheAllNhanVien != null && !cacheAllNhanVien.isEmpty()) {
			return new ArrayList<>(cacheAllNhanVien);
		}

		// Cache rỗng → Query DB và lưu vào cache
		ArrayList<NhanVien> danhSach = new ArrayList<>();
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = "SELECT * FROM NhanVien ORDER BY MaNhanVien DESC";

		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				danhSach.add(taoNhanVienTuResultSet(rs));
			}

		} catch (SQLException e) {
			System.err.println("❌ Lỗi lấy danh sách nhân viên: " + e.getMessage());
		}

		// Lưu vào cache để lần sau không cần query nữa
		cacheAllNhanVien = danhSach;

		return new ArrayList<>(danhSach); // Clone để tránh modify cache
	}

	/** 🔹 Thêm nhân viên mới */
	public boolean themNhanVien(NhanVien nv) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = """
				    INSERT INTO NhanVien (MaNhanVien, TenNhanVien, GioiTinh, NgaySinh, SoDienThoai, DiaChi, QuanLy, CaLam, TrangThai)
				    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, nv.getMaNhanVien());
			stmt.setString(2, nv.getTenNhanVien());
			stmt.setBoolean(3, nv.isGioiTinh());
			stmt.setDate(4, nv.getNgaySinh() != null ? Date.valueOf(nv.getNgaySinh()) : null);
			stmt.setString(5, nv.getSoDienThoai());
			stmt.setString(6, nv.getDiaChi());
			stmt.setBoolean(7, nv.isQuanLy());
			stmt.setInt(8, nv.getCaLam());
			stmt.setBoolean(9, nv.isTrangThai());
			boolean success = stmt.executeUpdate() > 0;

			// ✅ Cập nhật cache: Thêm nhân viên mới vào đầu danh sách
			if (success && cacheAllNhanVien != null) {
				cacheAllNhanVien.add(0, nv);
			}

			return success;
		} catch (SQLException e) {
			System.err.println("❌ Lỗi thêm nhân viên: " + e.getMessage());
		}
		return false;
	}

	/** 🔹 Cập nhật thông tin nhân viên */
	public boolean capNhatNhanVien(NhanVien nv) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = """
				    UPDATE NhanVien
				    SET TenNhanVien=?, GioiTinh=?, NgaySinh=?, SoDienThoai=?, DiaChi=?, QuanLy=?, CaLam=?, TrangThai=?
				    WHERE MaNhanVien=?
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, nv.getTenNhanVien());
			stmt.setBoolean(2, nv.isGioiTinh());
			stmt.setDate(3, nv.getNgaySinh() != null ? Date.valueOf(nv.getNgaySinh()) : null);
			stmt.setString(4, nv.getSoDienThoai());
			stmt.setString(5, nv.getDiaChi());
			stmt.setBoolean(6, nv.isQuanLy());
			stmt.setInt(7, nv.getCaLam());
			stmt.setBoolean(8, nv.isTrangThai());
			stmt.setString(9, nv.getMaNhanVien());
			boolean success = stmt.executeUpdate() > 0;

			// ✅ Xóa cache sau khi cập nhật thành công để load lại dữ liệu mới
			if (success) {
				cacheAllNhanVien = null;
			}

			return success;
		} catch (SQLException e) {
			System.err.println("❌ Lỗi cập nhật nhân viên: " + e.getMessage());
		}
		return false;
	}

	/** 🔹 Xóa nhân viên */
	public boolean xoaNhanVien(String maNhanVien) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = "DELETE FROM NhanVien WHERE MaNhanVien=?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maNhanVien);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("❌ Lỗi xóa nhân viên: " + e.getMessage());
		}
		return false;
	}

	/** 🔹 Tìm nhân viên theo mã, tên hoặc số điện thoại (LIKE gần đúng) */
	public ArrayList<NhanVien> timNhanVien(String tuKhoa) {
		ArrayList<NhanVien> danhSach = new ArrayList<>();
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = """
				    SELECT * FROM NhanVien
				    WHERE MaNhanVien LIKE ?
				       OR TenNhanVien LIKE ?
				       OR SoDienThoai LIKE ?
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			String key = "%" + tuKhoa.trim() + "%";
			stmt.setString(1, key);
			stmt.setString(2, key);
			stmt.setString(3, key);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					danhSach.add(taoNhanVienTuResultSet(rs));
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi tìm nhân viên: " + e.getMessage());
		}
		return danhSach;
	}

	/** 🔹 Tìm nhân viên chính xác theo mã (dùng cho các DAO khác) */
	public NhanVien timNhanVienTheoMa(String maNhanVien) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();
		String sql = "SELECT * FROM NhanVien WHERE MaNhanVien = ?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maNhanVien);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return taoNhanVienTuResultSet(rs);
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi tìm nhân viên theo mã: " + e.getMessage());
		}
		return null;
	}

	/** 🔹 Cập nhật trạng thái làm việc */
	public boolean capNhatTrangThai(String maNhanVien, boolean trangThai) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = "UPDATE NhanVien SET TrangThai=? WHERE MaNhanVien=?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setBoolean(1, trangThai);
			stmt.setString(2, maNhanVien);
			boolean success = stmt.executeUpdate() > 0;

			// ✅ Xóa cache sau khi cập nhật thành công
			if (success) {
				cacheAllNhanVien = null;
			}

			return success;
		} catch (SQLException e) {
			System.err.println("❌ Lỗi cập nhật trạng thái nhân viên: " + e.getMessage());
		}
		return false;
	}

	/** 🔹 Tạo đối tượng NhanVien từ ResultSet */
	private NhanVien taoNhanVienTuResultSet(ResultSet rs) throws SQLException {
		String ma = rs.getString("MaNhanVien");
		String ten = rs.getString("TenNhanVien");
		boolean gioiTinh = rs.getBoolean("GioiTinh");
		Date d = rs.getDate("NgaySinh");
		LocalDate ngaySinh = (d != null) ? d.toLocalDate() : null;
		String sdt = rs.getString("SoDienThoai");
		String diaChi = rs.getString("DiaChi");
		boolean quanLy = rs.getBoolean("QuanLy");
		int caLam = rs.getInt("CaLam");
		boolean trangThai = rs.getBoolean("TrangThai");

		return new NhanVien(ma, ten, gioiTinh, ngaySinh, sdt, diaChi, quanLy, caLam, trangThai);
	}

	public String taoMaNhanVienTuDong() {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();
		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String prefix = "NV-" + today + "-";
		String sql = """
				    SELECT TOP 1 LTRIM(RTRIM(MaNhanVien)) AS MaNhanVien
				    FROM NhanVien
				    WHERE MaNhanVien LIKE ?
				    ORDER BY MaNhanVien DESC
				""";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, prefix + "%");
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					String lastCode = rs.getString(1).trim();

					int lastNum = Integer.parseInt(lastCode.substring(lastCode.lastIndexOf('-') + 1).trim());

					return prefix + String.format("%04d", lastNum + 1);
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi tạo mã NV tự động: " + e.getMessage());
		}

		return prefix + "0001";
	}

	/**
	 * 🔄 Force refresh cache - Xóa cache và load lại từ DB
	 * Dùng khi cần đồng bộ dữ liệu real-time (VD: sau khi import data)
	 */
	public void refreshCache() {
		cacheAllNhanVien = null;
		layTatCaNhanVien(); // Load lại ngay
	}

	/**
	 * 🔍 Tìm nhân viên theo SĐT (từ cache - tối ưu)
	 */
	public List<NhanVien> timNhanVienTheoSoDienThoai(String soDienThoai) {
		List<NhanVien> ketQua = new ArrayList<>();

		// Đảm bảo cache đã được load
		if (cacheAllNhanVien == null || cacheAllNhanVien.isEmpty()) {
			layTatCaNhanVien();
		}

		// Tìm từ cache
		for (NhanVien nv : cacheAllNhanVien) {
			if (nv.getSoDienThoai() != null && nv.getSoDienThoai().contains(soDienThoai)) {
				ketQua.add(nv);
			}
		}

		return ketQua;
	}

}
