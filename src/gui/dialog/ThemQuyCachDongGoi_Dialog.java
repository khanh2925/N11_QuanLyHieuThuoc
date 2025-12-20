package gui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.util.List;
import javax.swing.*;
import dao.QuyCachDongGoi_DAO;
import entity.DonViTinh;
import entity.QuyCachDongGoi;
import entity.SanPham;

public class ThemQuyCachDongGoi_Dialog extends JDialog {

    private JComboBox<SanPham> cmbSanPham;
    private JComboBox<DonViTinh> cmbDonViTinh;
    private JTextField txtHeSoQuyDoi;
    private JTextField txtTiLeGiam;
    private JCheckBox chkDonViGoc;
    private JButton btnThem, btnThoat;

    private QuyCachDongGoi quyCachMoi = null;
    private final QuyCachDongGoi_DAO qcdg_DAO;

    public ThemQuyCachDongGoi_Dialog(Frame owner, List<SanPham> dsSanPham, List<DonViTinh> dsDonViTinh) {
        super(owner, "Thêm Quy Cách Đóng Gói", true);
        this.qcdg_DAO = new QuyCachDongGoi_DAO();
        initialize(dsSanPham, dsDonViTinh);
    }

    private void initialize(List<SanPham> dsSanPham, List<DonViTinh> dsDonViTinh) {
        setSize(550, 500);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(Color.WHITE);
        getContentPane().setLayout(null);

        JLabel lblTitle = new JLabel("Thêm Quy Cách Đóng Gói Mới");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBounds(100, 20, 350, 35);
        getContentPane().add(lblTitle);

        JLabel lblSanPham = new JLabel("Sản phẩm:");
        lblSanPham.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSanPham.setBounds(40, 80, 150, 25);
        getContentPane().add(lblSanPham);

        DefaultComboBoxModel<SanPham> spModel = new DefaultComboBoxModel<>();
        dsSanPham.forEach(spModel::addElement);
        cmbSanPham = new JComboBox<>(spModel);
        cmbSanPham.setBounds(40, 110, 450, 35);
        cmbSanPham.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbSanPham.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SanPham) {
                    setText(((SanPham) value).getTenSanPham());
                }
                return this;
            }
        });
        getContentPane().add(cmbSanPham);

        JLabel lblDonViTinh = new JLabel("Đơn vị tính:");
        lblDonViTinh.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDonViTinh.setBounds(40, 160, 150, 25);
        getContentPane().add(lblDonViTinh);

        DefaultComboBoxModel<DonViTinh> dvtModel = new DefaultComboBoxModel<>();
        dsDonViTinh.forEach(dvtModel::addElement);
        cmbDonViTinh = new JComboBox<>(dvtModel);
        cmbDonViTinh.setBounds(40, 190, 200, 35);
        cmbDonViTinh.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbDonViTinh.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof DonViTinh) {
                    setText(((DonViTinh) value).getTenDonViTinh());
                }
                return this;
            }
        });
        getContentPane().add(cmbDonViTinh);

        JLabel lblHeSo = new JLabel("Hệ số quy đổi:");
        lblHeSo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblHeSo.setBounds(290, 160, 150, 25);
        getContentPane().add(lblHeSo);
        txtHeSoQuyDoi = new JTextField("1");
        txtHeSoQuyDoi.setBounds(290, 190, 200, 35);
        txtHeSoQuyDoi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtHeSoQuyDoi);

        JLabel lblTiLeGiam = new JLabel("Tỉ lệ giảm giá (%):");
        lblTiLeGiam.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTiLeGiam.setBounds(40, 240, 150, 25);
        getContentPane().add(lblTiLeGiam);
        txtTiLeGiam = new JTextField("0");
        txtTiLeGiam.setBounds(40, 270, 200, 35);
        txtTiLeGiam.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtTiLeGiam);

        chkDonViGoc = new JCheckBox("Đây là đơn vị gốc");
        chkDonViGoc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        chkDonViGoc.setBackground(Color.WHITE);
        chkDonViGoc.setBounds(40, 330, 200, 30);
        chkDonViGoc.setSelected(true);
        getContentPane().add(chkDonViGoc);

        updateHeSoFieldState(true);

        chkDonViGoc.addItemListener(e -> updateHeSoFieldState(e.getStateChange() == ItemEvent.SELECTED));

        btnThoat = new JButton("Thoát");
        btnThoat.setBounds(400, 400, 110, 40);
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(0x6B7280));
        btnThoat.setForeground(Color.WHITE);
        btnThoat.addActionListener(e -> dispose());
        getContentPane().add(btnThoat);

        btnThem = new JButton("Thêm");
        btnThem.setBounds(270, 400, 110, 40);
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThem.setBackground(new Color(0x3B82F6));
        btnThem.setForeground(Color.WHITE);
        btnThem.addActionListener(e -> onThemButtonClick());
        getContentPane().add(btnThem);
    }

    private void updateHeSoFieldState(boolean isGoc) {
        if (isGoc) {
            txtHeSoQuyDoi.setText("1");
            txtHeSoQuyDoi.setEditable(false);
            txtHeSoQuyDoi.setBackground(new Color(0xE9ECEF));
        } else {
            txtHeSoQuyDoi.setText("");
            txtHeSoQuyDoi.setEditable(true);
            txtHeSoQuyDoi.setBackground(Color.WHITE);
        }
    }

    private void onThemButtonClick() {
        if (!validateInputFields()) return;

        try {
            String maQCDG = qcdg_DAO.taoMaQuyCach();
            SanPham sanPham = (SanPham) cmbSanPham.getSelectedItem();
            DonViTinh donViTinh = (DonViTinh) cmbDonViTinh.getSelectedItem();
            int heSo = Integer.parseInt(txtHeSoQuyDoi.getText().trim());
            double tiLeGiam = Double.parseDouble(txtTiLeGiam.getText().trim()) / 100.0;
            boolean isDonViGoc = chkDonViGoc.isSelected();

            this.quyCachMoi = new QuyCachDongGoi(maQCDG, donViTinh, sanPham, heSo, tiLeGiam, isDonViGoc, true);
            dispose();
        } catch (NumberFormatException ex) {
            showError("Hệ số quy đổi và tỉ lệ giảm phải là số hợp lệ.", null);
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage(), null);
        }
    }

    private boolean validateInputFields() {
        if (cmbSanPham.getSelectedItem() == null || cmbDonViTinh.getSelectedItem() == null) {
            showError("Vui lòng chọn sản phẩm và đơn vị tính.", null);
            return false;
        }
        if (txtHeSoQuyDoi.getText().trim().isEmpty() || txtTiLeGiam.getText().trim().isEmpty()) {
            showError("Hệ số và tỉ lệ giảm không được rỗng.", null);
            return false;
        }
        return true;
    }

    private void showError(String message, JComponent c) {
        JOptionPane.showMessageDialog(this, message, "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
        if (c != null) c.requestFocus();
    }

    public QuyCachDongGoi getQuyCachMoi() {
        return quyCachMoi;
    }
}