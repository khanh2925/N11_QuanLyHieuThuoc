package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.awt.event.*;
import java.util.ArrayList;

import customcomponent.ClipTooltipRenderer;
import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import dao.SanPham_DAO;
import entity.SanPham;
import enums.DuongDung;
import enums.LoaiSanPham;

/**
 * @author
 * @version 1.2
 * @since Oct 28, 2025
 */

public class SanPham_GUI extends JPanel implements ActionListener, MouseListener {

    private JPanel pnCenter, pnHeader, pnLoc;
    private DefaultTableModel modelSP;
    private JTable tblSP;
    private JScrollPane scrSP;
    private JTextField txtSearch;
    private PillButton btnThem, btnCapNhat, btnXemChiTiet;
    private JComboBox<String> cboLoaiHang;
    private DecimalFormat df = new DecimalFormat("#,##0 đ");
    private SanPham_DAO sanPhamDAO;

    private final Color blueMint = new Color(180, 220, 240);
    private final Color pinkPastel = new Color(255, 200, 220);

    public SanPham_GUI() {
        sanPhamDAO = new SanPham_DAO();
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1537, 1168));

        // Header
        pnHeader = new JPanel(null);
        pnHeader.setPreferredSize(new Dimension(1073, 88));
        pnHeader.setBackground(new Color(0xE3F2F5));
        add(pnHeader, BorderLayout.NORTH);

        txtSearch = new JTextField();
        txtSearch.setBounds(20, 17, 420, 60);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtSearch.setBorder(new RoundedBorder(20));
        PlaceholderSupport.addPlaceholder(txtSearch, "Tìm kiếm sản phẩm theo mã");
        pnHeader.add(txtSearch);

        pnLoc = new JPanel(null);
        pnLoc.setBounds(460, 10, 400, 70);
        pnLoc.setBorder(BorderFactory.createTitledBorder(new RoundedBorder(20), "Lọc theo tiêu chí"));
        pnLoc.setBackground(new Color(0, 0, 0, 0));
        pnHeader.add(pnLoc);

        cboLoaiHang = new JComboBox<>();
        cboLoaiHang.setFont(new Font("Tahoma", Font.PLAIN, 18));
        cboLoaiHang.setBounds(75, 19, 250, 40);
        pnLoc.add(cboLoaiHang);

        btnThem = new PillButton("Thêm");
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnThem.setBounds(895, 30, 120, 40);
        btnThem.addActionListener(this);
        pnHeader.add(btnThem);

        btnCapNhat = new PillButton("Cập nhật");
        btnCapNhat.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnCapNhat.setBounds(1045, 30, 120, 40);
        btnCapNhat.addActionListener(this);
        pnHeader.add(btnCapNhat);

        btnXemChiTiet = new PillButton("Xem chi tiết");
        btnXemChiTiet.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnXemChiTiet.setBounds(1210, 30, 140, 40);
        btnXemChiTiet.addActionListener(this);
        pnHeader.add(btnXemChiTiet);

        // Center
        pnCenter = new JPanel(new BorderLayout());
        add(pnCenter, BorderLayout.CENTER);

        initTable();
        loadLoaiSanPham();
        loadSanPham();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);

        if (src.equals(btnThem)) {
            ThemSanPham_Dialog dialog = new ThemSanPham_Dialog(parent);
            dialog.setVisible(true);
            if (dialog.isCreated()) loadSanPham();
        }
        else if (src.equals(btnCapNhat)) {
            int row = tblSP.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần cập nhật!");
                return;
            }
            String maSP = modelSP.getValueAt(row, 1).toString();
            SanPham sp = sanPhamDAO.getSanPhamTheoMa(maSP);
            CapNhatSanPham_Dialog dlg = new CapNhatSanPham_Dialog(parent, sp);
            dlg.setVisible(true);
            if (dlg.isUpdated()) loadSanPham();
        }
        else if (src.equals(btnXemChiTiet)) {
            int row = tblSP.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Chọn sản phẩm để xem chi tiết!");
                return;
            }
            JOptionPane.showMessageDialog(this, "Sản phẩm: " + modelSP.getValueAt(row, 2));
        }
    }

    private void initTable() {
        String[] cols = {
            "Hình ảnh", "Mã sản phẩm", "Tên sản phẩm", "Loại sản phẩm",
            "Số đăng ký", "Đường dùng", "Giá nhập", "Giá bán",
            "Kệ bán", "Trạng thái"
        };

        modelSP = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblSP = new JTable(modelSP);
        scrSP = new JScrollPane(tblSP);
        pnCenter.add(scrSP);
        formatTable(tblSP);

        tblSP.setRowHeight(55);
        tblSP.setSelectionBackground(pinkPastel);
        tblSP.getTableHeader().setBackground(blueMint);
        tblSP.addMouseListener(this);
    }

    private void loadLoaiSanPham() {
        cboLoaiHang.addItem("Chọn loại sản phẩm");
        cboLoaiHang.addItem("Thuốc kê đơn");
        cboLoaiHang.addItem("Thuốc không kê đơn");
        cboLoaiHang.addItem("Thực phẩm chức năng");
        cboLoaiHang.addItem("Dụng cụ y tế");
    }

    /** Nạp dữ liệu từ DAO */
    private void loadSanPham() {
        ArrayList<SanPham> ds = sanPhamDAO.getAllSanPham();
        modelSP.setRowCount(0);

        for (SanPham sp : ds) {
            String imgName = sp.getHinhAnh();
            ImageIcon icon = null;
            URL url = getClass().getResource("/images/" + imgName);
            if (url == null)
                url = getClass().getResource("/images/icon_anh_sp_null.png");
            if (url != null)
                icon = new ImageIcon(new ImageIcon(url).getImage()
                        .getScaledInstance(45, 45, Image.SCALE_SMOOTH));

            modelSP.addRow(new Object[]{
                icon,
                sp.getMaSanPham(),
                sp.getTenSanPham(),
                mapLoaiSanPham(sp.getLoaiSanPham()),
                sp.getSoDangKy(),
                mapDuongDung(sp.getDuongDung()),
                df.format(sp.getGiaNhap()),
                df.format(sp.getGiaBan()),
                sp.getKeBanSanPham(),
                sp.isHoatDong() ? "Đang kinh doanh" : "Ngừng bán"
            });
        }
    }

    private String mapLoaiSanPham(LoaiSanPham loai) {
        if (loai == null) return "";
        switch (loai) {
            case THUOC: return "Thuốc";
            case VAT_TU: return "Vật tư";
            case THUC_PHAM_BO_SUNG: return "Thực phẩm bổ sung";
            case THIET_BI_Y_TE: return "Thiết bị y tế";
            default: return loai.name();
        }
    }

    private String mapDuongDung(DuongDung dd) {
        if (dd == null) return "";
        switch (dd) {
            case UONG: return "Uống";
            case TIEM: return "Tiêm";
            case NHO:  return "Nhỏ";
            case BOI:  return "Bôi";
            case HIT:  return "Hít";
            case NGAM: return "Ngậm";
            case DAT:  return "Đặt";
            case DAN:  return "Dán";
            default: return dd.name();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            int row = tblSP.getSelectedRow();
            if (row >= 0)
                JOptionPane.showMessageDialog(this, "Chi tiết sản phẩm: " +
                        modelSP.getValueAt(row, 2));
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    private void formatTable(JTable table) {
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 18));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        table.setShowGrid(false);
        table.setRowHeight(50);
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(JLabel.RIGHT);

        TableColumnModel m = table.getColumnModel();
        for (int i = 1; i < m.getColumnCount(); i++) {
            String col = m.getColumn(i).getHeaderValue().toString().toLowerCase();
            if (col.contains("giá")) m.getColumn(i).setCellRenderer(right);
            else m.getColumn(i).setCellRenderer(center);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý sản phẩm");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new SanPham_GUI());
            frame.setVisible(true);
        });
    }
}
