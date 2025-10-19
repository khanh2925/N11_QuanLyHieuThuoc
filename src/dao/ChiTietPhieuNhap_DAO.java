package dao;

import connectDB.connectDB;
import entity.ChiTietPhieuNhap;
import entity.PhieuNhap;
import entity.LoSanPham; // nếu package khác, sửa import

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietPhieuNhap_DAO {

	// DTO (view-model) cho GUI
	public static class CTPNView {
		public final String maLo;
		public final String maSanPham;
		public final String tenSanPham;
		public final int soLuongNhap;
		public final double donGiaNhap;
		public final java.sql.Date ngayNhap;

		public CTPNView(String maLo, String maSP, String tenSP, int sl, double gia, java.sql.Date ngay) {
			this.maLo = maLo;
			this.maSanPham = maSP;
			this.tenSanPham = tenSP;
			this.soLuongNhap = sl;
			this.donGiaNhap = gia;
			this.ngayNhap = ngay;
		}
	}

	public List<CTPNView> findViewByMaPhieu(String maPhieuNhap) throws SQLException {
		String sql = "SELECT ctpn.MaLo, lsp.MaSanPham, sp.TenSanPham, "
				+ "       ctpn.SoLuongNhap, ctpn.DonGiaNhap, ctpn.NgayNhap " + "FROM ChiTietPhieuNhap ctpn "
				+ "JOIN LoSanPham lsp   ON lsp.MaLo = ctpn.MaLo "
				+ "JOIN SanPham sp      ON sp.MaSanPham = lsp.MaSanPham " + "WHERE ctpn.MaPhieuNhap = ? "
				+ "ORDER BY ctpn.NgayNhap, ctpn.MaLo";

		Connection con = connectDB.getConnection();
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maPhieuNhap);
			try (ResultSet rs = ps.executeQuery()) {
				List<CTPNView> out = new ArrayList<>();
				while (rs.next()) {
					out.add(new CTPNView(rs.getString("MaLo"), rs.getString("MaSanPham"), rs.getString("TenSanPham"),
							rs.getInt("SoLuongNhap"), rs.getDouble("DonGiaNhap"), rs.getDate("NgayNhap")));
				}
				return out;
			}
		}
	}
}
