package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import dao.KhuyenMai_DAO;
import entity.KhuyenMai;
import enums.HinhThucKM;

public class ThemKhuyenMai_Dialog extends JDialog {

    private JTextField txtTenKM, txtGiaTri, txtSoLuongToiThieu, txtSoLuongTangThem;
    private JTextField txtDieuKienGiaTri;
    private JLabel lblDieuKien;
    private JRadioButton radKMHoaDon, radKMSanPham;
    private JCheckBox chkTrangThai;
    private JComboBox<String> cmbHinhThuc;
    private JDateChooser dateBatDau, dateKetThuc;
    private JButton btnThem, btnThoat;
    private JPanel pnTangThem;
    private JLabel lblGiaTri;

    private KhuyenMai khuyenMaiMoi = null;

    public ThemKhuyenMai_Dialog(Frame owner) {
        super(owner, "Thêm chương trình khuyến mãi", true);
        initialize();
    }

    private void initialize() {
        setSize(800, 650);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(Color.WHITE);
        getContentPane().setLayout(null);

        // --- Tiêu đề Dialog ---
        JLabel lblTitle = new JLabel("Thêm chương trình khuyến mãi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBounds(200, 20, 400, 35);
        getContentPane().add(lblTitle);

        // --- Tên Khuyến mãi ---
        JLabel lblTen = new JLabel("Tên khuyến mãi:");
        lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTen.setBounds(40, 80, 150, 25);
        getContentPane().add(lblTen);
        txtTenKM = new JTextField();
        txtTenKM.setBounds(40, 110, 320, 35);
        txtTenKM.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtTenKM);

        // --- Loại Khuyến mãi ---
        JLabel lblLoaiKM = new JLabel("Loại khuyến mãi:");
        lblLoaiKM.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblLoaiKM.setBounds(410, 80, 150, 25);
        getContentPane().add(lblLoaiKM);
        radKMHoaDon = new JRadioButton("Khuyến mãi hóa đơn");
        radKMHoaDon.setSelected(true);
        radKMHoaDon.setBounds(410, 110, 170, 35);
        radKMHoaDon.setBackground(Color.WHITE);
        radKMHoaDon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(radKMHoaDon);
        radKMSanPham = new JRadioButton("Khuyến mãi sản phẩm");
        radKMSanPham.setBounds(590, 110, 180, 35);
        radKMSanPham.setBackground(Color.WHITE);
        radKMSanPham.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(radKMSanPham);
        ButtonGroup bgLoaiKM = new ButtonGroup();
        bgLoaiKM.add(radKMHoaDon);
        bgLoaiKM.add(radKMSanPham);
        
        // --- Ngày bắt đầu & kết thúc ---
        JLabel lblNgayBatDau = new JLabel("Ngày bắt đầu:");
        lblNgayBatDau.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblNgayBatDau.setBounds(40, 160, 120, 25);
        getContentPane().add(lblNgayBatDau);
        dateBatDau = new JDateChooser();
        dateBatDau.setBounds(40, 190, 320, 35);
        dateBatDau.setDateFormatString("dd-MM-yyyy");
        dateBatDau.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(dateBatDau);
        
        JLabel lblNgayKetThuc = new JLabel("Ngày kết thúc:");
        lblNgayKetThuc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblNgayKetThuc.setBounds(410, 160, 120, 25);
        getContentPane().add(lblNgayKetThuc);
        dateKetThuc = new JDateChooser();
        dateKetThuc.setBounds(410, 190, 320, 35);
        dateKetThuc.setDateFormatString("dd-MM-yyyy");
        dateKetThuc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(dateKetThuc);
        
        // --- Hình thức & Giá trị ---
        JLabel lblHinhThuc = new JLabel("Hình thức:");
        lblHinhThuc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblHinhThuc.setBounds(40, 240, 120, 25);
        getContentPane().add(lblHinhThuc);
        cmbHinhThuc = new JComboBox<>();
        cmbHinhThuc.setBounds(40, 270, 320, 35);
        cmbHinhThuc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(cmbHinhThuc);

        lblGiaTri = new JLabel("Giá trị (%):");
        lblGiaTri.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblGiaTri.setBounds(410, 240, 120, 25);
        getContentPane().add(lblGiaTri);
        txtGiaTri = new JTextField();
        txtGiaTri.setBounds(410, 270, 320, 35);
        txtGiaTri.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtGiaTri);
        
