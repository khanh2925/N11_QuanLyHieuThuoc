package gui.quanly;

import com.toedter.calendar.JDateChooser;
import component.button.PillButton;
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
    private JComboBox<String> cmbNhanVien;
    private JButton btnLoc;

    // Các Label thống kê
    private JLabel lblTongDoanhSo, lblSoHoaDon, lblTrungBinhDon;
    private JLabel lblSoPhieuTra, lblSoPhieuHuy, lblTyLeHoan;

    // Biểu đồ
    private BieuDoCotJFreeChart bieuDoHieuSuat;

    private ThongKeNhanVien_DAO dao;

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
        // 1. FILTER PANEL (layout null)
        // ==============================================================================
        JPanel pnFilter = new JPanel();
        pnFilter.setBackground(new Color(0xE3F2F5));
        pnFilter.setBorder(BorderFactory.createTitledBorder("Tiêu chí lọc thống kê"));
        pnFilter.setPreferredSize(new Dimension(0, 100));
        pnFilter.setLayout(null);

        JLabel lblTuNgay = new JLabel("Từ ngày");
        lblTuNgay.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblTuNgay.setBounds(20, 25, 80, 20);
        pnFilter.add(lblTuNgay);

        dateTuNgay = new JDateChooser();
        dateTuNgay.setDateFormatString("dd-MM-yyyy");
        dateTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateTuNgay.setBounds(20, 50, 140, 30);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        dateTuNgay.setDate(cal.getTime());
        pnFilter.add(dateTuNgay);

        JLabel lblDenNgay = new JLabel("Đến ngày");
        lblDenNgay.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblDenNgay.setBounds(180, 25, 80, 20);
        pnFilter.add(lblDenNgay);

        dateDenNgay = new JDateChooser();
        dateDenNgay.setDateFormatString("dd-MM-yyyy");
        dateDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateDenNgay.setBounds(180, 50, 140, 30);
        dateDenNgay.setDate(new Date());
        pnFilter.add(dateDenNgay);

        JLabel lblNhanVien = new JLabel("Nhân viên");
        lblNhanVien.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNhanVien.setBounds(340, 25, 100, 20);
        pnFilter.add(lblNhanVien);

        cmbNhanVien = new JComboBox<>();
        loadDanhSachNhanVien();
        cmbNhanVien.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbNhanVien.setBounds(340, 50, 300, 30);
        pnFilter.add(cmbNhanVien);

        JLabel lblCaLam = new JLabel("Ca làm");
        lblCaLam.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblCaLam.setBounds(660, 25, 80, 20);
        pnFilter.add(lblCaLam);

        cmbCaLam = new JComboBox<>();
        cmbCaLam.addItem("Tất cả ca");
        cmbCaLam.addItem("Ca 1 (Sáng)");
        cmbCaLam.addItem("Ca 2 (Chiều)");
        cmbCaLam.addItem("Ca 3 (Tối)");
        cmbCaLam.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbCaLam.setBounds(660, 50, 120, 30);
        pnFilter.add(cmbCaLam);

        btnLoc = new PillButton("Thống Kê");
        btnLoc.setBounds(810, 45, 120, 35);
        pnFilter.add(btnLoc);

        pnMain.add(pnFilter, BorderLayout.NORTH);

        // ==============================================================================
        // 2. STATS PANEL (dời lên trên biểu đồ)
        // ==============================================================================
        JPanel pnStats = new JPanel(new GridLayout(2, 3, 20, 15));
        pnStats.setBackground(new Color(0xE3F2F5));
        pnStats.setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder("Tổng quan số liệu chi tiết"),
                new EmptyBorder(10, 20, 10, 20)));
        pnStats.setPreferredSize(new Dimension(0, 180));

        Font fTitle = new Font("Tahoma", Font.PLAIN, 15);
        Font fValue = new Font("Tahoma", Font.BOLD, 18);

        lblTongDoanhSo = createLabel(pnStats, "Doanh số bán:", fTitle, fValue, new Color(40, 167, 69));
        lblSoHoaDon = createLabel(pnStats, "Số hóa đơn:", fTitle, fValue, new Color(0x005a9e));
        lblTrungBinhDon = createLabel(pnStats, "TB / Hóa đơn:", fTitle, fValue, new Color(102, 16, 242));
        lblSoPhieuTra = createLabel(pnStats, "Số phiếu trả:", fTitle, fValue, new Color(255, 140, 0));
        lblSoPhieuHuy = createLabel(pnStats, "Số phiếu hủy:", fTitle, fValue, new Color(220, 53, 69));
        lblTyLeHoan = createLabel(pnStats, "Tỷ lệ hoàn trả:", fTitle, fValue, Color.DARK_GRAY);

        // ==============================================================================
        // 3. CHART PANEL
        // ==============================================================================
        JPanel pnChart = new JPanel(new BorderLayout());
        pnChart.setBorder(BorderFactory.createTitledBorder("Biểu đồ tương quan các chỉ số"));
        pnChart.setBackground(Color.WHITE);

        bieuDoHieuSuat = new BieuDoCotJFreeChart();
        bieuDoHieuSuat.setTieuDeTrucX("Chỉ số");
        bieuDoHieuSuat.setTieuDeTrucY("Số lượng");
        pnChart.add(bieuDoHieuSuat, BorderLayout.CENTER);

        // ==============================================================================
        // 4. CONTENT PANEL
        // ==============================================================================
        JPanel pnContent = new JPanel(new BorderLayout(0, 10));
        pnContent.setBackground(Color.WHITE);

        // Đặt panel tổng quan ở trên, biểu đồ ở giữa
        JPanel pnTopSection = new JPanel(new BorderLayout(0, 10));
        pnTopSection.setBackground(Color.WHITE);
        pnTopSection.add(pnStats, BorderLayout.NORTH);
        pnTopSection.add(pnChart, BorderLayout.CENTER);

        pnContent.add(pnTopSection, BorderLayout.CENTER);
        pnMain.add(pnContent, BorderLayout.CENTER);

        // Sự kiện
        btnLoc.addActionListener(e -> loadData());

        // Load dữ liệu ban đầu
        loadData();
    }

    /**
     * Hàm hỗ trợ nạp danh sách nhân viên vào ComboBox
     */
    private void loadDanhSachNhanVien() {
        cmbNhanVien.removeAllItems();
        cmbNhanVien.addItem("Tất cả nhân viên");
        List<String[]> dsNV = dao.getDanhSachNhanVien();
        for (String[] nv : dsNV) {
            cmbNhanVien.addItem(nv[0] + " - " + nv[1]);
        }
    }

    /**
     * Hàm hỗ trợ tạo Label
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
     * Hàm tải dữ liệu
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

        // Xác định nhân viên cần xem
        String selectedItem = (String) cmbNhanVien.getSelectedItem();
        String targetMaNV = null;

        if (selectedItem != null && !selectedItem.equals("Tất cả nhân viên")) {
            if (selectedItem.contains(" - ")) {
                targetMaNV = selectedItem.split(" - ")[0];
            }
        }

        // Gọi DAO với mã nhân viên đã xác định
        KetQuaThongKe kq = dao.getThongKe(tu, den, targetMaNV, caLam);

        // Cập nhật giao diện
        DecimalFormat dfTien = new DecimalFormat("#,##0 đ");
        DecimalFormat dfSo = new DecimalFormat("#,##0");
        DecimalFormat dfTyLe = new DecimalFormat("0.00'%'");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        String range = String.format("(%s - %s)", sdf.format(tu), sdf.format(den));
        String nvText = (targetMaNV == null) ? "Toàn cửa hàng" : selectedItem;
        String caText = cmbCaLam.getSelectedItem().toString();
        String note = nvText + " | " + caText;

        lblTongDoanhSo.setText("<html>" + dfTien.format(kq.tongDoanhSo)
                + "<br><span style='font-size:10px;color:gray;font-weight:normal'>" + range + "</span></html>");
        lblSoHoaDon.setText("<html>" + dfSo.format(kq.soHoaDon)
                + "<br><span style='font-size:10px;color:gray;font-weight:normal'>" + note + "</span></html>");
        lblTrungBinhDon.setText(dfTien.format(kq.getGiaTriTrungBinh()));
        lblSoPhieuTra.setText("<html>" + dfSo.format(kq.soPhieuTra)
                + "<br><span style='font-size:10px;color:gray;font-weight:normal'>Tiền trả: "
                + dfTien.format(kq.tongTienTra) + "</span></html>");
        lblSoPhieuHuy.setText(dfSo.format(kq.soPhieuHuy));
        lblTyLeHoan.setText(dfTyLe.format(kq.getTyLeHoanTra()));

        // Update Chart
        bieuDoHieuSuat.xoaToanBoDuLieu();
        String chartTitle = (targetMaNV == null) ? "Tổng hợp chỉ số toàn cửa hàng"
                : "Chỉ số hiệu suất: " + selectedItem;
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
            JFrame frame = new JFrame("Thống Kê Nhân Viên - Quản Lý");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1300, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new ThongKeNhanVien_QL_GUI());
            frame.setVisible(true);
        });
    }
}