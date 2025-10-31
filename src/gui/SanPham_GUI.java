package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.awt.event.*;
import java.util.ArrayList;

import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import dao.SanPham_DAO;
import entity.SanPham;
import enums.DuongDung;
import enums.LoaiSanPham;

public class SanPham_GUI extends JPanel implements ActionListener, MouseListener {

    private JPanel pnCenter, pnHeader, pnLoc;
    private DefaultTableModel modelSP;
    private JTable tblSP;
    private JScrollPane scrSP;
    private JTextField txtSearch;
    private PillButton btnThem, btnCapNhat, btnXoa, btnXemChiTiet;
    private JComboBox<String> cboLoaiHang;
    
    // DecimalFormat để hiển thị tiền tệ có dấu phân cách
    private DecimalFormat df = new DecimalFormat("#,##0 đ"); 
    private SanPham_DAO sanPhamDAO;

    // Định nghĩa màu sắc
    private final Color blueMint = new Color(180, 220, 240);
    private final Color pinkPastel = new Color(255, 200, 220);

    public SanPham_GUI() {
        // Khởi tạo DAO ngay khi tạo đối tượng GUI
        sanPhamDAO = new SanPham_DAO(); 
        initialize();
    }

    // --- KHỞI TẠO GIAO DIỆN CHÍNH ---
    
