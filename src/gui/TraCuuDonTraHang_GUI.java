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
        add(pnHeader, BorderLayout.NORTH);
        pnHeader.setLayout(null);

        // ===== BỘ LỌC NGÀY =====
        JLabel lblTuNgay = new JLabel("Từ ngày:");
        lblTuNgay.setBounds(30, 30, 80, 40);
        lblTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        pnHeader.add(lblTuNgay);

        JDateChooser dateTu = new JDateChooser();
        dateTu.setBounds(130, 35, 130, 30);
        dateTu.setDateFormatString("dd/MM/yyyy");
        dateTu.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        dateTu.setDate(new java.util.Date());
        pnHeader.add(dateTu);

        JLabel lbLocTrangThai = new JLabel("Lọc theo trạng thái:");
        lbLocTrangThai.setBounds(580, 30, 160, 40);
        lbLocTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        pnHeader.add(lbLocTrangThai);

        JDateChooser dateDen = new JDateChooser();
        dateDen.setBounds(370, 35, 130, 30);
        dateDen.setDateFormatString("dd/MM/yyyy");
        dateDen.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        dateDen.setDate(cal.getTime());
        pnHeader.add(dateDen);

        cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đã xử lý", "Chưa xử lý"});
        cbTrangThai.setBounds(750, 35, 150, 30);
        cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        pnHeader.add(cbTrangThai);
        
        PillButton btnChiTieest = new PillButton("Lọc");
        btnChiTieest.setBounds(980, 30, 150, 40);
        btnChiTieest.setText("Xem Chi Tiết");
        btnChiTieest.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnChiTieest.setFocusPainted(false);
        pnHeader.add(btnChiTieest);
        
        JLabel lblDenNgay_1 = new JLabel("Đến:");
        lblDenNgay_1.setBounds(310, 30, 40, 40);
        lblDenNgay_1.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        pnHeader.add(lblDenNgay_1);

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
        tblTraHang.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        tblTraHang.setRowHeight(30);
        tblTraHang.setGridColor(new Color(230, 230, 230));
        tblTraHang.setSelectionBackground(new Color(0xC8E6C9));
        tblTraHang.setSelectionForeground(Color.BLACK);

        // Header
        JTableHeader header = tblTraHang.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setBackground(new Color(33, 150, 243));
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
        tblTraHang.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        // Renderer riêng cho trạng thái (tô màu)
        tblTraHang.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
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
