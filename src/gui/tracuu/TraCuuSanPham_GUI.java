package gui.tracuu;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

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

    public TraCuuSanPham_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        
        // 1. Khởi tạo DAO
        sanPhamDao = new SanPham_DAO();
        loSanPhamDao = new LoSanPham_DAO();
        quyCachDao = new QuyCachDongGoi_DAO();
        dsSanPhamHienTai = new ArrayList<>();

        // 2. Dựng giao diện
        initialize();
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
        PlaceholderSupport.addPlaceholder(txtTimThuoc, "Nhập tên thuốc, mã SP, số đăng ký...");
        txtTimThuoc.setFont(new Font("Segoe UI", Font.PLAIN, 20)); 
        txtTimThuoc.setBounds(25, 17, 480, 60);
        txtTimThuoc.setBorder(new RoundedBorder(20));
        txtTimThuoc.setBackground(Color.WHITE);
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
        btnTimKiem = new PillButton("Tìm kiếm");
        btnTimKiem.setBounds(1120, 22, 130, 50);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 18)); 
        pnHeader.add(btnTimKiem);

        btnLamMoi = new PillButton("Làm mới");
        btnLamMoi.setBounds(1265, 22, 130, 50);
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18)); 
        pnHeader.add(btnLamMoi);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        
        if (o == btnTimKiem || o == txtTimThuoc) {
            xuLyTimKiem();
        } else if (o == btnLamMoi) {
            xuLyLamMoi();
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