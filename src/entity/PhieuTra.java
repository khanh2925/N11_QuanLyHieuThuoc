package entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhieuTra {

	private String maPhieuTra;
	private KhachHang khachHang;
	private NhanVien nhanVien;
	private LocalDate ngayLap;
	private boolean daDuyet;
	private double tongTienHoan;
	private List<ChiTietPhieuTra> chiTietPhieuTraList;

	public PhieuTra() {
		this.chiTietPhieuTraList = new ArrayList<>();
		this.ngayLap = LocalDate.now();
		this.daDuyet = false;
		this.tongTienHoan = 0;
	}

	public PhieuTra(String maPhieuTra, KhachHang khachHang, NhanVien nhanVien, LocalDate ngayLap, boolean daDuyet,
			List<ChiTietPhieuTra> chiTietPhieuTraList) {
		setMaPhieuTra(maPhieuTra);
		setKhachHang(khachHang);
		setNhanVien(nhanVien);
		setNgayLap(ngayLap);
		setDaDuyet(daDuyet);
		setChiTietPhieuTraList(chiTietPhieuTraList);
		capNhatTongTienHoan();
	}

	// ===== GETTERS / SETTERS =====
	public String getMaPhieuTra() {
		return maPhieuTra;
	}

	public void setMaPhieuTra(String maPhieuTra) {
		if (maPhieuTra == null)
			throw new IllegalArgumentException("Mã phiếu trả không được để trống");

		maPhieuTra = maPhieuTra.trim(); // loại bỏ khoảng trắng đầu/cuối

		// Regex chuẩn: PT-yyyymmdd-xxxx (ví dụ PT-20251104-0001)
		if (!maPhieuTra.matches("^PT-\\d{8}-\\d{4}$")) {
			throw new IllegalArgumentException("Mã hoá đơn không hợp lệ. Định dạng: PT-yyyymmdd-xxxx");
		}

		this.maPhieuTra = maPhieuTra;
	}

	public KhachHang getKhachHang() {
		return khachHang;
	}

	public void setKhachHang(KhachHang khachHang) {
		if (khachHang == null)
			throw new IllegalArgumentException("Khách hàng không được null.");
		this.khachHang = khachHang;
	}

	public NhanVien getNhanVien() {
		return nhanVien;
	}

	public void setNhanVien(NhanVien nhanVien) {
		if (nhanVien == null)
			throw new IllegalArgumentException("Nhân viên không được null.");
		this.nhanVien = nhanVien;
	}

	public LocalDate getNgayLap() {
		return ngayLap;
	}

	public void setNgayLap(LocalDate ngayLap) {
		if (ngayLap == null || ngayLap.isAfter(LocalDate.now()))
			throw new IllegalArgumentException("Ngày lập không hợp lệ (≤ ngày hiện tại).");
		this.ngayLap = ngayLap;
	}

	public boolean isDaDuyet() {
		return daDuyet;
	}

	/** ✅ Khi duyệt phiếu → tự trừ điểm KH (nếu có) */
	public void setDaDuyet(boolean daDuyet) {
		this.daDuyet = daDuyet;
		if (daDuyet && khachHang != null) {
			capNhatTongTienHoan();
			khachHang.truDiemTheoPhieuTra(this);
		}
	}

	public double getTongTienHoan() {
		return tongTienHoan;
	}

	public List<ChiTietPhieuTra> getChiTietPhieuTraList() {
		return chiTietPhieuTraList;
	}

	public void setChiTietPhieuTraList(List<ChiTietPhieuTra> chiTietPhieuTraList) {
		if (chiTietPhieuTraList == null)
			throw new IllegalArgumentException("Danh sách chi tiết phiếu trả không được null.");
		this.chiTietPhieuTraList = chiTietPhieuTraList;
		capNhatTongTienHoan();
	}

	// ===== BUSINESS LOGIC =====
	/** ✅ Tổng tiền hoàn = tổng tất cả chi tiết */
//	public void capNhatTongTienHoan() {
//		this.tongTienHoan = chiTietPhieuTraList.stream().filter(Objects::nonNull)
//				.mapToDouble(ChiTietPhieuTra::getThanhTienHoan).sum();
//	}

	public void capNhatTongTienHoan() {
	    if (chiTietPhieuTraList == null || chiTietPhieuTraList.isEmpty()) {
	        this.tongTienHoan = 0;
	        return;
	    }

	    this.tongTienHoan = chiTietPhieuTraList.stream()
	        .mapToDouble(ChiTietPhieuTra::getThanhTienHoan)
	        .sum();
	}

	
	public String getTrangThaiText() {
		return daDuyet ? "Đã duyệt" : "Đang chờ duyệt";
	}

	// ===== OVERRIDES =====
	@Override
	public String toString() {
		return String.format("PhieuTra[%s | %s | %s | %.2fđ | %s]", maPhieuTra,
				khachHang != null ? khachHang.getTenKhachHang() : "N/A", ngayLap, tongTienHoan, getTrangThaiText());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof PhieuTra))
			return false;
		PhieuTra that = (PhieuTra) o;
		return Objects.equals(maPhieuTra, that.maPhieuTra);
	}

	@Override
	public int hashCode() {
		return Objects.hash(maPhieuTra);
	}
}
