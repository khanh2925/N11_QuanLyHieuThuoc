package entity;

import java.util.Objects;

public class ChiTietHoaDon {

	private HoaDon hoaDon;
	private LoSanPham loSanPham;
	private double soLuong; // Số lượng đơn vị gốc
	private double giaBan;
	private KhuyenMai khuyenMai;
	private double thanhTien;
	private DonViTinh donViTinh; // Đơn vị tính lúc bán
	// ===== CONSTRUCTORS =====

	public ChiTietHoaDon() {
	}

	public ChiTietHoaDon(HoaDon hoaDon, LoSanPham loSanPham, double soLuong, double giaBan, KhuyenMai khuyenMai,
			DonViTinh donViTinh) {
		setHoaDon(hoaDon);
		setLoSanPham(loSanPham);
		setSoLuong(soLuong);
		setGiaBan(giaBan);
		setKhuyenMai(khuyenMai);
		setDonViTinh(donViTinh);
		capNhatThanhTien();
	}

	public ChiTietHoaDon(ChiTietHoaDon other) {
		this.hoaDon = other.hoaDon;
		this.loSanPham = other.loSanPham;
		this.soLuong = other.soLuong;
		this.giaBan = other.giaBan;
		this.khuyenMai = other.khuyenMai;
		this.thanhTien = other.thanhTien;
	}

	// ===== DẪN SUẤT =====
	public void capNhatThanhTien() {
		double thanhTienChuaGiam = this.soLuong * this.giaBan;

		if (khuyenMai == null || khuyenMai.isKhuyenMaiHoaDon()) {
			this.thanhTien = thanhTienChuaGiam;
			return;
		}

		double giam = 0;
		switch (khuyenMai.getHinhThuc()) {
		case GIAM_GIA_PHAN_TRAM:
			giam = thanhTienChuaGiam * (khuyenMai.getGiaTri() / 100.0);
			break;
		case GIAM_GIA_TIEN:
			giam = khuyenMai.getGiaTri();
			break;
		case TANG_THEM:
			giam = 0;
			break;
		default:
			giam = 0;
		}

		this.thanhTien = Math.max(0, thanhTienChuaGiam - giam);
	}

	public double getThanhTien() {
		return thanhTien;
	}

	// ===== GETTERS / SETTERS =====
	public HoaDon getHoaDon() {
		return hoaDon;
	}

	public void setHoaDon(HoaDon hoaDon) {
		if (hoaDon == null)
			throw new IllegalArgumentException("Hóa đơn không được null.");
		this.hoaDon = hoaDon;
	}

	public LoSanPham getLoSanPham() {
		return loSanPham;
	}

	public void setLoSanPham(LoSanPham loSanPham) {
		if (loSanPham == null)
			throw new IllegalArgumentException("Lô sản phẩm không được null.");
		this.loSanPham = loSanPham;
	}

	public SanPham getSanPham() {
		return loSanPham != null ? loSanPham.getSanPham() : null;
	}

	public double getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(double soLuong) {
		if (soLuong <= 0)
			throw new IllegalArgumentException("Số lượng phải > 0.");
		this.soLuong = soLuong;
		capNhatThanhTien();
	}

	public double getGiaBan() {
		return giaBan;
	}

	public void setGiaBan(double giaBan) {
		if (giaBan <= 0)
			throw new IllegalArgumentException("Giá bán phải > 0.");
		this.giaBan = giaBan;
		capNhatThanhTien();
	}

	public KhuyenMai getKhuyenMai() {
		return khuyenMai;
	}

	public void setKhuyenMai(KhuyenMai khuyenMai) {
		if (khuyenMai != null && khuyenMai.isKhuyenMaiHoaDon())
			throw new IllegalArgumentException("Không thể gán khuyến mãi hóa đơn cho chi tiết sản phẩm.");
		this.khuyenMai = khuyenMai;
		capNhatThanhTien();
	}

	// ===== OVERRIDES =====
	@Override
	public String toString() {
		return String.format("CTHD[%s - %s] SL=%.0f, Giá=%.0f, Thành tiền=%.0f%s",
				hoaDon != null ? hoaDon.getMaHoaDon() : "N/A",
				getSanPham() != null ? getSanPham().getTenSanPham() : "N/A", soLuong, giaBan, thanhTien,
				khuyenMai != null ? ", KM=" + khuyenMai.getHinhThuc() : "");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ChiTietHoaDon))
			return false;
		ChiTietHoaDon that = (ChiTietHoaDon) o;
		return Objects.equals(hoaDon, that.hoaDon) && Objects.equals(loSanPham, that.loSanPham);
	}

	@Override
	public int hashCode() {
		return Objects.hash(hoaDon, loSanPham);
	}

	public DonViTinh getDonViTinh() {
		return donViTinh;
	}

	public void setDonViTinh(DonViTinh donViTinh) {
		if (donViTinh == null)
			throw new IllegalArgumentException("Đơn vị tính không được null.");

		this.donViTinh = donViTinh;
	}
}
