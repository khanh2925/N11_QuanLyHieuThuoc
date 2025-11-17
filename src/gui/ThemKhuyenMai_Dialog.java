package gui;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.ZoneId;
import javax.swing.*;

import com.toedter.calendar.JDateChooser;

import dao.KhuyenMai_DAO;
import entity.KhuyenMai;
import enums.HinhThucKM;

@SuppressWarnings("serial")
public class ThemKhuyenMai_Dialog extends JDialog {

    private JTextField txtTenKM, txtGiaTri, txtDieuKienGiaTri;
    private JLabel lblDieuKien, lblGiaTri;
    private JRadioButton radKMHoaDon, radKMSanPham;
    private JCheckBox chkTrangThai;
    private JComboBox<String> cmbHinhThuc;
    private JDateChooser dateBatDau, dateKetThuc;
    private JButton btnThem, btnThoat;

    private KhuyenMai khuyenMaiMoi = null;

    private final KhuyenMai_DAO kmDAO = new KhuyenMai_DAO();

    public ThemKhuyenMai_Dialog(Frame owner) {
        super(owner, "Thêm chương trình khuyến mãi", true);
        initUI();
    }

    private void initUI() {
        setSize(880, 720);
        setLocationRelativeTo(getParent());
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Thêm chương trình khuyến mãi", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBounds(0, 20, 880, 35);
        add(lblTitle);

        // Tên KM
        JLabel lblTen = new JLabel("Tên khuyến mãi:");
        lblTen.setBounds(40, 80, 150, 25);
        lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        add(lblTen);

        txtTenKM = new JTextField();
        txtTenKM.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTenKM.setBounds(40, 110, 320, 35);
        add(txtTenKM);

        // Loại KM
        JLabel lblLoai = new JLabel("Loại khuyến mãi:");
        lblLoai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblLoai.setBounds(400, 80, 150, 25);
        add(lblLoai);

        radKMHoaDon = new JRadioButton("Khuyến mãi hóa đơn", true);
        radKMSanPham = new JRadioButton("Khuyến mãi sản phẩm");
        radKMHoaDon.setBackground(Color.WHITE);
        radKMSanPham.setBackground(Color.WHITE);
        ButtonGroup bg = new ButtonGroup();
        bg.add(radKMHoaDon);
        bg.add(radKMSanPham);
        radKMHoaDon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        radKMSanPham.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        radKMHoaDon.setBounds(400, 110, 180, 35);
        radKMSanPham.setBounds(590, 110, 180, 35);
        add(radKMHoaDon);
        add(radKMSanPham);

        // Ngày
        JLabel lblNgayBD = new JLabel("Ngày bắt đầu:");
        lblNgayBD.setBounds(40, 160, 150, 25);
        lblNgayBD.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        add(lblNgayBD);

        dateBatDau = new JDateChooser();
        dateBatDau.setDateFormatString("dd-MM-yyyy");
        dateBatDau.setBounds(40, 190, 320, 35);
        add(dateBatDau);

        JLabel lblNgayKT = new JLabel("Ngày kết thúc:");
        lblNgayKT.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblNgayKT.setBounds(400, 160, 150, 25);
        add(lblNgayKT);

        dateKetThuc = new JDateChooser();
        dateKetThuc.setDateFormatString("dd-MM-yyyy");
        dateKetThuc.setBounds(400, 190, 320, 35);
        add(dateKetThuc);

        // Hình thức
        JLabel lblHinhThuc = new JLabel("Hình thức:");
        lblHinhThuc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblHinhThuc.setBounds(40, 240, 150, 25);
        add(lblHinhThuc);

        cmbHinhThuc = new JComboBox<>();
        cmbHinhThuc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbHinhThuc.setBounds(40, 270, 320, 35);
        add(cmbHinhThuc);

        lblGiaTri = new JLabel("Giá trị (%):");
        lblGiaTri.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblGiaTri.setBounds(400, 240, 150, 25);
        add(lblGiaTri);

        txtGiaTri = new JTextField();
        txtGiaTri.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtGiaTri.setBounds(400, 270, 320, 35);
        add(txtGiaTri);

        // Điều kiện áp dụng
        lblDieuKien = new JLabel("Giá trị HĐ tối thiểu (VND):");
        lblDieuKien.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDieuKien.setBounds(40, 320, 250, 25);
        add(lblDieuKien);

        txtDieuKienGiaTri = new JTextField("0");
        txtDieuKienGiaTri.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDieuKienGiaTri.setBounds(40, 350, 320, 35);
        add(txtDieuKienGiaTri);

        chkTrangThai = new JCheckBox("Đang áp dụng", true);
        chkTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        chkTrangThai.setBackground(Color.WHITE);
        chkTrangThai.setBounds(40, 400, 180, 30);
        add(chkTrangThai);

        // Nút thêm / thoát
        btnThem = new JButton("Thêm");
        btnThoat = new JButton("Thoát");
        btnThem.setBounds(640, 620, 90, 40);
        btnThoat.setBounds(740, 620, 90, 40);
        btnThem.setBackground(new Color(0x3B82F6));
        btnThem.setForeground(Color.WHITE);
        btnThoat.setBackground(new Color(0x6B7280));
        btnThoat.setForeground(Color.WHITE);
        add(btnThem);
        add(btnThoat);

        // ===== SỰ KIỆN =====
        radKMHoaDon.addActionListener(e -> updateHinhThuc());
        radKMSanPham.addActionListener(e -> updateHinhThuc());
        cmbHinhThuc.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateLabelGiaTri();
            }
        });
        btnThoat.addActionListener(e -> dispose());
        btnThem.addActionListener(e -> onThem());

        updateHinhThuc();
    }

    private void updateHinhThuc() {
        cmbHinhThuc.removeAllItems();
        // Cả KM hóa đơn và KM sản phẩm đều chỉ còn 2 loại này
        cmbHinhThuc.addItem("Giảm giá phần trăm");
        cmbHinhThuc.addItem("Giảm giá tiền");

        if (radKMHoaDon.isSelected()) {
            lblDieuKien.setVisible(true);
            txtDieuKienGiaTri.setVisible(true);
        } else {
            lblDieuKien.setVisible(false);
            txtDieuKienGiaTri.setVisible(false);
        }
        updateLabelGiaTri();
    }

    private void updateLabelGiaTri() {
        String selected = (String) cmbHinhThuc.getSelectedItem();
        if ("Giảm giá phần trăm".equals(selected)) {
            lblGiaTri.setText("Giá trị (%):");
        } else {
            lblGiaTri.setText("Giá trị (VND):");
        }
    }

    private void onThem() {
        try {
            if (!validateForm()) return;

            String maKM = kmDAO.taoMaKhuyenMai();
            String ten = txtTenKM.getText().trim();
            boolean laHD = radKMHoaDon.isSelected();
            LocalDate ngayBD = dateBatDau.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate ngayKT = dateKetThuc.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            boolean tt = chkTrangThai.isSelected();

            String selected = (String) cmbHinhThuc.getSelectedItem();
            HinhThucKM hinhThuc;
            double giaTri;

            if ("Giảm giá phần trăm".equals(selected)) {
                hinhThuc = HinhThucKM.GIAM_GIA_PHAN_TRAM;
                giaTri = Double.parseDouble(txtGiaTri.getText());
            } else {
                hinhThuc = HinhThucKM.GIAM_GIA_TIEN;
                giaTri = Double.parseDouble(txtGiaTri.getText());
            }

            double dieuKien = 0;
            if (laHD) {
                dieuKien = Double.parseDouble(txtDieuKienGiaTri.getText());
            }

            KhuyenMai km = new KhuyenMai(
                    maKM,
                    ten,
                    ngayBD,
                    ngayKT,
                    tt,
                    laHD,
                    hinhThuc,
                    giaTri,
                    dieuKien,
                    0
            );

            // KHÔNG insert DB ở đây, chỉ trả object về cho GUI
            this.khuyenMaiMoi = km;
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateForm() {
        if (txtTenKM.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên khuyến mãi không được để trống!");
            return false;
        }
        if (dateBatDau.getDate() == null || dateKetThuc.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày bắt đầu và kết thúc!");
            return false;
        }
        LocalDate bd = dateBatDau.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate kt = dateKetThuc.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (bd.isAfter(kt)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được sau ngày kết thúc!");
            return false;
        }
        if (txtGiaTri.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập giá trị khuyến mãi!");
            return false;
        }
        return true;
    }

    public KhuyenMai getKhuyenMaiMoi() {
        return khuyenMaiMoi;
    }
}
