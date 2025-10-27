package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;

public class NhapHang_GUI extends JPanel {

    private JPanel pnCenter;
    private JPanel pnHeader;
    private JPanel pnRight;
    private PillButton btnThem;
    private PillButton btnXuatFile;
    private DefaultTableModel modelPN;
    private JTable tblPN;
    private JScrollPane scrCTPN;
    private DefaultTableModel modelCTPN;
    private JScrollPane scrPN;
    private JTable tblCTPN;
    private JTextField txtSearch;

    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DecimalFormat df = new DecimalFormat("#,###đ");

    private Color blueMint = new Color(180, 220, 240);
    private Color pinkPastel = new Color(255, 200, 220);

    public NhapHang_GUI() {
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
        PlaceholderSupport.addPlaceholder(txtSearch, "Tìm kiếm theo tên / số điện thoại");
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBounds(20, 17, 420, 60);
        txtSearch.setBorder(new RoundedBorder(20));

        JLabel lblTuNgay = new JLabel("Từ ngày:");
        lblTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTuNgay.setBounds(475, 30, 71, 40);

        com.toedter.calendar.JDateChooser dateTu = new com.toedter.calendar.JDateChooser();
        dateTu.setDateFormatString("dd/MM/yyyy");
        dateTu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateTu.setBounds(537, 35, 130, 30);
        dateTu.setDate(new java.util.Date());

        JLabel lblDenNgay = new JLabel("Đến:");
        lblDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDenNgay.setBounds(699, 30, 40, 40);

        com.toedter.calendar.JDateChooser dateDen = new com.toedter.calendar.JDateChooser();
        dateDen.setDateFormatString("dd/MM/yyyy");
        dateDen.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateDen.setBounds(735, 35, 130, 30);

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DATE, 1);
        dateDen.setDate(cal.getTime());

        // ==== Nút thêm phiếu ====
        btnThem = new PillButton("Thêm");
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnThem.setBounds(922, 30, 120, 40);

        // Giữ nguyên sự kiện (nếu có màn ThemPhieuNhap_GUI)
        btnThem.addActionListener(e -> {
            java.awt.Window win = SwingUtilities.getWindowAncestor(this);
            if (win instanceof JFrame frame) {
                frame.setContentPane(new ThemPhieuNhap_GUI());
                frame.revalidate();
                frame.repaint();
            }
        });

        btnXuatFile = new PillButton("Xuất file");
        btnXuatFile.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnXuatFile.setBounds(1068, 30, 120, 40);

        pnHeader.add(txtSearch);
        pnHeader.add(lblTuNgay);
        pnHeader.add(dateTu);
        pnHeader.add(lblDenNgay);
        pnHeader.add(dateDen);
        pnHeader.add(btnThem);
        pnHeader.add(btnXuatFile);

        // ===== CENTER =====
        pnCenter = new JPanel(new BorderLayout());
        add(pnCenter, BorderLayout.CENTER);

        // ===== RIGHT =====
        pnRight = new JPanel();
        pnRight.setPreferredSize(new Dimension(600, 1080));
        pnRight.setBackground(new Color(0, 128, 255));
        pnRight.setLayout(new javax.swing.BoxLayout(pnRight, javax.swing.BoxLayout.Y_AXIS));
        add(pnRight, BorderLayout.EAST);

