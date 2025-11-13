package entity;

import java.time.LocalDate;
import java.util.Objects;

public class BangGia {

	private String maBangGia; // BG-yyyyMMdd-xxxx
	private NhanVien nhanVien; // Người lập bảng giá
	private String tenBangGia;
	private LocalDate ngayApDung;
	private boolean hoatDong; // ✅ chỉ một bảng giá được hoạt động tại 1 thời điểm

	// ===== CONSTRUCTORS =====
	public BangGia() {
	}

	public BangGia(String maBangGia) {
		setMaBangGia(maBangGia);
	}

	public BangGia(String maBangGia, NhanVien nhanVien, String tenBangGia, LocalDate ngayApDung, boolean hoatDong) {
		setMaBangGia(maBangGia);
		setNhanVien(nhanVien);
		setTenBangGia(tenBangGia);
		setNgayApDung(ngayApDung);
		setHoatDong(hoatDong);
	}

	// ===== GETTERS / SETTERS =====
	public String getMaBangGia() {
		return maBangGia;
	}

	public void setMaBangGia(String maBangGia) {
		if (maBangGia == null)
			throw new IllegalArgumentException("Mã bảng giá không được để trống");

		maBangGia = maBangGia.trim(); // loại bỏ khoảng trắng đầu/cuối

		// Regex chuẩn: BG-yyyymmdd-xxxx (ví dụ BG-20251104-0001)
		if (!maBangGia.matches("^BG-\\d{8}-\\d{4}$")) {
			throw new IllegalArgumentException("Mã bảng giá không hợp lệ. Định dạng: BG-yyyymmdd-xxxx");
		}

		this.maBangGia = maBangGia;
	}
	
	public NhanVien getNhanVien() {
		return nhanVien;
	}

	public void setNhanVien(NhanVien nhanVien) {
		if (nhanVien == null)
			throw new IllegalArgumentException("Nhân viên không được null.");
		this.nhanVien = nhanVien;
	}

	public String getTenBangGia() {
		return tenBangGia;
	}

	public void setTenBangGia(String tenBangGia) {
		if (tenBangGia == null || tenBangGia.trim().isEmpty())
			throw new IllegalArgumentException("Tên bảng giá không được rỗng.");
		if (tenBangGia.length() > 100)
			throw new IllegalArgumentException("Tên bảng giá không được vượt quá 100 ký tự.");
		this.tenBangGia = tenBangGia.trim();
	}

	public LocalDate getNgayApDung() {
		return ngayApDung;
	}

	public void setNgayApDung(LocalDate ngayApDung) {
		if (ngayApDung == null)
			throw new IllegalArgumentException("Ngày áp dụng không được null.");
		if (ngayApDung.isBefore(LocalDate.of(2000, 1, 1)))
			throw new IllegalArgumentException("Ngày áp dụng không hợp lệ (phải sau năm 2000).");
		this.ngayApDung = ngayApDung;
	}

	public boolean isHoatDong() {
		return hoatDong;
	}

	public void setHoatDong(boolean hoatDong) {
		this.hoatDong = hoatDong;
	}

	// ===== OVERRIDES =====
	@Override
	public String toString() {
		return String.format("BangGia[%s - %s, Ngày áp dụng: %s, Nhân viên: %s, Hoạt động: %s]", maBangGia, tenBangGia,
				ngayApDung != null ? ngayApDung : "N/A", nhanVien != null ? nhanVien.getTenNhanVien() : "N/A",
				hoatDong ? "Có" : "Không");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof BangGia))
			return false;
		BangGia that = (BangGia) o;
		return Objects.equals(maBangGia, that.maBangGia);
	}

	@Override
	public int hashCode() {
		return Objects.hash(maBangGia);
	}
}
