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
        // 1. Giá gốc của đơn vị hiện tại (Ví dụ: Giá Hộp = 100k)
        double giaGocDonViHienTai = this.donGiaGoc;

        // Lấy hệ số quy đổi của đơn vị hiện tại (Ví dụ Hộp = 100 viên -> heSo = 100)
        int heSo = getHeSoQuyCach(); 

        // 2. Tính tiền giảm (nếu có KM)
        double tienGiamTren1DonVi = 0;

        if (this.khuyenMai != null) {
            String hinhThuc = this.khuyenMai.getKhuyenMai().getHinhThuc().name();
            double giaTriKM = this.khuyenMai.getKhuyenMai().getGiaTri(); // Đây là tiền giảm cho 1 ĐƠN VỊ GỐC

            if (hinhThuc.equals("GIAM_GIA_PHAN_TRAM")) {
                // Phần trăm thì không cần nhân hệ số, vì giá hộp đã cao gấp 100 lần giá viên rồi
                // 10% của 100k tự động to hơn 10% của 1k.
                tienGiamTren1DonVi = giaGocDonViHienTai * (giaTriKM / 100.0);
                
            } else if (hinhThuc.equals("GIAM_GIA_TIEN")) {
                // ⚠️ LOGIC MỚI:
                // Tiền giảm = (Tiền giảm gốc) * (Hệ số quy đổi)
                // Ví dụ: Giảm 500đ/viên. Bán Hộp (100 viên) -> Giảm 500 * 100 = 50.000
                tienGiamTren1DonVi = giaTriKM * heSo;
            }
        }

        // 3. Đảm bảo không âm (Chặn giá chót)
        this.donGiaSauKM = Math.max(0, giaGocDonViHienTai - tienGiamTren1DonVi);

        // 4. Tổng tiền
        this.thanhTienSauKM = this.donGiaSauKM * this.soLuongMua;
        this.tongGiamGiaSP  = (giaGocDonViHienTai - this.donGiaSauKM) * this.soLuongMua; 
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
    public void setKhuyenMai(ChiTietKhuyenMaiSanPham khuyenMai) {
        this.khuyenMai = khuyenMai;
        // Sau khi set (hoặc xóa) KM, phải tính lại tiền ngay
        tinhLaiThanhTien();
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
