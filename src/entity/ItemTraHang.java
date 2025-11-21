package entity;

import java.text.DecimalFormat;

public class ItemTraHang {

	private static final DecimalFormat DF = new DecimalFormat("#,##0");

	private String maLo;
	private String tenSanPham;
	private String donViTinh;
	private double donGia;
	private int soLuongMua; // SL đã mua trong hoá đơn gốc
	private int soLuongTra; // SL muốn trả
	private String lyDo;
	private ChiTietHoaDon cthdGoc;

	public ItemTraHang(String maLo, String tenSanPham, String donViTinh, double donGia, int soLuongMua, ChiTietHoaDon cthd) {

		this.cthdGoc = cthd;
		this.maLo = maLo;
		this.tenSanPham = tenSanPham;
		this.donViTinh = donViTinh;
		this.donGia = donGia;
		this.soLuongMua = soLuongMua;

		this.soLuongTra = 1;
		this.lyDo = "";
	}

	public int getSoLuongTra() {
		return soLuongTra;
	}

	public void setSoLuongTra(int sl) {
		this.soLuongTra = Math.max(1, Math.min(sl, soLuongMua));
	}

	public double getThanhTien() {
		return soLuongTra * donGia;
	}

	// ==== GETTER/SETTER ====

	public String getMaLo() {
		return maLo;
	}

	public String getTenSanPham() {
		return tenSanPham;
	}

	public String getDonViTinh() {
		return donViTinh;
	}

	public double getDonGia() {
		return donGia;
	}

	public int getSoLuongMua() {
		return soLuongMua;
	}

	public String getLyDo() {
		return lyDo;
	}

	public void setLyDo(String lyDo) {
		this.lyDo = lyDo;
	}

	public ChiTietHoaDon getChiTietHoaDonGoc() {
		return cthdGoc;
	}
}
