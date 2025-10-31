package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.regex.Pattern;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;

import connectDB.connectDB;

import java.util.ArrayList;
import java.util.List;

import customcomponent.ImagePanel;
import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import dao.NhaCungCap_DAO;
import entity.KhachHang;
import entity.NhaCungCap;

public class NhaCungCap_GUI extends JPanel implements ActionListener, MouseListener {

    private JPanel pnCenter;
    private JPanel pnHeader;
    private JTextField txtTimKiem;
    private JTable table;

    // === KHAI BÁO BIẾN THÀNH VIÊN ===
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private JButton btnThem;
    private JButton btnSua;
    private NhaCungCap_DAO nhaCC_dao;
    private List<NhaCungCap> dsNhaCungCap;
	private JFrame frameThemNCC;
	private ThemNhaCungCap_Dialog dialogThemNCC;
	private JFrame frameCapNhapNCC;
	private ThemNhaCungCap_Dialog dialogCapNhapNCC;

    public NhaCungCap_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());

        // ===== HEADER =====
        pnHeader = new JPanel();
        pnHeader.setPreferredSize(new Dimension(1073, 88));
        pnHeader.setBackground(new Color(0xE3F2F5));
        pnHeader.setLayout(null);
        add(pnHeader, BorderLayout.NORTH);

        txtTimKiem = new JTextField("");
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo tên/ sđt nhà cung cấp");
        txtTimKiem.setForeground(Color.GRAY);
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtTimKiem.setBounds(10, 17, 420, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        pnHeader.add(txtTimKiem);
        
        btnThem=new PillButton("Thêm");
        pnHeader.add(btnThem);
        btnThem.setBounds(465, 26, 120, 40);
        btnThem.setLayout(null);
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        btnSua =new PillButton("Cập nhật");
        btnSua.setLayout(null);
        btnSua.setBounds(614, 26, 120, 40);
        pnHeader.add(btnSua);
        btnSua.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // ===== CENTER =====
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new LineBorder(new Color(200, 200, 200)));
        add(pnCenter, BorderLayout.CENTER);

        List<NhaCungCap> dsNhaCungCap = new ArrayList<>();
        String[] columnNames = {"Mã nhà cung cấp", "Tên nhà cung cấp", "Số điện thoại", "Địa chỉ"};
        model = new DefaultTableModel(columnNames, 0);

        for (NhaCungCap ncc : dsNhaCungCap) {
            model.addRow(new Object[]{
                ncc.getMaNhaCungCap(),
                ncc.getTenNhaCungCap(),
                ncc.getSoDienThoai(),
                ncc.getDiaChi()
            });
        }

        table = new JTable(model);
        // ... (Cấu hình JTable giữ nguyên)
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
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

        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(350);

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
        
        // ===== Sắp xếp và Lọc =====
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        // ===== PHẦN THÊM SỰ KIỆN TÌM KIẾM (ĐẶT Ở ĐÂY) =====
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applySearchFilter();
            }
        });
        
        try {
			connectDB.getInstance().connect();
        } catch (Exception e) {
			e.printStackTrace();
		}
        
        nhaCC_dao = new NhaCungCap_DAO();        
        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        table.addMouseListener(this);
        
        // đưa dữ liệu lên table
        loadTableData();

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
    
    private void loadTableData() {
        dsNhaCungCap = new ArrayList<>();
        model.setRowCount(0);
        
        try {
			dsNhaCungCap = nhaCC_dao.getAllNhaCungCap();
		} catch (Exception e) {
			e.printStackTrace();
		}

        for (NhaCungCap ncc : dsNhaCungCap) {
            model.addRow(new Object[]{
            	ncc.getMaNhaCungCap(),
                ncc.getTenNhaCungCap(),
                ncc.getSoDienThoai(),
                ncc.getDiaChi(),
                
            });
        }
    }
    
    private void applyFilters() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        // --- Lọc theo tên và SĐT ---
        String text = txtTimKiem.getText().trim();
        if (!text.isEmpty() && !txtTimKiem.getForeground().equals(Color.GRAY)) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 1, 3));
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    
    // ===== PHƯƠNG THỨC LỌC DỮ LIỆU TRÊN BẢNG (ĐẶT Ở ĐÂY) =====

    private void applySearchFilter() {
        String text = txtTimKiem.getText();
        
        // Kiểm tra xem người dùng đã xóa hết chữ chưa
        // Hoặc kiểm tra xem ô tìm kiếm có đang hiển thị placeholder không (nếu có)
        if (text.trim().isEmpty() || txtTimKiem.getForeground().equals(Color.GRAY)) {
            sorter.setRowFilter(null);
        } else {
            // Tạo một danh sách các bộ lọc
            List<RowFilter<Object, Object>> filters = new ArrayList<>();
            
            // Thêm "^" để chỉ tìm kiếm những dòng BẮT ĐẦU BẰNG chuỗi `text`
            // Lọc trên cột Tên NCC (index 1) - (?i) để không phân biệt hoa thường
            filters.add(RowFilter.regexFilter("(?i)^" + text, 1));
            // Lọc trên cột SĐT (index 2)
            filters.add(RowFilter.regexFilter("(?i)^" + text, 2));
            
            // Áp dụng bộ lọc "OR", hàng nào khớp với 1 trong các điều kiện sẽ được hiển thị
            sorter.setRowFilter(RowFilter.orFilter(filters));
        }
    }

    // ===== MAIN =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý nhà cung cấp");

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new NhaCungCap_GUI());
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

	private void moDialogCapNhat() {
		int viewRow = table.getSelectedRow();
		if (viewRow < 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 nhà cung cấp để cập nhật.", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		int modelRow = table.convertRowIndexToModel(viewRow);

		String ma = (String) model.getValueAt(modelRow, 0);
		String ten = (String) model.getValueAt(modelRow, 1);
		String sdt = (String) model.getValueAt(modelRow, 2);
		String diaC = (String) model.getValueAt(modelRow, 3);

		// object hiện tại
		NhaCungCap nccDangChon = new NhaCungCap(ma, ten, sdt, diaC);

		Window owner = SwingUtilities.getWindowAncestor(this);
		CapNhatNhaCungCap_Dialog dlg = new CapNhatNhaCungCap_Dialog(owner instanceof Frame ? (Frame) owner : null,
				nccDangChon);
		dlg.setVisible(true);

		NhaCungCap capNhat = dlg.getNhaCungCapCapNhat();
		if (capNhat != null) {
			// Reload toàn bộ để đảm bảo nhất quán
			loadTableData();

			// tìm lại supplier vừa cập nhật để giữ selection
			for (int i = 0; i < model.getRowCount(); i++) {
				if (capNhat.getMaNhaCungCap().equals(model.getValueAt(i, 0))) {
					int vRow = table.convertRowIndexToView(i);
					table.getSelectionModel().setSelectionInterval(vRow, vRow);
					table.scrollRectToVisible(table.getCellRect(vRow, 0, true));
					break;
				}
			}
		}
	}
	
	private void MoDiaLogThemNCC() {
		frameThemNCC = (JFrame) SwingUtilities.getWindowAncestor(this);
        dialogThemNCC = new ThemNhaCungCap_Dialog(frameThemNCC);
        dialogThemNCC.setVisible(true); 
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	    Object src = e.getSource();
	    if (src == btnThem) {
	    	MoDiaLogThemNCC();
	    	loadTableData();
	    } else if (src == btnSua) {
	        moDialogCapNhat();
	    }
	}

	
	
}