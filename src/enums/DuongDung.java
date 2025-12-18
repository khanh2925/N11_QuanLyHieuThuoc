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

    private final String tenDuongDung;

    DuongDung(String tenDuongDung) {
        this.tenDuongDung = tenDuongDung;
    }


    public String getTenDuongDung() {
        return tenDuongDung;
    }

    @Override
    public String toString() {
        return tenDuongDung; // tiện cho comboBox hiển thị tiếng Việt
    }
}
