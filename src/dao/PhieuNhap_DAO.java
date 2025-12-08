package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import database.connectDB;
import entity.ChiTietPhieuNhap;
import entity.LoSanPham;
import entity.NhaCungCap;
import entity.NhanVien;
import entity.PhieuNhap;

public class PhieuNhap_DAO {

	public PhieuNhap_DAO() {
	}

	public List<PhieuNhap> layDanhSachPhieuNhap() {
		List<PhieuNhap> dsPhieuNhap = new ArrayList<>();
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = "SELECT pn.MaPhieuNhap, pn.NgayNhap, pn.TongTien, " + "nv.MaNhanVien, nv.TenNhanVien, "
				+ "ncc.MaNhaCungCap, ncc.TenNhaCungCap " + "FROM PhieuNhap pn "
				+ "JOIN NhanVien nv ON pn.MaNhanVien = nv.MaNhanVien "
				+ "JOIN NhaCungCap ncc ON pn.MaNhaCungCap = ncc.MaNhaCungCap " + "ORDER BY pn.NgayNhap DESC";

		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				NhanVien nv = new NhanVien();
				nv.setMaNhanVien(rs.getString("MaNhanVien"));
				nv.setTenNhanVien(rs.getString("TenNhanVien"));

				NhaCungCap ncc = new NhaCungCap();
				ncc.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
				ncc.setTenNhaCungCap(rs.getString("TenNhaCungCap"));

				PhieuNhap pn = new PhieuNhap();
				pn.setMaPhieuNhap(rs.getString("MaPhieuNhap"));

				pn.setNgayNhap(rs.getDate("NgayNhap").toLocalDate());
				pn.setNhanVien(nv);
				pn.setNhaCungCap(ncc);

				// LẤY TỔNG TIỀN TỪ CSDL - BÂY GIỜ ĐÃ CÓ SETTER
				double tongTien = rs.getDouble("TongTien");
				if (!rs.wasNull()) {
					pn.setTongTien(tongTien);
				} else {
					pn.setTongTien(0.0);
				}

				dsPhieuNhap.add(pn);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dsPhieuNhap;
	}

	public PhieuNhap timPhieuNhapTheoMa(String maPhieuNhap) {
		PhieuNhap pn = null;
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = "SELECT pn.NgayNhap, pn.TongTien, " + "nv.MaNhanVien, nv.TenNhanVien, "
				+ "ncc.MaNhaCungCap, ncc.TenNhaCungCap " + "FROM PhieuNhap pn "
				+ "JOIN NhanVien nv ON pn.MaNhanVien = nv.MaNhanVien "
				+ "JOIN NhaCungCap ncc ON pn.MaNhaCungCap = ncc.MaNhaCungCap " + "WHERE pn.MaPhieuNhap = ?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maPhieuNhap);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					NhanVien nv = new NhanVien();
					nv.setMaNhanVien(rs.getString("MaNhanVien"));
					nv.setTenNhanVien(rs.getString("TenNhanVien"));

					NhaCungCap ncc = new NhaCungCap();
					ncc.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
					ncc.setTenNhaCungCap(rs.getString("TenNhaCungCap"));

					pn = new PhieuNhap();
					pn.setMaPhieuNhap(maPhieuNhap);
					pn.setNgayNhap(rs.getDate("NgayNhap").toLocalDate());
					pn.setNhanVien(nv);
					pn.setNhaCungCap(ncc);

					// Lấy danh sách chi tiết
					ChiTietPhieuNhap_DAO ctpnDAO = new ChiTietPhieuNhap_DAO();
					List<ChiTietPhieuNhap> dsChiTiet = ctpnDAO.timKiemChiTietPhieuNhapBangMa(maPhieuNhap);
					pn.setChiTietPhieuNhapList(dsChiTiet);
					// Entity tự tính tổng tiền khi set list chi tiết
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pn;
	}

