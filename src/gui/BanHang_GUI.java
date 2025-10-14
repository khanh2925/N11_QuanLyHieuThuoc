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

public class BanHang_GUI {

    private JFrame frame;
    private JTextField txtTimThuoc;
    private JPanel pnCotPhaiCenter; // vùng center chính
    private JPanel pnDanhSachDon;   // vùng chứa các đơn hàng

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                BanHang_GUI window = new BanHang_GUI();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public BanHang_GUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Bán Hàng - Hiệu thuốc Hòa An");
        frame.setSize(1920, 1080);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        JPanel pnMain = new JPanel(new BorderLayout());
        frame.getContentPane().add(pnMain, BorderLayout.CENTER);

        // ===== CỘT TRÁI =====
        JPanel pnCotTrai = new JPanel();
        pnCotTrai.setPreferredSize(new Dimension(383, 1080));
        pnCotTrai.setBackground(new Color(128, 255, 255));
        pnMain.add(pnCotTrai, BorderLayout.WEST);

        // ===== CỘT PHẢI =====
        JPanel pnCotPhai = new JPanel(new BorderLayout());
        pnMain.add(pnCotPhai, BorderLayout.CENTER);

        // ===== HEADER =====
        JPanel pnCotPhaiHead = new JPanel(null);
        pnCotPhaiHead.setPreferredSize(new Dimension(1073, 88));
        pnCotPhaiHead.setBackground(new Color(0xE3F2F5));
        pnCotPhai.add(pnCotPhaiHead, BorderLayout.NORTH);

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
        pnCotPhai.add(pnCotPhaiCenter, BorderLayout.CENTER);
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
        pnCotPhaiRight.setBackground(new Color(255, 255, 102));
        pnCotPhai.add(pnCotPhaiRight, BorderLayout.EAST);

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
}
