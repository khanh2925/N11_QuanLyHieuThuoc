package gui;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.toedter.calendar.JDateChooser;

import entity.ChiTietPhieuNhap;
import entity.DonViTinh;
import entity.LoSanPham;
import entity.QuyCachDongGoi;
import entity.SanPham;

public class ChonLo_Dialog extends JDialog {

    // ===== I. CÁC TRƯỜNG DỮ LIỆU (FIELDS) =====
    private final DateTimeFormatter fmtDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DecimalFormat df = new DecimalFormat("#,### đ");

    // Dữ liệu truyền vào
    private final SanPham sanPham;
    private String maLoDeNghi;
    private List<QuyCachDongGoi> dsQuyCach;
    private QuyCachDongGoi quyCachGoc;
    private List<ChiTietPhieuNhap> dsLoHienTai = null;

    // Kết quả trả về
    private boolean confirmed = false;
    private LoSanPham loDaChon = null;
    private double donGiaNhapGoc = 0;
    private int soLuongNhapGoc = 0;
    private DonViTinh donViTinhGoc = null;
    private ChiTietPhieuNhap chiTietCanSua = null;

    // Components Giao diện (UI)
    private JTabbedPane tabbedPane;
    private JTextField txtMaLoMoi;
    private JDateChooser dateHSDMoi;
    private JComboBox<QuyCachDongGoi> cmbQuyCachMoi;
    private JSpinner spinnerSoLuongMoi;
    private JTextField txtDonGiaMoi;

    private JComboBox<QuyCachDongGoi> cmbQuyCachCu;
    private JSpinner spinnerSoLuongCu;
    private JTextField txtDonGiaCu;
    private DefaultListModel<ChiTietPhieuNhap> modelLoCu;
    private JList<ChiTietPhieuNhap> listLoCu;

    private JButton btnLuu;
    private JButton btnThoat;
    
    private Font fontLabel;
    private Font fontField;
    
    // ✅ Thêm hằng số để căn chỉnh label
    private final int LABEL_WIDTH = 150;
    private final int COMPONENT_HEIGHT = 35;

    // ===== II. CONSTRUCTORS =====

    /**
     * Constructor 1 (Chỉ Thêm Mới): Dùng khi thêm SP mới vào phiếu
     * @wbp.parser.constructor
     */
    public ChonLo_Dialog(Frame owner, SanPham sp, String maLoDeNghi, List<QuyCachDongGoi> dsQuyCach, QuyCachDongGoi quyCachGoc) {
        super(owner, "Nhập lô cho: " + sp.getTenSanPham(), true);
        
        // 1. Set data
        this.sanPham = sp;
        this.maLoDeNghi = maLoDeNghi;
        this.dsQuyCach = dsQuyCach;
        this.quyCachGoc = quyCachGoc;
        this.dsLoHienTai = null;
        this.donViTinhGoc = quyCachGoc.getDonViTinh();
        this.donGiaNhapGoc = sp.getGiaNhap();

        // 2. Khởi tạo UI
        setSize(450, 450);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(Color.WHITE);
        
        initialize(); // Xây dựng Giao diện
        registerEvents(); // Gắn Sự kiện

        // 3. Cấu hình logic ban đầu
        capNhatGiaTheoQuyCach(cmbQuyCachMoi, txtDonGiaMoi);
        capNhatGiaTheoQuyCach(cmbQuyCachCu, txtDonGiaCu);

        tabbedPane.setEnabledAt(0, false); 
        tabbedPane.setSelectedIndex(1); 
    }

    /**
     * Constructor 2 (Thêm Mới hoặc Sửa): Dùng khi nhấn nút "Chọn Lô"
     */
    public ChonLo_Dialog(Frame owner, SanPham sp, String maLoDeNghi, List<QuyCachDongGoi> dsQuyCach, QuyCachDongGoi quyCachGoc, List<ChiTietPhieuNhap> dsLoHienTai) {
        // 1. Gọi constructor 1 để khởi tạo mọi thứ
        this(owner, sp, maLoDeNghi, dsQuyCach, quyCachGoc);

        // 2. Cập nhật lại các giá trị cho chế độ "Sửa"
        setTitle("Sửa lô hoặc Thêm lô mới cho: " + sp.getTenSanPham());
        setSize(550, 520); // Cần to hơn để chứa JList

        this.dsLoHienTai = dsLoHienTai;

        // 3. Cấu hình logic
        tabbedPane.setTitleAt(0, "Lô cũ");
        tabbedPane.setEnabledAt(0, true); 
        tabbedPane.setSelectedIndex(0); 

        loadDataLoHienTai(); 
    }

