package entity;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public class NhanVien {

    private String maNhanVien;
    private String tenNhanVien;
    private Boolean gioiTinh;
    private LocalDate ngaySinh;
    private String soDienThoai;
    private String diaChi;
    private Boolean quanLy;
    private TaiKhoan taiKhoan;
    private String caLam;

	
    public NhanVien(String maNhanVien, String tenNhanVien, boolean gioiTinh, LocalDate ngaySinh, String soDienThoai,
			String diaChi, boolean quanLy, String caLam, TaiKhoan taiKhoan) {
		super();
		this.maNhanVien = maNhanVien;
		this.tenNhanVien = tenNhanVien;
		this.gioiTinh = gioiTinh;
		this.ngaySinh = ngaySinh;
		this.soDienThoai = soDienThoai;
		this.diaChi = diaChi;
		this.quanLy = quanLy;
		this.caLam = caLam;
		this.taiKhoan = taiKhoan;
	}

	public NhanVien() {
		super();
	}

	// Getters and Setters
	public String getMaNhanVien() {
		return maNhanVien;
	}

    public String getCaLam() {
        return caLam;
    }



    public void setCaLam(String caLam) {
        if (caLam == null || caLam.trim().isEmpty()) {
            throw new IllegalArgumentException("Ca làm không rỗng.");
        }
        this.caLam = caLam;
    }

    @Override
    public String toString() {
        return "NhanVien{" +
                "maNhanVien='" + maNhanVien + '\'' +
                ", tenNhanVien='" + tenNhanVien + '\'' +
                ", gioiTinh=" + gioiTinh +
                ", ngaySinh=" + ngaySinh +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", diaChi='" + diaChi + '\'' +
                ", quanLy=" + quanLy +
                ", taiKhoan=" + taiKhoan +
                ", caLam='" + caLam + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NhanVien nhanVien = (NhanVien) o;
        return Objects.equals(maNhanVien, nhanVien.maNhanVien);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNhanVien);
    }
}