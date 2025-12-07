package dao;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.apache.poi.ss.formula.functions.Finance;

import connectDB.connectDB;
import entity.NhanVien;
import entity.TaiKhoan;

public class TaiKhoan_DAO {

	public TaiKhoan_DAO() {
	}

	/** 🔧 Tạo đối tượng NhanVien từ ResultSet */
	private NhanVien taoNhanVienTuResultSet(ResultSet rs) throws SQLException {
		String maNV = rs.getString("MaNhanVien");
		String tenNV = rs.getString("TenNhanVien");
		boolean gioiTinh = rs.getBoolean("GioiTinh");
		Date d = rs.getDate("NgaySinh");
		LocalDate ngaySinh = (d != null) ? d.toLocalDate() : null;
		String sdt = rs.getString("SoDienThoai");
		String diaChi = rs.getString("DiaChi");
		boolean quanLy = rs.getBoolean("QuanLy");
		int caLam = rs.getInt("CaLam");
		boolean trangThai = rs.getBoolean("TrangThai");

		// Trường hợp thiếu dữ liệu ngày sinh
		if (ngaySinh == null) {
			NhanVien nv = new NhanVien(maNV, tenNV, caLam, trangThai);
			nv.setQuanLy(quanLy);
			nv.setGioiTinh(gioiTinh);
			if (sdt != null)
				nv.setSoDienThoai(sdt);
			if (diaChi != null)
				nv.setDiaChi(diaChi);
			return nv;
		}

		return new NhanVien(maNV, tenNV, gioiTinh, ngaySinh, sdt, diaChi, quanLy, caLam, trangThai);
	}

