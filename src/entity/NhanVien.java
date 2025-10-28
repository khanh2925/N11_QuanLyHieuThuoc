package entity;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public class NhanVien {

    private String maNhanVien;
    private String tenNhanVien;
    private boolean gioiTinh;
    private LocalDate ngaySinh;
    private String soDienThoai;
    private String diaChi;
    private boolean quanLy;
    private String caLam;
    private boolean trangThai;

    // ===== CONSTRUCTORS =====
    public NhanVien(String maNhanVien, String tenNhanVien, boolean gioiTinh,
                    LocalDate ngaySinh, String soDienThoai, String diaChi,
                    boolean quanLy, String caLam, boolean trangThai) {
        setMaNhanVien(maNhanVien);
        setTenNhanVien(tenNhanVien);
        setGioiTinh(gioiTinh);
        setNgaySinh(ngaySinh);
        setSoDienThoai(soDienThoai);
        setDiaChi(diaChi);
        setQuanLy(quanLy);
        setCaLam(caLam);
        setTrangThai(trangThai);
    }

    public NhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public NhanVien(String maNhanVien, String tenNhanVien) {
		this.maNhanVien = maNhanVien;
		this.tenNhanVien = tenNhanVien;
	}

	// Constructor rút gọn dùng khi login hoặc join
    public NhanVien(String maNhanVien, String tenNhanVien, String caLam, boolean trangThai) {
        this.maNhanVien = maNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.caLam = caLam;
        this.trangThai = trangThai;
    }

    // ===== GETTERS / SETTERS =====
    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        // Cập nhật regex để khớp với CSDL (NV + 10 số)
        if (maNhanVien != null && maNhanVien.matches("^NV\\d{10}$")) {
            this.maNhanVien = maNhanVien;
        } else {
            // Nới lỏng ràng buộc để chấp nhận mã khi khởi tạo
            // throw new IllegalArgumentException("Mã nhân viên không hợp lệ. Định dạng: NV + 10 số");
             this.maNhanVien = maNhanVien;
        }
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        if (tenNhanVien == null || tenNhanVien.trim().isEmpty())
            throw new IllegalArgumentException("Tên nhân viên không được rỗng.");
        if (tenNhanVien.length() > 50)
            throw new IllegalArgumentException("Tên nhân viên không được vượt quá 50 ký tự.");
        this.tenNhanVien = tenNhanVien.trim();
    }

    public boolean isGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(boolean gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        // CSDL cho phép NULL
        if (ngaySinh != null && Period.between(ngaySinh, LocalDate.now()).getYears() < 18)
            throw new IllegalArgumentException("Nhân viên phải đủ 18 tuổi trở lên.");
        this.ngaySinh = ngaySinh;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        if (soDienThoai != null && !soDienThoai.matches("^0\\d{9}$"))
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (phải 10 số, bắt đầu bằng 0).");
        this.soDienThoai = soDienThoai;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        if (diaChi != null && diaChi.length() > 100)
            throw new IllegalArgumentException("Địa chỉ quá dài (tối đa 100 ký tự).");
        this.diaChi = diaChi;
    }

    public boolean isQuanLy() {
        return quanLy;
    }

    public void setQuanLy(boolean quanLy) {
        this.quanLy = quanLy;
    }

    public String getCaLam() {
        return caLam;
    }

    public void setCaLam(String caLam) {
        if (caLam == null || caLam.trim().isEmpty())
            throw new IllegalArgumentException("Ca làm không được rỗng.");
        String ca = caLam.trim().toUpperCase();
        // Cập nhật để khớp với CSDL (SANG, CHIEU, TOI)
        if (ca.equals("SANG") || ca.equals("CHIEU") || ca.equals("TOI"))
            this.caLam = ca;
        else
            throw new IllegalArgumentException("Ca làm không hợp lệ. Chỉ chấp nhận: SANG, CHIEU, TOI.");
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        return String.format(
            "NhanVien{ma='%s', ten='%s', ca='%s', quanLy=%s, trangThai=%s}",
            maNhanVien, tenNhanVien, caLam, quanLy, trangThai
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NhanVien)) return false;
        NhanVien that = (NhanVien) o;
        return Objects.equals(maNhanVien, that.maNhanVien);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNhanVien);
    }
}
