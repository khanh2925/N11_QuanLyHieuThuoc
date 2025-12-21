/**
 * @author Khôi
 * @version 2.0
 * @since Nov 19, 2025
 *
 * Mô tả: Giao diện tra cứu khách hàng và lịch sử giao dịch (Mua, Trả).
 * (Form chuẩn theo TraCuuNhanVien_GUI)
 */
package gui.tracuu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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

// Import các component riêng của bạn
import component.button.PillButton;
import component.input.PlaceholderSupport;
import component.border.RoundedBorder;

// Import DAO và Entity
import dao.KhachHang_DAO;
import dao.HoaDon_DAO;
import dao.PhieuTra_DAO;
import entity.KhachHang;
import entity.HoaDon;
import entity.PhieuTra;

@SuppressWarnings("serial")
public class TraCuuKhachHang_GUI extends JPanel {

    private static final String PLACEHOLDER_TIM_KIEM = "Tìm theo mã, tên hoặc SĐT... (F1 / Ctrl+F)";

    private JPanel pnHeader;
    private JPanel pnCenter;

    // Bảng Khách Hàng (Master)
    private JTable tblKhachHang;
    private DefaultTableModel modelKhachHang;

    // TabbedPane chứa các bảng chi tiết (Detail)
    private JTabbedPane tabChiTiet;
    private JTable tblLichSuMuaHang; // Đổi từ Bán -> Mua
    private DefaultTableModel modelLichSuMuaHang;

    private JTable tblLichSuTraHang;
    private DefaultTableModel modelLichSuTraHang;

    // Components lọc (Thay đổi cho phù hợp khách hàng)
    private JTextField txtTimKiem;
    private JComboBox<String> cbGioiTinh;
    private JComboBox<String> cbTrangThai;

    // Buttons
    private PillButton btnTim;
    private PillButton btnLamMoi;
    private PillButton btnXuatExcel;

    // DAO
    private final KhachHang_DAO khachHangDAO;
    private final HoaDon_DAO hoaDonDAO;
    private final PhieuTra_DAO phieuTraDAO;

    // Data cache
    private List<KhachHang> danhSachGoc = new ArrayList<>();

    // Formatter
    private final DecimalFormat df = new DecimalFormat("#,### đ");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TraCuuKhachHang_GUI() {
        setPreferredSize(new Dimension(1537, 850));

        // Khởi tạo DAO
        khachHangDAO = new KhachHang_DAO();
        hoaDonDAO = new HoaDon_DAO();
        phieuTraDAO = new PhieuTra_DAO();

        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 1. HEADER
        taoPhanHeader();
        add(pnHeader, BorderLayout.NORTH);

        // 2. CENTER
        taoPhanCenter();
        add(pnCenter, BorderLayout.CENTER);

        // 3. DATA
        addEvents();
        setupKeyboardShortcuts(); // Thiết lập phím tắt
        addFocusOnShow(); // Tự động focus ô tìm kiếm khi hiển thị
        loadDuLieuKhachHang();
    }

    // ==============================================================================
    // PHẦN HEADER
    // ==============================================================================
    private void taoPhanHeader() {
        pnHeader = new JPanel();
        pnHeader.setLayout(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94));
        pnHeader.setBackground(new Color(0xE3F2F5));

