package gui.trogiup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;


public class GioiThieu_GUI extends JPanel {

    // Colors
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);      // Blue
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94);      // Dark Gray
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);       // Green
    private static final Color TECH_COLOR = new Color(155, 89, 182);         // Purple
    private static final Color CREDITS_COLOR = new Color(230, 126, 34);      // Orange
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 252);
    private static final Color HEADER_BG = new Color(0xE3F2F5);

    // App Info
    private static final String APP_NAME = "PHẦN MỀM QUẢN LÝ HIỆU THUỐC";
    private static final String APP_VERSION = "1.0.0";
    private static final String APP_DESCRIPTION = "Giải pháp quản lý cho nhà thuốc hiện đại";

    public GioiThieu_GUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Header Panel
        JPanel headerPanel = createHeaderPanel();

        // Content Panel với scroll
        JPanel contentPanel = createContentPanel();
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
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

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        // 1. Thông tin cơ bản về ứng dụng
        contentPanel.add(createAppInfoSection());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // 2. Thông tin nhà phát triển
        contentPanel.add(createDeveloperInfoSection());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // 3. Công nghệ & công cụ sử dụng
        contentPanel.add(createTechnologySection());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // 4. Ghi nhận & cảm ơn
        contentPanel.add(createCreditsSection());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        return contentPanel;
    }

    // ======================== 1. THÔNG TIN ỨNG DỤNG ========================
    private JPanel createAppInfoSection() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = createSectionHeader("📱", "THÔNG TIN ỨNG DỤNG", PRIMARY_COLOR);

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
                + "</table>"
                + "<p style='margin-top: 15px; color: #555;'>"
                + "<b>Mô tả:</b> Phần mềm hỗ trợ các nhà thuốc quản lý hoạt động kinh doanh với các tính năng:</p>"
                + "<ul style='margin: 10px 0; padding-left: 25px; color: #555; line-height: 1.8;'>"
                + "<li>Bán hàng và xử lý đơn hàng</li>"
                + "<li>Quản lý kho và nhập hàng</li>"
                + "<li>Xử lý trả hàng / hủy hàng</li>"
                + "<li>Thống kê doanh thu và báo cáo</li>"
                + "<li>Quản lý khách hàng và nhân viên</li>"
                + "</ul>"
                + "</div></html>");
        lblContent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(lblContent);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    // ======================== 2. THÔNG TIN NHÀ PHÁT TRIỂN ========================
    private JPanel createDeveloperInfoSection() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = createSectionHeader("👨‍💻", "THÔNG TIN NHÀ PHÁT TRIỂN", SECONDARY_COLOR);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(15, 25, 20, 25));

        JLabel lblContent = new JLabel("<html><div style='line-height: 1.8; font-size: 14px;'>"
                + "<table cellpadding='5' style='width:100%;'>"
                + "<tr><td style='color: #666; width: 150px;'><b>Nhóm phát triển:</b></td>"
                + "<td style='color: #333;'>Nhóm 11 - N11</td></tr>"
                + "<tr><td style='color: #666;'><b>Khoa:</b></td>"
                + "<td style='color: #333;'>Kỹ thuật phần mềm</td></tr>"
                + "<tr><td style='color: #666;'><b>Trường:</b></td>"
                + "<td style='color: #333;'>Đại học Công nghiệp TP.HCM (IUH)</td></tr>"
                + "</table>"
                + "</div></html>");
        lblContent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(lblContent);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    // ======================== 3. CÔNG NGHỆ & CÔNG CỤ ========================
    private JPanel createTechnologySection() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = createSectionHeader("🛠️", "CÔNG NGHỆ & CÔNG CỤ SỬ DỤNG", TECH_COLOR);

        // Content - 2 cột
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(15, 25, 20, 25));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 15);

        // Cột trái
        JLabel leftContent = new JLabel("<html><div style='line-height: 2.0; font-size: 14px;'>"
                + "<p style='font-weight: bold; color: #8e44ad; margin-bottom: 8px; font-size: 15px;'>💻 Ngôn ngữ lập trình</p>"
                + "<ul style='margin: 0 0 15px 0; padding-left: 25px; color: #555;'>"
                + "<li>Java (Phiên bản SE 21 - LTS)</li>"
                + "</ul>"
                + "<p style='font-weight: bold; color: #8e44ad; margin-bottom: 8px; font-size: 15px;'>📚 Thư viện & Framework</p>"
                + "<ul style='margin: 0 0 15px 0; padding-left: 25px; color: #555;'>"
                + "<li>Java Swing (GUI)</li>"
                + "<li>JDBC (Database Connection)</li>"
                + "<li>Apache POI (Excel Export)</li>"
                + "<li>JFreeChart (Charts)</li>"
                + "</ul>"
                + "</div></html>");
        leftContent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        leftContent.setVerticalAlignment(SwingConstants.TOP);

        // Cột phải
        JLabel rightContent = new JLabel("<html><div style='line-height: 2.0; font-size: 14px;'>"
                + "<p style='font-weight: bold; color: #8e44ad; margin-bottom: 8px; font-size: 15px;'>🗄️ Cơ sở dữ liệu</p>"
                + "<ul style='margin: 0 0 15px 0; padding-left: 25px; color: #555;'>"
                + "<li>Microsoft SQL Server 2022</li>"
                + "</ul>"
                + "<p style='font-weight: bold; color: #8e44ad; margin-bottom: 8px; font-size: 15px;'>🔧 Công cụ phát triển</p>"
                + "<ul style='margin: 0 0 15px 0; padding-left: 25px; color: #555;'>"
                + "<li>Eclipse IDE</li>"
                + "<li>SQL Server Management Studio</li>"
                + "<li>Git & GitHub</li>"
                + "</ul>"
                + "</div></html>");
        rightContent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rightContent.setVerticalAlignment(SwingConstants.TOP);

        gbc.gridx = 0;
        contentPanel.add(leftContent, gbc);
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 15, 0, 0);
        contentPanel.add(rightContent, gbc);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    // ======================== 4. GHI NHẬN & CẢM ƠN ========================
    private JPanel createCreditsSection() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = createSectionHeader("🏆", "GHI NHẬN & CẢM ƠN", CREDITS_COLOR);

        // Main content panel
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(new EmptyBorder(15, 25, 20, 25));

        // Content - 2 cột
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 0, 30);

        // Cột trái - Thành viên nhóm
        JLabel leftContent = new JLabel("<html><div style='line-height: 2.0; font-size: 14px;'>"
                + "<p style='font-weight: bold; color: #e67e22; margin-bottom: 10px; font-size: 15px;'>👥 Thành viên nhóm</p>"
                + "<table cellpadding='3' style='color: #555;'>"
                + "<tr><td>1.</td><td><b>Phạm Quốc Khánh</b></td><td style='color: #888;'>- Nhóm trưởng</td></tr>"
                + "<tr><td>2.</td><td><b>Lê Thanh Kha</b></td><td style='color: #888;'>- Thành viên</td></tr>"
                + "<tr><td>3.</td><td><b>Chu Anh Khôi</b></td><td style='color: #888;'>- Thành viên</td></tr>"
                + "<tr><td>4.</td><td><b>Huỳnh Hoài Thanh</b></td><td style='color: #888;'>- Thành viên</td></tr>"
                + "</table>"
                + "</div></html>");
        leftContent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        leftContent.setVerticalAlignment(SwingConstants.TOP);

        // Cột phải - Người hướng dẫn
        JLabel rightContent = new JLabel("<html><div style='line-height: 2.0; font-size: 14px;'>"
                + "<p style='font-weight: bold; color: #e67e22; margin-bottom: 10px; font-size: 15px;'>👨‍🏫 Người hướng dẫn</p>"
                + "<table cellpadding='3' style='color: #555;'>"
                + "<tr><td><b>Trần Thị Anh Thi</b></td></tr>"
                + "<tr><td style='color: #888;'>Giảng viên hướng dẫn</td></tr>"
                + "<tr><td style='color: #888;'>Khoa CNTT - ĐH Công nghiệp TP.HCM</td></tr>"
                + "</table>"
                + "</div></html>");
        rightContent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rightContent.setVerticalAlignment(SwingConstants.TOP);

        gbc.gridx = 0;
        contentPanel.add(leftContent, gbc);
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 30, 0, 0);
        contentPanel.add(rightContent, gbc);

        // Dòng cảm ơn - căn giữa
        JLabel thankYouLabel = new JLabel("<html><div style='text-align: center; padding: 15px 0;'>"
                + "<p style='color: #777; font-style: italic; font-size: 14px;'>"
                + "\"Xin chân thành cảm ơn cô đã tận tình hướng dẫn và hỗ trợ trong suốt quá trình thực hiện đồ án.\"</p>"
                + "</div></html>");
        thankYouLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        thankYouLabel.setHorizontalAlignment(SwingConstants.CENTER);
        thankYouLabel.setAlignmentX(CENTER_ALIGNMENT);

        mainContent.add(contentPanel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 10)));
        mainContent.add(thankYouLabel);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(mainContent, BorderLayout.CENTER);

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
}
