/**
 * @author 
 * @version 1.0
 * @since Oct 26, 2025
 *
 * Giao diện quản lý sản phẩm (phiên bản độc lập không dùng entity)
 */

package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.net.URL;
import java.text.DecimalFormat;

import customcomponent.ClipTooltipRenderer;
import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;

public class SanPham_GUI extends JPanel {

    private JPanel pnCenter;
    private JPanel pnHeader;
    private DefaultTableModel modelSP;
    private JTable tblSP;
    private JScrollPane scrSP;
    private JTextField txtSearch;
    private PillButton btnThem;
    private PillButton btnCapNhat;
    private JComboBox<String> cboLoaiHang;
    private JPanel pnLoc;
    private DecimalFormat df = new DecimalFormat("#,##0 đ");

    private Color blueMint = new Color(180, 220, 240);
    private Color pinkPastel = new Color(255, 200, 220);

    public SanPham_GUI() {
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1537, 1168));

        UIManager.put("ComboBox.background", Color.WHITE);
        UIManager.put("ComboBox.selectionBackground", blueMint);
        UIManager.put("ComboBox.selectionForeground", Color.BLACK);
        UIManager.put("ComboBox.foreground", Color.BLACK);
        UIManager.put("ComboBox.disabledBackground", Color.WHITE);

        // ===== HEADER =====
        pnHeader = new JPanel();
        pnHeader.setPreferredSize(new Dimension(1073, 88));
        pnHeader.setBackground(new Color(0xE3F2F5));
        pnHeader.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        add(pnHeader, BorderLayout.NORTH);

        txtSearch = new JTextField("");
        PlaceholderSupport.addPlaceholder(txtSearch, "Tìm kiếm sản phẩm");
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setPreferredSize(new Dimension(420, 44));
        txtSearch.setBorder(new RoundedBorder(20));

        pnLoc = new JPanel();
        pnLoc.setBorder(BorderFactory.createTitledBorder(new RoundedBorder(20), "Lọc và sắp xếp"));
        pnLoc.setBackground(new Color(0, 0, 0, 0));
        pnLoc.setPreferredSize(new Dimension(400, 66));
        pnLoc.setLayout(null);

        cboLoaiHang = new JComboBox<>();
        cboLoaiHang.setLocation(20, 18);
        cboLoaiHang.setSize(150, 30);
        cboLoaiHang.setBackground(Color.white);
        pnLoc.add(cboLoaiHang);

        btnThem = new PillButton("Thêm");
        btnThem.setSize(100, 30);

        btnCapNhat = new PillButton("Cập nhật");
        btnCapNhat.setSize(100, 30);

        pnHeader.add(txtSearch);
        pnHeader.add(pnLoc);
        pnHeader.add(btnThem);
        pnHeader.add(btnCapNhat);

        // ===== CENTER =====
        pnCenter = new JPanel();
        pnCenter.setLayout(new BorderLayout());
        add(pnCenter, BorderLayout.CENTER);

        initTable();
        loadLoaiSanPham();
        loadSanPham();
    }

    private void initTable() {
        String[] cols = {
            "Hình ảnh", "Mã sản phẩm", "Tên sản phẩm", "Loại sản phẩm",
            "Số đăng ký", "Hoạt chất", "Hàm lượng",
            "Hãng sản xuất", "Xuất xứ", "Đường dùng",
            "Giá nhập", "Giá bán", "Quy cách đóng gói",
            "Kệ bán", "Trạng thái"
        };

        modelSP = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblSP = new JTable(modelSP);
        scrSP = new JScrollPane(tblSP);
        pnCenter.add(scrSP);

        formatTable(tblSP);
        tblSP.setSelectionBackground(pinkPastel);
        tblSP.getTableHeader().setBackground(blueMint);

        // Render ảnh
        tblSP.setRowHeight(55);
        tblSP.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                if (value instanceof ImageIcon) {
                    setIcon((ImageIcon) value);
                    setText("");
                } else {
                    setIcon(null);
                    setText(value == null ? "" : value.toString());
                }
            }
        });

        // Gắn tooltip
        for (int col = 1; col < tblSP.getColumnCount(); col++) {
            tblSP.getColumnModel().getColumn(col).setCellRenderer(new ClipTooltipRenderer());
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
            else if (col.contains("giá") || col.contains("tiền")) m.getColumn(i).setCellRenderer(right);
            else m.getColumn(i).setCellRenderer(left);
        }

        table.getTableHeader().setReorderingAllowed(false);
    }

    private void loadLoaiSanPham() {
        cboLoaiHang.addItem("Chọn loại sản phẩm");
        cboLoaiHang.addItem("Thuốc kê đơn");
        cboLoaiHang.addItem("Thuốc không kê đơn");
        cboLoaiHang.addItem("Thực phẩm chức năng");
        cboLoaiHang.addItem("Dụng cụ y tế");
    }

    private void loadSanPham() {
        String[][] data = {
            {"paracetamol.png", "SP000001", "Paracetamol 500mg", "Thuốc không kê đơn", "SDK-001", "Paracetamol", "500mg", "Traphaco", "Việt Nam", "Uống", "800", "1200", "Hộp 10 vỉ x 10 viên", "Kệ A1", "Đang kinh doanh"},
            {"amoxicillin.png", "SP000002", "Amoxicillin 500mg", "Thuốc kê đơn", "SDK-002", "Amoxicillin", "500mg", "DHG Pharma", "Việt Nam", "Uống", "1000", "1600", "Hộp 10 vỉ x 10 viên", "Kệ A2", "Đang kinh doanh"},
            {"vitC.png", "SP000003", "Vitamin C 1000mg", "Thực phẩm chức năng", "SDK-004", "Ascorbic Acid", "1000mg", "Mega We Care", "Thái Lan", "Uống", "900", "1500", "Hộp 10 vỉ x 10 viên", "Kệ B1", "Đang kinh doanh"},
            {"betadine.png", "SP000004", "Betadine 10%", "Dụng cụ y tế", "SDK-006", "Povidone Iodine", "10%", "Mundipharma", "Singapore", "Bôi", "15000", "25000", "Lọ 30ml", "Kệ C1", "Đang kinh doanh"},
            {"smecta.png", "SP000005", "Smecta", "Thuốc không kê đơn", "SDK-015", "Diosmectite", "3g/gói", "Ipsen", "Pháp", "Uống", "4000", "7000", "Hộp 10 gói", "Kệ A3", "Đang kinh doanh"}
        };

        for (String[] sp : data) {
            String imgName = sp[0];
            ImageIcon icon = null;
            URL url = getClass().getResource("/images/" + imgName);

            if (url == null) {
                url = getClass().getResource("/images/icon_anh_sp_null.png");
                if (url == null) {
                    System.err.println("Không tìm thấy ảnh: " + imgName);
                    continue;
                }
            }

            icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);

            modelSP.addRow(new Object[]{
                icon, sp[1], sp[2], sp[3], sp[4], sp[5], sp[6], sp[7], sp[8],
                sp[9], df.format(Double.parseDouble(sp[10])),
                df.format(Double.parseDouble(sp[11])), sp[12], sp[13], sp[14]
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý sản phẩm - Demo Data");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new SanPham_GUI());
            frame.setVisible(true);
        });
    }
}
