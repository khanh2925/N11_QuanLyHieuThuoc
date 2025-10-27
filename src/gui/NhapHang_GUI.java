/**
 * @author Thanh Kha
 * @version 1.1
 * @since Oct 27, 2025
 *
 * Mô tả: Giao diện quản lý phiếu nhập hàng (bấm Thêm -> mở ThemPhieuNhap_GUI)
 */

package gui;

import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

import customcomponent.*;
import entity.*;
import enums.DuongDung;

public class NhapHang_GUI extends JPanel {

    private JPanel pnCenter;
    private JPanel pnHeader;
    private JPanel pnRight;
    private JButton btnThem;
    private JButton btnXuatFile;
    private DefaultTableModel modelPN;
    private JTable tblPN;
    private JScrollPane scrCTPN;
    private DefaultTableModel modelCTPN;
    private JScrollPane scrPN;
    private JTable tblCTPN;
    private JTextField txtSearch;

    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DecimalFormat df = new DecimalFormat("#,000.#đ");

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

        // Sự kiện bấm -> chuyển sang ThemPhieuNhap_GUI
        btnThem.addActionListener(e -> {
            Window win = SwingUtilities.getWindowAncestor(this);
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
        pnRight.setLayout(new BoxLayout(pnRight, BoxLayout.Y_AXIS));
        add(pnRight, BorderLayout.EAST);

        initTable();
        LoadPhieuNhap();
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

    public void LoadPhieuNhap() {
        DonViTinh vien = new DonViTinh("DVT-001", "Viên", null);
        LoaiSanPham thuoc = new LoaiSanPham("LSP001", "Thuốc", null);
        DuongDung uong = DuongDung.UONG;

        SanPham sp1 = new SanPham("SP000001", "Paracetamol 500mg", thuoc, "VN-12345",
                "Paracetamol", "500mg", "DHG Pharma", "Việt Nam",
                vien, uong, 800, 1500, "paracetamol.jpg", "Hộp 10 vỉ x 10 viên", "A1", true);

        SanPham sp2 = new SanPham("SP000002", "Vitamin C 1000mg", thuoc, "VN-67890",
                "Ascorbic Acid", "1000mg", "Traphaco", "Việt Nam",
                vien, uong, 1200, 2500, "vitaminc.jpg", "Hộp 5 vỉ x 10 viên", "A2", true);

        LoSanPham lo1 = new LoSanPham("LO000001", LocalDate.of(2025, 1, 1),
                LocalDate.of(2027, 1, 1), 100, sp1);
        LoSanPham lo2 = new LoSanPham("LO000002", LocalDate.of(2024, 1, 1),
                LocalDate.of(2026, 1, 1), 200, sp2);

        TaiKhoan tk1 = new TaiKhoan("TK000001", "admin", "Aa123456@");
        NhanVien nv1 = new NhanVien("NV2025100001", "Lê Thanh Kha", true,
                LocalDate.of(1998, 5, 10), "0912345678", "TP.HCM",
                true, tk1, "SANG", true);

        NhaCungCap ncc1 = new NhaCungCap("NCC-001", "Công ty Dược Hậu Giang",
                "0283822334", "Cần Thơ");

        PhieuNhap pn1 = new PhieuNhap("PN001", LocalDate.of(2025, 10, 18),
                ncc1, nv1, 0);

        ChiTietPhieuNhap ct1 = new ChiTietPhieuNhap(pn1, lo1, 50, 800);
        ChiTietPhieuNhap ct2 = new ChiTietPhieuNhap(pn1, lo2, 30, 1200);
        List<ChiTietPhieuNhap> chiTiet = Arrays.asList(ct1, ct2);

        double tong = chiTiet.stream().mapToDouble(ChiTietPhieuNhap::getThanhTien).sum();
        pn1.setTongTien(tong);

        modelPN.addRow(new Object[]{pn1.getMaPhieuNhap(),
                pn1.getNgayNhap().format(fmt),
                pn1.getNhanVien().getTenNhanVien(),
                pn1.getNhaCungCap().getTenNhaCungCap(),
                df.format(pn1.getTongTien())});

        for (ChiTietPhieuNhap c : chiTiet) {
            modelCTPN.addRow(new Object[]{
                    c.getLoSanPham().getMaLo(),
                    c.getLoSanPham().getSanPham().getMaSanPham(),
                    c.getLoSanPham().getSanPham().getTenSanPham(),
                    c.getSoLuongNhap(),
                    df.format(c.getDonGiaNhap()),
                    df.format(c.getThanhTien())
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
            JFrame frame = new JFrame("Quản lý phiếu nhập");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new NhapHang_GUI());
            frame.setVisible(true);
        });
    }
}
