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

import customcomponent.PlaceholderSupport;

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
        txtTimThuoc.setBounds(10, 10, 284, 68);
        txtTimThuoc.setBorder(new LineBorder(new Color(0x00C0E2), 2, true));
        txtTimThuoc.setBackground(Color.WHITE);
        txtTimThuoc.setForeground(Color.GRAY);
        pnHeader.add(txtTimThuoc);
        
        JLabel lblLoaiSP = new JLabel("Loại sản phẩm:");
        lblLoaiSP.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLoaiSP.setBounds(320, 10, 130, 25);
        pnHeader.add(lblLoaiSP);
        
        JComboBox<String> cbLoaiSP = new JComboBox<String>();
        cbLoaiSP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbLoaiSP.setBounds(320, 40, 200, 32);
        pnHeader.add(cbLoaiSP);

     // ===== CENTER =====
        Image img = new ImageIcon(getClass().getResource("/images/ke-thuoc.png")).getImage(); // đường dẫn ảnh
        pnCenter = new customcomponent.ImagePanel(img);
        pnCenter.setLayout(null); 
        add(pnCenter, BorderLayout.CENTER); 


        // ===== Left =====
        pnLeft = new JPanel(); 
        pnLeft.setPreferredSize(new Dimension(300, 1080)); 
        pnLeft.setBackground(new Color(255, 255, 255));
        pnLeft.setBorder(new EmptyBorder(10, 10, 10, 10)); 
        add(pnLeft, BorderLayout.WEST);
        pnLeft.setLayout(new BoxLayout(pnLeft, BoxLayout.Y_AXIS));
        
        lblThongTin = new JLabel("Thông tin");
        lblThongTin.setFont(new Font("Tahoma", Font.BOLD, 20));
        pnLeft.add(lblThongTin);
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
