/**
 * @author Quốc Khánh cute
 * @version 4.0
 * @since Dec 11, 2025
 *
 * Mô tả: Dashboard tổng quan cho nghiệp vụ quản lý hiệu thuốc
 * - 6 KPI Cards (2 hàng x 3 cột):
 *   + Lợi nhuận tháng này
 *   + Lợi nhuận tháng trước
 *   + Tiền nhập hàng tháng này
 *   + Khách hàng mới
 *   + Đơn trả cần duyệt (có thể click)
 *   + Đơn hủy cần duyệt (có thể click)
 * - Biểu đồ cột nhóm hiển thị 5 chỉ số xu hướng 3 tháng gần nhất:
 *   + Doanh thu (xanh dương)
 *   + Tiền nhập hàng (tím)
 *   + Tiền trả hàng (cam)
 *   + Tiền hủy hàng (đỏ)
 *   + Lợi nhuận ròng (xanh lá) = Doanh thu - Nhập - Trả - Hủy
 * - Tích hợp DAO để lấy dữ liệu thực từ database
 * 
 * Cách sử dụng:
 * - Khởi tạo với Main_GUI reference: new TongQuanQuanLy_GUI(mainGUI)
 * - Tự động load dữ liệu tháng hiện tại khi khởi tạo
 * - Gọi refreshDashboard() để cập nhật dữ liệu mới nhất
 * - Click vào "Đơn trả cần duyệt" → chuyển sang màn hình Quản lý Phiếu Trả
 * - Click vào "Đơn hủy cần duyệt" → chuyển sang màn hình Quản lý Phiếu Hủy
 */
package gui.quanly;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.time.LocalDate;

import component.border.RoundedBorder;
import component.chart.BieuDoCotGroup;
import component.chart.DuLieuBieuDoCot;
import dao.HoaDon_DAO;
import dao.KhachHang_DAO;
import dao.ThongKe_DAO;
import dao.PhieuTra_DAO;
import dao.PhieuHuy_DAO;
import dao.PhieuNhap_DAO;

