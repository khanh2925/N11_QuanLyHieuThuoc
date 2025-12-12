package gui.nhanvien;

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
import javax.swing.*;
import javax.swing.border.*;

public class ThongKeNhanVien_GUI extends JPanel {

    private JDateChooser dateTuNgay, dateDenNgay;
    private JComboBox<String> cmbCaLam;
    private JButton btnLoc;

    // Các Label thống kê
    private JLabel lblTongDoanhSo, lblSoHoaDon, lblTrungBinhDon;
    private JLabel lblSoPhieuTra, lblSoPhieuHuy, lblTyLeHoan;

    private BieuDoCotJFreeChart bieuDoHieuSuat;
    private ThongKeNhanVien_DAO dao;
    private String maNhanVienHienTai;

    /**
     * Constructor
     * 
     * @param maNV Mã nhân viên của người đang đăng nhập
     */
    public ThongKeNhanVien_GUI(String maNV) {
        this.maNhanVienHienTai = maNV;
        this.dao = new ThongKeNhanVien_DAO();

        // Thiết lập layout cho Panel
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel pnMain = new JPanel(new BorderLayout(0, 10));
        pnMain.setBackground(Color.WHITE);
        pnMain.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(pnMain, BorderLayout.CENTER);

        // --- 1. FILTER PANEL (layout null) ---
        JPanel pnFilter = new JPanel();
        pnFilter.setBackground(new Color(0xE3F2F5));
        pnFilter.setBorder(BorderFactory.createTitledBorder("Tiêu chí lọc"));
        pnFilter.setPreferredSize(new Dimension(0, 100));
        pnFilter.setLayout(null);

        JLabel lblTuNgay = new JLabel("Từ ngày");
        lblTuNgay.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblTuNgay.setBounds(20, 25, 80, 20);
        pnFilter.add(lblTuNgay);

        dateTuNgay = new JDateChooser();
        dateTuNgay.setDateFormatString("dd-MM-yyyy");
        dateTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateTuNgay.setBounds(20, 50, 150, 30);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        dateTuNgay.setDate(cal.getTime());
        pnFilter.add(dateTuNgay);

        JLabel lblDenNgay = new JLabel("Đến ngày");
        lblDenNgay.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblDenNgay.setBounds(200, 25, 80, 20);
        pnFilter.add(lblDenNgay);

        dateDenNgay = new JDateChooser();
        dateDenNgay.setDateFormatString("dd-MM-yyyy");
        dateDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateDenNgay.setBounds(200, 50, 150, 30);
        dateDenNgay.setDate(new Date());
        pnFilter.add(dateDenNgay);

        JLabel lblCaLam = new JLabel("Ca làm");
        lblCaLam.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblCaLam.setBounds(380, 25, 80, 20);
        pnFilter.add(lblCaLam);

        cmbCaLam = new JComboBox<>();
        cmbCaLam.addItem("Tất cả");
        cmbCaLam.addItem("Ca 1 (Sáng)");
        cmbCaLam.addItem("Ca 2 (Chiều)");
        cmbCaLam.addItem("Ca 3 (Tối)");
        cmbCaLam.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbCaLam.setBounds(380, 50, 130, 30);
        pnFilter.add(cmbCaLam);

        btnLoc = new PillButton("Thống Kê");
        btnLoc.setBounds(540, 45, 120, 35);
        pnFilter.add(btnLoc);

        pnMain.add(pnFilter, BorderLayout.NORTH);

        // --- 2. STATS PANEL (dời lên trên biểu đồ) ---
        JPanel pnStats = new JPanel(new GridLayout(2, 3, 20, 15));
        pnStats.setBackground(new Color(0xE3F2F5));
        pnStats.setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder("Tổng quan hiệu suất"),
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

        // --- 3. CHART PANEL ---
        JPanel pnChart = new JPanel(new BorderLayout());
        pnChart.setBorder(BorderFactory.createTitledBorder("Biểu đồ hiệu suất cá nhân"));
        pnChart.setBackground(Color.WHITE);

        bieuDoHieuSuat = new BieuDoCotJFreeChart();
        bieuDoHieuSuat.setTieuDeTrucX("Chỉ số");
        bieuDoHieuSuat.setTieuDeTrucY("Số lượng");
        pnChart.add(bieuDoHieuSuat, BorderLayout.CENTER);

        // --- 4. CONTENT PANEL ---
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

        // Lấy dữ liệu theo Mã NV đang đăng nhập
        KetQuaThongKe kq = dao.getThongKe(tu, den, maNhanVienHienTai, caLam);

        DecimalFormat dfTien = new DecimalFormat("#,##0 đ");
        DecimalFormat dfSo = new DecimalFormat("#,##0");
        DecimalFormat dfTyLe = new DecimalFormat("0.00'%'");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        String range = String.format("(%s - %s)", sdf.format(tu), sdf.format(den));
        String caText = cmbCaLam.getSelectedItem().toString();

        lblTongDoanhSo.setText("<html>" + dfTien.format(kq.tongDoanhSo)
                + "<br><span style='font-size:10px;color:gray;font-weight:normal'>" + range + "</span></html>");
        lblSoHoaDon.setText("<html>" + dfSo.format(kq.soHoaDon)
                + "<br><span style='font-size:10px;color:gray;font-weight:normal'>" + caText + "</span></html>");
        lblTrungBinhDon.setText(dfTien.format(kq.getGiaTriTrungBinh()));
        lblSoPhieuTra.setText("<html>" + dfSo.format(kq.soPhieuTra)
                + "<br><span style='font-size:10px;color:gray;font-weight:normal'>Tiền trả: "
                + dfTien.format(kq.tongTienTra) + "</span></html>");
        lblSoPhieuHuy.setText(dfSo.format(kq.soPhieuHuy));
        lblTyLeHoan.setText(dfTyLe.format(kq.getTyLeHoanTra()));

        bieuDoHieuSuat.xoaToanBoDuLieu();
        bieuDoHieuSuat.setTieuDeBieuDo("Tương quan các chỉ số giao dịch");

        Color c1 = new Color(0x005a9e);
        Color c2 = new Color(255, 140, 0);
        Color c3 = new Color(220, 53, 69);

        bieuDoHieuSuat.themDuLieu(new DuLieuBieuDoCot("Số Hóa Đơn", "Giao dịch", kq.soHoaDon, c1));
        bieuDoHieuSuat.themDuLieu(new DuLieuBieuDoCot("Số Phiếu Trả", "Giao dịch", kq.soPhieuTra, c2));
        bieuDoHieuSuat.themDuLieu(new DuLieuBieuDoCot("Số Phiếu Hủy", "Giao dịch", kq.soPhieuHuy, c3));
    }
}