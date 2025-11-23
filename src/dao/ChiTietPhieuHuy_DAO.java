package dao;

import connectDB.connectDB;
import entity.ChiTietPhieuHuy;
import entity.LoSanPham;
import entity.PhieuHuy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietPhieuHuy_DAO {

    public ChiTietPhieuHuy_DAO() {}

    /** 🔹 Lấy danh sách chi tiết phiếu huỷ theo mã phiếu */
    public List<ChiTietPhieuHuy> timKiemChiTietPhieuHuyBangMa(String maPhieuHuy) {
        List<ChiTietPhieuHuy> danhSachChiTiet = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = """
            SELECT MaLo, SoLuongHuy, DonGiaNhap, LyDoChiTiet, TrangThai
            FROM ChiTietPhieuHuy
            WHERE MaPhieuHuy = ?
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maPhieuHuy);
            ResultSet rs = stmt.executeQuery();

            LoSanPham_DAO loDAO = new LoSanPham_DAO();
            PhieuHuy ph = new PhieuHuy();
            ph.setMaPhieuHuy(maPhieuHuy);

            while (rs.next()) {
                String maLo = rs.getString("MaLo");
                int soLuongHuy = rs.getInt("SoLuongHuy");
                double donGiaNhap = rs.getDouble("DonGiaNhap");
                String lyDo = rs.getString("LyDoChiTiet");
                int trangThai = rs.getInt("TrangThai");

                LoSanPham lo = loDAO.timLoTheoMa(maLo);
                if (lo != null) {
                    ChiTietPhieuHuy ct = new ChiTietPhieuHuy(ph, lo, soLuongHuy, donGiaNhap, lyDo, trangThai);
                    ct.setTrangThai(trangThai);
                    danhSachChiTiet.add(ct);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi tìm chi tiết phiếu huỷ: " + e.getMessage());
        }
        return danhSachChiTiet;
    }


   

  
    public boolean capNhatTrangThaiChiTiet(String maPhieuHuy, String maLo, int trangThaiMoi) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sqlUpdateTrangThai = 
            "UPDATE ChiTietPhieuHuy SET TrangThai = ? WHERE MaPhieuHuy = ? AND MaLo = ?";

        // ❗ CHỈ cập nhật tồn khi TRẠNG THÁI MỚI = 2 (hủy hàng)
        String sqlCapNhatTon = """
            UPDATE LoSanPham SET SoLuongTon = 
                SoLuongTon - (SELECT SoLuongHuy 
                              FROM ChiTietPhieuHuy 
                              WHERE MaPhieuHuy=? AND MaLo=?)
            WHERE MaLo = ?
        """;

        try {
            con.setAutoCommit(false);

            // 1️⃣ Update trạng thái
            try (PreparedStatement ps = con.prepareStatement(sqlUpdateTrangThai)) {
                ps.setInt(1, trangThaiMoi);
                ps.setString(2, maPhieuHuy);
                ps.setString(3, maLo);
                ps.executeUpdate();
            }

            // 2️⃣ Chỉ khi trạng thái mới = 2 (HỦY HÀNG) mới trừ tồn
            if (trangThaiMoi == 2) {
                try (PreparedStatement psTon = con.prepareStatement(sqlCapNhatTon)) {
                    psTon.setString(1, maPhieuHuy);
                    psTon.setString(2, maLo);
                    psTon.setString(3, maLo);
                    psTon.executeUpdate();
                }
            }

            // 3️⃣ Nếu trạng thái = 3 (TỪ CHỐI) → ❌ KHÔNG cập nhật tồn

            con.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi cập nhật trạng thái chi tiết phiếu huỷ: " + e.getMessage());
            try { con.rollback(); } catch (SQLException ignored) {}
            return false;
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }


    /** 🔹 Xoá chi tiết (và hoàn tồn nếu cần) */
    public boolean xoaChiTietPhieuHuy(ChiTietPhieuHuy ct) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sqlDelete = "DELETE FROM ChiTietPhieuHuy WHERE MaPhieuHuy = ? AND MaLo = ?";
        String sqlUpdate = "UPDATE LoSanPham SET SoLuongTon = SoLuongTon + ? WHERE MaLo = ?";

        try {
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sqlDelete)) {
                ps.setString(1, ct.getPhieuHuy().getMaPhieuHuy());
                ps.setString(2, ct.getLoSanPham().getMaLo());
                ps.executeUpdate();
            }

            // Nếu chi tiết đã trừ tồn (trạng thái = 2) thì cộng lại
            if (ct.getTrangThai() == 2) {
                try (PreparedStatement psTon = con.prepareStatement(sqlUpdate)) {
                    psTon.setInt(1, ct.getSoLuongHuy());
                    psTon.setString(2, ct.getLoSanPham().getMaLo());
                    psTon.executeUpdate();
                }
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi xoá chi tiết phiếu huỷ: " + e.getMessage());
            try { con.rollback(); } catch (SQLException ignored) {}
            return false;
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }
    
    /** 🔹 Trả về true nếu TẤT CẢ chi tiết của phiếu đã khác 'Chờ duyệt' */
    public boolean tatCaChiTietDaXuLy(String maPhieuHuy) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = """
            SELECT COUNT(*) 
            FROM ChiTietPhieuHuy 
            WHERE MaPhieuHuy = ? AND TrangThai = 1   -- 1 = Chờ duyệt
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPhieuHuy);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int soChoDuyet = rs.getInt(1);
                    // Nếu KHÔNG còn dòng nào 'Chờ duyệt' => mọi chi tiết đã xử lý
                    return soChoDuyet == 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi kiểm tra trạng thái chi tiết PH: " + e.getMessage());
        }
        // Lỡ lỗi gì thì coi như chưa xử lý hết
        return false;
    }

}
