/**
 * @author Quốc Khánh
 * @version 2.0
 * @since Oct 14, 2025
 *
 * Mô tả: Giao diện bán hàng - danh sách sản phẩm tự sinh ở vùng CENTER.
 */

package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.ArrayList;
import java.util.List;

import customcomponent.PillButton;

public class BanHang_GUI extends JPanel {

    private JTextField txtTimThuoc;
    private JPanel pnCotPhaiCenter; // vùng center chính
    private JPanel pnDanhSachDon;   // vùng chứa các đơn hàng
	private int COT_PHAI_HEAD_HEIGHT = 88;
    
    public BanHang_GUI() {
    	this.setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
    	
    	setLayout(new BorderLayout());

    	
        // ===== HEADER =====
        JPanel pnCotPhaiHead = new JPanel(null);
        pnCotPhaiHead.setPreferredSize(new Dimension(1073, 88));
        pnCotPhaiHead.setBackground(new Color(0xE3F2F5));
        add(pnCotPhaiHead, BorderLayout.NORTH);

        // Ô tìm kiếm
        txtTimThuoc = new JTextField("Tìm theo mã, tên...");
        txtTimThuoc.setFont(new Font("Tahoma", Font.PLAIN, 16));
        txtTimThuoc.setBounds(25, 10, 342, 68);
        txtTimThuoc.setBorder(new LineBorder(new Color(0x00C0E2), 2, false));
        txtTimThuoc.setBackground(Color.WHITE);
        pnCotPhaiHead.add(txtTimThuoc);

        // Nút viên thuốc 2 màu
        JButton btnThemDon = new PillButton("Thêm đơn");
        btnThemDon.setBounds(400, 20, 169, 48);
        pnCotPhaiHead.add(btnThemDon);
        
        JPanel pnDonMot = new JPanel();
        pnDonMot.setBounds(612, 52, 160, 26);
        pnCotPhaiHead.add(pnDonMot);
        pnDonMot.setLayout(new BorderLayout(0, 0));
        
        JButton btnDon1 = new JButton("Đơn Hàng 1");
        btnDon1.setFont(new Font("Tahoma", Font.PLAIN, 14));
        pnDonMot.add(btnDon1, BorderLayout.CENTER);
        
        JButton btnXoa = new JButton("X");
        pnDonMot.add(btnXoa, BorderLayout.EAST);

        // ===== CENTER (DANH SÁCH SẢN PHẨM) =====
        pnCotPhaiCenter = new JPanel();
        pnCotPhaiCenter.setPreferredSize(new Dimension(1073, 992));
        pnCotPhaiCenter.setBackground(Color.WHITE);
        add(pnCotPhaiCenter, BorderLayout.CENTER);
        pnCotPhaiCenter.setBorder(new CompoundBorder(
        	    new LineBorder(new Color(0x00C853), 3, true), // viền xanh lá
        	    new EmptyBorder(5, 5, 5, 5) ));              // padding: top, left, bottom, right
        pnCotPhaiCenter.setLayout(new BorderLayout(0, 0));


        // Panel chứa danh sách các đơn hàng
        pnDanhSachDon = new JPanel();
        pnDanhSachDon.setLayout(new BoxLayout(pnDanhSachDon, BoxLayout.Y_AXIS));
        pnDanhSachDon.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(pnDanhSachDon);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); // ẩn thanh cuộn
        scrollPane.getVerticalScrollBar().setOpaque(false); // trong suốt
        pnCotPhaiCenter.add(scrollPane);

        // ===== CỘT PHẢI PHỤ =====
        JPanel pnCotPhaiRight = new JPanel();
        pnCotPhaiRight.setPreferredSize(new Dimension(1920 - 383 - 1073, 1080));
        pnCotPhaiRight.setBackground(Color.WHITE);
        pnCotPhaiRight.setBorder(new EmptyBorder(20, 20, 20, 20)); // padding tổng thể
        pnCotPhaiRight.setLayout(new BoxLayout(pnCotPhaiRight, BoxLayout.Y_AXIS));
        add(pnCotPhaiRight, BorderLayout.EAST);
     // ==== TÊN NHÂN VIÊN & THỜI GIAN ====
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
        lineNV.setForeground(new Color(200, 200, 200)); // xám nhẹ
        lineNV.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        pnCotPhaiRight.add(Box.createVerticalStrut(4));
        pnCotPhaiRight.add(lineNV);
        pnCotPhaiRight.add(Box.createVerticalStrut(10));
        
