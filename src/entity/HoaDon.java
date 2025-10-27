	package entity;
	
	import java.time.LocalDate;
	import java.util.ArrayList;
	import java.util.List;
	import java.util.Objects;
	
	public class HoaDon {
	
	    private String maHoaDon;
	    private String maKhachHang;
	    private LocalDate ngayLap;
	    private NhanVien nhanVien;
	    private KhuyenMai khuyenMai;
	    private boolean thuocTheoDon;
	    private List<ChiTietHoaDon> chiTietHoaDonList;
	
	    public HoaDon() {
	        this.chiTietHoaDonList = new ArrayList<>();
	    }
	
	    public HoaDon(String maHoaDon, String maKhachHang, LocalDate ngayLap, NhanVien nhanVien, KhuyenMai khuyenMai, boolean thuocTheoDon) {
	        this.maHoaDon = maHoaDon;
	        setMaKhachHang(maKhachHang);
	        setNgayLap(ngayLap);
	        setNhanVien(nhanVien);
	        setKhuyenMai(khuyenMai);
	        setThuocTheoDon(thuocTheoDon);
	        this.chiTietHoaDonList = new ArrayList<>();
	    }
	
	    public HoaDon(HoaDon other) {
	        this.maHoaDon = other.maHoaDon;
	        this.maKhachHang = other.maKhachHang;
	        this.ngayLap = other.ngayLap;
	        this.nhanVien = other.nhanVien;
	        this.khuyenMai = other.khuyenMai;
	        this.thuocTheoDon = other.thuocTheoDon;
	        this.chiTietHoaDonList = new ArrayList<>(other.chiTietHoaDonList);
	    }
	
	    public double getTongTien() {
	        if (this.chiTietHoaDonList == null || this.chiTietHoaDonList.isEmpty()) {
	            return 0;
	        }
	        double total = 0;
	        for (ChiTietHoaDon ct : this.chiTietHoaDonList) {
	            total += ct.getThanhTien();
	        }
	        return total;
	    }
	
	    public String getMaHoaDon() {
	        return maHoaDon;
	    }
	
	    public void setMaHoaDon(String maHoaDon) {
	        if (maHoaDon != null && maHoaDon.matches("^HD-\\d{8}-\\d{4}$")) {
	            this.maHoaDon = maHoaDon;
	        } else {
	            throw new IllegalArgumentException("Mã hoá đơn không hợp lệ. Định dạng yêu cầu: HD-yyyymmdd-xxxx");
	        }
	    }
	
	    public String getMaKhachHang() {
	        return maKhachHang;
	    }
	
	    public void setMaKhachHang(String maKhachHang) {
	        if (maKhachHang == null || maKhachHang.trim().isEmpty()) {
	            throw new IllegalArgumentException("Khách hàng không tồn tại.");
	        }
	        this.maKhachHang = maKhachHang;
	    }
	
	    public LocalDate getNgayLap() {
	        return ngayLap;
	    }
	
	    public void setNgayLap(LocalDate ngayLap) {
	        if (ngayLap == null || ngayLap.isAfter(LocalDate.now())) {
	            throw new IllegalArgumentException("Ngày lập không hợp lệ.");
	        }
	        this.ngayLap = ngayLap;
	    }
	
	    public NhanVien getNhanVien() {
	        return nhanVien;
	    }
	
	    public void setNhanVien(NhanVien nhanVien) {
	        if (nhanVien == null) {
	            throw new IllegalArgumentException("Nhân viên không tồn tại.");
	        }
	        this.nhanVien = nhanVien;
	    }
	
	    public KhuyenMai getKhuyenMai() {
	        return khuyenMai;
	    }
	
	    public void setKhuyenMai(KhuyenMai khuyenMai) {
	        this.khuyenMai = khuyenMai;
	    }
	
	    public boolean isThuocTheoDon() {
	        return thuocTheoDon;
	    }
	
	    public void setThuocTheoDon(boolean thuocTheoDon) {
	        this.thuocTheoDon = thuocTheoDon;
	    }
	
	    public List<ChiTietHoaDon> getChiTietHoaDonList() {
	        return chiTietHoaDonList;
	    }
	
	    public void setChiTietHoaDonList(List<ChiTietHoaDon> chiTietHoaDonList) {
	        this.chiTietHoaDonList = chiTietHoaDonList;
	    }
	
	    @Override
	    public String toString() {
	        return "HoaDon{" +
	                "maHoaDon='" + maHoaDon + '\'' +
	                ", maKhachHang='" + maKhachHang + '\'' +
	                ", ngayLap=" + ngayLap +
	                ", nhanVien=" + nhanVien +
	                ", khuyenMai=" + khuyenMai +
	                ", thuocTheoDon=" + thuocTheoDon +
	                ", tongTien=" + getTongTien() +
	                '}';
	    }
	
	    @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (o == null || getClass() != o.getClass()) return false;
	        HoaDon hoaDon = (HoaDon) o;
	        return Objects.equals(maHoaDon, hoaDon.maHoaDon);
	    }
	
	    @Override
	    public int hashCode() {
	        return Objects.hash(maHoaDon);
	    }
	}