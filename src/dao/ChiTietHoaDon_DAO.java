package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.connectDB;
import entity.ChiTietHoaDon;
import entity.DonViTinh;
import entity.HoaDon;
import entity.KhuyenMai;
import entity.LoSanPham;
import entity.SanPham;
import enums.HinhThucKM;

public class ChiTietHoaDon_DAO {

	public ChiTietHoaDon_DAO() {
	}

	// ============================================================
	// 🔍 Tìm chi tiết hóa đơn theo mã HD, mã lô và mã đơn vị tính (OPTIMIZED - dùng
	// JOIN)
	// ============================================================
	public ChiTietHoaDon timKiemChiTietHoaDonBangMa(String maHD, String maLo, String maDVT) {
		String sql = """
				SELECT
					ct.MaLo, ct.SoLuong, ct.GiaBan, ct.ThanhTien,
					ct.MaDonViTinh, dvt.TenDonViTinh,
					lo.HanSuDung, lo.SoLuongTon,
					sp.MaSanPham, sp.TenSanPham, sp.GiaNhap,
					km.MaKM, km.TenKM, km.GiaTri, km.HinhThuc
				FROM ChiTietHoaDon ct
				LEFT JOIN DonViTinh dvt ON ct.MaDonViTinh = dvt.MaDonViTinh
				LEFT JOIN LoSanPham lo ON ct.MaLo = lo.MaLo
				LEFT JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
				LEFT JOIN KhuyenMai km ON ct.MaKM = km.MaKM
				WHERE ct.MaHoaDon = ? AND ct.MaLo = ? AND ct.MaDonViTinh = ?
				""";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, maHD);
			ps.setString(2, maLo);
			ps.setString(3, maDVT);
			rs = ps.executeQuery();

			if (rs.next()) {
				// ========== TẠO HÓA ĐƠN ==========
				HoaDon hd = new HoaDon();
				hd.setMaHoaDon(maHD);

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

				// ========== TẠO KHUYẾN MÃI (nếu có) ==========
				KhuyenMai km = null;
				if (rs.getString("MaKM") != null) {
					km = new KhuyenMai();
					km.setMaKM(rs.getString("MaKM"));
					km.setTenKM(rs.getString("TenKM"));
					km.setGiaTri(rs.getDouble("GiaTri"));
					if (rs.getString("HinhThuc") != null) {
						km.setHinhThuc(HinhThucKM.valueOf(rs.getString("HinhThuc")));
					}
				}

				// ========== TẠO CHI TIẾT HÓA ĐƠN ==========
				return new ChiTietHoaDon(hd, lo, rs.getDouble("SoLuong"), dvt, rs.getDouble("GiaBan"), km);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException ignored) {
			}
			try {
				if (ps != null)
					ps.close();
			} catch (SQLException ignored) {
			}
			// ❗ KHÔNG đóng connection (singleton)
		}

		return null;
	}

	// ============================================================
	// 📜 Lấy danh sách chi tiết theo Mã Hóa Đơn (OPTIMIZED - dùng JOIN)
	// ============================================================
	public List<ChiTietHoaDon> layDanhSachChiTietTheoMaHD(String maHD) {
		List<ChiTietHoaDon> ds = new ArrayList<>();

		String sql = """
				SELECT
					ct.MaLo, ct.SoLuong, ct.GiaBan, ct.ThanhTien,
					ct.MaDonViTinh, dvt.TenDonViTinh,
					lo.HanSuDung, lo.SoLuongTon,
					sp.MaSanPham, sp.TenSanPham, sp.GiaNhap,
					km.MaKM, km.TenKM, km.GiaTri, km.HinhThuc
				FROM ChiTietHoaDon ct
				LEFT JOIN DonViTinh dvt ON ct.MaDonViTinh = dvt.MaDonViTinh
				LEFT JOIN LoSanPham lo ON ct.MaLo = lo.MaLo
				LEFT JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
				LEFT JOIN KhuyenMai km ON ct.MaKM = km.MaKM
				WHERE ct.MaHoaDon = ?
				ORDER BY ct.MaLo
				""";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, maHD);
			rs = ps.executeQuery();

			HoaDon hd = new HoaDon();
			hd.setMaHoaDon(maHD);

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

				// ========== TẠO KHUYẾN MÃI (nếu có) ==========
				KhuyenMai km = null;
				if (rs.getString("MaKM") != null) {
					km = new KhuyenMai();
					km.setMaKM(rs.getString("MaKM"));
					km.setTenKM(rs.getString("TenKM"));
					km.setGiaTri(rs.getDouble("GiaTri"));
					if (rs.getString("HinhThuc") != null) {
						km.setHinhThuc(HinhThucKM.valueOf(rs.getString("HinhThuc")));
					}
				}

				// ========== TẠO CHI TIẾT HÓA ĐƠN ==========
				ChiTietHoaDon cthd = new ChiTietHoaDon(hd, lo, rs.getDouble("SoLuong"), dvt, rs.getDouble("GiaBan"),
						km);
				ds.add(cthd);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException ignored) {
			}
			try {
				if (ps != null)
					ps.close();
			} catch (SQLException ignored) {
			}
			// ❗ KHÔNG đóng connection (singleton)
		}

		return ds;
	}

	// ============================================================
	// 📊 Đếm số SP đã bán trong ngày hiện tại của nhân viên
	// ============================================================
	public int demSoSanPhamBanHomNay(String maNhanVien) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = """
				SELECT COUNT(DISTINCT sp.MaSanPham) AS SoSanPham
				FROM ChiTietHoaDon ct
				JOIN HoaDon hd ON ct.MaHoaDon = hd.MaHoaDon
				JOIN LoSanPham lo ON ct.MaLo = lo.MaLo
				JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
				WHERE hd.MaNhanVien = ?
				  AND CAST(hd.NgayLap AS DATE) = CAST(GETDATE() AS DATE)
				""";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, maNhanVien);
			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt("SoSanPham");
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi đếm số SP khác nhau bán hôm nay: " + e.getMessage());
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

		return 0;
	}
}