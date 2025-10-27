/**
 * @author Quốc Khánh
 * @version 3.0
 * @since Oct 16, 2025
 *
 * Mô tả: Khung giao diện trống - giữ lại bố cục chính để clone trang khác.
 */

package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class TraCuuKeSanPham_GUI extends JPanel {

    private JPanel pnCenter;   // vùng trung tâm
    private JPanel pnHeader;   // vùng đầu trang
    private JPanel pnLeft;    // vùng cột phải
	private JTextField txtTimThuoc;
	private JLabel lblThongTin;

    public TraCuuKeSanPham_GUI() {
        this.setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1537, 1168));

        // ===== HEADER =====
        pnHeader = new JPanel();
        pnHeader.setPreferredSize(new Dimension(1073, 88));
        pnHeader.setBackground(new Color(0xE3F2F5));
        pnHeader.setLayout(null);
        add(pnHeader, BorderLayout.NORTH);
        // Ô tìm kiếm
        txtTimThuoc = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimThuoc, "Tìm theo mã, tên...");
        txtTimThuoc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtTimThuoc.setBounds(10, 17, 420, 60);
        txtTimThuoc.setBorder(new RoundedBorder(20));
        txtTimThuoc.setBackground(Color.WHITE);
        txtTimThuoc.setForeground(Color.GRAY);
        pnHeader.add(txtTimThuoc);

     // ===== CENTER =====
        Image img = new ImageIcon(getClass().getResource("/images/ke-thuoc.png")).getImage(); // đường dẫn ảnh
        pnCenter = new customcomponent.ImagePanel(img);
        pnCenter.setLayout(null); 
        add(pnCenter, BorderLayout.CENTER); 


     // ===== Left =====
        pnLeft = new JPanel();
        pnLeft.setPreferredSize(new Dimension(350, 1080));
        pnLeft.setBackground(Color.WHITE);
        pnLeft.setBorder(new EmptyBorder(20, 20, 20, 20));
        pnLeft.setLayout(null);
        add(pnLeft, BorderLayout.WEST);

        // ----- Tiêu đề -----
        JLabel lblThongTin = new JLabel("Thông tin sản phẩm");
        lblThongTin.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblThongTin.setBounds(10, 10, 300, 30);
        pnLeft.add(lblThongTin);

        JSeparator sep1 = new JSeparator();
        sep1.setBounds(10, 45, 310, 2);
        pnLeft.add(sep1);

        // ----- Label & TextField: Tên thuốc -----
        JLabel lblTenThuoc = new JLabel("Tên thuốc:");
        lblTenThuoc.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblTenThuoc.setBounds(10, 60, 100, 25);
        pnLeft.add(lblTenThuoc);

        JTextField txtTenThuoc = new JTextField("Paracetamol 500mg");
        txtTenThuoc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTenThuoc.setBounds(10, 85, 300, 30);
        txtTenThuoc.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        pnLeft.add(txtTenThuoc);

        // ----- Bảng thông tin lô -----
        String[] colNames = {"Mã lô", "HSD", "Số lượng"};
        Object[][] duLieuLo = {
            {"LO-001", "12/2026", 120},
            {"LO-002", "05/2027", 200},
            {"LO-003", "09/2027", 150}
        };

        DefaultTableModel modelLo = new DefaultTableModel(duLieuLo, colNames);
        JTable tblLo = new JTable(modelLo);
        tblLo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblLo.setRowHeight(28);
        tblLo.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblLo.getTableHeader().setBackground(new Color(0x00C0E2));
        tblLo.getTableHeader().setForeground(Color.WHITE);
        tblLo.setGridColor(new Color(230, 230, 230));

        JScrollPane spLo = new JScrollPane(tblLo);
        spLo.setBounds(10, 135, 310, 120);
        spLo.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        pnLeft.add(spLo);

        // ----- Label & TextField: Vị trí kệ -----
        JLabel lblViTri = new JLabel("Vị trí kệ:");
        lblViTri.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblViTri.setBounds(10, 270, 100, 25);
        pnLeft.add(lblViTri);

        JTextField txtViTri = new JTextField("Kệ A2 - Tầng 3");
        txtViTri.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtViTri.setBounds(10, 295, 300, 30);
        txtViTri.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        pnLeft.add(txtViTri);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tra cứu kệ sản phẩm");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new TraCuuKeSanPham_GUI());
            frame.setVisible(true);
        });
    }
}
