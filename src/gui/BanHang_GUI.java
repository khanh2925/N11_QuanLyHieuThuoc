/**
 * @author Quốc Khánh
 * @version 3.0
 * @since Oct 27, 2025
 *
 * Giao diện bán hàng - sử dụng dữ liệu mẫu giả (fake) để test UI.
 */

package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;

public class BanHang_GUI extends JPanel {

    private JTextField txtTimThuoc;
    private JPanel pnDanhSachDon;

    public BanHang_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    /** Khởi tạo giao diện chính */
    private void initialize() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1537, 1168));

        // ===== HEADER =====
        JPanel pnHeader = new JPanel(null);
        pnHeader.setPreferredSize(new Dimension(1073, 88));
        pnHeader.setBackground(new Color(0xE3F2F5));
        add(pnHeader, BorderLayout.NORTH);

        // Ô tìm kiếm
        txtTimThuoc = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimThuoc, "Tìm theo mã, tên...");
        txtTimThuoc.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtTimThuoc.setBounds(25, 17, 420, 60);
        txtTimThuoc.setBorder(new RoundedBorder(20));
        txtTimThuoc.setBackground(Color.WHITE);
        txtTimThuoc.setForeground(Color.GRAY);
        pnHeader.add(txtTimThuoc);

        // Nút thêm đơn
        JButton btnThemDon = new PillButton("Thêm đơn");
        btnThemDon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnThemDon.setBounds(490, 30, 120, 40);
        pnHeader.add(btnThemDon);

        // Đơn hàng mẫu
        JButton btnDon1 = new JButton("Đơn Hàng 1");
        btnDon1.setFont(new Font("Tahoma", Font.PLAIN, 18));
        btnDon1.setBounds(653, 51, 160, 26);
        pnHeader.add(btnDon1);

        JButton btnXoa = new JButton("X");
        btnXoa.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnXoa.setBounds(813, 51, 39, 26);
        pnHeader.add(btnXoa);

        // ===== CENTER: DANH SÁCH SẢN PHẨM =====
        JPanel pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new CompoundBorder(
            new LineBorder(new Color(0x00C853), 3, true),
            new EmptyBorder(5, 5, 5, 5)
        ));
        add(pnCenter, BorderLayout.CENTER);

        pnDanhSachDon = new JPanel();
        pnDanhSachDon.setLayout(new BoxLayout(pnDanhSachDon, BoxLayout.Y_AXIS));
        pnDanhSachDon.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(pnDanhSachDon);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        pnCenter.add(scrollPane);

        // ===== CỘT PHẢI =====
        add(buildRightPanel(), BorderLayout.EAST);

        // ===== DỮ LIỆU FAKE =====
        ArrayList<Object[]> dsFake = taoDataFake();

        for (Object[] sp : dsFake) {
            pnDanhSachDon.add(createDonPanel(sp));
        }
    }

    /** Sinh dữ liệu giả để hiển thị */
    private ArrayList<Object[]> taoDataFake() {
        ArrayList<Object[]> ds = new ArrayList<>();

        ds.add(new Object[]{"SP001", "Paracetamol 500mg", "Viên", 5000.0, "/images/para.png"});
        ds.add(new Object[]{"SP002", "Decolgen", "Viên", 7000.0, "/images/decolgen.png"});
        ds.add(new Object[]{"SP003", "Efferalgan", "Viên sủi", 12000.0, "/images/efferalgan.png"});
        ds.add(new Object[]{"SP004", "Vitamin C", "Viên nhai", 8000.0, "/images/vitaminC.png"});
        ds.add(new Object[]{"SP005", "Panadol Extra", "Hộp", 90000.0, "/images/panadol.png"});

        return ds;
    }

    /** Tạo panel hiển thị 1 sản phẩm */
    private JPanel createDonPanel(Object[] sp) {
        String ma = (String) sp[0];
        String ten = (String) sp[1];
        String donVi = (String) sp[2];
        double giaBan = (double) sp[3];
        String hinh = (String) sp[4];

        JPanel pnDon = new JPanel(null);
        pnDon.setPreferredSize(new Dimension(1040, 120));
        pnDon.setBackground(Color.WHITE);
        pnDon.setBorder(new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        int centerY = 120 / 2;

        // ==== ẢNH ==== 
        JLabel lblHinh = new JLabel("", SwingConstants.CENTER);
        lblHinh.setBounds(27, centerY - 30, 100, 100);
        lblHinh.setBorder(new LineBorder(Color.LIGHT_GRAY));
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(hinh));
            lblHinh.setIcon(new ImageIcon(icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
        } catch (Exception e) {
            lblHinh.setText("Ảnh");
        }
        pnDon.add(lblHinh);

        // ==== TÊN ==== 
        JLabel lblTen = new JLabel(ten);
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTen.setBounds(168, centerY - 30, 300, 34);
        pnDon.add(lblTen);

        // ==== ĐƠN VỊ ==== 
        JLabel lblDonVi = new JLabel(donVi);
        lblDonVi.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDonVi.setBounds(350, centerY - 28, 100, 30);
        pnDon.add(lblDonVi);

        // ==== LÔ & SL ==== 
        JLabel lblLo = new JLabel("Lô: A" + ma.substring(2) + " - SL: " + (10 + new Random().nextInt(30)));
        lblLo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLo.setForeground(new Color(80, 80, 80));
        lblLo.setBounds(168, centerY + 12, 320, 25);
        pnDon.add(lblLo);

        // ==== PANEL TĂNG GIẢM ==== 
        JPanel pnTangGiam = new JPanel(new BorderLayout(5, 0));
        pnTangGiam.setBounds(500, centerY, 137, 36);
        pnTangGiam.setBackground(new Color(0xF8FAFB));
        pnTangGiam.setBorder(new LineBorder(new Color(0xB0BEC5), 2, true));
        pnDon.add(pnTangGiam);

        JButton btnGiam = new JButton("−");
        JButton btnTang = new JButton("+");
        JTextField txtSL = new JTextField("1");
        txtSL.setHorizontalAlignment(SwingConstants.CENTER);
        txtSL.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtSL.setBorder(null);

        styleMiniButton(btnGiam);
        styleMiniButton(btnTang);

        pnTangGiam.add(btnGiam, BorderLayout.WEST);
        pnTangGiam.add(txtSL, BorderLayout.CENTER);
        pnTangGiam.add(btnTang, BorderLayout.EAST);

        // ==== GIÁ ==== 
        JLabel lblGia = new JLabel(String.format("%,.0f vnđ", giaBan));
        lblGia.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblGia.setBounds(700, centerY, 120, 29);
        pnDon.add(lblGia);

        // ==== GIẢM GIÁ ==== 
        JLabel lblGiam = new JLabel("Giảm 5% - BlackFriday");
        lblGiam.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblGiam.setForeground(new Color(220, 0, 0));
        lblGiam.setBounds(700, centerY + 26, 160, 22);
        pnDon.add(lblGiam);

        // ==== TỔNG ==== 
        JLabel lblTong = new JLabel(String.format("%,.0f vnđ", giaBan));
        lblTong.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTong.setBounds(850, centerY, 120, 29);
        pnDon.add(lblTong);

        // ==== NÚT XÓA ==== 
        JButton btnXoa = new JButton();
        btnXoa.setBounds(980, centerY, 35, 35);
        try {
            ImageIcon iconBin = new ImageIcon(getClass().getResource("/images/bin.png"));
            Image img = iconBin.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            btnXoa.setIcon(new ImageIcon(img));
        } catch (Exception ignored) {}
        btnXoa.setBorderPainted(false);
        btnXoa.setContentAreaFilled(false);
        btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnDon.add(btnXoa);

        // ==== Xử lý tăng/giảm ==== 
        btnTang.addActionListener(e -> {
            int sl = parse(txtSL.getText()) + 1;
            txtSL.setText(String.valueOf(sl));
            lblTong.setText(String.format("%,.0f vnđ", sl * giaBan));
        });
        btnGiam.addActionListener(e -> {
            int sl = parse(txtSL.getText());
            if (sl > 1) sl--;
            txtSL.setText(String.valueOf(sl));
            lblTong.setText(String.format("%,.0f vnđ", sl * giaBan));
        });

        return pnDon;
    }

    /** Style cho nút + và − */
    private void styleMiniButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(0xE0F2F1));
        btn.setBorder(new LineBorder(new Color(0x80CBC4), 1, true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(40, 36));
    }

    /** Parse số an toàn */
    private int parse(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 1;
        }
    }

    /** Panel bên phải: khách hàng & thanh toán */
    private JPanel buildRightPanel() {
        JPanel pnRight = new JPanel();
        pnRight.setPreferredSize(new Dimension(350, 1080));
        pnRight.setBackground(Color.WHITE);
        pnRight.setBorder(new EmptyBorder(20, 20, 20, 20));
        pnRight.setLayout(new BoxLayout(pnRight, BoxLayout.Y_AXIS));

        pnRight.add(makeLabel("Tên khách hàng:", "Chu Anh Khôi"));
        pnRight.add(makeLabel("Tổng tiền hàng:", "140,000 đ"));
        pnRight.add(makeLabel("Giảm giá hóa đơn:", "5,000 đ"));
        pnRight.add(makeLabel("Tổng thanh toán:", "135,000 đ"));
        pnRight.add(Box.createVerticalStrut(20));

        JButton btnBanHang = new PillButton("Bán hàng");
        btnBanHang.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnBanHang.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnRight.add(btnBanHang);
        pnRight.add(Box.createVerticalGlue());
        return pnRight;
    }

    /** Tạo label trái-phải */
    private JPanel makeLabel(String left, String right) {
        JPanel pn = new JPanel(new BorderLayout());
        pn.setOpaque(false);
        JLabel l = new JLabel(left);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel r = new JLabel(right);
        r.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pn.add(l, BorderLayout.WEST);
        pn.add(r, BorderLayout.EAST);
        pn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        return pn;
    }

    /** Run thử UI */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Demo Bán Hàng - Data Fake");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new BanHang_GUI());
            frame.setVisible(true);
        });
    }
}
