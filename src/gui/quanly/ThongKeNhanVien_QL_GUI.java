package gui.quanly; // Bạn có thể đổi package thành gui.quanly nếu muốn

import com.toedter.calendar.JDateChooser;
import component.chart.BieuDoCotJFreeChart;
import component.chart.DuLieuBieuDoCot;
import dao.ThongKeNhanVien_DAO;
import dao.ThongKeNhanVien_DAO.KetQuaThongKe;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

public class ThongKeNhanVien_QL_GUI extends JPanel {

    private JDateChooser dateTuNgay, dateDenNgay;
    private JComboBox<String> cmbCaLam;
    private JComboBox<String> cmbNhanVien; // ✅ THÊM: ComboBox chọn nhân viên
    private JButton btnLoc;
    
    // Các Label thống kê (Style đồng bộ)
    private JLabel lblTongDoanhSo, lblSoHoaDon, lblTrungBinhDon;
    private JLabel lblSoPhieuTra, lblSoPhieuHuy, lblTyLeHoan;
    
    // Biểu đồ
    private BieuDoCotJFreeChart bieuDoHieuSuat;
    
    private ThongKeNhanVien_DAO dao;
    // Không cần lưu maNhanVienHienTai nữa vì QL có thể chọn bất kỳ ai

    /**
     * Constructor cho Quản lý
     */
    public ThongKeNhanVien_QL_GUI() {
        this.dao = new ThongKeNhanVien_DAO();
        
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // --- MAIN CONTAINER ---
        JPanel pnMain = new JPanel(new BorderLayout(0, 10));
        pnMain.setBackground(Color.WHITE);
        pnMain.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(pnMain, BorderLayout.CENTER);

        // ==============================================================================
        // 1. FILTER PANEL (Bộ lọc: Ngày + Nhân viên + Ca làm)
        // ==============================================================================
        JPanel pnFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnFilter.setBackground(new Color(0xE3F2F5)); // Màu nền xanh nhạt chuẩn
        pnFilter.setBorder(BorderFactory.createTitledBorder("Tiêu chí lọc thống kê"));
        // Tăng chiều cao panel lọc lên một chút để chứa đủ các component nếu cửa sổ nhỏ
        pnFilter.setPreferredSize(new Dimension(0, 85)); 

        // Date Choosers
        dateTuNgay = new JDateChooser();
        dateDenNgay = new JDateChooser();
        dateTuNgay.setDateFormatString("dd/MM/yyyy");
        dateDenNgay.setDateFormatString("dd/MM/yyyy");
        dateTuNgay.setPreferredSize(new Dimension(120, 30));
        dateDenNgay.setPreferredSize(new Dimension(120, 30));
        
        // Mặc định: Từ đầu tháng đến hiện tại
        Calendar cal = Calendar.getInstance();
        dateDenNgay.setDate(cal.getTime());
        cal.set(Calendar.DAY_OF_MONTH, 1);
        dateTuNgay.setDate(cal.getTime());

        // ✅ THÊM: ComboBox Nhân Viên
        cmbNhanVien = new JComboBox<>();
        cmbNhanVien.setPreferredSize(new Dimension(180, 30)); // Rộng hơn chút để hiển thị tên
        loadDanhSachNhanVien(); // Hàm nạp dữ liệu vào combo

        // Ca làm Filter
        cmbCaLam = new JComboBox<>();
        cmbCaLam.addItem("Tất cả ca");
        cmbCaLam.addItem("Ca 1 (Sáng)");
        cmbCaLam.addItem("Ca 2 (Chiều)");
        cmbCaLam.addItem("Ca 3 (Tối)");
        cmbCaLam.setPreferredSize(new Dimension(110, 30));

        // Button Lọc
        btnLoc = new JButton("Xem kết quả");
        btnLoc.setBackground(new Color(0x005a9e)); // Màu xanh đậm chuẩn
        btnLoc.setForeground(Color.WHITE);
        btnLoc.setPreferredSize(new Dimension(110, 30));
        btnLoc.setFocusPainted(false);
        btnLoc.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add components to Filter Panel
        pnFilter.add(new JLabel("Từ ngày:"));
        pnFilter.add(dateTuNgay);
        pnFilter.add(new JLabel("Đến ngày:"));
        pnFilter.add(dateDenNgay);
        pnFilter.add(new JLabel("| Nhân viên:")); // Thêm ngăn cách
        pnFilter.add(cmbNhanVien);              // ✅ Thêm combo vào giao diện
        pnFilter.add(new JLabel("Ca:"));
        pnFilter.add(cmbCaLam);
        pnFilter.add(btnLoc);
        
        pnMain.add(pnFilter, BorderLayout.NORTH);

        // ==============================================================================
        // 2. CONTENT PANEL (Chart + Stats) - GIỮ NGUYÊN CẤU TRÚC CŨ
        // ==============================================================================
        JPanel pnContent = new JPanel(new BorderLayout(0, 10));
        pnContent.setBackground(Color.WHITE);
        
        // --- 2a. CHART PANEL (Biểu đồ nằm giữa) ---
        JPanel pnChart = new JPanel(new BorderLayout());
        pnChart.setBorder(BorderFactory.createTitledBorder("Biểu đồ tương quan các chỉ số"));
        pnChart.setBackground(Color.WHITE);
        
        bieuDoHieuSuat = new BieuDoCotJFreeChart();
        bieuDoHieuSuat.setTieuDeTrucX("Chỉ số");
        bieuDoHieuSuat.setTieuDeTrucY("Số lượng");
        pnChart.add(bieuDoHieuSuat, BorderLayout.CENTER);
        
        // --- 2b. STATS PANEL (Các số liệu nằm dưới cùng) ---
        JPanel pnStats = new JPanel(new GridLayout(2, 3, 20, 15));
        pnStats.setBackground(new Color(0xE3F2F5));
        pnStats.setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder("Tổng quan số liệu chi tiết"), 
                new EmptyBorder(10, 20, 10, 20)
        ));
        pnStats.setPreferredSize(new Dimension(0, 200)); 
        
        Font fTitle = new Font("Tahoma", Font.PLAIN, 15);
        Font fValue = new Font("Tahoma", Font.BOLD, 18);
        
        // Tạo các label (Style giữ nguyên)
        lblTongDoanhSo = createLabel(pnStats, "Doanh số bán:", fTitle, fValue, new Color(40, 167, 69)); // Xanh lá
        lblSoHoaDon = createLabel(pnStats, "Số hóa đơn:", fTitle, fValue, new Color(0x005a9e));        // Xanh dương
        lblTrungBinhDon = createLabel(pnStats, "TB / Hóa đơn:", fTitle, fValue, new Color(102, 16, 242)); // Tím
        lblSoPhieuTra = createLabel(pnStats, "Số phiếu trả:", fTitle, fValue, new Color(255, 140, 0));   // Cam
        lblSoPhieuHuy = createLabel(pnStats, "Số phiếu hủy:", fTitle, fValue, new Color(220, 53, 69));   // Đỏ
        lblTyLeHoan = createLabel(pnStats, "Tỷ lệ hoàn trả:", fTitle, fValue, Color.DARK_GRAY);          // Xám đen

        pnContent.add(pnChart, BorderLayout.CENTER);
        pnContent.add(pnStats, BorderLayout.SOUTH);
        
        pnMain.add(pnContent, BorderLayout.CENTER);

        // Sự kiện
        btnLoc.addActionListener(e -> loadData());
        
        // Load dữ liệu ban đầu
        loadData();
    }

    /**
     * ✅ Hàm hỗ trợ nạp danh sách nhân viên vào ComboBox
     */
    private void loadDanhSachNhanVien() {
        cmbNhanVien.removeAllItems();
        cmbNhanVien.addItem("Tất cả nhân viên");
        // Giả sử DAO có hàm trả về danh sách mảng String[] {MaNV, TenNV}
        // Nếu chưa có, bạn cần thêm hàm getDanhSachNhanVien() vào ThongKeNhanVien_DAO
        List<String[]> dsNV = dao.getDanhSachNhanVien(); 
        for (String[] nv : dsNV) {
            // Hiển thị dạng: "NV001 - Nguyễn Văn A"
            cmbNhanVien.addItem(nv[0] + " - " + nv[1]);
        }
    }

    /**
     * Hàm hỗ trợ tạo Label (Giữ nguyên)
     */
    private JLabel createLabel(JPanel p, String t, Font f1, Font f2, Color c) {
        JPanel pChild = new JPanel(new BorderLayout(5, 5));
        pChild.setOpaque(false);
        JLabel lTitle = new JLabel(t);
        lTitle.setFont(f1);
        JLabel lValue = new JLabel("0");
        lValue.setFont(f2);
        lValue.setForeground(c);
        pChild.add(lTitle, BorderLayout.NORTH);
        pChild.add(lValue, BorderLayout.CENTER);
        p.add(pChild);
        return lValue;
    }

    /**
     * ✅ Hàm tải dữ liệu (Đã sửa đổi logic để lấy theo nhân viên được chọn)
     */
    private void loadData() {
        Date tu = dateTuNgay.getDate();
        Date den = dateDenNgay.getDate();
        
        if (tu == null || den == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ ngày tháng!");
            return;
        }
        if (tu.after(den)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được lớn hơn ngày kết thúc!");
            return;
        }

        int caLam = cmbCaLam.getSelectedIndex(); 

        // ✅ XÁC ĐỊNH NHÂN VIÊN CẦN XEM
        String selectedItem = (String) cmbNhanVien.getSelectedItem();
        String targetMaNV = null; // Mặc định null là xem tất cả
        
        if (selectedItem != null && !selectedItem.equals("Tất cả nhân viên")) {
            // SỬA: Kiểm tra xem chuỗi có chứa dấu gạch ngang không trước khi split
            if(selectedItem.contains(" - ")) {
                 targetMaNV = selectedItem.split(" - ")[0];
            }
        }

        // Gọi DAO với mã nhân viên đã xác định (null nếu chọn tất cả)
        KetQuaThongKe kq = dao.getThongKe(tu, den, targetMaNV, caLam);

        // --- Cập nhật giao diện (Phần dưới này giữ nguyên như cũ) ---
        DecimalFormat dfTien = new DecimalFormat("#,##0 đ");
        DecimalFormat dfSo = new DecimalFormat("#,##0");
        DecimalFormat dfTyLe = new DecimalFormat("0.00'%'");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        String range = String.format("(%s - %s)", sdf.format(tu), sdf.format(den));
        // Hiển thị tên nhân viên đang xem ở chú thích
        String nvText = (targetMaNV == null) ? "Toàn cửa hàng" : selectedItem;
        String caText = cmbCaLam.getSelectedItem().toString();
        String note = nvText + " | " + caText;

        lblTongDoanhSo.setText("<html>" + dfTien.format(kq.tongDoanhSo) + "<br><span style='font-size:10px;color:gray;font-weight:normal'>" + range + "</span></html>");
        lblSoHoaDon.setText("<html>" + dfSo.format(kq.soHoaDon) + "<br><span style='font-size:10px;color:gray;font-weight:normal'>" + note + "</span></html>");
        lblTrungBinhDon.setText(dfTien.format(kq.getGiaTriTrungBinh()));
        lblSoPhieuTra.setText("<html>" + dfSo.format(kq.soPhieuTra) + "<br><span style='font-size:10px;color:gray;font-weight:normal'>Tiền trả: " + dfTien.format(kq.tongTienTra) + "</span></html>");
        lblSoPhieuHuy.setText(dfSo.format(kq.soPhieuHuy));
        lblTyLeHoan.setText(dfTyLe.format(kq.getTyLeHoanTra()));

        // Update Chart
        bieuDoHieuSuat.xoaToanBoDuLieu();
        String chartTitle = (targetMaNV == null) ? "Tổng hợp chỉ số toàn cửa hàng" : "Chỉ số hiệu suất: " + selectedItem;
        bieuDoHieuSuat.setTieuDeBieuDo(chartTitle);
        
        Color c1 = new Color(0x005a9e); 
        Color c2 = new Color(255, 140, 0);
        Color c3 = new Color(220, 53, 69);
        
        bieuDoHieuSuat.themDuLieu(new DuLieuBieuDoCot("Số Hóa Đơn", "Số lượng", kq.soHoaDon, c1));
        bieuDoHieuSuat.themDuLieu(new DuLieuBieuDoCot("Số Phiếu Trả", "Số lượng", kq.soPhieuTra, c2));
        bieuDoHieuSuat.themDuLieu(new DuLieuBieuDoCot("Số Phiếu Hủy", "Số lượng", kq.soPhieuHuy, c3));
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản Lý Đơn Vị Tính");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1300, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new ThongKeNhanVien_QL_GUI());
            frame.setVisible(true);
        });
    }
}