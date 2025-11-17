package entity;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * Entity đóng gói một dòng trong đơn hàng (1 sản phẩm, 1 lô, 1 quy cách)
 * Chứa toàn bộ logic tính toán: khuyến mãi, thành tiền, kiểm kho
 */
public class ItemDonHang {
    private static final DecimalFormat DF = new DecimalFormat("#,##0");

    // === DỮ LIỆU GỐC (KHÔNG THAY ĐỔI) ===
    private SanPham sanPham;
    private LoSanPham loSanPham;
    private ChiTietKhuyenMaiSanPham khuyenMai;

    // Map từ TÊN đơn vị -> QuyCach
    private Map<String, QuyCachDongGoi> mapQuyCach;

    // === DỮ LIỆU HIỆN TẠI (CÓ THỂ THAY ĐỔI) ===
    private String tenDonViHienTai;   // Lưu tên đơn vị, dùng để tra map
    private int soLuongMua;

    // === KẾT QUẢ TÍNH TOÁN ===
    private double donGiaGoc;         // Giá gốc theo đơn vị hiện tại
    private double donGiaSauKM;       // Giá sau áp dụng KM (theo 1 đơn vị)
    private double thanhTienSauKM;    // Tổng tiền khách trả
    private double tongGiamGiaSP;     // Tổng tiền giảm cho dòng này
    private boolean coHangTang = false; // Giờ không dùng nữa nhưng giữ lại để code cũ khỏi lỗi
    private boolean khoaChinhSua = false;

    public ItemDonHang(SanPham sp, LoSanPham lo, ChiTietKhuyenMaiSanPham km,
                       Map<String, QuyCachDongGoi> mapQC,
                       String tenDonViMacDinh, double giaMacDinh) {
        this.sanPham = sp;
        this.loSanPham = lo;
        this.khuyenMai = km;
        this.mapQuyCach = mapQC;
        this.tenDonViHienTai = tenDonViMacDinh;
        this.donGiaGoc = giaMacDinh;
        this.soLuongMua = 1;
        tinhLaiThanhTien();
    }

    // ===== GETTER =====
    public SanPham getSanPham() { return sanPham; }
    public LoSanPham getLoSanPham() { return loSanPham; }
    public Map<String, QuyCachDongGoi> getMapQuyCach() { return mapQuyCach; }
    public String getTenDonViHienTai() { return tenDonViHienTai; }
    public QuyCachDongGoi getQuyCachHienTai() { return mapQuyCach.get(tenDonViHienTai); }
    public ChiTietKhuyenMaiSanPham getKhuyenMai() { return khuyenMai; }
    public int getSoLuongMua() { return soLuongMua; }
    public double getDonGiaGoc() { return donGiaGoc; }
    public double getDonGiaSauKM() { return donGiaSauKM; }
    public double getThanhTienSauKM() { return thanhTienSauKM; }
    public double getTongGiamGiaSP() { return tongGiamGiaSP; }
    public boolean isCoHangTang() { return coHangTang; }

    public String getTenKhuyenMai() {
        return khuyenMai != null ? khuyenMai.getKhuyenMai().getTenKM() : "Không có KM";
    }

    public String getTenSanPham() { return sanPham.getTenSanPham(); }
    public String getMaLo() { return loSanPham.getMaLo(); }
    public int getTonKho() { return loSanPham.getSoLuongTon(); }

    public boolean isKhoaChinhSua() {
        return khoaChinhSua;
    }

    public void setKhoaChinhSua(boolean khoaChinhSua) {
        this.khoaChinhSua = khoaChinhSua;
    }

    // ===== SETTER =====
    public void setSoLuongMua(int soLuong) {
        this.soLuongMua = Math.max(1, soLuong);
        tinhLaiThanhTien();
    }

