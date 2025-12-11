package gui.panel;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.time.Year;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import component.button.PillButton;
import component.chart.BieuDoTronJFreeChart;
import component.chart.BieuDoTronLegend;
import component.chart.DuLieuBieuDoTron;
import dao.ThongKe_DAO;

/**
 * Panel thống kê sản phẩm theo loại
 * Hiển thị biểu đồ tròn phân bố doanh thu theo từng loại sản phẩm
 * Bao gồm: So sánh kỳ trước, lợi nhuận theo loại
 */
public class ThongKeTheoLoai_Panel extends JPanel {

    private BieuDoTronLegend bieuDoTron;
    private JTable tblChiTiet;
    private DefaultTableModel tableModel;
    private JComboBox<Integer> cmbNam;

    // DAO
    private ThongKe_DAO thongKeDAO = new ThongKe_DAO();

    // Formatters
    private DecimalFormat dfMoney = new DecimalFormat("#,###");
    private DecimalFormat dfPercent = new DecimalFormat("0.0%");

    // Insight cards
    private JLabel lblTongDoanhThu;
    private JLabel lblLoiNhuan;
    private JLabel lblLoaiTotNhat;
    private JLabel lblXuHuong;

    // Summary labels
    private JLabel lblSumDoanhThu;
    private JLabel lblSumChiPhi;
    private JLabel lblSumLoiNhuan;
    private JLabel lblSumTyLeLN;
    private JLabel lblSumSoLuongSP;
    private JLabel lblSumLoaiCaoNhat;
    private JLabel lblSumLoaiThapNhat;
    private JLabel lblSumSoSanh;

    // Màu sắc cho biểu đồ
    private Color[] chartColors = {
            new Color(0x0077B6), // Blue
            new Color(0x00B4D8), // Light Blue
            new Color(0x90E0EF), // Cyan
            new Color(0xCAF0F8), // Light Cyan
            new Color(0xFD7E14), // Orange
            new Color(0x28A745), // Green
            new Color(0xDC3545), // Red
            new Color(0x6C757D) // Gray
    };

