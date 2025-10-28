package gui;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import customcomponent.ImagePanel;
import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import entity.NhanVien;
import entity.TaiKhoan;

public class NhanVien_QL_GUI extends JPanel {

    private JPanel pnCenter;
    private JPanel pnHeader;
    private JTextField txtTimKiem;
    private JTable table;

    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    
    // THAY ĐỔI 1: Chuyển danh sách nhân viên thành biến thành viên
    private List<NhanVien> dsNhanVien;
	private PillButton btnThem;
	private PillButton btnSua;

    public NhanVien_QL_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());

        // ===== HEADER =====
        pnHeader = new JPanel();
        pnHeader.setPreferredSize(new Dimension(1073, 88));
        pnHeader.setBackground(new Color(0xE3F2F5));
        pnHeader.setLayout(null);
        add(pnHeader, BorderLayout.NORTH);

        txtTimKiem = new JTextField("");
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo tên nhân viên / SĐT");
        txtTimKiem.setForeground(Color.GRAY);
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTimKiem.setBounds(20, 27, 350, 44);
        txtTimKiem.setBorder(new RoundedBorder(20));
        pnHeader.add(txtTimKiem);


        
        btnThem=new PillButton("Thêm");
        pnHeader.add(btnThem);
        btnThem.setBounds(456, 30, 120, 40);
        btnThem.setLayout(null);
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 18));

        
        btnSua=new PillButton("Sửa");
        btnSua.setLayout(null);
        btnSua.setBounds(637, 29, 120, 40);
        pnHeader.add(btnSua);
        btnSua.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        btnThem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(NhanVien_QL_GUI.this);
                ThemNhanVien_Dialog dialog = new ThemNhanVien_Dialog(owner);
                dialog.setVisible(true);
                NhanVien nvMoi = dialog.getNhanVienMoi();
                if (nvMoi != null) {
                    dsNhanVien.add(nvMoi); // Thêm vào danh sách chính
                    addNhanVienToTable(nvMoi);
                    JOptionPane.showMessageDialog(owner, "Thêm nhân viên mới thành công!");
                }
            }
        });

        // =================================================================
        // THÊM SỰ KIỆN CLICK CHO NÚT CẬP NHẬT
        // =================================================================
        btnSua.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(NhanVien_QL_GUI.this, "Vui lòng chọn một nhân viên để cập nhật.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Lấy mã nhân viên từ JTable (phải convert về model index nếu có sort)
                int modelRow = table.convertRowIndexToModel(selectedRow);
                String maNV = model.getValueAt(modelRow, 0).toString();

                // Tìm nhân viên trong danh sách dsNhanVien
                NhanVien nvToUpdate = null;
                for (NhanVien nv : dsNhanVien) {
                    if (nv.getMaNhanVien().equals(maNV)) {
                        nvToUpdate = nv;
                        break;
                    }
                }
                
                if (nvToUpdate != null) {
                    JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(NhanVien_QL_GUI.this);
                    CapNhatNhanVien_Dialog dialog = new CapNhatNhanVien_Dialog(owner, nvToUpdate);
                    dialog.setVisible(true);

                    // Nếu cập nhật thành công, làm mới lại dòng trong bảng
                    if (dialog.isUpdateSuccess()) {
                        updateNhanVienInTable(nvToUpdate, modelRow);
                        JOptionPane.showMessageDialog(owner, "Cập nhật thông tin thành công!");
                    }
                }
            }
        });

        // ===== CENTER =====
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new LineBorder(new Color(200, 200, 200)));
        add(pnCenter, BorderLayout.CENTER);

        // Khởi tạo danh sách
        dsNhanVien = new ArrayList<>();
        try {
        	TaiKhoan tk1 = new TaiKhoan("TK123456", "user01", "Password@123");
            dsNhanVien.add(new NhanVien("NV1729424268", "Nguyễn Văn An", true, LocalDate.of(1990, 5, 15), "0901234567", "123 Lê Lợi, Quận 1, TPHCM", false, tk1, "SANG", true));
            
            TaiKhoan tk2 = new TaiKhoan("TK123457", "user02", "Password@123");
            dsNhanVien.add(new NhanVien("NV1729424269", "Trần Thị Bình", false, LocalDate.of(1995, 8, 20), "0912345678", "456 Nguyễn Trãi, Quận 5, TPHCM", false, tk2, "SANG", true));
            
            TaiKhoan tk3 = new TaiKhoan("TK123458", "admin01", "Password@123");
            dsNhanVien.add(new NhanVien("NV1729424270", "Lê Hoàng Cường", true, LocalDate.of(1988, 1, 30), "0987654321", "789 CMT8, Q.Tân Bình, TPHCM", true, tk3, "SANG", true));
            
            TaiKhoan tk4 = new TaiKhoan("TK123459", "dungpham", "Password@123");
            dsNhanVien.add(new NhanVien("NV1729424271", "Phạm Thị Dung", false, LocalDate.of(2001, 11, 10), "0933445566", "101 Võ Văn Ngân, TP.Thủ Đức", false, tk4, "SANG", true));
            
            TaiKhoan tk5 = new TaiKhoan("TK123460", "longvo", "Password@123");
            dsNhanVien.add(new NhanVien("NV1729424272", "Võ Minh Long", true, LocalDate.of(1999, 3, 25), "0977889900", "222 Pasteur, Quận 3, TPHCM", false, tk5, "SANG", false));
            
            TaiKhoan tk6 = new TaiKhoan("TK123461", "ngocdo", "Password@123");
            dsNhanVien.add(new NhanVien("NV1729424273", "Đỗ Thị Ngọc", false, LocalDate.of(2002, 7, 7), "0909090909", "333 Lê Văn Sỹ, Quận Phú Nhuận", false, tk6, "SANG", true));
            
            TaiKhoan tk7 = new TaiKhoan("TK123462", "haivan", "Password@123");
            dsNhanVien.add(new NhanVien("NV1729424274", "Hoàng Văn Hải", true, LocalDate.of(1992, 9, 1), "0918273645", "55 Nguyễn Xí, Quận Bình Thạnh", false, tk7, "SANG", true));
            
            TaiKhoan tk8 = new TaiKhoan("TK123463", "lanmai", "Password@123");
            dsNhanVien.add(new NhanVien("NV1729424275", "Mai Thị Lan", false, LocalDate.of(1998, 12, 12), "0944556677", "88 An Dương Vương, Quận 6", false, tk8, "SANG", true));
            
            TaiKhoan tk9 = new TaiKhoan("TK123464", "toanbui", "Password@123");
            dsNhanVien.add(new NhanVien("NV1729424276", "Bùi Thế Toàn", true, LocalDate.of(1996, 4, 18), "0965874123", "12 Trường Chinh, Quận 12", false, tk9, "SANG", true));
            
            
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        String[] columnNames = {"Mã NV", "Tên nhân viên", "Giới tính", "Ngày sinh", "Số điện thoại", "Địa chỉ", "Chức vụ", "Ca làm", "Trạng thái"};
        model = new DefaultTableModel(columnNames, 0);

        for (NhanVien nv : dsNhanVien) {
            addNhanVienToTable(nv);
        }

        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(34);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(204, 229, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setIntercellSpacing(new Dimension(8, 5));
        table.setBackground(Color.WHITE);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(new Color(33, 150, 243));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 40));
        header.setReorderingAllowed(false);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(8).setCellRenderer(centerRenderer);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(60);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(250);
        table.getColumnModel().getColumn(6).setPreferredWidth(80);
        table.getColumnModel().getColumn(7).setPreferredWidth(80);
        table.getColumnModel().getColumn(8).setPreferredWidth(80);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                setBorder(new EmptyBorder(0, 8, 0, 8));
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 248, 252));
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnCenter.add(scrollPane, BorderLayout.CENTER);
        
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applySearchFilter();
            }
        });
    }
    
    private void addNhanVienToTable(NhanVien nv) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        model.addRow(new Object[]{
            nv.getMaNhanVien(),
            nv.getTenNhanVien(),
            nv.isGioiTinh() ? "Nam" : "Nữ",  
            nv.getNgaySinh().format(dtf),     
            nv.getSoDienThoai(),
            nv.getDiaChi(),
            nv.isQuanLy() ? "Quản lý" : "Nhân viên",
            nv.getCaLam(),
            nv.isTrangThai() ? "Đang làm" : "Đã nghỉ" 
        });
    }
    
    /**
     * Cập nhật lại thông tin của một dòng trong JTable
     * @param nv Nhân viên đã được cập nhật thông tin
     * @param row Index của dòng cần cập nhật (trong model)
     */
    private void updateNhanVienInTable(NhanVien nv, int row) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        model.setValueAt(nv.getTenNhanVien(), row, 1);
        model.setValueAt(nv.isGioiTinh() ? "Nam" : "Nữ", row, 2);
        model.setValueAt(nv.getNgaySinh().format(dtf), row, 3);
        model.setValueAt(nv.getSoDienThoai(), row, 4);
        model.setValueAt(nv.getDiaChi(), row, 5);
        model.setValueAt(nv.isQuanLy() ? "Quản lý" : "Nhân viên", row, 6);
        model.setValueAt(nv.getCaLam(), row, 7);
        model.setValueAt(nv.isTrangThai() ? "Đang làm" : "Đã nghỉ", row, 8);
        // Có thể thêm cột trạng thái nếu cần
    }
    
    private void applySearchFilter() {
        String text = txtTimKiem.getText();
        
        if (text.trim().isEmpty() || txtTimKiem.getForeground().equals(Color.GRAY)) {
            sorter.setRowFilter(null);
        } else {
            List<RowFilter<Object, Object>> filters = new ArrayList<>();
            filters.add(RowFilter.regexFilter("(?i)" + text, 1)); 
            filters.add(RowFilter.regexFilter("(?i)" + text, 4)); 
            sorter.setRowFilter(RowFilter.orFilter(filters));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý Nhân Viên");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1600, 900);
            frame.setLocationRelativeTo(null); 
            frame.setContentPane(new NhanVien_QL_GUI());
            frame.setVisible(true);
        });
    }
}