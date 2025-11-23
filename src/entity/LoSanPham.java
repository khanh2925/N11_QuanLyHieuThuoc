package entity;

import java.time.LocalDate;
import java.util.Objects;

public class LoSanPham {

    private String maLo;          
    private LocalDate hanSuDung;   

    // 🔹 Thuộc tính dẫn xuất nhưng được lưu DB để tiện truy vấn nhanh
    private int soLuongTon;        

    private SanPham sanPham;       

    // ===== CONSTRUCTORS =====
    public LoSanPham() {}

    public LoSanPham(String maLo, LocalDate hanSuDung, int soLuongTon, SanPham sanPham) {
        setMaLo(maLo);
        setHanSuDung(hanSuDung);
        setSoLuongTon(soLuongTon);
        setSanPham(sanPham);
    }

    public LoSanPham(String maLo) {
        setMaLo(maLo);
    }

    public LoSanPham(LoSanPham other) {
        this.maLo = other.maLo;
        this.hanSuDung = other.hanSuDung;
        this.soLuongTon = other.soLuongTon;
        this.sanPham = other.sanPham;
    }

    // ===== GETTERS / SETTERS =====
    public String getMaLo() {
        return maLo;
    }

    public void setMaLo(String maLo) {
        if (maLo == null)
            throw new IllegalArgumentException("Mã lô không được để trống");

        maLo = maLo.trim();

        if (!maLo.matches("^LO-\\d{6}$")) {
            throw new IllegalArgumentException("Mã lô không hợp lệ. Định dạng: LO-xxxxxx");
        }

        this.maLo = maLo;
    }

    public LocalDate getHanSuDung() {
        return hanSuDung;
    }

    public void setHanSuDung(LocalDate hanSuDung) {
        if (hanSuDung == null)
            throw new IllegalArgumentException("Hạn sử dụng không được rỗng.");
        if (hanSuDung.isBefore(LocalDate.now().minusYears(50))) // tránh nhập nhầm kiểu 1900
            throw new IllegalArgumentException("Hạn sử dụng không hợp lệ.");
        this.hanSuDung = hanSuDung;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        if (soLuongTon < 0)
            throw new IllegalArgumentException("Số lượng tồn phải ≥ 0.");
        this.soLuongTon = soLuongTon;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        if (sanPham == null)
            throw new IllegalArgumentException("Sản phẩm không được null.");
        this.sanPham = sanPham;
    }

    // ===== NGHIỆP VỤ =====
    /** 🔹 Cập nhật tồn kho an toàn (dùng khi nhập, bán, trả, hủy) */
    public void capNhatSoLuongTon(int delta) {
        int moi = this.soLuongTon + delta;
        if (moi < 0)
            throw new IllegalArgumentException("Không đủ hàng tồn trong kho để thực hiện thao tác.");
        this.soLuongTon = moi;
    }

    /** Kiểm tra lô đã hết hạn hay chưa */
    public boolean isHetHan() {
        return hanSuDung != null && hanSuDung.isBefore(LocalDate.now());
    }

    /** Kiểm tra còn hạn sử dụng hay không */
    public boolean isConHan() {
        return hanSuDung != null && !hanSuDung.isBefore(LocalDate.now());
    }

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        return String.format("Lô %s | HSD: %s | Tồn: %d | %s%s",
                maLo,
                hanSuDung,
                soLuongTon,
                sanPham != null ? sanPham.getMaSanPham() : "Không rõ sản phẩm",
                isHetHan() ? " ⚠️ (Hết hạn)" : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoSanPham)) return false;
        LoSanPham that = (LoSanPham) o;
        return Objects.equals(maLo, that.maLo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maLo);
    }
}
