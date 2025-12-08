package gui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.util.List;
import javax.swing.*;
import entity.DonViTinh;
import entity.QuyCachDongGoi;
import entity.SanPham;

public class CapNhatQuyCachDongGoi_Dialog extends JDialog {

    private JTextField txtMaQCDG;
    private JComboBox<SanPham> cmbSanPham;
    private JComboBox<DonViTinh> cmbDonViTinh;
    private JTextField txtHeSoQuyDoi;
    private JTextField txtTiLeGiam;
    private JCheckBox chkDonViGoc;
    private JButton btnLuu, btnThoat;

    private final QuyCachDongGoi quyCachCanCapNhat;
    private boolean isUpdateSuccess = false;

    public CapNhatQuyCachDongGoi_Dialog(Frame owner, QuyCachDongGoi qcToUpdate, List<SanPham> dsSanPham, List<DonViTinh> dsDonViTinh) {
        super(owner, "Cập Nhật Quy Cách Đóng Gói", true);
        this.quyCachCanCapNhat = qcToUpdate;
        initialize(dsSanPham, dsDonViTinh);
        populateData();
    }

    private void initialize(List<SanPham> dsSanPham, List<DonViTinh> dsDonViTinh) {
        setSize(550, 550);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(Color.WHITE);
        getContentPane().setLayout(null);

        JLabel lblTitle = new JLabel("Cập Nhật Quy Cách Đóng Gói");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBounds(100, 20, 350, 35);
        getContentPane().add(lblTitle);

        JLabel lblMa = new JLabel("Mã quy cách:");
        lblMa.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblMa.setBounds(40, 80, 150, 25);
        getContentPane().add(lblMa);
        txtMaQCDG = new JTextField();
        txtMaQCDG.setBounds(40, 110, 450, 35);
        txtMaQCDG.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMaQCDG.setEditable(false);
        txtMaQCDG.setBackground(new Color(0xE9ECEF));
        getContentPane().add(txtMaQCDG);

        JLabel lblSanPham = new JLabel("Sản phẩm:");
        lblSanPham.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSanPham.setBounds(40, 160, 150, 25);
        getContentPane().add(lblSanPham);

        DefaultComboBoxModel<SanPham> spModel = new DefaultComboBoxModel<>();
        dsSanPham.forEach(spModel::addElement);
        cmbSanPham = new JComboBox<>(spModel);
        cmbSanPham.setBounds(40, 190, 450, 35);
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
        lblDonViTinh.setBounds(40, 240, 150, 25);
        getContentPane().add(lblDonViTinh);

        DefaultComboBoxModel<DonViTinh> dvtModel = new DefaultComboBoxModel<>();
        dsDonViTinh.forEach(dvtModel::addElement);
        cmbDonViTinh = new JComboBox<>(dvtModel);
        cmbDonViTinh.setBounds(40, 270, 200, 35);
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
        lblHeSo.setBounds(290, 240, 150, 25);
        getContentPane().add(lblHeSo);
        txtHeSoQuyDoi = new JTextField();
        txtHeSoQuyDoi.setBounds(290, 270, 200, 35);
        txtHeSoQuyDoi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtHeSoQuyDoi);

        JLabel lblTiLeGiam = new JLabel("Tỉ lệ giảm giá (%):");
        lblTiLeGiam.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTiLeGiam.setBounds(40, 320, 150, 25);
        getContentPane().add(lblTiLeGiam);
        txtTiLeGiam = new JTextField();
        txtTiLeGiam.setBounds(40, 350, 200, 35);
        txtTiLeGiam.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtTiLeGiam);

        chkDonViGoc = new JCheckBox("Đây là đơn vị gốc");
        chkDonViGoc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        chkDonViGoc.setBackground(Color.WHITE);
        chkDonViGoc.setBounds(40, 410, 200, 30);
        getContentPane().add(chkDonViGoc);

        chkDonViGoc.addItemListener(e -> updateHeSoFieldState(e.getStateChange() == ItemEvent.SELECTED));

        btnThoat = new JButton("Thoát");
        btnThoat.setBounds(400, 460, 110, 40);
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(0x6B7280));
        btnThoat.setForeground(Color.WHITE);
        btnThoat.addActionListener(e -> dispose());
        getContentPane().add(btnThoat);

        btnLuu = new JButton("Lưu thay đổi");
        btnLuu.setBounds(250, 460, 130, 40);
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLuu.setBackground(new Color(0x3B82F6));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.addActionListener(e -> onLuuButtonClick());
        getContentPane().add(btnLuu);
    }

    private void updateHeSoFieldState(boolean isGoc) {
        if (isGoc) {
            txtHeSoQuyDoi.setText("1");
            txtHeSoQuyDoi.setEditable(false);
            txtHeSoQuyDoi.setBackground(new Color(0xE9ECEF));
        } else {
            txtHeSoQuyDoi.setEditable(true);
            txtHeSoQuyDoi.setBackground(Color.WHITE);
            if (txtHeSoQuyDoi.getText().trim().equals("1")) {
                txtHeSoQuyDoi.setText("");
            }
        }
    }

    private void populateData() {
        txtMaQCDG.setText(quyCachCanCapNhat.getMaQuyCach());
        cmbSanPham.setSelectedItem(quyCachCanCapNhat.getSanPham());
        cmbDonViTinh.setSelectedItem(quyCachCanCapNhat.getDonViTinh());
        txtHeSoQuyDoi.setText(String.valueOf(quyCachCanCapNhat.getHeSoQuyDoi()));
        txtTiLeGiam.setText(String.format("%.0f", quyCachCanCapNhat.getTiLeGiam() * 100));
        chkDonViGoc.setSelected(quyCachCanCapNhat.isDonViGoc());

        updateHeSoFieldState(quyCachCanCapNhat.isDonViGoc());
    }

    private void onLuuButtonClick() {
        if (!validateInputFields()) return;

        try {
            quyCachCanCapNhat.setSanPham((SanPham) cmbSanPham.getSelectedItem());
            quyCachCanCapNhat.setDonViTinh((DonViTinh) cmbDonViTinh.getSelectedItem());
            quyCachCanCapNhat.setTiLeGiam(Double.parseDouble(txtTiLeGiam.getText().trim()) / 100.0);
            quyCachCanCapNhat.setDonViGoc(chkDonViGoc.isSelected());
            quyCachCanCapNhat.setHeSoQuyDoi(Integer.parseInt(txtHeSoQuyDoi.getText().trim()));

            isUpdateSuccess = true;
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

    public boolean isUpdateSuccess() {
        return isUpdateSuccess;
    }
}