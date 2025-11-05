package gui;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import dao.*;
import entity.*;
import enums.HinhThucKM;

/**
 * @description: Giao diện Bán Hàng - Quản lý lô, tồn kho, khách hàng, thanh toán, KHUYẾN MÃI SẢN PHẨM
 * @version: 2.1 (có KM tự động + tặng SP) - FULL FIXED
 * @created: November 5, 2025
 * @updated: November 5, 2025 (sửa lỗi getComponentsList, tính đơn vị, xóa promo safe, actionCommand cho nút +/-)
 */
public class BanHang_GUI extends JPanel implements ActionListener, MouseListener {

    // ====================== COMPONENTS ======================
    private JTextField txtTimThuoc;
    private JPanel pnDanhSachDon;
    private JTextField txtTimKH;
    private JTextField txtTienKhach;
    private JLabel lblTongHangValue;
    private JLabel lblTongHDValue;
    private JLabel lblTienThuaValue;
    private JLabel lblTenKHValue;
    private JButton btnThemDon;
    private PillButton btnBanHang;

    // --- GIẢM GIÁ SP & HD ---
    private JLabel lblGiamSPValue;
    private JLabel lblGiamHDValue;

    // ====================== DAO & ENTITY ======================
    private final SanPham_DAO sanPhamDAO = new SanPham_DAO();
    private final LoSanPham_DAO loDAO = new LoSanPham_DAO();
    private final KhachHang_DAO khachHangDAO = new KhachHang_DAO();
    private final HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
    private final ChiTietHoaDon_DAO chiTietHoaDonDAO = new ChiTietHoaDon_DAO();
    private final QuyCachDongGoi_DAO quyCachDAO = new QuyCachDongGoi_DAO();
    private final DonViTinh_DAO donViTinhDAO = new DonViTinh_DAO();
    private final KhuyenMai_DAO khuyenMaiDAO = new KhuyenMai_DAO();
    private final ChiTietKhuyenMaiSanPham_DAO chiTietKMDAO = new ChiTietKhuyenMaiSanPham_DAO();

    private KhachHang khachHangHienTai = new KhachHang("KH-20000000-0001", "Khách vãng lai", false, "0000000000",
            LocalDate.now().minusYears(18));

