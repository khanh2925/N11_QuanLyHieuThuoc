/**
 * @author Quốc Khánh cute
 * @version 2.0
 * @since Oct 19, 2025
 *

 */
package gui.tracuu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.toedter.calendar.JDateChooser;

import component.border.RoundedBorder;
import component.button.PillButton;
import component.input.PlaceholderSupport;
import dao.ChiTietPhieuHuy_DAO;
import dao.PhieuHuy_DAO;
import entity.ChiTietPhieuHuy;
import entity.PhieuHuy;

@SuppressWarnings("serial")
public class TraCuuPhieuHuy_GUI extends JPanel implements ActionListener {

    private JPanel pnHeader;
    private JPanel pnCenter;

    // Bảng Phiếu Hủy (Trên)
    private JTable tblPhieuHuy;
    private DefaultTableModel modelPhieuHuy;

    // Bảng Chi Tiết Phiếu Hủy (Dưới)
    private JTable tblChiTiet;
    private DefaultTableModel modelChiTiet;

    // Các component lọc
    private JTextField txtTimKiem;
    private JDateChooser dateTuNgay;
    private JDateChooser dateDenNgay;
    private JComboBox<String> cbTrangThai;

    // DAO
    private PhieuHuy_DAO ph_dao;
    private ChiTietPhieuHuy_DAO ctph_dao;

    // DATA
    private List<PhieuHuy> allPhieuHuy = new ArrayList<>();
    private List<ChiTietPhieuHuy> dsCTPH;

    private PillButton btnLamMoi, btnTim;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DecimalFormat df = new DecimalFormat("#,###đ");

    public TraCuuPhieuHuy_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        ph_dao = new PhieuHuy_DAO();
        ctph_dao = new ChiTietPhieuHuy_DAO();

        initialize();
        addEvents();
        initData();

