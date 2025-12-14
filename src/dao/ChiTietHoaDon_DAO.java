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

public class ChiTietHoaDon_DAO {

	private final LoSanPham_DAO loSanPhamDAO;
	private final KhuyenMai_DAO khuyenMaiDAO;
	private final DonViTinh_DAO donViTinhDAO;

	public ChiTietHoaDon_DAO() {
		this.loSanPhamDAO = new LoSanPham_DAO();
		this.khuyenMaiDAO = new KhuyenMai_DAO();
		this.donViTinhDAO = new DonViTinh_DAO();
	}

	/**
	 * * Tìm chi tiết hóa đơn theo mã HD, mã lô và mã đơn vị tính.
	 */
	public ChiTietHoaDon timKiemChiTietHoaDonBangMa(String maHD, String maLo, String maDVT) {
	    Connection con = null;
	    PreparedStatement stmt = null;
	    ResultSet rs = null;

	    try {
	        connectDB.getInstance();
	        con = connectDB.getConnection();

	        String sql = """
	            SELECT MaLo, MaKM, SoLuong, GiaBan, MaDonViTinh
	            FROM ChiTietHoaDon
	            WHERE MaHoaDon = ? AND MaLo = ? AND MaDonViTinh = ?
	        """;
	        
	        stmt = con.prepareStatement(sql);
	        stmt.setString(1, maHD);
	        stmt.setString(2, maLo);
	        stmt.setString(3, maDVT);

	        rs = stmt.executeQuery();

	        if (rs.next()) {
	            int soLuong = rs.getInt("SoLuong");
	            double giaBan = rs.getDouble("GiaBan");
	            String maKM = rs.getString("MaKM");
	            String maDonViTinh = rs.getString("MaDonViTinh");

	            HoaDon hd = new HoaDon();
	            hd.setMaHoaDon(maHD);

	            LoSanPham lo = loSanPhamDAO.timLoTheoMa(maLo);
	            KhuyenMai km = (maKM != null ? khuyenMaiDAO.timKhuyenMaiTheoMa(maKM) : null);

	            DonViTinh dvt = (maDonViTinh != null ? donViTinhDAO.timDonViTinhTheoMa(maDonViTinh) : null);

	            if (lo != null) {
	                return new ChiTietHoaDon(hd, lo, soLuong, dvt, giaBan, km);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
	        try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
	    }

	    return null;
	}


	/**
	 * * Lấy danh sách chi tiết theo Mã Hóa Đơn.
	 */
	public List<ChiTietHoaDon> layDanhSachChiTietTheoMaHD(String maHD) {
		List<ChiTietHoaDon> danhSachChiTiet = new ArrayList<>();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		// ✅ List tạm lưu dữ liệu thô từ ResultSet
		class RowData {
			String maLo;
			double soLuong;
			double giaBan;
			String maKM;
			String maDVT;
		}
		List<RowData> rows = new ArrayList<>();

		try {
			connectDB.getInstance();
			con = connectDB.getConnection();

			String sql = "SELECT MaLo, MaKM, SoLuong, GiaBan, MaDonViTinh FROM ChiTietHoaDon WHERE MaHoaDon = ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, maHD);
			rs = stmt.executeQuery();

			// ❗ CHỈ ĐỌC DỮ LIỆU THÔ, KHÔNG GỌI DAO Ở ĐÂY
			while (rs.next()) {
				RowData row = new RowData();
				row.maLo = rs.getString("MaLo");
				row.maKM = rs.getString("MaKM");
				row.soLuong = rs.getDouble("SoLuong");
				row.giaBan = rs.getDouble("GiaBan");
				row.maDVT = rs.getString("MaDonViTinh");
				rows.add(row);
			}

		} catch (Exception e) {
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

		// ✅ SAU KHI ResultSet & Statement ĐÃ ĐÓNG → GIỜ MỚI GỌI DAO KHÁC

		HoaDon hd = new HoaDon();
		hd.setMaHoaDon(maHD);

		for (RowData r : rows) {
			LoSanPham lo = loSanPhamDAO.timLoTheoMa(r.maLo);
			System.out.println(lo.getSanPham());

			KhuyenMai km = null;
			if (r.maKM != null) {
				km = khuyenMaiDAO.timKhuyenMaiTheoMa(r.maKM);
			}

			DonViTinh donViTinh = null;
			if (r.maDVT != null) {
				donViTinh = donViTinhDAO.timDonViTinhTheoMa(r.maDVT);
			}

			if (lo != null) {
				ChiTietHoaDon cthd = new ChiTietHoaDon(hd, lo, r.soLuong, donViTinh, r.giaBan, km);
				danhSachChiTiet.add(cthd);
			}
		}

		return danhSachChiTiet;
	}
	// đếm số sp đã bán trong ngày hiện tại của nhân viên
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

	    try (PreparedStatement stmt = con.prepareStatement(sql)) {
	        stmt.setString(1, maNhanVien);

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("SoSanPham");
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("❌ Lỗi đếm số SP khác nhau bán hôm nay: " + e.getMessage());
	    }

	    return 0;
	}
}