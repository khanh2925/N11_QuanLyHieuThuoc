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
import dao.ThongKe_DAO.BanGhiTaiChinh;
import enums.LoaiSanPham;

public class ThongKeTheoNam_Panel extends JPanel {

    private JComboBox<Integer> cmbNamBatDau, cmbNamKetThuc;
    private JComboBox<String> cmbLoaiSP; // Đã xóa cmbKhuyenMai
    private BieuDoCotGroup bieuDoDoanhThu;
    private JLabel lblTongBanHang, lblTongNhapHang, lblTongTraHang, lblTongHuyHang, lblLoiNhuanRong;
    private ThongKe_DAO thongKeDAO;

    public ThongKeTheoNam_Panel() {
        thongKeDAO = new ThongKe_DAO();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel pnMain = new JPanel(new BorderLayout(0, 10));
        pnMain.setBackground(Color.WHITE);
        pnMain.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(pnMain, BorderLayout.CENTER);

        // --- FILTER ---
        JPanel pnFilter = new JPanel();
        pnFilter.setBackground(new Color(0xE3F2F5));
        pnFilter.setBorder(BorderFactory.createTitledBorder("Tiêu chí lọc"));
        pnFilter.setPreferredSize(new Dimension(0, 100));
        pnFilter.setLayout(null);

        int cur = Year.now().getValue();
        Integer[] years = new Integer[15];
        for (int i = 0; i < 15; i++) years[i] = cur - i;

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
        for (LoaiSanPham l : LoaiSanPham.values()) cmbLoaiSP.addItem(l.getTenLoai());
        cmbLoaiSP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbLoaiSP.setBounds(280, 50, 140, 30);
        pnFilter.add(cmbLoaiSP);

        // Đã xóa JLabel và JComboBox Khuyến mãi ở đây

        JButton btnXem = new PillButton("Thống Kê");
        btnXem.setBounds(450, 45, 120, 35); // Dời nút
        pnFilter.add(btnXem);

        pnMain.add(pnFilter, BorderLayout.NORTH);

        // --- STATS ---
        JPanel pnStats = new JPanel(new GridLayout(2, 3, 20, 15));
        pnStats.setBackground(new Color(0xE3F2F5));
        pnStats.setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Tổng quan tài chính"), new EmptyBorder(10, 20, 10, 20)));
        pnStats.setPreferredSize(new Dimension(0, 180));

        Font fTitle = new Font("Tahoma", Font.PLAIN, 15);
        Font fValue = new Font("Tahoma", Font.BOLD, 18);

        lblTongBanHang = createLabel(pnStats, "Tổng Bán Hàng (Thu):", fTitle, fValue, new Color(0x28a745));
        lblTongNhapHang = createLabel(pnStats, "Tổng Nhập Hàng (Chi):", fTitle, fValue, new Color(0x007bff));
        lblTongTraHang = createLabel(pnStats, "Tổng Trả Hàng (Chi):", fTitle, fValue, new Color(0xffc107));
        lblTongHuyHang = createLabel(pnStats, "Tổng Hủy Hàng (Chi):", fTitle, fValue, new Color(0xdc3545));
        lblLoiNhuanRong = createLabel(pnStats, "Lợi Nhuận (Thu - Chi):", fTitle, fValue, new Color(0x6610f2));
        createLabel(pnStats, "", fTitle, fValue, Color.BLACK).setVisible(false);

        // --- CHART ---
        JPanel pnChart = new JPanel(new BorderLayout());
        pnChart.setBorder(BorderFactory.createTitledBorder("Biểu đồ so sánh qua các năm"));
        pnChart.setBackground(Color.WHITE);
        bieuDoDoanhThu = new BieuDoCotGroup();
        bieuDoDoanhThu.setTieuDeTrucX("Năm");
        bieuDoDoanhThu.setTieuDeTrucY("Số tiền (VNĐ)");
        pnChart.add(bieuDoDoanhThu, BorderLayout.CENTER);

        JPanel pnContent = new JPanel(new BorderLayout(0, 10));
        pnContent.setBackground(Color.WHITE);
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
        JLabel lTitle = new JLabel(t); lTitle.setFont(f1);
        JLabel lValue = new JLabel("0 đ"); lValue.setFont(f2); lValue.setForeground(c);
        pChild.add(lTitle, BorderLayout.NORTH);
        pChild.add(lValue, BorderLayout.CENTER);
        p.add(pChild);
        return lValue;
    }

    private void loadDuLieu() {
        int namS = (Integer) cmbNamBatDau.getSelectedItem();
        int namE = (Integer) cmbNamKetThuc.getSelectedItem();
        if (namS > namE) {
            int t = namS; namS = namE; namE = t;
            cmbNamBatDau.setSelectedItem(namS);
            cmbNamKetThuc.setSelectedItem(namE);
        }

        String tenLoaiHienThi = (String) cmbLoaiSP.getSelectedItem();
        String maLoaiSP = "Tất cả";
        if (!"Tất cả".equals(tenLoaiHienThi)) {
            for (LoaiSanPham loai : LoaiSanPham.values()) {
                if (loai.getTenLoai().equals(tenLoaiHienThi)) { maLoaiSP = loai.name(); break; }
            }
        }

        // Gọi DAO mới (chỉ truyền năm và loại SP)
        List<BanGhiTaiChinh> ds = thongKeDAO.getThongKeTaiChinhTheoNam(namS, namE, maLoaiSP);

        bieuDoDoanhThu.xoaToanBoDuLieu();
        bieuDoDoanhThu.setTieuDeBieuDo("Tài Chính Giai Đoạn " + namS + " - " + namE);

        Color colBan = new Color(0x28a745);
        Color colNhap = new Color(0x007bff);
        Color colTra = new Color(0xffc107);
        Color colHuy = new Color(0xdc3545);

        double tongBan = 0, tongNhap = 0, tongTra = 0, tongHuy = 0;

        for (BanGhiTaiChinh item : ds) {
            String labelNam = item.thoiGian;

            bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot(labelNam, "Bán hàng", item.banHang, colBan));
            bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot(labelNam, "Nhập hàng", item.nhapHang, colNhap));
            bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot(labelNam, "Trả hàng", item.traHang, colTra));
            bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot(labelNam, "Hủy hàng", item.huyHang, colHuy));

            tongBan += item.banHang;
            tongNhap += item.nhapHang;
            tongTra += item.traHang;
            tongHuy += item.huyHang;
        }

        NumberFormat vn = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        lblTongBanHang.setText(vn.format(tongBan));
        lblTongNhapHang.setText(vn.format(tongNhap));
        lblTongTraHang.setText(vn.format(tongTra));
        lblTongHuyHang.setText(vn.format(tongHuy));

        double loiNhuan = tongBan - (tongNhap + tongTra + tongHuy);
        lblLoiNhuanRong.setText(vn.format(loiNhuan));
        lblLoiNhuanRong.setForeground(loiNhuan < 0 ? Color.RED : new Color(0x6610f2));
    }
}