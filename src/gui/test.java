package gui;

import dao.SanPham_DAO;
import entity.SanPham;

public class test {
    public static void main(String[] args) {
        SanPham_DAO sanPhamDAO = new SanPham_DAO();
        String maSanPhamCanTest = "SP-000001"; // ❗ Thay thế bằng Mã Sản Phẩm thực tế

        System.out.println("--- Bắt đầu Test SanPham_DAO ---");

        // 1. Test phương thức laySanPhamTheoMa
        SanPham sp = sanPhamDAO.laySanPhamTheoMa(maSanPhamCanTest);

        if (sp != null) {
            System.out.println("✅ Lấy sản phẩm thành công:");
            System.out.println("   Mã SP: " + sp.getMaSanPham() + " | Tên SP: " + sp.getTenSanPham());
            System.out.println("   Giá Bán: " + sp.getGiaBan()); // Kiểm tra giá bán

            // 2. Kiểm tra thông tin khuyến mãi đi kèm (Giả định SanPham có getter: getKhuyenMaiHienTai())
            try {
                // Giả định: SanPham.java có phương thức getKhuyenMaiHienTai()
                if (sp.getKhuyenMaiHienTai() != null) {
                    System.out.println("🎉 Khuyến mãi đang áp dụng:");
                    System.out.println("   " + sp.getKhuyenMaiHienTai().toString());
                } else {
                    System.out.println("⚠️ Sản phẩm không có khuyến mãi đang hoạt động.");
                }
            } catch (NoSuchMethodError e) {
                System.err.println("❌ Lỗi: Lớp SanPham chưa có phương thức getKhuyenMaiHienTai()!");
                System.err.println("   Hãy kiểm tra và bổ sung thuộc tính/setter vào SanPham.java.");
            }

        } else {
            System.out.println("❌ KHÔNG tìm thấy sản phẩm với mã: " + maSanPhamCanTest);
        }

        System.out.println("--- Kết thúc Test ---");
    }
}