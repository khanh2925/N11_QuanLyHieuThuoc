/**
 * @author Quốc Khánh cute
 * @version 1.0
 * @since Oct 19, 2025
 *
 * Mô tả: Lớp này được tạo bởi Quốc Khánh vào ngày Oct 19, 2025.
 */
package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import customcomponent.PillButton;

public class TraCuuDonHang_GUI extends JPanel {

    private JPanel pnCenter;   // vùng trung tâm
    private JPanel pnHeader;   // vùng đầu trang

    public TraCuuDonHang_GUI() {
        this.setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1537, 1168));

        // ===== HEADER =====
        pnHeader = new JPanel();
        pnHeader.setPreferredSize(new Dimension(1073, 88));
        pnHeader.setBackground(new Color(0xE3F2F5));
        pnHeader.setLayout(null);
        add(pnHeader, BorderLayout.NORTH);

     // ===== BỘ LỌC THEO NGÀY =====
        JLabel lblTuNgay = new JLabel("Từ ngày:");
        lblTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTuNgay.setBounds(30, 30, 60, 25);
        pnHeader.add(lblTuNgay);

        com.toedter.calendar.JDateChooser dateTu = new com.toedter.calendar.JDateChooser();
        dateTu.setDateFormatString("dd/MM/yyyy");
        dateTu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateTu.setBounds(90, 30, 130, 25);
        dateTu.setDate(new java.util.Date()); // mặc định là hôm nay
        pnHeader.add(dateTu);

        JLabel lblDenNgay = new JLabel("Đến:");
        lblDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDenNgay.setBounds(235, 30, 40, 25);
        pnHeader.add(lblDenNgay);

        com.toedter.calendar.JDateChooser dateDen = new com.toedter.calendar.JDateChooser();
        dateDen.setDateFormatString("dd/MM/yyyy");
        dateDen.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateDen.setBounds(275, 30, 130, 25);

        // set mặc định là ngày mai
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DATE, 1);
        dateDen.setDate(cal.getTime());
        pnHeader.add(dateDen);

        // ===== NÚT LỌC =====
        JButton btnLoc = new PillButton("Lọc");
        btnLoc.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLoc.setBounds(420, 30, 70, 25);
        btnLoc.setFocusPainted(false);
        pnHeader.add(btnLoc);
        

        // Sự kiện lọc (ví dụ in ra khoảng ngày)
        btnLoc.addActionListener(e -> {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            String tu = sdf.format(dateTu.getDate());
            String den = sdf.format(dateDen.getDate());
            System.out.println("Lọc đơn từ " + tu + " đến " + den);
        });
        
        JButton btnXemChiTiet = new PillButton("Chi tiết");
        btnXemChiTiet.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnXemChiTiet.setFocusPainted(false);
        btnXemChiTiet.setBounds(500, 30, 94, 25);
        pnHeader.add(btnXemChiTiet);
        
        // ===== CENTER =====
        pnCenter = new JPanel();
        pnCenter.setBackground(new Color(255, 255, 255));
        pnCenter.setLayout(new BorderLayout());
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(pnCenter, BorderLayout.CENTER);
        
     // ===== CENTER =====
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(pnCenter, BorderLayout.CENTER);

        // ===== DANH SÁCH HÓA ĐƠN =====
        String[] columnNames = {"Mã hóa đơn", "Khách hàng", "Ngày tạo", "Tổng tiền"};
        Object[][] data = {
            {"HD001", "Nguyễn Văn A", "19/10/2025", "150,000 đ"},
            {"HD002", "Trần Thị B", "18/10/2025", "320,000 đ"},
            {"HD003", "Phạm Quốc Khánh", "17/10/2025", "500,000 đ"},
            {"HD004", "Chu Anh Khôi", "17/10/2025", "120,000 đ"},
            {"HD005", "Lê Thanh Kha", "16/10/2025", "210,000 đ"},
        };

        // Model bảng
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // không cho sửa dữ liệu
            }
        };

        // Bảng hóa đơn
        JTable tblHoaDon = new JTable(model);
        tblHoaDon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblHoaDon.setRowHeight(30);
        tblHoaDon.setGridColor(new Color(230, 230, 230));
        tblHoaDon.setSelectionBackground(new Color(0xC8E6C9)); // xanh lá nhạt
        tblHoaDon.setSelectionForeground(Color.BLACK);

        // Header bảng
        JTableHeader header = tblHoaDon.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(0x00C853));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // Căn giữa cột “Ngày tạo”
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblHoaDon.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        // Căn phải cột “Tổng tiền”
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tblHoaDon.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        // ScrollPane chứa bảng
        JScrollPane scrollPane = new JScrollPane(tblHoaDon);
        scrollPane.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        pnCenter.add(scrollPane, BorderLayout.CENTER);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Khung trống - clone base");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new TraCuuDonHang_GUI());
            frame.setVisible(true);
        });
    }
}