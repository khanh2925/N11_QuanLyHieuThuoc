package gui.panel;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.*;
import com.toedter.calendar.JDateChooser;
import component.button.PillButton;
import component.chart.*;
import dao.ThongKe_DAO;
import dao.ThongKe_DAO.BanGhiTaiChinh;
import enums.LoaiSanPham;

public class ThongKeTheoNgay_Panel extends JPanel {

    private JDateChooser ngayBatDau_DataChoose, ngayKetThuc_DataChoose;
    private JComboBox<String> cmbLoaiSP; // Đã xóa cmbKhuyenMai
    private BieuDoCotGroup bieuDoDoanhThu;
    private JLabel lblTongBanHang, lblTongNhapHang, lblTongTraHang, lblTongHuyHang, lblLoiNhuanRong;
    private ThongKe_DAO thongKeDAO;

    public ThongKeTheoNgay_Panel() {
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

        JLabel lblTuNgay = new JLabel("Từ ngày");
        lblTuNgay.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblTuNgay.setBounds(20, 25, 80, 20);
        pnFilter.add(lblTuNgay);

        ngayBatDau_DataChoose = new JDateChooser();
        ngayBatDau_DataChoose.setDateFormatString("dd-MM-yyyy");
        ngayBatDau_DataChoose.setBounds(20, 50, 130, 30);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        ngayBatDau_DataChoose.setDate(cal.getTime());
        pnFilter.add(ngayBatDau_DataChoose);

        JLabel lblDenNgay = new JLabel("Đến ngày");
        lblDenNgay.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblDenNgay.setBounds(170, 25, 80, 20);
        pnFilter.add(lblDenNgay);

        ngayKetThuc_DataChoose = new JDateChooser();
        ngayKetThuc_DataChoose.setDateFormatString("dd-MM-yyyy");
        ngayKetThuc_DataChoose.setBounds(170, 50, 130, 30);
        ngayKetThuc_DataChoose.setDate(new Date());
        pnFilter.add(ngayKetThuc_DataChoose);

        JLabel lblLoaiSP = new JLabel("Loại sản phẩm");
        lblLoaiSP.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblLoaiSP.setBounds(320, 25, 120, 20);
        pnFilter.add(lblLoaiSP);

        cmbLoaiSP = new JComboBox<>();
        cmbLoaiSP.addItem("Tất cả");
        for (LoaiSanPham loai : LoaiSanPham.values()) cmbLoaiSP.addItem(loai.getTenLoai());
        cmbLoaiSP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbLoaiSP.setBounds(320, 50, 150, 30);
        pnFilter.add(cmbLoaiSP);

        // Đã xóa JLabel và JComboBox Khuyến mãi ở đây

        JButton btnLoc = new PillButton("Thống Kê");
        btnLoc.setBounds(500, 45, 120, 35); // Dời nút
        pnFilter.add(btnLoc);

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
        pnChart.setBorder(BorderFactory.createTitledBorder("Biểu đồ chi tiết Thu - Chi"));
        pnChart.setBackground(Color.WHITE);
        bieuDoDoanhThu = new BieuDoCotGroup();
        bieuDoDoanhThu.setTieuDeTrucX("Ngày");
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

        btnLoc.addActionListener(e -> loadDuLieu());
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
        Date tu = ngayBatDau_DataChoose.getDate();
        Date den = ngayKetThuc_DataChoose.getDate();
        if (tu == null || den == null) return;
        if (tu.after(den)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu phải trước ngày kết thúc!");
            return;
        }

        String tenLoaiHienThi = (String) cmbLoaiSP.getSelectedItem();
        String maLoaiSP = "Tất cả";
        if (!"Tất cả".equals(tenLoaiHienThi)) {
            for (LoaiSanPham loai : LoaiSanPham.values()) {
                if (loai.getTenLoai().equals(tenLoaiHienThi)) { maLoaiSP = loai.name(); break; }
            }
        }

        // Gọi DAO mới (chỉ truyền ngày và loại SP)
        List<BanGhiTaiChinh> ds = thongKeDAO.getThongKeTaiChinhTheoNgay(tu, den, maLoaiSP);

        bieuDoDoanhThu.xoaToanBoDuLieu();
        bieuDoDoanhThu.setTieuDeBieuDo("Thống Kê Từ " + formatDate(tu) + " Đến " + formatDate(den));

        Color colBan = new Color(0x28a745);
        Color colNhap = new Color(0x007bff);
        Color colTra = new Color(0xffc107);
        Color colHuy = new Color(0xdc3545);

        double tongBan = 0, tongNhap = 0, tongTra = 0, tongHuy = 0;

        for (BanGhiTaiChinh item : ds) {
            String labelNgay = item.thoiGian;

            bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot(labelNgay, "Bán hàng", item.banHang, colBan));
            bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot(labelNgay, "Nhập hàng", item.nhapHang, colNhap));
            bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot(labelNgay, "Trả hàng", item.traHang, colTra));
            bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot(labelNgay, "Hủy hàng", item.huyHang, colHuy));

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

    private String formatDate(Date d) {
        return new java.text.SimpleDateFormat("dd/MM/yyyy").format(d);
    }
}