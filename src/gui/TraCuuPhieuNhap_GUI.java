<<<<<<< HEAD
package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
=======
/**
 * @author Quốc Khánh cute
 * @version 1.0
 * @since Oct 19, 2025
 *
 * Mô tả: Giao diện tra cứu phiếu nhập hàng (Layout đồng bộ, Data Fake chuẩn).
 */
package gui;

import java.awt.*;
>>>>>>> f710d40c30013c0a0c1e4efa9a3b06756aca52ab
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
<<<<<<< HEAD
import java.util.List;

=======
>>>>>>> f710d40c30013c0a0c1e4efa9a3b06756aca52ab
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import com.toedter.calendar.JDateChooser;

import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
<<<<<<< HEAD
import dao.PhieuNhap_DAO;
import entity.ChiTietPhieuNhap;
import entity.PhieuNhap;

public class TraCuuPhieuNhap_GUI extends JPanel implements ActionListener, MouseListener {

    private JPanel pnHeader;
    private JPanel pnCenter;

    private JTable tblPhieuNhap;
    private DefaultTableModel modelPhieuNhap;

    private JTable tblChiTiet;
    private DefaultTableModel modelChiTiet;

    private JTextField txtTimKiem;
    private JDateChooser dateTuNgay;
    private JDateChooser dateDenNgay;
    private PillButton btnTimKiem;
    private PillButton btnLamMoi;

    private PhieuNhap_DAO phieuNhap_DAO;
=======

public class TraCuuPhieuNhap_GUI extends JPanel {

    private JPanel pnHeader;
    private JPanel pnCenter;
    
    // Bảng Phiếu Nhập (Trên)
    private JTable tblPhieuNhap;
    private DefaultTableModel modelPhieuNhap;

    // Bảng Chi Tiết Phiếu Nhập (Dưới)
    private JTable tblChiTiet;
    private DefaultTableModel modelChiTiet;

    // Các component lọc
    private JTextField txtTimKiem;
    private JDateChooser dateTuNgay;
    private JDateChooser dateDenNgay;

    // Format
>>>>>>> f710d40c30013c0a0c1e4efa9a3b06756aca52ab
    private final DecimalFormat df = new DecimalFormat("#,###đ");
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TraCuuPhieuNhap_GUI() {
<<<<<<< HEAD
        phieuNhap_DAO = new PhieuNhap_DAO();
=======
>>>>>>> f710d40c30013c0a0c1e4efa9a3b06756aca52ab
        setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
<<<<<<< HEAD
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        taoPhanDau();
        add(pnHeader, BorderLayout.NORTH);

        taoPhanGiua();
        add(pnCenter, BorderLayout.CENTER);

        taiDuLieuPhieuNhap();
        dangKySuKien();
    }

    private void taoPhanDau() {
=======
        // 1. LAYOUT CHÍNH
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 2. HEADER
        taoPhanHeader();
        add(pnHeader, BorderLayout.NORTH);

        // 3. CENTER (2 Bảng)
        taoPhanCenter();
        add(pnCenter, BorderLayout.CENTER);

        // 4. DATA & EVENTS
        loadDuLieuPhieuNhap();
        addEvents();
    }

    // ==============================================================================
    //                              PHẦN HEADER
    // ==============================================================================
    private void taoPhanHeader() {
>>>>>>> f710d40c30013c0a0c1e4efa9a3b06756aca52ab
        pnHeader = new JPanel();
        pnHeader.setLayout(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94));
        pnHeader.setBackground(new Color(0xE3F2F5));

<<<<<<< HEAD
        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã PN, tên nhân viên, nhà cung cấp...");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtTimKiem.setBounds(25, 17, 450, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        pnHeader.add(txtTimKiem);

=======
        // --- 1. Ô TÌM KIẾM TO (Bên trái) ---
        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã phiếu, tên nhân viên, nhà cung cấp...");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimKiem.setBounds(25, 17, 450, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setForeground(Color.GRAY);
        pnHeader.add(txtTimKiem);

