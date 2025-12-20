package gui.panel;

import entity.ItemHuyHang;
import entity.QuyCachDongGoi;
import entity.LoSanPham;
import dao.LoSanPham_DAO;
import dao.QuyCachDongGoi_DAO;

import javax.swing.*;
import javax.swing.border.*;

import component.input.TaoJtextNhanh;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.util.List;

public class HuyHangItemPanel extends JPanel {

	private static final DecimalFormat DF = new DecimalFormat("#,##0");

	private ItemHuyHang item;

	private JLabel lblSTT;
	private JLabel lblAnh;

	private JTextField txtTenSP;
	private JTextField txtLo;
	private JLabel lblSoLuongTon; // Đổi từ txtTon sang label

	private JButton btnGiam;
	private JButton btnTang;
	private JTextField txtSLHuy;

	private JTextField txtDonGia;
	private JTextField txtThanhTien;

	private JTextField txtLyDo;
	private JButton btnClone;
	private JButton btnXoa;

	private LoSanPham_DAO loDAO = new LoSanPham_DAO();
	private QuyCachDongGoi_DAO quyCachDAO = new QuyCachDongGoi_DAO();
	private List<QuyCachDongGoi> danhSachQuyCach;
	private JComboBox<String> cboDonVi;
	private int soLuongTonGoc; // Lưu số lượng tồn gốc từ DB

	public interface Listener {
		void onUpdate(ItemHuyHang it);

		void onDelete(ItemHuyHang it, HuyHangItemPanel panel);

		void onClone(ItemHuyHang itemMoi);
	}

	private Listener listener;

	private JLabel lblQuyDoi;

	public HuyHangItemPanel(ItemHuyHang item, int stt, Listener listener, String anh) {
		this.item = item;
		this.listener = listener;

		initGUI(stt, anh);
		updateUIValue();
	}

