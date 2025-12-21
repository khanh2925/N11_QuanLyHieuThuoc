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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.toedter.calendar.JDateChooser;
import component.button.PillButton;
import component.chart.*;
import dao.ThongKe_DAO;
import dao.ThongKe_DAO.BanGhiTaiChinh;
import enums.LoaiSanPham;

public class ThongKeTheoNgay_Panel extends JPanel implements MouseListener, ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JDateChooser ngayBatDau_DataChoose, ngayKetThuc_DataChoose;
    private JComboBox<String> cmbLoaiSP;
    private BieuDoCotGroup bieuDoDoanhThu;
    private JLabel lblTongBanHang, lblTongNhapHang, lblTongTraHang, lblTongHuyHang, lblLoiNhuanRong;
    private ThongKe_DAO thongKeDAO;
    
    // Khai báo biến toàn cục
    private JButton btnLoc, btnXuatExcel;

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

        // Nút Lọc (Thống kê)
        btnLoc = new PillButton("Thống Kê");
        btnLoc.setBounds(500, 45, 120, 35);
        pnFilter.add(btnLoc);

        // Nút Xuất Excel
        btnXuatExcel = new PillButton("Xuất Excel");
        btnXuatExcel.setBounds(640, 45, 120, 35);
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

        // --- Đăng ký sự kiện ---
        btnLoc.addActionListener(this);
        btnLoc.addMouseListener(this);
        
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

    // --- LOGIC LOADING DATA ---
    @SuppressWarnings("deprecation")
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

    // --- LOGIC XUẤT EXCEL (APACHE POI) ---
    private void xuatFileExcel() {
        Date tu = ngayBatDau_DataChoose.getDate();
        Date den = ngayKetThuc_DataChoose.getDate();
        if (tu == null || den == null || tu.after(den)) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày hợp lệ!");
            return;
        }

        String tenLoaiHienThi = (String) cmbLoaiSP.getSelectedItem();
        String maLoaiSP = "Tất cả";
        if (!"Tất cả".equals(tenLoaiHienThi)) {
            for (LoaiSanPham loai : LoaiSanPham.values()) {
                if (loai.getTenLoai().equals(tenLoaiHienThi)) { maLoaiSP = loai.name(); break; }
            }
        }

        // 1. Lấy dữ liệu
        List<BanGhiTaiChinh> ds = thongKeDAO.getThongKeTaiChinhTheoNgay(tu, den, maLoaiSP);

        if (ds == null || ds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Chọn nơi lưu file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu báo cáo ngày");
        String defaultName = "BaoCao_Ngay_" + new java.text.SimpleDateFormat("yyyyMMdd").format(tu) + "_" + new java.text.SimpleDateFormat("yyyyMMdd").format(den) + ".xlsx";
        fileChooser.setSelectedFile(new File(defaultName));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().endsWith(".xlsx")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".xlsx");
            }

            // 3. Tạo file Excel với Apache POI
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Thống Kê Theo Ngày");

                // --- Styles ---
                // Style cho Header (Nền xanh, chữ trắng)
                CellStyle headerStyle = workbook.createCellStyle();
                XSSFFont headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                // Style cho Tiêu đề lớn
                CellStyle titleStyle = workbook.createCellStyle();
                XSSFFont titleFont = workbook.createFont();
                titleFont.setBold(true);
                titleFont.setFontHeightInPoints((short) 16);
                titleStyle.setFont(titleFont);

                // Style cho Số tiền (Currency)
                CellStyle currencyStyle = workbook.createCellStyle();
                DataFormat format = workbook.createDataFormat();
                currencyStyle.setDataFormat(format.getFormat("#,##0"));

                // --- Rows & Cells ---

                // Tiêu đề
                Row titleRow = sheet.createRow(0);
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue("BÁO CÁO TÀI CHÍNH TỪ " + formatDate(tu) + " ĐẾN " + formatDate(den));
                titleCell.setCellStyle(titleStyle);

                // Thông tin phụ
                Row infoRow = sheet.createRow(1);
                infoRow.createCell(0).setCellValue("Loại sản phẩm: " + tenLoaiHienThi);

                // Header Table
                String[] headers = {"Ngày", "Bán hàng (Thu)", "Nhập hàng (Chi)", "Trả hàng (Chi)", "Hủy hàng (Chi)", "Lợi nhuận"};
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
                    
                    // Cột 0: Ngày
                    row.createCell(0).setCellValue(item.thoiGian);

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

                // Summary Row (Tổng kết - Giống form ThongKeTheoLoai)
                int summaryRowIndex = rowNum + 1;
                
                Row summaryTitleRow = sheet.createRow(summaryRowIndex);
                Cell sumTitle = summaryTitleRow.createCell(0);
                sumTitle.setCellValue("TỔNG KẾT");
                CellStyle boldStyle = workbook.createCellStyle();
                XSSFFont boldFont = workbook.createFont();
                boldFont.setBold(true);
                boldStyle.setFont(boldFont);
                sumTitle.setCellStyle(boldStyle);

                // Tổng Bán Hàng
                Row sumRow1 = sheet.createRow(summaryRowIndex + 1);
                sumRow1.createCell(0).setCellValue("Tổng Bán Hàng (Thu):");
                createCurrencyCell(sumRow1, 1, totalBan, currencyStyle);

                // Tổng Nhập Hàng
                Row sumRow2 = sheet.createRow(summaryRowIndex + 2);
                sumRow2.createCell(0).setCellValue("Tổng Nhập Hàng (Chi):");
                createCurrencyCell(sumRow2, 1, totalNhap, currencyStyle);

                // Tổng Lợi Nhuận
                double totalLoiNhuan = totalBan - (totalNhap + totalTra + totalHuy);
                Row sumRow3 = sheet.createRow(summaryRowIndex + 3);
                sumRow3.createCell(0).setCellValue("Tổng Lợi Nhuận:");
                createCurrencyCell(sumRow3, 1, totalLoiNhuan, currencyStyle);

                // Auto size columns
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                // 4. Ghi ra file
                try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
                    workbook.write(fos);
                }

                JOptionPane.showMessageDialog(this, "Xuất file thành công!\n" + fileToSave.getAbsolutePath());
                
                // Mở file ngay
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(fileToSave);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + ex.getMessage());
            }
        }
    }

    // Helper tạo cell số tiền nhanh
    private void createCurrencyCell(Row row, int colIndex, double value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private String formatDate(Date d) {
        return new java.text.SimpleDateFormat("dd/MM/yyyy").format(d);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(btnLoc)) {
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