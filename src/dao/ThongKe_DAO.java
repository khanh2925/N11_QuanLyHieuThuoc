package dao;

import database.connectDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO phục vụ các tính toán thống kê phức tạp cho Dashboard
 */
public class ThongKe_DAO {

	public ThongKe_DAO() {
	}
	

	/**
	 * Tính lợi nhuận theo tháng = Doanh thu - Chi phí nhập hàng đã bán
	 * 
	 * @param thang Tháng (1-12)
	 * @param nam   Năm
	 * @return Lợi nhuận ước tính (doanh thu - giá nhập trung bình)
	 */
	public double tinhLoiNhuanTheoThang(int thang, int nam) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		// Giả định đơn giản: Lợi nhuận = 25-30% doanh thu (tỷ suất lợi nhuận trung bình
		// ngành dược)
		// Nếu cần tính chính xác hơn, cần join với PhieuNhap để lấy giá nhập
		String sql = """
				SELECT COALESCE(SUM(TongThanhToan), 0) AS TongDoanhThu
				FROM HoaDon
				WHERE MONTH(NgayLap) = ? AND YEAR(NgayLap) = ?
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setInt(1, thang);
			stmt.setInt(2, nam);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					double doanhThu = rs.getDouble("TongDoanhThu");
					// Giả định tỷ suất lợi nhuận 25.5% (có thể điều chỉnh)
					return doanhThu * 0.255;
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi tính lợi nhuận: " + e.getMessage());
		}
		return 0;
	}

	/**
	 * Tính lợi nhuận theo tháng (phiên bản tính toán chính xác hơn) Dựa trên giá
	 * bán - giá nhập thực tế từ sản phẩm
	 * 
	 * @param thang Tháng (1-12)
	 * @param nam   Năm
	 * @return Lợi nhuận thực tế
	 */
	public double tinhLoiNhuanChinhXacTheoThang(int thang, int nam) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = """
				SELECT
				    COALESCE(SUM(cthd.ThanhTien), 0) AS TongDoanhThu,
				    COALESCE(SUM(cthd.SoLuong * qc.HeSoQuyDoi * sp.GiaNhap), 0) AS TongChiPhi
				FROM ChiTietHoaDon cthd
				INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon
				INNER JOIN LoSanPham lo ON cthd.MaLo = lo.MaLo
				INNER JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
				INNER JOIN QuyCachDongGoi qc ON cthd.MaDonViTinh = qc.MaDonViTinh
				    AND sp.MaSanPham = qc.MaSanPham
				WHERE MONTH(hd.NgayLap) = ? AND YEAR(hd.NgayLap) = ?
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setInt(1, thang);
			stmt.setInt(2, nam);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					double doanhThu = rs.getDouble("TongDoanhThu");
					double chiPhi = rs.getDouble("TongChiPhi");
					return doanhThu - chiPhi;
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi tính lợi nhuận chính xác: " + e.getMessage());
			// Fallback về phương pháp ước tính
			return tinhLoiNhuanTheoThang(thang, nam);
		}
		return 0;
	}

	// ============================================================
	// 📊 THỐNG KÊ TOP SẢN PHẨM BÁN CHẠY
	// ============================================================

	/**
	 * Lấy top N sản phẩm bán chạy theo khoảng thời gian
	 * 
	 * @param tuNgay  Ngày bắt đầu
	 * @param denNgay Ngày kết thúc
	 * @param topN    Số lượng top (5, 10, 15, 20...)
	 * @return List chứa Object[]: {MaSP, TenSP, LoaiSP, SoLuongBan, DoanhThu}
	 */
	public java.util.List<Object[]> layTopSanPhamBanChay(java.time.LocalDate tuNgay, java.time.LocalDate denNgay,
			int topN) {
		java.util.List<Object[]> result = new java.util.ArrayList<>();
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = """
				SELECT TOP (?)
				    sp.MaSanPham,
				    sp.TenSanPham,
				    sp.LoaiSanPham,
				    SUM(cthd.SoLuong * qc.HeSoQuyDoi) AS TongSoLuong,
				    SUM(cthd.ThanhTien) AS TongDoanhThu
				FROM ChiTietHoaDon cthd
				INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon
				INNER JOIN LoSanPham lo ON cthd.MaLo = lo.MaLo
				INNER JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
				INNER JOIN QuyCachDongGoi qc ON cthd.MaDonViTinh = qc.MaDonViTinh
				    AND sp.MaSanPham = qc.MaSanPham
				WHERE hd.NgayLap BETWEEN ? AND ?
				GROUP BY sp.MaSanPham, sp.TenSanPham, sp.LoaiSanPham
				ORDER BY TongSoLuong DESC
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setInt(1, topN);
			stmt.setDate(2, java.sql.Date.valueOf(tuNgay));
			stmt.setDate(3, java.sql.Date.valueOf(denNgay));

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Object[] row = new Object[5];
					row[0] = rs.getString("MaSanPham");
					row[1] = rs.getString("TenSanPham");
					row[2] = rs.getString("LoaiSanPham");
					row[3] = rs.getDouble("TongSoLuong");
					row[4] = rs.getDouble("TongDoanhThu");
					result.add(row);
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi lấy top sản phẩm bán chạy: " + e.getMessage());
		}

		return result;
	}

	/**
	 * Tính tổng doanh thu trong khoảng thời gian Doanh thu thực = Tổng bán hàng -
	 * Tổng tiền hoàn trả
	 * 
	 * @param tuNgay  Ngày bắt đầu
	 * @param denNgay Ngày kết thúc
	 * @return Tổng doanh thu (đã trừ hoàn trả)
	 */
	public double tinhTongDoanhThuTheoKhoangNgay(java.time.LocalDate tuNgay, java.time.LocalDate denNgay) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		// Doanh thu = Tổng thanh toán hóa đơn - Tổng tiền hoàn trả (phiếu trả đã duyệt)
		String sql = """
				SELECT
				    COALESCE((SELECT SUM(TongThanhToan) FROM HoaDon WHERE NgayLap BETWEEN ? AND ?), 0)
				    - COALESCE((SELECT SUM(TongTienHoan) FROM PhieuTra WHERE NgayLap BETWEEN ? AND ? AND DaDuyet = 1), 0)
				AS DoanhThuThuc
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setDate(1, java.sql.Date.valueOf(tuNgay));
			stmt.setDate(2, java.sql.Date.valueOf(denNgay));
			stmt.setDate(3, java.sql.Date.valueOf(tuNgay));
			stmt.setDate(4, java.sql.Date.valueOf(denNgay));

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getDouble("DoanhThuThuc");
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi tính tổng doanh thu: " + e.getMessage());
		}
		return 0;
	}

	/**
	 * Lấy doanh số sản phẩm của kỳ trước (để tính xu hướng)
	 * 
	 * @param maSanPham Mã sản phẩm
	 * @param tuNgay    Ngày bắt đầu kỳ trước
	 * @param denNgay   Ngày kết thúc kỳ trước
	 * @return Số lượng bán kỳ trước
	 */
	public double laySoLuongBanKyTruoc(String maSanPham, java.time.LocalDate tuNgay, java.time.LocalDate denNgay) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = """
				SELECT COALESCE(SUM(cthd.SoLuong * qc.HeSoQuyDoi), 0) AS TongSoLuong
				FROM ChiTietHoaDon cthd
				INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon
				INNER JOIN LoSanPham lo ON cthd.MaLo = lo.MaLo
				INNER JOIN QuyCachDongGoi qc ON cthd.MaDonViTinh = qc.MaDonViTinh
				    AND lo.MaSanPham = qc.MaSanPham
				WHERE lo.MaSanPham = ? AND hd.NgayLap BETWEEN ? AND ?
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maSanPham);
			stmt.setDate(2, java.sql.Date.valueOf(tuNgay));
			stmt.setDate(3, java.sql.Date.valueOf(denNgay));

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getDouble("TongSoLuong");
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi lấy số lượng bán kỳ trước: " + e.getMessage());
		}
		return 0;
	}

	/**
	 * Tính tổng doanh thu kỳ trước (để so sánh xu hướng)
	 * 
	 * @param tuNgay  Ngày bắt đầu kỳ trước
	 * @param denNgay Ngày kết thúc kỳ trước
	 * @return Tổng doanh thu kỳ trước
	 */
	public double tinhTongDoanhThuKyTruoc(java.time.LocalDate tuNgay, java.time.LocalDate denNgay) {
		return tinhTongDoanhThuTheoKhoangNgay(tuNgay, denNgay);
	}

	// ============================================================
	// 📦 THỐNG KÊ TỒN KHO THẤP
	// ============================================================

	/**
	 * Lấy danh sách sản phẩm có tồn kho thấp dưới ngưỡng
	 * 
	 * @param nguongTonKho Ngưỡng tồn kho tối thiểu
	 * @param loaiSanPham  Loại sản phẩm (null = tất cả)
	 * @return List chứa Object[]: {MaSP, TenSP, LoaiSP, TongTonKho, GiaNhap, MaNCC,
	 *         TenNCC}
	 */
	public java.util.List<Object[]> laySanPhamTonKhoThap(int nguongTonKho, String loaiSanPham) {
		java.util.List<Object[]> result = new java.util.ArrayList<>();
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		// Query: Group by sản phẩm, tính tổng tồn kho từ các lô còn hạn, join NCC từ
		// phiếu nhập gần nhất
		String sql = """
				SELECT
				    sp.MaSanPham,
				    sp.TenSanPham,
				    sp.LoaiSanPham,
				    COALESCE(SUM(lo.SoLuongTon), 0) AS TongTonKho,
				    sp.GiaNhap,
				    ncc.MaNhaCungCap,
				    ncc.TenNhaCungCap
				FROM SanPham sp
				LEFT JOIN LoSanPham lo ON sp.MaSanPham = lo.MaSanPham
				    AND lo.HanSuDung >= GETDATE() AND lo.SoLuongTon > 0
				LEFT JOIN (
				    SELECT lo_pn.MaSanPham, pn.MaNhaCungCap,
				           ROW_NUMBER() OVER (PARTITION BY lo_pn.MaSanPham ORDER BY pn.NgayNhap DESC) AS rn
				    FROM ChiTietPhieuNhap ctpn
				    INNER JOIN LoSanPham lo_pn ON ctpn.MaLo = lo_pn.MaLo
				    INNER JOIN PhieuNhap pn ON ctpn.MaPhieuNhap = pn.MaPhieuNhap
				) AS pn_latest ON sp.MaSanPham = pn_latest.MaSanPham AND pn_latest.rn = 1
				LEFT JOIN NhaCungCap ncc ON pn_latest.MaNhaCungCap = ncc.MaNhaCungCap
				WHERE sp.HoatDong = 1
				""";

		if (loaiSanPham != null && !loaiSanPham.isEmpty() && !loaiSanPham.equals("Tất cả")) {
			sql += " AND sp.LoaiSanPham = ? ";
		}

		sql += """
				GROUP BY sp.MaSanPham, sp.TenSanPham, sp.LoaiSanPham, sp.GiaNhap,
				         ncc.MaNhaCungCap, ncc.TenNhaCungCap
				HAVING COALESCE(SUM(lo.SoLuongTon), 0) <= ?
				ORDER BY TongTonKho ASC
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			int idx = 1;
			if (loaiSanPham != null && !loaiSanPham.isEmpty() && !loaiSanPham.equals("Tất cả")) {
				stmt.setString(idx++, loaiSanPham);
			}
			stmt.setInt(idx, nguongTonKho);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Object[] row = new Object[7];
					row[0] = rs.getString("MaSanPham");
					row[1] = rs.getString("TenSanPham");
					row[2] = rs.getString("LoaiSanPham");
					row[3] = rs.getInt("TongTonKho");
					row[4] = rs.getDouble("GiaNhap");
					row[5] = rs.getString("MaNhaCungCap");
					row[6] = rs.getString("TenNhaCungCap");
					result.add(row);
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi lấy sản phẩm tồn kho thấp: " + e.getMessage());
		}

		return result;
	}

	/**
	 * Tính trung bình số lượng bán/ngày của một sản phẩm trong N ngày gần nhất
	 * 
	 * @param maSanPham Mã sản phẩm
	 * @param soNgay    Số ngày để tính trung bình (ví dụ: 30 ngày)
	 * @return Trung bình số lượng bán/ngày
	 */
	public double tinhTrungBinhBanNgay(String maSanPham, int soNgay) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = """
				SELECT COALESCE(SUM(cthd.SoLuong * qc.HeSoQuyDoi), 0) / ? AS TrungBinhBanNgay
				FROM ChiTietHoaDon cthd
				INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon
				INNER JOIN LoSanPham lo ON cthd.MaLo = lo.MaLo
				INNER JOIN QuyCachDongGoi qc ON cthd.MaDonViTinh = qc.MaDonViTinh
				    AND lo.MaSanPham = qc.MaSanPham
				WHERE lo.MaSanPham = ?
				    AND hd.NgayLap >= DATEADD(DAY, -?, GETDATE())
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setDouble(1, soNgay);
			stmt.setString(2, maSanPham);
			stmt.setInt(3, soNgay);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getDouble("TrungBinhBanNgay");
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi tính trung bình bán/ngày: " + e.getMessage());
		}
		return 0;
	}

	/**
	 * Đếm số nhà cung cấp xuất hiện nhiều nhất trong danh sách sản phẩm cần nhập
	 * 
	 * @param nguongTonKho Ngưỡng tồn kho
	 * @return Object[]: {TenNCC, SoLuongSP} - NCC gợi ý và số SP cần nhập từ NCC đó
	 */
	public Object[] timNhaCungCapGoiY(int nguongTonKho) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		// Query: group by NCC, count số SP tồn thấp thuộc NCC đó
		String sql2 = """
				WITH SP_TonThap AS (
				    SELECT
				        sp.MaSanPham,
				        COALESCE(SUM(lo.SoLuongTon), 0) AS TongTon,
				        pn_latest.MaNhaCungCap
				    FROM SanPham sp
				    LEFT JOIN LoSanPham lo ON sp.MaSanPham = lo.MaSanPham
				        AND lo.HanSuDung >= GETDATE() AND lo.SoLuongTon > 0
				    LEFT JOIN (
				        SELECT lo_pn.MaSanPham, pn.MaNhaCungCap,
				               ROW_NUMBER() OVER (PARTITION BY lo_pn.MaSanPham ORDER BY pn.NgayNhap DESC) AS rn
				        FROM ChiTietPhieuNhap ctpn
				        INNER JOIN LoSanPham lo_pn ON ctpn.MaLo = lo_pn.MaLo
				        INNER JOIN PhieuNhap pn ON ctpn.MaPhieuNhap = pn.MaPhieuNhap
				    ) AS pn_latest ON sp.MaSanPham = pn_latest.MaSanPham AND pn_latest.rn = 1
				    WHERE sp.HoatDong = 1
				    GROUP BY sp.MaSanPham, pn_latest.MaNhaCungCap
				    HAVING COALESCE(SUM(lo.SoLuongTon), 0) <= ?
				)
				SELECT TOP 1 ncc.TenNhaCungCap, COUNT(*) AS SoLuongSP
				FROM SP_TonThap stt
				INNER JOIN NhaCungCap ncc ON stt.MaNhaCungCap = ncc.MaNhaCungCap
				GROUP BY ncc.MaNhaCungCap, ncc.TenNhaCungCap
				ORDER BY SoLuongSP DESC
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql2)) {
			stmt.setInt(1, nguongTonKho);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return new Object[] { rs.getString("TenNhaCungCap"), rs.getInt("SoLuongSP") };
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi tìm NCC gợi ý: " + e.getMessage());
		}
		return new Object[] { "Không có dữ liệu", 0 };
	}

	/**
	 * Lấy loại sản phẩm (enum values) để hiển thị trong dropdown
	 * 
	 * @return Danh sách tên loại sản phẩm
	 */
	public java.util.List<String> layDanhSachLoaiSanPham() {
		java.util.List<String> result = new java.util.ArrayList<>();
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = "SELECT DISTINCT LoaiSanPham FROM SanPham WHERE HoatDong = 1 ORDER BY LoaiSanPham";

		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				String loai = rs.getString("LoaiSanPham");
				if (loai != null && !loai.isEmpty()) {
					result.add(loai);
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi lấy danh sách loại sản phẩm: " + e.getMessage());
		}
		return result;
	}

	// ============================================================
	// ⏰ THỐNG KÊ LÔ SẮP HẾT HẠN
	// ============================================================

	/**
	 * Lấy danh sách lô sản phẩm sắp hết hạn trong vòng N ngày
	 * 
	 * @param soNgay      Số ngày để lọc (7, 15, 30, 60, 90)
	 * @param loaiSanPham Loại sản phẩm (null = tất cả)
	 * @return List chứa Object[]: {MaLo, TenSP, LoaiSP, HanSuDung, SoLuongTon,
	 *         GiaBan, MaSP}
	 */
	public java.util.List<Object[]> layLoSapHetHan(int soNgay, String loaiSanPham) {
		java.util.List<Object[]> result = new java.util.ArrayList<>();
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		// Query: Lấy các lô còn hạn, hết hạn trong vòng N ngày tới, còn tồn kho
		String sql = """
				SELECT
				    lo.MaLo,
				    sp.TenSanPham,
				    sp.LoaiSanPham,
				    lo.HanSuDung,
				    lo.SoLuongTon,
				    sp.GiaBan,
				    sp.MaSanPham
				FROM LoSanPham lo
				INNER JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
				WHERE lo.HanSuDung >= GETDATE()
				    AND lo.HanSuDung <= DATEADD(DAY, ?, GETDATE())
				    AND lo.SoLuongTon > 0
				    AND sp.HoatDong = 1
				""";

		if (loaiSanPham != null && !loaiSanPham.isEmpty() && !loaiSanPham.equals("Tất cả")) {
			sql += " AND sp.LoaiSanPham = ? ";
		}

		sql += " ORDER BY lo.HanSuDung ASC, lo.SoLuongTon DESC";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			int idx = 1;
			stmt.setInt(idx++, soNgay);
			if (loaiSanPham != null && !loaiSanPham.isEmpty() && !loaiSanPham.equals("Tất cả")) {
				stmt.setString(idx, loaiSanPham);
			}

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Object[] row = new Object[7];
					row[0] = rs.getString("MaLo");
					row[1] = rs.getString("TenSanPham");
					row[2] = rs.getString("LoaiSanPham");
					row[3] = rs.getDate("HanSuDung").toLocalDate();
					row[4] = rs.getInt("SoLuongTon");
					row[5] = rs.getDouble("GiaBan");
					row[6] = rs.getString("MaSanPham");
					result.add(row);
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi lấy lô sắp hết hạn: " + e.getMessage());
		}

		return result;
	}

	/**
	 * Tính trung bình số lượng bán/ngày của một LÔ cụ thể trong N ngày gần nhất
	 * 
	 * @param maLo   Mã lô
	 * @param soNgay Số ngày để tính trung bình (ví dụ: 30 ngày)
	 * @return Trung bình số lượng bán/ngày của lô đó
	 */
	public double tinhTrungBinhBanNgayTheoLo(String maLo, int soNgay) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = """
				SELECT COALESCE(SUM(cthd.SoLuong * qc.HeSoQuyDoi), 0) / ? AS TrungBinhBanNgay
				FROM ChiTietHoaDon cthd
				INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon
				INNER JOIN LoSanPham lo ON cthd.MaLo = lo.MaLo
				INNER JOIN QuyCachDongGoi qc ON cthd.MaDonViTinh = qc.MaDonViTinh
				    AND lo.MaSanPham = qc.MaSanPham
				WHERE cthd.MaLo = ?
				    AND hd.NgayLap >= DATEADD(DAY, -?, GETDATE())
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setDouble(1, soNgay);
			stmt.setString(2, maLo);
			stmt.setInt(3, soNgay);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					double tb = rs.getDouble("TrungBinhBanNgay");
					// Nếu lô này không có bán gì trong N ngày, thử lấy TB bán của cả SP
					if (tb < 0.01) {
						// Fallback: lấy mã SP từ lô và tính TB bán của toàn bộ SP
						return tinhTrungBinhBanNgayTuMaLo(maLo, soNgay);
					}
					return tb;
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi tính TB bán/ngày theo lô: " + e.getMessage());
		}
		return 0.1; // Mặc định nhỏ để tránh chia 0
	}

	/**
	 * Helper: Tính TB bán/ngày của sản phẩm dựa trên mã lô (fallback)
	 */
	private double tinhTrungBinhBanNgayTuMaLo(String maLo, int soNgay) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = """
				SELECT COALESCE(SUM(cthd.SoLuong * qc.HeSoQuyDoi), 0) / ? AS TrungBinhBanNgay
				FROM ChiTietHoaDon cthd
				INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon
				INNER JOIN LoSanPham lo ON cthd.MaLo = lo.MaLo
				INNER JOIN QuyCachDongGoi qc ON cthd.MaDonViTinh = qc.MaDonViTinh
				    AND lo.MaSanPham = qc.MaSanPham
				WHERE lo.MaSanPham = (SELECT MaSanPham FROM LoSanPham WHERE MaLo = ?)
				    AND hd.NgayLap >= DATEADD(DAY, -?, GETDATE())
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setDouble(1, soNgay);
			stmt.setString(2, maLo);
			stmt.setInt(3, soNgay);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					double tb = rs.getDouble("TrungBinhBanNgay");
					return tb > 0.01 ? tb : 0.1; // Mặc định 0.1 nếu không có dữ liệu
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi tính TB bán/ngày từ mã lô: " + e.getMessage());
		}
		return 0.1;
	}

	// ============================================================
	// 📊 THỐNG KÊ THEO LOẠI SẢN PHẨM
	// ============================================================

	/**
	 * Lấy thống kê doanh thu, chi phí, lợi nhuận theo loại sản phẩm trong năm
	 * 
	 * @param nam Năm cần thống kê
	 * @return List chứa Object[]: {LoaiSP, SoLuongSP, DoanhThu, ChiPhi}
	 */
	public java.util.List<Object[]> layThongKeTheoLoaiSanPham(int nam) {
		java.util.List<Object[]> result = new java.util.ArrayList<>();
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		// Query: INNER JOIN để chỉ lấy sản phẩm có bán trong năm, WHERE để lọc năm
		String sql = """
				SELECT
				    sp.LoaiSanPham,
				    COUNT(DISTINCT sp.MaSanPham) AS SoLuongSP,
				    SUM(cthd.ThanhTien) AS TongDoanhThu,
				    SUM(cthd.SoLuong * qc.HeSoQuyDoi * sp.GiaNhap) AS TongChiPhi
				FROM ChiTietHoaDon cthd
				INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon
				INNER JOIN LoSanPham lo ON cthd.MaLo = lo.MaLo
				INNER JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
				INNER JOIN QuyCachDongGoi qc ON cthd.MaDonViTinh = qc.MaDonViTinh
				    AND sp.MaSanPham = qc.MaSanPham
				WHERE YEAR(hd.NgayLap) = ?
				    AND sp.HoatDong = 1
				GROUP BY sp.LoaiSanPham
				ORDER BY TongDoanhThu DESC
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setInt(1, nam);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Object[] row = new Object[4];
					row[0] = rs.getString("LoaiSanPham");
					row[1] = rs.getInt("SoLuongSP");
					row[2] = rs.getDouble("TongDoanhThu");
					row[3] = rs.getDouble("TongChiPhi");
					result.add(row);
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi lấy thống kê theo loại SP: " + e.getMessage());
		}

		return result;
	}

	/**
	 * Lấy thống kê theo loại sản phẩm cho năm trước để so sánh
	 * 
	 * @param nam Năm hiện tại (sẽ trả về dữ liệu năm trước = nam - 1)
	 * @return Map: LoaiSP -> DoanhThu năm trước
	 */
	public java.util.Map<String, Double> layDoanhThuNamTruocTheoLoai(int nam) {
		java.util.Map<String, Double> result = new java.util.HashMap<>();
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		// Query: INNER JOIN để chỉ lấy doanh thu thực tế của năm trước
		String sql = """
				SELECT
				    sp.LoaiSanPham,
				    SUM(cthd.ThanhTien) AS TongDoanhThu
				FROM ChiTietHoaDon cthd
				INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon
				INNER JOIN LoSanPham lo ON cthd.MaLo = lo.MaLo
				INNER JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
				WHERE YEAR(hd.NgayLap) = ?
				    AND sp.HoatDong = 1
				GROUP BY sp.LoaiSanPham
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setInt(1, nam - 1); // Năm trước

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String loai = rs.getString("LoaiSanPham");
					double doanhThu = rs.getDouble("TongDoanhThu");
					result.put(loai, doanhThu);
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi lấy doanh thu năm trước theo loại: " + e.getMessage());
		}

		return result;
	}

	/**
	 * Tính tổng doanh thu theo năm
	 * 
	 * @param nam Năm cần tính
	 * @return Tổng doanh thu trong năm
	 */
	public double tinhTongDoanhThuTheoNam(int nam) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = """
				SELECT COALESCE(SUM(TongThanhToan), 0) AS TongDoanhThu
				FROM HoaDon
				WHERE YEAR(NgayLap) = ?
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setInt(1, nam);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getDouble("TongDoanhThu");
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi tính tổng doanh thu theo năm: " + e.getMessage());
		}
		return 0;
	}

	//=============================================================Thanh===================================================
	public static class BanGhiThongKe {
		public String thoiGian;
		public double doanhThu;
		public int soLuongDon;

		public BanGhiThongKe(String thoiGian, double doanhThu, int soLuongDon) {
			this.thoiGian = thoiGian;
			this.doanhThu = doanhThu;
			this.soLuongDon = soLuongDon;
		}
	}

	// Hàm hỗ trợ xây dựng câu WHERE động
	private String getDieuKienLoc(String loaiSP, String maKM) {
		String sql = "";
		// Lọc theo Loại sản phẩm
		if (loaiSP != null && !loaiSP.equals("Tất cả")) {
			sql += " AND sp.LoaiSanPham = ? ";
		}
		// Lọc theo Mã khuyến mãi (Kiểm tra cả KM hóa đơn và KM chi tiết)
		if (maKM != null && !maKM.equals("Tất cả")) {
			sql += " AND (hd.MaKM = ? OR ct.MaKM = ?) ";
		}
		return sql;
	}

	private void setThamSoLoc(PreparedStatement ps, int startIndex, String loaiSP, String maKM) throws SQLException {
		int idx = startIndex;
		if (loaiSP != null && !loaiSP.equals("Tất cả")) {
			ps.setString(idx++, loaiSP); // Enum trong DB lưu dạng String (ví dụ 'THUOC')
		}
		if (maKM != null && !maKM.equals("Tất cả")) {
			ps.setString(idx++, maKM);
			ps.setString(idx++, maKM);
		}
	}

	/**
	 * Thống kê theo ngày với bộ lọc mở rộng
	 */
	public List<BanGhiThongKe> getDoanhThuTheoNgay(java.util.Date tuNgay, java.util.Date denNgay, String loaiSP,
			String maKM) {
		List<BanGhiThongKe> list = new ArrayList<>();
		Connection con = null;
		try {
			connectDB.getInstance();
			con = connectDB.getConnection();

			// JOIN các bảng để lấy thông tin Loại SP và Khuyến Mãi
			String sql = "SELECT FORMAT(hd.NgayLap, 'dd/MM/yyyy') as Ngay, " + "SUM(ct.ThanhTien) as TongTien, " + // Cộng
																													// tiền
																													// chi
																													// tiết
					"COUNT(DISTINCT hd.MaHoaDon) as SoDon " + "FROM HoaDon hd "
					+ "JOIN ChiTietHoaDon ct ON hd.MaHoaDon = ct.MaHoaDon " + "JOIN LoSanPham lo ON ct.MaLo = lo.MaLo "
					+ "JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham " + "WHERE hd.NgayLap BETWEEN ? AND ? "
					+ getDieuKienLoc(loaiSP, maKM) + " GROUP BY hd.NgayLap " + "ORDER BY hd.NgayLap ASC";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setDate(1, new java.sql.Date(tuNgay.getTime()));
			ps.setDate(2, new java.sql.Date(denNgay.getTime()));
			setThamSoLoc(ps, 3, loaiSP, maKM);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				list.add(new BanGhiThongKe(rs.getString("Ngay"), rs.getDouble("TongTien"), rs.getInt("SoDon")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Thống kê theo tháng với bộ lọc mở rộng
	 */
	public List<BanGhiThongKe> getDoanhThuTheoThang(int nam, String loaiSP, String maKM) {
		List<BanGhiThongKe> list = new ArrayList<>();
		Connection con = null;
		try {
			connectDB.getInstance();
			con = connectDB.getConnection();

			String sql = "SELECT MONTH(hd.NgayLap) as Thang, " + "SUM(ct.ThanhTien) as TongTien, "
					+ "COUNT(DISTINCT hd.MaHoaDon) as SoDon " + "FROM HoaDon hd "
					+ "JOIN ChiTietHoaDon ct ON hd.MaHoaDon = ct.MaHoaDon " + "JOIN LoSanPham lo ON ct.MaLo = lo.MaLo "
					+ "JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham " + "WHERE YEAR(hd.NgayLap) = ? "
					+ getDieuKienLoc(loaiSP, maKM) + " GROUP BY MONTH(hd.NgayLap) " + "ORDER BY MONTH(hd.NgayLap) ASC";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, nam);
			setThamSoLoc(ps, 2, loaiSP, maKM);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				list.add(new BanGhiThongKe("T" + rs.getInt("Thang"), rs.getDouble("TongTien"), rs.getInt("SoDon")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Thống kê theo năm với bộ lọc mở rộng
	 */
	public List<BanGhiThongKe> getDoanhThuTheoNam(int namBatDau, int namKetThuc, String loaiSP, String maKM) {
		List<BanGhiThongKe> list = new ArrayList<>();
		Connection con = null;
		try {
			connectDB.getInstance();
			con = connectDB.getConnection();

			String sql = "SELECT YEAR(hd.NgayLap) as Nam, " + "SUM(ct.ThanhTien) as TongTien, "
					+ "COUNT(DISTINCT hd.MaHoaDon) as SoDon " + "FROM HoaDon hd "
					+ "JOIN ChiTietHoaDon ct ON hd.MaHoaDon = ct.MaHoaDon " + "JOIN LoSanPham lo ON ct.MaLo = lo.MaLo "
					+ "JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham " + "WHERE YEAR(hd.NgayLap) BETWEEN ? AND ? "
					+ getDieuKienLoc(loaiSP, maKM) + " GROUP BY YEAR(hd.NgayLap) " + "ORDER BY YEAR(hd.NgayLap) ASC";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, namBatDau);
			ps.setInt(2, namKetThuc);
			setThamSoLoc(ps, 3, loaiSP, maKM);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				list.add(new BanGhiThongKe(String.valueOf(rs.getInt("Nam")), rs.getDouble("TongTien"),
						rs.getInt("SoDon")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	// Hàm phụ để lấy danh sách Khuyến mãi đưa vào ComboBox (nếu cần)
	public List<String[]> getDanhSachKhuyenMai() {
		List<String[]> list = new ArrayList<>();
		try {
			Connection con = connectDB.getConnection();
			// Lấy các khuyến mãi còn hoạt động hoặc tất cả
			String sql = "SELECT MaKM, TenKM FROM KhuyenMai";
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				list.add(new String[] { rs.getString("MaKM"), rs.getString("TenKM") });
			}
		} catch (Exception e) {
		}
		return list;
	}

	/**
	 * Hàm phụ trợ: Lấy tổng doanh thu duy nhất trong 1 khoảng thời gian Dùng để
	 * tính % tăng trưởng so với kỳ trước
	 */
	public double getTongDoanhThuTrongKhoang(java.util.Date tuNgay, java.util.Date denNgay, String loaiSP,
			String maKM) {
		double tong = 0;
		Connection con = null;
		try {
			connectDB.getInstance();
			con = connectDB.getConnection();

			String sql = "SELECT COALESCE(SUM(ct.ThanhTien), 0) as TongTien " + "FROM HoaDon hd "
					+ "JOIN ChiTietHoaDon ct ON hd.MaHoaDon = ct.MaHoaDon " + "JOIN LoSanPham lo ON ct.MaLo = lo.MaLo "
					+ "JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham " + "WHERE hd.NgayLap BETWEEN ? AND ? "
					+ getDieuKienLoc(loaiSP, maKM); // Tận dụng hàm getDieuKienLoc có sẵn

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setDate(1, new java.sql.Date(tuNgay.getTime()));
			ps.setDate(2, new java.sql.Date(denNgay.getTime()));
			setThamSoLoc(ps, 3, loaiSP, maKM); // Tận dụng hàm setThamSoLoc có sẵn

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				tong = rs.getDouble("TongTien");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tong;
	}
	  // --- Class DTO mới để chứa dữ liệu 4 cột ---
	public static class BanGhiTaiChinh {
        public String thoiGian; // Sửa từ int thang -> String thoiGian
        public double banHang;
        public double nhapHang;
        public double traHang;
        public double huyHang;

        public BanGhiTaiChinh(String thoiGian, double banHang, double nhapHang, double traHang, double huyHang) {
            this.thoiGian = thoiGian;
            this.banHang = banHang;
            this.nhapHang = nhapHang;
            this.traHang = traHang;
            this.huyHang = huyHang;
        }
    }

// --- 1. Hàm lấy dữ liệu tài chính theo THÁNG (Đã bỏ maKM) ---
    public List<BanGhiTaiChinh> getThongKeTaiChinhTheoThang(int nam, String loaiSP) {
        List<BanGhiTaiChinh> list = new ArrayList<>();
        Connection con = null;
        try {
            connectDB.getInstance();
            con = connectDB.getConnection();

            String filterLoaiSP = "";
            if (loaiSP != null && !loaiSP.equals("Tất cả")) {
                filterLoaiSP = " AND sp.LoaiSanPham = N'" + loaiSP + "' ";
            }

            // Đã xóa phần filterKM
            String sql = """
                SELECT T.Thang, SUM(T.Val_Ban) AS BanHang, SUM(T.Val_Nhap) AS NhapHang, SUM(T.Val_Tra) AS TraHang, SUM(T.Val_Huy) AS HuyHang
                FROM (
                    -- 1. Bán
                    SELECT MONTH(hd.NgayLap) AS Thang, SUM(ct.ThanhTien) AS Val_Ban, 0 AS Val_Nhap, 0 AS Val_Tra, 0 AS Val_Huy
                    FROM HoaDon hd JOIN ChiTietHoaDon ct ON hd.MaHoaDon = ct.MaHoaDon JOIN LoSanPham lo ON ct.MaLo = lo.MaLo JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
                    WHERE YEAR(hd.NgayLap) = ? %s GROUP BY MONTH(hd.NgayLap)
                    UNION ALL
                    -- 2. Nhập
                    SELECT MONTH(pn.NgayNhap), 0, SUM(ct.ThanhTien), 0, 0
                    FROM PhieuNhap pn JOIN ChiTietPhieuNhap ct ON pn.MaPhieuNhap = ct.MaPhieuNhap JOIN LoSanPham lo ON ct.MaLo = lo.MaLo JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
                    WHERE YEAR(pn.NgayNhap) = ? %s GROUP BY MONTH(pn.NgayNhap)
                    UNION ALL
                    -- 3. Trả
                    SELECT MONTH(pt.NgayLap), 0, 0, SUM(ct.ThanhTienHoan), 0
                    FROM PhieuTra pt JOIN ChiTietPhieuTra ct ON pt.MaPhieuTra = ct.MaPhieuTra JOIN HoaDon hd ON ct.MaHoaDon = hd.MaHoaDon JOIN LoSanPham lo ON ct.MaLo = lo.MaLo JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
                    WHERE YEAR(pt.NgayLap) = ? AND pt.DaDuyet = 1 %s GROUP BY MONTH(pt.NgayLap)
                    UNION ALL
                    -- 4. Hủy
                    SELECT MONTH(ph.NgayLapPhieu), 0, 0, 0, SUM(ct.ThanhTien)
                    FROM PhieuHuy ph JOIN ChiTietPhieuHuy ct ON ph.MaPhieuHuy = ct.MaPhieuHuy JOIN LoSanPham lo ON ct.MaLo = lo.MaLo JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
                    WHERE YEAR(ph.NgayLapPhieu) = ? %s GROUP BY MONTH(ph.NgayLapPhieu)
                ) AS T GROUP BY T.Thang ORDER BY T.Thang
            """.formatted(filterLoaiSP, filterLoaiSP, filterLoaiSP, filterLoaiSP);

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, nam); ps.setInt(2, nam); ps.setInt(3, nam); ps.setInt(4, nam);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new BanGhiTaiChinh("T" + rs.getInt("Thang"), rs.getDouble("BanHang"), rs.getDouble("NhapHang"), rs.getDouble("TraHang"), rs.getDouble("HuyHang")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // --- 2. Hàm lấy dữ liệu tài chính theo NGÀY (Đã bỏ maKM) ---
    public List<BanGhiTaiChinh> getThongKeTaiChinhTheoNgay(java.util.Date tuNgay, java.util.Date denNgay, String loaiSP) {
        List<BanGhiTaiChinh> list = new ArrayList<>();
        Connection con = null;
        try {
            connectDB.getInstance();
            con = connectDB.getConnection();

            String filterLoaiSP = "";
            if (loaiSP != null && !loaiSP.equals("Tất cả")) {
                filterLoaiSP = " AND sp.LoaiSanPham = N'" + loaiSP + "' ";
            }

            String sql = """
                SELECT T.Ngay, SUM(T.Val_Ban) AS Ban, SUM(T.Val_Nhap) AS Nhap, SUM(T.Val_Tra) AS Tra, SUM(T.Val_Huy) AS Huy
                FROM (
                    SELECT hd.NgayLap AS Ngay, SUM(ct.ThanhTien) AS Val_Ban, 0 AS Val_Nhap, 0 AS Val_Tra, 0 AS Val_Huy
                    FROM HoaDon hd JOIN ChiTietHoaDon ct ON hd.MaHoaDon = ct.MaHoaDon JOIN LoSanPham lo ON ct.MaLo = lo.MaLo JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
                    WHERE hd.NgayLap BETWEEN ? AND ? %s GROUP BY hd.NgayLap
                    UNION ALL
                    SELECT pn.NgayNhap, 0, SUM(ct.ThanhTien), 0, 0
                    FROM PhieuNhap pn JOIN ChiTietPhieuNhap ct ON pn.MaPhieuNhap = ct.MaPhieuNhap JOIN LoSanPham lo ON ct.MaLo = lo.MaLo JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
                    WHERE pn.NgayNhap BETWEEN ? AND ? %s GROUP BY pn.NgayNhap
                    UNION ALL
                    SELECT pt.NgayLap, 0, 0, SUM(ct.ThanhTienHoan), 0
                    FROM PhieuTra pt JOIN ChiTietPhieuTra ct ON pt.MaPhieuTra = ct.MaPhieuTra JOIN HoaDon hd ON ct.MaHoaDon = hd.MaHoaDon JOIN LoSanPham lo ON ct.MaLo = lo.MaLo JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
                    WHERE pt.NgayLap BETWEEN ? AND ? AND pt.DaDuyet = 1 %s GROUP BY pt.NgayLap
                    UNION ALL
                    SELECT ph.NgayLapPhieu, 0, 0, 0, SUM(ct.ThanhTien)
                    FROM PhieuHuy ph JOIN ChiTietPhieuHuy ct ON ph.MaPhieuHuy = ct.MaPhieuHuy JOIN LoSanPham lo ON ct.MaLo = lo.MaLo JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
                    WHERE ph.NgayLapPhieu BETWEEN ? AND ? %s GROUP BY ph.NgayLapPhieu
                ) AS T GROUP BY T.Ngay ORDER BY T.Ngay
            """.formatted(filterLoaiSP, filterLoaiSP, filterLoaiSP, filterLoaiSP);

            PreparedStatement ps = con.prepareStatement(sql);
            java.sql.Date d1 = new java.sql.Date(tuNgay.getTime());
            java.sql.Date d2 = new java.sql.Date(denNgay.getTime());
            ps.setDate(1, d1); ps.setDate(2, d2); 
            ps.setDate(3, d1); ps.setDate(4, d2); 
            ps.setDate(5, d1); ps.setDate(6, d2); 
            ps.setDate(7, d1); ps.setDate(8, d2); 

            ResultSet rs = ps.executeQuery();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            while (rs.next()) {
                java.sql.Date ngaySQL = rs.getDate("Ngay");
                String labelNgay = (ngaySQL != null) ? sdf.format(ngaySQL) : "";
                list.add(new BanGhiTaiChinh(labelNgay, rs.getDouble("Ban"), rs.getDouble("Nhap"), rs.getDouble("Tra"), rs.getDouble("Huy")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // --- 3. Hàm lấy dữ liệu tài chính theo NĂM (Đã bỏ maKM) ---
    public List<BanGhiTaiChinh> getThongKeTaiChinhTheoNam(int namBatDau, int namKetThuc, String loaiSP) {
        List<BanGhiTaiChinh> list = new ArrayList<>();
        Connection con = null;
        try {
            connectDB.getInstance();
            con = connectDB.getConnection();

            String filterLoaiSP = "";
            if (loaiSP != null && !loaiSP.equals("Tất cả")) {
                filterLoaiSP = " AND sp.LoaiSanPham = N'" + loaiSP + "' ";
            }

            String sql = """
                SELECT T.Nam, SUM(T.Val_Ban) AS Ban, SUM(T.Val_Nhap) AS Nhap, SUM(T.Val_Tra) AS Tra, SUM(T.Val_Huy) AS Huy
                FROM (
                    SELECT YEAR(hd.NgayLap) AS Nam, SUM(ct.ThanhTien) AS Val_Ban, 0 AS Val_Nhap, 0 AS Val_Tra, 0 AS Val_Huy
                    FROM HoaDon hd JOIN ChiTietHoaDon ct ON hd.MaHoaDon = ct.MaHoaDon JOIN LoSanPham lo ON ct.MaLo = lo.MaLo JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
                    WHERE YEAR(hd.NgayLap) BETWEEN ? AND ? %s GROUP BY YEAR(hd.NgayLap)
                    UNION ALL
                    SELECT YEAR(pn.NgayNhap), 0, SUM(ct.ThanhTien), 0, 0
                    FROM PhieuNhap pn JOIN ChiTietPhieuNhap ct ON pn.MaPhieuNhap = ct.MaPhieuNhap JOIN LoSanPham lo ON ct.MaLo = lo.MaLo JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
                    WHERE YEAR(pn.NgayNhap) BETWEEN ? AND ? %s GROUP BY YEAR(pn.NgayNhap)
                    UNION ALL
                    SELECT YEAR(pt.NgayLap), 0, 0, SUM(ct.ThanhTienHoan), 0
                    FROM PhieuTra pt JOIN ChiTietPhieuTra ct ON pt.MaPhieuTra = ct.MaPhieuTra JOIN HoaDon hd ON ct.MaHoaDon = hd.MaHoaDon JOIN LoSanPham lo ON ct.MaLo = lo.MaLo JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
                    WHERE YEAR(pt.NgayLap) BETWEEN ? AND ? AND pt.DaDuyet = 1 %s GROUP BY YEAR(pt.NgayLap)
                    UNION ALL
                    SELECT YEAR(ph.NgayLapPhieu), 0, 0, 0, SUM(ct.ThanhTien)
                    FROM PhieuHuy ph JOIN ChiTietPhieuHuy ct ON ph.MaPhieuHuy = ct.MaPhieuHuy JOIN LoSanPham lo ON ct.MaLo = lo.MaLo JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
                    WHERE YEAR(ph.NgayLapPhieu) BETWEEN ? AND ? %s GROUP BY YEAR(ph.NgayLapPhieu)
                ) AS T GROUP BY T.Nam ORDER BY T.Nam
            """.formatted(filterLoaiSP, filterLoaiSP, filterLoaiSP, filterLoaiSP);

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, namBatDau); ps.setInt(2, namKetThuc);
            ps.setInt(3, namBatDau); ps.setInt(4, namKetThuc);
            ps.setInt(5, namBatDau); ps.setInt(6, namKetThuc);
            ps.setInt(7, namBatDau); ps.setInt(8, namKetThuc);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new BanGhiTaiChinh(String.valueOf(rs.getInt("Nam")), rs.getDouble("Ban"), rs.getDouble("Nhap"), rs.getDouble("Tra"), rs.getDouble("Huy")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
	
	
}
