package dao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataPreloader {

    private static boolean isLoaded = false;

    public static void preloadAllData() {
        if (isLoaded)
            return; // Prevent double loading
        isLoaded = true;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            System.out.println("⏳ [DataPreloader] Bắt đầu tải dữ liệu ngầm...");
            long start = System.currentTimeMillis();

            try {
                // Batch 1: Basic Dictionaries (Ít phụ thuộc)
                new DonViTinh_DAO().layTatCaDonViTinh();
                new NhanVien_DAO().layTatCaNhanVien();
                new NhaCungCap_DAO().layTatCaNhaCungCap();
                new KhachHang_DAO().layTatCaKhachHang();
                new TaiKhoan_DAO().layTatCaTaiKhoan();

                // Batch 2: Products & Pricing (Nặng hơn)
                new SanPham_DAO().layTatCaSanPham();
                new BangGia_DAO().layTatCaBangGia();
                new LoSanPham_DAO().layTatCaLoSanPham();
                new QuyCachDongGoi_DAO().layTatCaQuyCachDongGoi();
                new KhuyenMai_DAO().layTatCaKhuyenMai();

                // Batch 3: Transaction History (Nặng nhất)
                // Lưu ý: Các DAO này có thể chưa có cache full list hoặc list quá lớn
                // Chỉ load nếu đã cài đặt cache
                new HoaDon_DAO().layTatCaHoaDon();
                new PhieuNhap_DAO().layDanhSachPhieuNhap();
                new PhieuTra_DAO().layTatCaPhieuTra();
                new PhieuHuy_DAO().layTatCaPhieuHuy();

            } catch (Exception e) {
                System.err.println("❌ [DataPreloader] Lỗi khi tải dữ liệu ngầm: " + e.getMessage());
                e.printStackTrace();
            }

            long end = System.currentTimeMillis();
            System.out.println("✅ [DataPreloader] Hoàn tất tải dữ liệu trong: " + (end - start) + "ms");
            executor.shutdown();
        });
    }
}