        // --- 2. BỘ LỌC NGÀY (Ở giữa) ---
        // Từ ngày
>>>>>>> f710d40c30013c0a0c1e4efa9a3b06756aca52ab
        JLabel lblTu = new JLabel("Từ ngày:");
        lblTu.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTu.setBounds(500, 28, 70, 35);
        pnHeader.add(lblTu);

        dateTuNgay = new JDateChooser();
        dateTuNgay.setDateFormatString("dd/MM/yyyy");
        dateTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dateTuNgay.setBounds(570, 28, 140, 38);
<<<<<<< HEAD
        dateTuNgay.setDate(java.sql.Date.valueOf(LocalDate.now().minusDays(30)));
        pnHeader.add(dateTuNgay);

=======
        dateTuNgay.setDate(new Date()); 
        pnHeader.add(dateTuNgay);

        // Đến ngày
>>>>>>> f710d40c30013c0a0c1e4efa9a3b06756aca52ab
        JLabel lblDen = new JLabel("Đến:");
        lblDen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDen.setBounds(730, 28, 40, 35);
        pnHeader.add(lblDen);

        dateDenNgay = new JDateChooser();
        dateDenNgay.setDateFormatString("dd/MM/yyyy");
        dateDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dateDenNgay.setBounds(770, 28, 140, 38);
<<<<<<< HEAD
        dateDenNgay.setDate(new Date());
        pnHeader.add(dateDenNgay);

        btnTimKiem = new PillButton("Tìm kiếm");
        btnTimKiem.setBounds(1120, 22, 130, 50);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnTimKiem.setIcon(new ImageIcon("src/icon/search.png"));
        pnHeader.add(btnTimKiem);

        btnLamMoi = new PillButton("Làm mới");
        btnLamMoi.setBounds(1265, 22, 130, 50);
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnLamMoi.setIcon(new ImageIcon("src/icon/refresh.png"));
        pnHeader.add(btnLamMoi);
    }

    private void taoPhanGiua() {
=======
        
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DATE, 1);
        dateDenNgay.setDate(cal.getTime());
        pnHeader.add(dateDenNgay);

        // --- 3. CÁC NÚT CHỨC NĂNG (Bên phải) ---
        PillButton btnTimKiem = new PillButton("Tìm kiếm");
        btnTimKiem.setBounds(1120, 22, 130, 50);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnHeader.add(btnTimKiem);
        
        PillButton btnLamMoi = new PillButton("Làm mới");
        btnLamMoi.setBounds(1265, 22, 130, 50);
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnHeader.add(btnLamMoi);
    }

    // ==============================================================================
    //                              PHẦN CENTER
    // ==============================================================================
    private void taoPhanCenter() {
>>>>>>> f710d40c30013c0a0c1e4efa9a3b06756aca52ab
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.5);
        pnCenter.add(splitPane, BorderLayout.CENTER);

