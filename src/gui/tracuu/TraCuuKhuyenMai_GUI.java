package gui.tracuu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import component.button.PillButton;
import component.input.PlaceholderSupport;
import component.border.RoundedBorder;
import dao.ChiTietKhuyenMaiSanPham_DAO;
import dao.HoaDon_DAO;
import dao.KhuyenMai_DAO;
import entity.ChiTietHoaDon;
import entity.ChiTietKhuyenMaiSanPham;
import entity.HoaDon;
import entity.KhuyenMai;
import enums.HinhThucKM;

public class TraCuuKhuyenMai_GUI extends JPanel implements ActionListener, MouseListener {

    private JPanel pnHeader;
    private JPanel pnCenter;

    private JTable tblKhuyenMai;
    private DefaultTableModel modelKhuyenMai;

    private JTabbedPane tabChiTiet;

    private JTable tblSanPhamApDung;
    private DefaultTableModel modelSanPhamApDung;

    private JTable tblLichSuApDung;
    private DefaultTableModel modelLichSuApDung;

    private JTextField txtTimKiem;
    private JComboBox<String> cbLoaiKM;
    private JComboBox<String> cbHinhThuc;
    private JComboBox<String> cbTrangThai;
    private PillButton btnTim;
    private PillButton btnLamMoi;
    private PillButton btnXuatExcel;

    private KhuyenMai_DAO khuyenMaiDAO;
    private ChiTietKhuyenMaiSanPham_DAO ctkmDAO;
    private HoaDon_DAO hoaDonDAO;

    private final DecimalFormat df = new DecimalFormat("#,###");
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TraCuuKhuyenMai_GUI() {
        khuyenMaiDAO = new KhuyenMai_DAO();
        ctkmDAO = new ChiTietKhuyenMaiSanPham_DAO();
        hoaDonDAO = new HoaDon_DAO();

        setPreferredSize(new Dimension(1537, 850));
        initialize();
        setupKeyboardShortcuts();
        addFocusOnShow(); // Tự động focus ô tìm kiếm khi hiển thị
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        taoPhanDau();
        add(pnHeader, BorderLayout.NORTH);

        taoPhanGiua();
        add(pnCenter, BorderLayout.CENTER);

        taiDuLieuKhuyenMai();
        dangKySuKien();
    }

    private void taoPhanDau() {
        pnHeader = new JPanel();
        pnHeader.setLayout(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94));
        pnHeader.setBackground(new Color(0xE3F2F5));

        // Ô tìm kiếm
        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã KM, tên chương trình... (F1 / Ctrl+F)");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        txtTimKiem.setBounds(25, 17, 480, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setToolTipText("<html><b>Phím tắt:</b> F1 hoặc Ctrl+F<br>Nhấn Enter để tìm kiếm</html>");
        pnHeader.add(txtTimKiem);

        // Filter: Loại KM
        addFilterLabel("Loại KM:", 525, 28, 70, 35);
        cbLoaiKM = new JComboBox<>(new String[]{"Tất cả", "Theo hóa đơn", "Theo sản phẩm"});
        setupCombo(cbLoaiKM, 605, 28, 140, 35);

        // Filter: Hình thức
        addFilterLabel("Hình thức:", 755, 28, 90, 35);
        cbHinhThuc = new JComboBox<>(new String[]{"Tất cả", "Giảm tiền", "Giảm %", "Tặng quà"});
        setupCombo(cbHinhThuc, 845, 28, 120, 35);

        // Filter: Trạng thái
        addFilterLabel("Trạng thái:", 975, 28, 90, 35);
        cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Hoạt động", "Không hoạt động"});
        setupCombo(cbTrangThai, 1075, 28, 150, 35);

        // Nút Tìm kiếm
        btnTim = new PillButton(
                "<html>" +
                        "<center>" +
                        "TÌM KIẾM<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Enter)</span>" +
                        "</center>" +
                        "</html>");
        btnTim.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnTim.setBounds(1250, 22, 130, 50);
        btnTim.setToolTipText(
                "<html><b>Phím tắt:</b> Enter (khi ở ô tìm kiếm)<br>Tìm kiếm theo mã, tên và bộ lọc</html>");
        pnHeader.add(btnTim);