        // Ô tìm khách hàng
        JTextField txtTimKH = new JTextField("🔍 Số điện thoại khách hàng (F4)");
        txtTimKH.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTimKH.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtTimKH.setBorder(new LineBorder(new Color(0x00C0E2), 2, true));
        pnCotPhaiRight.add(txtTimKH);
        pnCotPhaiRight.add(Box.createVerticalStrut(15));

        // Label thông tin khách hàng
        pnCotPhaiRight.add(makeLabel("Tên khách hàng:", "Chu Anh Khôi"));
        pnCotPhaiRight.add(makeLabel("Tổng tiền hàng:", "140,000 vnd"));
        pnCotPhaiRight.add(makeLabel("Giảm giá sản phẩm:", "2,000 vnd"));
        pnCotPhaiRight.add(makeLabel("Giảm giá hóa đơn:", "5,000 vnd"));

        // Label giảm % đặc biệt
        JLabel lblGiamPhanTram = new JLabel("-5% Cuối tuần");
        lblGiamPhanTram.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblGiamPhanTram.setForeground(Color.RED);
        lblGiamPhanTram.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnCotPhaiRight.add(lblGiamPhanTram);
        pnCotPhaiRight.add(Box.createVerticalStrut(10));

        // Tổng hóa đơn
        JLabel lblTongHD = new JLabel("Tổng hóa đơn: 133,000 vnd");
        lblTongHD.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongHD.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnCotPhaiRight.add(lblTongHD);
        pnCotPhaiRight.add(Box.createVerticalStrut(10));

        // Ô nhập tiền khách đưa
        JPanel pnTienKhach = new JPanel(new BorderLayout(5, 5));
        pnTienKhach.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        pnTienKhach.setOpaque(false);
        JLabel lblTienKhach = new JLabel("Tiền khách đưa:");
        lblTienKhach.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField txtTienKhach = new JTextField("133,000");
        txtTienKhach.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTienKhach.setHorizontalAlignment(SwingConstants.RIGHT);
        pnTienKhach.add(lblTienKhach, BorderLayout.WEST);
        pnTienKhach.add(txtTienKhach, BorderLayout.CENTER);
        pnCotPhaiRight.add(pnTienKhach);
        pnCotPhaiRight.add(Box.createVerticalStrut(10));

        // Tiền thừa
        pnCotPhaiRight.add(makeLabel("Tiền thừa:", "0 vnd"));
        pnCotPhaiRight.add(Box.createVerticalStrut(15));

        // ====== DÒNG GỢI Ý TIỀN ======
        String[] goiY = {"133,000", "135,000", "140,000", "150,000", "200,000", "500,000"};
        JPanel pnGoiY = new JPanel(new GridLayout(2, 3, 10, 10));
        pnGoiY.setOpaque(false);
        for (String s : goiY) {
            JButton btn = new JButton(s);
            btn.setFocusPainted(false);
            btn.setBackground(new Color(0xF7F7F7));
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btn.setBorder(new LineBorder(new Color(0xDDDDDD), 1, true));
            pnGoiY.add(btn);
        }
        pnCotPhaiRight.add(pnGoiY);
        pnCotPhaiRight.add(Box.createVerticalStrut(20));

        // ====== NÚT BÁN HÀNG ======
        JButton btnBanHang = new JButton("Bán hàng");
        btnBanHang.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnBanHang.setForeground(Color.BLACK);
        btnBanHang.setBackground(new Color(0xFBE9E7));
        btnBanHang.setPreferredSize(new Dimension(250, 60));
        btnBanHang.setBorder(new LineBorder(new Color(0xE0E0E0), 2, true));
        btnBanHang.setFocusPainted(false);
        btnBanHang.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnCotPhaiRight.add(Box.createVerticalStrut(30));
        pnCotPhaiRight.add(btnBanHang);

        // ===== SINH DỮ LIỆU GIẢ & GEN PANEL =====
        List<Product> dsSanPham = new ArrayList<>();
        dsSanPham.add(new Product("Paracetamol", "LO0001 - 10/12/2025 - SL: 20", "500mg", "Hộp", 1, 100_000));
        dsSanPham.add(new Product("Decolgen", "LO0002 - 01/12/2026 - SL: 50", "250mg", "Viên", 3, 7_000));
        dsSanPham.add(new Product("Panadol Extra", "LO0003 - 05/03/2026 - SL: 100", "500mg", "Vỉ", 2, 15_000));
        dsSanPham.add(new Product("Efferalgan", "LO0004 - 22/01/2026 - SL: 80", "500mg", "Hộp", 5, 12_000));
        dsSanPham.add(new Product("Efferalgan", "LO0004 - 22/01/2026 - SL: 80", "500mg", "Hộp", 5, 12_000));
        dsSanPham.add(new Product("Efferalgan", "LO0004 - 22/01/2026 - SL: 80", "500mg", "Hộp", 5, 12_000));
        dsSanPham.add(new Product("Efferalgan", "LO0004 - 22/01/2026 - SL: 80", "500mg", "Hộp", 5, 12_000));
        dsSanPham.add(new Product("Efferalgan", "LO0004 - 22/01/2026 - SL: 80", "500mg", "Hộp", 5, 12_000));
        
