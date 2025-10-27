package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import connectDB.connectDB;
import customcomponent.ImagePanel;
import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import dao.KhachHang_DAO;
import entity.KhachHang;

public class KhachHang_NV_GUI extends JPanel implements ActionListener, MouseListener {

    private JPanel pnCenter;
    private JPanel pnHeader;
    private JTextField txtTimKiem;
    private JTable table;

    // === KHAI BÁO BIẾN THÀNH VIÊN ===
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private JCheckBox chckbxNam;
    private JCheckBox chckbxNu;
    private JCheckBox chckbxTangDan;
    private JCheckBox chckbxGiamDan;
    private JPanel pnLoc;
    private KhachHang_DAO kh_dao;
    private JButton btnThem;
    private ThemKhachHang_Dialog dialogThemKH;
    private CapNhatKhachHang_Dialog dialogCapNhap;
    private JButton btnCapNhat;
    
    public KhachHang_NV_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
//    	 kết nói database
//    	
//    	try {
//			connectDB.getInstance().connect();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//    	kh_dao = new KhachHang_DAO();
    	
    	
        setLayout(new BorderLayout());

        // ===== HEADER =====
        pnHeader = new JPanel();
        pnHeader.setPreferredSize(new Dimension(1073, 88));
        pnHeader.setBackground(new Color(0xE3F2F5));
        pnHeader.setLayout(null);
        add(pnHeader, BorderLayout.NORTH);

