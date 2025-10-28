package gui;

import java.awt.*;
import java.io.File;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.event.*;

import dao.SanPham_DAO;
import entity.SanPham;
import enums.DuongDung;
import enums.LoaiSanPham;

public class ThemSanPham_Dialog extends JDialog implements ActionListener {

    private JTextField txtTenSanPham, txtSoDangKy, txtGiaNhap, txtHinhAnh, txtKeBanSanPham;
    private JComboBox<LoaiSanPham> cmbLoaiSanPham;
    private JComboBox<DuongDung> cmbDuongDung;
    private JCheckBox chkHoatDong;
    private JLabel lblGiaBanTuDong;
    private JButton btnThem, btnThoat, btnChonAnh;

    private SanPham sanPhamMoi;
    private SanPham_DAO sanPhamDAO = new SanPham_DAO();
    private boolean isCreated = false;
    private final Random random = new Random();

    public ThemSanPham_Dialog(Frame owner) {
        super(owner, "Thêm sản phẩm mới", true);
        initialize();
    }

    private void initialize() {
        setSize(780, 600);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel lblTitle = new JLabel("Thêm sản phẩm mới");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBounds(260, 20, 280, 35);
        getContentPane().add(lblTitle);

        // Cột 1
        JLabel lblTen = new JLabel("Tên sản phẩm:");
        lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTen.setBounds(40, 80, 200, 25);
        getContentPane().add(lblTen);

        txtTenSanPham = new JTextField();
        txtTenSanPham.setBounds(40, 110, 320, 35);
        getContentPane().add(txtTenSanPham);

        JLabel lblLoai = new JLabel("Loại sản phẩm:");
        lblLoai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblLoai.setBounds(40, 160, 200, 25);
        getContentPane().add(lblLoai);

        cmbLoaiSanPham = new JComboBox<>(LoaiSanPham.values());
        cmbLoaiSanPham.setBounds(40, 190, 320, 35);
        getContentPane().add(cmbLoaiSanPham);

        JLabel lblGiaNhap = new JLabel("Giá nhập (VND):");
        lblGiaNhap.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblGiaNhap.setBounds(40, 240, 200, 25);
        getContentPane().add(lblGiaNhap);

        txtGiaNhap = new JTextField();
        txtGiaNhap.setBounds(40, 270, 320, 35);
        getContentPane().add(txtGiaNhap);

        JLabel lblGiaBan = new JLabel("Giá bán (Tự động tính):");
        lblGiaBan.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblGiaBan.setBounds(40, 320, 300, 25);
        getContentPane().add(lblGiaBan);

        lblGiaBanTuDong = new JLabel("Nhập giá nhập để tính");
        lblGiaBanTuDong.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblGiaBanTuDong.setBorder(new LineBorder(Color.LIGHT_GRAY));
        lblGiaBanTuDong.setBounds(40, 350, 320, 35);
        lblGiaBanTuDong.setHorizontalAlignment(SwingConstants.CENTER);
        getContentPane().add(lblGiaBanTuDong);

        txtGiaNhap.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                capNhatGiaBanTuDong();
            }
        });

        // Cột 2
        JLabel lblSoDK = new JLabel("Số đăng ký:");
        lblSoDK.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSoDK.setBounds(390, 80, 200, 25);
        getContentPane().add(lblSoDK);

        txtSoDangKy = new JTextField();
        txtSoDangKy.setBounds(390, 110, 340, 35);
        getContentPane().add(txtSoDangKy);

        JLabel lblDuongDung = new JLabel("Đường dùng:");
        lblDuongDung.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDuongDung.setBounds(390, 160, 200, 25);
        getContentPane().add(lblDuongDung);

        cmbDuongDung = new JComboBox<>(DuongDung.values());
        cmbDuongDung.setBounds(390, 190, 340, 35);
        getContentPane().add(cmbDuongDung);

        JLabel lblKeBan = new JLabel("Kệ bán sản phẩm:");
        lblKeBan.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblKeBan.setBounds(390, 240, 200, 25);
        getContentPane().add(lblKeBan);

        txtKeBanSanPham = new JTextField();
        txtKeBanSanPham.setBounds(390, 270, 340, 35);
        getContentPane().add(txtKeBanSanPham);

        JLabel lblAnh = new JLabel("Hình ảnh (Tên file):");
        lblAnh.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblAnh.setBounds(390, 320, 200, 25);
        getContentPane().add(lblAnh);

        txtHinhAnh = new JTextField();
        txtHinhAnh.setBounds(390, 350, 220, 35);
        getContentPane().add(txtHinhAnh);

        btnChonAnh = new JButton("Chọn ảnh");
        btnChonAnh.setBounds(620, 350, 110, 35);
        btnChonAnh.setBackground(new Color(0x3B82F6));
        btnChonAnh.setForeground(Color.WHITE);
        btnChonAnh.setBorder(null);
        getContentPane().add(btnChonAnh);
        btnChonAnh.addActionListener(this);

        chkHoatDong = new JCheckBox("Đang hoạt động", true);
        chkHoatDong.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        chkHoatDong.setBackground(Color.WHITE);
        chkHoatDong.setBounds(40, 420, 200, 35);
        getContentPane().add(chkHoatDong);

        // Nút
        btnThem = new JButton("Thêm");
        btnThem.setBounds(490, 500, 110, 35);
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThem.setBackground(new Color(0x3B82F6));
        btnThem.setForeground(Color.WHITE);
        btnThem.setBorder(null);
        getContentPane().add(btnThem);

        btnThoat = new JButton("Thoát");
        btnThoat.setBounds(620, 500, 110, 35);
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(0x6B7280));
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setBorder(null);
        getContentPane().add(btnThoat);

        btnThem.addActionListener(this);
        btnThoat.addActionListener(this);
    }

    private void capNhatGiaBanTuDong() {
        try {
            double giaNhap = Double.parseDouble(txtGiaNhap.getText());
            double giaBan = tinhGiaBanToiThieu(giaNhap);
            lblGiaBanTuDong.setText(String.format("%,.0f VND", giaBan));
            lblGiaBanTuDong.setForeground(Color.BLUE);
        } catch (NumberFormatException e) {
            lblGiaBanTuDong.setText("Nhập số hợp lệ");
            lblGiaBanTuDong.setForeground(Color.RED);
        }
    }

    private double tinhGiaBanToiThieu(double giaNhap) {
        double heSo;
        if (giaNhap < 10000) heSo = 1.5;
        else if (giaNhap < 50000) heSo = 1.3;
        else if (giaNhap < 200000) heSo = 1.2;
        else heSo = 1.1;
        return giaNhap * heSo;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src.equals(btnThoat)) {
            dispose();
        } else if (src.equals(btnChonAnh)) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Chọn ảnh sản phẩm");
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                txtHinhAnh.setText(chooser.getSelectedFile().getName());
            }
        } else if (src.equals(btnThem)) {
            handleThemAction();
        }
    }

    private void handleThemAction() {
        try {
            String maSP = String.format("SP%06d", random.nextInt(1000000));
            String ten = txtTenSanPham.getText().trim();
            LoaiSanPham loai = (LoaiSanPham) cmbLoaiSanPham.getSelectedItem();
            String soDK = txtSoDangKy.getText().trim().isEmpty() ? null : txtSoDangKy.getText();
            DuongDung dd = (DuongDung) cmbDuongDung.getSelectedItem();
            double giaNhap = Double.parseDouble(txtGiaNhap.getText());
            String hinhAnh = txtHinhAnh.getText();
            String keBan = txtKeBanSanPham.getText();
            boolean hoatDong = chkHoatDong.isSelected();

            // Tạo đối tượng và tự tính giá bán
            sanPhamMoi = new SanPham(maSP, ten, loai, soDK, dd, giaNhap, hinhAnh, keBan, hoatDong);

            if (sanPhamDAO.createSanPham(sanPhamMoi)) {
                JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công!");
                isCreated = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại! Kiểm tra dữ liệu hoặc mã trùng.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isCreated() {
        return isCreated;
    }

    // ==== Test ====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ThemSanPham_Dialog dlg = new ThemSanPham_Dialog(null);
            dlg.setVisible(true);
        });
    }
}