    // ===== III. KHỞI TẠO GIAO DIỆN (UI) =====

    /**
     * Khởi tạo các thành phần giao diện chính
     */
    private void initialize() {
        getContentPane().setLayout(new BorderLayout());

        fontLabel = new Font("Segoe UI", Font.PLAIN, 16);
        fontField = new Font("Segoe UI", Font.PLAIN, 14);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setOpaque(true);

        tabbedPane.addTab("  Lô Cũ  ", createPanelSuaLo());
        tabbedPane.addTab("  Lô Mới  ", createPanelLoMoi());

        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // Panel chứa nút bấm
        JPanel pnButton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnButton.setBackground(Color.WHITE);
        pnButton.setBorder(new EmptyBorder(10, 10, 10, 10));

        btnLuu = new JButton("Lưu");
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLuu.setBackground(new Color(0x3B82F6));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setPreferredSize(new Dimension(100, 35));

        btnThoat = new JButton("Thoát");
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(0x6B7280));
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setPreferredSize(new Dimension(100, 35));

        pnButton.add(btnLuu);
        pnButton.add(btnThoat);
        getContentPane().add(pnButton, BorderLayout.SOUTH);
    }
    
    /**
     * ✅ [BoxLayout] Tạo Panel cho Tab "Lô Mới"
     */
    private JPanel createPanelLoMoi() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // ✅ Sử dụng BoxLayout
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20)); // ✅ Padding

        // Khởi tạo components
        txtMaLoMoi = new JTextField(maLoDeNghi);
        txtMaLoMoi.setFont(fontField);
        txtMaLoMoi.setEditable(false);
        txtMaLoMoi.setBackground(new Color(0xF3F4F6));
        
        dateHSDMoi = new JDateChooser();
        dateHSDMoi.setDateFormatString("dd/MM/yyyy");
        dateHSDMoi.setFont(fontField);
        dateHSDMoi.setDate(Date.from(LocalDate.now().plusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        
        cmbQuyCachMoi = new JComboBox<>();
        cmbQuyCachMoi.setFont(fontField);
        cmbQuyCachMoi.setBackground(Color.WHITE);
        loadQuyCachComboBox(cmbQuyCachMoi);
        
        spinnerSoLuongMoi = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        spinnerSoLuongMoi.setFont(fontField);

        txtDonGiaMoi = new JTextField();
        txtDonGiaMoi.setFont(fontField);
        txtDonGiaMoi.setEditable(false);
        txtDonGiaMoi.setBackground(new Color(0xF3F4F6));
        txtDonGiaMoi.setHorizontalAlignment(JTextField.RIGHT);

        // Thêm các hàng vào panel
        panel.add(createRowPanel(new JLabel("Mã Lô (tự sinh):"), txtMaLoMoi));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createRowPanel(new JLabel("Hạn sử dụng:"), dateHSDMoi));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createRowPanel(new JLabel("Đơn vị tính:"), cmbQuyCachMoi));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createRowPanel(new JLabel("Số lượng nhập:"), spinnerSoLuongMoi));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createRowPanel(new JLabel("Đơn giá (theo ĐVT):"), txtDonGiaMoi));
        
        panel.add(Box.createVerticalGlue()); // Đẩy các trường lên trên

        return panel;
    }

    /**
     * ✅ [BoxLayout] Tạo Panel cho Tab "Sửa Lô Hiện Tại"
     */
    private JPanel createPanelSuaLo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // ✅ Sử dụng BoxLayout
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Hàng 0: Tiêu đề
        JLabel lblTitle = new JLabel("Chọn một lô bên dưới để sửa số lượng:");
        lblTitle.setFont(fontLabel);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(10));

        // Hàng 1: Danh sách
        modelLoCu = new DefaultListModel<>();
        listLoCu = new JList<>(modelLoCu);
        listLoCu.setFont(fontField);
        listLoCu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listLoCu.setCellRenderer(new ChiTietPhieuNhapRenderer());
        JScrollPane scrollList = new JScrollPane(listLoCu);
        scrollList.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollList.setPreferredSize(new Dimension(100, 150));
        scrollList.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        panel.add(scrollList);
        panel.add(Box.createVerticalStrut(10));

        // Hàng 2: Đường kẻ
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(separator);
        panel.add(Box.createVerticalStrut(15));
        
        // Khởi tạo components
        cmbQuyCachCu = new JComboBox<>();
        cmbQuyCachCu.setFont(fontField);
        cmbQuyCachCu.setBackground(Color.WHITE);
        loadQuyCachComboBox(cmbQuyCachCu);

        spinnerSoLuongCu = new JSpinner(new SpinnerNumberModel(1, 0, 10000, 1));
        spinnerSoLuongCu.setFont(fontField);
        
        txtDonGiaCu = new JTextField();
        txtDonGiaCu.setFont(fontField);
        txtDonGiaCu.setEditable(false);
        txtDonGiaCu.setBackground(new Color(0xF3F4F6));
        txtDonGiaCu.setHorizontalAlignment(JTextField.RIGHT);

        // Hàng 3, 4, 5: Thêm các hàng
        panel.add(createRowPanel(new JLabel("Đơn vị tính (Mới):"), cmbQuyCachCu));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createRowPanel(new JLabel("Số lượng nhập (Mới):"), spinnerSoLuongCu));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createRowPanel(new JLabel("Đơn giá (theo ĐVT):"), txtDonGiaCu));

        return panel;
    }

    /**
     * ✅ [Helper] Tạo một hàng (row) chuẩn cho form BoxLayout
     */
    private JPanel createRowPanel(JLabel label, Component component) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(Color.WHITE);
        
        label.setFont(fontLabel);
        label.setPreferredSize(new Dimension(LABEL_WIDTH, COMPONENT_HEIGHT));
        panel.add(label, BorderLayout.WEST);
        
        if (component instanceof JSpinner || component instanceof JDateChooser) {
            component.setPreferredSize(new Dimension(100, COMPONENT_HEIGHT));
        } else {
             component.setPreferredSize(new Dimension(100, COMPONENT_HEIGHT));
        }
        
        panel.add(component, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, COMPONENT_HEIGHT + 5));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        return panel;
    }
    
    /**
     * Nạp dữ liệu Quy Cách vào JComboBox
     */
    private void loadQuyCachComboBox(JComboBox<QuyCachDongGoi> cmb) {
        cmb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof QuyCachDongGoi qc) {
                    setText(qc.getDonViTinh().getTenDonViTinh());
                }
                return this;
            }
        });
        for (QuyCachDongGoi qc : dsQuyCach) {
            cmb.addItem(qc);
        }
        cmb.setSelectedIndex(0);
    }

    // ===== IV. XỬ LÝ SỰ KIỆN & NGHIỆP VỤ (LOGIC) =====

    /**
     * Gán sự kiện cho các component
     */
    private void registerEvents() {
        btnLuu.addActionListener(e -> xuLyXacNhan());
        
        btnThoat.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        // Sự kiện cập nhật giá khi đổi ComboBox
        cmbQuyCachMoi.addActionListener(e -> capNhatGiaTheoQuyCach(cmbQuyCachMoi, txtDonGiaMoi));
        cmbQuyCachCu.addActionListener(e -> capNhatGiaTheoQuyCach(cmbQuyCachCu, txtDonGiaCu));
    }

    /**
     * Xử lý khi nhấn nút Lưu (Xác nhận)
     */
    private void xuLyXacNhan() {
        int selectedIndex = tabbedPane.getSelectedIndex();

        try {
            if (selectedIndex == 1) { // Tab "Lô Mới"
                Date selectedDate = dateHSDMoi.getDate();
                if (selectedDate == null) {
                    throw new Exception("Vui lòng chọn Hạn Sử Dụng.");
                }
                LocalDate hsd = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (hsd.isBefore(LocalDate.now().plusDays(30))) {
                    throw new Exception("Hạn sử dụng phải lớn hơn 30 ngày kể từ hôm nay.");
                }

                QuyCachDongGoi qcDaChon = (QuyCachDongGoi) cmbQuyCachMoi.getSelectedItem();
                if (qcDaChon == null) {
                    throw new Exception("Vui lòng chọn một Quy Cách Đóng Gói.");
                }

                int soLuongQuyCach = (Integer) spinnerSoLuongMoi.getValue();
                this.soLuongNhapGoc = soLuongQuyCach * qcDaChon.getHeSoQuyDoi();
                String maLo = txtMaLoMoi.getText();

                this.loDaChon = new LoSanPham(maLo, hsd, 0, this.sanPham);
                this.chiTietCanSua = null;

            } else { // Tab "Sửa Lô Hiện Tại"
                ChiTietPhieuNhap ctDuocChon = listLoCu.getSelectedValue();
                if (ctDuocChon == null) {
                    throw new Exception("Vui lòng chọn một lô hiện tại từ danh sách để sửa.");
                }

                QuyCachDongGoi qcDaChon = (QuyCachDongGoi) cmbQuyCachCu.getSelectedItem();
                if (qcDaChon == null) {
                    throw new Exception("Vui lòng chọn một Quy Cách Đóng Gói mới.");
                }

                int soLuongQuyCach = (Integer) spinnerSoLuongCu.getValue();
                this.soLuongNhapGoc = soLuongQuyCach * qcDaChon.getHeSoQuyDoi();
                this.chiTietCanSua = ctDuocChon;
                this.loDaChon = ctDuocChon.getLoSanPham();
            }

            // Set các giá trị chung
            this.donGiaNhapGoc = this.sanPham.getGiaNhap();
            this.donViTinhGoc = this.quyCachGoc.getDonViTinh();
            this.confirmed = true;
            this.dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Cập nhật giá khi chọn JComboBox
     */
    private void capNhatGiaTheoQuyCach(JComboBox<QuyCachDongGoi> cmb, JTextField txtDonGia) {
        QuyCachDongGoi qcDaChon = (QuyCachDongGoi) cmb.getSelectedItem();
        if (qcDaChon == null) return;
        double giaGoc = sanPham.getGiaNhap();
        int heSo = qcDaChon.getHeSoQuyDoi();
        double tiLeGiam = qcDaChon.getTiLeGiam();
        double giaHienThi = (giaGoc * heSo) * (1 - tiLeGiam);
        txtDonGia.setText(df.format(giaHienThi));
    }

    // ===== V. TẢI DỮ LIỆU (DATA) =====

    /**
     * Nạp dữ liệu Lô hiện tại từ danh sách được truyền vào
     */
    private void loadDataLoHienTai() {
        modelLoCu.clear();
        if (this.dsLoHienTai != null) {
            for (ChiTietPhieuNhap ct : dsLoHienTai) {
                modelLoCu.addElement(ct);
            }
        }
    }

    // ===== VI. TRUY XUẤT KẾT QUẢ (GETTERS) =====

    public boolean isConfirmed() {
        return confirmed;
    }

    public LoSanPham getLoSanPham() {
        return loDaChon;
    }

    public double getDonGiaNhap() {
        return donGiaNhapGoc;
    }

    public int getSoLuongNhap() {
        return soLuongNhapGoc;
    }

    public DonViTinh getDonViTinh() {
        return donViTinhGoc;
    }

    public ChiTietPhieuNhap getChiTietCanSua() {
        return chiTietCanSua;
    }

    // ===== VII. LỚP NỘI BỘ (INNER CLASS) =====

    /**
     * Lớp nội bộ để render JList (Đổi sang ChiTietPhieuNhap)
     */
    class ChiTietPhieuNhapRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ChiTietPhieuNhap ct) {
                LoSanPham lo = ct.getLoSanPham();
                String text = String.format("%s - HSD: %s - (Hiện có: %d %s)",
                        lo.getMaLo(),
                        lo.getHanSuDung().format(fmtDate),
                        ct.getSoLuongNhap(),
                        quyCachGoc.getDonViTinh().getTenDonViTinh()
                );
                setText(text);
                setBorder(new EmptyBorder(5, 5, 5, 5));

                if (lo.isHetHan()) {
                    setForeground(Color.RED);
                } else {
                    setForeground(Color.BLACK);
                }
            }
            return this;
        }
    }
}