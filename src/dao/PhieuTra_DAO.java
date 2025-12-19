package dao;

import entity.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import database.connectDB;

public class PhieuTra_DAO {

	// CACHE LAYER
	private static List<PhieuTra> cacheAllPhieuTra = null;

	public PhieuTra_DAO() {
	}

	// ============================================================
	// 🔍 Tìm phiếu theo mã (OPTIMIZED - dùng JOIN)
	// ============================================================
	public PhieuTra timKiemPhieuTraBangMa(String maPhieuTra) {

		String sql = """
				SELECT
					pt.MaPhieuTra, pt.NgayLap, pt.DaDuyet, pt.TongTienHoan,
					nv.MaNhanVien, nv.TenNhanVien, nv.QuanLy, nv.CaLam,
					kh.MaKhachHang, kh.TenKhachHang, kh.GioiTinh, kh.SoDienThoai, kh.NgaySinh, kh.HoatDong
				FROM PhieuTra pt
				JOIN NhanVien nv ON pt.MaNhanVien = nv.MaNhanVien
				JOIN KhachHang kh ON pt.MaKhachHang = kh.MaKhachHang
				WHERE pt.MaPhieuTra = ?
				""";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, maPhieuTra);
			rs = ps.executeQuery();

			if (rs.next()) {
				// ========== TẠO NHANVIEN TỪ RESULTSET ==========
				NhanVien nv = new NhanVien();
				nv.setMaNhanVien(rs.getString("MaNhanVien"));
				nv.setTenNhanVien(rs.getString("TenNhanVien"));
				nv.setQuanLy(rs.getBoolean("QuanLy"));
				nv.setCaLam(rs.getInt("CaLam"));

				// ========== TẠO KHACHHANG TỪ RESULTSET ==========
				KhachHang kh = new KhachHang();
				kh.setMaKhachHang(rs.getString("MaKhachHang"));
				kh.setTenKhachHang(rs.getString("TenKhachHang"));
				kh.setGioiTinh(rs.getBoolean("GioiTinh"));
				kh.setSoDienThoai(rs.getString("SoDienThoai"));
				java.sql.Date ngaySinhKH = rs.getDate("NgaySinh");
				if (ngaySinhKH != null) {
					kh.setNgaySinh(ngaySinhKH.toLocalDate());
				}
				kh.setHoatDong(rs.getBoolean("HoatDong"));

				// ========== TẠO PHIEUTRA TỪ RESULTSET ==========
				LocalDate ngayLap = rs.getDate("NgayLap").toLocalDate();
				boolean daDuyet = rs.getBoolean("DaDuyet");

				// Đóng rs, ps trước khi gọi layChiTietPhieuTra
				rs.close();
				ps.close();

				// ========== LẤY CHI TIẾT PHIẾU TRẢ ==========
				List<ChiTietPhieuTra> dsCT = layChiTietPhieuTra(maPhieuTra);

				return new PhieuTra(maPhieuTra, kh, nv, ngayLap, daDuyet, dsCT);
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

		return null;
	}

	// ============================================================
	// 📜 Lấy chi tiết phiếu trả (dùng JOIN - tránh gọi DAO khác)
	// ============================================================
	private List<ChiTietPhieuTra> layChiTietPhieuTra(String maPhieuTra) {
		List<ChiTietPhieuTra> ds = new ArrayList<>();

		String sql = """
				SELECT
				    ctp.MaHoaDon, ctp.MaLo, ctp.SoLuong, ctp.ThanhTienHoan,
				    ctp.LyDoChiTiet, ctp.TrangThai,
				    ctp.MaDonViTinh AS MaDonViTinhCT,

				    -- ChiTietHoaDon
				    cthd.GiaBan, cthd.SoLuong AS SoLuongHD,
				    cthd.MaDonViTinh AS MaDonViTinhHD, cthd.MaKM, cthd.ThanhTien AS ThanhTienHD,

				    -- LoSanPham
				    lo.HanSuDung, lo.SoLuongTon,
				    sp.MaSanPham, sp.TenSanPham,

				    -- DonViTinh
				    dvt.TenDonViTinh,

				    -- KhuyenMai
				    km.TenKM, km.GiaTri, km.HinhThuc
				FROM ChiTietPhieuTra ctp
				LEFT JOIN ChiTietHoaDon cthd
				    ON  ctp.MaHoaDon   = cthd.MaHoaDon
				    AND ctp.MaLo       = cthd.MaLo
				    AND ctp.MaDonViTinh = cthd.MaDonViTinh
				LEFT JOIN LoSanPham lo
				    ON lo.MaLo = ctp.MaLo
				LEFT JOIN SanPham sp
				    ON sp.MaSanPham = lo.MaSanPham
				LEFT JOIN DonViTinh dvt
				    ON dvt.MaDonViTinh = ctp.MaDonViTinh
				LEFT JOIN KhuyenMai km
				    ON km.MaKM = cthd.MaKM
				WHERE ctp.MaPhieuTra = ?
				ORDER BY ctp.MaLo
				""";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, maPhieuTra);
			rs = ps.executeQuery();

