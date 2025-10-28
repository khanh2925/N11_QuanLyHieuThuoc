package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.util.Random;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import entity.SanPham;
import enums.DuongDung;
import enums.LoaiSanPham;

public class ThemSanPham_Dialog extends JDialog implements ActionListener { // <<< IMPLEMENT ActionListener

    private JTextField txtTenSanPham;
    private JTextField txtSoDangKy;
    private JTextField txtGiaNhap;
    private JTextField txtHinhAnh; 
    private JTextField txtKeBanSanPham;
    
    private JComboBox<LoaiSanPham> cmbLoaiSanPham;
    private JComboBox<DuongDung> cmbDuongDung;
    private JCheckBox chkHoatDong; 
    
    private JLabel lblGiaBanTuDong; 
    private JButton btnChonAnh;     

    private JButton btnThem;
    private JButton btnThoat;

    private SanPham sanPhamMoi = null;
    private final Random random = new Random();

    public ThemSanPham_Dialog(Frame owner) {
        super(owner, "Thêm sản phẩm mới", true);
        initialize();
    }
    
    /**
     * Hàm tính toán Giá bán tối thiểu (theo logic entity SanPham.java)
     */
    private double tinhGiaBanToiThieu(double giaNhap) {
        double heSoLoiNhuan;
        if (giaNhap < 10000) heSoLoiNhuan = 1.5;
        else if (giaNhap < 50000) heSoLoiNhuan = 1.3;
        else if (giaNhap < 200000) heSoLoiNhuan = 1.2;
        else heSoLoiNhuan = 1.1;
        
        return giaNhap * heSoLoiNhuan; 
    }
    
