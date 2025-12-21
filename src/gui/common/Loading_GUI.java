package gui.common;

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
import javax.swing.border.EmptyBorder;

import component.panel.ImagePanel;
import dao.DonViTinh_DAO;
import dao.KhachHang_DAO;
import dao.NhaCungCap_DAO;
import dao.NhanVien_DAO;
import dao.QuyCachDongGoi_DAO;
import dao.SanPham_DAO;
import database.connectDB;
import gui.DangNhap_GUI;

/**
 * Màn hình Loading khi khởi động ứng dụng
 * Hiển thị tiến trình khởi tạo và kết nối database
 * 
 * @author N11_QuanLyHieuThuoc
 * @version 2.0
 */
public class Loading_GUI extends JWindow {

    private static final long serialVersionUID = 1L;
    
    // Components
    private JProgressBar progressBar;
    private JLabel lblStatus;
    private JLabel lblPercentage;
    
    // Cờ để kiểm soát việc đóng cửa sổ
    private boolean loadingComplete = false;

    public Loading_GUI() {
        buildUI();
        startLoading();
    }

    /**
     * Xây dựng giao diện Loading
     */
    private void buildUI() {
        setSize(850, 610);
        setLocationRelativeTo(null);

        // Panel chính
        JPanel contentPanel = new JPanel(new BorderLayout());
        setContentPane(contentPanel);

        // Image Panel (Nền)
        URL bgImageUrl = getClass().getResource("/resources/images/Loading.png");
        ImagePanel imagePanel; 

        if (bgImageUrl != null) {
            ImageIcon bgImageIcon = new ImageIcon(bgImageUrl);
            imagePanel = new ImagePanel(bgImageIcon.getImage());
        } else {
            // Fallback nếu không tìm thấy ảnh
            imagePanel = new ImagePanel(null);
            imagePanel.setLayout(new GridBagLayout());
            imagePanel.setBackground(new Color(0xE3F2F5));
            imagePanel.setOpaque(true);
            imagePanel.add(new JLabel("Không tìm thấy ảnh nền..."));
            System.err.println("⚠️ Không tìm thấy ảnh nền: /resources/images/Loading.png");
        }
        
        imagePanel.setLayout(new BorderLayout());
        contentPanel.add(imagePanel, BorderLayout.CENTER);
        
        // Panel tiến trình
        JPanel progressPanel = createProgressPanel();
        imagePanel.add(progressPanel, BorderLayout.SOUTH);
    }

    /**
     * Tạo panel chứa thanh tiến trình và label trạng thái
     */
    private JPanel createProgressPanel() {
        JPanel progressPanel = new JPanel(new BorderLayout(0, 8));
        progressPanel.setOpaque(false); 
        progressPanel.setPreferredSize(new Dimension(0, 85));
        progressPanel.setBorder(new EmptyBorder(10, 30, 20, 30));
        
        // Panel chứa label trạng thái và phần trăm
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setOpaque(false);
        
        // Label trạng thái
        lblStatus = new JLabel("Đang khởi tạo ứng dụng...");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblStatus.setForeground(new Color(33, 33, 33));
        statusPanel.add(lblStatus, BorderLayout.WEST);
        
        // Label phần trăm
        lblPercentage = new JLabel("0%");
        lblPercentage.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPercentage.setForeground(new Color(0, 150, 136));
        statusPanel.add(lblPercentage, BorderLayout.EAST);
        
        progressPanel.add(statusPanel, BorderLayout.NORTH);
        
        // Thanh tiến trình
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(false); // Tắt text trên thanh, dùng label riêng
        progressBar.setPreferredSize(new Dimension(0, 25));
        progressBar.setForeground(new Color(0, 150, 136)); 
        progressBar.setBackground(new Color(230, 230, 230));
        progressPanel.add(progressBar, BorderLayout.CENTER);

        return progressPanel;
    }

