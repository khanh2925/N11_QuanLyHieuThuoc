/**
 * @author Quốc Khánh
 * @version 1.0
 * @since Oct 19, 2025
 *
 * Mô tả: Giao diện tra cứu đơn trả hàng (lọc theo ngày và trạng thái).
 */

package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.util.*;
import com.toedter.calendar.JDateChooser;

import customcomponent.PillButton;

public class TraCuuDonTraHang_GUI extends JPanel {

    private JPanel pnHeader;
    private JPanel pnCenter;
    private JComboBox<String> cbTrangThai;
    private JTable tblTraHang;

    public TraCuuDonTraHang_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===== HEADER =====
        pnHeader = new JPanel();
        pnHeader.setPreferredSize(new Dimension(1073, 88));
        pnHeader.setBackground(new Color(0xE3F2F5));
        pnHeader.setLayout(null);
        add(pnHeader, BorderLayout.NORTH);

        // ===== BỘ LỌC NGÀY =====
        JLabel lblTuNgay = new JLabel("Từ ngày:");
        lblTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTuNgay.setBounds(30, 30, 60, 25);
        pnHeader.add(lblTuNgay);

        JDateChooser dateTu = new JDateChooser();
        dateTu.setDateFormatString("dd/MM/yyyy");
        dateTu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateTu.setBounds(90, 30, 130, 25);
        dateTu.setDate(new java.util.Date());
        pnHeader.add(dateTu);

        JLabel lblDenNgay = new JLabel("Đến:");
        lblDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDenNgay.setBounds(235, 30, 40, 25);
        pnHeader.add(lblDenNgay);

        JDateChooser dateDen = new JDateChooser();
        dateDen.setDateFormatString("dd/MM/yyyy");
        dateDen.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateDen.setBounds(275, 30, 130, 25);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        dateDen.setDate(cal.getTime());
        pnHeader.add(dateDen);

        // ===== COMBOBOX TRẠNG THÁI =====
        JLabel lblTrangThai = new JLabel("Trạng thái:");
        lblTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTrangThai.setBounds(430, 30, 80, 25);
        pnHeader.add(lblTrangThai);

        cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đã xử lý", "Chưa xử lý"});
        cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbTrangThai.setBounds(510, 30, 120, 25);
        pnHeader.add(cbTrangThai);

        // ===== NÚT LỌC =====
        JButton btnLoc = new PillButton("Lọc");
        btnLoc.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLoc.setBounds(650, 30, 70, 25);
        btnLoc.setFocusPainted(false);
        pnHeader.add(btnLoc);
        
        PillButton btnChiTieest = new PillButton("Lọc");
        btnChiTieest.setText("Chi Tiết");
        btnChiTieest.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnChiTieest.setFocusPainted(false);
        btnChiTieest.setBounds(730, 30, 89, 25);
        pnHeader.add(btnChiTieest);

        btnLoc.addActionListener(e -> {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            String tu = sdf.format(dateTu.getDate());
            String den = sdf.format(dateDen.getDate());
            String trangThai = (String) cbTrangThai.getSelectedItem();
            System.out.println("Lọc từ " + tu + " đến " + den + " | Trạng thái: " + trangThai);
        });

        // ===== CENTER =====
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(pnCenter, BorderLayout.CENTER);

        // ===== BẢNG DANH SÁCH ĐƠN TRẢ HÀNG =====
        String[] columnNames = {"Mã trả hàng", "Khách hàng", "Ngày tạo", "Tổng tiền", "Trạng thái"};
        Object[][] data = {
            {"TH001", "Nguyễn Văn A", "19/10/2025", "150,000 đ", "Đã xử lý"},
            {"TH002", "Trần Thị B", "18/10/2025", "210,000 đ", "Chưa xử lý"},
            {"TH003", "Lê Thanh Kha", "17/10/2025", "320,000 đ", "Đã xử lý"},
            {"TH004", "Chu Anh Khôi", "17/10/2025", "120,000 đ", "Chưa xử lý"},
            {"TH005", "Phạm Quốc Khánh", "16/10/2025", "500,000 đ", "Đã xử lý"},
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblTraHang = new JTable(model);
        tblTraHang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblTraHang.setRowHeight(30);
        tblTraHang.setGridColor(new Color(230, 230, 230));
        tblTraHang.setSelectionBackground(new Color(0xC8E6C9));
        tblTraHang.setSelectionForeground(Color.BLACK);

        // Header
        JTableHeader header = tblTraHang.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(0x00C853));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // Căn giữa các cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblTraHang.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        tblTraHang.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        // Căn phải tiền
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tblTraHang.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        // Renderer riêng cho trạng thái (tô màu)
        tblTraHang.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                String trangThai = value.toString();
                if (trangThai.equals("Đã xử lý")) {
                    lbl.setForeground(new Color(0x2E7D32)); // xanh đậm
                } else {
                    lbl.setForeground(new Color(0xD32F2F)); // đỏ đậm
                }
                return lbl;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblTraHang);
        scrollPane.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        pnCenter.add(scrollPane, BorderLayout.CENTER);
    }

    // ===== MAIN TEST =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tra cứu đơn trả hàng");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new TraCuuDonTraHang_GUI());
            frame.setVisible(true);
        });
    }
}
