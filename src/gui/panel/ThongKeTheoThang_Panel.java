package gui.panel;

import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.time.Year;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.*;
import component.chart.*;
import dao.ThongKe_DAO;
import dao.ThongKe_DAO.BanGhiThongKe;
import enums.LoaiSanPham;

public class ThongKeTheoThang_Panel extends JPanel {

    private JComboBox<Integer> cmbChonNam;
    private JComboBox<String> cmbLoaiSP, cmbKhuyenMai;
    private BieuDoCotJFreeChart bieuDoDoanhThu;
    private JLabel lblTongDoanhThu, lblCaoNhat, lblThapNhat, lblTrungBinh, lblSoGiaoDich;
    private ThongKe_DAO thongKeDAO;

    private final Color COLOR_BG = Color.WHITE;
    private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    public ThongKeTheoThang_Panel() {
        thongKeDAO = new ThongKe_DAO();
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);

        // -- Filter --
        JPanel pnFilter = new JPanel(new GridLayout(1, 4, 20, 0));
        pnFilter.setBackground(COLOR_BG);
        pnFilter.setBorder(new CompoundBorder(
            new TitledBorder(new LineBorder(new Color(200, 200, 200)), "Bộ lọc tùy chọn", TitledBorder.LEADING, TitledBorder.TOP, FONT_BOLD, new Color(100, 100, 100)),
            new EmptyBorder(10, 20, 15, 20)
        ));
        pnFilter.setPreferredSize(new Dimension(0, 100));

        int curYear = Year.now().getValue();
        Integer[] years = new Integer[10]; for (int i = 0; i < 10; i++) years[i] = curYear - i;
        cmbChonNam = new JComboBox<>(years);
        pnFilter.add(createFilterItem("Chọn năm", cmbChonNam));

        cmbLoaiSP = new JComboBox<>(); cmbLoaiSP.addItem("Tất cả sản phẩm");
        for (LoaiSanPham l : LoaiSanPham.values()) cmbLoaiSP.addItem(l.getTenLoai());
        pnFilter.add(createFilterItem("Loại sản phẩm", cmbLoaiSP));

        cmbKhuyenMai = new JComboBox<>(); cmbKhuyenMai.addItem("Tất cả khuyến mãi");
        for (String[] km : thongKeDAO.getDanhSachKhuyenMai()) cmbKhuyenMai.addItem(km[0]);
        pnFilter.add(createFilterItem("Khuyến mãi", cmbKhuyenMai));

        // Button
        JPanel pnButton = new JPanel(new BorderLayout()); pnButton.setOpaque(false);
        JButton btnXem = new JButton("Áp dụng");
        btnXem.setFocusPainted(false);
        btnXem.setBackground(new Color(0, 153, 102));
        btnXem.setForeground(Color.WHITE);
        btnXem.setFont(FONT_BOLD);
        btnXem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXem.setPreferredSize(new Dimension(100, 36));
        
        JPanel pnBtnContainer = new JPanel(new GridBagLayout()); pnBtnContainer.setOpaque(false); pnBtnContainer.add(btnXem);
        JPanel pnWrapBtn = new JPanel(new BorderLayout()); pnWrapBtn.setOpaque(false);
        pnWrapBtn.add(new JLabel(" "), BorderLayout.NORTH); pnWrapBtn.add(pnBtnContainer, BorderLayout.CENTER);
        pnFilter.add(pnWrapBtn);
        add(pnFilter, BorderLayout.NORTH);

        // -- Content --
        JPanel pnContent = new JPanel(new BorderLayout(0, 15));
        pnContent.setBackground(COLOR_BG);
        pnContent.setBorder(new EmptyBorder(10, 15, 10, 15));

        // KPI
        JPanel pnKPI = new JPanel(new GridLayout(1, 5, 15, 0));
        pnKPI.setBackground(COLOR_BG);
        pnKPI.setPreferredSize(new Dimension(0, 90));

        lblTongDoanhThu = createModernKPICard(pnKPI, "Tổng Doanh Thu", new Color(0, 123, 255));
        lblCaoNhat = createModernKPICard(pnKPI, "Tháng Cao Nhất", new Color(40, 167, 69));
        lblThapNhat = createModernKPICard(pnKPI, "Tháng Thấp Nhất", new Color(220, 53, 69));
        lblTrungBinh = createModernKPICard(pnKPI, "TB / Tháng", new Color(255, 193, 7));
        lblSoGiaoDich = createModernKPICard(pnKPI, "Tổng Giao Dịch", new Color(108, 117, 125));
        pnContent.add(pnKPI, BorderLayout.NORTH);