	public boolean themPhieuNhap(PhieuNhap pn) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		PreparedStatement stmtPhieuNhap = null;
		PreparedStatement stmtLoSanPham = null;
		PreparedStatement stmtChiTiet = null;

		try {
			con.setAutoCommit(false);

			// 1. Thêm PhieuNhap
			String sqlPhieuNhap = "INSERT INTO PhieuNhap (MaPhieuNhap, NgayNhap, MaNhaCungCap, MaNhanVien, TongTien) "
					+ "VALUES (?, ?, ?, ?, ?)";
			stmtPhieuNhap = con.prepareStatement(sqlPhieuNhap);
			stmtPhieuNhap.setString(1, pn.getMaPhieuNhap());
			stmtPhieuNhap.setDate(2, Date.valueOf(pn.getNgayNhap()));
			stmtPhieuNhap.setString(3, pn.getNhaCungCap().getMaNhaCungCap());
			stmtPhieuNhap.setString(4, pn.getNhanVien().getMaNhanVien());
			stmtPhieuNhap.setDouble(5, pn.getTongTien()); // Lấy từ entity (đã tính từ chi tiết)
			stmtPhieuNhap.executeUpdate();

			// 2. Thêm LoSanPham
			String sqlLoSanPham = "INSERT INTO LoSanPham (MaLo, HanSuDung, SoLuongTon, MaSanPham) "
					+ "VALUES (?, ?, ?, ?)";
			stmtLoSanPham = con.prepareStatement(sqlLoSanPham);

			// 3. Thêm ChiTietPhieuNhap
			String sqlChiTiet = "INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaLo, MaDonViTinh, SoLuongNhap, DonGiaNhap, ThanhTien) "
					+ "VALUES (?, ?, ?, ?, ?, ?)";
			stmtChiTiet = con.prepareStatement(sqlChiTiet);

			for (ChiTietPhieuNhap ctpn : pn.getChiTietPhieuNhapList()) {
				LoSanPham lo = ctpn.getLoSanPham();

				// Batch LoSanPham
				stmtLoSanPham.setString(1, lo.getMaLo());
				stmtLoSanPham.setDate(2, Date.valueOf(lo.getHanSuDung()));
				stmtLoSanPham.setInt(3, lo.getSoLuongTon());
				stmtLoSanPham.setString(4, lo.getSanPham().getMaSanPham());
				stmtLoSanPham.addBatch();

				// Batch ChiTietPhieuNhap
				stmtChiTiet.setString(1, pn.getMaPhieuNhap());
				stmtChiTiet.setString(2, lo.getMaLo());
				stmtChiTiet.setString(3, ctpn.getDonViTinh().getMaDonViTinh());
				stmtChiTiet.setInt(4, ctpn.getSoLuongNhap());
				stmtChiTiet.setDouble(5, ctpn.getDonGiaNhap());
				stmtChiTiet.setDouble(6, ctpn.getThanhTien());
				stmtChiTiet.addBatch();
			}

			stmtLoSanPham.executeBatch();
			stmtChiTiet.executeBatch();

			con.commit();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				if (con != null)
					con.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return false;
		} finally {
			try {
				if (stmtPhieuNhap != null)
					stmtPhieuNhap.close();
				if (stmtLoSanPham != null)
					stmtLoSanPham.close();
				if (stmtChiTiet != null)
					stmtChiTiet.close();
				if (con != null)
					con.setAutoCommit(true);
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Tạo mã phiếu nhập tự động theo định dạng PN-yyyymmdd-xxxx (CHAR(16))
	 */
	public String taoMaPhieuNhap() {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String ngayHomNay = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String maPrefix = "PN-" + ngayHomNay + "-";
		String sql = "SELECT MAX(MaPhieuNhap) FROM PhieuNhap WHERE MaPhieuNhap LIKE ?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maPrefix + "%");

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String maCuoi = rs.getString(1);
					if (maCuoi != null) {
						int soCuoi = Integer.parseInt(maCuoi.substring(maCuoi.length() - 4).trim());
						int soMoi = soCuoi + 1;
						return String.format(maPrefix + "%04d", soMoi);
					}
				}
				return maPrefix + "0001";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return maPrefix + "0001";
		}
	}

