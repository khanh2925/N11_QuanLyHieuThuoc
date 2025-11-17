package dao;

import connectDB.connectDB;
import entity.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PhieuHuy_DAO {

	/** 🔹 Lấy tất cả phiếu huỷ (kèm chi tiết) */
	public List<PhieuHuy> layTatCaPhieuHuy() {
		List<PhieuHuy> list = new ArrayList<>();
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
		ChiTietPhieuHuy_DAO chiTietDAO = new ChiTietPhieuHuy_DAO();

		String sql = """
				    SELECT MaPhieuHuy, NgayLapPhieu, MaNhanVien, TongTienHuy, TrangThai
				    FROM PhieuHuy
				    ORDER BY NgayLapPhieu DESC, MaPhieuHuy DESC
				""";

		try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {

			while (rs.next()) {
				String ma = rs.getString("MaPhieuHuy");
				LocalDate ngay = rs.getDate("NgayLapPhieu").toLocalDate();
				String maNV = rs.getString("MaNhanVien");
				boolean trangThai = rs.getBoolean("TrangThai"); // true = đã duyệt

				// ✅ Dùng timNhanVien() để tìm theo mã
				NhanVien nv = null;
				ArrayList<NhanVien> dsNV = nhanVienDAO.timNhanVien(maNV);
				if (!dsNV.isEmpty())
					nv = dsNV.get(0);

				PhieuHuy ph = new PhieuHuy(ma, ngay, nv, trangThai);
				ph.setChiTietPhieuHuyList(chiTietDAO.timKiemChiTietPhieuHuyBangMa(ma));

				list.add(ph);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/** 🔹 Lấy phiếu huỷ theo mã */
	public PhieuHuy layTheoMa(String maPhieuHuy) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
		ChiTietPhieuHuy_DAO chiTietDAO = new ChiTietPhieuHuy_DAO();

		String sql = """
				    SELECT MaPhieuHuy, NgayLapPhieu, MaNhanVien, TongTienHuy, TrangThai
				    FROM PhieuHuy WHERE MaPhieuHuy = ?
				""";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maPhieuHuy);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					LocalDate ngay = rs.getDate("NgayLapPhieu").toLocalDate();
					String maNV = rs.getString("MaNhanVien");
					boolean trangThai = rs.getBoolean("TrangThai");

					// ✅ Dùng timNhanVien() và lấy phần tử đầu tiên
					NhanVien nv = null;
					ArrayList<NhanVien> dsNV = nhanVienDAO.timNhanVien(maNV);
					if (!dsNV.isEmpty())
						nv = dsNV.get(0);

					PhieuHuy ph = new PhieuHuy(maPhieuHuy, ngay, nv, trangThai);
					ph.setChiTietPhieuHuyList(chiTietDAO.timKiemChiTietPhieuHuyBangMa(maPhieuHuy));
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

	/** 🔹 Thêm phiếu huỷ + chi tiết (Transaction) */
	public boolean themPhieuHuy(PhieuHuy ph) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		double tongTien = 0;
		if (ph.getChiTietPhieuHuyList() != null) {
			for (ChiTietPhieuHuy ct : ph.getChiTietPhieuHuyList())
				tongTien += ct.getThanhTien();
		}
		tongTien = Math.round(tongTien * 100.0) / 100.0;

		String sqlPH = "INSERT INTO PhieuHuy (MaPhieuHuy, NgayLapPhieu, MaNhanVien, TongTienHuy, TrangThai) VALUES (?, ?, ?, ?, ?)";
		String sqlCT = "INSERT INTO ChiTietPhieuHuy (MaPhieuHuy, MaLo, SoLuongHuy, LyDoChiTiet, DonGiaNhap, ThanhTien, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";

		try {
			con.setAutoCommit(false);

			// 1️⃣ Thêm header
			try (PreparedStatement ps = con.prepareStatement(sqlPH)) {
				ps.setString(1, ph.getMaPhieuHuy());
				ps.setDate(2, Date.valueOf(ph.getNgayLapPhieu()));
				ps.setString(3, ph.getNhanVien() != null ? ph.getNhanVien().getMaNhanVien() : null);
				ps.setBigDecimal(4, java.math.BigDecimal.valueOf(tongTien));
				ps.setBoolean(5, ph.isTrangThai());
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
					psCT.setInt(7, ct.getTrangThai()); // 1=chờ, 2=đã huỷ, 3=nhập lại kho
					psCT.addBatch();
				}
				psCT.executeBatch();
			}

			con.commit();
			return true;
		} catch (SQLException e) {
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

	/** 🔹 Cập nhật tổng tiền từ chi tiết */
	public boolean capNhatTongTienTheoChiTiet(String maPhieuHuy) {
		PhieuHuy ph = layTheoMa(maPhieuHuy);
		if (ph == null)
			return false;

		double sum = 0;
		if (ph.getChiTietPhieuHuyList() != null)
			for (ChiTietPhieuHuy ct : ph.getChiTietPhieuHuyList())
				sum += ct.getThanhTien();

		sum = Math.round(sum * 100.0) / 100.0;

		connectDB.getInstance();
		Connection con = connectDB.getConnection();
		String sql = "UPDATE PhieuHuy SET TongTienHuy = ? WHERE MaPhieuHuy = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setBigDecimal(1, java.math.BigDecimal.valueOf(sum));
			ps.setString(2, maPhieuHuy);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/** 🔹 Tạo mã tự động PH-yyyyMMdd-xxxx */
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
}
