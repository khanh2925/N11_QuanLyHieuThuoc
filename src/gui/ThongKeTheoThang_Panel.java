package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.Year; // Dùng để lấy năm hiện tại một cách tự động

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import customcomponent.BieuDoCotJFreeChart;
import customcomponent.DuLieuBieuDoCot;

public class ThongKeTheoThang_Panel extends JPanel {

    // Thay đổi bộ lọc: dùng JComboBox cho Tháng và Năm
    private JComboBox<String> cmbChonThang;
    private JComboBox<Integer> cmbChonNam;
    private BieuDoCotJFreeChart bieuDoDoanhThu;

    public ThongKeTheoThang_Panel() {
        // --- Thiết lập cơ bản cho panel này ---
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // --- Bắt đầu xây dựng giao diện ---
        JPanel pnMain = new JPanel(new BorderLayout(0, 10));
        pnMain.setBackground(Color.WHITE);
        pnMain.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(pnMain, BorderLayout.CENTER);

        // -- Panel chứa các tiêu chí lọc (Đã điều chỉnh) --
        JPanel pnTieuChiLoc = new JPanel();
        pnTieuChiLoc.setBackground(new Color(0xE3F2F5));
        pnTieuChiLoc.setBorder(BorderFactory.createTitledBorder("Tiêu chí lọc"));
        pnTieuChiLoc.setPreferredSize(new Dimension(0, 120));
        pnTieuChiLoc.setLayout(null);

        // === THAY ĐỔI BỘ LỌC TỪ JDateChooser SANG JComboBox ===
        
        JLabel lblChonThang = new JLabel("Chọn tháng");
        lblChonThang.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblChonThang.setBounds(20, 27, 114, 20);
        pnTieuChiLoc.add(lblChonThang);

        String[] thangData = {
            "Tất cả các tháng", "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
            "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
        };
        cmbChonThang = new JComboBox<>(thangData);
        cmbChonThang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbChonThang.setBounds(20, 63, 200, 30);
        ((JLabel) cmbChonThang.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        pnTieuChiLoc.add(cmbChonThang);

        JLabel lblChonNam = new JLabel("Chọn năm");
        lblChonNam.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblChonNam.setBounds(299, 27, 122, 20);
        pnTieuChiLoc.add(lblChonNam);

        // Tự động lấy 5 năm gần nhất để làm dữ liệu cho ComboBox
        Integer[] namData = new Integer[5];
        int namHienTai = Year.now().getValue();
        for (int i = 0; i < 5; i++) {
            namData[i] = namHienTai - i;
        }
        cmbChonNam = new JComboBox<>(namData);
        cmbChonNam.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbChonNam.setBounds(299, 63, 200, 30);
        ((JLabel) cmbChonNam.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        pnTieuChiLoc.add(cmbChonNam);
        
        // === CÁC BỘ LỌC CÒN LẠI GIỮ NGUYÊN ===

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

        JPanel pnMainContent = new JPanel(new BorderLayout(0, 10));
        pnMainContent.setBackground(Color.WHITE);

        JPanel pnBieuDo = new JPanel();
        pnBieuDo.setBorder(BorderFactory.createTitledBorder("Biểu đồ doanh thu"));
        pnBieuDo.setBackground(Color.WHITE);
        pnBieuDo.setLayout(new BorderLayout(0, 0));

        bieuDoDoanhThu = new BieuDoCotJFreeChart();
        bieuDoDoanhThu.setBuocNhayTrucY(5000000);
        bieuDoDoanhThu.setTieuDeTrucX("Tháng trong năm");
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
        
        // === CẬP NHẬT NHÃN THỐNG KÊ CHO PHÙ HỢP ===

        JLabel lblTongDoanhThu = new JLabel("Tổng doanh thu (năm):", SwingConstants.LEFT);
        lblTongDoanhThu.setFont(labelFont);
        JLabel lblGiaTriTongDoanhThu = new JLabel("0 VNĐ");
        lblGiaTriTongDoanhThu.setFont(valueFont);
        lblGiaTriTongDoanhThu.setForeground(valueColor);

        JLabel lblDoanhThuCaoNhat = new JLabel("Tháng doanh thu cao nhất:", SwingConstants.LEFT);
        lblDoanhThuCaoNhat.setFont(labelFont);
        JLabel lblGiaTriCaoNhat = new JLabel("0 VNĐ (Tháng X/YYYY)");
        lblGiaTriCaoNhat.setFont(valueFont);
        lblGiaTriCaoNhat.setForeground(valueColor);

        JLabel lblTongGiaoDich = new JLabel("Tổng số giao dịch (năm):", SwingConstants.LEFT);
        lblTongGiaoDich.setFont(labelFont);
        JLabel lblGiaTriTongGiaoDich = new JLabel("0");
        lblGiaTriTongGiaoDich.setFont(valueFont);
        lblGiaTriTongGiaoDich.setForeground(valueColor);

        JLabel lblDoanhThuTrungBinh = new JLabel("Doanh thu trung bình/tháng:", SwingConstants.LEFT);
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

        veBieuDoVoiDuLieuMau();
    }

    /**
     * Nạp dữ liệu mẫu vào biểu đồ (Dữ liệu theo tháng).
     */
    private void veBieuDoVoiDuLieuMau() {
        bieuDoDoanhThu.xoaToanBoDuLieu();
        bieuDoDoanhThu.setTieuDeBieuDo("Thống Kê Doanh Thu Theo Tháng");

        Color mauCot = new Color(0, 153, 102); // Đổi màu để phân biệt với biểu đồ ngày
        String tenNhom = "Doanh thu";

        // === CẬP NHẬT DỮ LIỆU MẪU CHO 12 THÁNG ===
        bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot("T1", tenNhom, 15000000, mauCot));
        bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot("T2", tenNhom, 12100000, mauCot));
        bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot("T3", tenNhom, 18500000, mauCot));
        bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot("T4", tenNhom, 23200000, mauCot));
        bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot("T5", tenNhom, 21800000, mauCot));
        bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot("T6", tenNhom, 19500000, mauCot));
        bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot("T7", tenNhom, 25100000, mauCot));
        bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot("T8", tenNhom, 24300000, mauCot));
        bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot("T9", tenNhom, 28900000, mauCot));
        bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot("T10", tenNhom, 31100000, mauCot));
        bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot("T11", tenNhom, 26700000, mauCot));
        bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot("T12", tenNhom, 45000000, mauCot));
    }
}