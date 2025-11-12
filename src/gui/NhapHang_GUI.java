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
import javax.swing.JSplitPane; // 💡 THÊM IMPORT
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
import dao.NhanVien_DAO;
import dao.PhieuNhap_DAO;
import entity.ChiTietPhieuNhap;
import entity.NhanVien; 
import entity.PhieuNhap;

public class NhapHang_GUI extends JPanel implements ActionListener, MouseListener {

    private static final long serialVersionUID = 1L;
    private JPanel pnCenter, pnHeader, pnRight;
    private PillButton btnThem, btnXuatFile;
    private DefaultTableModel modelPN, modelCTPN;
    private JTable tblPN, tblCTPN;
    private JTextField txtSearch;
    private JSplitPane splitPane; // 💡 THÊM BIẾN SPLITPANE

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DecimalFormat df = new DecimalFormat("#,### đ");

    private final Color blueMint = new Color(180, 220, 240);
    private final Color pinkPastel = new Color(255, 200, 220);

    private PhieuNhap_DAO phieuNhapDAO;
    private ChiTietPhieuNhap_DAO chiTietPhieuNhapDAO;

    public NhapHang_GUI() {
        phieuNhapDAO = new PhieuNhap_DAO();
        chiTietPhieuNhapDAO = new ChiTietPhieuNhap_DAO();
        
        try {
            connectDB.getInstance().connect();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Không thể kết nối đến cơ sở dữ liệu.", 
                "Lỗi Kết Nối", 
                JOptionPane.ERROR_MESSAGE);
        }

        setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        
        pnHeader = new JPanel();
        pnHeader.setPreferredSize(new Dimension(0, 88)); 
        pnHeader.setLayout(null); 
        add(pnHeader, BorderLayout.NORTH);

        txtSearch = new JTextField();
        PlaceholderSupport.addPlaceholder(txtSearch, "Tìm kiếm theo mã PN, tên NCC, tên NV..."); 
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBounds(20, 17, 420, 50); 
        txtSearch.setBorder(new RoundedBorder(15)); 
        pnHeader.add(txtSearch);

        JLabel lblTuNgay = new JLabel("Từ ngày:");
        lblTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTuNgay.setBounds(460, 27, 71, 30); 
        pnHeader.add(lblTuNgay);

        com.toedter.calendar.JDateChooser dateTu = new com.toedter.calendar.JDateChooser();
        dateTu.setDateFormatString("dd/MM/yyyy");
        dateTu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateTu.setBounds(525, 27, 130, 30); 
        dateTu.setDate(new java.util.Date()); 
        pnHeader.add(dateTu);

        JLabel lblDenNgay = new JLabel("Đến:");
        lblDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDenNgay.setBounds(670, 27, 40, 30); 
        pnHeader.add(lblDenNgay);

        com.toedter.calendar.JDateChooser dateDen = new com.toedter.calendar.JDateChooser();
        dateDen.setDateFormatString("dd/MM/yyyy");
        dateDen.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateDen.setBounds(705, 27, 130, 30); 
        dateDen.setDate(new java.util.Date()); 
        pnHeader.add(dateDen);
        


        btnXuatFile = new PillButton("Xuất Excel"); 
        btnXuatFile.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnXuatFile.setBounds(905, 21, 120, 40); 
        btnXuatFile.addActionListener(this);
        pnHeader.add(btnXuatFile);

        // 💡 SỬA: Thay vì add pnCenter và pnRight trực tiếp, hãy tạo JSplitPane
        
        // Panel cho bảng bên trái (Danh sách Phiếu Nhập)
        pnCenter = new JPanel(new BorderLayout());
        
        // Panel cho bảng bên phải (Chi Tiết Phiếu Nhập)
        pnRight = new JPanel();
        pnRight.setLayout(new BorderLayout()); 

