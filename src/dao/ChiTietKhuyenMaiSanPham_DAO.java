package dao;

import connectDB.connectDB;
import entity.ChiTietKhuyenMaiSanPham;
import entity.KhuyenMai;
import entity.SanPham;
import enums.HinhThucKM;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietKhuyenMaiSanPham_DAO {

	public ChiTietKhuyenMaiSanPham_DAO() {
	}

	// =====================================================
	// 1. LẤY DANH SÁCH SP ÁP DỤNG CHO KM (CƠ BẢN)
	// =====================================================
	public List<ChiTietKhuyenMaiSanPham> timKiemChiTietKhuyenMaiSanPhamBangMa(String maKM) {
		List<ChiTietKhuyenMaiSanPham> ds = new ArrayList<>();
		connectDB.getInstance();
		Connection con = connectDB.getConnection();
		String sql = "SELECT MaSanPham FROM ChiTietKhuyenMaiSanPham WHERE MaKM = ?";
		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maKM);
			try (ResultSet rs = stmt.executeQuery()) {
				SanPham_DAO sanPhamDAO = new SanPham_DAO();
				KhuyenMai_DAO khuyenMaiDAO = new KhuyenMai_DAO();

				while (rs.next()) {
					String maSP = rs.getString("MaSanPham");

					SanPham sp = sanPhamDAO.laySanPhamTheoMa(maSP);
					KhuyenMai km = khuyenMaiDAO.timKhuyenMaiTheoMa(maKM);

					if (sp == null || km == null) {
						System.err.println("Bỏ qua CTKM: không tìm thấy SP hoặc KM (" + maKM + ")");
						continue;
					}
					ds.add(new ChiTietKhuyenMaiSanPham(sp, km));
				}
			}
		} catch (SQLException e) {
			System.err.println("Lỗi lấy chi tiết KM theo mã: " + e.getMessage());
		}
		return ds;
	}

	// =====================================================
	// 2. THÊM SẢN PHẨM VÀO KM
	// =====================================================
	public boolean themChiTietKhuyenMaiSanPham(ChiTietKhuyenMaiSanPham ctkm) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		// Không thêm trùng
		if (daTonTai(ctkm.getKhuyenMai().getMaKM(), ctkm.getSanPham().getMaSanPham())) {
			System.err.println("CTKM đã tồn tại, bỏ qua.");
			return false;
		}

		String sql = "INSERT INTO ChiTietKhuyenMaiSanPham (MaKM, MaSanPham) VALUES (?, ?)";
		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, ctkm.getKhuyenMai().getMaKM());
			stmt.setString(2, ctkm.getSanPham().getMaSanPham());
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Lỗi thêm CTKM: " + e.getMessage());
		}
		return false;
	}

	// =====================================================
	// 3. XOÁ 1 SP RA KHỎI KM
	// =====================================================
	public boolean xoaChiTietKhuyenMaiSanPham(String maKM, String maSP) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();
		String sql = "DELETE FROM ChiTietKhuyenMaiSanPham WHERE MaKM=? AND MaSanPham=?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maKM);
			ps.setString(2, maSP);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Lỗi xoá CTKM: " + e.getMessage());
		}
		return false;
	}

	// =====================================================
	// ⭐ 4. XOÁ TOÀN BỘ SP CỦA 1 KM — DÙNG KHI XOÁ KM
	// =====================================================
	public boolean xoaTatCaSanPhamCuaKM(String maKM) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();
		String sql = "DELETE FROM ChiTietKhuyenMaiSanPham WHERE MaKM=?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maKM);
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.err.println("Lỗi xoá ALL SP của KM: " + e.getMessage());
		}
		return false;
	}

	// =====================================================
	// ⭐ 5. CHECK TỒN TẠI — TRÁNH THÊM TRÙNG
	// =====================================================
	public boolean daTonTai(String maKM, String maSP) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();
		String sql = "SELECT 1 FROM ChiTietKhuyenMaiSanPham WHERE MaKM=? AND MaSanPham=?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maKM);
			ps.setString(2, maSP);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			System.err.println("Lỗi kiểm tra tồn tại CTKM: " + e.getMessage());
		}
		return false;
	}

	// =====================================================
	// 6. LẤY TOÀN BỘ JOIN (SP + KM) CHO GUI TAB 2
	// =====================================================
	public List<ChiTietKhuyenMaiSanPham> layChiTietKhuyenMaiTheoMaCoJoin(String maKM) {
		List<ChiTietKhuyenMaiSanPham> ds = new ArrayList<>();
		connectDB.getInstance();
		Connection con = connectDB.getConnection();
		String sql = """
				    SELECT ctkm.MaKM, ctkm.MaSanPham,
				           sp.TenSanPham, sp.GiaNhap, sp.KeBanSanPham, sp.HoatDong,
				           km.TenKM, km.HinhThuc, km.GiaTri, km.TrangThai
				    FROM ChiTietKhuyenMaiSanPham ctkm
				    JOIN SanPham sp ON ctkm.MaSanPham = sp.MaSanPham
				    JOIN KhuyenMai km ON ctkm.MaKM = km.MaKM
				    WHERE ctkm.MaKM = ?
				""";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maKM);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {

					SanPham sp = new SanPham();
					sp.setMaSanPham(rs.getString("MaSanPham"));
					sp.setTenSanPham(rs.getString("TenSanPham"));
					sp.setGiaNhap(rs.getDouble("GiaNhap"));
					sp.setKeBanSanPham(rs.getString("KeBanSanPham"));
					sp.setHoatDong(rs.getBoolean("HoatDong"));

					KhuyenMai km = new KhuyenMai();
					km.setMaKM(rs.getString("MaKM"));
					km.setTenKM(rs.getString("TenKM"));
					km.setTrangThai(rs.getBoolean("TrangThai"));

					String ht = rs.getString("HinhThuc");
					try {
						km.setHinhThuc(HinhThucKM.valueOf(ht.trim().toUpperCase()));
					} catch (Exception ignored) {
					}

					km.setGiaTri(rs.getDouble("GiaTri"));

					ds.add(new ChiTietKhuyenMaiSanPham(sp, km));
				}
			}

		} catch (SQLException e) {
			System.err.println("Lỗi JOIN CTKM: " + e.getMessage());
		}
		return ds;
	}

	// =====================================================
	// 7. LẤY CTKM ĐANG HOẠT ĐỘNG THEO MÃ SP
	// =====================================================
	public List<ChiTietKhuyenMaiSanPham> layChiTietKhuyenMaiDangHoatDongTheoMaSP(String maSP) {
		List<ChiTietKhuyenMaiSanPham> ds = new ArrayList<>();
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = """
				    SELECT ctkm.MaKM, ctkm.MaSanPham,
				           sp.TenSanPham, km.TenKM,
				           km.HinhThuc, km.GiaTri,
				           km.NgayBatDau, km.NgayKetThuc,
				           km.TrangThai, km.SoLuongKhuyenMai, km.KhuyenMaiHoaDon
				    FROM ChiTietKhuyenMaiSanPham ctkm
				    JOIN SanPham sp ON ctkm.MaSanPham = sp.MaSanPham
				    JOIN KhuyenMai km ON ctkm.MaKM = km.MaKM
				    WHERE ctkm.MaSanPham = ?
				      AND km.TrangThai = 1
				      AND GETDATE() BETWEEN km.NgayBatDau AND km.NgayKetThuc
				      AND km.SoLuongKhuyenMai > 0
				      AND km.KhuyenMaiHoaDon = 0
				""";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maSP);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					SanPham sp = new SanPham();
					sp.setMaSanPham(rs.getString("MaSanPham"));
					sp.setTenSanPham(rs.getString("TenSanPham"));

					KhuyenMai km = new KhuyenMai();
					km.setMaKM(rs.getString("MaKM"));
					km.setTenKM(rs.getString("TenKM"));
					km.setNgayBatDau(rs.getDate("NgayBatDau").toLocalDate());
					km.setNgayKetThuc(rs.getDate("NgayKetThuc").toLocalDate());
					km.setTrangThai(rs.getBoolean("TrangThai"));
					km.setKhuyenMaiHoaDon(rs.getBoolean("KhuyenMaiHoaDon"));

					String ht = rs.getString("HinhThuc");
					try {
						km.setHinhThuc(HinhThucKM.valueOf(ht.trim().toUpperCase()));
					} catch (Exception ignored) {
					}

					km.setGiaTri(rs.getDouble("GiaTri"));
					km.setSoLuongKhuyenMai(rs.getInt("SoLuongKhuyenMai"));

					ds.add(new ChiTietKhuyenMaiSanPham(sp, km));
				}
			}

		} catch (SQLException e) {
			System.err.println("Lỗi lấy CTKM đang hoạt động theo SP: " + e.getMessage());
		}

		return ds;
	}
}
