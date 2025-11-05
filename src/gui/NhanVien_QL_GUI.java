package gui;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import connectDB.connectDB;
import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import dao.NhanVien_DAO;
import dao.TaiKhoan_DAO;
import entity.NhanVien;
import entity.TaiKhoan;

public class NhanVien_QL_GUI extends JPanel {

    private JTextField txtTimKiem;
    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    
    private final NhanVien_DAO nhanVien_DAO;
    private final TaiKhoan_DAO taiKhoan_DAO;

    // === THAY ĐỔI 1: Lưu danh sách TÀI KHOẢN (chứa nhân viên) ===
    private List<TaiKhoan> dsTaiKhoan;
    
	private PillButton btnThem;
	private PillButton btnSua;

    public NhanVien_QL_GUI() {
        nhanVien_DAO = new NhanVien_DAO();
        taiKhoan_DAO = new TaiKhoan_DAO();

        setPreferredSize(new Dimension(1537, 850));
        initialize(); // Khởi tạo UI

        // Kết nối CSDL và tải dữ liệu
        try {
            connectDB.getInstance().connect();
            System.out.println("Connected to DB");
            loadDataToTable(); // Tải dữ liệu
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến cơ sở dữ liệu.", "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void initialize() {
        setLayout(new BorderLayout());

        // Header
        JPanel pnHeader = new JPanel();
        pnHeader.setPreferredSize(new Dimension(1073, 88));
        pnHeader.setBackground(new Color(0xE3F2F5));
        pnHeader.setLayout(null);
        add(pnHeader, BorderLayout.NORTH);

        txtTimKiem = new JTextField("");
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo Mã NV / Tên / SĐT");
        txtTimKiem.setBounds(20, 27, 350, 44);
        txtTimKiem.setBorder(new RoundedBorder(20));
        pnHeader.add(txtTimKiem);
        
        btnThem = new PillButton("Thêm");
        btnThem.setBounds(456, 30, 120, 40);
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnHeader.add(btnThem);
        
        btnSua = new PillButton("Sửa");
        btnSua.setBounds(637, 29, 120, 40);
        btnSua.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnHeader.add(btnSua);
        
        // Center
        JPanel pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new LineBorder(new Color(200, 200, 200)));
        add(pnCenter, BorderLayout.CENTER);

        String[] columnNames = {"Mã NV", "Tên nhân viên", "Giới tính", "Ngày sinh", "Số điện thoại", "Địa chỉ", "Chức vụ", "Ca làm", "Trạng thái"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        setupTableUI();
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnCenter.add(scrollPane, BorderLayout.CENTER);
        
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        addEventListeners();
    }

    /**
     * Tải dữ liệu từ CSDL và hiển thị lên JTable
     */
    private void loadDataToTable() {
        model.setRowCount(0);
        
        // === THAY ĐỔI 2: Lấy TÀI KHOẢN (đã join) làm nguồn dữ liệu chính ===
        dsTaiKhoan = taiKhoan_DAO.layTatCaTaiKhoan();
        
        if (dsTaiKhoan == null || dsTaiKhoan.isEmpty()) {
            System.out.println("Không có dữ liệu tài khoản/nhân viên từ CSDL");
            return;
        }

        // Duyệt qua danh sách tài khoản
        for (TaiKhoan tk : dsTaiKhoan) {
            addDataToTable(tk); // Thêm vào table
        }
    }

    private void addEventListeners() {
        btnThem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleThemNhanVien();
            }
        });

