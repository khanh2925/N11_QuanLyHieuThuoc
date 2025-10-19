package dao;

import connectDB.connectDB;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class ChiTietPhieuHuy_DAO {

	public static class CTPHView {
		private String maLo, maSanPham, tenSanPham, tenDonViTinh, lyDo;
		private LocalDate hanSuDung;
		private int soLuongHuy;

		public String getMaLo() {
			return maLo;
		}

		public String getMaSanPham() {
			return maSanPham;
		}

		public String getTenSanPham() {
			return tenSanPham;
		}

		public String getTenDonViTinh() {
			return tenDonViTinh;
		}

		public LocalDate getHanSuDung() {
			return hanSuDung;
		}

		public int getSoLuongHuy() {
			return soLuongHuy;
		}

		public String getLyDo() {
			return lyDo;
		}
	}

	/** Lấy chi tiết 1 phiếu hủy */
	public List<CTPHView> findByMaPhieu(String maPhieuHuy) throws SQLException {
		List<CTPHView> out = new ArrayList<>();
		Connection con = connectDB.getConnection();
		final String sql = "SELECT ct.MaLo, sp.MaSanPham, sp.TenSanPham, "
				+ "       ISNULL(dvt.TenDonViTinh, N'') AS TenDonViTinh, "
				+ "       lsp.HanSuDung, ct.SoLuongHuy, ISNULL(ct.LyDoChiTiet, N'') AS LyDoChiTiet "
				+ "FROM ChiTietPhieuHuy ct " + "JOIN LoSanPham lsp ON lsp.MaLo = ct.MaLo "
				+ "JOIN SanPham sp    ON sp.MaSanPham = lsp.MaSanPham "
				+ "LEFT JOIN DonViTinh dvt ON dvt.MaDonViTinh = sp.MaDonViTinh " + "WHERE ct.MaPhieuHuy = ? "
				+ "ORDER BY ct.MaLo";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maPhieuHuy);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					CTPHView v = new CTPHView();
					v.maLo = rs.getString("MaLo");
					v.maSanPham = rs.getString("MaSanPham");
					v.tenSanPham = rs.getString("TenSanPham");
					v.tenDonViTinh = rs.getString("TenDonViTinh");
					Date d = rs.getDate("HanSuDung");
					v.hanSuDung = d != null ? d.toLocalDate() : null;
					v.soLuongHuy = rs.getInt("SoLuongHuy");
					v.lyDo = rs.getString("LyDoChiTiet");
					out.add(v);
				}
			}
		}
		return out;
	}
}
