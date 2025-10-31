package enums;

public enum DuongDung {
    UONG("Uống"),
    TIEM("Tiêm"),
    NHO("Nhỏ"),
    BOI("Bôi"),
    HIT("Hít"),
    NGAM("Ngậm"),
    DAT("Đặt"),
    DAN("Dán");

    private final String moTa;

    DuongDung(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }

    @Override
    public String toString() {
        return moTa; // tiện cho comboBox hiển thị tiếng Việt
    }
}
