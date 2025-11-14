/**
 * @author Quốc Khánh
 * @version 1.0
 * @since Oct 19, 2025
 *
 * Mô tả: Giao diện tra cứu sản phẩm và danh sách lô hàng.
 */
package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.util.*;
import com.toedter.calendar.JDateChooser;

import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;

@SuppressWarnings("serial")
public class TraCuuSanPham_GUI extends JPanel {

    private JPanel pnHeader;
    private JPanel pnCenter;
    private JTable tblSanPham;
    private JTable tblLoSanPham;
    private DefaultTableModel modelSanPham;
    private DefaultTableModel modelLoSanPham;

    // Các thành phần lọc
    private JTextField txtTimKiem;
    private JTextField txtSoDangKy;
    private JComboBox<String> cbLoaiSanPham;
    private JComboBox<String> cbKeBan;
    private JComboBox<String> cbHoatDong;

    public TraCuuSanPham_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

     // ===== HEADER - ABSOLUTE LAYOUT - 1 Ô TÌM KIẾM TO =====
        pnHeader = new JPanel();
        pnHeader.setLayout(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94)); // tăng nhẹ lên 94 để vừa 60px + margin
        pnHeader.setBackground(new Color(0xE3F2F5));
        add(pnHeader, BorderLayout.NORTH);

        // === Ô TÌM KIẾM SIÊU TO ===
        JTextField txtTimThuoc = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimThuoc, "Nhập tên thuốc, mã sản phẩm hoặc số đăng ký");
        txtTimThuoc.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimThuoc.setBounds(25, 17, 480, 60);        // đúng như mẫu bạn đưa
        txtTimThuoc.setBorder(new RoundedBorder(20));
        txtTimThuoc.setBackground(Color.WHITE);
        txtTimThuoc.setForeground(Color.GRAY);

        // === Các bộ lọc nhỏ bên phải ===
        JLabel lblLoai = new JLabel("Loại:");
        lblLoai.setBounds(530, 28, 50, 35);
        lblLoai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pnHeader.add(lblLoai);

        JComboBox<String> cbLoai = new JComboBox<>(new String[]{"Tất cả", "Thuốc", "TPCN", "Dụng cụ y tế"});
        cbLoai.setBounds(580, 28, 140, 38);
        cbLoai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pnHeader.add(cbLoai);

        JLabel lblKe = new JLabel("Kệ:");
        lblKe.setBounds(735, 28, 40, 35);
        lblKe.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pnHeader.add(lblKe);

        JComboBox<String> cbKe = new JComboBox<>(new String[]{"Tất cả", "A1", "A2", "B1", "B2", "C1", "D1"});
        cbKe.setBounds(770, 28, 100, 38);
        cbKe.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pnHeader.add(cbKe);

        JLabel lblTrangThai = new JLabel("Trạng thái:");
        lblTrangThai.setBounds(885, 28, 80, 35);
        lblTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pnHeader.add(lblTrangThai);

        JComboBox<String> cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đang bán", "Ngừng bán"});
        cbTrangThai.setBounds(970, 28, 130, 38);
        cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pnHeader.add(cbTrangThai);

        // === NÚT TÌM KIẾM & LÀM MỚI ===
        PillButton btnTimKiem = new PillButton("Tìm kiếm");
        btnTimKiem.setBounds(1120, 22, 130, 50);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnHeader.add(btnTimKiem);

        PillButton btnLamMoi = new PillButton("Làm mới");
        btnLamMoi.setBounds(1265, 22, 130, 50);
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnHeader.add(btnLamMoi);

        // === THÊM Ô TÌM KIẾM VÀO CUỐI CÙNG ===
        pnHeader.add(txtTimThuoc);
        // ===== CENTER - SPLIT PANE (giữ nguyên) =====
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(pnCenter, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.5);
        pnCenter.add(splitPane, BorderLayout.CENTER);

        // ===== BẢNG SẢN PHẨM (TRÊN) =====
        String[] colSanPham = {
            "Mã SP", "Tên sản phẩm", "Loại", "Số ĐK", "Đường dùng", "Đơn vị",
            "Giá nhập", "Giá bán", "Kệ", "Hoạt động"
        };
        modelSanPham = new DefaultTableModel(colSanPham, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblSanPham = new JTable(modelSanPham);
        tblSanPham.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tblSanPham.setRowHeight(28);
        tblSanPham.setSelectionBackground(new Color(0xC8E6C9));
        JTableHeader headerSP = tblSanPham.getTableHeader();
        headerSP.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerSP.setBackground(new Color(33, 150, 243));
        headerSP.setForeground(Color.WHITE);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tblSanPham.getColumnCount(); i++) {
            if (i != 1 && i != 4) tblSanPham.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        tblSanPham.getColumnModel().getColumn(6).setCellRenderer(right);
        tblSanPham.getColumnModel().getColumn(7).setCellRenderer(right);

        JScrollPane scrollSP = new JScrollPane(tblSanPham);
        scrollSP.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Danh sách sản phẩm",
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), Color.DARK_GRAY
        ));
        splitPane.setTopComponent(scrollSP);

        // ===== BẢNG LÔ (DƯỚI) =====
        String[] colLo = {"Mã lô", "Hạn sử dụng", "Số lượng tồn", "Mã SP"};
        modelLoSanPham = new DefaultTableModel(colLo, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblLoSanPham = new JTable(modelLoSanPham);
        tblLoSanPham.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tblLoSanPham.setRowHeight(28);
        tblLoSanPham.setSelectionBackground(new Color(0xC8E6C9));
        JTableHeader headerLo = tblLoSanPham.getTableHeader();
        headerLo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLo.setBackground(new Color(33, 150, 243));
        headerLo.setForeground(Color.WHITE);

        for (int i = 0; i < tblLoSanPham.getColumnCount(); i++) {
            tblLoSanPham.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JScrollPane scrollLo = new JScrollPane(tblLoSanPham);
        scrollLo.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Danh sách lô",
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), Color.DARK_GRAY
        ));
        splitPane.setBottomComponent(scrollLo);

        // ===== LOAD DỮ LIỆU =====
        loadDuLieuSanPham();
        loadDuLieuLoSanPham();

        // ===== CHỌN SẢN PHẨM → HIỆN LÔ =====
        tblSanPham.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblSanPham.getSelectedRow();
                if (row >= 0) {
                    String maSP = tblSanPham.getValueAt(row, 0).toString();
                    loadLoTheoSanPham(maSP);
                }
            }
        });
    }

    private void loadDuLieuSanPham() {
        Object[][] data = {
            {"SP001", "Paracetamol 500mg", "Thuốc", "VD-12345", "Uống", "Viên", "2,500 đ", "5,000 đ", "Kệ A1", "Có"},
            {"SP002", "Vitamin C 1000mg", "Thực phẩm chức năng", "VD-67890", "Uống", "Viên", "8,000 đ", "15,000 đ", "Kệ B2", "Có"},
            {"SP003", "Băng cá nhân", "Dụng cụ y tế", "VD-11111", "Dán ngoài", "Hộp", "12,000 đ", "20,000 đ", "Kệ C3", "Có"},
            {"SP004", "Panadol Extra", "Thuốc", "VD-22222", "Uống", "Viên", "15,000 đ", "25,000 đ", "Kệ A2", "Có"},
            {"SP005", "Oral Rehydration Salt", "Thuốc", "VD-33333", "Pha uống", "Gói", "3,000 đ", "6,000 đ", "Kệ D1", "Không"}
        };
        for (Object[] row : data) modelSanPham.addRow(row);
    }

    private void loadDuLieuLoSanPham() {
        Object[][] data = {
            {"L001", "15/12/2026", "150", "SP001"},
            {"L002", "20/11/2026", "200", "SP001"},
            {"L003", "10/03/2027", "300", "SP002"},
            {"L004", "05/05/2026", "80", "SP003"},
            {"L005", "30/09/2026", "120", "SP004"}
        };
        for (Object[] row : data) modelLoSanPham.addRow(row);
    }

    private void loadLoTheoSanPham(String maSP) {
        modelLoSanPham.setRowCount(0);
        Object[][] all = {
            {"L001", "15/12/2026", "150", "SP001"},
            {"L002", "20/11/2026", "200", "SP001"},
            {"L003", "10/03/2027", "300", "SP002"},
            {"L004", "05/05/2026", "80", "SP003"},
            {"L005", "30/09/2026", "120", "SP004"}
        };
        boolean found = false;
        for (Object[] row : all) {
            if (row[3].equals(maSP)) {
                modelLoSanPham.addRow(row);
                found = true;
            }
        }
        if (!found) {
            modelLoSanPham.addRow(new Object[]{"", "Không có lô", "", ""});
        }
    }

    // ===== MAIN =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tra cứu sản phẩm & lô hàng");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1450, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new TraCuuSanPham_GUI());
            frame.setVisible(true);
        });
    }
}