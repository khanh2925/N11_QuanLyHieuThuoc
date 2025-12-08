package gui.quanly;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import com.toedter.calendar.JDateChooser;

import component.button.PillButton;
import gui.panel.ThongKeTheoNam_Panel;
import gui.panel.ThongKeTheoNgay_Panel;
import gui.panel.ThongKeTheoThang_Panel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ThongKeDoanhThu_GUI extends JPanel {

    private JPanel pnCenter;
    private JPanel pnHeader;
    private JDateChooser ngayBatDau_DataChoose;
    
    // === KHAI BÁO CHO CARDLAYOUT ===
    private JPanel pnCardContainer; // Panel chứa các "lá bài" giao diện
    private CardLayout cardLayout; // Đối tượng quản lý việc chuyển đổi
    
    // Tên hằng số cho các lá bài
    private final static String VIEW_THEO_NGAY = "VIEW_THEO_NGAY";
    private final static String VIEW_THEO_THANG = "VIEW_THEO_THANG";
    private final static String VIEW_THEO_NAM = "VIEW_THEO_NAM";
    // ===============================

    public ThongKeDoanhThu_GUI() {
        this.setPreferredSize(new Dimension(1280, 800));
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1280, 800));

        // ===== HEADER =====
        pnHeader = new JPanel();
        pnHeader.setPreferredSize(new Dimension(1073, 50));
        pnHeader.setBackground(new Color(0xE3F2F5));
        add(pnHeader, BorderLayout.NORTH);
        pnHeader.setLayout(null);
        
        // --- NÚT THEO NGÀY ---
        JButton btnTheoNgay = new PillButton("Theo Ngày");
        btnTheoNgay.setBounds(10, 5, 120, 40);
        pnHeader.add(btnTheoNgay);
        
        // --- NÚT THEO THÁNG ---
        JButton btnTheoThang = new PillButton("Theo Tháng");
        btnTheoThang.setBounds(170, 5, 120, 40);
        pnHeader.add(btnTheoThang);
        
        // --- NÚT THEO NĂM ---
        JButton btnTheoNam = new PillButton("Theo Năm");
        btnTheoNam.setBounds(330, 5, 120, 40);
        pnHeader.add(btnTheoNam);

        // ===== CENTER =====
        pnCenter = new JPanel();
        pnCenter.setBackground(new Color(255, 255, 255));
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(pnCenter, BorderLayout.CENTER);
        pnCenter.setLayout(new BorderLayout()); // Layout chính của vùng Center

        // === THIẾT LẬP CARDLAYOUT ===
        
        // 1. Khởi tạo CardLayout và Panel chứa các card
        cardLayout = new CardLayout();
        pnCardContainer = new JPanel(cardLayout);
        
        // 2. Tạo các instance của các panel giao diện
        ThongKeTheoNgay_Panel viewNgay = new ThongKeTheoNgay_Panel();
        ThongKeTheoThang_Panel viewThang = new ThongKeTheoThang_Panel();
        ThongKeTheoNam_Panel viewNam = new ThongKeTheoNam_Panel();
        
        // 3. Thêm các panel vào container với tên định danh
        pnCardContainer.add(viewNgay, VIEW_THEO_NGAY);
        pnCardContainer.add(viewThang, VIEW_THEO_THANG);
        pnCardContainer.add(viewNam, VIEW_THEO_NAM);
        
        // 4. Thêm container vào vùng trung tâm của pnCenter
        pnCenter.add(pnCardContainer, BorderLayout.CENTER);
        
        // ===============================

        // === THÊM SỰ KIỆN CHO CÁC NÚT ===
        
        btnTheoNgay.addActionListener(e -> {
            cardLayout.show(pnCardContainer, VIEW_THEO_NGAY);

        });
        
        btnTheoThang.addActionListener(e -> {
            cardLayout.show(pnCardContainer, VIEW_THEO_THANG);
        });
        
        btnTheoNam.addActionListener(e -> {
            cardLayout.show(pnCardContainer, VIEW_THEO_NAM);
        });
        
        // Hiển thị giao diện mặc định khi khởi động
        cardLayout.show(pnCardContainer, VIEW_THEO_NGAY);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Thống kê doanh thu");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new ThongKeDoanhThu_GUI());
            frame.setVisible(true);
        });
    }
}