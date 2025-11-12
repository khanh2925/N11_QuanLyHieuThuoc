package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat; // 💡 THÊM IMPORT
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.toedter.calendar.JDateChooser;
import entity.DonViTinh;
import entity.LoSanPham;
import entity.SanPham;

public class ThemLo_Dialog extends JDialog {

    private JTextField txtMaLo;
    private JSpinner spinnerSoLuong;
    private JDateChooser dateHanSuDung;
    private JButton btnLuu, btnThoat;
    private JTextField txtDonGia; // ✅ SỬA 1: Đổi JSpinner thành JTextField
    private JComboBox<DonViTinh> cmbDonViTinh;
    
    // Nơi lưu trữ kết quả
    private boolean confirmed = false;
    private LoSanPham loSanPham = null;
    private double donGiaNhap = 0;
    private int soLuongNhap = 0;
    private DonViTinh donViTinh = null;
    
    // Thông tin truyền vào
    private SanPham sanPham;
    private String maLoDeNghi;
    
    // ✅ SỬA 2: Thêm định dạng tiền tệ
    private final DecimalFormat df = new DecimalFormat("#,##0.00 đ");

    /**
     * Constructor mới để nhận dữ liệu
     * @param owner Frame cha (Main_GUI)
     * @param sp Sản phẩm cần thêm lô
     * @param maLoDeNghi Mã lô được tạo tự động
     * @param dsDVT Danh sách đơn vị tính để chọn
     */
    public ThemLo_Dialog(Frame owner, SanPham sp, String maLoDeNghi, List<DonViTinh> dsDVT) {
        super(owner, "Nhập lô cho: " + sp.getTenSanPham(), true);
        this.sanPham = sp;
        this.maLoDeNghi = maLoDeNghi;
        
        initialize();
        
        // Cập nhật các trường với dữ liệu được truyền vào
        txtMaLo.setText(maLoDeNghi);
        
        // ✅ SỬA 3: Đặt giá trị cho JTextField (đã định dạng)
        txtDonGia.setText(df.format(sp.getGiaNhap()));
        
        // Nạp JComboBox
        for (DonViTinh dvt : dsDVT) {
            cmbDonViTinh.addItem(dvt);
        }
        cmbDonViTinh.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof DonViTinh) {
                    setText(((DonViTinh) value).getTenDonViTinh());
                }
                return this;
            }
        });
    }
    
    /**
     * Constructor cũ (chỉ dùng để test)
     */
    public ThemLo_Dialog(Frame owner) {
        super(owner, "Tạo lô sản phẩm", true);
        this.sanPham = new SanPham("SP-000001");
        this.sanPham.setTenSanPham("Paracetamol (Test)");
        this.sanPham.setGiaNhap(10000.0);
        this.maLoDeNghi = "LO-000001";
        
        initialize();
        
        // Dữ liệu giả để test
        txtMaLo.setText(maLoDeNghi);
        // ✅ SỬA 4: Cập nhật constructor test
        txtDonGia.setText(df.format(this.sanPham.getGiaNhap())); 
        cmbDonViTinh.addItem(new DonViTinh("DVT-001", "Viên"));
        cmbDonViTinh.addItem(new DonViTinh("DVT-002", "Vỉ"));
    }

    private void initialize() {
        setSize(450, 450);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(Color.WHITE);
        
        // Panel chính với GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5); // Khoảng cách giữa các component
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
        txtMaLo.setBackground(new Color(0xF3F4F6)); // Màu xám nhạt
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

        // Hàng 2: Số Lượng
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblSoLuong = new JLabel("Số lượng nhập:");
        lblSoLuong.setFont(fontLabel);
        mainPanel.add(lblSoLuong, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        spinnerSoLuong = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        spinnerSoLuong.setFont(fontField);
        mainPanel.add(spinnerSoLuong, gbc);

        // ✅ SỬA 5: Thay thế JSpinner bằng JTextField
        // Hàng 3: Đơn Giá
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblDonGia = new JLabel("Đơn giá nhập:");
        lblDonGia.setFont(fontLabel);
        mainPanel.add(lblDonGia, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        txtDonGia = new JTextField();
        txtDonGia.setFont(fontField);
        txtDonGia.setEditable(false); // Không cho chỉnh sửa
        txtDonGia.setBackground(new Color(0xF3F4F6)); // Đặt màu nền xám
        txtDonGia.setHorizontalAlignment(JTextField.RIGHT); // Căn phải cho đẹp
        
        mainPanel.add(txtDonGia, gbc);

        // Hàng 4: Đơn Vị Tính
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblDonViTinh = new JLabel("Đơn vị tính:");
        lblDonViTinh.setFont(fontLabel);
        mainPanel.add(lblDonViTinh, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        cmbDonViTinh = new JComboBox<>();
        cmbDonViTinh.setFont(fontField);
        mainPanel.add(cmbDonViTinh, gbc);

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
        
        // Thêm panel chính và panel nút vào JDialog
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        // Thêm Action Listeners
        btnLuu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyLuu();
            }
        });
        
        btnThoat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmed = false;
                dispose();
            }
        });
    }
    
    private void xuLyLuu() {
        // 1. Validate dữ liệu
        Date selectedDate = dateHanSuDung.getDate();
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Hạn Sử Dụng.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LocalDate hsd = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (hsd.isBefore(LocalDate.now().plusDays(30))) {
             JOptionPane.showMessageDialog(this, "Hạn sử dụng phải lớn hơn 30 ngày kể từ hôm nay.", "Ngày không hợp lệ", JOptionPane.WARNING_MESSAGE);
             return;
        }
        
        DonViTinh dvtChon = (DonViTinh) cmbDonViTinh.getSelectedItem();
        if (dvtChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Đơn Vị Tính.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            this.soLuongNhap = (Integer) spinnerSoLuong.getValue();
            
            this.donGiaNhap = this.sanPham.getGiaNhap(); 
            
            this.donViTinh = dvtChon;
            String maLo = txtMaLo.getText();
            
            // 3. Tạo đối tượng LoSanPham (với soLuongTon = 0)
            this.loSanPham = new LoSanPham(maLo, hsd, 0, this.sanPham);
            
            // 4. Xác nhận và đóng
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
        return donGiaNhap;
    }

    public int getSoLuongNhap() {
        return soLuongNhap;
    }

    public DonViTinh getDonViTinh() {
        return donViTinh;
    }

    // =================== TEST MAIN ===================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Test với dữ liệu giả
            ThemLo_Dialog dialog = new ThemLo_Dialog(null);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                System.out.println("Đã xác nhận:");
                System.out.println("Lô: " + dialog.getLoSanPham());
                System.out.println("Số lượng: " + dialog.getSoLuongNhap());
                System.out.println("Đơn giá: " + dialog.getDonGiaNhap());
                System.out.println("ĐVT: " + dialog.getDonViTinh());
            } else {
                System.out.println("Đã hủy.");
            }
        });
    }
}