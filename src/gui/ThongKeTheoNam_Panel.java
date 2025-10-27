package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.Year;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;


import customcomponent.BieuDoCotJFreeChart;
import customcomponent.DuLieuBieuDoCot;

public class ThongKeTheoNam_Panel extends JPanel {

    private JComboBox<Integer> cmbNamBatDau;
    private JComboBox<Integer> cmbNamKetThuc;
    
    private BieuDoCotJFreeChart bieuDoDoanhThu;

    public ThongKeTheoNam_Panel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // --- Bắt đầu xây dựng giao diện ---
        JPanel pnMain = new JPanel(new BorderLayout(0, 10));
        pnMain.setBackground(Color.WHITE);
        pnMain.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(pnMain, BorderLayout.CENTER);

        // -- Panel chứa các tiêu chí lọc --
        JPanel pnTieuChiLoc = new JPanel();
        pnTieuChiLoc.setBackground(new Color(0xE3F2F5));
        pnTieuChiLoc.setBorder(BorderFactory.createTitledBorder("Tiêu chí lọc"));
        pnTieuChiLoc.setPreferredSize(new Dimension(0, 120));
        pnTieuChiLoc.setLayout(null);

        
        JLabel lblNamBatDau = new JLabel("Năm bắt đầu");
        lblNamBatDau.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblNamBatDau.setBounds(20, 27, 114, 20);
        pnTieuChiLoc.add(lblNamBatDau);

        // Tạo dữ liệu 10 năm gần nhất
        int namHienTai = Year.now().getValue();
        Integer[] namData = new Integer[10];
        for (int i = 0; i < 10; i++) {
            namData[i] = namHienTai - i;
        }

