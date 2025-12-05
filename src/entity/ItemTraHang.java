package entity;

import java.util.ArrayList;
import java.util.List;

public class ItemTraHang {

	private String maLo;
	private String tenSanPham;

	private List<QuyCachDongGoi> dsQuyCach; // Danh sách QC của sản phẩm
	private List<ChiTietHoaDon> dsCthd; // Danh sách CTHD của sản phẩm

	private QuyCachDongGoi quyCachDangChon; // QC đang chọn (vỉ/viên/hộp)

	private int soLuongMuaGoc; // Tổng SL đơn vị gốc (viên)
	private int soLuongMua; // SL theo đơn vị hiện tại
	private int soLuongTra; // SL trả

	private String donViTinh;
	private double donGia; // Giá theo DVT hiện tại
	private double donGiaGocTheoVien; // Giá thực tế trên 1 đơn vị gốc (viên)

	private String lyDo;
	private int soLuongMuaTheoDVT;

	// ===================== CONSTRUCTOR CHUẨN ======================
	public ItemTraHang(String maLo, String tenSanPham, List<ChiTietHoaDon> dsCthd, List<QuyCachDongGoi> dsQuyCach,
			QuyCachDongGoi qcMacDinh) {

		this.maLo = maLo;
		this.tenSanPham = tenSanPham;

		this.dsCthd = dsCthd;
		this.dsQuyCach = dsQuyCach;

		// tính tổng SL và đơn giá theo viên
		gomDuLieuHoaDon();

		// áp QC mặc định (thường là QC lớn nhất mà khách mua)
		applyQuyCach(qcMacDinh);

		this.soLuongTra = 1;
		this.lyDo = "";
	}

	// Constructor legacy - dùng khi sản phẩm KHÔNG có QuyCachDongGoi
	public ItemTraHang(String maLo, String tenSanPham, String donViTinh, double donGia, int soLuongMua,
			ChiTietHoaDon cthdGoc) {

		this.maLo = maLo;
		this.tenSanPham = tenSanPham;

		// chỉ có 1 dòng CTHD
		this.dsCthd = new ArrayList<>();
		this.dsCthd.add(cthdGoc);

		this.dsQuyCach = null; // không dùng quy cách

		// coi đơn vị hiện tại là đơn vị gốc luôn
		this.soLuongMuaGoc = soLuongMua;
		this.soLuongMua = soLuongMua;
		this.soLuongTra = 1;

		this.donViTinh = donViTinh;
		this.donGia = donGia;

		this.lyDo = "";
	}

	// ===================== GOM TẤT CẢ CTHD CỦA SP ======================
	private void gomDuLieuHoaDon() {
		int tongSL = 0;
		double tongTien = 0;

		for (ChiTietHoaDon ct : dsCthd) {
			QuyCachDongGoi qc = getQuyCachTheoMaDonViTinh(ct.getDonViTinh().getMaDonViTinh());
			if (qc == null)
				continue;

			int heSo = qc.getHeSoQuyDoi(); // quy ra viên

			tongSL += ct.getSoLuong() * heSo;
			tongTien += ct.getThanhTien();
		}

		this.soLuongMuaGoc = tongSL;
		this.donGiaGocTheoVien = tongTien / tongSL;
	}

	// ===================== ÁP QUY CÁCH ĐỔI ĐƠN VỊ ======================
	public void applyQuyCach(QuyCachDongGoi qc) {
		if (qc == null)
			return;

		this.quyCachDangChon = qc;

		int heSo = qc.getHeSoQuyDoi(); // ví dụ: vỉ = 10, viên = 1

		// đơn giá theo DVT hiện tại
		this.donGia = donGiaGocTheoVien * heSo;

		this.soLuongMua = 0;
		this.soLuongMuaTheoDVT = 0;

		// chỉnh giới hạn trả
		if (soLuongTra > soLuongMua) {
			soLuongTra = Math.max(1, soLuongMua);
		}

		this.donViTinh = qc.getDonViTinh().getTenDonViTinh();
	}

	// ===================== GET QC theo mã DVT ======================
	public QuyCachDongGoi getQuyCachTheoMaDonViTinh(String maDVT) {
		if (dsQuyCach == null)
			return null;
		for (QuyCachDongGoi qc : dsQuyCach) {
			if (qc.getDonViTinh().getMaDonViTinh().equals(maDVT)) {
				return qc;
			}
		}
		return null;
	}

	// ===================== GETTERS ======================
	public String getMaLo() {
		return maLo;
	}

	public String getTenSanPham() {
		return tenSanPham;
	}

	public List<QuyCachDongGoi> getDsQuyCach() {
		return dsQuyCach;
	}

	public List<ChiTietHoaDon> getDsCthd() {
		return dsCthd;
	}

	public int getSoLuongMuaGoc() {
		return soLuongMuaGoc;
	}

	public int getSoLuongMua() {
		return soLuongMua;
	}

	public int getSoLuongTra() {
		return soLuongTra;
	}

	public void setSoLuongTra(int soLuongTra) {
		this.soLuongTra = soLuongTra;
	}

	public int getSoLuongTraQuyVeGoc() {
		return soLuongTra * quyCachDangChon.getHeSoQuyDoi();
	}

	public String getDonViTinh() {
		return donViTinh;
	}

	public double getDonGia() {
		return donGia;
	}

	public double getThanhTien() {
		return donGia * soLuongTra;
	}

	public String getLyDo() {
		return lyDo;
	}

	public void setLyDo(String lyDo) {
		this.lyDo = lyDo;
	}

	public QuyCachDongGoi getQuyCachDangChon() {
		return quyCachDangChon;
	}

	public ChiTietHoaDon getChiTietHoaDonGoc() {
		if (dsCthd == null || dsCthd.isEmpty()) {
			return null;
		}
		return dsCthd.get(0);
	}

	public int getSoLuongMuaTheoDVT() {
		return soLuongMuaTheoDVT;
	}

	public void setSoLuongMuaTheoDVT(int sl) {
		this.soLuongMuaTheoDVT = sl;
	}

	public void setSoLuongMua(int soLuongMua) {
		this.soLuongMua = soLuongMua;
	}

	public int getSoLuongMuaGocTheoDVT() {
		return getSoLuongMuaTheoDVT() * getQuyCachDangChon().getHeSoQuyDoi();
	}

	@Override
	public ItemTraHang clone() {
		ItemTraHang cp = new ItemTraHang(this.maLo, this.tenSanPham, new ArrayList<>(this.dsCthd),
				new ArrayList<>(this.dsQuyCach), this.quyCachDangChon);

		cp.soLuongTra = this.soLuongTra;
		cp.lyDo = this.lyDo;
		cp.soLuongMuaTheoDVT = this.soLuongMuaTheoDVT;
		cp.soLuongMua = this.soLuongMua;
		cp.quyCachDangChon = this.quyCachDangChon;
		cp.donGia = this.donGia;
		cp.donViTinh = this.donViTinh;

		return cp;
	}

	// ===================== DEBUG ======================
	@Override
	public String toString() {
		return tenSanPham + " | " + donViTinh + " | Đã mua gốc: " + soLuongMuaGoc + " | Đã mua hiện tại: " + soLuongMua
				+ " | Đơn giá: " + donGia;
	}
}
