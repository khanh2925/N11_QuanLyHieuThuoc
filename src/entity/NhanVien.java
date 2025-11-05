package entity;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public class NhanVien {

    private String maNhanVien;    // VD: NV-20251031-0001
    private String tenNhanVien;
    private boolean gioiTinh;
    private LocalDate ngaySinh;
    private String soDienThoai;
    private String diaChi;
    private boolean quanLy;       // true = quản lý, false = nhân viên thường
    /**
     * Quy ước ca làm:
     * 1 = Sáng
     * 2 = Chiều
     * 3 = Tối
     */
    private int caLam;
    private boolean trangThai;    // true = đang làm, false = nghỉ

    // ===== CONSTRUCTORS =====
    public NhanVien() {}

    public NhanVien(String maNhanVien, String tenNhanVien, boolean gioiTinh,
                    LocalDate ngaySinh, String soDienThoai, String diaChi,
                    boolean quanLy, int caLam, boolean trangThai) {
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
        setMaNhanVien(maNhanVien);
    }

    public NhanVien(String maNhanVien, String tenNhanVien) {
        setMaNhanVien(maNhanVien);
        setTenNhanVien(tenNhanVien);
    }

    // Constructor rút gọn cho login hoặc join
    public NhanVien(String maNhanVien, String tenNhanVien, int caLam, boolean trangThai) {
        setMaNhanVien(maNhanVien);
        setTenNhanVien(tenNhanVien);
        setCaLam(caLam);
        setTrangThai(trangThai);
    }
    
    public NhanVien(String maNhanVien, String tenNhanVien, boolean quanLy, int caLam) {
		super();
		this.maNhanVien = maNhanVien;
		this.tenNhanVien = tenNhanVien;
		this.quanLy = quanLy;
		this.caLam = caLam;
	}

	// ===== GETTERS / SETTERS =====
    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        if (maNhanVien == null)
            throw new IllegalArgumentException("Mã nhân viên không được để trống");

        maNhanVien = maNhanVien.trim(); // loại bỏ khoảng trắng đầu/cuối

        // Regex chuẩn: NV-yyyymmdd-xxxx (ví dụ NV-20251104-0001)
        if (!maNhanVien.matches("^NV-\\d{8}-\\d{4}$")) {
            throw new IllegalArgumentException("Mã nhân viên không hợp lệ. Định dạng: NV-yyyymmdd-xxxx");
        }

        this.maNhanVien = maNhanVien;
    }

    
    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        if (tenNhanVien == null)
            throw new IllegalArgumentException("Tên nhân viên không được null.");
        tenNhanVien = tenNhanVien.trim();
        if (tenNhanVien.isEmpty())
            throw new IllegalArgumentException("Tên nhân viên không được rỗng.");
        if (tenNhanVien.length() > 50)
            throw new IllegalArgumentException("Tên nhân viên không được vượt quá 50 ký tự.");
        this.tenNhanVien = tenNhanVien;
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
        if (ngaySinh == null)
            throw new IllegalArgumentException("Ngày sinh không được null.");
        if (Period.between(ngaySinh, LocalDate.now()).getYears() < 18)
            throw new IllegalArgumentException("Nhân viên phải đủ 18 tuổi trở lên.");
        this.ngaySinh = ngaySinh;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        if (soDienThoai == null)
            throw new IllegalArgumentException("Số điện thoại không được null.");
        if (!soDienThoai.matches("^0\\d{9}$"))
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (10 số, bắt đầu bằng 0).");
        this.soDienThoai = soDienThoai;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        if (diaChi == null)
            throw new IllegalArgumentException("Địa chỉ không được null.");
        diaChi = diaChi.trim();
        if (diaChi.isEmpty())
            throw new IllegalArgumentException("Địa chỉ không được rỗng.");
        if (diaChi.length() > 100)
            throw new IllegalArgumentException("Địa chỉ quá dài (tối đa 100 ký tự).");
        this.diaChi = diaChi;
    }

    public boolean isQuanLy() {
        return quanLy;
    }

    public void setQuanLy(boolean quanLy) {
        this.quanLy = quanLy;
    }

    public int getCaLam() {
        return caLam;
    }

    public void setCaLam(int caLam) {
        if (caLam < 1 || caLam > 3)
            throw new IllegalArgumentException("Ca làm không hợp lệ. Chỉ chấp nhận: 1 (Sáng), 2 (Chiều), 3 (Tối).");
        this.caLam = caLam;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    // ===== HELPER =====
    public String getTenCaLam() {
        return switch (caLam) {
            case 1 -> "Sáng";
            case 2 -> "Chiều";
            case 3 -> "Tối";
            default -> "Không xác định";
        };
    }

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        return String.format(
                "NhanVien{ma='%s', ten='%s', ca=%s, quanLy=%s, trangThai=%s}",
                maNhanVien, tenNhanVien, getTenCaLam(),
                quanLy ? "Quản lý" : "Nhân viên", trangThai ? "Đang làm" : "Nghỉ việc"
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
