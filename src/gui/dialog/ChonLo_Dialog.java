package gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.toedter.calendar.JDateChooser;

import entity.ChiTietPhieuNhap;
import entity.DonViTinh;
import entity.LoSanPham;
import entity.QuyCachDongGoi;
import entity.SanPham;

public class ChonLo_Dialog extends JDialog implements ActionListener, MouseListener,ChangeListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// ===== I. CÁC TRƯỜNG DỮ LIỆU (FIELDS) =====
    private final DateTimeFormatter fmtDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DecimalFormat df = new DecimalFormat("#,### đ");

    // Dữ liệu truyền vào
    private final SanPham sanPham;
    private String maLoDeNghi;
    private List<QuyCachDongGoi> dsQuyCach;
    private QuyCachDongGoi quyCachGoc;
    private List<ChiTietPhieuNhap> dsLoHienTai = null;
    private java.util.Map<entity.ChiTietPhieuNhap, Object> mapThongTinHienThi = null; // Map để lưu thông tin hiển thị

    // Kết quả trả về
    private boolean confirmed = false;
    private LoSanPham loDaChon = null;
    private double donGiaNhapGoc = 0;
    private int soLuongNhapGoc = 0;
    private DonViTinh donViTinhGoc = null;
    private ChiTietPhieuNhap chiTietCanSua = null;
    
    // Thông tin hiển thị
    private QuyCachDongGoi quyCachDaChon = null;
    private int soLuongHienThi = 0;
    
    // Flag để phân biệt đang load data hay user đang sửa
    private boolean isLoadingData = false;

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
    
    private final int LABEL_WIDTH = 150;
    private final int COMPONENT_HEIGHT = 35;

    // ===== II. CONSTRUCTORS =====

    public ChonLo_Dialog(Frame owner, SanPham sp, String maLoDeNghi, List<QuyCachDongGoi> dsQuyCach, QuyCachDongGoi quyCachGoc) {
        super(owner, "Nhập lô cho: " + sp.getTenSanPham(), true);
        
        this.sanPham = sp;
        this.maLoDeNghi = maLoDeNghi;
        this.dsQuyCach = dsQuyCach;
        this.quyCachGoc = quyCachGoc;
        this.dsLoHienTai = null;
        this.donViTinhGoc = quyCachGoc.getDonViTinh();
        this.donGiaNhapGoc = sp.getGiaNhap();

        setSize(450, 450);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(Color.WHITE);
        
        initialize(); 
        registerEvents();

        capNhatGiaTheoQuyCach(cmbQuyCachMoi, txtDonGiaMoi);
        capNhatGiaTheoQuyCach(cmbQuyCachCu, txtDonGiaCu);

        tabbedPane.setEnabledAt(0, false); 
        tabbedPane.setSelectedIndex(1); 
    }

    public ChonLo_Dialog(Frame owner, SanPham sp, String maLoDeNghi, List<QuyCachDongGoi> dsQuyCach, QuyCachDongGoi quyCachGoc, List<ChiTietPhieuNhap> dsLoHienTai, java.util.Map<entity.ChiTietPhieuNhap, Object> mapThongTinHienThi) {
        this(owner, sp, maLoDeNghi, dsQuyCach, quyCachGoc);

        setTitle("Sửa lô hoặc Thêm lô mới cho: " + sp.getTenSanPham());
        setSize(550, 520);

        this.dsLoHienTai = dsLoHienTai;
        this.mapThongTinHienThi = mapThongTinHienThi;

        tabbedPane.setTitleAt(0, "Lô cũ");
        tabbedPane.setEnabledAt(0, true); 
        tabbedPane.setSelectedIndex(0); 

        loadDataLoHienTai(); 
    }

    // ===== III. KHỞI TẠO GIAO DIỆN (UI) =====

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
    
    private JPanel createPanelLoMoi() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

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
        
        spinnerSoLuongMoi = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        spinnerSoLuongMoi.setFont(fontField);
        applyNumericFilter(spinnerSoLuongMoi);

        txtDonGiaMoi = new JTextField();
        txtDonGiaMoi.setFont(fontField);
        txtDonGiaMoi.setEditable(false);
        txtDonGiaMoi.setBackground(new Color(0xF3F4F6));
        txtDonGiaMoi.setHorizontalAlignment(JTextField.RIGHT);

        panel.add(createRowPanel(new JLabel("Mã Lô (tự sinh):"), txtMaLoMoi));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createRowPanel(new JLabel("Hạn sử dụng:"), dateHSDMoi));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createRowPanel(new JLabel("Đơn vị tính:"), cmbQuyCachMoi));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createRowPanel(new JLabel("Số lượng nhập:"), spinnerSoLuongMoi));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createRowPanel(new JLabel("Đơn giá (theo ĐVT):"), txtDonGiaMoi));
        
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createPanelSuaLo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Chọn một lô bên dưới để sửa số lượng:");
        lblTitle.setFont(fontLabel);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(10));

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

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(separator);
        panel.add(Box.createVerticalStrut(15));
        
        cmbQuyCachCu = new JComboBox<>();
        cmbQuyCachCu.setFont(fontField);
        cmbQuyCachCu.setBackground(Color.WHITE);
        loadQuyCachComboBox(cmbQuyCachCu);

        spinnerSoLuongCu = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        spinnerSoLuongCu.setFont(fontField);
        applyNumericFilter(spinnerSoLuongCu);
        
        txtDonGiaCu = new JTextField();
        txtDonGiaCu.setFont(fontField);
        txtDonGiaCu.setEditable(false);
        txtDonGiaCu.setBackground(new Color(0xF3F4F6));
        txtDonGiaCu.setHorizontalAlignment(JTextField.RIGHT);

        panel.add(createRowPanel(new JLabel("Đơn vị tính (Mới):"), cmbQuyCachCu));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createRowPanel(new JLabel("Số lượng nhập (Mới):"), spinnerSoLuongCu));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createRowPanel(new JLabel("Đơn giá (theo ĐVT):"), txtDonGiaCu));

        return panel;
    }

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

    private void registerEvents() {
        // Đăng ký ActionListener cho các nút và ComboBox
        btnLuu.addActionListener(this);
        btnThoat.addActionListener(this);
        cmbQuyCachMoi.addActionListener(this);
        cmbQuyCachCu.addActionListener(this);

        // Đăng ký MouseListener cho List
        listLoCu.addMouseListener(this);
        
        // Đăng ký ChangeListener cho Spinner
        spinnerSoLuongMoi.addChangeListener(this);
        spinnerSoLuongCu.addChangeListener(this);
    }
    
    /**
     * Hàm cập nhật form từ List khi người dùng chọn dòng
     */
    private void capNhatFormTuList() {
        ChiTietPhieuNhap itemDuocChon = listLoCu.getSelectedValue();
        
        if (itemDuocChon == null || itemDuocChon.getLoSanPham() == null) {
            return;
        }
        
        try {
            // Bật đầu load data - không trigger validation
            isLoadingData = true;
            
            QuyCachDongGoi quyCachDuocChon = null;
            int soLuongHienThi = itemDuocChon.getSoLuongNhap();
            
            // Kiểm tra xem có thông tin hiển thị trong Map không
            if (mapThongTinHienThi != null && mapThongTinHienThi.containsKey(itemDuocChon)) {
                Object ttht = mapThongTinHienThi.get(itemDuocChon);
                try {
                    java.lang.reflect.Field quyCachField = ttht.getClass().getDeclaredField("quyCach");
                    java.lang.reflect.Field soLuongField = ttht.getClass().getDeclaredField("soLuong");
                    quyCachField.setAccessible(true);
                    soLuongField.setAccessible(true);
                    
                    quyCachDuocChon = (QuyCachDongGoi) quyCachField.get(ttht);
                    soLuongHienThi = (Integer) soLuongField.get(ttht);
                } catch (Exception e) {
                    // Nếu có lỗi, dùng logic cũ
                    quyCachDuocChon = null;
                }
            }
            
            // Nếu không có thông tin trong Map, thử quy đổi ngược từ đơn vị gốc
            if (quyCachDuocChon == null) {
                DonViTinh dvtHienTai = itemDuocChon.getDonViTinh();
                if (dvtHienTai != null) {
                    for (int i = 0; i < cmbQuyCachCu.getItemCount(); i++) {
                        QuyCachDongGoi qc = cmbQuyCachCu.getItemAt(i);
                        if (qc.getDonViTinh().getMaDonViTinh().equals(dvtHienTai.getMaDonViTinh())) {
                            quyCachDuocChon = qc;
                            break;
                        }
                    }
                }
                
                // Quy đổi số lượng từ đơn vị gốc về đơn vị hiển thị
                if (quyCachDuocChon != null && quyCachDuocChon.getHeSoQuyDoi() > 0) {
                    soLuongHienThi = itemDuocChon.getSoLuongNhap() / quyCachDuocChon.getHeSoQuyDoi();
                }
            }
            
            // Cập nhật ComboBox theo quy cách đã tìm được
            if (quyCachDuocChon != null) {
                for (int i = 0; i < cmbQuyCachCu.getItemCount(); i++) {
                    QuyCachDongGoi qc = cmbQuyCachCu.getItemAt(i);
                    if (qc.getDonViTinh().getMaDonViTinh().equals(quyCachDuocChon.getDonViTinh().getMaDonViTinh())) {
                        cmbQuyCachCu.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
            // Cập nhật số lượng
            spinnerSoLuongCu.setValue(soLuongHienThi);
            
            // Cập nhật giá tiền
            capNhatGiaTheoQuyCach(cmbQuyCachCu, txtDonGiaCu);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Có lỗi khi cập nhật thông tin lô: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            // Kết thúc load data - bật lại validation
            isLoadingData = false;
        }
    }

    private void xuLyXacNhan() {
        int selectedIndex = tabbedPane.getSelectedIndex();

        try {
            if (selectedIndex == 1) { // Tab "Lô Mới"
                Date selectedDate = dateHSDMoi.getDate();
                if (selectedDate == null) {
                    throw new Exception("Vui lòng chọn Hạn Sử Dụng.");
                }
                LocalDate hsd = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                
                // Kiểm tra HSD đã hết hạn
                if (hsd.isBefore(LocalDate.now())) {
                    String tenSP = sanPham.getTenSanPham();
                    String maSP = sanPham.getMaSanPham();
                    String ngayHSD = hsd.format(fmtDate);
                    JOptionPane.showMessageDialog(this,
                        String.format("Sản phẩm '%s' (Mã: %s) có HSD %s đã hết hạn!\nKhông thể nhập lô này.",
                            tenSP, maSP, ngayHSD),
                        "Lỗi - HSD Hết Hạn",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Cảnh báo nếu HSD dưới 3 tháng
                if (hsd.isBefore(LocalDate.now().plusMonths(3))) {
                    String tenSP = sanPham.getTenSanPham();
                    String maSP = sanPham.getMaSanPham();
                    String ngayHSD = hsd.format(fmtDate);
                    long soNgayConLai = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), hsd);
                    
                    int choice = JOptionPane.showConfirmDialog(this,
                        String.format("⚠ Sản phẩm '%s' (Mã: %s)\n" +
                                      "Hạn sử dụng: %s (còn %d ngày)\n\n" +
                                      "HSD sắp hết (dưới 3 tháng). Bạn có chắc muốn nhập không?",
                            tenSP, maSP, ngayHSD, soNgayConLai),
                        "Cảnh báo - HSD Gần Hết",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                    
                    if (choice != JOptionPane.YES_OPTION) {
                        return; // Người dùng chọn NO → không lưu
                    }
                }

                QuyCachDongGoi qcDaChon = (QuyCachDongGoi) cmbQuyCachMoi.getSelectedItem();
                if (qcDaChon == null) {
                    throw new Exception("Vui lòng chọn một Quy Cách Đóng Gói.");
                }

                // Validate số lượng nhập - kiểm tra text trong editor trước
                JSpinner.DefaultEditor editorMoi = (JSpinner.DefaultEditor) spinnerSoLuongMoi.getEditor();
                String textMoi = editorMoi.getTextField().getText().trim();
                
                // Kiểm tra xem text có phải là số nguyên dương hợp lệ không
                if (textMoi.isEmpty() || !textMoi.matches("\\d+")) {
                    JOptionPane.showMessageDialog(this, 
                        "Số lượng không hợp lệ.\nVui lòng nhập một số nguyên dương.", 
                        "Số lượng không hợp lệ", 
                        JOptionPane.ERROR_MESSAGE);
                    editorMoi.getTextField().requestFocus();
                    editorMoi.getTextField().selectAll();
                    return;
                }
                
                try {
                    spinnerSoLuongMoi.commitEdit();
                } catch (java.text.ParseException ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Số lượng không hợp lệ.\nVui lòng nhập một số nguyên.", 
                        "Số lượng không hợp lệ", 
                        JOptionPane.ERROR_MESSAGE);
                    spinnerSoLuongMoi.requestFocus();
                    return;
                }

                int soLuongQuyCach = (Integer) spinnerSoLuongMoi.getValue();
                
                // Kiểm tra số lượng > 0
                if (soLuongQuyCach <= 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Số lượng nhập phải lớn hơn 0!", 
                        "Số lượng không hợp lệ", 
                        JOptionPane.WARNING_MESSAGE);
                    spinnerSoLuongMoi.requestFocus();
                    return;
                }
                
                this.soLuongNhapGoc = soLuongQuyCach * qcDaChon.getHeSoQuyDoi();
                
                // Lưu thông tin hiển thị
                this.quyCachDaChon = qcDaChon;
                this.soLuongHienThi = soLuongQuyCach;
                
                // Kiểm tra giới hạn 1,000,000 đơn vị gốc
                if (this.soLuongNhapGoc > 1000000) {
                    int choice = JOptionPane.showConfirmDialog(this,
                        String.format("⚠️ CẢNH BÁO: Số lượng vượt quá giới hạn!\n\n" +
                            "Số lượng nhập: %,d %s\n" +
                            "Quy đổi về đơn vị gốc: %,d %s\n" +
                            "Giới hạn tối đa: 1,000,000 đơn vị gốc\n\n" +
                            "Bạn có chắc chắn muốn nhập số lượng này không?",
                            soLuongQuyCach, qcDaChon.getDonViTinh().getTenDonViTinh(),
                            this.soLuongNhapGoc, quyCachGoc.getDonViTinh().getTenDonViTinh()),
                        "Cảnh báo - Vượt giới hạn số lượng",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                    
                    if (choice != JOptionPane.YES_OPTION) {
                        spinnerSoLuongMoi.requestFocus();
                        return;
                    }
                }
                
                String maLo = txtMaLoMoi.getText();

                this.loDaChon = new LoSanPham(maLo, hsd, 0, this.sanPham);
                this.chiTietCanSua = null;

            } else { // Tab "Sửa Lô Hiện Tại"
                // KIỂM TRA CHỌN LÔ TRƯỚC - ƯU TIÊN HÀNG ĐẦU
                ChiTietPhieuNhap ctDuocChon = listLoCu.getSelectedValue();
                if (ctDuocChon == null) {
                    throw new Exception("Vui lòng chọn một lô hiện tại từ danh sách để sửa.");
                }

                // Sau khi đã chọn lô, mới kiểm tra quy cách và số lượng
                QuyCachDongGoi qcDaChon = (QuyCachDongGoi) cmbQuyCachCu.getSelectedItem();
                if (qcDaChon == null) {
                    throw new Exception("Vui lòng chọn một Quy Cách Đóng Gói mới.");
                }

                // Validate số lượng nhập - kiểm tra text trong editor trước
                JSpinner.DefaultEditor editorCu = (JSpinner.DefaultEditor) spinnerSoLuongCu.getEditor();
                String textCu = editorCu.getTextField().getText().trim();
                
                // Kiểm tra xem text có phải là số nguyên dương hợp lệ không
                if (textCu.isEmpty() || !textCu.matches("\\d+")) {
                    JOptionPane.showMessageDialog(this, 
                        "Số lượng không hợp lệ.\nVui lòng nhập một số nguyên dương.", 
                        "Số lượng không hợp lệ", 
                        JOptionPane.ERROR_MESSAGE);
                    editorCu.getTextField().requestFocus();
                    editorCu.getTextField().selectAll();
                    return;
                }
                
                try {
                    spinnerSoLuongCu.commitEdit();
                } catch (java.text.ParseException ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Số lượng không hợp lệ.\nVui lòng nhập một số nguyên.", 
                        "Số lượng không hợp lệ", 
                        JOptionPane.ERROR_MESSAGE);
                    spinnerSoLuongCu.requestFocus();
                    return;
                }

                int soLuongQuyCach = (Integer) spinnerSoLuongCu.getValue();
                
                // Kiểm tra số lượng > 0
                if (soLuongQuyCach <= 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Số lượng nhập phải lớn hơn 0!", 
                        "Số lượng không hợp lệ", 
                        JOptionPane.WARNING_MESSAGE);
                    spinnerSoLuongCu.requestFocus();
                    return;
                }
                
                this.soLuongNhapGoc = soLuongQuyCach * qcDaChon.getHeSoQuyDoi();
                
                // Lưu thông tin hiển thị
                this.quyCachDaChon = qcDaChon;
                this.soLuongHienThi = soLuongQuyCach;
                
                // Kiểm tra giới hạn 1,000,000 đơn vị gốc
                if (this.soLuongNhapGoc > 1000000) {
                    int choice = JOptionPane.showConfirmDialog(this,
                        String.format("⚠️ CẢNH BÁO: Số lượng vượt quá giới hạn!\n\n" +
                            "Số lượng nhập: %,d %s\n" +
                            "Quy đổi về đơn vị gốc: %,d %s\n" +
                            "Giới hạn tối đa: 1,000,000 đơn vị gốc\n\n" +
                            "Bạn có chắc chắn muốn nhập số lượng này không?",
                            soLuongQuyCach, qcDaChon.getDonViTinh().getTenDonViTinh(),
                            this.soLuongNhapGoc, quyCachGoc.getDonViTinh().getTenDonViTinh()),
                        "Cảnh báo - Vượt giới hạn số lượng",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                    
                    if (choice != JOptionPane.YES_OPTION) {
                        spinnerSoLuongCu.requestFocus();
                        return;
                    }
                }
                
                this.chiTietCanSua = ctDuocChon;
                this.loDaChon = ctDuocChon.getLoSanPham();
            }

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
        
        // Giá hiển thị = Giá gốc * Hệ số
        double giaHienThi = giaGoc * heSo;
        
        txtDonGia.setText(df.format(giaHienThi));
    }
    
    /**
     * Kiểm tra giới hạn số lượng khi thay đổi đơn vị tính
     */
    private void kiemTraGioiHanSoLuong(JSpinner spinner, JComboBox<QuyCachDongGoi> cmb) {
        // Nếu đang load data từ list, không kiểm tra
        if (isLoadingData) {
            return;
        }
        
        try {
            int giaTri = (Integer) spinner.getValue();
            
            // Chỉ kiểm tra giới hạn 1,000,000 đơn vị gốc (kiểm tra ≤0 sẽ làm trong xuLyXacNhan)
            // Kiểm tra giới hạn 1,000,000 đơn vị gốc
            QuyCachDongGoi qc = (QuyCachDongGoi) cmb.getSelectedItem();
            if (qc != null) {
                int soLuongGoc = giaTri * qc.getHeSoQuyDoi();
                
                if (soLuongGoc > 1000000) {
                    int choice = JOptionPane.showConfirmDialog(this,
                        String.format("⚠️ CẢNH BÁO: Số lượng vượt quá giới hạn!\n\n" +
                            "Số lượng nhập: %,d %s\n" +
                            "Quy đổi về đơn vị gốc: %,d %s\n" +
                            "Giới hạn tối đa: 1,000,000 đơn vị gốc\n\n" +
                            "Bạn có chắc chắn muốn nhập số lượng này không?",
                            giaTri, qc.getDonViTinh().getTenDonViTinh(),
                            soLuongGoc, quyCachGoc.getDonViTinh().getTenDonViTinh()),
                        "Cảnh báo - Vượt giới hạn số lượng",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                    
                    if (choice != JOptionPane.YES_OPTION) {
                        spinner.setValue(1);
                        spinner.requestFocus();
                    }
                }
            }
        } catch (ClassCastException e) {
            // Xử lý trường hợp giá trị không phải Integer
            JOptionPane.showMessageDialog(this, 
                "Số lượng không hợp lệ. Vui lòng nhập một số nguyên.", 
                "Lỗi định dạng", 
                JOptionPane.ERROR_MESSAGE);
            spinner.setValue(1);
            spinner.requestFocus();
        } catch (Exception e) {
            // Xử lý các lỗi khác
            JOptionPane.showMessageDialog(this, 
                "Có lỗi xảy ra khi kiểm tra số lượng: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== V. TẢI DỮ LIỆU (DATA) =====

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
    
    public QuyCachDongGoi getQuyCachDaChon() {
        return quyCachDaChon;
    }
    
    public int getSoLuongHienThi() {
        return soLuongHienThi;
    }

    // ===== VII. IMPLEMENTS LISTENERS =====

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source == btnLuu) {
            xuLyXacNhan();
        } else if (source == btnThoat) {
            confirmed = false;
            dispose();
        } else if (source == cmbQuyCachMoi) {
            capNhatGiaTheoQuyCach(cmbQuyCachMoi, txtDonGiaMoi);
            // Trigger validation lại cho số lượng hiện tại
            kiemTraGioiHanSoLuong(spinnerSoLuongMoi, cmbQuyCachMoi);
        } else if (source == cmbQuyCachCu) {
            capNhatGiaTheoQuyCach(cmbQuyCachCu, txtDonGiaCu);
            // Trigger validation lại cho số lượng hiện tại
            kiemTraGioiHanSoLuong(spinnerSoLuongCu, cmbQuyCachCu);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Xử lý khi click vào list
        if (e.getSource() == listLoCu) {
            if (listLoCu.getSelectedIndex() != -1) {
                capNhatFormTuList();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    // ===== VIII. LỚP NỘI BỘ (INNER CLASS) =====
    
    /**
     * Áp dụng bộ lọc chỉ cho phép nhập số vào JSpinner
     */
    private void applyNumericFilter(JSpinner spinner) {
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) editor;
            JTextField textField = spinnerEditor.getTextField();
            ((AbstractDocument) textField.getDocument()).setDocumentFilter(new NumericDocumentFilter());
            
            // Thêm FocusListener để validate khi rời khỏi field
            textField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    String text = textField.getText().trim();
                    // Nếu text rỗng hoặc không phải số hợp lệ
                    if (text.isEmpty() || !text.matches("\\d+")) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(ChonLo_Dialog.this, 
                                "Số lượng không hợp lệ.\nVui lòng nhập một số nguyên dương.", 
                                "Lỗi nhập liệu", 
                                JOptionPane.ERROR_MESSAGE);
                            textField.requestFocus();
                            textField.selectAll();
                        });
                        return;
                    }
                    
                    try {
                        int value = Integer.parseInt(text);
                        if (value <= 0) {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(ChonLo_Dialog.this, 
                                    "Số lượng phải lớn hơn 0.\nVui lòng nhập lại.", 
                                    "Lỗi nhập liệu", 
                                    JOptionPane.ERROR_MESSAGE);
                                textField.requestFocus();
                                textField.selectAll();
                            });
                        }
                    } catch (NumberFormatException ex) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(ChonLo_Dialog.this, 
                                "Số lượng không hợp lệ.\nVui lòng nhập một số nguyên dương.", 
                                "Lỗi nhập liệu", 
                                JOptionPane.ERROR_MESSAGE);
                            textField.requestFocus();
                            textField.selectAll();
                        });
                    }
                }
            });
        }
    }
    
    /**
     * DocumentFilter chỉ cho phép nhập số
     */
    class NumericDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string != null && isValidInput(string)) {
                super.insertString(fb, offset, string, attr);
            } else {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(ChonLo_Dialog.this, 
                    "Chỉ được nhập số!", 
                    "Lỗi nhập liệu", 
                    JOptionPane.WARNING_MESSAGE);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text != null && isValidInput(text)) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(ChonLo_Dialog.this, 
                    "Chỉ được nhập số!", 
                    "Lỗi nhập liệu", 
                    JOptionPane.WARNING_MESSAGE);
            }
        }
        
        private boolean isValidInput(String text) {
            // Cho phép chuỗi rỗng (khi xóa) hoặc chỉ chứa số
            return text.isEmpty() || text.matches("[0-9]+");
        }
    }

    class ChiTietPhieuNhapRenderer extends DefaultListCellRenderer {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ChiTietPhieuNhap ct) {
                LoSanPham lo = ct.getLoSanPham();
                
                String tenDonViHienThi;
                int soLuongHienThi;
                
                // Kiểm tra xem có thông tin hiển thị trong Map không
                if (mapThongTinHienThi != null && mapThongTinHienThi.containsKey(ct)) {
                    Object ttht = mapThongTinHienThi.get(ct);
                    // Sử dụng reflection để lấy thông tin (vì không thể import class từ GUI)
                    try {
                        java.lang.reflect.Field quyCachField = ttht.getClass().getDeclaredField("quyCach");
                        java.lang.reflect.Field soLuongField = ttht.getClass().getDeclaredField("soLuong");
                        quyCachField.setAccessible(true);
                        soLuongField.setAccessible(true);
                        
                        QuyCachDongGoi qc = (QuyCachDongGoi) quyCachField.get(ttht);
                        soLuongHienThi = (Integer) soLuongField.get(ttht);
                        tenDonViHienThi = qc.getDonViTinh().getTenDonViTinh();
                    } catch (Exception e) {
                        // Nếu có lỗi, dùng đơn vị gốc
                        tenDonViHienThi = quyCachGoc.getDonViTinh().getTenDonViTinh();
                        soLuongHienThi = ct.getSoLuongNhap();
                    }
                } else {
                    // Không có thông tin hiển thị -> dùng đơn vị gốc
                    tenDonViHienThi = quyCachGoc.getDonViTinh().getTenDonViTinh();
                    soLuongHienThi = ct.getSoLuongNhap();
                }
                
                String text = String.format("%s - HSD: %s - (Hiện có: %d %s)",
                        lo.getMaLo(),
                        lo.getHanSuDung().format(fmtDate),
                        soLuongHienThi,
                        tenDonViHienThi
                );
                setText(text);
                setBorder(new EmptyBorder(5, 5, 5, 5));
            }
            return this;
        }
    }

	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		
		if (source == spinnerSoLuongMoi) {
			kiemTraGioiHanSoLuong(spinnerSoLuongMoi, cmbQuyCachMoi);
		} else if (source == spinnerSoLuongCu) {
			kiemTraGioiHanSoLuong(spinnerSoLuongCu, cmbQuyCachCu);
		}
	}
}