	public List<PhieuNhap> timKiemPhieuNhap(String keyword, java.util.Date tuNgay, java.util.Date denNgay) {
		List<PhieuNhap> dsPhieuNhap = new ArrayList<>();
		connectDB.getInstance();
		Connection con = connectDB.getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		String sql = "SELECT pn.MaPhieuNhap, pn.NgayNhap, pn.TongTien, " + "nv.MaNhanVien, nv.TenNhanVien, "
				+ "ncc.MaNhaCungCap, ncc.TenNhaCungCap " + "FROM PhieuNhap pn "
				+ "JOIN NhanVien nv ON pn.MaNhanVien = nv.MaNhanVien "
				+ "JOIN NhaCungCap ncc ON pn.MaNhaCungCap = ncc.MaNhaCungCap "
				+ "WHERE (pn.MaPhieuNhap LIKE ? OR ncc.TenNhaCungCap LIKE ? OR nv.TenNhanVien LIKE ?) "
				+ "AND pn.NgayNhap BETWEEN ? AND ?";

		try {
			stmt = con.prepareStatement(sql);
			String keywordParam = "%" + keyword + "%";
			stmt.setString(1, keywordParam);
			stmt.setString(2, keywordParam);
			stmt.setString(3, keywordParam);
			stmt.setDate(4, new java.sql.Date(tuNgay.getTime()));
			stmt.setDate(5, new java.sql.Date(denNgay.getTime()));

			rs = stmt.executeQuery();

			while (rs.next()) {
				NhanVien nv = new NhanVien();
				nv.setMaNhanVien(rs.getString("MaNhanVien"));
				nv.setTenNhanVien(rs.getString("TenNhanVien"));

				NhaCungCap ncc = new NhaCungCap();
				ncc.setMaNhaCungCap(rs.getString("MaNhaCungCap"));
				ncc.setTenNhaCungCap(rs.getString("TenNhaCungCap"));

				PhieuNhap pn = new PhieuNhap();
				pn.setMaPhieuNhap(rs.getString("MaPhieuNhap"));
				pn.setNgayNhap(rs.getDate("NgayNhap").toLocalDate());
				pn.setNhanVien(nv);
				pn.setNhaCungCap(ncc);

				// LẤY TỔNG TIỀN TỪ CSDL - BÂY GIỜ AN TOÀN
				double tongTien = rs.getDouble("TongTien");
				if (!rs.wasNull()) {
					pn.setTongTien(tongTien);
				} else {
					pn.setTongTien(0.0);
				}

				dsPhieuNhap.add(pn);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dsPhieuNhap;
	}

	public List<PhieuNhap> layPhieuNhapTheoNhaCungCap(String maNCC) {
		List<PhieuNhap> dsPhieuNhap = new ArrayList<>();
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = "SELECT pn.MaPhieuNhap, pn.NgayNhap, pn.TongTien, " + "nv.MaNhanVien, nv.TenNhanVien "
				+ "FROM PhieuNhap pn " + "JOIN NhanVien nv ON pn.MaNhanVien = nv.MaNhanVien "
				+ "WHERE pn.MaNhaCungCap = ? " + "ORDER BY pn.NgayNhap DESC";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maNCC);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					NhanVien nv = new NhanVien();
					nv.setMaNhanVien(rs.getString("MaNhanVien"));
					nv.setTenNhanVien(rs.getString("TenNhanVien"));

					PhieuNhap pn = new PhieuNhap();
					pn.setMaPhieuNhap(rs.getString("MaPhieuNhap"));
					pn.setNgayNhap(rs.getDate("NgayNhap").toLocalDate());
					pn.setNhanVien(nv);
					pn.setTongTien(rs.getDouble("TongTien"));

					dsPhieuNhap.add(pn);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dsPhieuNhap;
	}

}