package gui.dialog;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;

import javax.swing.*;

import com.toedter.calendar.JDateChooser;

import dao.NhanVien_DAO;
import dao.TaiKhoan_DAO;
import entity.NhanVien;
import entity.TaiKhoan;

public class CapNhatNhanVien_Dialog extends JDialog {

    private JTextField txtTenNhanVien;
    private JTextField txtTenDangNhap;
    private JTextField txtDiaChi;
    private JTextField txtSoDienThoai;
    private JPasswordField txtMatKhau;
    private JDateChooser ngaySinhDateChooser;
    private JRadioButton radNam, radNu;
    private JCheckBox chkQuanLy;
    private JComboBox<String> cmbCaLam;
    private JComboBox<String> cmbTrangThai;
    private JButton btnLuu, btnThoat;

    private TaiKhoan taiKhoanCanCapNhat;
    private boolean isUpdateSuccess = false;

    private TaiKhoan_DAO taiKhoan_DAO;
    private NhanVien_DAO nhanVien_DAO;

    public CapNhatNhanVien_Dialog(Frame owner, TaiKhoan tkToUpdate) {
        super(owner, "Cập nhật thông tin nhân viên", true);
        this.taiKhoanCanCapNhat = tkToUpdate;
        this.taiKhoan_DAO = new TaiKhoan_DAO();
        this.nhanVien_DAO = new NhanVien_DAO();
        initialize();
        populateData();
    }

    private void initialize() {
        setSize(650, 650);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel lblTitle = new JLabel("Cập nhật thông tin nhân viên");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBounds(180, 20, 350, 35);
        add(lblTitle);

        JLabel lblTen = new JLabel("Tên nhân viên:");
        lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTen.setBounds(40, 80, 150, 25);
        add(lblTen);
        txtTenNhanVien = new JTextField();
        txtTenNhanVien.setBounds(40, 110, 250, 35);
        add(txtTenNhanVien);

        JLabel lblTenDangNhap = new JLabel("Tên đăng nhập:");
        lblTenDangNhap.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTenDangNhap.setBounds(340, 80, 200, 25);
        add(lblTenDangNhap);
        txtTenDangNhap = new JTextField();
        txtTenDangNhap.setBounds(340, 110, 250, 35);
        add(txtTenDangNhap);

        JLabel lblDiaChi = new JLabel("Địa chỉ:");
        lblDiaChi.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDiaChi.setBounds(40, 160, 120, 25);
        add(lblDiaChi);
        txtDiaChi = new JTextField();
        txtDiaChi.setBounds(40, 190, 250, 35);
        add(txtDiaChi);

        JLabel lblSdt = new JLabel("Số điện thoại:");
        lblSdt.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSdt.setBounds(340, 160, 120, 25);
        add(lblSdt);
        txtSoDienThoai = new JTextField();
        txtSoDienThoai.setBounds(340, 190, 250, 35);
        add(txtSoDienThoai);

        JLabel lblNgaySinh = new JLabel("Ngày sinh:");
        lblNgaySinh.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblNgaySinh.setBounds(40, 240, 120, 25);
        add(lblNgaySinh);
        ngaySinhDateChooser = new JDateChooser();
        ngaySinhDateChooser.setBounds(40, 270, 250, 35);
        ngaySinhDateChooser.setDateFormatString("dd-MM-yyyy");
        add(ngaySinhDateChooser);

        JLabel lblGioiTinh = new JLabel("Giới tính:");
        lblGioiTinh.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblGioiTinh.setBounds(340, 240, 120, 25);
        add(lblGioiTinh);
        radNam = new JRadioButton("Nam");
        radNam.setBackground(Color.WHITE);
        radNam.setBounds(340, 270, 80, 35);
        add(radNam);
        radNu = new JRadioButton("Nữ");
        radNu.setBackground(Color.WHITE);
        radNu.setBounds(430, 270, 80, 35);
        add(radNu);
        ButtonGroup bg = new ButtonGroup();
        bg.add(radNam);
        bg.add(radNu);

        chkQuanLy = new JCheckBox("Là quản lý");
        chkQuanLy.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        chkQuanLy.setBackground(Color.WHITE);
        chkQuanLy.setBounds(40, 320, 120, 35);
        add(chkQuanLy);

        JLabel lblTrangThai = new JLabel("Trạng thái:");
        lblTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTrangThai.setBounds(180, 320, 120, 25);
        add(lblTrangThai);
        cmbTrangThai = new JComboBox<>(new String[]{"Đang làm", "Đã nghỉ"});
        cmbTrangThai.setBounds(180, 350, 140, 35);
        add(cmbTrangThai);

        JLabel lblCaLam = new JLabel("Ca làm:");
        lblCaLam.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblCaLam.setBounds(340, 320, 120, 25);
        add(lblCaLam);
        cmbCaLam = new JComboBox<>(new String[]{"1 - Sáng", "2 - Chiều", "3 - Tối"});
        cmbCaLam.setBounds(340, 350, 250, 35);
        add(cmbCaLam);

        JLabel lblMatKhau = new JLabel("Mật khẩu (để trống nếu không đổi):");
        lblMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblMatKhau.setBounds(40, 410, 300, 25);
        add(lblMatKhau);
        txtMatKhau = new JPasswordField();
        txtMatKhau.setBounds(40, 440, 550, 35);
        add(txtMatKhau);

        btnLuu = new JButton("Lưu thay đổi");
        btnLuu.setBounds(320, 520, 140, 40);
        btnLuu.setBackground(new Color(0x3B82F6));
        btnLuu.setForeground(Color.WHITE);
        add(btnLuu);

        btnThoat = new JButton("Thoát");
        btnThoat.setBounds(480, 520, 110, 40);
        btnThoat.setBackground(new Color(0x6B7280));
        btnThoat.setForeground(Color.WHITE);
        add(btnThoat);

        btnThoat.addActionListener(e -> dispose());
        btnLuu.addActionListener(e -> onLuuButtonClick());
    }

