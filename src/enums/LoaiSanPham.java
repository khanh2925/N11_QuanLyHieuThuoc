package enums;


public enum LoaiSanPham {
    THUOC("Thuốc"),
    VAT_TU("Vật tư y tế"),
    THUC_PHAM_BO_SUNG("Thực phẩm bổ sung"),
    THIET_BI_Y_TE("Thiết bị y tế");

    private final String tenLoai;

    LoaiSanPham(String tenLoai) {
        this.tenLoai = tenLoai;
    }

    public String getTenLoai() {
        return tenLoai;
    }
}