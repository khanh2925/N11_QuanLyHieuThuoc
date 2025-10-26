package gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import customcomponent.ImagePanel;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import entity.KhachHang;

public class KhachHang_QL_GUI extends JPanel {

    private JPanel pnCenter;
    private JPanel pnHeader;
    private JPanel pnRight;
    private JTextField txtTimKiem;
    private JTable table;
    private JLabel lbThem;
    private JLabel lbKhachHang;

    // === KHAI BÁO BIẾN THÀNH VIÊN ===
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private JCheckBox chckbxNam;
    private JCheckBox chckbxNu;
    private JCheckBox chckbxTangDan;
    private JCheckBox chckbxGiamDan;

    public KhachHang_QL_GUI() {
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
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo tên / số điện thoại");
        txtTimKiem.setForeground(Color.GRAY);
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTimKiem.setBounds(20, 27, 336, 44);
        txtTimKiem.setBorder(new RoundedBorder(20));
        pnHeader.add(txtTimKiem);

        ImageIcon iconSearch = new ImageIcon(getClass().getResource("/images/search.png"));

        ImageIcon icon = new ImageIcon(getClass().getResource("/images/add.png"));
        ImagePanel btnThem = new ImagePanel(icon.getImage());
        btnThem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThem.setBounds(456, 27, 30, 30);
        pnHeader.add(btnThem);
        btnThem.setLayout(null);
        
        lbThem = new JLabel("Thêm", SwingConstants.CENTER);
        lbThem.setBounds(435, 58, 70, 19);
        pnHeader.add(lbThem);
        lbThem.setFont(new Font("Arial", Font.BOLD, 16));
        lbThem.setForeground(Color.BLACK);
        
        ImageIcon iconSua = new ImageIcon(getClass().getResource("/images/edit.png"));
        ImagePanel btnSua = new ImagePanel(iconSua.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
        btnSua.setLayout(null);
        btnSua.setBounds(577, 27, 30, 30);
        btnSua.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnHeader.add(btnSua);
        
        JLabel lblSua = new JLabel("Cập nhật", SwingConstants.CENTER);
        lblSua.setBounds(553, 55, 70, 25);
        pnHeader.add(lblSua);
        lblSua.setFont(new Font("Arial", Font.BOLD, 16));
        lblSua.setForeground(Color.BLACK);
        
        // ===== CENTER =====
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new LineBorder(new Color(200, 200, 200)));
        add(pnCenter, BorderLayout.CENTER);

        // Dữ liệu mẫu...
        List<KhachHang> dsKhachHang = new ArrayList<>();
        dsKhachHang.add(new KhachHang("KH-0001", "Nguyễn Văn A", true, "0901234567", LocalDate.of(1995, 5, 12)));
        dsKhachHang.add(new KhachHang("KH-0002", "Trần Thị B", false, "0912345678", LocalDate.of(2000, 8, 20)));
        dsKhachHang.add(new KhachHang("KH-0003", "Lê Minh C", true, "0923456789", LocalDate.of(1988, 3, 5)));
        dsKhachHang.add(new KhachHang("KH-0004", "Phạm Ngọc D", false, "0934567890", LocalDate.of(1999, 12, 30)));
        dsKhachHang.add(new KhachHang("KH-0005", "Võ Thanh E", true, "0945678901", LocalDate.of(1992, 7, 18)));
        dsKhachHang.add(new KhachHang("KH-0006", "Bùi Thị F", false, "0956789012", LocalDate.of(1997, 9, 10)));
        dsKhachHang.add(new KhachHang("KH-0007", "Đặng Hoàng G", true, "0967890123", LocalDate.of(1985, 2, 22)));
        dsKhachHang.add(new KhachHang("KH-0008", "Phan Thị H", false, "0978901234", LocalDate.of(1998, 4, 8)));
        dsKhachHang.add(new KhachHang("KH-0009", "Ngô Minh I", true, "0989012345", LocalDate.of(1993, 11, 15)));
        dsKhachHang.add(new KhachHang("KH-0010", "Huỳnh Thị K", false, "0990123456", LocalDate.of(2001, 1, 25)));
        dsKhachHang.add(new KhachHang("KH-0011", "Trịnh Công L", true, "0902345678", LocalDate.of(1990, 6, 2)));
        dsKhachHang.add(new KhachHang("KH-0012", "Đoàn Thị M", false, "0913456789", LocalDate.of(1996, 8, 14)));
        dsKhachHang.add(new KhachHang("KH-0013", "Lâm Hữu N", true, "0924567890", LocalDate.of(1989, 3, 28)));
        dsKhachHang.add(new KhachHang("KH-0014", "Tạ Thị O", false, "0935678901", LocalDate.of(1994, 5, 9)));
        dsKhachHang.add(new KhachHang("KH-0015", "Hồ Nhật P", true, "0946789012", LocalDate.of(1998, 10, 19)));
        dsKhachHang.add(new KhachHang("KH-0016", "Lý Thị Q", false, "0957890123", LocalDate.of(2002, 12, 2)));
        dsKhachHang.add(new KhachHang("KH-0017", "Trương Văn R", true, "0968901234", LocalDate.of(1991, 7, 11)));
        dsKhachHang.add(new KhachHang("KH-0018", "Đinh Thị S", false, "0979012345", LocalDate.of(1993, 9, 22)));
        dsKhachHang.add(new KhachHang("KH-0019", "Cao Văn T", true, "0980123456", LocalDate.of(1987, 4, 30)));
        dsKhachHang.add(new KhachHang("KH-0020", "Nguyễn Thị U", false, "0991234567", LocalDate.of(1999, 11, 5)));

        //... (dữ liệu còn lại)

        String[] columnNames = {"Mã khách hàng", "Tên khách hàng", "Giới tính", "Số điện thoại", "Ngày sinh", "Điểm tích lũy"};

     model = new DefaultTableModel(columnNames, 0) {
         @Override
         public Class<?> getColumnClass(int columnIndex) {
             if (columnIndex == 5) {
                 return Integer.class;
             }
             return super.getColumnClass(columnIndex);
         }
     };

        for (KhachHang kh : dsKhachHang) {
            model.addRow(new Object[]{
                kh.getMaKhachHang(),
                kh.getTenKhachHang(),
                kh.isGioiTinh() ? "Nam" : "Nữ",
                kh.getSoDienThoai(),
                kh.getNgaySinh(),
               
            });
        }

        table = new JTable(model);
        // ... (Toàn bộ phần cấu hình JTable giữ nguyên)
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
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 1 && i != 3 && i != 4) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);

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

        // ===== RIGHT =====
        pnRight = new JPanel();
        pnRight.setPreferredSize(new Dimension(300, 1080));
        pnRight.setBackground(new Color(255, 255, 255));
        pnRight.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(pnRight, BorderLayout.EAST);
        pnRight.setLayout(null);
        
        lbKhachHang = new JLabel("Khách Hàng");
        lbKhachHang.setBounds(25, 25, 172, 53);
        lbKhachHang.setFont(new Font("Times New Roman", Font.BOLD, 30));
        pnRight.add(lbKhachHang);
        
        JLabel lblNewLabel = new JLabel("Giới tính");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
        lblNewLabel.setBounds(25, 101, 90, 25);
        pnRight.add(lblNewLabel);
        
        chckbxNam = new JCheckBox("Nam"); // Sử dụng biến thành viên
        chckbxNam.setBackground(new Color(255, 255, 255));
        chckbxNam.setFont(new Font("Tahoma", Font.PLAIN, 15));
        chckbxNam.setBounds(25, 151, 97, 23);
        pnRight.add(chckbxNam);
        
        chckbxNu = new JCheckBox("Nữ"); // Sử dụng biến thành viên
        chckbxNu.setFont(new Font("Tahoma", Font.PLAIN, 15));
        chckbxNu.setBackground(Color.WHITE);
        chckbxNu.setBounds(145, 151, 97, 23);
        pnRight.add(chckbxNu);
        
        JLabel lblimTchLy = new JLabel("Điểm tích lũy");
        lblimTchLy.setFont(new Font("Tahoma", Font.PLAIN, 20));
        lblimTchLy.setBounds(25, 201, 124, 25);
        pnRight.add(lblimTchLy);
        
        chckbxTangDan = new JCheckBox("Tăng dần"); // Sử dụng biến thành viên
        chckbxTangDan.setFont(new Font("Tahoma", Font.PLAIN, 15));
        chckbxTangDan.setBackground(Color.WHITE);
        chckbxTangDan.setBounds(25, 245, 97, 23);
        pnRight.add(chckbxTangDan);
        
        chckbxGiamDan = new JCheckBox("Giảm dần"); // Sử dụng biến thành viên
        chckbxGiamDan.setFont(new Font("Tahoma", Font.PLAIN, 15));
        chckbxGiamDan.setBackground(Color.WHITE);
        chckbxGiamDan.setBounds(145, 247, 97, 23);
        pnRight.add(chckbxGiamDan);
        
        // ===== SỰ KIỆN LỌC VÀ SẮP XẾP =====
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        ActionListener filterListener = e -> {
            JCheckBox source = (JCheckBox) e.getSource();
            if (source == chckbxNam && chckbxNam.isSelected()) {
                chckbxNu.setSelected(false);
            } else if (source == chckbxNu && chckbxNu.isSelected()) {
                chckbxNam.setSelected(false);
            }
            applyFilters();
        };
        chckbxNam.addActionListener(filterListener);
        chckbxNu.addActionListener(filterListener);

        ActionListener sortListener = e -> {
            JCheckBox source = (JCheckBox) e.getSource();
            if (source == chckbxTangDan && chckbxTangDan.isSelected()) {
                chckbxGiamDan.setSelected(false);
            } else if (source == chckbxGiamDan && chckbxGiamDan.isSelected()) {
                chckbxTangDan.setSelected(false);
            }
            
            int diemTichLuyColumnIndex = 5;
            List<RowSorter.SortKey> sortKeys = new ArrayList<>();
            if (chckbxTangDan.isSelected()) {
                sortKeys.add(new RowSorter.SortKey(diemTichLuyColumnIndex, SortOrder.ASCENDING));
            } else if (chckbxGiamDan.isSelected()) {
                sortKeys.add(new RowSorter.SortKey(diemTichLuyColumnIndex, SortOrder.DESCENDING));
            }
            sorter.setSortKeys(sortKeys);
        };
        chckbxTangDan.addActionListener(sortListener);
        chckbxGiamDan.addActionListener(sortListener);
    }
    
    // ===== PHƯƠNG THỨC LỌC =====
    private void applyFilters() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        int gioiTinhColumnIndex = 2;

        if (chckbxNam.isSelected()) {
            filters.add(RowFilter.regexFilter("Nam", gioiTinhColumnIndex));
        } else if (chckbxNu.isSelected()) {
            filters.add(RowFilter.regexFilter("Nữ", gioiTinhColumnIndex));
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    // ===== MAIN =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý Khách Hàng");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new KhachHang_QL_GUI());
            frame.setVisible(true);
        });
    }
}