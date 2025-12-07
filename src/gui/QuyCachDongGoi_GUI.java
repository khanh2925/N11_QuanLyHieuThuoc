package gui;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import database.connectDB;
import component.button.PillButton;
import component.input.PlaceholderSupport;
import component.border.RoundedBorder;
import dao.DonViTinh_DAO;
import dao.QuyCachDongGoi_DAO;
import dao.SanPham_DAO;
import entity.DonViTinh;
import entity.QuyCachDongGoi;
import entity.SanPham;
import enums.LoaiSanPham;
import gui.dialog.CapNhatQuyCachDongGoi_Dialog;
import gui.dialog.ThemQuyCachDongGoi_Dialog;

public class QuyCachDongGoi_GUI extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private QuyCachDongGoi_DAO qcdg_DAO;
    private SanPham_DAO sp_DAO;
    private DonViTinh_DAO dvt_DAO;
    private List<QuyCachDongGoi> dsQuyCach;
    private List<SanPham> dsSanPham;
    private List<DonViTinh> dsDonViTinh;

    public QuyCachDongGoi_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        
        qcdg_DAO = new QuyCachDongGoi_DAO();
        sp_DAO = new SanPham_DAO();
        dvt_DAO = new DonViTinh_DAO();
        
        try {
            connectDB.getInstance().connect();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến cơ sở dữ liệu.", "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
        }

        initialize();
        loadAllDataFromDatabase();
    }

    private void loadAllDataFromDatabase() {
        dsQuyCach = qcdg_DAO.layTatCaQuyCachDongGoi();
        dsSanPham = sp_DAO.layTatCaSanPham();
        dsDonViTinh = dvt_DAO.layTatCaDonViTinh();
        loadDataTable();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        JPanel pnHeader = new JPanel();
        pnHeader.setPreferredSize(new Dimension(1073, 88));
        pnHeader.setBackground(new Color(0xE3F2F5));
        pnHeader.setLayout(null);
        add(pnHeader, BorderLayout.NORTH);

        JTextField txtTimKiem = new JTextField("");
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo tên sản phẩm...");
        txtTimKiem.setForeground(Color.GRAY);
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtTimKiem.setBounds(20, 17, 420, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        pnHeader.add(txtTimKiem);

        PillButton btnThem = new PillButton("Thêm");
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnThem.setBounds(504, 30, 120, 40);
        pnHeader.add(btnThem);

        PillButton btnCapNhat = new PillButton("Cập nhật");
        btnCapNhat.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnCapNhat.setBounds(670, 30, 130, 40);
        pnHeader.add(btnCapNhat);

        PillButton btnXoa = new PillButton("Xoá");
        btnXoa.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnXoa.setBounds(860, 30, 120, 40);
        pnHeader.add(btnXoa);

        JPanel pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new LineBorder(new Color(200, 200, 200)));
        add(pnCenter, BorderLayout.CENTER);

        String[] columnNames = {"Mã Quy Cách", "Sản Phẩm", "Đơn Vị Tính", "Loại Sản Phẩm", "Hệ Số", "Tỉ Lệ Giảm", "Đơn Vị Gốc"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        setupTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnCenter.add(scrollPane, BorderLayout.CENTER);
        
        btnThem.addActionListener(e -> handleThem());
        btnCapNhat.addActionListener(e -> handleCapNhat());
    }
    
    private void handleThem() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        ThemQuyCachDongGoi_Dialog dialog = new ThemQuyCachDongGoi_Dialog(parentFrame, dsSanPham, dsDonViTinh);
        dialog.setVisible(true);

        QuyCachDongGoi quyCachMoi = dialog.getQuyCachMoi();
        if (quyCachMoi != null) {
            if (qcdg_DAO.themQuyCachDongGoi(quyCachMoi)) {
                loadAllDataFromDatabase();
                JOptionPane.showMessageDialog(this, "Thêm quy cách thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Thêm quy cách thất bại. Dữ liệu có thể đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleCapNhat() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một quy cách để cập nhật.", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        QuyCachDongGoi qcCanCapNhat = dsQuyCach.get(selectedRow);
        
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        CapNhatQuyCachDongGoi_Dialog dialog = new CapNhatQuyCachDongGoi_Dialog(parentFrame, qcCanCapNhat, dsSanPham, dsDonViTinh);
        dialog.setVisible(true);
        
        if (dialog.isUpdateSuccess()) {
            if (qcdg_DAO.capNhatQuyCachDongGoi(qcCanCapNhat)) {
                updateRowInTable(selectedRow, qcCanCapNhat);
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            } else {
                 JOptionPane.showMessageDialog(this, "Cập nhật thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadDataTable() {
        model.setRowCount(0);
        if (dsQuyCach != null) {
            for (QuyCachDongGoi qc : dsQuyCach) {
                addRowToTable(qc);
            }
        }
    }

    private void addRowToTable(QuyCachDongGoi qc) {
        model.addRow(new Object[]{
            qc.getMaQuyCach(),
            qc.getSanPham().getTenSanPham(),
            qc.getDonViTinh().getTenDonViTinh(),
            qc.getSanPham().getLoaiSanPham().getTenLoai(),
            qc.getHeSoQuyDoi(),
            String.format("%.0f%%", qc.getTiLeGiam() * 100),
            qc.isDonViGoc() ? "Có" : "Không"
        });
    }

    private void updateRowInTable(int rowIndex, QuyCachDongGoi qc) {
        model.setValueAt(qc.getSanPham().getTenSanPham(), rowIndex, 1);
        model.setValueAt(qc.getDonViTinh().getTenDonViTinh(), rowIndex, 2);
        model.setValueAt(qc.getSanPham().getLoaiSanPham().getTenLoai(), rowIndex, 3);
        model.setValueAt(qc.getHeSoQuyDoi(), rowIndex, 4);
        model.setValueAt(String.format("%.0f%%", qc.getTiLeGiam() * 100), rowIndex, 5);
        model.setValueAt(qc.isDonViGoc() ? "Có" : "Không", rowIndex, 6);
    }
    
    private void setupTable() {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        table.setRowHeight(34);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowGrid(true);
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
        table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);

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
            JFrame frame = new JFrame("Quản lý Quy Cách Đóng Gói");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new QuyCachDongGoi_GUI ());
            frame.setVisible(true);
        });
    }
}