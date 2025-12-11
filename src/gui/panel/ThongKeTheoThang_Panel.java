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

public class ThongKeTheoThang_Panel extends JPanel {

    private JComboBox<Integer> cmbChonNam;
    private JComboBox<String> cmbLoaiSP, cmbKhuyenMai;
    private BieuDoCotJFreeChart bieuDoDoanhThu;
    
    // Thêm các Label mới
    private JLabel lblGiaTriTongDoanhThu, lblGiaTriCaoNhat, lblGiaTriThapNhat;
    private JLabel lblGiaTriTrungBinh, lblGiaTriTongGiaoDich, lblGiaTriTangTruong;
    
    private ThongKe_DAO thongKeDAO;

    public ThongKeTheoThang_Panel() {
        thongKeDAO = new ThongKe_DAO();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel pnMain = new JPanel(new BorderLayout(0, 10));
        pnMain.setBackground(Color.WHITE);
        pnMain.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(pnMain, BorderLayout.CENTER);

        // --- FILTER ---
        JPanel pnFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnFilter.setBackground(new Color(0xE3F2F5));
        pnFilter.setBorder(BorderFactory.createTitledBorder("Tiêu chí lọc"));
        pnFilter.setPreferredSize(new Dimension(0, 80));

        pnFilter.add(new JLabel("Năm:"));
        int curYear = Year.now().getValue();
        Integer[] years = new Integer[10];
        for (int i = 0; i < 10; i++) years[i] = curYear - i;
        cmbChonNam = new JComboBox<>(years);
        cmbChonNam.setPreferredSize(new Dimension(80, 30));
        pnFilter.add(cmbChonNam);

        pnFilter.add(new JLabel("Loại SP:"));
        cmbLoaiSP = new JComboBox<>();
        cmbLoaiSP.addItem("Tất cả");
        for (LoaiSanPham l : LoaiSanPham.values()) cmbLoaiSP.addItem(l.getTenLoai());
        cmbLoaiSP.setPreferredSize(new Dimension(130, 30));
        pnFilter.add(cmbLoaiSP);

        pnFilter.add(new JLabel("Khuyến mãi:"));
        cmbKhuyenMai = new JComboBox<>();
        cmbKhuyenMai.addItem("Tất cả");
        for (String[] km : thongKeDAO.getDanhSachKhuyenMai()) cmbKhuyenMai.addItem(km[0]);
        cmbKhuyenMai.setPreferredSize(new Dimension(150, 30));
        pnFilter.add(cmbKhuyenMai);

        JButton btnXem = new JButton("Xem");
        btnXem.setBackground(new Color(0x009966));
        btnXem.setForeground(Color.WHITE);
        pnFilter.add(btnXem);
        pnMain.add(pnFilter, BorderLayout.NORTH);

        // --- CHART ---
        JPanel pnContent = new JPanel(new BorderLayout(0, 10));
        pnContent.setBackground(Color.WHITE);
        JPanel pnChart = new JPanel(new BorderLayout());
        pnChart.setBorder(BorderFactory.createTitledBorder("Biểu đồ doanh thu"));
        pnChart.setBackground(Color.WHITE);
        bieuDoDoanhThu = new BieuDoCotJFreeChart();
        bieuDoDoanhThu.setTieuDeTrucX("Tháng");
        bieuDoDoanhThu.setTieuDeTrucY("Doanh thu");
        pnChart.add(bieuDoDoanhThu, BorderLayout.CENTER);

        // --- STATS (GridLayout 2x3) ---
        JPanel pnStats = new JPanel(new GridLayout(2, 3, 20, 15));
        pnStats.setBackground(new Color(0xE3F2F5));
        pnStats.setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Tổng quan"), new EmptyBorder(10, 20, 10, 20)));
        pnStats.setPreferredSize(new Dimension(0, 200));
        
        Font fTitle = new Font("Tahoma", Font.PLAIN, 15);
        Font fValue = new Font("Tahoma", Font.BOLD, 18);
        Color cMain = new Color(0x009966);

        lblGiaTriTongDoanhThu = createLabel(pnStats, "Tổng doanh thu:", fTitle, fValue, cMain);
        lblGiaTriCaoNhat = createLabel(pnStats, "Tháng cao nhất:", fTitle, fValue, new Color(0x28a745));
        lblGiaTriThapNhat = createLabel(pnStats, "Tháng thấp nhất:", fTitle, fValue, new Color(0xdc3545));
        
        lblGiaTriTrungBinh = createLabel(pnStats, "TB/Tháng:", fTitle, fValue, cMain);
        lblGiaTriTongGiaoDich = createLabel(pnStats, "Tổng đơn hàng:", fTitle, fValue, cMain);
        lblGiaTriTangTruong = createLabel(pnStats, "So với năm trước:", fTitle, fValue, Color.GRAY);

        pnContent.add(pnChart, BorderLayout.CENTER);
        pnContent.add(pnStats, BorderLayout.SOUTH);
        pnMain.add(pnContent, BorderLayout.CENTER);

        btnXem.addActionListener(e -> loadDuLieu());
        loadDuLieu();
    }

