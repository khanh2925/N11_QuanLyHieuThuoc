package gui.panel;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import com.toedter.calendar.JDateChooser;
import component.chart.BieuDoCotJFreeChart;
import component.chart.DuLieuBieuDoCot;
import dao.ThongKe_DAO;
import dao.ThongKe_DAO.BanGhiThongKe;
import enums.LoaiSanPham;

public class ThongKeTheoNgay_Panel extends JPanel {

    private JDateChooser ngayBatDau_DataChoose, ngayKetThuc_DataChoose;
    private JComboBox<String> cmbLoaiSP;
    private JComboBox<String> cmbKhuyenMai;
    private BieuDoCotJFreeChart bieuDoDoanhThu;
    
    // Thêm các Label mới
    private JLabel lblGiaTriTongDoanhThu, lblGiaTriCaoNhat, lblGiaTriThapNhat;
    private JLabel lblGiaTriTrungBinh, lblGiaTriTongGiaoDich, lblGiaTriTangTruong;
    
    private ThongKe_DAO thongKeDAO;

    public ThongKeTheoNgay_Panel() {
        thongKeDAO = new ThongKe_DAO(); 
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel pnMain = new JPanel(new BorderLayout(0, 10));
        pnMain.setBackground(Color.WHITE);
        pnMain.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(pnMain, BorderLayout.CENTER);

        // --- Panel Tiêu chí lọc ---
        JPanel pnTieuChiLoc = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnTieuChiLoc.setBackground(new Color(0xE3F2F5));
        pnTieuChiLoc.setBorder(BorderFactory.createTitledBorder("Tiêu chí lọc"));
        pnTieuChiLoc.setPreferredSize(new Dimension(0, 80));

        pnTieuChiLoc.add(new JLabel("Từ:"));
        ngayBatDau_DataChoose = new JDateChooser();
        ngayBatDau_DataChoose.setDateFormatString("dd/MM/yyyy");
        ngayBatDau_DataChoose.setPreferredSize(new Dimension(110, 30));
        // Mặc định lấy 7 ngày gần nhất
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        ngayBatDau_DataChoose.setDate(cal.getTime());
        pnTieuChiLoc.add(ngayBatDau_DataChoose);

        pnTieuChiLoc.add(new JLabel("Đến:"));
        ngayKetThuc_DataChoose = new JDateChooser();
        ngayKetThuc_DataChoose.setDateFormatString("dd/MM/yyyy");
        ngayKetThuc_DataChoose.setPreferredSize(new Dimension(110, 30));
        ngayKetThuc_DataChoose.setDate(new Date()); 
        pnTieuChiLoc.add(ngayKetThuc_DataChoose);

        pnTieuChiLoc.add(new JLabel("Loại SP:"));
        cmbLoaiSP = new JComboBox<>();
        cmbLoaiSP.addItem("Tất cả");
        for (LoaiSanPham loai : LoaiSanPham.values()) {
            cmbLoaiSP.addItem(loai.getTenLoai()); 
        }
        cmbLoaiSP.setPreferredSize(new Dimension(130, 30));
        pnTieuChiLoc.add(cmbLoaiSP);

        pnTieuChiLoc.add(new JLabel("Khuyến mãi:"));
        cmbKhuyenMai = new JComboBox<>();
        loadDuLieuKhuyenMaiVaoComboBox();
        cmbKhuyenMai.setPreferredSize(new Dimension(150, 30));
        pnTieuChiLoc.add(cmbKhuyenMai);

        JButton btnLoc = new JButton("Lọc");
        btnLoc.setBackground(new Color(0x005a9e));
        btnLoc.setForeground(Color.WHITE);
        pnTieuChiLoc.add(btnLoc);
        
        pnMain.add(pnTieuChiLoc, BorderLayout.NORTH);

        JPanel pnContent = new JPanel(new BorderLayout(0, 10));
        pnContent.setBackground(Color.WHITE);
        
        // --- Biểu đồ ---
        JPanel pnBieuDo = new JPanel(new BorderLayout());
        pnBieuDo.setBorder(BorderFactory.createTitledBorder("Biểu đồ doanh thu"));
        pnBieuDo.setBackground(Color.WHITE);
        bieuDoDoanhThu = new BieuDoCotJFreeChart();
        bieuDoDoanhThu.setTieuDeTrucX("Thời gian");
        bieuDoDoanhThu.setTieuDeTrucY("Doanh thu (VNĐ)");
        pnBieuDo.add(bieuDoDoanhThu, BorderLayout.CENTER);
        
        // --- Panel Tổng quan (Sửa lại GridLayout 2 hàng, 3 cột) ---
        JPanel pnThongKe = new JPanel(new GridLayout(2, 3, 20, 15)); // 2 rows, 3 cols, gap
        pnThongKe.setBackground(new Color(0xE3F2F5));
        pnThongKe.setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Tổng quan"), new EmptyBorder(10, 20, 10, 20)));
        pnThongKe.setPreferredSize(new Dimension(0, 200)); // Tăng chiều cao xíu
        
        Font fTitle = new Font("Tahoma", Font.PLAIN, 15); 
        Font fValue = new Font("Tahoma", Font.BOLD, 18); 
        Color cMain = new Color(0x005a9e);

        lblGiaTriTongDoanhThu = createLabel(pnThongKe, "Tổng doanh thu:", fTitle, fValue, cMain);
        lblGiaTriCaoNhat = createLabel(pnThongKe, "Cao nhất:", fTitle, fValue, new Color(0x28a745)); // Xanh lá
        lblGiaTriThapNhat = createLabel(pnThongKe, "Thấp nhất:", fTitle, fValue, new Color(0xdc3545)); // Đỏ
        
        lblGiaTriTrungBinh = createLabel(pnThongKe, "Trung bình/ngày:", fTitle, fValue, cMain);
        lblGiaTriTongGiaoDich = createLabel(pnThongKe, "Tổng đơn hàng:", fTitle, fValue, cMain);
        lblGiaTriTangTruong = createLabel(pnThongKe, "So với kỳ trước:", fTitle, fValue, Color.GRAY);

        pnContent.add(pnBieuDo, BorderLayout.CENTER);
        pnContent.add(pnThongKe, BorderLayout.SOUTH);
        pnMain.add(pnContent, BorderLayout.CENTER);

        btnLoc.addActionListener(e -> loadDuLieuTuDatabase());
        loadDuLieuTuDatabase();
    }
    