        // Chart
        JPanel pnChart = new JPanel(new BorderLayout());
        pnChart.setBackground(COLOR_BG);
        pnChart.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(new Color(220, 220, 220)), "Biểu đồ chi tiết", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, FONT_BOLD, new Color(80, 80, 80)
        ));
        bieuDoDoanhThu = new BieuDoCotJFreeChart();
        bieuDoDoanhThu.setTieuDeTrucX("Tháng"); bieuDoDoanhThu.setTieuDeTrucY("Doanh thu");
        pnChart.add(bieuDoDoanhThu, BorderLayout.CENTER);
        pnContent.add(pnChart, BorderLayout.CENTER);

        add(pnContent, BorderLayout.CENTER);

        btnXem.addActionListener(e -> {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            loadDuLieu();
            setCursor(Cursor.getDefaultCursor());
        });
        loadDuLieu();
    }

    private JPanel createFilterItem(String title, JComponent component) {
        JPanel p = new JPanel(new BorderLayout(5, 5)); p.setOpaque(false);
        JLabel lbl = new JLabel(title); lbl.setFont(new Font("Segoe UI", 0, 13)); lbl.setForeground(new Color(100,100,100));
        p.add(lbl, BorderLayout.NORTH); component.setFont(FONT_TEXT); component.setPreferredSize(new Dimension(component.getPreferredSize().width, 36));
        p.add(component, BorderLayout.CENTER); return p;
    }
    
    private JLabel createModernKPICard(JPanel parent, String title, Color accentColor) {
        JPanel p = new JPanel(new BorderLayout(10, 5)); p.setBackground(new Color(250, 250, 250));
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, accentColor), new EmptyBorder(10, 15, 10, 15)));
        JLabel lblTitle = new JLabel(title); lblTitle.setFont(new Font("Segoe UI", 0, 13)); lblTitle.setForeground(new Color(100, 100, 100));
        JLabel lblValue = new JLabel("0"); lblValue.setFont(new Font("Segoe UI", 1, 18)); lblValue.setForeground(new Color(50, 50, 50));
        p.add(lblTitle, BorderLayout.NORTH); p.add(lblValue, BorderLayout.CENTER);
        JPanel shadowWrap = new JPanel(new BorderLayout()); shadowWrap.setBorder(new LineBorder(new Color(230,230,230), 1)); shadowWrap.add(p);
        parent.add(shadowWrap); return lblValue;
    }

    private void loadDuLieu() {
        int nam = (Integer) cmbChonNam.getSelectedItem();
        String tenLoai = (String) cmbLoaiSP.getSelectedItem();
        String maLoaiSP = "Tất cả";
        if (!"Tất cả sản phẩm".equals(tenLoai)) { for (LoaiSanPham l : LoaiSanPham.values()) if (l.getTenLoai().equals(tenLoai)) { maLoaiSP = l.name(); break; } }
        String maKM = (String) cmbKhuyenMai.getSelectedItem();
        if ("Tất cả khuyến mãi".equals(maKM)) maKM = "Tất cả";

        List<BanGhiThongKe> ds = thongKeDAO.getDoanhThuTheoThang(nam, maLoaiSP, maKM);
        
        bieuDoDoanhThu.xoaToanBoDuLieu();
        bieuDoDoanhThu.setTieuDeBieuDo("Doanh Thu Năm " + nam);
        Color col = new Color(0, 153, 102);
        
        double tong = 0, max = Double.MIN_VALUE, min = Double.MAX_VALUE; int don = 0; String tMax = "", tMin = "";
        
        for (BanGhiThongKe item : ds) {
            bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot(item.thoiGian, "Doanh thu", item.doanhThu, col));
            tong += item.doanhThu; don += item.soLuongDon;
            if (item.doanhThu > max) { max = item.doanhThu; tMax = item.thoiGian; }
            if (item.doanhThu < min) { min = item.doanhThu; tMin = item.thoiGian; }
        }
        
        if (ds.isEmpty()) { max = 0; min = 0; }
        
        NumberFormat vn = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        lblTongDoanhThu.setText(vn.format(tong));
        lblSoGiaoDich.setText(String.valueOf(don));
        lblCaoNhat.setText(max > 0 ? "<html>" + vn.format(max) + "<br><span style='font-size:10px;color:gray'>(" + tMax + ")</span></html>" : "0 đ");
        lblThapNhat.setText(min < Double.MAX_VALUE ? "<html>" + vn.format(min) + "<br><span style='font-size:10px;color:gray'>(" + tMin + ")</span></html>" : "0 đ");
        
        double trungBinh = tong / 12; // Chia 12 tháng để so sánh
        lblTrungBinh.setText(vn.format(trungBinh));
        
        // === VẼ ĐƯỜNG TRUNG BÌNH ===
        bieuDoDoanhThu.veDuongTrungBinh(trungBinh);
    }
}