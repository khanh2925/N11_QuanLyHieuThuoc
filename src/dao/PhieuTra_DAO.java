package dao;

import entity.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import database.connectDB;

public class PhieuTra_DAO {

	private final NhanVien_DAO nhanVienDAO;
	private final KhachHang_DAO khachHangDAO;
	private final ChiTietPhieuTra_DAO chiTietPhieuTraDAO;

	public PhieuTra_DAO() {
		this.nhanVienDAO = new NhanVien_DAO();
		this.khachHangDAO = new KhachHang_DAO();
		this.chiTietPhieuTraDAO = new ChiTietPhieuTra_DAO();
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
	// � Đếm số phiếu trả chưa duyệt (cho Dashboard)
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
			try { if (rs != null) rs.close(); } catch (Exception ignored) {}
			try { if (st != null) st.close(); } catch (Exception ignored) {}
		}
		
		return 0;
	}
	
	/**
	 * Tính tổng tiền trả hàng theo tháng (cho biểu đồ)
	 * @param thang Tháng (1-12)
	 * @param nam Năm
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
			try { if (rs != null) rs.close(); } catch (Exception ignored) {}
			try { if (ps != null) ps.close(); } catch (Exception ignored) {}
		}
		
		return 0;
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

				// ⭐ 6.1. Lấy thông tin lô, sản phẩm, đơn vị tính - TRỰC TIẾP trong connection này
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
				ctHuy.setLyDoChiTiet(lyDoHuy != null && !lyDoHuy.isEmpty() ? lyDoHuy : "Huỷ từ phiếu trả " + maPhieuTra);
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
	//Đếm số PT của nhân viên đã tạo trong ngày hiện tại
	public int demSoPhieuTraHomNayCuaNhanVien(String maNhanVien) {
	    connectDB.getInstance();
	    Connection con = connectDB.getConnection();

	    String sql = """
	        SELECT COUNT(*) AS SoLuong
	        FROM PhieuTra
	        WHERE MaNhanVien = ?
	          AND CAST(NgayLap AS DATE) = CAST(GETDATE() AS DATE)
	    """;

	    try (PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setString(1, maNhanVien);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("SoLuong");
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("❌ Lỗi đếm số phiếu trả hôm nay của nhân viên: " + e.getMessage());
	    }

	    return 0;
	}
	

}
