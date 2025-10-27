package gui;

import java.awt.*;
import javax.swing.*;
import com.toedter.calendar.JDateChooser;

public class ThemLo_Dialog extends JDialog {

    private JTextField txtTenLo;
    private JSpinner spnSoLuong;
    private JDateChooser dateHanSuDung;
    private JButton btnLuu, btnThoat;

    public ThemLo_Dialog(Frame owner) {
        super(owner, "Tạo lô sản phẩm", true);
        initialize();
    }

    private void initialize() {
        setSize(550, 420);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(Color.WHITE);
        getContentPane().setLayout(null);

        // --- Tiêu đề ---
        JLabel lblTitle = new JLabel("Tạo lô sản phẩm", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBounds(0, 25, 550, 35);
        getContentPane().add(lblTitle);

        // --- Tên lô ---
        JLabel lblTenLo = new JLabel("Tên lô:");
        lblTenLo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTenLo.setBounds(40, 90, 120, 25);
        getContentPane().add(lblTenLo);

        txtTenLo = new JTextField();
        txtTenLo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTenLo.setBounds(40, 120, 460, 35);
        getContentPane().add(txtTenLo);

        // --- Hạn sử dụng ---
        JLabel lblHanSuDung = new JLabel("Hạn sử dụng:");
        lblHanSuDung.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblHanSuDung.setBounds(40, 170, 120, 25);
        getContentPane().add(lblHanSuDung);

        dateHanSuDung = new JDateChooser();
        dateHanSuDung.setDateFormatString("dd-MM-yyyy");
        dateHanSuDung.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateHanSuDung.setBounds(40, 200, 460, 35);
        getContentPane().add(dateHanSuDung);

        // --- Số lượng ---
        JLabel lblSoLuong = new JLabel("Số lượng:");
        lblSoLuong.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSoLuong.setBounds(40, 250, 120, 25);
        getContentPane().add(lblSoLuong);

        spnSoLuong = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 1));
        spnSoLuong.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        spnSoLuong.setBounds(40, 280, 460, 35);
        getContentPane().add(spnSoLuong);

        // --- Nút ---
        btnLuu = new JButton("Lưu");
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLuu.setBackground(new Color(0x3B82F6));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setBorder(null);
        btnLuu.setFocusPainted(false);
        btnLuu.setBounds(290, 335, 100, 38);
        getContentPane().add(btnLuu);

        btnThoat = new JButton("Thoát");
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(0x6B7280));
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setBorder(null);
        btnThoat.setFocusPainted(false);
        btnThoat.setBounds(400, 335, 100, 38);
        getContentPane().add(btnThoat);
    }

    // =================== TEST MAIN ===================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ThemLo_Dialog dialog = new ThemLo_Dialog(null);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        });
    }
}
