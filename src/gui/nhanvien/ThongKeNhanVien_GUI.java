package gui.nhanvien;

import com.toedter.calendar.JDateChooser;
import component.button.PillButton;
import component.chart.BieuDoCotJFreeChart;
import component.chart.DuLieuBieuDoCot;
import dao.ThongKeNhanVien_DAO;
import dao.ThongKeNhanVien_DAO.KetQuaThongKe;

import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ThongKeNhanVien_GUI extends JPanel implements ActionListener, MouseListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2091812809316193815L;
	private JDateChooser dateTuNgay, dateDenNgay;
    private JComboBox<String> cmbCaLam;
    
    // Khai báo nút là thuộc tính toàn cục
    private JButton btnLoc, btnXuatExcel; 

    // Các Label thống kê
    private JLabel lblTongDoanhSo, lblSoHoaDon, lblTrungBinhDon;
    private JLabel lblSoPhieuTra, lblSoPhieuHuy, lblTyLeHoan;

    private BieuDoCotJFreeChart bieuDoHieuSuat;
    private ThongKeNhanVien_DAO dao;
    private String maNhanVienHienTai;

    /**
     * Constructor
     * @param maNV Mã nhân viên của người đang đăng nhập
     */
    public ThongKeNhanVien_GUI(String maNV) {
        this.maNhanVienHienTai = maNV;
        this.dao = new ThongKeNhanVien_DAO();

        // Thiết lập layout cho Panel
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel pnMain = new JPanel(new BorderLayout(0, 10));
        pnMain.setBackground(Color.WHITE);
        pnMain.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(pnMain, BorderLayout.CENTER);

        // --- 1. FILTER PANEL (layout null) ---
        JPanel pnFilter = new JPanel();
        pnFilter.setBackground(new Color(0xE3F2F5));
        pnFilter.setBorder(BorderFactory.createTitledBorder("Tiêu chí lọc"));
        pnFilter.setPreferredSize(new Dimension(0, 100));
        pnFilter.setLayout(null);

        JLabel lblTuNgay = new JLabel("Từ ngày");
        lblTuNgay.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblTuNgay.setBounds(20, 25, 80, 20);
        pnFilter.add(lblTuNgay);

        dateTuNgay = new JDateChooser();
        dateTuNgay.setDateFormatString("dd-MM-yyyy");
        dateTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateTuNgay.setBounds(20, 50, 150, 30);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        dateTuNgay.setDate(cal.getTime());
        pnFilter.add(dateTuNgay);

        JLabel lblDenNgay = new JLabel("Đến ngày");
        lblDenNgay.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblDenNgay.setBounds(200, 25, 80, 20);
        pnFilter.add(lblDenNgay);

        dateDenNgay = new JDateChooser();
        dateDenNgay.setDateFormatString("dd-MM-yyyy");
        dateDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateDenNgay.setBounds(200, 50, 150, 30);
        dateDenNgay.setDate(new Date());
        pnFilter.add(dateDenNgay);

        JLabel lblCaLam = new JLabel("Ca làm");
        lblCaLam.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblCaLam.setBounds(380, 25, 80, 20);
        pnFilter.add(lblCaLam);

        cmbCaLam = new JComboBox<>();
        cmbCaLam.addItem("Tất cả");
        cmbCaLam.addItem("Ca 1 (Sáng)");
        cmbCaLam.addItem("Ca 2 (Chiều)");
        cmbCaLam.addItem("Ca 3 (Tối)");
        cmbCaLam.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbCaLam.setBounds(380, 50, 130, 30);
        pnFilter.add(cmbCaLam);

        // Nút Thống Kê
        btnLoc = new PillButton("Thống Kê");
        btnLoc.setBounds(540, 45, 120, 35);
        pnFilter.add(btnLoc);

        // Nút Xuất Excel
        btnXuatExcel = new PillButton("Xuất Excel");
        btnXuatExcel.setBounds(670, 45, 120, 35); 
        pnFilter.add(btnXuatExcel);

        pnMain.add(pnFilter, BorderLayout.NORTH);

        // --- 2. STATS PANEL ---
        JPanel pnStats = new JPanel(new GridLayout(2, 3, 20, 15));
        pnStats.setBackground(new Color(0xE3F2F5));
        pnStats.setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder("Tổng quan hiệu suất"),
                new EmptyBorder(10, 20, 10, 20)));
        pnStats.setPreferredSize(new Dimension(0, 180));

        Font fTitle = new Font("Tahoma", Font.PLAIN, 15);
        Font fValue = new Font("Tahoma", Font.BOLD, 18);

        lblTongDoanhSo = createLabel(pnStats, "Doanh số bán:", fTitle, fValue, new Color(40, 167, 69));
        lblSoHoaDon = createLabel(pnStats, "Số hóa đơn:", fTitle, fValue, new Color(0x005a9e));
        lblTrungBinhDon = createLabel(pnStats, "TB / Hóa đơn:", fTitle, fValue, new Color(102, 16, 242));
        lblSoPhieuTra = createLabel(pnStats, "Số phiếu trả:", fTitle, fValue, new Color(255, 140, 0));
        lblSoPhieuHuy = createLabel(pnStats, "Số phiếu hủy:", fTitle, fValue, new Color(220, 53, 69));
        lblTyLeHoan = createLabel(pnStats, "Tỷ lệ hoàn trả:", fTitle, fValue, Color.DARK_GRAY);

        // --- 3. CHART PANEL ---
        JPanel pnChart = new JPanel(new BorderLayout());
        pnChart.setBorder(BorderFactory.createTitledBorder("Biểu đồ hiệu suất cá nhân"));
        pnChart.setBackground(Color.WHITE);

        bieuDoHieuSuat = new BieuDoCotJFreeChart();
        bieuDoHieuSuat.setTieuDeTrucX("Chỉ số");
        bieuDoHieuSuat.setTieuDeTrucY("Số lượng");
        pnChart.add(bieuDoHieuSuat, BorderLayout.CENTER);

        // --- 4. CONTENT PANEL ---
        JPanel pnContent = new JPanel(new BorderLayout(0, 10));
        pnContent.setBackground(Color.WHITE);
        JPanel pnTopSection = new JPanel(new BorderLayout(0, 10));
        pnTopSection.setBackground(Color.WHITE);
        pnTopSection.add(pnStats, BorderLayout.NORTH);
        pnTopSection.add(pnChart, BorderLayout.CENTER);
        pnContent.add(pnTopSection, BorderLayout.CENTER);
        pnMain.add(pnContent, BorderLayout.CENTER);

        // --- ĐĂNG KÝ SỰ KIỆN ---
        // Sử dụng 'this' vì class đã implements interface
        btnLoc.addActionListener(this);
        btnLoc.addMouseListener(this);
        
        btnXuatExcel.addActionListener(this);
        btnXuatExcel.addMouseListener(this);

        // Load dữ liệu ban đầu
        loadData();
    }

    // --- IMPLEMENTED METHODS (ACTION LISTENER) ---

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(btnLoc)) {
            loadData();
        } else if (source.equals(btnXuatExcel)) {
            xuatFileExcel();
        }
    }

    // --- IMPLEMENTED METHODS (MOUSE LISTENER) ---

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

    // --- CÁC HÀM LOGIC NGHIỆP VỤ ---

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

    private void loadData() {
        Date tu = dateTuNgay.getDate();
        Date den = dateDenNgay.getDate();

        if (tu == null || den == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ ngày tháng!");
            return;
        }
        if (tu.after(den)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được lớn hơn ngày kết thúc!");
            return;
        }

        int caLam = cmbCaLam.getSelectedIndex();
        KetQuaThongKe kq = dao.getThongKe(tu, den, maNhanVienHienTai, caLam);

        DecimalFormat dfTien = new DecimalFormat("#,##0 đ");
        DecimalFormat dfSo = new DecimalFormat("#,##0");
        DecimalFormat dfTyLe = new DecimalFormat("0.00'%'");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        String range = String.format("(%s - %s)", sdf.format(tu), sdf.format(den));
        String caText = cmbCaLam.getSelectedItem().toString();

        lblTongDoanhSo.setText("<html>" + dfTien.format(kq.tongDoanhSo)
                + "<br><span style='font-size:10px;color:gray;font-weight:normal'>" + range + "</span></html>");
        lblSoHoaDon.setText("<html>" + dfSo.format(kq.soHoaDon)
                + "<br><span style='font-size:10px;color:gray;font-weight:normal'>" + caText + "</span></html>");
        lblTrungBinhDon.setText(dfTien.format(kq.getGiaTriTrungBinh()));
        lblSoPhieuTra.setText("<html>" + dfSo.format(kq.soPhieuTra)
                + "<br><span style='font-size:10px;color:gray;font-weight:normal'>Tiền trả: "
                + dfTien.format(kq.tongTienTra) + "</span></html>");
        lblSoPhieuHuy.setText(dfSo.format(kq.soPhieuHuy));
        lblTyLeHoan.setText(dfTyLe.format(kq.getTyLeHoanTra()));

        bieuDoHieuSuat.xoaToanBoDuLieu();
        bieuDoHieuSuat.setTieuDeBieuDo("Tương quan các chỉ số giao dịch");

        Color c1 = new Color(0x005a9e);
        Color c2 = new Color(255, 140, 0);
        Color c3 = new Color(220, 53, 69);

        bieuDoHieuSuat.themDuLieu(new DuLieuBieuDoCot("Số Hóa Đơn", "Giao dịch", kq.soHoaDon, c1));
        bieuDoHieuSuat.themDuLieu(new DuLieuBieuDoCot("Số Phiếu Trả", "Giao dịch", kq.soPhieuTra, c2));
        bieuDoHieuSuat.themDuLieu(new DuLieuBieuDoCot("Số Phiếu Hủy", "Giao dịch", kq.soPhieuHuy, c3));
    }

    private void xuatFileExcel() {
        Date tu = dateTuNgay.getDate();
        Date den = dateDenNgay.getDate();

        if (tu == null || den == null || tu.after(den)) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày hợp lệ để xuất file!");
            return;
        }

        // 1. Lấy dữ liệu thống kê
        int caLam = cmbCaLam.getSelectedIndex();
        KetQuaThongKe kq = dao.getThongKe(tu, den, maNhanVienHienTai, caLam);
        
        // --- BỔ SUNG: SỬA LẠI LOGIC TÌM TÊN (THÊM TRIM) ---
        String tenNhanVien = "Không xác định";
        java.util.List<String[]> dsNV = dao.getDanhSachNhanVien(); 
        
        // Debug: In ra console để kiểm tra nếu vẫn lỗi
        System.out.println("Mã hiện tại (GUI): '" + maNhanVienHienTai + "'");
        
        if (dsNV != null) {
            for (String[] nv : dsNV) {
                // nv[0] là Mã, nv[1] là Tên
                if (nv[0] != null) {
                    // Cắt khoảng trắng 2 đầu bằng .trim() trước khi so sánh
                    String maTrongList = nv[0].trim();
                    String maDangNhap = maNhanVienHienTai.trim();
                    
                    if (maTrongList.equalsIgnoreCase(maDangNhap)) {
                        tenNhanVien = nv[1];
                        break;
                    }
                }
            }
        }
        // --------------------------------------------------

        // 2. Chọn nơi lưu file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu báo cáo hiệu suất cá nhân");
        // Thay khoảng trắng trong tên thành dấu _
        String safeName = tenNhanVien.replaceAll("\\s+", "_");
        String defaultName = "BaoCao_" + safeName + "_" + maNhanVienHienTai + ".xlsx";
        fileChooser.setSelectedFile(new File(defaultName));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".xlsx")) {
                file = new File(file.getAbsolutePath() + ".xlsx");
            }

            // 3. Tạo file Excel
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Hiệu Suất Cá Nhân");

                // --- Styles ---
                CellStyle headerStyle = workbook.createCellStyle();
                XSSFFont headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);

                CellStyle titleStyle = workbook.createCellStyle();
                XSSFFont titleFont = workbook.createFont();
                titleFont.setBold(true);
                titleFont.setFontHeightInPoints((short) 16);
                titleStyle.setFont(titleFont);
                titleStyle.setAlignment(HorizontalAlignment.CENTER);

                CellStyle currencyStyle = workbook.createCellStyle();
                DataFormat format = workbook.createDataFormat();
                currencyStyle.setDataFormat(format.getFormat("#,##0"));

                // --- Nội dung ---
                Row titleRow = sheet.createRow(0);
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue("BÁO CÁO HIỆU SUẤT CÁ NHÂN");
                titleCell.setCellStyle(titleStyle);
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3));

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                
                sheet.createRow(1).createCell(0).setCellValue("Thời gian: " + sdf.format(tu) + " - " + sdf.format(den));
                
                // Dòng hiển thị tên nhân viên
                sheet.createRow(2).createCell(0).setCellValue("Nhân viên: " + maNhanVienHienTai + " - " + tenNhanVien);
                
                sheet.createRow(3).createCell(0).setCellValue("Ca làm việc: " + cmbCaLam.getSelectedItem());

                // Header Bảng
                String[] headers = {"Chỉ số", "Giá trị", "Ghi chú"};
                Row headerRow = sheet.createRow(5);
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                // Dữ liệu
                int rowNum = 6;
                addMetricRow(sheet, rowNum++, "Tổng doanh số", kq.tongDoanhSo, currencyStyle, "VNĐ");
                addMetricRow(sheet, rowNum++, "Số lượng hóa đơn", kq.soHoaDon, null, "Hóa đơn");
                addMetricRow(sheet, rowNum++, "Giá trị trung bình/HĐ", kq.getGiaTriTrungBinh(), currencyStyle, "VNĐ");
                addMetricRow(sheet, rowNum++, "Số phiếu trả hàng", kq.soPhieuTra, null, "Phiếu");
                addMetricRow(sheet, rowNum++, "Tổng tiền trả lại", kq.tongTienTra, currencyStyle, "VNĐ");
                addMetricRow(sheet, rowNum++, "Số phiếu hủy", kq.soPhieuHuy, null, "Phiếu");

                Row rateRow = sheet.createRow(rowNum++);
                rateRow.createCell(0).setCellValue("Tỷ lệ hoàn trả");
                Cell rateVal = rateRow.createCell(1);
                rateVal.setCellValue(kq.getTyLeHoanTra() / 100.0);
                CellStyle percentStyle = workbook.createCellStyle();
                percentStyle.setDataFormat(format.getFormat("0.00%"));
                rateVal.setCellStyle(percentStyle);
                rateRow.createCell(2).setCellValue("% theo số lượng");

                sheet.autoSizeColumn(0);
                sheet.autoSizeColumn(1);
                sheet.autoSizeColumn(2);

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                }

                JOptionPane.showMessageDialog(this, "Xuất file thành công!\n" + file.getAbsolutePath());
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi xuất file: " + ex.getMessage());
            }
        }
    }

    private void addMetricRow(Sheet sheet, int rowIdx, String label, double val, CellStyle style, String note) {
        Row row = sheet.createRow(rowIdx);
        row.createCell(0).setCellValue(label);
        Cell valCell = row.createCell(1);
        valCell.setCellValue(val);
        if (style != null) valCell.setCellStyle(style);
        row.createCell(2).setCellValue(note);
    }
}