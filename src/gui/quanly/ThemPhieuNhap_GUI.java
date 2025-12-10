package gui.quanly;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;

// Imports của Apache POI
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import com.toedter.calendar.JDateChooser;

import database.connectDB;
import component.button.PillButton;
import component.input.PlaceholderSupport;
import component.border.RoundedBorder;
import component.input.TaoJtextNhanh;
import dao.DonViTinh_DAO;
import dao.LoSanPham_DAO;
import dao.NhaCungCap_DAO;
import dao.NhanVien_DAO;
import dao.PhieuNhap_DAO;
import dao.SanPham_DAO;
import dao.QuyCachDongGoi_DAO; 
import entity.ChiTietPhieuNhap;
import entity.DonViTinh;
import entity.LoSanPham;
import entity.NhaCungCap;
import entity.NhanVien;
import entity.PhieuNhap;
import entity.SanPham;
import entity.QuyCachDongGoi; 
import entity.Session;
import entity.TaiKhoan;
import gui.dialog.ChonLo_Dialog;
import gui.dialog.ThemLo_Dialog;


public class ThemPhieuNhap_GUI extends JPanel implements ActionListener, Serializable {
    private JPanel pnDanhSachDon;
    private JTextField txtSearch;
    private JTextField txtTimNCC;
    private JTextField txtTongTienHang;
    private JTextField txtTenNCC;
    private JTextField txtDiaChiNCC;
    private JTextField txtEmailNCC;

    private JButton btnThemLo, btnNhapFile, btnNhapPhieu;
    private JScrollPane scrollPane;

    // ===== DAOs =====
    private SanPham_DAO sanPhamDAO;
    private LoSanPham_DAO loSanPhamDAO;
    private PhieuNhap_DAO phieuNhapDAO;
    private NhaCungCap_DAO nhaCungCapDAO;
    private DonViTinh_DAO donViTinhDAO;
    private QuyCachDongGoi_DAO quyCachDAO; 

    // ===== Formatting =====
    private final DecimalFormat df = new DecimalFormat("#,###");
    private final DateTimeFormatter fmtDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter fmtDateTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ===== Dữ liệu phiên làm việc =====
    private NhaCungCap nhaCungCapDaChon = null;
    private NhanVien nhanVienDangNhap = null;
    private JFrame mainFrame;

    private int soLoTiepTheo = 1;

    // ... (Toàn bộ code Constructor và khoiTaoGiaoDien() giữ nguyên) ...
    // ... (Bỏ qua để tiết kiệm không gian) ...
    
