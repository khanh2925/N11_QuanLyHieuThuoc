package gui.panel;

import entity.ItemTraHang;
import entity.QuyCachDongGoi;

import javax.swing.*;
import javax.swing.border.*;

import component.input.PlaceholderSupport;
import component.input.TaoJtextNhanh;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;

public class TraHangItemPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final DecimalFormat DF = new DecimalFormat("#,##0");

	private ItemTraHang item;
	private String anhPath;

	// Flag để tránh xử lý trùng lặp khi nhấn Enter
	private boolean isDangXuLy = false;

	// UI
	private JLabel lblSTT;
	private JTextField txtTenThuoc;
	private JTextField txtLo;
	private JTextField txtSoLuongMua;
	private JLabel lblAnh;
	private JComboBox<String> cboDonViTinh;
	private JButton btnGiam;
	private JButton btnTang;
	private JTextField txtSoLuongTra;
	private JTextField txtDonGia;
	private JTextField txtThanhTien;
	private JButton btnXoa;
	private JTextField txtLyDo;
	private JLabel lblDaTra; // Số lượng đã trả trước đó
	private int soLuongDaTraTruocDo = 0; // Số lượng đã trả (quy về gốc)

	public ItemTraHang getItem() {
		return this.item;
	}

	public interface Listener {
		void onUpdate(ItemTraHang item);

		void onDelete(ItemTraHang item, TraHangItemPanel panel);

		void onClone(ItemTraHang itemMoi);
	}

	private Listener listener;
	private JButton btnClone;
	private JLabel lblQuyDoi;

	public TraHangItemPanel(ItemTraHang item, int stt, Listener listener, String anhPath) {
		this(item, stt, listener, anhPath, 0);
	}

	public TraHangItemPanel(ItemTraHang item, int stt, Listener listener, String anhPath, int soLuongDaTra) {
		this.item = item;
		this.listener = listener;
		this.anhPath = anhPath;
		this.soLuongDaTraTruocDo = soLuongDaTra;

		initGUI(stt);
		updateUIValue();
	}

	private void initGUI(int stt) {

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
		setBorder(new CompoundBorder(new LineBorder(new Color(0xDDDDDD), 1), new EmptyBorder(8, 10, 8, 10)));
		setBackground(new Color(0xFAFAFA));

		// ===== STT =====
		lblSTT = new JLabel(String.valueOf(stt));
		lblSTT.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblSTT.setPreferredSize(new Dimension(40, 30));
		add(lblSTT);
		add(Box.createHorizontalStrut(5));

		// ===== ẢNH =====
		lblAnh = new JLabel();
		lblAnh.setPreferredSize(new Dimension(80, 80));
		lblAnh.setBorder(new LineBorder(Color.LIGHT_GRAY));
		lblAnh.setHorizontalAlignment(JLabel.CENTER);
		try {
			ImageIcon icon = new ImageIcon(getClass().getResource("/resources/images/"+anhPath));
			lblAnh.setIcon(new ImageIcon(icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
		} catch (Exception e) {
			lblAnh.setText("Ảnh");
		}
		add(lblAnh);
		add(Box.createHorizontalStrut(5));

		// ===== INFO (Tên + Lô + SL mua) =====
		Box infoBox = Box.createVerticalBox();

		txtTenThuoc = TaoJtextNhanh.taoTextDonHang(item.getTenSanPham(), new Font("Segoe UI", Font.BOLD, 16),
				new Color(0x00796B), 300);
		infoBox.add(txtTenThuoc);

		Box loBox = Box.createHorizontalBox();
		loBox.setMaximumSize(new Dimension(300, 30));

		txtLo = TaoJtextNhanh.taoTextDonHang("Lô: " + item.getMaLo(), new Font("Segoe UI", Font.BOLD, 16),
				new Color(0x00796B), 150);
		loBox.add(txtLo);

		txtSoLuongMua = TaoJtextNhanh.taoTextDonHang("Đã mua: " + item.getSoLuongMua(),
				new Font("Segoe UI", Font.BOLD, 16), new Color(0x00796B), 150);
		loBox.add(txtSoLuongMua);

		infoBox.add(loBox);
		add(infoBox);

		// ===== ĐƠN VỊ (comboBox – có thể đổi DVT) =====
		cboDonViTinh = new JComboBox<>();
		cboDonViTinh.setPreferredSize(new Dimension(70, 30));
		cboDonViTinh.setMaximumSize(new Dimension(70, 30));
		cboDonViTinh.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		cboDonViTinh.setMinimumSize(new Dimension(80, 26));

		// ===== LOAD TẤT CẢ DVT KHÔNG GIỚI HẠN =====
		if (item.getDsQuyCach() != null && !item.getDsQuyCach().isEmpty()) {

			for (QuyCachDongGoi qc : item.getDsQuyCach()) {
				String tenDVT = qc.getDonViTinh().getTenDonViTinh();
				cboDonViTinh.addItem(tenDVT);

				if (item.getQuyCachDangChon() != null && item.getQuyCachDangChon().getDonViTinh().getMaDonViTinh()
						.equals(qc.getDonViTinh().getMaDonViTinh())) {
					cboDonViTinh.setSelectedItem(tenDVT);
				}
			}
		} else {
			// fallback: chỉ 1 DVT như cũ
			cboDonViTinh.addItem(item.getDonViTinh());
		}

		add(cboDonViTinh);

		Box lblBox = Box.createVerticalBox();

		lblQuyDoi = new JLabel();
		lblQuyDoi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblQuyDoi.setPreferredSize(new Dimension(70, 30));
		lblBox.add(lblQuyDoi);

		// Label hiển thị số lượng đã trả trước đó (nằm dưới combobox)
		lblDaTra = new JLabel();
		lblDaTra.setFont(new Font("Segoe UI", Font.ITALIC, 12));
		lblDaTra.setForeground(new Color(0xE65100)); // Màu cam
		lblBox.add(lblDaTra);

		add(lblBox);
		// ===== SỐ LƯỢNG TRẢ (+/-) =====
		Box soLuongBox = Box.createHorizontalBox();
		soLuongBox.setMaximumSize(new Dimension(140, 30));
		soLuongBox.setPreferredSize(new Dimension(140, 30));
		soLuongBox.setBorder(new LineBorder(new Color(0xDDDDDD), 1, true));

		btnGiam = new JButton("-");
		btnGiam.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btnGiam.setPreferredSize(new Dimension(40, 30));
		btnGiam.setMargin(new Insets(0, 0, 0, 0));
		btnGiam.setFocusPainted(false);
		soLuongBox.add(btnGiam);

		txtSoLuongTra = TaoJtextNhanh.hienThi(String.valueOf(item.getSoLuongTra()),
				new Font("Segoe UI", Font.PLAIN, 16), Color.BLACK);
		txtSoLuongTra.setMaximumSize(new Dimension(600, 30));
		txtSoLuongTra.setHorizontalAlignment(SwingConstants.CENTER);
		txtSoLuongTra.setEditable(true);
		soLuongBox.add(txtSoLuongTra);

		btnTang = new JButton("+");
		btnTang.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btnTang.setPreferredSize(new Dimension(40, 30));
		btnTang.setMargin(new Insets(0, 0, 0, 0));
		btnTang.setFocusPainted(false);
		btnTang.setName("btnTang");
		soLuongBox.add(btnTang);

		add(soLuongBox);
		add(Box.createHorizontalStrut(10));

		// ===== ĐƠN GIÁ =====
		txtDonGia = TaoJtextNhanh.taoTextDonHang(formatTien(item.getDonGia()), new Font("Segoe UI", Font.PLAIN, 16),
				Color.BLACK, 100);
		txtDonGia.setHorizontalAlignment(SwingConstants.RIGHT);
		add(txtDonGia);
		add(Box.createHorizontalStrut(5));

		// ===== THÀNH TIỀN =====
		txtThanhTien = TaoJtextNhanh.taoTextDonHang(formatTien(0), new Font("Segoe UI", Font.BOLD, 16),
				new Color(0xD32F2F), 120);
		txtThanhTien.setHorizontalAlignment(SwingConstants.RIGHT);
		add(txtThanhTien);
		add(Box.createHorizontalGlue());

		// ===== LÝ DO TRẢ (đặt ở dưới – không phá layout gốc) =====
		txtLyDo = new JTextField();
		PlaceholderSupport.addPlaceholder(txtLyDo, "Lý do trả");
		txtLyDo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
		txtLyDo.setForeground(Color.GRAY);
		txtLyDo.setPreferredSize(new Dimension(85, 26));
		txtLyDo.setMaximumSize(new Dimension(180, 26));

		add(Box.createHorizontalStrut(10));
		add(txtLyDo);

		// ===== CLONE =====
		btnClone = new JButton();
		btnClone.setPreferredSize(new Dimension(40, 40));
		btnClone.setBorderPainted(false);
		btnClone.setContentAreaFilled(false);
		btnClone.setCursor(new Cursor(Cursor.HAND_CURSOR));
		try {
			ImageIcon icon = new ImageIcon(getClass().getResource("/resources/images/dublicate.png"));
			btnClone.setIcon(new ImageIcon(icon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH)));
		} catch (Exception ignored) {
		}
		add(btnClone);

		// ===== ENABLE / DISABLE CLONE =====
		int soLuongDVTLoad = cboDonViTinh.getItemCount();
		// btnClone.setVisible(soLuongDVTLoad > 1); // logic cũ
		if (soLuongDVTLoad <= 1) {
			btnClone.setEnabled(false);
		}

		// ===== XÓA =====
		btnXoa = new JButton();

		btnXoa.setPreferredSize(new Dimension(40, 40));
		btnXoa.setBorderPainted(false);
		btnXoa.setContentAreaFilled(false);
		btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));
		try {
			ImageIcon icon = new ImageIcon(getClass().getResource("/resources/images/bin.png"));
			btnXoa.setIcon(new ImageIcon(icon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH)));
		} catch (Exception ignored) {
		}
		add(btnXoa);

		addEvents();
	}

	private void addEvents() {

		// ===== NÚT TĂNG =====
		btnTang.addActionListener(e -> {
			int slCu = item.getSoLuongTra();
			item.setSoLuongTra(slCu + 1);

			if (!kiemTraTraVuotGioiHan()) {
				// rollback
				item.setSoLuongTra(slCu);
				return;
			}

			updateUIValue();
			listener.onUpdate(item);
		});

		// ===== NÚT GIẢM =====
		btnGiam.addActionListener(e -> {
			int sl = item.getSoLuongTra();
			if (sl > 1) {
				item.setSoLuongTra(sl - 1);
				updateUIValue();
				listener.onUpdate(item);
			} else {
				JOptionPane.showMessageDialog(this,
						"Số lượng trả không thể nhỏ hơn 1!",
						"Cảnh báo", JOptionPane.WARNING_MESSAGE);
			}
		});

		// ===== NHẬP TAY =====
		txtSoLuongTra.addActionListener(e -> {
			if (!isDangXuLy) {
				isDangXuLy = true;
				nhapSL();
				isDangXuLy = false;
			}
		});

		txtSoLuongTra.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (!isDangXuLy) {
					isDangXuLy = true;
					nhapSL();
					isDangXuLy = false;
				}
			}
		});

		// ===== CLONE =====
		btnClone.addActionListener(e -> {

			// 1) Không clone khi chỉ có 1 DVT
			int soLuongDVTLoad = cboDonViTinh.getItemCount();
			if (soLuongDVTLoad <= 1)
				return;

			// 2) Lấy danh sách các DVT đã được dùng ở các dòng cùng SP & lô
			java.util.Set<String> usedDV = new java.util.HashSet<>();
			Container parent = getParent(); // pnDanhSachDon

			for (Component c : parent.getComponents()) {
				if (c instanceof TraHangItemPanel p) {
					ItemTraHang it = p.item;

					if (!it.getMaLo().equals(item.getMaLo()))
						continue;
					if (!it.getTenSanPham().equals(item.getTenSanPham()))
						continue;

					usedDV.add(it.getQuyCachDangChon().getDonViTinh().getTenDonViTinh());
				}
			}

			// 3) Tìm đơn vị tính chưa dùng
			String dvMoi = null;
			for (int i = 0; i < soLuongDVTLoad; i++) {
				String dv = cboDonViTinh.getItemAt(i);
				if (!usedDV.contains(dv)) {
					dvMoi = dv;
					break;
				}
			}

			// 4) Nếu không còn DVT để clone -> giống bán hàng
			if (dvMoi == null) {
				JOptionPane.showMessageDialog(this,
						"Đã dùng hết tất cả đơn vị tính cho sản phẩm này.\nKhông thể clone thêm dòng.",
						"Không thể clone", JOptionPane.WARNING_MESSAGE);
				return;
			}

			// 5) Clone item
			ItemTraHang itemMoi = item.clone();
			itemMoi.setSoLuongTra(1);
			itemMoi.setLyDo(item.getLyDo());

			// 6) Áp QC theo dvMoi
			for (QuyCachDongGoi qc : item.getDsQuyCach()) {
				if (qc.getDonViTinh().getTenDonViTinh().equals(dvMoi)) {
					itemMoi.applyQuyCach(qc);
					break;
				}
			}

			// CẬP NHẬT lại số lượng mua theo DVT (bắt buộc)
			itemMoi.setSoLuongMuaTheoDVT(itemMoi.getSoLuongMua());

			// 7) Callback thêm dòng clone
			if (listener != null)
				listener.onClone(itemMoi);
		});

		// ===== XÓA =====
		btnXoa.addActionListener(e -> listener.onDelete(item, this));

		// ===== LÝ DO =====
		txtLyDo.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				// Xóa placeholder khi focus vào
				if (txtLyDo.getText().equals("Lý do trả (không bắt buộc)")
						&& txtLyDo.getForeground().equals(Color.GRAY)) {
					txtLyDo.setText("");
					txtLyDo.setForeground(Color.BLACK);
					txtLyDo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				String text = txtLyDo.getText().trim();

				// Hiển thị lại placeholder nếu rỗng
				if (text.isEmpty()) {
					txtLyDo.setText("Lý do trả (không bắt buộc)");
					txtLyDo.setForeground(Color.GRAY);
					txtLyDo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
					item.setLyDo(""); // lưu chuỗi rỗng vào item
				} else {
					item.setLyDo(text);
				}
			}
		});

		// ===== ĐỔI ĐƠN VỊ TÍNH (comboBox) =====
		cboDonViTinh.addActionListener(e -> {

			if (item.getDsQuyCach() == null || item.getDsQuyCach().isEmpty())
				return;

			String dvMoi = (String) cboDonViTinh.getSelectedItem();
			if (dvMoi == null)
				return;

			// ===== KIỂM TRA TRÙNG ĐƠN VỊ =====
			Container parent = getParent(); // pnDanhSachDon

			for (Component c : parent.getComponents()) {
				if (c instanceof TraHangItemPanel p) {

					ItemTraHang it = p.item;

					// cùng sản phẩm + cùng lô
					if (!it.getMaLo().equals(item.getMaLo()))
						continue;
					if (!it.getTenSanPham().equals(item.getTenSanPham()))
						continue;

					// nếu đơn vị đang chọn bị trùng
					if (p.item != item && it.getQuyCachDangChon().getDonViTinh().getTenDonViTinh().equals(dvMoi)) {

						JOptionPane.showMessageDialog(this,
								"Đơn vị '" + dvMoi + "' đã được dùng ở dòng khác.\nVui lòng chọn đơn vị khác.",
								"Trùng đơn vị", JOptionPane.WARNING_MESSAGE);

						// rollback về đơn vị cũ
						cboDonViTinh.setSelectedItem(item.getQuyCachDangChon().getDonViTinh().getTenDonViTinh());
						return;
					}
				}
			}

			// ===== ÁP QUY CÁCH MỚI =====
			for (QuyCachDongGoi qc : item.getDsQuyCach()) {
				if (dvMoi.equals(qc.getDonViTinh().getTenDonViTinh())) {
					item.applyQuyCach(qc);

					// Cập nhật giá bán và thành tiền ngay sau khi đổi DVT
					updateUIValue();
					listener.onUpdate(item);
					break;
				}
			}

			btnClone.setEnabled(cboDonViTinh.getItemCount() > 1);
		});

	}

	private boolean kiemTraTraVuotGioiHan() {
		// Tổng trả quy về gốc tất cả dòng cùng sản phẩm + lô
		Container parent = getParent();
		int tongTraGoc = 0;

		for (Component c : parent.getComponents()) {
			if (c instanceof TraHangItemPanel p) {
				ItemTraHang it = p.getItem();
				if (!it.getMaLo().equals(item.getMaLo()))
					continue;
				if (!it.getTenSanPham().equals(item.getTenSanPham()))
					continue;

				tongTraGoc += it.getSoLuongTra() * it.getQuyCachDangChon().getHeSoQuyDoi();
			}
		}

		// Số lượng gốc được mua
		int muaGoc = item.getSoLuongMuaGoc();

		// Số lượng còn có thể trả = đã mua - đã trả trước đó
		int conLai = muaGoc - soLuongDaTraTruocDo;

		if (tongTraGoc > conLai) {
			// Tìm đơn vị gốc để hiển thị
			String dvGoc = item.getDonViTinh();
			if (item.getDsQuyCach() != null) {
				for (QuyCachDongGoi qc : item.getDsQuyCach()) {
					if (qc.getHeSoQuyDoi() == 1) {
						dvGoc = qc.getDonViTinh().getTenDonViTinh();
						break;
					}
				}
			}

			String msg = String.format(
					"Vượt quá số lượng cho phép!\n\n" +
							"Sản phẩm: %s\n" +
							"Lô: %s\n" +
							"Đã mua: %d %s\n" +
							"Đã trả trước đó: %d %s\n" +
							"Còn có thể trả: %d %s\n" +
							"Số lượng bạn đang trả: %d %s",
					item.getTenSanPham(), item.getMaLo(),
					muaGoc, dvGoc,
					soLuongDaTraTruocDo, dvGoc,
					conLai, dvGoc,
					tongTraGoc, dvGoc);
			JOptionPane.showMessageDialog(this, msg, "Vượt số lượng", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		return true;
	}

	private void nhapSL() {
		int slCu = item.getSoLuongTra();
		try {
			String input = txtSoLuongTra.getText().trim();

			// Kiểm tra số hợp lệ
			if (!input.matches("\\d+")) {
				JOptionPane.showMessageDialog(this,
						"Số lượng phải là số nguyên dương!",
						"Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
				txtSoLuongTra.setText(String.valueOf(slCu));
				return;
			}

			int slMoi = Integer.parseInt(input);

			if (slMoi <= 0) {
				JOptionPane.showMessageDialog(this,
						"Số lượng trả phải lớn hơn 0!",
						"Số lượng không hợp lệ", JOptionPane.WARNING_MESSAGE);
				txtSoLuongTra.setText(String.valueOf(slCu));
				return;
			}

			item.setSoLuongTra(slMoi);

			if (!kiemTraTraVuotGioiHan()) {
				item.setSoLuongTra(slCu);
				txtSoLuongTra.setText(String.valueOf(slCu));
				return;
			}

		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this,
					"Số lượng không hợp lệ! Vui lòng nhập số nguyên.",
					"Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
			item.setSoLuongTra(slCu);
			txtSoLuongTra.setText(String.valueOf(slCu));
			return;
		}

		updateUIValue();
		listener.onUpdate(item);
	}

	private void updateUIValue() {
		// số lượng trả
		txtSoLuongTra.setText(String.valueOf(item.getSoLuongTra()));

		// đơn giá
		txtDonGia.setText(formatTien(item.getDonGia()));

		// thành tiền
		txtThanhTien.setText(formatTien(item.getThanhTien()));

		// Panel chỉ HIỂN THỊ, không tự phân bổ nữa
		txtSoLuongMua.setText("Đã mua: " + item.getSoLuongMuaTheoDVT());

		// Cập nhật quy đổi (giống DonHangItemPanel)
		if (item.getQuyCachDangChon() != null) {
			int heSo = item.getQuyCachDangChon().getHeSoQuyDoi();
			String dvGoc = item.getQuyCachDangChon().getDonViTinh().getTenDonViTinh();
			// Tìm đơn vị gốc (hệ số = 1)
			for (QuyCachDongGoi qc : item.getDsQuyCach()) {
				if (qc.getHeSoQuyDoi() == 1) {
					dvGoc = qc.getDonViTinh().getTenDonViTinh();
					break;
				}
			}
			lblQuyDoi.setText("x" + heSo + " " + dvGoc);
		}

		// Cập nhật label "Đã trả"
		if (soLuongDaTraTruocDo > 0) {
			String dvGoc = item.getDonViTinh();
			if (item.getDsQuyCach() != null) {
				for (QuyCachDongGoi qc : item.getDsQuyCach()) {
					if (qc.getHeSoQuyDoi() == 1) {
						dvGoc = qc.getDonViTinh().getTenDonViTinh();
						break;
					}
				}
			}
			lblDaTra.setText("(Đã trả: " + soLuongDaTraTruocDo + " " + dvGoc + ")");
			lblDaTra.setVisible(true);
		} else {
			lblDaTra.setText("");
			lblDaTra.setVisible(false);
		}

		// notify gui cha để update tiền
		if (listener != null)
			listener.onUpdate(item);

	}

	public void updateSoLuongMuaFromOutside() {
		txtSoLuongMua.setText("Đã mua: " + item.getSoLuongMuaTheoDVT());
	}

	public void setSTT(int stt) {
		lblSTT.setText(String.valueOf(stt));
	}

	private String formatTien(double t) {
		return DF.format(t) + " đ";
	}
}
