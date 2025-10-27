package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.net.URL;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;


public class Loading_GUI extends JWindow {

    private JProgressBar progressBar;
    private JLabel lblStatus;

    public Loading_GUI() {
        buildUI();
        startLoading();
    }

    private void buildUI() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new LineBorder(new Color(0, 120, 215), 2)); // Viền xanh đẹp mắt
        setSize(600, 400);
        setLocationRelativeTo(null); // Canh giữa màn hình
        setContentPane(contentPanel);

        // Hiển thị logo ở trung tâm
        URL logoUrl = getClass().getResource("/images/Logo.png");
        if (logoUrl != null) {
            ImageIcon logoIcon = new ImageIcon(logoUrl);
            JLabel lblLogo = new JLabel(logoIcon);
            contentPanel.add(lblLogo, BorderLayout.CENTER);
        } else {
            // Fallback nếu không tìm thấy logo
            JLabel lblAppName = new JLabel("Hiệu thuốc Hòa An", SwingConstants.CENTER);
            lblAppName.setFont(new Font("Segoe UI", Font.BOLD, 36));
            contentPanel.add(lblAppName, BorderLayout.CENTER);
        }

        // Panel chứa thanh progress và text status
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setPreferredSize(new Dimension(0, 60));
        progressPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        progressPanel.setBackground(Color.WHITE);
        contentPanel.add(progressPanel, BorderLayout.SOUTH);

        // Label trạng thái
        lblStatus = new JLabel("Đang khởi tạo ứng dụng...");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblStatus.setHorizontalAlignment(SwingConstants.LEFT);
        progressPanel.add(lblStatus, BorderLayout.NORTH);

        // Thanh tiến trình
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        progressBar.setForeground(new Color(0, 120, 215));
        progressPanel.add(progressBar, BorderLayout.CENTER);
    }

    /**
     * Sử dụng SwingWorker để chạy tác vụ nền mà không làm treo giao diện
     */
    private void startLoading() {
        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            
            // Tác vụ chạy trên background thread
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(40); // Giả lập thời gian tải
                    
                    // Cập nhật trạng thái tại các mốc nhất định
                    if (i == 10) {
                    	publishStatus("Đang tải các thành phần giao diện...");
                    } else if (i == 40) {
                    	publishStatus("Đang kết nối cơ sở dữ liệu...");
                    } else if (i == 70) {
                    	publishStatus("Đang xác thực thông tin...");
                    } else if (i == 90) {
                    	publishStatus("Sắp hoàn tất...");
                    }
                    
                    publish(i); // Gửi tiến trình về cho EDT
                }
                return null;
            }

            // Cập nhật giao diện trên Event Dispatch Thread (EDT)
            @Override
            protected void process(List<Integer> chunks) {
                int latestProgress = chunks.get(chunks.size() - 1);
                progressBar.setValue(latestProgress);
            }

            // Chạy trên EDT sau khi doInBackground hoàn tất
            @Override
            protected void done() {
                dispose(); // Đóng màn hình loading
                new DangNhap_GUI().setVisible(true); // Mở màn hình đăng nhập
            }
        };

        worker.execute(); // Bắt đầu thực thi
        setVisible(true); // Hiển thị cửa sổ loading
    }
    
    /**
     * Phương thức helper để cập nhật label trạng thái một cách an toàn từ background thread
     * @param text
     */
    private void publishStatus(String text) {
    	SwingUtilities.invokeLater(() -> lblStatus.setText(text));
    }
}