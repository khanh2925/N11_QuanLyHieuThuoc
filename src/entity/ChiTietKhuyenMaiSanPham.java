package entity;

import java.util.Objects;
import enums.HinhThucKM;

public class ChiTietKhuyenMaiSanPham {

    private SanPham sanPham;
    private KhuyenMai khuyenMai;
    private int soLuongToiThieu;     // ✅ Số lượng tối thiểu để được khuyến mãi
    private int soLuongTangThem;     // ✅ Số lượng tặng thêm (nếu hình thức = TANG_THEM)

    // ===== CONSTRUCTORS =====
    public ChiTietKhuyenMaiSanPham() {}

    public ChiTietKhuyenMaiSanPham(SanPham sanPham, KhuyenMai khuyenMai,
                                   int soLuongToiThieu, int soLuongTangThem) {
        setSanPham(sanPham);
        setKhuyenMai(khuyenMai);
        setSoLuongToiThieu(soLuongToiThieu);
        setSoLuongTangThem(soLuongTangThem);
        apDungRangBuocTheoLoaiKM(); // ✅ tự điều chỉnh logic theo loại khuyến mãi
    }

    // ===== GETTERS / SETTERS =====
    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        if (sanPham == null)
            throw new IllegalArgumentException("Sản phẩm không được null.");
        this.sanPham = sanPham;
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        if (khuyenMai == null)
            throw new IllegalArgumentException("Khuyến mãi không được null." + khuyenMai);
        if (khuyenMai.isKhuyenMaiHoaDon())
            throw new IllegalArgumentException("Không thể gán khuyến mãi hóa đơn cho sản phẩm." + khuyenMai);
        if (!khuyenMai.isDangHoatDong())
            throw new IllegalArgumentException("Không thể gán khuyến mãi đã hết hạn hoặc ngừng hoạt động." + khuyenMai);
        this.khuyenMai = khuyenMai;
    }

    public int getSoLuongToiThieu() {
        return soLuongToiThieu;
    }

    public void setSoLuongToiThieu(int soLuongToiThieu) {
        if (soLuongToiThieu < 0)
            throw new IllegalArgumentException("Số lượng tối thiểu phải ≥ 0.");
        this.soLuongToiThieu = soLuongToiThieu;
    }

    public int getSoLuongTangThem() {
        return soLuongTangThem;
    }

    public void setSoLuongTangThem(int soLuongTangThem) {
        if (soLuongTangThem < 0)
            throw new IllegalArgumentException("Số lượng tặng thêm phải ≥ 0.");
        this.soLuongTangThem = soLuongTangThem;
    }

    // ===== NGHIỆP VỤ =====
    private void apDungRangBuocTheoLoaiKM() {
        if (khuyenMai == null || khuyenMai.getHinhThuc() == null) return;

        HinhThucKM hinhThuc = khuyenMai.getHinhThuc();

        switch (hinhThuc) {
            case TANG_THEM -> {
                if (soLuongToiThieu <= 0) {
                    throw new IllegalArgumentException("Khuyến mãi TẶNG THÊM yêu cầu số lượng tối thiểu > 0. Lỗi ở khuyến mãi: " + this.khuyenMai.getMaKM());
                }
                if (soLuongTangThem <= 0)
                    throw new IllegalArgumentException("Khuyến mãi TẶNG THÊM yêu cầu số lượng tặng thêm > 0. Lỗi ở khuyến mãi: " + this.khuyenMai.getMaKM());
                khuyenMai.setGiaTri(0); // ❗ Không có giá trị giảm, chỉ tặng thêm
            }
            case GIAM_GIA_TIEN, GIAM_GIA_PHAN_TRAM -> {
                // reset các giá trị không liên quan
                if (soLuongToiThieu != 0 || soLuongTangThem != 0) {
                    this.soLuongToiThieu = 0;
                    this.soLuongTangThem = 0;
                }
            }
            default -> throw new IllegalArgumentException("Hình thức khuyến mãi không hợp lệ.");
        }
    }

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        return String.format(
            "CTKM{KM='%s', SP='%s', Hình thức=%s, SLTT=%d, SLTặng=%d}",
            khuyenMai != null ? khuyenMai.getMaKM() : "N/A",
            sanPham != null ? sanPham.getTenSanPham() : "N/A",
            khuyenMai != null ? khuyenMai.getHinhThuc() : "N/A",
            soLuongToiThieu, soLuongTangThem
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietKhuyenMaiSanPham)) return false;
        ChiTietKhuyenMaiSanPham that = (ChiTietKhuyenMaiSanPham) o;
        return Objects.equals(sanPham, that.sanPham) &&
               Objects.equals(khuyenMai, that.khuyenMai);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sanPham, khuyenMai);
    }
}