        btnSua.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleSuaNhanVien();
            }
        });
        
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applySearchFilter();
            }
        });
    }
    
    /**
     * Xử lý sự kiện khi nhấn nút "Thêm"
     */
    private void handleThemNhanVien() {
        JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(NhanVien_QL_GUI.this);
        ThemNhanVien_Dialog dialog = new ThemNhanVien_Dialog(owner);
        dialog.setVisible(true);
        
        // === THAY ĐỔI 3: Giả định dialog trả về TaiKhoan (chứa NhanVien) ===
        // Lưu ý: Bạn cần sửa ThemNhanVien_Dialog để có phương thức getTaiKhoanMoi()
        TaiKhoan tkMoi = dialog.getTaiKhoanMoi(); 
        
        if (tkMoi != null) {
            NhanVien nvMoi = tkMoi.getNhanVien(); // Lấy nhân viên từ tài khoản

            // Thêm Nhân viên trước (vì Tài khoản tham chiếu đến Nhân viên)
            boolean themNVSuccess = nhanVien_DAO.themNhanVien(nvMoi);
            
            if (themNVSuccess) {
                // Thêm Tài khoản sau
                boolean themTKSuccess = taiKhoan_DAO.themTaiKhoan(tkMoi);
                
                if (themTKSuccess) {
                    dsTaiKhoan.add(tkMoi); // Thêm vào danh sách TÀI KHOẢN
                    addDataToTable(tkMoi); // Thêm vào table
                    JOptionPane.showMessageDialog(owner, "Thêm nhân viên mới thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(owner, "Thêm nhân viên thành công nhưng thêm tài khoản thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    // Cần logic rollback (xóa nhân viên đã lỡ thêm)
                }
            } else {
                JOptionPane.showMessageDialog(owner, "Thêm nhân viên thất bại. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Sửa"
     */
    private void handleSuaNhanVien() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân viên để cập nhật.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = table.convertRowIndexToModel(selectedRow);
        String maNV = model.getValueAt(modelRow, 0).toString();

        // === THAY ĐỔI 4: Tìm TÀI KHOẢN trong danh sách dựa trên Mã NV ===
        TaiKhoan tkToUpdate = dsTaiKhoan.stream()
            .filter(tk -> tk.getNhanVien().getMaNhanVien().equals(maNV))
            .findFirst()
            .orElse(null);
        
        if (tkToUpdate != null) {
            JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(NhanVien_QL_GUI.this);
            
            // Lưu ý: Bạn cần sửa CapNhatNhanVien_Dialog để nhận TAIKHOAN
            CapNhatNhanVien_Dialog dialog = new CapNhatNhanVien_Dialog(owner, tkToUpdate);
            dialog.setVisible(true);

            if (dialog.isUpdateSuccess()) {
                // Cập nhật thông tin nhân viên
                boolean updateNVSuccess = nhanVien_DAO.capNhatNhanVien(tkToUpdate.getNhanVien());
                // Cập nhật thông tin tài khoản (tên đăng nhập, mật khẩu)
                boolean updateTKSuccess = taiKhoan_DAO.capNhatTaiKhoan(tkToUpdate);

                if (updateNVSuccess && updateTKSuccess) {
                    updateDataInTable(tkToUpdate, modelRow); // Cập nhật dòng trong table
                    JOptionPane.showMessageDialog(owner, "Cập nhật thông tin thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(owner, "Cập nhật thất bại. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    /**
     * Thêm một dòng vào JTable từ đối tượng TaiKhoan
     */
    private void addDataToTable(TaiKhoan tk) {
        NhanVien nv = tk.getNhanVien(); // Lấy nhân viên từ tài khoản
        if (nv == null) return; // An toàn nếu nhân viên bị null

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String ngaySinhStr = (nv.getNgaySinh() != null) ? nv.getNgaySinh().format(dtf) : "N/A";

        model.addRow(new Object[]{
            nv.getMaNhanVien(),
            nv.getTenNhanVien(),
            nv.isGioiTinh() ? "Nam" : "Nữ",  
            ngaySinhStr,     
            nv.getSoDienThoai(),
            nv.getDiaChi(),
            nv.isQuanLy() ? "Quản lý" : "Nhân viên",
            nv.getCaLam(),
            nv.isTrangThai() ? "Đang làm" : "Đã nghỉ" 
        });
    }
    
    /**
     * Cập nhật lại thông tin của một dòng trong JTable
     */
    private void updateDataInTable(TaiKhoan tk, int row) {
        NhanVien nv = tk.getNhanVien();
        if (nv == null) return;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String ngaySinhStr = (nv.getNgaySinh() != null) ? nv.getNgaySinh().format(dtf) : "N/A";

        model.setValueAt(nv.getTenNhanVien(), row, 1);
        model.setValueAt(nv.isGioiTinh() ? "Nam" : "Nữ", row, 2);
        model.setValueAt(ngaySinhStr, row, 3);
        model.setValueAt(nv.getSoDienThoai(), row, 4);
        model.setValueAt(nv.getDiaChi(), row, 5);
        model.setValueAt(nv.isQuanLy() ? "Quản lý" : "Nhân viên", row, 6);
        model.setValueAt(nv.getCaLam(), row, 7);
        model.setValueAt(nv.isTrangThai() ? "Đang làm" : "Đã nghỉ", row, 8);
    }
    
    /**
     * Lọc JTable dựa trên nội dung của JTextField tìm kiếm
     */
    private void applySearchFilter() {
        String text = txtTimKiem.getText();
        
        if (text.trim().isEmpty() || "Tìm kiếm theo Mã NV / Tên / SĐT".equals(text)) {
            sorter.setRowFilter(null);
        } else {
            // Lọc theo Mã (cột 0), Tên (cột 1), SĐT (cột 4)
            List<RowFilter<Object, Object>> filters = new ArrayList<>();
            filters.add(RowFilter.regexFilter("(?i)" + text, 0)); 
            filters.add(RowFilter.regexFilter("(?i)" + text, 1)); 
            filters.add(RowFilter.regexFilter("(?i)" + text, 4)); 
            sorter.setRowFilter(RowFilter.orFilter(filters));
        }
    }
    
    /**
     * Thiết lập giao diện cho JTable
     */
    private void setupTableUI() {
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

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã NV
        table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);   // Tên
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Giới tính
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Ngày sinh
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // SĐT
        table.getColumnModel().getColumn(5).setCellRenderer(leftRenderer);   // Địa chỉ
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer); // Chức vụ
        table.getColumnModel().getColumn(7).setCellRenderer(centerRenderer); // Ca làm
        table.getColumnModel().getColumn(8).setCellRenderer(centerRenderer); // Trạng thái
        
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(5).setPreferredWidth(250);

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
