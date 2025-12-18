package dao;

import database.connectDB;
import entity.ChiTietPhieuHuy;
import entity.DonViTinh;
import entity.LoSanPham;
import entity.PhieuHuy;
import entity.SanPham;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietPhieuHuy_DAO {

	public ChiTietPhieuHuy_DAO() {
	}

	// ============================================================
	// 🔍 Lấy danh sách chi tiết phiếu huỷ theo mã phiếu (OPTIMIZED - dùng JOIN)
	// ============================================================
	public List<ChiTietPhieuHuy> timKiemChiTietPhieuHuyBangMa(String maPhieuHuy) {
		List<ChiTietPhieuHuy> ds = new ArrayList<>();
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

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

		} catch (SQLException e) {
			System.err.println("❌ Lỗi tìm chi tiết phiếu huỷ: " + e.getMessage());
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
	// 🔄 Cập nhật trạng thái chi tiết
	// ============================================================
	public boolean capNhatTrangThaiChiTiet(String maPhieuHuy, String maLo, int trangThaiMoi) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sqlUpdateTrangThai = "UPDATE ChiTietPhieuHuy SET TrangThai = ? WHERE MaPhieuHuy = ? AND MaLo = ?";

		// ✅ SQL cộng lại tồn kho (dùng khi TỪ CHỐI HỦY - vì đã trừ khi tạo phiếu)
		String sqlCongTon = """
				    UPDATE LoSanPham SET SoLuongTon =
				        SoLuongTon + (SELECT SoLuongHuy
				                      FROM ChiTietPhieuHuy
				                      WHERE MaPhieuHuy=? AND MaLo=?)
				    WHERE MaLo = ?
				""";

		try {
			con.setAutoCommit(false);

			// 1️⃣ Update trạng thái
			try (PreparedStatement ps = con.prepareStatement(sqlUpdateTrangThai)) {
				ps.setInt(1, trangThaiMoi);
				ps.setString(2, maPhieuHuy);
				ps.setString(3, maLo);
				ps.executeUpdate();
			}

			// 2️⃣ Nếu trạng thái mới = 2 (HỦY HÀNG) → KHÔNG làm gì (đã trừ tồn khi tạo phiếu)

			// 3️⃣ Nếu trạng thái mới = 3 (TỪ CHỐI HỦY) → CỘNG LẠI TỒN KHO
			if (trangThaiMoi == 3) {
				try (PreparedStatement psTon = con.prepareStatement(sqlCongTon)) {
					psTon.setString(1, maPhieuHuy);
					psTon.setString(2, maLo);
					psTon.setString(3, maLo);
					psTon.executeUpdate();
				}
			}

			con.commit();
			return true;

		} catch (SQLException e) {
			System.err.println("❌ Lỗi cập nhật trạng thái chi tiết phiếu huỷ: " + e.getMessage());
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

	// ============================================================
	// 🗑️ Xoá chi tiết (và hoàn tồn nếu cần)
	// ============================================================
	public boolean xoaChiTietPhieuHuy(ChiTietPhieuHuy ct) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sqlDelete = "DELETE FROM ChiTietPhieuHuy WHERE MaPhieuHuy = ? AND MaLo = ?";
		String sqlUpdate = "UPDATE LoSanPham SET SoLuongTon = SoLuongTon + ? WHERE MaLo = ?";

		try {
			con.setAutoCommit(false);

			try (PreparedStatement ps = con.prepareStatement(sqlDelete)) {
				ps.setString(1, ct.getPhieuHuy().getMaPhieuHuy());
				ps.setString(2, ct.getLoSanPham().getMaLo());
				ps.executeUpdate();
			}

			// ✅ Cộng lại tồn kho nếu trạng thái là 1 (Chờ duyệt) hoặc 2 (Hủy hàng)
			// Vì tồn kho đã bị trừ khi tạo phiếu hủy
			// Không cộng nếu trạng thái = 3 (Từ chối) vì đã cộng lại khi từ chối
			if (ct.getTrangThai() == 1 || ct.getTrangThai() == 2) {
				try (PreparedStatement psTon = con.prepareStatement(sqlUpdate)) {
					psTon.setInt(1, ct.getSoLuongHuy());
					psTon.setString(2, ct.getLoSanPham().getMaLo());
					psTon.executeUpdate();
				}
			}

			con.commit();
			return true;
		} catch (SQLException e) {
			System.err.println("❌ Lỗi xoá chi tiết phiếu huỷ: " + e.getMessage());
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

	// ============================================================
	// ✅ Kiểm tra tất cả chi tiết đã xử lý chưa
	// ============================================================
	public boolean tatCaChiTietDaXuLy(String maPhieuHuy) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = """
				    SELECT COUNT(*)
				    FROM ChiTietPhieuHuy
				    WHERE MaPhieuHuy = ? AND TrangThai = 1   -- 1 = Chờ duyệt
				""";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, maPhieuHuy);
			rs = ps.executeQuery();

			if (rs.next()) {
				int soChoDuyet = rs.getInt(1);
				// Nếu KHÔNG còn dòng nào 'Chờ duyệt' => mọi chi tiết đã xử lý
				return soChoDuyet == 0;
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi kiểm tra trạng thái chi tiết PH: " + e.getMessage());
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
		// Lỡ lỗi gì thì coi như chưa xử lý hết
		return false;
	}

}