	private void initGUI(int stt, String anhPath) {

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

		// ===== ẢNH SP =====
		lblAnh = new JLabel();
		lblAnh.setPreferredSize(new Dimension(80, 80));
		lblAnh.setBorder(new LineBorder(Color.LIGHT_GRAY));
		lblAnh.setHorizontalAlignment(JLabel.CENTER);
		try {
			// Logic load ảnh thông minh hơn
			// 1. Thử load trực tiếp
			java.net.URL imgUrl = getClass().getResource("/resources/images/" + anhPath);

			// 2. Nếu không thấy & path chưa có 'products/' -> thử thêm vào
			if (imgUrl == null && anhPath != null && !anhPath.startsWith("products/")) {
				imgUrl = getClass().getResource("/resources/images/products/" + anhPath);
			}

			// 3. Nếu vẫn null -> thử tìm trong folder gốc images (fallback legacy)
			if (imgUrl == null && anhPath != null) {
				imgUrl = getClass().getResource("/images/" + anhPath);
			}

			if (imgUrl != null) {
				ImageIcon icon = new ImageIcon(imgUrl);
				lblAnh.setIcon(new ImageIcon(icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
			} else {
				lblAnh.setText("Ảnh");
			}
		} catch (Exception e) {
			lblAnh.setText("Ảnh");
		}
		add(lblAnh);
		add(Box.createHorizontalStrut(5));

		// ==== INFO BOX (Tên – Lô – DVT – Tồn) ====
		Box infoBox = Box.createVerticalBox();

		txtTenSP = TaoJtextNhanh.taoTextDonHang(item.getTenSanPham(), new Font("Segoe UI", Font.BOLD, 16),
				new Color(0x00796B), 300);
		infoBox.add(txtTenSP);

		Box loBox = Box.createHorizontalBox();
		loBox.setMaximumSize(new Dimension(300, 30));

		txtLo = TaoJtextNhanh.taoTextDonHang("Lô: " + item.getMaLo(), new Font("Segoe UI", Font.BOLD, 14),
				new Color(0x00796B), 150);
		loBox.add(txtLo);

		// Label hiển thị số lượng tồn (sẽ thay đổi theo đơn vị)
		lblSoLuongTon = new JLabel("Tồn: " + item.getSoLuongTon());
		lblSoLuongTon.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblSoLuongTon.setForeground(new Color(0x00796B));
		loBox.add(lblSoLuongTon);

		infoBox.add(loBox);
		add(infoBox);

		add(Box.createHorizontalStrut(10));

		// ComboBox Đơn vị tính (load từ sản phẩm)
		cboDonVi = new JComboBox<>();
		cboDonVi.setPreferredSize(new Dimension(70, 30));
		cboDonVi.setMaximumSize(new Dimension(70, 30));
		cboDonVi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		cboDonVi.setMinimumSize(new Dimension(80, 26));
		// NOTE: loadDonViTinh() is called later after btnClone is created
		add(cboDonVi);
		lblQuyDoi = new JLabel();
		lblQuyDoi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblQuyDoi.setPreferredSize(new Dimension(70, 30));
		add(Box.createHorizontalStrut(3));
		add(lblQuyDoi);

		add(Box.createHorizontalStrut(5));
		// ===== SỐ LƯỢNG HUỶ =====
		Box soLuongBox = Box.createHorizontalBox();
		soLuongBox.setMaximumSize(new Dimension(140, 30));
		soLuongBox.setPreferredSize(new Dimension(140, 30));
		soLuongBox.setBorder(new LineBorder(new Color(0xDDDDDD), 1, true));

		// nút Giảm
		btnGiam = new JButton("-");
		btnGiam.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btnGiam.setPreferredSize(new Dimension(40, 30));
		btnGiam.setMargin(new Insets(0, 0, 0, 0));
		btnGiam.setFocusPainted(false);
		soLuongBox.add(btnGiam);

		txtSLHuy = TaoJtextNhanh.hienThi(
				String.valueOf(item.getSoLuongHuy()),
				new Font("Segoe UI", Font.PLAIN, 16),
				Color.BLACK);
		txtSLHuy.setMaximumSize(new Dimension(600, 30));
		txtSLHuy.setHorizontalAlignment(SwingConstants.CENTER);
		txtSLHuy.setEditable(true);
		soLuongBox.add(txtSLHuy);

		// nút Tăng >>> BỊ THIẾU Ở BẢN CŨ
		btnTang = new JButton("+");
		btnTang.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btnTang.setPreferredSize(new Dimension(40, 30));
		btnTang.setMargin(new Insets(0, 0, 0, 0));
		btnTang.setFocusPainted(false);
		btnTang.setName("btnTang");
		soLuongBox.add(btnTang);

		// thêm box số lượng vào panel
		add(soLuongBox);
		add(Box.createHorizontalStrut(5));

		// ===== ĐƠN GIÁ NHẬP =====
		txtDonGia = TaoJtextNhanh.taoTextDonHang(formatTien(item.getDonGiaNhap()), new Font("Segoe UI", Font.PLAIN, 16),
				Color.BLACK, 100);
		txtDonGia.setHorizontalAlignment(SwingConstants.RIGHT);
		add(txtDonGia);
		add(Box.createHorizontalStrut(5));

		// ===== THÀNH TIỀN =====
		txtThanhTien = TaoJtextNhanh.taoTextDonHang(formatTien(item.getThanhTien()),
				new Font("Segoe UI", Font.BOLD, 16), new Color(0xD32F2F), 120);
		txtThanhTien.setHorizontalAlignment(SwingConstants.RIGHT);
		add(txtThanhTien);
		add(Box.createHorizontalStrut(10)); // Fixed spacing instead of glue

		// ===== LÝ DO =====
		txtLyDo = new JTextField("Lý do huỷ (không bắt buộc)");
		txtLyDo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
		txtLyDo.setForeground(Color.GRAY);
		// Set consistent sizes to prevent misalignment when text is pre-populated
		txtLyDo.setMinimumSize(new Dimension(140, 26));
		txtLyDo.setPreferredSize(new Dimension(140, 26));
		txtLyDo.setMaximumSize(new Dimension(140, 26));

		// Hiển thị lý do có sẵn từ ItemHuyHang (nếu có)
		String lyDoCoSan = item.getLyDo();
		if (lyDoCoSan != null && !lyDoCoSan.isEmpty()) {
			txtLyDo.setText(lyDoCoSan);
			txtLyDo.setForeground(Color.BLACK);
			txtLyDo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		}

		add(txtLyDo);
		add(Box.createHorizontalStrut(5)); // Add spacing before buttons

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
		// Visibility is set in loadDonViTinh() based on number of DVT

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

		// Load đơn vị tính của sản phẩm (phải gọi sau khi btnClone được tạo)
		loadDonViTinh();

		addEvents();
	}

	private String formatTien(double t) {
		return DF.format(t) + " đ";
	}

	private void addEvents() {

		// Tăng
		btnTang.addActionListener(e -> {
			int sl = item.getSoLuongHuy();
			int slTonHienThi = getSoLuongTonTheoQuyCachHienTai();

			if (slTonHienThi <= 0) {
				JOptionPane.showMessageDialog(this,
						"Lô này đã hết hàng!",
						"Cảnh báo", JOptionPane.WARNING_MESSAGE);
				return;
			}

			if (sl >= slTonHienThi) {
				JOptionPane.showMessageDialog(this,
						"Số lượng huỷ đã đạt tối đa tồn kho (" + slTonHienThi + ")!",
						"Cảnh báo", JOptionPane.WARNING_MESSAGE);
				return;
			}

			item.setSoLuongHuy(sl + 1);
			updateUIValue();
			if (listener != null) {
				listener.onUpdate(item);
			}
		});

		// Giảm
		btnGiam.addActionListener(e -> {
			int sl = item.getSoLuongHuy();
			if (sl <= 1) {
				JOptionPane.showMessageDialog(this,
						"Số lượng huỷ không thể nhỏ hơn 1!",
						"Cảnh báo", JOptionPane.WARNING_MESSAGE);
				return;
			}

			item.setSoLuongHuy(sl - 1);
			updateUIValue();
			if (listener != null) {
				listener.onUpdate(item);
			}
		});

		// Nhập tay số lượng - Enter key
		txtSLHuy.addActionListener(e -> xuLySoLuongNhapTay());

		// Nhập tay số lượng - Lost focus
		txtSLHuy.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				xuLySoLuongNhapTay();
			}
		});