        // --- Panel cho hình thức "Tặng thêm" ---
        pnTangThem = new JPanel();
        pnTangThem.setLayout(null);
        pnTangThem.setBackground(Color.WHITE);
        pnTangThem.setBounds(410, 240, 360, 80);
        pnTangThem.setVisible(false);
        getContentPane().add(pnTangThem);

        JLabel lblSoLuongToiThieu = new JLabel("Số lượng tối thiểu (Mua):");
        lblSoLuongToiThieu.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSoLuongToiThieu.setBounds(0, 0, 180, 25);
        pnTangThem.add(lblSoLuongToiThieu);
        txtSoLuongToiThieu = new JTextField();
        txtSoLuongToiThieu.setBounds(0, 30, 150, 35);
        txtSoLuongToiThieu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnTangThem.add(txtSoLuongToiThieu);

        JLabel lblSoLuongTangThem = new JLabel("Số lượng tặng thêm:");
        lblSoLuongTangThem.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSoLuongTangThem.setBounds(190, 0, 150, 25);
        pnTangThem.add(lblSoLuongTangThem);
        txtSoLuongTangThem = new JTextField();
        txtSoLuongTangThem.setBounds(190, 30, 150, 35);
        txtSoLuongTangThem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnTangThem.add(txtSoLuongTangThem);

        // --- Điều kiện áp dụng cho hóa đơn ---
        lblDieuKien = new JLabel("Giá trị HĐ tối thiểu (VND):");
        lblDieuKien.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDieuKien.setBounds(40, 320, 200, 25);
        getContentPane().add(lblDieuKien);
        
        txtDieuKienGiaTri = new JTextField("0");
        txtDieuKienGiaTri.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDieuKienGiaTri.setBounds(40, 350, 320, 35);
        getContentPane().add(txtDieuKienGiaTri);
        
        // --- Trạng thái ---
        chkTrangThai = new JCheckBox("Đang áp dụng");
        chkTrangThai.setSelected(true);
        chkTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        chkTrangThai.setBackground(Color.WHITE);
        chkTrangThai.setBounds(40, 450, 150, 30);
        getContentPane().add(chkTrangThai);
        
