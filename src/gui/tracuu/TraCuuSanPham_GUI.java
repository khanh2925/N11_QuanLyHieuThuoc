package gui.tracuu;

import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import component.button.PillButton;
import component.input.PlaceholderSupport;
import component.border.RoundedBorder;

// Import DAO & Entity & Enum
import dao.LoSanPham_DAO;
import dao.QuyCachDongGoi_DAO;
import dao.SanPham_DAO;
import entity.LoSanPham;
import entity.QuyCachDongGoi;
import entity.SanPham;
import enums.LoaiSanPham;

/**
 * @author Quốc Khánh
 * @version 1.8 (Modified: Use getTenLoai() for ComboBox and Table)
 */
@SuppressWarnings("serial")
public class TraCuuSanPham_GUI extends JPanel implements ActionListener {

    // --- Components UI ---
    private JPanel pnHeader;
    private JPanel pnCenter;
    
    // Bảng Master (Sản phẩm)
    private JTable tblSanPham;
    private DefaultTableModel modelSanPham;
    
    // Khu vực Tab chi tiết
    private JTabbedPane tabChiTiet;
    
    // Bảng Lô
    private JTable tblLoSanPham;
    private DefaultTableModel modelLoSanPham;
    
    // Bảng Quy Cách
    private JTable tblQuyCach;
    private DefaultTableModel modelQuyCach;

    // Bộ lọc
    private JTextField txtTimThuoc;
    private JComboBox<String> cbLoai;
    // private JComboBox<String> cbKe; // Đã xóa
    private JComboBox<String> cbTrangThai;
    private PillButton btnTimKiem;
    private PillButton btnLamMoi;

    // --- Utils & DAO ---
    private final DecimalFormat df = new DecimalFormat("#,### đ");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private SanPham_DAO sanPhamDao;
    private LoSanPham_DAO loSanPhamDao;
    private QuyCachDongGoi_DAO quyCachDao;

    // Cache Data
    private List<SanPham> dsSanPhamHienTai;
	private PillButton btnXuatExcel;

    public TraCuuSanPham_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        
        // 1. Khởi tạo DAO
        sanPhamDao = new SanPham_DAO();
        loSanPhamDao = new LoSanPham_DAO();
        quyCachDao = new QuyCachDongGoi_DAO();
        dsSanPhamHienTai = new ArrayList<>();

        // 2. Dựng giao diện
        initialize();
        setupKeyboardShortcuts(); // Thiết lập phím tắt

    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        taoPhanHeader();
        add(pnHeader, BorderLayout.NORTH);

        // Center (Bảng + Tabs)
        taoPhanCenter();
        add(pnCenter, BorderLayout.CENTER);
        
        // Gán sự kiện (ActionListener & MouseListener)
        addEvents();