    private void loadDuLieuKhuyenMaiVaoComboBox() {
        cmbKhuyenMai.addItem("Tất cả");
        List<String[]> listKM = thongKeDAO.getDanhSachKhuyenMai();
        for (String[] km : listKM) {
            cmbKhuyenMai.addItem(km[0]); 
        }
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

    private void loadDuLieuTuDatabase() {
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
                if (loai.getTenLoai().equals(tenLoaiHienThi)) {
                    maLoaiSP = loai.name(); break;
                }
            }
        }
        String maKM = (String) cmbKhuyenMai.getSelectedItem();

        // 1. Lấy dữ liệu chính
        List<BanGhiThongKe> ds = thongKeDAO.getDoanhThuTheoNgay(tu, den, maLoaiSP, maKM);
        
        bieuDoDoanhThu.xoaToanBoDuLieu();
        bieuDoDoanhThu.setTieuDeBieuDo("Thống Kê Từ " + formatDate(tu) + " Đến " + formatDate(den));
        Color col = new Color(79, 129, 189);
        
        double tong = 0;
        double max = 0;
        double min = Double.MAX_VALUE; // Khởi tạo min
        int don = 0; 
        String ngayMax = "";
        String ngayMin = "";
        
        for (BanGhiThongKe item : ds) {
            bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot(item.thoiGian, "Doanh thu", item.doanhThu, col));
            tong += item.doanhThu;
            don += item.soLuongDon;
            
            if (item.doanhThu > max) { max = item.doanhThu; ngayMax = item.thoiGian; }
            if (item.doanhThu < min) { min = item.doanhThu; ngayMin = item.thoiGian; }
        }
        if (!ds.isEmpty()) {
            double trungBinh = tong / ds.size(); // Trung bình trên số ngày có dữ liệu hoặc số ngày chọn
            // Nếu muốn chính xác theo khoảng thời gian chọn (kể cả ngày ko có doanh thu):
            // long soNgay = (den.getTime() - tu.getTime()) / (24 * 60 * 60 * 1000) + 1;
            // double trungBinh = tong / soNgay;
            
            if (trungBinh > 0) {
                bieuDoDoanhThu.themDuongTrungBinh(trungBinh);
            }
        }
        if (ds.isEmpty()) min = 0; // Xử lý nếu không có dữ liệu

        NumberFormat vn = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        
        // Update Labels cơ bản
        lblGiaTriTongDoanhThu.setText(vn.format(tong));
        lblGiaTriTongGiaoDich.setText(String.valueOf(don));
        lblGiaTriTrungBinh.setText(vn.format(ds.isEmpty() ? 0 : tong / ds.size()));
        
        lblGiaTriCaoNhat.setText(max > 0 ? "<html>" + vn.format(max) + "<br><span style='font-size:10px; color:gray'>(" + ngayMax + ")</span></html>" : "0 VNĐ");
        lblGiaTriThapNhat.setText(min > 0 ? "<html>" + vn.format(min) + "<br><span style='font-size:10px; color:gray'>(" + ngayMin + ")</span></html>" : (ds.isEmpty() ? "0 VNĐ" : vn.format(min)));

        // 2. Tính Tăng trưởng so với kỳ trước
        // Kỳ trước = (Tu - duration) đến (Tu - 1 ngày)
        long duration = den.getTime() - tu.getTime(); // Milliseconds
        Date prevDen = new Date(tu.getTime() - 24L * 60 * 60 * 1000); // Trước ngày bắt đầu 1 ngày
        Date prevTu = new Date(prevDen.getTime() - duration);
        
        double tongKyTruoc = thongKeDAO.getTongDoanhThuTrongKhoang(prevTu, prevDen, maLoaiSP, maKM);
        
        if (tongKyTruoc == 0) {
            lblGiaTriTangTruong.setText("---");
            lblGiaTriTangTruong.setForeground(Color.GRAY);
        } else {
            double phanTram = ((tong - tongKyTruoc) / tongKyTruoc) * 100;
            String icon = phanTram >= 0 ? "▲" : "▼";
            Color color = phanTram >= 0 ? new Color(0x28a745) : new Color(0xdc3545);
            lblGiaTriTangTruong.setText(String.format("%s %.1f%%", icon, Math.abs(phanTram)));
            lblGiaTriTangTruong.setForeground(color);
            lblGiaTriTangTruong.setToolTipText("Kỳ trước: " + vn.format(tongKyTruoc));
        }
    }
    private String formatDate(Date d) { return new java.text.SimpleDateFormat("dd/MM/yyyy").format(d); }
}