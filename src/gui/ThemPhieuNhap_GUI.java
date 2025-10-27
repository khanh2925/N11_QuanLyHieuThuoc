package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import entity.DonViTinh;
import entity.LoaiSanPham;
import entity.SanPham;
import enums.DuongDung;

public class ThemPhieuNhap_GUI extends JPanel {
    private JPanel pnCotPhaiCenter;
    private JPanel pnDanhSachDon;
	private JTextField txtSearch;

    public ThemPhieuNhap_GUI() {
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
        

        // Nút thêm lô
        JButton btnThemLo = new PillButton("Thêm lô");
        btnThemLo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnThemLo.setBounds(486, 30, 120, 40);
        pnCotPhaiHead.add(btnThemLo);

        // Nút nhập file
        JButton btnNhapFile = new PillButton("Nhập từ file");
        btnNhapFile.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnNhapFile.setBounds(640, 30, 150, 40);
        pnCotPhaiHead.add(btnNhapFile);
        
        txtSearch = new JTextField();
        PlaceholderSupport.addPlaceholder(txtSearch, "Tìm sản phẩm theo mã");
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBounds(20, 17, 420, 60);
        txtSearch.setBorder(new RoundedBorder(20));
        pnCotPhaiHead.add(txtSearch);

        // ===== CENTER (DANH SÁCH SẢN PHẨM NHẬP) =====
        pnCotPhaiCenter = new JPanel();
        pnCotPhaiCenter.setPreferredSize(new Dimension(1073, 992));
        pnCotPhaiCenter.setBackground(Color.WHITE);
        add(pnCotPhaiCenter, BorderLayout.CENTER);
        pnCotPhaiCenter.setBorder(new CompoundBorder(
            new LineBorder(new Color(0x00C853), 3, true),
            new EmptyBorder(5, 5, 5, 5)
        ));
        pnCotPhaiCenter.setLayout(new BorderLayout(0, 0));

        // Panel chứa danh sách lô hàng nhập
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

        JLabel lblThoiGian = new JLabel("08/10/2025 11:45", SwingConstants.RIGHT);
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

        // Ô tìm NCC
        JTextField txtTimNCC = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimNCC, "Tìm kiếm nhà cung cấp");
        txtTimNCC.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTimNCC.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtTimNCC.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(0xCCCCCC), 2, true),
            new EmptyBorder(5,10,5,10)
        ));
        txtTimNCC.setBackground(new Color(0xFAFAFA));
        txtTimNCC.setForeground(Color.GRAY);
        pnCotPhaiRight.add(txtTimNCC);
        pnCotPhaiRight.add(Box.createVerticalStrut(15));

        pnCotPhaiRight.add(makeLabel("Nhà cung cấp:", "Công ty Pharmedic"));
        pnCotPhaiRight.add(makeLabel("Tổng tiền hàng:", "3,200,000 vnd"));
        pnCotPhaiRight.add(Box.createVerticalStrut(20));

        // ====== NÚT NHẬP PHIẾU ======
        JButton btnNhapPhieu = new PillButton("Nhập phiếu");
        btnNhapPhieu.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnNhapPhieu.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnCotPhaiRight.add(Box.createVerticalStrut(30));
        pnCotPhaiRight.add(btnNhapPhieu);
        pnCotPhaiRight.add(Box.createVerticalStrut(10));

        JLabel lblQuayLai = new JLabel("Quay lại");
        lblQuayLai.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblQuayLai.setForeground(Color.RED);
        lblQuayLai.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblQuayLai.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblQuayLai.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                Window win = SwingUtilities.getWindowAncestor(ThemPhieuNhap_GUI.this);
                if (win instanceof JFrame f) {
                    f.setContentPane(new NhapHang_GUI());
                    f.revalidate();
                    f.repaint();
                }
            }
        });
        pnCotPhaiRight.add(lblQuayLai);
        pnCotPhaiRight.add(Box.createVerticalStrut(270));

        // ===== DỮ LIỆU SẢN PHẨM NHẬP =====
        LoaiSanPham loaiThuoc = new LoaiSanPham("LSP001", "Thuốc nhập kho", "Test");
        DonViTinh hop = new DonViTinh("DVT-001", "Hộp", "TEST");
        DonViTinh vi = new DonViTinh("DVT-002", "Vỉ", "TEST");

        List<SanPham> dsSanPham = new ArrayList<>();
        dsSanPham.add(new SanPham("SP000001", "Paracetamol", loaiThuoc, "SDK001", "Paracetamol", "500mg", "Pymepharco",
                "Việt Nam", hop, DuongDung.UONG, 80000, 100000, "/images/para.png", "Hộp 10 vỉ x 10 viên", "K1", true));
        dsSanPham.add(new SanPham("SP000002", "Vitamin C", loaiThuoc, "SDK002", "Ascorbic Acid", "1000mg", "Traphaco",
                "Việt Nam", vi, DuongDung.UONG, 50000, 70000, "/images/vitaminc.png", "Vỉ 10 viên", "K2", true));

        for (SanPham sp : dsSanPham) {
            pnDanhSachDon.add(createDonPanel(sp));
        }
    }

    // ===== TẠO 1 DÒNG SẢN PHẨM NHẬP =====
    private JPanel createDonPanel(SanPham sp) {
        JPanel pnDonMau = new JPanel();
        pnDonMau.setPreferredSize(new Dimension(1040, 120));
        pnDonMau.setLayout(null);
        pnDonMau.setBackground(Color.WHITE);
        pnDonMau.setBorder(new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        int centerY = 120 / 2;

        JLabel lblHinhAnh = new JLabel("Ảnh", SwingConstants.CENTER);
        lblHinhAnh.setBorder(new LineBorder(Color.LIGHT_GRAY));
        lblHinhAnh.setBounds(27, centerY - 30, 100, 100);
        pnDonMau.add(lblHinhAnh);

        JLabel lblTenThuoc = new JLabel(sp.getTenSanPham());
        lblTenThuoc.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTenThuoc.setBounds(168, centerY - 30, 250, 34);
        pnDonMau.add(lblTenThuoc);

        JLabel lblDonViTinh = new JLabel(sp.getDonViTinh().getTenDonViTinh());
        lblDonViTinh.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDonViTinh.setBounds(350, centerY - 28, 100, 30);
        pnDonMau.add(lblDonViTinh);

        JLabel lblLoThuoc = new JLabel("Lô: " + sp.getMaSanPham() + " - SL: 20");
        lblLoThuoc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLoThuoc.setForeground(new Color(80, 80, 80));
        lblLoThuoc.setBounds(168, centerY + 12, 320, 25);
        pnDonMau.add(lblLoThuoc);

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
        pnTangGiam.add(btnTang, BorderLayout.EAST);

        JLabel lblDonGia = new JLabel(String.format("%,.0f vnđ", sp.getGiaBan()));
        lblDonGia.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDonGia.setBounds(700, centerY, 120, 29);
        pnDonMau.add(lblDonGia);

        JLabel lblTongTien = new JLabel(String.format("%,.0f vnđ", sp.getGiaBan()));
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTongTien.setBounds(850, centerY, 120, 29);
        pnDonMau.add(lblTongTien);

        JButton btnXoa = new JButton();
        btnXoa.setBounds(980, centerY, 35, 35);
        ImageIcon iconBin = new ImageIcon(getClass().getResource("/images/bin.png"));
        Image img = iconBin.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        btnXoa.setIcon(new ImageIcon(img));
        btnXoa.setBorderPainted(false);
        btnXoa.setContentAreaFilled(false);
        btnXoa.setFocusPainted(false);
        btnXoa.setOpaque(false);
        btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnDonMau.add(btnXoa);

        // ==== SỰ KIỆN TĂNG GIẢM ====
        btnTang.addActionListener(e -> {
            try {
                int sl = Integer.parseInt(txtSoLuong.getText().trim());
                txtSoLuong.setText(String.valueOf(sl + 1));
            } catch (NumberFormatException ex) {
                txtSoLuong.setText("1");
            }
        });

        btnGiam.addActionListener(e -> {
            try {
                int sl = Integer.parseInt(txtSoLuong.getText().trim());
                if (sl > 1) txtSoLuong.setText(String.valueOf(sl - 1));
            } catch (NumberFormatException ex) {
                txtSoLuong.setText("1");
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
            JFrame frame = new JFrame("Thêm Phiếu Nhập");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new ThemPhieuNhap_GUI());
            frame.setVisible(true);
        });
    }
}