		// Thay đổi đơn vị tính
		cboDonVi.addActionListener(e -> {
			int idx = cboDonVi.getSelectedIndex();
			if (idx >= 0 && idx < danhSachQuyCach.size()) {
				QuyCachDongGoi qcChon = danhSachQuyCach.get(idx);

				// CHECK TRÙNG ĐƠN VỊ VỚI PANEL KHÁC
				Container parent = getParent();
				for (Component c : parent.getComponents()) {
					if (c instanceof HuyHangItemPanel p) {
						ItemHuyHang it = p.item;
						if (p == this)
							continue; // Skip self

						if (!it.getMaLo().equals(item.getMaLo()))
							continue;
						if (!it.getTenSanPham().equals(item.getTenSanPham()))
							continue;

						if (it.getQuyCachHienTai() != null
								&& it.getQuyCachHienTai().getDonViTinh().getMaDonViTinh()
										.equals(qcChon.getDonViTinh().getMaDonViTinh())) {

							JOptionPane.showMessageDialog(this,
									"Đơn vị '" + qcChon.getDonViTinh().getTenDonViTinh()
											+ "' đã được dùng ở dòng khác.",
									"Trùng đơn vị", JOptionPane.WARNING_MESSAGE);

							// Rollback
							cboDonVi.setSelectedItem(item.getQuyCachHienTai().getDonViTinh().getTenDonViTinh());
							return;
						}
					}
				}

				item.setQuyCachHienTai(qcChon);
				capNhatSoLuongTonTheoQuyCach();
				if (listener != null) {
					listener.onUpdate(item);
				}
			}
		});

		// Xóa
		btnXoa.addActionListener(e -> {
			if (listener != null) {
				listener.onDelete(item, this);
			}
		});