    /**
     * Đổi đơn vị, cập nhật lại số lượng & đơn giá theo đơn vị mới
     */
    public void setDonVi(String tenDonVi) {
        if (!mapQuyCach.containsKey(tenDonVi)) return;

        QuyCachDongGoi qcCu  = mapQuyCach.get(tenDonViHienTai);
        QuyCachDongGoi qcMoi = mapQuyCach.get(tenDonVi);
        if (qcCu == null || qcMoi == null) return;

        // 1) Quy đổi SL hiện tại về đơn vị nhỏ nhất
        int heSoCu  = qcCu.getHeSoQuyDoi();
        int heSoMoi = qcMoi.getHeSoQuyDoi();
        int slQuyVeNhoNhat = this.soLuongMua * heSoCu;

        // 2) Đổi sang đơn vị mới, làm tròn xuống, đảm bảo >= 1
        int slMoi = slQuyVeNhoNhat / heSoMoi;
        if (slMoi < 1) slMoi = 1;
        this.soLuongMua = slMoi;

        // 3) Đơn giá gốc theo đơn vị mới
        double giaBanGoc     = sanPham.getGiaBan();         // giá 1 đơn vị nhỏ nhất
        double donGiaNoiDung = giaBanGoc * heSoMoi;        // giá 1 đơn vị mới
        this.donGiaGoc       = donGiaNoiDung - donGiaNoiDung * qcMoi.getTiLeGiam();

        // 4) Cập nhật đơn vị hiện tại và tính lại KM
        this.tenDonViHienTai = tenDonVi;
        tinhLaiThanhTien();
    }

    /**
     * Tính lại thành tiền + áp dụng khuyến mãi
     * (KHÔNG còn khuyến mãi tặng thêm, KHÔNG còn số lượng tối thiểu)
     */
    public void tinhLaiThanhTien() {
        // ===== 0. Giá theo đơn vị hiện tại (đã có TiLeGiam của quy cách) =====
        // donGiaGoc ở đây là GIÁ BÁN / 1 ĐƠN VỊ HIỆN TẠI (viên / vỉ / hộp...) sau khi áp TiLeGiam.
        double giaTheoDonViHienTai = this.donGiaGoc;

        // Reset
        this.coHangTang    = false;
        this.tongGiamGiaSP = 0;
        this.donGiaSauKM   = giaTheoDonViHienTai;

        // Nếu không có KM thì khỏi tính nhiều
        if (this.khuyenMai == null) {
            if (this.donGiaSauKM < 0) this.donGiaSauKM = 0;
            this.thanhTienSauKM = this.donGiaSauKM * this.soLuongMua;
            return;
        }

        // ===== 1. Quy TẤT CẢ về đơn vị gốc =====
        QuyCachDongGoi qc = getQuyCachHienTai();
        int heSo = (qc != null) ? qc.getHeSoQuyDoi() : 1;  // số đơn vị gốc / 1 đơn vị hiện tại

        int soLuongBase = this.soLuongMua * heSo;          // tổng số đơn vị gốc
        if (soLuongBase <= 0) {
            // phòng hờ trường hợp số lượng <= 0
            this.soLuongMua = 1;
            soLuongBase = heSo;
        }

        double giaBasePerUnit = sanPham.getGiaBan();       // GIÁ / 1 ĐƠN VỊ GỐC
        double thanhTienBaseTruocKM = giaBasePerUnit * soLuongBase; // tiền gốc tính theo ĐƠN VỊ GỐC

        // ===== 2. TÍNH SỐ TIỀN GIẢM KM THEO ĐƠN VỊ GỐC =====
        String hinhThuc = this.khuyenMai.getKhuyenMai().getHinhThuc().name();
        double giaTriKM = this.khuyenMai.getKhuyenMai().getGiaTri();

        double tongGiamKmBase = 0; // tổng tiền giảm KM TÍNH THEO ĐƠN VỊ GỐC

        if (hinhThuc.equals("GIAM_GIA_PHAN_TRAM")) {
            // % khuyến mãi tính trên tổng tiền gốc theo đơn vị gốc
            tongGiamKmBase = thanhTienBaseTruocKM * (giaTriKM / 100.0);
        } else if (hinhThuc.equals("GIAM_GIA_TIEN")) {
            // GIAM_GIA_TIEN hiểu là giảm X tiền / 1 ĐƠN VỊ GỐC
            tongGiamKmBase = giaTriKM * soLuongBase;
        }

        if (tongGiamKmBase < 0) tongGiamKmBase = 0;
        if (tongGiamKmBase > thanhTienBaseTruocKM) tongGiamKmBase = thanhTienBaseTruocKM;

        // ===== 3. Phân bổ lại tiền giảm về đơn vị hiện tại =====
        // Tổng giảm trên cả dòng (theo base) -> giảm mỗi 1 đơn vị hiện tại bao nhiêu?
        double giamMoiDonViHienTai = tongGiamKmBase / this.soLuongMua;  // vì dòng có 'soLuongMua' đơn vị hiện tại

        this.donGiaSauKM = giaTheoDonViHienTai - giamMoiDonViHienTai;
        if (this.donGiaSauKM < 0) this.donGiaSauKM = 0;

        this.thanhTienSauKM = this.donGiaSauKM * this.soLuongMua;
        this.tongGiamGiaSP  = giamMoiDonViHienTai * this.soLuongMua; // = tongGiamKmBase (sau clamp)
    }


