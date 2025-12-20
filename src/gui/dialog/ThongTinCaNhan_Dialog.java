package gui.dialog;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import dao.NhanVien_DAO;
import dao.TaiKhoan_DAO;
import entity.NhanVien;
import entity.Session;
import entity.TaiKhoan;

public class ThongTinCaNhan_Dialog extends JDialog {

    // Fields có thể chỉnh sửa
    private JTextField txtTenNhanVien;
    private JTextField txtSoDienThoai;
    private JTextArea txtDiaChi;

    // Fields read-only
    private JLabel lblMaNV;
    private JLabel lblGioiTinh;
    private JLabel lblCaLam;
    private JLabel lblChucVu;
    private JLabel lblNgaySinh;

    // Section đổi mật khẩu
    private JPasswordField txtMatKhauHienTai;
    private JPasswordField txtMatKhauMoi;
    private JPasswordField txtXacNhanMatKhau;

    // Buttons
    private JButton btnLuu;
    private JButton btnThoat;

    // DAO
    private NhanVien_DAO nhanVienDAO;
    private TaiKhoan_DAO taiKhoanDAO;

    // Dữ liệu
    private TaiKhoan taiKhoanDangNhap;
    private NhanVien nhanVien;

    // Callback để refresh UI sau khi cập nhật
    private Runnable onUpdateCallback;

    public ThongTinCaNhan_Dialog(Frame owner) {
        this(owner, null);
    }

    public ThongTinCaNhan_Dialog(Frame owner, Runnable onUpdateCallback) {
        super(owner, "Thông tin cá nhân", true);
        this.onUpdateCallback = onUpdateCallback;
        this.nhanVienDAO = new NhanVien_DAO();
        this.taiKhoanDAO = new TaiKhoan_DAO();
        this.taiKhoanDangNhap = Session.getInstance().getTaiKhoanDangNhap();
        this.nhanVien = taiKhoanDangNhap != null ? taiKhoanDangNhap.getNhanVien() : null;

        initialize();
        populateData();
    }

