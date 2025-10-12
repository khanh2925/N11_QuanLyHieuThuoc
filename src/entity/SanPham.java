package entity;
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
    private String quyCachDongGoi;
    private String keBanSanPham;
    private boolean hoatDong;
    private String hanSuDung;

    public SanPham() {
    }


    public SanPham(String maSanPham, String tenSanPham, LoaiSanPham loaiSanPham, String soDangKy, String hoatChat, String hamLuong, String hangSanXuat, String xuatXu, DonViTinh donViTinh, DuongDung duongDung, double giaNhap, double giaBan, String hinhAnh, String quyCachDongGoi, String keBanSanPham, boolean hoatDong, String hanSuDung) {
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.loaiSanPham = loaiSanPham;
        this.soDangKy = soDangKy;
        this.hoatChat = hoatChat;
        this.hamLuong = hamLuong;
        this.hangSanXuat = hangSanXuat;
        this.xuatXu = xuatXu;
        this.donViTinh = donViTinh;
        this.duongDung = duongDung;
        this.giaNhap = giaNhap;
        this.giaBan = giaBan; // <-- Gán giá trị giaBan
        this.hinhAnh = hinhAnh;
        this.quyCachDongGoi = quyCachDongGoi;
        this.keBanSanPham = keBanSanPham;
        this.hoatDong = hoatDong;
        this.hanSuDung = hanSuDung;
    }

    // --- GETTERS AND SETTERS ---

    // getGiaBan() và setGiaBan() đã được trả lại như một thuộc tính thông thường
    public double getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(double giaBan) {
        this.giaBan = giaBan;
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public LoaiSanPham getLoaiSanPham() {
        return loaiSanPham;
    }

    public void setLoaiSanPham(LoaiSanPham loaiSanPham) {
        this.loaiSanPham = loaiSanPham;
    }

    public String getSoDangKy() {
        return soDangKy;
    }

    public void setSoDangKy(String soDangKy) {
        this.soDangKy = soDangKy;
    }

    public String getHoatChat() {
        return hoatChat;
    }

    public void setHoatChat(String hoatChat) {
        this.hoatChat = hoatChat;
    }

    public String getHamLuong() {
        return hamLuong;
    }

    public void setHamLuong(String hamLuong) {
        this.hamLuong = hamLuong;
    }

    public String getHangSanXuat() {
        return hangSanXuat;
    }

    public void setHangSanXuat(String hangSanXuat) {
        this.hangSanXuat = hangSanXuat;
    }

    public String getXuatXu() {
        return xuatXu;
    }

    public void setXuatXu(String xuatXu) {
        this.xuatXu = xuatXu;
    }

    public DonViTinh getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(DonViTinh donViTinh) {
        this.donViTinh = donViTinh;
    }

    public DuongDung getDuongDung() {
        return duongDung;
    }

    public void setDuongDung(DuongDung duongDung) {
        this.duongDung = duongDung;
    }

    public double getGiaNhap() {
        return giaNhap;
    }

    public void setGiaNhap(double giaNhap) {
        this.giaNhap = giaNhap;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public String getQuyCachDongGoi() {
        return quyCachDongGoi;
    }

    public void setQuyCachDongGoi(String quyCachDongGoi) {
        this.quyCachDongGoi = quyCachDongGoi;
    }

    public String getKeBanSanPham() {
        return keBanSanPham;
    }

    public void setKeBanSanPham(String keBanSanPham) {
        this.keBanSanPham = keBanSanPham;
    }

    public boolean isHoatDong() {
        return hoatDong;
    }

    public void setHoatDong(boolean hoatDong) {
        this.hoatDong = hoatDong;
    }

    public String getHanSuDung() {
        return hanSuDung;
    }

    public void setHanSuDung(String hanSuDung) {
        this.hanSuDung = hanSuDung;
    }

    @Override
    public String toString() {
        return "SanPham{" +
                "maSanPham='" + maSanPham + '\'' +
                ", tenSanPham='" + tenSanPham + '\'' +
                ", giaNhap=" + giaNhap +
                ", giaBan=" + giaBan +
                '}';
    }
}