package gui.panel;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

import com.toedter.calendar.JDateChooser;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import component.button.PillButton;
import component.chart.BieuDoCotJFreeChart;
import component.chart.DuLieuBieuDoCot;
import dao.ThongKe_DAO;
import entity.Session;

/**
 * Panel thống kê Top sản phẩm bán chạy
 * Hiển thị biểu đồ cột + bảng chi tiết top N sản phẩm
 * Bao gồm: Insight cards, % đóng góp, xu hướng
 */
public class TopSanPhamBanChay_Panel extends JPanel {

    private JDateChooser ngayBatDau;
    private JDateChooser ngayKetThuc;
    private JComboBox<Integer> cmbSoLuong;
    private BieuDoCotJFreeChart bieuDoTop;
    private JTable tblTopSanPham;
    private DefaultTableModel tableModel;

    // DAO
    private ThongKe_DAO thongKeDAO;

    // Insight cards labels
    private JLabel lblTongDoanhThu;
    private JLabel lblTopContribution;
    private JLabel lblBestSeller;
    private JLabel lblTrend;

    // Formatters
    private final DecimalFormat dfMoney = new DecimalFormat("#,### VNĐ");
    private final DecimalFormat dfPercent = new DecimalFormat("0.0%");
    private final DecimalFormat dfNumber = new DecimalFormat("#,###");

    public TopSanPhamBanChay_Panel() {
        thongKeDAO = new ThongKe_DAO();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel pnMain = new JPanel(new BorderLayout(0, 10));
        pnMain.setBackground(Color.WHITE);
        pnMain.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(pnMain, BorderLayout.CENTER);

        // ===== PANEL BỘ LỌC =====
        JPanel pnTieuChiLoc = new JPanel();
        pnTieuChiLoc.setBackground(new Color(0xE3F2F5));
        pnTieuChiLoc.setBorder(BorderFactory.createTitledBorder("Tiêu chí lọc"));
        pnTieuChiLoc.setPreferredSize(new Dimension(0, 100));
        pnTieuChiLoc.setLayout(null);

        JLabel lblTuNgay = new JLabel("Từ ngày");
        lblTuNgay.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblTuNgay.setBounds(20, 25, 80, 20);
        pnTieuChiLoc.add(lblTuNgay);

        ngayBatDau = new JDateChooser();
        ngayBatDau.setDateFormatString("dd-MM-yyyy");
        ngayBatDau.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ngayBatDau.setBounds(20, 50, 150, 30);
        // Mặc định: đầu tháng hiện tại
        LocalDate dauThang = LocalDate.now().withDayOfMonth(1);
        ngayBatDau.setDate(Date.from(dauThang.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        pnTieuChiLoc.add(ngayBatDau);

        JLabel lblDenNgay = new JLabel("Đến ngày");
        lblDenNgay.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblDenNgay.setBounds(200, 25, 80, 20);
        pnTieuChiLoc.add(lblDenNgay);

        ngayKetThuc = new JDateChooser();
        ngayKetThuc.setDateFormatString("dd-MM-yyyy");
        ngayKetThuc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ngayKetThuc.setBounds(200, 50, 150, 30);
        // Mặc định: hôm nay
        ngayKetThuc.setDate(new Date());
        pnTieuChiLoc.add(ngayKetThuc);

        JLabel lblSoLuong = new JLabel("Số lượng Top");
        lblSoLuong.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblSoLuong.setBounds(380, 25, 100, 20);
        pnTieuChiLoc.add(lblSoLuong);

        Integer[] topOptions = { 5, 10, 15, 20 };
        cmbSoLuong = new JComboBox<>(topOptions);
        cmbSoLuong.setSelectedItem(10);
        cmbSoLuong.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbSoLuong.setBounds(380, 50, 100, 30);
        pnTieuChiLoc.add(cmbSoLuong);

        JButton btnThongKe = new PillButton("Thống Kê");
        btnThongKe.setBounds(520, 45, 120, 35);
        btnThongKe.addActionListener(e -> loadDuLieuThongKe());
        pnTieuChiLoc.add(btnThongKe);

        JButton btnXuatExcel = new PillButton("Xuất Excel");
        btnXuatExcel.setBounds(660, 45, 120, 35);
        btnXuatExcel.addActionListener(e -> xuatExcel());
        pnTieuChiLoc.add(btnXuatExcel);

        pnMain.add(pnTieuChiLoc, BorderLayout.NORTH);

        // ===== INSIGHT CARDS =====
        JPanel pnInsights = createInsightCardsPanel();

        // ===== PANEL CHỨA BIỂU ĐỒ VÀ BẢNG =====
        JPanel pnContent = new JPanel(new BorderLayout(0, 10));
        pnContent.setBackground(Color.WHITE);

        // Panel biểu đồ
        JPanel pnBieuDo = new JPanel(new BorderLayout());
        pnBieuDo.setBorder(BorderFactory.createTitledBorder("Biểu đồ Top sản phẩm bán chạy"));
        pnBieuDo.setBackground(Color.WHITE);
        pnBieuDo.setPreferredSize(new Dimension(0, 300));

        bieuDoTop = new BieuDoCotJFreeChart();
        bieuDoTop.setTieuDeBieuDo("Top Sản Phẩm Bán Chạy");
        bieuDoTop.setTieuDeTrucX("Sản phẩm");
        bieuDoTop.setTieuDeTrucY("Số lượng bán");
        bieuDoTop.setBuocNhayTrucY(50);
        pnBieuDo.add(bieuDoTop, BorderLayout.CENTER);

        // Panel bảng với cột mới
        JPanel pnBang = new JPanel(new BorderLayout());
        pnBang.setBorder(BorderFactory.createTitledBorder("Chi tiết Top sản phẩm"));
        pnBang.setBackground(Color.WHITE);

        // Thêm cột % Đóng góp và Xu hướng
        String[] columnNames = { "STT", "Mã SP", "Tên sản phẩm", "Loại", "SL bán", "Doanh thu", "% Đóng góp",
                "Xu hướng" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblTopSanPham = new JTable(tableModel);
        tblTopSanPham.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblTopSanPham.setRowHeight(30);
        tblTopSanPham.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblTopSanPham.getTableHeader().setBackground(new Color(0x0077B6));
        tblTopSanPham.getTableHeader().setForeground(Color.WHITE);

        // Căn giữa các cột số
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblTopSanPham.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblTopSanPham.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        tblTopSanPham.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        // Căn phải cột doanh thu
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblTopSanPham.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

        // Custom renderer cho cột xu hướng
        tblTopSanPham.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);

                String trend = value.toString();
                if (trend.contains("↑")) {
                    setForeground(new Color(0x28A745)); // Xanh lá
                } else if (trend.contains("↓")) {
                    setForeground(new Color(0xDC3545)); // Đỏ
                } else {
                    setForeground(new Color(0x6C757D)); // Xám
                }

                if (!isSelected) {
                    setBackground(Color.WHITE);
                }
                return c;
            }
        });

