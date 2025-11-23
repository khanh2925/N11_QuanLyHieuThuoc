package entity;

import java.util.Objects;

public class ChiTietPhieuTra {

	private PhieuTra phieuTra;
	private ChiTietHoaDon chiTietHoaDon;
	private String lyDoChiTiet;
	private int soLuong;
	private double thanhTienHoan;
	private int trangThai; // 0=Chờ duyệt, 1=Nhập lại hàng, 2=Huỷ hàng
	private DonViTinh donViTinh;

	// ===== CONSTRUCTORS =====
	public ChiTietPhieuTra() {
	}

	public ChiTietPhieuTra(PhieuTra phieuTra, ChiTietHoaDon chiTietHoaDon, String lyDoChiTiet, int soLuong,
			int trangThai) {
		setPhieuTra(phieuTra);
		setChiTietHoaDon(chiTietHoaDon);
		setLyDoChiTiet(lyDoChiTiet);
		setSoLuong(soLuong);
		setTrangThai(trangThai);
		capNhatThanhTienHoan(); // ✅ Tính tiền hoàn ngay khi tạo
	}

	public ChiTietPhieuTra(ChiTietPhieuTra other) {
		this.phieuTra = other.phieuTra;
		this.chiTietHoaDon = other.chiTietHoaDon;
		this.lyDoChiTiet = other.lyDoChiTiet;
		this.soLuong = other.soLuong;
		this.thanhTienHoan = other.thanhTienHoan;
		this.trangThai = other.trangThai;
	}

	// ===== GETTERS / SETTERS =====
	public PhieuTra getPhieuTra() {
		return phieuTra;
	}

	public void setPhieuTra(PhieuTra phieuTra) {
		if (phieuTra == null)
			throw new IllegalArgumentException("Phiếu trả không được null.");
		this.phieuTra = phieuTra;
	}

	public ChiTietHoaDon getChiTietHoaDon() {
		return chiTietHoaDon;
	}

	public void setChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) {
		if (chiTietHoaDon == null)
			throw new IllegalArgumentException("Chi tiết hóa đơn không được null.");
		this.chiTietHoaDon = chiTietHoaDon;
		capNhatThanhTienHoan();
	}

	public String getLyDoChiTiet() {
		return lyDoChiTiet;
	}

	public void setLyDoChiTiet(String lyDoChiTiet) {
		if (lyDoChiTiet != null && lyDoChiTiet.length() > 200)
			throw new IllegalArgumentException("Lý do chi tiết không được vượt quá 200 ký tự.");
		this.lyDoChiTiet = lyDoChiTiet;
	}

	public int getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(int soLuong) {
		if (soLuong <= 0)
			throw new IllegalArgumentException("Số lượng trả phải > 0.");
		if (this.chiTietHoaDon != null && soLuong > this.chiTietHoaDon.getSoLuong())
			throw new IllegalArgumentException(
					String.format("Số lượng trả (%d) không được vượt quá số lượng đã mua (%d).", soLuong,
							this.chiTietHoaDon.getSoLuong()));
		this.soLuong = soLuong;
		capNhatThanhTienHoan();
	}

	public double getThanhTienHoan() {
		return thanhTienHoan;
	}

	public void setThanhTienHoan(double thanhTienHoan) {
		this.thanhTienHoan = thanhTienHoan;
	}

	/** Tự động tính lại tiền hoàn (dẫn xuất nhưng lưu DB để tra cứu nhanh) */
	public void capNhatThanhTienHoan() {
		if (chiTietHoaDon == null || chiTietHoaDon.getSoLuong() <= 0) {
			this.thanhTienHoan = 0;
			return;
		}

		double donGiaThucTe = chiTietHoaDon.getThanhTien() / chiTietHoaDon.getSoLuong();
		this.thanhTienHoan = Math.round(donGiaThucTe * this.soLuong * 100.0) / 100.0;
	}

	public int getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(int trangThai) {
		if (trangThai < 0 || trangThai > 2)
			throw new IllegalArgumentException("Trạng thái chỉ hợp lệ: 0=Chờ duyệt, 1=Nhập lại hàng, 2=Huỷ hàng.");
		this.trangThai = trangThai;
	}

	public String getTrangThaiText() {
		return switch (trangThai) {
		case 0 -> "Chờ duyệt";
		case 1 -> "Nhập lại hàng";
		case 2 -> "Huỷ hàng";
		default -> "Không xác định";
		};
	}

	public void setDonViTinh(DonViTinh dvt) {
		this.donViTinh = dvt;
	}

	public DonViTinh getDonViTinh() {
		return this.donViTinh;
	}

	// ===== OVERRIDES =====
	@Override
	public String toString() {
		return String.format("CTPT[%s - %s - SL:%d - Hoàn:%.0fđ - %s]",
				phieuTra != null ? phieuTra.getMaPhieuTra() : "N/A",
				chiTietHoaDon != null && chiTietHoaDon.getSanPham() != null ? chiTietHoaDon.getSanPham().getTenSanPham()
						: "N/A",
				soLuong, thanhTienHoan, getTrangThaiText());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ChiTietPhieuTra))
			return false;
		ChiTietPhieuTra that = (ChiTietPhieuTra) o;
		return Objects.equals(phieuTra, that.phieuTra) && Objects.equals(chiTietHoaDon, that.chiTietHoaDon);
	}

	@Override
	public int hashCode() {
		return Objects.hash(phieuTra, chiTietHoaDon);
	}
}