    public ThongKeTheoLoai_Panel() {
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
        pnTieuChiLoc.setPreferredSize(new Dimension(0, 80));
        pnTieuChiLoc.setLayout(null);

        JLabel lblNam = new JLabel("Chọn năm:");
        lblNam.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNam.setBounds(20, 30, 80, 25);
        pnTieuChiLoc.add(lblNam);

        int currentYear = Year.now().getValue();
        Integer[] years = new Integer[5];
        for (int i = 0; i < 5; i++) {
            years[i] = currentYear - i;
        }
        cmbNam = new JComboBox<>(years);
        cmbNam.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbNam.setBounds(100, 28, 100, 30);
        pnTieuChiLoc.add(cmbNam);

        JButton btnThongKe = new PillButton("📊 Thống Kê");
        btnThongKe.setBounds(230, 25, 120, 35);
        btnThongKe.addActionListener(e -> loadDuLieu());
        pnTieuChiLoc.add(btnThongKe);

        JButton btnXuatExcel = new PillButton("📥 Xuất Excel");
        btnXuatExcel.setBounds(370, 25, 120, 35);
        btnXuatExcel.addActionListener(e -> xuatExcel());
        pnTieuChiLoc.add(btnXuatExcel);

        pnMain.add(pnTieuChiLoc, BorderLayout.NORTH);

        // ===== INSIGHT CARDS =====
        JPanel pnInsights = createInsightCardsPanel();

        // ===== PANEL NỘI DUNG CHÍNH =====
        JPanel pnContent = new JPanel(new GridLayout(1, 2, 20, 0));
        pnContent.setBackground(Color.WHITE);

        // Panel biểu đồ tròn (bên trái)
        JPanel pnBieuDo = new JPanel(new BorderLayout());
        pnBieuDo.setBorder(BorderFactory.createTitledBorder("Phân bổ doanh thu theo loại sản phẩm"));
        pnBieuDo.setBackground(Color.WHITE);

        bieuDoTron = new BieuDoTronLegend();
        pnBieuDo.add(bieuDoTron, BorderLayout.CENTER);

        // Panel chi tiết (bên phải)
        JPanel pnChiTiet = new JPanel(new BorderLayout(0, 10));
        pnChiTiet.setBorder(BorderFactory.createTitledBorder("Chi tiết theo loại"));
        pnChiTiet.setBackground(Color.WHITE);

        // Bảng chi tiết với cột mới
        String[] columnNames = { "Loại sản phẩm", "SL SP", "Doanh thu", "Chi phí", "Lợi nhuận", "Tỷ lệ LN",
                "So với năm trước" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblChiTiet = new JTable(tableModel);
        tblChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblChiTiet.setRowHeight(35);
        tblChiTiet.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblChiTiet.getTableHeader().setBackground(new Color(0x0077B6));
        tblChiTiet.getTableHeader().setForeground(Color.WHITE);

        // Căn giữa/phải các cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblChiTiet.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        tblChiTiet.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblChiTiet.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        tblChiTiet.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        tblChiTiet.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        // Custom renderer cho cột so sánh năm trước
        tblChiTiet.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setFont(getFont().deriveFont(Font.BOLD));

                String trend = value != null ? value.toString() : "";
                if (trend.contains("↑")) {
                    setForeground(new Color(0x28A745));
                } else if (trend.contains("↓")) {
                    setForeground(new Color(0xDC3545));
                } else {
                    setForeground(new Color(0x6C757D));
                }

                if (!isSelected) {
                    setBackground(Color.WHITE);
                }
                return c;
            }
        });

        // Độ rộng cột
        tblChiTiet.getColumnModel().getColumn(0).setPreferredWidth(140);
        tblChiTiet.getColumnModel().getColumn(1).setPreferredWidth(50);
        tblChiTiet.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblChiTiet.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblChiTiet.getColumnModel().getColumn(4).setPreferredWidth(90);
        tblChiTiet.getColumnModel().getColumn(5).setPreferredWidth(60);
        tblChiTiet.getColumnModel().getColumn(6).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(tblChiTiet);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Panel tổng quan
        JPanel pnTongQuan = createSummaryPanel();

        pnChiTiet.add(scrollPane, BorderLayout.CENTER);
        pnChiTiet.add(pnTongQuan, BorderLayout.SOUTH);

        pnContent.add(pnBieuDo);
        pnContent.add(pnChiTiet);

        // Panel chứa insight + content
        JPanel pnMainContent = new JPanel(new BorderLayout(0, 10));
        pnMainContent.setBackground(Color.WHITE);
        pnMainContent.add(pnInsights, BorderLayout.NORTH);
        pnMainContent.add(pnContent, BorderLayout.CENTER);

        pnMain.add(pnMainContent, BorderLayout.CENTER);

        // Load dữ liệu thực từ database
        loadDuLieu();
    }

    /**
     * Tạo panel tổng quan (summary) ở dưới bảng
     */
    private JPanel createSummaryPanel() {
        JPanel pnTongQuan = new JPanel(new GridLayout(2, 4, 10, 10));
        pnTongQuan.setBackground(new Color(0xE3F2F5));
        pnTongQuan.setBorder(new EmptyBorder(15, 15, 15, 15));
        pnTongQuan.setPreferredSize(new Dimension(0, 100));

        Font labelFont = new Font("Tahoma", Font.PLAIN, 12);
        Font valueFont = new Font("Tahoma", Font.BOLD, 14);
        Color valueColor = new Color(0x0077B6);

        // Row 1
        JPanel item1 = createSummaryItem("💰 Tổng doanh thu:", "0 VNĐ", labelFont, valueFont, valueColor);
        lblSumDoanhThu = (JLabel) item1.getComponent(1);
        pnTongQuan.add(item1);

        JPanel item2 = createSummaryItem("💵 Tổng chi phí:", "0 VNĐ", labelFont, valueFont, new Color(0xDC3545));
        lblSumChiPhi = (JLabel) item2.getComponent(1);
        pnTongQuan.add(item2);

        JPanel item3 = createSummaryItem("📈 Tổng lợi nhuận:", "0 VNĐ", labelFont, valueFont, new Color(0x28A745));
        lblSumLoiNhuan = (JLabel) item3.getComponent(1);
        pnTongQuan.add(item3);

        JPanel item4 = createSummaryItem("📊 Tỷ lệ LN trung bình:", "0%", labelFont, valueFont, new Color(0x28A745));
        lblSumTyLeLN = (JLabel) item4.getComponent(1);
        pnTongQuan.add(item4);

        // Row 2
        JPanel item5 = createSummaryItem("📦 Tổng số sản phẩm:", "0 sản phẩm", labelFont, valueFont, valueColor);
        lblSumSoLuongSP = (JLabel) item5.getComponent(1);
        pnTongQuan.add(item5);

        JPanel item6 = createSummaryItem("🏆 Loại LN cao nhất:", "N/A", labelFont, valueFont, new Color(0x28A745));
        lblSumLoaiCaoNhat = (JLabel) item6.getComponent(1);
        pnTongQuan.add(item6);

        JPanel item7 = createSummaryItem("📉 Loại LN thấp nhất:", "N/A", labelFont, valueFont, new Color(0xDC3545));
        lblSumLoaiThapNhat = (JLabel) item7.getComponent(1);
        pnTongQuan.add(item7);

        JPanel item8 = createSummaryItem("🔄 So với năm trước:", "N/A", labelFont, valueFont, new Color(0x28A745));
        lblSumSoSanh = (JLabel) item8.getComponent(1);
        pnTongQuan.add(item8);

        return pnTongQuan;
    }

    /**
     * Tạo một summary item
     */
    private JPanel createSummaryItem(String label, String value, Font labelFont, Font valueFont, Color valueColor) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 2));
        panel.setBackground(new Color(0xE3F2F5));

        JLabel lblLabel = new JLabel(label, SwingConstants.LEFT);
        lblLabel.setFont(labelFont);
        lblLabel.setForeground(new Color(0x6C757D));

        JLabel lblValue = new JLabel(value, SwingConstants.LEFT);
        lblValue.setFont(valueFont);
        lblValue.setForeground(valueColor);

        panel.add(lblLabel);
        panel.add(lblValue);
        return panel;
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
        JPanel card1 = createInsightCard("💰 TỔNG DOANH THU", "0 VNĐ", new Color(0x0077B6));
        lblTongDoanhThu = (JLabel) ((JPanel) card1.getComponent(0)).getComponent(1);

        // Card 2: Lợi nhuận
        JPanel card2 = createInsightCard("📈 LỢI NHUẬN", "0 VNĐ (0%)", new Color(0x28A745));
        lblLoiNhuan = (JLabel) ((JPanel) card2.getComponent(0)).getComponent(1);

        // Card 3: Loại sinh lời nhất
        JPanel card3 = createInsightCard("🏆 LOẠI SINH LỜI NHẤT", "N/A", new Color(0xFD7E14));
        lblLoaiTotNhat = (JLabel) ((JPanel) card3.getComponent(0)).getComponent(1);

        // Card 4: So với năm trước
        JPanel card4 = createInsightCard("🔄 SO VỚI NĂM TRƯỚC", "N/A", new Color(0x28A745));
        lblXuHuong = (JLabel) ((JPanel) card4.getComponent(0)).getComponent(1);

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
                BorderFactory.createLineBorder(new Color(0xE0E0E0), 1),
                new CompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                        new EmptyBorder(10, 15, 10, 15))));

        JPanel content = new JPanel(new GridLayout(2, 1, 0, 5));
        content.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Tahoma", Font.PLAIN, 11));
        lblTitle.setForeground(new Color(0x6C757D));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblValue.setForeground(accentColor);

        content.add(lblTitle);
        content.add(lblValue);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    /**
     * Chuyển đổi enum LoaiSanPham thành tên hiển thị
     */
    private String chuyenEnumThanhTenHienThi(String enumName) {
        if (enumName == null)
            return "Không xác định";
        return switch (enumName) {
            case "THUOC" -> "Thuốc";
            case "THUC_PHAM_BO_SUNG" -> "Thực phẩm bổ sung";
            case "MY_PHAM" -> "Mỹ phẩm";
            case "DUNG_CU_Y_TE" -> "Dụng cụ y tế";
            case "SAN_PHAM_CHO_ME_VA_BE" -> "SP cho mẹ và bé";
            case "SAN_PHAM_KHAC" -> "Sản phẩm khác";
            default -> enumName;
        };
    }

    /**
     * Load dữ liệu từ database
     */
    private void loadDuLieu() {
        // Xóa dữ liệu cũ
        bieuDoTron.xoaDuLieu();
        tableModel.setRowCount(0);

        int nam = (Integer) cmbNam.getSelectedItem();

        // Lấy dữ liệu từ DAO
        List<Object[]> danhSach = thongKeDAO.layThongKeTheoLoaiSanPham(nam);
        Map<String, Double> doanhThuNamTruoc = thongKeDAO.layDoanhThuNamTruocTheoLoai(nam);
        double tongDoanhThuNamTruoc = thongKeDAO.tinhTongDoanhThuTheoNam(nam - 1);

        // Biến tính tổng
        double tongDoanhThu = 0;
        double tongChiPhi = 0;
        int tongSoLuongSP = 0;
        String loaiTotNhat = "";
        double tyLeTotNhat = 0;
        String loaiThapNhat = "";
        double tyLeThapNhat = Double.MAX_VALUE;

        // Duyệt qua dữ liệu để tính tổng và tìm loại tốt/thấp nhất
        for (Object[] row : danhSach) {
            double doanhThu = (double) row[2];
            double chiPhi = (double) row[3];
            int slSP = (int) row[1];

            tongDoanhThu += doanhThu;
            tongChiPhi += chiPhi;
            tongSoLuongSP += slSP;

            double tyLeLN = doanhThu > 0 ? (doanhThu - chiPhi) / doanhThu : 0;
            if (tyLeLN > tyLeTotNhat && doanhThu > 0) {
                tyLeTotNhat = tyLeLN;
                loaiTotNhat = chuyenEnumThanhTenHienThi((String) row[0]);
            }
            if (tyLeLN < tyLeThapNhat && doanhThu > 0) {
                tyLeThapNhat = tyLeLN;
                loaiThapNhat = chuyenEnumThanhTenHienThi((String) row[0]);
            }
        }

        double tongLoiNhuan = tongDoanhThu - tongChiPhi;
        double tyLeLNTB = tongDoanhThu > 0 ? tongLoiNhuan / tongDoanhThu : 0;

        // Thêm dữ liệu vào bảng và biểu đồ
        int colorIndex = 0;
        for (Object[] row : danhSach) {
            String loaiEnum = (String) row[0];
            String loaiHienThi = chuyenEnumThanhTenHienThi(loaiEnum);
            int soLuongSP = (int) row[1];
            double doanhThu = (double) row[2];
            double chiPhi = (double) row[3];
            double loiNhuan = doanhThu - chiPhi;
            double tyLeLN = doanhThu > 0 ? loiNhuan / doanhThu : 0;

            // Tính xu hướng so với năm trước
            Double doanhThuTruoc = doanhThuNamTruoc.get(loaiEnum);
            String trend;
            if (doanhThuTruoc == null || doanhThuTruoc == 0) {
                if (doanhThu > 0) {
                    trend = "↑ Mới";
                } else {
                    trend = "→ 0%";
                }
            } else {
                double thayDoi = ((doanhThu - doanhThuTruoc) / doanhThuTruoc) * 100;
                if (thayDoi > 0) {
                    trend = String.format("↑ +%.1f%%", thayDoi);
                } else if (thayDoi < 0) {
                    trend = String.format("↓ %.1f%%", thayDoi);
                } else {
                    trend = "→ 0%";
                }
            }

            // Thêm vào biểu đồ tròn (chỉ khi có doanh thu)
            if (doanhThu > 0) {
                bieuDoTron.themDuLieu(new DuLieuBieuDoTron(loaiHienThi, doanhThu,
                        chartColors[colorIndex % chartColors.length]));
            }

            // Thêm vào bảng
            tableModel.addRow(new Object[] {
                    loaiHienThi,
                    soLuongSP,
                    dfMoney.format(doanhThu),
                    dfMoney.format(chiPhi),
                    dfMoney.format(loiNhuan),
                    dfPercent.format(tyLeLN),
                    trend
            });

            colorIndex++;
        }

        // Cập nhật insight cards
        lblTongDoanhThu.setText(dfMoney.format(tongDoanhThu) + " VNĐ");
        lblLoiNhuan.setText(dfMoney.format(tongLoiNhuan) + " VNĐ (" + dfPercent.format(tyLeLNTB) + ")");
        lblLoaiTotNhat
                .setText(loaiTotNhat.isEmpty() ? "N/A" : loaiTotNhat + " (" + dfPercent.format(tyLeTotNhat) + ")");

        // Tính xu hướng tổng thể
        if (tongDoanhThuNamTruoc > 0) {
            double thayDoiTong = ((tongDoanhThu - tongDoanhThuNamTruoc) / tongDoanhThuNamTruoc) * 100;
            if (thayDoiTong > 0) {
                lblXuHuong.setText(String.format("↑ +%.1f%% doanh thu", thayDoiTong));
                lblXuHuong.setForeground(new Color(0x28A745));
            } else if (thayDoiTong < 0) {
                lblXuHuong.setText(String.format("↓ %.1f%% doanh thu", thayDoiTong));
                lblXuHuong.setForeground(new Color(0xDC3545));
            } else {
                lblXuHuong.setText("→ Không đổi");
                lblXuHuong.setForeground(new Color(0x6C757D));
            }
        } else {
            lblXuHuong.setText("Năm đầu tiên");
            lblXuHuong.setForeground(new Color(0x6C757D));
        }

        // Cập nhật summary panel
        lblSumDoanhThu.setText(dfMoney.format(tongDoanhThu) + " VNĐ");
        lblSumChiPhi.setText(dfMoney.format(tongChiPhi) + " VNĐ");
        lblSumLoiNhuan.setText(dfMoney.format(tongLoiNhuan) + " VNĐ");
        lblSumTyLeLN.setText(dfPercent.format(tyLeLNTB));
        lblSumSoLuongSP.setText(tongSoLuongSP + " sản phẩm");
        lblSumLoaiCaoNhat.setText(loaiTotNhat.isEmpty() ? "N/A" : loaiTotNhat);
        lblSumLoaiThapNhat.setText(loaiThapNhat.isEmpty() ? "N/A" : loaiThapNhat);

        // Xu hướng so với năm trước (tổng)
        if (tongDoanhThuNamTruoc > 0) {
            double thayDoiTong = ((tongDoanhThu - tongDoanhThuNamTruoc) / tongDoanhThuNamTruoc) * 100;
            if (thayDoiTong > 0) {
                lblSumSoSanh.setText(String.format("↑ +%.1f%%", thayDoiTong));
                lblSumSoSanh.setForeground(new Color(0x28A745));
            } else if (thayDoiTong < 0) {
                lblSumSoSanh.setText(String.format("↓ %.1f%%", thayDoiTong));
                lblSumSoSanh.setForeground(new Color(0xDC3545));
            } else {
                lblSumSoSanh.setText("→ 0%");
                lblSumSoSanh.setForeground(new Color(0x6C757D));
            }
        } else {
            lblSumSoSanh.setText("N/A");
            lblSumSoSanh.setForeground(new Color(0x6C757D));
        }
    }

    /**
     * Xuất dữ liệu ra file Excel
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
            fileChooser.setSelectedFile(new File("ThongKeTheoLoai.xlsx"));
            fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().endsWith(".xlsx")) {
                    file = new File(file.getAbsolutePath() + ".xlsx");
                }

                XSSFWorkbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Thống Kê Theo Loại");

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
                titleCell.setCellValue("THỐNG KÊ SẢN PHẨM THEO LOẠI");

                CellStyle titleStyle = workbook.createCellStyle();
                XSSFFont titleFont = workbook.createFont();
                titleFont.setBold(true);
                titleFont.setFontHeightInPoints((short) 16);
                titleStyle.setFont(titleFont);
                titleCell.setCellStyle(titleStyle);

                // Thông tin năm
                Row periodRow = sheet.createRow(1);
                periodRow.createCell(0).setCellValue("Năm: " + cmbNam.getSelectedItem());

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

                // Summary rows
                int summaryStartRow = tableModel.getRowCount() + 6;
                Row summaryRow1 = sheet.createRow(summaryStartRow);
                summaryRow1.createCell(0).setCellValue("TỔNG KẾT");

                Row summaryRow2 = sheet.createRow(summaryStartRow + 1);
                summaryRow2.createCell(0).setCellValue("Tổng doanh thu:");
                summaryRow2.createCell(1).setCellValue(lblSumDoanhThu.getText());

                Row summaryRow3 = sheet.createRow(summaryStartRow + 2);
                summaryRow3.createCell(0).setCellValue("Tổng chi phí:");
                summaryRow3.createCell(1).setCellValue(lblSumChiPhi.getText());

                Row summaryRow4 = sheet.createRow(summaryStartRow + 3);
                summaryRow4.createCell(0).setCellValue("Tổng lợi nhuận:");
                summaryRow4.createCell(1).setCellValue(lblSumLoiNhuan.getText());

                Row summaryRow5 = sheet.createRow(summaryStartRow + 4);
                summaryRow5.createCell(0).setCellValue("Tỷ lệ LN trung bình:");
                summaryRow5.createCell(1).setCellValue(lblSumTyLeLN.getText());

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