<<<<<<< HEAD
        String[] colPhieuNhap = { "STT", "Mã phiếu nhập", "Ngày lập", "Nhân viên", "Nhà cung cấp", "Tổng tiền" };
        modelPhieuNhap = new DefaultTableModel(colPhieuNhap, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblPhieuNhap = thietLapBang(modelPhieuNhap);

=======
        // --- BẢNG 1: DANH SÁCH PHIẾU NHẬP (TOP) ---
        String[] colPhieuNhap = {"STT", "Mã phiếu nhập", "Ngày lập", "Nhân viên", "Nhà cung cấp", "Tổng tiền"};
        modelPhieuNhap = new DefaultTableModel(colPhieuNhap, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        tblPhieuNhap = setupTable(modelPhieuNhap);
        
        // Căn lề
>>>>>>> f710d40c30013c0a0c1e4efa9a3b06756aca52ab
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);

<<<<<<< HEAD
        tblPhieuNhap.getColumnModel().getColumn(0).setCellRenderer(center);
        tblPhieuNhap.getColumnModel().getColumn(1).setCellRenderer(center);
        tblPhieuNhap.getColumnModel().getColumn(2).setCellRenderer(center);
        tblPhieuNhap.getColumnModel().getColumn(5).setCellRenderer(right);

        tblPhieuNhap.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblPhieuNhap.getColumnModel().getColumn(4).setPreferredWidth(250);

        JScrollPane scrollPN = new JScrollPane(tblPhieuNhap);
        scrollPN.setBorder(taoVienTieuDe("Danh sách phiếu nhập hàng"));
        splitPane.setTopComponent(scrollPN);

        String[] colChiTiet = { "STT", "Mã Lô", "Mã SP", "Tên sản phẩm", "ĐVT", "Số lượng", "Đơn giá nhập", "Thành tiền" };
        modelChiTiet = new DefaultTableModel(colChiTiet, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblChiTiet = thietLapBang(modelChiTiet);

        tblChiTiet.getColumnModel().getColumn(0).setCellRenderer(center);
        tblChiTiet.getColumnModel().getColumn(1).setCellRenderer(center);
        tblChiTiet.getColumnModel().getColumn(2).setCellRenderer(center);
        tblChiTiet.getColumnModel().getColumn(4).setCellRenderer(center);
        tblChiTiet.getColumnModel().getColumn(5).setCellRenderer(center);
        tblChiTiet.getColumnModel().getColumn(6).setCellRenderer(right);
        tblChiTiet.getColumnModel().getColumn(7).setCellRenderer(right);

        tblChiTiet.getColumnModel().getColumn(3).setPreferredWidth(250);

        JScrollPane scrollChiTiet = new JScrollPane(tblChiTiet);
        scrollChiTiet.setBorder(taoVienTieuDe("Chi tiết phiếu nhập"));
        splitPane.setBottomComponent(scrollChiTiet);
    }

    private JTable thietLapBang(DefaultTableModel model) {
=======
        tblPhieuNhap.getColumnModel().getColumn(0).setCellRenderer(center); // STT
        tblPhieuNhap.getColumnModel().getColumn(1).setCellRenderer(center); // Mã
        tblPhieuNhap.getColumnModel().getColumn(2).setCellRenderer(center); // Ngày
        tblPhieuNhap.getColumnModel().getColumn(5).setCellRenderer(right);  // Tiền

        tblPhieuNhap.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblPhieuNhap.getColumnModel().getColumn(4).setPreferredWidth(250); // NCC dài
        
        JScrollPane scrollPN = new JScrollPane(tblPhieuNhap);
        scrollPN.setBorder(createTitledBorder("Danh sách phiếu nhập hàng"));
        splitPane.setTopComponent(scrollPN);

        // --- BẢNG 2: CHI TIẾT PHIẾU NHẬP (BOTTOM) ---
        String[] colChiTiet = {"STT", "Mã Lô", "Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá nhập", "Thành tiền"};
        modelChiTiet = new DefaultTableModel(colChiTiet, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        tblChiTiet = setupTable(modelChiTiet);
        
        tblChiTiet.getColumnModel().getColumn(0).setCellRenderer(center);
        tblChiTiet.getColumnModel().getColumn(1).setCellRenderer(center);
        tblChiTiet.getColumnModel().getColumn(2).setCellRenderer(center);
        tblChiTiet.getColumnModel().getColumn(4).setCellRenderer(center); // SL
        tblChiTiet.getColumnModel().getColumn(5).setCellRenderer(right);  // Đơn giá
        tblChiTiet.getColumnModel().getColumn(6).setCellRenderer(right);  // Thành tiền

        tblChiTiet.getColumnModel().getColumn(3).setPreferredWidth(250); // Tên SP

        JScrollPane scrollChiTiet = new JScrollPane(tblChiTiet);
        scrollChiTiet.setBorder(createTitledBorder("Chi tiết phiếu nhập"));
        splitPane.setBottomComponent(scrollChiTiet);
    }

    private JTable setupTable(DefaultTableModel model) {
>>>>>>> f710d40c30013c0a0c1e4efa9a3b06756aca52ab
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(0xC8E6C9));
<<<<<<< HEAD

=======
        
>>>>>>> f710d40c30013c0a0c1e4efa9a3b06756aca52ab
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(new Color(33, 150, 243));
        header.setForeground(Color.WHITE);
        return table;
    }

<<<<<<< HEAD
    private TitledBorder taoVienTieuDe(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), Color.DARK_GRAY);
    }

    private void dangKySuKien() {
        btnTimKiem.addActionListener(this);
        btnLamMoi.addActionListener(this);
        tblPhieuNhap.addMouseListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source.equals(btnTimKiem)) {
            xuLyTimKiem();
        } else if (source.equals(btnLamMoi)) {
            xuLyLamMoi();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource().equals(tblPhieuNhap)) {
            int row = tblPhieuNhap.getSelectedRow();
            if (row != -1) {
                String maPN = tblPhieuNhap.getValueAt(row, 1).toString();
                hienThiChiTietPhieuNhap(maPN);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    private void taiDuLieuPhieuNhap() {
        modelPhieuNhap.setRowCount(0);
        List<PhieuNhap> listPN = phieuNhap_DAO.layDanhSachPhieuNhap();

        int stt = 1;
        for (PhieuNhap pn : listPN) {
            modelPhieuNhap.addRow(new Object[] {
                    stt++,
                    pn.getMaPhieuNhap(),
                    pn.getNgayNhap().format(fmt),
                    pn.getNhanVien().getTenNhanVien(),
                    pn.getNhaCungCap().getTenNhaCungCap(),
                    df.format(pn.getTongTien())
=======
    private TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), Color.DARK_GRAY
        );
    }

    // ==============================================================================
    //                              DỮ LIỆU & SỰ KIỆN
    // ==============================================================================
    
    private void addEvents() {
        // Click phiếu nhập -> Load chi tiết
        tblPhieuNhap.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblPhieuNhap.getSelectedRow();
                if (row >= 0) {
                    String maPN = tblPhieuNhap.getValueAt(row, 1).toString();
                    loadChiTietPhieuNhap(maPN);
                }
            }
        });
    }

    private void loadDuLieuPhieuNhap() {
        // Dữ liệu Chi Tiết (Nguồn để tính tổng tiền)
        Object[][] ctData = {
            { "PN001", "LO000001", "SP000001", "Paracetamol 500mg", 50, 800.0 },
            { "PN001", "LO000002", "SP000002", "Vitamin C 1000mg",  30, 1200.0 },
            { "PN002", "LO000003", "SP000003", "Efferalgan 500mg",  40, 950.0 },
            { "PN002", "LO000004", "SP000004", "Bông y tế",         80, 120.0 },
            { "PN003", "LO000005", "SP000005", "Khẩu trang y tế",   100, 500.0 }
        };

        // Dữ liệu Phiếu Nhập (Master)
        Object[][] pnData = {
            { "1", "PN001", LocalDate.of(2025, 10, 18), "Lê Thanh Kha", "Công ty Dược Hậu Giang", 0.0 },
            { "2", "PN002", LocalDate.of(2025, 10, 20), "Trần Thị B",   "Mekophar",                0.0 },
            { "3", "PN003", LocalDate.of(2025, 10, 21), "Nguyễn Văn A", "Dược Phẩm TW1",           0.0 }
        };

        modelPhieuNhap.setRowCount(0);

        // Tính tổng tiền và đổ dữ liệu
        for (Object[] pn : pnData) {
            String maPN = pn[1].toString();
            double tongTien = 0;

            for (Object[] ct : ctData) {
                if (maPN.equals(ct[0])) {
                    int sl = (int) ct[4];
                    double gia = (double) ct[5];
                    tongTien += sl * gia;
                }
            }

            modelPhieuNhap.addRow(new Object[]{
                pn[0], // STT
                pn[1], // Mã
                ((LocalDate) pn[2]).format(fmt), // Ngày
                pn[3], // NV
                pn[4], // NCC
                df.format(tongTien) // Tổng tiền
>>>>>>> f710d40c30013c0a0c1e4efa9a3b06756aca52ab
            });
        }
    }

