package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import database.connectDB;
import entity.BangGia;
import entity.ChiTietBangGia;
import entity.ChiTietKhuyenMaiSanPham;
import entity.SanPham;
import enums.LoaiSanPham;
import enums.DuongDung;

public class SanPham_DAO {

	// Thêm các DAO dependency
	private BangGia_DAO bangGiaDAO;
	private ChiTietBangGia_DAO chiTietBangGiaDAO;
	private ChiTietKhuyenMaiSanPham_DAO chiTietKM_DAO; // 💡 Dependency mới

	public SanPham_DAO() {
		// Khởi tạo các DAO cần thiết để lấy thông tin giá bán và khuyến mãi
		bangGiaDAO = new BangGia_DAO();
		chiTietBangGiaDAO = new ChiTietBangGia_DAO();
		chiTietKM_DAO = new ChiTietKhuyenMaiSanPham_DAO(); // 💡 Khởi tạo
	}

	/** 🔹 Lấy toàn bộ sản phẩm */
	public ArrayList<SanPham> layTatCaSanPham() {
		ArrayList<SanPham> danhSach = new ArrayList<>();
		connectDB.getInstance();
		Connection con = connectDB.getConnection();
		String sql = "SELECT * FROM SanPham";

		try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) {
				danhSach.add(taoSanPhamTuResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi lấy danh sách sản phẩm: " + e.getMessage());
		}
		return danhSach;
	}

