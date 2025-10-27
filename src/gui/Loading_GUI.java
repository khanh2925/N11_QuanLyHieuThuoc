package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.net.URL;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

// <<< THAY ĐỔI 1: Import lớp ImagePanel thật từ package của bạn
import customcomponent.ImagePanel;

public class Loading_GUI extends JWindow {

    private JProgressBar progressBar;
    private JLabel lblStatus;

    public Loading_GUI() {
        buildUI();
        startLoading();
    }

    private void buildUI() {
        setSize(850, 610);
        setLocationRelativeTo(null);

        // --- PANEL CHÍNH VỚI BORDER LAYOUT ---
        JPanel contentPanel = new JPanel(new BorderLayout());
        setContentPane(contentPanel);

        // --- IMAGE PANEL (Nền) ---
        URL bgImageUrl = getClass().getResource("/images/Loading.png");
        ImagePanel imagePanel; 

        if (bgImageUrl != null) {
            ImageIcon bgImageIcon = new ImageIcon(bgImageUrl);
            imagePanel = new ImagePanel(bgImageIcon.getImage());
        } else {
            // Fallback nếu không tìm thấy ảnh
            imagePanel = new ImagePanel(null);
            imagePanel.setLayout(new GridBagLayout());
            imagePanel.setBackground(new Color(0xE3F2F5));
            imagePanel.setOpaque(true); // Cần set Opaque thành true cho fallback để màu nền hiển thị
            imagePanel.add(new JLabel("Không tìm thấy ảnh nền..."));
            System.err.println("Không tìm thấy ảnh nền: /images/Loading.png");
        }
        
        imagePanel.setLayout(new BorderLayout());
        contentPanel.add(imagePanel, BorderLayout.CENTER);
        
        // --- PANEL TIẾN TRÌNH (Footer) SẼ NẰM TRÊN ẢNH ---
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setOpaque(false); 
        progressPanel.setPreferredSize(new Dimension(0, 65));
        
        Border padding = new EmptyBorder(5, 20, 15, 20);
        progressPanel.setBorder(padding);
        
        // --- LABEL TRẠNG THÁI ---
        lblStatus = new JLabel("Đang khởi tạo ứng dụng...");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblStatus.setForeground(Color.RED); // Giữ lại thay đổi màu đỏ của bạn
        progressPanel.add(lblStatus, BorderLayout.NORTH);
        
        // --- THANH TIẾN TRÌNH ---
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        progressBar.setForeground(new Color(0, 150, 136)); 
        progressBar.setBackground(new Color(230, 230, 230)); 
        progressPanel.add(progressBar, BorderLayout.CENTER);

        imagePanel.add(progressPanel, BorderLayout.SOUTH);
    }

    private void startLoading() {
        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(30);
                    if (i == 10) publishStatus("Đang tải tài nguyên...");
                    else if (i == 40) publishStatus("Đang kết nối cơ sở dữ liệu...");
                    else if (i == 70) publishStatus("Đang cấu hình giao diện...");
                    else if (i == 90) publishStatus("Sắp hoàn tất...");
                    publish(i);
                }
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                progressBar.setValue(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {

                 new DangNhap_GUI().setVisible(true);
            }
        };
        worker.execute();
        setVisible(true);
    }
    
    private void publishStatus(String text) {
    	SwingUtilities.invokeLater(() -> lblStatus.setText(text));
    }

    public static void main(String[] args) {
        new Loading_GUI();
    }
}