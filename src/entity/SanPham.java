package entity;

import java.util.Objects;

/**
 * Lớp đại diện cho thông tin Sản phẩm trong hệ thống quản lý hiệu thuốc.
 * 
 * @author Quốc
 * @version 2.2
 * @since 15/10/2025
 */
public class SanPham {

    private String maSanPham;
    private String tenSanPham;
    private LoaiSanPham loaiSanPham;
    private String soDangKy;
    private String hoatChat;
    private String hamLuong;
    private String hangSanXuat;
    private String xuatXu;
    private DonViTinh donViTinh;
    private DuongDung duongDung;
    private double giaNhap;
    private double giaBan;
    private String hinhAnh;
    
    public SanPham(String maSanPham) {
		this.maSanPham = maSanPham;
	}

	private String quyCachDongGoi;
    private String keBanSanPham;
    private boolean hoatDong; // ✅ đổi sang kiểu primitive để tránh NullPointerException

    // ===== CONSTRUCTOR =====

    public SanPham() {
    }

    public SanPham(String maSanPham, String tenSanPham, LoaiSanPham loaiSanPham, String soDangKy,
                   String hoatChat, String hamLuong, String hangSanXuat, String xuatXu,
                   DonViTinh donViTinh, DuongDung duongDung,
                   double giaNhap, double giaBan, String hinhAnh,
                   String quyCachDongGoi, String keBanSanPham, boolean hoatDong) {

        setMaSanPham(maSanPham);
        setTenSanPham(tenSanPham);
        setLoaiSanPham(loaiSanPham);
        setSoDangKy(soDangKy);
        setHoatChat(hoatChat);
        setHamLuong(hamLuong);
        setHangSanXuat(hangSanXuat);
        setXuatXu(xuatXu);
        setDonViTinh(donViTinh);
        setDuongDung(duongDung);
        setGiaNhap(giaNhap);
        setGiaBan(giaBan);
        setHinhAnh(hinhAnh);
        setQuyCachDongGoi(quyCachDongGoi);
        setKeBanSanPham(keBanSanPham);
        setHoatDong(hoatDong);
    }

    public SanPham(SanPham sp) {
        this.maSanPham = sp.maSanPham;
        this.tenSanPham = sp.tenSanPham;
        this.loaiSanPham = sp.loaiSanPham;
        this.soDangKy = sp.soDangKy;
        this.hoatChat = sp.hoatChat;
        this.hamLuong = sp.hamLuong;
        this.hangSanXuat = sp.hangSanXuat;
        this.xuatXu = sp.xuatXu;
        this.donViTinh = sp.donViTinh;
        this.duongDung = sp.duongDung;
        this.giaNhap = sp.giaNhap;
        this.giaBan = sp.giaBan;
        this.hinhAnh = sp.hinhAnh;
        this.quyCachDongGoi = sp.quyCachDongGoi;
        this.keBanSanPham = sp.keBanSanPham;
        this.hoatDong = sp.hoatDong;
    }

    // ===== GETTER / SETTER =====