        // --- Các nút ---
        btnThoat = new JButton("Thoát");
        btnThoat.setBounds(650, 550, 110, 40);
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(0x6B7280));
        btnThoat.setForeground(Color.WHITE);
        btnThoat.addActionListener(e -> dispose());
        getContentPane().add(btnThoat);

        btnThem = new JButton("Thêm");
        btnThem.setBounds(520, 550, 110, 40);
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThem.setBackground(new Color(0x3B82F6));
        btnThem.setForeground(Color.WHITE);
        btnThem.addActionListener(e -> onThemButtonClick());
        getContentPane().add(btnThem);
        
        // --- SỰ KIỆN ---
        cmbHinhThuc.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateUIBasedOnHinhThuc();
            }
        });

        radKMHoaDon.addActionListener(e -> updateHinhThucOptions());
        radKMSanPham.addActionListener(e -> updateHinhThucOptions());
        
        updateHinhThucOptions();
    }
    
    private void updateHinhThucOptions() {
        cmbHinhThuc.removeAllItems();
        
        if (radKMHoaDon.isSelected()) {
            cmbHinhThuc.addItem("Giảm giá phần trăm");
            cmbHinhThuc.addItem("Giảm giá tiền");
            lblDieuKien.setVisible(true);
            txtDieuKienGiaTri.setVisible(true);
        } else {
            cmbHinhThuc.addItem("Giảm giá phần trăm");
            cmbHinhThuc.addItem("Giảm giá tiền");
            cmbHinhThuc.addItem("Tặng thêm");
            lblDieuKien.setVisible(false);
            txtDieuKienGiaTri.setVisible(false);
        }
        updateUIBasedOnHinhThuc();
    }
    
    private void updateUIBasedOnHinhThuc() {
        Object selectedItem = cmbHinhThuc.getSelectedItem();
        String selected = (selectedItem != null) ? selectedItem.toString() : "";

        if ("Tặng thêm".equals(selected)) {
            lblGiaTri.setVisible(false);
            txtGiaTri.setVisible(false);
            pnTangThem.setVisible(true);
        } else {
            pnTangThem.setVisible(false);
            lblGiaTri.setVisible(true);
            txtGiaTri.setVisible(true);
            if ("Giảm giá phần trăm".equals(selected)) {
                lblGiaTri.setText("Giá trị (%):");
            } else {
                lblGiaTri.setText("Giá trị (VND):");
            }
        }
    }

    private void onThemButtonClick() {
        if (!validateForm()) {
            return;
        }

        try {
            KhuyenMai_DAO khuyenMaiDAO = new KhuyenMai_DAO();
            String maKM = khuyenMaiDAO.taoMaKhuyenMai();
            
            String tenKM = txtTenKM.getText().trim();
            boolean laKMHoaDon = radKMHoaDon.isSelected();
            LocalDate ngayBatDau = dateBatDau.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate ngayKetThuc = dateKetThuc.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            String selectedHinhThuc = cmbHinhThuc.getSelectedItem().toString();
            HinhThucKM hinhThuc;
            double giaTri = 0;
            int slToiThieu = 0, slTangThem = 0;

            if ("Giảm giá phần trăm".equals(selectedHinhThuc)) {
                hinhThuc = HinhThucKM.GIAM_GIA_PHAN_TRAM;
                giaTri = Double.parseDouble(txtGiaTri.getText());
            } else if ("Giảm giá tiền".equals(selectedHinhThuc)) {
                hinhThuc = HinhThucKM.GIAM_GIA_TIEN;
                giaTri = Double.parseDouble(txtGiaTri.getText());
            } else {
                hinhThuc = HinhThucKM.TANG_THEM;
                slToiThieu = Integer.parseInt(txtSoLuongToiThieu.getText());
                slTangThem = Integer.parseInt(txtSoLuongTangThem.getText());
            }

            double dieuKien = 0;
            if(laKMHoaDon) {
                dieuKien = Double.parseDouble(txtDieuKienGiaTri.getText());
            }

            boolean trangThai = chkTrangThai.isSelected();

            this.khuyenMaiMoi = new KhuyenMai(maKM, tenKM, ngayBatDau, ngayKetThuc, trangThai, laKMHoaDon, hinhThuc,
                                            giaTri, dieuKien, slToiThieu, slTangThem);
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateForm() {
        if (txtTenKM.getText().trim().isEmpty()) {
            showError("Tên khuyến mãi không được rỗng.", txtTenKM);
            return false;
        }
        if (dateBatDau.getDate() == null || dateKetThuc.getDate() == null) {
            showError("Ngày bắt đầu và kết thúc không được rỗng.", dateBatDau);
            return false;
        }
        LocalDate ngayBatDau = dateBatDau.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate ngayKetThuc = dateKetThuc.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (ngayBatDau.isAfter(ngayKetThuc)) {
            showError("Ngày bắt đầu không được sau ngày kết thúc.", dateBatDau);
            return false;
        }

        String selectedHinhThuc = cmbHinhThuc.getSelectedItem().toString();
        try {
            if ("Giảm giá phần trăm".equals(selectedHinhThuc)) {
                double giaTri = Double.parseDouble(txtGiaTri.getText());
                if (giaTri <= 0 || giaTri > 100) {
                    showError("Giá trị khuyến mãi (%) phải từ 1 đến 100.", txtGiaTri);
                    return false;
                }
            } else if ("Giảm giá tiền".equals(selectedHinhThuc)) {
                double giaTri = Double.parseDouble(txtGiaTri.getText());
                if (giaTri <= 0) {
                    showError("Giá trị khuyến mãi (VND) phải lớn hơn 0.", txtGiaTri);
                    return false;
                }
            } else {
                int slToiThieu = Integer.parseInt(txtSoLuongToiThieu.getText());
                int slTangThem = Integer.parseInt(txtSoLuongTangThem.getText());
                if (slToiThieu <= 0 || slTangThem <= 0) {
                    showError("Số lượng tối thiểu và tặng thêm phải lớn hơn 0.", txtSoLuongToiThieu);
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            showError("Giá trị/Số lượng phải là một con số hợp lệ.", null);
            return false;
        }

        if (radKMHoaDon.isSelected()) {
            try {
                double dieuKien = Double.parseDouble(txtDieuKienGiaTri.getText());
                if (dieuKien < 0) {
                    showError("Giá trị hóa đơn tối thiểu phải lớn hơn hoặc bằng 0.", txtDieuKienGiaTri);
                    return false;
                }
            } catch (NumberFormatException e) {
                showError("Giá trị hóa đơn tối thiểu phải là một con số hợp lệ.", txtDieuKienGiaTri);
                return false;
            }
        }
        
        return true;
    }
    
    private void showError(String message, JComponent c) {
        JOptionPane.showMessageDialog(this, message, "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
        if (c != null) c.requestFocus();
    }

    public KhuyenMai getKhuyenMaiMoi() {
        return khuyenMaiMoi;
    }
}