    private void populateData() {
        NhanVien nv = taiKhoanCanCapNhat.getNhanVien();
        if (nv == null) return;

        txtTenNhanVien.setText(nv.getTenNhanVien());
        txtTenDangNhap.setText(taiKhoanCanCapNhat.getTenDangNhap());
        txtDiaChi.setText(nv.getDiaChi());
        txtSoDienThoai.setText(nv.getSoDienThoai());

        LocalDate ngaySinh = nv.getNgaySinh();
        if (ngaySinh != null)
            ngaySinhDateChooser.setDate(Date.from(ngaySinh.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        if (nv.isGioiTinh()) radNam.setSelected(true); else radNu.setSelected(true);
        chkQuanLy.setSelected(nv.isQuanLy());
        cmbCaLam.setSelectedIndex(Math.max(0, nv.getCaLam() - 1));
        cmbTrangThai.setSelectedItem(nv.isTrangThai() ? "Đang làm" : "Đã nghỉ");
    }

    private void onLuuButtonClick() {
        try {
            NhanVien nv = taiKhoanCanCapNhat.getNhanVien();
            if (nv == null) return;

            String ten = txtTenNhanVien.getText().trim();
            String tenDangNhap = txtTenDangNhap.getText().trim();
            String diaChi = txtDiaChi.getText().trim();
            String sdt = txtSoDienThoai.getText().trim();
            String matKhau = new String(txtMatKhau.getPassword());

            if (ten.isEmpty() || diaChi.isEmpty() || sdt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.", "Thiếu dữ liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Date date = ngaySinhDateChooser.getDate();
            if (date == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày sinh.", "Thiếu dữ liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
            LocalDate ngaySinh = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (ngaySinh.isAfter(LocalDate.now().minusYears(18))) {
                JOptionPane.showMessageDialog(this, "Nhân viên phải đủ 18 tuổi.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean gioiTinh = radNam.isSelected();
            boolean quanLy = chkQuanLy.isSelected();
            int caLam = cmbCaLam.getSelectedIndex() + 1;
            boolean trangThai = cmbTrangThai.getSelectedItem().toString().equals("Đang làm");

            // Kiểm tra trùng tên đăng nhập
            if (!taiKhoanCanCapNhat.getTenDangNhap().equals(tenDangNhap)
                    && taiKhoan_DAO.kiemTraTenDangNhapTonTai(tenDangNhap)) {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Cập nhật entity
            nv.setTenNhanVien(ten);
            nv.setDiaChi(diaChi);
            nv.setSoDienThoai(sdt);
            nv.setNgaySinh(ngaySinh);
            nv.setGioiTinh(gioiTinh);
            nv.setQuanLy(quanLy);
            nv.setCaLam(caLam);
            nv.setTrangThai(trangThai);

            taiKhoanCanCapNhat.setTenDangNhap(tenDangNhap);
            if (!matKhau.isEmpty()) taiKhoanCanCapNhat.setMatKhau(matKhau);

            // Cập nhật DB
            boolean nvOK = nhanVien_DAO.capNhatNhanVien(nv);
            boolean tkOK = taiKhoan_DAO.capNhatTaiKhoan(taiKhoanCanCapNhat);

            if (nvOK && tkOK) {
                isUpdateSuccess = true;
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại! Kiểm tra lại dữ liệu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IllegalArgumentException | DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isUpdateSuccess() {
        return isUpdateSuccess;
    }
}