        // Nút Làm mới
        btnLamMoi = new PillButton(
                "<html>" +
                        "<center>" +
                        "LÀM MỚI<br>" +
                        "<span style='font-size:10px; color:#888888;'>(F5)</span>" +
                        "</center>" +
                        "</html>");
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnLamMoi.setBounds(1250, 22, 130, 50);
        btnLamMoi.setToolTipText("<html><b>Phím tắt:</b> F5<br>Làm mới toàn bộ dữ liệu và xóa bộ lọc</html>");
        pnHeader.add(btnLamMoi);

        // Nút Xuất Excel
        btnXuatExcel = new PillButton(
                "<html>" +
                        "<center>" +
                        "XUẤT EXCEL<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Ctrl+E)</span>" +
                        "</center>" +
                        "</html>");
        btnXuatExcel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnXuatExcel.setBounds(1390, 22, 150, 50);
        btnXuatExcel.setToolTipText("<html><b>Phím tắt:</b> Ctrl+E<br>Xuất dữ liệu khuyến mãi ra file Excel</html>");
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

    private void taoPhanGiua() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.5);

        String[] colKM = {
            "Mã KM", "Tên chương trình", "Loại KM", "Hình thức", 
            "Giá trị", "Ngày bắt đầu", "Ngày kết thúc", "SL còn", "Trạng thái"
        };
        modelKhuyenMai = new DefaultTableModel(colKM, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblKhuyenMai = thietLapBang(modelKhuyenMai);

        tblKhuyenMai.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                String status = String.valueOf(value);
                if ("Hoạt động".equals(status)) {
                    lbl.setForeground(new Color(0, 128, 0)); // Xanh lá
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 16)); // In đậm
                } else {
                    lbl.setForeground(Color.RED); // Đỏ
                    lbl.setFont(new Font("Segoe UI", Font.ITALIC, 16)); // In nghiêng
                }
                return lbl;
            }
        });
        
        tblKhuyenMai.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
                if ("Hóa đơn".equals(value)) {
                    lbl.setForeground(new Color(0, 102, 204)); // Xanh dương
                } else if ("Sản phẩm".equals(value)) {
                    lbl.setForeground(new Color(255, 140, 0)); // Cam
                }
                return lbl;
            }
        });

        JScrollPane scrollKM = new JScrollPane(tblKhuyenMai);
        scrollKM.setBorder(taoVienTieuDe("Danh sách chương trình khuyến mãi"));
        splitPane.setTopComponent(scrollKM);

        tabChiTiet = new JTabbedPane();
        tabChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        tabChiTiet.addTab("Sản phẩm áp dụng", taoTabSanPhamApDung());
        tabChiTiet.addTab("Lịch sử áp dụng (Đơn hàng)", taoTabLichSu());

        splitPane.setBottomComponent(tabChiTiet);
        pnCenter.add(splitPane, BorderLayout.CENTER);
    }

    private JComponent taoTabSanPhamApDung() {
        String[] cols = {"STT", "Mã SP", "Tên sản phẩm", "Đơn vị tính", "Giá gốc", "Giá sau giảm"};
        modelSanPhamApDung = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblSanPhamApDung = thietLapBang(modelSanPhamApDung);
        
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        tblSanPhamApDung.getColumnModel().getColumn(4).setCellRenderer(right);
        tblSanPhamApDung.getColumnModel().getColumn(5).setCellRenderer(right);
        
        return new JScrollPane(tblSanPhamApDung);
    }

    private JComponent taoTabLichSu() {
        String[] cols = {"STT", "Mã Hóa Đơn", "Ngày lập", "Khách hàng", "Tổng tiền HĐ", "Số tiền được giảm"};
        modelLichSuApDung = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLichSuApDung = thietLapBang(modelLichSuApDung);
        
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        tblLichSuApDung.getColumnModel().getColumn(4).setCellRenderer(right);
        tblLichSuApDung.getColumnModel().getColumn(5).setCellRenderer(right);

        return new JScrollPane(tblLichSuApDung);
    }

    private JTable thietLapBang(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(0xC8E6C9));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(new Color(33, 150, 243));
        header.setForeground(Color.WHITE);
        
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i=0; i<table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        
        return table;
    }

    private TitledBorder taoVienTieuDe(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), Color.DARK_GRAY
        );
    }

    private void dangKySuKien() {
        btnTim.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnXuatExcel.addActionListener(this);
        tblKhuyenMai.addMouseListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnTim)) {
            taiDuLieuKhuyenMai();
        } else if (o.equals(btnLamMoi)) {
            xuLyLamMoi();
        } else if (o.equals(btnXuatExcel)) {
            xuatExcelDayDu();
        }
    }

    /**
     * Xử lý làm mới form
     */
    private void xuLyLamMoi() {
        txtTimKiem.setText("");
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã KM, tên chương trình... (F1 / Ctrl+F)");
        cbLoaiKM.setSelectedIndex(0);
        cbHinhThuc.setSelectedIndex(0);
        cbTrangThai.setSelectedIndex(0);
        taiDuLieuKhuyenMai();
        modelSanPhamApDung.setRowCount(0);
        modelLichSuApDung.setRowCount(0);
    }

    /**
     * Thiết lập phím tắt cho giao diện
     */
    private void setupKeyboardShortcuts() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        // F1: Focus tìm kiếm
        inputMap.put(KeyStroke.getKeyStroke("F1"), "focusTimKiem");
        actionMap.put("focusTimKiem", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtTimKiem.requestFocus();
                txtTimKiem.selectAll();
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
                txtTimKiem.requestFocus();
                txtTimKiem.selectAll();
            }
        });

        // Enter trên ô tìm kiếm
        txtTimKiem.addActionListener(ev -> taiDuLieuKhuyenMai());

        // Ctrl+E: Xuất Excel
        inputMap.put(KeyStroke.getKeyStroke("control E"), "xuatExcel");
        actionMap.put("xuatExcel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuatExcelDayDu();
            }
        });
    }

    /**
     * Tự động focus vào ô tìm kiếm khi panel được hiển thị
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

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource().equals(tblKhuyenMai)) {
            int row = tblKhuyenMai.getSelectedRow();
            if (row != -1) {
                String maKM = tblKhuyenMai.getValueAt(row, 0).toString();
                String loaiKM = tblKhuyenMai.getValueAt(row, 2).toString();
                
                double giaTri = 0;
                String giaTriStr = tblKhuyenMai.getValueAt(row, 4).toString();
                String hinhThucStr = tblKhuyenMai.getValueAt(row, 3).toString();
                
                try {
                    giaTriStr = giaTriStr.replace(",", "").replace("%", "").trim();
                    giaTri = Double.parseDouble(giaTriStr);
                } catch (Exception ex) {}

                hienThiChiTietKhuyenMai(maKM, loaiKM, hinhThucStr, giaTri);
            }
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    private void taiDuLieuKhuyenMai() {
        modelKhuyenMai.setRowCount(0);
        List<KhuyenMai> listKM = khuyenMaiDAO.layTatCaKhuyenMai();
        
        String tuKhoa = txtTimKiem.getText().trim().toLowerCase();
        if (tuKhoa.contains("tìm theo mã")) tuKhoa = "";

        String locLoai = cbLoaiKM.getSelectedItem().toString();
        String locHinhThuc = cbHinhThuc.getSelectedItem().toString();
        String locTrangThai = cbTrangThai.getSelectedItem().toString();

        for (KhuyenMai km : listKM) {
            if (!tuKhoa.isEmpty()) {
                boolean matchMa = km.getMaKM().toLowerCase().contains(tuKhoa);
                boolean matchTen = km.getTenKM().toLowerCase().contains(tuKhoa);
                if (!matchMa && !matchTen) continue;
            }

            if (locLoai.equals("Theo hóa đơn") && !km.isKhuyenMaiHoaDon()) continue;
            if (locLoai.equals("Theo sản phẩm") && km.isKhuyenMaiHoaDon()) continue;

            // Xác định hình thức khuyến mãi
            String hinhThucHienThi = "Không xác định";
            if (km.getHinhThuc() != null) {
                if (km.getHinhThuc() == HinhThucKM.GIAM_GIA_PHAN_TRAM) {
                    hinhThucHienThi = "Giảm %";
                } else if (km.getHinhThuc() == HinhThucKM.GIAM_GIA_TIEN) {
                    hinhThucHienThi = "Giảm tiền";
                } 
            }

            // Lọc theo hình thức
            if (locHinhThuc.equals("Giảm tiền") && km.getHinhThuc() != HinhThucKM.GIAM_GIA_TIEN) continue;
            if (locHinhThuc.equals("Giảm %") && km.getHinhThuc() != HinhThucKM.GIAM_GIA_PHAN_TRAM) continue;

            LocalDate now = LocalDate.now();
            String trangThaiHienThi;
            // Đơn giản hóa: chỉ còn 2 trạng thái Hoạt động/Không hoạt động
            if (km.isTrangThai() && km.getSoLuongKhuyenMai() > 0 && 
                !now.isBefore(km.getNgayBatDau()) && !now.isAfter(km.getNgayKetThuc())) {
                trangThaiHienThi = "Hoạt động";
            } else {
                trangThaiHienThi = "Không hoạt động";
            }

            if (locTrangThai.equals("Hoạt động") && !trangThaiHienThi.equals("Hoạt động")) continue;
            if (locTrangThai.equals("Không hoạt động") && !trangThaiHienThi.equals("Không hoạt động")) continue;

            String giaTriHienThi = "";
            if (km.getHinhThuc() == HinhThucKM.GIAM_GIA_PHAN_TRAM) {
                giaTriHienThi = df.format(km.getGiaTri()) + "%";
            } else {
                giaTriHienThi = df.format(km.getGiaTri());
            }

            modelKhuyenMai.addRow(new Object[]{
                km.getMaKM(),
                km.getTenKM(),
                km.isKhuyenMaiHoaDon() ? "Hóa đơn" : "Sản phẩm",
                hinhThucHienThi,
                giaTriHienThi,
                km.getNgayBatDau().format(fmt),
                km.getNgayKetThuc().format(fmt),
                km.getSoLuongKhuyenMai(),
                trangThaiHienThi
            });
        }
    }

    private void hienThiChiTietKhuyenMai(String maKM, String loaiKM, String hinhThuc, double giaTri) {
        modelSanPhamApDung.setRowCount(0);
        modelLichSuApDung.setRowCount(0);

        if ("Hóa đơn".equals(loaiKM)) {
            modelSanPhamApDung.addRow(new Object[]{"-", "Toàn bộ cửa hàng", "Áp dụng trên tổng tiền hóa đơn", "-", "-", "-"});
        } else {
            List<ChiTietKhuyenMaiSanPham> listCT = ctkmDAO.layChiTietKhuyenMaiTheoMaCoJoin(maKM);
            int stt = 1;
            for (ChiTietKhuyenMaiSanPham ct : listCT) {
                double giaGoc = ct.getSanPham().getGiaNhap() * 1.3;
                
                double giaSauGiam = giaGoc;
                if (hinhThuc.contains("%")) {
                    giaSauGiam = giaGoc * (1 - giaTri / 100);
                } else if (hinhThuc.toLowerCase().contains("tiền")) {
                    giaSauGiam = giaGoc - giaTri;
                }

                String donViTinh = "Hộp"; 

                modelSanPhamApDung.addRow(new Object[]{
                    stt++,
                    ct.getSanPham().getMaSanPham(),
                    ct.getSanPham().getTenSanPham(),
                    donViTinh, 
                    df.format(giaGoc),
                    df.format(giaSauGiam)
                });
            }
        }

        List<HoaDon> listHD = hoaDonDAO.layTatCaHoaDon();
        int sttHD = 1;
        for (HoaDon hd : listHD) {
            boolean found = false;
            double tienGiam = 0;

            if ("Hóa đơn".equals(loaiKM)) {
                if (hd.getKhuyenMai() != null && hd.getKhuyenMai().getMaKM().equals(maKM)) {
                    found = true;
                    tienGiam = hd.getSoTienGiamKhuyenMai();
                }
            } else {
                for (ChiTietHoaDon cthd : hd.getDanhSachChiTiet()) {
                    if (cthd.getKhuyenMai() != null && cthd.getKhuyenMai().getMaKM().equals(maKM)) {
                        found = true;
                        double thanhTienGoc = cthd.getSoLuong() * cthd.getGiaBan();
                        double thanhTienThuc = cthd.getThanhTien();
                        tienGiam += (thanhTienGoc - thanhTienThuc);
                    }
                }
            }

            if (found) {
                modelLichSuApDung.addRow(new Object[]{
                    sttHD++,
                    hd.getMaHoaDon(),
                    hd.getNgayLap().format(fmt),
                    hd.getKhachHang() != null ? hd.getKhachHang().getTenKhachHang() : "Khách vãng lai",
                    df.format(hd.getTongThanhToan()),
                    df.format(tienGiam)
                });
            }
        }
    }

    /**
     * Xuất dữ liệu khuyến mãi ra file Excel đầy đủ (Danh sách + Sản phẩm áp dụng + Lịch sử áp dụng)
     */
    private void xuatExcelDayDu() {
        if (modelKhuyenMai.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy danh sách dòng được chọn
        int[] selectedRows = tblKhuyenMai.getSelectedRows();
        int[] rowsToExport;
        String thongTinXuat;
        
        if (selectedRows.length > 0) {
            // Có chọn dòng → xuất các dòng đã chọn
            rowsToExport = selectedRows;
            thongTinXuat = "Số lượng: " + selectedRows.length + " chương trình khuyến mãi được chọn";
        } else {
            // Không chọn dòng nào → xuất toàn bộ
            rowsToExport = new int[modelKhuyenMai.getRowCount()];
            for (int i = 0; i < rowsToExport.length; i++) {
                rowsToExport[i] = i;
            }
            thongTinXuat = "Tổng số: " + rowsToExport.length + " chương trình khuyến mãi";
        }

        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
            fileChooser.setSelectedFile(new File("DanhSachKhuyenMai.xlsx"));
            fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().endsWith(".xlsx")) {
                    file = new File(file.getAbsolutePath() + ".xlsx");
                }

                XSSFWorkbook workbook = new XSSFWorkbook();

                // Header style
                CellStyle headerStyle = workbook.createCellStyle();
                XSSFFont headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                // Tiêu đề style
                CellStyle titleStyle = workbook.createCellStyle();
                XSSFFont titleFont = workbook.createFont();
                titleFont.setBold(true);
                titleFont.setFontHeightInPoints((short) 16);
                titleStyle.setFont(titleFont);

                // Header style cho chi tiết (màu xanh đậm)
                CellStyle headerStyleDetail = workbook.createCellStyle();
                XSSFFont headerFontDetail = workbook.createFont();
                headerFontDetail.setBold(true);
                headerFontDetail.setColor(IndexedColors.WHITE.getIndex());
                headerStyleDetail.setFont(headerFontDetail);
                headerStyleDetail.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
                headerStyleDetail.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyleDetail.setAlignment(HorizontalAlignment.CENTER);

                // ===== SHEET 1: DANH SÁCH KHUYẾN MÃI =====
                Sheet sheetKM = workbook.createSheet("Danh Sách Khuyến Mãi");

                // Tiêu đề
                Row titleRow = sheetKM.createRow(0);
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue("DANH SÁCH CHƯƠNG TRÌNH KHUYẾN MÃI");
                titleCell.setCellStyle(titleStyle);

                // Thông tin ngày xuất và số lượng
                Row periodRow = sheetKM.createRow(1);
                periodRow.createCell(0).setCellValue(
                        "Ngày xuất: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                
                Row infoRow = sheetKM.createRow(2);
                infoRow.createCell(0).setCellValue(thongTinXuat);

                // Header row
                Row headerRow = sheetKM.createRow(4);
                for (int i = 0; i < modelKhuyenMai.getColumnCount(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(modelKhuyenMai.getColumnName(i));
                    cell.setCellStyle(headerStyle);
                }

                // Data rows - chỉ xuất các dòng được chọn hoặc toàn bộ
                int excelRowIndex = 5;
                for (int tableRow : rowsToExport) {
                    Row dataRow = sheetKM.createRow(excelRowIndex++);
                    for (int col = 0; col < modelKhuyenMai.getColumnCount(); col++) {
                        Object value = modelKhuyenMai.getValueAt(tableRow, col);
                        dataRow.createCell(col).setCellValue(value != null ? value.toString() : "");
                    }
                }

                // Auto-size columns
                for (int i = 0; i < modelKhuyenMai.getColumnCount(); i++) {
                    sheetKM.autoSizeColumn(i);
                }

                // ===== SHEET 2: SẢN PHẨM ÁP DỤNG (nếu có dữ liệu) =====
                if (modelSanPhamApDung.getRowCount() > 0) {
                    Sheet sheetSP = workbook.createSheet("Sản Phẩm Áp Dụng");

                    // Tiêu đề
                    Row titleRowSP = sheetSP.createRow(0);
                    Cell titleCellSP = titleRowSP.createCell(0);
                    titleCellSP.setCellValue("DANH SÁCH SẢN PHẨM ÁP DỤNG KHUYẾN MÃI");
                    titleCellSP.setCellStyle(titleStyle);

                    // Ngày xuất
                    Row dateRowSP = sheetSP.createRow(1);
                    dateRowSP.createCell(0).setCellValue(
                            "Ngày xuất: " + LocalDate.now().format(fmt));
                    
                    Row countRowSP = sheetSP.createRow(2);
                    countRowSP.createCell(0).setCellValue("Số lượng: " + modelSanPhamApDung.getRowCount() + " sản phẩm");

                    // Header
                    Row headerRowSP = sheetSP.createRow(4);
                    for (int i = 0; i < modelSanPhamApDung.getColumnCount(); i++) {
                        Cell cell = headerRowSP.createCell(i);
                        cell.setCellValue(modelSanPhamApDung.getColumnName(i));
                        cell.setCellStyle(headerStyleDetail);
                    }

                    // Data
                    for (int row = 0; row < modelSanPhamApDung.getRowCount(); row++) {
                        Row dataRow = sheetSP.createRow(row + 5);
                        for (int col = 0; col < modelSanPhamApDung.getColumnCount(); col++) {
                            Object value = modelSanPhamApDung.getValueAt(row, col);
                            dataRow.createCell(col).setCellValue(value != null ? value.toString() : "");
                        }
                    }

                    // Auto-size
                    for (int i = 0; i < modelSanPhamApDung.getColumnCount(); i++) {
                        sheetSP.autoSizeColumn(i);
                    }
                }

                // ===== SHEET 3: LỊCH SỬ ÁP DỤNG (nếu có dữ liệu) =====
                if (modelLichSuApDung.getRowCount() > 0) {
                    Sheet sheetLS = workbook.createSheet("Lịch Sử Áp Dụng");

                    // Tiêu đề
                    Row titleRowLS = sheetLS.createRow(0);
                    Cell titleCellLS = titleRowLS.createCell(0);
                    titleCellLS.setCellValue("LỊCH SỬ ÁP DỤNG KHUYẾN MÃI");
                    titleCellLS.setCellStyle(titleStyle);

                    // Ngày xuất
                    Row dateRowLS = sheetLS.createRow(1);
                    dateRowLS.createCell(0).setCellValue(
                            "Ngày xuất: " + LocalDate.now().format(fmt));
                    
                    Row countRowLS = sheetLS.createRow(2);
                    countRowLS.createCell(0).setCellValue("Số lượng: " + modelLichSuApDung.getRowCount() + " hóa đơn");

                    // Header
                    Row headerRowLS = sheetLS.createRow(4);
                    for (int i = 0; i < modelLichSuApDung.getColumnCount(); i++) {
                        Cell cell = headerRowLS.createCell(i);
                        cell.setCellValue(modelLichSuApDung.getColumnName(i));
                        cell.setCellStyle(headerStyleDetail);
                    }

                    // Data
                    for (int row = 0; row < modelLichSuApDung.getRowCount(); row++) {
                        Row dataRow = sheetLS.createRow(row + 5);
                        for (int col = 0; col < modelLichSuApDung.getColumnCount(); col++) {
                            Object value = modelLichSuApDung.getValueAt(row, col);
                            dataRow.createCell(col).setCellValue(value != null ? value.toString() : "");
                        }
                    }

                    // Auto-size
                    for (int i = 0; i < modelLichSuApDung.getColumnCount(); i++) {
                        sheetLS.autoSizeColumn(i);
                    }
                }

                // Write file
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                }
                workbook.close();

                // Thống kê số sheet
                int totalSheets = 1 + (modelSanPhamApDung.getRowCount() > 0 ? 1 : 0) + 
                                  (modelLichSuApDung.getRowCount() > 0 ? 1 : 0);

                JOptionPane.showMessageDialog(this,
                        "Xuất Excel thành công!\n" + thongTinXuat + 
                        "\nSố sheet: " + totalSheets +
                        "\nFile: " + file.getAbsolutePath(),
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý khuyến mãi");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1450, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new TraCuuKhuyenMai_GUI());
            frame.setVisible(true);
        });
    }
}