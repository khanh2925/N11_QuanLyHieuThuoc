package dao;

import connectDB.connectDB;
import entity.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class PhieuTra_DAO {

	private final NhanVien_DAO nhanVienDAO;
	private final KhachHang_DAO khachHangDAO;
	private final ChiTietPhieuTra_DAO chiTietPhieuTraDAO;
	private final PhieuHuy_DAO phieuHuyDAO;

	public PhieuTra_DAO() {
		this.nhanVienDAO = new NhanVien_DAO();
		this.khachHangDAO = new KhachHang_DAO();
		this.chiTietPhieuTraDAO = new ChiTietPhieuTra_DAO();
		this.phieuHuyDAO = new PhieuHuy_DAO();
	}

	// ============================================================
	// 🔍 Tìm phiếu theo mã
	// ============================================================
	public PhieuTra timKiemPhieuTraBangMa(String maPhieuTra) {

		String sql = """
						SELECT MaPhieuTra, NgayLap, MaNhanVien, MaKhachHang, DaDuyet
						FROM PhieuTra WHERE MaPhieuTra = ?
				""";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, maPhieuTra);
			rs = ps.executeQuery();

			if (rs.next()) {

				LocalDate ngayLap = rs.getDate("NgayLap").toLocalDate();
				String maNV = rs.getString("MaNhanVien");
				String maKH = rs.getString("MaKhachHang");
				boolean daDuyet = rs.getBoolean("DaDuyet");

				NhanVien nv = nhanVienDAO.timNhanVienTheoMa(maNV);
				KhachHang kh = khachHangDAO.timKhachHangTheoMa(maKH);

				List<ChiTietPhieuTra> dsCT = chiTietPhieuTraDAO.timKiemChiTietBangMaPhieuTra(maPhieuTra);

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
		}

		return null;
	}

	// ============================================================
	// 📜 Lấy tất cả phiếu trả
	// ============================================================
	public List<PhieuTra> layTatCaPhieuTra() {

		List<String> danhSachMa = new ArrayList<>();
		List<PhieuTra> ketQua = new ArrayList<>();

		String sql = """
				    SELECT MaPhieuTra
				    FROM PhieuTra
				    ORDER BY NgayLap DESC, MaPhieuTra DESC
				""";

		Connection con = connectDB.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			// ====== PHA 1: chỉ lấy danh sách mã phiếu ======
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();

			while (rs.next()) {
				danhSachMa.add(rs.getString("MaPhieuTra"));
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
			// ❗ KHÔNG đóng connection
		}

		// ====== PHA 2: dùng timKiemPhieuTraBangMa() để tạo entity đầy đủ ======
		for (String maPT : danhSachMa) {
			PhieuTra pt = timKiemPhieuTraBangMa(maPT);
			if (pt != null) {
				ketQua.add(pt);
			}
		}

		return ketQua;
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
			psPT.setBoolean(6, pt.isDaDuyet());
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
			NhanVien nv, int trangThaiMoi, String lyDoMoi) {

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
			String lyDo = "";

			try (PreparedStatement ps = con.prepareStatement(sqlGetOld)) {

				ps.setString(1, maPhieuTra);
				ps.setString(2, maHoaDon);
				ps.setString(3, maLo);
				ps.setString(4, maDonViTinh);

				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						trangThaiCu = rs.getInt("TrangThai");
						soLuongTra = rs.getInt("SoLuong");
						lyDo = rs.getString("LyDoChiTiet");
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
			// 6. Nếu chuyển sang HỦY → tạo phiếu hủy tự động
			// =====================================================
			if (trangThaiMoi == 2 && trangThaiCu != 2) {

				// ⭐ 6.1. Lấy thông tin lô để tính đơn giá nhập
				LoSanPham_DAO loDAO = new LoSanPham_DAO();
				LoSanPham lo = loDAO.timLoTheoMa(maLo);

				double donGiaNhap = (lo != null ? lo.getSanPham().getGiaNhap() : 0);

				// ⭐ 6.2. Tạo chi tiết phiếu hủy
				ChiTietPhieuHuy ctHuy = new ChiTietPhieuHuy();
				ctHuy.setLoSanPham(lo);
				ctHuy.setSoLuongHuy(soLuongTra);
				ctHuy.setLyDoChiTiet(lyDoMoi != null ? lyDoMoi : lyDo);
				ctHuy.setDonGiaNhap(donGiaNhap);
				ctHuy.capNhatThanhTien();
				ctHuy.setTrangThai(2); // 2 = Hủy

				List<ChiTietPhieuHuy> ds = new ArrayList<>();
				ds.add(ctHuy);

				// ⭐ 6.3. Tạo phiếu hủy
				String maPH = phieuHuyDAO.taoMaPhieuHuy();
				maPhieuHuyDuocTao = maPH; // gắn vào để GUI báo

				PhieuHuy ph = new PhieuHuy(maPH, LocalDate.now(), nv, true);
				ph.setChiTietPhieuHuyList(ds);
				ph.capNhatTongTienTheoChiTiet();

				// ⭐ 6.4. Lưu xuống DB (vẫn đang trong transaction của PhieuTra)
				boolean okPH = phieuHuyDAO.themPhieuHuy(ph);

				if (!okPH) {
					con.rollback();
					return "ERR";
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

		try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, maHD);
			ps.setString(2, maLo);

			ResultSet rs = ps.executeQuery();
			if (rs.next())
				return rs.getInt(1) > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

}