    /**
     * Constructor chính
     */
    public ThemPhieuNhap_GUI(JFrame frame) {
        this.mainFrame = frame;

        TaiKhoan taiKhoanDangNhap = Session.getInstance().getTaiKhoanDangNhap();
        if (taiKhoanDangNhap != null) {
            this.nhanVienDangNhap = taiKhoanDangNhap.getNhanVien();
        } else {
            this.nhanVienDangNhap = null; 
        }

        sanPhamDAO = new SanPham_DAO();
        loSanPhamDAO = new LoSanPham_DAO();
        phieuNhapDAO = new PhieuNhap_DAO();
        nhaCungCapDAO = new NhaCungCap_DAO();
        donViTinhDAO = new DonViTinh_DAO();
        quyCachDAO = new QuyCachDongGoi_DAO(); 

        if (this.nhanVienDangNhap == null) {
            JOptionPane.showMessageDialog(this, "Lỗi: Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại!", "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
        }

        try {
            String maLoDauTien = loSanPhamDAO.taoMaLoTuDong();
            if (maLoDauTien != null && maLoDauTien.matches("^LO-\\d{6}$")) {
                this.soLoTiepTheo = Integer.parseInt(maLoDauTien.substring(3));
            } else {
                this.soLoTiepTheo = 1;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy mã lô đầu tiên: " + e.getMessage());
            this.soLoTiepTheo = 1;
        }

        this.setPreferredSize(new Dimension(1537, 850));
        initialize(); //
    }

    /**
     * Constructor mặc định
     */
    public ThemPhieuNhap_GUI() {
        this.mainFrame = null; // Không có frame chính khi test

        NhanVien_DAO nhanVienDAO_Test = new NhanVien_DAO();
        this.nhanVienDangNhap = nhanVienDAO_Test.timNhanVienTheoMa("NV-20250210-0017");

        if(nhanVienDangNhap == null) {
            System.err.println("⚠️ [ThemPhieuNhap_GUI] Không tìm thấy NV 'NV-20250210-0017 '. Tạo NV tạm để test UI.");
            try {
                nhanVienDangNhap = new NhanVien("NV-20250210-0017 ", "NV Test (Fallback)", 1, true);
                nhanVienDangNhap.setQuanLy(true);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                nhanVienDangNhap = new NhanVien();
            }
        }

        sanPhamDAO = new SanPham_DAO();
        loSanPhamDAO = new LoSanPham_DAO();
        phieuNhapDAO = new PhieuNhap_DAO();
        nhaCungCapDAO = new NhaCungCap_DAO();
        donViTinhDAO = new DonViTinh_DAO();
        quyCachDAO = new QuyCachDongGoi_DAO(); 

        try {
            String maLoDauTien = loSanPhamDAO.taoMaLoTuDong();
            if (maLoDauTien != null && maLoDauTien.matches("^LO-\\d{6}$")) {
                this.soLoTiepTheo = Integer.parseInt(maLoDauTien.substring(3));
            } else {
                this.soLoTiepTheo = 1;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy mã lô đầu tiên: " + e.getMessage());
            this.soLoTiepTheo = 1;
        }

        this.setPreferredSize(new Dimension(1537, 850));
        initialize(); // <-- ĐÃ VIỆT HÓA (từ initialize)
    }


    /**
     * Phương thức khởi tạo giao diện chính
     */
    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===== HEADER (NORTH) =====
        JPanel pnHeader = new JPanel();
        pnHeader.setPreferredSize(new Dimension(0, 88));
        pnHeader.setBackground(new Color(0xE3F2F5));
        pnHeader.setBorder(new EmptyBorder(15, 20, 15, 20));
        add(pnHeader, BorderLayout.NORTH);

        txtSearch = TaoJtextNhanh.nhapLieu("Nhập Mã SP để thêm lô và nhấn Enter...");
        txtSearch.setBounds(20, 15, 420, 58);
//        PlaceholderSupport.addPlaceholder(txtSearch, "Nhập Mã SP để thêm lô và nhấn Enter...");
//        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 16));
//        txtSearch.setBorder(new RoundedBorder(15));
        txtSearch.addActionListener(this);
        pnHeader.setLayout(null);
        txtSearch.setPreferredSize(new Dimension(420, 60));
        pnHeader.add(txtSearch);

        JPanel pnHeaderButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnHeaderButtons.setBounds(500, 20, 300, 58);
        pnHeaderButtons.setOpaque(false);

        btnThemLo = new PillButton("Thêm lô");
        btnThemLo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnThemLo.setPreferredSize(new Dimension(120, 40));
        btnThemLo.addActionListener(this);
        pnHeaderButtons.add(btnThemLo);

        btnNhapFile = new PillButton("Nhập từ file");
        btnNhapFile.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnNhapFile.setPreferredSize(new Dimension(150, 40));
        btnNhapFile.addActionListener(this);
        pnHeaderButtons.add(btnNhapFile);

        pnHeader.add(pnHeaderButtons);


        // ===== CENTER (DANH SÁCH SẢN PHẨM NHẬP) =====
        JPanel pnCenterPanel = new JPanel();
        pnCenterPanel.setBackground(Color.WHITE);
        add(pnCenterPanel, BorderLayout.CENTER);
        pnCenterPanel.setBorder(new CompoundBorder(new LineBorder(new Color(0, 191, 165), 4, true), new EmptyBorder(5, 5, 5, 5)));
        pnCenterPanel.setLayout(new BorderLayout(0, 0));

        pnDanhSachDon = new JPanel();
        pnDanhSachDon.setLayout(new BoxLayout(pnDanhSachDon, BoxLayout.Y_AXIS));
        pnDanhSachDon.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(pnDanhSachDon);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        pnCenterPanel.add(scrollPane);

        // ====== SIDEBAR (EAST) ======
        JPanel pnSidebar = new JPanel();
        pnSidebar.setPreferredSize(new Dimension(450, 0));
        pnSidebar.setBackground(Color.WHITE);
        pnSidebar.setBorder(new EmptyBorder(20, 20, 20, 20));
        pnSidebar.setLayout(new BoxLayout(pnSidebar, BoxLayout.Y_AXIS));
        add(pnSidebar, BorderLayout.EAST);

        // --- Thông tin nhân viên ---
        JPanel pnNhanVien = new JPanel(new BorderLayout(5, 5));
        pnNhanVien.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        pnNhanVien.setOpaque(false);
        JLabel lblNhanVienLabel = new JLabel("Nhân viên:");
        lblNhanVienLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel lblNhanVienValue = new JLabel(nhanVienDangNhap != null ? nhanVienDangNhap.getTenNhanVien() : "N/A");
        lblNhanVienValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel lblThoiGian = new JLabel(java.time.LocalDateTime.now().format(fmtDateTime), SwingConstants.RIGHT);
        lblThoiGian.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnNhanVien.add(lblNhanVienLabel, BorderLayout.WEST);
        pnNhanVien.add(lblNhanVienValue, BorderLayout.CENTER);
        pnNhanVien.add(lblThoiGian, BorderLayout.EAST);
        pnSidebar.add(pnNhanVien);
        pnSidebar.add(Box.createVerticalStrut(10));
        JSeparator lineNV = new JSeparator();
        lineNV.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        pnSidebar.add(Box.createVerticalStrut(4));
        pnSidebar.add(lineNV);
        pnSidebar.add(Box.createVerticalStrut(15));

        // --- Giao diện tìm kiếm NCC ---


        JPanel pnTimNCC = new JPanel(new BorderLayout(5, 0));
        pnTimNCC.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        pnTimNCC.setOpaque(false);

        txtTimNCC = TaoJtextNhanh.nhapLieu("Nhập Số điện thoại NCC rồi nhấn Enter");
        txtTimNCC.setPreferredSize(new Dimension(120, 200));;

        txtTimNCC.addActionListener(this);
        pnTimNCC.add(txtTimNCC, BorderLayout.CENTER);
        pnSidebar.add(pnTimNCC);
        pnSidebar.add(Box.createVerticalStrut(15));

     // --- Panel thông tin NCC ---
        JPanel pnThongTinNCC = new JPanel();
        pnThongTinNCC.setBackground(Color.WHITE);
        pnThongTinNCC.setLayout(new BoxLayout(pnThongTinNCC, BoxLayout.Y_AXIS));
        pnThongTinNCC.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Khai báo Font chữ
        Font fontLabelNCC = new Font("Segoe UI", Font.PLAIN, 18);
        Font fontValueNCC = new Font("Segoe UI", Font.BOLD, 18); // Font đậm cho nội dung
        int txtWidth = 310; // Chiều rộng trường hiển thị (Sidebar 450 - padding - label)

        // --- Hàng 1: Tên Nhà Cung Cấp ---
        Box boxTen = Box.createHorizontalBox();
        boxTen.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitleTen = new JLabel("Tên NCC: ");
        lblTitleTen.setFont(fontLabelNCC);
        lblTitleTen.setPreferredSize(new Dimension(80, 30)); // Cố định chiều rộng label tiêu đề

        // SỬA Ở ĐÂY: Dùng TaoJtextNhanh
        txtTenNCC = TaoJtextNhanh.hienThi("Chưa chọn NCC", new Font("Segoe UI", Font.BOLD, 18), new Color(0x00796B));

        boxTen.add(lblTitleTen);
        boxTen.add(txtTenNCC);

        // --- Hàng 2: Địa chỉ ---
        Box boxDiaChi = Box.createHorizontalBox();
        boxDiaChi.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitleDiaChi = new JLabel("Địa chỉ: ");
        lblTitleDiaChi.setFont(fontLabelNCC);
        lblTitleDiaChi.setPreferredSize(new Dimension(80, 30));

        // SỬA Ở ĐÂY: Dùng TaoJtextNhanh
        txtDiaChiNCC = TaoJtextNhanh.hienThi("N/A", new Font("Segoe UI", Font.BOLD, 18), new Color(0x00796B));

        boxDiaChi.add(lblTitleDiaChi);
        boxDiaChi.add(txtDiaChiNCC);

        // --- Hàng 3: Email ---
        Box boxEmail = Box.createHorizontalBox();
        boxEmail.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitleEmail = new JLabel("Email: ");
        lblTitleEmail.setFont(fontLabelNCC);
        lblTitleEmail.setPreferredSize(new Dimension(80, 30));

        // SỬA Ở ĐÂY: Dùng TaoJtextNhanh
        txtEmailNCC = TaoJtextNhanh.hienThi("N/A", new Font("Segoe UI", Font.BOLD, 18), new Color(0x00796B));

        boxEmail.add(lblTitleEmail);
        boxEmail.add(txtEmailNCC);

        // --- Thêm các Box vào Panel chính ---
        pnThongTinNCC.add(boxTen);
        pnThongTinNCC.add(Box.createVerticalStrut(10)); // Khoảng cách dòng
        pnThongTinNCC.add(boxDiaChi);
        pnThongTinNCC.add(Box.createVerticalStrut(10));
        pnThongTinNCC.add(boxEmail);

        // Thiết lập kích thước cho panel chứa
        int desiredHeight = 150;
        Dimension fixedSize = new Dimension(Integer.MAX_VALUE, desiredHeight);
        pnThongTinNCC.setPreferredSize(fixedSize);
        pnThongTinNCC.setMinimumSize(fixedSize);
        pnThongTinNCC.setMaximumSize(fixedSize);

        pnSidebar.add(pnThongTinNCC);

        pnSidebar.add(Box.createVerticalStrut(100)); // Đẩy tổng tiền xuống

        JSeparator lineTotal = new JSeparator();
        lineTotal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        pnSidebar.add(lineTotal);
        pnSidebar.add(Box.createVerticalStrut(10));

        // --- Tổng tiền và Nút Nhập ---
     // --- Tổng tiền hàng (Đã sửa thành Label + TextField) ---
        Box boxTongTien = Box.createHorizontalBox();
        boxTongTien.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblTitleTongTien = new JLabel("Tổng tiền hàng: ");
        lblTitleTongTien.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        // Tạo TextField hiển thị tổng tiền: Font to, màu đỏ, rộng khoảng 250px
        txtTongTienHang = TaoJtextNhanh.hienThi("0 đ", new Font("Segoe UI", Font.BOLD, 20), Color.RED);
        txtTongTienHang.setHorizontalAlignment(SwingConstants.RIGHT); // Căn phải số tiền cho đẹp
        txtTongTienHang.setBackground(Color.WHITE); // Nền trắng cho nổi bật
        
        boxTongTien.add(lblTitleTongTien);
        boxTongTien.add(Box.createHorizontalGlue()); // Đẩy text field sang hết bên phải (tùy chọn)
        boxTongTien.add(txtTongTienHang);
        
        pnSidebar.add(boxTongTien);
        pnSidebar.add(Box.createVerticalStrut(15));

        btnNhapPhieu = new PillButton("Nhập Phiếu");
        btnNhapPhieu.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnNhapPhieu.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnNhapPhieu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnNhapPhieu.addActionListener(this);
        pnSidebar.add(btnNhapPhieu);
    }



    /**
     * Helper: Tạo một JLabel để hiển thị thông tin (dạng Nhãn: Giá trị)
     */
    private JLabel taoNhanThongTin(String labelText, String valueText) {
        JLabel label = new JLabel(String.format("<html>%s <b style='color: #333;'>%s</b></html>", labelText, valueText));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return label;
    }
    /**
     * Hàm đánh lại số thứ tự cho các dòng sản phẩm
     */
    private void capNhatLaiSTT() {
        Component[] components = pnDanhSachDon.getComponents();
        int stt = 1;
        for (Component comp : components) {
            if (comp instanceof ChiTietSanPhamPanel panel) {
                panel.setSTT(stt++);
            }
        }
    }

    /**
     * Cập nhật tổng tiền hàng (hiển thị lên TextField)
     */
    public void capNhatTongTienHang() {
        double tongTien = 0;
        Component[] components = pnDanhSachDon.getComponents();
        for (Component comp : components) {
            if (comp instanceof ChiTietSanPhamPanel panel) {
                tongTien += panel.layTongThanhTien();
            }
        }
        // Cập nhật giá trị vào TextField
        txtTongTienHang.setText(df.format(tongTien) + " đ");
    }

    /**
     * Helper: Tìm một component con theo tên
     */
    private Component timComponentTheoTen(Container container, String name) {
        for (Component comp : container.getComponents()) {
            if (name.equals(comp.getName())) {
                return comp;
            }
            if (comp instanceof Container subContainer) {
                 Component found = timComponentTheoTen(subContainer, name); // <-- ĐÃ VIỆT HÓA
                 if (found != null) return found;
            }
        }
        return null;
    }

    /**
     * Hàm bắt sự kiện (Không thể đổi tên hàm này)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == btnThemLo || source == txtSearch) {
            xuLyThemLo();
        } else if (source == btnNhapFile) {
            xuLyNhapFile();
        } else if (source == btnNhapPhieu) {
            xuLyNhapPhieu();
        } else if (source == txtTimNCC) {
             xuLyTimNhaCungCap();
        }
    }

    /**
     * Xử lý nghiệp vụ nhập hàng từ file Excel
     * ✅ ĐÃ CẬP NHẬT: Tự động đọc SĐT Nhà Cung Cấp từ cột thứ 6 (Cell 5) của dòng dữ liệu đầu tiên
     */
    private void xuLyNhapFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file Excel để nhập");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        int userSelection = fileChooser.showOpenDialog(mainFrame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToRead = fileChooser.getSelectedFile();

            JOptionPane.showMessageDialog(mainFrame,
                "Đang xử lý file, vui lòng chờ...\nGiao diện có thể bị treo trong giây lát.",
                "Đang nhập file",
                JOptionPane.INFORMATION_MESSAGE);

            StringBuilder errorMessages = new StringBuilder();
            int successCount = 0;
            int failCount = 0;
            boolean daTimNCC = false; // 🚩 Cờ kiểm tra xem đã tìm NCC chưa

            try (FileInputStream fis = new FileInputStream(fileToRead);
                 Workbook workbook = new XSSFWorkbook(fis)) {

                Sheet sheet = workbook.getSheetAt(0);
                Iterator<Row> rowIterator = sheet.iterator();

                // Bỏ qua dòng tiêu đề (Header)
                if (rowIterator.hasNext()) {
                    rowIterator.next();
                }

                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    try {
                        // 1. Đọc các ô dữ liệu cơ bản
                        String maSP = layGiaTriChuoiTuO(row.getCell(0)); 
                        LocalDate hsd = layGiaTriNgayTuO(row.getCell(1)); 
                        int soLuong = (int) layGiaTriSoTuO(row.getCell(2)); 
                        double donGia_Excel = layGiaTriSoTuO(row.getCell(3)); 
                        String tenDVT_Excel = layGiaTriChuoiTuO(row.getCell(4)); 

                        // ============================================================
                        // 🚩 LOGIC MỚI: Đọc SĐT Nhà Cung Cấp từ cột 5 (Cột F)
                        // Chỉ thực hiện 1 lần duy nhất cho dòng dữ liệu hợp lệ đầu tiên
                        // ============================================================
                        if (!daTimNCC) {
                            String sdtNCC = layGiaTriChuoiTuO(row.getCell(5)); // Lấy cột F
                            if (!sdtNCC.isEmpty()) {
                                txtTimNCC.setText(sdtNCC); // Điền SĐT vào ô tìm kiếm
                                xuLyTimNhaCungCap();       // Gọi hàm xử lý tìm kiếm (tự động Enter)
                                
                                // Kiểm tra nếu tìm thấy thì khóa cờ lại, nếu không thấy thì báo lỗi nhẹ
                                if (nhaCungCapDaChon != null) {
                                    daTimNCC = true; 
                                } else {
                                    errorMessages.append("⚠️ Cảnh báo: Không tìm thấy NCC với SĐT: ").append(sdtNCC).append("\n");
                                }
                            }
                        }
                        // ============================================================

                        if (maSP.isEmpty() && tenDVT_Excel.isEmpty() && (hsd == null || hsd.toString().isEmpty())) {
                            continue; // Bỏ qua dòng trống
                        }

                        if (maSP.isEmpty() || tenDVT_Excel.isEmpty() || hsd == null) {
                            throw new Exception("Mã SP, HSD, hoặc Tên ĐVT không được rỗng.");
                        }

                        SanPham sp = sanPhamDAO.laySanPhamTheoMa(maSP);
                        if (sp == null) {
                            throw new Exception("Không tìm thấy Mã SP: " + maSP);
                        }

                        QuyCachDongGoi qc_goc = quyCachDAO.timQuyCachGocTheoSanPham(sp.getMaSanPham());
                        if (qc_goc == null) {
                            throw new Exception("Sản phẩm '" + sp.getTenSanPham() + "' (SP: " + maSP + ") chưa được cấu hình Đơn Vị Gốc.");
                        }
                        DonViTinh dvtGoc = qc_goc.getDonViTinh();

                        if (!tenDVT_Excel.equalsIgnoreCase(dvtGoc.getTenDonViTinh())) {
                             throw new Exception(String.format("Đơn vị tính '%s' không phải Đơn Vị Gốc (%s) của sản phẩm.", tenDVT_Excel, dvtGoc.getTenDonViTinh()));
                        }
                        if (donGia_Excel != sp.getGiaNhap()) {
                            throw new Exception(String.format("Đơn giá nhập '%,.0f' không khớp với Đơn Giá Gốc (%,.0f) của sản phẩm.", donGia_Excel, sp.getGiaNhap()));
                        }
                        
                        String maLo = String.format("LO-%06d", this.soLoTiepTheo);
                        this.soLoTiepTheo++;
                        LoSanPham loMoi = new LoSanPham(maLo, hsd, 0, sp);

                        ChiTietPhieuNhap chiTietMoi = new ChiTietPhieuNhap();
                        chiTietMoi.setLoSanPham(loMoi);
                        chiTietMoi.setDonViTinh(dvtGoc); 
                        chiTietMoi.setSoLuongNhap(soLuong);
                        chiTietMoi.setDonGiaNhap(sp.getGiaNhap()); 

                        ChiTietSanPhamPanel panelSanPham = timPanelSanPham(sp.getMaSanPham());

                        if(panelSanPham != null) {
                            if (!panelSanPham.layDonViTinh().equals(dvtGoc) || panelSanPham.layDonGia() != sp.getGiaNhap()) { 
                                throw new Exception(String.format("DVT/Đơn giá không khớp. (Cần: %s - %.0f đ)",
                                    panelSanPham.layDonViTinh().getTenDonViTinh(), panelSanPham.layDonGia())); 
                            }
                            panelSanPham.themLot(chiTietMoi);
                        } else {
                            ChiTietSanPhamPanel newPanel = new ChiTietSanPhamPanel(sp, dvtGoc, sp.getGiaNhap());
                            newPanel.themLot(chiTietMoi);
                            pnDanhSachDon.add(newPanel);
                            capNhatLaiSTT();
                        }
                        successCount++;

                    } catch (Exception e) {
                        failCount++;
                        errorMessages.append("Dòng ").append(row.getRowNum() + 1).append(": ").append(e.getMessage()).append("\n");
                    }
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi nghiêm trọng khi đọc file:\n" + e.getMessage(), "Lỗi File", JOptionPane.ERROR_MESSAGE);
                return;
            }

            capNhatTongTienHang();
            pnDanhSachDon.revalidate();
            pnDanhSachDon.repaint();
            SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum()));

