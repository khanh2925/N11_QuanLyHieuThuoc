package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;
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
import entity.NhanVien;

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
    private JPanel pnLoc;
    private KhachHang_DAO kh_dao;
    private JButton btnThem;
    private ThemKhachHang_Dialog dialogThemKH;
    private CapNhatKhachHang_Dialog dialogCapNhap;
    private JButton btnCapNhat;
    private JFrame frameThemKH;
    private PillButton btnTim;
    
    private List<KhachHang> dsKhachHang;
    
    public KhachHang_NV_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
//    	 kết nói database
  	
    	try {
				connectDB.getInstance().connect();
		} catch (Exception e) {
				e.printStackTrace();
		}
  		kh_dao = new KhachHang_DAO();
   	
    	
        setLayout(new BorderLayout());

        // ===== HEADER =====
        pnHeader = new JPanel();
        pnHeader.setPreferredSize(new Dimension(1073, 88));
        pnHeader.setBackground(new Color(0xE3F2F5));
        pnHeader.setLayout(null);
        add(pnHeader, BorderLayout.NORTH);

        txtTimKiem = new JTextField("");
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo mã/ tên  khách hàng");
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
        
        btnTim = new PillButton("Tìm");
        btnTim.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnTim.setBounds(1117, 25, 120, 40);
        btnTim.setLayout(null);
        pnHeader.add(btnTim);
        
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
        
        loadTableData(); 

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
        
        // format table
        formatTable(table);

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
        btnTim.addActionListener(this);
        
        table.addMouseListener(this);
        
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
        dsKhachHang = new ArrayList<>();
        model.setRowCount(0);
        
        try {
			dsKhachHang = kh_dao.getAllKhachHang();
		} catch (Exception e) {
			e.printStackTrace();
		}

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

        // --- Lọc theo tên và SĐT ---
        String text = txtTimKiem.getText().trim();
        if (!text.isEmpty() && !txtTimKiem.getForeground().equals(Color.GRAY)) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 1, 3));
        }

        // --- Lọc theo giới tính ---
        if (chckbxNam.isSelected()) {
            filters.add(RowFilter.regexFilter("(?i)Nam", 2));
        } else if (chckbxNu.isSelected()) {
            filters.add(RowFilter.regexFilter("(?i)Nữ", 2));
        }

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
	    	 CapNhatKH();
	    	 return;
	    }
	    

	}
	
	

	// mở diaLog Thêm khách hàng
	private void MoDiaLogThemKH() {
		frameThemKH = (JFrame) SwingUtilities.getWindowAncestor(this);
        dialogThemKH = new ThemKhachHang_Dialog(frameThemKH);
        dialogThemKH.setVisible(true); 
	}
	
	

	
	// Sk thêm khách hàng
	private void ThemKH() {
		MoDiaLogThemKH();
		KhachHang khMoi = dialogThemKH.getKhachHangMoi();
		if (kh_dao.createKhachHang(khMoi)) {
			addKhachHangToTable(khMoi);
			JOptionPane.showMessageDialog(frameThemKH, "Thêm khách hàng thành công");
		} else {
			JOptionPane.showMessageDialog(frameThemKH, "Thêm khách hàng thất bại!!!");
		}
		
		
	}
	
	// Sk cập nhật khách hàng
	private void CapNhatKH() {

		int selectRow = table.getSelectedRow();
		if( selectRow ==-1) {
			JOptionPane.showMessageDialog(null, "Vui lòng chọn khách hàng để cập nhật");
			return;
		}
		 int modelRow = table.convertRowIndexToModel(selectRow);
		 String maKH = model.getValueAt(selectRow, 0).toString();
		 
		 KhachHang khUpdate = null;
		 for(KhachHang kh : dsKhachHang) {
			 if(kh.getMaKhachHang().equals(maKH)) {
				 khUpdate = kh;
				 break;
			 }
		 }
		 if(khUpdate != null) {
			 JFrame frameCapNhat = (JFrame) SwingUtilities.getWindowAncestor(this);
			    dialogCapNhap = new CapNhatKhachHang_Dialog(frameCapNhat, khUpdate);
			    dialogCapNhap.setVisible(true); 
			    
			  if (dialogCapNhap.isUpdateKHSuccess())  {
				  if(kh_dao.updateKhachHang(khUpdate)) {
					 updateKhachHangInTable(khUpdate, selectRow);
					  JOptionPane.showMessageDialog(frameCapNhat, "Cập nhật thông tin thành công!");
				  }
			  } else {
				  JOptionPane.showMessageDialog(frameThemKH, "Cập nhật khách hàng thất bại!!!");
			  }
			  
		 }
		
	}

	 private void addKhachHangToTable(KhachHang kh) {
	        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	        model.addRow(new Object[]{
	            kh.getMaKhachHang(),
	            kh.getTenKhachHang(),
	            kh.isGioiTinh() ? "Nam" : "Nữ", 
	            kh.getSoDienThoai(),
	            kh.getNgaySinh().format(dtf),     

	        });
	    }
	
	 private void updateKhachHangInTable(KhachHang kh, int row) {
	        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	        model.setValueAt(kh.getTenKhachHang(), row, 1);
	        model.setValueAt(kh.isGioiTinh() ? "Nam" : "Nữ", row, 2);
	        model.setValueAt(kh.getSoDienThoai(), row, 3);
	        model.setValueAt(kh.getNgaySinh().format(dtf), row, 4);

	    }
	 private void formatTable(JTable table) {
	        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 18));
	        table.getTableHeader().setBorder(null);
	        table.setFont(new Font("Segoe UI", Font.PLAIN, 18));
	        table.setRowHeight(28);
	        table.setShowGrid(false);
	        table.getTableHeader().setReorderingAllowed(false);
	        table.setSelectionBackground(new Color(180, 205, 230));

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
	    }
}