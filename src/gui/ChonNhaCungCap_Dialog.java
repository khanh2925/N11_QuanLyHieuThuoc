package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import dao.NhaCungCap_DAO;
import entity.NhaCungCap;

public class ChonNhaCungCap_Dialog extends JDialog {

    private JTable tblNCC;
    private DefaultTableModel modelNCC;
    private JTextField txtSearch;
    private JButton btnChon, btnHuy;
    private NhaCungCap_DAO nccDAO;
    private NhaCungCap selectedNhaCungCap = null;
    private TableRowSorter<DefaultTableModel> sorter;

    public ChonNhaCungCap_Dialog(JFrame parent) {
        super(parent, "Chọn Nhà Cung Cấp", true); // true để dialog này chặn tương tác với cửa sổ cha
        nccDAO = new NhaCungCap_DAO();

        setSize(800, 500);
        setLocationRelativeTo(parent);
        initialize();
        loadData();
    }

    private void initialize() {
        // === Main Panel ===
        JPanel pnMain = new JPanel(new BorderLayout(10, 10));
        pnMain.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(pnMain);

        // === Top Panel (Search) ===
        JPanel pnTop = new JPanel(new BorderLayout());
        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnTop.add(txtSearch, BorderLayout.CENTER);
        pnMain.add(pnTop, BorderLayout.NORTH);

        // === Center Panel (Table) ===
        String[] columnNames = {"Mã NCC", "Tên Nhà Cung Cấp", "Số Điện Thoại", "Địa Chỉ"};
        modelNCC = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblNCC = new JTable(modelNCC);
        sorter = new TableRowSorter<>(modelNCC);
        tblNCC.setRowSorter(sorter);
        tblNCC.setRowHeight(25);
        tblNCC.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(tblNCC);
        pnMain.add(scrollPane, BorderLayout.CENTER);

        // === Bottom Panel (Buttons) ===
        JPanel pnBottom = new JPanel();
        btnChon = new JButton("Chọn");
        btnHuy = new JButton("Hủy");
        pnBottom.add(Box.createHorizontalGlue()); // Đẩy nút sang phải
        pnBottom.add(btnChon);
        pnBottom.add(btnHuy);
        pnMain.add(pnBottom, BorderLayout.SOUTH);

        // === Events ===
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = txtSearch.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    // Lọc trên tất cả các cột
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        btnChon.addActionListener(e -> handleSelect());
        
        // Cho phép double-click để chọn
        tblNCC.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleSelect();
                }
            }
        });

        btnHuy.addActionListener(e -> dispose());
    }

    private void loadData() {
        modelNCC.setRowCount(0);
        List<NhaCungCap> dsNCC = nccDAO.layTatCaNhaCungCap();
        for (NhaCungCap ncc : dsNCC) {
            modelNCC.addRow(new Object[]{
                ncc.getMaNhaCungCap(),
                ncc.getTenNhaCungCap(),
                ncc.getSoDienThoai(),
                ncc.getDiaChi()
            });
        }
    }
    
    private void handleSelect() {
        int selectedRow = tblNCC.getSelectedRow();
        if (selectedRow != -1) {
            // Chuyển đổi index của view sang model trong trường hợp bảng đang được sắp xếp/lọc
            int modelRow = tblNCC.convertRowIndexToModel(selectedRow);
            String maNCC = modelNCC.getValueAt(modelRow, 0).toString();
            this.selectedNhaCungCap = nccDAO.timNhaCungCapTheoMa(maNCC);
            dispose(); // Đóng dialog
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhà cung cấp.", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Lấy nhà cung cấp đã được người dùng chọn.
     * @return Đối tượng NhaCungCap hoặc null nếu không chọn.
     */
    public NhaCungCap getSelectedNhaCungCap() {
        return this.selectedNhaCungCap;
    }
}