    private void initialize() {
        setSize(650, 700);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(Color.WHITE);
        getContentPane().setLayout(null);

        // --- Tiêu đề Dialog ---
        JLabel lblTitle = new JLabel("Thông tin cá nhân");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(0, 20, 650, 35);
        getContentPane().add(lblTitle);

        // ========== SECTION: THÔNG TIN CƠ BẢN ==========
        JPanel panelInfo = new JPanel(null);
        panelInfo.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0x1E9086), 1),
                "Thông tin nhân viên",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(0x1E9086)));
        panelInfo.setBackground(Color.WHITE);
        panelInfo.setBounds(30, 70, 580, 280);
        getContentPane().add(panelInfo);

        // Row 1: Mã NV + Tên NV
        JLabel lblMaNVTitle = new JLabel("Mã NV:");
        lblMaNVTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblMaNVTitle.setBounds(20, 30, 80, 25);
        panelInfo.add(lblMaNVTitle);

        lblMaNV = new JLabel("-");
        lblMaNV.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblMaNV.setBounds(100, 30, 150, 25);
        panelInfo.add(lblMaNV);

        JLabel lblTenTitle = new JLabel("Tên NV:");
        lblTenTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTenTitle.setBounds(270, 30, 80, 25);
        panelInfo.add(lblTenTitle);

        txtTenNhanVien = new JTextField();
        txtTenNhanVien.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTenNhanVien.setBounds(350, 25, 200, 30);
        panelInfo.add(txtTenNhanVien);

        // Row 2: Giới tính + Ngày sinh
        JLabel lblGioiTinhTitle = new JLabel("Giới tính:");
        lblGioiTinhTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblGioiTinhTitle.setBounds(20, 70, 80, 25);
        panelInfo.add(lblGioiTinhTitle);

        lblGioiTinh = new JLabel("-");
        lblGioiTinh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblGioiTinh.setBounds(100, 70, 100, 25);
        panelInfo.add(lblGioiTinh);

        JLabel lblNgaySinhTitle = new JLabel("Ngày sinh:");
        lblNgaySinhTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblNgaySinhTitle.setBounds(270, 70, 80, 25);
        panelInfo.add(lblNgaySinhTitle);

        lblNgaySinh = new JLabel("-");
        lblNgaySinh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNgaySinh.setBounds(350, 70, 150, 25);
        panelInfo.add(lblNgaySinh);

        // Row 3: Chức vụ + Ca làm
        JLabel lblChucVuTitle = new JLabel("Chức vụ:");
        lblChucVuTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblChucVuTitle.setBounds(20, 110, 80, 25);
        panelInfo.add(lblChucVuTitle);

        lblChucVu = new JLabel("-");
        lblChucVu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblChucVu.setBounds(100, 110, 120, 25);
        panelInfo.add(lblChucVu);

        JLabel lblCaLamTitle = new JLabel("Ca làm:");
        lblCaLamTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblCaLamTitle.setBounds(270, 110, 80, 25);
        panelInfo.add(lblCaLamTitle);

        lblCaLam = new JLabel("-");
        lblCaLam.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCaLam.setBounds(350, 110, 150, 25);
        panelInfo.add(lblCaLam);

        // Row 4: Số điện thoại
        JLabel lblSdtTitle = new JLabel("Số điện thoại:");
        lblSdtTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSdtTitle.setBounds(20, 150, 100, 25);
        panelInfo.add(lblSdtTitle);

        txtSoDienThoai = new JTextField();
        txtSoDienThoai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSoDienThoai.setBounds(120, 145, 200, 30);
        panelInfo.add(txtSoDienThoai);

        // Row 5: Địa chỉ
        JLabel lblDiaChiTitle = new JLabel("Địa chỉ:");
        lblDiaChiTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDiaChiTitle.setBounds(20, 190, 80, 25);
        panelInfo.add(lblDiaChiTitle);

        txtDiaChi = new JTextArea();
        txtDiaChi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDiaChi.setLineWrap(true);
        txtDiaChi.setWrapStyleWord(true);
        JScrollPane scrollDiaChi = new JScrollPane(txtDiaChi);
        scrollDiaChi.setBounds(120, 185, 430, 70);
        panelInfo.add(scrollDiaChi);

        // ========== SECTION: ĐỔI MẬT KHẨU ==========
        JPanel panelPassword = new JPanel(null);
        panelPassword.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0xE65100), 1),
                "Đổi mật khẩu (bỏ trống nếu không đổi)",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(0xE65100)));
        panelPassword.setBackground(Color.WHITE);
        panelPassword.setBounds(30, 360, 580, 160);
        getContentPane().add(panelPassword);

        JLabel lblMKHienTai = new JLabel("Mật khẩu hiện tại:");
        lblMKHienTai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblMKHienTai.setBounds(20, 30, 130, 25);
        panelPassword.add(lblMKHienTai);

        txtMatKhauHienTai = new JPasswordField();
        txtMatKhauHienTai.setBounds(160, 25, 390, 30);
        panelPassword.add(txtMatKhauHienTai);

        JLabel lblMKMoi = new JLabel("Mật khẩu mới:");
        lblMKMoi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblMKMoi.setBounds(20, 70, 130, 25);
        panelPassword.add(lblMKMoi);

        txtMatKhauMoi = new JPasswordField();
        txtMatKhauMoi.setBounds(160, 65, 390, 30);
        panelPassword.add(txtMatKhauMoi);

        JLabel lblXacNhan = new JLabel("Xác nhận MK mới:");
        lblXacNhan.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblXacNhan.setBounds(20, 110, 130, 25);
        panelPassword.add(lblXacNhan);

        txtXacNhanMatKhau = new JPasswordField();
        txtXacNhanMatKhau.setBounds(160, 105, 390, 30);
        panelPassword.add(txtXacNhanMatKhau);

        // ========== BUTTONS ==========
        btnLuu = new JButton("Lưu thay đổi");
        btnLuu.setBounds(350, 540, 130, 40);
        btnLuu.setBackground(new Color(0x1E9086));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLuu.setFocusPainted(false);
        btnLuu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLuu.addActionListener(e -> onLuuButtonClick());
        getContentPane().add(btnLuu);

        btnThoat = new JButton("Thoát");
        btnThoat.setBounds(490, 540, 100, 40);
        btnThoat.setBackground(new Color(0x6B7280));
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setFocusPainted(false);
        btnThoat.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnThoat.addActionListener(e -> dispose());
        getContentPane().add(btnThoat);
    }

    private void populateData() {
        if (nhanVien == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin nhân viên!", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        lblMaNV.setText(nhanVien.getMaNhanVien());
        txtTenNhanVien.setText(nhanVien.getTenNhanVien());
        lblGioiTinh.setText(nhanVien.isGioiTinh() ? "Nam" : "Nữ");
        lblNgaySinh.setText(nhanVien.getNgaySinh() != null
                ? nhanVien.getNgaySinh().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "-");
        lblChucVu.setText(nhanVien.isQuanLy() ? "Quản lý" : "Nhân viên");
        lblCaLam.setText(nhanVien.getTenCaLam());
        txtSoDienThoai.setText(nhanVien.getSoDienThoai());
        txtDiaChi.setText(nhanVien.getDiaChi());
    }

    private void onLuuButtonClick() {
        try {
            // 1. Validate thông tin cơ bản
            String tenNV = txtTenNhanVien.getText().trim();
            String sdt = txtSoDienThoai.getText().trim();
            String diaChi = txtDiaChi.getText().trim();

            if (tenNV.isEmpty() || sdt.isEmpty() || diaChi.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!",
                        "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validate SĐT
            if (!sdt.matches("^0\\d{9}$")) {
                JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ (10 số, bắt đầu bằng 0)!",
                        "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
                txtSoDienThoai.requestFocus();
                return;
            }

            // 2. Xử lý đổi mật khẩu (nếu có)
            String mkHienTai = new String(txtMatKhauHienTai.getPassword());
            String mkMoi = new String(txtMatKhauMoi.getPassword());
            String mkXacNhan = new String(txtXacNhanMatKhau.getPassword());

            boolean doiMatKhau = !mkHienTai.isEmpty() || !mkMoi.isEmpty() || !mkXacNhan.isEmpty();

            if (doiMatKhau) {
                // Kiểm tra mật khẩu hiện tại
                if (mkHienTai.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu hiện tại!",
                            "Lỗi", JOptionPane.WARNING_MESSAGE);
                    txtMatKhauHienTai.requestFocus();
                    return;
                }

                // So sánh mật khẩu hiện tại
                if (!mkHienTai.equals(taiKhoanDangNhap.getMatKhau())) {
                    JOptionPane.showMessageDialog(this, "Mật khẩu hiện tại không đúng!",
                            "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
                    txtMatKhauHienTai.requestFocus();
                    txtMatKhauHienTai.selectAll();
                    return;
                }

                // Kiểm tra mật khẩu mới
                if (mkMoi.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu mới!",
                            "Lỗi", JOptionPane.WARNING_MESSAGE);
                    txtMatKhauMoi.requestFocus();
                    return;
                }

                // Kiểm tra độ dài và format mật khẩu mới
                if (mkMoi.length() < 8) {
                    JOptionPane.showMessageDialog(this, "Mật khẩu mới phải có ít nhất 8 ký tự!",
                            "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
                    txtMatKhauMoi.requestFocus();
                    return;
                }

                if (!mkMoi.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+$")) {
                    JOptionPane.showMessageDialog(this,
                            "Mật khẩu phải có ít nhất 1 chữ hoa, 1 chữ thường và 1 số!",
                            "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
                    txtMatKhauMoi.requestFocus();
                    return;
                }

                // Kiểm tra xác nhận mật khẩu
                if (!mkMoi.equals(mkXacNhan)) {
                    JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!",
                            "Lỗi", JOptionPane.WARNING_MESSAGE);
                    txtXacNhanMatKhau.requestFocus();
                    txtXacNhanMatKhau.selectAll();
                    return;
                }
            }

            // 3. Cập nhật thông tin nhân viên
            nhanVien.setTenNhanVien(tenNV);
            nhanVien.setSoDienThoai(sdt);
            nhanVien.setDiaChi(diaChi);

            boolean nvOK = nhanVienDAO.capNhatNhanVien(nhanVien);

            // 4. Cập nhật mật khẩu (nếu có)
            boolean mkOK = true;
            if (doiMatKhau) {
                mkOK = taiKhoanDAO.capNhatMatKhau(taiKhoanDangNhap.getMaTaiKhoan(), mkMoi);
                if (mkOK) {
                    // Cập nhật session với mật khẩu mới
                    taiKhoanDangNhap.setMatKhau(mkMoi);
                }
            }

            if (nvOK && mkOK) {
                String message = "Cập nhật thông tin thành công!";
                if (doiMatKhau) {
                    message += "\nMật khẩu đã được thay đổi.";
                }
                JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);

                // Gọi callback để refresh UI
                if (onUpdateCallback != null) {
                    onUpdateCallback.run();
                }

                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại! Vui lòng thử lại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }
}