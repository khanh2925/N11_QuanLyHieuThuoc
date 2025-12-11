package gui.panel;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import component.button.PillButton;
import dao.ThongKe_DAO;

/**
 * Panel hiển thị danh sách lô sản phẩm sắp hết hạn
 * Cảnh báo các sản phẩm cần xử lý trước khi hết hạn
 * Bao gồm: Giá trị thiệt hại, tốc độ bán, đề xuất hành động
 */
public class SapHetHan_Panel extends JPanel {

    private JTable tblSapHetHan;
    private DefaultTableModel tableModel;
    private JLabel lblTongQuan;
    private JComboBox<String> cmbThoiGian;
    private JComboBox<String> cmbLoaiSP;

    // Insight cards
    private JLabel lblTongLo;
    private JLabel lblGiaTriThietHai;
    private JLabel lblCanXuLyGap;
    private JLabel lblDeXuatHanhDong;

    // DAO
    private ThongKe_DAO thongKeDAO;
    private static final int SO_NGAY_TINH_TB = 30; // Tính TB bán trong 30 ngày

    public SapHetHan_Panel() {
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
        pnTieuChiLoc.setPreferredSize(new Dimension(0, 80));
        pnTieuChiLoc.setLayout(null);

        JLabel lblThoiGian = new JLabel("Hết hạn trong vòng:");
        lblThoiGian.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblThoiGian.setBounds(20, 30, 150, 25);
        pnTieuChiLoc.add(lblThoiGian);

        String[] thoiGianOptions = { "7 ngày", "15 ngày", "30 ngày", "60 ngày", "90 ngày" };
        cmbThoiGian = new JComboBox<>(thoiGianOptions);
        cmbThoiGian.setSelectedItem("30 ngày");
        cmbThoiGian.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbThoiGian.setBounds(170, 28, 120, 30);
        pnTieuChiLoc.add(cmbThoiGian);

        JLabel lblLoaiSP = new JLabel("Loại sản phẩm:");
        lblLoaiSP.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblLoaiSP.setBounds(320, 30, 120, 25);
        pnTieuChiLoc.add(lblLoaiSP);

        cmbLoaiSP = new JComboBox<>();
        cmbLoaiSP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbLoaiSP.setBounds(440, 28, 180, 30);
        pnTieuChiLoc.add(cmbLoaiSP);
        loadLoaiSanPham();

        JButton btnLoc = new PillButton("🔍 Lọc");
        btnLoc.setBounds(650, 25, 100, 35);
        pnTieuChiLoc.add(btnLoc);

        JButton btnXuatExcel = new PillButton("📥 Xuất Excel");
        btnXuatExcel.setBounds(770, 25, 120, 35);
        pnTieuChiLoc.add(btnXuatExcel);

        pnMain.add(pnTieuChiLoc, BorderLayout.NORTH);

        // ===== INSIGHT CARDS =====
        JPanel pnInsights = createInsightCardsPanel();

        // ===== PANEL TỔNG QUAN =====
        JPanel pnTongQuan = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        pnTongQuan.setBackground(new Color(0xF8D7DA));
        pnTongQuan.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(0xDC3545)),
                new EmptyBorder(10, 15, 10, 15)));
        pnTongQuan.setPreferredSize(new Dimension(0, 50));

        JLabel lblIcon = new JLabel("⏰");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        pnTongQuan.add(lblIcon);

        lblTongQuan = new JLabel("Đang tải dữ liệu...");
        lblTongQuan.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblTongQuan.setForeground(new Color(0x721C24));
        pnTongQuan.add(lblTongQuan);

        // ===== PANEL BẢNG =====
        JPanel pnBang = new JPanel(new BorderLayout());
        pnBang.setBorder(BorderFactory.createTitledBorder("Danh sách lô sản phẩm sắp hết hạn"));
        pnBang.setBackground(Color.WHITE);

        // Thêm các cột mới
        String[] columnNames = { "STT", "Mã Lô", "Tên sản phẩm", "HSD", "Còn lại", "SL tồn", "TB bán/ngày", "Kịp bán?",
                "Giá trị", "Đề xuất" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblSapHetHan = new JTable(tableModel);
        tblSapHetHan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblSapHetHan.setRowHeight(32);
        tblSapHetHan.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblSapHetHan.getTableHeader().setBackground(new Color(0x0077B6));
        tblSapHetHan.getTableHeader().setForeground(Color.WHITE);

        // Căn giữa các cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblSapHetHan.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblSapHetHan.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tblSapHetHan.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        tblSapHetHan.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        tblSapHetHan.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        // Căn phải cột giá trị
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblSapHetHan.getColumnModel().getColumn(8).setCellRenderer(rightRenderer);

        // Custom renderer cho cột "Kịp bán?"
        tblSapHetHan.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);

                String status = value.toString();
                if (status.contains("Không")) {
                    setBackground(new Color(0xF8D7DA));
                    setForeground(new Color(0x721C24));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if (status.contains("Khó")) {
                    setBackground(new Color(0xFFF3CD));
                    setForeground(new Color(0x856404));
                } else {
                    setBackground(new Color(0xD4EDDA));
                    setForeground(new Color(0x155724));
                }

                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                }
                return c;
            }
        });

        // Custom renderer cho cột Đề xuất
        tblSapHetHan.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setFont(getFont().deriveFont(Font.BOLD, 11f));

                String suggestion = value.toString();
                if (suggestion.contains("Hủy")) {
                    setBackground(new Color(0xF8D7DA));
                    setForeground(new Color(0x721C24));
                } else if (suggestion.contains("Giảm")) {
                    setBackground(new Color(0xFFF3CD));
                    setForeground(new Color(0x856404));
                } else {
                    setBackground(new Color(0xD4EDDA));
                    setForeground(new Color(0x155724));
                }

                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                }
                return c;
            }
        });

        // Độ rộng cột
        tblSapHetHan.getColumnModel().getColumn(0).setPreferredWidth(35);
        tblSapHetHan.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblSapHetHan.getColumnModel().getColumn(2).setPreferredWidth(170);
        tblSapHetHan.getColumnModel().getColumn(3).setPreferredWidth(80);
        tblSapHetHan.getColumnModel().getColumn(4).setPreferredWidth(65);
        tblSapHetHan.getColumnModel().getColumn(5).setPreferredWidth(55);
        tblSapHetHan.getColumnModel().getColumn(6).setPreferredWidth(75);
        tblSapHetHan.getColumnModel().getColumn(7).setPreferredWidth(70);
        tblSapHetHan.getColumnModel().getColumn(8).setPreferredWidth(100);
        tblSapHetHan.getColumnModel().getColumn(9).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(tblSapHetHan);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        pnBang.add(scrollPane, BorderLayout.CENTER);

        // Panel chứa insight + tổng quan + bảng
        JPanel pnContent = new JPanel(new BorderLayout(0, 10));
        pnContent.setBackground(Color.WHITE);

        JPanel pnTop = new JPanel(new BorderLayout(0, 10));
        pnTop.setBackground(Color.WHITE);
        pnTop.add(pnInsights, BorderLayout.NORTH);
        pnTop.add(pnTongQuan, BorderLayout.SOUTH);

        pnContent.add(pnTop, BorderLayout.NORTH);
        pnContent.add(pnBang, BorderLayout.CENTER);

        pnMain.add(pnContent, BorderLayout.CENTER);

        // ===== SỰ KIỆN =====
        btnLoc.addActionListener(e -> loadDuLieu());
        btnXuatExcel.addActionListener(e -> xuatExcel());

        // Load dữ liệu lần đầu
        loadDuLieu();
    }

    /**
     * Load danh sách loại sản phẩm vào ComboBox
     */
    private void loadLoaiSanPham() {
        cmbLoaiSP.removeAllItems();
        cmbLoaiSP.addItem("Tất cả");

        List<String> danhSachLoai = thongKeDAO.layDanhSachLoaiSanPham();
        for (String loai : danhSachLoai) {
            String tenHienThi = chuyenEnumThanhTenHienThi(loai);
            cmbLoaiSP.addItem(tenHienThi);
        }
    }

    /**
     * Chuyển enum name thành tên hiển thị dễ đọc
     */
    private String chuyenEnumThanhTenHienThi(String enumName) {
        if (enumName == null)
            return "";
        switch (enumName) {
            case "THUOC":
                return "Thuốc";
            case "MY_PHAM":
                return "Mỹ phẩm";
            case "THUC_PHAM_BO_SUNG":
                return "Thực phẩm bổ sung";
            case "DUNG_CU_Y_TE":
                return "Dụng cụ y tế";
            case "SAN_PHAM_CHO_ME_VA_BE":
                return "Sản phẩm cho mẹ và bé";
            case "SAN_PHAM_KHAC":
                return "Sản phẩm khác";
            default:
                return enumName;
        }
    }

    /**
     * Chuyển tên hiển thị thành enum name để query
     */
    private String chuyenTenHienThiThanhEnum(String tenHienThi) {
        if (tenHienThi == null || tenHienThi.equals("Tất cả"))
            return null;
        switch (tenHienThi) {
            case "Thuốc":
                return "THUOC";
            case "Mỹ phẩm":
                return "MY_PHAM";
            case "Thực phẩm bổ sung":
                return "THUC_PHAM_BO_SUNG";
            case "Dụng cụ y tế":
                return "DUNG_CU_Y_TE";
            case "Sản phẩm cho mẹ và bé":
                return "SAN_PHAM_CHO_ME_VA_BE";
            case "Sản phẩm khác":
                return "SAN_PHAM_KHAC";
            default:
                return tenHienThi;
        }
    }

    /**
     * Parse số ngày từ chuỗi "30 ngày" -> 30
     */
    private int parseSoNgay(String thoiGian) {
        if (thoiGian == null)
            return 30;
        try {
            return Integer.parseInt(thoiGian.replace(" ngày", "").trim());
        } catch (NumberFormatException e) {
            return 30;
        }
    }

    /**
     * Tạo panel chứa các Insight Cards
     */
    private JPanel createInsightCardsPanel() {
        JPanel pnInsights = new JPanel(new GridLayout(1, 4, 15, 0));
        pnInsights.setBackground(Color.WHITE);
        pnInsights.setBorder(new EmptyBorder(0, 0, 10, 0));
        pnInsights.setPreferredSize(new Dimension(0, 80));

        // Card 1: Tổng lô sắp hết hạn
        JPanel card1 = createInsightCard("📦 TỔNG LÔ SẮP HẾT HẠN", "0 lô", new Color(0xDC3545));
        lblTongLo = (JLabel) ((JPanel) card1.getComponent(0)).getComponent(1);

        // Card 2: Giá trị thiệt hại
        JPanel card2 = createInsightCard("💸 GIÁ TRỊ THIỆT HẠI", "0 VNĐ", new Color(0xDC3545));
        lblGiaTriThietHai = (JLabel) ((JPanel) card2.getComponent(0)).getComponent(1);

        // Card 3: Cần xử lý gấp
        JPanel card3 = createInsightCard("🚨 KHÔNG KỊP BÁN", "0 lô", new Color(0xFD7E14));
        lblCanXuLyGap = (JLabel) ((JPanel) card3.getComponent(0)).getComponent(1);

        // Card 4: Đề xuất
        JPanel card4 = createInsightCard("💡 ĐỀ XUẤT GIẢM GIÁ", "0 lô", new Color(0x28A745));
        lblDeXuatHanhDong = (JLabel) ((JPanel) card4.getComponent(0)).getComponent(1);

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
     * Load dữ liệu từ database
     */
    private void loadDuLieu() {
        tableModel.setRowCount(0);

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DecimalFormat dfMoney = new DecimalFormat("#,### VNĐ");

        int soNgay = parseSoNgay((String) cmbThoiGian.getSelectedItem());
        String loaiSP = chuyenTenHienThiThanhEnum((String) cmbLoaiSP.getSelectedItem());

        // Lấy danh sách lô sắp hết hạn từ DAO
        List<Object[]> danhSachLo = thongKeDAO.layLoSapHetHan(soNgay, loaiSP);

        long tongThietHai = 0;
        int countKhongKip = 0;
        int countCanGiam = 0;

        for (int i = 0; i < danhSachLo.size(); i++) {
            Object[] row = danhSachLo.get(i);
            String maLo = (String) row[0];
            String tenSP = (String) row[1];
            // String loaiSPStr = (String) row[2]; // Không dùng
            LocalDate hsd = (LocalDate) row[3];
            int slTon = (Integer) row[4];
            double giaBan = (Double) row[5];
            // String maSP = (String) row[6]; // Không dùng

            long daysLeft = ChronoUnit.DAYS.between(today, hsd);

            // Tính trung bình bán/ngày của lô
            double tbBan = thongKeDAO.tinhTrungBinhBanNgayTheoLo(maLo, SO_NGAY_TINH_TB);
            if (tbBan < 0.1)
                tbBan = 0.1; // Tránh chia 0

            // Tính số lượng có thể bán được trong thời gian còn lại
            int coTheBan = (int) (tbBan * daysLeft);

            // Phân tích kịp bán không
            String kipBan;
            String deXuat;
            int slKhongBanDuoc = slTon - coTheBan;
            if (slKhongBanDuoc < 0)
                slKhongBanDuoc = 0;

            if (coTheBan >= slTon) {
                kipBan = "✅ Kịp";
                deXuat = "Bán bình thường";
            } else if (coTheBan >= slTon * 0.7) {
                // Có thể bán 70-100% → Khuyến nghị giảm giá nhẹ
                kipBan = "⚠️ Khó";
                deXuat = "Giảm 10-20%";
                countCanGiam++;
                tongThietHai += (long) (slKhongBanDuoc * giaBan * 0.20); // Ước tính thiệt hại 20%
            } else if (coTheBan >= slTon * 0.5) {
                // Có thể bán 50-70% → Khuyến nghị giảm giá mạnh
                kipBan = "⚠️ Khó";
                deXuat = "Giảm 30-50%";
                countCanGiam++;
                tongThietHai += (long) (slKhongBanDuoc * giaBan * 0.40); // Ước tính thiệt hại 40%
            } else {
                // Có thể bán < 50% → Không kịp bán, chỉ hủy
                kipBan = "❌ Không";
                deXuat = "Hủy";
                countKhongKip++;
                tongThietHai += (long) (slKhongBanDuoc * giaBan); // Thiệt hại 100% phần không bán được
            }

            long giaTriLo = (long) (slTon * giaBan);

            tableModel.addRow(new Object[] {
                    i + 1,
                    maLo,
                    tenSP,
                    hsd.format(formatter),
                    daysLeft + " ngày",
                    slTon,
                    String.format("%.1f", tbBan),
                    kipBan,
                    dfMoney.format(giaTriLo),
                    deXuat
            });
        }

        // Cập nhật insight cards
        lblTongLo.setText(danhSachLo.size() + " lô");
        lblGiaTriThietHai.setText(dfMoney.format(tongThietHai));
        lblCanXuLyGap.setText(countKhongKip + " lô (cần hủy)");
        lblDeXuatHanhDong.setText(countCanGiam + " lô");

        // Cập nhật tổng quan
        if (danhSachLo.isEmpty()) {
            lblTongQuan.setText("✅ Không có lô sản phẩm nào sắp hết hạn trong " + soNgay + " ngày tới.");
        } else {
            lblTongQuan.setText(String.format("Có %d lô sản phẩm sắp hết hạn. %d lô không kịp bán cần xử lý gấp!",
                    danhSachLo.size(), countKhongKip));
        }
    }

    /**
     * Xuất dữ liệu ra file Excel
     */
    private void xuatExcel() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Không có dữ liệu để xuất!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel");
        fileChooser.setSelectedFile(new File("SapHetHan_" +
                java.time.LocalDate.now().toString() + ".xlsx"));

        int result = fileChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fileChooser.getSelectedFile();
        if (!file.getName().endsWith(".xlsx")) {
            file = new File(file.getAbsolutePath() + ".xlsx");
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sắp hết hạn");

            // Style cho header
            CellStyle headerStyle = workbook.createCellStyle();
            XSSFFont headerFont = (XSSFFont) workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Style cho dữ liệu
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Style cho số tiền
            CellStyle moneyStyle = workbook.createCellStyle();
            moneyStyle.cloneStyleFrom(dataStyle);
            moneyStyle.setAlignment(HorizontalAlignment.RIGHT);
            DataFormat format = workbook.createDataFormat();
            moneyStyle.setDataFormat(format.getFormat("#,##0 \"VNĐ\""));

            // Style cho cảnh báo đỏ (Không kịp bán / Hủy)
            CellStyle warningStyle = workbook.createCellStyle();
            warningStyle.cloneStyleFrom(dataStyle);
            warningStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
            warningStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            XSSFFont whiteFont = (XSSFFont) workbook.createFont();
            whiteFont.setColor(IndexedColors.WHITE.getIndex());
            whiteFont.setBold(true);
            warningStyle.setFont(whiteFont);

            // Tạo header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(tableModel.getColumnName(i));
                cell.setCellStyle(headerStyle);
            }

            // Tạo data rows
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    Cell cell = row.createCell(j);
                    Object value = tableModel.getValueAt(i, j);
                    if (value != null) {
                        String strValue = value.toString();
                        // Cột giá trị - bỏ VNĐ và format số
                        if (j == 8) {
                            try {
                                String numStr = strValue.replaceAll("[^\\d]", "");
                                if (!numStr.isEmpty()) {
                                    cell.setCellValue(Long.parseLong(numStr));
                                    cell.setCellStyle(moneyStyle);
                                } else {
                                    cell.setCellValue(strValue);
                                    cell.setCellStyle(dataStyle);
                                }
                            } catch (NumberFormatException ex) {
                                cell.setCellValue(strValue);
                                cell.setCellStyle(dataStyle);
                            }
                        }
                        // Cột Kịp bán - bỏ emoji
                        else if (j == 7) {
                            cell.setCellValue(strValue.replaceAll("[✅⚠️❌]", "").trim());
                            if (strValue.contains("Không")) {
                                cell.setCellStyle(warningStyle);
                            } else {
                                cell.setCellStyle(dataStyle);
                            }
                        }
                        // Cột Đề xuất
                        else if (j == 9) {
                            cell.setCellValue(strValue);
                            if (strValue.contains("Hủy")) {
                                cell.setCellStyle(warningStyle);
                            } else {
                                cell.setCellStyle(dataStyle);
                            }
                        } else {
                            cell.setCellValue(strValue);
                            cell.setCellStyle(dataStyle);
                        }
                    } else {
                        cell.setCellValue("");
                        cell.setCellStyle(dataStyle);
                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Thêm sheet tóm tắt
            Sheet summarySheet = workbook.createSheet("Tóm tắt");
            Row row1 = summarySheet.createRow(0);
            row1.createCell(0).setCellValue("Tổng lô sắp hết hạn:");
            row1.createCell(1).setCellValue(lblTongLo.getText());

            Row row2 = summarySheet.createRow(1);
            row2.createCell(0).setCellValue("Giá trị thiệt hại ước tính:");
            row2.createCell(1).setCellValue(lblGiaTriThietHai.getText());

            Row row3 = summarySheet.createRow(2);
            row3.createCell(0).setCellValue("Số lô không kịp bán (cần hủy):");
            row3.createCell(1).setCellValue(lblCanXuLyGap.getText());

            Row row4 = summarySheet.createRow(3);
            row4.createCell(0).setCellValue("Số lô đề xuất giảm giá:");
            row4.createCell(1).setCellValue(lblDeXuatHanhDong.getText());

            Row row5 = summarySheet.createRow(4);
            row5.createCell(0).setCellValue("Thời gian lọc:");
            row5.createCell(1).setCellValue(cmbThoiGian.getSelectedItem() + " tới");

            Row row6 = summarySheet.createRow(5);
            row6.createCell(0).setCellValue("Ngày xuất:");
            row6.createCell(1).setCellValue(java.time.LocalDateTime.now().toString());

            summarySheet.autoSizeColumn(0);
            summarySheet.autoSizeColumn(1);

            // Ghi file
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }

            JOptionPane.showMessageDialog(this,
                    "Xuất Excel thành công!\n" + file.getAbsolutePath(),
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

            // Mở file sau khi xuất
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi xuất Excel: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
