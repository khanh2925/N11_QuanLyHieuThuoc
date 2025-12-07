package gui.dialog;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;

import javax.swing.*;
import javax.swing.border.LineBorder;

import com.toedter.calendar.JDateChooser; // THAY ĐỔI 1: Import class mới từ thư viện JCalendar

import entity.KhachHang;


public class ThemKhachHang_Dialog extends JDialog implements ActionListener {

    private JTextField txtTenKhachHang;
    private JTextField txtSoDienThoai;
    private JRadioButton radNam, radNu;
    private JDateChooser ngaySinhDateChooser;
    private JButton btnThem;
    private JButton btnThoat;

    private KhachHang khachHangMoi = null;

    public ThemKhachHang_Dialog(Frame owner) {
        super(owner, "Thêm khách hàng", true);
        initialize();
    }

    private void initialize() {
        setSize(650, 400);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(Color.WHITE);
        getContentPane().setLayout(null);


        
        // --- Tiêu đề Dialog ---
        JLabel lblTitle = new JLabel("Thêm khách hàng");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBounds(225, 20, 200, 35);
        getContentPane().add(lblTitle);

        // --- Tên khách hàng ---
        JLabel lblTen = new JLabel("Tên khách hàng:");
        lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTen.setBounds(40, 80, 120, 25);
        getContentPane().add(lblTen);

        txtTenKhachHang = new JTextField();
        txtTenKhachHang.setBounds(40, 110, 250, 35);
        txtTenKhachHang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtTenKhachHang);



        // --- Số điện thoại ---
        JLabel lblSdt = new JLabel("Số điện thoại:");
        lblSdt.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSdt.setBounds(340, 80, 120, 25);
        getContentPane().add(lblSdt);

        txtSoDienThoai = new JTextField();
        txtSoDienThoai.setBounds(340, 110, 250, 35);
        txtSoDienThoai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtSoDienThoai);
        
        // --- Ngày sinh ---
        JLabel lblNgaySinh = new JLabel("Ngày sinh:");
        lblNgaySinh.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblNgaySinh.setBounds(40, 155, 120, 25);
        getContentPane().add(lblNgaySinh);

        // THAY ĐỔI 3: Khởi tạo JDateChooser và thiết lập định dạng ngày tháng
        ngaySinhDateChooser = new JDateChooser(); // Sử dụng constructor của JDateChooser
        ngaySinhDateChooser.setBounds(40, 200, 250, 35);
        ngaySinhDateChooser.setDateFormatString("dd-MM-yyyy"); // Đặt định dạng hiển thị
        ngaySinhDateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(ngaySinhDateChooser);


        // --- Giới tính ---
        JLabel lblGioiTinh = new JLabel("Giới tính:");
        lblGioiTinh.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblGioiTinh.setBounds(340, 155, 120, 25);
        getContentPane().add(lblGioiTinh);

        radNam = new JRadioButton("Nam");
        radNam.setSelected(true);
        radNam.setBounds(345, 200, 80, 35);
        radNam.setBackground(Color.WHITE);
        radNam.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(radNam);
        
        radNu = new JRadioButton("Nữ");
        radNu.setBounds(427, 200, 80, 35);
        radNu.setBackground(Color.WHITE);
        radNu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(radNu);
        
        ButtonGroup bgGioiTinh = new ButtonGroup();
        bgGioiTinh.add(radNam);
        bgGioiTinh.add(radNu);
        

        

        // --- Các nút ---
        btnThoat = new JButton("Thoát");
        btnThoat.setBounds(467, 275, 110, 35);
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(0x3B82F6));
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setBorder(null);
        btnThoat.setFocusPainted(false);
        getContentPane().add(btnThoat);

        btnThem = new JButton("Thêm");
        btnThem.setBounds(340, 275, 110, 35);
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThem.setBackground(Color.LIGHT_GRAY);
        btnThem.setBorder(new LineBorder(Color.GRAY));
        getContentPane().add(btnThem);
        
        // --- Thêm sự kiện cho các nút ---

        btnThoat.addActionListener(this);
        btnThem.addActionListener(this);
    }
    //String maKH = String.format("KH-%04d", (int)(System.currentTimeMillis() % 10000));

    private void themKH() {
        try {
        	if(!isvalidForm()) return;
        	
        	String maKH = String.format("KH-%04d", (int)(System.currentTimeMillis() % 10000));
            String ten = txtTenKhachHang.getText();
            boolean gioiTinh = radNam.isSelected();
            String sdt = txtSoDienThoai.getText();
            Date selectedDate = ngaySinhDateChooser.getDate();
            LocalDate ngaySinh = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                      
            this.khachHangMoi = new KhachHang(maKH, ten, gioiTinh, sdt, ngaySinh);           
            dispose();
            
        } catch (IllegalArgumentException | DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi không xác định: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
 // --- Helper: hiển thị lỗi & focus vào control ---
    private void showError(String message, JComponent c) {
        JOptionPane.showMessageDialog(this, message, "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
        if (c != null) c.requestFocus();
    }





 // === HÀM VALIDATE CHÍNH ===
    private boolean isvalidForm() {
    	String maKH = String.format("KH-%04d", (int)(System.currentTimeMillis() % 10000));
        String ten = txtTenKhachHang.getText() != null ? txtTenKhachHang.getText().trim() : "";
        if (ten.isEmpty()) {
            showError("Tên khách hàng không được rỗng.", txtTenKhachHang);
            return false;
        }
        if (ten.length() > 100) {
            showError("Tên khách hàng không được vượt quá 100 ký tự.", txtTenKhachHang);
            return false;
        }
        if (!ten.matches("^([A-ZÀ-Ỵ][a-zà-ỹ]*)(\\s[A-ZÀ-Ỵ][a-zà-ỹ]*)*$")) {
       	 	showError("Tên khách hàng phải viết hoa chữ cái đầu", txtTenKhachHang);
            return false;
		}

        String sdt = txtSoDienThoai.getText() != null ? txtSoDienThoai.getText().trim() : "";
        if (!sdt.matches("^0\\d{9}$")) {
            showError("Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0.", txtSoDienThoai);
            return false;
        }     
        

        Date selectedDate = ngaySinhDateChooser.getDate();
        if (selectedDate == null) {
            showError("Vui lòng chọn ngày sinh.", ngaySinhDateChooser);
            return false;
        }
        LocalDate ngaySinh = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (ngaySinh.isAfter(LocalDate.now().minusYears(16))) {
            showError("Khách hàng phải từ 16 tuổi trở lên.", ngaySinhDateChooser);
            return false;
        }

        return true;
    }



    public KhachHang getKhachHangMoi() {
        return khachHangMoi;
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if(o.equals(btnThem)) {
			themKH();;
			return;
		}
		
		if(o.equals(btnThoat)) {
			dispose();
			return;
		}

		
	}
}