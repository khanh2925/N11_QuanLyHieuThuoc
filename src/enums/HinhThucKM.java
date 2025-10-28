package enums;

/**
 * Mô tả: Hình thức khuyến mãi áp dụng cho sản phẩm / hóa đơn.
 * Các giá trị lưu trong CSDL: 'GIAM_GIA_PHAN_TRAM', 'GIAM_GIA_TIEN', 'TANG_THEM'
 */
public enum HinhThucKM {
    GIAM_GIA_PHAN_TRAM("Giảm giá %"),
    GIAM_GIA_TIEN("Giảm giá tiền mặt"),
    TANG_THEM("Tặng thêm sản phẩm");

    private final String moTa;

    HinhThucKM(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }

    @Override
    public String toString() {
        return moTa;
    }
}
