package gui.panel;

import java.awt.*;
import java.text.NumberFormat;
import java.time.Year;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.*;
import component.button.PillButton;
import component.chart.*;
import dao.ThongKe_DAO;
import dao.ThongKe_DAO.BanGhiThongKe;
import enums.LoaiSanPham;

public class ThongKeTheoNam_Panel extends JPanel {

    private JComboBox<Integer> cmbNamBatDau, cmbNamKetThuc;
    private JComboBox<String> cmbLoaiSP, cmbKhuyenMai;
    private BieuDoCotJFreeChart bieuDoDoanhThu;

    // Labels
    private JLabel lblGiaTriTongDoanhThu, lblGiaTriCaoNhat, lblGiaTriThapNhat;
    private JLabel lblGiaTriTrungBinh, lblGiaTriTongGiaoDich, lblGiaTriTangTruong;

    private ThongKe_DAO thongKeDAO;

    public ThongKeTheoNam_Panel() {
        thongKeDAO = new ThongKe_DAO();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel pnMain = new JPanel(new BorderLayout(0, 10));
        pnMain.setBackground(Color.WHITE);
        pnMain.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(pnMain, BorderLayout.CENTER);

        // --- FILTER LAYOUT (dùng layout null) ---
        JPanel pnFilter = new JPanel();
        pnFilter.setBackground(new Color(0xE3F2F5));
        pnFilter.setBorder(BorderFactory.createTitledBorder("Tiêu chí lọc"));
        pnFilter.setPreferredSize(new Dimension(0, 100));
        pnFilter.setLayout(null);

        int cur = Year.now().getValue();
        Integer[] years = new Integer[15];
        for (int i = 0; i < 15; i++)
            years[i] = cur - i;

        JLabel lblNamBatDau = new JLabel("Năm bắt đầu");
        lblNamBatDau.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNamBatDau.setBounds(20, 25, 100, 20);
        pnFilter.add(lblNamBatDau);

        cmbNamBatDau = new JComboBox<>(years);
        cmbNamBatDau.setSelectedItem(cur - 4);
        cmbNamBatDau.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbNamBatDau.setBounds(20, 50, 100, 30);
        pnFilter.add(cmbNamBatDau);

        JLabel lblNamKetThuc = new JLabel("Năm kết thúc");
        lblNamKetThuc.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNamKetThuc.setBounds(150, 25, 100, 20);
        pnFilter.add(lblNamKetThuc);

        cmbNamKetThuc = new JComboBox<>(years);
        cmbNamKetThuc.setSelectedItem(cur);
        cmbNamKetThuc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbNamKetThuc.setBounds(150, 50, 100, 30);
        pnFilter.add(cmbNamKetThuc);

        JLabel lblLoaiSP = new JLabel("Loại sản phẩm");
        lblLoaiSP.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblLoaiSP.setBounds(280, 25, 120, 20);
        pnFilter.add(lblLoaiSP);

        cmbLoaiSP = new JComboBox<>();
        cmbLoaiSP.addItem("Tất cả");
        for (LoaiSanPham l : LoaiSanPham.values())
            cmbLoaiSP.addItem(l.getTenLoai());
        cmbLoaiSP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbLoaiSP.setBounds(280, 50, 140, 30);
        pnFilter.add(cmbLoaiSP);

        JLabel lblKhuyenMai = new JLabel("Khuyến mãi");
        lblKhuyenMai.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblKhuyenMai.setBounds(450, 25, 100, 20);
        pnFilter.add(lblKhuyenMai);

        cmbKhuyenMai = new JComboBox<>();
        cmbKhuyenMai.addItem("Tất cả");
        for (String[] km : thongKeDAO.getDanhSachKhuyenMai())
            cmbKhuyenMai.addItem(km[0]);
        cmbKhuyenMai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbKhuyenMai.setBounds(450, 50, 140, 30);
        pnFilter.add(cmbKhuyenMai);

        JButton btnXem = new PillButton("Thống Kê");
        btnXem.setBounds(620, 45, 120, 35);
        pnFilter.add(btnXem);

        pnMain.add(pnFilter, BorderLayout.NORTH);

        // --- STATS (dời lên trên biểu đồ) ---
        JPanel pnStats = new JPanel(new GridLayout(2, 3, 20, 15));
        pnStats.setBackground(new Color(0xE3F2F5));
        pnStats.setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Tổng quan giai đoạn"),
                new EmptyBorder(10, 20, 10, 20)));
        pnStats.setPreferredSize(new Dimension(0, 180));

        Font fTitle = new Font("Tahoma", Font.PLAIN, 15);
        Font fValue = new Font("Tahoma", Font.BOLD, 18);
        Color cMain = new Color(219, 100, 100);

        lblGiaTriTongDoanhThu = createLabel(pnStats, "Tổng doanh thu:", fTitle, fValue, cMain);
        lblGiaTriCaoNhat = createLabel(pnStats, "Năm cao nhất:", fTitle, fValue, new Color(0x28a745));
        lblGiaTriThapNhat = createLabel(pnStats, "Năm thấp nhất:", fTitle, fValue, new Color(0xdc3545));

        lblGiaTriTrungBinh = createLabel(pnStats, "TB/Năm:", fTitle, fValue, cMain);
        lblGiaTriTongGiaoDich = createLabel(pnStats, "Tổng đơn hàng:", fTitle, fValue, cMain);
        lblGiaTriTangTruong = createLabel(pnStats, "So với giai đoạn trước:", fTitle, fValue, Color.GRAY);

        // --- CHART ---
        JPanel pnChart = new JPanel(new BorderLayout());
        pnChart.setBorder(BorderFactory.createTitledBorder("Biểu đồ so sánh qua các năm"));
        pnChart.setBackground(Color.WHITE);
        bieuDoDoanhThu = new BieuDoCotJFreeChart();
        bieuDoDoanhThu.setTieuDeTrucX("Năm");
        bieuDoDoanhThu.setTieuDeTrucY("Doanh thu");
        pnChart.add(bieuDoDoanhThu, BorderLayout.CENTER);

        JPanel pnContent = new JPanel(new BorderLayout(0, 10));
        pnContent.setBackground(Color.WHITE);

        // Đặt panel tổng quan ở trên, biểu đồ ở giữa
        JPanel pnTopSection = new JPanel(new BorderLayout(0, 10));
        pnTopSection.setBackground(Color.WHITE);
        pnTopSection.add(pnStats, BorderLayout.NORTH);
        pnTopSection.add(pnChart, BorderLayout.CENTER);

        pnContent.add(pnTopSection, BorderLayout.CENTER);
        pnMain.add(pnContent, BorderLayout.CENTER);

        btnXem.addActionListener(e -> loadDuLieu());
        loadDuLieu();
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

    private void loadDuLieu() {
        int namS = (Integer) cmbNamBatDau.getSelectedItem();
        int namE = (Integer) cmbNamKetThuc.getSelectedItem();
        if (namS > namE) {
            int t = namS;
            namS = namE;
            namE = t;
            cmbNamBatDau.setSelectedItem(namS);
            cmbNamKetThuc.setSelectedItem(namE);
        }

        String tenLoai = (String) cmbLoaiSP.getSelectedItem();
        String maLoaiSP = "Tất cả";
        if (!"Tất cả".equals(tenLoai)) {
            for (LoaiSanPham l : LoaiSanPham.values())
                if (l.getTenLoai().equals(tenLoai)) {
                    maLoaiSP = l.name();
                    break;
                }
        }
        String maKM = (String) cmbKhuyenMai.getSelectedItem();

        List<BanGhiThongKe> ds = thongKeDAO.getDoanhThuTheoNam(namS, namE, maLoaiSP, maKM);

        bieuDoDoanhThu.xoaToanBoDuLieu();
        bieuDoDoanhThu.setTieuDeBieuDo("Doanh Thu " + namS + " - " + namE);
        Color col = new Color(219, 100, 100);

        double tong = 0, max = 0;
        double min = Double.MAX_VALUE;
        int don = 0;
        String nMax = "", nMin = "";

        for (BanGhiThongKe item : ds) {
            bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot(item.thoiGian, "Doanh thu", item.doanhThu, col));
            tong += item.doanhThu;
            don += item.soLuongDon;

            if (item.doanhThu > max) {
                max = item.doanhThu;
                nMax = item.thoiGian;
            }
            if (item.doanhThu < min) {
                min = item.doanhThu;
                nMin = item.thoiGian;
            }
        }

        int soNamDuocChon = namE - namS + 1;
        if (soNamDuocChon > 0) {
            double trungBinh = tong / soNamDuocChon;
            if (trungBinh > 0) {
                bieuDoDoanhThu.themDuongTrungBinh(trungBinh);
            }
        }
        if (ds.isEmpty())
            min = 0;

        NumberFormat vn = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        lblGiaTriTongDoanhThu.setText(vn.format(tong));
        lblGiaTriTongGiaoDich.setText(String.valueOf(don));
        lblGiaTriTrungBinh.setText(vn.format(ds.isEmpty() ? 0 : tong / ds.size()));

        lblGiaTriCaoNhat.setText(max > 0
                ? "<html>" + vn.format(max) + "<br><span style='font-size:10px;color:gray'>(" + nMax + ")</span></html>"
                : "0 VNĐ");
        lblGiaTriThapNhat.setText(min > 0
                ? "<html>" + vn.format(min) + "<br><span style='font-size:10px;color:gray'>(" + nMin + ")</span></html>"
                : (ds.isEmpty() ? "0 VNĐ" : vn.format(min)));

        // Tính so sánh giai đoạn trước
        int soNam = namE - namS + 1;
        int namSBef = namS - soNam;
        int namEBef = namS - 1;

        List<BanGhiThongKe> dsTruoc = thongKeDAO.getDoanhThuTheoNam(namSBef, namEBef, maLoaiSP, maKM);
        double tongTruoc = dsTruoc.stream().mapToDouble(i -> i.doanhThu).sum();

        if (tongTruoc == 0) {
            lblGiaTriTangTruong.setText("---");
            lblGiaTriTangTruong.setForeground(Color.GRAY);
        } else {
            double phanTram = ((tong - tongTruoc) / tongTruoc) * 100;
            String icon = phanTram >= 0 ? "▲" : "▼";
            Color color = phanTram >= 0 ? new Color(0x28a745) : new Color(0xdc3545);
            lblGiaTriTangTruong.setText(String.format("%s %.1f%%", icon, Math.abs(phanTram)));
            lblGiaTriTangTruong.setForeground(color);
            lblGiaTriTangTruong.setToolTipText("Giai đoạn " + namSBef + "-" + namEBef + ": " + vn.format(tongTruoc));
        }
    }
}