        //  Tự động reload mỗi khi panel được hiển thị
        addHierarchyListener(new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                    if (isShowing()) {
                        xuLyLamMoi(); // reset filter + load lại tất cả
                    }
                }
            }
        });
    }

    // ==============================================================================
    // KHỞI TẠO LAYOUT
    // ==============================================================================
    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // HEADER
        taoPhanHeader();
        add(pnHeader, BorderLayout.NORTH);

        // CENTER 
        taoPhanCenter();
        add(pnCenter, BorderLayout.CENTER);
    }

    // ==============================================================================
    // HEADER
    // ==============================================================================
    private void taoPhanHeader() {
        pnHeader = new JPanel();
        pnHeader.setLayout(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94));
        pnHeader.setBackground(new Color(0xE3F2F5));

        // --- 1. Ô TÌM KIẾM TO (Bên trái) ---
        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã phiếu, tên nhân viên");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 20)); // Font 20
        txtTimKiem.setBounds(25, 17, 480, 60); 
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        pnHeader.add(txtTimKiem);

        // --- 2. BỘ LỌC (Trạng thái + Ngày) ---

        // Trạng thái
        JLabel lblTT = new JLabel("Trạng thái:");
        lblTT.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // Font 18
        lblTT.setBounds(530, 28, 90, 35);
        pnHeader.add(lblTT);

        cbTrangThai = new JComboBox<>();
        cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // Font 18
        cbTrangThai.setBounds(625, 28, 135, 38);
        pnHeader.add(cbTrangThai);

        // Từ ngày
        JLabel lblTu = new JLabel("Từ ngày:");
        lblTu.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblTu.setBounds(775, 28, 80, 35);
        pnHeader.add(lblTu);

        dateTuNgay = new JDateChooser();
        dateTuNgay.setDateFormatString("dd/MM/yyyy");
        dateTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        dateTuNgay.setBounds(860, 28, 150, 38);
        pnHeader.add(dateTuNgay);

        // Đến ngày
        JLabel lblDen = new JLabel("Đến:");
        lblDen.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblDen.setBounds(1025, 28, 50, 35);
        pnHeader.add(lblDen);

        dateDenNgay = new JDateChooser();
        dateDenNgay.setDateFormatString("dd/MM/yyyy");
        dateDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        dateDenNgay.setBounds(1080, 28, 150, 38);
        pnHeader.add(dateDenNgay);

        // --- 3. CÁC NÚT CHỨC NĂNG (Bên phải) ---
        btnTim = new PillButton("Tìm");
        btnTim.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnTim.setBounds(1252, 22, 109, 50);
        pnHeader.add(btnTim);

        btnLamMoi = new PillButton("Làm mới");
        btnLamMoi.setBounds(1373, 22, 130, 50);
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnHeader.add(btnLamMoi);
    }

    // ==============================================================================
    // CENTER
    // ==============================================================================
    private void taoPhanCenter() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));        
        
        createTable();
        
    }
    
    
    // ==============================================================================
    // TAO BẢNG
    // ==============================================================================
    
    private void createTable() {
    	
    	JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.5);
        pnCenter.add(splitPane, BorderLayout.CENTER);
        
        
    	// --- BẢNG 1: DANH SÁCH PHIẾU HỦY (TOP) ---
        String[] colPhieuHuy = { "STT", "Mã phiếu hủy", "Người lập / Hệ thống", "Ngày lập", "Tổng tiền", "Trạng thái" };
        modelPhieuHuy = new DefaultTableModel(colPhieuHuy, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblPhieuHuy = setupTable(modelPhieuHuy);

        // Căn lề & Render màu sắc
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        tblPhieuHuy.getColumnModel().getColumn(0).setCellRenderer(center); // STT
        tblPhieuHuy.getColumnModel().getColumn(1).setCellRenderer(center); // Mã
        tblPhieuHuy.getColumnModel().getColumn(2).setCellRenderer(center); // Người lập
        tblPhieuHuy.getColumnModel().getColumn(3).setCellRenderer(center); // Ngày
        tblPhieuHuy.getColumnModel().getColumn(4).setCellRenderer(right);  // Tiền

        // Render cột Trạng Thái (Màu sắc)
        tblPhieuHuy.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
                String status = (String) value;
                if ("Đã duyệt".equals(status)) {
                    lbl.setForeground(new Color(0x2E7D32)); 
                } else {
                    lbl.setForeground(new Color(0xE65100)); 
                }
                return lbl;
            }
        });

        tblPhieuHuy.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblPhieuHuy.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblPhieuHuy.getColumnModel().getColumn(4).setPreferredWidth(180);

        JScrollPane scrollPH = new JScrollPane(tblPhieuHuy);
        scrollPH.setBorder(createTitledBorder("Danh sách phiếu hủy hàng"));
        splitPane.setTopComponent(scrollPH);

        // --- BẢNG 2: CHI TIẾT PHIẾU HỦY (BOTTOM) ---
        String[] colChiTiet = { "STT", "Mã Lô", "Sản phẩm", "Lý do chi tiết", "Số lượng", "Giá vốn", "Thành tiền",
                "Trạng thái" };
        modelChiTiet = new DefaultTableModel(colChiTiet, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblChiTiet = setupTable(modelChiTiet);

        tblChiTiet.getColumnModel().getColumn(0).setCellRenderer(center);// stt
        tblChiTiet.getColumnModel().getColumn(1).setCellRenderer(center);// mã lô
        tblChiTiet.getColumnModel().getColumn(2).setPreferredWidth(250);  // Tên SP
        tblChiTiet.getColumnModel().getColumn(3).setPreferredWidth(200);  // Lý do
        tblChiTiet.getColumnModel().getColumn(4).setCellRenderer(right);  // SL
        tblChiTiet.getColumnModel().getColumn(5).setCellRenderer(right);  // Giá nhập
        tblChiTiet.getColumnModel().getColumn(6).setCellRenderer(right);  // Thành tiền
        tblChiTiet.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
                String status = (String) value;
                if ("Đã hủy hàng".equals(status)) {
                    lbl.setForeground(new Color(0x2E7D32)); // Xanh lá
                } else if ("Đã từ chối hủy".equals(status)) {
                    lbl.setForeground(new Color(0xE65100)); // Cam
                } else if ("Chờ duyệt".equals(status)) {
                    lbl.setForeground(Color.BLACK);
                }
                return lbl;
            }
        });

        JScrollPane scrollChiTiet = new JScrollPane(tblChiTiet);
        scrollChiTiet.setBorder(createTitledBorder("Chi tiết sản phẩm hủy"));
        splitPane.setBottomComponent(scrollChiTiet);
	}
    
    private JTable setupTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Font 16
        table.setRowHeight(35); // Cao 35
        table.setSelectionBackground(new Color(0xC8E6C9)); 
        table.setGridColor(new Color(230, 230, 230));
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Header Font 16 Bold
        header.setBackground(new Color(33, 150, 243));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 40)); // Header Cao 40
        return table;
    }

    private TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 18), Color.DARK_GRAY
        );
    }

    // ==============================================================================
    // INIT DATA 
    // ==============================================================================
    private void initData() {
        loadComboTrangThai();
        xuLyLamMoi(); 
        loadDuLieuPhieuHuyTheoPH(); 
    }

    private void loadComboTrangThai() {
        cbTrangThai.removeAllItems();
        cbTrangThai.addItem("Tất cả");
        cbTrangThai.addItem("Chờ duyệt");
        cbTrangThai.addItem("Đã duyệt");
    }

    /** load all phiếu huỷ từ DB vào allPhieuHuy */
    private void taiDanhSachPhieuHuy() {
        allPhieuHuy = new ArrayList<>();
        try {
            allPhieuHuy = ph_dao.layTatCaPhieuHuy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==============================================================================
    // EVENT
    // ==============================================================================
    private void addEvents() {
        btnLamMoi.addActionListener(this);
        btnTim.addActionListener(this);
        txtTimKiem.addActionListener(this); 
     
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnLamMoi) {
            xuLyLamMoi();
            return;
        }

        if (src == btnTim || src == txtTimKiem) {            
            xuLyTimKiem(true);
            return;
        }
    }

    // ==============================================================================
    // TÌM KIẾM 
    // ==============================================================================
    private void xuLyTimKiem(boolean includeDateRange) {
        String keyword = txtTimKiem.getText().trim();
        // Nếu có placeholder dạng "Tìm theo mã phiếu..." thì coi như rỗng
        if (keyword.toLowerCase().startsWith("tìm theo")) {
            keyword = "";
        }

        String tt = (String) cbTrangThai.getSelectedItem();
        if (tt == null) tt = "Tất cả";

        // Clone list gốc
        List<PhieuHuy> ds = new ArrayList<>(allPhieuHuy);

        // --- keyword: mã phiếu + tên nhân viên ---
        if (!keyword.isEmpty()) {
            String kw = keyword.toLowerCase();
            ds.removeIf(ph -> {
                String ma = ph.getMaPhieuHuy() != null ? ph.getMaPhieuHuy().toLowerCase() : "";
                String tenNV = (ph.getNhanVien() != null && ph.getNhanVien().getTenNhanVien() != null)
                        ? ph.getNhanVien().getTenNhanVien().toLowerCase()
                        : "";
                return !(ma.contains(kw) || tenNV.contains(kw));
            });
        }

        // --- Lọc theo Ngày (CHỈ khi includeDateRange = true) ---
        if (includeDateRange) {
            Date dTu = dateTuNgay.getDate();
            Date dDen = dateDenNgay.getDate();

            // Kiểm tra logic ngày nếu cả 2 đều được chọn
            if (dTu != null && dDen != null) {
                LocalDate from = dTu.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate to = dDen.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (to.isBefore(from)) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Ngày đến không được trước ngày từ!",
                            "Lỗi ngày",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
            }

            if (dTu != null || dDen != null) {
                LocalDate fromDate = (dTu != null)
                        ? dTu.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                        : LocalDate.MIN;
                LocalDate toDate = (dDen != null)
                        ? dDen.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                        : LocalDate.MAX;

                ds.removeIf(ph -> {
                    LocalDate ngayLap = ph.getNgayLapPhieu();
                    if (ngayLap == null) return true;
                    return ngayLap.isBefore(fromDate) || ngayLap.isAfter(toDate);
                });
            }
        }

        // --- Lọc theo trạng thái (text) ---
        if (!"Tất cả".equalsIgnoreCase(tt.trim())) {
            String ttFilter = tt.trim();
            ds.removeIf(ph -> {
                String text = ph.getTrangThaiText();
                if (text == null) return true;
                return !text.equalsIgnoreCase(ttFilter);
            });
        }

        // --- Load lên bảng ---
        loadTablePhieuHuy(ds);
        // Clear chi tiết khi tìm mới
        modelChiTiet.setRowCount(0);
    }

    // ==============================================================================
    // LÀM MỚI 
    // ==============================================================================
    private void xuLyLamMoi() {
        // 1. Reset ô tìm kiếm + placeholder
        txtTimKiem.setText("");
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã phiếu, tên nhân viên");

        // 2. Load lại danh sách phiếu hủy từ DB
        taiDanhSachPhieuHuy();

        // 3. Set ngày mặc định       
        dateDenNgay.setDate(null);
        dateTuNgay.setDate(null);

        

        // 4. Trạng thái = Tất cả
        cbTrangThai.setSelectedIndex(0);

        // 5. Hiển thị full danh sách
        loadTablePhieuHuy(allPhieuHuy);
        modelChiTiet.setRowCount(0);
    }


    private void lamMoi() {
        xuLyLamMoi();
    }

    // ==============================================================================
    // LOAD TABLE
    // ==============================================================================
    private void loadTablePhieuHuy(List<PhieuHuy> ds) {
        modelPhieuHuy.setRowCount(0);
        int stt = 1;
        for (PhieuHuy ph : ds) {
            modelPhieuHuy.addRow(new Object[]{
                    stt++,
                    ph.getMaPhieuHuy(),
                    (ph.getNhanVien() != null) ? ph.getNhanVien().getTenNhanVien() : "",
                    fmt.format(ph.getNgayLapPhieu()),
                    df.format(ph.getTongTien()),
                    ph.getTrangThaiText()
            });
        }
    }

    private void loadDuLieuPhieuHuyTheoPH() {
        // Click phiếu hủy -> Load chi tiết
        tblPhieuHuy.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblPhieuHuy.getSelectedRow();
                if (row >= 0) {
                    String maPH = tblPhieuHuy.getValueAt(row, 1).toString();
                    loadChiTietPhieuHuy(maPH);
                }
            }
        });
    }

    private void loadChiTietPhieuHuy(String maPH) {
        dsCTPH = new ArrayList<>();
        modelChiTiet.setRowCount(0);

        try {
            dsCTPH = ph_dao.layChiTietTheoMaPhieu(maPH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int stt = 1;
        for (ChiTietPhieuHuy ctph : dsCTPH) {
            modelChiTiet.addRow(new Object[]{
                    stt++,
                    ctph.getLoSanPham().getMaLo(),
                    ctph.getLoSanPham().getSanPham().getTenSanPham(),
                    ctph.getLyDoChiTiet(),
                    ctph.getSoLuongHuy(),
                    df.format(ctph.getDonGiaNhap()),
                    df.format(ctph.getThanhTien()),
                    ctph.getTrangThaiText()
            });
        }
    }
    
   

    // ==============================================================================
    // TEST MAIN
    // ==============================================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {}
            JFrame frame = new JFrame("Tra cứu phiếu hủy");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1450, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new TraCuuPhieuHuy_GUI());
            frame.setVisible(true);
        });
    }
}
