package gui.panel;

import java.awt.*;
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

public class ThongKeTheoNam_Panel extends JPanel {

    private JComboBox<Integer> cmbNamBatDau, cmbNamKetThuc;
    private JComboBox<String> cmbLoaiSP, cmbKhuyenMai;
    private BieuDoCotJFreeChart bieuDoDoanhThu;
    private JLabel lblGiaTriTongDoanhThu, lblGiaTriCaoNhat, lblGiaTriTongGiaoDich, lblGiaTriTrungBinh;
    private ThongKe_DAO thongKeDAO;

    public ThongKeTheoNam_Panel() {
        thongKeDAO = new ThongKe_DAO();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel pnMain = new JPanel(new BorderLayout(0, 10));
        pnMain.setBackground(Color.WHITE);
        pnMain.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(pnMain, BorderLayout.CENTER);

        // --- FILTER LAYOUT (Khớp với image_dda317.png) ---
        JPanel pnFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnFilter.setBackground(new Color(0xE3F2F5));
        pnFilter.setBorder(BorderFactory.createTitledBorder("Tiêu chí lọc"));
        pnFilter.setPreferredSize(new Dimension(0, 80));

        int cur = Year.now().getValue();
        Integer[] years = new Integer[15]; for (int i = 0; i < 15; i++) years[i] = cur - i;

        // Năm bắt đầu
        pnFilter.add(new JLabel("Năm bắt đầu:"));
        cmbNamBatDau = new JComboBox<>(years);
        cmbNamBatDau.setSelectedItem(cur - 4);
        cmbNamBatDau.setPreferredSize(new Dimension(90, 30));
        pnFilter.add(cmbNamBatDau);

        // Năm kết thúc
        pnFilter.add(new JLabel("Năm kết thúc:"));
        cmbNamKetThuc = new JComboBox<>(years);
        cmbNamKetThuc.setSelectedItem(cur);
        cmbNamKetThuc.setPreferredSize(new Dimension(90, 30));
        pnFilter.add(cmbNamKetThuc);

        // Loại sản phẩm
        pnFilter.add(new JLabel("Loại sản phẩm:"));
        cmbLoaiSP = new JComboBox<>();
        cmbLoaiSP.addItem("Tất cả");
        for (LoaiSanPham l : LoaiSanPham.values()) cmbLoaiSP.addItem(l.getTenLoai());
        cmbLoaiSP.setPreferredSize(new Dimension(140, 30));
        pnFilter.add(cmbLoaiSP);

        // Khuyến mãi
        pnFilter.add(new JLabel("Khuyến mãi:"));
        cmbKhuyenMai = new JComboBox<>();
        cmbKhuyenMai.addItem("Tất cả");
        for (String[] km : thongKeDAO.getDanhSachKhuyenMai()) cmbKhuyenMai.addItem(km[0]);
        cmbKhuyenMai.setPreferredSize(new Dimension(140, 30));
        pnFilter.add(cmbKhuyenMai);

        JButton btnXem = new JButton("Xem");
        btnXem.setBackground(new Color(219, 100, 100));
        btnXem.setForeground(Color.WHITE);
        pnFilter.add(btnXem);
        
        pnMain.add(pnFilter, BorderLayout.NORTH);

        // --- CHART & STATS ---
        JPanel pnContent = new JPanel(new BorderLayout(0, 10));
        pnContent.setBackground(Color.WHITE);
        JPanel pnChart = new JPanel(new BorderLayout());
        pnChart.setBorder(BorderFactory.createTitledBorder("Biểu đồ so sánh qua các năm"));
        pnChart.setBackground(Color.WHITE);
        bieuDoDoanhThu = new BieuDoCotJFreeChart();
        bieuDoDoanhThu.setTieuDeTrucX("Năm");
        bieuDoDoanhThu.setTieuDeTrucY("Doanh thu");
        pnChart.add(bieuDoDoanhThu, BorderLayout.CENTER);

        JPanel pnStats = new JPanel(new GridLayout(2, 4, 20, 10));
        pnStats.setBackground(new Color(0xE3F2F5));
        pnStats.setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Tổng quan giai đoạn"), new EmptyBorder(10, 20, 10, 20)));
        pnStats.setPreferredSize(new Dimension(0, 140));
        Font f1 = new Font("Tahoma", 0, 16); Font f2 = new Font("Tahoma", 1, 18); Color c = new Color(219, 100, 100);
        lblGiaTriTongDoanhThu = createLabel(pnStats, "Tổng doanh thu:", f1, f2, c);
        lblGiaTriCaoNhat = createLabel(pnStats, "Năm cao nhất:", f1, f2, c);
        lblGiaTriTongGiaoDich = createLabel(pnStats, "Tổng giao dịch:", f1, f2, c);
        lblGiaTriTrungBinh = createLabel(pnStats, "TB/Năm:", f1, f2, c);

        pnContent.add(pnChart, BorderLayout.CENTER);
        pnContent.add(pnStats, BorderLayout.SOUTH);
        pnMain.add(pnContent, BorderLayout.CENTER);

        btnXem.addActionListener(e -> loadDuLieu());
        loadDuLieu();
    }

    private JLabel createLabel(JPanel p, String t, Font f1, Font f2, Color c) {
        p.add(new JLabel(t)).setFont(f1); JLabel l = new JLabel("0"); l.setFont(f2); l.setForeground(c); p.add(l); return l;
    }

    private void loadDuLieu() {
        int namS = (Integer) cmbNamBatDau.getSelectedItem();
        int namE = (Integer) cmbNamKetThuc.getSelectedItem();
        if (namS > namE) { int t = namS; namS = namE; namE = t; cmbNamBatDau.setSelectedItem(namS); cmbNamKetThuc.setSelectedItem(namE); }
        
        String tenLoai = (String) cmbLoaiSP.getSelectedItem();
        String maLoaiSP = "Tất cả";
        if (!"Tất cả".equals(tenLoai)) {
            for (LoaiSanPham l : LoaiSanPham.values()) if (l.getTenLoai().equals(tenLoai)) { maLoaiSP = l.name(); break; }
        }
        String maKM = (String) cmbKhuyenMai.getSelectedItem();

        List<BanGhiThongKe> ds = thongKeDAO.getDoanhThuTheoNam(namS, namE, maLoaiSP, maKM);
        
        bieuDoDoanhThu.xoaToanBoDuLieu();
        bieuDoDoanhThu.setTieuDeBieuDo("Doanh Thu " + namS + " - " + namE);
        Color col = new Color(219, 100, 100);
        double tong = 0, max = 0; int don = 0; String nMax = "";
        
        for (BanGhiThongKe item : ds) {
            bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot(item.thoiGian, "Doanh thu", item.doanhThu, col));
            tong += item.doanhThu; don += item.soLuongDon;
            if (item.doanhThu > max) { max = item.doanhThu; nMax = item.thoiGian; }
        }
        
        NumberFormat vn = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        lblGiaTriTongDoanhThu.setText(vn.format(tong));
        lblGiaTriTongGiaoDich.setText(String.valueOf(don));
        lblGiaTriCaoNhat.setText(max > 0 ? "<html>" + vn.format(max) + "<br><span style='font-size:10px'>(" + nMax + ")</span></html>" : "0 VNĐ");
        lblGiaTriTrungBinh.setText(vn.format(ds.isEmpty() ? 0 : tong / ds.size()));
    }
}