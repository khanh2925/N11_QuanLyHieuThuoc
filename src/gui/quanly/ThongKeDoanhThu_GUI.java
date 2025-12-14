package gui.quanly;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import component.button.PillButton;
import gui.panel.ThongKeTheoNam_Panel;
import gui.panel.ThongKeTheoNgay_Panel;
import gui.panel.ThongKeTheoThang_Panel;

/**
 * Màn hình thống kê doanh thu với 3 tab:
 * 1. Theo ngày
 * 2. Theo tháng
 * 3. Theo năm
 */
public class ThongKeDoanhThu_GUI extends JPanel {

    private JPanel pnCenter;
    private JPanel pnHeader;

    // === KHAI BÁO CHO CARDLAYOUT ===
    private JPanel pnCardContainer;
    private CardLayout cardLayout;

    // Tên hằng số cho các tab
    private final static String VIEW_THEO_NGAY = "VIEW_THEO_NGAY";
    private final static String VIEW_THEO_THANG = "VIEW_THEO_THANG";
    private final static String VIEW_THEO_NAM = "VIEW_THEO_NAM";

    // Buttons để quản lý trạng thái active
    private JButton btnTheoNgay;
    private JButton btnTheoThang;
    private JButton btnTheoNam;
    private JButton currentActiveButton;

    // Màu sắc
    private final Color ACTIVE_COLOR = new Color(0x0077B6);
    private final Color INACTIVE_COLOR = new Color(0x6C757D);

    public ThongKeDoanhThu_GUI() {
        this.setPreferredSize(new Dimension(1280, 800));
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1280, 800));
        setBackground(Color.WHITE);

        // ===== HEADER =====
        pnHeader = new JPanel();
        pnHeader.setPreferredSize(new Dimension(1073, 50));
        pnHeader.setBackground(new Color(0xE3F2F5));
        add(pnHeader, BorderLayout.NORTH);
        pnHeader.setLayout(null);

        // --- NÚT THEO NGÀY ---
        btnTheoNgay = new PillButton("Theo Ngày");
        btnTheoNgay.setBounds(10, 5, 150, 40);
        pnHeader.add(btnTheoNgay);

        // --- NÚT THEO THÁNG ---
        btnTheoThang = new PillButton("Theo Tháng");
        btnTheoThang.setBounds(180, 5, 150, 40);
        pnHeader.add(btnTheoThang);

        // --- NÚT THEO NĂM ---
        btnTheoNam = new PillButton("Theo Năm");
        btnTheoNam.setBounds(350, 5, 150, 40);
        pnHeader.add(btnTheoNam);

        // ===== CENTER =====
        pnCenter = new JPanel();
        pnCenter.setBackground(new Color(255, 255, 255));
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(pnCenter, BorderLayout.CENTER);
        pnCenter.setLayout(new BorderLayout());

        // === THIẾT LẬP CARDLAYOUT ===
        cardLayout = new CardLayout();
        pnCardContainer = new JPanel(cardLayout);
        pnCardContainer.setBackground(Color.WHITE);

        // Tạo các panel
        ThongKeTheoNgay_Panel viewNgay = new ThongKeTheoNgay_Panel();
        ThongKeTheoThang_Panel viewThang = new ThongKeTheoThang_Panel();
        ThongKeTheoNam_Panel viewNam = new ThongKeTheoNam_Panel();

        // Thêm các panel vào container
        pnCardContainer.add(viewNgay, VIEW_THEO_NGAY);
        pnCardContainer.add(viewThang, VIEW_THEO_THANG);
        pnCardContainer.add(viewNam, VIEW_THEO_NAM);

        pnCenter.add(pnCardContainer, BorderLayout.CENTER);

        // === THÊM SỰ KIỆN CHO CÁC NÚT ===
        btnTheoNgay.addActionListener(e -> {
            cardLayout.show(pnCardContainer, VIEW_THEO_NGAY);
            setActiveButton(btnTheoNgay);
        });

        btnTheoThang.addActionListener(e -> {
            cardLayout.show(pnCardContainer, VIEW_THEO_THANG);
            setActiveButton(btnTheoThang);
        });

        btnTheoNam.addActionListener(e -> {
            cardLayout.show(pnCardContainer, VIEW_THEO_NAM);
            setActiveButton(btnTheoNam);
        });

        // Hiển thị giao diện mặc định
        cardLayout.show(pnCardContainer, VIEW_THEO_NGAY);
        setActiveButton(btnTheoNgay);
    }

    /**
     * Đặt button active và reset các button khác
     */
    private void setActiveButton(JButton button) {
        currentActiveButton = button;
        // Có thể thêm logic highlight button active ở đây nếu cần
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