    /**
     * Bắt đầu quá trình loading
     */
    private void startLoading() {
        SwingWorker<Boolean, LoadingProgress> worker = new SwingWorker<Boolean, LoadingProgress>() {
            @SuppressWarnings("static-access")
			@Override
            protected Boolean doInBackground() throws Exception {
                try {
                    // Bước 1: Khởi tạo cấu hình (0-10%)
                    publish(new LoadingProgress(5, "Đang khởi tạo ứng dụng..."));
                    Thread.sleep(300);
                    
                    // Bước 2: Kết nối database (10-20%)
                    publish(new LoadingProgress(10, "Đang kết nối cơ sở dữ liệu..."));
                    connectDB.getInstance().connect();
                    
                    if (connectDB.getInstance().getConnection() == null) {
                        throw new Exception("Không thể kết nối đến database");
                    }
                    publish(new LoadingProgress(20, "Kết nối database thành công!"));
                    Thread.sleep(200);
                    
                    // Bước 3: Load danh mục cơ bản (20-35%)
                    publish(new LoadingProgress(22, "Đang tải danh mục đơn vị tính..."));
                    DonViTinh_DAO dvtDAO = new DonViTinh_DAO();
                    int countDVT = dvtDAO.layTatCaDonViTinh().size();
                    publish(new LoadingProgress(28, "✓ Đã tải " + countDVT + " đơn vị tính"));
                    Thread.sleep(100);
                    
                    publish(new LoadingProgress(30, "Đang tải quy cách đóng gói..."));
                    QuyCachDongGoi_DAO qcDAO = new QuyCachDongGoi_DAO();
                    int countQC = qcDAO.layTatCaQuyCachDongGoi().size();
                    publish(new LoadingProgress(35, "✓ Đã tải " + countQC + " quy cách đóng gói"));
                    Thread.sleep(100);
                    
                    // Bước 4: Load nhân viên (35-50%)
                    publish(new LoadingProgress(38, "Đang tải danh sách nhân viên..."));
                    NhanVien_DAO nvDAO = new NhanVien_DAO();
                    int countNV = nvDAO.layTatCaNhanVien().size();
                    publish(new LoadingProgress(50, "✓ Đã tải " + countNV + " nhân viên"));
                    Thread.sleep(150);
                    
                    // Bước 5: Load khách hàng (50-65%)
                    publish(new LoadingProgress(52, "Đang tải danh sách khách hàng..."));
                    KhachHang_DAO khDAO = new KhachHang_DAO();
                    int countKH = khDAO.layTatCaKhachHang().size();
                    publish(new LoadingProgress(65, "✓ Đã tải " + countKH + " khách hàng"));
                    Thread.sleep(150);
                    
                    // Bước 6: Load nhà cung cấp (65-75%)
                    publish(new LoadingProgress(67, "Đang tải danh sách nhà cung cấp..."));
                    NhaCungCap_DAO nccDAO = new NhaCungCap_DAO();
                    int countNCC = nccDAO.layTatCaNhaCungCap().size();
                    publish(new LoadingProgress(75, "✓ Đã tải " + countNCC + " nhà cung cấp"));
                    Thread.sleep(150);
                    
                    // Bước 7: Load sản phẩm (75-95%) - Mất nhiều thời gian nhất
                    publish(new LoadingProgress(78, "Đang tải danh sách sản phẩm..."));
                    SanPham_DAO spDAO = new SanPham_DAO();
                    int countSP = spDAO.layTatCaSanPham().size();
                    publish(new LoadingProgress(95, "✓ Đã tải " + countSP + " sản phẩm"));
                    Thread.sleep(200);
                    
                    // Bước 8: Hoàn tất (95-100%)
                    publish(new LoadingProgress(97, "Đang hoàn tất khởi động..."));
                    Thread.sleep(300);
                    publish(new LoadingProgress(100, "Hoàn tất! Chào mừng bạn đến với hệ thống."));
                    
                    return true;
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    publish(new LoadingProgress(0, "❌ Lỗi: " + e.getMessage()));
                    return false;
                }
            }

            @Override
            protected void process(List<LoadingProgress> chunks) {
                LoadingProgress lastProgress = chunks.get(chunks.size() - 1);
                progressBar.setValue(lastProgress.getValue());
                lblStatus.setText(lastProgress.getMessage());
                lblPercentage.setText(lastProgress.getValue() + "%");
            }

            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    loadingComplete = true;
                    
                    if (success) {
                        // Đợi một chút để người dùng thấy 100%
                        Thread.sleep(500);
                        
                        // Đóng loading và mở màn hình đăng nhập
                        SwingUtilities.invokeLater(() -> {
                            dispose();
                            new DangNhap_GUI().setVisible(true);
                        });
                    } else {
                        // Nếu có lỗi, hiển thị thông báo và đóng ứng dụng
                        SwingUtilities.invokeLater(() -> {
                            javax.swing.JOptionPane.showMessageDialog(
                                Loading_GUI.this,
                                "Không thể khởi tạo ứng dụng!\nVui lòng kiểm tra kết nối database.",
                                "Lỗi khởi tạo",
                                javax.swing.JOptionPane.ERROR_MESSAGE
                            );
                            System.exit(0);
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
        setVisible(true);
    }

    /**
     * Inner class để lưu trữ thông tin tiến trình
     */
    private static class LoadingProgress {
        private final int value;
        private final String message;
        
        public LoadingProgress(int value, String message) {
            this.value = value;
            this.message = message;
        }
        
        public int getValue() {
            return value;
        }
        
        public String getMessage() {
            return message;
        }
    }

    /**
     * Kiểm tra xem loading đã hoàn tất chưa
     */
    public boolean isLoadingComplete() {
        return loadingComplete;
    }

    /**
     * Main method để test Loading GUI
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set look and feel
//                javax.swing.UIManager.setLookAndFeel(
//                    javax.swing.UIManager.getSystemLookAndFeelClassName()
//                );
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Loading_GUI();
        });
    }
}