package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import customcomponent.PillButton;
import dao.DonViTinh_DAO;
import entity.DonViTinh;

@SuppressWarnings("serial")
public class DonViTinh_QL_GUI extends JPanel implements ActionListener {

    // Components UI
    private JPanel pnHeader, pnCenter;
    private JSplitPane splitPane;

    // Input fields
    private JTextField txtMaDVT, txtTenDVT;
    
    // Search & Table
    private JTextField txtTimKiem;
    private JTable tblDonViTinh;
    private DefaultTableModel modelDonViTinh;

    // Buttons
    private PillButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;

    // DAO & Font
    private final DonViTinh_DAO dvtDAO = new DonViTinh_DAO();
    private List<DonViTinh> dsDonViTinh;
    
    private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);
    private final Color COLOR_PRIMARY = new Color(33, 150, 243);

    public DonViTinh_QL_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 1. HEADER
        taoPhanHeader();
        add(pnHeader, BorderLayout.NORTH);

        // 2. CENTER (SplitPane)
        taoPhanCenter();
        add(pnCenter, BorderLayout.CENTER);

        // 3. LOAD DATA
        loadDataLenBang();
    }

    // ==========================================================================
    //                              PHẦN HEADER
    // ==========================================================================
    private void taoPhanHeader() {
        pnHeader = new JPanel(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94));
        pnHeader.setBackground(new Color(0xE3F2F5));

        // Ô tìm kiếm
        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo tên đơn vị tính...");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimKiem.setBounds(25, 17, 500, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setForeground(Color.GRAY);
        pnHeader.add(txtTimKiem);

        // Nút Tìm kiếm
        btnTimKiem = new PillButton("Tìm kiếm");
        btnTimKiem.setBounds(540, 22, 130, 50);
        btnTimKiem.setFont(FONT_BOLD);
        btnTimKiem.addActionListener(e -> xuLyTimKiem());
        pnHeader.add(btnTimKiem);
    }

    // ==========================================================================
    //                              PHẦN CENTER (SPLIT PANE)
    // ==========================================================================
    private void taoPhanCenter() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- PHẦN TRÊN (TOP): CONTAINER CHỨA FORM VÀ NÚT ---
        JPanel pnTopWrapper = new JPanel(new BorderLayout());
        pnTopWrapper.setBackground(Color.WHITE);
        pnTopWrapper.setBorder(createTitledBorder("Thông tin đơn vị tính"));

        // 1. Panel Form (Nằm giữa - CENTER)
        JPanel pnForm = new JPanel(null);
        pnForm.setBackground(Color.WHITE);
        taoFormNhapLieu(pnForm);
        pnTopWrapper.add(pnForm, BorderLayout.CENTER);

        // 2. Panel Nút (Nằm phải - EAST)
        JPanel pnButton = new JPanel();
        pnButton.setBackground(Color.WHITE);
        taoPanelNutBam(pnButton);
        pnTopWrapper.add(pnButton, BorderLayout.EAST);

        // --- PHẦN DƯỚI (BOTTOM): DANH SÁCH ---
        JPanel pnTable = new JPanel(new BorderLayout());
        pnTable.setBackground(Color.WHITE);
        taoBangDanhSach(pnTable);

        // --- SPLIT PANE ---
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnTopWrapper, pnTable);
        splitPane.setDividerLocation(250); // Form này ít trường nên thấp hơn (250px)
        splitPane.setResizeWeight(0.0); // Cố định form
        
        pnCenter.add(splitPane, BorderLayout.CENTER);
    }

    private void taoFormNhapLieu(JPanel p) {
        // Cấu hình kích thước (Căn giữa vì ít trường)
        int xStart = 100;       
        int yStart = 50;
        int hText = 40;         
        int wTxt = 400;         
        int gap = 30;           

        // Hàng 1: Mã Đơn Vị
        p.add(createLabel("Mã ĐVT:", xStart, yStart));
        txtMaDVT = createTextField(xStart + 100, yStart, wTxt);
        // txtMaDVT.setEditable(false); // Nếu mã tự sinh thì mở dòng này
        p.add(txtMaDVT);

        // Hàng 2: Tên Đơn Vị Tính
        yStart += hText + gap;
        p.add(createLabel("Tên ĐVT:", xStart, yStart));
        txtTenDVT = createTextField(xStart + 100, yStart, wTxt);
        p.add(txtTenDVT);
        
        // Có thể thêm ghi chú hoặc thông tin khác nếu Entity mở rộng sau này
    }

    // --- PANEL RIÊNG CHO NÚT BẤM (BÊN PHẢI) ---
    private void taoPanelNutBam(JPanel p) {
        p.setPreferredSize(new Dimension(200, 0)); 
        p.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY)); 
        
        p.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(8, 0, 8, 0); 
        gbc.fill = GridBagConstraints.HORIZONTAL; 

        int btnH = 45;
        int btnW = 140;

        btnThem = new PillButton("Thêm");
        btnThem.setFont(FONT_BOLD);
        btnThem.setPreferredSize(new Dimension(btnW, btnH));
        btnThem.addActionListener(this);
        gbc.gridy = 0; p.add(btnThem, gbc);

        btnSua = new PillButton("Cập nhật");
        btnSua.setFont(FONT_BOLD);
        btnSua.setPreferredSize(new Dimension(btnW, btnH));
        btnSua.addActionListener(this);
        gbc.gridy = 1; p.add(btnSua, gbc);

        btnXoa = new PillButton("Xóa");
        btnXoa.setFont(FONT_BOLD);
        btnXoa.setPreferredSize(new Dimension(btnW, btnH));
        btnXoa.addActionListener(this);
        gbc.gridy = 2; p.add(btnXoa, gbc);

        btnLamMoi = new PillButton("Làm mới");
        btnLamMoi.setFont(FONT_BOLD);
        btnLamMoi.setPreferredSize(new Dimension(btnW, btnH));
        btnLamMoi.addActionListener(this);
        gbc.gridy = 3; p.add(btnLamMoi, gbc);
    }

    private void taoBangDanhSach(JPanel p) {
        String[] cols = {"Mã Đơn Vị Tính", "Tên Đơn Vị Tính"};
        modelDonViTinh = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDonViTinh = setupTable(modelDonViTinh);

        // Căn lề
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setHorizontalAlignment(JLabel.LEFT);

        // Apply render
        tblDonViTinh.getColumnModel().getColumn(0).setCellRenderer(center);
        tblDonViTinh.getColumnModel().getColumn(1).setCellRenderer(left);
        
        // Set width
        tblDonViTinh.getColumnModel().getColumn(0).setPreferredWidth(200);
        
        // Event click
        tblDonViTinh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                doToForm(tblDonViTinh.getSelectedRow());
            }
        });

        JScrollPane scr = new JScrollPane(tblDonViTinh);
        scr.setBorder(createTitledBorder("Danh sách đơn vị tính"));
        p.add(scr, BorderLayout.CENTER);
    }

    // ==========================================================================
    //                              XỬ LÝ LOGIC
    // ==========================================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o.equals(btnThem)) {
            if (validData()) {
                DonViTinh dvt = getFromForm();
                if (dvtDAO.themDonViTinh(dvt)) {
                    JOptionPane.showMessageDialog(this, "Thêm thành công!");
                    loadDataLenBang();
                    lamMoiForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm thất bại (Trùng mã hoặc lỗi DB)!");
                }
            }
        } 
        else if (o.equals(btnSua)) {
            if (tblDonViTinh.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần sửa!");
                return;
            }
            if (validData()) {
                DonViTinh dvt = getFromForm();
                if (dvtDAO.capNhatDonViTinh(dvt)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadDataLenBang();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
                }
            }
        }
        else if (o.equals(btnXoa)) {
            int row = tblDonViTinh.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa!");
                return;
            }
            String ma = tblDonViTinh.getValueAt(row, 0).toString();
            if (JOptionPane.showConfirmDialog(this, "Xóa đơn vị tính: " + ma + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (dvtDAO.xoaDonViTinh(ma)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa!");
                    loadDataLenBang();
                    lamMoiForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại (Có thể đang được sử dụng)!");
                }
            }
        }
        else if (o.equals(btnLamMoi)) {
            lamMoiForm();
            loadDataLenBang();
        }
    }

    private void doToForm(int row) {
        if (row < 0) return;
        txtMaDVT.setText(tblDonViTinh.getValueAt(row, 0).toString());
        txtTenDVT.setText(tblDonViTinh.getValueAt(row, 1).toString());
        txtMaDVT.setEditable(false); // Khi click vào bảng (sửa) thì khóa mã lại
    }

    private void loadDataLenBang() {
        modelDonViTinh.setRowCount(0);
        dsDonViTinh = dvtDAO.layTatCaDonViTinh();
        for (DonViTinh dvt : dsDonViTinh) {
            modelDonViTinh.addRow(new Object[]{dvt.getMaDonViTinh(), dvt.getTenDonViTinh()});
        }
    }

    private DonViTinh getFromForm() {
        String ma = txtMaDVT.getText().trim();
        String ten = txtTenDVT.getText().trim();
        return new DonViTinh(ma, ten);
    }

    private void lamMoiForm() {
        txtMaDVT.setText("");
        txtTenDVT.setText("");
        txtMaDVT.setEditable(true); // Mở khóa mã để nhập mới
        txtMaDVT.requestFocus();
        tblDonViTinh.clearSelection();
    }

    private void xuLyTimKiem() {
        String kw = txtTimKiem.getText().trim();
        if(kw.isEmpty() || kw.equals("Tìm kiếm theo tên đơn vị tính...")) {
            loadDataLenBang();
            return;
        }
        // Nếu DAO có hàm tìm kiếm thì gọi, không thì filter list
        // Ví dụ filter đơn giản:
        modelDonViTinh.setRowCount(0);
        for (DonViTinh dvt : dsDonViTinh) {
            if (dvt.getTenDonViTinh().toLowerCase().contains(kw.toLowerCase())) {
                modelDonViTinh.addRow(new Object[]{dvt.getMaDonViTinh(), dvt.getTenDonViTinh()});
            }
        }
    }

    private boolean validData() {
        if (txtMaDVT.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã đơn vị tính không được rỗng");
            txtMaDVT.requestFocus(); return false;
        }
        if (txtTenDVT.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên đơn vị tính không được rỗng");
            txtTenDVT.requestFocus(); return false;
        }
        return true;
    }

    // --- UI Helpers ---
    private JLabel createLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TEXT);
        lbl.setBounds(x, y, 100, 35);
        return lbl;
    }

    private JTextField createTextField(int x, int y, int w) {
        JTextField txt = new JTextField();
        txt.setFont(FONT_TEXT);
        txt.setBounds(x, y, w, 35);
        return txt;
    }

    private JTable setupTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(FONT_TEXT);
        table.setRowHeight(35);
        table.setSelectionBackground(new Color(0xC8E6C9));
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setBackground(COLOR_PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        return table;
    }

    private TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
            TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD, Color.DARK_GRAY
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản Lý Đơn Vị Tính");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1300, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new DonViTinh_QL_GUI());
            frame.setVisible(true);
        });
    }
}