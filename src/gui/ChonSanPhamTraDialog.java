package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import entity.ChiTietHoaDon;

public class ChonSanPhamTraDialog extends JDialog {

	private JTable tbl;
	private DefaultTableModel model;
	private List<ChiTietHoaDon> dsCT;
	private List<ChiTietHoaDon> dsChon = new ArrayList<>();

	public ChonSanPhamTraDialog(List<ChiTietHoaDon> dsCT) {
		this.dsCT = dsCT;

		setTitle("Chọn sản phẩm cần trả");
		setSize(700, 500);
		setModal(true);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout(8, 8));

		// ================= MODEL TABLE =================
		model = new DefaultTableModel(new String[] { "Chọn", "Tên sản phẩm" }, 0) {
			@Override
			public Class<?> getColumnClass(int column) {
				return column == 0 ? Boolean.class : String.class;
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 0;
			}
		};

		for (ChiTietHoaDon ct : dsCT) {
			model.addRow(new Object[] { false, ct.getSanPham().getTenSanPham() });
		}

		// ================= TABLE =================
		tbl = new JTable(model);

		// Tăng chiều cao dòng
		tbl.setRowHeight(40);

		// Font chữ to hơn
		tbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 18));

		// Cột checkbox nhỏ và fix cứng
		TableColumn colCheck = tbl.getColumnModel().getColumn(0);
		colCheck.setMinWidth(60);
		colCheck.setMaxWidth(60);
		colCheck.setPreferredWidth(60);

		// Renderer cho tên sản phẩm to hơn & căn trái
		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
		leftRenderer.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		tbl.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);

		JScrollPane scroll = new JScrollPane(tbl);
		add(scroll, BorderLayout.CENTER);

		// ================= PANEL DƯỚI =================
		JPanel bottom = new JPanel(new BorderLayout());

		// --------- Nút CHỌN TẤT CẢ (bên trái) ----------
		JButton btnSelectAll = new JButton("Chọn tất cả");
		btnSelectAll.setFont(new Font("Segoe UI", Font.BOLD, 16));

		btnSelectAll.addActionListener(e -> {
			boolean select = btnSelectAll.getText().equals("Chọn tất cả");
			for (int i = 0; i < model.getRowCount(); i++)
				model.setValueAt(select, i, 0);

			btnSelectAll.setText(select ? "Bỏ chọn tất cả" : "Chọn tất cả");
		});

		bottom.add(btnSelectAll, BorderLayout.WEST);

		// --------- Panel chứa 2 nút OK & HỦY (bên phải) ----------
		JPanel rightButtons = new JPanel();

		JButton btnOK = new JButton("OK");
		btnOK.setFont(new Font("Segoe UI", Font.BOLD, 16));

		JButton btnCancel = new JButton("Hủy");
		btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btnCancel.setBackground(new Color(0xE53935)); // Đỏ
		btnCancel.setForeground(Color.WHITE); // Chữ trắng
		btnCancel.setOpaque(true);
		btnCancel.setBorderPainted(false);

		rightButtons.add(btnCancel);

		rightButtons.add(btnOK);
		rightButtons.add(btnCancel);

		bottom.add(rightButtons, BorderLayout.EAST);

		// ================= SỰ KIỆN NÚT =================
		btnOK.addActionListener(e -> {
			dsChon.clear();
			for (int i = 0; i < model.getRowCount(); i++) {
				boolean chon = (boolean) model.getValueAt(i, 0);
				if (chon)
					dsChon.add(dsCT.get(i));
			}
			if (dsChon.isEmpty() || dsChon == null) {
				JOptionPane.showMessageDialog(this, "Bạn chưa chọn sản phẩm nào để trả hàng!", "Chưa có sản phẩm",
						JOptionPane.WARNING_MESSAGE);
			} else {
				dispose();
			}
		});

		btnCancel.addActionListener(e -> {
			dsChon.clear(); 
			dispose();
		});

		add(bottom, BorderLayout.SOUTH);
	}

	public List<ChiTietHoaDon> getDsSanPhamDuocChon() {
		return dsChon;
	}
}
