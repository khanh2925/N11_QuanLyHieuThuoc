package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

import dao.NhaCungCap_DAO;
import entity.NhaCungCap;

@SuppressWarnings("serial")
public class ThemNhaCungCap_Dialog extends JDialog implements ActionListener {

    private JTextField txtTen;
    private JTextField txtSdt;
    private JTextField txtDiaChi;
    private JButton btnThem, btnThoat;

    private NhaCungCap nhaCungCapMoi; // trả về sau khi lưu
    private final NhaCungCap_DAO nccDAO = new NhaCungCap_DAO();

    public ThemNhaCungCap_Dialog(Frame owner) {
        super(owner, "Thêm nhà cung cấp", true);
        initUI();
    }

    private void initUI() {
        setSize(520, 420);
        setLocationRelativeTo(getParent());
        getContentPane().setLayout(new BorderLayout(12, 12));
        getContentPane().setBackground(Color.WHITE);

        // ==== TIÊU ĐỀ ====
        JPanel pnTitle = new JPanel(new BorderLayout());
        pnTitle.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        JLabel lbl = new JLabel("Thông tin nhà cung cấp", SwingConstants.LEFT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnTitle.add(lbl, BorderLayout.CENTER);
        pnTitle.setBackground(Color.WHITE);
        getContentPane().add(pnTitle, BorderLayout.NORTH);

        // ==== FORM CHÍNH ====
        JPanel pnForm = new JPanel(null);
        pnForm.setBackground(Color.WHITE);
        pnForm.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        // --- Tên NCC ---
        JLabel lbTen = new JLabel("Tên nhà cung cấp:");
        lbTen.setBounds(12, 10, 180, 24);
        lbTen.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnForm.add(lbTen);

        txtTen = new JTextField();
        txtTen.setBounds(12, 36, 470, 36);
        txtTen.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTen.setBorder(new LineBorder(new Color(0x00C0E2), 1, true));
        pnForm.add(txtTen);

        // --- Số điện thoại ---
        JLabel lbSdt = new JLabel("Số điện thoại:");
        lbSdt.setBounds(12, 84, 180, 24);
        lbSdt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnForm.add(lbSdt);

        txtSdt = new JTextField();
        txtSdt.setBounds(12, 110, 470, 36);
        txtSdt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSdt.setBorder(new LineBorder(new Color(0x00C0E2), 1, true));
        pnForm.add(txtSdt);

        // --- Địa chỉ ---
        JLabel lbDiaChi = new JLabel("Địa chỉ:");
        lbDiaChi.setBounds(12, 158, 180, 24);
        lbDiaChi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnForm.add(lbDiaChi);

        txtDiaChi = new JTextField();
        txtDiaChi.setBounds(12, 185, 470, 36);
        txtDiaChi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDiaChi.setBorder(new LineBorder(new Color(0x00C0E2), 1, true));
        pnForm.add(txtDiaChi);

        getContentPane().add(pnForm, BorderLayout.CENTER);

        // ==== NÚT CHỨC NĂNG ====
        JPanel pnButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        pnButtons.setBackground(Color.WHITE);

        btnThoat = new JButton("Thoát");
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(0x3B82F6));
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setBorder(null);
        btnThoat.setFocusPainted(false);
        btnThoat.setPreferredSize(new Dimension(110, 35));

        btnThem = new JButton("Thêm");
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThem.setBackground(Color.WHITE);
        btnThem.setBorder(new LineBorder(Color.GRAY));
        btnThem.setPreferredSize(new Dimension(110, 35));

        pnButtons.add(btnThoat);
        pnButtons.add(btnThem);
        getContentPane().add(pnButtons, BorderLayout.SOUTH);

        // ==== SỰ KIỆN ====
        getRootPane().setDefaultButton(btnThem);

        btnThem.addActionListener(this);
        btnThoat.addActionListener(this);

        KeyAdapter enterToSave = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    themNCC();
            }
        };
        txtTen.addKeyListener(enterToSave);
        txtSdt.addKeyListener(enterToSave);
        txtDiaChi.addKeyListener(enterToSave);
    }

    // ==== HÀM XỬ LÝ CHÍNH ====
    private void themNCC() {
        String ten = txtTen.getText().trim();
        String sdt = txtSdt.getText().trim();
        String diaChi = txtDiaChi.getText().trim();

        // ✅ Kiểm tra tên
        if (ten.isEmpty()) {
            showWarn("Vui lòng nhập tên nhà cung cấp.");
            txtTen.requestFocus();
            return;
        }

        // ✅ Kiểm tra số điện thoại (10 số, bắt đầu bằng 0)
        if (!sdt.matches("^0[0-9]{9}$")) {
            showWarn("Số điện thoại không hợp lệ. Phải gồm 10 chữ số và bắt đầu bằng 0.");
            txtSdt.requestFocus();
            return;
        }

        // ✅ Kiểm tra địa chỉ
        if (diaChi.length() < 5) {
            showWarn("Địa chỉ quá ngắn. Vui lòng nhập chi tiết hơn.");
            txtDiaChi.requestFocus();
            return;
        }

        // ✅ Sinh mã NCC dạng NCC-xxx (theo DB)
        String maMoi = nccDAO.generateId();
        if (maMoi == null) {
            showWarn("Không tạo được mã nhà cung cấp mới.");
            return;
        }

        NhaCungCap ncc = new NhaCungCap(maMoi, ten, sdt, diaChi);

        // ✅ Ghi DB
        boolean ok = nccDAO.createNhaCungCap(ncc);
        if (!ok) {
            showWarn("Lưu nhà cung cấp thất bại. Vui lòng thử lại.");
            return;
        }

        // ✅ Thành công → trả về đối tượng mới
        nhaCungCapMoi = ncc;
        JOptionPane.showMessageDialog(
                this,
                "Đã thêm nhà cung cấp mới: " + ten,
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE
        );
        dispose();
    }

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
    }

    /** Trả về NCC vừa tạo, hoặc null nếu người dùng hủy */
    public NhaCungCap getNhaCungCapMoi() {
        return nhaCungCapMoi;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnThem)) {
            themNCC();
        } else if (o.equals(btnThoat)) {
            dispose();
        }
    }
}
