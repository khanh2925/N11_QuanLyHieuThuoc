package gui.tracuu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

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
        cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đang chạy", "Sắp chạy", "Đã kết thúc"});
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
        btnLamMoi.setBounds(1390, 22, 130, 50);
        btnLamMoi.setToolTipText("<html><b>Phím tắt:</b> F5<br>Làm mới toàn bộ dữ liệu và xóa bộ lọc</html>");
        pnHeader.add(btnLamMoi);
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
                if ("Đang áp dụng".equals(status)) {
                    lbl.setForeground(new Color(0x2E7D32));
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                } else if ("Ngừng hoạt động".equals(status) || "Hết hạn".equals(status) || "Đã kết thúc".equals(status)) {
                    lbl.setForeground(Color.RED);
                    lbl.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                } else {
                    lbl.setForeground(new Color(255, 140, 0));
                }
                return lbl;
            }
        });
        
        tblKhuyenMai.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if ("Hóa đơn".equals(value)) lbl.setForeground(new Color(0, 102, 204));
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
        tblKhuyenMai.addMouseListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnTim)) {
            taiDuLieuKhuyenMai();
        } else if (o.equals(btnLamMoi)) {
            xuLyLamMoi();
        }
    }

    /**
     * Xử lý làm mới form
     */
    private void xuLyLamMoi() {
        txtTimKiem.setText("");
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã KM, tên chương trình...");
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
                } else if (km.getHinhThuc() == HinhThucKM.TANG_THEM) {
                    hinhThucHienThi = "Tặng quà";
                }
            }

            // Lọc theo hình thức
            if (locHinhThuc.equals("Giảm tiền") && km.getHinhThuc() != HinhThucKM.GIAM_GIA_TIEN) continue;
            if (locHinhThuc.equals("Giảm %") && km.getHinhThuc() != HinhThucKM.GIAM_GIA_PHAN_TRAM) continue;
            if (locHinhThuc.equals("Tặng quà") && km.getHinhThuc() != HinhThucKM.TANG_THEM) continue;

            LocalDate now = LocalDate.now();
            String trangThaiHienThi;
            if (!km.isTrangThai()) {
                trangThaiHienThi = "Ngừng hoạt động";
            } else if (km.getSoLuongKhuyenMai() <= 0) {
                trangThaiHienThi = "Hết số lượng";
            } else if (now.isBefore(km.getNgayBatDau())) {
                trangThaiHienThi = "Sắp chạy";
            } else if (now.isAfter(km.getNgayKetThuc())) {
                trangThaiHienThi = "Đã kết thúc";
            } else {
                trangThaiHienThi = "Đang áp dụng";
            }

            if (locTrangThai.equals("Đang chạy") && !trangThaiHienThi.equals("Đang áp dụng")) continue;
            if (locTrangThai.equals("Sắp chạy") && !trangThaiHienThi.equals("Sắp chạy")) continue;
            if (locTrangThai.equals("Đã kết thúc") && !trangThaiHienThi.equals("Đã kết thúc")) continue;

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