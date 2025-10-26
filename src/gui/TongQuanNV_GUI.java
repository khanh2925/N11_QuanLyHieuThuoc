/**
 * @author Quốc Khánh
 * @version 3.0
 * @since Oct 16, 2025
 *
 * Mô tả: Khung giao diện trống - giữ lại bố cục chính để clone trang khác.
 */

package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import com.toedter.calendar.JDateChooser;

import customcomponent.PillButton;
import customcomponent.RoundedBorder;

public class TongQuanNV_GUI extends JPanel {

    private JPanel pnCenter;   // vùng trung tâm
    private JPanel pnHeader;   // vùng đầu trang
    private JButton btnThongTinCaNhan;
    private JLabel lblNewLabel_1;
    private JDateChooser dateTu;
    private JDateChooser dateDen;
    private JTextField txtSPBanNhieuNhat;
    private JTextField txtSLuongBiTra;
    private JTextField txtSLuongDonHang;
    private JTextField txtDoanhThu;
    
    
    public TongQuanNV_GUI() {
        this.setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1537, 1168));

        // ===== HEADER =====
        pnHeader = new JPanel();
        pnHeader.setPreferredSize(new Dimension(1073, 88));
        pnHeader.setBackground(new Color(227, 242, 245));
        pnHeader.setLayout(null);
        add(pnHeader, BorderLayout.NORTH);
        
        JLabel lblNewLabel = new JLabel("Tổng quan nhân viên");
        lblNewLabel.setBounds(35, 19, 300, 40); // tăng kích thước để phù hợp chữ to
        lblNewLabel.setFont(new Font("Segoe UI", Font.BOLD, 22)); // FONT BOLD + SIZE TO
        pnHeader.add(lblNewLabel);

     // ===== CENTER =====
        pnCenter = new JPanel();
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnCenter.setLayout(null);
        pnCenter.setBackground(Color.WHITE);
        add(pnCenter, BorderLayout.CENTER);
        
        // chọn ngày
        JLabel lblTuNgay = new JLabel("Từ ngày:");
        lblTuNgay.setFont(new Font("Segoe UI", Font.BOLD, 20)); // to + in đậm
        lblTuNgay.setBounds(152, 75, 121, 38);
        pnCenter.add(lblTuNgay);

        dateTu = new JDateChooser();
        dateTu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateTu.setDateFormatString("dd/MM/yyyy");
        dateTu.setBounds(152, 126, 328, 30);
        pnCenter.add(dateTu);

        JLabel lblDenNgay = new JLabel("Đến:");
        lblDenNgay.setFont(new Font("Segoe UI", Font.BOLD, 20)); // to + in đậm
        lblDenNgay.setBounds(816, 79, 85, 30);
        pnCenter.add(lblDenNgay);

        dateDen = new JDateChooser();
        dateDen.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateDen.setDateFormatString("dd/MM/yyyy");
        dateDen.setBounds(816, 126, 345, 30);
        pnCenter.add(dateDen);
        
     // Tạo font chung cho label
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 20); // chữ to + đậm
        Color mauChu = new Color(0, 51, 102); // xanh đậm nhẹ cho nổi bật
        
        // các panel chứa tiêu chí thống kê
        JPanel pnlSLuongDonHang = new JPanel();
        pnlSLuongDonHang.setBounds(816, 197, 345, 136);        
        pnCenter.add(pnlSLuongDonHang);
        pnlSLuongDonHang.setLayout(null);
        pnlSLuongDonHang.setBackground(new Color(227, 242, 245));
        pnlSLuongDonHang.setBorder(new RoundedBorder(25));
        
        JLabel lblSLuongDonHang = new JLabel("Số lượng đơn hàng");
        lblSLuongDonHang.setFont(fontTieuDe);
        lblSLuongDonHang.setForeground(mauChu);
        lblSLuongDonHang.setBounds(89, 21, 197, 40);
        pnlSLuongDonHang.add(lblSLuongDonHang);
        

        JPanel pnlHAMHSLDH = new JPanel() {
            private Image backgroundImage;

            {
                java.net.URL imgURL = getClass().getResource("/images/soluongdonhangNV.png");
                if (imgURL != null) {
                    backgroundImage = new ImageIcon(imgURL).getImage();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        pnlHAMHSLDH.setBounds(10, 31, 69, 65);
        pnlHAMHSLDH.setOpaque(false);
        pnlSLuongDonHang.add(pnlHAMHSLDH);
        
        txtSLuongDonHang = new JTextField();
        txtSLuongDonHang.setColumns(10);
        txtSLuongDonHang.setBounds(89, 54, 246, 48);
        pnlSLuongDonHang.add(txtSLuongDonHang);
        txtSLuongDonHang.setOpaque(false);              
        txtSLuongDonHang.setBorder(null);               
        txtSLuongDonHang.setForeground(new Color(0,51,102)); 
        txtSLuongDonHang.setFont(new Font("Segoe UI", Font.BOLD, 20)); 
        
        
        
        JPanel pnlDoanhThu = new JPanel();
        pnlDoanhThu.setBounds(816, 419, 345, 136);
        pnCenter.add(pnlDoanhThu);
        pnlDoanhThu.setLayout(null);
        pnlDoanhThu.setBackground(new Color(227, 242, 245));
        pnlDoanhThu.setBorder(new RoundedBorder(25));
        

        
        JLabel lblDoanhThu = new JLabel("Doanh thu");
        lblDoanhThu.setFont(fontTieuDe);
        lblDoanhThu.setForeground(mauChu);
        lblDoanhThu.setBounds(89, 21, 197, 40);
        pnlDoanhThu.add(lblDoanhThu);
        
        JPanel pnlHAMHDoanhThu = new JPanel() {
            private Image backgroundImage;

            { 
                try {
                    
                    java.net.URL imgURL = getClass().getResource("/images/doanhthuNV.png");
                    if (imgURL != null) {
                        backgroundImage = new ImageIcon(imgURL).getImage();
                    } else {
                        System.err.println("Không tìm thấy ảnh: /images/hinhAnhDoanhThu.png");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        
        pnlHAMHDoanhThu.setBounds(10, 31, 69, 65);
        pnlHAMHDoanhThu.setOpaque(false); 
        pnlDoanhThu.add(pnlHAMHDoanhThu);
        
        txtDoanhThu = new JTextField();
        txtDoanhThu.setColumns(10);
        txtDoanhThu.setBounds(89, 54, 246, 48);
        pnlDoanhThu.add(txtDoanhThu);
        txtDoanhThu.setOpaque(false);
        txtDoanhThu.setBorder(null);
        txtDoanhThu.setForeground(new Color(0,51,102));
        txtDoanhThu.setFont(new Font("Segoe UI", Font.BOLD, 20));

        
        JPanel pnlSPBanNhieuNhat = new JPanel();
        pnlSPBanNhieuNhat.setBounds(152, 197, 345, 136);
        pnCenter.add(pnlSPBanNhieuNhat);
        pnlSPBanNhieuNhat.setLayout(null);
        pnlSPBanNhieuNhat.setBackground(new Color(227, 242, 245));
        pnlSPBanNhieuNhat.setBorder(new RoundedBorder(25));
        
        JLabel lblSPBanNhieuNhat = new JLabel("Sản phẩm bán nhiều nhất");
        lblSPBanNhieuNhat.setFont(fontTieuDe);
        lblSPBanNhieuNhat.setForeground(mauChu);
        lblSPBanNhieuNhat.setBounds(89, 21, 246, 40);
        pnlSPBanNhieuNhat.add(lblSPBanNhieuNhat);
        
        JPanel pnlHAMHSPBanNhieuNhat = new JPanel() {
            private Image backgroundImage;

            { 
                try {
                    
                    java.net.URL imgURL = getClass().getResource("/images/best-seller.png");
                    if (imgURL != null) {
                        backgroundImage = new ImageIcon(imgURL).getImage();
                    } else {
                        System.err.println("Không tìm thấy ảnh: /images/hinhAnhSPBanNhieuNhat.png");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        pnlHAMHSPBanNhieuNhat.setBounds(10, 31, 69, 65);
        pnlHAMHSPBanNhieuNhat.setOpaque(false); // để không bị che nền
        pnlSPBanNhieuNhat.add(pnlHAMHSPBanNhieuNhat);
        
        txtSPBanNhieuNhat = new JTextField();
        txtSPBanNhieuNhat.setBounds(89, 54, 246, 48);
        pnlSPBanNhieuNhat.add(txtSPBanNhieuNhat);
        txtSPBanNhieuNhat.setColumns(10);
        txtSPBanNhieuNhat.setOpaque(false);
        txtSPBanNhieuNhat.setBorder(null);
        txtSPBanNhieuNhat.setForeground(new Color(0,51,102));
        txtSPBanNhieuNhat.setFont(new Font("Segoe UI", Font.BOLD, 20));

        
        JPanel pnlSLuongBiTra = new JPanel();
        pnlSLuongBiTra.setBounds(152, 419, 346, 136);
        pnCenter.add(pnlSLuongBiTra);
        pnlSLuongBiTra.setLayout(null);
        pnlSLuongBiTra.setBackground(new Color(227, 242, 245));
        pnlSLuongBiTra.setBorder(new RoundedBorder(25));
        
      
        JLabel lblSLuongBiTra = new JLabel("Số lượng sản phẩm bị trả");
        lblSLuongBiTra.setFont(fontTieuDe);
        lblSLuongBiTra.setForeground(mauChu);
        lblSLuongBiTra.setBounds(89, 21, 257, 40);
        pnlSLuongBiTra.add(lblSLuongBiTra);
        
        JPanel pnlHAMHSLuongBiTra = new JPanel() {
            private Image backgroundImage;

            { 
                try {
                    
                    java.net.URL imgURL = getClass().getResource("/images/sale-return-icon.png");
                    if (imgURL != null) {
                        backgroundImage = new ImageIcon(imgURL).getImage();
                    } else {
                        System.err.println("Không tìm thấy ảnh: /images/hinhAnhBiTra.png");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        pnlHAMHSLuongBiTra.setBounds(10, 31, 69, 65);
        pnlHAMHSLuongBiTra.setOpaque(false);
        pnlSLuongBiTra.add(pnlHAMHSLuongBiTra);
        
        txtSLuongBiTra = new JTextField();
        txtSLuongBiTra.setColumns(10);
        txtSLuongBiTra.setBounds(89, 54, 246, 48);
        pnlSLuongBiTra.add(txtSLuongBiTra);
        txtSLuongBiTra.setOpaque(false);
        txtSLuongBiTra.setBorder(null);
        txtSLuongBiTra.setForeground(new Color(0,51,102));
        txtSLuongBiTra.setFont(new Font("Segoe UI", Font.BOLD, 20));
        
        btnThongTinCaNhan = new PillButton("Thông tin cá nhân");
        btnThongTinCaNhan.setBounds(1079, 595, 180, 66);
        pnCenter.add(btnThongTinCaNhan);
        
       
        
        
        btnThongTinCaNhan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lấy frame cha (để dialog nằm trên frame chính)
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(TongQuanNV_GUI.this);

                // Tạo dialog
                ThongTinCaNhan_Dialog dialog = new ThongTinCaNhan_Dialog(parentFrame);
                dialog.setVisible(true); // hiển thị dialog
            }
        });


        pnCenter.revalidate();
        pnCenter.repaint();
        
        
        
        
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Khung trống - clone base");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new TongQuanNV_GUI());
            frame.setVisible(true);
        });
    }
}
