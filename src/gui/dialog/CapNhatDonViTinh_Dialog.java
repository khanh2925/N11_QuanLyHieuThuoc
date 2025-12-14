package gui.dialog;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import dao.DonViTinh_DAO;
import entity.DonViTinh;

@SuppressWarnings("serial")
public class CapNhatDonViTinh_Dialog extends JDialog {

    private final DonViTinh_DAO dvtDAO = new DonViTinh_DAO();
    private final DonViTinh dvt;
    private boolean updateSuccess = false;

    private JTextField txtMa, txtTen;

    public CapNhatDonViTinh_Dialog(Frame owner, DonViTinh dvt) {
        super(owner, "Cập nhật đơn vị tính", true);
        this.dvt = dvt;
        initUI();
        napDuLieu();
    }

    private void initUI() {
        setSize(400, 250);
        setLocationRelativeTo(getParent());
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        JLabel lblMa = new JLabel("Mã đơn vị tính:");
        lblMa.setBounds(30, 30, 150, 25);
        add(lblMa);

        txtMa = new JTextField();
        txtMa.setBounds(30, 55, 320, 35);
        txtMa.setEditable(false);
        txtMa.setBorder(new LineBorder(new Color(0x00C0E2), 1, true));
        add(txtMa);

        JLabel lblTen = new JLabel("Tên đơn vị tính:");
        lblTen.setBounds(30, 100, 150, 25);
        add(lblTen);

        txtTen = new JTextField();
        txtTen.setBounds(30, 125, 320, 35);
        txtTen.setBorder(new LineBorder(new Color(0x00C0E2), 1, true));
        add(txtTen);

        JButton btnLuu = new JButton("Lưu");
        btnLuu.setBounds(190, 175, 75, 35);
        btnLuu.setBackground(new Color(0x3B82F6));
        btnLuu.setForeground(Color.WHITE);
        add(btnLuu);

        JButton btnThoat = new JButton("Thoát");
        btnThoat.setBounds(275, 175, 75, 35);
        add(btnThoat);

        btnLuu.addActionListener(e -> capNhat());
        btnThoat.addActionListener(e -> dispose());
    }

    private void napDuLieu() {
        txtMa.setText(dvt.getMaDonViTinh());
        txtTen.setText(dvt.getTenDonViTinh());
    }

    private void capNhat() {
        String ten = txtTen.getText().trim();
        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên đơn vị tính không được để trống.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        dvt.setTenDonViTinh(ten);
        if (dvtDAO.capNhatDonViTinh(dvt)) {
            updateSuccess = true;
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isUpdateSuccess() {
        return updateSuccess;
    }
}
