package dao;

import connectDB.connectDB;
import entity.ChiTietPhieuTra;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuTra;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PhieuTra_DAO {

	public PhieuTra_DAO() {
	}

	// ===== Lấy 1 phiếu trả theo mã (kèm chi tiết) =====
	public PhieuTra timKiemPhieuTraBangMa(String maPhieuTra) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = "SELECT MaPhieuTra, NgayLap, MaNhanVien, MaKhachHang, TongTienHoan, DaDuyet "
				+ "FROM PhieuTra WHERE MaPhieuTra = ?";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maPhieuTra);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					String maNV = rs.getString("MaNhanVien");
					String maKH = rs.getString("MaKhachHang");
					LocalDate ngayLap = rs.getDate("NgayLap").toLocalDate();
					boolean daDuyet = rs.getBoolean("DaDuyet");

					NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
					KhachHang_DAO khachHangDAO = new KhachHang_DAO();
					NhanVien nv = nhanVienDAO.timNhanVienTheoMa(maNV);
					KhachHang kh = khachHangDAO.timKhachHangTheoMa(maKH);

					ChiTietPhieuTra_DAO ctDAO = new ChiTietPhieuTra_DAO();
					List<ChiTietPhieuTra> chiTietList = ctDAO.timKiemChiTietBangMaPhieuTra(maPhieuTra);

					return new PhieuTra(maPhieuTra, kh, nv, ngayLap, daDuyet, chiTietList);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// ===== Lấy tất cả phiếu trả (kèm chi tiết) =====
	public List<PhieuTra> layTatCaPhieuTra() {
		List<PhieuTra> ds = new ArrayList<>();
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String sql = "SELECT MaPhieuTra FROM PhieuTra ORDER BY NgayLap DESC, MaPhieuTra DESC";

		try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) {
				String maPT = rs.getString("MaPhieuTra");
				PhieuTra pt = timKiemPhieuTraBangMa(maPT);
				if (pt != null)
					ds.add(pt);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ds;
	}

	// ============================================================
	//  Hàm thêm Phiếu trả + Chi tiết (Transaction trong DAO)
	// ============================================================
	public boolean themPhieuTraVaChiTiet(PhieuTra pt, List<ChiTietPhieuTra> dsChiTiet) {
		Connection con = connectDB.getConnection();
		PreparedStatement psPT = null, psCT = null;
		boolean ok = false;

		try {
			con.setAutoCommit(false);

			// === 1. Insert Phiếu trả ===
			String sqlPT = "INSERT INTO PhieuTra(maPhieuTra, maNhanVien, maKhachHang, ngayLap, tongTienHoan, daDuyet) "
					+ "VALUES (?, ?, ?, ?, ?, ?)";
			psPT = con.prepareStatement(sqlPT);
			psPT.setString(1, pt.getMaPhieuTra());
			psPT.setString(2, pt.getNhanVien().getMaNhanVien());
			psPT.setString(3, pt.getKhachHang().getMaKhachHang());
			psPT.setDate(4, java.sql.Date.valueOf(pt.getNgayLap())); // LocalDate -> SQL Date
			psPT.setDouble(5, pt.getTongTienHoan());
			psPT.setBoolean(6, pt.isDaDuyet());
			psPT.executeUpdate();

			// === 2. Insert Chi tiết phiếu trả ===
			String sqlCT = "INSERT INTO ChiTietPhieuTra(maPhieuTra, maHoaDon, maLo, soLuong, thanhTienHoan, lyDoChiTiet, trangThai) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
			psCT = con.prepareStatement(sqlCT);
			for (ChiTietPhieuTra ct : dsChiTiet) {
				psCT.setString(1, pt.getMaPhieuTra());
				psCT.setString(2, ct.getChiTietHoaDon().getHoaDon().getMaHoaDon());
				psCT.setString(3, ct.getChiTietHoaDon().getLoSanPham().getMaLo());
				psCT.setDouble(4, ct.getSoLuong());
				psCT.setDouble(5, ct.getThanhTienHoan());
				psCT.setString(6, ct.getLyDoChiTiet());
				psCT.setInt(7, ct.getTrangThai());
				psCT.addBatch();
			}
			psCT.executeBatch();

			con.commit();
			ok = true;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				con.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		} finally {
			try {
				con.setAutoCommit(true);
				if (psPT != null)
					psPT.close();
				if (psCT != null)
					psCT.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return ok;
	}

	// ===== Cập nhật trạng thái đã duyệt =====
	public boolean capNhatTrangThai(String maPhieuTra, boolean daDuyetMoi) {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();
		String sql = "UPDATE PhieuTra SET DaDuyet = ? WHERE MaPhieuTra = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setBoolean(1, daDuyetMoi);
			ps.setString(2, maPhieuTra);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// ===== Tạo mã PTxxxxxx =====
//    public String taoMaPhieuTra() {
//        connectDB.getInstance();
//        Connection con = connectDB.getConnection();
//        String prefix = "PT";
//
//        String sql = "SELECT MAX(MaPhieuTra) AS MaxMa FROM PhieuTra WHERE MaPhieuTra LIKE 'PT%%%%%%'";
//        try (PreparedStatement ps = con.prepareStatement(sql);
//             ResultSet rs = ps.executeQuery()) {
//
//            if (rs.next()) {
//                String lastID = rs.getString("MaxMa");
//                if (lastID != null) {
//                    int lastNum = Integer.parseInt(lastID.substring(prefix.length()));
//                    return String.format("%s%06d", prefix, lastNum + 1);
//                }
//            }
//            return prefix + "000001";
//        } catch (SQLException | NumberFormatException e) {
//            e.printStackTrace();
//            return prefix + "000001";
//        }
//    }

	public String taoMaPhieuTra() {
		connectDB.getInstance();
		Connection con = connectDB.getConnection();

		String prefix = "PT-";
		String ngayHomNay = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
		String likePattern = prefix + ngayHomNay + "-%";

		String sql = "SELECT MAX(MaPhieuTra) AS MaxMa " + "FROM PhieuTra " + "WHERE MaPhieuTra LIKE ?";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, likePattern);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					String lastID = rs.getString("MaxMa");
					if (lastID != null) {
						int lastNum = Integer.parseInt(lastID.substring(lastID.lastIndexOf('-') + 1).trim());
						return String.format("%s%s-%04d", prefix, ngayHomNay, lastNum + 1);
					}
				}
			}
		} catch (SQLException | NumberFormatException e) {
			e.printStackTrace();
		}

		return String.format("%s%s-%04d", prefix, ngayHomNay, 1);
	}

}