public class TongQuanQuanLy_GUI extends JPanel implements ActionListener, MouseListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 907476279580670931L;
	private JPanel pnCenter;
    private JPanel pnHeader;
    
    // KPIs Top Row (2 hàng x 3 cột = 5 thẻ + 1 ô trống)
    private JTextField txtLoiNhuanThangNay;
    private JTextField txtLoiNhuanThangTruoc;
    private JTextField txtTienNhapThangNay;
    private JTextField txtKhachHangMoi;
    private JTextField txtPhieuTraChuaDuyet;
    private JTextField txtPhieuHuyChuaDuyet;
    
    // Panel chứa Biểu đồ
    private JPanel pnChartArea;
    private BieuDoCotGroup bieuDoCot;
    
    // DAOs
    private HoaDon_DAO hoaDonDAO;
    private KhachHang_DAO khachHangDAO;
    private ThongKe_DAO thongKeDAO;
    private PhieuTra_DAO phieuTraDAO;
    private PhieuHuy_DAO phieuHuyDAO;
    private PhieuNhap_DAO phieuNhapDAO;
    
    // Main GUI reference để chuyển card
    private gui.Main_GUI mainGUI;
    
    // Formatter
    private DecimalFormat formatter = new DecimalFormat("#,###");
   
    
    /**
     * Constructor với Main_GUI reference để hỗ trợ chuyển tab
     * @param mainGUI Tham chiếu đến Main_GUI
     */
    public TongQuanQuanLy_GUI(gui.Main_GUI mainGUI) {
        this.mainGUI = mainGUI;
        this.setPreferredSize(new Dimension(1537, 850));
        
        // Khởi tạo DAOs
        hoaDonDAO = new HoaDon_DAO();
        khachHangDAO = new KhachHang_DAO();
        thongKeDAO = new ThongKe_DAO();
        phieuTraDAO = new PhieuTra_DAO();
        phieuHuyDAO = new PhieuHuy_DAO();
        phieuNhapDAO = new PhieuNhap_DAO();
        
        initialize();
        // Load dữ liệu thật từ database
        loadRealData();
        loadChartData();
    }
    
    /**
     * Constructor mặc định (dùng cho test)
     */
    public TongQuanQuanLy_GUI() {
        this(null);
    }

    /**
     * Load dữ liệu thật từ database
     */
    private void loadRealData() {
        LocalDate today = LocalDate.now();
        int thangHienTai = today.getMonthValue();
        int namHienTai = today.getYear();
        
        try {
            // 1. Lợi nhuận tháng này
            double loiNhuan = thongKeDAO.tinhLoiNhuanChinhXacTheoThang(thangHienTai, namHienTai);
            txtLoiNhuanThangNay.setText(formatter.format(loiNhuan) + " ₫");
            
            // 2. Lợi nhuận tháng trước
            int thangTruoc = thangHienTai - 1;
            int namThangTruoc = namHienTai;
            if (thangTruoc <= 0) {
                thangTruoc = 12;
                namThangTruoc--;
            }
            double loiNhuanThangTruoc = thongKeDAO.tinhLoiNhuanChinhXacTheoThang(thangTruoc, namThangTruoc);
            txtLoiNhuanThangTruoc.setText(formatter.format(loiNhuanThangTruoc) + " ₫");
            
            // 3. Tổng tiền nhập hàng tháng này
            double tienNhap = phieuNhapDAO.tinhTongTienNhapTheoThang(thangHienTai, namHienTai);
            txtTienNhapThangNay.setText(formatter.format(tienNhap) + " ₫");
            
            // 4. Khách hàng mới
            int khachHangMoi = khachHangDAO.demKhachHangMoiTheoThang(thangHienTai, namHienTai);
            txtKhachHangMoi.setText(String.valueOf(khachHangMoi));
            
            // 5. Phiếu trả chưa duyệt
            int phieuTraChuaDuyet = phieuTraDAO.demPhieuTraChuaDuyet();
            txtPhieuTraChuaDuyet.setText(String.valueOf(phieuTraChuaDuyet));
            
            // 6. Phiếu hủy chưa duyệt
            int phieuHuyChuaDuyet = phieuHuyDAO.demPhieuHuyChuaDuyet();
            txtPhieuHuyChuaDuyet.setText(String.valueOf(phieuHuyChuaDuyet));
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi load dữ liệu dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
    

    
    /**
     * Load dữ liệu cho biểu đồ cột - 5 chỉ số: Doanh thu, Tiền nhập, Tiền trả, Tiền hủy, Lợi nhuận
     */
    private void loadChartData() {
        LocalDate today = LocalDate.now();
        int namHienTai = today.getYear();
        int thangHienTai = today.getMonthValue();
        
        // Định nghĩa màu sắc cho 5 series
        Color mauDoanhThu = new Color(54, 162, 235);   // Xanh dương
        Color mauTienNhap = new Color(153, 102, 255);  // Tím
        Color mauTienTra = new Color(255, 159, 64);    // Cam
        Color mauTienHuy = new Color(255, 99, 132);    // Đỏ
        Color mauLoiNhuan = new Color(75, 192, 192);   // Xanh lá
        
        try {
            // Load dữ liệu 3 tháng gần nhất
            for (int i = 2; i >= 0; i--) {
                int thang = thangHienTai - i;
                int nam = namHienTai;
                
                // Xử lý trường hợp tháng âm (sang năm trước)
                if (thang <= 0) {
                    thang += 12;
                    nam--;
                }
                
                String labelThang = "Tháng " + thang;
                
                // Lấy dữ liệu từ database
                double doanhThu = hoaDonDAO.layDoanhThuTheoThang(thang, nam);
                double tienNhap = phieuNhapDAO.tinhTongTienNhapTheoThang(thang, nam);
                double tienTra = phieuTraDAO.tinhTongTienTraTheoThang(thang, nam);
                double tienHuy = phieuHuyDAO.tinhTongTienHuyTheoThang(thang, nam);
                double loiNhuan = thongKeDAO.tinhLoiNhuanChinhXacTheoThang(thang, nam);
                
                // Thêm vào biểu đồ - 5 cột với màu riêng
                bieuDoCot.themDuLieu(new DuLieuBieuDoCot(labelThang, "Doanh thu", doanhThu, mauDoanhThu));
                bieuDoCot.themDuLieu(new DuLieuBieuDoCot(labelThang, "Tiền nhập", tienNhap, mauTienNhap));
                bieuDoCot.themDuLieu(new DuLieuBieuDoCot(labelThang, "Tiền trả", tienTra, mauTienTra));
                bieuDoCot.themDuLieu(new DuLieuBieuDoCot(labelThang, "Tiền hủy", tienHuy, mauTienHuy));
                bieuDoCot.themDuLieu(new DuLieuBieuDoCot(labelThang, "Lợi nhuận", loiNhuan, mauLoiNhuan));
            }
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi load dữ liệu biểu đồ: " + e.getMessage());
            e.printStackTrace();
        }
    }
    


    private void initialize() {
        setLayout(new BorderLayout());
        
        // ===== HEADER (NORTH) =====
        pnHeader = new JPanel();
        pnHeader.setPreferredSize(new Dimension(1073, 80));
        pnHeader.setBackground(new Color(227, 242, 245));
        add(pnHeader, BorderLayout.NORTH);
        pnHeader.setLayout(new BorderLayout(0, 0));
        
        JLabel lblTongQuan = new JLabel("Tổng Quan");
        lblTongQuan.setHorizontalAlignment(SwingConstants.CENTER);
        lblTongQuan.setForeground(new Color(0, 51, 102));
        lblTongQuan.setFont(new Font("Segoe UI", Font.BOLD, 36)); 
        pnHeader.add(lblTongQuan);

        // ===== CENTER (Phần chứa KPIs và Biểu đồ) =====
        pnCenter = new JPanel();
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnCenter.setBackground(Color.WHITE);
        add(pnCenter, BorderLayout.CENTER);
        
        pnCenter.setLayout(new BoxLayout(pnCenter, BoxLayout.Y_AXIS)); 
        
        // --- 1. KHU VỰC KPI (TOP) ---
        JPanel pnlKPIs = new JPanel(new GridLayout(2, 3, 15, 15)); // 2 hàng, 3 cột
        pnlKPIs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320)); 
        pnlKPIs.setOpaque(false); 
        
        Color mauChu = new Color(0, 51, 102); 
        
        // **KHỞI TẠO VÀ GÁN BIẾN TRƯỚC KHI THÊM VÀO PANEL**
        
        // Hàng 1
        // Thẻ 1: Lợi nhuận tháng này
        txtLoiNhuanThangNay = new JTextField();
        pnlKPIs.add(taoTheKPI("Lợi nhuận tháng này", mauChu, txtLoiNhuanThangNay, null, null));

        // Thẻ 2: Lợi nhuận tháng trước
        txtLoiNhuanThangTruoc = new JTextField();
        pnlKPIs.add(taoTheKPI("Lợi nhuận tháng trước", mauChu, txtLoiNhuanThangTruoc, null, null));
        
        // Thẻ 3: Tổng tiền nhập hàng tháng này
        txtTienNhapThangNay = new JTextField();
        pnlKPIs.add(taoTheKPI("Tiền nhập tháng này", mauChu, txtTienNhapThangNay, null, null));
        
        // Hàng 2
        // Thẻ 4: Khách hàng mới
        txtKhachHangMoi = new JTextField();
        pnlKPIs.add(taoTheKPI("Khách hàng mới (tháng này)", mauChu, txtKhachHangMoi, null, null));
        
        // Thẻ 5: Đơn trả cần duyệt (có thể click) - Border đỏ
        txtPhieuTraChuaDuyet = new JTextField();
        pnlKPIs.add(taoTheKPI("Đơn trả cần duyệt", mauChu, txtPhieuTraChuaDuyet, "PHIEU_TRA", new Color(220, 53, 69)));
        
        // Thẻ 6: Đơn hủy cần duyệt (có thể click) - Border đỏ
        txtPhieuHuyChuaDuyet = new JTextField();
        pnlKPIs.add(taoTheKPI("Đơn hủy cần duyệt", mauChu, txtPhieuHuyChuaDuyet, "PHIEU_HUY", new Color(220, 53, 69)));
        
        pnCenter.add(pnlKPIs);
        pnCenter.add(Box.createVerticalStrut(20)); // Tạo khoảng cách 20px
        
        // --- 2. KHU VỰC BIỂU ĐỒ (BOTTOM) ---
        pnChartArea = new JPanel();
        pnChartArea.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 51, 102)), 
            "Biểu đồ Xu hướng 3 Tháng Gần Nhất", 
            TitledBorder.LEFT, TitledBorder.TOP, 
            new Font("Segoe UI", Font.BOLD, 18), mauChu
        ));
        pnChartArea.setBackground(Color.WHITE); 
        pnChartArea.setLayout(new BorderLayout());
        
        // Tạo biểu đồ cột nhóm
        bieuDoCot = new BieuDoCotGroup();
        bieuDoCot.setTieuDeBieuDo("So sánh Doanh thu - Nhập - Trả - Hủy - Lợi nhuận");
        bieuDoCot.setTieuDeTrucX("Tháng");
        bieuDoCot.setTieuDeTrucY("Số tiền (VNĐ)");
        pnChartArea.add(bieuDoCot, BorderLayout.CENTER);

        pnChartArea.setPreferredSize(new Dimension(Integer.MAX_VALUE, 600)); 
        pnCenter.add(pnChartArea);


        pnCenter.revalidate();
        pnCenter.repaint();
    }
    
    /**
     * Phương thức helper tạo thẻ KPI - Chức năng: chỉ thiết lập giao diện cho JTextField đã được tạo.
     * @param title Tiêu đề thẻ
     * @param mauChu Màu chữ
     * @param textField TextField hiển thị giá trị
     * @param actionType Loại hành động khi click (null = không có action)
     * @param mauBorder Màu border (null = màu mặc định)
     */
    private JPanel taoTheKPI(String title, Color mauChu, JTextField textField, String actionType, Color mauBorder) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(227, 242, 245));
        
        // Thiết lập border - nếu có màu border đặc biệt thì dùng LineBorder, không thì dùng RoundedBorder
        if (mauBorder != null) {
            // Border màu đỏ nổi bật cho các item cần chú ý
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mauBorder, 3), // Border ngoài màu đỏ dày 3px
                new EmptyBorder(10, 10, 10, 10) // Padding bên trong
            ));
        } else {
            panel.setBorder(new RoundedBorder(15));
        }
        
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setPreferredSize(new Dimension(100, 100));
        
        // Chỉ set cursor hand khi có action
        if (actionType != null) {
            panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            panel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    xuLyClickKPI(actionType);
                }
                @Override
                public void mousePressed(MouseEvent e) {}
                @Override
                public void mouseReleased(MouseEvent e) {}
                @Override
                public void mouseEntered(MouseEvent e) {
                    panel.setBackground(new Color(200, 230, 240)); // Màu sáng hơn khi hover
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    panel.setBackground(new Color(227, 242, 245)); // Màu gốc
                }
            });
        }
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTitle.setForeground(mauChu);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTitle.setBorder(new EmptyBorder(10, 10, 0, 10));

        // Setup JTextField đã được truyền vào
        textField.setColumns(10);
        textField.setOpaque(false);
        textField.setBorder(null);
        textField.setForeground(mauChu);
        textField.setFont(new Font("Segoe UI", Font.BOLD, 35)); 
        textField.setEditable(false); 
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        textField.setBorder(new EmptyBorder(5, 10, 10, 10));
        
        panel.add(lblTitle);
        panel.add(textField);
        
        return panel;
    }
    
    /**
     * Xử lý sự kiện click vào KPI card
     */
    private void xuLyClickKPI(String actionType) {
        if (mainGUI == null) {
            JOptionPane.showMessageDialog(this, 
                "Chức năng chuyển màn hình chưa khả dụng",
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        switch (actionType) {
            case "PHIEU_TRA":
                // Chuyển sang màn hình Quản lý Phiếu Trả
                mainGUI.chuyenDenCard("trahang");
                break;
            case "PHIEU_HUY":
                // Chuyển sang màn hình Quản lý Phiếu Hủy
                mainGUI.chuyenDenCard("xuathuy");
                break;
        }
    }
    
    /**
     * Refresh lại toàn bộ dữ liệu dashboard
     * Gọi method này khi cần cập nhật dữ liệu (VD: sau khi thêm hóa đơn mới)
     */
    public void refreshDashboard() {
        // Xóa dữ liệu biểu đồ cũ
        bieuDoCot.xoaToanBoDuLieu();
        
        // Load lại dữ liệu mới
        loadRealData();
        loadChartData();
    }


    // --- MAIN VÀ INTERFACE METHODS ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dashboard Tổng Quan");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new TongQuanQuanLy_GUI());
            frame.setVisible(true);
        });
    }

    @Override
    public void mouseClicked(MouseEvent e) { }
    @Override
    public void mousePressed(MouseEvent e) { }
    @Override
    public void mouseReleased(MouseEvent e) { }
    @Override
    public void mouseEntered(MouseEvent e) { }
    @Override
    public void mouseExited(MouseEvent e) { }
    @Override
    public void actionPerformed(ActionEvent e) { }
}