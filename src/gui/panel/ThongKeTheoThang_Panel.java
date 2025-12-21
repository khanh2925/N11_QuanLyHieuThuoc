package gui.panel;

import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.time.Year;
import java.util.List;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import component.button.PillButton;
import component.chart.*;
import dao.ThongKe_DAO;
import dao.ThongKe_DAO.BanGhiTaiChinh;
import enums.LoaiSanPham;

public class ThongKeTheoThang_Panel extends JPanel implements MouseListener, ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox<Integer> cmbChonNam;
    private JComboBox<String> cmbLoaiSP;
    private BieuDoCotGroup bieuDoDoanhThu;
    private JLabel lblTongBanHang, lblTongNhapHang, lblTongTraHang, lblTongHuyHang, lblLoiNhuanRong;
    private ThongKe_DAO thongKeDAO;
    
    // Khai báo nút là biến toàn cục
    private JButton btnXem, btnXuatExcel;

    public ThongKeTheoThang_Panel() {
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

        JLabel lblNam = new JLabel("Năm");
        lblNam.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNam.setBounds(20, 25, 80, 20);
        pnFilter.add(lblNam);

        int curYear = Year.now().getValue();
        Integer[] years = new Integer[10];
        for (int i = 0; i < 10; i++) years[i] = curYear - i;
        cmbChonNam = new JComboBox<>(years);
        cmbChonNam.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbChonNam.setBounds(20, 50, 100, 30);
        pnFilter.add(cmbChonNam);

        JLabel lblLoaiSP = new JLabel("Loại sản phẩm");
        lblLoaiSP.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblLoaiSP.setBounds(150, 25, 120, 20);
        pnFilter.add(lblLoaiSP);

        cmbLoaiSP = new JComboBox<>();
        cmbLoaiSP.addItem("Tất cả");
        for (LoaiSanPham l : LoaiSanPham.values()) cmbLoaiSP.addItem(l.getTenLoai());
        cmbLoaiSP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbLoaiSP.setBounds(150, 50, 150, 30);
        pnFilter.add(cmbLoaiSP);

        // Nút Xem (Thống Kê)
        btnXem = new PillButton("Thống Kê");
        btnXem.setBounds(330, 45, 120, 35);
        pnFilter.add(btnXem);
        
        // Nút Xuất Excel
        btnXuatExcel = new PillButton("Xuất Excel");
        btnXuatExcel.setBounds(475, 45, 120, 35);
        pnFilter.add(btnXuatExcel);

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
        bieuDoDoanhThu.setTieuDeTrucX("Tháng");
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

        // --- Đăng ký sự kiện ---
        btnXem.addActionListener(this);
        btnXem.addMouseListener(this);
        
        btnXuatExcel.addActionListener(this);
        btnXuatExcel.addMouseListener(this);

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

    @SuppressWarnings("deprecation")
	private void loadDuLieu() {
        int nam = (Integer) cmbChonNam.getSelectedItem();
        String tenLoai = (String) cmbLoaiSP.getSelectedItem();
        String maLoaiSP = "Tất cả";
        if (!"Tất cả".equals(tenLoai)) {
            for (LoaiSanPham l : LoaiSanPham.values()) if (l.getTenLoai().equals(tenLoai)) { maLoaiSP = l.name(); break; }
        }
        
        List<BanGhiTaiChinh> ds = thongKeDAO.getThongKeTaiChinhTheoThang(nam, maLoaiSP);

        bieuDoDoanhThu.xoaToanBoDuLieu();
        bieuDoDoanhThu.setTieuDeBieuDo("Tài Chính Năm " + nam);

        Color colBan = new Color(0x28a745);
        Color colNhap = new Color(0x007bff);
        Color colTra = new Color(0xffc107);
        Color colHuy = new Color(0xdc3545);

        double tongBan = 0, tongNhap = 0, tongTra = 0, tongHuy = 0;

        for (BanGhiTaiChinh item : ds) {
            String labelThang = "T" + item.thoiGian.replace("T", ""); 
            
            bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot(labelThang, "Bán hàng", item.banHang, colBan));
            bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot(labelThang, "Nhập hàng", item.nhapHang, colNhap));
            bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot(labelThang, "Trả hàng", item.traHang, colTra));
            bieuDoDoanhThu.themDuLieu(new DuLieuBieuDoCot(labelThang, "Hủy hàng", item.huyHang, colHuy));

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

    /**
     * Xuất file Excel sử dụng Apache POI, định dạng giống ThongKeTheoLoai_Panel
     */
    private void xuatFileExcel() {
        int nam = (Integer) cmbChonNam.getSelectedItem();
        String tenLoai = (String) cmbLoaiSP.getSelectedItem();
        String maLoaiSP = "Tất cả";
        if (!"Tất cả".equals(tenLoai)) {
            for (LoaiSanPham l : LoaiSanPham.values()) if (l.getTenLoai().equals(tenLoai)) { maLoaiSP = l.name(); break; }
        }

        // 1. Lấy dữ liệu
        List<BanGhiTaiChinh> ds = thongKeDAO.getThongKeTaiChinhTheoThang(nam, maLoaiSP);
        if (ds == null || ds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Chọn nơi lưu file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu báo cáo tài chính");
        fileChooser.setSelectedFile(new File("BaoCao_TaiChinh_Nam_" + nam + ".xlsx")); // Đuôi .xlsx
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".xlsx")) {
                file = new File(file.getAbsolutePath() + ".xlsx");
            }

            // 3. Tạo file Excel với Apache POI
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Thống Kê Tháng");

                // --- Styles ---
                CellStyle headerStyle = workbook.createCellStyle();
                XSSFFont headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex()); // Màu xanh đẹp
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                CellStyle titleStyle = workbook.createCellStyle();
                XSSFFont titleFont = workbook.createFont();
                titleFont.setBold(true);
                titleFont.setFontHeightInPoints((short) 16);
                titleStyle.setFont(titleFont);

                CellStyle currencyStyle = workbook.createCellStyle();
                DataFormat format = workbook.createDataFormat();
                currencyStyle.setDataFormat(format.getFormat("#,##0")); // Định dạng số tiền

                // --- Rows & Cells ---

                // Tiêu đề
                Row titleRow = sheet.createRow(0);
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue("BÁO CÁO TÀI CHÍNH NĂM " + nam);
                titleCell.setCellStyle(titleStyle);

                // Thông tin phụ
                Row infoRow = sheet.createRow(1);
                infoRow.createCell(0).setCellValue("Loại sản phẩm: " + tenLoai);

                // Header Table
                String[] headers = {"Tháng", "Bán hàng (Thu)", "Nhập hàng (Chi)", "Trả hàng (Chi)", "Hủy hàng (Chi)", "Lợi nhuận"};
                Row headerRow = sheet.createRow(3);
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                // Data Rows
                int rowNum = 4;
                long totalBan = 0, totalNhap = 0, totalTra = 0, totalHuy = 0;

                for (BanGhiTaiChinh item : ds) {
                    Row row = sheet.createRow(rowNum++);
                    double loiNhuan = item.banHang - (item.nhapHang + item.traHang + item.huyHang);
                    String labelThang = "Tháng " + item.thoiGian.replace("T", "");

                    // Cột 0: Tháng
                    row.createCell(0).setCellValue(labelThang);

                    // Các cột số tiền
                    createCurrencyCell(row, 1, item.banHang, currencyStyle);
                    createCurrencyCell(row, 2, item.nhapHang, currencyStyle);
                    createCurrencyCell(row, 3, item.traHang, currencyStyle);
                    createCurrencyCell(row, 4, item.huyHang, currencyStyle);
                    createCurrencyCell(row, 5, loiNhuan, currencyStyle);

                    // Cộng dồn tổng
                    totalBan += item.banHang;
                    totalNhap += item.nhapHang;
                    totalTra += item.traHang;
                    totalHuy += item.huyHang;
                }

                // Summary Row (Tổng kết)
                Row sumRow = sheet.createRow(rowNum + 1);
                Cell sumTitle = sumRow.createCell(0);
                sumTitle.setCellValue("TỔNG CỘNG");
                CellStyle sumStyle = workbook.createCellStyle();
                XSSFFont sumFont = workbook.createFont();
                sumFont.setBold(true);
                sumStyle.setFont(sumFont);
                sumTitle.setCellStyle(sumStyle);

                double totalLoiNhuan = totalBan - (totalNhap + totalTra + totalHuy);

                createCurrencyCell(sumRow, 1, totalBan, currencyStyle);
                createCurrencyCell(sumRow, 2, totalNhap, currencyStyle);
                createCurrencyCell(sumRow, 3, totalTra, currencyStyle);
                createCurrencyCell(sumRow, 4, totalHuy, currencyStyle);
                createCurrencyCell(sumRow, 5, totalLoiNhuan, currencyStyle);

                // Auto size columns
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                // 4. Ghi ra file
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                }

                JOptionPane.showMessageDialog(this, "Xuất file thành công!\n" + file.getAbsolutePath());
                
                // Mở file ngay
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Helper tạo cell số tiền nhanh
    private void createCurrencyCell(Row row, int colIndex, double value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    // --- IMPLEMENTED METHODS ---

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(btnXem)) {
            loadDuLieu();
        } else if (source.equals(btnXuatExcel)) {
            xuatFileExcel();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        Object source = e.getSource();
        if (source instanceof JButton) {
            ((JButton) source).setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        Object source = e.getSource();
        if (source instanceof JButton) {
            ((JButton) source).setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}
}