    public String getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        if (maSanPham == null)
            throw new IllegalArgumentException("Mã sản phẩm không được để trống.");
        if (!maSanPham.matches("^SP\\d{6}$"))
            throw new IllegalArgumentException("Mã sản phẩm không hợp lệ (định dạng yêu cầu: SPxxxxxx).");
        this.maSanPham = maSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        if (tenSanPham == null || tenSanPham.trim().isEmpty())
            throw new IllegalArgumentException("Tên sản phẩm không được rỗng.");
        if (tenSanPham.length() > 100)
            throw new IllegalArgumentException("Tên sản phẩm không được vượt quá 100 ký tự.");
        this.tenSanPham = tenSanPham;
    }

    public LoaiSanPham getLoaiSanPham() {
        return loaiSanPham;
    }

    public void setLoaiSanPham(LoaiSanPham loaiSanPham) {
        if (loaiSanPham == null)
            throw new IllegalArgumentException("Loại sản phẩm không được null.");
        this.loaiSanPham = loaiSanPham;
    }

    public String getSoDangKy() {
        return soDangKy;
    }

    public void setSoDangKy(String soDangKy) {
        if (soDangKy != null && soDangKy.length() > 20)
            throw new IllegalArgumentException("Số đăng ký không hợp lệ (tối đa 20 ký tự).");
        this.soDangKy = soDangKy;
    }

    public String getHoatChat() {
        return hoatChat;
    }

    public void setHoatChat(String hoatChat) {
        if (hoatChat != null && hoatChat.length() > 100)
            throw new IllegalArgumentException("Hoạt chất không được vượt quá 100 ký tự.");
        this.hoatChat = hoatChat;
    }

    public String getHamLuong() {
        return hamLuong;
    }

    public void setHamLuong(String hamLuong) {
        if (hamLuong != null && hamLuong.length() > 50)
            throw new IllegalArgumentException("Hàm lượng không được vượt quá 50 ký tự.");
        this.hamLuong = hamLuong;
    }

    public String getHangSanXuat() {
        return hangSanXuat;
    }

    public void setHangSanXuat(String hangSanXuat) {
        if (hangSanXuat != null && hangSanXuat.length() > 100)
            throw new IllegalArgumentException("Hãng sản xuất không được vượt quá 100 ký tự.");
        this.hangSanXuat = hangSanXuat;
    }

    public String getXuatXu() {
        return xuatXu;
    }

    public void setXuatXu(String xuatXu) {
        if (xuatXu != null && xuatXu.length() > 100)
            throw new IllegalArgumentException("Xuất xứ không được vượt quá 100 ký tự.");
        this.xuatXu = xuatXu;
    }

    public DonViTinh getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(DonViTinh donViTinh) {
        if (donViTinh == null)
            throw new IllegalArgumentException("Đơn vị tính không tồn tại.");
        this.donViTinh = donViTinh;
    }

    public DuongDung getDuongDung() {
        return duongDung;
    }

    public void setDuongDung(DuongDung duongDung) {
        if (duongDung == null)
            throw new IllegalArgumentException("Đường dùng không tồn tại.");
        this.duongDung = duongDung;
    }

    public double getGiaNhap() {
        return giaNhap;
    }

    public void setGiaNhap(double giaNhap) {
        if (giaNhap <= 0)
            throw new IllegalArgumentException("Giá nhập phải lớn hơn 0.");
        this.giaNhap = giaNhap;
    }

    public double getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(double giaBan) {
        if (giaBan <= 0)
            throw new IllegalArgumentException("Giá bán phải lớn hơn 0.");

        if (this.giaNhap <= 0)
            throw new IllegalStateException("Cần nhập giá nhập trước khi xác định giá bán.");

        // Tính hệ số lợi nhuận tối thiểu
        double heSoLoiNhuan;
        if (this.giaNhap < 10000) heSoLoiNhuan = 1.5;
        else if (this.giaNhap < 50000) heSoLoiNhuan = 1.3;
        else if (this.giaNhap < 200000) heSoLoiNhuan = 1.2;
        else heSoLoiNhuan = 1.1;

        double giaBanToiThieu = this.giaNhap * heSoLoiNhuan;

        if (giaBan < giaBanToiThieu)
            throw new IllegalArgumentException("Giá bán phải cao hơn giá nhập theo tỷ lệ lợi nhuận tối thiểu.");

        this.giaBan = giaBan; // ✅ giữ nguyên giá người nhập, không ép về giá tối thiểu
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        if (hinhAnh != null && hinhAnh.length() > 255)
            throw new IllegalArgumentException("Đường dẫn hình ảnh không được vượt quá 255 ký tự.");
        this.hinhAnh = hinhAnh;
    }

    public String getQuyCachDongGoi() {
        return quyCachDongGoi;
    }

    public void setQuyCachDongGoi(String quyCachDongGoi) {
        if (quyCachDongGoi != null && quyCachDongGoi.length() > 100)
            throw new IllegalArgumentException("Quy cách đóng gói không được vượt quá 100 ký tự.");
        this.quyCachDongGoi = quyCachDongGoi;
    }

    public String getKeBanSanPham() {
        return keBanSanPham;
    }

    public void setKeBanSanPham(String keBanSanPham) {
        if (keBanSanPham != null && keBanSanPham.length() > 100)
            throw new IllegalArgumentException("Kệ bán sản phẩm không được vượt quá 100 ký tự.");
        this.keBanSanPham = keBanSanPham;
    }

    public boolean isHoatDong() {
        return hoatDong;
    }

    public void setHoatDong(boolean hoatDong) {
        this.hoatDong = hoatDong;
    }

    // ===== OVERRIDE =====

    @Override
    public String toString() {
        return String.format("SanPham{ma='%s', ten='%s', giaBan=%.0f, hoatDong=%s}",
                maSanPham, tenSanPham, giaBan, hoatDong);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SanPham sp = (SanPham) o;
        return Objects.equals(maSanPham, sp.maSanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maSanPham);
    }
}
