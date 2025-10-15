package entity;

public class LoaiSanPham {
	private String maLoaiSanPham;
	private String tenLoaiSanPham;
	private String moTa;

	public LoaiSanPham() {

	}

	public LoaiSanPham(String maLoaiSanPham, String tenLoaiSanPham, String moTa) {
		this.maLoaiSanPham = maLoaiSanPham;
		this.tenLoaiSanPham = tenLoaiSanPham;
		this.moTa = moTa;

	}

	// Getters and Setters
	public String getMaLoaiSanPham() {
		return maLoaiSanPham;
	}

	public void setMaLoaiSanPham(String maLoaiSanPham) {
		this.maLoaiSanPham = maLoaiSanPham;
	}

	public String getTenLoaiSanPham() {
		return tenLoaiSanPham;
	}

	public void setTenLoaiSanPham(String tenLoaiSanPham) {
		this.tenLoaiSanPham = tenLoaiSanPham;
	}

	public String getMoTa() {
		return moTa;
	}

	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}

	@Override
	public String toString() {
		return "LoaiSanPham [maLoaiSanPham=" + maLoaiSanPham + ", tenLoaiSanPham=" + tenLoaiSanPham + ", moTa=" + moTa
				+ "]";
	}
}
