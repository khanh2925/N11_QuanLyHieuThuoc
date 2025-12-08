package gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import com.toedter.calendar.JDateChooser;

import component.chart.BieuDoCotJFreeChart;
import component.chart.DuLieuBieuDoCot;

public class ThongKeTheoNgay_Panel extends JPanel {

    private JDateChooser ngayBatDau_DataChoose;
    private BieuDoCotJFreeChart bieuDoDoanhThu;

    public ThongKeTheoNgay_Panel() {
        // --- Thiết lập cơ bản cho panel này ---
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

        // Các component trong panel Tiêu chí lọc
        JLabel lblNgayBatDau = new JLabel("Ngày bắt đầu");
        lblNgayBatDau.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblNgayBatDau.setBounds(20, 27, 114, 20);
        pnTieuChiLoc.add(lblNgayBatDau);

        ngayBatDau_DataChoose = new JDateChooser();
        ngayBatDau_DataChoose.setDateFormatString("dd-MM-yyyy");
        ngayBatDau_DataChoose.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ngayBatDau_DataChoose.setBounds(20, 63, 200, 30);
        pnTieuChiLoc.add(ngayBatDau_DataChoose);

        JLabel lblNgayKetThuc = new JLabel("Ngày kết thúc");
        lblNgayKetThuc.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblNgayKetThuc.setBounds(299, 27, 122, 20);
        pnTieuChiLoc.add(lblNgayKetThuc);

        JDateChooser ngayKetThuc_DataChoose = new JDateChooser();
        ngayKetThuc_DataChoose.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ngayKetThuc_DataChoose.setDateFormatString("dd-MM-yyyy");
        ngayKetThuc_DataChoose.setBounds(299, 63, 200, 30);
        pnTieuChiLoc.add(ngayKetThuc_DataChoose);

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
        bieuDoDoanhThu.setBuocNhayTrucY(500000);
        bieuDoDoanhThu.setTieuDeTrucX("Ngày trong tuần");
        bieuDoDoanhThu.setTieuDeTrucY("Doanh thu (VNĐ)");
        pnBieuDo.add(bieuDoDoanhThu, BorderLayout.CENTER);

        // ===== PANEL THỐNG KÊ TỔNG QUAN =====
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

        JLabel lblDoanhThuCaoNhat = new JLabel("Doanh thu cao nhất:", SwingConstants.LEFT);
        lblDoanhThuCaoNhat.setFont(labelFont);
        JLabel lblGiaTriCaoNhat = new JLabel("0 VNĐ (dd/mm/yyyy)");
        lblGiaTriCaoNhat.setFont(valueFont);
        lblGiaTriCaoNhat.setForeground(valueColor);

        JLabel lblTongGiaoDich = new JLabel("Tổng số giao dịch:", SwingConstants.LEFT);
        lblTongGiaoDich.setFont(labelFont);
        JLabel lblGiaTriTongGiaoDich = new JLabel("0");
        lblGiaTriTongGiaoDich.setFont(valueFont);
        lblGiaTriTongGiaoDich.setForeground(valueColor);

        JLabel lblDoanhThuTrungBinh = new JLabel("Doanh thu trung bình/ngày:", SwingConstants.LEFT);
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

        // Vẽ biểu đồ với dữ liệu mẫu
        veBieuDoVoiDuLieuMau();
    }

    /**
     * Nạp dữ liệu mẫu vào biểu đồ.
     * Sau này bạn sẽ thay thế phần dữ liệu cứng này bằng dữ liệu lấy từ database.
     */
    private void veBieuDoVoiDuLieuMau() {
        bieuDoDoanhThu.xoaToanBoDuLieu();
        bieuDoDoanhThu.setTieuDeBieuDo("Thống Kê Doanh Thu Theo Ngày");

        Color mauCot = new Color(79, 129, 189);
        String tenNhom = "Doanh thu";

        // Sử dụng ngày hôm nay và lùi lại 7 ngày để dữ liệu luôn mới
        // (Ví dụ hôm nay là 26-10-2025)
        bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot("20/10/2025", tenNhom, 1500000, mauCot));
        bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot("21/10/2025", tenNhom, 2100000, mauCot));
        bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot("22/10/2025", tenNhom, 1850000, mauCot));
        bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot("23/10/2025", tenNhom, 3200000, mauCot));
        bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot("24/10/2025", tenNhom, 2800000, mauCot));
        bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot("25/10/2025", tenNhom, 2500000, mauCot));
        bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot("26/10/2025", tenNhom, 4100000, mauCot));
    }
}