    // ====================== CONSTRUCTOR ======================
    public BanHang_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    // ====================== INITIALIZE UI ======================
    private void initialize() {
        setLayout(new BorderLayout());
        add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new CompoundBorder(
                new LineBorder(new Color(0x00C853), 3, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        pnDanhSachDon = new JPanel();
        pnDanhSachDon.setLayout(new BoxLayout(pnDanhSachDon, BoxLayout.Y_AXIS));
        pnDanhSachDon.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(pnDanhSachDon);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        pnCenter.add(scrollPane, BorderLayout.CENTER);
        add(pnCenter, BorderLayout.CENTER);

        add(createRightPanel(), BorderLayout.EAST);
    }

    // ====================== HEADER PANEL ======================
    private JPanel createHeaderPanel() {
        JPanel pnHeader = new JPanel(null);
        pnHeader.setPreferredSize(new Dimension(1073, 88));
        pnHeader.setBackground(new Color(0xE3F2F5));

        txtTimThuoc = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimThuoc, "Nhập số đăng ký thuốc (VD: VN-12345)...");
        txtTimThuoc.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimThuoc.setBounds(25, 17, 480, 60);
        txtTimThuoc.setBorder(new RoundedBorder(20));
        txtTimThuoc.setBackground(Color.WHITE);
        txtTimThuoc.setForeground(Color.GRAY);
        txtTimThuoc.addActionListener(this);
        pnHeader.add(txtTimThuoc);

        btnThemDon = new PillButton("Thêm đơn");
        btnThemDon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnThemDon.setBounds(530, 30, 130, 45);
        btnThemDon.addActionListener(this);
        pnHeader.add(btnThemDon);

        return pnHeader;
    }

    // ====================== RIGHT PANEL ======================
    private JPanel createRightPanel() {
        JPanel pnRight = new JPanel();
        pnRight.setPreferredSize(new Dimension(450, 1080));
        pnRight.setBackground(Color.WHITE);
        pnRight.setBorder(new EmptyBorder(25, 25, 25, 25));
        pnRight.setLayout(new BoxLayout(pnRight, BoxLayout.Y_AXIS));

        // --- TÌM KHÁCH HÀNG ---
        txtTimKH = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKH, "Nhập số điện thoại khách hàng");
        txtTimKH.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtTimKH.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        txtTimKH.setBorder(new LineBorder(new Color(0x00C0E2), 3, true));
        txtTimKH.addActionListener(this);
        pnRight.add(txtTimKH);
        pnRight.add(Box.createVerticalStrut(15));

        // --- TÊN KHÁCH HÀNG ---
        JPanel pnTenKH = new JPanel(new BorderLayout());
        pnTenKH.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pnTenKH.setOpaque(false);
        JLabel lblTenKHLeft = new JLabel("Tên khách hàng:");
        lblTenKHLeft.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblTenKHLeft.setPreferredSize(new Dimension(160, 40));
        lblTenKHValue = new JLabel("Khách lẻ");
        lblTenKHValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTenKHValue.setForeground(new Color(0x00796B));
        lblTenKHValue.setHorizontalAlignment(SwingConstants.RIGHT);
        pnTenKH.add(lblTenKHLeft, BorderLayout.WEST);
        pnTenKH.add(lblTenKHValue, BorderLayout.CENTER);
        pnRight.add(pnTenKH);

        JSeparator sepTenKH = new JSeparator();
        sepTenKH.setForeground(new Color(200, 200, 200));
        sepTenKH.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        pnRight.add(Box.createVerticalStrut(8));
        pnRight.add(sepTenKH);
        pnRight.add(Box.createVerticalStrut(20));

        // --- TỔNG TIỀN HÀNG ---
        JPanel pnTongHang = new JPanel(new BorderLayout());
        pnTongHang.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pnTongHang.setOpaque(false);
        JLabel lblTongHangLeft = new JLabel("Tổng tiền hàng:");
        lblTongHangLeft.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblTongHangLeft.setPreferredSize(new Dimension(160, 40));
        lblTongHangValue = new JLabel("0 đ");
        lblTongHangValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTongHangValue.setHorizontalAlignment(SwingConstants.RIGHT);
        pnTongHang.add(lblTongHangLeft, BorderLayout.WEST);
        pnTongHang.add(lblTongHangValue, BorderLayout.CENTER);
        pnRight.add(pnTongHang);

        // --- GIẢM GIÁ SẢN PHẨM ---
        JPanel pnGiamSP = new JPanel(new BorderLayout());
        pnGiamSP.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pnGiamSP.setOpaque(false);
        JLabel lblGiamSPLeft = new JLabel("Giảm giá sản phẩm:");
        lblGiamSPLeft.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblGiamSPLeft.setPreferredSize(new Dimension(160, 40));
        lblGiamSPValue = new JLabel("0 đ");
        lblGiamSPValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblGiamSPValue.setHorizontalAlignment(SwingConstants.RIGHT);
        pnGiamSP.add(lblGiamSPLeft, BorderLayout.WEST);
        pnGiamSP.add(lblGiamSPValue, BorderLayout.CENTER);
        pnRight.add(pnGiamSP);

        // --- GIẢM GIÁ HÓA ĐƠN ---
        JPanel pnGiamHD = new JPanel(new BorderLayout());
        pnGiamHD.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pnGiamHD.setOpaque(false);
        JLabel lblGiamHDLeft = new JLabel("Giảm giá hóa đơn:");
        lblGiamHDLeft.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblGiamHDLeft.setPreferredSize(new Dimension(160, 40));
        lblGiamHDValue = new JLabel("0 đ");
        lblGiamHDValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblGiamHDValue.setHorizontalAlignment(SwingConstants.RIGHT);
        pnGiamHD.add(lblGiamHDLeft, BorderLayout.WEST);
        pnGiamHD.add(lblGiamHDValue, BorderLayout.CENTER);
        pnRight.add(pnGiamHD);

        JSeparator sepGiamGia = new JSeparator();
        sepGiamGia.setForeground(new Color(200, 200, 200));
        sepGiamGia.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        pnRight.add(Box.createVerticalStrut(8));
        pnRight.add(sepGiamGia);
        pnRight.add(Box.createVerticalStrut(15));

        // --- TỔNG CỘNG ---
        JPanel pnTong = new JPanel(new BorderLayout());
        pnTong.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        pnTong.setOpaque(false);
        lblTongHDValue = new JLabel("TỔNG CỘNG: 0 Đ");
        lblTongHDValue.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTongHDValue.setForeground(new Color(0xD32F2F));
        lblTongHDValue.setHorizontalAlignment(SwingConstants.RIGHT);
        pnTong.add(lblTongHDValue, BorderLayout.CENTER);
        pnRight.add(pnTong);
        pnRight.add(Box.createVerticalStrut(20));

        // --- TIỀN KHÁCH ĐƯA ---
        JPanel pnTienKhachPanel = new JPanel(new BorderLayout(10, 0));
        pnTienKhachPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        pnTienKhachPanel.setOpaque(false);
        JLabel lblTienKhachLabel = new JLabel("Tiền khách đưa:");
        lblTienKhachLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblTienKhachLabel.setPreferredSize(new Dimension(160, 55));
        txtTienKhach = new JTextField();
        txtTienKhach.setFont(new Font("Segoe UI", Font.BOLD, 22));
        txtTienKhach.setHorizontalAlignment(SwingConstants.RIGHT);
        txtTienKhach.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0x00C0E2), 3, true),
                new EmptyBorder(8, 15, 8, 15)));
        txtTienKhach.setBackground(new Color(0xF0FAFA));
        txtTienKhach.setForeground(new Color(0x00796B));
        txtTienKhach.addActionListener(this);
        pnTienKhachPanel.add(lblTienKhachLabel, BorderLayout.WEST);
        pnTienKhachPanel.add(txtTienKhach, BorderLayout.CENTER);
        pnRight.add(pnTienKhachPanel);
        pnRight.add(Box.createVerticalStrut(15));

        // --- TIỀN THỪA ---
        JPanel pnTienThua = new JPanel(new BorderLayout());
        pnTienThua.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pnTienThua.setOpaque(false);
        JLabel lblTienThuaLeft = new JLabel("Tiền thừa:");
        lblTienThuaLeft.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblTienThuaLeft.setPreferredSize(new Dimension(160, 40));
        lblTienThuaValue = new JLabel("0 đ");
        lblTienThuaValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTienThuaValue.setHorizontalAlignment(SwingConstants.RIGHT);
        pnTienThua.add(lblTienThuaLeft, BorderLayout.WEST);
        pnTienThua.add(lblTienThuaValue, BorderLayout.CENTER);
        pnRight.add(pnTienThua);

        pnRight.add(Box.createVerticalGlue());
        btnBanHang = new PillButton("BÁN HÀNG");
        btnBanHang.setFont(new Font("Segoe UI", Font.BOLD, 28));
        btnBanHang.setPreferredSize(new Dimension(300, 70));
        btnBanHang.setMaximumSize(new Dimension(300, 70));
        btnBanHang.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBanHang.addActionListener(this);
        pnRight.add(btnBanHang);
        pnRight.add(Box.createVerticalStrut(20));

        return pnRight;
    }

    // ====================== TẠO ĐƠN HÀNG ======================
    private JPanel createDonPanel(SanPham sp, String maLo, LocalDate hsd, int tonGoc, double khuyenMai, boolean isTang) {
        JPanel pnDon = new JPanel();
        pnDon.setLayout(new GridBagLayout());
        pnDon.setPreferredSize(new Dimension(1040, 130));
        pnDon.setMaximumSize(new Dimension(Short.MAX_VALUE, 130));
        pnDon.setMinimumSize(new Dimension(800, 130));
        pnDon.setBackground(isTang ? new Color(0xFFF3E0) : Color.WHITE); // Nổi bật nếu là tặng
        pnDon.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(isTang ? Color.ORANGE : new Color(0xE0E0E0), 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));

        pnDon.putClientProperty("maLo", maLo);
        pnDon.putClientProperty("tonGoc", tonGoc);
        pnDon.putClientProperty("khuyenMai", khuyenMai);
        pnDon.putClientProperty("sanPham", sp);
        pnDon.putClientProperty("isTang", isTang);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 8, 0, 8);

        // === ẢNH ===
        JLabel lblHinh = new JLabel("", SwingConstants.CENTER);
        lblHinh.setPreferredSize(new Dimension(100, 100));
        lblHinh.setBorder(new LineBorder(Color.LIGHT_GRAY));
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(sp.getHinhAnh()));
            lblHinh.setIcon(new ImageIcon(icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
        } catch (Exception e) { lblHinh.setText("Ảnh"); }
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.WEST;
        pnDon.add(lblHinh, gbc);

        // === TÊN + INFO ===
        JPanel pnTenVaInfo = new JPanel();
        pnTenVaInfo.setLayout(new BoxLayout(pnTenVaInfo, BoxLayout.Y_AXIS));
        pnTenVaInfo.setOpaque(false);

        JLabel lblTen = new JLabel(sp.getTenSanPham() + (isTang ? " (Tặng)" : ""));
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTen.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pnInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnInfo.setOpaque(false);
        JLabel lblLo = new JLabel("Lô: " + maLo);
        JLabel lblHsd = new JLabel("HSD: " + (hsd != null ? hsd.toString() : "--"));
        JLabel lblTon = new JLabel("Tồn: " + tonGoc); // HIỂN THỊ CHÍNH XÁC BAN ĐẦU
        lblTon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTon.setForeground(new Color(150, 0, 0));
        lblTon.setName("lblTon");
        pnInfo.add(lblLo); pnInfo.add(lblHsd); pnInfo.add(lblTon);

        pnTenVaInfo.add(lblTen);
        pnTenVaInfo.add(Box.createVerticalStrut(8));
        pnTenVaInfo.add(pnInfo);

        gbc.gridx = 1; gbc.gridy = 0; gbc.gridheight = 2; gbc.weightx = 1.0;
        pnDon.add(pnTenVaInfo, gbc);

        // === ĐƠN VỊ ===
        JPanel pnDonVi = new JPanel(new BorderLayout());
        pnDonVi.setPreferredSize(new Dimension(100, 40));
        pnDonVi.setBackground(new Color(0xF0F8FF));
        pnDonVi.setBorder(new LineBorder(new Color(0x4682B4), 2, true));
        JComboBox<String> cbDonVi = new JComboBox<>(new String[]{"Hộp", "Lẻ", "Viên"});
        cbDonVi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbDonVi.setName("cbDonVi");
        pnDonVi.add(cbDonVi, BorderLayout.CENTER);
        gbc.gridx = 2; gbc.gridy = 0; gbc.gridheight = 2; gbc.weightx = 0;
        pnDon.add(pnDonVi, gbc);

        // === SỐ LƯỢNG ===
        JPanel pnSoLuong = new JPanel(new BorderLayout(5, 0));
        pnSoLuong.setPreferredSize(new Dimension(120, 40));
        pnSoLuong.setBackground(new Color(0xE8F5E9));
        pnSoLuong.setBorder(new LineBorder(new Color(0x66BB6A), 2, true));
        JButton btnGiam = new JButton("−");
        JButton btnTang = new JButton("+");
        // dùng action command để so sánh an toàn
        btnGiam.setActionCommand("DECREASE");
        btnTang.setActionCommand("INCREASE");

        JTextField txtSL = new JTextField(isTang ? "0" : "1");
        txtSL.setEnabled(!isTang); // Không cho sửa SL nếu là tặng
        btnGiam.setEnabled(!isTang);
        btnTang.setEnabled(!isTang);
        styleMiniButton(btnGiam); styleMiniButton(btnTang);
        txtSL.setHorizontalAlignment(SwingConstants.CENTER);
        txtSL.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtSL.setBorder(null);
        txtSL.setName("txtSL");
        pnSoLuong.add(btnGiam, BorderLayout.WEST);
        pnSoLuong.add(txtSL, BorderLayout.CENTER);
        pnSoLuong.add(btnTang, BorderLayout.EAST);
        gbc.gridx = 3; gbc.gridy = 0; gbc.gridheight = 2; gbc.weightx = 0;
        pnDon.add(pnSoLuong, gbc);

        // === GIÁ + KHUYẾN MÃI ===
        JPanel pnGia = new JPanel(new GridLayout(2, 1, 0, 5));
        pnGia.setOpaque(false);
        pnGia.setPreferredSize(new Dimension(120, 50));
        JLabel lblGia = new JLabel();
        lblGia.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblGia.setName("lblGia");
        JLabel lblKhuyenMai = new JLabel(String.format("−%,.0f đ", khuyenMai));
        lblKhuyenMai.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblKhuyenMai.setForeground(Color.RED);
        lblKhuyenMai.setName("lblKhuyenMai");
        pnGia.add(lblGia); pnGia.add(lblKhuyenMai);
        gbc.gridx = 4; gbc.gridy = 0; gbc.gridheight = 2; gbc.weightx = 0;
        pnDon.add(pnGia, gbc);

        // === TỔNG TIỀN ===
        JLabel lblTong = new JLabel();
        lblTong.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTong.setForeground(new Color(0x00796B));
        lblTong.setName("lblTong");
        lblTong.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 5; gbc.gridy = 0; gbc.gridheight = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        pnDon.add(lblTong, gbc);

        // === NÚT XÓA ===
        JButton btnXoa = new JButton();
        btnXoa.setPreferredSize(new Dimension(35, 35));
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/bin.png"));
            btnXoa.setIcon(new ImageIcon(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        } catch (Exception ignored) {}
        btnXoa.setBorderPainted(false);
        btnXoa.setContentAreaFilled(false);
        btnXoa.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridx = 6; gbc.gridy = 0; gbc.gridheight = 2; gbc.weightx = 0; gbc.insets = new Insets(0, 20, 0, 0);
        pnDon.add(btnXoa, gbc);

        // === SỰ KIỆN ===
        ActionListener update = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                capNhatGiaVaTong(pnDon);
            }
        };
        cbDonVi.addActionListener(update);
        if (!isTang) {
            txtSL.addActionListener(update);
            btnGiam.addActionListener(this);
            btnTang.addActionListener(this);
        }
        btnXoa.addActionListener(e -> xoaDonHang(pnDon));

        // Document listener để tự động cập nhật tặng (nếu có chương trình tặng)
        capNhatGiaVaTong(pnDon);
        return pnDon;
    }

    // ====================== TÍNH KHUYẾN MÃI ======================
    private double tinhKhuyenMai(SanPham sp, int soLuong, ChiTietKhuyenMaiSanPham ct) {
        if (ct == null || soLuong < ct.getSoLuongToiThieu()) return 0;
        int soLan = soLuong / ct.getSoLuongToiThieu();
        if (ct.getKhuyenMai().getHinhThuc() == HinhThucKM.GIAM_GIA_TIEN) {
            return soLan * ct.getKhuyenMai().getGiaTri();
        }
        return 0;
    }

    private int tinhSoLuongTang(ChiTietKhuyenMaiSanPham ct, int soLuongMua) {
        if (ct == null || ct.getKhuyenMai().getHinhThuc() != HinhThucKM.TANG_THEM && ct.getKhuyenMai().getHinhThuc() != HinhThucKM.TANG_THEM) return 0;
        if (soLuongMua < ct.getSoLuongToiThieu()) return 0;
        return (soLuongMua / ct.getSoLuongToiThieu()) * ct.getSoLuongTangThem();
    }

    // ====================== TÌM SẢN PHẨM ======================
    private void timSanPhamTheoSoDangKy() {
        String tuKhoa = txtTimThuoc.getText().trim();
        if (tuKhoa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số đăng ký hoặc mã sản phẩm!");
            return;
        }

        SanPham sp = sanPhamDAO.timSanPhamTheoSoDangKy(tuKhoa);
        if (sp == null) sp = sanPhamDAO.laySanPhamTheoMa(tuKhoa);
        if (sp == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm với SĐK/Mã: " + tuKhoa);
            return;
        }

        LoSanPham lo = loDAO.timLoGanHetHanTheoSanPham(sp.getMaSanPham());
        if (lo == null || loDAO.tinhSoLuongTonThucTe(lo.getMaLo()) <= 0) {
            JOptionPane.showMessageDialog(this, "Sản phẩm này hiện đã hết hàng!");
            return;
        }

        int tonGoc = loDAO.tinhSoLuongTonThucTe(lo.getMaLo());
        ArrayList<QuyCachDongGoi> dsQC = quyCachDAO.layTatCaQuyCachDongGoi();
        QuyCachDongGoi qcMacDinh = null;
        for (QuyCachDongGoi qc : dsQC) {
            if (qc.getSanPham().getMaSanPham().equals(sp.getMaSanPham()) && qc.isDonViGoc()) {
                qcMacDinh = qc;
                break;
            }
        }
        if (qcMacDinh == null && !dsQC.isEmpty()) qcMacDinh = dsQC.get(0);

        // === TÌM KHUYẾN MÃI ĐANG HOẠT ĐỘNG ===
        List<KhuyenMai> dsKM = khuyenMaiDAO.layKhuyenMaiDangHoatDong();
        ChiTietKhuyenMaiSanPham ctKM = null;
        for (KhuyenMai km : dsKM) {
            List<ChiTietKhuyenMaiSanPham> dsCT = chiTietKMDAO.timKiemChiTietKhuyenMaiSanPhamBangMa(km.getMaKM());
            for (ChiTietKhuyenMaiSanPham ct : dsCT) {
                if (ct.getSanPham().getMaSanPham().equals(sp.getMaSanPham())) {
                    ctKM = ct;
                    break;
                }
            }
            if (ctKM != null) break;
        }

        JPanel pnDon = createDonPanel(sp, lo.getMaLo(), lo.getHanSuDung(), tonGoc, 0, false);
        JComboBox<String> cbDonVi = (JComboBox<String>) findByName(pnDon, "cbDonVi");
        if (qcMacDinh != null && cbDonVi != null) {
            cbDonVi.removeAllItems();
            for (QuyCachDongGoi qc : dsQC) {
                if (qc.getSanPham().getMaSanPham().equals(sp.getMaSanPham())) {
                    cbDonVi.addItem(qc.getDonViTinh().getTenDonViTinh());
                }
            }
            cbDonVi.setSelectedItem(qcMacDinh.getDonViTinh().getTenDonViTinh());
        }

        // thêm panel đơn vào danh sách
        pnDanhSachDon.add(pnDon);
        pnDanhSachDon.revalidate();
        pnDanhSachDon.repaint();

        // === TỰ ĐỘNG TẶNG SẢN PHẨM ===
        final ChiTietKhuyenMaiSanPham finalCtKM = ctKM;
        JTextField txtSL = (JTextField) findByName(pnDon, "txtSL");
        if (txtSL != null) {
            txtSL.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { capNhatKhuyenMaiTang(pnDon, finalCtKM); }
                public void removeUpdate(DocumentEvent e) { capNhatKhuyenMaiTang(pnDon, finalCtKM); }
                public void changedUpdate(DocumentEvent e) { capNhatKhuyenMaiTang(pnDon, finalCtKM); }
            });
        }

        capNhatGiaVaTong(pnDon);
        capNhatTongTien();
        txtTimThuoc.setText("");
        txtTimThuoc.requestFocus();
    }

    private void capNhatKhuyenMaiTang(JPanel pnDon, ChiTietKhuyenMaiSanPham ctKM) {
        if (ctKM == null || ctKM.getKhuyenMai().getHinhThuc() != HinhThucKM.TANG_THEM) return;
        JTextField txtSL = (JTextField) findByName(pnDon, "txtSL");
        int slMua = parse(txtSL != null ? txtSL.getText() : "0");
        int slTang = tinhSoLuongTang(ctKM, slMua);

        // Xóa các dòng tặng cũ: thu trước rồi xóa
        Component[] comps = pnDanhSachDon.getComponents();
        List<JPanel> toRemove = new ArrayList<>();
        for (Component c : comps) {
            if (c instanceof JPanel) {
                JPanel p = (JPanel) c;
                Object isTang = p.getClientProperty("isTang");
                Object maLoP = p.getClientProperty("maLo");
                if (Boolean.TRUE.equals(isTang) && Objects.equals(maLoP, pnDon.getClientProperty("maLo"))) {
                    toRemove.add(p);
                }
            }
        }
        for (JPanel p : toRemove) pnDanhSachDon.remove(p);

        if (slTang > 0) {
            LoSanPham lo = loDAO.timLoTheoMa((String) pnDon.getClientProperty("maLo"));
            if (lo != null) {
                int tonCon = loDAO.tinhSoLuongTonThucTe(lo.getMaLo());
                if (tonCon >= slTang) {
                    JPanel pnTang = createDonPanel(
                            (SanPham) pnDon.getClientProperty("sanPham"),
                            lo.getMaLo(), lo.getHanSuDung(), tonCon, 0, true
                    );
                    JTextField txtSLT = (JTextField) findByName(pnTang, "txtSL");
                    if (txtSLT != null) txtSLT.setText(String.valueOf(slTang));
                    // đặt vị trí: thêm ngay sau pnDon nếu muốn
                    int idx = -1;
                    Component[] current = pnDanhSachDon.getComponents();
                    for (int i = 0; i < current.length; i++) {
                        if (current[i] == pnDon) { idx = i; break; }
                    }
                    if (idx >= 0 && idx < pnDanhSachDon.getComponentCount() - 1) {
                        pnDanhSachDon.add(pnTang, idx + 1);
                    } else pnDanhSachDon.add(pnTang);
                }
            }
        }

        pnDanhSachDon.revalidate();
        pnDanhSachDon.repaint();
        capNhatTongTien();
    }

    // ====================== CẬP NHẬT GIÁ & TỔNG ======================
    private void capNhatGiaVaTong(JPanel pnDon) {
        JComboBox<String> cbDonVi = (JComboBox<String>) findByName(pnDon, "cbDonVi");
        JTextField txtSL = (JTextField) findByName(pnDon, "txtSL");
        JLabel lblGia = (JLabel) findByName(pnDon, "lblGia");
        JLabel lblTong = (JLabel) findByName(pnDon, "lblTong");
        JLabel lblTon = (JLabel) findByName(pnDon, "lblTon");
        if (cbDonVi == null || txtSL == null || lblGia == null || lblTong == null || lblTon == null) return;

        SanPham sp = (SanPham) pnDon.getClientProperty("sanPham");
        int sl = parse(txtSL.getText());
        int tonGoc = (int) pnDon.getClientProperty("tonGoc");
        boolean isTang = Boolean.TRUE.equals(pnDon.getClientProperty("isTang"));

        String donVi = (String) cbDonVi.getSelectedItem();
        double giaBan = sp.getGiaBan();
        double unitFactor = 1.0;
        if ("Lẻ".equals(donVi)) unitFactor = 0.1;
        else if ("Viên".equals(donVi)) unitFactor = 0.01;
        double giaBanDieuChinh = giaBan * unitFactor;

        double giamTien = 0;
        if (!isTang) {
            List<KhuyenMai> dsKM = khuyenMaiDAO.layKhuyenMaiDangHoatDong();
            for (KhuyenMai km : dsKM) {
                List<ChiTietKhuyenMaiSanPham> listCT = chiTietKMDAO.timKiemChiTietKhuyenMaiSanPhamBangMa(km.getMaKM());
                ChiTietKhuyenMaiSanPham ct = null;
                for (ChiTietKhuyenMaiSanPham c : listCT) {
                    if (c.getSanPham().getMaSanPham().equals(sp.getMaSanPham())) {
                        ct = c; break;
                    }
                }
                if (ct != null && ct.getKhuyenMai().getHinhThuc() == HinhThucKM.GIAM_GIA_TIEN) {
                    // giảm tiền cũng phải theo đơn vị
                    giamTien = tinhKhuyenMai(sp, sl, ct) * unitFactor;
                    break;
                }
            }
        }

        double tong = isTang ? 0 : (giaBanDieuChinh * sl - giamTien);
        lblGia.setText(String.format("%,.0f đ", giaBanDieuChinh));
        lblTong.setText(String.format("%,.0f đ", tong));
        lblTon.setText("Tồn: " + Math.max(0, tonGoc - sl));
        pnDon.putClientProperty("khuyenMai", giamTien);
        pnDon.putClientProperty("unitFactor", unitFactor);
    }

    private void capNhatTongTien() {
        double tongHang = 0, giamSP = 0;
        Component[] components = pnDanhSachDon.getComponents();
        for (Component c : components) {
            if (c instanceof JPanel) {
                JPanel pn = (JPanel) c;
                if (Boolean.TRUE.equals(pn.getClientProperty("isTang"))) continue;
                JTextField txtSL = (JTextField) findByName(pn, "txtSL");
                if (txtSL == null) continue;
                int sl = parse(txtSL.getText());
                SanPham sp = (SanPham) pn.getClientProperty("sanPham");
                double unitFactor = 1.0;
                Object ufObj = pn.getClientProperty("unitFactor");
                if (ufObj instanceof Double) unitFactor = (Double) ufObj;
                else {
                    JComboBox<String> cb = (JComboBox<String>) findByName(pn, "cbDonVi");
                    if (cb != null) {
                        String dv = (String) cb.getSelectedItem();
                        if ("Lẻ".equals(dv)) unitFactor = 0.1;
                        else if ("Viên".equals(dv)) unitFactor = 0.01;
                    }
                }
                double giaDonVi = sp.getGiaBan() * unitFactor;
                tongHang += sl * giaDonVi;
                Object gkm = pn.getClientProperty("khuyenMai");
                if (gkm instanceof Double) giamSP += (Double) gkm;
            }
        }
        lblTongHangValue.setText(String.format("%,.0f đ", tongHang));
        lblGiamSPValue.setText(String.format("−%,.0f đ", giamSP));
        lblTongHDValue.setText(String.format("TỔNG CỘNG: %,.0f Đ", tongHang - giamSP));
    }

    // ====================== XỬ LÝ NÚT ======================
    private void xoaDonHang(JPanel pnDon) {
        pnDanhSachDon.remove(pnDon);
        pnDanhSachDon.revalidate();
        pnDanhSachDon.repaint();
        capNhatTongTien();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == txtTimThuoc) timSanPhamTheoSoDangKy();
        else if (src == txtTimKH) timKhachHang();
        else if (src == txtTienKhach) capNhatTienThua();
        else if (src == btnBanHang) xuLyBanHang();
        else if (src == btnThemDon) JOptionPane.showMessageDialog(this, "Tính năng thêm đơn mới đang phát triển!");
        else {
            // tìm panel cha (đơn)
            Component comp = (Component) src;
            JPanel pnDon = null;
            Container parent = comp.getParent();
            while (parent != null) {
                if (parent instanceof JPanel && parent.getParent() == pnDanhSachDon) {
                    pnDon = (JPanel) parent;
                    break;
                }
                parent = parent.getParent();
            }

            if (pnDon != null) {
                // xử lý theo actionCommand nếu là nút +/-
                if (src instanceof JButton) {
                    JButton btn = (JButton) src;
                    String cmd = btn.getActionCommand();
                    if ("INCREASE".equals(cmd)) {
                        JTextField txtSL = (JTextField) findByName(pnDon, "txtSL");
                        int sl = parse(txtSL.getText()) + 1;
                        int ton = (int) pnDon.getClientProperty("tonGoc");
                        if (sl <= ton) txtSL.setText(String.valueOf(sl));
                    } else if ("DECREASE".equals(cmd)) {
                        JTextField txtSL = (JTextField) findByName(pnDon, "txtSL");
                        int sl = parse(txtSL.getText());
                        if (sl > 1) txtSL.setText(String.valueOf(sl - 1));
                    }
                }
                capNhatGiaVaTong(pnDon);
                capNhatTongTien();
            }
        }
    }

    private void timKhachHang() { JOptionPane.showMessageDialog(this, "Tìm khách hàng - đang phát triển!"); }
    private void capNhatTienThua() {
        // simple implementation: parse tiền khách và hiển thị tiền thừa
        double tong = 0;
        try {
            String txt = lblTongHDValue.getText().replaceAll("[^0-9]", "");
            if (!txt.isEmpty()) tong = Double.parseDouble(txt);
        } catch (Exception ex) { tong = 0; }
        double tienKhach = 0;
        try {
            String t = txtTienKhach.getText().replaceAll("[^0-9]", "");
            if (!t.isEmpty()) tienKhach = Double.parseDouble(t);
        } catch (Exception ex) { tienKhach = 0; }
        double thua = Math.max(0, tienKhach - tong);
        lblTienThuaValue.setText(String.format("%,.0f đ", thua));
    }
    private void xuLyBanHang() { JOptionPane.showMessageDialog(this, "Xử lý bán hàng - đang phát triển!"); }

    private Component findByName(Container c, String name) {
        for (Component comp : c.getComponents()) {
            if (name.equals(comp.getName())) return comp;
            if (comp instanceof Container) {
                Component found = findByName((Container) comp, name);
                if (found != null) return found;
            }
        }
        return null;
    }

    private int parse(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception e) { return 0; }
    }

    private void styleMiniButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(0xE0F2F1));
        btn.setBorder(new LineBorder(new Color(0x80CBC4), 1, true));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(40, 40));
    }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mousePressed(MouseEvent e) { maybeShowPopup(e); }
    @Override public void mouseReleased(MouseEvent e) { maybeShowPopup(e); }
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            JPopupMenu popup = new JPopupMenu();
            JMenuItem mi = new JMenuItem("Làm mới");
            mi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    capNhatTongTien();
                }
            });
            popup.add(mi);
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Bán Hàng - Có Khuyến Mãi");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(1600, 900);
            f.setLocationRelativeTo(null);
            f.setContentPane(new BanHang_GUI());
            f.setVisible(true);
        });
    }
}