    private void initialize() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1537, 1168));

        // 1. HEADER (Vùng tìm kiếm và nút chức năng)
        pnHeader = new JPanel(null);
        pnHeader.setPreferredSize(new Dimension(1073, 88));
        pnHeader.setBackground(new Color(0xE3F2F5));
        add(pnHeader, BorderLayout.NORTH);

        setupHeaderComponents();

        // 2. CENTER (Bảng dữ liệu)
        pnCenter = new JPanel(new BorderLayout());
        add(pnCenter, BorderLayout.CENTER);

        initTable();
        loadSanPham(); // Tải dữ liệu ban đầu
    }
    
    private void setupHeaderComponents() {
        // Ô tìm kiếm
        txtSearch = new JTextField();
        txtSearch.setBounds(20, 17, 420, 60);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtSearch.setBorder(new RoundedBorder(20));
        PlaceholderSupport.addPlaceholder(txtSearch, "Tìm kiếm sản phẩm theo tên hoặc mã");
        pnHeader.add(txtSearch);
        txtSearch.addActionListener(e -> timKiemSanPham()); // Sự kiện khi nhấn Enter

        // Panel Lọc theo loại hàng
        pnLoc = new JPanel(null);
        pnLoc.setBounds(460, 10, 400, 70);
        pnLoc.setBorder(BorderFactory.createTitledBorder(new RoundedBorder(20), "Lọc loại"));
        pnLoc.setBackground(new Color(0, 0, 0, 0)); // Nền trong suốt
        pnHeader.add(pnLoc);

        cboLoaiHang = new JComboBox<>();
        cboLoaiHang.setFont(new Font("Tahoma", Font.PLAIN, 18));
        cboLoaiHang.setBounds(75, 19, 250, 40);
        pnLoc.add(cboLoaiHang);
        loadLoaiSanPham();
        cboLoaiHang.addActionListener(e -> locTheoLoai()); // Sự kiện khi chọn loại

        // Các nút chức năng
        btnThem = new PillButton("Thêm");
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnThem.setBounds(895, 30, 120, 40);
        btnThem.addActionListener(this);
        pnHeader.add(btnThem);

        btnCapNhat = new PillButton("Cập nhật");
        btnCapNhat.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnCapNhat.setBounds(1045, 30, 120, 40);
        btnCapNhat.addActionListener(this);
        pnHeader.add(btnCapNhat);

        btnXoa = new PillButton("Xóa");
        btnXoa.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnXoa.setBounds(1185, 30, 100, 40);
        btnXoa.addActionListener(this);
        pnHeader.add(btnXoa);

        btnXemChiTiet = new PillButton("Chi tiết");
        btnXemChiTiet.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnXemChiTiet.setBounds(1300, 30, 140, 40);
        btnXemChiTiet.addActionListener(this);
        pnHeader.add(btnXemChiTiet);
    }

    // --- XỬ LÝ SỰ KIỆN CỦA NÚT ---

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        // Lấy JFrame cha để mở Dialog (quan trọng)
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this); 

        if (src.equals(btnThem)) {
            // NOTE: Cần có class ThemSanPham_Dialog để chức năng này hoạt động
            ThemSanPham_Dialog dialog = new ThemSanPham_Dialog(parent); 
            dialog.setVisible(true);
            if (dialog.isCreated()) loadSanPham();
        }
        else if (src.equals(btnCapNhat)) {
            int row = tblSP.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần cập nhật!");
                return;
            }
            // Lấy Mã SP từ cột thứ 1 (index 1)
            String maSP = modelSP.getValueAt(row, 1).toString(); 
            SanPham sp = sanPhamDAO.laySanPhamTheoMa(maSP);
            // NOTE: Cần có class CapNhatSanPham_Dialog để chức năng này hoạt động
            CapNhatSanPham_Dialog dlg = new CapNhatSanPham_Dialog(parent, sp); 
            dlg.setVisible(true);
            if (dlg.isUpdated()) loadSanPham();
        }
        else if (src.equals(btnXoa)) {
            int row = tblSP.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Chọn sản phẩm cần xóa!");
                return;
            }
            String maSP = modelSP.getValueAt(row, 1).toString();
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc muốn xóa sản phẩm " + maSP + " không?", 
                "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (sanPhamDAO.xoaSanPham(maSP)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                    loadSanPham();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else if (src.equals(btnXemChiTiet)) {
            int row = tblSP.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Chọn sản phẩm để xem chi tiết!");
                return;
            }
            String maSP = modelSP.getValueAt(row, 1).toString();
            SanPham sp = sanPhamDAO.laySanPhamTheoMa(maSP);
            if (sp != null)
                // Hiển thị thông tin chi tiết cơ bản của sản phẩm
                JOptionPane.showMessageDialog(this,
                        "Mã: " + sp.getMaSanPham() + "\nTên: " + sp.getTenSanPham() +
                        "\nGiá nhập: " + df.format(sp.getGiaNhap()) +
                        "\nKệ bán: " + sp.getKeBanSanPham(),
                        "Chi tiết sản phẩm", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // --- KHỞI TẠO VÀ ĐỊNH DẠNG BẢNG ---

    private void initTable() {
        String[] cols = {
            "Hình ảnh", "Mã sản phẩm", "Tên sản phẩm", "Loại sản phẩm",
            "Số đăng ký", "Đường dùng", "Giá nhập", "Giá bán",
            "Kệ bán", "Trạng thái"
        };

        modelSP = new DefaultTableModel(cols, 0) {
            // NOTE: Bật tính năng hiển thị ImageIcon cho cột đầu tiên (index 0)
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return ImageIcon.class;
                return Object.class;
            }
        };

        tblSP = new JTable(modelSP);
        scrSP = new JScrollPane(tblSP);
        pnCenter.add(scrSP);
        
        formatTable(tblSP); // Định dạng chung cho bảng

        // Định dạng màu và sự kiện riêng
        tblSP.setRowHeight(55);
        tblSP.setSelectionBackground(pinkPastel);
        tblSP.getTableHeader().setBackground(blueMint);
        tblSP.addMouseListener(this);
    }
    
    private void formatTable(JTable table) {
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 18));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        table.setShowGrid(false);
        table.setRowHeight(50);
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(JLabel.RIGHT);

        TableColumnModel m = table.getColumnModel();
        // Cố định kích thước cột Hình ảnh và Mã sản phẩm
        m.getColumn(0).setMinWidth(60);
        m.getColumn(0).setMaxWidth(60);
        m.getColumn(1).setMinWidth(120);
        m.getColumn(1).setMaxWidth(120);
        
        // Đặt căn phải cho cột giá, còn lại căn giữa
        for (int i = 2; i < m.getColumnCount(); i++) {
            String col = m.getColumn(i).getHeaderValue().toString().toLowerCase();
            if (col.contains("giá")) m.getColumn(i).setCellRenderer(right);
            else m.getColumn(i).setCellRenderer(center);
        }
    }

    // --- XỬ LÝ DỮ LIỆU (LOAD/TÌM KIẾM/LỌC) ---
    
    private void loadLoaiSanPham() {
        cboLoaiHang.addItem("Tất cả");
        // NOTE: Đổ dữ liệu từ enum LoaiSanPham vào JComboBox
        for (LoaiSanPham loai : LoaiSanPham.values()) {
            cboLoaiHang.addItem(loai.name());
        }
    }

    /** 🔹 Nạp tất cả sản phẩm */
    private void loadSanPham() {
        ArrayList<SanPham> ds = sanPhamDAO.layTatCaSanPham();
        hienThiDanhSach(ds);
    }

    /** 🔹 Tìm kiếm theo tên hoặc mã */
    private void timKiemSanPham() {
        String keyword = txtSearch.getText().trim();
        ArrayList<SanPham> ds = sanPhamDAO.timKiemSanPham(keyword);
        hienThiDanhSach(ds);
    }

    /** 🔹 Lọc theo loại từ ComboBox */
    private void locTheoLoai() {
        String selected = (String) cboLoaiHang.getSelectedItem();
        if (selected == null || selected.equals("Tất cả")) {
            loadSanPham(); // Hiển thị tất cả nếu chọn "Tất cả"
            return;
        }

        try {
            // Chuyển tên loại từ ComboBox sang Enum
            LoaiSanPham loai = LoaiSanPham.valueOf(selected);
            ArrayList<SanPham> ds = sanPhamDAO.laySanPhamTheoLoai(loai);
            hienThiDanhSach(ds);
        } catch (IllegalArgumentException e) {
            loadSanPham(); // Tránh lỗi nếu giá trị trong ComboBox không hợp lệ
        }
    }

    /** Hiển thị danh sách sản phẩm lên bảng */
    private void hienThiDanhSach(ArrayList<SanPham> ds) {
        modelSP.setRowCount(0);
        for (SanPham sp : ds) {
            ImageIcon icon = null;
            // NOTE: Tải ảnh sản phẩm (hoặc ảnh null nếu không tìm thấy)
            URL url = getClass().getResource("/images/" + sp.getHinhAnh()); 
            if (url == null)
                url = getClass().getResource("/images/icon_anh_sp_null.png");
            if (url != null)
                icon = new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH));

            modelSP.addRow(new Object[]{
                icon,
                sp.getMaSanPham(),
                sp.getTenSanPham(),
                mapLoaiSanPham(sp.getLoaiSanPham()), // Chuyển Enum sang chuỗi mô tả
                sp.getSoDangKy(),
                mapDuongDung(sp.getDuongDung()), // Chuyển Enum sang chuỗi mô tả
                df.format(sp.getGiaNhap()),
                df.format(sp.getGiaBan()),
                sp.getKeBanSanPham(),
                sp.isHoatDong() ? "Đang bán" : "Ngừng bán"
            });
        }
    }

    // --- CÁC HÀM MAPPER (Chuyển đổi Enum sang String) ---
    
    // NOTE: Các hàm này chuyển Enum (LoaiSanPham, DuongDung) thành chuỗi mô tả để hiển thị
    private String mapLoaiSanPham(LoaiSanPham loai) {
        if (loai == null) return "";
        return loai.getTenLoai();
    }

    private String mapDuongDung(DuongDung dd) {
        if (dd == null) return "";
        return dd.getMoTa();
    }

    // --- XỬ LÝ SỰ KIỆN CHUỘT ---

    @Override
    public void mouseClicked(MouseEvent e) {
        // NOTE: Xử lý click đúp chuột để xem chi tiết
        if (e.getClickCount() == 2) { 
            int row = tblSP.getSelectedRow();
            if (row >= 0) {
                 btnXemChiTiet.doClick(); // Kích hoạt sự kiện nút "Chi tiết"
            }
        }
    }

    // Các hàm lắng nghe sự kiện chuột không dùng đến
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    // --- MAIN (Hàm chạy thử) ---

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý sản phẩm");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new SanPham_GUI());
            frame.setVisible(true);
        });
    }
}