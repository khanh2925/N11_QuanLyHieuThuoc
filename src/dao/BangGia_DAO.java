package dao;

import database.connectDB;
import entity.BangGia;
import entity.NhanVien;
import entity.ChiTietBangGia;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BangGia_DAO {

	// ============================================================
	// 📦 CACHE - Lưu trữ dữ liệu trong bộ nhớ
	// ============================================================
	private static List<BangGia> cacheAllBangGia = null;

	public BangGia_DAO() {
	}

	// ============================================================
	// 🔍 Lấy tất cả bảng giá (OPTIMIZED - CÓ CACHE)
	// ============================================================
	public List<BangGia> layTatCaBangGia() {
		// 1. Kiểm tra cache
		if (cacheAllBangGia != null && !cacheAllBangGia.isEmpty()) {
			return new ArrayList<>(cacheAllBangGia);
		}

		// 2. Nếu không có cache -> Query DB với JOIN để lấy tên nhân viên
		List<BangGia> ketQua = new ArrayList<>();
		String sql = """
				SELECT bg.MaBangGia, bg.TenBangGia, bg.NgayApDung, bg.HoatDong,
				       bg.MaNhanVien, nv.TenNhanVien
				FROM BangGia bg
				LEFT JOIN NhanVien nv ON bg.MaNhanVien = nv.MaNhanVien
				ORDER BY bg.NgayApDung DESC
				""";

		Connection con = connectDB.getConnection();
		Statement st = null;
		ResultSet rs = null;

		try {
			st = con.createStatement();
			rs = st.executeQuery(sql);

			while (rs.next()) {
				ketQua.add(taoBangGiaTuResultSet(rs));
			}

			// 3. Lưu vào cache
			cacheAllBangGia = ketQua;
			System.out.println("✅ Đã tải " + ketQua.size() + " bảng giá vào cache");

		} catch (SQLException e) {
			System.err.println("❌ Lỗi lấy danh sách bảng giá: " + e.getMessage());
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
			// ❗ KHÔNG đóng connection (singleton)
		}

		return new ArrayList<>(ketQua);
	}

	// ============================================================
	// 🔍 Lấy bảng giá đang hoạt động (TỪ CACHE)
	// ============================================================
	public BangGia layBangGiaDangHoatDong() {
		// Nếu có cache, tìm trong cache
		if (cacheAllBangGia != null) {
			for (BangGia bg : cacheAllBangGia) {
				if (bg.isHoatDong()) {
					return bg;
				}
			}
		}

		// Nếu chưa có cache, query DB với JOIN
		String sql = """
				SELECT bg.MaBangGia, bg.TenBangGia, bg.NgayApDung, bg.HoatDong,
				       bg.MaNhanVien, nv.TenNhanVien
				FROM BangGia bg
				LEFT JOIN NhanVien nv ON bg.MaNhanVien = nv.MaNhanVien
				WHERE bg.HoatDong = 1
				""";

		Connection con = connectDB.getConnection();
		Statement st = null;
		ResultSet rs = null;

		try {
			st = con.createStatement();
			rs = st.executeQuery(sql);

			if (rs.next()) {
				return taoBangGiaTuResultSet(rs);
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi lấy bảng giá đang hoạt động: " + e.getMessage());
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
			// ❗ KHÔNG đóng connection (singleton)
		}
		return null;
	}

	// ============================================================
	// 🔍 Tìm bảng giá theo mã (TỪ CACHE)
	// ============================================================
	public BangGia timBangGiaTheoMa(String maBangGia) {
		// Nếu có cache, tìm trong cache
		if (cacheAllBangGia != null) {
			for (BangGia bg : cacheAllBangGia) {
				if (bg.getMaBangGia().equals(maBangGia)) {
					return bg;
				}
			}
		}

		// Nếu chưa có cache, query DB với JOIN
		String sql = """
				SELECT bg.MaBangGia, bg.TenBangGia, bg.NgayApDung, bg.HoatDong,
				       bg.MaNhanVien, nv.TenNhanVien
				FROM BangGia bg
				LEFT JOIN NhanVien nv ON bg.MaNhanVien = nv.MaNhanVien
				WHERE bg.MaBangGia = ?
				""";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, maBangGia);
			rs = ps.executeQuery();

			if (rs.next()) {
				return taoBangGiaTuResultSet(rs);
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi tìm bảng giá theo mã: " + e.getMessage());
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
	// ➕ Thêm bảng giá mới (CẬP NHẬT CẢ DB VÀ CACHE)
	// ============================================================
	public boolean themBangGia(BangGia bg) {
		String sql = """
				INSERT INTO BangGia (MaBangGia, MaNhanVien, TenBangGia, NgayApDung, HoatDong)
				VALUES (?, ?, ?, ?, ?)
				""";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, bg.getMaBangGia());
			ps.setString(2, bg.getNhanVien().getMaNhanVien());
			ps.setString(3, bg.getTenBangGia());
			ps.setDate(4, Date.valueOf(bg.getNgayApDung()));
			ps.setBoolean(5, bg.isHoatDong());

			boolean success = ps.executeUpdate() > 0;
			if (success) {
				// ✅ Cập nhật cache: Thêm vào đầu danh sách
				if (cacheAllBangGia != null) {
					cacheAllBangGia.add(0, bg);
					System.out.println("✅ Đã thêm bảng giá vào cache: " + bg.getMaBangGia());
				}
			}
			return success;
		} catch (SQLException e) {
			System.err.println("❌ Lỗi thêm bảng giá: " + e.getMessage());
			return false;
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception ignored) {
			}
			// ❗ KHÔNG đóng connection (singleton)
		}
	}

	// ============================================================
	// 🔄 Cập nhật bảng giá (CẬP NHẬT CẢ DB VÀ CACHE)
	// ============================================================
	public boolean capNhatBangGia(BangGia bg) {
		String sql = """
				UPDATE BangGia
				SET MaNhanVien=?, TenBangGia=?, NgayApDung=?, HoatDong=?
				WHERE MaBangGia=?
				""";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, bg.getNhanVien().getMaNhanVien());
			ps.setString(2, bg.getTenBangGia());
			ps.setDate(3, Date.valueOf(bg.getNgayApDung()));
			ps.setBoolean(4, bg.isHoatDong());
			ps.setString(5, bg.getMaBangGia());

			boolean success = ps.executeUpdate() > 0;
			if (success && cacheAllBangGia != null) {
				// ✅ Cập nhật trong cache
				for (int i = 0; i < cacheAllBangGia.size(); i++) {
					if (cacheAllBangGia.get(i).getMaBangGia().equals(bg.getMaBangGia())) {
						cacheAllBangGia.set(i, bg);
						System.out.println("✅ Đã cập nhật bảng giá trong cache: " + bg.getMaBangGia());
						break;
					}
				}
			}
			return success;
		} catch (SQLException e) {
			System.err.println("❌ Lỗi cập nhật bảng giá: " + e.getMessage());
			return false;
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception ignored) {
			}
			// ❗ KHÔNG đóng connection (singleton)
		}
	}

	// ============================================================
	// 🔄 Hủy kích hoạt tất cả bảng giá khác (CẬP NHẬT CẢ DB VÀ CACHE)
	// ============================================================
	public boolean huyHoatDongTatCaTruBangGia(String maBangGia) {
		String sql = "UPDATE BangGia SET HoatDong = 0 WHERE MaBangGia <> ?";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, maBangGia);

			boolean success = ps.executeUpdate() > 0;
			if (success && cacheAllBangGia != null) {
				// ✅ Cập nhật trong cache
				for (BangGia bg : cacheAllBangGia) {
					if (!bg.getMaBangGia().equals(maBangGia)) {
						bg.setHoatDong(false);
					}
				}
				System.out.println("✅ Đã hủy hoạt động các bảng giá khác trong cache");
			}
			return success;
		} catch (SQLException e) {
			System.err.println("❌ Lỗi hủy hoạt động các bảng giá khác: " + e.getMessage());
			return false;
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception ignored) {
			}
			// ❗ KHÔNG đóng connection (singleton)
		}
	}

	// ============================================================
	// ❌ Xóa bảng giá (CẬP NHẬT CẢ DB VÀ CACHE)
	// ============================================================
	public boolean xoaBangGia(String maBangGia) {
		String sql = "DELETE FROM BangGia WHERE MaBangGia = ?";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, maBangGia);

			boolean success = ps.executeUpdate() > 0;
			if (success && cacheAllBangGia != null) {
				// ✅ Xóa khỏi cache
				cacheAllBangGia.removeIf(bg -> bg.getMaBangGia().equals(maBangGia));
				System.out.println("✅ Đã xóa bảng giá khỏi cache: " + maBangGia);
			}
			return success;
		} catch (SQLException e) {
			System.err.println("❌ Lỗi xóa bảng giá: " + e.getMessage());
			return false;
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception ignored) {
			}
			// ❗ KHÔNG đóng connection (singleton)
		}
	}

	// ============================================================
	// 🔄 Làm mới cache (reload từ database)
	// ============================================================
	public void lamMoiCache() {
		cacheAllBangGia = null;
		System.out.println("✅ Đã reset cache bảng giá");
	}

	// ============================================================
	// 🔍 Lấy danh sách chi tiết bảng giá theo mã bảng giá
	// ============================================================
	public List<ChiTietBangGia> layChiTietTheoMaBangGia(String maBangGia) {
		List<ChiTietBangGia> ds = new ArrayList<>();
		String sql = """
				SELECT MaBangGia, GiaTu, GiaDen, TiLe
				FROM ChiTietBangGia
				WHERE MaBangGia = ?
				""";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, maBangGia);
			rs = ps.executeQuery();

			while (rs.next()) {
				BangGia bg = new BangGia(maBangGia);
				ChiTietBangGia ct = new ChiTietBangGia(bg, rs.getDouble("GiaTu"), rs.getDouble("GiaDen"),
						rs.getDouble("TiLe"));
				ds.add(ct);
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi lấy chi tiết bảng giá: " + e.getMessage());
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
	// ➕ Thêm chi tiết bảng giá
	// ============================================================
	public boolean themChiTietBangGia(ChiTietBangGia ct) {
		String sql = "INSERT INTO ChiTietBangGia (MaBangGia, GiaTu, GiaDen, TiLe) VALUES (?, ?, ?, ?)";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, ct.getBangGia().getMaBangGia());
			ps.setDouble(2, ct.getGiaTu());
			ps.setDouble(3, ct.getGiaDen());
			ps.setDouble(4, ct.getTiLe());

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("❌ Lỗi thêm chi tiết bảng giá: " + e.getMessage());
			return false;
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception ignored) {
			}
			// ❗ KHÔNG đóng connection (singleton)
		}
	}

	// ============================================================
	// ❌ Xóa toàn bộ chi tiết của một bảng giá
	// ============================================================
	public boolean xoaTatCaChiTiet(String maBangGia) {
		String sql = "DELETE FROM ChiTietBangGia WHERE MaBangGia = ?";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, maBangGia);

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("❌ Lỗi xóa chi tiết bảng giá: " + e.getMessage());
			return false;
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception ignored) {
			}
			// ❗ KHÔNG đóng connection (singleton)
		}
	}

	// ============================================================
	// 🔧 TIỆN ÍCH
	// ============================================================
	private BangGia taoBangGiaTuResultSet(ResultSet rs) throws SQLException {
		String ma = rs.getString("MaBangGia");
		String ten = rs.getString("TenBangGia");
		LocalDate ngay = rs.getDate("NgayApDung").toLocalDate();
		boolean hoatDong = rs.getBoolean("HoatDong");

		// Lấy thông tin nhân viên từ JOIN
		String maNV = rs.getString("MaNhanVien");
		String tenNV = rs.getString("TenNhanVien");
		NhanVien nv = new NhanVien(maNV, tenNV);
		
		return new BangGia(ma, nv, ten, ngay, hoatDong);
	}

	// ============================================================
	// 🔧 Sinh mã bảng giá tự động (theo format BG-yyyyMMdd-xxxx)
	// ============================================================
	public String taoMaBangGia() {
		String today = java.time.LocalDate.now().toString().replaceAll("-", "");
		String prefix = "BG-" + today + "-";
		String sql = "SELECT MAX(MaBangGia) AS MaCuoi FROM BangGia WHERE MaBangGia LIKE ?";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, prefix + "%");
			rs = ps.executeQuery();

			if (rs.next() && rs.getString("MaCuoi") != null) {
				String last = rs.getString("MaCuoi").trim();
				int num = Integer.parseInt(last.substring(last.lastIndexOf("-") + 1));
				return prefix + String.format("%04d", num + 1);
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
		return prefix + "0001";
	}
}