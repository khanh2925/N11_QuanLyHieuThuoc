package gui.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.toedter.calendar.JDateChooser;
import entity.DonViTinh;
import entity.LoSanPham;
import entity.QuyCachDongGoi;
import entity.SanPham;

public class ThemLo_Dialog extends JDialog {

    private JTextField txtMaLo;
    private JSpinner spinnerSoLuong;
    private JDateChooser dateHanSuDung;
    private JButton btnLuu, btnThoat;
    private JTextField txtDonGia;
    
    private JComboBox<QuyCachDongGoi> cmbQuyCach; 

    // Nơi lưu trữ kết quả (luôn là ĐƠN VỊ GỐC)
    private boolean confirmed = false;
    private LoSanPham loSanPham = null;
    private double donGiaNhapGoc = 0;   // Giá của đơn vị gốc
    private int soLuongNhapGoc = 0;   // Số lượng đã quy đổi về đơn vị gốc
    private DonViTinh donViTinhGoc = null; // Đơn vị tính gốc

    // Thông tin truyền vào
    private SanPham sanPham;
    private String maLoDeNghi;
    private List<QuyCachDongGoi> dsQuyCach;
    private QuyCachDongGoi quyCachGoc;
    
    private final DecimalFormat df = new DecimalFormat("#,### đ");

    public ThemLo_Dialog(Frame owner, SanPham sp, String maLoDeNghi, List<QuyCachDongGoi> dsQuyCach, QuyCachDongGoi quyCachGoc) {
        super(owner, "Nhập lô cho: " + sp.getTenSanPham(), true);
        this.sanPham = sp;
        this.maLoDeNghi = maLoDeNghi;
        this.dsQuyCach = dsQuyCach;
        this.quyCachGoc = quyCachGoc;
        
        this.donViTinhGoc = quyCachGoc.getDonViTinh();
        this.donGiaNhapGoc = sp.getGiaNhap();
        
        initialize();
        
        txtMaLo.setText(maLoDeNghi);
        
        for (QuyCachDongGoi qc : dsQuyCach) {
            cmbQuyCach.addItem(qc);
        }
        
        cmbQuyCach.setSelectedIndex(0);
        capNhatGiaTheoQuyCach();
    }

