package gui.dialog;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;
import javax.swing.*;
import com.toedter.calendar.JDateChooser; // Sử dụng thư viện JCalendar

import dao.NhanVien_DAO;
import dao.TaiKhoan_DAO; // Cần dùng để kiểm tra tên đăng nhập
import entity.TaiKhoan;

public class ThemNhanVien_Dialog extends JDialog {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5067882818719547760L;
	private JTextField txtTenNhanVien;
    private JTextField txtTenDangNhap;
    private JTextField txtDiaChi;
    private JTextField txtSoDienThoai;
    private JPasswordField txtMatKhau;
    private JDateChooser ngaySinhDateChooser;
    private JRadioButton radNam, radNu;
    private JCheckBox chkQuanLy;
    private JComboBox<String> cmbCaLam;
    private JButton btnThem;
    private JButton btnThoat;

    // Đối tượng trả về sau khi thêm thành công
    private TaiKhoan taiKhoanMoi = null;
    private TaiKhoan_DAO taiKhoan_DAO; // Thêm DAO để kiểm tra

    public ThemNhanVien_Dialog(Frame owner) {
        super(owner, "Thêm nhân viên", true);
        this.taiKhoan_DAO = new TaiKhoan_DAO(); // Khởi tạo DAO
        initialize();
    }

    private void initialize() {
        setSize(650, 600);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(Color.WHITE);
        getContentPane().setLayout(null);
        
        JLabel lblTitle = new JLabel("Thêm nhân viên");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBounds(225, 20, 200, 35);
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
        radNam.setSelected(true);
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
        chkQuanLy.setBounds(40, 348, 120, 35);
        getContentPane().add(chkQuanLy);
        
        JLabel lblCaLam = new JLabel("Ca làm:");
        lblCaLam.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblCaLam.setBounds(340, 320, 120, 25);
        getContentPane().add(lblCaLam);
        
        // Cập nhật ca làm cho khớp CSDL
        cmbCaLam = new JComboBox<>(new String[]{"SANG", "CHIEU", "TOI"});
        cmbCaLam.setBounds(340, 350, 250, 35);
        getContentPane().add(cmbCaLam);

        JLabel lblMatKhau = new JLabel("Mật khẩu:");
        lblMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblMatKhau.setBounds(40, 400, 120, 25);
        getContentPane().add(lblMatKhau);

        txtMatKhau = new JPasswordField();
        txtMatKhau.setBounds(40, 430, 550, 35);
        txtMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtMatKhau);

        btnThoat = new JButton("Thoát");
        btnThoat.setBounds(480, 500, 110, 35);
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(0x6B7280)); // Màu xám
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setBorder(null);
        btnThoat.setFocusPainted(false);
        getContentPane().add(btnThoat);

        btnThem = new JButton("Thêm");
        btnThem.setBounds(350, 500, 110, 35);
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThem.setBackground(new Color(0x3B82F6)); // Màu xanh
        btnThem.setForeground(Color.WHITE);
        btnThem.setBorder(null);
        getContentPane().add(btnThem);
        
        btnThoat.addActionListener(e -> dispose());
        btnThem.addActionListener(e -> onThemButtonClick());
    }

    private void onThemButtonClick() {
        try {
            // 1. Lấy dữ liệu từ các trường
            @SuppressWarnings("unused")
			String ten = txtTenNhanVien.getText();
            String tenDangNhap = txtTenDangNhap.getText();
            @SuppressWarnings("unused")
			String diaChi = txtDiaChi.getText();
            @SuppressWarnings("unused")
			String sdt = txtSoDienThoai.getText();
            @SuppressWarnings("unused")
			String matKhau = new String(txtMatKhau.getPassword());
            
            Date selectedDate = ngaySinhDateChooser.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày sinh.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
            @SuppressWarnings("unused")
			LocalDate ngaySinh = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            @SuppressWarnings("unused")
			boolean gioiTinh = radNam.isSelected();
            @SuppressWarnings("unused")
			boolean isQuanLy = chkQuanLy.isSelected();
            @SuppressWarnings("unused")
			String caLam = cmbCaLam.getSelectedItem().toString();
            @SuppressWarnings("unused")
			boolean trangThai = true; // Nhân viên mới luôn đang làm

            // 2. Kiểm tra nghiệp vụ (ví dụ: tên đăng nhập tồn tại)
            if (taiKhoan_DAO.kiemTraTenDangNhapTonTai(tenDangNhap)) {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại.", "Lỗi trùng lặp", JOptionPane.ERROR_MESSAGE);
                return;
            }



            NhanVien_DAO nvDAO = new NhanVien_DAO();
            TaiKhoan_DAO tkDAO = new TaiKhoan_DAO();

            @SuppressWarnings("unused")
			String maNV = nvDAO.taoMaNhanVienTuDong();
            @SuppressWarnings("unused")
			String maTK = tkDAO.taoMaTaiKhoanTuDong();

            
            // 6. Đóng dialog
            dispose();
            
        } catch (IllegalArgumentException | DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi không xác định: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Phương thức để NhanVien_QL_GUI gọi để lấy đối tượng mới
     */
    public TaiKhoan getTaiKhoanMoi() {
        return taiKhoanMoi;
    }
}