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


    /** 🔹 Thêm chi tiết phiếu huỷ (chỉ trừ tồn nếu trạng thái = ĐÃ HUỶ [2]) */
    public boolean themChiTietPhieuHuy(ChiTietPhieuHuy ct) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sqlInsert = """
            INSERT INTO ChiTietPhieuHuy (MaPhieuHuy, MaLo, SoLuongHuy, DonGiaNhap, LyDoChiTiet, TrangThai)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        String sqlUpdate = "UPDATE LoSanPham SET SoLuongTon = SoLuongTon - ? WHERE MaLo = ?";

        try {
            con.setAutoCommit(false);

            // 1️⃣ Insert chi tiết
            try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
                ps.setString(1, ct.getPhieuHuy().getMaPhieuHuy());
                ps.setString(2, ct.getLoSanPham().getMaLo());
                ps.setInt(3, ct.getSoLuongHuy());
                ps.setDouble(4, ct.getDonGiaNhap());
                ps.setString(5, ct.getLyDoChiTiet());
                ps.setInt(6, ct.getTrangThai());
                ps.executeUpdate();
            }

            // 2️⃣ Nếu chi tiết đã duyệt (trạng thái = 2), trừ tồn
            if (ct.getTrangThai() == 2) {
                try (PreparedStatement psUpd = con.prepareStatement(sqlUpdate)) {
                    psUpd.setInt(1, ct.getSoLuongHuy());
                    psUpd.setString(2, ct.getLoSanPham().getMaLo());
                    psUpd.executeUpdate();
                }
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi thêm chi tiết phiếu huỷ: " + e.getMessage());
            try { con.rollback(); } catch (SQLException ignored) {}
            return false;
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    /** 🔹 Cập nhật trạng thái chi tiết (khi duyệt / nhập lại kho) */
    public boolean capNhatTrangThaiChiTiet(String maPhieuHuy, String maLo, int trangThaiMoi) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "UPDATE ChiTietPhieuHuy SET TrangThai = ? WHERE MaPhieuHuy = ? AND MaLo = ?";
        String sqlUpdateTon = """
            UPDATE LoSanPham SET SoLuongTon = 
                CASE 
                    WHEN ? = 2 THEN SoLuongTon - (SELECT SoLuongHuy FROM ChiTietPhieuHuy WHERE MaPhieuHuy=? AND MaLo=?)
                    WHEN ? = 3 THEN SoLuongTon + (SELECT SoLuongHuy FROM ChiTietPhieuHuy WHERE MaPhieuHuy=? AND MaLo=?)
                    ELSE SoLuongTon
                END
            WHERE MaLo = ?
        """;

        try {
            con.setAutoCommit(false);

            // 1️⃣ Update trạng thái chi tiết
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, trangThaiMoi);
                ps.setString(2, maPhieuHuy);
                ps.setString(3, maLo);
                ps.executeUpdate();
            }

            // 2️⃣ Cập nhật tồn kho tuỳ theo trạng thái mới
            try (PreparedStatement psTon = con.prepareStatement(sqlUpdateTon)) {
                psTon.setInt(1, trangThaiMoi);
                psTon.setString(2, maPhieuHuy);
                psTon.setString(3, maLo);
                psTon.setInt(4, trangThaiMoi);
                psTon.setString(5, maPhieuHuy);
                psTon.setString(6, maLo);
                psTon.setString(7, maLo);
                psTon.executeUpdate();
            }

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
}
