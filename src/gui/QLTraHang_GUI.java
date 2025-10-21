package gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.toedter.calendar.JDateChooser;
import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;

public class QLTraHang_GUI extends JPanel {

    private JPanel pnCenter; // vùng trung tâm
    private JPanel pnHeader; // vùng đầu trang
    private JPanel pnRight;  // vùng cột phải
    private JButton btnXuatFile;
    private JTextField txtSearch;
    private DefaultTableModel modelPT;
    private JTable tblPT;
    private JScrollPane scrCTPT;
    private DefaultTableModel modelCTPT;
    private JScrollPane scrPT;
    private JTable tblCTPT;
    private PillButton btnHuyHang;
    private PillButton btnNhapKho;
    private JDateChooser dateTu;
    private JDateChooser dateDen;

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private final DecimalFormat df = new DecimalFormat("#,##0.###'đ'");

    private final Color blueMint = new Color(180, 220, 240);
    private final Color pinkPastel = new Color(255, 200, 220);

    public QLTraHang_GUI() {
        this.setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1537, 1168));

        // ===== HEADER =====
        pnHeader = new JPanel();
        pnHeader.setPreferredSize(new Dimension(1073, 88));
        pnHeader.setLayout(null);
        add(pnHeader, BorderLayout.NORTH);


        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtSearch.setBounds(10, 10, 342, 68);
        txtSearch.setBorder(new LineBorder(new Color(0x00C0E2), 2,true));
        txtSearch.setBackground(Color.WHITE);
        PlaceholderSupport.addPlaceholder(txtSearch, "Tìm theo mã/tên ...");


        btnXuatFile = new PillButton("Xuất file");
        btnXuatFile.setSize(100, 30);
        btnXuatFile.setLocation(796, 30);

        pnHeader.add(txtSearch);
        pnHeader.add(btnXuatFile);

        btnNhapKho = new PillButton("Nhập lại kho");
        btnNhapKho.setBounds(953, 30, 130, 30);
        pnHeader.add(btnNhapKho);

        btnHuyHang = new PillButton("Hủy hàng");
        btnHuyHang.setBounds(1135, 30, 100, 30);
        pnHeader.add(btnHuyHang);
        
        JLabel lblTuNgay = new JLabel("Từ ngày:");
        lblTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTuNgay.setBounds(362, 30, 60, 25);
        pnHeader.add(lblTuNgay);
        
        dateTu = new JDateChooser();
        dateTu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateTu.setDateFormatString("dd/MM/yyyy");
        dateTu.setBounds(429, 30, 130, 25);
        pnHeader.add(dateTu);
        
        JLabel lblDenNgay = new JLabel("Đến:");
        lblDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDenNgay.setBounds(579, 30, 40, 25);
        pnHeader.add(lblDenNgay);
        
        dateDen = new JDateChooser();
        dateDen.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateDen.setDateFormatString("dd/MM/yyyy");
        dateDen.setBounds(619, 30, 130, 25);
        pnHeader.add(dateDen);

        // ===== CENTER =====
        pnCenter = new JPanel(new BorderLayout());
        add(pnCenter, BorderLayout.CENTER);

        // ===== RIGHT =====
        pnRight = new JPanel();
        pnRight.setPreferredSize(new Dimension(600, 1080));
        pnRight.setBackground(new Color(0, 128, 255));
        pnRight.setLayout(new BoxLayout(pnRight, BoxLayout.Y_AXIS));
        add(pnRight, BorderLayout.EAST);

        initTable();
    }

    private void initTable() {
        String[] phieuTraCols = { "Mã PT", "Người bán", "Người trả", "Mã HD","Thời gian","Khách hàng", "Tổng tiền","Trạng thái" };
        modelPT = new DefaultTableModel(phieuTraCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPT = new JTable(modelPT);
        scrPT = new JScrollPane(tblPT);
        pnCenter.add(scrPT, BorderLayout.CENTER);

        String[] cTPhieuTraCols = { "Ngày lập phiếu", "Tên hàng", "Số lượng", "Thành tiền", "Lý do trả", "Trạng thái" };
        modelCTPT = new DefaultTableModel(cTPhieuTraCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCTPT = new JTable(modelCTPT);
        scrCTPT = new JScrollPane(tblCTPT);
        pnRight.add(scrCTPT);

        formatTable(tblPT);
        tblPT.setSelectionBackground(blueMint);
        tblPT.getTableHeader().setBackground(pinkPastel);

        formatTable(tblCTPT);
        tblCTPT.setSelectionBackground(pinkPastel);
        tblCTPT.getTableHeader().setBackground(blueMint);

        modelPT.addRow(new Object[]{"PT000001", "Chu Anh Khôi","Chu Anh Khôi", "HD-20251003-0001", "2025-10-15", "Công ty Minh Tâm", "1.250.000", "Đã xử lý"});
        modelPT.addRow(new Object[]{"PT000002", "Chu Anh Khôi","Chu Anh Khôi", "HD-20251003-0002", "2025-10-15", "Nguyễn Thị Hoa",   "820.000",   "Chờ duyệt"});
        modelPT.addRow(new Object[]{"PT000003", "Chu Anh Khôi","Chu Anh Khôi", "HD-20251003-0003", "2025-10-16", "Phạm Anh Khoa",    "560.000",   "Đã xử lý"});

        modelCTPT.addRow(new Object[]{"2025-10-16", "Thuốc ho", 2, "300.000", "Sai thuốc", "Đã xử lý"});
        modelCTPT.addRow(new Object[]{"2025-10-16", "Băng cá nhân",  1, "10.000", "Thiếu 1 băng", "Đã xử lý"});
    }

    private void formatTable(JTable table) {
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        // KHÔNG dùng setBounds(null) cho header
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(180, 205, 230));
        table.setShowGrid(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);

        TableColumnModel m = table.getColumnModel();
        for (int i = 0; i < m.getColumnCount(); i++) {
            String col = m.getColumn(i).getHeaderValue().toString().toLowerCase();
            if (col.contains("mã"))
                m.getColumn(i).setCellRenderer(centerRenderer);
            else if (col.contains("số lượng") || col.contains("sl"))
                m.getColumn(i).setCellRenderer(rightRenderer);
            else if (col.contains("giá") || col.contains("tiền"))
                m.getColumn(i).setCellRenderer(rightRenderer);
            else if (col.contains("ngày"))
                m.getColumn(i).setCellRenderer(centerRenderer);
            else
                m.getColumn(i).setCellRenderer(leftRenderer);
        }
        table.getTableHeader().setReorderingAllowed(false);
    }

    // Placeholder tự xử lý (không cần thư viện ngoài)
    private void addPlaceholder(JTextField field, String placeholder) {
        field.setForeground(Color.GRAY);
        field.setText(placeholder);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Khung trống - clone base");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new QLTraHang_GUI());
            frame.setVisible(true);
        });
    }
}