    /**
     * Kiểm tra tồn kho hợp lệ (KHÔNG tính hàng tặng nữa)
     */
    public boolean kiemTraTonKhoHopLe(int soLuongTangThemKhongDungNua) {
        QuyCachDongGoi qc = mapQuyCach.get(tenDonViHienTai);
        if (qc == null) return false;

        // Quy hết về đơn vị nhỏ nhất (vd: viên)
        int heSo = qc.getHeSoQuyDoi();

        // SL cần lấy từ kho (đơn vị nhỏ nhất)
        int soLuongCanLay = this.soLuongMua * heSo;

        // Tồn kho đã đang là đơn vị nhỏ nhất
        int tonQuyVeNhoNhat = this.loSanPham.getSoLuongTon();

        return soLuongCanLay <= tonQuyVeNhoNhat;
    }

    /**
     * Lấy số lượng TẶNG THÊM (giờ bỏ logic tặng → luôn 0)
     */
    public int getSoLuongTangThem() {
        return 0;
    }

    /**
     * Lấy hệ số quy đổi của đơn vị hiện tại
     */
    public int getHeSoQuyCach() {
        QuyCachDongGoi qc = mapQuyCach.get(tenDonViHienTai);
        return qc != null ? qc.getHeSoQuyDoi() : 1;
    }

    /**
     * Lấy tên đơn vị gốc (nhỏ nhất)
     */
    public String getDonViGoc() {
        for (QuyCachDongGoi qc : mapQuyCach.values()) {
            if (qc.isDonViGoc()) {
                return qc.getDonViTinh().getTenDonViTinh();
            }
        }
        return tenDonViHienTai;
    }

    /**
     * Tooltip cho hiển thị khuyến mãi (KHÔNG còn "Mua ≥ ...")
     */
    public String getTooltipKM() {
        if (khuyenMai == null) return null;

        String hinhThuc = khuyenMai.getKhuyenMai().getHinhThuc().name();
        double giaTri = khuyenMai.getKhuyenMai().getGiaTri();

        if (hinhThuc.equals("GIAM_GIA_PHAN_TRAM")) {
            double giam = donGiaGoc - donGiaSauKM;
            return String.format(
                    "<html>"
                    + "Giá gốc: %s/đv<br>"
                    + "Giảm: %s/đv (%.0f%%)<br>"
                    + "Giá sau giảm: %s/đv"
                    + "</html>",
                    DF.format(donGiaGoc),
                    DF.format(giam),
                    giaTri,
                    DF.format(donGiaSauKM)
            );
        } else if (hinhThuc.equals("GIAM_GIA_TIEN")) {
            double soTienGiam = giaTri;
            return String.format(
                    "<html>"
                    + "Giá gốc: %s/đv<br>"
                    + "Giảm: %s/đv<br>"
                    + "Giá sau giảm: %s/đv"
                    + "</html>",
                    DF.format(donGiaGoc),
                    DF.format(soTienGiam),
                    DF.format(donGiaSauKM)
            );
        }

        return null;
    }

    /**
     * Text hiển thị khuyến mãi (cho txtKM ở GUI)
     * KHÔNG còn text "Mua ≥ ..."
     */
    public String getTextKM() {
        if (khuyenMai == null) return "Không có KM";

        String hinhThuc = khuyenMai.getKhuyenMai().getHinhThuc().name();
        double giaTri = khuyenMai.getKhuyenMai().getGiaTri();

        if (hinhThuc.equals("GIAM_GIA_PHAN_TRAM")) {
            return "Giảm " + (int) giaTri + "%";
        } else if (hinhThuc.equals("GIAM_GIA_TIEN")) {
            return "Giảm " + DF.format(giaTri) + "/đv";
        }

        return "Không có KM";
    }

}
