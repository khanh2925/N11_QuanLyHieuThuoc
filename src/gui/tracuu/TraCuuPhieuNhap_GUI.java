package gui.tracuu;

import java.awt.*;
import java.awt.Color;
import java.awt.Font;
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
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import com.toedter.calendar.JDateChooser;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import component.button.PillButton;
import component.input.PlaceholderSupport;
import component.border.RoundedBorder;
import dao.PhieuNhap_DAO;
import entity.ChiTietPhieuNhap;
import entity.PhieuNhap;

public class TraCuuPhieuNhap_GUI extends JPanel implements ActionListener, MouseListener {

    private JPanel pnHeader;
    private JPanel pnCenter;
    private String hello;
    private JTable tblPhieuNhap;
    private DefaultTableModel modelPhieuNhap;
    private JFrame mainFrame;

    private JTable tblChiTiet;
    private DefaultTableModel modelChiTiet;

    private JTextField txtTimKiem;
    private JDateChooser dateTuNgay;
    private JDateChooser dateDenNgay;
    private PillButton btnTimKiem;
    private PillButton btnLamMoi;
    private PillButton btnXemHoaDon;

    private PhieuNhap_DAO phieuNhap_DAO;
    private final DecimalFormat df = new DecimalFormat("#,###đ");
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TraCuuPhieuNhap_GUI() {
        this(null);
    }

    public TraCuuPhieuNhap_GUI(JFrame mainFrame) {
        this.mainFrame = mainFrame;
        phieuNhap_DAO = new PhieuNhap_DAO();
        setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        taoPhanDau();
        add(pnHeader, BorderLayout.NORTH);

        taoPhanGiua();
        add(pnCenter, BorderLayout.CENTER);

        taiDuLieuPhieuNhap();
        dangKySuKien();
        setupKeyboardShortcuts();
        addFocusOnShow();
    }

    private void taoPhanDau() {
        pnHeader = new JPanel();
        pnHeader.setLayout(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94));
        pnHeader.setBackground(new Color(0xE3F2F5));

        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã PN, tên nhân viên, nhà cung cấp...");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        txtTimKiem.setBounds(25, 17, 480, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setToolTipText("<html><b>Phím tắt:</b> F1 hoặc Ctrl+F<br>Nhấn Enter để tìm kiếm</html>");
        pnHeader.add(txtTimKiem);

        JLabel lblTu = new JLabel("Từ ngày:");
        lblTu.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblTu.setBounds(530, 28, 80, 35);
        pnHeader.add(lblTu);

        dateTuNgay = new JDateChooser();
        dateTuNgay.setDateFormatString("dd/MM/yyyy");
        dateTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        dateTuNgay.setBounds(610, 28, 180, 38);
        dateTuNgay.setDate(java.sql.Date.valueOf(LocalDate.now().minusDays(30)));
        pnHeader.add(dateTuNgay);

        JLabel lblDen = new JLabel("Đến:");
        lblDen.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblDen.setBounds(830, 28, 50, 35);
        pnHeader.add(lblDen);

        dateDenNgay = new JDateChooser();
        dateDenNgay.setDateFormatString("dd/MM/yyyy");
        dateDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        dateDenNgay.setBounds(890, 28, 180, 38);
        dateDenNgay.setDate(new Date());
        pnHeader.add(dateDenNgay);

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
        btnTimKiem.setToolTipText("<html><b>Phím tắt:</b> Enter (khi ở ô tìm kiếm)<br>Tìm kiếm theo mã phiếu nhập, nhân viên, nhà cung cấp và bộ lọc ngày</html>");
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

