package gui.dialog;

import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.event.*;

import dao.SanPham_DAO;
import entity.SanPham;
import enums.DuongDung;
import enums.LoaiSanPham;

@SuppressWarnings("serial")
public class CapNhatSanPham_Dialog extends JDialog implements ActionListener {

    private JTextField txtTenSanPham, txtSoDangKy, txtGiaNhap, txtHinhAnh, txtKeBanSanPham;
    private JComboBox<LoaiSanPham> cmbLoaiSanPham;
    private JComboBox<DuongDung> cmbDuongDung;
    private JCheckBox chkHoatDong;
    private JLabel lblGiaBanTuDong, lblMaSP;
    private JButton btnLuu, btnThoat, btnChonAnh;

    private SanPham sanPhamCanCapNhat;
    private final SanPham_DAO sanPhamDAO = new SanPham_DAO();
    private boolean isUpdated = false;

    public CapNhatSanPham_Dialog(Frame owner, SanPham sp) {
        super(owner, "Cập nhật sản phẩm", true);
        this.sanPhamCanCapNhat = sp;
        initialize();
        loadDataToForm();
    }

    // ======================= UI SETUP =======================
    private void initialize() {
        setSize(780, 600);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel lblTitle = new JLabel("Cập nhật sản phẩm");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBounds(260, 20, 280, 35);
        getContentPane().add(lblTitle);

        // --- Mã sản phẩm ---
        JLabel lblMa = new JLabel("Mã sản phẩm:");
        lblMa.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblMa.setBounds(40, 70, 150, 25);
        getContentPane().add(lblMa);

        lblMaSP = new JLabel();
        lblMaSP.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblMaSP.setBounds(160, 70, 200, 25);
        getContentPane().add(lblMaSP);

        // --- Tên sản phẩm ---
        JLabel lblTen = new JLabel("Tên sản phẩm:");
        lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTen.setBounds(40, 110, 200, 25);
        getContentPane().add(lblTen);

        txtTenSanPham = new JTextField();
        txtTenSanPham.setBounds(40, 140, 320, 35);
        getContentPane().add(txtTenSanPham);

        // --- Loại sản phẩm ---
        JLabel lblLoai = new JLabel("Loại sản phẩm:");
        lblLoai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblLoai.setBounds(40, 190, 200, 25);
        getContentPane().add(lblLoai);

        cmbLoaiSanPham = new JComboBox<>(LoaiSanPham.values());
        cmbLoaiSanPham.setBounds(40, 220, 320, 35);
        getContentPane().add(cmbLoaiSanPham);

        // --- Giá nhập ---
        JLabel lblGiaNhap = new JLabel("Giá nhập (VND):");
        lblGiaNhap.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblGiaNhap.setBounds(40, 270, 200, 25);
        getContentPane().add(lblGiaNhap);

        txtGiaNhap = new JTextField();
        txtGiaNhap.setBounds(40, 300, 320, 35);
        getContentPane().add(txtGiaNhap);

        JLabel lblGiaBan = new JLabel("Giá bán (Tự động tính):");
        lblGiaBan.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblGiaBan.setBounds(40, 350, 200, 25);
        getContentPane().add(lblGiaBan);

        lblGiaBanTuDong = new JLabel();
        lblGiaBanTuDong.setBounds(40, 380, 320, 35);
        lblGiaBanTuDong.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblGiaBanTuDong.setBorder(new LineBorder(Color.LIGHT_GRAY));
        lblGiaBanTuDong.setHorizontalAlignment(SwingConstants.CENTER);
        getContentPane().add(lblGiaBanTuDong);

        txtGiaNhap.addActionListener(e -> capNhatGiaBanTuDong());
        txtGiaNhap.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                capNhatGiaBanTuDong();
            }
        });

        // --- Cột phải ---
        JLabel lblSoDK = new JLabel("Số đăng ký:");
        lblSoDK.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSoDK.setBounds(390, 110, 200, 25);
        getContentPane().add(lblSoDK);

        txtSoDangKy = new JTextField();
        txtSoDangKy.setBounds(390, 140, 340, 35);
        getContentPane().add(txtSoDangKy);

        JLabel lblDuongDung = new JLabel("Đường dùng:");
        lblDuongDung.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDuongDung.setBounds(390, 190, 200, 25);
        getContentPane().add(lblDuongDung);

        cmbDuongDung = new JComboBox<>(DuongDung.values());
        cmbDuongDung.setBounds(390, 220, 340, 35);
        getContentPane().add(cmbDuongDung);

        JLabel lblKeBan = new JLabel("Kệ bán sản phẩm:");
        lblKeBan.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblKeBan.setBounds(390, 270, 200, 25);
        getContentPane().add(lblKeBan);

        txtKeBanSanPham = new JTextField();
        txtKeBanSanPham.setBounds(390, 300, 340, 35);
        getContentPane().add(txtKeBanSanPham);

        JLabel lblAnh = new JLabel("Hình ảnh (Tên file):");
        lblAnh.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblAnh.setBounds(390, 350, 200, 25);
        getContentPane().add(lblAnh);

        txtHinhAnh = new JTextField();
        txtHinhAnh.setBounds(390, 380, 220, 35);
        getContentPane().add(txtHinhAnh);

        btnChonAnh = new JButton("Chọn ảnh");
        btnChonAnh.setBounds(620, 380, 110, 35);
        btnChonAnh.setBackground(new Color(0x3B82F6));
        btnChonAnh.setForeground(Color.WHITE);
        btnChonAnh.setBorder(null);
        btnChonAnh.addActionListener(this);
        getContentPane().add(btnChonAnh);

        chkHoatDong = new JCheckBox("Đang hoạt động");
        chkHoatDong.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        chkHoatDong.setBackground(Color.WHITE);
        chkHoatDong.setBounds(40, 440, 200, 35);
        getContentPane().add(chkHoatDong);

        btnLuu = new JButton("Lưu thay đổi");
        btnLuu.setBounds(490, 500, 150, 35);
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLuu.setBackground(new Color(0x10B981));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setBorder(null);
        btnLuu.addActionListener(this);
        getContentPane().add(btnLuu);

        btnThoat = new JButton("Thoát");
        btnThoat.setBounds(650, 500, 80, 35);
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(0x6B7280));
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setBorder(null);
        btnThoat.addActionListener(this);
        getContentPane().add(btnThoat);
    }

    // ======================= LOAD DỮ LIỆU =======================
    private void loadDataToForm() {
        lblMaSP.setText(sanPhamCanCapNhat.getMaSanPham());
        txtTenSanPham.setText(sanPhamCanCapNhat.getTenSanPham());
        cmbLoaiSanPham.setSelectedItem(sanPhamCanCapNhat.getLoaiSanPham());
        txtSoDangKy.setText(sanPhamCanCapNhat.getSoDangKy());
        cmbDuongDung.setSelectedItem(sanPhamCanCapNhat.getDuongDung());
        txtGiaNhap.setText(String.valueOf((long) sanPhamCanCapNhat.getGiaNhap()));
        txtHinhAnh.setText(sanPhamCanCapNhat.getHinhAnh());
        txtKeBanSanPham.setText(sanPhamCanCapNhat.getKeBanSanPham());
        chkHoatDong.setSelected(sanPhamCanCapNhat.isHoatDong());
        capNhatGiaBanTuDong();
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

    // ======================= SỰ KIỆN NÚT =======================
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src.equals(btnThoat)) {
            dispose();
        } else if (src.equals(btnChonAnh)) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Chọn ảnh sản phẩm");
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                txtHinhAnh.setText(file.getName());
            }
        } else if (src.equals(btnLuu)) {
            handleLuuAction();
        }
    }

    private void handleLuuAction() {
        try {
            String ten = txtTenSanPham.getText().trim();
            LoaiSanPham loai = (LoaiSanPham) cmbLoaiSanPham.getSelectedItem();
            String soDK = txtSoDangKy.getText().trim().isEmpty() ? null : txtSoDangKy.getText().trim();
            DuongDung dd = (DuongDung) cmbDuongDung.getSelectedItem();
            double giaNhap = Double.parseDouble(txtGiaNhap.getText().trim());
            String hinhAnh = txtHinhAnh.getText().trim();
            String keBan = txtKeBanSanPham.getText().trim();
            boolean hoatDong = chkHoatDong.isSelected();

            sanPhamCanCapNhat.setTenSanPham(ten);
            sanPhamCanCapNhat.setLoaiSanPham(loai);
            sanPhamCanCapNhat.setSoDangKy(soDK);
            sanPhamCanCapNhat.setDuongDung(dd);
            sanPhamCanCapNhat.setGiaNhap(giaNhap);
            sanPhamCanCapNhat.setHinhAnh(hinhAnh);
            sanPhamCanCapNhat.setKeBanSanPham(keBan);
            sanPhamCanCapNhat.setHoatDong(hoatDong);

            if (sanPhamDAO.capNhatSanPham(sanPhamCanCapNhat)) { // ✅ gọi đúng tên DAO
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                isUpdated = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại! Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    // ======================= TEST =======================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SanPham sp = new SanPham("SP000123", "Paracetamol 500mg", LoaiSanPham.THUOC, "VN-12345",
                    DuongDung.UONG, 1500, "thuoc.png", "Kệ A1", true);
            CapNhatSanPham_Dialog dlg = new CapNhatSanPham_Dialog(null, sp);
            dlg.setVisible(true);
        });
    }
}