        // Tạo JSplitPane
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnCenter, pnRight);
        splitPane.setDividerLocation(700); // Đặt vị trí thanh chia ban đầu
        splitPane.setOneTouchExpandable(true); // Thêm nút mũi tên để thu/gọn
        splitPane.setResizeWeight(0.45); // Tỉ lệ phân chia không gian khi resize cửa sổ
        
        // Thêm JSplitPane vào vị trí CENTER của panel chính
        add(splitPane, BorderLayout.CENTER);

        initTable();
        loadDataPhieuNhap(); 
    }

    private void initTable() {
        String[] phieuNhapCols = {"Mã PN", "Ngày Nhập", "Nhân Viên Lập", "Nhà Cung Cấp", "Tổng Tiền"};
        modelPN = new DefaultTableModel(phieuNhapCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPN = new JTable(modelPN);
        JScrollPane scrollPN = new JScrollPane(tblPN);
        // Thêm scrollPN vào pnCenter (bảng bên trái)
        pnCenter.add(scrollPN, BorderLayout.CENTER); 
        tblPN.addMouseListener(this); 

        // 💡 SỬA: Thêm cột "Đơn Vị Tính"
        String[] cTPhieuCols = {"Mã Lô", "Mã SP", "Tên Sản Phẩm", "Đơn Vị Tính", "SL Nhập", "Đơn Giá Nhập", "Thành Tiền"}; 
        modelCTPN = new DefaultTableModel(cTPhieuCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCTPN = new JTable(modelCTPN);
        JScrollPane scrollCTPN = new JScrollPane(tblCTPN);
        // Thêm scrollCTPN vào pnRight (bảng bên phải)
        pnRight.add(scrollCTPN, BorderLayout.CENTER); 

        formatTableAppearance(tblPN);
        formatTableAppearance(tblCTPN);

        tblPN.setSelectionBackground(blueMint);
        tblPN.getTableHeader().setBackground(pinkPastel);
        tblCTPN.setSelectionBackground(pinkPastel);
        tblCTPN.getTableHeader().setBackground(blueMint);

        setColumnFormats(tblPN);
        setColumnFormats(tblCTPN);
        
         TableColumnModel columnModelCTPN = tblCTPN.getColumnModel();
         columnModelCTPN.getColumn(2).setPreferredWidth(200); // Tên Sản Phẩm
         columnModelCTPN.getColumn(3).setPreferredWidth(100); // Đơn Vị Tính
    }
    
    public void loadDataPhieuNhap() {
        modelPN.setRowCount(0); 
        List<PhieuNhap> dsPhieuNhap = phieuNhapDAO.layDanhSachPhieuNhap(); 
        if (dsPhieuNhap == null || dsPhieuNhap.isEmpty()) {
            return;
        }
        for (PhieuNhap pn : dsPhieuNhap) {
            String tenNV = (pn.getNhanVien() != null) ? pn.getNhanVien().getTenNhanVien() : "N/A";
            String tenNCC = (pn.getNhaCungCap() != null) ? pn.getNhaCungCap().getTenNhaCungCap() : "N/A"; 

            if (pn.getNhaCungCap() != null) {
                tenNCC = pn.getNhaCungCap().getTenNhaCungCap();
            }

            String ngayNhapStr = (pn.getNgayNhap() != null) ? pn.getNgayNhap().format(fmt) : "N/A";
            
            modelPN.addRow(new Object[]{
                pn.getMaPhieuNhap(),
                ngayNhapStr,
                tenNV,
                tenNCC,
                df.format(pn.getTongTien()) 
            });
        }
    }

    private void loadDataChiTietPhieuNhap(String maPhieuNhap) {
        modelCTPN.setRowCount(0); 
        if (maPhieuNhap == null || maPhieuNhap.trim().isEmpty()) {
            return; 
        }
        
        List<ChiTietPhieuNhap> dsChiTiet = chiTietPhieuNhapDAO.timKiemChiTietPhieuNhapBangMa(maPhieuNhap); 
        if (dsChiTiet == null || dsChiTiet.isEmpty()) {
            return;
        }

        for (ChiTietPhieuNhap ct : dsChiTiet) {
            String maLo = (ct.getLoSanPham() != null) ? ct.getLoSanPham().getMaLo() : "N/A";
            String maSP = "N/A";
            String tenSP = "N/A";
            
            // 💡 THÊM: Lấy tên đơn vị tính
            String tenDVT = (ct.getDonViTinh() != null) ? ct.getDonViTinh().getTenDonViTinh() : "N/A";

            if (ct.getLoSanPham() != null && ct.getLoSanPham().getSanPham() != null) {
                 maSP = ct.getLoSanPham().getSanPham().getMaSanPham();
                 tenSP = ct.getLoSanPham().getSanPham().getTenSanPham(); 
                 
                 if (tenSP == null) {
                    tenSP = "N/A";
                 }
            }

            // 💡 SỬA: Thêm tenDVT vào mảng dữ liệu
            modelCTPN.addRow(new Object[]{
                maLo,
                maSP,
                tenSP, 
                tenDVT, // <-- Thêm vào đây
                ct.getSoLuongNhap(),
                df.format(ct.getDonGiaNhap()),
                df.format(ct.getThanhTien()) 
            });
        }
    }

    private void formatTableAppearance(JTable table) {
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 17)); 
        table.getTableHeader().setOpaque(false); 
        table.getTableHeader().setForeground(Color.BLACK); 
        table.getTableHeader().setPreferredSize(new Dimension(0, 30)); 
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 17)); 
        table.setShowGrid(false); 
        table.setIntercellSpacing(new Dimension(0, 0)); 
        table.setFillsViewportHeight(true); 
        table.setSelectionBackground(new Color(180, 205, 230)); 
        table.getTableHeader().setReorderingAllowed(false); 
    }

    private void setColumnFormats(JTable table) {
          DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
          centerRenderer.setHorizontalAlignment(JLabel.CENTER);
          DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
          rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
          DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
          leftRenderer.setHorizontalAlignment(JLabel.LEFT);

          TableColumnModel columnModel = table.getColumnModel();
          for (int i = 0; i < columnModel.getColumnCount(); i++) {
              String header = columnModel.getColumn(i).getHeaderValue().toString();
              if (header.contains("Mã")) {
                  columnModel.getColumn(i).setCellRenderer(centerRenderer);
                   if (header.equals("Mã PN") || header.equals("Mã Lô")) {
                       columnModel.getColumn(i).setPreferredWidth(100);
                   } else if (header.equals("Mã SP")) {
                        columnModel.getColumn(i).setPreferredWidth(80);
                   }
              } else if (header.contains("Ngày")) {
                  columnModel.getColumn(i).setCellRenderer(centerRenderer);
                   columnModel.getColumn(i).setPreferredWidth(100);
              } else if (header.contains("SL")) {
                  columnModel.getColumn(i).setCellRenderer(rightRenderer);
                   columnModel.getColumn(i).setPreferredWidth(80);
              } else if (header.contains("Tiền")) {
                  columnModel.getColumn(i).setCellRenderer(rightRenderer);
                  columnModel.getColumn(i).setPreferredWidth(120);
              } else if (header.equals("Đơn Vị Tính")) {
                  columnModel.getColumn(i).setCellRenderer(centerRenderer);
                  columnModel.getColumn(i).setPreferredWidth(100);
              } else { 
                  columnModel.getColumn(i).setCellRenderer(leftRenderer);
              }
          }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == tblPN) {
            int selectedRow = tblPN.getSelectedRow();
            if (selectedRow != -1 && modelPN.getRowCount() > 0) {
                String maPhieuNhap = modelPN.getValueAt(selectedRow, 0).toString();
                loadDataChiTietPhieuNhap(maPhieuNhap);
            } else {
                modelCTPN.setRowCount(0);
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
 if (source == btnXuatFile) {
             JOptionPane.showMessageDialog(this, "Chức năng Xuất Excel đang được xây dựng.");
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản Lý Nhập Hàng");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1550, 800); 
            frame.setLocationRelativeTo(null); 
            frame.setContentPane(new NhapHang_GUI());
            frame.setVisible(true);
        });
    }
}