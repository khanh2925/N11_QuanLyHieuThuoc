package gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

import dao.NhaCungCap_DAO;
import entity.NhaCungCap;

@SuppressWarnings("serial")
public class CapNhatNhaCungCap_Dialog extends JDialog implements ActionListener {

    private final NhaCungCap_DAO nccDAO = new NhaCungCap_DAO();

    @SuppressWarnings("unused")
	private final NhaCungCap nccBanDau;
    private NhaCungCap nccCapNhat;

    private JTextField txtMa, txtTen, txtSdt, txtEmail;
    private JTextArea txtDiaChi;
    private JCheckBox chkHoatDong;
    private JButton btnLuu, btnThoat;

    public CapNhatNhaCungCap_Dialog(Frame owner, NhaCungCap ncc) {
        super(owner, "Cập nhật nhà cung cấp", true);
        if (ncc == null)
            throw new IllegalArgumentException("NhaCungCap không được null.");
        this.nccBanDau = ncc;
        initUI();
        napDuLieu(ncc);
    }

    private void initUI() {
        setSize(600, 600);
        setLocationRelativeTo(getParent());
        getContentPane().setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        JLabel lblTieuDe = new JLabel("Cập nhật thông tin nhà cung cấp", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTieuDe.setBounds(0, 20, 600, 30);
        add(lblTieuDe);

        int y = 80;
        addLabel("Mã nhà cung cấp:", 40, y);
        txtMa = addTextField(false, 40, y += 25);

        addLabel("Tên nhà cung cấp:", 40, y += 60);
        txtTen = addTextField(true, 40, y += 25);

        addLabel("Số điện thoại:", 40, y += 60);
        txtSdt = addTextField(true, 40, y += 25);

        addLabel("Email:", 40, y += 60);
        txtEmail = addTextField(true, 40, y += 25);

        addLabel("Địa chỉ:", 40, y += 60);
        txtDiaChi = new JTextArea();
        JScrollPane sp = new JScrollPane(txtDiaChi);
        sp.setBounds(40, y += 25, 520, 80);
        sp.setBorder(new LineBorder(new Color(0x00C0E2), 1, true));
        add(sp);

        chkHoatDong = new JCheckBox("Đang hợp tác");
        chkHoatDong.setBounds(40, y += 100, 200, 30);
        chkHoatDong.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        chkHoatDong.setBackground(Color.WHITE);
        add(chkHoatDong);

        btnLuu = new JButton("Lưu thay đổi");
        btnLuu.setBounds(280, y += 50, 130, 40);
        btnLuu.setBackground(new Color(0x3B82F6));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLuu.setBorder(null);
        add(btnLuu);

        btnThoat = new JButton("Thoát");
        btnThoat.setBounds(430, y, 130, 40);
        btnThoat.setBackground(new Color(0x6B7280));
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBorder(null);
        add(btnThoat);

        btnLuu.addActionListener(this);
        btnThoat.addActionListener(this);
    }

    private void addLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lbl.setBounds(x, y, 200, 25);
        add(lbl);
    }

    private JTextField addTextField(boolean editable, int x, int y) {
        JTextField tf = new JTextField();
        tf.setBounds(x, y, 520, 35);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(new LineBorder(new Color(0x00C0E2), 1, true));
        tf.setEditable(editable);
        add(tf);
        return tf;
    }

    private void napDuLieu(NhaCungCap n) {
        txtMa.setText(n.getMaNhaCungCap());
        txtTen.setText(n.getTenNhaCungCap());
        txtSdt.setText(n.getSoDienThoai());
        txtEmail.setText(n.getEmail());
        txtDiaChi.setText(n.getDiaChi());
        chkHoatDong.setSelected(n.isHoatDong());
    }

    private void capNhat() {
        try {
            String ma = txtMa.getText().trim();
            String ten = txtTen.getText().trim();
            String sdt = txtSdt.getText().trim();
            String email = txtEmail.getText().trim();
            String diachi = txtDiaChi.getText().trim();
            boolean hoatDong = chkHoatDong.isSelected();

            if (ten.isEmpty()) throw new IllegalArgumentException("Tên nhà cung cấp không được để trống.");
            if (!sdt.matches("^0\\d{9}$"))
                throw new IllegalArgumentException("Số điện thoại không hợp lệ (10 chữ số, bắt đầu bằng 0).");
            if (diachi.isEmpty()) throw new IllegalArgumentException("Địa chỉ không được trống.");
            if (!email.isEmpty() && !email.matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}$"))
                throw new IllegalArgumentException("Email không hợp lệ.");

            NhaCungCap nccMoi = new NhaCungCap(ma, ten, sdt, diachi, email);
            nccMoi.setHoatDong(hoatDong);

            if (!nccDAO.capNhatNhaCungCap(nccMoi)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại. Vui lòng thử lại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            nccCapNhat = nccMoi;
            JOptionPane.showMessageDialog(this, "Đã cập nhật thông tin nhà cung cấp thành công!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnLuu) capNhat();
        else if (src == btnThoat) dispose();
    }

    public NhaCungCap getNhaCungCapCapNhat() {
        return nccCapNhat;
    }
}
