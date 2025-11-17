package gui;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.*;

import customcomponent.TaoJtextNhanh;
import dao.LoSanPham_DAO;
import dao.QuyCachDongGoi_DAO;
import entity.ItemDonHang;
import entity.LoSanPham;
import entity.QuyCachDongGoi;
import entity.SanPham;

/**
 * JPanel đại diện 1 dòng sản phẩm trong đơn hàng
 */
public class DonHangItemPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final DecimalFormat DF = new DecimalFormat("#,##0");

    // ===== DATA =====
    private ItemDonHang item;
    private int stt;
    private String[] donViArr;
    private int[] heSoArr;
    private double[] giaArr;
    private String anhPath;

    private BanHang_GUI parentGUI;
    private List<ItemDonHang> dsItem;
    private LoSanPham_DAO loSanPhamDao;
    private QuyCachDongGoi_DAO quyCachDongGoiDao;

    // ===== UI =====
    private JLabel lblSTT;
    private JTextField txtTonLocal;
    private JComboBox<String> cbDonVi;
    private JLabel lblQuyDoi;
    private JButton btnGiam;
    private JButton btnTang;
    private JTextField txtSoLuong;
    private JTextField txtKM;
    private JTextField txtDonGia;
    private JTextField txtThanhTien;
    private JButton btnNhanDoi;
    private JButton btnXoa;

    // ===== Listener callback về GUI cha =====
    public interface ItemPanelListener {
        void onItemUpdated(ItemDonHang item);
        void onItemDeleted(ItemDonHang item, DonHangItemPanel panel);
    }

    private ItemPanelListener listener;

    public DonHangItemPanel(
            ItemDonHang item,
            int stt,
            String[] donViArr,
            int[] heSoArr,
            double[] giaArr,
            String anhPath,
            ItemPanelListener listener,
            BanHang_GUI parentGUI,
            List<ItemDonHang> dsItem,
            LoSanPham_DAO loSanPhamDao,
            QuyCachDongGoi_DAO quyCachDongGoiDao
    ) {
        this.item = item;
        this.stt = stt;
        this.donViArr = donViArr;
        this.heSoArr = heSoArr;
        this.giaArr = giaArr;
        this.anhPath = anhPath;
        this.listener = listener;
        this.parentGUI = parentGUI;
        this.dsItem = dsItem;
        this.loSanPhamDao = loSanPhamDao;
        this.quyCachDongGoiDao = quyCachDongGoiDao;

        khoiTaoGiaoDien();
        ganSuKien();
        capNhatGiaoDien();
    }

    // =====================================================
    // =============== KHỞI TẠO GIAO DIỆN ==================
    // =====================================================
    private void khoiTaoGiaoDien() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        setBorder(new CompoundBorder(
                new LineBorder(new Color(0xDDDDDD), 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        setBackground(new Color(0xFAFAFA));
        setOpaque(true);

        putClientProperty("item", item);

        // ===== STT =====
        lblSTT = new JLabel(String.valueOf(stt));
        lblSTT.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSTT.setPreferredSize(new Dimension(40, 30));
        add(lblSTT);
        add(Box.createHorizontalStrut(10));

        // ===== ẢNH =====
        JLabel lblAnh = new JLabel();
        lblAnh.setPreferredSize(new Dimension(80, 80));
        lblAnh.setBorder(new LineBorder(Color.LIGHT_GRAY));
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(anhPath));
            lblAnh.setIcon(new ImageIcon(icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
        } catch (Exception e) {
            lblAnh.setText("Ảnh");
        }
        add(lblAnh);
        add(Box.createHorizontalStrut(10));

        // ===== THÔNG TIN THUỐC =====
        Box infoBox = Box.createVerticalBox();
        infoBox.setBorder(new LineBorder(Color.BLACK));

        JTextField txtTenThuoc = TaoJtextNhanh.taoTextDonHang(
                item.getTenSanPham(), new Font("Segoe UI", Font.BOLD, 16), new Color(0x00796B), 300);
        infoBox.add(txtTenThuoc);

        Box loBox = Box.createHorizontalBox();
        loBox.setMaximumSize(new Dimension(300, 30));

        JTextField txtLo = TaoJtextNhanh.taoTextDonHang(
                "Lô: " + item.getMaLo(), new Font("Segoe UI", Font.BOLD, 16), new Color(0x00796B), 150);
        loBox.add(txtLo);
        loBox.add(Box.createHorizontalStrut(8));

        txtTonLocal = TaoJtextNhanh.taoTextDonHang(
                "Tồn: " + item.getTonKho(), new Font("Segoe UI", Font.BOLD, 16), new Color(0x00796B), 150);
        loBox.add(txtTonLocal);

        infoBox.add(loBox);
        add(infoBox);
        add(Box.createHorizontalStrut(10));

        // ===== ĐƠN VỊ =====
        cbDonVi = new JComboBox<>(donViArr);
        cbDonVi.setPreferredSize(new Dimension(70, 30));
        cbDonVi.setMaximumSize(new Dimension(70, 30));
        cbDonVi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbDonVi.setSelectedItem(item.getTenDonViHienTai());
        add(cbDonVi);

        lblQuyDoi = new JLabel();
        lblQuyDoi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblQuyDoi.setPreferredSize(new Dimension(80, 30));
        add(Box.createHorizontalStrut(5));
        add(lblQuyDoi);

        add(Box.createHorizontalStrut(20));

        // ===== SỐ LƯỢNG + / - =====
        Box soLuongBox = Box.createHorizontalBox();
        soLuongBox.setMaximumSize(new Dimension(140, 30));
        soLuongBox.setPreferredSize(new Dimension(140, 30));
        soLuongBox.setBorder(new LineBorder(new Color(0xDDDDDD), 1, true));

        btnGiam = new JButton("-");
        btnGiam.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnGiam.setPreferredSize(new Dimension(40, 30));
        btnGiam.setMargin(new Insets(0, 0, 0, 0));
        btnGiam.setFocusPainted(false);
        soLuongBox.add(btnGiam);

        txtSoLuong = TaoJtextNhanh.hienThi(
                String.valueOf(item.getSoLuongMua()), new Font("Segoe UI", Font.PLAIN, 16), Color.BLACK);
        txtSoLuong.setMaximumSize(new Dimension(600, 30));
        txtSoLuong.setHorizontalAlignment(SwingConstants.CENTER);
        txtSoLuong.setEditable(true);
        soLuongBox.add(txtSoLuong);

        btnTang = new JButton("+");
        btnTang.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnTang.setPreferredSize(new Dimension(40, 30));
        btnTang.setMargin(new Insets(0, 0, 0, 0));
        btnTang.setFocusPainted(false);
        btnTang.setName("btnTang");
        soLuongBox.add(btnTang);

        add(soLuongBox);
        add(Box.createHorizontalStrut(10));

        // ===== KHUYẾN MÃI =====
        txtKM = TaoJtextNhanh.taoTextDonHang(
                item.getTextKM(), new Font("Segoe UI", Font.PLAIN, 16), Color.BLACK, 110);
        txtKM.setToolTipText(item.getTooltipKM());
        add(txtKM);
        add(Box.createHorizontalStrut(10));

        // ===== ĐƠN GIÁ =====
        txtDonGia = TaoJtextNhanh.taoTextDonHang(
                formatTien(item.getDonGiaSauKM()), new Font("Segoe UI", Font.PLAIN, 16), Color.BLACK, 100);
        txtDonGia.setHorizontalAlignment(SwingConstants.RIGHT);
        add(txtDonGia);
        add(Box.createHorizontalStrut(10));

        // ===== THÀNH TIỀN =====
        txtThanhTien = TaoJtextNhanh.taoTextDonHang(
                formatTien(item.getThanhTienSauKM()), new Font("Segoe UI", Font.BOLD, 16), new Color(0xD32F2F), 120);
        txtThanhTien.setHorizontalAlignment(SwingConstants.RIGHT);
        add(txtThanhTien);
        add(Box.createHorizontalGlue());

        // ===== NHÂN DÒNG =====
        btnNhanDoi = new JButton("Nhân dòng");
        btnNhanDoi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnNhanDoi.setPreferredSize(new Dimension(40, 40));
        btnNhanDoi.setFocusPainted(false);
        add(Box.createHorizontalStrut(5));
        add(btnNhanDoi);

        if (donViArr == null || donViArr.length <= 1) {
            btnNhanDoi.setEnabled(false);
        }

        // ===== XÓA =====
        btnXoa = new JButton();
        btnXoa.setPreferredSize(new Dimension(40, 40));
        btnXoa.setBorderPainted(false);
        btnXoa.setContentAreaFilled(false);
        btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/bin.png"));
            btnXoa.setIcon(new ImageIcon(icon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH)));
        } catch (Exception ignored) {}
        add(btnXoa);
    }

    // =====================================================
    // ================= GÁN SỰ KIỆN =======================
    // =====================================================
    private void ganSuKien() {
        Runnable baoDongDaKhoa = () -> JOptionPane.showMessageDialog(
                parentGUI,
                "Dòng này đã đủ số lượng cho lô hiện tại.\n"
                        + "Vui lòng chỉnh sửa ở lô mới phía dưới trước.",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE
        );

        // ===== NÚT TĂNG =====
        btnTang.addActionListener(ev -> {
            if (item.isKhoaChinhSua()) {
                baoDongDaKhoa.run();
                return;
            }

            int slCu;
            try {
                slCu = Integer.parseInt(txtSoLuong.getText().trim());
            } catch (NumberFormatException ex) {
                slCu = item.getSoLuongMua();
            }

            int slMoi = slCu + 1;
            int slMaxTrongLo = tinhSoLuongToiDaTrongLo(item);

            if (slMaxTrongLo <= 0) {
                JOptionPane.showMessageDialog(parentGUI,
                        "Lô " + item.getMaLo() + " đã hết hàng.",
                        "Hết hàng",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (slMoi <= slMaxTrongLo && kiemTraTongTonChoLo(item, slMoi)) {
                item.setSoLuongMua(slMoi);
                capNhatGiaoDien();
                thongBaoCapNhat();
                return;
            }

            int choice = JOptionPane.showConfirmDialog(
                    parentGUI,
                    "Lô " + item.getMaLo() + " đã đủ số lượng cho các dòng hiện tại.\n"
                            + "Bạn có muốn thêm lô kế tiếp để lấy thêm 1 "
                            + item.getTenDonViHienTai() + " không?",
                    "Thiếu hàng",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                boolean ok = taoDongMoiChoLoKeTiep(1);
                if (!ok) {
                    JOptionPane.showMessageDialog(parentGUI,
                            "Không còn lô nào khác đủ hàng.",
                            "Hết hàng",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // ===== NÚT GIẢM =====
        btnGiam.addActionListener(ev -> {
            if (item.isKhoaChinhSua()) {
                baoDongDaKhoa.run();
                return;
            }

            int sl;
            try {
                sl = Integer.parseInt(txtSoLuong.getText().trim());
            } catch (NumberFormatException ex) {
                sl = item.getSoLuongMua();
            }
            int slMoi = sl - 1;
            if (slMoi < 1) {
                JOptionPane.showMessageDialog(parentGUI, "Số lượng không được bé hơn 1");
                return;
            }
            item.setSoLuongMua(slMoi);
            capNhatGiaoDien();
            thongBaoCapNhat();
        });

        // ===== NHẬP TAY SỐ LƯỢNG (Enter) =====
        txtSoLuong.addActionListener(ev -> xuLyNhapSoLuong());

        // ===== NHẬP TAY - MẤT FOCUS =====
        txtSoLuong.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                xuLyNhapSoLuong();
            }
        });

        // ===== ĐỔI ĐƠN VỊ =====
        final String[] lastDonVi = { item.getTenDonViHienTai() };
        cbDonVi.addActionListener(ev -> {
            if (item.isKhoaChinhSua()) {
                baoDongDaKhoa.run();
                cbDonVi.setSelectedItem(item.getTenDonViHienTai());
                return;
            }

            String dv = (String) cbDonVi.getSelectedItem();
            if (dv == null || dv.equals(lastDonVi[0])) return;

            for (ItemDonHang it : dsItem) {
                if (it == item) continue;
                if (!it.getSanPham().getMaSanPham().equals(item.getSanPham().getMaSanPham())) continue;
                if (!it.getLoSanPham().getMaLo().equals(item.getLoSanPham().getMaLo())) continue;

                if (dv.equals(it.getTenDonViHienTai())) {
                    JOptionPane.showMessageDialog(parentGUI,
                            "Đơn vị " + dv + " đã được dùng ở dòng khác của cùng sản phẩm & lô.\n"
                                    + "Vui lòng chọn đơn vị khác.",
                            "Trùng đơn vị",
                            JOptionPane.WARNING_MESSAGE);
                    cbDonVi.setSelectedItem(lastDonVi[0]);
                    return;
                }
            }

            item.setDonVi(dv);
            lastDonVi[0] = dv;
            capNhatGiaoDien();
            thongBaoCapNhat();
        });

        // ===== NÚT NHÂN DÒNG =====
        btnNhanDoi.addActionListener(ev -> xuLyNhanDoiDong(baoDongDaKhoa));

        // ===== NÚT XÓA =====
        btnXoa.addActionListener(ev -> {
            if (item.isKhoaChinhSua()) {
                baoDongDaKhoa.run();
                return;
            }

            // Mở khóa các dòng cùng SP nếu đang khóa
            for (ItemDonHang it : dsItem) {
                if (it != item
                        && it.isKhoaChinhSua()
                        && it.getSanPham().getMaSanPham().equals(item.getSanPham().getMaSanPham())) {
                    it.setKhoaChinhSua(false);
                }
            }

            dsItem.remove(item);
            if (listener != null) listener.onItemDeleted(item, this);
            
        });
    }

    // =====================================================
    // ================== XỬ LÝ NGHIỆP VỤ ==================
    // =====================================================

    private void xuLyNhapSoLuong() {
        if (item.isKhoaChinhSua()) {
            txtSoLuong.setText(String.valueOf(item.getSoLuongMua()));
            return;
        }

        try {
            int slMoi = Integer.parseInt(txtSoLuong.getText().trim());
            if (slMoi < 1) {
                JOptionPane.showMessageDialog(parentGUI, "Số lượng phải >= 1");
                txtSoLuong.setText(String.valueOf(item.getSoLuongMua()));
                return;
            }

            int slMax = tinhSoLuongToiDaTrongLo(item);

            if (slMax <= 0) {
                JOptionPane.showMessageDialog(parentGUI,
                        "Lô " + item.getMaLo() + " đã hết hàng.",
                        "Hết hàng",
                        JOptionPane.WARNING_MESSAGE);
                txtSoLuong.setText(String.valueOf(item.getSoLuongMua()));
                return;
            }

            if (slMoi <= slMax && !kiemTraTongTonChoLo(item, slMoi)) {
                JOptionPane.showMessageDialog(parentGUI,
                        "Tổng số lượng các dòng cùng sản phẩm & lô này đã vượt tồn khả dụng.\n"
                                + "Vui lòng giảm bớt số lượng ở các dòng khác hoặc tạo lô mới.",
                        "Vượt tồn lô",
                        JOptionPane.WARNING_MESSAGE);
                txtSoLuong.setText(String.valueOf(item.getSoLuongMua()));
                return;
            }

            if (slMoi <= slMax) {
                item.setSoLuongMua(slMoi);
                capNhatGiaoDien();
                thongBaoCapNhat();
                return;
            }

            int soDu = slMoi - slMax;
            item.setSoLuongMua(slMax);
            capNhatGiaoDien();
            thongBaoCapNhat();

            int choice = JOptionPane.showConfirmDialog(
                    parentGUI,
                    "Lô " + item.getMaLo() + " chỉ còn tối đa "
                            + slMax + " " + item.getTenDonViHienTai() + ".\n"
                            + "Bạn có muốn thêm lô kế tiếp để lấy "
                            + soDu + " " + item.getTenDonViHienTai() + " còn lại không?",
                    "Thiếu hàng",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                boolean ok = taoDongMoiChoLoKeTiep(soDu);
                if (!ok) {
                    JOptionPane.showMessageDialog(parentGUI,
                            "Không còn lô nào khác đủ hàng.",
                            "Hết hàng",
                            JOptionPane.WARNING_MESSAGE);
                }
            }

        } catch (NumberFormatException ex) {
            txtSoLuong.setText(String.valueOf(item.getSoLuongMua()));
        }
    }

    private void xuLyNhanDoiDong(Runnable baoDongDaKhoa) {
        if (item.isKhoaChinhSua()) {
            baoDongDaKhoa.run();
            return;
        }

        if (donViArr == null || donViArr.length <= 1) {
            JOptionPane.showMessageDialog(parentGUI,
                    "Sản phẩm này chỉ có 1 đơn vị tính, không thể nhân dòng sang đơn vị khác.",
                    "Không thể nhân dòng",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        java.util.Set<String> usedDV = new java.util.HashSet<>();
        for (ItemDonHang it : dsItem) {
            if (!it.getSanPham().getMaSanPham().equals(item.getSanPham().getMaSanPham())) continue;
            if (!it.getLoSanPham().getMaLo().equals(item.getLoSanPham().getMaLo())) continue;
            usedDV.add(it.getTenDonViHienTai());
        }

        String dvMoi = null;
        for (String dv : donViArr) {
            if (!usedDV.contains(dv)) {
                dvMoi = dv;
                break;
            }
        }

        if (dvMoi == null) {
            JOptionPane.showMessageDialog(parentGUI,
                    "Đã dùng hết các đơn vị có thể cho lô này, không thể nhân dòng thêm.",
                    "Không thể nhân dòng",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        LoSanPham lo = item.getLoSanPham();
        SanPham sp = item.getSanPham();

        int tongCanLayBase = 0;
        for (ItemDonHang it : dsItem) {
            if (!it.getSanPham().getMaSanPham().equals(sp.getMaSanPham())) continue;
            if (!it.getLoSanPham().getMaLo().equals(lo.getMaLo())) continue;

            QuyCachDongGoi qcIt = it.getMapQuyCach().get(it.getTenDonViHienTai());
            if (qcIt == null) continue;
            tongCanLayBase += it.getSoLuongMua() * qcIt.getHeSoQuyDoi();
        }

        QuyCachDongGoi qcMoi = item.getMapQuyCach().get(dvMoi);
        if (qcMoi == null) {
            JOptionPane.showMessageDialog(parentGUI,
                    "Không lấy được quy cách của đơn vị " + dvMoi,
                    "Lỗi quy cách",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int heSoMoi = qcMoi.getHeSoQuyDoi();
        int tonBase = lo.getSoLuongTon();

        if (tongCanLayBase + heSoMoi > tonBase) {
            JOptionPane.showMessageDialog(parentGUI,
                    "Không thể nhân dòng vì tồn lô không đủ để thêm 1 " + dvMoi + ".",
                    "Vượt tồn lô",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double giaBanGocDonViGoc = sp.getGiaBan();
        double giaNoiDung = giaBanGocDonViGoc * heSoMoi;
        double donGiaMoi = giaNoiDung - giaNoiDung * qcMoi.getTiLeGiam();

        ItemDonHang itemMoi = new ItemDonHang(sp, lo, item.getKhuyenMai(),
                item.getMapQuyCach(), dvMoi, donGiaMoi);
        itemMoi.setSoLuongMua(1);

        dsItem.add(itemMoi);

        // Giao cho GUI cha tạo UI dòng mới
        parentGUI.themSanPhamTuPanel(itemMoi, donViArr, heSoArr, giaArr, anhPath);
        thongBaoCapNhat();
    }

    private boolean taoDongMoiChoLoKeTiep(int soLuongChoLoMoi) {
        LoSanPham loHienTai = item.getLoSanPham();
        SanPham sp = item.getSanPham();

        LoSanPham loKe = loSanPhamDao.timLoKeTiepTheoSanPham(
                sp.getMaSanPham(), loHienTai.getHanSuDung());
        if (loKe == null) return false;

        QuyCachDongGoi qcHienTai = item.getQuyCachHienTai();
        if (qcHienTai == null) return false;

        int heSo = qcHienTai.getHeSoQuyDoi();
        int tonKeBase = loKe.getSoLuongTon();
        int slMaxLoKe = tonKeBase / heSo;
        if (slMaxLoKe <= 0) return false;

        int slThucTeChoLoMoi = Math.min(soLuongChoLoMoi, slMaxLoKe);

        item.setKhoaChinhSua(true);

        Map<String, QuyCachDongGoi> mapQC = item.getMapQuyCach();
        String dv = item.getTenDonViHienTai();

        double giaBanGoc = sp.getGiaBan();
        double donGiaNoiDung = giaBanGoc * heSo;
        double donGiaMacDinh = donGiaNoiDung - donGiaNoiDung * qcHienTai.getTiLeGiam();

        ItemDonHang itemMoi = new ItemDonHang(sp, loKe, item.getKhuyenMai(),
                mapQC, dv, donGiaMacDinh);
        itemMoi.setSoLuongMua(slThucTeChoLoMoi);
        dsItem.add(itemMoi);

        parentGUI.themSanPhamTuPanel(itemMoi, donViArr, heSoArr, giaArr, anhPath);
        thongBaoCapNhat();
        return true;
    }

    private int tinhSoLuongToiDaTrongLo(ItemDonHang item) {
        QuyCachDongGoi qc = item.getQuyCachHienTai();
        if (qc == null) return 0;
        int heSo = qc.getHeSoQuyDoi();
        int tonBase = item.getTonKho();
        if (heSo <= 0) return 0;
        return tonBase / heSo;
    }

    private boolean kiemTraTongTonChoLo(ItemDonHang itemDangSua, int soLuongMoi) {
        LoSanPham lo = itemDangSua.getLoSanPham();
        SanPham sp = itemDangSua.getSanPham();
        int tongCanLayBase = 0;

        for (ItemDonHang it : dsItem) {
            if (!it.getSanPham().getMaSanPham().equals(sp.getMaSanPham())) continue;
            if (!it.getLoSanPham().getMaLo().equals(lo.getMaLo())) continue;

            QuyCachDongGoi qc = it.getMapQuyCach().get(it.getTenDonViHienTai());
            if (qc == null) continue;

            int heSo = qc.getHeSoQuyDoi();
            int sl = (it == itemDangSua) ? soLuongMoi : it.getSoLuongMua();
            tongCanLayBase += sl * heSo;
        }

        int tonBase = lo.getSoLuongTon();
        return tongCanLayBase <= tonBase;
    }

    // =====================================================
    // ================== CẬP NHẬT UI ======================
    // =====================================================
    private void capNhatGiaoDien() {
        lblQuyDoi.setText("x" + item.getHeSoQuyCach() + " " + item.getDonViGoc());
        txtTonLocal.setText("Tồn: " + item.getTonKho());
        txtSoLuong.setText(String.valueOf(item.getSoLuongMua()));
        txtDonGia.setText(formatTien(item.getDonGiaSauKM()));
        txtThanhTien.setText(formatTien(item.getThanhTienSauKM()));
        txtKM.setText(item.getTextKM());
        txtKM.setToolTipText(item.getTooltipKM());

        double giaGoc = item.getDonGiaGoc();
        int sl = item.getSoLuongMua();
        double thanhTien = item.getThanhTienSauKM();
        double tongGiamSP = item.getTongGiamGiaSP();

        String tooltip;
        if (tongGiamSP > 0) {
            tooltip = String.format(
                    "<html>Giá gốc: %s/đv<br>Số lượng: %d<br>Tổng giảm: %s<br><b>Thành tiền: %s</b></html>",
                    formatTien(giaGoc), sl, formatTien(tongGiamSP), formatTien(thanhTien));
        } else {
            tooltip = String.format(
                    "<html>Giá: %s/đv<br>Số lượng: %d<br><b>Thành tiền: %s</b></html>",
                    formatTien(giaGoc), sl, formatTien(thanhTien));
        }
        txtThanhTien.setToolTipText(tooltip);
    }

    private void thongBaoCapNhat() {
        if (listener != null) listener.onItemUpdated(item);
    }

    private String formatTien(double t) {
        return DF.format(t) + " đ";
    }

    // =====================================================
    // ================== GET/SET PHỤ ======================
    // =====================================================
    public void setStt(int stt) {
        this.stt = stt;
        lblSTT.setText(String.valueOf(stt));
    }

    public ItemDonHang getItem() {
        return item;
    }
}