<<<<<<< HEAD
    private void xuLyTimKiem() {
        String keyword = txtTimKiem.getText().trim();
        if (keyword.equals("Tìm theo mã PN, tên nhân viên, nhà cung cấp...")) {
            keyword = "";
        }

        Date tuNgay = dateTuNgay.getDate();
        Date denNgay = dateDenNgay.getDate();

        if (tuNgay == null || denNgay == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ ngày bắt đầu và kết thúc!");
            return;
        }

        if (tuNgay.after(denNgay)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được lớn hơn ngày kết thúc!");
            return;
        }

        modelPhieuNhap.setRowCount(0);
        List<PhieuNhap> listPN = phieuNhap_DAO.timKiemPhieuNhap(keyword, tuNgay, denNgay);

        if (listPN.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu nhập nào phù hợp!");
            return;
        }

        int stt = 1;
        for (PhieuNhap pn : listPN) {
            modelPhieuNhap.addRow(new Object[] {
                    stt++,
                    pn.getMaPhieuNhap(),
                    pn.getNgayNhap().format(fmt),
                    pn.getNhanVien().getTenNhanVien(),
                    pn.getNhaCungCap().getTenNhaCungCap(),
                    df.format(pn.getTongTien())
            });
        }
    }

    private void xuLyLamMoi() {
        txtTimKiem.setText("");
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã PN, tên nhân viên, nhà cung cấp...");
        dateTuNgay.setDate(java.sql.Date.valueOf(LocalDate.now().minusDays(30)));
        dateDenNgay.setDate(new Date());
        taiDuLieuPhieuNhap();
        modelChiTiet.setRowCount(0);
    }

    private void hienThiChiTietPhieuNhap(String maPN) {
        modelChiTiet.setRowCount(0);

        PhieuNhap pn = phieuNhap_DAO.timPhieuNhapTheoMa(maPN);

        if (pn != null && pn.getChiTietPhieuNhapList() != null) {
            int stt = 1;
            for (ChiTietPhieuNhap ct : pn.getChiTietPhieuNhapList()) {
                String tenSP = "Không xác định";
                String maSP = "";

                if (ct.getLoSanPham() != null && ct.getLoSanPham().getSanPham() != null) {
                    tenSP = ct.getLoSanPham().getSanPham().getTenSanPham();
                    maSP = ct.getLoSanPham().getSanPham().getMaSanPham();
                }

                String donVi = "";
                if (ct.getDonViTinh() != null) {
                    donVi = ct.getDonViTinh().getTenDonViTinh();
                }

                modelChiTiet.addRow(new Object[] {
                        stt++,
                        ct.getLoSanPham().getMaLo(),
                        maSP,
                        tenSP,
                        donVi,
                        ct.getSoLuongNhap(),
                        df.format(ct.getDonGiaNhap()),
                        df.format(ct.getThanhTien())
=======
    private void loadChiTietPhieuNhap(String maPN) {
        modelChiTiet.setRowCount(0);
        
        Object[][] ctData = {
            { "PN001", "LO000001", "SP000001", "Paracetamol 500mg", 50, 800.0 },
            { "PN001", "LO000002", "SP000002", "Vitamin C 1000mg",  30, 1200.0 },
            { "PN002", "LO000003", "SP000003", "Efferalgan 500mg",  40, 950.0 },
            { "PN002", "LO000004", "SP000004", "Bông y tế",         80, 120.0 },
            { "PN003", "LO000005", "SP000005", "Khẩu trang y tế",   100, 500.0 }
        };

        int stt = 1;
        for (Object[] ct : ctData) {
            if (ct[0].equals(maPN)) {
                int sl = (int) ct[4];
                double gia = (double) ct[5];
                double thanhTien = sl * gia;

                modelChiTiet.addRow(new Object[]{
                    stt++,
                    ct[1], // Mã Lô
                    ct[2], // Mã SP
                    ct[3], // Tên SP
                    sl,
                    df.format(gia),
                    df.format(thanhTien)
>>>>>>> f710d40c30013c0a0c1e4efa9a3b06756aca52ab
                });
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
<<<<<<< HEAD
            } catch (Exception e) {
            }
=======
            } catch (Exception e) {}
>>>>>>> f710d40c30013c0a0c1e4efa9a3b06756aca52ab
            JFrame frame = new JFrame("Tra cứu phiếu nhập");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new TraCuuPhieuNhap_GUI());
            frame.setVisible(true);
        });
    }
}