			while (rs.next()) {

				// ========== TẠO HÓA ĐƠN ==========
				HoaDon hd = new HoaDon();
				hd.setMaHoaDon(rs.getString("MaHoaDon"));

				// ========== TẠO SẢN PHẨM ==========
				SanPham sp = null;
				if (rs.getString("MaSanPham") != null) {
					sp = new SanPham();
					sp.setMaSanPham(rs.getString("MaSanPham"));
					sp.setTenSanPham(rs.getString("TenSanPham"));
				}

				// ========== TẠO LÔ ==========
				LoSanPham lo = new LoSanPham();
				lo.setMaLo(rs.getString("MaLo"));
				if (rs.getDate("HanSuDung") != null)
					lo.setHanSuDung(rs.getDate("HanSuDung").toLocalDate());
				lo.setSoLuongTon(rs.getInt("SoLuongTon"));
				lo.setSanPham(sp);

				// ========== ĐƠN VỊ TÍNH ==========
				DonViTinh dvt = null;
				if (rs.getString("MaDonViTinhCT") != null) {
					dvt = new DonViTinh();
					dvt.setMaDonViTinh(rs.getString("MaDonViTinhCT"));
					dvt.setTenDonViTinh(rs.getString("TenDonViTinh"));
				}
				// ========== KHUYẾN MÃI ==========
				KhuyenMai km = null;
				if (rs.getString("MaKM") != null) {
					km = new KhuyenMai();
					km.setMaKM(rs.getString("MaKM"));
					km.setTenKM(rs.getString("TenKM"));
					km.setGiaTri(rs.getDouble("GiaTri"));
					km.setHinhThuc(enums.HinhThucKM.valueOf(rs.getString("HinhThuc")));
				}

				// ========== ChiTietHoaDon ==========
				ChiTietHoaDon cthd = new ChiTietHoaDon(hd, lo, rs.getInt("SoLuongHD"), dvt, rs.getDouble("GiaBan"),
						km);

				// ========== Phiếu trả ==========
				PhieuTra pt = new PhieuTra();
				pt.setMaPhieuTra(maPhieuTra);

				// ========== ChiTietPhieuTra ==========
				ChiTietPhieuTra ctpt = new ChiTietPhieuTra(pt, cthd, rs.getString("LyDoChiTiet"),
						rs.getInt("SoLuong"), rs.getInt("TrangThai"));
				ctpt.setDonViTinh(dvt);
				ds.add(ctpt);
			}

		} catch (Exception e) {
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

		return ds;
	}

	// ============================================================
	// 🔔 Đếm số phiếu trả chưa duyệt (cho Dashboard)
	// ============================================================
	public int demPhieuTraChuaDuyet() {
		String sql = "SELECT COUNT(*) AS SoLuong FROM PhieuTra WHERE DaDuyet = 0";

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
			System.err.println("❌ Lỗi đếm phiếu trả chưa duyệt: " + e.getMessage());
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
		}

		return 0;
	}

	/**
	 * Tính tổng tiền trả hàng theo tháng (cho biểu đồ)
	 * 
	 * @param thang Tháng (1-12)
	 * @param nam   Năm
	 * @return Tổng tiền đã hoàn trả
	 */
	public double tinhTongTienTraTheoThang(int thang, int nam) {
		String sql = """
				SELECT COALESCE(SUM(TongTienHoan), 0) AS TongTienTra
				FROM PhieuTra
				WHERE MONTH(NgayLap) = ? AND YEAR(NgayLap) = ?
				""";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setInt(1, thang);
			ps.setInt(2, nam);
			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getDouble("TongTienTra");
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi tính tổng tiền trả theo tháng: " + e.getMessage());
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

		return 0;
	}

	// ============================================================
	// 📜 Lấy tất cả phiếu trả (OPTIMIZED - dùng JOIN, CÓ CACHE)
	// ============================================================
	public List<PhieuTra> layTatCaPhieuTra() {
		// 1. Kiểm tra cache
		if (cacheAllPhieuTra != null && !cacheAllPhieuTra.isEmpty()) {
			return new ArrayList<>(cacheAllPhieuTra);
		}

		// 2. Nếu không có cache -> Query DB với JOIN
		List<PhieuTra> ketQua = new ArrayList<>();

		// 2.1. Lấy danh sách phiếu trả với thông tin NhanVien, KhachHang
		String sql = """
				SELECT
					pt.MaPhieuTra, pt.NgayLap, pt.DaDuyet, pt.TongTienHoan,
					nv.MaNhanVien, nv.TenNhanVien, nv.QuanLy, nv.CaLam,
					kh.MaKhachHang, kh.TenKhachHang, kh.GioiTinh, kh.SoDienThoai, kh.NgaySinh, kh.HoatDong
				FROM PhieuTra pt
				JOIN NhanVien nv ON pt.MaNhanVien = nv.MaNhanVien
				JOIN KhachHang kh ON pt.MaKhachHang = kh.MaKhachHang
				ORDER BY pt.NgayLap DESC, pt.MaPhieuTra DESC
				""";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Tạm lưu danh sách phiếu trả (chưa có chi tiết)
		List<PhieuTraTemp> tempList = new ArrayList<>();

		try {
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();

			while (rs.next()) {
				// ========== TẠO NHANVIEN TỪ RESULTSET ==========
				NhanVien nv = new NhanVien();
				nv.setMaNhanVien(rs.getString("MaNhanVien"));
				nv.setTenNhanVien(rs.getString("TenNhanVien"));
				nv.setQuanLy(rs.getBoolean("QuanLy"));
				nv.setCaLam(rs.getInt("CaLam"));

				// ========== TẠO KHACHHANG TỪ RESULTSET ==========
				KhachHang kh = new KhachHang();
				kh.setMaKhachHang(rs.getString("MaKhachHang"));
				kh.setTenKhachHang(rs.getString("TenKhachHang"));
				kh.setGioiTinh(rs.getBoolean("GioiTinh"));
				kh.setSoDienThoai(rs.getString("SoDienThoai"));
				java.sql.Date ngaySinhKH = rs.getDate("NgaySinh");
				if (ngaySinhKH != null) {
					kh.setNgaySinh(ngaySinhKH.toLocalDate());
				}
				kh.setHoatDong(rs.getBoolean("HoatDong"));

				// ========== LƯU TẠM ==========
				PhieuTraTemp temp = new PhieuTraTemp();
				temp.maPT = rs.getString("MaPhieuTra");
				temp.ngayLap = rs.getDate("NgayLap").toLocalDate();
				temp.daDuyet = rs.getBoolean("DaDuyet");
				temp.nv = nv;
				temp.kh = kh;
				tempList.add(temp);
			}

		} catch (Exception e) {
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

		// 2.2. Sau khi đóng ResultSet, lấy chi tiết cho từng phiếu
		for (PhieuTraTemp temp : tempList) {
			List<ChiTietPhieuTra> dsCT = layChiTietPhieuTra(temp.maPT);
			PhieuTra pt = new PhieuTra(temp.maPT, temp.kh, temp.nv, temp.ngayLap, temp.daDuyet, dsCT);
			ketQua.add(pt);
		}

		// 3. Lưu vào cache
		cacheAllPhieuTra = ketQua;

		return new ArrayList<>(ketQua);
	}

	// Class tạm để lưu thông tin phiếu trả
	private static class PhieuTraTemp {
		String maPT;
		LocalDate ngayLap;
		boolean daDuyet;
		NhanVien nv;
		KhachHang kh;
	}

	// ============================================================
	// ➕ Thêm phiếu trả và chi tiết
	// ============================================================
	public boolean themPhieuTraVaChiTiet(PhieuTra pt, List<ChiTietPhieuTra> dsChiTiet) {
		boolean ok = false;

		String sqlPT = """
						INSERT INTO PhieuTra(maPhieuTra, maNhanVien, maKhachHang, ngayLap, tongTienHoan, daDuyet)
						VALUES (?, ?, ?, ?, ?, ?)
				""";

		String sqlCT = """
					    	INSERT INTO ChiTietPhieuTra(maPhieuTra, maHoaDon, maLo, soLuong, thanhTienHoan, lyDoChiTiet, trangThai, MaDonViTinh)
					    	VALUES (?, ?, ?, ?, ?, ?, ?, ?)
				""";

		Connection con = connectDB.getConnection();
		PreparedStatement psPT = null;
		PreparedStatement psCT = null;

		try {
			con.setAutoCommit(false);

			psPT = con.prepareStatement(sqlPT);
			psCT = con.prepareStatement(sqlCT);

			// Insert phiếu trả
			psPT.setString(1, pt.getMaPhieuTra());
			psPT.setString(2, pt.getNhanVien().getMaNhanVien());
			psPT.setString(3, pt.getKhachHang().getMaKhachHang());
			psPT.setDate(4, java.sql.Date.valueOf(pt.getNgayLap()));
			psPT.setDouble(5, pt.getTongTienHoan());
			psPT.setBoolean(6, pt.isTrangThai());
			psPT.executeUpdate();

			// Insert chi tiết
			for (ChiTietPhieuTra ct : dsChiTiet) {
				psCT.setString(1, pt.getMaPhieuTra());
				psCT.setString(2, ct.getChiTietHoaDon().getHoaDon().getMaHoaDon());
				psCT.setString(3, ct.getChiTietHoaDon().getLoSanPham().getMaLo());
				psCT.setInt(4, ct.getSoLuong());
				psCT.setDouble(5, ct.getThanhTienHoan());
				psCT.setString(6, ct.getLyDoChiTiet());
				psCT.setInt(7, ct.getTrangThai());

				// ✅ DVT đang chọn khi trả (set ở GUI)
				if (ct.getDonViTinh() != null) {
					psCT.setString(8, ct.getDonViTinh().getMaDonViTinh());
				} else {
					// fallback: vẫn dùng DVT trên hóa đơn nếu chưa set
					psCT.setString(8, ct.getChiTietHoaDon().getDonViTinh().getMaDonViTinh());
				}

				psCT.addBatch();
			}

			psCT.executeBatch();
			con.commit();
			ok = true;

			// ✅ Update Cache: Thêm vào đầu danh sách
			if (cacheAllPhieuTra != null) {
				cacheAllPhieuTra.add(0, pt);
			}

		} catch (Exception e) {
			e.printStackTrace();
			try {
				con.rollback();
			} catch (Exception ignored) {
			}
		} finally {
			try {
				if (psPT != null)
					psPT.close();
			} catch (Exception ignored) {
			}
			try {
				if (psCT != null)
					psCT.close();
			} catch (Exception ignored) {
			}
			try {
				con.setAutoCommit(true);
			} catch (Exception ignored) {
			}
		}

		return ok;
	}

	// ============================================================
	// 🔄 Cập nhật trạng thái (transaction)
	// ============================================================
	public String capNhatTrangThai_GiaoDich(String maPhieuTra, String maHoaDon, String maLo, String maDonViTinh,
			NhanVien nv, int trangThaiMoi) {

		Connection con = connectDB.getConnection();
		String maPhieuHuyDuocTao = null;

		try {
			con.setAutoCommit(false);

			// =====================================================
			// 1. Lấy trạng thái cũ + số lượng + lý do (ĐÃ FIX)
			// =====================================================
			String sqlGetOld = """
					        SELECT TrangThai, SoLuong, LyDoChiTiet
					        FROM ChiTietPhieuTra
					        WHERE MaPhieuTra=? AND MaHoaDon=? AND MaLo=? AND MaDonViTinh=?
					""";

			int trangThaiCu = 0;
			int soLuongTra = 0;

			try (PreparedStatement ps = con.prepareStatement(sqlGetOld)) {

				ps.setString(1, maPhieuTra);
				ps.setString(2, maHoaDon);
				ps.setString(3, maLo);
				ps.setString(4, maDonViTinh);

				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						trangThaiCu = rs.getInt("TrangThai");
						soLuongTra = rs.getInt("SoLuong");
					}
				}
			}

			// =====================================================
			// 2. Chặn đổi từ HỦY sang trạng thái khác
			// =====================================================
			if (trangThaiCu == 2 && trangThaiMoi != 2) {
				con.rollback();
				return "ERR";
			}

			// =====================================================
			// 3. Tính delta thay đổi tồn kho
			// =====================================================
			int delta = 0;

			if (trangThaiCu != trangThaiMoi) {

				// 0 → 1: nhập kho
				if (trangThaiCu == 0 && trangThaiMoi == 1)
					delta = +soLuongTra;

				// 1 → 0: trả lại trạng thái chờ → giảm tồn
				if (trangThaiCu == 1 && trangThaiMoi == 0)
					delta = -soLuongTra;

				// 1 → 2: từ nhập kho sang hủy → giảm tồn
				if (trangThaiCu == 1 && trangThaiMoi == 2)
					delta = -soLuongTra;

				// ❗ 2 → 1 KHÔNG HỢP LỆ (GUI cũng không cho)
				// đoạn cũ của bạn "+soLuong" bị sai → loại bỏ
			}

			// =====================================================
			// 4. Update tồn kho nếu có delta
			// =====================================================
			if (delta != 0) {
				String sqlUpdTon = """
						        UPDATE LoSanPham SET SoLuongTon = SoLuongTon + ?
						        WHERE MaLo = ?
						""";
				try (PreparedStatement ps = con.prepareStatement(sqlUpdTon)) {
					ps.setInt(1, delta);
					ps.setString(2, maLo);
					ps.executeUpdate();
				}
			}

			// =====================================================
			// 5. Update trạng thái chi tiết
			// =====================================================
			String sqlUpdCT = """
					        UPDATE ChiTietPhieuTra
					        SET TrangThai = ?
					        WHERE MaPhieuTra=? AND MaHoaDon=? AND MaLo=? AND MaDonViTinh=?
					""";

			try (PreparedStatement ps = con.prepareStatement(sqlUpdCT)) {
				ps.setInt(1, trangThaiMoi);
				ps.setString(2, maPhieuTra);
				ps.setString(3, maHoaDon);
				ps.setString(4, maLo);
				ps.setString(5, maDonViTinh);
				ps.executeUpdate();
			}
			// =====================================================
			// 6. Nếu chuyển sang HỦY → tạo/nhóm phiếu hủy tự động
			// =====================================================
			if (trangThaiMoi == 2 && trangThaiCu != 2) {

				// ⭐ 6.1. Lấy thông tin lô, sản phẩm, đơn vị tính - TRỰC TIẾP trong connection
				// này
				LoSanPham lo = null;
				DonViTinh dvt = null;
				double donGiaNhap = 0;
				String lyDoHuy = "";

				String sqlInfo = """
							SELECT
								lo.MaLo, lo.HanSuDung, lo.SoLuongTon, lo.MaSanPham,
								sp.TenSanPham, sp.GiaNhap,
								dvt.MaDonViTinh, dvt.TenDonViTinh,
								ctp.LyDoChiTiet
							FROM LoSanPham lo
							LEFT JOIN SanPham sp ON sp.MaSanPham = lo.MaSanPham
							LEFT JOIN DonViTinh dvt ON dvt.MaDonViTinh = ?
							LEFT JOIN ChiTietPhieuTra ctp ON ctp.MaPhieuTra = ? AND ctp.MaHoaDon = ? AND ctp.MaLo = ? AND ctp.MaDonViTinh = ?
							WHERE lo.MaLo = ?
						""";

				try (PreparedStatement ps = con.prepareStatement(sqlInfo)) {
					ps.setString(1, maDonViTinh);
					ps.setString(2, maPhieuTra);
					ps.setString(3, maHoaDon);
					ps.setString(4, maLo);
					ps.setString(5, maDonViTinh);
					ps.setString(6, maLo);
					try (ResultSet rs = ps.executeQuery()) {
						if (rs.next()) {
							// Tạo LoSanPham
							lo = new LoSanPham();
							lo.setMaLo(rs.getString("MaLo"));
							if (rs.getDate("HanSuDung") != null)
								lo.setHanSuDung(rs.getDate("HanSuDung").toLocalDate());
							lo.setSoLuongTon(rs.getInt("SoLuongTon"));

							// Tạo SanPham
							SanPham sp = new SanPham();
							sp.setMaSanPham(rs.getString("MaSanPham"));
							sp.setTenSanPham(rs.getString("TenSanPham"));
							sp.setGiaNhap(rs.getDouble("GiaNhap"));
							lo.setSanPham(sp);

							donGiaNhap = rs.getDouble("GiaNhap");

							// Tạo DonViTinh
							if (rs.getString("MaDonViTinh") != null) {
								dvt = new DonViTinh();
								dvt.setMaDonViTinh(rs.getString("MaDonViTinh"));
								dvt.setTenDonViTinh(rs.getString("TenDonViTinh"));
							}

							lyDoHuy = rs.getString("LyDoChiTiet");
						}
					}
				}

				if (lo == null) {
					con.rollback();
					return "ERR";
				}

				// ⭐ 6.2. Kiểm tra xem đã có phiếu huỷ cho phiếu trả này chưa
				String maPHDaCo = timPhieuHuyTheoPhieuTra(con, maPhieuTra);

				ChiTietPhieuHuy ctHuy = new ChiTietPhieuHuy();
				ctHuy.setLoSanPham(lo);
				ctHuy.setSoLuongHuy(soLuongTra);
				ctHuy.setDonGiaNhap(donGiaNhap);
				ctHuy.setDonViTinh(dvt);
				ctHuy.setLyDoChiTiet(
						lyDoHuy != null && !lyDoHuy.isEmpty() ? lyDoHuy : "Huỷ từ phiếu trả " + maPhieuTra);
				ctHuy.capNhatThanhTien();
				ctHuy.setTrangThai(2); // 2 = Đã hủy

				if (maPHDaCo != null) {
					// ⭐ 6.3a. Nếu đã có phiếu huỷ → thêm chi tiết vào phiếu đó
					PhieuHuy phDaCo = new PhieuHuy();
					phDaCo.setMaPhieuHuy(maPHDaCo);
					ctHuy.setPhieuHuy(phDaCo);

					// Thêm chi tiết trực tiếp trong connection này
					boolean okCT = themChiTietPhieuHuy(con, ctHuy);

					if (!okCT) {
						con.rollback();
						return "ERR";
					}

					// Cập nhật tổng tiền phiếu huỷ
					capNhatTongTienPhieuHuy(con, maPHDaCo);

					maPhieuHuyDuocTao = maPHDaCo;
				} else {
					// ⭐ 6.3b. Nếu chưa có → tạo phiếu huỷ mới
					String maPH = taoMaPhieuHuyTrongConnection(con);
					maPhieuHuyDuocTao = maPH;

					// Insert phiếu huỷ trực tiếp
					String sqlPH = "INSERT INTO PhieuHuy (MaPhieuHuy, NgayLapPhieu, MaNhanVien, TrangThai, TongTien) VALUES (?, ?, ?, ?, ?)";
					try (PreparedStatement psPH = con.prepareStatement(sqlPH)) {
						psPH.setString(1, maPH);
						psPH.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
						psPH.setString(3, nv.getMaNhanVien());
						psPH.setBoolean(4, true);
						psPH.setDouble(5, ctHuy.getThanhTien());
						psPH.executeUpdate();
					}

					// Insert chi tiết phiếu huỷ
					PhieuHuy ph = new PhieuHuy();
					ph.setMaPhieuHuy(maPH);
					ctHuy.setPhieuHuy(ph);

					boolean okCT = themChiTietPhieuHuy(con, ctHuy);
					if (!okCT) {
						con.rollback();
						return "ERR";
					}
				}
			}

			// =====================================================
			// 7. Kiểm tra phiếu đã xử lý hết chưa
			// =====================================================
			String sqlCheck = """
					        SELECT COUNT(*) FROM ChiTietPhieuTra
					        WHERE MaPhieuTra=? AND TrangThai=0
					""";

			boolean daXuLyHet = true;

			try (PreparedStatement ps = con.prepareStatement(sqlCheck)) {
				ps.setString(1, maPhieuTra);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next() && rs.getInt(1) > 0)
						daXuLyHet = false;
				}
			}

			if (daXuLyHet) {
				String sqlUpdPT = """
						        UPDATE PhieuTra SET DaDuyet=1 WHERE MaPhieuTra=?
						""";
				try (PreparedStatement ps = con.prepareStatement(sqlUpdPT)) {
					ps.setString(1, maPhieuTra);
					ps.executeUpdate();
				}
			}

			con.commit();

			// ✅ Xóa cache sau khi cập nhật thành công
			cacheAllPhieuTra = null;

			return (maPhieuHuyDuocTao != null) ? "OK|" + maPhieuHuyDuocTao : "OK";

		} catch (Exception e) {
			e.printStackTrace();
			try {
				con.rollback();
			} catch (Exception ignored) {
			}
			return "ERR";
		} finally {
			try {
				con.setAutoCommit(true);
			} catch (Exception ignored) {
			}
		}
	}

	// ============================================================
	// 🧾 Sinh mã tự động
	// ============================================================
	public String taoMaPhieuTra() {

		String prefix = "PT-";
		String today = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));

		String likePattern = prefix + today + "-%";
		String sql = "SELECT MAX(MaPhieuTra) AS MaxMa FROM PhieuTra WHERE MaPhieuTra LIKE ?";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, likePattern);
			rs = ps.executeQuery();

			if (rs.next()) {

				String lastID = rs.getString("MaxMa");
				if (lastID != null) {

					// Lấy phần số phía sau dấu "-"
					String numberPart = lastID.substring(lastID.lastIndexOf('-') + 1);

					// ⭐ BUGFIX: Trim bỏ khoảng trắng
					numberPart = numberPart.trim();

					// ⭐ BUGFIX: Nếu chuỗi chứa rác hoặc không phải số → reset về 0
					int lastNum = 0;
					try {
						lastNum = Integer.parseInt(numberPart);
					} catch (NumberFormatException e) {
						System.err.println("⚠️ Mã phiếu trả trong DB bị lỗi format: " + numberPart + " → reset = 0");
					}

					return String.format("%s%s-%04d", prefix, today, lastNum + 1);
				}
			}

		} catch (Exception e) {
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
		}

		return String.format("%s%s-%04d", prefix, today, 1);
	}

	public boolean daTraLoTrongHoaDon(String maHD, String maLo) {
		String sql = """
				    SELECT COUNT(*)
				    FROM ChiTietPhieuTra ct
				    JOIN PhieuTra pt ON ct.MaPhieuTra = pt.MaPhieuTra
				    WHERE pt.MaHoaDon = ? AND ct.MaLo = ?
				""";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, maHD);
			ps.setString(2, maLo);

			rs = ps.executeQuery();
			if (rs.next())
				return rs.getInt(1) > 0;

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
		}

		return false;
	}

	// ============================================================
	// 🔍 Tìm phiếu huỷ của phiếu trả này (dựa vào các lô trong phiếu trả)
	// ============================================================
	private String timPhieuHuyTheoPhieuTra(Connection con, String maPhieuTra) {
		// Tìm phiếu huỷ có chi tiết với MaLo trùng với các lô trong phiếu trả này
		String sql = """
				SELECT TOP 1 ph.MaPhieuHuy
				FROM PhieuHuy ph
				INNER JOIN ChiTietPhieuHuy ctph ON ph.MaPhieuHuy = ctph.MaPhieuHuy
				WHERE ctph.MaLo IN (
					SELECT MaLo FROM ChiTietPhieuTra WHERE MaPhieuTra = ?
				)
				ORDER BY ph.MaPhieuHuy DESC
				""";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maPhieuTra);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getString("MaPhieuHuy");
				}
			}
		} catch (SQLException e) {
			System.err.println("⚠️ Lỗi tìm phiếu huỷ theo phiếu trả: " + e.getMessage());
		}
		return null;
	}

	// ============================================================
	// 💰 Cập nhật tổng tiền phiếu huỷ sau khi thêm chi tiết
	// ============================================================
	private void capNhatTongTienPhieuHuy(Connection con, String maPhieuHuy) {
		String sql = """
				UPDATE PhieuHuy
				SET TongTien = (SELECT ISNULL(SUM(ThanhTien), 0) FROM ChiTietPhieuHuy WHERE MaPhieuHuy = ?)
				WHERE MaPhieuHuy = ?
				""";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maPhieuHuy);
			ps.setString(2, maPhieuHuy);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("⚠️ Lỗi cập nhật tổng tiền phiếu huỷ: " + e.getMessage());
		}
	}

	// ============================================================
	// ➕ Thêm chi tiết phiếu huỷ (trong cùng connection)
	// ============================================================
	private boolean themChiTietPhieuHuy(Connection con, ChiTietPhieuHuy ct) {
		String sql = """
				INSERT INTO ChiTietPhieuHuy
				(MaPhieuHuy, MaLo, SoLuongHuy, LyDoChiTiet, DonGiaNhap, ThanhTien, MaDonViTinh, TrangThai)
				VALUES (?, ?, ?, ?, ?, ?, ?, ?)
				""";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, ct.getPhieuHuy().getMaPhieuHuy());
			ps.setString(2, ct.getLoSanPham().getMaLo());
			ps.setInt(3, ct.getSoLuongHuy());
			ps.setString(4, ct.getLyDoChiTiet());
			ps.setDouble(5, ct.getDonGiaNhap());
			ps.setDouble(6, ct.getThanhTien());
			ps.setString(7, ct.getDonViTinh() != null ? ct.getDonViTinh().getMaDonViTinh() : null);
			ps.setInt(8, ct.getTrangThai());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("❌ Lỗi thêm chi tiết phiếu huỷ: " + e.getMessage());
			return false;
		}
	}

	// ============================================================
	// 🔢 Tạo mã phiếu huỷ (trong cùng connection)
	// ============================================================
	private String taoMaPhieuHuyTrongConnection(Connection con) {
		String date = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
		String prefix = "PH-" + date + "-";

		String sql = "SELECT COUNT(*) FROM PhieuHuy WHERE MaPhieuHuy LIKE ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, prefix + "%");
			try (ResultSet rs = ps.executeQuery()) {
				int count = rs.next() ? rs.getInt(1) : 0;
				return String.format("%s%04d", prefix, count + 1);
			}
		} catch (SQLException e) {
			System.err.println("⚠️ Lỗi tạo mã phiếu huỷ: " + e.getMessage());
			return prefix + "0001";
		}
	}

	// Đếm số PT của nhân viên đã tạo trong ngày hiện tại
	public int demSoPhieuTraHomNayCuaNhanVien(String maNhanVien) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = """
				    SELECT COUNT(*) AS SoLuong
				    FROM PhieuTra
				    WHERE MaNhanVien = ?
				      AND CAST(NgayLap AS DATE) = CAST(GETDATE() AS DATE)
				""";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, maNhanVien);
			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt("SoLuong");
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi đếm số phiếu trả hôm nay của nhân viên: " + e.getMessage());
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

		return 0;
	}

	/**
	 * 🔄 Làm mới cache
	 */
	public void refreshCache() {
		cacheAllPhieuTra = null;
		layTatCaPhieuTra();
	}

	/**
	 * 🔍 Tìm phiếu trả theo SĐT khách hàng (OPTIMIZED - dùng JOIN)
	 */
	public List<PhieuTra> timPhieuTraTheoSoDienThoai(String sdt) {
		List<PhieuTra> ds = new ArrayList<>();

		String sql = """
				SELECT
					pt.MaPhieuTra, pt.NgayLap, pt.DaDuyet, pt.TongTienHoan,
					nv.MaNhanVien, nv.TenNhanVien, nv.QuanLy, nv.CaLam,
					kh.MaKhachHang, kh.TenKhachHang, kh.GioiTinh, kh.SoDienThoai, kh.NgaySinh, kh.HoatDong
				FROM PhieuTra pt
				JOIN NhanVien nv ON pt.MaNhanVien = nv.MaNhanVien
				JOIN KhachHang kh ON pt.MaKhachHang = kh.MaKhachHang
				WHERE kh.SoDienThoai = ?
				ORDER BY pt.NgayLap DESC
				""";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		// Tạm lưu danh sách phiếu trả (chưa có chi tiết)
		List<PhieuTraTemp> tempList = new ArrayList<>();

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, sdt);
			rs = ps.executeQuery();

			while (rs.next()) {
				// ========== TẠO NHANVIEN TỪ RESULTSET ==========
				NhanVien nv = new NhanVien();
				nv.setMaNhanVien(rs.getString("MaNhanVien"));
				nv.setTenNhanVien(rs.getString("TenNhanVien"));
				nv.setQuanLy(rs.getBoolean("QuanLy"));
				nv.setCaLam(rs.getInt("CaLam"));

				// ========== TẠO KHACHHANG TỪ RESULTSET ==========
				KhachHang kh = new KhachHang();
				kh.setMaKhachHang(rs.getString("MaKhachHang"));
				kh.setTenKhachHang(rs.getString("TenKhachHang"));
				kh.setGioiTinh(rs.getBoolean("GioiTinh"));
				kh.setSoDienThoai(rs.getString("SoDienThoai"));
				java.sql.Date ngaySinhKH = rs.getDate("NgaySinh");
				if (ngaySinhKH != null) {
					kh.setNgaySinh(ngaySinhKH.toLocalDate());
				}
				kh.setHoatDong(rs.getBoolean("HoatDong"));

				// ========== LƯU TẠM ==========
				PhieuTraTemp temp = new PhieuTraTemp();
				temp.maPT = rs.getString("MaPhieuTra");
				temp.ngayLap = rs.getDate("NgayLap").toLocalDate();
				temp.daDuyet = rs.getBoolean("DaDuyet");
				temp.nv = nv;
				temp.kh = kh;
				tempList.add(temp);
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
		}

		// Sau khi đóng ResultSet, lấy chi tiết cho từng phiếu
		for (PhieuTraTemp temp : tempList) {
			List<ChiTietPhieuTra> dsCT = layChiTietPhieuTra(temp.maPT);
			PhieuTra pt = new PhieuTra(temp.maPT, temp.kh, temp.nv, temp.ngayLap, temp.daDuyet, dsCT);
			ds.add(pt);
		}

		return ds;
	}

	/**
	 * 🔍 Tìm phiếu trả theo keyword (mã phiếu, tên KH, SĐT) - OPTIMIZED với JOIN
	 * Hỗ trợ:
	 * - Mã phiếu: LIKE, case-insensitive (VD: pt-2025 -> tìm PT-2025%)
	 * - Tên KH: LIKE, case-insensitive (VD: Cúc -> tìm %Cúc%)
	 * - SĐT: LIKE or exact (VD: 090 -> tìm 090%, 0901234567 -> tìm chính xác)
	 * 
	 * ⚡ OPTIMIZED: Sử dụng JOIN để lấy đủ dữ liệu trong 1 query thay vì gọi nhiều
	 * DAO
	 */
	public List<PhieuTra> timPhieuTraTheoKeyword(String keyword) {
		List<PhieuTra> ds = new ArrayList<>();

		// Query với JOIN lấy đủ thông tin PhieuTra + NhanVien + KhachHang
		String sql = """
				SELECT
					pt.MaPhieuTra, pt.NgayLap, pt.DaDuyet, pt.TongTienHoan,
					nv.MaNhanVien, nv.TenNhanVien, nv.QuanLy, nv.CaLam,
					kh.MaKhachHang, kh.TenKhachHang, kh.GioiTinh, kh.SoDienThoai, kh.NgaySinh, kh.HoatDong
				FROM PhieuTra pt
				JOIN NhanVien nv ON pt.MaNhanVien = nv.MaNhanVien
				JOIN KhachHang kh ON pt.MaKhachHang = kh.MaKhachHang
				WHERE UPPER(pt.MaPhieuTra) LIKE UPPER(?)
				   OR kh.TenKhachHang LIKE ?
				   OR kh.SoDienThoai LIKE ?
				ORDER BY pt.NgayLap DESC, pt.MaPhieuTra DESC
				""";

		String likeKeyword = "%" + keyword + "%";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, keyword + "%"); // Mã phiếu: prefix match (PT-2025%)
			ps.setString(2, likeKeyword); // Tên KH: contains match (%Cúc%)
			ps.setString(3, likeKeyword); // SĐT: contains match (%090%)

			rs = ps.executeQuery();
			while (rs.next()) {
				// ========== TẠO NHANVIEN TỪ RESULTSET ==========
				NhanVien nv = new NhanVien();
				nv.setMaNhanVien(rs.getString("MaNhanVien"));
				nv.setTenNhanVien(rs.getString("TenNhanVien"));
				nv.setQuanLy(rs.getBoolean("QuanLy"));
				nv.setCaLam(rs.getInt("CaLam"));

				// ========== TẠO KHACHHANG TỪ RESULTSET ==========
				KhachHang kh = new KhachHang();
				kh.setMaKhachHang(rs.getString("MaKhachHang"));
				kh.setTenKhachHang(rs.getString("TenKhachHang"));
				kh.setGioiTinh(rs.getBoolean("GioiTinh"));
				kh.setSoDienThoai(rs.getString("SoDienThoai"));
				java.sql.Date ngaySinhKH = rs.getDate("NgaySinh");
				if (ngaySinhKH != null) {
					kh.setNgaySinh(ngaySinhKH.toLocalDate());
				}
				kh.setHoatDong(rs.getBoolean("HoatDong"));

				// ========== TẠO PHIEUTRA TỪ RESULTSET ==========
				String maPT = rs.getString("MaPhieuTra");
				LocalDate ngayLap = rs.getDate("NgayLap").toLocalDate();
				boolean daDuyet = rs.getBoolean("DaDuyet");

				// Tạo PhieuTra cơ bản (không có chiTietPhieuTraList để tránh validate lỗi)
				PhieuTra pt = new PhieuTra();
				pt.setMaPhieuTra(maPT);
				pt.setKhachHang(kh);
				pt.setNhanVien(nv);
				pt.setNgayLap(ngayLap);
				// Set daDuyet trực tiếp để tránh logic phụ trong setter
				try {
					java.lang.reflect.Field f = PhieuTra.class.getDeclaredField("daDuyet");
					f.setAccessible(true);
					f.set(pt, daDuyet);
				} catch (Exception ignored) {
					// Fallback nếu reflection fail
				}

				// Set tongTienHoan từ DB (đã tính sẵn)
				try {
					java.lang.reflect.Field f = PhieuTra.class.getDeclaredField("tongTienHoan");
					f.setAccessible(true);
					f.set(pt, rs.getDouble("TongTienHoan"));
				} catch (Exception ignored) {
				}

				ds.add(pt);
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
		}

		return ds;
	}
}