        // Load data ban đầu
        xuLyLamMoi(); 
    }

    // ==============================================================================
    //                                  UI: HEADER
    // ==============================================================================
    private void taoPhanHeader() {
        pnHeader = new JPanel();
        pnHeader.setLayout(null); 
        pnHeader.setPreferredSize(new Dimension(1073, 94)); 
        pnHeader.setBackground(new Color(0xE3F2F5));

        // --- Ô TÌM KIẾM (Font 20) ---
        txtTimThuoc = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimThuoc, "Nhập tên thuốc, mã SP, số đăng ký... (F1 / Ctrl+F)");
        txtTimThuoc.setFont(new Font("Segoe UI", Font.PLAIN, 20)); 
        txtTimThuoc.setBounds(25, 17, 480, 60);
        txtTimThuoc.setBorder(new RoundedBorder(20));
        txtTimThuoc.setBackground(Color.WHITE);
        txtTimThuoc.setToolTipText("<html><b>Phím tắt:</b> F1 hoặc Ctrl+F<br>Nhấn Enter để tìm kiếm</html>");
        pnHeader.add(txtTimThuoc);

        // --- BỘ LỌC (Font 18) ---
        // 1. Loại
        addFilterLabel("Loại:", 530, 28, 50, 35);
        cbLoai = new JComboBox<>();
        cbLoai.addItem("Tất cả");
        for (LoaiSanPham loai : LoaiSanPham.values()) {
            // --- SỬA ĐỔI 1: Dùng getTenLoai() thay vì name() ---
            cbLoai.addItem(loai.getTenLoai()); 
        }
        setupComboBox(cbLoai, 580, 28, 180, 38);

        // 2. Trạng thái 
        addFilterLabel("Trạng thái:", 790, 28, 100, 35);
        cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đang bán", "Ngừng kinh doanh"});
        setupComboBox(cbTrangThai, 890, 28, 180, 38);

        // --- NÚT (Font 18) ---
        btnTimKiem = new PillButton(
                "<html>" +
                    "<center>" +
                        "TÌM KIẾM<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Enter)</span>" +
                    "</center>" +
                "</html>"
            );
        btnTimKiem.setBounds(1120, 22, 130, 50);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 18)); 
        btnTimKiem.setToolTipText("<html><b>Phím tắt:</b> Enter (khi ở ô tìm kiếm)<br>Tìm kiếm theo mã, tên sản phẩm và bộ lọc</html>");
        pnHeader.add(btnTimKiem);

        btnLamMoi = new PillButton(
                "<html>" +
                    "<center>" +
                        "LÀM MỚI<br>" +
                        "<span style='font-size:10px; color:#888888;'>(F5)</span>" +
                    "</center>" +
                "</html>"
            );
        btnLamMoi.setBounds(1265, 22, 130, 50);
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18)); 
        btnLamMoi.setToolTipText("<html><b>Phím tắt:</b> F5<br>Làm mới toàn bộ dữ liệu và xóa bộ lọc</html>");
        pnHeader.add(btnLamMoi);
        
        btnXuatExcel = new PillButton(
                "<html>" +
                    "<center>" +
                        "XUẤT EXCEL<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Ctrl+E)</span>" +
                    "</center>" +
                "</html>"
            );
        btnXuatExcel.setBounds(1410, 22, 150, 50);
        btnXuatExcel.setFont(new Font("Segoe UI", Font.BOLD, 18)); 
        btnXuatExcel.setToolTipText("<html><b>Phím tắt:</b> Ctrl+E<br>Xuất dữ liệu ra file Excel (Danh sách, Lô hàng, Quy cách)</html>");
        pnHeader.add(btnXuatExcel);
    }

    // Helper tạo label và combobox (Font 18)
    private void addFilterLabel(String text, int x, int y, int w, int h) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, w, h);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 18)); 
        pnHeader.add(lbl);
    }

    private void setupComboBox(JComboBox<?> cb, int x, int y, int w, int h) {
        cb.setBounds(x, y, w, h);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 18)); 
        pnHeader.add(cb);
    }

    // ==============================================================================
    //                                  UI: CENTER
    // ==============================================================================
    private void taoPhanCenter() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(400); 
        splitPane.setResizeWeight(0.5);

        // --- TOP: BẢNG SẢN PHẨM ---
        String[] colSanPham = {
            "Mã SP", "Tên sản phẩm", "Loại", "Số ĐK", "Đường dùng", 
            "Giá Bán Gốc", "Vị trí", "Trạng thái"
        };
        modelSanPham = new DefaultTableModel(colSanPham, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        tblSanPham = setupTable(modelSanPham);
        
        configureTableRenderers();

        JScrollPane scrollSP = new JScrollPane(tblSanPham);
        scrollSP.setBorder(createTitledBorder("Danh sách sản phẩm"));
        splitPane.setTopComponent(scrollSP);

        // --- BOTTOM: TABBED PANE ---
        tabChiTiet = new JTabbedPane();
        tabChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 16)); 
        
        tabChiTiet.addTab("Danh sách lô hàng", createTabLoHang());
        tabChiTiet.addTab("Quy cách đóng gói & Giá bán", createTabQuyCach());

        splitPane.setBottomComponent(tabChiTiet);
        pnCenter.add(splitPane, BorderLayout.CENTER);
    }

    private void configureTableRenderers() {
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        for(int i=0; i<tblSanPham.getColumnCount(); i++) {
            if(i != 1) tblSanPham.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        tblSanPham.getColumnModel().getColumn(5).setCellRenderer(right); 
        
        tblSanPham.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if("Đang bán".equals(value)) {
                    lbl.setForeground(new Color(0x2E7D32));
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 15)); 
                } else {
                    lbl.setForeground(Color.RED);
                    lbl.setFont(new Font("Segoe UI", Font.ITALIC, 15));
                }
                return lbl;
            }
        });
    }

    private JComponent createTabLoHang() {
        String[] colLo = {"STT", "Mã lô", "Hạn sử dụng", "Số lượng tồn"};
        modelLoSanPham = new DefaultTableModel(colLo, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLoSanPham = setupTable(modelLoSanPham);
        
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tblLoSanPham.getColumnCount(); i++) {
            tblLoSanPham.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        return new JScrollPane(tblLoSanPham);
    }

    private JComponent createTabQuyCach() {
        String[] colQC = {"STT", "Mã quy cách", "Đơn vị tính", "Quy đổi", "Giá bán (Sau CK)", "Tỉ lệ giảm giá", "Loại đơn vị"};
        modelQuyCach = new DefaultTableModel(colQC, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblQuyCach = setupTable(modelQuyCach);
        
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        
        tblQuyCach.getColumnModel().getColumn(0).setCellRenderer(center);
        tblQuyCach.getColumnModel().getColumn(1).setCellRenderer(center);
        tblQuyCach.getColumnModel().getColumn(2).setCellRenderer(center);
        tblQuyCach.getColumnModel().getColumn(3).setCellRenderer(center);
        tblQuyCach.getColumnModel().getColumn(4).setCellRenderer(right);
        tblQuyCach.getColumnModel().getColumn(5).setCellRenderer(center);
        
        tblQuyCach.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if ("Đơn vị gốc".equals(value)) {
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    lbl.setForeground(new Color(0, 102, 204));
                } else {
                    lbl.setForeground(Color.GRAY);
                }
                return lbl;
            }
        });
        
        return new JScrollPane(tblQuyCach);
    }

    private JTable setupTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16)); 
        table.setRowHeight(35); 
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(0xC8E6C9));
        table.setSelectionForeground(Color.BLACK);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
        header.setBackground(new Color(33, 150, 243));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 40));
        return table;
    }

    private TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 18), Color.DARK_GRAY
        );
    }

    // ==============================================================================
    //                                  XỬ LÝ SỰ KIỆN
    // ==============================================================================
    private void addEvents() {
        btnTimKiem.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnXuatExcel.addActionListener(this);
        txtTimThuoc.addActionListener(this);

        tblSanPham.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadChiTietTuDongChon();
            }
        });

        tblSanPham.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { 
                    int row = tblSanPham.getSelectedRow();
                    if (row != -1) {
                        String tenSP = tblSanPham.getValueAt(row, 1).toString();
                        JOptionPane.showMessageDialog(TraCuuSanPham_GUI.this, 
                                "Bạn vừa click đúp vào sản phẩm: " + tenSP + "\n(Có thể mở form sửa hoặc xem chi tiết tại đây)");
                    }
                }
            }
        });
    }

    /**
     * Thiết lập phím tắt cho màn hình Tra cứu Sản phẩm
     */
    private void setupKeyboardShortcuts() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        // F1: Focus tìm kiếm
        inputMap.put(KeyStroke.getKeyStroke("F1"), "focusTimKiem");
        actionMap.put("focusTimKiem", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtTimThuoc.requestFocus();
                txtTimThuoc.selectAll();
            }
        });

        // F5: Làm mới
        inputMap.put(KeyStroke.getKeyStroke("F5"), "lamMoi");
        actionMap.put("lamMoi", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyLamMoi();
            }
        });

        // Ctrl+F: Focus tìm kiếm
        inputMap.put(KeyStroke.getKeyStroke("control F"), "timKiem");
        actionMap.put("timKiem", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtTimThuoc.requestFocus();
                txtTimThuoc.selectAll();
            }
        });

        // Ctrl+E: Xuất Excel
        inputMap.put(KeyStroke.getKeyStroke("control E"), "xuatExcel");
        actionMap.put("xuatExcel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuatExcel();
            }
        });

        // Enter trên ô tìm kiếm
        txtTimThuoc.addActionListener(ev -> xuLyTimKiem());
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        
        if (o == btnTimKiem || o == txtTimThuoc) {
            xuLyTimKiem();
        } else if (o == btnLamMoi) {
            xuLyLamMoi();
        } else if (o == btnXuatExcel) {
            xuatExcel();
        }
    }

    // ==============================================================================
    //                                  LOGIC NGHIỆP VỤ
    // ==============================================================================

    private void loadChiTietTuDongChon() {
        int row = tblSanPham.getSelectedRow();
        if (row >= 0) {
            String maSP = tblSanPham.getValueAt(row, 0).toString();
            
            SanPham spChon = dsSanPhamHienTai.stream()
                    .filter(s -> s.getMaSanPham().equals(maSP))
                    .findFirst()
                    .orElse(null);
            
            if (spChon != null) {
                loadChiTietSanPham(spChon);
            }
        }
    }

    private void xuLyLamMoi() {
        txtTimThuoc.setText("");
        PlaceholderSupport.addPlaceholder(txtTimThuoc, "Nhập tên thuốc, mã SP, số đăng ký...");
        cbLoai.setSelectedIndex(0);
        cbTrangThai.setSelectedIndex(0);
        
        dsSanPhamHienTai = sanPhamDao.layTatCaSanPham(); 
        renderBangSanPham(dsSanPhamHienTai);
        
        modelLoSanPham.setRowCount(0);
        modelQuyCach.setRowCount(0);
    }

    private void xuLyTimKiem() {
        String tuKhoa = txtTimThuoc.getText().trim();
        if (tuKhoa.contains("Nhập tên thuốc")) tuKhoa = "";

        List<SanPham> ketQuaTimKiem;
        if (!tuKhoa.isEmpty()) {
            ketQuaTimKiem = sanPhamDao.timKiemSanPham(tuKhoa);
        } else {
            ketQuaTimKiem = sanPhamDao.layTatCaSanPham();
        }

        String loaiChon = (String) cbLoai.getSelectedItem();
        String trangThaiChon = (String) cbTrangThai.getSelectedItem();

        List<SanPham> ketQuaCuoiCung = ketQuaTimKiem.stream().filter(sp -> {
            // --- SỬA ĐỔI 2: So sánh bằng getTenLoai() thay vì name() ---
            boolean passLoai = "Tất cả".equals(loaiChon) || 
                               (sp.getLoaiSanPham() != null && sp.getLoaiSanPham().getTenLoai().equals(loaiChon));
            
            boolean passTrangThai = "Tất cả".equals(trangThaiChon) || 
                                    (sp.isHoatDong() == "Đang bán".equals(trangThaiChon));
            
            return passLoai && passTrangThai;
        }).collect(Collectors.toList());

        dsSanPhamHienTai = ketQuaCuoiCung;
        renderBangSanPham(dsSanPhamHienTai);
        
        modelLoSanPham.setRowCount(0);
        modelQuyCach.setRowCount(0);
        
        if (ketQuaCuoiCung.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm nào phù hợp!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void renderBangSanPham(List<SanPham> list) {
        modelSanPham.setRowCount(0);
        for (SanPham sp : list) {
            String trangThaiText = sp.isHoatDong() ? "Đang bán" : "Ngừng kinh doanh";
            
            // --- SỬA ĐỔI 3: Hiển thị getTenLoai() lên bảng ---
            String loaiText = sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().getTenLoai() : "";
            
            String duongDungText = sp.getDuongDung() != null ? sp.getDuongDung().name() : ""; 
            
            double giaBanGoc = sp.getGiaBan(); 

            modelSanPham.addRow(new Object[] {
                sp.getMaSanPham(),
                sp.getTenSanPham(),
                loaiText,
                sp.getSoDangKy(),
                duongDungText,   
                df.format(giaBanGoc), 
                sp.getKeBanSanPham(),
                trangThaiText
            });
        }
    }

    private void loadChiTietSanPham(SanPham sp) {
        modelLoSanPham.setRowCount(0);
        modelQuyCach.setRowCount(0);
        
        String maSP = sp.getMaSanPham();
        double giaBanGoc = sp.getGiaBan(); 

        // --- TAB 1: LÔ HÀNG ---
        List<LoSanPham> listLo = loSanPhamDao.layDanhSachLoTheoMaSanPham(maSP); 
        int sttLo = 1;
        if (listLo != null) {
            for (LoSanPham lo : listLo) {
                modelLoSanPham.addRow(new Object[]{
                    sttLo++,
                    lo.getMaLo(),
                    dtf.format(lo.getHanSuDung()),
                    lo.getSoLuongTon()
                });
            }
        }

        // --- TAB 2: QUY CÁCH ---
        List<QuyCachDongGoi> listQC = quyCachDao.layDanhSachQuyCachTheoSanPham(maSP); 
        int sttQC = 1;
        if (listQC != null) {
            for (QuyCachDongGoi qc : listQC) {
                String tenDVT = qc.getDonViTinh() != null ? qc.getDonViTinh().getTenDonViTinh() : "N/A";
                
                double giaBanQuyCach = giaBanGoc * qc.getHeSoQuyDoi() * (1 - qc.getTiLeGiam());
                
                String loaiDVT = qc.isDonViGoc() ? "Đơn vị gốc" : "Quy đổi";
                String tiLeGiamText = (int)(qc.getTiLeGiam() * 100) + "%";

                modelQuyCach.addRow(new Object[]{
                    sttQC++,
                    qc.getMaQuyCach(),
                    tenDVT,
                    qc.getHeSoQuyDoi(),
                    df.format(giaBanQuyCach),
                    tiLeGiamText,
                    loaiDVT
                });
            }
        }
    }
    /**
     * Xuất danh sách sản phẩm ra file Excel
     */
    private void xuatExcel() {
        // Tự động tìm kiếm trước khi xuất để chắc chắn xuất đúng tiêu chí
        xuLyTimKiem();
        if (modelSanPham.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
        fileChooser.setSelectedFile(new File("DanhSachSanPham_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getName().endsWith(".xlsx")) {
            fileToSave = new File(fileToSave.getAbsolutePath() + ".xlsx");
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            // Style cho tiêu đề
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Style cho dữ liệu
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // ===== SHEET 1: DANH SÁCH SẢN PHẨM =====
            Sheet sheetSP = workbook.createSheet("Danh sách sản phẩm");

            // Tạo header
            Row headerRow = sheetSP.createRow(0);
            String[] headers = {"Mã SP", "Tên sản phẩm", "Loại", "Số ĐK", "Đường dùng", "Giá Bán Gốc", "Vị trí", "Trạng thái"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Điền dữ liệu từ bảng
            for (int row = 0; row < modelSanPham.getRowCount(); row++) {
                Row dataRow = sheetSP.createRow(row + 1);
                for (int col = 0; col < modelSanPham.getColumnCount(); col++) {
                    Cell cell = dataRow.createCell(col);
                    Object value = modelSanPham.getValueAt(row, col);
                    cell.setCellValue(value != null ? value.toString() : "");
                    cell.setCellStyle(dataStyle);
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheetSP.autoSizeColumn(i);
            }

            // ===== SHEET 2: LÔ SẢN PHẨM =====
            Sheet sheetLo = workbook.createSheet("Lô sản phẩm");

            // Header lô sản phẩm
            Row headerRowLo = sheetLo.createRow(0);
            String[] headersLo = {"Mã SP", "Tên SP", "Mã lô", "Hạn sử dụng", "Số lượng tồn"};
            for (int i = 0; i < headersLo.length; i++) {
                Cell cell = headerRowLo.createCell(i);
                cell.setCellValue(headersLo[i]);
                cell.setCellStyle(headerStyle);
            }

            // Điền dữ liệu lô cho tất cả SP
            int loRowIdx = 1;
            for (int row = 0; row < modelSanPham.getRowCount(); row++) {
                String maSP = modelSanPham.getValueAt(row, 0).toString();
                String tenSP = modelSanPham.getValueAt(row, 1).toString();
                
                List<LoSanPham> listLo = loSanPhamDao.layDanhSachLoTheoMaSanPham(maSP);
                if (listLo != null && !listLo.isEmpty()) {
                    for (LoSanPham lo : listLo) {
                        Row dataRow = sheetLo.createRow(loRowIdx++);
                        dataRow.createCell(0).setCellValue(maSP);
                        dataRow.createCell(1).setCellValue(tenSP);
                        dataRow.createCell(2).setCellValue(lo.getMaLo());
                        dataRow.createCell(3).setCellValue(dtf.format(lo.getHanSuDung()));
                        dataRow.createCell(4).setCellValue(lo.getSoLuongTon());
                        
                        for (int col = 0; col < 5; col++) {
                            dataRow.getCell(col).setCellStyle(dataStyle);
                        }
                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headersLo.length; i++) {
                sheetLo.autoSizeColumn(i);
            }

            // ===== SHEET 3: QUY CÁCH ĐÓNG GÓI =====
            Sheet sheetQC = workbook.createSheet("Quy cách đóng gói");

            // Header quy cách
            Row headerRowQC = sheetQC.createRow(0);
            String[] headersQC = {"Mã SP", "Tên SP", "Mã quy cách", "Đơn vị tính", "Hệ số quy đổi", "Giá bán", "Tỷ lệ giảm", "Loại DVT"};
            for (int i = 0; i < headersQC.length; i++) {
                Cell cell = headerRowQC.createCell(i);
                cell.setCellValue(headersQC[i]);
                cell.setCellStyle(headerStyle);
            }

            // Điền dữ liệu quy cách cho tất cả SP
            int qcRowIdx = 1;
            for (int row = 0; row < modelSanPham.getRowCount(); row++) {
                String maSP = modelSanPham.getValueAt(row, 0).toString();
                String tenSP = modelSanPham.getValueAt(row, 1).toString();
                
                SanPham sp = dsSanPhamHienTai.stream()
                        .filter(s -> s.getMaSanPham().equals(maSP))
                        .findFirst()
                        .orElse(null);
                
                if (sp != null) {
                    double giaBanGoc = sp.getGiaBan();
                    List<QuyCachDongGoi> listQC = quyCachDao.layDanhSachQuyCachTheoSanPham(maSP);
                    
                    if (listQC != null && !listQC.isEmpty()) {
                        for (QuyCachDongGoi qc : listQC) {
                            Row dataRow = sheetQC.createRow(qcRowIdx++);
                            String tenDVT = qc.getDonViTinh() != null ? qc.getDonViTinh().getTenDonViTinh() : "N/A";
                            double giaBanQuyCach = giaBanGoc * qc.getHeSoQuyDoi() * (1 - qc.getTiLeGiam());
                            String loaiDVT = qc.isDonViGoc() ? "Đơn vị gốc" : "Quy đổi";
                            String tiLeGiamText = (int)(qc.getTiLeGiam() * 100) + "%";
                            
                            dataRow.createCell(0).setCellValue(maSP);
                            dataRow.createCell(1).setCellValue(tenSP);
                            dataRow.createCell(2).setCellValue(qc.getMaQuyCach());
                            dataRow.createCell(3).setCellValue(tenDVT);
                            dataRow.createCell(4).setCellValue(qc.getHeSoQuyDoi());
                            dataRow.createCell(5).setCellValue(df.format(giaBanQuyCach));
                            dataRow.createCell(6).setCellValue(tiLeGiamText);
                            dataRow.createCell(7).setCellValue(loaiDVT);
                            
                            for (int col = 0; col < 8; col++) {
                                dataRow.getCell(col).setCellStyle(dataStyle);
                            }
                        }
                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headersQC.length; i++) {
                sheetQC.autoSizeColumn(i);
            }

            // Ghi file
            try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
                workbook.write(fos);
            }

            JOptionPane.showMessageDialog(this, 
                "Xuất Excel thành công!\nFile: " + fileToSave.getAbsolutePath() + 
                "\n\nĐã xuất " + modelSanPham.getRowCount() + " sản phẩm kèm đầy đủ thông tin Lô và Quy cách.", 
                "Thành công", JOptionPane.INFORMATION_MESSAGE);

            // Mở file sau khi xuất
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(fileToSave);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi xuất file Excel:\n" + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    // ==============================================================================
    //                                  MAIN
    // ==============================================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tra cứu sản phẩm");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1500, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new TraCuuSanPham_GUI());
            frame.setVisible(true);
        });
    }
}