    /**
     * Mở JFileChooser để chọn file ảnh, điền tên file vào txtHinhAnh
     */
    private void openFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file hình ảnh");
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            txtHinhAnh.setText(selectedFile.getName()); 
        }
    }
    
    /**
     * Tính toán và hiển thị giá bán tự động dựa trên giá nhập
     */
    private void capNhatGiaBanTuDong() {
        try {
            double giaNhap = Double.parseDouble(txtGiaNhap.getText());
            if (giaNhap <= 0) {
                 lblGiaBanTuDong.setText("Giá nhập > 0");
                 lblGiaBanTuDong.setForeground(Color.RED);
                 return;
            }
            double giaBanToiThieu = tinhGiaBanToiThieu(giaNhap);
            lblGiaBanTuDong.setText(String.format("%,.0f VND", giaBanToiThieu));
            lblGiaBanTuDong.setForeground(Color.BLUE);
        } catch (NumberFormatException ex) {
            lblGiaBanTuDong.setText("Nhập số");
            lblGiaBanTuDong.setForeground(Color.RED);
        }
    }

    private void initialize() {
        setSize(780, 600);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        // --- Tiêu đề Dialog ---
        JLabel lblTitle = new JLabel("Thêm sản phẩm");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBounds(280, 20, 250, 35);
        getContentPane().add(lblTitle);

        // --- Cột 1 ---

        // Tên sản phẩm
        JLabel lblTen = new JLabel("Tên sản phẩm (Max 100):");
        lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTen.setBounds(40, 80, 250, 25);
        getContentPane().add(lblTen);

        txtTenSanPham = new JTextField();
        txtTenSanPham.setBounds(40, 110, 320, 35);
        txtTenSanPham.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtTenSanPham);
        
        // Loại sản phẩm (Enum)
        JLabel lblLoaiSP = new JLabel("Loại sản phẩm:");
        lblLoaiSP.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblLoaiSP.setBounds(40, 160, 250, 25);
        getContentPane().add(lblLoaiSP);

        cmbLoaiSanPham = new JComboBox<>(LoaiSanPham.values());
        cmbLoaiSanPham.setBounds(40, 190, 320, 35);
        cmbLoaiSanPham.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(cmbLoaiSanPham);

        // Giá nhập
        JLabel lblGiaNhap = new JLabel("Giá nhập (VND) *:");
        lblGiaNhap.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblGiaNhap.setBounds(40, 240, 250, 25);
        getContentPane().add(lblGiaNhap);

        txtGiaNhap = new JTextField();
        txtGiaNhap.setBounds(40, 270, 320, 35);
        txtGiaNhap.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtGiaNhap);
        
        // Giá bán TỰ ĐỘNG 
        JLabel lblGiaBanHeader = new JLabel("Giá bán (Tự động tính):");
        lblGiaBanHeader.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblGiaBanHeader.setBounds(40, 320, 300, 25);
        getContentPane().add(lblGiaBanHeader);

        lblGiaBanTuDong = new JLabel("Nhập Giá nhập để tính"); 
        lblGiaBanTuDong.setBounds(40, 350, 320, 35);
        lblGiaBanTuDong.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblGiaBanTuDong.setBorder(new LineBorder(Color.LIGHT_GRAY));
        lblGiaBanTuDong.setHorizontalAlignment(SwingConstants.CENTER);
        getContentPane().add(lblGiaBanTuDong);
        
        // Listener để tính toán khi Giá nhập thay đổi (vẫn dùng Lambda/Adapter vì không phải JButton)
        txtGiaNhap.addActionListener(e -> capNhatGiaBanTuDong());
        txtGiaNhap.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                capNhatGiaBanTuDong();
            }
        });


        // --- Cột 2 ---
        
        // Số đăng ký
        JLabel lblSoDangKy = new JLabel("Số đăng ký (Max 20):");
        lblSoDangKy.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSoDangKy.setBounds(390, 80, 250, 25);
        getContentPane().add(lblSoDangKy);

        txtSoDangKy = new JTextField();
        txtSoDangKy.setBounds(390, 110, 340, 35);
        txtSoDangKy.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtSoDangKy);
        
        // Đường dùng (Enum)
        JLabel lblDuongDung = new JLabel("Đường dùng:");
        lblDuongDung.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDuongDung.setBounds(390, 160, 250, 25);
        getContentPane().add(lblDuongDung);

        cmbDuongDung = new JComboBox<>(DuongDung.values());
        cmbDuongDung.setBounds(390, 190, 340, 35);
        cmbDuongDung.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(cmbDuongDung);
        
        // Kệ bán sản phẩm
        JLabel lblKeBan = new JLabel("Kệ bán sản phẩm (Max 100):");
        lblKeBan.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblKeBan.setBounds(390, 240, 250, 25);
        getContentPane().add(lblKeBan);

        txtKeBanSanPham = new JTextField();
        txtKeBanSanPham.setBounds(390, 270, 340, 35);
        txtKeBanSanPham.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtKeBanSanPham);
        
        // Hình ảnh (Tên file/URL) + Button
        JLabel lblHinhAnh = new JLabel("Tên file / URL hình ảnh (Max 255):");
        lblHinhAnh.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblHinhAnh.setBounds(390, 320, 300, 25);
        getContentPane().add(lblHinhAnh);

        txtHinhAnh = new JTextField();
        txtHinhAnh.setBounds(390, 350, 220, 35); // Giảm chiều rộng cho nút
        txtHinhAnh.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtHinhAnh);
        
        btnChonAnh = new JButton("Chọn ảnh");
        btnChonAnh.setBounds(620, 350, 110, 35); // Nút chọn ảnh
        btnChonAnh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnChonAnh.setBackground(new Color(0x3B82F6));
        btnChonAnh.setForeground(Color.WHITE);
        btnChonAnh.setBorder(null);
        getContentPane().add(btnChonAnh);
        
        // GẮN SỰ KIỆN CHO BUTTON CHỌN ẢNH (DÙNG this)
        btnChonAnh.addActionListener(this); 

        // Trạng thái Hoạt động
        chkHoatDong = new JCheckBox("Đang hoạt động");
        chkHoatDong.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        chkHoatDong.setBackground(Color.WHITE);
        chkHoatDong.setSelected(true);
        chkHoatDong.setBounds(40, 420, 200, 35);
        getContentPane().add(chkHoatDong);

        // --- Các nút thao tác ---
        btnThoat = new JButton("Thoát");
        btnThoat.setBounds(620, 500, 110, 35);
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(0x6B7280));
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setBorder(null);
        getContentPane().add(btnThoat);

        btnThem = new JButton("Thêm");
        btnThem.setBounds(490, 500, 110, 35);
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThem.setBackground(new Color(0x3B82F6));
        btnThem.setForeground(Color.WHITE);
        btnThem.setBorder(null);
        getContentPane().add(btnThem);

        // GẮN SỰ KIỆN CHO BUTTON THÊM VÀ THOÁT (DÙNG this)
        btnThoat.addActionListener(this); 
        btnThem.addActionListener(this); 
    }
    
    // <<< PHƯƠNG THỨC actionPERFORMED (BƯỚC 3) >>>
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source.equals(btnThem)) {
            handleThemAction();
        } else if (source.equals(btnThoat)) {
            dispose();
        } else if (source.equals(btnChonAnh)) {
            openFileChooser();
        }
    }

    // <<< TÁCH LOGIC XỬ LÝ NÚT THÊM (BƯỚC 4) >>>
    private void handleThemAction() {
        try {
            // 1. Lấy dữ liệu từ Form
            String tenSanPham = txtTenSanPham.getText();
            LoaiSanPham loaiSanPham = (LoaiSanPham) cmbLoaiSanPham.getSelectedItem();
            String soDangKy = txtSoDangKy.getText().trim().isEmpty() ? null : txtSoDangKy.getText();
            DuongDung duongDung = (DuongDung) cmbDuongDung.getSelectedItem();
            
            // 2. Kiểm tra và lấy Giá nhập
            double giaNhap = Double.parseDouble(txtGiaNhap.getText());
            if (giaNhap <= 0) {
                 JOptionPane.showMessageDialog(this, "Giá nhập phải lớn hơn 0.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            
            // 3. TÍNH TOÁN GIÁ BÁN TỰ ĐỘNG
            double giaBanTuDong = tinhGiaBanToiThieu(giaNhap);
            
            String hinhAnh = txtHinhAnh.getText().trim().isEmpty() ? null : txtHinhAnh.getText();
            String keBanSanPham = txtKeBanSanPham.getText().trim().isEmpty() ? null : txtKeBanSanPham.getText();
            boolean hoatDong = chkHoatDong.isSelected();

            // 4. Tạo mã SP (SPxxxxxx - 8 ký tự)
            String maSanPham = String.format("SP%06d", random.nextInt(1000000));

            // 5. Kiểm tra logic đơn giản (trước khi gọi Entity setters)
            if (tenSanPham.trim().isEmpty() || tenSanPham.length() > 100) {
                 JOptionPane.showMessageDialog(this, "Tên sản phẩm không hợp lệ (Không được rỗng, tối đa 100 ký tự).", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            if (soDangKy != null && soDangKy.length() > 20) {
                 JOptionPane.showMessageDialog(this, "Số đăng ký tối đa 20 ký tự.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            
            // 6. Tạo Entity (sẽ tự động kiểm tra logic nghiệp vụ phức tạp)
            this.sanPhamMoi = new SanPham(
                maSanPham, tenSanPham, loaiSanPham, soDangKy,
                duongDung, giaNhap, giaBanTuDong, hinhAnh, 
                keBanSanPham, hoatDong
            );
            
            // Nếu tạo thành công, đóng dialog
            dispose();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá nhập phải là số hợp lệ.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            // Bắt các lỗi validation từ setters của Entity (ví dụ: logic lợi nhuận, độ dài)
            JOptionPane.showMessageDialog(this, "Lỗi nghiệp vụ: " + ex.getMessage(), "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi không xác định: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public SanPham getSanPhamMoi() {
        return sanPhamMoi;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Dialog");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            
            ThemSanPham_Dialog dialog = new ThemSanPham_Dialog(frame);
            dialog.setVisible(true);
            
            // In kết quả sau khi đóng dialog
            if (dialog.getSanPhamMoi() != null) {
                System.out.println("Sản phẩm mới được thêm: " + dialog.getSanPhamMoi());
            } else {
                System.out.println("Hủy bỏ hoặc thất bại.");
            }
            // Khởi tạo lại frame test nếu cần
        });
    }
}