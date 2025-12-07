package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import com.toedter.calendar.JDateChooser;

import component.button.PillButton;
import component.input.PlaceholderSupport;
import component.border.RoundedBorder;
import dao.PhieuNhap_DAO;
import entity.ChiTietPhieuNhap;
import entity.PhieuNhap;

public class TraCuuPhieuNhap_GUI extends JPanel implements ActionListener, MouseListener {

    private JPanel pnHeader;
    private JPanel pnCenter;
    private String hello;
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
    private final DecimalFormat df = new DecimalFormat("#,###đ");
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TraCuuPhieuNhap_GUI() {
        phieuNhap_DAO = new PhieuNhap_DAO();
        setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
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
        pnHeader = new JPanel();
        pnHeader.setLayout(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94));
        pnHeader.setBackground(new Color(0xE3F2F5));

        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã PN, tên nhân viên, nhà cung cấp...");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimKiem.setBounds(25, 17, 480, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        pnHeader.add(txtTimKiem);

        JLabel lblTu = new JLabel("Từ ngày:");
        lblTu.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTu.setBounds(540, 28, 70, 35);
        pnHeader.add(lblTu);

        dateTuNgay = new JDateChooser();
        dateTuNgay.setDateFormatString("dd/MM/yyyy");
        dateTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dateTuNgay.setBounds(620, 28, 140, 38);
        dateTuNgay.setDate(java.sql.Date.valueOf(LocalDate.now().minusDays(30)));
        pnHeader.add(dateTuNgay);

        JLabel lblDen = new JLabel("Đến:");
        lblDen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDen.setBounds(780, 28, 40, 35);
        pnHeader.add(lblDen);

        dateDenNgay = new JDateChooser();
        dateDenNgay.setDateFormatString("dd/MM/yyyy");
        dateDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dateDenNgay.setBounds(820, 28, 140, 38);
        dateDenNgay.setDate(new Date());
        pnHeader.add(dateDenNgay);

        btnTimKiem = new PillButton("Tìm kiếm");
        btnTimKiem.setBounds(1020, 22, 130, 50);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnTimKiem.setIcon(new ImageIcon("src/icon/search.png"));
        pnHeader.add(btnTimKiem);

        btnLamMoi = new PillButton("Làm mới");
        btnLamMoi.setBounds(1165, 22, 130, 50);
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnLamMoi.setIcon(new ImageIcon("src/icon/refresh.png"));
        pnHeader.add(btnLamMoi);
    }

    private void taoPhanGiua() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.5);
        pnCenter.add(splitPane, BorderLayout.CENTER);

        String[] colPhieuNhap = { "STT", "Mã phiếu nhập", "Ngày lập", "Nhân viên", "Nhà cung cấp", "Tổng tiền" };
        modelPhieuNhap = new DefaultTableModel(colPhieuNhap, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblPhieuNhap = thietLapBang(modelPhieuNhap);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);

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
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(0xC8E6C9));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(new Color(33, 150, 243));
        header.setForeground(Color.WHITE);
        return table;
    }

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
            });
        }
    }

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
                });
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
            }
            JFrame frame = new JFrame("Tra cứu phiếu nhập");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new TraCuuPhieuNhap_GUI());
            frame.setVisible(true);
        });
    }
}