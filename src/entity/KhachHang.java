package entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class KhachHang {

	private static final double TIEN_MOI_DIEM = 1000; // 1 điểm = 1000đ
	private static final double NGUONG_CONG_DIEM = 10000; // 1 điểm thưởng mỗi 10.000đ thanh toán

	private String maKhachHang;
	private String tenKhachHang;
	private boolean gioiTinh;
	private String soDienThoai;
	private LocalDate ngaySinh;
	private boolean hoatDong = true;
	private double diemTichLuy;
	private List<HoaDon> danhSachHoaDon;

	// ===== CONSTRUCTORS =====
	public KhachHang() {
	}

	public KhachHang(String maKhachHang, String tenKhachHang, boolean gioiTinh, String soDienThoai,
			LocalDate ngaySinh) {
		setMaKhachHang(maKhachHang);
		setTenKhachHang(tenKhachHang);
		setGioiTinh(gioiTinh);
		setSoDienThoai(soDienThoai);
		setNgaySinh(ngaySinh);
		this.diemTichLuy = 0;
	}

	// ===== GETTERS / SETTERS =====
	public String getMaKhachHang() {
		return maKhachHang;
	}

	public void setMaKhachHang(String maKhachHang) {
		if (maKhachHang == null)
			throw new IllegalArgumentException("Mã khách hàng không được để trống");

		maKhachHang = maKhachHang.trim(); // loại bỏ khoảng trắng đầu/cuối

		// Regex chuẩn: KH-yyyymmdd-xxxx (ví dụ KH-20251104-0001)
		if (!maKhachHang.matches("^KH-\\d{8}-\\d{4}$")) {
			throw new IllegalArgumentException("Mã khách hàng không hợp lệ. Định dạng: KH-yyyymmdd-xxxx");
		}

		this.maKhachHang = maKhachHang;
	}

	public String getTenKhachHang() {
		return tenKhachHang;
	}

	public void setTenKhachHang(String tenKhachHang) {
		if (tenKhachHang == null || tenKhachHang.trim().isEmpty())
			throw new IllegalArgumentException("Tên khách hàng không được rỗng.");
		if (tenKhachHang.length() > 100)
			throw new IllegalArgumentException("Tên khách hàng không vượt quá 100 ký tự.");
		this.tenKhachHang = tenKhachHang.trim();
	}

	public boolean isGioiTinh() {
		return gioiTinh;
	}

	public void setGioiTinh(boolean gioiTinh) {
		this.gioiTinh = gioiTinh;
	}

	public String getSoDienThoai() {
		return soDienThoai;
	}

	public void setSoDienThoai(String soDienThoai) {
		if (soDienThoai == null || !soDienThoai.matches("^0\\d{9}$"))
			throw new IllegalArgumentException("SĐT không hợp lệ (10 chữ số, bắt đầu bằng 0).");
		this.soDienThoai = soDienThoai;
	}

	public LocalDate getNgaySinh() {
		return ngaySinh;
	}

	public void setNgaySinh(LocalDate ngaySinh) {
		if (ngaySinh == null || ngaySinh.isAfter(LocalDate.now()))
			throw new IllegalArgumentException("Ngày sinh không hợp lệ.");
		if (ngaySinh.isAfter(LocalDate.now().minusYears(16)))
			throw new IllegalArgumentException("Khách hàng phải từ 16 tuổi trở lên.");
		this.ngaySinh = ngaySinh;
	}

	public boolean isHoatDong() {
		return hoatDong;
	}

	public void setHoatDong(boolean hoatDong) {
		this.hoatDong = hoatDong;
	}

	public double getDiemTichLuy() {
		return diemTichLuy;
	}

	public void setDiemTichLuy(double diemTichLuy) {
		if (diemTichLuy < 0)
			diemTichLuy = 0;
		this.diemTichLuy = diemTichLuy;
	}

	public List<HoaDon> getDanhSachHoaDon() {
		return danhSachHoaDon;
	}

	public void setDanhSachHoaDon(List<HoaDon> danhSachHoaDon) {
		this.danhSachHoaDon = danhSachHoaDon;
	}

	// ===== NGHIỆP VỤ =====

	/** ✅ Cộng điểm khi hóa đơn được hoàn tất */
	public void congDiemTheoHoaDon(HoaDon hoaDon) {
		if (hoaDon == null || hoaDon.getTongThanhToan() <= 0)
			return;
		double diemCong = Math.floor(hoaDon.getTongThanhToan() / NGUONG_CONG_DIEM);
		this.diemTichLuy += diemCong;
	}

	/** ✅ Trừ điểm khi dùng để giảm hóa đơn */
	public double dungDiemTichLuy(double diemMuonDung) {
		if (diemMuonDung <= 0)
			throw new IllegalArgumentException("Số điểm sử dụng phải > 0.");
		if (diemMuonDung > diemTichLuy)
			throw new IllegalArgumentException("Không đủ điểm tích lũy để sử dụng.");

		diemTichLuy -= diemMuonDung;
		return diemMuonDung * TIEN_MOI_DIEM;
	}

	/** ✅ Trừ điểm khi khách trả hàng (phiếu trả đã duyệt) */
	public void truDiemTheoPhieuTra(PhieuTra phieuTra) {
		if (phieuTra == null || !phieuTra.isDaDuyet())
			return;
		double soTienHoan = phieuTra.getTongTienHoan();
		if (soTienHoan <= 0)
			return;

		double diemTru = Math.floor(soTienHoan / NGUONG_CONG_DIEM);
		this.diemTichLuy = Math.max(0, this.diemTichLuy - diemTru);
	}

	// ===== OVERRIDES =====
	@Override
	public String toString() {
		return String.format("KhachHang{ma='%s', ten='%s', sdt='%s', diem=%.0f, %s}", maKhachHang, tenKhachHang,
				soDienThoai, diemTichLuy, hoatDong ? "Hoạt động" : "Ngừng");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof KhachHang))
			return false;
		KhachHang that = (KhachHang) o;
		return Objects.equals(maKhachHang, that.maKhachHang);
	}

	@Override
	public int hashCode() {
		return Objects.hash(maKhachHang);
	}
}