    private void initialize() {
        setSize(450, 450);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(Color.WHITE);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fontLabel = new Font("Segoe UI", Font.PLAIN, 16);
        Font fontField = new Font("Segoe UI", Font.PLAIN, 14);

        // Hàng 0: Mã Lô
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        JLabel lblMaLo = new JLabel("Mã Lô (tự sinh):");
        lblMaLo.setFont(fontLabel);
        mainPanel.add(lblMaLo, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtMaLo = new JTextField();
        txtMaLo.setFont(fontField);
        txtMaLo.setEditable(false);
        txtMaLo.setBackground(new Color(0xF3F4F6));
        mainPanel.add(txtMaLo, gbc);

        // Hàng 1: Hạn Sử Dụng
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel lblHanSuDung = new JLabel("Hạn sử dụng:");
        lblHanSuDung.setFont(fontLabel);
        mainPanel.add(lblHanSuDung, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        dateHanSuDung = new JDateChooser();
        dateHanSuDung.setDateFormatString("dd/MM/yyyy");
        dateHanSuDung.setFont(fontField);
        dateHanSuDung.setDate(Date.from(LocalDate.now().plusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        mainPanel.add(dateHanSuDung, gbc);

        // Hàng 2: Đơn Vị Tính (Quy Cách)
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblDonViTinh = new JLabel("Đơn vị tính:");
        lblDonViTinh.setFont(fontLabel);
        mainPanel.add(lblDonViTinh, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        cmbQuyCach = new JComboBox<>();
        cmbQuyCach.setFont(fontField);
        cmbQuyCach.setBackground(Color.WHITE);
        cmbQuyCach.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof QuyCachDongGoi qc) {
                    setText(qc.getDonViTinh().getTenDonViTinh());
                }
                return this;
            }
        });
        cmbQuyCach.addActionListener(e -> capNhatGiaTheoQuyCach());
        mainPanel.add(cmbQuyCach, gbc);
        
        // Hàng 3: Số Lượng
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblSoLuong = new JLabel("Số lượng nhập:");
        lblSoLuong.setFont(fontLabel);
        mainPanel.add(lblSoLuong, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        spinnerSoLuong = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        spinnerSoLuong.setFont(fontField);
        mainPanel.add(spinnerSoLuong, gbc);

        // Hàng 4: Đơn Giá
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblDonGia = new JLabel("Đơn giá (theo ĐVT):");
        lblDonGia.setFont(fontLabel);
        mainPanel.add(lblDonGia, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        txtDonGia = new JTextField();
        txtDonGia.setFont(fontField);
        txtDonGia.setEditable(false); 
        txtDonGia.setBackground(new Color(0xF3F4F6)); 
        txtDonGia.setHorizontalAlignment(JTextField.RIGHT);
        mainPanel.add(txtDonGia, gbc);

        // Panel Nút Bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
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

        buttonPanel.add(btnLuu);
        buttonPanel.add(btnThoat);
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        btnLuu.addActionListener(e -> xuLyLuu());
        btnThoat.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
    }
    
    /**
     * ✅ SỬA 7: Hàm mới để cập nhật giá khi chọn JComboBox
     */
    private void capNhatGiaTheoQuyCach() {
        QuyCachDongGoi qcDaChon = (QuyCachDongGoi) cmbQuyCach.getSelectedItem();
        if (qcDaChon == null) return;
        
        double giaGoc = sanPham.getGiaNhap();
        int heSo = qcDaChon.getHeSoQuyDoi();
        // double tiLeGiam = qcDaChon.getTiLeGiam(); // <-- BỎ TỶ LỆ GIẢM
        
        // Giá hiển thị = Giá gốc * Hệ số
        double giaHienThi = giaGoc * heSo; // <-- SỬA LẠI CÔNG THỨC
        
        txtDonGia.setText(df.format(giaHienThi));
    }
    
    private void xuLyLuu() {
        Date selectedDate = dateHanSuDung.getDate();
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Hạn Sử Dụng.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LocalDate hsd = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        // Kiểm tra HSD đã hết hạn
        if (hsd.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, 
                String.format("Hạn sử dụng đã hết hạn!\nSản phẩm: %s\nHSD: %s", 
                    sanPham.getTenSanPham(), hsd), 
                "HSD không hợp lệ", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Cảnh báo nếu HSD <= 3 tháng
        if (hsd.isBefore(LocalDate.now().plusMonths(3))) {
            int option = JOptionPane.showConfirmDialog(this,
                String.format("⚠️ CẢNH BÁO: Sản phẩm sắp hết hạn!\n\n" +
                    "Sản phẩm: %s\n" +
                    "Hạn sử dụng: %s\n" +
                    "Còn lại: %d ngày\n\n" +
                    "Bạn có chắc chắn muốn nhập sản phẩm này không?",
                    sanPham.getTenSanPham(), hsd, 
                    java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), hsd)),
                "Cảnh báo HSD sắp hết hạn",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (option != JOptionPane.YES_OPTION) {
                return; // Người dùng chọn Không → hủy việc thêm lô
            }
        }
        
        QuyCachDongGoi qcDaChon = (QuyCachDongGoi) cmbQuyCach.getSelectedItem();
        if (qcDaChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một Quy Cách Đóng Gói.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int soLuongQuyCach = (Integer) spinnerSoLuong.getValue();
            
            // Kiểm tra số lượng hợp lệ
            if (soLuongQuyCach <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Số lượng nhập phải lớn hơn 0!", 
                    "Số lượng không hợp lệ", 
                    JOptionPane.WARNING_MESSAGE);
                spinnerSoLuong.requestFocus();
                return;
            }
            
            // QUY ĐỔI VỀ GỐC
            this.soLuongNhapGoc = soLuongQuyCach * qcDaChon.getHeSoQuyDoi();
            
            // Kiểm tra sau khi quy đổi
            if (this.soLuongNhapGoc <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi quy đổi: Số lượng sau khi quy đổi không hợp lệ!", 
                    "Lỗi quy đổi", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String maLo = txtMaLo.getText();
            
            this.loSanPham = new LoSanPham(maLo, hsd, 0, this.sanPham);
            
            this.confirmed = true;
            this.dispose();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== Các getter để ThemPhieuNhap_GUI lấy kết quả =====
    
    public boolean isConfirmed() {
        return confirmed;
    }

    public LoSanPham getLoSanPham() {
        return loSanPham;
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
}