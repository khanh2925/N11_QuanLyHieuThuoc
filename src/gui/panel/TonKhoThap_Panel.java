package gui.panel;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
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
 * Panel hiển thị danh sách sản phẩm có tồn kho thấp
 * Cảnh báo các sản phẩm cần nhập thêm hàng
 * Bao gồm: Dự báo hết hàng, SL đề xuất nhập, chi phí ước tính
 */
public class TonKhoThap_Panel extends JPanel {

    private JTable tblTonKho;
    private DefaultTableModel tableModel;
    private JLabel lblTongQuan;
    private JComboBox<Integer> cmbNguong;
    private JComboBox<String> cmbLoaiSP;

    // Insight cards
    private JLabel lblTongSP;
    private JLabel lblChiPhiNhap;
    private JLabel lblCanNhapGap;
    private JLabel lblNCCGoiY;

    // DAO
    private ThongKe_DAO thongKeDAO;
    private static final int SO_NGAY_TINH_TB = 30; // Tính TB bán trong 30 ngày

    public TonKhoThap_Panel() {
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

        JLabel lblNguong = new JLabel("Ngưỡng tồn kho tối thiểu:");
        lblNguong.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 14));
        lblNguong.setBounds(20, 30, 180, 25);
        pnTieuChiLoc.add(lblNguong);

        Integer[] nguongOptions = { 5, 10, 20, 30, 50, 100 };
        cmbNguong = new JComboBox<>(nguongOptions);
        cmbNguong.setSelectedItem(10);
        cmbNguong.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        cmbNguong.setBounds(200, 28, 100, 30);
        pnTieuChiLoc.add(cmbNguong);

        JLabel lblLoaiSP = new JLabel("Loại sản phẩm:");
        lblLoaiSP.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 14));
        lblLoaiSP.setBounds(330, 30, 120, 25);
        pnTieuChiLoc.add(lblLoaiSP);

        cmbLoaiSP = new JComboBox<>();
        cmbLoaiSP.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        cmbLoaiSP.setBounds(450, 28, 180, 30);
        pnTieuChiLoc.add(cmbLoaiSP);
        loadLoaiSanPham();

        JButton btnLoc = new PillButton("🔍 Lọc");
        btnLoc.setBounds(660, 25, 100, 35);
        pnTieuChiLoc.add(btnLoc);

        JButton btnXuatExcel = new PillButton("📥 Xuất Excel");
        btnXuatExcel.setBounds(780, 25, 120, 35);
        pnTieuChiLoc.add(btnXuatExcel);

        pnMain.add(pnTieuChiLoc, BorderLayout.NORTH);

        // ===== INSIGHT CARDS =====
        JPanel pnInsights = createInsightCardsPanel();

        // ===== PANEL TỔNG QUAN =====
        JPanel pnTongQuan = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        pnTongQuan.setBackground(new Color(0xFFF3CD));
        pnTongQuan.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(0xFFC107)),
                new EmptyBorder(10, 15, 10, 15)));
        pnTongQuan.setPreferredSize(new Dimension(0, 50));

        JLabel lblIcon = new JLabel("⚠️");
        lblIcon.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 20));
        pnTongQuan.add(lblIcon);

        lblTongQuan = new JLabel("Đang tải dữ liệu...");
        lblTongQuan.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 14));
        lblTongQuan.setForeground(new Color(0x856404));
        pnTongQuan.add(lblTongQuan);

        // ===== PANEL BẢNG =====
        JPanel pnBang = new JPanel(new BorderLayout());
        pnBang.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm tồn kho thấp"));
        pnBang.setBackground(Color.WHITE);

        // Thêm các cột mới: Dự báo hết, TB bán/ngày, SL đề xuất nhập, Chi phí ước tính
        String[] columnNames = { "STT", "Mã SP", "Tên sản phẩm", "Tồn kho", "TB bán/ngày", "Dự báo hết", "SL đề xuất",
                "Chi phí ước tính", "NCC gợi ý", "Trạng thái" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblTonKho = new JTable(tableModel);
        tblTonKho.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        tblTonKho.setRowHeight(32);
        tblTonKho.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        tblTonKho.getTableHeader().setBackground(new Color(0x0077B6));
        tblTonKho.getTableHeader().setForeground(Color.WHITE);

        // Căn giữa các cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblTonKho.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblTonKho.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tblTonKho.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        tblTonKho.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        tblTonKho.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        // Căn phải cột chi phí
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblTonKho.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);

        // Custom renderer cho cột dự báo hết (màu theo urgency)
        tblTonKho.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);

                String forecast = value != null ? value.toString() : "";
                if (forecast.contains("1 ngày") || forecast.contains("2 ngày") || forecast.contains("Hết")) {
                    setBackground(new Color(0xF8D7DA));
                    setForeground(new Color(0x721C24));
                    setFont(getFont().deriveFont(java.awt.Font.BOLD));
                } else if (forecast.contains("3 ngày") || forecast.contains("4 ngày") || forecast.contains("5 ngày")) {
                    setBackground(new Color(0xFFF3CD));
                    setForeground(new Color(0x856404));
                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }

                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                }
                return c;
            }
        });

        // Custom renderer cho cột trạng thái
        tblTonKho.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);

                String status = value != null ? value.toString() : "";
                if (status.contains("Cần nhập gấp")) {
                    setBackground(new Color(0xF8D7DA));
                    setForeground(new Color(0x721C24));
                } else if (status.contains("Cần nhập")) {
                    setBackground(new Color(0xFFF3CD));
                    setForeground(new Color(0x856404));
                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }

                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                }
                return c;
            }
        });

        // Độ rộng cột
        tblTonKho.getColumnModel().getColumn(0).setPreferredWidth(35);
        tblTonKho.getColumnModel().getColumn(1).setPreferredWidth(70);
        tblTonKho.getColumnModel().getColumn(2).setPreferredWidth(180);
        tblTonKho.getColumnModel().getColumn(3).setPreferredWidth(60);
        tblTonKho.getColumnModel().getColumn(4).setPreferredWidth(75);
        tblTonKho.getColumnModel().getColumn(5).setPreferredWidth(80);
        tblTonKho.getColumnModel().getColumn(6).setPreferredWidth(70);
        tblTonKho.getColumnModel().getColumn(7).setPreferredWidth(100);
        tblTonKho.getColumnModel().getColumn(8).setPreferredWidth(100);
        tblTonKho.getColumnModel().getColumn(9).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(tblTonKho);
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
            // Chuyển enum name thành tên hiển thị
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
     * Tạo panel chứa các Insight Cards
     */
    private JPanel createInsightCardsPanel() {
        JPanel pnInsights = new JPanel(new GridLayout(1, 4, 15, 0));
        pnInsights.setBackground(Color.WHITE);
        pnInsights.setBorder(new EmptyBorder(0, 0, 10, 0));
        pnInsights.setPreferredSize(new Dimension(0, 80));

        // Card 1: Tổng SP cần nhập
        JPanel card1 = createInsightCard("📦 TỔNG SP CẦN NHẬP", "0 sản phẩm", new Color(0xDC3545));
        lblTongSP = (JLabel) ((JPanel) card1.getComponent(0)).getComponent(1);

        // Card 2: Chi phí nhập ước tính
        JPanel card2 = createInsightCard("💵 CHI PHÍ ƯỚC TÍNH", "0 VNĐ", new Color(0xFD7E14));
        lblChiPhiNhap = (JLabel) ((JPanel) card2.getComponent(0)).getComponent(1);

        // Card 3: Cần nhập gấp
        JPanel card3 = createInsightCard("🚨 CẦN NHẬP GẤP", "0 SP", new Color(0xDC3545));
        lblCanNhapGap = (JLabel) ((JPanel) card3.getComponent(0)).getComponent(1);

        // Card 4: NCC gợi ý
        JPanel card4 = createInsightCard("🏢 NCC GỢI Ý", "Đang tải...", new Color(0x0077B6));
        lblNCCGoiY = (JLabel) ((JPanel) card4.getComponent(0)).getComponent(1);

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
        lblTitle.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 11));
        lblTitle.setForeground(new Color(0x6C757D));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 13));
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

        DecimalFormat dfMoney = new DecimalFormat("#,### VNĐ");

        int nguong = (Integer) cmbNguong.getSelectedItem();
        String loaiSP = chuyenTenHienThiThanhEnum((String) cmbLoaiSP.getSelectedItem());

        // Lấy danh sách sản phẩm tồn kho thấp
        List<Object[]> danhSachSP = thongKeDAO.laySanPhamTonKhoThap(nguong, loaiSP);

        long tongChiPhi = 0;
        int countUrgent = 0;

        for (int i = 0; i < danhSachSP.size(); i++) {
            Object[] row = danhSachSP.get(i);
            String maSP = (String) row[0];
            String tenSP = (String) row[1];
            // String loaiSPStr = (String) row[2]; // Không sử dụng trong bảng
            int tonKho = (Integer) row[3];
            double giaNhap = (Double) row[4];
            String tenNCC = (String) row[6];
            if (tenNCC == null || tenNCC.isEmpty()) {
                tenNCC = "Không rõ";
            }

            // Tính trung bình bán/ngày
            double tbBan = thongKeDAO.tinhTrungBinhBanNgay(maSP, SO_NGAY_TINH_TB);
            if (tbBan < 0.1)
                tbBan = 0.1; // Tránh chia cho 0

            // Tính dự báo hết hàng
            int duBaoHet = (int) Math.ceil(tonKho / tbBan);
            String duBaoText;
            if (tonKho <= 0) {
                duBaoText = "Đã hết!";
                duBaoHet = 0;
            } else if (duBaoHet <= 0) {
                duBaoText = "Đã hết!";
            } else if (duBaoHet == 1) {
                duBaoText = "1 ngày";
            } else {
                duBaoText = duBaoHet + " ngày";
            }

            // Tính SL đề xuất nhập (đủ bán 30 ngày)
            int slDeXuat = (int) Math.ceil(tbBan * 30) - tonKho;
            if (slDeXuat < 0)
                slDeXuat = 0;

            // Chi phí ước tính
            long chiPhi = (long) (slDeXuat * giaNhap);
            tongChiPhi += chiPhi;

            // Trạng thái
            String trangThai;
            if (duBaoHet <= 3) {
                trangThai = "🔴 Cần nhập gấp";
                countUrgent++;
            } else {
                trangThai = "🟡 Cần nhập";
            }

            tableModel.addRow(new Object[] {
                    i + 1,
                    maSP,
                    tenSP,
                    tonKho,
                    String.format("%.1f", tbBan),
                    duBaoText,
                    slDeXuat,
                    dfMoney.format(chiPhi),
                    tenNCC,
                    trangThai
            });
        }

        // Cập nhật insight cards
        lblTongSP.setText(danhSachSP.size() + " sản phẩm");
        lblChiPhiNhap.setText(dfMoney.format(tongChiPhi));
        lblCanNhapGap.setText(countUrgent + " SP (hết trong 3 ngày)");

        // Lấy NCC gợi ý
        Object[] nccGoiY = thongKeDAO.timNhaCungCapGoiY(nguong);
        String tenNCCGoiY = (String) nccGoiY[0];
        int soSP = (Integer) nccGoiY[1];
        if (soSP > 0) {
            lblNCCGoiY.setText(tenNCCGoiY + " (" + soSP + " SP)");
        } else {
            lblNCCGoiY.setText("Không có dữ liệu");
        }

        // Cập nhật tổng quan
        if (danhSachSP.isEmpty()) {
            lblTongQuan.setText("✅ Không có sản phẩm nào tồn kho thấp dưới ngưỡng " + nguong);
        } else {
            lblTongQuan.setText(String.format("Có %d sản phẩm tồn kho thấp. Ưu tiên nhập %d SP cần gấp trước!",
                    danhSachSP.size(), countUrgent));
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
        fileChooser.setSelectedFile(new File("TonKhoThap_" +
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
            Sheet sheet = workbook.createSheet("Tồn kho thấp");

            // Style cho header
            CellStyle headerStyle = workbook.createCellStyle();
            XSSFFont headerFont = (XSSFFont) workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
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

            // Style cho cảnh báo đỏ
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
                        // Cột chi phí - bỏ VNĐ và format số
                        if (j == 7) {
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
                        // Cột trạng thái
                        else if (j == 9) {
                            cell.setCellValue(strValue.replaceAll("[🔴🟡]", "").trim());
                            if (strValue.contains("Cần nhập gấp")) {
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
            row1.createCell(0).setCellValue("Tổng sản phẩm tồn kho thấp:");
            row1.createCell(1).setCellValue(lblTongSP.getText());

            Row row2 = summarySheet.createRow(1);
            row2.createCell(0).setCellValue("Chi phí nhập ước tính:");
            row2.createCell(1).setCellValue(lblChiPhiNhap.getText());

            Row row3 = summarySheet.createRow(2);
            row3.createCell(0).setCellValue("Số SP cần nhập gấp:");
            row3.createCell(1).setCellValue(lblCanNhapGap.getText());

            Row row4 = summarySheet.createRow(3);
            row4.createCell(0).setCellValue("NCC gợi ý:");
            row4.createCell(1).setCellValue(lblNCCGoiY.getText());

            Row row5 = summarySheet.createRow(4);
            row5.createCell(0).setCellValue("Ngưỡng tồn kho:");
            row5.createCell(1).setCellValue(cmbNguong.getSelectedItem() + " sản phẩm");

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
