package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import customcomponent.ClipTooltipRenderer;
import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import dao.SanPham_DAO;
import entity.SanPham;

import java.util.ArrayList;

/**
 * @author
 * @version 1.1
 * @since Oct 26, 2025
 */

public class SanPham_GUI extends JPanel implements ActionListener, MouseListener { // <<< IMPLEMENTS ActionListener, MouseListener

    // ===== KHAI BÁO THUỘC TÍNH =====
    private JPanel pnCenter, pnHeader, pnLoc;
    private DefaultTableModel modelSP;
    private JTable tblSP;
    private JScrollPane scrSP;
    private JTextField txtSearch;
    private PillButton btnThem, btnCapNhat, btnXemChiTiet;
    private JComboBox<String> cboLoaiHang;
    private DecimalFormat df = new DecimalFormat("#,##0 đ");

    private SanPham_DAO sanPhamDAO; // Khai báo DAO

    private final Color blueMint = new Color(180, 220, 240);
    private final Color pinkPastel = new Color(255, 200, 220);

    // ===== CONSTRUCTOR =====
    public SanPham_GUI() {
        sanPhamDAO = new SanPham_DAO(); // Khởi tạo DAO
        initialize();
    }

    // ===== KHỞI TẠO GIAO DIỆN =====
    private void initialize() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1537, 1168));

        UIManager.put("ComboBox.background", Color.WHITE);
        UIManager.put("ComboBox.selectionBackground", blueMint);
        UIManager.put("ComboBox.selectionForeground", Color.BLACK);
        UIManager.put("ComboBox.foreground", Color.BLACK);
        UIManager.put("ComboBox.disabledBackground", Color.WHITE);

        // ----- HEADER -----
        pnHeader = new JPanel(null);
        pnHeader.setPreferredSize(new Dimension(1073, 88));
        pnHeader.setBackground(new Color(0xE3F2F5));
        add(pnHeader, BorderLayout.NORTH);

        // Ô tìm kiếm
        txtSearch = new JTextField();
        txtSearch.setBounds(20, 17, 420, 60);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setBorder(new RoundedBorder(20));
        PlaceholderSupport.addPlaceholder(txtSearch, "Tìm kiếm sản phẩm theo mã");
        pnHeader.add(txtSearch);

        // Ô lọc loại sản phẩm
        pnLoc = new JPanel(null);
        pnLoc.setBounds(460, 10, 400, 70);
        pnLoc.setBorder(BorderFactory.createTitledBorder(new RoundedBorder(20), "Lọc theo tiêu chí"));
        pnLoc.setBackground(new Color(0, 0, 0, 0));
        pnHeader.add(pnLoc);

        cboLoaiHang = new JComboBox<>();
        cboLoaiHang.setFont(new Font("Tahoma", Font.PLAIN, 18));
        cboLoaiHang.setBounds(75, 19, 250, 40);
        cboLoaiHang.setBackground(Color.WHITE);
        pnLoc.add(cboLoaiHang);

        // Nút thêm
        btnThem = new PillButton("Thêm");
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnThem.setBounds(895, 30, 120, 40);
        btnThem.addActionListener(this); 
        pnHeader.add(btnThem);

        // Nút cập nhật
        btnCapNhat = new PillButton("Cập nhật");
        btnCapNhat.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnCapNhat.setBounds(1045, 30, 120, 40);
        pnHeader.add(btnCapNhat);

        // Nút xem chi tiết
        btnXemChiTiet = new PillButton("Xem chi tiết");
        btnXemChiTiet.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnXemChiTiet.setBounds(1210, 30, 140, 40);
        btnXemChiTiet.addActionListener(this); // Gắn sự kiện cho nút xem chi tiết
        pnHeader.add(btnXemChiTiet);

        // ----- CENTER -----
        pnCenter = new JPanel(new BorderLayout());
        add(pnCenter, BorderLayout.CENTER);

        initTable();
        loadLoaiSanPham();
        loadSanPham();
    }
    
    // <<< TRIỂN KHAI PHƯƠNG THỨC actionPERFORMED >>>
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source.equals(btnThem)) {
            handleThemSanPhamAction();
        } else if (source.equals(btnXemChiTiet)) {
            handleXemChiTietAction(tblSP.getSelectedRow()); // Xử lý khi nhấn nút
        }
        // Thêm logic cho các nút khác (btnCapNhat, etc.) tại đây khi cần
    }

    /**
     * Logic xử lý khi nút Thêm Sản phẩm được nhấn.
     */
    private void handleThemSanPhamAction() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        
        ThemSanPham_Dialog dialog = new ThemSanPham_Dialog(parentFrame);
        dialog.setVisible(true);
        
        SanPham sanPhamMoi = dialog.getSanPhamMoi();
        
        if (sanPhamMoi != null) {
            if (sanPhamDAO.createSanPham(sanPhamMoi)) { 
                JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadSanPham(); 
            } else {
                JOptionPane.showMessageDialog(this, "Thêm sản phẩm thất bại (Lỗi CSDL hoặc trùng Mã)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Logic xử lý khi xem chi tiết (từ nút hoặc click đúp).
     */
    private void handleXemChiTietAction(int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm để xem chi tiết.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String maSP = modelSP.getValueAt(selectedRow, 1).toString();
        
        // Theo yêu cầu: hiện thông báo số dòng đang chọn
        JOptionPane.showMessageDialog(this, 
                "Chức năng Xem Chi Tiết cho sản phẩm: " + maSP + "\n" +
                "Dòng đang chọn: " + (selectedRow + 1), 
                "Thông báo", 
                JOptionPane.INFORMATION_MESSAGE);
        
        // TODO: Khi có dialog, mở dialog XemChiTietSanPham_Dialog(parentFrame, maSP);
    }


    // ===== KHỞI TẠO BẢNG =====
    private void initTable() {
        String[] cols = {
            "Hình ảnh", "Mã sản phẩm", "Tên sản phẩm", "Loại sản phẩm",
            "Số đăng ký", "Đường dùng", "Giá nhập", "Giá bán",
            "Quy cách đóng gói", "Kệ bán", "Trạng thái"
        };

        modelSP = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblSP = new JTable(modelSP);
        scrSP = new JScrollPane(tblSP);
        pnCenter.add(scrSP);

        formatTable(tblSP);

        tblSP.setRowHeight(55);
        tblSP.setSelectionBackground(pinkPastel);
        tblSP.getTableHeader().setBackground(blueMint);
        
        // <<< GẮN MOUSE LISTENER CHO BẢNG >>>
        tblSP.addMouseListener(this);

        // Hiển thị ảnh sản phẩm
        tblSP.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                if (value instanceof ImageIcon) {
                    setIcon((ImageIcon) value);
                    setText("");
                } else {
                    setIcon(null);
                    setText(value == null ? "" : value.toString());
                }
            }
        });

        // Gắn tooltip cho các cột khác
        for (int col = 1; col < tblSP.getColumnCount(); col++) {
            tblSP.getColumnModel().getColumn(col).setCellRenderer(new ClipTooltipRenderer());
        }
    }
    
    // <<< TRIỂN KHAI PHƯƠNG THỨC MOUSE CLICKED >>>
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            // Xử lý click đúp (giả lập chức năng xem chi tiết)
            handleXemChiTietAction(tblSP.getSelectedRow());
        }
        // Click đơn không cần xử lý vì không có ô nhập liệu để điền.
    }
    
    // Các phương thức MouseListener khác không cần triển khai logic
    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
    // END MOUSELISTENER

    // ===== ĐỊNH DẠNG BẢNG =====
    private void formatTable(JTable table) {
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 18));
        table.getTableHeader().setBorder(null);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionBackground(new Color(180, 205, 230));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(JLabel.RIGHT);
        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setHorizontalAlignment(JLabel.LEFT);

        TableColumnModel m = table.getColumnModel();
        for (int i = 0; i < m.getColumnCount(); i++) {
            String col = m.getColumn(i).getHeaderValue().toString().toLowerCase();
            if (col.contains("mã")) m.getColumn(i).setCellRenderer(center);
            else if (col.contains("giá") || col.contains("tiền")) m.getColumn(i).setCellRenderer(right);
            else m.getColumn(i).setCellRenderer(left);
        }
    }

    // ===== NẠP DỮ LIỆU =====
    private void loadLoaiSanPham() {
        cboLoaiHang.addItem("Chọn loại sản phẩm");
        cboLoaiHang.addItem("Thuốc kê đơn");
        cboLoaiHang.addItem("Thuốc không kê đơn");
        cboLoaiHang.addItem("Thực phẩm chức năng");
        cboLoaiHang.addItem("Dụng cụ y tế");
    }

    private void loadSanPham() {
        ArrayList<Object[]> ds = sanPhamDAO.getSanPhamKemQuyCachNhoNhat();

        modelSP.setRowCount(0);
        for (Object[] sp : ds) {
            String imgName = (String) sp[0];

            // Ảnh sản phẩm
            ImageIcon icon = null;
            URL url = getClass().getResource("/images/" + imgName);
            if (url == null)
                url = getClass().getResource("/images/icon_anh_sp_null.png");
            if (url != null)
                icon = new ImageIcon(new ImageIcon(url).getImage()
                        .getScaledInstance(45, 45, Image.SCALE_SMOOTH));

            // Hiển thị định dạng đẹp
            String loaiHienThi = mapLoaiSanPham((String) sp[3]);
            String duongDungHienThi = mapDuongDung((String) sp[5]);

            modelSP.addRow(new Object[]{
                icon,
                sp[1], // Mã
                sp[2], // Tên
                loaiHienThi,
                sp[4], // Số đăng ký
                duongDungHienThi,
                df.format(((Number) sp[6]).doubleValue()), // Giá nhập
                df.format(((Number) sp[7]).doubleValue()), // Giá bán
                sp[8], // Quy cách nhỏ nhất (TenDonViTinh)
                sp[9], // Kệ
                sp[10] // Trạng thái
            });
        }
    }

    // ===== HÀM MAP ENUM → TÊN HIỂN THỊ =====
    private String mapLoaiSanPham(String name) {
        if (name == null) return "";
        switch (name) {
            case "THUOC": return "Thuốc";
            case "VAT_TU": return "Vật tư";
            case "THUC_PHAM_BO_SUNG": return "Thực phẩm bổ sung";
            case "THIET_BI_Y_TE": return "Thiết bị y tế";
            default: return name;
        }
    }

    private String mapDuongDung(String name) {
        if (name == null || name.isEmpty()) return "";
        switch (name) {
            case "UONG": return "Uống";
            case "TIEM": return "Tiêm";
            case "NHO":  return "Nhỏ";
            case "BOI":  return "Bôi";
            case "HIT":  return "Hít";
            case "NGAM": return "Ngậm";
            case "DAT":  return "Đặt";
            case "DAN":  return "Dán";
            default: return name;
        }
    }

    // ===== CHẠY DEMO =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý sản phẩm - Demo Data");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new SanPham_GUI());
            frame.setVisible(true);
        });
    }
}