package entity;

public class ItemHuyHang {

	private String maLo;
	private String tenSanPham;
	private int soLuongTon;
	private int soLuongHuy;
	private double donGiaNhap;
	private String lyDo;
	private QuyCachDongGoi quyCachHienTai; // Quy cách đang chọn
	private QuyCachDongGoi quyCachGoc; // Quy cách gốc (để tính toán cuối)

	public ItemHuyHang(String maLo, String tenSanPham, int soLuongTon, double donGiaNhap) {
		this.maLo = maLo;
		this.tenSanPham = tenSanPham;
		this.soLuongTon = soLuongTon;
		this.donGiaNhap = donGiaNhap;
		this.soLuongHuy = 1; // default
		this.lyDo = "";
	}

	public String getMaLo() {
		return maLo;
	}

	public String getTenSanPham() {
		return tenSanPham;
	}

	public int getSoLuongTon() {
		return soLuongTon;
	}

	public int getSoLuongHuy() {
		return soLuongHuy;
	}

	public void setSoLuongHuy(int soLuongHuy) {
		this.soLuongHuy = soLuongHuy;
	}

	public double getDonGiaNhap() {
		return donGiaNhap;
	}

	public double getThanhTien() {
		// Tính theo số lượng gốc vì đơn giá là của đơn vị gốc
		return donGiaNhap * getSoLuongHuyTheoGoc();
	}

	public String getLyDo() {
		return lyDo;
	}

	public void setLyDo(String lyDo) {
		this.lyDo = lyDo;
	}

	public int getSoLuongHuyTheoGoc() {
		if (quyCachHienTai == null || quyCachGoc == null) {
			return soLuongHuy;
		}
		return soLuongHuy * quyCachHienTai.getHeSoQuyDoi();
	}

	// Getters/Setters
	public QuyCachDongGoi getQuyCachHienTai() {
		return quyCachHienTai;
	}

	public void setQuyCachHienTai(QuyCachDongGoi qc) {
		this.quyCachHienTai = qc;
	}

	public QuyCachDongGoi getQuyCachGoc() {
		return quyCachGoc;
	}

	public void setQuyCachGoc(QuyCachDongGoi qc) {
		this.quyCachGoc = qc;
	}
}
