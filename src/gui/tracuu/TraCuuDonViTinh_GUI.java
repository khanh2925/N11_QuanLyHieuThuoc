package gui.tracuu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.ActionMap;
import javax.swing.KeyStroke;
import javax.swing.JComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import component.button.PillButton;
import component.input.PlaceholderSupport;
import component.border.RoundedBorder;
import dao.DonViTinh_DAO;
import dao.QuyCachDongGoi_DAO;
import entity.DonViTinh;
import entity.QuyCachDongGoi;

@SuppressWarnings("serial")
public class TraCuuDonViTinh_GUI extends JPanel implements ActionListener, MouseListener {

    private JPanel pnHeader;
    private JPanel pnCenter;

    // Bảng Master: Đơn vị tính
    private JTable tblDonViTinh;
    private DefaultTableModel modelDonViTinh;

    // Bảng Detail: Sản phẩm sử dụng đơn vị này
    private JTable tblSanPhamSuDung;
    private DefaultTableModel modelSanPhamSuDung;

    private JTextField txtTimKiem;
    private PillButton btnTim;
    private PillButton btnLamMoi;

    // DAO
    private DonViTinh_DAO donViTinhDAO;
    private QuyCachDongGoi_DAO quyCachDAO;

    // Cache dữ liệu để xử lý nhanh
    private List<DonViTinh> listDVT;
    private List<QuyCachDongGoi> listQuyCach;

    public TraCuuDonViTinh_GUI() {
        donViTinhDAO = new DonViTinh_DAO();
        quyCachDAO = new QuyCachDongGoi_DAO();
        
        setPreferredSize(new Dimension(1537, 850));
        initialize();
        setupKeyboardShortcuts();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 1. HEADER
        taoPhanDau();
        add(pnHeader, BorderLayout.NORTH);

        // 2. CENTER
        taoPhanGiua();
        add(pnCenter, BorderLayout.CENTER);

        // 3. DATA
        taiDuLieuLenBang();
        dangKySuKien();
    }

    // ==============================================================================
    //                              PHẦN GIAO DIỆN (VIEW)
    // ==============================================================================
    private void taoPhanDau() {
        pnHeader = new JPanel();
        pnHeader.setLayout(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94));
        pnHeader.setBackground(new Color(0xE3F2F5));

        // --- Ô TÌM KIẾM ---
        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm mã hoặc tên đơn vị tính... (F1 / Ctrl+F)");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimKiem.setBounds(25, 17, 480, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setToolTipText("<html><b>Phím tắt:</b> F1 hoặc Ctrl+F<br>Nhấn Enter để tìm kiếm</html>");
        pnHeader.add(txtTimKiem);

        // --- NÚT CHỨC NĂNG ---
        btnTim = new PillButton(
                "<html>" +
                    "<center>" +
                        "TÌM KIẾM<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Enter)</span>" +
                    "</center>" +
                "</html>"
            );
        btnTim.setBounds(550, 22, 140, 50);
        btnTim.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnTim.setToolTipText("<html><b>Phím tắt:</b> Enter (khi ở ô tìm kiếm)<br>Tìm kiếm theo mã, tên đơn vị tính</html>");
        pnHeader.add(btnTim);
        
        btnLamMoi = new PillButton(
                "<html>" +
                    "<center>" +
                        "LÀM MỚI<br>" +
                        "<span style='font-size:10px; color:#888888;'>(F5)</span>" +
                    "</center>" +
                "</html>"
            );
        btnLamMoi.setBounds(710, 22, 140, 50);
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnLamMoi.setToolTipText("<html><b>Phím tắt:</b> F5<br>Làm mới toàn bộ dữ liệu và xóa bộ lọc</html>");
        pnHeader.add(btnLamMoi);
    }

