package gui.quanly;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import component.button.PillButton;
import gui.panel.TopSanPhamBanChay_Panel;
import gui.panel.TonKhoThap_Panel;
import gui.panel.SapHetHan_Panel;
import gui.panel.ThongKeTheoLoai_Panel;

/**
 * Màn hình thống kê sản phẩm với 4 tab:
 * 1. Top sản phẩm bán chạy
 * 2. Sản phẩm tồn kho thấp
 * 3. Sản phẩm sắp hết hạn
 * 4. Thống kê theo loại sản phẩm
 */
public class ThongKeSanPham_GUI extends JPanel {

    private JPanel pnCenter;
    private JPanel pnHeader;

    // === KHAI BÁO CHO CARDLAYOUT ===
    private JPanel pnCardContainer;
    private CardLayout cardLayout;

    // Tên hằng số cho các tab
    private final static String VIEW_TOP_BAN_CHAY = "VIEW_TOP_BAN_CHAY";
    private final static String VIEW_TON_KHO_THAP = "VIEW_TON_KHO_THAP";
    private final static String VIEW_SAP_HET_HAN = "VIEW_SAP_HET_HAN";
    private final static String VIEW_THEO_LOAI = "VIEW_THEO_LOAI";

    // Buttons để quản lý trạng thái active
    private JButton btnTopBanChay;
    private JButton btnTonKhoThap;
    private JButton btnSapHetHan;
    private JButton btnTheoLoai;
    private JButton currentActiveButton;

    // Màu sắc
    private final Color ACTIVE_COLOR = new Color(0x0077B6);
    private final Color INACTIVE_COLOR = new Color(0x6C757D);

    public ThongKeSanPham_GUI() {
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

        // --- NÚT TOP BÁN CHẠY ---
        btnTopBanChay = new PillButton("🏆 Top Bán Chạy");
        btnTopBanChay.setBounds(10, 5, 150, 40);
        pnHeader.add(btnTopBanChay);

        // --- NÚT TỒN KHO THẤP ---
        btnTonKhoThap = new PillButton("📦 Tồn Kho Thấp");
        btnTonKhoThap.setBounds(180, 5, 150, 40);
        pnHeader.add(btnTonKhoThap);

        // --- NÚT SẮP HẾT HẠN ---
        btnSapHetHan = new PillButton("⏰ Sắp Hết Hạn");
        btnSapHetHan.setBounds(350, 5, 150, 40);
        pnHeader.add(btnSapHetHan);

        // --- NÚT THEO LOẠI ---
        btnTheoLoai = new PillButton("📊 Theo Loại");
        btnTheoLoai.setBounds(520, 5, 150, 40);
        pnHeader.add(btnTheoLoai);

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
        TopSanPhamBanChay_Panel viewTopBanChay = new TopSanPhamBanChay_Panel();
        TonKhoThap_Panel viewTonKhoThap = new TonKhoThap_Panel();
        SapHetHan_Panel viewSapHetHan = new SapHetHan_Panel();
        ThongKeTheoLoai_Panel viewTheoLoai = new ThongKeTheoLoai_Panel();

        // Thêm các panel vào container
        pnCardContainer.add(viewTopBanChay, VIEW_TOP_BAN_CHAY);
        pnCardContainer.add(viewTonKhoThap, VIEW_TON_KHO_THAP);
        pnCardContainer.add(viewSapHetHan, VIEW_SAP_HET_HAN);
        pnCardContainer.add(viewTheoLoai, VIEW_THEO_LOAI);

        pnCenter.add(pnCardContainer, BorderLayout.CENTER);

        // === THÊM SỰ KIỆN CHO CÁC NÚT ===
        btnTopBanChay.addActionListener(e -> {
            cardLayout.show(pnCardContainer, VIEW_TOP_BAN_CHAY);
            setActiveButton(btnTopBanChay);
        });

        btnTonKhoThap.addActionListener(e -> {
            cardLayout.show(pnCardContainer, VIEW_TON_KHO_THAP);
            setActiveButton(btnTonKhoThap);
        });

        btnSapHetHan.addActionListener(e -> {
            cardLayout.show(pnCardContainer, VIEW_SAP_HET_HAN);
            setActiveButton(btnSapHetHan);
        });

        btnTheoLoai.addActionListener(e -> {
            cardLayout.show(pnCardContainer, VIEW_THEO_LOAI);
            setActiveButton(btnTheoLoai);
        });

        // Hiển thị giao diện mặc định
        cardLayout.show(pnCardContainer, VIEW_TOP_BAN_CHAY);
        setActiveButton(btnTopBanChay);
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
            JFrame frame = new JFrame("Thống kê sản phẩm");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new ThongKeSanPham_GUI());
            frame.setVisible(true);
        });
    }
}