        btnXemHoaDon = new PillButton(
                "<html>" +
                    "<center>" +
                        "XEM HÓA ĐƠN<br>" +
                        "<span style='font-size:10px; color:#888888;'>(F3)</span>" +
                    "</center>" +
                "</html>"
            );
        btnXemHoaDon.setBounds(1410, 22, 170, 50);
        btnXemHoaDon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnXemHoaDon.setToolTipText("<html><b>Phím tắt:</b> F3<br>Xem chi tiết hóa đơn nhập hàng đang chọn</html>");
        pnHeader.add(btnXemHoaDon);
    }

    private void taoPhanGiua() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.5);
        pnCenter.add(splitPane, BorderLayout.CENTER);

        String[] colPhieuNhap = { "STT", "Mã phiếu nhập", "Ngày lập", "Nhân viên", "Nhà cung cấp", "Tổng tiền" };
        modelPhieuNhap = new DefaultTableModel(colPhieuNhap, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblPhieuNhap = thietLapBang(modelPhieuNhap);

        // Tạo renderer theo chuẩn UX
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        // Áp dụng căn chỉnh cho bảng Phiếu Nhập
        tblPhieuNhap.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);  // STT - Giữa
        tblPhieuNhap.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);  // Mã phiếu nhập - Giữa
        tblPhieuNhap.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);  // Ngày lập - Giữa
        tblPhieuNhap.getColumnModel().getColumn(3).setCellRenderer(leftRenderer);    // Nhân viên - Trái
        tblPhieuNhap.getColumnModel().getColumn(4).setCellRenderer(leftRenderer);    // Nhà cung cấp - Trái
        tblPhieuNhap.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);   // Tổng tiền - Phải

        tblPhieuNhap.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblPhieuNhap.getColumnModel().getColumn(4).setPreferredWidth(250);

        JScrollPane scrollPN = new JScrollPane(tblPhieuNhap);
        scrollPN.setBorder(taoVienTieuDe("Danh sách phiếu nhập hàng"));
        splitPane.setTopComponent(scrollPN);

        String[] colChiTiet = { "STT", "Mã Lô", "Mã SP", "Tên sản phẩm", "ĐVT", "Số lượng", "Đơn giá nhập", "Thành tiền" };
        modelChiTiet = new DefaultTableModel(colChiTiet, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblChiTiet = thietLapBang(modelChiTiet);

        // Áp dụng căn chỉnh cho bảng Chi Tiết
        tblChiTiet.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);  // STT - Giữa
        tblChiTiet.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);  // Mã Lô - Giữa
        tblChiTiet.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);  // Mã SP - Giữa
        tblChiTiet.getColumnModel().getColumn(3).setCellRenderer(leftRenderer);    // Tên sản phẩm - Trái
        tblChiTiet.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);  // ĐVT - Giữa
        tblChiTiet.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);   // Số lượng - Phải
        tblChiTiet.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);   // Đơn giá nhập - Phải
        tblChiTiet.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);   // Thành tiền - Phải

        tblChiTiet.getColumnModel().getColumn(3).setPreferredWidth(250);

        JScrollPane scrollChiTiet = new JScrollPane(tblChiTiet);
        scrollChiTiet.setBorder(taoVienTieuDe("Chi tiết phiếu nhập"));
        splitPane.setBottomComponent(scrollChiTiet);
    }

    private JTable thietLapBang(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight(35);
        table.setSelectionBackground(new Color(0xC8E6C9));
        table.setGridColor(new Color(230, 230, 230));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(new Color(33, 150, 243));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 40));
        return table;
    }

    private TitledBorder taoVienTieuDe(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 18), Color.DARK_GRAY);
    }

    private void dangKySuKien() {
        btnTimKiem.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnXemHoaDon.addActionListener(this);
        txtTimKiem.addActionListener(this);
        tblPhieuNhap.addMouseListener(this);
    }

    /**
     * Thiết lập phím tắt cho màn hình Tra cứu Phiếu nhập
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

        // F3: Xem hóa đơn
        inputMap.put(KeyStroke.getKeyStroke("F3"), "xemHoaDon");
        actionMap.put("xemHoaDon", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tblPhieuNhap.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(TraCuuPhieuNhap_GUI.this,
                        "Vui lòng chọn một phiếu nhập để xem hóa đơn!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                String maPhieuNhap = tblPhieuNhap.getValueAt(selectedRow, 1).toString();
                PhieuNhap phieuNhap = phieuNhap_DAO.timPhieuNhapTheoMa(maPhieuNhap);
                if (phieuNhap != null) {
                    hienThiHoaDon(phieuNhap);
                }
            }
        });

        // Enter trên ô tìm kiếm
        txtTimKiem.addActionListener(ev -> xuLyTimKiem());
    }

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
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source.equals(btnTimKiem)) {
            xuLyTimKiem();
        } else if (source.equals(btnLamMoi)) {
            xuLyLamMoi();
        } else if (source.equals(btnXemHoaDon)) {
            int selectedRow = tblPhieuNhap.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn một phiếu nhập để xem hóa đơn!", 
                    "Thông báo", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String maPhieuNhap = tblPhieuNhap.getValueAt(selectedRow, 1).toString();
            PhieuNhap phieuNhap = phieuNhap_DAO.timPhieuNhapTheoMa(maPhieuNhap);
            if (phieuNhap != null) {
                hienThiHoaDon(phieuNhap);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource().equals(tblPhieuNhap)) {
            if (e.getClickCount() == 2) {
                // Double click: hiển thị form thông tin phiếu nhập
                int row = tblPhieuNhap.getSelectedRow();
                if (row != -1) {
                    String maPN = tblPhieuNhap.getValueAt(row, 1).toString();
                    PhieuNhap pn = phieuNhap_DAO.timPhieuNhapTheoMa(maPN);
                    if (pn != null) {
                        hienThiHoaDon(pn);
                    }
                }
            } else {
                // Single click: hiển thị chi tiết
                int row = tblPhieuNhap.getSelectedRow();
                if (row != -1) {
                    String maPN = tblPhieuNhap.getValueAt(row, 1).toString();
                    hienThiChiTietPhieuNhap(maPN);
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    private void taiDuLieuPhieuNhap() {
        modelPhieuNhap.setRowCount(0);
        List<PhieuNhap> listPN = phieuNhap_DAO.layDanhSachPhieuNhap();

        int stt = 1;
        for (PhieuNhap pn : listPN) {
            modelPhieuNhap.addRow(new Object[] {
                    stt++,
                    pn.getMaPhieuNhap(),
                    pn.getNgayNhap().format(fmt),
                    pn.getNhanVien().getTenNhanVien(),
                    pn.getNhaCungCap().getTenNhaCungCap(),
                    df.format(pn.getTongTien())
            });
        }
    }

    private void xuLyTimKiem() {
        String keyword = txtTimKiem.getText().trim();
        if (keyword.equals("Tìm theo mã PN, tên nhân viên, nhà cung cấp...(F1 / Ctrl+F)")) {
            keyword = "";
        }

        Date tuNgay = dateTuNgay.getDate();
        Date denNgay = dateDenNgay.getDate();

        if (tuNgay == null || denNgay == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ ngày bắt đầu và kết thúc!");
            return;
        }

        if (tuNgay.after(denNgay)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được lớn hơn ngày kết thúc!");
            return;
        }

        modelPhieuNhap.setRowCount(0);
        List<PhieuNhap> listPN = phieuNhap_DAO.timKiemPhieuNhap(keyword, tuNgay, denNgay);

        if (listPN.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu nhập nào phù hợp!");
            return;
        }

        int stt = 1;
        for (PhieuNhap pn : listPN) {
            modelPhieuNhap.addRow(new Object[] {
                    stt++,
                    pn.getMaPhieuNhap(),
                    pn.getNgayNhap().format(fmt),
                    pn.getNhanVien().getTenNhanVien(),
                    pn.getNhaCungCap().getTenNhaCungCap(),
                    df.format(pn.getTongTien())
            });
        }
    }

    private void xuLyLamMoi() {
        txtTimKiem.setText("");
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã PN, tên nhân viên, nhà cung cấp...(F1 / Ctrl+F)");
        dateTuNgay.setDate(java.sql.Date.valueOf(LocalDate.now().minusDays(30)));
        dateDenNgay.setDate(new Date());
        taiDuLieuPhieuNhap();
        modelChiTiet.setRowCount(0);
    }

    private void hienThiChiTietPhieuNhap(String maPN) {
        modelChiTiet.setRowCount(0);

        PhieuNhap pn = phieuNhap_DAO.timPhieuNhapTheoMa(maPN);

        if (pn != null && pn.getChiTietPhieuNhapList() != null) {
            int stt = 1;
            for (ChiTietPhieuNhap ct : pn.getChiTietPhieuNhapList()) {
                String tenSP = "Không xác định";
                String maSP = "";

                if (ct.getLoSanPham() != null && ct.getLoSanPham().getSanPham() != null) {
                    tenSP = ct.getLoSanPham().getSanPham().getTenSanPham();
                    maSP = ct.getLoSanPham().getSanPham().getMaSanPham();
                }

                String donVi = "";
                if (ct.getDonViTinh() != null) {
                    donVi = ct.getDonViTinh().getTenDonViTinh();
                }

                modelChiTiet.addRow(new Object[] {
                        stt++,
                        ct.getLoSanPham().getMaLo(),
                        maSP,
                        tenSP,
                        donVi,
                        ct.getSoLuongNhap(),
                        df.format(ct.getDonGiaNhap()),
                        df.format(ct.getThanhTien())
                });
            }
        }
    }

    /**
     * Xuất danh sách phiếu nhập ra file Excel
     * Tất cả chi tiết trong một sheet duy nhất
     */
    private void hienThiHoaDon(PhieuNhap phieuNhap) {
        JDialog dialog = new JDialog(mainFrame, "Hóa Đơn Nhập Hàng", true);
        dialog.setSize(650, 700);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.getContentPane().setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        mainPanel.setBackground(Color.WHITE);
        dialog.getContentPane().add(mainPanel, BorderLayout.CENTER);

        // ===== 1. NORTH: Tiêu đề =====
        JLabel lblTitle = new JLabel("HÓA ĐƠN NHẬP HÀNG", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.BLACK);
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // ===== 2. CENTER: Thông tin và Bảng =====
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // --- Thông tin Header ---
        JPanel pnHeader = new JPanel(new GridLayout(0, 2, 20, 8));
        pnHeader.setOpaque(false);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);

        pnHeader.add(taoNhanThuong("Mã hóa đơn nhập:", labelFont));
        pnHeader.add(taoNhanInDam(phieuNhap.getMaPhieuNhap(), labelFont));

        pnHeader.add(taoNhanThuong("Nhân viên:", labelFont));
        pnHeader.add(taoNhanInDam(phieuNhap.getNhanVien().getTenNhanVien(), labelFont));

        pnHeader.add(taoNhanThuong("Ngày lập phiếu:", labelFont));
        pnHeader.add(taoNhanInDam(phieuNhap.getNgayNhap().format(fmt), labelFont));

        pnHeader.add(taoNhanThuong("Nhà cung cấp:", labelFont));
        pnHeader.add(taoNhanInDam(phieuNhap.getNhaCungCap().getTenNhaCungCap(), labelFont));

        pnHeader.add(taoNhanThuong("Điện thoại:", labelFont));
        pnHeader.add(taoNhanInDam(phieuNhap.getNhaCungCap().getSoDienThoai(), labelFont));

        pnHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        centerPanel.add(pnHeader);

        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(taoDuongKeDut());
        centerPanel.add(Box.createVerticalStrut(10));

        // --- Tiêu đề Bảng ---
        JLabel lblChiTiet = new JLabel("Chi tiết sản phẩm nhập");
        lblChiTiet.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblChiTiet.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(lblChiTiet);
        centerPanel.add(Box.createVerticalStrut(5));

        // --- Bảng Chi Tiết ---
        String[] columns = { "Tên sản phẩm", "Đơn vị tính", "Số lô", "Số lượng", "Đơn giá", "Thành tiền" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa
            }
        };

        for (ChiTietPhieuNhap ct : phieuNhap.getChiTietPhieuNhapList()) {
            model.addRow(new Object[] {
                    ct.getLoSanPham().getSanPham().getTenSanPham(),
                    ct.getDonViTinh().getTenDonViTinh(),
                    ct.getLoSanPham().getMaLo(),
                    ct.getSoLuongNhap(),
                    df.format(ct.getDonGiaNhap()),
                    df.format(ct.getThanhTien())
            });
        }

        JTable table = new JTable(model);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Số lô
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // Số lượng
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer); // Đơn giá
        table.getColumnModel().getColumn(5).setCellRenderer(rightRenderer); // Thành tiền

        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.setRowHeight(35);

        JScrollPane scrollTable = new JScrollPane(table);
        centerPanel.add(scrollTable);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ===== 3. SOUTH: Tổng tiền và Nút Đóng =====
        JPanel pnFooter = new JPanel();
        pnFooter.setLayout(new BoxLayout(pnFooter, BoxLayout.Y_AXIS));
        pnFooter.setOpaque(false);

        pnFooter.add(taoDuongKeDut());
        pnFooter.add(Box.createVerticalStrut(10));

        JLabel lblTongCong = new JLabel(String.format("Tổng hóa đơn: %s", df.format(phieuNhap.getTongTien())));
        lblTongCong.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTongCong.setForeground(Color.BLACK);
        lblTongCong.setAlignmentX(Component.RIGHT_ALIGNMENT);
        pnFooter.add(lblTongCong);

        pnFooter.add(Box.createVerticalStrut(15));

        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnClose.addActionListener(e -> dialog.dispose());
        pnFooter.add(btnClose);

        mainPanel.add(pnFooter, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JLabel taoNhanThuong(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(new Color(100, 100, 100));
        return label;
    }

    private JLabel taoNhanInDam(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
        label.setForeground(Color.BLACK);
        return label;
    }

    private JSeparator taoDuongKeDut() {
        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return separator;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
            }
            JFrame frame = new JFrame("Tra cứu phiếu nhập");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new TraCuuPhieuNhap_GUI());
            frame.setVisible(true);
        });
    }
}