private void taoPhanGiua() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Chia đôi màn hình: Trên (Ds Đơn vị) - Dưới (Ds Thuốc dùng đơn vị đó)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.5);

        String[] colDVT = {"STT", "Mã Đơn Vị", "Tên Đơn Vị Tính", "Số lượng sản phẩm đang dùng"};
        modelDonViTinh = new DefaultTableModel(colDVT, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDonViTinh = thietLapBang(modelDonViTinh);
        
        // Căn giữa
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tblDonViTinh.getColumnCount(); i++) {
            tblDonViTinh.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JScrollPane scrollDVT = new JScrollPane(tblDonViTinh);
        scrollDVT.setBorder(taoVienTieuDe("Danh mục Đơn vị tính"));
        splitPane.setTopComponent(scrollDVT);

        // --- BOTTOM: SẢN PHẨM SỬ DỤNG (ĐÃ SỬA) ---
        // Không dùng JTabbedPane nữa, lấy trực tiếp Panel/ScrollPane
        JComponent pnlChiTiet = createTabSanPhamSuDung();
        
        // Đặt viền có tiêu đề giống bảng trên
        pnlChiTiet.setBorder(taoVienTieuDe("Sản phẩm sử dụng đơn vị này"));
        
        splitPane.setBottomComponent(pnlChiTiet);
        pnCenter.add(splitPane, BorderLayout.CENTER);
    }

    private JComponent createTabSanPhamSuDung() {
        String[] cols = {"STT", "Mã Sản Phẩm", "Tên Sản Phẩm", "Vai trò đơn vị", "Quy đổi"};
        modelSanPhamSuDung = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblSanPhamSuDung = thietLapBang(modelSanPhamSuDung);
        
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        
        tblSanPhamSuDung.getColumnModel().getColumn(0).setCellRenderer(center);
        tblSanPhamSuDung.getColumnModel().getColumn(1).setCellRenderer(center);
        // Tên sản phẩm để mặc định (Left)
        tblSanPhamSuDung.getColumnModel().getColumn(3).setCellRenderer(center);
        tblSanPhamSuDung.getColumnModel().getColumn(4).setCellRenderer(center);
        
        return new JScrollPane(tblSanPhamSuDung);
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
        
        return table;
    }

    private TitledBorder taoVienTieuDe(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), Color.DARK_GRAY
        );
    }

    // ==============================================================================
    //                              DATA & LOGIC (CONTROLLER)
    // ==============================================================================
    
    private void dangKySuKien() {
        btnTim.addActionListener(this);
        btnLamMoi.addActionListener(this);
        tblDonViTinh.addMouseListener(this);
    }

    /**
     * Thiết lập phím tắt cho màn hình Tra cứu Đơn vị tính
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

        // Enter: Tìm kiếm (từ bất kỳ đâu trong panel)
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "timKiemEnter");
        actionMap.put("timKiemEnter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyTimKiem();
            }
        });
    }

    /**
     * Tải dữ liệu mới nhất từ DB và hiển thị lên bảng
     */
    private void taiDuLieuLenBang() {
        modelDonViTinh.setRowCount(0);
        
        // Load dữ liệu từ DAO
        listDVT = donViTinhDAO.layTatCaDonViTinh();
        listQuyCach = quyCachDAO.layTatCaQuyCachDongGoi(); // Dùng để đếm số lượng thuốc dùng

        int stt = 1;
        for (DonViTinh dvt : listDVT) {
            // Tính số lượng thuốc (sản phẩm) đang sử dụng đơn vị này
            // Logic: Đếm số lượng QuyCachDongGoi có MaDonViTinh này
            long soLuongSuDung = listQuyCach.stream()
                    .filter(qc -> qc.getDonViTinh().getMaDonViTinh().equals(dvt.getMaDonViTinh()))
                    .map(qc -> qc.getSanPham().getMaSanPham()) // Map sang mã SP để đếm distinct (nếu cần)
                    .distinct()
                    .count();

            modelDonViTinh.addRow(new Object[]{
                stt++,
                dvt.getMaDonViTinh(),
                dvt.getTenDonViTinh(),
                soLuongSuDung
            });
        }
    }

    /**
     * Hiển thị danh sách sản phẩm khi chọn một đơn vị tính
     */
    private void hienThiSanPhamTheoDonVi(String maDVT) {
        modelSanPhamSuDung.setRowCount(0);
        
        // Lọc danh sách quy cách có mã đơn vị tính tương ứng
        List<QuyCachDongGoi> listLoc = listQuyCach.stream()
                .filter(qc -> qc.getDonViTinh().getMaDonViTinh().equals(maDVT))
                .collect(Collectors.toList());

        int stt = 1;
        for (QuyCachDongGoi qc : listLoc) {
            String vaiTro = qc.isDonViGoc() ? "Đơn vị gốc" : "Đơn vị quy đổi";
            String quyDoi = "1";

            if (!qc.isDonViGoc()) {
                // Nếu là đơn vị quy đổi, tìm đơn vị gốc của sản phẩm đó để hiển thị (VD: 10 Viên)
                QuyCachDongGoi qcBase = listQuyCach.stream()
                        .filter(q -> q.getSanPham().getMaSanPham().equals(qc.getSanPham().getMaSanPham()) && q.isDonViGoc())
                        .findFirst()
                        .orElse(null);
                
                String tenDonViGoc = (qcBase != null) ? qcBase.getDonViTinh().getTenDonViTinh() : "Đơn vị gốc";
                quyDoi = qc.getHeSoQuyDoi() + " " + tenDonViGoc;
            }

            modelSanPhamSuDung.addRow(new Object[]{
                stt++,
                qc.getSanPham().getMaSanPham(),
                qc.getSanPham().getTenSanPham(),
                vaiTro,
                quyDoi
            });
        }
    }

    private void xuLyTimKiem() {
        String tuKhoa = txtTimKiem.getText().trim().toLowerCase();
        if (tuKhoa.isEmpty() || tuKhoa.contains("tìm kiếm")) {
            taiDuLieuLenBang();
            return;
        }

        modelDonViTinh.setRowCount(0);
        int stt = 1;
        for (DonViTinh dvt : listDVT) {
            boolean matchMa = dvt.getMaDonViTinh().toLowerCase().contains(tuKhoa);
            boolean matchTen = dvt.getTenDonViTinh().toLowerCase().contains(tuKhoa);

            if (matchMa || matchTen) {
                long soLuongSuDung = listQuyCach.stream()
                        .filter(qc -> qc.getDonViTinh().getMaDonViTinh().equals(dvt.getMaDonViTinh()))
                        .map(qc -> qc.getSanPham().getMaSanPham())
                        .distinct()
                        .count();

                modelDonViTinh.addRow(new Object[]{
                    stt++,
                    dvt.getMaDonViTinh(),
                    dvt.getTenDonViTinh(),
                    soLuongSuDung
                });
            }
        }
    }

    private void xuLyLamMoi() {
        txtTimKiem.setText("");
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm mã hoặc tên đơn vị tính...");
        taiDuLieuLenBang();
        modelSanPhamSuDung.setRowCount(0);
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
        txtTimKiem.addActionListener(ev -> xuLyTimKiem());
    }

    // ==============================================================================
    //                              EVENT HANDLERS
    // ==============================================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnTim)) {
            xuLyTimKiem();
        } else if (o.equals(btnLamMoi)) {
            xuLyLamMoi();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource().equals(tblDonViTinh)) {
            int row = tblDonViTinh.getSelectedRow();
            if (row != -1) {
                String maDVT = tblDonViTinh.getValueAt(row, 1).toString();
                hienThiSanPhamTheoDonVi(maDVT);
            }
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

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
            frame.setContentPane(new TraCuuDonViTinh_GUI());
            frame.setVisible(true);
        });
    }
}