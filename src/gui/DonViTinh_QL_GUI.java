package gui;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import customcomponent.ImagePanel;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import entity.DonViTinh;
import customcomponent.PillButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DonViTinh_QL_GUI extends JPanel {

    private JPanel pnCenter;
    private JPanel pnHeader;
    private JTextField txtTimKiem;
    private JTable table;

    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    // Biến thành viên để quản lý danh sách
    private List<DonViTinh> dsDonViTinh;
    private PillButton btnThem;
    private PillButton btnCapNhat;
    private PillButton btnXoa;

    public DonViTinh_QL_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        initialize();
        addEvents(); // Thêm phương thức gọi các sự kiện
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
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo tên đơn vị tính...");
        txtTimKiem.setForeground(Color.GRAY);
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtTimKiem.setBounds(10, 17, 420, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        pnHeader.add(txtTimKiem);
        
        btnThem = new PillButton("Thêm");
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnThem.setBounds(520, 27, 120, 40);
        pnHeader.add(btnThem);
        
        btnCapNhat = new PillButton("Cập nhật");
        btnCapNhat.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnCapNhat.setBounds(920, 27, 120, 40);
        pnHeader.add(btnCapNhat);
        
        btnXoa = new PillButton("Xoá");
        btnXoa.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnXoa.setBounds(720, 27, 120, 40);
        pnHeader.add(btnXoa);

        // ===== CENTER =====
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new LineBorder(new Color(200, 200, 200)));
        add(pnCenter, BorderLayout.CENTER);

        // Khởi tạo danh sách và nạp dữ liệu mẫu
        dsDonViTinh = new ArrayList<>();
        dsDonViTinh.add(new DonViTinh("DVT-001", "Hộp", "Đựng sản phẩm theo đơn vị hộp"));
        dsDonViTinh.add(new DonViTinh("DVT-002", "Vỉ", "Đựng thuốc theo từng vỉ"));
        dsDonViTinh.add(new DonViTinh("DVT-003", "Chai", "Sản phẩm dạng lỏng, đựng trong chai"));
        dsDonViTinh.add(new DonViTinh("DVT-004", "Tuýp", "Sản phẩm dạng kem, gel"));
        dsDonViTinh.add(new DonViTinh("DVT-005", "Viên", "Sản phẩm dạng viên nén, viên nang"));

        String[] columnNames = {"Mã Đơn Vị Tính", "Tên Đơn Vị Tính", "Mô Tả"};
        model = new DefaultTableModel(columnNames, 0) {
        	@Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp trên table
            }
        };

        for (DonViTinh dvt : dsDonViTinh) {
            addDonViTinhToTable(dvt);
        }

        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 18));
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
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setBackground(new Color(33, 150, 243));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 40));
        header.setReorderingAllowed(false);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);

        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(400);

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
    }

    /**
     * Phương thức đăng ký các sự kiện cho component
     */
    private void addEvents() {
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applySearchFilter();
            }
        });
        
        btnThem.addActionListener(e -> handleThem());
        btnXoa.addActionListener(e -> handleXoa());
        btnCapNhat.addActionListener(e -> handleCapNhat());
    }
    
    /**
     * Xử lý sự kiện nhấn nút Thêm.
     * Mở dialog thêm, nhận kết quả và cập nhật vào danh sách và bảng.
     */
    private void handleThem() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        ThemDonViTinh_Dialog dialog = new ThemDonViTinh_Dialog(parentFrame);
        dialog.setVisible(true);
        
        DonViTinh dvtMoi = dialog.getDonViTinhMoi();
        if (dvtMoi != null) {
            dsDonViTinh.add(dvtMoi);
            addDonViTinhToTable(dvtMoi);
            JOptionPane.showMessageDialog(this, "Thêm đơn vị tính thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Xử lý sự kiện nhấn nút Xóa.
     * Yêu cầu xác nhận, sau đó xóa khỏi danh sách và bảng.
     */
    private void handleXoa() {
        int selectedViewRow = table.getSelectedRow();
        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn vị tính để xóa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa đơn vị tính này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            int modelRow = table.convertRowIndexToModel(selectedViewRow);
            String maDVT = (String) model.getValueAt(modelRow, 0);
            
            // Xóa khỏi danh sách
            dsDonViTinh.removeIf(dvt -> dvt.getMaDonViTinh().equals(maDVT));
            
            // Xóa khỏi bảng
            model.removeRow(modelRow);
            
            JOptionPane.showMessageDialog(this, "Xóa đơn vị tính thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Xử lý sự kiện nhấn nút Cập nhật.
     * Mở dialog cập nhật, nhận kết quả và cập nhật lại danh sách và bảng.
     */
    private void handleCapNhat() {
        int selectedViewRow = table.getSelectedRow();
        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn vị tính để cập nhật.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = table.convertRowIndexToModel(selectedViewRow);
        String maDVT = (String) model.getValueAt(modelRow, 0);
        
        // Tìm DonViTinh trong danh sách để cập nhật
        DonViTinh dvtCanCapNhat = dsDonViTinh.stream()
            .filter(dvt -> dvt.getMaDonViTinh().equals(maDVT))
            .findFirst()
            .orElse(null);
        
        if (dvtCanCapNhat != null) {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            CapNhatDonViTinh_Dialog dialog = new CapNhatDonViTinh_Dialog(parentFrame, dvtCanCapNhat);
            dialog.setVisible(true);
            
            if (dialog.isUpdateSuccess()) {
                updateDonViTinhInTable(dvtCanCapNhat, modelRow);
                JOptionPane.showMessageDialog(this, "Cập nhật đơn vị tính thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void addDonViTinhToTable(DonViTinh dvt) {
        model.addRow(new Object[]{
            dvt.getMaDonViTinh(),
            dvt.getTenDonViTinh(),
            dvt.getMoTa()
        });
    }
    
    private void updateDonViTinhInTable(DonViTinh dvt, int row) {
        model.setValueAt(dvt.getTenDonViTinh(), row, 1);
        model.setValueAt(dvt.getMoTa(), row, 2);
    }
    
    private void applySearchFilter() {
        String text = txtTimKiem.getText();
        if (text.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            // Lọc không phân biệt chữ hoa/thường theo Tên Đơn Vị Tính (cột 1)
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý Đơn Vị Tính");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new DonViTinh_QL_GUI());
            frame.setVisible(true);
        });
    }
}