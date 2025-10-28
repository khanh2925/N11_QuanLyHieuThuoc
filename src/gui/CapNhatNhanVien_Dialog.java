package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;

import javax.swing.*;
import javax.swing.border.LineBorder;

import com.toedter.calendar.JDateChooser;
import dao.TaiKhoan_DAO; // Cần dùng để kiểm tra tên đăng nhập
import entity.NhanVien;
import entity.TaiKhoan; // Import TaiKhoan

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
    private JComboBox<String> cmbTrangThai; // Thêm ComboBox trạng thái
    private JButton btnLuu;
    private JButton btnThoat;

    // Thay đổi 1: Nhận vào TaiKhoan thay vì NhanVien
    private TaiKhoan taiKhoanCanCapNhat;
    private boolean isUpdateSuccess = false;
    private TaiKhoan_DAO taiKhoan_DAO;

    // Thay đổi 2: Sửa constructor
    public CapNhatNhanVien_Dialog(Frame owner, TaiKhoan tkToUpdate) {
        super(owner, "Cập nhật thông tin nhân viên", true);
        this.taiKhoanCanCapNhat = tkToUpdate;
        this.taiKhoan_DAO = new TaiKhoan_DAO();
        initialize();
        populateData(); // Nạp dữ liệu
    }

    private void initialize() {
        setSize(650, 650); // Tăng chiều cao
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel lblTitle = new JLabel("Cập nhật thông tin");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBounds(225, 20, 250, 35);
        getContentPane().add(lblTitle);

        JLabel lblTen = new JLabel("Tên nhân viên:");
        lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTen.setBounds(40, 80, 120, 25);
        getContentPane().add(lblTen);
        txtTenNhanVien = new JTextField();
        txtTenNhanVien.setBounds(40, 110, 250, 35);
        txtTenNhanVien.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtTenNhanVien);

        JLabel lblTenDangNhap = new JLabel("Tên đăng nhập:");
        lblTenDangNhap.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTenDangNhap.setBounds(340, 80, 200, 25);
        getContentPane().add(lblTenDangNhap);
        txtTenDangNhap = new JTextField();
        txtTenDangNhap.setBounds(340, 110, 250, 35);
        txtTenDangNhap.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtTenDangNhap);

        JLabel lblDiaChi = new JLabel("Địa chỉ:");
        lblDiaChi.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDiaChi.setBounds(40, 160, 120, 25);
        getContentPane().add(lblDiaChi);
        txtDiaChi = new JTextField();
        txtDiaChi.setBounds(40, 190, 250, 35);
        txtDiaChi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtDiaChi);

        JLabel lblSdt = new JLabel("Số điện thoại:");
        lblSdt.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSdt.setBounds(340, 160, 120, 25);
        getContentPane().add(lblSdt);
        txtSoDienThoai = new JTextField();
        txtSoDienThoai.setBounds(340, 190, 250, 35);
        txtSoDienThoai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtSoDienThoai);
        
        JLabel lblNgaySinh = new JLabel("Ngày sinh:");
        lblNgaySinh.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblNgaySinh.setBounds(40, 240, 120, 25);
        getContentPane().add(lblNgaySinh);

        ngaySinhDateChooser = new JDateChooser();
        ngaySinhDateChooser.setBounds(40, 270, 250, 35);
        ngaySinhDateChooser.setDateFormatString("dd-MM-yyyy");
        ngaySinhDateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(ngaySinhDateChooser);
      
        JLabel lblGioiTinh = new JLabel("Giới tính:");
        lblGioiTinh.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblGioiTinh.setBounds(340, 240, 120, 25);
        getContentPane().add(lblGioiTinh);
        radNam = new JRadioButton("Nam");
        radNam.setBounds(340, 270, 80, 35);
        radNam.setBackground(Color.WHITE);
        radNam.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(radNam);
        radNu = new JRadioButton("Nữ");
        radNu.setBounds(430, 270, 80, 35);
        radNu.setBackground(Color.WHITE);
        radNu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(radNu);
        ButtonGroup bgGioiTinh = new ButtonGroup();
        bgGioiTinh.add(radNam);
        bgGioiTinh.add(radNu);
        
        chkQuanLy = new JCheckBox("Là quản lý");
        chkQuanLy.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        chkQuanLy.setBackground(Color.WHITE);
        chkQuanLy.setBounds(40, 320, 120, 35);
        getContentPane().add(chkQuanLy);
        
        JLabel lblTrangThai = new JLabel("Trạng thái:");
        lblTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTrangThai.setBounds(180, 320, 120, 25);
        getContentPane().add(lblTrangThai);
        cmbTrangThai = new JComboBox<>(new String[]{"Đang làm", "Đã nghỉ"});
        cmbTrangThai.setBounds(180, 350, 140, 35);
        getContentPane().add(cmbTrangThai);

        JLabel lblCaLam = new JLabel("Ca làm:");
        lblCaLam.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblCaLam.setBounds(340, 320, 120, 25);
        getContentPane().add(lblCaLam);
        // Cập nhật ca làm cho khớp CSDL
        cmbCaLam = new JComboBox<>(new String[]{"SANG", "CHIEU", "TOI"});
        cmbCaLam.setBounds(340, 350, 250, 35);
        getContentPane().add(cmbCaLam);

        JLabel lblMatKhau = new JLabel("Mật khẩu (để trống nếu không đổi):");
        lblMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblMatKhau.setBounds(40, 410, 300, 25);
        getContentPane().add(lblMatKhau);
        txtMatKhau = new JPasswordField();
        txtMatKhau.setBounds(40, 440, 550, 35);
        txtMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtMatKhau);

        btnThoat = new JButton("Thoát");
        btnThoat.setBounds(480, 520, 110, 40);
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(0x6B7280));
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setBorder(null);
        btnThoat.setFocusPainted(false);
        getContentPane().add(btnThoat);

        btnLuu = new JButton("Lưu thay đổi");
        btnLuu.setBounds(320, 520, 140, 40);
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLuu.setBackground(new Color(0x3B82F6));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setBorder(null);
        getContentPane().add(btnLuu);

        btnThoat.addActionListener(e -> dispose());
        btnLuu.addActionListener(e -> onLuuButtonClick());
    }
    
    /**
     * Thay đổi 3: Nạp dữ liệu từ TaiKhoan và NhanVien
     */
    private void populateData() {
        NhanVien nv = taiKhoanCanCapNhat.getNhanVien();
        
        txtTenNhanVien.setText(nv.getTenNhanVien());
        txtTenDangNhap.setText(taiKhoanCanCapNhat.getTenDangNhap());
        txtDiaChi.setText(nv.getDiaChi());
        txtSoDienThoai.setText(nv.getSoDienThoai());
        
        LocalDate ngaySinhLocalDate = nv.getNgaySinh();
        if (ngaySinhLocalDate != null) {
            Date ngaySinhDate = Date.from(ngaySinhLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            ngaySinhDateChooser.setDate(ngaySinhDate);
        }

        if (nv.isGioiTinh()) {
            radNam.setSelected(true);
        } else {
            radNu.setSelected(true);
        }

        chkQuanLy.setSelected(nv.isQuanLy());
        cmbCaLam.setSelectedItem(nv.getCaLam()); // CSDL lưu "SANG", "CHIEU", "TOI"
        cmbTrangThai.setSelectedItem(nv.isTrangThai() ? "Đang làm" : "Đã nghỉ");
    }

    /**
     * Thay đổi 4: Cập nhật dữ liệu vào đối tượng TaiKhoan và NhanVien
     */
    private void onLuuButtonClick() {
        try {
            NhanVien nv = taiKhoanCanCapNhat.getNhanVien(); // Lấy nhân viên để cập nhật
            
            String ten = txtTenNhanVien.getText();
            String tenDangNhap = txtTenDangNhap.getText();
            String diaChi = txtDiaChi.getText();
            String sdt = txtSoDienThoai.getText();
            String matKhau = new String(txtMatKhau.getPassword());
            
            Date selectedDate = ngaySinhDateChooser.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày sinh.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
            LocalDate ngaySinh = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            if (ngaySinh.isAfter(LocalDate.now().minusYears(18))) {
                JOptionPane.showMessageDialog(this, "Nhân viên phải đủ 18 tuổi.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean gioiTinh = radNam.isSelected();
            boolean isQuanLy = chkQuanLy.isSelected();
            String caLam = cmbCaLam.getSelectedItem().toString();
            boolean trangThai = cmbTrangThai.getSelectedItem().toString().equals("Đang làm");
            
            // Kiểm tra nếu tên đăng nhập thay đổi VÀ đã tồn tại
            String tenDangNhapCu = taiKhoanCanCapNhat.getTenDangNhap();
            if (!tenDangNhapCu.equals(tenDangNhap) && taiKhoan_DAO.isUsernameExists(tenDangNhap)) {
                 JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại.", "Lỗi trùng lặp", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Cập nhật đối tượng NhanVien
            nv.setTenNhanVien(ten);
            nv.setDiaChi(diaChi);
            nv.setSoDienThoai(sdt);
            nv.setNgaySinh(ngaySinh);
            nv.setGioiTinh(gioiTinh);
            nv.setQuanLy(isQuanLy);
            nv.setCaLam(caLam);
            nv.setTrangThai(trangThai);

            // Cập nhật đối tượng TaiKhoan
            taiKhoanCanCapNhat.setTenDangNhap(tenDangNhap);
            if (!matKhau.isEmpty()) { // Chỉ cập nhật nếu người dùng nhập mật khẩu mới
                taiKhoanCanCapNhat.setMatKhau(matKhau);
            }
            
            isUpdateSuccess = true;
            dispose();
            
        } catch (IllegalArgumentException | DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isUpdateSuccess() {
        return isUpdateSuccess;
    }
}