	/** 🔹 Lấy toàn bộ tài khoản (kèm thông tin nhân viên) */
	public ArrayList<TaiKhoan> layTatCaTaiKhoan() {
		ArrayList<TaiKhoan> danhSach = new ArrayList<>();
		connectDB.getInstance();
		String sql = """
				    SELECT tk.MaTaiKhoan, tk.TenDangNhap, tk.MatKhau,
				           nv.MaNhanVien, nv.TenNhanVien, nv.GioiTinh, nv.NgaySinh,
				           nv.SoDienThoai, nv.DiaChi, nv.QuanLy, nv.CaLam, nv.TrangThai
				    FROM TaiKhoan tk
				    JOIN NhanVien nv ON tk.MaNhanVien = nv.MaNhanVien
				""";

		try (Connection con = connectDB.getConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(sql)) {

			while (rs.next()) {
				NhanVien nv = taoNhanVienTuResultSet(rs);
				TaiKhoan tk = new TaiKhoan(rs.getString("MaTaiKhoan"), rs.getString("TenDangNhap"),
						rs.getString("MatKhau"), nv);
				danhSach.add(tk);
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi lấy danh sách tài khoản: " + e.getMessage());
		}
		return danhSach;
	}

	/** 🔹 Thêm tài khoản mới */
	public boolean themTaiKhoan(TaiKhoan tk) {
		connectDB.getInstance();
		String sql = "INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau, MaNhanVien) VALUES (?, ?, ?, ?)";
		try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, tk.getMaTaiKhoan());
			ps.setString(2, tk.getTenDangNhap());
			ps.setString(3, tk.getMatKhau());
			ps.setString(4, tk.getNhanVien().getMaNhanVien());
			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			if (e.getMessage() != null && e.getMessage().contains("UNIQUE"))
				System.err.println("❌ Lỗi: Tên đăng nhập đã tồn tại!");
			else
				System.err.println("❌ Lỗi thêm tài khoản: " + e.getMessage());
		}
		return false;
	}

	/** 🔹 Cập nhật thông tin tài khoản (tên đăng nhập + mật khẩu) */
	public boolean capNhatTaiKhoan(TaiKhoan tk) {
		if (tk == null || tk.getMaTaiKhoan() == null)
			return false;
		connectDB.getInstance();
		String sql = "UPDATE TaiKhoan SET TenDangNhap=?, MatKhau=? WHERE MaTaiKhoan=?";
		try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, tk.getTenDangNhap());
			ps.setString(2, tk.getMatKhau());
			ps.setString(3, tk.getMaTaiKhoan());
			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			if (e.getMessage() != null && e.getMessage().contains("UNIQUE"))
				System.err.println("❌ Lỗi: Tên đăng nhập đã tồn tại!");
			else
				System.err.println("❌ Lỗi cập nhật tài khoản: " + e.getMessage());
		}
		return false;
	}

	/** 🔹 Cập nhật mật khẩu riêng */
	public boolean capNhatMatKhau(String maTaiKhoan, String matKhauMoi) {
		connectDB.getInstance();
		String sql = "UPDATE TaiKhoan SET MatKhau = ? WHERE MaTaiKhoan = ?";
		try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, matKhauMoi);
			ps.setString(2, maTaiKhoan);
			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			System.err.println("❌ Lỗi cập nhật mật khẩu: " + e.getMessage());
		}
		return false;
	}

	/** 🔹 Xóa tài khoản */
	public boolean xoaTaiKhoan(String maTaiKhoan) {
		connectDB.getInstance();
		String sql = "DELETE FROM TaiKhoan WHERE MaTaiKhoan = ?";
		try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, maTaiKhoan);
			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			if (e.getMessage() != null && e.getMessage().contains("FOREIGN KEY"))
				System.err.println("❌ Không thể xóa: tài khoản đang gắn với nhân viên!");
			else
				System.err.println("❌ Lỗi xóa tài khoản: " + e.getMessage());
		}
		return false;
	}

	/** 🔹 Kiểm tra đăng nhập */
	public TaiKhoan dangNhap(String tenDangNhap, String matKhau) {
		TaiKhoan tk = null;
		String sql = """
				    SELECT tk.MaTaiKhoan, tk.TenDangNhap, tk.MatKhau,
				           nv.MaNhanVien, nv.TenNhanVien, nv.GioiTinh, nv.NgaySinh,
				           nv.SoDienThoai, nv.DiaChi, nv.QuanLy, nv.CaLam, nv.TrangThai
				    FROM TaiKhoan tk
				    JOIN NhanVien nv ON tk.MaNhanVien = nv.MaNhanVien
				    WHERE tk.TenDangNhap=? AND tk.MatKhau=?
				""";

		try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, tenDangNhap);
			ps.setString(2, matKhau);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					NhanVien nv = taoNhanVienTuResultSet(rs);
					tk = new TaiKhoan(rs.getString("MaTaiKhoan"), rs.getString("TenDangNhap"), rs.getString("MatKhau"),
							nv);
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi đăng nhập: " + e.getMessage());
		}
		return tk;
	}

	/** 🔹 Kiểm tra tên đăng nhập đã tồn tại */
	public boolean kiemTraTenDangNhapTonTai(String tenDangNhap) {
		connectDB.getInstance();
		String sql = "SELECT 1 FROM TaiKhoan WHERE TenDangNhap = ?";
		try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, tenDangNhap);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi kiểm tra tên đăng nhập: " + e.getMessage());
		}
		return false;
	}

	/** 🔹 Lấy tài khoản theo mã (kèm nhân viên) */
	public TaiKhoan layTaiKhoanTheoMa(String maTaiKhoan) {
		TaiKhoan tk = null;
		String sql = """
				    SELECT tk.MaTaiKhoan, tk.TenDangNhap, tk.MatKhau,
				           nv.MaNhanVien, nv.TenNhanVien, nv.GioiTinh, nv.NgaySinh,
				           nv.SoDienThoai, nv.DiaChi, nv.QuanLy, nv.CaLam, nv.TrangThai
				    FROM TaiKhoan tk
				    JOIN NhanVien nv ON tk.MaNhanVien = nv.MaNhanVien
				    WHERE tk.MaTaiKhoan = ?
				""";

		try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, maTaiKhoan);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					NhanVien nv = taoNhanVienTuResultSet(rs);
					tk = new TaiKhoan(rs.getString("MaTaiKhoan"), rs.getString("TenDangNhap"), rs.getString("MatKhau"),
							nv);
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi lấy tài khoản theo mã: " + e.getMessage());
		}
		return tk;
	}

	public String taoMaTaiKhoanTuDong() {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();
		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String prefix = "TK-" + today + "-";
		String sql = "SELECT TOP 1 MaTaiKhoan FROM TaiKhoan WHERE MaTaiKhoan LIKE ? ORDER BY MaTaiKhoan DESC";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, "TK-" + today + "%");
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					String lastCode = rs.getString(1).trim();
					int lastNum = Integer.parseInt(lastCode.substring(lastCode.lastIndexOf('-') + 1));
					return prefix + String.format("%04d", lastNum + 1);
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Lỗi tạo mã TK tự động: " + e.getMessage());
		} catch (NumberFormatException e) {
			System.err.println("❌ Lỗi parse mã TK: " + e.getMessage());
		}
		return prefix + "0001";
	}

}
