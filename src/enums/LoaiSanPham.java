package enums;

/**
 * Mô tả: Phân loại các nhóm sản phẩm trong hệ thống nhà thuốc.
 * 
 * Các giá trị lưu trong CSDL:
 *  - THUOC
 *  - THUC_PHAM_BO_SUNG
 *  - MY_PHAM
 *  - DUNG_CU_Y_TE
 *  - SAN_PHAM_CHO_ME_VA_BE
 *  - SAN_PHAM_KHAC
 */
public enum LoaiSanPham {
    THUOC("Thuốc"),
    THUC_PHAM_BO_SUNG("Thực phẩm bổ sung"),
    MY_PHAM("Mỹ phẩm"),
    DUNG_CU_Y_TE("Dụng cụ y tế"),
    SAN_PHAM_CHO_ME_VA_BE("Sản phẩm cho mẹ và bé"),
    SAN_PHAM_KHAC("Sản phẩm khác");

    private final String tenLoai;

    LoaiSanPham(String tenLoai) {
        this.tenLoai = tenLoai;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    @Override
    public String toString() {
        return tenLoai;
    }
}
