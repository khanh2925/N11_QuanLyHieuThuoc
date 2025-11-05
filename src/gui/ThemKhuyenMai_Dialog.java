package gui;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import dao.ChiTietKhuyenMaiSanPham_DAO;
import dao.KhuyenMai_DAO;
import dao.SanPham_DAO;
import entity.ChiTietKhuyenMaiSanPham;
import entity.KhuyenMai;
import entity.SanPham;
import enums.HinhThucKM;

@SuppressWarnings("serial")
public class ThemKhuyenMai_Dialog extends JDialog {

    private JTextField txtTenKM, txtGiaTri, txtDieuKienGiaTri;
    private JLabel lblDieuKien, lblGiaTri;
    private JRadioButton radKMHoaDon, radKMSanPham;
    private JCheckBox chkTrangThai;
    private JComboBox<String> cmbHinhThuc;
    private JDateChooser dateBatDau, dateKetThuc;
    private JButton btnThem, btnThoat, btnThemDong, btnXoaDong;
    private JTable tblChiTiet;
    private DefaultTableModel modelCT;

    private JPanel pnChiTietTangThem;

    private KhuyenMai khuyenMaiMoi = null;

    private final KhuyenMai_DAO kmDAO = new KhuyenMai_DAO();
    private final ChiTietKhuyenMaiSanPham_DAO ctkmDAO = new ChiTietKhuyenMaiSanPham_DAO();
    private final SanPham_DAO spDAO = new SanPham_DAO();

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

        // ===== PANEL CHI TIẾT TẶNG THÊM =====
        pnChiTietTangThem = new JPanel(new BorderLayout());
        pnChiTietTangThem.setBorder(new LineBorder(Color.LIGHT_GRAY));
        pnChiTietTangThem.setBackground(Color.WHITE);
        pnChiTietTangThem.setBounds(40, 450, 790, 150);
        add(pnChiTietTangThem);
        pnChiTietTangThem.setVisible(false);

        String[] cols = {"Mã / SĐK / Tên sản phẩm", "SL mua tối thiểu", "SL tặng thêm"};
        modelCT = new DefaultTableModel(cols, 0);
        tblChiTiet = new JTable(modelCT);
        tblChiTiet.setRowHeight(28);
        JScrollPane sp = new JScrollPane(tblChiTiet);
        pnChiTietTangThem.add(sp, BorderLayout.CENTER);

        JPanel pnBtnCT = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnThemDong = new JButton("+ Thêm dòng");
        btnXoaDong = new JButton("Xóa dòng");
        pnBtnCT.add(btnThemDong);
        pnBtnCT.add(btnXoaDong);
        pnChiTietTangThem.add(pnBtnCT, BorderLayout.SOUTH);

        btnThemDong.addActionListener(e -> modelCT.addRow(new Object[]{"", "", ""}));
        btnXoaDong.addActionListener(e -> {
            int r = tblChiTiet.getSelectedRow();
            if (r >= 0) modelCT.removeRow(r);
        });

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
            if (e.getStateChange() == ItemEvent.SELECTED) updateTangThemVisibility();
        });
        btnThoat.addActionListener(e -> dispose());
        btnThem.addActionListener(e -> onThem());

        updateHinhThuc();
    }

    private void updateHinhThuc() {
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
        updateTangThemVisibility();
    }

    private void updateTangThemVisibility() {
        String selected = (String) cmbHinhThuc.getSelectedItem();
        if ("Tặng thêm".equals(selected)) {
            lblGiaTri.setVisible(false);
            txtGiaTri.setVisible(false);
            pnChiTietTangThem.setVisible(true);
        } else {
            lblGiaTri.setVisible(true);
            txtGiaTri.setVisible(true);
            pnChiTietTangThem.setVisible(false);
            lblGiaTri.setText(selected.contains("phần trăm") ? "Giá trị (%):" : "Giá trị (VND):");
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
            double giaTri = 0;
            double dieuKien = 0;

            if ("Giảm giá phần trăm".equals(selected)) {
                hinhThuc = HinhThucKM.GIAM_GIA_PHAN_TRAM;
                giaTri = Double.parseDouble(txtGiaTri.getText());
            } else if ("Giảm giá tiền".equals(selected)) {
                hinhThuc = HinhThucKM.GIAM_GIA_TIEN;
                giaTri = Double.parseDouble(txtGiaTri.getText());
            } else {
                hinhThuc = HinhThucKM.TANG_THEM;
            }

            if (laHD) dieuKien = Double.parseDouble(txtDieuKienGiaTri.getText());

            KhuyenMai km = new KhuyenMai(maKM, ten, ngayBD, ngayKT, tt, laHD, hinhThuc, giaTri, dieuKien, 0);
            if (!kmDAO.themKhuyenMai(km)) {
                JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Nếu là tặng thêm → thêm chi tiết
            if (hinhThuc == HinhThucKM.TANG_THEM) {
                for (int i = 0; i < modelCT.getRowCount(); i++) {
                    String keyword = modelCT.getValueAt(i, 0).toString().trim();
                    String slMuaStr = modelCT.getValueAt(i, 1).toString().trim();
                    String slTangStr = modelCT.getValueAt(i, 2).toString().trim();

                    if (keyword.isEmpty() || slMuaStr.isEmpty() || slTangStr.isEmpty()) continue;

                    List<SanPham> dsSanPham = spDAO.timKiemSanPham(keyword);
                    if (dsSanPham == null || dsSanPham.isEmpty()) {
                        JOptionPane.showMessageDialog(this,
                            "Không tìm thấy sản phẩm: " + keyword,
                            "Lỗi", JOptionPane.WARNING_MESSAGE);
                        continue;
                    }

                    // Nếu có nhiều kết quả → cho chọn 1 cái
                    SanPham sp = null;
                    if (dsSanPham.size() == 1) {
                        sp = dsSanPham.get(0);
                    } else {
                        String[] tenSPs = dsSanPham.stream()
                                .map(SanPham::getTenSanPham)
                                .toArray(String[]::new);
                        String chon = (String) JOptionPane.showInputDialog(
                                this,
                                "Có nhiều sản phẩm trùng khớp: " + keyword + "\nChọn 1 sản phẩm:",
                                "Chọn sản phẩm",
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                tenSPs,
                                tenSPs[0]
                        );
                        if (chon != null) {
                            for (SanPham s : dsSanPham)
                                if (s.getTenSanPham().equals(chon)) { sp = s; break; }
                        }
                    }

                    // Nếu vẫn chưa chọn thì bỏ qua dòng này
                    if (sp == null) continue;

                    int slMua = Integer.parseInt(slMuaStr);
                    int slTang = Integer.parseInt(slTangStr);
                    ChiTietKhuyenMaiSanPham ct = new ChiTietKhuyenMaiSanPham(sp, km, slMua, slTang);
                    ctkmDAO.themChiTietKhuyenMaiSanPham(ct);

                }
            }

            khuyenMaiMoi = km;
            JOptionPane.showMessageDialog(this, "Đã thêm khuyến mãi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
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
        return true;
    }

    public KhuyenMai getKhuyenMaiMoi() {
        return khuyenMaiMoi;
    }
}
