package dao;

import connectDB.connectDB;
import entity.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_DAO {

	private final NhanVien_DAO nhanVienDAO;
	private final KhachHang_DAO khachHangDAO;
	private final ChiTietHoaDon_DAO chiTietHoaDonDAO;
	private final KhuyenMai_DAO khuyenMaiDAO;
	
	public HoaDon_DAO() {
		this.nhanVienDAO = new NhanVien_DAO();
		this.khachHangDAO = new KhachHang_DAO();
		this.chiTietHoaDonDAO = new ChiTietHoaDon_DAO();
		this.khuyenMaiDAO = new KhuyenMai_DAO();
	}

	/** 🔍 Tìm hóa đơn theo mã (load đầy đủ chi tiết, nhân viên, khách hàng) */
	public HoaDon timHoaDonTheoMa(String maHD) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connectDB.getInstance();
			con = connectDB.getConnection();

			String sql = "SELECT * FROM HoaDon WHERE MaHoaDon = ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, maHD);
			rs = stmt.executeQuery();

			if (rs.next()) {
				String maNV = rs.getString("MaNhanVien");
				String maKH = rs.getString("MaKhachHang");
				LocalDate ngayLap = rs.getDate("NgayLap").toLocalDate();
				String maKM = rs.getString("MaKM");
				System.out.println(maKM);
				double tongTien = rs.getDouble("TongTien");
				boolean thuocKeDon = rs.getBoolean("ThuocKeDon"); // ✅ đổi tên cột đúng với entity

				// Lấy nhân viên & khách hàng
				NhanVien nhanVien = nhanVienDAO.timNhanVienTheoMa(maNV);
				KhachHang khachHang = khachHangDAO.timKhachHangTheoMa(maKH);
				
				KhuyenMai khuyenMai = null;
				if (maKM != null && !maKM.trim().isEmpty()) {
				    khuyenMai = khuyenMaiDAO.timKhuyenMaiTheoMa(maKM);
				}

				System.out.println(khuyenMai);
				
				// 🔹 Load danh sách chi tiết hóa đơn
				List<ChiTietHoaDon> dsCT = chiTietHoaDonDAO.layDanhSachChiTietTheoMaHD(maHD);
				// ✅ Tạo hóa đơn đầy đủ
				HoaDon hd = new HoaDon(maHD, nhanVien, khachHang, ngayLap, khuyenMai, dsCT, thuocKeDon);

				// Gán lại tổng tiền (nếu cần đảm bảo trùng DB)
				try {
					var setTongTien = HoaDon.class.getDeclaredField("tongTien");
					setTongTien.setAccessible(true);
					setTongTien.set(hd, tongTien);
				} catch (Exception ignore) {
				}

				return hd;
			}
		} catch (Exception e) {
			System.err.println("❌ Lỗi khi tìm hóa đơn theo mã: " + e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			} catch (SQLException ignore) {
			}
		}
		return null;
	}

	/** 📜 Lấy toàn bộ hóa đơn */
	public List<HoaDon> layTatCaHoaDon() {
		List<HoaDon> dsHD = new ArrayList<>();
		try (Connection con = connectDB.getConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("SELECT MaHoaDon FROM HoaDon ORDER BY NgayLap DESC")) {

			while (rs.next()) {
				HoaDon hd = timHoaDonTheoMa(rs.getString("MaHoaDon"));
				if (hd != null)
					dsHD.add(hd);
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi lấy danh sách hóa đơn: " + e.getMessage());
		}
		return dsHD;
	}

	/** ➕ Thêm hóa đơn mới */
	public boolean themHoaDon(HoaDon hd) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();
		PreparedStatement stmtHD = null;
		PreparedStatement stmtCTHD = null;

		try {
			con.setAutoCommit(false); // bắt đầu transaction

			// 🔹 1️⃣ Tính tổng tiền từ chi tiết
			double tongTien = hd.getTongTien();

			// 🔹 2️⃣ Thêm hóa đơn — cập nhật đúng tên cột
			String sqlHD = """
					INSERT INTO HoaDon (MaHoaDon, NgayLap, MaNhanVien, MaKhachHang, TongTien, ThuocKeDon)
					VALUES (?, ?, ?, ?, ?, ?)
					""";
			stmtHD = con.prepareStatement(sqlHD);
			stmtHD.setString(1, hd.getMaHoaDon());
			stmtHD.setDate(2, Date.valueOf(hd.getNgayLap()));
			stmtHD.setString(3, hd.getNhanVien().getMaNhanVien());
			stmtHD.setString(4, hd.getKhachHang().getMaKhachHang());
			stmtHD.setDouble(5, tongTien);
			stmtHD.setBoolean(6, hd.isThuocKeDon());
			stmtHD.executeUpdate();

			// 🔹 3️⃣ Thêm chi tiết hóa đơn
			String sqlCT = """
					INSERT INTO ChiTietHoaDon (MaHoaDon, MaLo, MaKM, SoLuong, GiaBan)
					VALUES (?, ?, ?, ?, ?)
					""";
			stmtCTHD = con.prepareStatement(sqlCT);

			for (ChiTietHoaDon cthd : hd.getDanhSachChiTiet()) {
				stmtCTHD.setString(1, hd.getMaHoaDon());
				stmtCTHD.setString(2, cthd.getLoSanPham().getMaLo());

				KhuyenMai km = cthd.getKhuyenMai();
				if (km != null)
					stmtCTHD.setString(3, km.getMaKM());
				else
					stmtCTHD.setNull(3, Types.VARCHAR);

				stmtCTHD.setDouble(4, cthd.getSoLuong());
				stmtCTHD.setDouble(5, cthd.getGiaBan());
				stmtCTHD.addBatch();
			}
			stmtCTHD.executeBatch();

			con.commit();
			return true;
		} catch (SQLException e) {
			System.err.println("❌ Lỗi thêm hóa đơn: " + e.getMessage());
			try {
				if (con != null)
					con.rollback();
			} catch (SQLException ignore) {
			}
			return false;
		} finally {
			try {
				if (stmtHD != null)
					stmtHD.close();
				if (stmtCTHD != null)
					stmtCTHD.close();
				if (con != null)
					con.setAutoCommit(true);
			} catch (SQLException ignore) {
			}
		}
	}

	/** 🧾 Tạo mã hóa đơn tự động theo ngày */
	public String taoMaHoaDon() {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
			String prefix = "HD-" + today + "-";
			String sql = "SELECT COUNT(*) FROM HoaDon WHERE MaHoaDon LIKE ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, prefix + "%");
			rs = stmt.executeQuery();

			if (rs.next()) {
				int count = rs.getInt(1);
				return String.format("%s%04d", prefix, count + 1);
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi tạo mã hóa đơn: " + e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			} catch (SQLException ignore) {
			}
		}

		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		return "HD-" + today + "-0001";
	}

	/**
	 * 🔍 Tìm danh sách hóa đơn theo số điện thoại khách hàng (Dùng cho dialog chọn
	 * hóa đơn, chỉ load thông tin cơ bản)
	 */
	public List<HoaDon> timHoaDonTheoSoDienThoai(String soDienThoai) {
		List<HoaDon> dsHD = new ArrayList<>();

		String sql = """
				    SELECT hd.MaHoaDon, hd.NgayLap, hd.TongTien, hd.TongThanhToan,
				           hd.DiemSuDung, hd.SoTienGiamKhuyenMai, hd.ThuocKeDon,
				           hd.MaNhanVien, nv.TenNhanVien,
				           hd.MaKhachHang, kh.TenKhachHang, kh.SoDienThoai,
				           hd.MaKM
				    FROM HoaDon hd
				    JOIN KhachHang kh ON hd.MaKhachHang = kh.MaKhachHang
				    JOIN NhanVien nv ON hd.MaNhanVien = nv.MaNhanVien
				    WHERE kh.SoDienThoai = ?
				    ORDER BY hd.NgayLap DESC
				""";

		try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, soDienThoai);

			try (ResultSet rs = ps.executeQuery()) {

				while (rs.next()) {
					HoaDon hd = timHoaDonTheoMa(rs.getString("MaHoaDon"));
					if (hd != null)
						dsHD.add(hd);
				}
			}

		} catch (SQLException e) {
			System.err.println("❌ Lỗi khi tìm hóa đơn theo SĐT: " + e.getMessage());
			e.printStackTrace();
		}

		return dsHD;
	}

}
