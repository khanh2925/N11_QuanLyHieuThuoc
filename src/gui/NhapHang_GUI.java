package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import connectDB.connectDB;
import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import dao.ChiTietPhieuNhap_DAO;
import dao.PhieuNhap_DAO;
import entity.ChiTietPhieuNhap;
import entity.PhieuNhap;

public class NhapHang_GUI extends JPanel implements ActionListener, MouseListener {

    private static final long serialVersionUID = 1L;
    private JPanel pnCenter, pnHeader, pnRight;
    private PillButton btnThem, btnXuatFile;
    private DefaultTableModel modelPN, modelCTPN;
    private JTable tblPN, tblCTPN;
    private JTextField txtSearch;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DecimalFormat df = new DecimalFormat("#,###đ");

    private final Color blueMint = new Color(180, 220, 240);
    private final Color pinkPastel = new Color(255, 200, 220);

    // Khai báo các DAO
    private PhieuNhap_DAO phieuNhapDAO;
    private ChiTietPhieuNhap_DAO chiTietPhieuNhapDAO;

    public NhapHang_GUI() {
        // Khởi tạo DAO
        phieuNhapDAO = new PhieuNhap_DAO();
        chiTietPhieuNhapDAO = new ChiTietPhieuNhap_DAO();
        
        try {
            connectDB.getInstance().connect();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL", "Error", JOptionPane.ERROR_MESSAGE);
        }

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
        dateDen.setDate(new java.util.Date());
        
        btnThem = new PillButton("Thêm");
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnThem.setBounds(922, 30, 120, 40);
        btnThem.addActionListener(this);

        btnXuatFile = new PillButton("Xuất file");
        btnXuatFile.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnXuatFile.setBounds(1068, 30, 120, 40);
        btnXuatFile.addActionListener(this);

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
        pnRight.setPreferredSize(new Dimension(700, 1080));
        pnRight.setLayout(new javax.swing.BoxLayout(pnRight, javax.swing.BoxLayout.Y_AXIS));
        add(pnRight, BorderLayout.EAST);

        initTable();
        loadDataPhieuNhap(); // Thay thế dữ liệu fake
    }

    private void initTable() {
        // Bảng phiếu nhập
        String[] phieuNhapCols = {"Mã PN", "Ngày lập phiếu", "Nhân Viên", "NCC", "Tổng tiền"};
        modelPN = new DefaultTableModel(phieuNhapCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPN = new JTable(modelPN);
        pnCenter.add(new JScrollPane(tblPN));
        tblPN.addMouseListener(this); // Thêm sự kiện click

        // Bảng chi tiết phiếu nhập
        String[] cTPhieuCols = {"Mã lô", "Mã SP", "Tên SP", "SL nhập", "Đơn giá", "Thành tiền"};
        modelCTPN = new DefaultTableModel(cTPhieuCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCTPN = new JTable(modelCTPN);
        pnRight.add(new JScrollPane(tblCTPN));

        formatTable(tblPN);
        tblPN.setSelectionBackground(blueMint);
        tblPN.getTableHeader().setBackground(pinkPastel);

        formatTable(tblCTPN);
        tblCTPN.setSelectionBackground(pinkPastel);
        tblCTPN.getTableHeader().setBackground(blueMint);
    }
    
    // Nạp dữ liệu Phiếu Nhập từ CSDL
    private void loadDataPhieuNhap() {
        modelPN.setRowCount(0);
        List<PhieuNhap> dsPhieuNhap = phieuNhapDAO.layTatCaPhieuNhap();
        for (PhieuNhap pn : dsPhieuNhap) {
            modelPN.addRow(new Object[]{
                pn.getMaPhieuNhap(),
                pn.getNgayNhap().format(fmt),
                pn.getNhanVien().getTenNhanVien(),
                pn.getNhaCungCap().getTenNhaCungCap(),
                df.format(pn.getTongTien())
            });
        }
    }

    // Nạp dữ liệu Chi Tiết Phiếu Nhập từ CSDL
    private void loadDataChiTietPhieuNhap(String maPhieuNhap) {
        modelCTPN.setRowCount(0);
        List<ChiTietPhieuNhap> dsChiTiet = chiTietPhieuNhapDAO.timKiemChiTietPhieuNhapBangMa(maPhieuNhap);
        for (ChiTietPhieuNhap ct : dsChiTiet) {
            modelCTPN.addRow(new Object[]{
                ct.getLoSanPham().getMaLo(),
                ct.getLoSanPham().getSanPham().getMaSanPham(),
                ct.getLoSanPham().getSanPham().getTenSanPham(),
                ct.getSoLuongNhap(),
                df.format(ct.getDonGiaNhap()),
                df.format(ct.getThanhTien())
            });
        }
    }

    private void formatTable(JTable table) {
    	  table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 18));
          table.getTableHeader().setBorder(null);
          table.setRowHeight(28);
          table.setFont(new Font("Segoe UI", Font.PLAIN, 18));
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

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource().equals(tblPN)) {
            int row = tblPN.getSelectedRow();
            if (row != -1) {
                String maPhieuNhap = modelPN.getValueAt(row, 0).toString();
                loadDataChiTietPhieuNhap(maPhieuNhap);
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnThem)) {
            // Logic chuyển trang của bạn
            java.awt.Window win = SwingUtilities.getWindowAncestor(this);
            if (win instanceof JFrame frame) {
                // Tạm thời comment lại để tránh lỗi nếu ThemPhieuNhap_GUI chưa có
                // frame.setContentPane(new ThemPhieuNhap_GUI()); 
                // frame.revalidate();
                // frame.repaint();
                JOptionPane.showMessageDialog(this, "Chức năng Thêm Phiếu Nhập đang được phát triển.");
            }
        } else if (o.equals(btnXuatFile)) {
             JOptionPane.showMessageDialog(this, "Chức năng Xuất File đang được phát triển.");
        }
    }

    // Các phương thức mouse listener khác không cần thiết
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

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