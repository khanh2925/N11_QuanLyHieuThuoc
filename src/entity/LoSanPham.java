
package entity;

import java.time.LocalDate;
import java.util.Objects;

public class LoSanPham {

	private String maLo;
	private LocalDate hanSuDung;
	private int soLuongTon;
	private SanPham sanPham;

	public LoSanPham() {
	}

	public LoSanPham(String maLo, LocalDate hanSuDung, int soLuongTon, SanPham sanPham) {
		setMaLo(maLo);
		setHanSuDung(hanSuDung);
		setSoLuongTon(soLuongTon);
		setSanPham(sanPham);
	}

	public LoSanPham(LoSanPham other) {
		this.maLo = other.maLo;
		this.hanSuDung = other.hanSuDung;
		this.soLuongTon = other.soLuongTon;
		this.sanPham = other.sanPham;
	}

	public LoSanPham(String maLo) {
		setMaLo(maLo);
	}

	public String getMaLo() {
		return maLo;
	}

	public void setMaLo(String maLo) {
		if (maLo != null && maLo.matches("^LO-\\d{6}$")) {
			this.maLo = maLo;
		} else {
			throw new IllegalArgumentException("Mã lô không hợp lệ. Định dạng yêu cầu: LO-xxxxxx");
		}
	}

	public LocalDate getHanSuDung() {
		return hanSuDung;
	}

	public void setHanSuDung(LocalDate hanSuDung) {
		if (hanSuDung == null) {
			throw new IllegalArgumentException("Hạn sử dụng không được rỗng.");
		}
		this.hanSuDung = hanSuDung;
	}

	public int getSoLuongTon() {
		return soLuongTon;
	}

	public void setSoLuongTon(int soLuongTon) {
		if (soLuongTon < 0) {
			throw new IllegalArgumentException("Số lượng tồn phải lớn hơn hoặc bằng 0.");
		}
		this.soLuongTon = soLuongTon;
	}

	public SanPham getSanPham() {
		return sanPham;
	}

	public void setSanPham(SanPham sanPham) {
		if (sanPham == null) {
			throw new IllegalArgumentException("Sản phẩm không tồn tại.");
		}
		this.sanPham = sanPham;
	}

	@Override
	public String toString() {
		return "LoSanPham{" + "maLo='" + maLo + '\'' + ", hanSuDung=" + hanSuDung + ", soLuongTon=" + soLuongTon
				+ ", sanPham=" + (sanPham != null ? sanPham.getTenSanPham() : "N/A") + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		LoSanPham loSanPham = (LoSanPham) o;
		return Objects.equals(maLo, loSanPham.maLo);
	}

	@Override
	public int hashCode() {
		return Objects.hash(maLo);
	}
}
