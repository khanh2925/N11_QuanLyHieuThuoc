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

public class DonViTinh_QL_GUI extends JPanel {

    private JPanel pnCenter;
    private JPanel pnHeader;
    private JTextField txtTimKiem;
    private JTable table;
    private JLabel lbThem;

    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    
    // Biến thành viên để quản lý danh sách
    private List<DonViTinh> dsDonViTinh;

    public DonViTinh_QL_GUI() {
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
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo tên đơn vị tính...");
        txtTimKiem.setForeground(Color.GRAY);
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTimKiem.setBounds(20, 27, 350, 44);
        txtTimKiem.setBorder(new RoundedBorder(20));
        pnHeader.add(txtTimKiem);

        ImagePanel btnThem = new ImagePanel(new ImageIcon(getClass().getResource("/images/add.png")).getImage());
        btnThem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThem.setBounds(456, 27, 30, 30);
        pnHeader.add(btnThem);
        
        lbThem = new JLabel("Thêm", SwingConstants.CENTER);
        lbThem.setBounds(435, 58, 70, 19);
        pnHeader.add(lbThem);
        lbThem.setFont(new Font("Arial", Font.BOLD, 16));
        lbThem.setForeground(Color.BLACK);
        
        ImagePanel btnSua = new ImagePanel(new ImageIcon(getClass().getResource("/images/edit.png")).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        btnSua.setBounds(577, 27, 30, 30);
        btnSua.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnHeader.add(btnSua);
        
        JLabel lblSua = new JLabel("Cập nhật", SwingConstants.CENTER);
        lblSua.setBounds(553, 55, 70, 25);
        pnHeader.add(lblSua);
        lblSua.setFont(new Font("Arial", Font.BOLD, 16));
        lblSua.setForeground(Color.BLACK);
        
        // --- SỰ KIỆN NÚT THÊM ---
        btnThem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(DonViTinh_QL_GUI.this);
                ThemDonViTinh_Dialog dialog = new ThemDonViTinh_Dialog(owner);
                dialog.setVisible(true);
                
                DonViTinh dvtMoi = dialog.getDonViTinhMoi();
                if (dvtMoi != null) {
                    dsDonViTinh.add(dvtMoi);
                    addDonViTinhToTable(dvtMoi);
                    JOptionPane.showMessageDialog(owner, "Thêm đơn vị tính thành công!");
                }
            }
        });

        // --- SỰ KIỆN NÚT CẬP NHẬT ---
        btnSua.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(DonViTinh_QL_GUI.this, "Vui lòng chọn một đơn vị tính để cập nhật.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int modelRow = table.convertRowIndexToModel(selectedRow);
                String maDVT = model.getValueAt(modelRow, 0).toString();

                DonViTinh dvtToUpdate = dsDonViTinh.stream()
                        .filter(dvt -> dvt.getMaDonViTinh().equals(maDVT))
                        .findFirst()
                        .orElse(null);
                
                if (dvtToUpdate != null) {
                    JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(DonViTinh_QL_GUI.this);
                    CapNhatDonViTinh_Dialog dialog = new CapNhatDonViTinh_Dialog(owner, dvtToUpdate);
                    dialog.setVisible(true);

                    if (dialog.isUpdateSuccess()) {
                        updateDonViTinhInTable(dvtToUpdate, modelRow);
                        JOptionPane.showMessageDialog(owner, "Cập nhật thành công!");
                    }
                }
            }
        });


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
        model = new DefaultTableModel(columnNames, 0);

        for (DonViTinh dvt : dsDonViTinh) {
            addDonViTinhToTable(dvt);
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
        
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applySearchFilter();
            }
        });
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