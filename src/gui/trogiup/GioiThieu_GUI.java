package gui.trogiup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
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

    private static final Color PRIMARY_COLOR = new Color(0, 102, 204);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color EMPLOYEE_COLOR = new Color(46, 204, 113);
    private static final Color MANAGER_COLOR = new Color(155, 89, 182);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 252);

    public GioiThieu_GUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Header Panel với gradient
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
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(0, 0, PRIMARY_COLOR, getWidth(), 0, SECONDARY_COLOR);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setPreferredSize(new Dimension(0, 120));
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("PHẦN MỀM QUẢN LÝ HIỆU THUỐC TÂY");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Giải pháp quản lý toàn diện cho nhà thuốc hiện đại");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setForeground(new Color(255, 255, 255, 200));
        lblSubtitle.setAlignmentX(CENTER_ALIGNMENT);

        headerPanel.add(Box.createVerticalGlue());
        headerPanel.add(lblTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(lblSubtitle);
        headerPanel.add(Box.createVerticalGlue());

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        // Giới thiệu
        contentPanel.add(createIntroSection());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Phần nhân viên
        contentPanel.add(createEmployeeFunctionsPanel());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Phần quản lý - chia 2 cột
        contentPanel.add(createManagerFunctionsPanel());

        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        return contentPanel;
    }

    private JPanel createIntroSection() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                panel.getBorder(),
                new EmptyBorder(20, 25, 20, 25)));

        JLabel lblIntro = new JLabel("<html><div style='line-height: 1.6;'>"
                + "<p style='font-size: 16px; color: #333;'>"
                + "Phần mềm <b>Quản lý Hiệu thuốc Tây</b> là giải pháp "
                + "<b style='color: #0066CC;'>Nhóm N11</b> nhằm hỗ trợ các hiệu thuốc trong việc quản lý hoạt động "
                + "kinh doanh một cách hiệu quả.</p>"
                + "<p style='font-size: 16px; color: #333; margin-top: 10px;'>"
                + "Hệ thống cung cấp đầy đủ các tính năng từ bán hàng, quản lý kho, nhập hàng đến thống kê doanh thu, "
                + "giúp tối ưu hóa quy trình vận hành và nâng cao hiệu suất kinh doanh.</p>"
                + "</div></html>");
        lblIntro.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(lblIntro, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createEmployeeFunctionsPanel() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        headerPanel.setBackground(Color.WHITE);
        JLabel iconLabel = new JLabel("👤");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        JLabel titleLabel = new JLabel("CHỨC NĂNG NHÂN VIÊN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(EMPLOYEE_COLOR);
        headerPanel.add(iconLabel);
        headerPanel.add(titleLabel);

        // Content - 2 cột
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        contentPanel.setBackground(Color.WHITE);
        
        // Cột trái
        JLabel leftContent = new JLabel("<html><div style='line-height: 2.0; padding: 10px; font-size: 14px;'>"
                + "<p style='font-weight: bold; color: #27ae60; margin-bottom: 5px; font-size: 15px;'>📊 Tổng quan</p>"
                + "<ul style='margin: 0; padding-left: 20px;'>"
                + "<li>Xem thống kê doanh thu cá nhân</li>"
                + "<li>Theo dõi số đơn hàng đã bán</li>"
                + "</ul>"
                + "<p style='font-weight: bold; color: #27ae60; margin-top: 15px; margin-bottom: 5px; font-size: 15px;'>🛒 Bán hàng</p>"
                + "<ul style='margin: 0; padding-left: 20px;'>"
                + "<li>Tạo đơn hàng mới cho khách</li>"
                + "<li>Áp dụng khuyến mãi tự động</li>"
                + "<li>In hóa đơn cho khách hàng</li>"
                + "</ul>"
                + "<p style='font-weight: bold; color: #27ae60; margin-top: 15px; margin-bottom: 5px; font-size: 15px;'>🔄 Trả hàng</p>"
                + "<ul style='margin: 0; padding-left: 20px;'>"
                + "<li>Xử lý yêu cầu trả hàng</li>"
                + "<li>Kiểm tra thời hạn trả hàng</li>"
                + "</ul>"
                + "<p style='font-weight: bold; color: #27ae60; margin-top: 15px; margin-bottom: 5px; font-size: 15px;'>🗑️ Hủy hàng</p>"
                + "<ul style='margin: 0; padding-left: 20px;'>"
                + "<li>Lập phiếu hủy sản phẩm</li>"
                + "<li>Báo cáo sản phẩm hết hạn</li>"
                + "</ul>"
                + "</div></html>");
        leftContent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        leftContent.setVerticalAlignment(SwingConstants.TOP);
        
        // Cột phải
        JLabel rightContent = new JLabel("<html><div style='line-height: 2.0; padding: 10px; font-size: 14px;'>"
                + "<p style='font-weight: bold; color: #27ae60; margin-bottom: 5px; font-size: 15px;'>📈 Thống kê</p>"
                + "<ul style='margin: 0; padding-left: 20px;'>"
                + "<li>Xem thống kê cá nhân</li>"
                + "<li>Theo dõi hiệu suất làm việc</li>"
                + "</ul>"
                + "<p style='font-weight: bold; color: #27ae60; margin-top: 15px; margin-bottom: 5px; font-size: 15px;'>👤 Quản lý khách hàng</p>"
                + "<ul style='margin: 0; padding-left: 20px;'>"
                + "<li>Thêm mới khách hàng</li>"
                + "<li>Cập nhật thông tin khách</li>"
                + "<li>Xem lịch sử mua hàng</li>"
                + "</ul>"
                + "<p style='font-weight: bold; color: #27ae60; margin-top: 15px; margin-bottom: 5px; font-size: 15px;'>🔍 Tra cứu</p>"
                + "<ul style='margin: 0; padding-left: 20px;'>"
                + "<li>Tra cứu sản phẩm và giá</li>"
                + "<li>Tra cứu khách hàng</li>"
                + "<li>Tra cứu đơn hàng đã bán</li>"
                + "<li>Tra cứu khuyến mãi</li>"
                + "</ul>"
                + "</div></html>");
        rightContent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rightContent.setVerticalAlignment(SwingConstants.TOP);
        
        contentPanel.add(leftContent);
        contentPanel.add(rightContent);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createManagerFunctionsPanel() {
        JPanel mainPanel = createCardPanel();
        mainPanel.setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        headerPanel.setBackground(Color.WHITE);
        JLabel iconLabel = new JLabel("👔");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        JLabel titleLabel = new JLabel("CHỨC NĂNG QUẢN LÝ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(MANAGER_COLOR);
        headerPanel.add(iconLabel);
        headerPanel.add(titleLabel);

        // Content - 2 cột
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        contentPanel.setBackground(Color.WHITE);
        
        // Cột trái
        JLabel leftContent = new JLabel("<html><div style='line-height: 2.0; padding: 10px; font-size: 14px;'>"
                + "<p style='font-weight: bold; color: #8e44ad; margin-bottom: 5px; font-size: 15px;'>📊 Tổng quan</p>"
                + "<ul style='margin: 0; padding-left: 20px;'>"
                + "<li>Thống kê doanh thu toàn hệ thống</li>"
                + "<li>Giám sát hoạt động kinh doanh</li>"
                + "</ul>"
                + "<p style='font-weight: bold; color: #8e44ad; margin-top: 15px; margin-bottom: 5px; font-size: 15px;'>📦 Quản lý sản phẩm</p>"
                + "<ul style='margin: 0; padding-left: 20px;'>"
                + "<li>Thêm, sửa, xóa sản phẩm</li>"
                + "<li>Quản lý lô sản phẩm</li>"
                + "<li>Cập nhật giá và thông tin</li>"
                + "</ul>"
                + "<p style='font-weight: bold; color: #8e44ad; margin-top: 15px; margin-bottom: 5px; font-size: 15px;'>🏷️ Bảng giá &amp; Khuyến mãi</p>"
                + "<ul style='margin: 0; padding-left: 20px;'>"
                + "<li>Thiết lập bảng giá</li>"
                + "<li>Tạo chương trình khuyến mãi</li>"
                + "</ul>"
                + "<p style='font-weight: bold; color: #8e44ad; margin-top: 15px; margin-bottom: 5px; font-size: 15px;'>📥 Nhập hàng</p>"
                + "<ul style='margin: 0; padding-left: 20px;'>"
                + "<li>Lập phiếu nhập kho</li>"
                + "<li>Quản lý nhà cung cấp</li>"
                + "</ul>"
                + "<p style='font-weight: bold; color: #8e44ad; margin-top: 15px; margin-bottom: 5px; font-size: 15px;'>👥 Quản lý nhân sự</p>"
                + "<ul style='margin: 0; padding-left: 20px;'>"
                + "<li>Quản lý thông tin nhân viên</li>"
                + "<li>Phân quyền tài khoản</li>"
                + "<li>Thống kê hiệu suất nhân viên</li>"
                + "</ul>"
                + "</div></html>");
        leftContent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        leftContent.setVerticalAlignment(SwingConstants.TOP);
        
        // Cột phải
        JLabel rightContent = new JLabel("<html><div style='line-height: 2.0; padding: 10px; font-size: 14px;'>"
                + "<p style='font-weight: bold; color: #8e44ad; margin-bottom: 5px; font-size: 15px;'>👤 Quản lý khách hàng</p>"
                + "<ul style='margin: 0; padding-left: 20px;'>"
                + "<li>Thêm và cập nhật khách hàng</li>"
                + "<li>Xem lịch sử giao dịch</li>"
                + "</ul>"
                + "<p style='font-weight: bold; color: #8e44ad; margin-top: 15px; margin-bottom: 5px; font-size: 15px;'>📋 Danh mục hệ thống</p>"
                + "<ul style='margin: 0; padding-left: 20px;'>"
                + "<li>Quản lý đơn vị tính</li>"
                + "<li>Quản lý quy cách đóng gói</li>"
                + "</ul>"
                + "<p style='font-weight: bold; color: #8e44ad; margin-top: 15px; margin-bottom: 5px; font-size: 15px;'>📊 Thống kê &amp; Báo cáo</p>"
                + "<ul style='margin: 0; padding-left: 20px;'>"
                + "<li>Thống kê doanh thu theo thời gian</li>"
                + "<li>Thống kê sản phẩm bán chạy</li>"
                + "<li>Báo cáo nhân viên</li>"
                + "</ul>"
                + "<p style='font-weight: bold; color: #8e44ad; margin-top: 15px; margin-bottom: 5px; font-size: 15px;'>🔍 Tra cứu</p>"
                + "<ul style='margin: 0; padding-left: 20px;'>"
                + "<li>Tra cứu sản phẩm, lô hàng</li>"
                + "<li>Tra cứu phiếu nhập, phiếu hủy</li>"
                + "<li>Tra cứu đơn hàng, trả hàng</li>"
                + "<li>Tra cứu nhân viên, khách hàng</li>"
                + "<li>Tra cứu khuyến mãi, nhà cung cấp</li>"
                + "</ul>"
                + "</div></html>");
        rightContent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rightContent.setVerticalAlignment(SwingConstants.TOP);
        
        contentPanel.add(leftContent);
        contentPanel.add(rightContent);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        return panel;
    }
}