            String summaryMessage = String.format("Hoàn thành nhập file!\n\nThành công: %d dòng.\nThất bại: %d dòng.", successCount, failCount);
            
            // Thêm thông báo về NCC trong kết quả
            if (nhaCungCapDaChon != null) {
                summaryMessage += "\n\n✅ Đã chọn NCC: " + nhaCungCapDaChon.getTenNhaCungCap();
            } else {
                summaryMessage += "\n\n⚠️ Chưa chọn được NCC (Kiểm tra cột F trong file Excel).";
            }

            if (failCount > 0) {
                JTextArea textArea = new JTextArea(errorMessages.toString());
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(500, 200));
                JOptionPane.showMessageDialog(this,
                    new Object[]{summaryMessage, "\nChi tiết lỗi:", scrollPane},
                    "Kết Quả Nhập File",
                    JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, summaryMessage, "Kết Quả Nhập File", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * Helper: Lấy giá trị dạng Chuỗi từ ô Excel
     */
    private String layGiaTriChuoiTuO(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return new DecimalFormat("#").format(cell.getNumericCellValue());
        } else {
            return "";
        }
    }

    /**
     * Helper: Lấy giá trị dạng Số từ ô Excel
     */
    private double layGiaTriSoTuO(Cell cell) throws Exception {
        if (cell == null) {
            throw new Exception("Ô số lượng/đơn giá bị rỗng.");
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(cell.getStringCellValue().trim());
            } catch (NumberFormatException e) {
                throw new Exception("Ô '" + cell.getStringCellValue() + "' không phải là số.");
            }
        } else {
            throw new Exception("Ô số lượng/đơn giá có kiểu dữ liệu không hợp lệ.");
        }
    }

    /**
     * Helper: Lấy giá trị dạng Ngày từ ô Excel
     */
    private LocalDate layGiaTriNgayTuO(Cell cell) throws Exception {
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.STRING) {
            String dateString = cell.getStringCellValue().trim();
            if (dateString.isEmpty()) return null;
            try {
                return LocalDate.parse(dateString, fmtDate);
            } catch (Exception e) {
                throw new Exception("Định dạng ngày '" + dateString + "' không hợp lệ (cần dd/MM/yyyy).");
            }
        }
        else if (cell.getCellType() == CellType.NUMERIC && org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
            Date javaDate = cell.getDateCellValue();
            return javaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        else if (cell.getCellType() == CellType.BLANK) {
            return null;
        }
        else {
            throw new Exception("Ô HSD không phải là ngày tháng (hãy định dạng là Text dd/MM/yyyy).");
        }
    }


    /**
     * Đặt lại (reset) thông tin nhà cung cấp trên UI
     */
    private void datLaiThongTinNCC() {
        nhaCungCapDaChon = null;
        txtTenNCC.setText("N/A");
        txtTenNCC.setForeground(Color.GRAY);
        
        txtDiaChiNCC.setText("N/A");
        txtDiaChiNCC.setToolTipText(null);

        txtEmailNCC.setText("N/A");
    }

    /**
     * Cập nhật thông tin nhà cung cấp lên UI
     */
    private void capNhatThongTinNCC(NhaCungCap ncc) {
        nhaCungCapDaChon = ncc;
        txtTimNCC.setText(ncc.getSoDienThoai());
        txtTimNCC.setForeground(Color.BLACK);

        // Cập nhật Tên
        txtTenNCC.setText(ncc.getTenNhaCungCap());
        txtTenNCC.setForeground(new Color(0x007BFF)); // Màu xanh nổi bật
        txtTenNCC.setToolTipText(ncc.getTenNhaCungCap()); // Hiển thị tooltip nếu tên quá dài

        // Cập nhật Địa chỉ
        txtDiaChiNCC.setText(ncc.getDiaChi());
        txtDiaChiNCC.setToolTipText(ncc.getDiaChi());

        // Cập nhật Email
        txtEmailNCC.setText(ncc.getEmail() != null ? ncc.getEmail() : "N/A");
    }

    /**
     * Xử lý nghiệp vụ tìm nhà cung cấp
     */
