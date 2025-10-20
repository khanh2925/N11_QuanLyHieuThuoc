package dao;

import connectDB.connectDB;
import entity.PhieuNhap;
// Nếu bạn đã có các class này, giữ nguyên import; nếu package khác, sửa lại:
import entity.NhaCungCap;
import entity.NhanVien;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PhieuNhap_DAO {

	public List<PhieuNhap> findAll() throws Exception {
	    List<PhieuNhap> ds = new ArrayList<>();
	    Connection con = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    try {
	        con = connectDB.getConnection();
	        if (con == null || con.isClosed()) {
	            throw new SQLException("Kết nối cơ sở dữ liệu không khả dụng!");
	        }
	        stmt = con.createStatement();
	        // Lấy đúng các cột cần map theo entity
	        rs = stmt.executeQuery(
	            "SELECT MaPhieuNhap, NgayNhap, MaNhaCungCap, MaNhanVien, TongTien FROM PhieuNhap"
	        );

	        while (rs.next()) {
	            String maPN   = rs.getString(1);
	            java.sql.Date d = rs.getDate(2);
	            java.time.LocalDate ngayNhap = (d != null) ? d.toLocalDate() : null;
	            String maNCC = rs.getString(3);
	            String maNV  = rs.getString(4);
	            double tong  = rs.getDouble(5);
	            if (rs.wasNull()) tong = 0.0;

	            PhieuNhap pn = new PhieuNhap();
	            pn.setMaPhieuNhap(maPN);
	            pn.setNgayNhap(ngayNhap);

	            // map đối tượng lồng bằng mã (nếu entity có các field này)
	            if (maNCC != null) {
	                NhaCungCap ncc = new NhaCungCap();
	                ncc.setMaNhaCungCap(maNCC);
	                pn.setNhaCungCap(ncc);
	            }
	            if (maNV != null) {
	                NhanVien nv = new NhanVien();
	                nv.setMaNhanVien(maNV);
	                pn.setNhanVien(nv);
	            }

	            pn.setTongTien(tong);

	            ds.add(pn);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (stmt != null) stmt.close();
	            // KHÔNG đóng Connection ở đây nếu connectDB giữ connection dùng chung
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	    return ds;
	}


}