    private JLabel createLabel(JPanel p, String t, Font f1, Font f2, Color c) {
        JPanel pChild = new JPanel(new BorderLayout(5, 5));
        pChild.setOpaque(false);
        pChild.add(new JLabel(t){{setFont(f1);}}, BorderLayout.NORTH);
        JLabel l = new JLabel("0"); l.setFont(f2); l.setForeground(c);
        pChild.add(l, BorderLayout.CENTER);
        p.add(pChild); 
        return l;
    }

    private void loadDuLieu() {
        int nam = (Integer) cmbChonNam.getSelectedItem();
        
        String tenLoai = (String) cmbLoaiSP.getSelectedItem();
        String maLoaiSP = "Tất cả";
        if (!"Tất cả".equals(tenLoai)) {
            for (LoaiSanPham l : LoaiSanPham.values()) if (l.getTenLoai().equals(tenLoai)) { maLoaiSP = l.name(); break; }
        }
        String maKM = (String) cmbKhuyenMai.getSelectedItem();

        List<BanGhiThongKe> ds = thongKeDAO.getDoanhThuTheoThang(nam, maLoaiSP, maKM);
        
        bieuDoDoanhThu.xoaToanBoDuLieu();
        bieuDoDoanhThu.setTieuDeBieuDo("Doanh Thu Năm " + nam);
        Color col = new Color(0, 153, 102);
        
        double tong = 0, max = 0; 
        double min = Double.MAX_VALUE;
        int don = 0; 
        String tMax = "", tMin = "";
        
        for (BanGhiThongKe item : ds) {
            bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot(item.thoiGian, "Doanh thu", item.doanhThu, col));
            tong += item.doanhThu; 
            don += item.soLuongDon;
            
            if (item.doanhThu > max) { max = item.doanhThu; tMax = item.thoiGian; }
            if (item.doanhThu < min) { min = item.doanhThu; tMin = item.thoiGian; }
        }
     // === ĐOẠN CODE MỚI THÊM VÀO ĐÂY ===
        // Tính trung bình theo tháng (chia cho 12 tháng cố định hoặc chia cho ds.size() tùy nghiệp vụ)
        // Ở đây tôi dùng chia 12 theo logic label cũ của bạn
        double trungBinh = tong / 12; 
        if (trungBinh > 0) {
            bieuDoDoanhThu.themDuongTrungBinh(trungBinh);
        }
        if(ds.isEmpty()) min = 0;
        
        NumberFormat vn = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        lblGiaTriTongDoanhThu.setText(vn.format(tong));
        lblGiaTriTongGiaoDich.setText(String.valueOf(don));
        lblGiaTriTrungBinh.setText(vn.format(tong / 12)); // Trung bình chia 12 tháng
        
        lblGiaTriCaoNhat.setText(max > 0 ? "<html>" + vn.format(max) + "<br><span style='font-size:10px;color:gray'>(" + tMax + ")</span></html>" : "0 VNĐ");
        lblGiaTriThapNhat.setText(min > 0 ? "<html>" + vn.format(min) + "<br><span style='font-size:10px;color:gray'>(" + tMin + ")</span></html>" : (ds.isEmpty() ? "0 VNĐ" : vn.format(min)));

        // Tính so sánh năm trước
        // Năm trước
        List<BanGhiThongKe> dsNamTruoc = thongKeDAO.getDoanhThuTheoThang(nam - 1, maLoaiSP, maKM);
        double tongNamTruoc = dsNamTruoc.stream().mapToDouble(i -> i.doanhThu).sum();
        
        if (tongNamTruoc == 0) {
            lblGiaTriTangTruong.setText("---");
            lblGiaTriTangTruong.setForeground(Color.GRAY);
        } else {
            double phanTram = ((tong - tongNamTruoc) / tongNamTruoc) * 100;
            String icon = phanTram >= 0 ? "▲" : "▼";
            Color color = phanTram >= 0 ? new Color(0x28a745) : new Color(0xdc3545);
            lblGiaTriTangTruong.setText(String.format("%s %.1f%%", icon, Math.abs(phanTram)));
            lblGiaTriTangTruong.setForeground(color);
            lblGiaTriTangTruong.setToolTipText("Năm " + (nam-1) + ": " + vn.format(tongNamTruoc));
        }
    }
}