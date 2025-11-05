package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;

import connectDB.connectDB;
import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import dao.NhaCungCap_DAO;
import entity.NhaCungCap;

@SuppressWarnings("serial")
public class NhaCungCap_GUI extends JPanel implements ActionListener, MouseListener {

    private JPanel pnCenter;
    private JPanel pnHeader;
    private JTextField txtTimKiem;
    private JTable table;

    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    private PillButton btnThem;
    private PillButton btnCapNhat;

    private NhaCungCap_DAO nccDAO;
    private List<NhaCungCap> dsNhaCungCap;

    public NhaCungCap_GUI() {
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
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo tên, SĐT hoặc email nhà cung cấp...");
        txtTimKiem.setForeground(Color.GRAY);
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtTimKiem.setBounds(10, 17, 450, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        pnHeader.add(txtTimKiem);

        btnThem = new PillButton("Thêm");
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnThem.setBounds(500, 26, 120, 40);
        pnHeader.add(btnThem);

        btnCapNhat = new PillButton("Cập nhật");
        btnCapNhat.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnCapNhat.setBounds(640, 26, 140, 40);
        pnHeader.add(btnCapNhat);

        // ===== CENTER =====
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new LineBorder(new Color(200, 200, 200)));
        add(pnCenter, BorderLayout.CENTER);

        String[] columnNames = {
            "Mã nhà cung cấp", 
            "Tên nhà cung cấp", 
            "Số điện thoại", 
            "Email", 
            "Địa chỉ",
            "Trạng thái"
        };
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

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

        // Căn giữa các cột cần thiết
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        table.getColumnModel().getColumn(0).setPreferredWidth(130);
        table.getColumnModel().getColumn(1).setPreferredWidth(230);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        table.getColumnModel().getColumn(4).setPreferredWidth(320);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
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

        // ===== DAO =====
        try {
            connectDB.getInstance().connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        nccDAO = new NhaCungCap_DAO();

        // ===== Load dữ liệu =====
        loadTableData();

        // ===== SỰ KIỆN =====
        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilters();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilters();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {}
        });

        btnThem.addActionListener(this);
        btnCapNhat.addActionListener(this);
        table.addMouseListener(this);
    }

    private void loadTableData() {
        dsNhaCungCap = nccDAO.layTatCaNhaCungCap();
        model.setRowCount(0);

        for (NhaCungCap ncc : dsNhaCungCap) {
            model.addRow(new Object[]{
                ncc.getMaNhaCungCap(),
                ncc.getTenNhaCungCap(),
                ncc.getSoDienThoai(),
                ncc.getEmail(),
                ncc.getDiaChi(),
                ncc.isHoatDong() ? "Hoạt động" : "Ngừng"
            });
        }
    }

    private void applyFilters() {
        String text = txtTimKiem.getText().trim();
        if (text.isEmpty() || txtTimKiem.getForeground().equals(Color.GRAY)) {
            sorter.setRowFilter(null);
            return;
        }

        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 1)); // Tên
        filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 2)); // SĐT
        filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 3)); // Email

        sorter.setRowFilter(RowFilter.orFilter(filters));
    }

    /** 🟢 Mở dialog thêm NCC */
    private void moDialogThem() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        ThemNhaCungCap_Dialog dlg = new ThemNhaCungCap_Dialog(parentFrame);
        dlg.setVisible(true);

        if (dlg.getNhaCungCapMoi() != null) {
            loadTableData();
        }
    }

    /** 🟠 Mở dialog cập nhật NCC */
    private void moDialogCapNhat() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 nhà cung cấp để cập nhật.", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        String ma = (String) model.getValueAt(modelRow, 0);
        String ten = (String) model.getValueAt(modelRow, 1);
        String sdt = (String) model.getValueAt(modelRow, 2);
        String email = (String) model.getValueAt(modelRow, 3);
        String diaChi = (String) model.getValueAt(modelRow, 4);
        String trangThai = (String) model.getValueAt(modelRow, 5);

        NhaCungCap ncc = new NhaCungCap(ma, ten, sdt, diaChi, email);
        ncc.setHoatDong("Hoạt động".equals(trangThai));

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        CapNhatNhaCungCap_Dialog dlg = new CapNhatNhaCungCap_Dialog(owner, ncc);
        dlg.setVisible(true);

        NhaCungCap capNhat = dlg.getNhaCungCapCapNhat();
        if (capNhat != null) {
            loadTableData();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnThem) {
            moDialogThem();
        } else if (src == btnCapNhat) {
            moDialogCapNhat();
        }
    }

    // ==== MouseListener (hiện chưa dùng) ====
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    // ==== MAIN ====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý nhà cung cấp");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new NhaCungCap_GUI());
            frame.setVisible(true);
        });
    }
}
