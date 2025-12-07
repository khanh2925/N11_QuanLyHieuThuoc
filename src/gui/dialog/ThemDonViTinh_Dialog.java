package gui.dialog;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

import dao.DonViTinh_DAO;
import entity.DonViTinh;

@SuppressWarnings("serial")
public class ThemDonViTinh_Dialog extends JDialog {

    private JTextField txtTenDonViTinh;
    private JButton btnThem;
    private JButton btnThoat;

    private DonViTinh donViTinhMoi = null;
    private DonViTinh_DAO donViTinhDAO;

    public ThemDonViTinh_Dialog(Frame owner) {
        super(owner, "Thêm đơn vị tính", true);
        donViTinhDAO = new DonViTinh_DAO();
        initialize();
    }

    private void initialize() {
        setSize(450, 300);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel lblTitle = new JLabel("Thêm đơn vị tính", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBounds(0, 20, 450, 35);
        getContentPane().add(lblTitle);

        JLabel lblTen = new JLabel("Tên đơn vị tính:");
        lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTen.setBounds(40, 80, 150, 25);
        getContentPane().add(lblTen);

        txtTenDonViTinh = new JTextField();
        txtTenDonViTinh.setBounds(40, 110, 360, 35);
        txtTenDonViTinh.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTenDonViTinh.setBorder(new LineBorder(new Color(0x00C0E2), 1, true));
        getContentPane().add(txtTenDonViTinh);

        btnThem = new JButton("Thêm");
        btnThem.setBounds(180, 180, 110, 35);
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThem.setBackground(new Color(0x3B82F6));
        btnThem.setForeground(Color.WHITE);
        btnThem.setBorder(null);
        getContentPane().add(btnThem);

        btnThoat = new JButton("Thoát");
        btnThoat.setBounds(310, 180, 90, 35);
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(0x6B7280));
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setBorder(null);
        getContentPane().add(btnThoat);

        // ====== SỰ KIỆN ======
        btnThoat.addActionListener(e -> dispose());
        btnThem.addActionListener(e -> onThemButtonClick());
    }

    private void onThemButtonClick() {
        String ten = txtTenDonViTinh.getText().trim();

        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đơn vị tính.", "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Sinh mã mới qua DAO (tự động tăng DVT-xxx)
            String maDVT = donViTinhDAO.taoMaTuDong();

            // Tạo đối tượng DonViTinh mới
            this.donViTinhMoi = new DonViTinh(maDVT, ten);

            // Ghi vào DB
            boolean ok = donViTinhDAO.themDonViTinh(donViTinhMoi);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Thêm đơn vị tính thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể thêm đơn vị tính (trùng tên hoặc lỗi DB).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Trả về đơn vị tính vừa được thêm (hoặc null nếu hủy) */
    public DonViTinh getDonViTinhMoi() {
        return donViTinhMoi;
    }
}
