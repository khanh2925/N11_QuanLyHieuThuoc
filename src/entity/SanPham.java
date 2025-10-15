package entity;

import java.util.Objects;

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
    private Boolean hoatDong;

    public SanPham() {
    }

    public SanPham(String maSanPham, String tenSanPham, LoaiSanPham loaiSanPham, String soDangKy, String hoatChat, String hamLuong, String hangSanXuat, String xuatXu, DonViTinh donViTinh, DuongDung duongDung, double giaNhap, double giaBan, String hinhAnh, String quyCachDongGoi, String keBanSanPham, Boolean hoatDong) {
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

    public String getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        if (maSanPham != null && maSanPham.matches("^SP\\d{6}$")) {
            this.maSanPham = maSanPham;
        } else {
            throw new IllegalArgumentException("Mã sản phẩm không hợp lệ. Định dạng yêu cầu: SPxxxxxx");
        }
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        if (tenSanPham == null || tenSanPham.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được rỗng.");
        }
        if (tenSanPham.length() > 100) {
            throw new IllegalArgumentException("Tên sản phẩm không được vượt quá 100 ký tự.");
        }
        this.tenSanPham = tenSanPham;
    }

    public LoaiSanPham getLoaiSanPham() {
        return loaiSanPham;
    }

    public void setLoaiSanPham(LoaiSanPham loaiSanPham) {
        if (loaiSanPham == null) {
            throw new IllegalArgumentException("Loại sản phẩm không tồn tại.");
        }
        this.loaiSanPham = loaiSanPham;
    }

    public String getSoDangKy() {
        return soDangKy;
    }

    public void setSoDangKy(String soDangKy) {
        if (soDangKy != null && soDangKy.length() > 20) {
            throw new IllegalArgumentException("Số đăng ký không hợp lệ.");
        }
        this.soDangKy = soDangKy;
    }

    public String getHoatChat() {
        return hoatChat;
    }

    public void setHoatChat(String hoatChat) {
        if (hoatChat != null && hoatChat.length() > 100) {
            throw new IllegalArgumentException("Hoạt chất không được vượt quá 100 ký tự.");
        }
        this.hoatChat = hoatChat;
    }

    public String getHamLuong() {
        return hamLuong;
    }

    public void setHamLuong(String hamLuong) {
        if (hamLuong != null && hamLuong.length() > 50) {
            throw new IllegalArgumentException("Hàm lượng không được vượt quá 50 ký tự.");
        }
        this.hamLuong = hamLuong;
    }

    public String getHangSanXuat() {
        return hangSanXuat;
    }

    public void setHangSanXuat(String hangSanXuat) {
        if (hangSanXuat != null && hangSanXuat.length() > 100) {
            throw new IllegalArgumentException("Hãng sản xuất không được vượt quá 100 ký tự.");
        }
        this.hangSanXuat = hangSanXuat;
    }

    public String getXuatXu() {
        return xuatXu;
    }

    public void setXuatXu(String xuatXu) {
        if (xuatXu != null && xuatXu.length() > 100) {
            throw new IllegalArgumentException("Xuất xứ không được vượt quá 100 ký tự.");
        }
        this.xuatXu = xuatXu;
    }

    public DonViTinh getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(DonViTinh donViTinh) {
        if (donViTinh == null) {
            throw new IllegalArgumentException("Đơn vị tính không tồn tại.");
        }
        this.donViTinh = donViTinh;
    }

    public DuongDung getDuongDung() {
        return duongDung;
    }

    public void setDuongDung(DuongDung duongDung) {
        if (duongDung == null) {
            throw new IllegalArgumentException("Đường dùng không tồn tại.");
        }
        this.duongDung = duongDung;
    }

    public double getGiaNhap() {
        return giaNhap;
    }

    public void setGiaNhap(double giaNhap) {
        if (giaNhap <= 0) {
            throw new IllegalArgumentException("Giá nhập phải lớn hơn 0.");
        }
        if (this.giaBan > 0 && giaNhap >= this.giaBan) {
            throw new IllegalArgumentException("Giá nhập phải nhỏ hơn giá bán.");
        }
        this.giaNhap = giaNhap;
    }

    public double getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(double giaBan) {
        if (giaBan <= 0) {
            throw new IllegalArgumentException("Giá bán phải lớn hơn 0.");
        }

        double heSoLoiNhuan = 0;
        if (this.giaNhap < 10000) {
            heSoLoiNhuan = 1.5;
        } else if (this.giaNhap < 50000) {
            heSoLoiNhuan = 1.3;
        } else if (this.giaNhap < 200000) {
            heSoLoiNhuan = 1.2;
        } else {
            heSoLoiNhuan = 1.1;
        }

        double giaBanToiThieu = this.giaNhap * heSoLoiNhuan;

        if (giaBan < giaBanToiThieu) {
            throw new IllegalArgumentException("Giá bán phải cao hơn giá nhập theo đúng tỷ lệ lợi nhuận tối thiểu.");
        }
        this.giaBan = giaBan;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        if (hinhAnh != null && hinhAnh.length() > 255) {
            throw new IllegalArgumentException("Đường dẫn hình ảnh không được vượt quá 255 ký tự.");
        }
        this.hinhAnh = hinhAnh;
    }

    public String getQuyCachDongGoi() {
        return quyCachDongGoi;
    }

    public void setQuyCachDongGoi(String quyCachDongGoi) {
        if (quyCachDongGoi != null && quyCachDongGoi.length() > 100) {
            throw new IllegalArgumentException("Quy cách đóng gói không được vượt quá 100 ký tự.");
        }
        this.quyCachDongGoi = quyCachDongGoi;
    }

    public String getKeBanSanPham() {
        return keBanSanPham;
    }

    public void setKeBanSanPham(String keBanSanPham) {
        if (keBanSanPham != null && keBanSanPham.length() > 100) {
            throw new IllegalArgumentException("Kệ bán sản phẩm không được vượt quá 100 ký tự.");
        }
        this.keBanSanPham = keBanSanPham;
    }

    public Boolean getHoatDong() {
        return hoatDong;
    }

    public void setHoatDong(Boolean hoatDong) {
        this.hoatDong = hoatDong;
    }

    @Override
    public String toString() {
        return "SanPham{" +
                "maSanPham='" + maSanPham + '\'' +
                ", tenSanPham='" + tenSanPham + '\'' +
                ", giaBan=" + giaBan +
                ", hoatDong=" + hoatDong +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SanPham sanPham = (SanPham) o;
        return Objects.equals(maSanPham, sanPham.maSanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maSanPham);
    }
}