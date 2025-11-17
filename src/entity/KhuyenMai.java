package entity;

import java.time.LocalDate;
import java.util.Objects;
import enums.HinhThucKM;

public class KhuyenMai {

	private String maKM; // VD: KM-20251031-0001
	private String tenKM;
	private LocalDate ngayBatDau;
	private LocalDate ngayKetThuc;
	private boolean trangThai; // true = đang hoạt động
	private boolean khuyenMaiHoaDon; // true = KM hóa đơn, false = KM sản phẩm
	private HinhThucKM hinhThuc;
	private double giaTri; // phần trăm hoặc tiền giảm
	private double dieuKienApDungHoaDon; // tổng tiền tối thiểu để áp dụng (chỉ dùng cho hóa đơn)
	private int soLuongKhuyenMai;

	// ===== CONSTRUCTORS =====
	public KhuyenMai() {
	}

	public KhuyenMai(String maKM, String tenKM, LocalDate ngayBatDau, LocalDate ngayKetThuc, boolean trangThai,
			boolean khuyenMaiHoaDon, HinhThucKM hinhThuc, double giaTri, double dieuKienApDungHoaDon,
			int soLuongKhuyenMai) {
		setMaKM(maKM);
		setTenKM(tenKM);
		setNgayBatDau(ngayBatDau);
		setNgayKetThuc(ngayKetThuc);
		setTrangThai(trangThai);
		setKhuyenMaiHoaDon(khuyenMaiHoaDon);
		setHinhThuc(hinhThuc);
		setGiaTri(giaTri);
		setDieuKienApDungHoaDon(dieuKienApDungHoaDon);
		setSoLuongKhuyenMai(soLuongKhuyenMai);
		apDungRangBuocLoaiKM();
	}

	public KhuyenMai(String makm) {
		this.maKM = makm;
	}

	// ===== GETTERS / SETTERS =====
	public String getMaKM() {
		return maKM;
	}

	public void setMaKM(String maKM) {
		if (maKM == null)
			throw new IllegalArgumentException("Mã khuyến mãi không được để trống");

		maKM = maKM.trim(); // loại bỏ khoảng trắng đầu/cuối

		// Regex chuẩn: KM-yyyymmdd-xxxx (ví dụ KM-20251104-0001)
		if (!maKM.matches("^KM-\\d{8}-\\d{4}$")) {
			throw new IllegalArgumentException("Mã khuyến mãi không hợp lệ. Định dạng: KM-yyyymmdd-xxxx");
		}

		this.maKM = maKM;
	}

	public String getTenKM() {
		return tenKM;
	}

	public void setTenKM(String tenKM) {
		if (tenKM == null || tenKM.trim().isEmpty() || tenKM.length() > 200)
			throw new IllegalArgumentException("Tên khuyến mãi không hợp lệ (không rỗng, ≤200 ký tự).");
		this.tenKM = tenKM.trim();
	}

	public LocalDate getNgayBatDau() {
		return ngayBatDau;
	}

	public void setNgayBatDau(LocalDate ngayBatDau) {
		if (ngayBatDau == null)
			throw new IllegalArgumentException("Ngày bắt đầu không được null.");
		if (this.ngayKetThuc != null && ngayBatDau.isAfter(this.ngayKetThuc))
			throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc.");
		this.ngayBatDau = ngayBatDau;
	}

	public LocalDate getNgayKetThuc() {
		return ngayKetThuc;
	}

	public void setNgayKetThuc(LocalDate ngayKetThuc) {
		if (ngayKetThuc == null)
			throw new IllegalArgumentException("Ngày kết thúc không được null.");
		if (this.ngayBatDau != null && ngayKetThuc.isBefore(this.ngayBatDau))
			throw new IllegalArgumentException("Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.");
		this.ngayKetThuc = ngayKetThuc;
	}

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}

	public boolean isKhuyenMaiHoaDon() {
		return khuyenMaiHoaDon;
	}

	public void setKhuyenMaiHoaDon(boolean khuyenMaiHoaDon) {
		this.khuyenMaiHoaDon = khuyenMaiHoaDon;
		apDungRangBuocLoaiKM();
	}

	public HinhThucKM getHinhThuc() {
		return hinhThuc;
	}

	public void setHinhThuc(HinhThucKM hinhThuc) {
		if (hinhThuc == null)
			throw new IllegalArgumentException("Hình thức khuyến mãi không được null.");
		this.hinhThuc = hinhThuc;
		apDungRangBuocLoaiKM();
	}

	public double getGiaTri() {
		return giaTri;
	}

	public void setGiaTri(double giaTri) {
		if (giaTri < 0)
			throw new IllegalArgumentException("Giá trị khuyến mãi phải ≥ 0.");
		if (hinhThuc == HinhThucKM.GIAM_GIA_PHAN_TRAM && giaTri > 100)
			throw new IllegalArgumentException("Giảm giá phần trăm không được vượt quá 100%.");
		this.giaTri = giaTri;
	}

	public double getDieuKienApDungHoaDon() {
		return dieuKienApDungHoaDon;
	}

	public void setDieuKienApDungHoaDon(double dieuKienApDungHoaDon) {
		if (dieuKienApDungHoaDon < 0)
			throw new IllegalArgumentException("Điều kiện áp dụng hóa đơn phải ≥ 0.");
		this.dieuKienApDungHoaDon = dieuKienApDungHoaDon;
	}

	public int getSoLuongKhuyenMai() {
		return soLuongKhuyenMai;
	}

	public void setSoLuongKhuyenMai(int soLuongKhuyenMai) {
		if (soLuongKhuyenMai < 0)
			throw new IllegalArgumentException("Số lượng khuyến mãi phải ≥ 0.");
		this.soLuongKhuyenMai = soLuongKhuyenMai;
		// nếu hết số lượng → tự dừng hoạt động
		if (this.soLuongKhuyenMai == 0)
			this.trangThai = false;
	}

	// ===== RÀNG BUỘC NGHIỆP VỤ =====
	private void apDungRangBuocLoaiKM() {
		if (hinhThuc == null)
			return;
		if (khuyenMaiHoaDon && hinhThuc == HinhThucKM.TANG_THEM)
			throw new IllegalArgumentException("Khuyến mãi hóa đơn không thể có hình thức 'TẶNG THÊM'.");
	}

	// ===== UTILS =====
	public boolean isDangHoatDong() {
		LocalDate now = LocalDate.now();
		return trangThai && soLuongKhuyenMai > 0 && !now.isBefore(ngayBatDau) && !now.isAfter(ngayKetThuc);
	}

	public void capNhatTrangThaiTuDong() {
		LocalDate now = LocalDate.now();
		this.trangThai = (soLuongKhuyenMai > 0 && !now.isBefore(ngayBatDau) && !now.isAfter(ngayKetThuc));
	}

	// ===== OVERRIDES =====
	@Override
	public String toString() {
		return String.format("KhuyenMai{ma='%s', ten='%s', loai=%s, hinhThuc=%s, giaTri=%.2f, SLKM=%d, hoatDong=%s}",
				maKM, tenKM, khuyenMaiHoaDon ? "Hóa đơn" : "Sản phẩm", hinhThuc, giaTri, soLuongKhuyenMai,
				isDangHoatDong() ? "Đang áp dụng" : "Ngừng");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof KhuyenMai))
			return false;
		KhuyenMai km = (KhuyenMai) o;
		return Objects.equals(maKM, km.maKM);
	}

	@Override
	public int hashCode() {
		return Objects.hash(maKM);
	}
}
