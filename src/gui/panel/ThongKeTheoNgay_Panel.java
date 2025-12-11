package gui.panel;

import java.awt.*;
import java.text.NumberFormat;
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
    
    private JLabel lblGiaTriTongDoanhThu, lblGiaTriCaoNhat, lblGiaTriTongGiaoDich, lblGiaTriTrungBinh;
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

        // 1. Ngày bắt đầu
        pnTieuChiLoc.add(new JLabel("Từ:"));
        ngayBatDau_DataChoose = new JDateChooser();
        ngayBatDau_DataChoose.setDateFormatString("dd/MM/yyyy");
        ngayBatDau_DataChoose.setPreferredSize(new Dimension(110, 30));
        ngayBatDau_DataChoose.setDate(new Date(System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000));
        pnTieuChiLoc.add(ngayBatDau_DataChoose);

        // 2. Ngày kết thúc
        pnTieuChiLoc.add(new JLabel("Đến:"));
        ngayKetThuc_DataChoose = new JDateChooser();
        ngayKetThuc_DataChoose.setDateFormatString("dd/MM/yyyy");
        ngayKetThuc_DataChoose.setPreferredSize(new Dimension(110, 30));
        ngayKetThuc_DataChoose.setDate(new Date()); 
        pnTieuChiLoc.add(ngayKetThuc_DataChoose);

        // 3. Loại sản phẩm (Dùng Enum LoaiSanPham)
        pnTieuChiLoc.add(new JLabel("Loại SP:"));
        cmbLoaiSP = new JComboBox<>();
        cmbLoaiSP.addItem("Tất cả");
        for (LoaiSanPham loai : LoaiSanPham.values()) {
            // Lưu ý: Giá trị gửi xuống DB phải khớp với DB (THUOC, MY_PHAM...)
            // Ở đây ta hiển thị Tiếng Việt, nhưng khi gửi DAO sẽ cần xử lý hoặc lấy name()
            cmbLoaiSP.addItem(loai.getTenLoai()); 
        }
        cmbLoaiSP.setPreferredSize(new Dimension(130, 30));
        pnTieuChiLoc.add(cmbLoaiSP);

        // 4. Khuyến mãi (Load từ DB hoặc Mock)
        pnTieuChiLoc.add(new JLabel("Khuyến mãi:"));
        cmbKhuyenMai = new JComboBox<>();
        loadDuLieuKhuyenMaiVaoComboBox(); // Hàm load dữ liệu
        cmbKhuyenMai.setPreferredSize(new Dimension(150, 30));
        pnTieuChiLoc.add(cmbKhuyenMai);

        JButton btnLoc = new JButton("Lọc");
        btnLoc.setBackground(new Color(0x005a9e));
        btnLoc.setForeground(Color.WHITE);
        pnTieuChiLoc.add(btnLoc);
        
        pnMain.add(pnTieuChiLoc, BorderLayout.NORTH);

        // ... (Phần UI biểu đồ giữ nguyên)
        JPanel pnContent = new JPanel(new BorderLayout(0, 10));
        pnContent.setBackground(Color.WHITE);
        JPanel pnBieuDo = new JPanel(new BorderLayout());
        pnBieuDo.setBorder(BorderFactory.createTitledBorder("Biểu đồ doanh thu"));
        pnBieuDo.setBackground(Color.WHITE);
        bieuDoDoanhThu = new BieuDoCotJFreeChart();
        bieuDoDoanhThu.setTieuDeTrucX("Thời gian");
        bieuDoDoanhThu.setTieuDeTrucY("Doanh thu (VNĐ)");
        pnBieuDo.add(bieuDoDoanhThu, BorderLayout.CENTER);
        
        // ... (Phần UI thống kê giữ nguyên)
        JPanel pnThongKe = new JPanel(new GridLayout(2, 4, 20, 10));
        pnThongKe.setBackground(new Color(0xE3F2F5));
        pnThongKe.setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Tổng quan"), new EmptyBorder(10, 20, 10, 20)));
        pnThongKe.setPreferredSize(new Dimension(0, 140));
        Font f1 = new Font("Tahoma", Font.PLAIN, 16); Font f2 = new Font("Tahoma", Font.BOLD, 18); Color c = new Color(0x005a9e);
        lblGiaTriTongDoanhThu = createLabel(pnThongKe, "Tổng doanh thu:", f1, f2, c);
        lblGiaTriCaoNhat = createLabel(pnThongKe, "Cao nhất:", f1, f2, c);
        lblGiaTriTongGiaoDich = createLabel(pnThongKe, "Tổng đơn hàng:", f1, f2, c);
        lblGiaTriTrungBinh = createLabel(pnThongKe, "Trung bình/ngày:", f1, f2, c);

        pnContent.add(pnBieuDo, BorderLayout.CENTER);
        pnContent.add(pnThongKe, BorderLayout.SOUTH);
        pnMain.add(pnContent, BorderLayout.CENTER);

        // Event
        btnLoc.addActionListener(e -> loadDuLieuTuDatabase());
        loadDuLieuTuDatabase();
    }
    
    private void loadDuLieuKhuyenMaiVaoComboBox() {
        cmbKhuyenMai.addItem("Tất cả");
        // Lấy danh sách từ DAO
        List<String[]> listKM = thongKeDAO.getDanhSachKhuyenMai();
        for (String[] km : listKM) {
            // Item hiển thị: "Tên KM", giá trị ẩn cần xử lý khi lấy selected item
            // Để đơn giản, ta hiển thị Mã KM hoặc Tên KM
            cmbKhuyenMai.addItem(km[0]); // Lưu Mã KM để dễ query
        }
    }

    private JLabel createLabel(JPanel p, String t, Font f1, Font f2, Color c) {
        p.add(new JLabel(t)).setFont(f1); JLabel l = new JLabel("0"); l.setFont(f2); l.setForeground(c); p.add(l); return l;
    }

    private void loadDuLieuTuDatabase() {
        Date tu = ngayBatDau_DataChoose.getDate();
        Date den = ngayKetThuc_DataChoose.getDate();
        if (tu == null || den == null) return;

        // Xử lý Loại SP: Từ "Thuốc" (hiển thị) -> "THUOC" (DB)
        String tenLoaiHienThi = (String) cmbLoaiSP.getSelectedItem();
        String maLoaiSP = "Tất cả";
        if (!"Tất cả".equals(tenLoaiHienThi)) {
            for (LoaiSanPham loai : LoaiSanPham.values()) {
                if (loai.getTenLoai().equals(tenLoaiHienThi)) {
                    maLoaiSP = loai.name(); // Lấy tên Enum (THUOC, MY_PHAM...)
                    break;
                }
            }
        }
        
        String maKM = (String) cmbKhuyenMai.getSelectedItem();

        List<BanGhiThongKe> ds = thongKeDAO.getDoanhThuTheoNgay(tu, den, maLoaiSP, maKM);
        
        bieuDoDoanhThu.xoaToanBoDuLieu();
        bieuDoDoanhThu.setTieuDeBieuDo("Thống Kê Từ " + formatDate(tu) + " Đến " + formatDate(den));
        Color col = new Color(79, 129, 189);
        double tong = 0, max = 0; int don = 0; String ngayMax = "";
        
        for (BanGhiThongKe item : ds) {
            bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot(item.thoiGian, "Doanh thu", item.doanhThu, col));
            tong += item.doanhThu; don += item.soLuongDon;
            if (item.doanhThu > max) { max = item.doanhThu; ngayMax = item.thoiGian; }
        }
        
        NumberFormat vn = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        lblGiaTriTongDoanhThu.setText(vn.format(tong));
        lblGiaTriTongGiaoDich.setText(String.valueOf(don));
        lblGiaTriCaoNhat.setText(max > 0 ? "<html>" + vn.format(max) + "<br><span style='font-size:10px'>(" + ngayMax + ")</span></html>" : "0 VNĐ");
        lblGiaTriTrungBinh.setText(vn.format(ds.isEmpty() ? 0 : tong / ds.size()));
    }
    private String formatDate(Date d) { return new java.text.SimpleDateFormat("dd/MM/yyyy").format(d); }
}