		// Lý do - placeholder
		txtLyDo.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (txtLyDo.getText().equals("Lý do huỷ (không bắt buộc)")) {
					txtLyDo.setText("");
					txtLyDo.setForeground(Color.BLACK);
					txtLyDo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				String text = txtLyDo.getText().trim();
				if (text.isEmpty()) {
					txtLyDo.setText("Lý do huỷ (không bắt buộc)");
					txtLyDo.setForeground(Color.GRAY);
					txtLyDo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
					item.setLyDo("");
				} else {
					item.setLyDo(text);
				}
			}
		});

		// ===== CLONE =====
		btnClone.addActionListener(e -> {
			// 1) Không clone khi chỉ có 1 DVT
			int soLuongDVTLoad = cboDonVi.getItemCount();
			if (soLuongDVTLoad <= 1)
				return;

			// 2) Lấy danh sách các DVT đã được dùng
			java.util.Set<String> usedDV = new java.util.HashSet<>();
			Container parent = getParent();

			for (Component c : parent.getComponents()) {
				if (c instanceof HuyHangItemPanel p) {
					ItemHuyHang it = p.item;

					if (!it.getMaLo().equals(item.getMaLo()))
						continue;

					if (it.getQuyCachHienTai() != null) {
						usedDV.add(it.getQuyCachHienTai().getDonViTinh().getTenDonViTinh());
					}
				}
			}

			// 3) Tìm MỘT đơn vị tính chưa dùng (đơn vị đầu tiên chưa được sử dụng)
			QuyCachDongGoi qcMoi = null;
			for (int i = 0; i < soLuongDVTLoad; i++) {
				String dv = cboDonVi.getItemAt(i);
				if (!usedDV.contains(dv)) {
					if (danhSachQuyCach != null && i < danhSachQuyCach.size()) {
						qcMoi = danhSachQuyCach.get(i);
						break; // Chỉ lấy 1 đơn vị
					}
				}
			}

			// 4) Nếu không còn đơn vị nào chưa dùng
			if (qcMoi == null) {
				JOptionPane.showMessageDialog(this,
						"Đã dùng hết tất cả đơn vị tính cho sản phẩm này.\nKhông thể clone thêm dòng.",
						"Không thể clone", JOptionPane.WARNING_MESSAGE);
				return;
			}

			// 5) Clone MỘT đơn vị chưa dùng
			ItemHuyHang itemMoi = new ItemHuyHang(
					item.getMaLo(),
					item.getTenSanPham(),
					soLuongTonGoc,
					item.getDonGiaNhap(),
					item.getHinhAnh());
			itemMoi.setSoLuongHuy(1);
			itemMoi.setLyDo(item.getLyDo());
			itemMoi.setQuyCachGoc(item.getQuyCachGoc());
			itemMoi.setQuyCachHienTai(qcMoi);

			// Callback thêm dòng clone
			if (listener != null) {
				listener.onClone(itemMoi);
			}
		});
	}

	/**
	 * Xử lý khi nhập số lượng bằng tay (Enter hoặc mất focus)
	 */
	private void xuLySoLuongNhapTay() {
		int soMoi;
		try {
			soMoi = Integer.parseInt(txtSLHuy.getText().trim());
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this,
					"Vui lòng nhập số hợp lệ!",
					"Lỗi", JOptionPane.ERROR_MESSAGE);
			txtSLHuy.setText(String.valueOf(item.getSoLuongHuy()));
			return;
		}

		int slTonHienThi = getSoLuongTonTheoQuyCachHienTai();

		if (soMoi < 1) {
			JOptionPane.showMessageDialog(this,
					"Số lượng huỷ phải lớn hơn 0!",
					"Cảnh báo", JOptionPane.WARNING_MESSAGE);
			soMoi = 1;
		} else if (soMoi > slTonHienThi) {
			JOptionPane.showMessageDialog(this,
					"Số lượng huỷ không được vượt quá tồn kho (" + slTonHienThi + ")!",
					"Cảnh báo", JOptionPane.WARNING_MESSAGE);
			soMoi = slTonHienThi;
		}

		item.setSoLuongHuy(soMoi);
		updateUIValue();
		if (listener != null) {
			listener.onUpdate(item);
		}
	}

	private void loadDonViTinh() {
		// Lấy lô sản phẩm từ mã lô
		LoSanPham lo = loDAO.timLoTheoMa(item.getMaLo());

		if (lo != null && lo.getSanPham() != null) {
			String maSP = lo.getSanPham().getMaSanPham();

			// Lấy danh sách quy cách của sản phẩm
			danhSachQuyCach = quyCachDAO.layDanhSachQuyCachTheoSanPham(maSP);

			if (danhSachQuyCach == null || danhSachQuyCach.isEmpty()) {
				cboDonVi.addItem("Không có đơn vị");
				cboDonVi.setEnabled(false);
				btnClone.setVisible(false);
				return;
			}

			// Lưu số lượng tồn gốc từ item
			soLuongTonGoc = item.getSoLuongTon();

			// Tìm quy cách gốc và đơn vị mặc định
			QuyCachDongGoi quyCachGoc = null;
			int indexGoc = 0;

			// Load vào combo và tìm quy cách gốc
			for (int i = 0; i < danhSachQuyCach.size(); i++) {
				QuyCachDongGoi qc = danhSachQuyCach.get(i);
				cboDonVi.addItem(qc.getDonViTinh().getTenDonViTinh());

				if (qc.isDonViGoc()) {
					quyCachGoc = qc;
					indexGoc = i;
				}
			}

			// Nếu không tìm thấy đơn vị gốc, dùng đơn vị đầu tiên
			if (quyCachGoc == null && !danhSachQuyCach.isEmpty()) {
				quyCachGoc = danhSachQuyCach.get(0);
				indexGoc = 0;
			}

			// Set quy cách cho item
			if (quyCachGoc != null) {
				item.setQuyCachGoc(quyCachGoc);
				// Chỉ set quyCachHienTai nếu chưa được set từ trước (khi clone)
				if (item.getQuyCachHienTai() == null) {
					item.setQuyCachHienTai(quyCachGoc);
				}
			}

			// Chọn đơn vị trong combo
			// Nếu item đã có quyCachHienTai (từ clone), chọn đúng đơn vị đó
			if (item.getQuyCachHienTai() != null) {
				String tenDVHienTai = item.getQuyCachHienTai().getDonViTinh().getTenDonViTinh();
				for (int i = 0; i < cboDonVi.getItemCount(); i++) {
					if (cboDonVi.getItemAt(i).equals(tenDVHienTai)) {
						cboDonVi.setSelectedIndex(i);
						break;
					}
				}
			} else {
				cboDonVi.setSelectedIndex(indexGoc);
			}

			// Cập nhật visibility của nút clone sau khi load xong
			btnClone.setVisible(danhSachQuyCach.size() > 1);

		} else {
			cboDonVi.addItem("Lỗi");
			cboDonVi.setEnabled(false);
			btnClone.setVisible(false);
		}
	}

	private void capNhatSoLuongTonTheoQuyCach() {
		if (item.getQuyCachGoc() == null || item.getQuyCachHienTai() == null) {
			return;
		}

		// Quy đổi sang đơn vị đang chọn
		int slTonHienThi = getSoLuongTonTheoQuyCachHienTai();

		lblSoLuongTon.setText("Tồn: " + slTonHienThi);

		// Reset số lượng huỷ về 1
		item.setSoLuongHuy(1);
		updateUIValue();
	}

	private int getSoLuongTonTheoQuyCachHienTai() {
		if (item.getQuyCachHienTai() == null) {
			return soLuongTonGoc;
		}
		int heSo = item.getQuyCachHienTai().getHeSoQuyDoi();
		return soLuongTonGoc / heSo;
	}

	public void updateUIValue() {
		txtSLHuy.setText(String.valueOf(item.getSoLuongHuy()));
		txtThanhTien.setText(formatTien(item.getThanhTien()));

		// Cập nhật quy đổi (giống TraHangItemPanel)
		if (item.getQuyCachHienTai() != null && danhSachQuyCach != null) {
			int heSo = item.getQuyCachHienTai().getHeSoQuyDoi();
			String dvGoc = item.getQuyCachHienTai().getDonViTinh().getTenDonViTinh();
			// Tìm đơn vị gốc (hệ số = 1)
			for (QuyCachDongGoi qc : danhSachQuyCach) {
				if (qc.getHeSoQuyDoi() == 1) {
					dvGoc = qc.getDonViTinh().getTenDonViTinh();
					break;
				}
			}
			lblQuyDoi.setText("x" + heSo + " " + dvGoc);
		}
	}

	public void setSTT(int stt) {
		lblSTT.setText(String.valueOf(stt));
	}

	public QuyCachDongGoi getQuyCachDaChon() {
		if (danhSachQuyCach == null || cboDonVi.getSelectedIndex() < 0) {
			return null;
		}
		return danhSachQuyCach.get(cboDonVi.getSelectedIndex());
	}
}
