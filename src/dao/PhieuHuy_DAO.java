package dao;

import database.connectDB;
import entity.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PhieuHuy_DAO {

	// ============ CACHE LAYER ============
	// Cache toàn bộ phiếu hủy (dùng chung toàn ứng dụng)
	private static List<PhieuHuy> cacheAllPhieuHuy = null;

	/** 🔹 Lấy tất cả phiếu huỷ (kèm chi tiết, entity tự tính tongTien) - CÓ CACHE */
	public List<PhieuHuy> layTatCaPhieuHuy() {
		// Nếu cache đã có dữ liệu → Return cache (clone để tránh modify trực tiếp)
		if (cacheAllPhieuHuy != null && !cacheAllPhieuHuy.isEmpty()) {
			return new ArrayList<>(cacheAllPhieuHuy);
		}
		
		// Cache rỗng → Query DB và lưu vào cache
		List<PhieuHuy> list = new ArrayList<>();
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
		ChiTietPhieuHuy_DAO chiTietDAO = new ChiTietPhieuHuy_DAO();

		String sql = """
				    SELECT MaPhieuHuy, NgayLapPhieu, MaNhanVien, TrangThai
				    FROM PhieuHuy
				    ORDER BY NgayLapPhieu DESC, MaPhieuHuy DESC
				""";

		Statement st = null;
		ResultSet rs = null;

		try {
			st = con.createStatement();
			rs = st.executeQuery(sql);

			// Đọc tất cả mã phiếu trước
			List<String> dsMa = new ArrayList<>();
			List<LocalDate> dsNgay = new ArrayList<>();
			List<String> dsMaNV = new ArrayList<>();
			List<Boolean> dsTrangThai = new ArrayList<>();

			while (rs.next()) {
				dsMa.add(rs.getString("MaPhieuHuy"));
				dsNgay.add(rs.getDate("NgayLapPhieu").toLocalDate());
				dsMaNV.add(rs.getString("MaNhanVien"));
				dsTrangThai.add(rs.getBoolean("TrangThai"));
			}

			// Đóng ResultSet và Statement
			rs.close();
			st.close();

			// Load chi tiết sau
			for (int i = 0; i < dsMa.size(); i++) {
				NhanVien nv = null;
				ArrayList<NhanVien> dsNV = nhanVienDAO.timNhanVien(dsMaNV.get(i));
				if (!dsNV.isEmpty())
					nv = dsNV.get(0);

				PhieuHuy ph = new PhieuHuy(dsMa.get(i), dsNgay.get(i), nv, dsTrangThai.get(i));
				ph.setChiTietPhieuHuyList(chiTietDAO.timKiemChiTietPhieuHuyBangMa(dsMa.get(i)));
				ph.capNhatTongTienTheoChiTiet();
				list.add(ph);
			}
			
			// Lưu vào cache để lần sau không cần query nữa
			cacheAllPhieuHuy = list;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException ignored) {
			}
			try {
				if (st != null)
					st.close();
			} catch (SQLException ignored) {
			}
		}
		return new ArrayList<>(list); // Clone để tránh modify cache
	}
	/** � Đếm số phiếu hủy chưa duyệt (cho Dashboard) */
	public int demPhieuHuyChuaDuyet() {
		String sql = "SELECT COUNT(*) AS SoLuong FROM PhieuHuy WHERE TrangThai = 0";
		
		connectDB.getInstance();
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
			try { if (rs != null) rs.close(); } catch (Exception ignored) {}
			try { if (st != null) st.close(); } catch (Exception ignored) {}
		}
		
		return 0;
	}
	/**
	 * Tính tổng tiền hủy hàng theo tháng (cho biểu đồ)
	 * @param thang Tháng (1-12)
	 * @param nam Năm
	 * @return Tổng tiền hàng bị hủy
	 */
	public double tinhTongTienHuyTheoThang(int thang, int nam) {
		String sql = """
				SELECT COALESCE(SUM(TongTien), 0) AS TongTienHuy
				FROM PhieuHuy
				WHERE MONTH(NgayLapPhieu) = ? AND YEAR(NgayLapPhieu) = ?
				""";
		
		connectDB.getInstance();
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
			try { if (rs != null) rs.close(); } catch (Exception ignored) {}
			try { if (ps != null) ps.close(); } catch (Exception ignored) {}
		}
		
		return 0;
	}
	/** 🔹 Lấy phiếu huỷ theo mã (kèm chi tiết, entity tự tính tongTien) */
	public PhieuHuy layTheoMa(String maPhieuHuy) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
		ChiTietPhieuHuy_DAO chiTietDAO = new ChiTietPhieuHuy_DAO();

		String sql = """
				    SELECT MaPhieuHuy, NgayLapPhieu, MaNhanVien, TrangThai
				    FROM PhieuHuy WHERE MaPhieuHuy = ?
				""";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maPhieuHuy);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					LocalDate ngay = rs.getDate("NgayLapPhieu").toLocalDate();
					String maNV = rs.getString("MaNhanVien");
					boolean trangThai = rs.getBoolean("TrangThai");

					NhanVien nv = null;
					ArrayList<NhanVien> dsNV = nhanVienDAO.timNhanVien(maNV);
					if (!dsNV.isEmpty())
						nv = dsNV.get(0);

					PhieuHuy ph = new PhieuHuy(maPhieuHuy, ngay, nv, trangThai);
					ph.setChiTietPhieuHuyList(chiTietDAO.timKiemChiTietPhieuHuyBangMa(maPhieuHuy));
					ph.capNhatTongTienTheoChiTiet(); // tính trên entity
					return ph;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 🔹 Lấy danh sách chi tiết theo mã phiếu */
	public List<ChiTietPhieuHuy> layChiTietTheoMaPhieu(String maPhieuHuy) {
		return new ChiTietPhieuHuy_DAO().timKiemChiTietPhieuHuyBangMa(maPhieuHuy);
	}

	/**
	 * 🔹 Thêm phiếu huỷ + chi tiết (Transaction) – KHÔNG lưu TongTienHuy vì bảng
	 * không có cột này
	 */
	public boolean themPhieuHuy(PhieuHuy ph) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		if (ph.getChiTietPhieuHuyList() != null) {
			ph.capNhatTongTienTheoChiTiet();
		}

		String sqlPH = "INSERT INTO PhieuHuy (MaPhieuHuy, NgayLapPhieu, MaNhanVien, TrangThai, TongTien) VALUES (?, ?, ?, ?, ?)";

		// ✅ Thêm cột MaDonViTinh
		String sqlCT = "INSERT INTO ChiTietPhieuHuy (MaPhieuHuy, MaLo, SoLuongHuy, LyDoChiTiet, DonGiaNhap, ThanhTien, MaDonViTinh, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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

			con.commit();
			
			// ✅ Cập nhật cache: Thêm phiếu hủy mới vào đầu danh sách
			if (cacheAllPhieuHuy != null) {
				cacheAllPhieuHuy.add(0, ph); // Thêm vào đầu (mới nhất)
			}
			
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

	/** 🔹 Cập nhật trạng thái phiếu (true=đã duyệt, false=chờ duyệt) */
	public boolean capNhatTrangThai(String maPhieuHuy, boolean trangThaiMoi) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = "UPDATE PhieuHuy SET TrangThai = ? WHERE MaPhieuHuy = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setBoolean(1, trangThaiMoi);
			ps.setString(2, maPhieuHuy);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
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
		connectDB.getInstance();
		Connection con = connectDB.getConnection();
		String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String prefix = "PH-" + date + "-";

		String sql = "SELECT COUNT(*) FROM PhieuHuy WHERE MaPhieuHuy LIKE ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, prefix + "%");
			try (ResultSet rs = ps.executeQuery()) {
				int count = rs.next() ? rs.getInt(1) : 0;
				return String.format("%s%04d", prefix, count + 1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return prefix + "0001";
		}
	}

	/** 🔹 Xoá phiếu huỷ (xoá cả chi tiết) */
	public boolean xoa(String maPhieuHuy) {
		connectDB.getInstance();
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
			
			// ✅ Cập nhật cache: Xóa phiếu hủy khỏi cache
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

	/** Trả về true nếu mọi ChiTietPhieuHuy của phiếu đều KHÁC 'Chờ duyệt' */
	public boolean checkTrangThai(String maPhieuHuy) {
		ChiTietPhieuHuy_DAO ctDao = new ChiTietPhieuHuy_DAO();
		List<ChiTietPhieuHuy> ds = ctDao.timKiemChiTietPhieuHuyBangMa(maPhieuHuy); // :contentReference[oaicite:4]{index=4}

		for (ChiTietPhieuHuy ct : ds) {
			if (ct.getTrangThai() == ChiTietPhieuHuy.CHO_DUYET) { // 1 = Chờ duyệt :contentReference[oaicite:5]{index=5}
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
	    connectDB.getInstance();
	    Connection con = connectDB.getConnection();

	    String sql = """
	        SELECT COUNT(*) AS SoLuong
	        FROM PhieuHuy
	        WHERE MaNhanVien = ?
	          AND CAST(NgayLapPhieu AS DATE) = CAST(GETDATE() AS DATE)
	    """;

	    try (PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setString(1, maNhanVien);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("SoLuong");
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("❌ Lỗi đếm số phiếu huỷ hôm nay của nhân viên: " + e.getMessage());
	    }

	    return 0;
	}
	
	/**
	 * 🔄 Force refresh cache - Xóa cache và load lại từ DB
	 * Dùng khi cần đồng bộ dữ liệu real-time (VD: sau khi import data)
	 */
	public void refreshCache() {
		cacheAllPhieuHuy = null;
		layTatCaPhieuHuy(); // Load lại ngay
	}
	
	/**
	 * 📄 Lấy danh sách phiếu hủy từ cache (không query DB)
	 * @return Danh sách phiếu hủy trong cache, hoặc null nếu chưa load
	 */
	public List<PhieuHuy> layCacheTam() {
		if (cacheAllPhieuHuy == null) {
			return null;
		}
		return new ArrayList<>(cacheAllPhieuHuy);
	}
	
	/**
	 * 📝 Thêm phiếu hủy vào cache tạm (chưa lưu DB)
	 * @param ph Phiếu hủy cần thêm
	 */
	public void themVaoCacheTam(PhieuHuy ph) {
		if (cacheAllPhieuHuy == null) {
			cacheAllPhieuHuy = new ArrayList<>();
		}
		cacheAllPhieuHuy.add(0, ph);
	}
	
	/**
	 * ❌ Xóa phiếu hủy khỏi cache tạm
	 * @param maPhieuHuy Mã phiếu hủy cần xóa
	 */
	public void xoaKhoiCacheTam(String maPhieuHuy) {
		if (cacheAllPhieuHuy != null) {
			cacheAllPhieuHuy.removeIf(ph -> ph.getMaPhieuHuy().equals(maPhieuHuy));
		}
	}
	
	/**
	 * 🗑️ Xóa toàn bộ cache tạm
	 */
	public void xoaCacheTam() {
		cacheAllPhieuHuy = null;
	}

}
