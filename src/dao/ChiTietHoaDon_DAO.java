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

	/**
	 * * Tìm chi tiết hóa đơn theo mã HD, mã lô và mã đơn vị tính (OPTIMIZED - dùng JOIN).
	 */
	public ChiTietHoaDon timKiemChiTietHoaDonBangMa(String maHD, String maLo, String maDVT) {
	    Connection con = null;
	    PreparedStatement stmt = null;
	    ResultSet rs = null;

	    try {
	        connectDB.getInstance();
	        con = connectDB.getConnection();

	        String sql = """
	            SELECT 
	                cthd.SoLuong, cthd.GiaBan, cthd.ThanhTien,
	                cthd.MaDonViTinh,
	                -- LoSanPham
	                lo.HanSuDung, lo.SoLuongTon,
	                -- SanPham
	                sp.MaSanPham, sp.TenSanPham,
	                -- DonViTinh
	                dvt.TenDonViTinh,
	                -- KhuyenMai
	                km.MaKM, km.TenKM, km.GiaTri, km.HinhThuc
	            FROM ChiTietHoaDon cthd
	            LEFT JOIN LoSanPham lo ON cthd.MaLo = lo.MaLo
	            LEFT JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
	            LEFT JOIN DonViTinh dvt ON cthd.MaDonViTinh = dvt.MaDonViTinh
	            LEFT JOIN KhuyenMai km ON cthd.MaKM = km.MaKM
	            WHERE cthd.MaHoaDon = ? AND cthd.MaLo = ? AND cthd.MaDonViTinh = ?
	        """;
	        
	        stmt = con.prepareStatement(sql);
	        stmt.setString(1, maHD);
	        stmt.setString(2, maLo);
	        stmt.setString(3, maDVT);

	        rs = stmt.executeQuery();

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
	            }

	            // ========== TẠO LÔ ==========
	            LoSanPham lo = new LoSanPham();
	            lo.setMaLo(maLo);
	            if (rs.getDate("HanSuDung") != null)
	                lo.setHanSuDung(rs.getDate("HanSuDung").toLocalDate());
	            lo.setSoLuongTon(rs.getInt("SoLuongTon"));
	            lo.setSanPham(sp);

	            // ========== ĐƠN VỊ TÍNH ==========
	            DonViTinh dvt = null;
	            if (rs.getString("MaDonViTinh") != null) {
	                dvt = new DonViTinh();
	                dvt.setMaDonViTinh(rs.getString("MaDonViTinh"));
	                dvt.setTenDonViTinh(rs.getString("TenDonViTinh"));
	            }

	            // ========== KHUYẾN MÃI ==========
	            KhuyenMai km = null;
	            if (rs.getString("MaKM") != null) {
	                km = new KhuyenMai();
	                km.setMaKM(rs.getString("MaKM"));
	                km.setTenKM(rs.getString("TenKM"));
	                km.setGiaTri(rs.getDouble("GiaTri"));
	                String hinhThuc = rs.getString("HinhThuc");
	                if (hinhThuc != null) {
	                    km.setHinhThuc(HinhThucKM.valueOf(hinhThuc));
	                }
	            }

	            return new ChiTietHoaDon(hd, lo, rs.getInt("SoLuong"), dvt, rs.getDouble("GiaBan"), km);
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
	 * * Lấy danh sách chi tiết theo Mã Hóa Đơn (OPTIMIZED - dùng JOIN).
	 */
	public List<ChiTietHoaDon> layDanhSachChiTietTheoMaHD(String maHD) {
		List<ChiTietHoaDon> danhSachChiTiet = new ArrayList<>();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connectDB.getInstance();
			con = connectDB.getConnection();

			String sql = """
			    SELECT 
			        cthd.MaLo, cthd.SoLuong, cthd.GiaBan, cthd.ThanhTien,
			        cthd.MaDonViTinh,
			        -- LoSanPham
			        lo.HanSuDung, lo.SoLuongTon,
			        -- SanPham
			        sp.MaSanPham, sp.TenSanPham,
			        -- DonViTinh
			        dvt.TenDonViTinh,
			        -- KhuyenMai
			        km.MaKM, km.TenKM, km.GiaTri, km.HinhThuc
			    FROM ChiTietHoaDon cthd
			    LEFT JOIN LoSanPham lo ON cthd.MaLo = lo.MaLo
			    LEFT JOIN SanPham sp ON lo.MaSanPham = sp.MaSanPham
			    LEFT JOIN DonViTinh dvt ON cthd.MaDonViTinh = dvt.MaDonViTinh
			    LEFT JOIN KhuyenMai km ON cthd.MaKM = km.MaKM
			    WHERE cthd.MaHoaDon = ?
			    ORDER BY cthd.MaLo
			    """;
			    
			stmt = con.prepareStatement(sql);
			stmt.setString(1, maHD);
			rs = stmt.executeQuery();

			HoaDon hd = new HoaDon();
			hd.setMaHoaDon(maHD);

			while (rs.next()) {
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
				if (rs.getString("MaDonViTinh") != null) {
					dvt = new DonViTinh();
					dvt.setMaDonViTinh(rs.getString("MaDonViTinh"));
					dvt.setTenDonViTinh(rs.getString("TenDonViTinh"));
				}

				// ========== KHUYẾN MÃI ==========
				KhuyenMai km = null;
				if (rs.getString("MaKM") != null) {
					km = new KhuyenMai();
					km.setMaKM(rs.getString("MaKM"));
					km.setTenKM(rs.getString("TenKM"));
					km.setGiaTri(rs.getDouble("GiaTri"));
					String hinhThuc = rs.getString("HinhThuc");
					if (hinhThuc != null) {
						km.setHinhThuc(HinhThucKM.valueOf(hinhThuc));
					}
				}

				ChiTietHoaDon cthd = new ChiTietHoaDon(hd, lo, rs.getInt("SoLuong"), dvt, rs.getDouble("GiaBan"), km);
				danhSachChiTiet.add(cthd);
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