        cmbNamBatDau = new JComboBox<>(namData);
        cmbNamBatDau.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbNamBatDau.setBounds(20, 63, 200, 30);
        ((JLabel) cmbNamBatDau.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        pnTieuChiLoc.add(cmbNamBatDau);

        JLabel lblNamKetThuc = new JLabel("Năm kết thúc");
        lblNamKetThuc.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblNamKetThuc.setBounds(299, 27, 122, 20);
        pnTieuChiLoc.add(lblNamKetThuc);

        cmbNamKetThuc = new JComboBox<>(namData);
        cmbNamKetThuc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbNamKetThuc.setBounds(299, 63, 200, 30);
        ((JLabel) cmbNamKetThuc.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        pnTieuChiLoc.add(cmbNamKetThuc);

        
        JLabel lblLoaiSanPham = new JLabel("Loại sản phẩm");
        lblLoaiSanPham.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblLoaiSanPham.setBounds(580, 27, 122, 20);
        pnTieuChiLoc.add(lblLoaiSanPham);

        String[] loaiSanPhamData = {"Tất cả sản phẩm", "Thuốc kê đơn", "Thuốc không kê đơn", "Thực phẩm chức năng", "Dụng cụ y tế"};
        JComboBox<String> cmbLoaiSanPham = new JComboBox<>(loaiSanPhamData);
        ((JLabel) cmbLoaiSanPham.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        cmbLoaiSanPham.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbLoaiSanPham.setBounds(580, 63, 200, 30);
        pnTieuChiLoc.add(cmbLoaiSanPham);

        JLabel lblKhuyenMai = new JLabel("Khuyến mãi");
        lblKhuyenMai.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblKhuyenMai.setBounds(860, 27, 122, 20);
        pnTieuChiLoc.add(lblKhuyenMai);

        String[] khuyenMaiData = {"Tất cả khuyến mãi", "Giảm giá 10%", "Mua 1 tặng 1", "Không áp dụng"};
        JComboBox<String> cmbKhuyenMai = new JComboBox<>(khuyenMaiData);
        ((JLabel) cmbKhuyenMai.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        cmbKhuyenMai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbKhuyenMai.setBounds(860, 63, 200, 30);
        pnTieuChiLoc.add(cmbKhuyenMai);

        pnMain.add(pnTieuChiLoc, BorderLayout.NORTH);

        // Panel bao bọc biểu đồ và thống kê
        JPanel pnMainContent = new JPanel(new BorderLayout(0, 10));
        pnMainContent.setBackground(Color.WHITE);

        // ===== PANEL BIỂU ĐỒ =====
        JPanel pnBieuDo = new JPanel();
        pnBieuDo.setBorder(BorderFactory.createTitledBorder("Biểu đồ doanh thu"));
        pnBieuDo.setBackground(Color.WHITE);
        pnBieuDo.setLayout(new BorderLayout(0, 0));

        bieuDoDoanhThu = new BieuDoCotJFreeChart();
        bieuDoDoanhThu.setBuocNhayTrucY(50000000);
        bieuDoDoanhThu.setTieuDeTrucX("Năm");
        bieuDoDoanhThu.setTieuDeTrucY("Doanh thu (VNĐ)");
        pnBieuDo.add(bieuDoDoanhThu, BorderLayout.CENTER);
        JPanel pnThongKe = new JPanel();
        pnThongKe.setBackground(new Color(0xE3F2F5));
        pnThongKe.setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder("Tổng quan"),
                new EmptyBorder(10, 20, 10, 20)
        ));
        pnThongKe.setPreferredSize(new Dimension(0, 140));
        pnThongKe.setLayout(new GridLayout(2, 4, 20, 10));

        Font labelFont = new Font("Tahoma", Font.PLAIN, 16);
        Font valueFont = new Font("Tahoma", Font.BOLD, 18);
        Color valueColor = new Color(0x005a9e);

        JLabel lblTongDoanhThu = new JLabel("Tổng doanh thu:", SwingConstants.LEFT);
        lblTongDoanhThu.setFont(labelFont);
        JLabel lblGiaTriTongDoanhThu = new JLabel("0 VNĐ");
        lblGiaTriTongDoanhThu.setFont(valueFont);
        lblGiaTriTongDoanhThu.setForeground(valueColor);

        JLabel lblDoanhThuCaoNhat = new JLabel("Năm doanh thu cao nhất:", SwingConstants.LEFT);
        lblDoanhThuCaoNhat.setFont(labelFont);
        JLabel lblGiaTriCaoNhat = new JLabel("0 VNĐ (YYYY)");
        lblGiaTriCaoNhat.setFont(valueFont);
        lblGiaTriCaoNhat.setForeground(valueColor);

        JLabel lblTongGiaoDich = new JLabel("Tổng số giao dịch:", SwingConstants.LEFT);
        lblTongGiaoDich.setFont(labelFont);
        JLabel lblGiaTriTongGiaoDich = new JLabel("0");
        lblGiaTriTongGiaoDich.setFont(valueFont);
        lblGiaTriTongGiaoDich.setForeground(valueColor);

        JLabel lblDoanhThuTrungBinh = new JLabel("Doanh thu trung bình/năm:", SwingConstants.LEFT);
        lblDoanhThuTrungBinh.setFont(labelFont);
        JLabel lblGiaTriTrungBinh = new JLabel("0 VNĐ");
        lblGiaTriTrungBinh.setFont(valueFont);
        lblGiaTriTrungBinh.setForeground(valueColor);

        pnThongKe.add(lblTongDoanhThu);
        pnThongKe.add(lblGiaTriTongDoanhThu);
        pnThongKe.add(lblDoanhThuCaoNhat);
        pnThongKe.add(lblGiaTriCaoNhat);
        pnThongKe.add(lblTongGiaoDich);
        pnThongKe.add(lblGiaTriTongGiaoDich);
        pnThongKe.add(lblDoanhThuTrungBinh);
        pnThongKe.add(lblGiaTriTrungBinh);

        pnMainContent.add(pnBieuDo, BorderLayout.CENTER);
        pnMainContent.add(pnThongKe, BorderLayout.SOUTH);
        pnMain.add(pnMainContent, BorderLayout.CENTER);

        // Vẽ biểu đồ với dữ liệu mẫu theo năm
        veBieuDoVoiDuLieuMau();
    }

    /**
     * Nạp dữ liệu mẫu vào biểu đồ (Dữ liệu theo năm).
     */
    private void veBieuDoVoiDuLieuMau() {
        bieuDoDoanhThu.xoaToanBoDuLieu();
        bieuDoDoanhThu.setTieuDeBieuDo("Thống Kê Doanh Thu Theo Năm");

        Color mauCot = new Color(219, 100, 100);
        String tenNhom = "Doanh thu";

        // Thêm dữ liệu mẫu cho 5 năm
        int namHienTai = Year.now().getValue();
        bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot(Integer.toString(namHienTai - 4), tenNhom, 150000000, mauCot));
        bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot(Integer.toString(namHienTai - 3), tenNhom, 210000000, mauCot));
        bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot(Integer.toString(namHienTai - 2), tenNhom, 185000000, mauCot));
        bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot(Integer.toString(namHienTai - 1), tenNhom, 320000000, mauCot));
        bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot(Integer.toString(namHienTai), tenNhom, 410000000, mauCot));
    }
}