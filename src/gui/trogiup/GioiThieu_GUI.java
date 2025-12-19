package gui.trogiup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


public class GioiThieu_GUI extends JPanel {

    // Colors
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);      // Blue
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94);      // Dark Gray
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 252);
    private static final Color HEADER_BG = new Color(0xE3F2F5);

    // App Info
    private static final String APP_NAME = "PHẦN MỀM QUẢN LÝ HIỆU THUỐC";
    private static final String APP_VERSION = "1.0.0";

    public GioiThieu_GUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Header Panel
        JPanel headerPanel = createHeaderPanel();

        // Content Panel - căn giữa
        JPanel contentWrapper = new JPanel(new java.awt.GridBagLayout());
        contentWrapper.setBackground(BACKGROUND_COLOR);
        contentWrapper.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentWrapper.add(createAppInfoSection());

        add(headerPanel, BorderLayout.NORTH);
        add(contentWrapper, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(HEADER_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setPreferredSize(new Dimension(0, 140));
        headerPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        // App Logo/Icon
        JLabel lblIcon = new JLabel("💊");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        lblIcon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel(APP_NAME);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(SECONDARY_COLOR);
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblVersion = new JLabel("Phiên bản " + APP_VERSION);
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblVersion.setForeground(PRIMARY_COLOR);
        lblVersion.setAlignmentX(CENTER_ALIGNMENT);

        headerPanel.add(Box.createVerticalGlue());
        headerPanel.add(lblIcon);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(lblTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(lblVersion);
        headerPanel.add(Box.createVerticalGlue());

        return headerPanel;
    }

    // ======================== THÔNG TIN ỨNG DỤNG ========================
    private JPanel createAppInfoSection() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(700, 420));

        // Header
        JPanel sectionHeader = createSectionHeader("📱", "THÔNG TIN ỨNG DỤNG", PRIMARY_COLOR);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(15, 25, 20, 25));

        JLabel lblContent = new JLabel("<html><div style='line-height: 1.8; font-size: 14px;'>"
                + "<table cellpadding='5' style='width:100%;'>"
                + "<tr><td style='color: #666; width: 150px;'><b>Tên ứng dụng:</b></td>"
                + "<td style='color: #333;'>Quản Lý Hiệu Thuốc</td></tr>"
                + "<tr><td style='color: #666;'><b>Phiên bản:</b></td>"
                + "<td style='color: #333;'>" + APP_VERSION + " (Stable Release)</td></tr>"
                + "<tr><td style='color: #666;'><b>Ngày phát hành:</b></td>"
                + "<td style='color: #333;'>Tháng 12, 2025</td></tr>"
                + "</table>"
                + "<p style='margin-top: 15px; color: #555;'>"
                + "<b>Mô tả:</b> Phần mềm hỗ trợ các nhà thuốc quản lý hoạt động kinh doanh với các tính năng:</p>"
                + "<ul style='margin: 10px 0; padding-left: 25px; color: #555; line-height: 1.8;'>"
                + "<li>Bán hàng và xử lý đơn hàng</li>"
                + "<li>Quản lý kho và nhập hàng</li>"
                + "<li>Xử lý trả hàng / hủy hàng</li>"
                + "<li>Thống kê doanh thu và báo cáo</li>"
                + "<li>Tra cứu thông tin</li>"
                + "<li>Quản lý khách hàng và nhân viên</li>"
                + "</ul>"
                + "</div></html>");
        lblContent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(lblContent);

        panel.add(sectionHeader, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    // ======================== HELPER METHODS ========================
    private JPanel createSectionHeader(String icon, String title, Color color) {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(color);

        headerPanel.add(iconLabel);
        headerPanel.add(titleLabel);

        return headerPanel;
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Shadow effect
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, 15, 15);
                
                // Card background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 15, 15);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 8, 8));
        return panel;
    }

    /**
     * Method static để mở dialog Giới thiệu - có thể gọi từ bất kỳ đâu
     */
    public static void moGioiThieu() {
        javax.swing.JDialog dialog = new javax.swing.JDialog();
        dialog.setTitle("Giới thiệu");
        dialog.setModal(true);
        dialog.setContentPane(new GioiThieu_GUI());
        dialog.setSize(900, 650);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}