        txtTimKiem = new JTextField("");
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo tên / số điện thoại");
        txtTimKiem.setForeground(Color.GRAY);
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtTimKiem.setBounds(10, 17, 420, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        pnHeader.add(txtTimKiem);


        btnThem=new PillButton("Thêm");
        pnHeader.add(btnThem);
        btnThem.setBounds(786, 25, 120, 40);
        btnThem.setLayout(null);
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 18));

        
        btnCapNhat =new PillButton("Cập nhật");
        btnCapNhat.setLayout(null);
        btnCapNhat.setBounds(947, 25, 120, 40);
        pnHeader.add(btnCapNhat);
        btnCapNhat.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        // ===== CENTER =====
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new LineBorder(new Color(200, 200, 200)));
        add(pnCenter, BorderLayout.CENTER);

        // ===== DỮ LIỆU BẢNG =====
        String[] columnNames = {"Mã khách hàng", "Tên khách hàng", "Giới tính", "Số điện thoại", "Ngày sinh"};

        model = new DefaultTableModel(columnNames, 0) {
             @Override
             public Class<?> getColumnClass(int columnIndex) {
                 if (columnIndex == 5) {
                     return Integer.class;
                 }
                 return super.getColumnClass(columnIndex);
             }
        };
        
        loadTableData(); // Tải dữ liệu mẫu vào bảng

        table = new JTable(model);
        
        // ===== CẤU HÌNH GIAO DIỆN BẢNG =====
        table.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        table.setRowHeight(34);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(204, 229, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setIntercellSpacing(new Dimension(8, 5));
        table.setBackground(Color.WHITE);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(new Color(33, 150, 243));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 40));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);


        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 248, 252));
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnCenter.add(scrollPane, BorderLayout.CENTER);



        
        // 1. Khởi tạo pnLoc và thêm nó vào pnHeader
        pnLoc = new JPanel();
        pnLoc.setBorder(new RoundedBorder(20));
        pnLoc.setBackground(new Color(240, 255, 255)); // Cùng màu nền với header
        pnLoc.setBounds(459, 9, 284, 70);
        pnHeader.add(pnLoc);
        pnLoc.setLayout(null);


       

        JLabel lblGioiTinh = new JLabel("Giới tính:");
        lblGioiTinh.setBackground(new Color(240, 255, 255));
        lblGioiTinh.setBounds(20, 31, 90, 25);
        lblGioiTinh.setFont(new Font("Tahoma", Font.PLAIN, 18));
        pnLoc.add(lblGioiTinh);

        chckbxNam = new JCheckBox("Nam");
        chckbxNam.setBounds(116, 33, 57, 23);
        chckbxNam.setBackground(new Color(240, 255, 255));
        chckbxNam.setFont(new Font("Tahoma", Font.PLAIN, 15));
        pnLoc.add(chckbxNam);

        chckbxNu = new JCheckBox("Nữ");
        chckbxNu.setBounds(190, 33, 57, 23);
        chckbxNu.setFont(new Font("Tahoma", Font.PLAIN, 15));
        chckbxNu.setBackground(new Color(240, 255, 255));
        pnLoc.add(chckbxNu);
        JLabel lbLoc = new JLabel("Lọc dữ liệu");
        lbLoc.setBounds(10, 5, 100, 14);
        lbLoc.setFont(new Font("Tahoma", Font.PLAIN, 15));
        pnLoc.add(lbLoc);


        // ===== SỰ KIỆN LỌC VÀ SẮP XẾP =====
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // --- SỰ KIỆN LỌC GIỚI TÍNH ---
        ActionListener filterListener = e -> {
            JCheckBox source = (JCheckBox) e.getSource();
            if (source == chckbxNam && chckbxNam.isSelected()) {
                chckbxNu.setSelected(false);
            } else if (source == chckbxNu && chckbxNu.isSelected()) {
                chckbxNam.setSelected(false);
            }
            applyFilters();
        };
        
        // thêm sự kiện
        chckbxNam.addActionListener(filterListener);
        chckbxNu.addActionListener(filterListener);
        btnThem.addActionListener(this);
        btnCapNhat.addActionListener(this);

        // --- SỰ KIỆN TÌM KIẾM THEO TEXTFIELD ---
        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilters();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilters();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                // Not used for plain text fields
            }
        });
        

    }

    /**
     * Tải dữ liệu mẫu vào table model.
     */
    private void loadTableData() {
        List<KhachHang> dsKhachHang = new ArrayList<>();
        dsKhachHang.add(new KhachHang("KH-0001", "Nguyễn Văn A", true, "0901234567", LocalDate.of(1995, 5, 12)));
        dsKhachHang.add(new KhachHang("KH-0002", "Trần Thị B", false, "0912345678", LocalDate.of(2000, 8, 20)));
        dsKhachHang.add(new KhachHang("KH-0003", "Lê Minh C", true, "0923456789", LocalDate.of(1988, 3, 5)));
        dsKhachHang.add(new KhachHang("KH-0004", "Phạm Ngọc D", false, "0934567890", LocalDate.of(1999, 12, 30)));
        dsKhachHang.add(new KhachHang("KH-0005", "Võ Thanh E", true, "0945678901", LocalDate.of(1992, 7, 18)));
        dsKhachHang.add(new KhachHang("KH-0006", "Bùi Thị F", false, "0956789012", LocalDate.of(1997, 9, 10)));
        dsKhachHang.add(new KhachHang("KH-0007", "Đặng Hoàng G", true, "0967890123", LocalDate.of(1985, 2, 22)));
        dsKhachHang.add(new KhachHang("KH-0008", "Phan Thị H", false, "0978901234", LocalDate.of(1998, 4, 8)));
        dsKhachHang.add(new KhachHang("KH-0009", "Ngô Minh I", true, "0989012345", LocalDate.of(1993, 11, 15)));
        dsKhachHang.add(new KhachHang("KH-0010", "Huỳnh Thị K", false, "0990123456", LocalDate.of(2001, 1, 25)));
        dsKhachHang.add(new KhachHang("KH-0011", "Trịnh Công L", true, "0902345678", LocalDate.of(1990, 6, 2)));
        dsKhachHang.add(new KhachHang("KH-0012", "Đoàn Thị M", false, "0913456789", LocalDate.of(1996, 8, 14)));
        dsKhachHang.add(new KhachHang("KH-0013", "Lâm Hữu N", true, "0924567890", LocalDate.of(1989, 3, 28)));
        dsKhachHang.add(new KhachHang("KH-0014", "Tạ Thị O", false, "0935678901", LocalDate.of(1994, 5, 9)));
        dsKhachHang.add(new KhachHang("KH-0015", "Hồ Nhật P", true, "0946789012", LocalDate.of(1998, 10, 19)));
        dsKhachHang.add(new KhachHang("KH-0016", "Lý Thị Q", false, "0957890123", LocalDate.of(2002, 12, 2)));
        dsKhachHang.add(new KhachHang("KH-0017", "Trương Văn R", true, "0968901234", LocalDate.of(1991, 7, 11)));
        dsKhachHang.add(new KhachHang("KH-0018", "Đinh Thị S", false, "0979012345", LocalDate.of(1993, 9, 22)));
        dsKhachHang.add(new KhachHang("KH-0019", "Cao Văn T", true, "0980123456", LocalDate.of(1987, 4, 30)));
        dsKhachHang.add(new KhachHang("KH-0020", "Nguyễn Thị U", false, "0991234567", LocalDate.of(1999, 11, 5)));

        for (KhachHang kh : dsKhachHang) {
            model.addRow(new Object[]{
                kh.getMaKhachHang(),
                kh.getTenKhachHang(),
                kh.isGioiTinh() ? "Nam" : "Nữ",
                kh.getSoDienThoai(),
                kh.getNgaySinh(),
                
            });
        }
    }

    /**
     * Áp dụng đồng thời các bộ lọc từ ô tìm kiếm và checkbox giới tính.
     */
    private void applyFilters() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        // 1. Lọc theo ô tìm kiếm (Tên và SĐT)
        String searchText = txtTimKiem.getText().trim();
        // Chỉ lọc khi ô tìm kiếm không rỗng và không phải là placeholder
        if (!searchText.isEmpty() && !txtTimKiem.getForeground().equals(Color.GRAY)) {
            // "(?i)" để tìm kiếm không phân biệt hoa thường
            filters.add(RowFilter.regexFilter("(?i)" + searchText, 1, 3));
        }

        // 2. Lọc theo giới tính
        if (chckbxNam.isSelected()) {
            filters.add(RowFilter.regexFilter("Nam", 2));
        } else if (chckbxNu.isSelected()) {
            filters.add(RowFilter.regexFilter("Nữ", 2));
        }

        // Kết hợp các bộ lọc
        if (filters.isEmpty()) {
            sorter.setRowFilter(null); 
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters)); 
        }
    }
    
    // ===== MAIN =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Ql khách hàng");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new KhachHang_NV_GUI());
            frame.setVisible(true);
        });
    }

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	    Object src = e.getSource();

	    if (src == btnThem) {
	        ThemKH();
	        return;
	    }

	    if (src == btnCapNhat) {
	    	JFrame frameCapNhat = (JFrame) SwingUtilities.getWindowAncestor(this);
	    	dialogCapNhap = new CapNhatKhachHang_Dialog(frameCapNhat, null);
	    	dialogCapNhap.setVisible(true); 
	    }
	}
	// mở diaLog Thêm khách hàng
	private void MoDiaLogThemKH() {
		JFrame frameThemKH = (JFrame) SwingUtilities.getWindowAncestor(this);
        dialogThemKH = new ThemKhachHang_Dialog(frameThemKH);
        dialogThemKH.setVisible(true); 
	}
	
	// Sk thêm khách hàng
	private void ThemKH() {
		MoDiaLogThemKH();
		
		
	}
	
	
	//mở diaLog Cập nhật khách hàng
	private void moDiaLogCapNhatKH() {
		JFrame frameCapNhat = (JFrame) SwingUtilities.getWindowAncestor(this);
	    dialogCapNhap = new CapNhatKhachHang_Dialog(frameCapNhat, null);
	    dialogCapNhap.setVisible(true); 
	}
		
	// Sk cập nhật khách hàng
	private void CapNhatKH() {
		moDiaLogCapNhatKH();
		
	}

}