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
	private TaiKhoan taiKhoan;
	private String caLam;
	private boolean trangThai;

	public NhanVien() {
	}

	public NhanVien(String maNhanVien, String tenNhanVien, boolean gioiTinh, LocalDate ngaySinh, String soDienThoai,
			String diaChi, boolean quanLy, TaiKhoan taiKhoan, String caLam, boolean trangThai) {
		setMaNhanVien(maNhanVien);
		setTenNhanVien(tenNhanVien);
		setGioiTinh(gioiTinh);
		setNgaySinh(ngaySinh);
		setSoDienThoai(soDienThoai);
		setDiaChi(diaChi);
		setQuanLy(quanLy);
		setTaiKhoan(taiKhoan);
		setCaLam(caLam);
		setTrangThai(trangThai);
	}

	public NhanVien(String maNhanVien) {
		this.maNhanVien = maNhanVien;
	}

	public NhanVien(NhanVien nv) {
		this.maNhanVien = nv.maNhanVien;
		this.tenNhanVien = nv.tenNhanVien;
		this.gioiTinh = nv.gioiTinh;
		this.ngaySinh = nv.ngaySinh;
		this.soDienThoai = nv.soDienThoai;
		this.diaChi = nv.diaChi;
		this.quanLy = nv.quanLy;
		this.taiKhoan = nv.taiKhoan;
		this.trangThai = nv.trangThai;
	}

	public String getMaNhanVien() {
		return maNhanVien;
	}

	public void setMaNhanVien(String maNhanVien) {
		if (maNhanVien != null && maNhanVien.matches("^NV\\d{10}$")) {
			this.maNhanVien = maNhanVien;
		} else {
			throw new IllegalArgumentException("Mã nhân viên không hợp lệ. Định dạng yêu cầu: NVyyyyMMxxxx");
		}
	}

	public String getTenNhanVien() {
		return tenNhanVien;
	}

	public void setTenNhanVien(String tenNhanVien) {
		if (tenNhanVien == null || tenNhanVien.trim().isEmpty()) {
			throw new IllegalArgumentException("Họ tên không được rỗng.");
		}
		if (tenNhanVien.length() > 50) {
			throw new IllegalArgumentException("Họ tên không được vượt quá 50 ký tự.");
		}
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
		if (ngaySinh == null || Period.between(ngaySinh, LocalDate.now()).getYears() < 18) {
			throw new IllegalArgumentException("Nhân viên phải đủ 18 tuổi trở lên.");
		}
		this.ngaySinh = ngaySinh;
	}

	public String getSoDienThoai() {
		return soDienThoai;
	}

	public void setSoDienThoai(String soDienThoai) {
		if (soDienThoai != null && !soDienThoai.matches("^0\\d{9}$")) {
			throw new IllegalArgumentException("Số điện thoại không hợp lệ (phải có 10 số, bắt đầu bằng 0).");
		}
		this.soDienThoai = soDienThoai;
	}

	public String getDiaChi() {
		return diaChi;
	}

	public void setDiaChi(String diaChi) {
		if (diaChi != null && diaChi.length() > 100) {
			throw new IllegalArgumentException("Địa chỉ quá dài (tối đa 100 ký tự).");
		}
		this.diaChi = diaChi;
	}

	public boolean isQuanLy() {
		return quanLy;
	}

	public void setQuanLy(boolean quanLy) {
		this.quanLy = quanLy;
	}

	public TaiKhoan getTaiKhoan() {
		return taiKhoan;
	}

	public void setTaiKhoan(TaiKhoan taiKhoan) {
		if (taiKhoan == null) {
			throw new IllegalArgumentException("Tài khoản không tồn tại.");
		}
		this.taiKhoan = taiKhoan;
	}

	public String getCaLam() {
		return caLam;
	}

	public void setCaLam(String caLam) {
		if (caLam == null || caLam.trim().isEmpty()) {
			throw new IllegalArgumentException("Ca làm không được rỗng.");
		}
		this.caLam = caLam;
	}

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}

	@Override
	public String toString() {
		return "NhanVien{" + "maNhanVien='" + maNhanVien + '\'' + ", tenNhanVien='" + tenNhanVien + '\'' + ", gioiTinh="
				+ gioiTinh + ", ngaySinh=" + ngaySinh + ", soDienThoai='" + soDienThoai + '\'' + ", diaChi='" + diaChi
				+ '\'' + ", quanLy=" + quanLy + ", taiKhoan=" + taiKhoan + ", caLam=" + caLam + ", trangThai=" + trangThai + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		NhanVien nhanVien = (NhanVien) o;
		return Objects.equals(maNhanVien, nhanVien.maNhanVien);
	}

	@Override
	public int hashCode() {
		return Objects.hash(maNhanVien);
	}
}
