package entity;

import java.util.Objects;
import enums.DuongDung;
import enums.LoaiSanPham;
import entity.ChiTietKhuyenMaiSanPham; // 💡 Bổ sung import

public class SanPham {

    private String maSanPham; // SP-xxxxxx
    private String tenSanPham;
    private LoaiSanPham loaiSanPham;
    private String soDangKy;
    private DuongDung duongDung;
    private double giaNhap;
    private double giaBan; // ✅ dẫn xuất theo bảng giá
    private String hinhAnh;
    private String keBanSanPham;
    private boolean hoatDong;

    private ChiTietBangGia chiTietBangGiaHienTai; // 🔗 bảng giá đang áp dụng
    private ChiTietKhuyenMaiSanPham khuyenMaiHienTai; // 💡 BỔ SUNG: Khuyến mãi đang áp dụng

    // ===== CONSTRUCTORS =====
    public SanPham() {}

    public SanPham(String maSanPham) {
        setMaSanPham(maSanPham);
    }

    public SanPham(String maSanPham, String tenSanPham, LoaiSanPham loaiSanPham, String soDangKy,
                   DuongDung duongDung, double giaNhap, String hinhAnh,
                   String keBanSanPham, boolean hoatDong) {
        setMaSanPham(maSanPham);
        setTenSanPham(tenSanPham);
        setLoaiSanPham(loaiSanPham);
        setSoDangKy(soDangKy);
        setDuongDung(duongDung);
        setGiaNhap(giaNhap);
        setHinhAnh(hinhAnh);
        setKeBanSanPham(keBanSanPham);
        setHoatDong(hoatDong);
        this.giaBan = 0; // chưa có bảng giá → giá bán = 0
    }

    public SanPham(SanPham sp) {
        this.maSanPham = sp.maSanPham;
        this.tenSanPham = sp.tenSanPham;
        this.loaiSanPham = sp.loaiSanPham;
        this.soDangKy = sp.soDangKy;
        this.duongDung = sp.duongDung;
        this.giaNhap = sp.giaNhap;
        this.giaBan = sp.giaBan;
        this.hinhAnh = sp.hinhAnh;
        this.keBanSanPham = sp.keBanSanPham;
        this.hoatDong = sp.hoatDong;
        this.chiTietBangGiaHienTai = sp.chiTietBangGiaHienTai;
        this.khuyenMaiHienTai = sp.khuyenMaiHienTai; // 💡 Sao chép KM
    }

    // ===== GETTERS / SETTERS =====
    public String getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        if (maSanPham == null)
            throw new IllegalArgumentException("Mã sản phẩm không được để trống");

        maSanPham = maSanPham.trim();

        if (!maSanPham.matches("^SP-\\d{6}$")) {
            throw new IllegalArgumentException("Mã sản phẩm không hợp lệ. Định dạng: SP-xxxxxx");
        }

        this.maSanPham = maSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        if (tenSanPham == null || tenSanPham.trim().isEmpty())
            throw new IllegalArgumentException("Tên sản phẩm không được rỗng.");
        if (tenSanPham.length() > 100)
            throw new IllegalArgumentException("Tên sản phẩm không được vượt quá 100 ký tự.");
        this.tenSanPham = tenSanPham.trim();
    }

    public LoaiSanPham getLoaiSanPham() {
        return loaiSanPham;
    }

    public void setLoaiSanPham(LoaiSanPham loaiSanPham) {
        if (loaiSanPham == null)
            throw new IllegalArgumentException("Loại sản phẩm không được null.");
        this.loaiSanPham = loaiSanPham;
    }

    public String getSoDangKy() {
        return soDangKy;
    }

    public void setSoDangKy(String soDangKy) {
        if (soDangKy != null && soDangKy.length() > 20)
            throw new IllegalArgumentException("Số đăng ký không hợp lệ (tối đa 20 ký tự).");
        this.soDangKy = soDangKy;
    }

    public DuongDung getDuongDung() {
        return duongDung;
    }

    public void setDuongDung(DuongDung duongDung) {
        this.duongDung = duongDung;
    }

    public double getGiaNhap() {
        return giaNhap;
    }

    public void setGiaNhap(double giaNhap) {
        if (giaNhap <= 0)
            throw new IllegalArgumentException("Giá nhập phải lớn hơn 0.");
        this.giaNhap = giaNhap;
        capNhatGiaBanTheoTiLe();
    }

    public double getGiaBan() {
        if (chiTietBangGiaHienTai == null)
        	giaBan = 0;
        return giaBan;
    }

    public ChiTietBangGia getChiTietBangGiaHienTai() {
        return chiTietBangGiaHienTai;
    }

    public void setChiTietBangGiaHienTai(ChiTietBangGia chiTietBangGiaHienTai) {
        if (chiTietBangGiaHienTai == null)
            throw new IllegalArgumentException("Sản phẩm phải có bảng giá để xác định giá bán.");
        this.chiTietBangGiaHienTai = chiTietBangGiaHienTai;
        capNhatGiaBanTheoTiLe();
    }

    // ✅ Cập nhật giá bán dựa theo tỉ lệ bảng giá
    public void capNhatGiaBanTheoTiLe() {
        if (chiTietBangGiaHienTai == null) {
            this.giaBan = 0;
            return;
        }
        double tiLe = chiTietBangGiaHienTai.getTiLe();
        if (tiLe <= 0)
            throw new IllegalArgumentException("Tỉ lệ bảng giá không hợp lệ (phải > 0).");
        this.giaBan = Math.round(giaNhap * tiLe);
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        if (hinhAnh != null && hinhAnh.length() > 255)
            throw new IllegalArgumentException("Đường dẫn hình ảnh không được vượt quá 255 ký tự.");
        this.hinhAnh = hinhAnh;
    }

    public String getKeBanSanPham() {
        return keBanSanPham;
    }

    public void setKeBanSanPham(String keBanSanPham) {
        if (keBanSanPham != null && keBanSanPham.length() > 100)
            throw new IllegalArgumentException("Kệ bán sản phẩm không được vượt quá 100 ký tự.");
        this.keBanSanPham = keBanSanPham;
    }

    public boolean isHoatDong() {
        return hoatDong;
    }

    public void setHoatDong(boolean hoatDong) {
        this.hoatDong = hoatDong;
    }
    
    // 💡 GETTER / SETTER BỔ SUNG CHO KHUYẾN MÃI
    public ChiTietKhuyenMaiSanPham getKhuyenMaiHienTai() {
        return khuyenMaiHienTai;
    }

    public void setKhuyenMaiHienTai(ChiTietKhuyenMaiSanPham khuyenMaiHienTai) {
        this.khuyenMaiHienTai = khuyenMaiHienTai;
    }

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        // Có thể bổ sung hiển thị KM vào đây nếu cần
        return String.format(
            "SanPham[%s - %s, loại=%s, giá nhập=%.0f, tỉ lệ=%s, giá bán=%.0f, KM=%s]",
            maSanPham,
            tenSanPham,
            loaiSanPham != null ? loaiSanPham : "N/A",
            giaNhap,
            chiTietBangGiaHienTai != null ? chiTietBangGiaHienTai.getTiLe() : "Chưa có bảng giá",
            giaBan,
            khuyenMaiHienTai != null ? khuyenMaiHienTai.getKhuyenMai().getMaKM() : "Không"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SanPham)) return false;
        SanPham sp = (SanPham) o;
        return Objects.equals(maSanPham, sp.maSanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maSanPham);
    }
}