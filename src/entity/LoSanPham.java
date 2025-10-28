package entity;

import java.time.LocalDate;
import java.util.Objects;


public class LoSanPham {

    private String maLo;           // VD: LO-000001
    private LocalDate hanSuDung;   // Hạn sử dụng
    private int soLuongNhap;       // Số lượng nhập ban đầu
    private int soLuongTon;        // Số lượng tồn hiện tại
    private SanPham sanPham;       // FK: Sản phẩm tương ứng

    // ===== CONSTRUCTORS =====
    public LoSanPham() {}

    public LoSanPham(String maLo, LocalDate hanSuDung,
                     int soLuongNhap, int soLuongTon, SanPham sanPham) {
        setMaLo(maLo);
        setHanSuDung(hanSuDung);
        setSoLuongNhap(soLuongNhap);
        setSoLuongTon(soLuongTon);
        setSanPham(sanPham);
    }

    public LoSanPham(String maLo) {
        setMaLo(maLo);
    }

    public LoSanPham(LoSanPham other) {
        this.maLo = other.maLo;
        this.hanSuDung = other.hanSuDung;
        this.soLuongNhap = other.soLuongNhap;
        this.soLuongTon = other.soLuongTon;
        this.sanPham = other.sanPham;
    }

    // ===== GETTERS / SETTERS =====
    public String getMaLo() {
        return maLo;
    }

    public void setMaLo(String maLo) {
        if (maLo == null || !maLo.matches("^LO-\\d{6}$"))
            throw new IllegalArgumentException("Mã lô không hợp lệ (định dạng: LO-xxxxxx).");
        this.maLo = maLo;
    }

    public LocalDate getHanSuDung() {
        return hanSuDung;
    }

    public void setHanSuDung(LocalDate hanSuDung) {
        if (hanSuDung == null)
            throw new IllegalArgumentException("Hạn sử dụng không được rỗng.");
        this.hanSuDung = hanSuDung;
    }

    public int getSoLuongNhap() {
        return soLuongNhap;
    }

    public void setSoLuongNhap(int soLuongNhap) {
        if (soLuongNhap < 0)
            throw new IllegalArgumentException("Số lượng nhập phải >= 0.");
        this.soLuongNhap = soLuongNhap;
        kiemTraSoLuongHopLe();
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        if (soLuongTon < 0)
            throw new IllegalArgumentException("Số lượng tồn phải >= 0.");
        this.soLuongTon = soLuongTon;
        kiemTraSoLuongHopLe();
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        if (sanPham == null)
            throw new IllegalArgumentException("Sản phẩm không được null.");
        this.sanPham = sanPham;
    }

    // ===== VALIDATION =====
    private void kiemTraSoLuongHopLe() {
        if (soLuongTon > soLuongNhap)
            throw new IllegalArgumentException("Số lượng tồn không được vượt quá số lượng nhập.");
    }


    /**
     * Kiểm tra lô đã hết hạn hay chưa.
     * @return true nếu hạn sử dụng đã qua ngày hiện tại, false nếu còn hạn.
     */
    public boolean isHetHan() {
        return hanSuDung != null && hanSuDung.isBefore(LocalDate.now());
    }

    /**
     * Kiểm tra còn hạn (đảo ngược của isHetHan).
     */
    public boolean isConHan() {
        return hanSuDung != null && !hanSuDung.isBefore(LocalDate.now());
    }

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        return String.format("Lô %s | HSD: %s | Nhập: %d | Tồn: %d | %s%s",
                maLo,
                hanSuDung,
                soLuongNhap,
                soLuongTon,
                sanPham != null ? sanPham.getTenSanPham() : "Không rõ sản phẩm",
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