        // --- Ô TÌM KIẾM TO ---
        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, PLACEHOLDER_TIM_KIEM);
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        txtTimKiem.setBounds(25, 17, 480, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setToolTipText("<html><b>Phím tắt:</b> F1 hoặc Ctrl+F<br>Nhấn Enter để tìm kiếm</html>");
        pnHeader.add(txtTimKiem);

        // --- BỘ LỌC ---
        addFilterLabel("Giới tính:", 530, 28, 80, 35);
        cbGioiTinh = new JComboBox<>(new String[] { "Tất cả", "Nam", "Nữ" });
        setupCombo(cbGioiTinh, 610, 28, 140, 38);

        addFilterLabel("Trạng thái:", 790, 28, 100, 35);
        cbTrangThai = new JComboBox<>(new String[] { "Tất cả", "Hoạt động", "Ngừng" });
        setupCombo(cbTrangThai, 890, 28, 180, 38);

        // --- NÚT ---
        btnTim = new PillButton(
                "<html>" +
                        "<center>" +
                        "TÌM KIẾM<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Enter)</span>" +
                        "</center>" +
                        "</html>");
        btnTim.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnTim.setBounds(1120, 22, 130, 50);
        btnTim.setToolTipText(
                "<html><b>Phím tắt:</b> Enter (khi ở ô tìm kiếm)<br>Tìm kiếm theo mã, tên và bộ lọc</html>");
        pnHeader.add(btnTim);

        btnLamMoi = new PillButton(
                "<html>" +
                        "<center>" +
                        "LÀM MỚI<br>" +
                        "<span style='font-size:10px; color:#888888;'>(F5)</span>" +
                        "</center>" +
                        "</html>");
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnLamMoi.setBounds(1265, 22, 130, 50);
        btnLamMoi.setToolTipText("<html><b>Phím tắt:</b> F5<br>Làm mới toàn bộ dữ liệu và xóa bộ lọc</html>");
        pnHeader.add(btnLamMoi);

        btnXuatExcel = new PillButton(
                "<html>" +
                        "<center>" +
                        "XUẤT EXCEL<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Ctrl+E)</span>" +
                        "</center>" +
                        "</html>");
        btnXuatExcel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnXuatExcel.setBounds(1410, 22, 180, 50);
        btnXuatExcel.setToolTipText("<html><b>Phím tắt:</b> Ctrl+E<br>Xuất dữ liệu ra file Excel</html>");
        pnHeader.add(btnXuatExcel);
    }

    private void addFilterLabel(String text, int x, int y, int w, int h) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, w, h);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        pnHeader.add(lbl);
    }

    private void setupCombo(JComboBox<?> cb, int x, int y, int w, int h) {
        cb.setBounds(x, y, w, h);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        pnHeader.add(cb);
    }

    // ==============================================================================
    // PHẦN CENTER
    // ==============================================================================
    private void taoPhanCenter() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(400); // Chia tỉ lệ giống bên Nhân viên
        splitPane.setResizeWeight(0.5);
        pnCenter.add(splitPane, BorderLayout.CENTER);

        // --- TOP: BẢNG KHÁCH HÀNG ---
        // Cột dữ liệu phù hợp với Khách Hàng
        String[] colKH = { "STT", "Mã KH", "Tên khách hàng", "SĐT", "Ngày sinh", "Giới tính", "Trạng thái" };
        modelKhachHang = new DefaultTableModel(colKH, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblKhachHang = setupTable(modelKhachHang);

        // Render căn lề
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < tblKhachHang.getColumnCount(); i++) {
            if (i != 2)
                tblKhachHang.getColumnModel().getColumn(i).setCellRenderer(center); // Tên canh trái
        }
        tblKhachHang.getColumnModel().getColumn(2).setPreferredWidth(200);

        tblKhachHang.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if ("Hoạt động".equals(value)) {
                    lbl.setForeground(new Color(0x2E7D32)); // Xanh lá
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
                } else {
                    lbl.setForeground(Color.RED);
                    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                }
                return lbl;
            }
        });

        JScrollPane scrollKH = new JScrollPane(tblKhachHang);
        scrollKH.setBorder(createTitledBorder("Danh sách khách hàng"));
        splitPane.setTopComponent(scrollKH);

        // --- BOTTOM: TABBED PANE (LỊCH SỬ) ---
        tabChiTiet = new JTabbedPane();
        tabChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        // Tab 1: Lịch sử Mua Hàng
        tabChiTiet.addTab("Lịch sử mua hàng", createTabMuaHang());

        // Tab 2: Lịch sử Trả Hàng
        tabChiTiet.addTab("Lịch sử trả hàng", createTabTraHang());

        splitPane.setBottomComponent(tabChiTiet);
    }

    // Tạo Panel cho Tab Mua Hàng (Khác với bán hàng là hiển thị Nhân viên bán)
    private JComponent createTabMuaHang() {
        String[] cols = { "STT", "Mã hóa đơn", "Ngày mua", "Nhân viên bán", "Tổng tiền" };
        modelLichSuMuaHang = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblLichSuMuaHang = setupTable(modelLichSuMuaHang);
        setupTableAlign(tblLichSuMuaHang);
        return new JScrollPane(tblLichSuMuaHang);
    }

    // Tạo Panel cho Tab Trả Hàng
    private JComponent createTabTraHang() {
        String[] cols = { "STT", "Mã đơn trả", "Ngày trả", "Lý do trả", "Tiền hoàn lại" };
        modelLichSuTraHang = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblLichSuTraHang = setupTable(modelLichSuTraHang);
        setupTableAlign(tblLichSuTraHang);
        return new JScrollPane(tblLichSuTraHang);
    }

    // Setup chung cho table (Giữ nguyên style)
    private JTable setupTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight(35);
        table.setSelectionBackground(new Color(0xC8E6C9)); // Màu xanh nhạt khi chọn
        table.setGridColor(new Color(230, 230, 230));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(new Color(33, 150, 243)); // Màu xanh header
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 40));
        return table;
    }

    // Setup căn lề
    private void setupTableAlign(JTable table) {
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        int lastCol = table.getColumnCount() - 1;
        // Căn giữa các cột trừ cột 3 (Nhân viên bán / Lý do trả)
        for (int i = 0; i < lastCol; i++) {
            if (i != 3) // Cột 3 để căn trái (văn bản)
                table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        // Cột cuối (tiền) căn phải
        table.getColumnModel().getColumn(lastCol).setCellRenderer(right);
    }

    private TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 18), Color.DARK_GRAY);
    }

    // ==============================================================================
    // DỮ LIỆU & SỰ KIỆN
    // ==============================================================================

    private void addEvents() {
        // Sự kiện nút
        btnTim.addActionListener(e -> timKiem());
        btnLamMoi.addActionListener(e -> lamMoi());
        btnXuatExcel.addActionListener(e -> xuatExcel());
        txtTimKiem.addActionListener(e -> timKiem()); // Enter để tìm

        // Sự kiện click vào khách hàng -> Load dữ liệu tab bên dưới
        tblKhachHang.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblKhachHang.getSelectedRow();
                if (row >= 0) {
                    String maKH = tblKhachHang.getValueAt(row, 1).toString();
                    loadLichSuGiaoDich(maKH);
                }
            }
        });
    }

    /**
     * Thiết lập phím tắt cho màn hình Tra cứu Khách hàng
     */
    private void setupKeyboardShortcuts() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        // F1: Focus tìm kiếm
        inputMap.put(KeyStroke.getKeyStroke("F1"), "focusTimKiem");
        actionMap.put("focusTimKiem", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                txtTimKiem.requestFocus();
                txtTimKiem.selectAll();
            }
        });

        // F5: Làm mới
        inputMap.put(KeyStroke.getKeyStroke("F5"), "lamMoi");
        actionMap.put("lamMoi", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                lamMoi();
            }
        });

        // Ctrl+F: Focus tìm kiếm
        inputMap.put(KeyStroke.getKeyStroke("control F"), "timKiem");
        actionMap.put("timKiem", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                txtTimKiem.requestFocus();
                txtTimKiem.selectAll();
            }
        });

        // Ctrl+E: Xuất Excel
        inputMap.put(KeyStroke.getKeyStroke("control E"), "xuatExcel");
        actionMap.put("xuatExcel", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                xuatExcel();
            }
        });
    }

    /**
     * Validate dữ liệu trước khi tìm kiếm
     * 
     * @return true nếu dữ liệu hợp lệ, false nếu không
     */
    private boolean validateTimKiem() {
        String tuKhoa = txtTimKiem.getText().trim();
        if (tuKhoa.equals(PLACEHOLDER_TIM_KIEM) || tuKhoa.contains("Tìm theo mã"))
            tuKhoa = "";

        // VALIDATION: Kiểm tra độ dài từ khóa tìm kiếm (tối đa 35 ký tự cho tên khách
        // hàng)
        if (!tuKhoa.isEmpty() && tuKhoa.length() > 35) {
            JOptionPane.showMessageDialog(this,
                    "Từ khóa tìm kiếm không được vượt quá 35 ký tự!",
                    "Lỗi nhập liệu",
                    JOptionPane.ERROR_MESSAGE);
            txtTimKiem.requestFocus();
            txtTimKiem.selectAll();
            return false;
        }

        return true;
    }

    private void timKiem() {
        // Validate dữ liệu trước khi tìm kiếm
        if (!validateTimKiem()) {
            return;
        }

        String keyword = txtTimKiem.getText().trim();
        String gioiTinh = cbGioiTinh.getSelectedItem().toString();
        String trangThai = cbTrangThai.getSelectedItem().toString();

        List<KhachHang> ketQua = new ArrayList<>();

        // LOGIC TỐI ƯU: Tìm kiếm trên cache
        // 1. Nếu chỉ có bộ lọc → Filter trên cache (nhanh hơn)
        if (keyword.isEmpty() || keyword.equals(PLACEHOLDER_TIM_KIEM)) {
            // Đảm bảo cache đã được load
            if (danhSachGoc == null || danhSachGoc.isEmpty()) {
                danhSachGoc = khachHangDAO.layTatCaKhachHang();
            }
            ketQua = new ArrayList<>(danhSachGoc); // Clone từ cache
        }
        // 2. Nếu có keyword cụ thể → Tìm kiếm trong cache
        else {
            String kw = keyword.toLowerCase();
            // Đảm bảo cache đã được load
            if (danhSachGoc == null || danhSachGoc.isEmpty()) {
                danhSachGoc = khachHangDAO.layTatCaKhachHang();
            }
            // Tìm kiếm trên cache
            for (KhachHang kh : danhSachGoc) {
                if (kh.getMaKhachHang().toLowerCase().contains(kw)
                        || kh.getTenKhachHang().toLowerCase().contains(kw)
                        || (kh.getSoDienThoai() != null && kh.getSoDienThoai().contains(kw))) {
                    ketQua.add(kh);
                }
            }
        }

        // --- Áp dụng bộ lọc: giới tính ---
        if (!"Tất cả".equals(gioiTinh)) {
            boolean isNam = gioiTinh.equals("Nam");
            ketQua.removeIf(kh -> kh.isGioiTinh() != isNam);
        }

        // --- Áp dụng bộ lọc: trạng thái ---
        if (!"Tất cả".equals(trangThai)) {
            boolean isActive = trangThai.equals("Hoạt động");
            ketQua.removeIf(kh -> kh.isHoatDong() != isActive);
        }

        loadKetQuaTimKiem(ketQua);

        // Nếu tìm kiếm cụ thể (có nhập text) mà không thấy thì báo
        if (ketQua.isEmpty() && !keyword.isEmpty() && !keyword.equals(PLACEHOLDER_TIM_KIEM)) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng nào!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Load kết quả tìm kiếm lên bảng
     */
    private void loadKetQuaTimKiem(List<KhachHang> dsKhachHang) {
        modelKhachHang.setRowCount(0);
        int stt = 1;
        for (KhachHang kh : dsKhachHang) {
            String gioiTinh = kh.isGioiTinh() ? "Nam" : "Nữ";
            String ngaySinh = kh.getNgaySinh() != null ? kh.getNgaySinh().format(dtf) : "";
            String trangThai = kh.isHoatDong() ? "Hoạt động" : "Ngừng";

            modelKhachHang.addRow(new Object[] {
                    stt++,
                    kh.getMaKhachHang(),
                    kh.getTenKhachHang(),
                    kh.getSoDienThoai(),
                    ngaySinh,
                    gioiTinh,
                    trangThai
            });
        }
    }

    private void lamMoi() {
        txtTimKiem.setText("");
        PlaceholderSupport.addPlaceholder(txtTimKiem, PLACEHOLDER_TIM_KIEM);
        cbGioiTinh.setSelectedIndex(0);
        cbTrangThai.setSelectedIndex(0);
        modelKhachHang.setRowCount(0);
        modelLichSuMuaHang.setRowCount(0);
        modelLichSuTraHang.setRowCount(0);

        // Refresh cache và tải lại dữ liệu từ DB
        khachHangDAO.refreshCache();
        loadDuLieuKhachHang();
    }

    /**
     * Tự động focus vào ô tìm kiếm khi form được hiển thị
     */
    private void addFocusOnShow() {
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                SwingUtilities.invokeLater(() -> {
                    txtTimKiem.requestFocusInWindow();
                    txtTimKiem.selectAll();
                });
            }
        });
    }

    /**
     * Xuất dữ liệu ra file Excel
     */
    private void xuatExcel() {
        if (modelKhachHang.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
            fileChooser.setSelectedFile(new File("DanhSachKhachHang.xlsx"));
            fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().endsWith(".xlsx")) {
                    file = new File(file.getAbsolutePath() + ".xlsx");
                }

                XSSFWorkbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Danh Sách Khách Hàng");

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
                titleCell.setCellValue("DANH SÁCH KHÁCH HÀNG");

                CellStyle titleStyle = workbook.createCellStyle();
                XSSFFont titleFont = workbook.createFont();
                titleFont.setBold(true);
                titleFont.setFontHeightInPoints((short) 16);
                titleStyle.setFont(titleFont);
                titleCell.setCellStyle(titleStyle);

                // Thông tin ngày xuất
                Row periodRow = sheet.createRow(1);
                periodRow.createCell(0).setCellValue(
                        "Ngày xuất: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                // Header row
                Row headerRow = sheet.createRow(3);
                for (int i = 0; i < modelKhachHang.getColumnCount(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(modelKhachHang.getColumnName(i));
                    cell.setCellStyle(headerStyle);
                }

                // Data rows
                for (int row = 0; row < modelKhachHang.getRowCount(); row++) {
                    Row dataRow = sheet.createRow(row + 4);
                    for (int col = 0; col < modelKhachHang.getColumnCount(); col++) {
                        Object value = modelKhachHang.getValueAt(row, col);
                        dataRow.createCell(col).setCellValue(value != null ? value.toString() : "");
                    }
                }

                // Auto-size columns
                for (int i = 0; i < modelKhachHang.getColumnCount(); i++) {
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

    private void loadDuLieuKhachHang() {
        modelKhachHang.setRowCount(0);
        danhSachGoc = khachHangDAO.layTatCaKhachHang();

        int stt = 1;
        for (KhachHang kh : danhSachGoc) {
            String gioiTinh = kh.isGioiTinh() ? "Nam" : "Nữ";
            String ngaySinh = kh.getNgaySinh() != null ? kh.getNgaySinh().format(dtf) : "";
            String trangThai = kh.isHoatDong() ? "Hoạt động" : "Ngừng";

            modelKhachHang.addRow(new Object[] {
                    stt++,
                    kh.getMaKhachHang(),
                    kh.getTenKhachHang(),
                    kh.getSoDienThoai(),
                    ngaySinh,
                    gioiTinh,
                    trangThai
            });
        }
    }

    private void loadLichSuGiaoDich(String maKH) {
        // Xóa dữ liệu cũ
        modelLichSuMuaHang.setRowCount(0);
        modelLichSuTraHang.setRowCount(0);

        try {
            // Load lịch sử mua hàng (Hoá đơn) - Lấy tất cả rồi filter
            List<HoaDon> allHoaDon = hoaDonDAO.layTatCaHoaDon();
            int stt1 = 1;
            for (HoaDon hd : allHoaDon) {
                // Chỉ lấy hóa đơn của khách hàng này
                if (hd.getKhachHang() != null && hd.getKhachHang().getMaKhachHang().equals(maKH)) {
                    String ngayLap = hd.getNgayLap() != null ? hd.getNgayLap().format(dtf) : "";
                    String nhanVien = hd.getNhanVien() != null ? hd.getNhanVien().getTenNhanVien() : "";
                    String tongTien = df.format(hd.getTongTien());

                    modelLichSuMuaHang.addRow(new Object[] {
                            stt1++,
                            hd.getMaHoaDon(),
                            ngayLap,
                            nhanVien,
                            tongTien
                    });
                }
            }

            // Load lịch sử trả hàng - Lấy tất cả rồi filter
            List<PhieuTra> allPhieuTra = phieuTraDAO.layTatCaPhieuTra();
            int stt2 = 1;
            for (PhieuTra pt : allPhieuTra) {
                // Chỉ lấy phiếu trả của khách hàng này
                if (pt.getKhachHang() != null && pt.getKhachHang().getMaKhachHang().equals(maKH)) {
                    String ngayLap = pt.getNgayLap() != null ? pt.getNgayLap().format(dtf) : "";

                    // Lấy lý do từ chi tiết phiếu trả (lấy lý do đầu tiên hoặc tổng hợp)
                    String lyDo = "";
                    if (pt.getChiTietPhieuTraList() != null && !pt.getChiTietPhieuTraList().isEmpty()) {
                        lyDo = pt.getChiTietPhieuTraList().get(0).getLyDoChiTiet();
                        if (pt.getChiTietPhieuTraList().size() > 1) {
                            lyDo += " (+" + (pt.getChiTietPhieuTraList().size() - 1) + " SP khác)";
                        }
                    }

                    String tienHoan = df.format(pt.getTongTienHoan());

                    modelLichSuTraHang.addRow(new Object[] {
                            stt2++,
                            pt.getMaPhieuTra(),
                            ngayLap,
                            lyDo,
                            tienHoan
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải lịch sử giao dịch: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}