private void xuLyTimNhaCungCap() {
        String keyword = txtTimNCC.getText().trim();
        if (keyword.isEmpty()) {
            datLaiThongTinNCC(); 
            return;
        }
        NhaCungCap ncc = nhaCungCapDAO.timNhaCungCapTheoMaHoacSDT(keyword);

        if (ncc != null) {
            if (!ncc.isHoatDong()) {
                JOptionPane.showMessageDialog(this, 
                    "Nhà cung cấp '" + ncc.getTenNhaCungCap() + "' đã ngừng hợp tác.\nVui lòng chọn nhà cung cấp khác!", 
                    "Cảnh báo", 
                    JOptionPane.WARNING_MESSAGE);
                

                datLaiThongTinNCC();
                
                txtTimNCC.selectAll();
                txtTimNCC.requestFocus();
            } else {
                capNhatThongTinNCC(ncc);
            }
        } else {
            datLaiThongTinNCC();
            txtTenNCC.setText("Không tìm thấy nhà cung cấp");
            txtTenNCC.setForeground(Color.RED);
            txtTimNCC.setForeground(Color.RED);
        }
    }

    /**
     * Tìm panel sản phẩm đã tồn tại trong danh sách
     */
    private ChiTietSanPhamPanel timPanelSanPham(String maSP) {
        Component[] components = pnDanhSachDon.getComponents();
        for (Component comp : components) {
            if (comp instanceof ChiTietSanPhamPanel panel) {
                if (panel.laySanPham().getMaSanPham().equals(maSP)) { // <-- ĐÃ VIỆT HÓA
                    return panel;
                }
            }
        }
        return null;
    }


    /**
     * Xử lý nghiệp vụ thêm 1 lô sản phẩm (thủ công)
     */
    private void xuLyThemLo() {
        String maSP = txtSearch.getText().trim();
        if (maSP.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã Sản Phẩm.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            txtSearch.requestFocus();
            return;
        }

        SanPham sp = sanPhamDAO.laySanPhamTheoMa(maSP);
        if (sp == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm với mã: " + maSP, "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtSearch.selectAll();
            return;
        }

        String maLoHienThi = String.format("LO-%06d", this.soLoTiepTheo);
        
        ArrayList<QuyCachDongGoi> dsQuyCach = quyCachDAO.layDanhSachQuyCachTheoSanPham(sp.getMaSanPham());
        QuyCachDongGoi qc_goc = quyCachDAO.timQuyCachGocTheoSanPham(sp.getMaSanPham());

        if (dsQuyCach == null || dsQuyCach.isEmpty() || qc_goc == null) {
            JOptionPane.showMessageDialog(this, "Sản phẩm '" + sp.getTenSanPham() + "' chưa được cấu hình Quy Cách Đóng Gói (hoặc thiếu Đơn Vị Gốc).\nVui lòng kiểm tra trong Quản lý sản phẩm.", "Lỗi cấu hình", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ThemLo_Dialog dialog = new ThemLo_Dialog(mainFrame, sp, maLoHienThi, dsQuyCach, qc_goc);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                int soLuongNhapDaQuyDoi = dialog.getSoLuongNhap();
                double donGiaGoc = dialog.getDonGiaNhap(); 
                DonViTinh dvtGoc = dialog.getDonViTinh(); 
                LoSanPham loMoi = dialog.getLoSanPham();

                ChiTietPhieuNhap chiTietMoi = new ChiTietPhieuNhap();
                chiTietMoi.setLoSanPham(loMoi);
                chiTietMoi.setDonViTinh(dvtGoc);
                chiTietMoi.setSoLuongNhap(soLuongNhapDaQuyDoi);
                chiTietMoi.setDonGiaNhap(donGiaGoc); 

                ChiTietSanPhamPanel panelSanPham = timPanelSanPham(sp.getMaSanPham());

                if (panelSanPham != null) {
                    if (!panelSanPham.layDonViTinh().equals(dvtGoc) || panelSanPham.layDonGia() != donGiaGoc) { 
                        JOptionPane.showMessageDialog(this,
                            String.format("Lỗi: Lô mới phải có cùng Đơn vị tính (%s) và Đơn giá (%,.0f đ) với các lô đã thêm.",
                                panelSanPham.layDonViTinh().getTenDonViTinh(), panelSanPham.layDonGia()), 
                            "Lỗi Thêm Lô", JOptionPane.ERROR_MESSAGE);
                        return; // Không thêm
                    }
                    panelSanPham.themLot(chiTietMoi);
                } else {
                    ChiTietSanPhamPanel newPanel = new ChiTietSanPhamPanel(sp, dvtGoc, donGiaGoc);
                    newPanel.themLot(chiTietMoi);
                    pnDanhSachDon.add(newPanel);
                    capNhatLaiSTT(); 
                }

                this.soLoTiepTheo++; 

                capNhatTongTienHang();
                pnDanhSachDon.revalidate();
                pnDanhSachDon.repaint();
                SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum()));

                txtSearch.setText("");
                txtSearch.requestFocus();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm lô: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }


    /**
     * Xử lý nghiệp vụ nhập phiếu (lưu vào CSDL)
     */
    private void xuLyNhapPhieu() {
        // ... (Code xử lý nhập phiếu giữ nguyên, không cần thay đổi)
        // ...
        if (nhaCungCapDaChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Nhà Cung Cấp.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            txtTimNCC.requestFocus();
            return;
        }
        if (nhanVienDangNhap == null) {
            JOptionPane.showMessageDialog(this, "Lỗi: Không có thông tin Nhân Viên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (pnDanhSachDon.getComponentCount() == 0) {
            JOptionPane.showMessageDialog(this, "Phiếu nhập chưa có sản phẩm nào.", "Phiếu nhập rỗng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Xác nhận nhập phiếu với nhà cung cấp '" + nhaCungCapDaChon.getTenNhaCungCap() + "'?",
            "Xác nhận nhập phiếu", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            connectDB.getInstance().connect();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        PhieuNhap phieuNhapMoi = new PhieuNhap();
        phieuNhapMoi.setMaPhieuNhap(phieuNhapDAO.taoMaPhieuNhap());
        phieuNhapMoi.setNgayNhap(LocalDate.now());
        phieuNhapMoi.setNhanVien(nhanVienDangNhap);
        phieuNhapMoi.setNhaCungCap(nhaCungCapDaChon);

        List<ChiTietPhieuNhap> dsChiTiet = new ArrayList<>();
        Component[] components = pnDanhSachDon.getComponents();
        for (Component comp : components) {
            if (comp instanceof ChiTietSanPhamPanel panel) {
                List<ChiTietPhieuNhap> dsLoCuaPanel = panel.layTatCaChiTiet(phieuNhapMoi); 
                dsChiTiet.addAll(dsLoCuaPanel);
            }
        }

        phieuNhapMoi.setChiTietPhieuNhapList(dsChiTiet);
        boolean success = phieuNhapDAO.themPhieuNhap(phieuNhapMoi);

        if (success) {
            hienThiHoaDon(phieuNhapMoi);

            JOptionPane.showMessageDialog(this, "Nhập phiếu thành công!\nMã phiếu: " + phieuNhapMoi.getMaPhieuNhap(),
                                          "Thành công", JOptionPane.INFORMATION_MESSAGE);

            // Reset form
            pnDanhSachDon.removeAll();
            capNhatTongTienHang();

            txtTimNCC.setText("");
            datLaiThongTinNCC(); // <-- ĐÃ VIỆT HÓA

            pnDanhSachDon.revalidate();
            pnDanhSachDon.repaint();

        } else {
            JOptionPane.showMessageDialog(this, "Nhập phiếu thất bại! Vui lòng kiểm tra log lỗi.",
                                          "Thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Tạo và hiển thị JDialog hóa đơn dựa trên thông tin PhieuNhap
     */
    private void hienThiHoaDon(PhieuNhap phieuNhap) {
        // ... (Code hiển thị hóa đơn giữ nguyên, không cần thay đổi)
        // ...
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
        pnHeader.add(taoNhanInDam(phieuNhap.getNgayNhap().format(fmtDate), labelFont)); 

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
        String[] columns = {"Tên sản phẩm", "Đơn vị tính", "Số lô", "Số lượng", "Đơn giá", "Thành tiền"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa
            }
        };

        for (ChiTietPhieuNhap ct : phieuNhap.getChiTietPhieuNhapList()) {
            model.addRow(new Object[]{
                ct.getLoSanPham().getSanPham().getTenSanPham(),
                ct.getDonViTinh().getTenDonViTinh(),
                ct.getLoSanPham().getMaLo(),
                ct.getSoLuongNhap(),
                df.format(ct.getDonGiaNhap()) + " đ",
                df.format(ct.getThanhTien()) + " đ"
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

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setRowHeight(25);

        JScrollPane scrollTable = new JScrollPane(table);
        centerPanel.add(scrollTable);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ===== 3. SOUTH: Tổng tiền và Nút Đóng =====
        JPanel pnFooter = new JPanel();
        pnFooter.setLayout(new BoxLayout(pnFooter, BoxLayout.Y_AXIS));
        pnFooter.setOpaque(false);

        pnFooter.add(taoDuongKeDut()); 
        pnFooter.add(Box.createVerticalStrut(10));

        JLabel lblTongCong = new JLabel(String.format("Tổng hóa đơn: %s đ", df.format(phieuNhap.getTongTien())));
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

    /** Helper để tạo JLabel in đậm */
    private JLabel taoNhanInDam(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font.deriveFont(Font.BOLD));
        return label;
    }

    /** Helper để tạo JLabel thường */
    private JLabel taoNhanThuong(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }

    /** Helper để tạo 1 đường gạch ngang đứt */
    private Component taoDuongKeDut() {
        JSeparator separator = new JSeparator();
        Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0);
        separator.setForeground(Color.GRAY);

        JPanel dashedLinePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.GRAY);
                g2d.setStroke(dashed);
                g2d.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
            }
        };
        dashedLinePanel.setOpaque(false);
        dashedLinePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));
        return dashedLinePanel;
    }

// ✅ ===================================================================
    // ✅ CLASS CHI TIẾT SẢN PHẨM (ĐÃ CẬP NHẬT STT VÀ NÚT XÓA)
    // ✅ ===================================================================
    class ChiTietSanPhamPanel extends JPanel {
        private SanPham sanPham;
        private DonViTinh donViTinh;
        private double donGia;
        private List<ChiTietPhieuNhap> dsChiTietCuaSP;

        // UI Components
        private JLabel lblSTT; // <-- MỚI: Label số thứ tự
        private JLabel lblTenSP;
        private JTextField txtTongSoLuong;
        private JLabel lblDonViTinh;
        private JLabel lblDonGia;
        private JLabel lblTongThanhTien;
        
        private JPanel pnDanhSachLo; 
        private JScrollPane scrollLots; 
        private JPanel pnRow2; 
        private JButton btnChonLo; 

        public ChiTietSanPhamPanel(SanPham sp, DonViTinh dvt, double donGia) {
            this.sanPham = sp;
            this.donViTinh = dvt;
            this.donGia = donGia;
            this.dsChiTietCuaSP = new ArrayList<>();

            setLayout(new BorderLayout(5, 5));
            setBackground(Color.WHITE);
            setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                new EmptyBorder(5, 10, 5, 10)
            ));

            // ----- HÀNG 1: Thông tin sản phẩm (GridBagLayout) -----
            JPanel pnMain = new JPanel();
            pnMain.setOpaque(false);
            pnMain.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 5, 0, 5);
            gbc.anchor = GridBagConstraints.CENTER; // Căn giữa theo chiều dọc
            gbc.fill = GridBagConstraints.VERTICAL;
            gbc.gridy = 0; 
            gbc.gridheight = 1; 

            // --- Cột 0: Số Thứ Tự (MỚI) ---
            gbc.gridx = 0; gbc.weightx = 0;
            lblSTT = new JLabel("1");
            lblSTT.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblSTT.setForeground(Color.black);
            lblSTT.setPreferredSize(new Dimension(30, 40));
            lblSTT.setHorizontalAlignment(SwingConstants.CENTER);
            pnMain.add(lblSTT, gbc);

            // --- Cột 1: Hình ảnh ---
            gbc.gridx = 1; gbc.weightx = 0;
            JLabel lblHinhAnh = new JLabel();
            lblHinhAnh.setBorder(new LineBorder(Color.LIGHT_GRAY));
            lblHinhAnh.setPreferredSize(new Dimension(80, 80));
            lblHinhAnh.setHorizontalAlignment(SwingConstants.CENTER);
            try {
                String imagePath = "/resources/images/" + sp.getHinhAnh(); 
                if (sp.getHinhAnh() != null && !sp.getHinhAnh().isBlank()) {
                    ImageIcon imgIcon = new ImageIcon(getClass().getResource(imagePath));
                    if (imgIcon.getIconWidth() != -1) { 
                         Image img = imgIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                         lblHinhAnh.setIcon(new ImageIcon(img));
                    }
                }
            } catch (Exception ex) {
                lblHinhAnh.setText("Ảnh"); 
            }
            pnMain.add(lblHinhAnh, gbc);

            // --- Cột 2: Tên SP ---
            gbc.gridx = 2; gbc.weightx = 1.0; // Chiếm phần dư
            gbc.anchor = GridBagConstraints.WEST; // Canh trái tên
            lblTenSP = new JLabel(sp.getTenSanPham());
            lblTenSP.setFont(new Font("Segoe UI", Font.BOLD, 16));
            pnMain.add(lblTenSP, gbc);
            
            // Reset anchor về Center cho các cột sau
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.weightx = 0;

            // --- Cột 3: Đơn vị tính ---
            gbc.gridx = 3; 
            lblDonViTinh = new JLabel(dvt.getTenDonViTinh()); 
            lblDonViTinh.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            lblDonViTinh.setPreferredSize(new Dimension(80, 30));
            lblDonViTinh.setHorizontalAlignment(SwingConstants.CENTER);
            pnMain.add(lblDonViTinh, gbc);

         // --- Cột 4: Tổng số lượng ---
            gbc.gridx = 4;
            gbc.fill = GridBagConstraints.NONE; // <--- QUAN TRỌNG: Thêm dòng này để không bị giãn chiều cao theo ảnh
            
            txtTongSoLuong = new JTextField("0"); 
            txtTongSoLuong.setFont(new Font("Segoe UI", Font.BOLD, 14));
            txtTongSoLuong.setForeground(Color.BLACK); // Màu chữ đen cho dễ nhìn
            txtTongSoLuong.setEditable(false); 
            txtTongSoLuong.setBackground(Color.WHITE); 
            txtTongSoLuong.setHorizontalAlignment(JTextField.CENTER); 
            
            // Set kích thước cố định cho ô nhập
            txtTongSoLuong.setPreferredSize(new Dimension(80, 30)); 
            txtTongSoLuong.setMinimumSize(new Dimension(80, 30));
            
            // Thêm viền nhẹ cho đẹp (tùy chọn)
            txtTongSoLuong.setBorder(new LineBorder(new Color(0xD1D5DB), 1));
            
            pnMain.add(txtTongSoLuong, gbc);

            // --- Cột 5: Đơn giá ---
            gbc.gridx = 5;
            lblDonGia = new JLabel(df.format(donGia) + " đ"); 
            lblDonGia.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            lblDonGia.setPreferredSize(new Dimension(120, 30));
            lblDonGia.setHorizontalAlignment(SwingConstants.RIGHT);
            pnMain.add(lblDonGia, gbc);

            // --- Cột 6: Tổng thành tiền ---
            gbc.gridx = 6;
            lblTongThanhTien = new JLabel("0 đ");
            lblTongThanhTien.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblTongThanhTien.setPreferredSize(new Dimension(140, 30));
            lblTongThanhTien.setHorizontalAlignment(SwingConstants.RIGHT);
            pnMain.add(lblTongThanhTien, gbc);

            // --- Cột 7: Nút Xóa (Đã chuyển xuống cuối) ---
            gbc.gridx = 7;
            JButton btnXoaSP = new JButton();
            // Lưu ý: Đảm bảo đường dẫn icon đúng
            ImageIcon icon = new ImageIcon(getClass().getResource("/resources/images/bin.png")); 
            btnXoaSP.setIcon(new ImageIcon(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
            btnXoaSP.setToolTipText("Xóa sản phẩm này");
            btnXoaSP.setPreferredSize(new Dimension(40, 40));
            btnXoaSP.setContentAreaFilled(false);
            btnXoaSP.setBorderPainted(false);
            btnXoaSP.setFocusPainted(false);
            btnXoaSP.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Thêm hiệu ứng tay
            btnXoaSP.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(ChiTietSanPhamPanel.this,
                    "Xóa tất cả các lô của sản phẩm '" + sanPham.getTenSanPham() + "'?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    pnDanhSachDon.remove(ChiTietSanPhamPanel.this);
                    capNhatTongTienHang();
                    
                    // Quan trọng: Cập nhật lại STT sau khi xóa
                    capNhatLaiSTT(); 

                    pnDanhSachDon.revalidate();
                    pnDanhSachDon.repaint();
                }
            });
            pnMain.add(btnXoaSP, gbc);
            
            add(pnMain, BorderLayout.CENTER);

            // ----- HÀNG 2: (Code giữ nguyên) -----
            pnRow2 = new JPanel(new BorderLayout(10, 5)); 
            pnRow2.setOpaque(false);

            btnChonLo = new JButton("Chọn Lô");
            btnChonLo.setFont(new Font("Segoe UI", Font.PLAIN, 14)); 
            btnChonLo.setMargin(new Insets(2, 8, 2, 8));
            btnChonLo.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnChonLo.setBackground(Color.WHITE);
            btnChonLo.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
            btnChonLo.setFocusPainted(false);
            btnChonLo.addActionListener(e -> xuLyChonLoNoiBo());
            
            JPanel pnButtonWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT,0, 0));
            pnButtonWrapper.setOpaque(false);
            pnButtonWrapper.add(btnChonLo);
            // Tăng lề trái để nút Chọn lô thẳng hàng với Tên SP (STT 30 + Ảnh 80 + Spacing ~15)
            pnButtonWrapper.setBorder(new EmptyBorder(0, 60, 0, 0)); 
            
            pnRow2.add(pnButtonWrapper, BorderLayout.WEST);

            pnDanhSachLo = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            pnDanhSachLo.setOpaque(true); 
            pnDanhSachLo.setBackground(Color.WHITE);
            
            scrollLots = new JScrollPane(pnDanhSachLo);
            scrollLots.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollLots.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            scrollLots.setBorder(null);
            scrollLots.setOpaque(false);
            scrollLots.getViewport().setOpaque(false);
            scrollLots.setPreferredSize(new Dimension(100, 45)); 

            pnRow2.add(scrollLots, BorderLayout.CENTER);
            add(pnRow2, BorderLayout.SOUTH);
            
            capNhatTongSoLuongVaTien();
        }

        /**
         * Hàm setter để cập nhật số thứ tự từ bên ngoài
         */
        public void setSTT(int stt) {
            lblSTT.setText(String.valueOf(stt));
        }

        // ... (Các hàm xuLyChonLoNoiBo, laySanPham, v.v. giữ nguyên như cũ)
        // Chỉ lưu ý sửa chỗ xóa lô cuối cùng:
        
        private void xoaLoKhoiPanel(ChiTietPhieuNhap chiTiet) {
            if (dsChiTietCuaSP.contains(chiTiet)) {
                dsChiTietCuaSP.remove(chiTiet);
            }
            xoaTagChiTiet(chiTiet);
            capNhatTongSoLuongVaTien(); 
            
            if (dsChiTietCuaSP.isEmpty()) {
                pnDanhSachDon.remove(this);
                // Cập nhật lại STT nếu panel tự hủy
                capNhatLaiSTT(); 
                
                pnDanhSachDon.revalidate();
                pnDanhSachDon.repaint();
                capNhatTongTienHang(); 
            }
        }

        // ... (Giữ nguyên phần còn lại của class ChiTietSanPhamPanel)
        // Copy lại các hàm xuLyChonLoNoiBo, getters, themLot, xoaTagChiTiet, capNhatTongSoLuongVaTien từ code cũ vào đây
        
        private void xuLyChonLoNoiBo() {
             // Copy y nguyên logic cũ
            SanPham sp = this.sanPham; 
            String maLoHienThi = String.format("LO-%06d", soLoTiepTheo);
            ArrayList<QuyCachDongGoi> dsQuyCach = quyCachDAO.layDanhSachQuyCachTheoSanPham(sp.getMaSanPham());
            QuyCachDongGoi qc_goc = quyCachDAO.timQuyCachGocTheoSanPham(sp.getMaSanPham());

            if (dsQuyCach == null || dsQuyCach.isEmpty() || qc_goc == null) {
                JOptionPane.showMessageDialog(this, "Sản phẩm chưa cấu hình Quy Cách.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ChonLo_Dialog dialog = new ChonLo_Dialog(mainFrame, sp, maLoHienThi, dsQuyCach, qc_goc, this.dsChiTietCuaSP);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                try {
                    double donGiaGoc = dialog.getDonGiaNhap(); 
                    DonViTinh dvtGoc = dialog.getDonViTinh(); 
                    if (!this.donViTinh.equals(dvtGoc) || this.donGia != donGiaGoc) {
                        JOptionPane.showMessageDialog(this, "Lỗi: ĐVT/Giá nhập không khớp lô cũ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return; 
                    }
                    ChiTietPhieuNhap ctCanSua = dialog.getChiTietCanSua();
                    int soLuongNhapMoi_Goc = dialog.getSoLuongNhap();

                    if (ctCanSua != null) {
                        if (soLuongNhapMoi_Goc > 0) {
                            ctCanSua.setSoLuongNhap(soLuongNhapMoi_Goc); 
                            xoaTagChiTiet(ctCanSua); 
                            dsChiTietCuaSP.remove(ctCanSua); 
                            themLot(ctCanSua);       
                        } else {
                            xoaLoKhoiPanel(ctCanSua); 
                        }
                    } else {
                        LoSanPham loMoi = dialog.getLoSanPham(); 
                        ChiTietPhieuNhap chiTietMoi = new ChiTietPhieuNhap();
                        chiTietMoi.setLoSanPham(loMoi);
                        chiTietMoi.setDonViTinh(dvtGoc);
                        chiTietMoi.setSoLuongNhap(soLuongNhapMoi_Goc);
                        chiTietMoi.setDonGiaNhap(donGiaGoc);
                        this.themLot(chiTietMoi);
                        if (loMoi.getMaLo().equals(maLoHienThi)) soLoTiepTheo++; 
                    }
                    capNhatTongTienHang();
                    pnDanhSachDon.revalidate();
                    pnDanhSachDon.repaint();
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }
        public SanPham laySanPham() { return sanPham; }
        public DonViTinh layDonViTinh() { return donViTinh; }
        public double layDonGia() { return donGia; }
        public double layTongThanhTien() { 
            double total = 0;
            for (ChiTietPhieuNhap ct : dsChiTietCuaSP) total += ct.getThanhTien();
            return total;
        }
        public List<ChiTietPhieuNhap> layTatCaChiTiet(PhieuNhap pn) { 
            for(ChiTietPhieuNhap ctpn : dsChiTietCuaSP) {
                ctpn.setPhieuNhap(pn); 
                ctpn.getLoSanPham().setSoLuongTon(ctpn.getSoLuongNhap()); 
            }
            return dsChiTietCuaSP;
        }
        private void xoaTagChiTiet(ChiTietPhieuNhap chiTiet) {
            String maLoCanXoa = chiTiet.getLoSanPham().getMaLo();
            for (Component comp : pnDanhSachLo.getComponents()) {
                if (comp instanceof JPanel pnlLoTag) {
                    if (pnlLoTag.getName() != null && pnlLoTag.getName().equals(maLoCanXoa)) {
                        pnDanhSachLo.remove(pnlLoTag);
                        return; 
                    }
                }
            }
        }
        public void themLot(ChiTietPhieuNhap chiTiet) {
            dsChiTietCuaSP.add(chiTiet);
            JPanel pnlLoTag = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
            pnlLoTag.setBackground(new Color(0x3B82F6));
            pnlLoTag.setBorder(new EmptyBorder(2, 5, 2, 5));
            pnlLoTag.setName(chiTiet.getLoSanPham().getMaLo());
            String loText = String.format("%s - %s - SL: %d", chiTiet.getLoSanPham().getMaLo(), chiTiet.getLoSanPham().getHanSuDung().format(fmtDate), chiTiet.getSoLuongNhap());
            JLabel lblLoInfo = new JLabel(loText);
            lblLoInfo.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblLoInfo.setForeground(Color.WHITE);
            pnlLoTag.add(lblLoInfo);
            JButton btnXoaLo = new JButton("X");
            btnXoaLo.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnXoaLo.setForeground(Color.WHITE);
            btnXoaLo.setMargin(new Insets(0, 2, 0, 2));
            btnXoaLo.setBorder(null);
            btnXoaLo.setContentAreaFilled(false);
            btnXoaLo.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnXoaLo.addActionListener(e -> {
                if (dsChiTietCuaSP.contains(chiTiet)) dsChiTietCuaSP.remove(chiTiet);
                pnDanhSachLo.remove(pnlLoTag);
                capNhatTongSoLuongVaTien();
                if (dsChiTietCuaSP.isEmpty()) {
                    pnDanhSachDon.remove(this);
                    capNhatLaiSTT(); // <-- CẬP NHẬT STT
                    pnDanhSachDon.revalidate();
                    pnDanhSachDon.repaint();
                    capNhatTongTienHang();
                }
            });
            pnlLoTag.add(btnXoaLo);
            pnDanhSachLo.add(pnlLoTag);
            capNhatTongSoLuongVaTien();
        }
        private void capNhatTongSoLuongVaTien() {
            int tongSoLuong = 0;
            double tongThanhTien = 0;
            for (ChiTietPhieuNhap ct : dsChiTietCuaSP) {
                tongSoLuong += ct.getSoLuongNhap();
                tongThanhTien += ct.getThanhTien();
            }
            txtTongSoLuong.setText(String.valueOf(tongSoLuong));
            lblTongThanhTien.setText(df.format(tongThanhTien) + " đ");
            capNhatTongTienHang();
            int totalHeight = 150; 
            setMaximumSize(new Dimension(Integer.MAX_VALUE, totalHeight));
            setPreferredSize(new Dimension(getPreferredSize().width, totalHeight));
            revalidate();
            repaint();
        }
    }
}