        for (Product sp : dsSanPham) {
            pnDanhSachDon.add(createDonPanel(sp));
        }
        
    }

    /**
     * Tạo panel đơn hàng (theo mẫu cũ)
     */
    private JPanel createDonPanel(Product sp) {
        JPanel pnDonMau = new JPanel();
        pnDonMau.setPreferredSize(new Dimension(1040, 120));
        pnDonMau.setLayout(null);
        pnDonMau.setBackground(new Color(0xFFFFFF));
        pnDonMau.setBorder(new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        // Ảnh
        JLabel lblHinhAnh = new JLabel("Ảnh");
        lblHinhAnh.setHorizontalAlignment(SwingConstants.CENTER);
        lblHinhAnh.setBorder(new LineBorder(Color.LIGHT_GRAY));
        lblHinhAnh.setBounds(27, 10, 100, 100);
        pnDonMau.add(lblHinhAnh);

        // Tên thuốc
        JLabel lblTenThuoc = new JLabel(sp.tenThuoc);
        lblTenThuoc.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTenThuoc.setBounds(168, 20, 200, 29);
        pnDonMau.add(lblTenThuoc);

        // Đơn vị tính
        JLabel lblDonViTinh = new JLabel(sp.donViTinh);
        lblDonViTinh.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDonViTinh.setBounds(350, 20, 100, 29);
        pnDonMau.add(lblDonViTinh);

        // Lô hàng
        JLabel lblLoHang = new JLabel(sp.loHang);
        lblLoHang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblLoHang.setBounds(168, 70, 320, 29);
        pnDonMau.add(lblLoHang);

        // Panel tăng giảm
        JPanel pnTangGiam = new JPanel();
        pnTangGiam.setBounds(500, 40, 137, 36);
        pnTangGiam.setLayout(new BorderLayout(0, 0));
        pnDonMau.add(pnTangGiam);

        JButton btnGiam = new JButton("-");
        btnGiam.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pnTangGiam.add(btnGiam, BorderLayout.WEST);

        JTextField txtSoLuong = new JTextField(String.valueOf(sp.soLuong));
        txtSoLuong.setHorizontalAlignment(SwingConstants.CENTER);
        txtSoLuong.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnTangGiam.add(txtSoLuong, BorderLayout.CENTER);

        JButton btnTang = new JButton("+");
        btnTang.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pnTangGiam.add(btnTang, BorderLayout.EAST);

        // Đơn giá
        JLabel lblDonGia = new JLabel(sp.donGia + " vnđ");
        lblDonGia.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDonGia.setBounds(700, 30, 120, 29);
        pnDonMau.add(lblDonGia);

        // Tổng tiền
        JLabel lblTongTien = new JLabel((sp.donGia * sp.soLuong) + " vnđ");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongTien.setBounds(850, 30, 120, 29);
        pnDonMau.add(lblTongTien);

        // Nút xóa
        JButton btnXoa = new JButton("Xóa");
        btnXoa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnXoa.setBounds(980, 40, 70, 25);
        pnDonMau.add(btnXoa);

        return pnDonMau;
    }

    // ===== CLASS GIẢ DỮ LIỆU =====
    class Product {
        String tenThuoc, loHang, hamLuong, donViTinh;
        int soLuong, donGia;

        public Product(String tenThuoc, String loHang, String hamLuong, String donViTinh, int soLuong, int donGia) {
            this.tenThuoc = tenThuoc;
            this.loHang = loHang;
            this.hamLuong = hamLuong;
            this.donViTinh = donViTinh;
            this.soLuong = soLuong;
            this.donGia = donGia;
        }
    }
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            try {
                JFrame frame = new JFrame("Test Bán Hàng");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1280, 800);
                frame.setLocationRelativeTo(null);
                frame.setContentPane(new BanHang_GUI());
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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
}
