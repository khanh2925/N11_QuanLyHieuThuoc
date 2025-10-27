package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;

import java.util.ArrayList;
import java.util.List;

import java.awt.event.*;

public class TraHangNhanVien_GUI extends JPanel {

    private JTextField txtTimThuoc;
    private JPanel pnCotPhaiCenter;
    private JPanel pnDanhSachDon;

    // ===== Data model siêu gọn cho màn hình (không dùng entity/enums) =====
    private static class Product {
        private final String maSanPham;
        private final String tenSanPham;
        private final String donViTinhTen;
        private final double giaBan;
        private final String hinh; // path resource, nếu có

        Product(String ma, String ten, String dvt, double giaBan, String hinh) {
            this.maSanPham = ma;
            this.tenSanPham = ten;
            this.donViTinhTen = dvt;
            this.giaBan = giaBan;
            this.hinh = hinh;
        }
        public String getMaSanPham() { return maSanPham; }
        public String getTenSanPham() { return tenSanPham; }
        public String getDonViTinhTen() { return donViTinhTen; }
        public double getGiaBan() { return giaBan; }
        public String getHinh() { return hinh; }
    }

    public TraHangNhanVien_GUI() {
        this.setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1537, 1168));

        // ===== HEADER =====
        JPanel pnCotPhaiHead = new JPanel(null);
        pnCotPhaiHead.setPreferredSize(new Dimension(1073, 88));
        pnCotPhaiHead.setBackground(new Color(0xE3F2F5));
        add(pnCotPhaiHead, BorderLayout.NORTH);

        // Ô tìm kiếm
        txtTimThuoc = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimThuoc, "Tìm theo mã, tên...");
        txtTimThuoc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtTimThuoc.setBounds(25, 17, 420, 60);
        txtTimThuoc.setBorder(new RoundedBorder(20));
        txtTimThuoc.setBackground(Color.WHITE);
        txtTimThuoc.setForeground(Color.GRAY);
        pnCotPhaiHead.add(txtTimThuoc);

        // ===== CENTER (DANH SÁCH SẢN PHẨM) =====
        pnCotPhaiCenter = new JPanel();
        pnCotPhaiCenter.setPreferredSize(new Dimension(1073, 992));
        pnCotPhaiCenter.setBackground(Color.WHITE);
        add(pnCotPhaiCenter, BorderLayout.CENTER);
        pnCotPhaiCenter.setBorder(new CompoundBorder(
            new LineBorder(new Color(0x00C853), 3, true),
            new EmptyBorder(5, 5, 5, 5)
        ));
        pnCotPhaiCenter.setLayout(new BorderLayout(0, 0));

        // Panel chứa danh sách đơn hàng
        pnDanhSachDon = new JPanel();
        pnDanhSachDon.setLayout(new BoxLayout(pnDanhSachDon, BoxLayout.Y_AXIS));
        pnDanhSachDon.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(pnDanhSachDon);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getVerticalScrollBar().setOpaque(false);
        pnCotPhaiCenter.add(scrollPane);

        // ====== CỘT PHẢI ======
        JPanel pnCotPhaiRight = new JPanel();
        pnCotPhaiRight.setPreferredSize(new Dimension(1920 - 383 - 1073, 1080));
        pnCotPhaiRight.setBackground(Color.WHITE);
        pnCotPhaiRight.setBorder(new EmptyBorder(20, 20, 20, 20));
        pnCotPhaiRight.setLayout(new BoxLayout(pnCotPhaiRight, BoxLayout.Y_AXIS));
        add(pnCotPhaiRight, BorderLayout.EAST);

        // ==== Thông tin nhân viên & thời gian ====
        JPanel pnNhanVien = new JPanel(new BorderLayout(5, 5));
        pnNhanVien.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        pnNhanVien.setOpaque(false);

        JLabel lblNhanVien = new JLabel("Phạm Quốc Khánh");
        lblNhanVien.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel lblThoiGian = new JLabel("15/10/2025 19:00", SwingConstants.RIGHT);
        lblThoiGian.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        pnNhanVien.add(lblNhanVien, BorderLayout.WEST);
        pnNhanVien.add(lblThoiGian, BorderLayout.EAST);

        pnCotPhaiRight.add(pnNhanVien);
        pnCotPhaiRight.add(Box.createVerticalStrut(10));

        // ===== ĐƯỜNG LINE NGAY DƯỚI =====
        JSeparator lineNV = new JSeparator();
        lineNV.setForeground(new Color(200, 200, 200));
        lineNV.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        pnCotPhaiRight.add(Box.createVerticalStrut(4));
        pnCotPhaiRight.add(lineNV);
        pnCotPhaiRight.add(Box.createVerticalStrut(10));

        // Ô tìm khách hàng
        JTextField txtTimKH = new JTextField("🔍 Số điện thoại khách hàng (F4)");
        txtTimKH.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTimKH.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtTimKH.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(0xCCCCCC), 2, true),
            new EmptyBorder(5,10,5,10)
        ));
        txtTimKH.setBackground(new Color(0xFAFAFA));
        txtTimKH.setForeground(Color.GRAY);
        txtTimKH.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtTimKH.getText().equals("🔍 Số điện thoại khách hàng (F4)")) {
                    txtTimKH.setText("");
                    txtTimKH.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtTimKH.getText().isEmpty()) {
                    txtTimKH.setText("🔍 Số điện thoại khách hàng (F4)");
                    txtTimKH.setForeground(Color.GRAY);
                }
            }
        });
        pnCotPhaiRight.add(txtTimKH);
        pnCotPhaiRight.add(Box.createVerticalStrut(15));

        // Label thông tin khách hàng
        pnCotPhaiRight.add(makeLabel("Mã hóa đơn:", "HD00001"));
        pnCotPhaiRight.add(makeLabel("Người bán:", "Chu Anh Khôi"));
        pnCotPhaiRight.add(makeLabel("Tên khách hàng:", "Lê Thanh Kha"));
        pnCotPhaiRight.add(makeLabel("Tiền trả:", "108,000 vnd"));

        JPanel pnMGG = new JPanel((LayoutManager) null);
        pnMGG.setOpaque(false);
        pnMGG.setMaximumSize(new Dimension(2147483647, 85));
        pnCotPhaiRight.add(pnMGG);
        pnMGG.setLayout(new BorderLayout(5, 5));

        JTextArea txtGhiChuGiamGia = new JTextArea(
            "Hóa đơn có áp dụng khuyến mãi “cuối tuần” - giảm 10% cho hóa đơn trên 100,000 vnđ - "
          + "Tổng giá trị sản phẩm khách trả trên 100,000 vnđ nên trừ 10% tiền trả khách."
        );
        txtGhiChuGiamGia.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtGhiChuGiamGia.setForeground(Color.RED);
        txtGhiChuGiamGia.setOpaque(false);
        txtGhiChuGiamGia.setEditable(false);
        txtGhiChuGiamGia.setFocusable(false);
        txtGhiChuGiamGia.setLineWrap(true);
        txtGhiChuGiamGia.setWrapStyleWord(true);
        txtGhiChuGiamGia.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtGhiChuGiamGia.setBorder(null);
        txtGhiChuGiamGia.setMargin(new Insets(0, 0, 0, 0));
        txtGhiChuGiamGia.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        pnMGG.add(txtGhiChuGiamGia, BorderLayout.CENTER);
        pnCotPhaiRight.add(Box.createVerticalStrut(10));
        pnCotPhaiRight.add(Box.createVerticalStrut(10));

        // ====== NÚT TRẢ HÀNG ======
        JButton btnBanHang = new PillButton("Trả hàng");
        btnBanHang.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnBanHang.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnCotPhaiRight.add(Box.createVerticalStrut(30));
        pnCotPhaiRight.add(btnBanHang);
        pnCotPhaiRight.add(Box.createVerticalStrut(270));

        // ===== DỮ LIỆU SẢN PHẨM (FAKE, không entity) =====
        List<Product> dsSanPham = new ArrayList<>();
        dsSanPham.add(new Product("SP000001", "Paracetamol",   "Hộp", 10000, "/images/para.png"));
        dsSanPham.add(new Product("SP000002", "Decolgen",      "Vỉ",   7500,  "/images/decolgen.png"));
        dsSanPham.add(new Product("SP000003", "Panadol Extra", "Hộp", 15600, "/images/panadol.png"));
        dsSanPham.add(new Product("SP000004", "Efferalgan",    "Hộp", 13000, "/images/efferalgan.png"));

        for (Product sp : dsSanPham) {
            pnDanhSachDon.add(createDonPanel(sp));
        }
    }

    private JPanel createDonPanel(Product sp) {
        JPanel pnDonMau = new JPanel();
        pnDonMau.setPreferredSize(new Dimension(1040, 120));
        pnDonMau.setLayout(null);
        pnDonMau.setBackground(Color.WHITE);
        pnDonMau.setBorder(new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        int centerY = 120 / 2; // để canh giữa theo chiều cao

        // ==== ẢNH SẢN PHẨM ====
        JLabel lblHinhAnh = new JLabel("Ảnh", SwingConstants.CENTER);
        lblHinhAnh.setBorder(new LineBorder(Color.LIGHT_GRAY));
        lblHinhAnh.setBounds(27, centerY - 30, 100, 100);
        if (sp.getHinh() != null) {
            java.net.URL url = getClass().getResource(sp.getHinh());
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image scaled = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                lblHinhAnh.setIcon(new ImageIcon(scaled));
                lblHinhAnh.setText("");
            }
        }
        pnDonMau.add(lblHinhAnh);

        // ==== TÊN THUỐC ====
        JLabel lblTenThuoc = new JLabel(sp.getTenSanPham());
        lblTenThuoc.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTenThuoc.setBounds(168, centerY - 30, 320, 34);
        pnDonMau.add(lblTenThuoc);

        // ==== ĐƠN VỊ TÍNH ====
        JLabel lblDonViTinh = new JLabel(sp.getDonViTinhTen());
        lblDonViTinh.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDonViTinh.setBounds(350, centerY - 28, 120, 30);
        pnDonMau.add(lblDonViTinh);

        // ==== LÔ THUỐC ====
        JLabel lblLoThuoc = new JLabel("Lô: " + sp.getMaSanPham() + " - SL: 20");
        lblLoThuoc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLoThuoc.setForeground(new Color(80, 80, 80));
        lblLoThuoc.setBounds(168, centerY + 12, 320, 25);
        pnDonMau.add(lblLoThuoc);

        // ==== PANEL TĂNG GIẢM ==== 
        JPanel pnTangGiam = new JPanel(new BorderLayout(5, 0));
        pnTangGiam.setBounds(500, centerY, 137, 36);
        pnTangGiam.setBackground(new Color(0xF8FAFB));
        pnTangGiam.setBorder(new LineBorder(new Color(0xB0BEC5), 2, true));
        pnDonMau.add(pnTangGiam);

        JButton btnGiam = new JButton("−");
        btnGiam.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnGiam.setFocusPainted(false);
        btnGiam.setBackground(new Color(0xE0F2F1));
        btnGiam.setBorder(new LineBorder(new Color(0x80CBC4), 1, true));
        btnGiam.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGiam.setOpaque(true);
        btnGiam.setPreferredSize(new Dimension(40, 36));
        pnTangGiam.add(btnGiam, BorderLayout.WEST);

        JTextField txtSoLuong = new JTextField("1");
        txtSoLuong.setHorizontalAlignment(SwingConstants.CENTER);
        txtSoLuong.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtSoLuong.setBorder(null);
        txtSoLuong.setBackground(Color.WHITE);
        pnTangGiam.add(txtSoLuong, BorderLayout.CENTER);

        JButton btnTang = new JButton("+");
        btnTang.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnTang.setFocusPainted(false);
        btnTang.setBackground(new Color(0xE0F2F1));
        btnTang.setBorder(new LineBorder(new Color(0x80CBC4), 1, true));
        btnTang.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTang.setOpaque(true);
        btnTang.setPreferredSize(new Dimension(40, 36));
        pnTangGiam.add(btnTang, BorderLayout.EAST);

        // ==== ĐƠN GIÁ ====
        JLabel lblDonGia = new JLabel(String.format("%,.0f vnđ", sp.getGiaBan()));
        lblDonGia.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDonGia.setBounds(700, centerY, 120, 29);
        pnDonMau.add(lblDonGia);

        // ==== GIẢM GIÁ ====
        JLabel lblGiamGia = new JLabel("Giảm 5% - BlackFriday");
        lblGiamGia.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblGiamGia.setForeground(new Color(220, 0, 0));
        lblGiamGia.setBounds(700, centerY + 26, 160, 22);
        pnDonMau.add(lblGiamGia);

        // ==== TỔNG TIỀN ====
        JLabel lblTongTien = new JLabel(String.format("%,.0f vnđ", sp.getGiaBan()));
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTongTien.setBounds(850, centerY , 120, 29);
        pnDonMau.add(lblTongTien);

        // ==== NÚT XÓA ====
        JButton btnXoa = new JButton();
        btnXoa.setBounds(980, centerY, 35, 35);
        java.net.URL binUrl = getClass().getResource("/images/bin.png");
        if (binUrl != null) {
            ImageIcon iconBin = new ImageIcon(binUrl);
            Image img = iconBin.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            btnXoa.setIcon(new ImageIcon(img));
        }
        btnXoa.setBorderPainted(false);
        btnXoa.setContentAreaFilled(false);
        btnXoa.setFocusPainted(false);
        btnXoa.setOpaque(false);
        btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnDonMau.add(btnXoa);

        // ==== Xử lý tăng giảm (cập nhật tổng tiền) ====
        btnTang.addActionListener(e -> {
            try {
                int sl = Integer.parseInt(txtSoLuong.getText().trim());
                sl = Math.max(1, sl + 1);
                txtSoLuong.setText(String.valueOf(sl));
                lblTongTien.setText(String.format("%,.0f vnđ", sl * sp.getGiaBan()));
            } catch (NumberFormatException ex) {
                txtSoLuong.setText("1");
                lblTongTien.setText(String.format("%,.0f vnđ", sp.getGiaBan()));
            }
        });

        btnGiam.addActionListener(e -> {
            try {
                int sl = Integer.parseInt(txtSoLuong.getText().trim());
                sl = Math.max(1, sl - 1);
                txtSoLuong.setText(String.valueOf(sl));
                lblTongTien.setText(String.format("%,.0f vnđ", sl * sp.getGiaBan()));
            } catch (NumberFormatException ex) {
                txtSoLuong.setText("1");
                lblTongTien.setText(String.format("%,.0f vnđ", sp.getGiaBan()));
            }
        });

        btnXoa.addActionListener(e -> {
            pnDanhSachDon.remove(pnDonMau);
            pnDanhSachDon.revalidate();
            pnDanhSachDon.repaint();
        });

        pnDonMau.setMaximumSize(new Dimension(1060, 150));
        pnDonMau.setMinimumSize(new Dimension(1040, 120));
        return pnDonMau;
    }

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Trả hàng - Data Fake");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new TraHangNhanVien_GUI());
            frame.setVisible(true);
        });
    }
}
