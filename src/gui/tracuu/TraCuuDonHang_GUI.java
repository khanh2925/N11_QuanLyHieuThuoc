package gui.tracuu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import com.toedter.calendar.JDateChooser;

import component.button.PillButton;
import component.input.PlaceholderSupport;
import component.border.RoundedBorder;

import dao.HoaDon_DAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import gui.dialog.HoaDonPreviewDialog;

public class TraCuuDonHang_GUI extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JPanel pnHeader;
    private JPanel pnCenter;
    
    // Bảng Hóa Đơn (Trên)
    private JTable tblHoaDon;
    private DefaultTableModel modelHoaDon;

    // Bảng Chi Tiết Hóa Đơn (Dưới)
    private JTable tblChiTiet;
    private DefaultTableModel modelChiTiet;

    // Các component lọc
    private JTextField txtTimKiem;
    private JDateChooser dateTuNgay;
    private JDateChooser dateDenNgay;
    private PillButton btnTimKiem;
    private PillButton btnLamMoi;

    // DAO và Utils
    private HoaDon_DAO hoaDonDao;
    private final DecimalFormat df = new DecimalFormat("#,### đ");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    // Cache danh sách để lọc ngày
    private List<HoaDon> dsHoaDonHienTai;

    public TraCuuDonHang_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        
        // Khởi tạo DAO
        hoaDonDao = new HoaDon_DAO();
        dsHoaDonHienTai = new ArrayList<>();
        
        initialize();
    }

    private void initialize() {
        // 1. LAYOUT CHÍNH
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 2. HEADER (Vùng Bắc)
        taoPhanHeader();
        add(pnHeader, BorderLayout.NORTH);

        // 3. CENTER (Vùng Giữa - Chứa 2 bảng)
        taoPhanCenter();
        add(pnCenter, BorderLayout.CENTER);

        // 4. DATA & EVENTS
        addEvents();
        xuLyLamMoi(); // Load dữ liệu ban đầu
    }

    // ==============================================================================
    //                                  PHẦN HEADER
    // ==============================================================================
    private void taoPhanHeader() {
        pnHeader = new JPanel();
        pnHeader.setLayout(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94));
        pnHeader.setBackground(new Color(0xE3F2F5));

        // --- 1. Ô TÌM KIẾM TO (Bên trái) - KHỚP VỚI BÁN HÀNG/SẢN PHẨM ---
        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã hóa đơn, SĐT khách hàng");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 20)); // Font 20
        // Set width = 480 để bằng với bên TraCuuSanPham
        txtTimKiem.setBounds(25, 17, 480, 60); 
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        pnHeader.add(txtTimKiem);

        // --- 2. BỘ LỌC NGÀY (Ở giữa) - KHỚP VỊ TRÍ COMBOX SẢN PHẨM ---
        
        // Từ ngày (Vị trí tương đương ComboBox Loại)
        JLabel lblTu = new JLabel("Từ ngày:");
        lblTu.setFont(new Font("Segoe UI", Font.PLAIN, 18)); 
        lblTu.setBounds(530, 28, 80, 35); // x=530 giống label Loại
        pnHeader.add(lblTu);

        dateTuNgay = new JDateChooser();
        dateTuNgay.setDateFormatString("dd/MM/yyyy");
        dateTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 18)); 
        // x=610 (dịch sang phải xíu vì chữ Từ Ngày dài hơn chữ Loại), Width=180
        dateTuNgay.setBounds(610, 28, 180, 38); 
        pnHeader.add(dateTuNgay);

        // Đến ngày (Vị trí tương đương ComboBox Trạng thái)
        JLabel lblDen = new JLabel("Đến:");
        lblDen.setFont(new Font("Segoe UI", Font.PLAIN, 18)); 
        lblDen.setBounds(830, 28, 50, 35); 
        pnHeader.add(lblDen);

        dateDenNgay = new JDateChooser();
        dateDenNgay.setDateFormatString("dd/MM/yyyy");
        dateDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 18)); 
        // x=890 giống ComboBox Trạng Thái, Width=180
        dateDenNgay.setBounds(890, 28, 180, 38); 
        pnHeader.add(dateDenNgay);

        // --- 3. CÁC NÚT CHỨC NĂNG (Bên phải) - KHỚP 100% ---
        btnTimKiem = new PillButton("Tìm kiếm");
        btnTimKiem.setBounds(1120, 22, 130, 50);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 18)); 
        pnHeader.add(btnTimKiem);

        btnLamMoi = new PillButton("Làm mới");
        btnLamMoi.setBounds(1265, 22, 130, 50);
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18)); 
        pnHeader.add(btnLamMoi);
    }

    // ==============================================================================
    //                                  PHẦN CENTER
    // ==============================================================================
    private void taoPhanCenter() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Tạo SplitPane chia đôi trên dưới
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.5);
        pnCenter.add(splitPane, BorderLayout.CENTER);

        // --- BẢNG 1: DANH SÁCH HÓA ĐƠN (TOP) ---
        String[] colHoaDon = {"STT", "Mã hóa đơn", "Khách hàng", "SĐT", "Nhân viên", "Ngày lập", "Tổng tiền"};
        modelHoaDon = new DefaultTableModel(colHoaDon, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        tblHoaDon = setupTable(modelHoaDon);
        
        // Căn lề bảng Hóa Đơn
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        tblHoaDon.getColumnModel().getColumn(0).setCellRenderer(center); // STT
        tblHoaDon.getColumnModel().getColumn(1).setCellRenderer(center); // Mã
        tblHoaDon.getColumnModel().getColumn(3).setCellRenderer(center); // SĐT
        tblHoaDon.getColumnModel().getColumn(5).setCellRenderer(center); // Ngày
        tblHoaDon.getColumnModel().getColumn(6).setCellRenderer(right);  // Tiền

        // Độ rộng cột
        tblHoaDon.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblHoaDon.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblHoaDon.getColumnModel().getColumn(2).setPreferredWidth(200);
        
        JScrollPane scrollHD = new JScrollPane(tblHoaDon);
        scrollHD.setBorder(createTitledBorder("Danh sách hóa đơn"));
        splitPane.setTopComponent(scrollHD);

        // --- BẢNG 2: CHI TIẾT HÓA ĐƠN (BOTTOM) ---
        String[] colChiTiet = {"STT", "Mã SP", "Tên sản phẩm", "Đơn vị", "Số lượng", "Đơn giá", "Thành tiền"};
        modelChiTiet = new DefaultTableModel(colChiTiet, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        tblChiTiet = setupTable(modelChiTiet);
        
        // Căn lề bảng Chi Tiết
        tblChiTiet.getColumnModel().getColumn(0).setCellRenderer(center); // STT
        tblChiTiet.getColumnModel().getColumn(1).setCellRenderer(center); // Mã SP
        tblChiTiet.getColumnModel().getColumn(3).setCellRenderer(center); // Đơn vị
        tblChiTiet.getColumnModel().getColumn(4).setCellRenderer(center); // SL
        tblChiTiet.getColumnModel().getColumn(5).setCellRenderer(right);  // Đơn giá
        tblChiTiet.getColumnModel().getColumn(6).setCellRenderer(right);  // Thành tiền

        // Độ rộng cột
        tblChiTiet.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblChiTiet.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblChiTiet.getColumnModel().getColumn(2).setPreferredWidth(300);

        JScrollPane scrollChiTiet = new JScrollPane(tblChiTiet);
        scrollChiTiet.setBorder(createTitledBorder("Chi tiết đơn hàng"));
        splitPane.setBottomComponent(scrollChiTiet);
    }

    // Hàm setup Table chung (Font 16, RowHeight 35)
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

    // Hàm tạo border tiêu đề chung (Font 18 Bold)
    private TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 18), Color.DARK_GRAY // Font 18
        );
    }

    // ==============================================================================
    //                                  SỰ KIỆN & LOGIC
    // ==============================================================================
    
    private void addEvents() {
        btnTimKiem.addActionListener(this);
        btnLamMoi.addActionListener(this);
        txtTimKiem.addActionListener(this); 

        // ListSelectionListener: Click vào bảng hóa đơn -> Load chi tiết
        tblHoaDon.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadChiTietTuDongChon();
            }
        });
        
        // MouseListener: Double click để xem lại hóa đơn
        tblHoaDon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tblHoaDon.getSelectedRow();
                    if (row != -1) {
                        String maHD = tblHoaDon.getValueAt(row, 1).toString();
                        xemLaiHoaDon(maHD);
                    }
                }
            }
        });
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == btnTimKiem || o == txtTimKiem) {
            xuLyTimKiem();
        } else if (o == btnLamMoi) {
            xuLyLamMoi();
        }
    }

    // --- 1. Load dữ liệu ban đầu / Reset ---
    private void xuLyLamMoi() {
        txtTimKiem.setText("");
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã hóa đơn, SĐT khách hàng");
        
        // --- CHỌN NGÀY MẶC ĐỊNH ---
        Calendar cal = Calendar.getInstance();
        
        // Đến ngày: Hôm nay
        Date now = cal.getTime();
        dateDenNgay.setDate(now);
        
        // Từ ngày: Mùng 1 của tháng hiện tại
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date d30 = cal.getTime();
        dateTuNgay.setDate(d30);
        
        // Lấy tất cả từ DAO
        dsHoaDonHienTai = hoaDonDao.layTatCaHoaDon(); 
        
        // Render có lọc theo ngày mặc định luôn cho hợp lý
        xuLyTimKiem();
    }

    // --- 2. Tìm kiếm và Lọc ---
    private void xuLyTimKiem() {
        String tuKhoa = txtTimKiem.getText().trim();
        if (tuKhoa.contains("Tìm theo mã")) tuKhoa = "";

        List<HoaDon> ketQua = new ArrayList<>();

        // Bước 1: Tìm theo Mã hoặc SĐT (Database) hoặc lấy hết
        if (!tuKhoa.isEmpty()) {
            if (tuKhoa.toUpperCase().startsWith("HD-")) {
                HoaDon hd = hoaDonDao.timHoaDonTheoMa(tuKhoa);
                if (hd != null) ketQua.add(hd);
            } else {
                ketQua = hoaDonDao.timHoaDonTheoSoDienThoai(tuKhoa);
            }
        } else {
            // Không nhập gì thì lấy hết (để sau đó lọc ngày)
            ketQua = hoaDonDao.layTatCaHoaDon();
        }

        // Bước 2: Lọc theo Ngày (Java Filter)
        Date dTu = dateTuNgay.getDate();
        Date dDen = dateDenNgay.getDate();

        if (dTu != null || dDen != null) {
            LocalDate fromDate = (dTu != null) ? dTu.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : LocalDate.MIN;
            LocalDate toDate = (dDen != null) ? dDen.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : LocalDate.MAX;

            List<HoaDon> ketQuaLocNgay = new ArrayList<>();
            for (HoaDon hd : ketQua) {
                LocalDate ngayLap = hd.getNgayLap();
                // So sánh ngày: fromDate <= ngayLap <= toDate
                if ((ngayLap.isEqual(fromDate) || ngayLap.isAfter(fromDate)) &&
                    (ngayLap.isEqual(toDate) || ngayLap.isBefore(toDate))) {
                    ketQuaLocNgay.add(hd);
                }
            }
            ketQua = ketQuaLocNgay;
        }

        // Bước 3: Hiển thị
        dsHoaDonHienTai = ketQua;
        renderBangHoaDon(dsHoaDonHienTai);
        modelChiTiet.setRowCount(0);
        
        // Nếu tìm kiếm cụ thể (có nhập text) mà không thấy thì báo
        if (ketQua.isEmpty() && !tuKhoa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn nào!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // --- 3. Render Bảng Hóa Đơn ---
    private void renderBangHoaDon(List<HoaDon> list) {
        modelHoaDon.setRowCount(0);
        int stt = 1;
        for (HoaDon hd : list) {
            String tenKH = (hd.getKhachHang() != null) ? hd.getKhachHang().getTenKhachHang() : "Khách lẻ";
            String sdtKH = (hd.getKhachHang() != null) ? hd.getKhachHang().getSoDienThoai() : "";
            String tenNV = (hd.getNhanVien() != null) ? hd.getNhanVien().getTenNhanVien() : "N/A";
            
            modelHoaDon.addRow(new Object[] {
                stt++,
                hd.getMaHoaDon(),
                tenKH,
                sdtKH,
                tenNV,
                dtf.format(hd.getNgayLap()),
                df.format(hd.getTongThanhToan())
            });
        }
    }

    // --- 4. Load Chi Tiết khi chọn dòng ---
    private void loadChiTietTuDongChon() {
        int row = tblHoaDon.getSelectedRow();
        if (row >= 0) {
            String maHD = tblHoaDon.getValueAt(row, 1).toString();
            
            HoaDon hdChon = null;
            for (HoaDon h : dsHoaDonHienTai) {
                if (h.getMaHoaDon().equals(maHD)) {
                    hdChon = h;
                    break;
                }
            }
            if (hdChon == null) {
                hdChon = hoaDonDao.timHoaDonTheoMa(maHD);
            }

            if (hdChon != null) {
                renderBangChiTiet(hdChon.getDanhSachChiTiet());
            }
        }
    }

    private void renderBangChiTiet(List<ChiTietHoaDon> list) {
        modelChiTiet.setRowCount(0);
        int stt = 1;
        for (ChiTietHoaDon ct : list) {
            modelChiTiet.addRow(new Object[]{
                stt++,
                ct.getSanPham().getMaSanPham(),
                ct.getSanPham().getTenSanPham(),
                ct.getDonViTinh().getTenDonViTinh(),
                ct.getSoLuong(),
                df.format(ct.getGiaBan()), 
                df.format(ct.getThanhTien()) 
            });
        }
    }
    
    // --- 5. Xem lại hóa đơn ---
    private void xemLaiHoaDon(String maHD) {
        HoaDon hd = hoaDonDao.timHoaDonTheoMa(maHD);
        if (hd != null) {
             new HoaDonPreviewDialog(SwingUtilities.getWindowAncestor(this), hd).setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tra cứu đơn hàng");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new TraCuuDonHang_GUI());
            frame.setVisible(true);
        });
    }
}