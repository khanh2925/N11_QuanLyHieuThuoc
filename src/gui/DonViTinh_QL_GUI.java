package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import customcomponent.PillButton;
import dao.DonViTinh_DAO;
import entity.DonViTinh;

@SuppressWarnings("serial")
public class DonViTinh_QL_GUI extends JPanel {

    private JPanel pnCenter;
    private JPanel pnHeader;
    private JTextField txtTimKiem;
    private JTable table;

    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    private List<DonViTinh> dsDonViTinh;
    private final DonViTinh_DAO dvtDAO = new DonViTinh_DAO();

    private PillButton btnThem;
    private PillButton btnCapNhat;
    private PillButton btnXoa;

    public DonViTinh_QL_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        initialize();
        addEvents();
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

        btnXoa = new PillButton("Xóa");
        btnXoa.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnXoa.setBounds(720, 27, 120, 40);
        pnHeader.add(btnXoa);

        btnCapNhat = new PillButton("Cập nhật");
        btnCapNhat.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnCapNhat.setBounds(920, 27, 120, 40);
        pnHeader.add(btnCapNhat);

        // ===== CENTER =====
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new LineBorder(new Color(200, 200, 200)));
        add(pnCenter, BorderLayout.CENTER);

        String[] columnNames = {"Mã Đơn Vị Tính", "Tên Đơn Vị Tính"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

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
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);

        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(400);

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

        // === Load dữ liệu từ DB ===
        loadDataToTable();
    }

    private void loadDataToTable() {
        model.setRowCount(0);
        dsDonViTinh = dvtDAO.layTatCaDonViTinh();
        for (DonViTinh dvt : dsDonViTinh) {
            model.addRow(new Object[]{dvt.getMaDonViTinh(), dvt.getTenDonViTinh()});
        }
    }

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

    private void handleThem() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        ThemDonViTinh_Dialog dialog = new ThemDonViTinh_Dialog(parentFrame);
        dialog.setVisible(true);

        DonViTinh dvtMoi = dialog.getDonViTinhMoi();
        if (dvtMoi != null) {
            if (dvtDAO.themDonViTinh(dvtMoi)) {
                dsDonViTinh.add(dvtMoi);
                model.addRow(new Object[]{dvtMoi.getMaDonViTinh(), dvtMoi.getTenDonViTinh()});
                JOptionPane.showMessageDialog(this, "Thêm đơn vị tính thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Không thể thêm đơn vị tính (lỗi CSDL).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleXoa() {
        int selectedViewRow = table.getSelectedRow();
        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn vị tính để xóa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedViewRow);
        String maDVT = (String) model.getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa đơn vị tính này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dvtDAO.xoaDonViTinh(maDVT)) {
                dsDonViTinh.removeIf(d -> d.getMaDonViTinh().equals(maDVT));
                model.removeRow(modelRow);
                JOptionPane.showMessageDialog(this, "Đã xóa đơn vị tính.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa đơn vị tính (đang được sử dụng).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleCapNhat() {
        int selectedViewRow = table.getSelectedRow();
        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn vị tính để cập nhật.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedViewRow);
        DonViTinh dvt = dsDonViTinh.get(modelRow);

        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        CapNhatDonViTinh_Dialog dialog = new CapNhatDonViTinh_Dialog(parentFrame, dvt);
        dialog.setVisible(true);

        if (dialog.isUpdateSuccess()) {
            if (dvtDAO.capNhatDonViTinh(dvt)) {
                model.setValueAt(dvt.getTenDonViTinh(), modelRow, 1);
                JOptionPane.showMessageDialog(this, "Cập nhật đơn vị tính thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại (lỗi CSDL).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
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
