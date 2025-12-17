/**
 * @author Quốc Khánh cute
 * @version 1.0
 * @since Dec 17, 2025
 *
 * Mô tả: Lớp này được tạo bởi Quốc Khánh vào ngày Dec 17, 2025.
 */
package gui.trogiup;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class HuongDan_GUI extends JPanel {

    public HuongDan_GUI() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Panel chính ở giữa
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Icon lớn
        JLabel iconLabel = new JLabel("📖");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 120));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(iconLabel);

        centerPanel.add(Box.createVerticalStrut(30));

        // Tiêu đề
        JLabel titleLabel = new JLabel("Hướng Dẫn Sử Dụng");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(33, 150, 243));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);

        centerPanel.add(Box.createVerticalStrut(15));

        // Mô tả
        JLabel descLabel = new JLabel("Tài liệu hướng dẫn chi tiết về cách sử dụng phần mềm");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descLabel.setForeground(new Color(100, 100, 100));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(descLabel);

        centerPanel.add(Box.createVerticalStrut(40));

        // Nút mở hướng dẫn
        JButton btnMoHuongDan = new JButton("📄 Mở File Hướng Dẫn");
        btnMoHuongDan.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnMoHuongDan.setForeground(Color.WHITE);
        btnMoHuongDan.setBackground(new Color(33, 150, 243));
        btnMoHuongDan.setFocusPainted(false);
        btnMoHuongDan.setBorderPainted(false);
        btnMoHuongDan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMoHuongDan.setMaximumSize(new Dimension(300, 60));
        btnMoHuongDan.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnMoHuongDan.addActionListener(e -> moHuongDan());

        // Hiệu ứng hover
        btnMoHuongDan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnMoHuongDan.setBackground(new Color(25, 118, 210));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnMoHuongDan.setBackground(new Color(33, 150, 243));
            }
        });

        centerPanel.add(btnMoHuongDan);

        centerPanel.add(Box.createVerticalStrut(20));

        // Thông tin file
        JLabel infoLabel = new JLabel("File sẽ được mở trong trình duyệt mặc định");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        infoLabel.setForeground(new Color(150, 150, 150));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(infoLabel);

        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Method static để mở file hướng dẫn - có thể gọi từ bất kỳ đâu
     */
    public static void moHuongDan() {
        try {
            // Tìm file HTML
            File htmlFile = new File("src\\resources\\hdsn\\HuongDanSuDung.html");
            if (!htmlFile.exists()) {
                htmlFile = new File("HuongDanSuDung.html");
            }

            if (!htmlFile.exists()) {
                JOptionPane.showMessageDialog(null,
                        "Không tìm thấy file hướng dẫn sử dụng!\n" +
                        "File: HuongDanSuDung.html hoặc HuongDanSuDung_new.html",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra Desktop API
            if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                JOptionPane.showMessageDialog(null,
                        "Hệ thống không hỗ trợ mở trình duyệt.\n" +
                        "Vui lòng mở file thủ công tại:\n" + htmlFile.getAbsolutePath(),
                        "Không hỗ trợ",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Mở file
            Desktop.getDesktop().browse(htmlFile.toURI());
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Không thể mở file hướng dẫn!\n" +
                    "Lỗi: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}