        initTable();
        LoadPhieuNhap(); // nạp data fake
    }

    private void initTable() {
        String[] phieuNhapCols = {"Mã PN", "Ngày lập phiếu", "Nhân Viên", "NCC", "Tổng tiền"};
        modelPN = new DefaultTableModel(phieuNhapCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPN = new JTable(modelPN);
        scrPN = new JScrollPane(tblPN);
        pnCenter.add(scrPN);

        String[] cTPhieuCols = {"Mã lô", "Mã SP", "Tên SP", "SL nhập", "Đơn giá", "Thành tiền"};
        modelCTPN = new DefaultTableModel(cTPhieuCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCTPN = new JTable(modelCTPN);
        scrCTPN = new JScrollPane(tblCTPN);
        pnRight.add(scrCTPN);

        formatTable(tblPN);
        tblPN.setSelectionBackground(blueMint);
        tblPN.getTableHeader().setBackground(pinkPastel);

        formatTable(tblCTPN);
        tblCTPN.setSelectionBackground(pinkPastel);
        tblCTPN.getTableHeader().setBackground(blueMint);
    }

    /** Data fake thuần tuý, không dùng entity/enums */
    public void LoadPhieuNhap() {
        // ==== PHIẾU NHẬP (master) ====
        // fields: maPN, ngayLap, tenNV, tenNCC, tongTien (sẽ tính từ chi tiết)
        Object[][] pnData = {
            { "PN001", LocalDate.of(2025, 10, 18), "Lê Thanh Kha", "Công ty Dược Hậu Giang", 0.0 },
            { "PN002", LocalDate.of(2025, 10, 20), "Trần Thị B",   "Mekophar",               0.0 }
        };

        // ==== CHI TIẾT (detail) ====
        // fields: (maPN), maLo, maSP, tenSP, slNhap, donGia
        Object[][] ctData = {
            { "PN001", "LO000001", "SP000001", "Paracetamol 500mg", 50, 800.0 },
            { "PN001", "LO000002", "SP000002", "Vitamin C 1000mg",  30, 1200.0 },
            { "PN002", "LO000003", "SP000003", "Efferalgan 500mg",  40, 950.0 },
            { "PN002", "LO000004", "SP000004", "Bông y tế",         80, 120.0 }
        };

        // Tính tổng tiền cho từng PN từ ctData
        for (int i = 0; i < pnData.length; i++) {
            String maPN = pnData[i][0].toString();
            double tong = 0.0;
            for (Object[] ct : ctData) {
                if (maPN.equals(ct[0])) {
                    int sl = (int) ct[4];
                    double donGia = ((Number) ct[5]).doubleValue();
                    tong += sl * donGia;
                }
            }
            pnData[i][4] = tong;
        }

        // Đổ master
        modelPN.setRowCount(0);
        for (Object[] pn : pnData) {
            modelPN.addRow(new Object[]{
                pn[0],
                ((LocalDate) pn[1]).format(fmt),
                pn[2],
                pn[3],
                df.format(((Number) pn[4]).doubleValue())
            });
        }

        // Đổ detail
        modelCTPN.setRowCount(0);
        for (Object[] ct : ctData) {
            int sl = (int) ct[4];
            double donGia = ((Number) ct[5]).doubleValue();
            double thanhTien = sl * donGia;
            modelCTPN.addRow(new Object[]{
                ct[1], // Mã lô
                ct[2], // Mã SP
                ct[3], // Tên SP
                sl,
                df.format(donGia),
                df.format(thanhTien)
            });
        }
    }

    private void formatTable(JTable table) {
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBorder(null);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(180, 205, 230));
        table.setShowGrid(false);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(JLabel.RIGHT);
        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setHorizontalAlignment(JLabel.LEFT);

        TableColumnModel m = table.getColumnModel();
        for (int i = 0; i < m.getColumnCount(); i++) {
            String col = m.getColumn(i).getHeaderValue().toString().toLowerCase();
            if (col.contains("mã")) m.getColumn(i).setCellRenderer(center);
            else if (col.contains("số lượng") || col.contains("sl")) m.getColumn(i).setCellRenderer(right);
            else if (col.contains("giá") || col.contains("tiền")) m.getColumn(i).setCellRenderer(right);
            else if (col.contains("ngày")) m.getColumn(i).setCellRenderer(center);
            else m.getColumn(i).setCellRenderer(left);
        }
        table.getTableHeader().setReorderingAllowed(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý phiếu nhập - Data Fake");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new NhapHang_GUI());
            frame.setVisible(true);
        });
    }
}