        // Độ rộng cột
        tblTopSanPham.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblTopSanPham.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblTopSanPham.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblTopSanPham.getColumnModel().getColumn(3).setPreferredWidth(120);
        tblTopSanPham.getColumnModel().getColumn(4).setPreferredWidth(70);
        tblTopSanPham.getColumnModel().getColumn(5).setPreferredWidth(120);
        tblTopSanPham.getColumnModel().getColumn(6).setPreferredWidth(80);
        tblTopSanPham.getColumnModel().getColumn(7).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(tblTopSanPham);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        pnBang.add(scrollPane, BorderLayout.CENTER);

        // Thêm insight cards vào content
        JPanel pnTopSection = new JPanel(new BorderLayout(0, 10));
        pnTopSection.setBackground(Color.WHITE);
        pnTopSection.add(pnInsights, BorderLayout.NORTH);
        pnTopSection.add(pnBieuDo, BorderLayout.CENTER);

        pnContent.add(pnTopSection, BorderLayout.CENTER);
        pnContent.add(pnBang, BorderLayout.SOUTH);
        pnBang.setPreferredSize(new Dimension(0, 220));

        pnMain.add(pnContent, BorderLayout.CENTER);

        // Load dữ liệu khi khởi tạo
        loadDuLieuThongKe();
    }

    /**
     * Tạo panel chứa các Insight Cards
     */
    private JPanel createInsightCardsPanel() {
        JPanel pnInsights = new JPanel(new GridLayout(1, 4, 15, 0));
        pnInsights.setBackground(Color.WHITE);
        pnInsights.setBorder(new EmptyBorder(0, 0, 10, 0));
        pnInsights.setPreferredSize(new Dimension(0, 80));

        // Card 1: Tổng doanh thu
        JPanel card1 = createInsightCard("TỔNG DOANH THU", "0 VNĐ", new Color(0x0077B6));
        lblTongDoanhThu = (JLabel) ((JPanel) card1.getComponent(0)).getComponent(1);

        // Card 2: Top 10 đóng góp
        JPanel card2 = createInsightCard("TOP 10 CHIẾM", "-- doanh thu", new Color(0x00B4D8));
        lblTopContribution = (JLabel) ((JPanel) card2.getComponent(0)).getComponent(1);

        // Card 3: SP bán chạy nhất
        JPanel card3 = createInsightCard("BÁN CHẠY #1", "Chưa có dữ liệu", new Color(0x48CAE4));
        lblBestSeller = (JLabel) ((JPanel) card3.getComponent(0)).getComponent(1);

        // Card 4: Xu hướng
        JPanel card4 = createInsightCard("XU HƯỚNG", "-- vs kỳ trước", new Color(0x28A745));
        lblTrend = (JLabel) ((JPanel) card4.getComponent(0)).getComponent(1);

        pnInsights.add(card1);
        pnInsights.add(card2);
        pnInsights.add(card3);
        pnInsights.add(card4);

        return pnInsights;
    }

    /**
     * Tạo một Insight Card
     */
    private JPanel createInsightCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                new EmptyBorder(10, 15, 10, 15)));
        card.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE0E0E0), 1),
                card.getBorder()));

        JPanel content = new JPanel(new GridLayout(2, 1, 0, 5));
        content.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Tahoma", Font.PLAIN, 11));
        lblTitle.setForeground(new Color(0x6C757D));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblValue.setForeground(accentColor);

        content.add(lblTitle);
        content.add(lblValue);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    /**
     * Load dữ liệu thống kê từ database
     */
    private void loadDuLieuThongKe() {
        // Xóa dữ liệu cũ
        bieuDoTop.xoaToanBoDuLieu();
        tableModel.setRowCount(0);

        // Lấy tham số từ bộ lọc
        LocalDate tuNgay = getLocalDateFromChooser(ngayBatDau);
        LocalDate denNgay = getLocalDateFromChooser(ngayKetThuc);
        int topN = (Integer) cmbSoLuong.getSelectedItem();

        if (tuNgay == null || denNgay == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khoảng thời gian!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (tuNgay.isAfter(denNgay)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu phải trước ngày kết thúc!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy dữ liệu từ DAO
        List<Object[]> danhSach = thongKeDAO.layTopSanPhamBanChay(tuNgay, denNgay, topN);
        double tongDoanhThuToanBo = thongKeDAO.tinhTongDoanhThuTheoKhoangNgay(tuNgay, denNgay);

        // Tính kỳ trước (cùng khoảng thời gian)
        long soNgay = java.time.temporal.ChronoUnit.DAYS.between(tuNgay, denNgay) + 1;
        LocalDate tuNgayKyTruoc = tuNgay.minusDays(soNgay);
        LocalDate denNgayKyTruoc = tuNgay.minusDays(1);
        double doanhThuKyTruoc = thongKeDAO.tinhTongDoanhThuKyTruoc(tuNgayKyTruoc, denNgayKyTruoc);

        // Màu sắc cho biểu đồ
        Color[] colors = {
                new Color(255, 99, 132), new Color(54, 162, 235), new Color(255, 206, 86),
                new Color(75, 192, 192), new Color(153, 102, 255), new Color(255, 159, 64),
                new Color(199, 199, 199), new Color(83, 102, 255), new Color(255, 99, 255),
                new Color(99, 255, 132), new Color(255, 128, 0), new Color(128, 0, 255),
                new Color(0, 255, 128), new Color(255, 0, 128), new Color(128, 255, 0),
                new Color(0, 128, 255), new Color(255, 64, 64), new Color(64, 255, 64),
                new Color(64, 64, 255), new Color(255, 255, 64)
        };

        String tenNhom = "Số lượng";
        double tongDoanhThuTop = 0;
        String bestSeller = "Chưa có dữ liệu";

        for (int i = 0; i < danhSach.size(); i++) {
            Object[] row = danhSach.get(i);
            String maSP = (String) row[0];
            String tenSP = (String) row[1];
            String loai = (String) row[2];
            double soLuong = (double) row[3];
            double doanhThu = (double) row[4];

            tongDoanhThuTop += doanhThu;

            // Tính % đóng góp
            double phanTram = tongDoanhThuToanBo > 0 ? doanhThu / tongDoanhThuToanBo : 0;

            // Tính xu hướng so với kỳ trước
            double soLuongKyTruoc = thongKeDAO.laySoLuongBanKyTruoc(maSP, tuNgayKyTruoc, denNgayKyTruoc);
            String trend;
            if (soLuongKyTruoc == 0) {
                if (soLuong > 0) {
                    trend = "↑ Mới";
                } else {
                    trend = "→ 0%";
                }
            } else {
                double phanTramThayDoi = ((soLuong - soLuongKyTruoc) / soLuongKyTruoc) * 100;
                if (phanTramThayDoi > 0) {
                    trend = String.format("↑ +%.0f%%", phanTramThayDoi);
                } else if (phanTramThayDoi < 0) {
                    trend = String.format("↓ %.0f%%", phanTramThayDoi);
                } else {
                    trend = "→ 0%";
                }
            }

            // Lưu best seller
            if (i == 0) {
                bestSeller = tenSP;
            }

            // Thêm vào biểu đồ
            String tenRutGon = tenSP.length() > 15 ? tenSP.substring(0, 12) + "..." : tenSP;
            bieuDoTop.themDuLieu(new DuLieuBieuDoCot(tenRutGon, tenNhom, (int) soLuong, colors[i % colors.length]));

            // Thêm vào bảng
            tableModel.addRow(new Object[] {
                    i + 1,
                    maSP,
                    tenSP,
                    loai,
                    dfNumber.format(soLuong),
                    dfMoney.format(doanhThu),
                    dfPercent.format(phanTram),
                    trend
            });
        }

        // Cập nhật insight cards
        lblTongDoanhThu.setText(dfMoney.format(tongDoanhThuToanBo));

        // % đóng góp của top N
        double tyLeTop = tongDoanhThuToanBo > 0 ? tongDoanhThuTop / tongDoanhThuToanBo : 0;
        lblTopContribution.setText(dfPercent.format(tyLeTop) + " doanh thu");

        lblBestSeller.setText(bestSeller);

        // Xu hướng tổng thể
        if (doanhThuKyTruoc > 0) {
            double thayDoiPhanTram = ((tongDoanhThuToanBo - doanhThuKyTruoc) / doanhThuKyTruoc) * 100;
            if (thayDoiPhanTram > 0) {
                lblTrend.setText(String.format("↑ +%.1f%% vs kỳ trước", thayDoiPhanTram));
                lblTrend.setForeground(new Color(0x28A745));
            } else if (thayDoiPhanTram < 0) {
                lblTrend.setText(String.format("↓ %.1f%% vs kỳ trước", thayDoiPhanTram));
                lblTrend.setForeground(new Color(0xDC3545));
            } else {
                lblTrend.setText("→ 0% vs kỳ trước");
                lblTrend.setForeground(new Color(0x6C757D));
            }
        } else {
            lblTrend.setText("-- vs kỳ trước");
            lblTrend.setForeground(new Color(0x6C757D));
        }

        // Cập nhật tiêu đề biểu đồ
        bieuDoTop.setTieuDeBieuDo("Top " + topN + " Sản Phẩm Bán Chạy");

        // Thông báo nếu không có dữ liệu
        if (danhSach.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu trong khoảng thời gian đã chọn!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Chuyển đổi JDateChooser sang LocalDate
     */
    private LocalDate getLocalDateFromChooser(JDateChooser dateChooser) {
        Date date = dateChooser.getDate();
        if (date == null)
            return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Xuất dữ liệu ra Excel
     */
    private void xuatExcel() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
            fileChooser.setSelectedFile(new File("TopSanPhamBanChay.xlsx"));
            fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().endsWith(".xlsx")) {
                    file = new File(file.getAbsolutePath() + ".xlsx");
                }

                XSSFWorkbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Top Sản Phẩm Bán Chạy");

                // Header style
                CellStyle headerStyle = workbook.createCellStyle();
                XSSFFont headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                // Tiêu đề
                Row titleRow = sheet.createRow(0);
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue("THỐNG KÊ TOP SẢN PHẨM BÁN CHẠY");

                CellStyle titleStyle = workbook.createCellStyle();
                XSSFFont titleFont = workbook.createFont();
                titleFont.setBold(true);
                titleFont.setFontHeightInPoints((short) 16);
                titleStyle.setFont(titleFont);
                titleCell.setCellStyle(titleStyle);

                // Thông tin kỳ thống kê
                Row periodRow = sheet.createRow(1);
                LocalDate tuNgay = getLocalDateFromChooser(ngayBatDau);
                LocalDate denNgay = getLocalDateFromChooser(ngayKetThuc);
                periodRow.createCell(0).setCellValue("Kỳ thống kê: " +
                        tuNgay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " - " +
                        denNgay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                // Header row
                Row headerRow = sheet.createRow(3);
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(tableModel.getColumnName(i));
                    cell.setCellStyle(headerStyle);
                }

                // Data rows
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    Row dataRow = sheet.createRow(row + 4);
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        Object value = tableModel.getValueAt(row, col);
                        dataRow.createCell(col).setCellValue(value != null ? value.toString() : "");
                    }
                }

                // Auto-size columns
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    sheet.autoSizeColumn(i);
                }

                // Write file
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                }
                workbook.close();

                JOptionPane.showMessageDialog(this,
                        "Xuất Excel thành công!\nFile: " + file.getAbsolutePath(),
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);

                // Mở file
                Desktop.getDesktop().open(file);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xuất Excel: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