	/** 🔹 Thêm sản phẩm mới */
	public boolean themSanPham(SanPham sp) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();
		String sql = """
				    INSERT INTO SanPham (MaSanPham, TenSanPham, LoaiSanPham, SoDangKy, DuongDung,
				                         GiaNhap, GiaBan, HinhAnh, KeBanSanPham, HoatDong)
				    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
				""";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ganGiaTriChoPreparedStatement(ps, sp);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("❌ Lỗi thêm sản phẩm: " + e.getMessage());
		}
		return false;
	}

	/** 🔹 Cập nhật thông tin sản phẩm */
	public boolean capNhatSanPham(SanPham sp) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();
		String sql = """
				    UPDATE SanPham
				    SET TenSanPham=?, LoaiSanPham=?, SoDangKy=?, DuongDung=?,
				        GiaNhap=?, GiaBan=?, HinhAnh=?, KeBanSanPham=?, HoatDong=?
				    WHERE MaSanPham=?
				""";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, sp.getTenSanPham());
			ps.setString(2, sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().name() : null);
			ps.setString(3, sp.getSoDangKy());
			ps.setString(4, sp.getDuongDung() != null ? sp.getDuongDung().name() : null);
			ps.setDouble(5, sp.getGiaNhap());

			double giaBan = 0;
			try {
				giaBan = sp.getGiaBan();
			} catch (Exception ignored) {
			}
			ps.setDouble(6, giaBan);

			ps.setString(7, sp.getHinhAnh());
			ps.setString(8, sp.getKeBanSanPham());
			ps.setBoolean(9, sp.isHoatDong());
			ps.setString(10, sp.getMaSanPham());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("❌ Lỗi cập nhật sản phẩm: " + e.getMessage());
		}
		return false;
	}

	/** 🔹 Xóa sản phẩm */
	public boolean xoaSanPham(String maSanPham) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();
		String sql = "DELETE FROM SanPham WHERE MaSanPham=?";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maSanPham);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("❌ Lỗi xóa sản phẩm: " + e.getMessage());
		}
		return false;
	}

	/** 🔹 Lấy sản phẩm theo mã */
	public SanPham laySanPhamTheoMa(String maSanPham) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();
		String sql = "SELECT * FROM SanPham WHERE MaSanPham=?";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maSanPham);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return taoSanPhamTuResultSet(rs);
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi lấy sản phẩm theo mã: " + e.getMessage());
		}
		return null;
	}

	/** 🔹 🔍 Tìm sản phẩm chính xác theo số đăng ký (SoDangKy) */
	public SanPham timSanPhamTheoSoDangKy(String soDangKy) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();
		String sql = "SELECT * FROM SanPham WHERE SoDangKy = ?";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, soDangKy);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return taoSanPhamTuResultSet(rs);
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi tìm sản phẩm theo số đăng ký: " + e.getMessage());
		}
		return null;
	}

	/** 🔹 Tìm kiếm sản phẩm theo mã / tên / số đăng ký (LIKE gần đúng) */
	public ArrayList<SanPham> timKiemSanPham(String tuKhoa) {
		ArrayList<SanPham> ds = new ArrayList<>();
		connectDB.getInstance();
		Connection con = connectDB.getConnection();
		String sql = """
				    SELECT * FROM SanPham
				    WHERE MaSanPham LIKE ?
				        OR TenSanPham LIKE ?
				        OR SoDangKy LIKE ?
				""";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			String key = "%" + tuKhoa.trim() + "%";
			ps.setString(1, key);
			ps.setString(2, key);
			ps.setString(3, key);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					ds.add(taoSanPhamTuResultSet(rs));
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi tìm kiếm sản phẩm: " + e.getMessage());
		}
		return ds;
	}

	/** 🔹 Lấy danh sách sản phẩm theo loại */
	public ArrayList<SanPham> laySanPhamTheoLoai(LoaiSanPham loaiSP) {
		ArrayList<SanPham> ds = new ArrayList<>();
		connectDB.getInstance();
		Connection con = connectDB.getConnection();
		String sql = "SELECT * FROM SanPham WHERE LoaiSanPham=?";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, loaiSP.name());
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					ds.add(taoSanPhamTuResultSet(rs));
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi lấy sản phẩm theo loại: " + e.getMessage());
		}
		return ds;
	}

	// 💡 PHƯƠNG THỨC TIỆN ÍCH DÙNG CHO CÁC LỚP KHÁC
	/** 🔹 Lấy danh sách chi tiết khuyến mãi đang áp dụng cho một sản phẩm */
	public List<ChiTietKhuyenMaiSanPham> layKhuyenMaiDangApDungChoSanPham(String maSanPham) {
		return chiTietKM_DAO.layChiTietKhuyenMaiDangHoatDongTheoMaSP(maSanPham);
	}

	/** 🔹 Hàm tiện ích: tạo SanPham từ ResultSet (ĐÃ CẬP NHẬT để lấy KM) */
	private SanPham taoSanPhamTuResultSet(ResultSet rs) throws SQLException {
		LoaiSanPham loai = null;
		String loaiStr = rs.getString("LoaiSanPham");
		if (loaiStr != null) {
			try {
				loai = LoaiSanPham.valueOf(loaiStr.trim().toUpperCase());
			} catch (Exception ignore) {
			}
		}

		DuongDung duongDung = null;
		String ddStr = rs.getString("DuongDung");
		if (ddStr != null) {
			try {
				duongDung = DuongDung.valueOf(ddStr.trim().toUpperCase());
			} catch (Exception ignore) {
			}
		}

		SanPham sp = new SanPham(rs.getString("MaSanPham"), rs.getString("TenSanPham"), loai, rs.getString("SoDangKy"),
				duongDung, rs.getDouble("GiaNhap"), rs.getString("HinhAnh"), rs.getString("KeBanSanPham"),
				rs.getBoolean("HoatDong"));

		// 💡 LẤY THÔNG TIN GIÁ BÁN THEO KHOẢNG GIÁ
		try {
			// 1. Lấy bảng giá đang hoạt động
			BangGia bgActive = bangGiaDAO.layBangGiaDangHoatDong();

			if (bgActive != null) {
				double giaNhap = sp.getGiaNhap();

				// 2. Tìm chi tiết bảng giá bằng cách so khớp khoảng giá nhập
				ChiTietBangGia ctbg = chiTietBangGiaDAO.timChiTietTheoKhoangGia(bgActive.getMaBangGia(), giaNhap);

				if (ctbg != null) {
					// 3. Gán chi tiết bảng giá, kích hoạt tính toán giaBan trong entity
					sp.setChiTietBangGiaHienTai(ctbg);
				}
			}
		} catch (Exception e) {
			System.err.println("❌ Lỗi thiết lập giá bán cho sản phẩm " + sp.getMaSanPham() + ": " + e.getMessage());
		}

		// ====================================================================
		// 💡 BỔ SUNG LOGIC: LẤY KHUYẾN MÃI ĐANG ÁP DỤNG VÀ GÁN VÀO SẢN PHẨM
		try {
			List<ChiTietKhuyenMaiSanPham> dsKM = chiTietKM_DAO
					.layChiTietKhuyenMaiDangHoatDongTheoMaSP(sp.getMaSanPham());

			// Giả định: Nếu tìm thấy, lấy khuyến mãi đầu tiên và gán vào SanPham
			if (dsKM != null && !dsKM.isEmpty()) {
				ChiTietKhuyenMaiSanPham kmHienTai = dsKM.get(0);

				// ⚠️ Dòng code này yêu cầu SanPham.java có phương thức setKhuyenMaiHienTai()
				// sp.setKhuyenMaiHienTai(kmHienTai);
			}
		} catch (Exception e) {
			System.err.println("❌ Lỗi thiết lập khuyến mãi cho sản phẩm " + sp.getMaSanPham() + ": " + e.getMessage());
		}
		// ====================================================================

		return sp;
	}

	/** 🔹 Hàm tiện ích: gán giá trị cho PreparedStatement (thêm) */
	private void ganGiaTriChoPreparedStatement(PreparedStatement ps, SanPham sp) throws SQLException {
		ps.setString(1, sp.getMaSanPham());
		ps.setString(2, sp.getTenSanPham());
		ps.setString(3, sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().name() : null);
		ps.setString(4, sp.getSoDangKy());
		ps.setString(5, sp.getDuongDung() != null ? sp.getDuongDung().name() : null);
		ps.setDouble(6, sp.getGiaNhap());
		double giaBan = 0;
		try {
			giaBan = sp.getGiaBan();
		} catch (Exception ignored) {
		}
		ps.setDouble(7, giaBan);
		ps.setString(8, sp.getHinhAnh());
		ps.setString(9, sp.getKeBanSanPham());
		ps.setBoolean(10, sp.isHoatDong());
	}

	public Map<String, Object[]> thongKeSanPhamTheoNCC(String maNCC) {
		Map<String, Object[]> result = new LinkedHashMap<>();
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = "SELECT sp.MaSanPham, sp.TenSanPham, sp.LoaiSanPham, "
				+ "COUNT(DISTINCT pn.MaPhieuNhap) AS SoLanNhap, " + "SUM(ct.SoLuongNhap) AS TongSoLuong "
				+ "FROM PhieuNhap pn " + "JOIN ChiTietPhieuNhap ct ON pn.MaPhieuNhap = ct.MaPhieuNhap "
				+ "JOIN LoSanPham lo ON ct.MaLo = lo.MaLo " + "JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham "
				+ "WHERE pn.MaNhaCungCap = ? " + "GROUP BY sp.MaSanPham, sp.TenSanPham, sp.LoaiSanPham "
				+ "ORDER BY TongSoLuong DESC";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maNCC);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String maSP = rs.getString("MaSanPham");
					result.put(maSP, new Object[] { rs.getString("TenSanPham"), rs.getString("LoaiSanPham"),
							rs.getInt("SoLanNhap"), rs.getInt("TongSoLuong") });
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

}