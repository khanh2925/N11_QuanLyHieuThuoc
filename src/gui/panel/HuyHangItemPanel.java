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
	private JButton btnXoa;

	private LoSanPham_DAO loDAO = new LoSanPham_DAO();
	private QuyCachDongGoi_DAO quyCachDAO = new QuyCachDongGoi_DAO();
	private List<QuyCachDongGoi> danhSachQuyCach;
	private JComboBox<String> cboDonVi;
	private int soLuongTonGoc; // Lưu số lượng tồn gốc từ DB

	public interface Listener {
		void onUpdate(ItemHuyHang it);

		void onDelete(ItemHuyHang it, HuyHangItemPanel panel);
	}

	private Listener listener;

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
		setOpaque(true);


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
		try {
			ImageIcon icon = new ImageIcon(getClass().getResource("/images/" + anhPath));
			lblAnh.setIcon(new ImageIcon(icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
		} catch (Exception e) {
			lblAnh.setText("Ảnh");
		}
		add(lblAnh);
		add(Box.createHorizontalStrut(5));

		// ==== INFO BOX (Tên – Lô – DVT – Tồn) ====
		Box infoBox = Box.createVerticalBox();
		infoBox.setBorder(new LineBorder(Color.BLACK));

		txtTenSP = TaoJtextNhanh.taoTextDonHang(item.getTenSanPham(), new Font("Segoe UI", Font.BOLD, 16),
				new Color(0x00796B), 300);
		infoBox.add(txtTenSP);

		Box loBox = Box.createHorizontalBox();
		loBox.setMaximumSize(new Dimension(300, 30));

		txtLo = TaoJtextNhanh.taoTextDonHang("Lô: " + item.getMaLo(), new Font("Segoe UI", Font.BOLD, 14),
				new Color(0x00796B), 150);
		loBox.add(txtLo);
		loBox.add(Box.createHorizontalStrut(8));

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
		add(cboDonVi);
		
		add(Box.createHorizontalStrut(10));

		// Load đơn vị tính của sản phẩm
		loadDonViTinh();

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

		// ô nhập SL huỷ
		txtSLHuy = TaoJtextNhanh.hienThi(
		        String.valueOf(item.getSoLuongHuy()),
		        new Font("Segoe UI", Font.PLAIN, 16),
		        Color.BLACK
		);
		txtSLHuy.setMaximumSize(new Dimension(60, 30));
		txtSLHuy.setHorizontalAlignment(SwingConstants.CENTER);
		txtSLHuy.setEditable(true);
		soLuongBox.add(txtSLHuy);

		// nút Tăng  >>> BỊ THIẾU Ở BẢN CŨ
		btnTang = new JButton("+");
		btnTang.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btnTang.setPreferredSize(new Dimension(40, 30));
		btnTang.setMargin(new Insets(0, 0, 0, 0));
		btnTang.setFocusPainted(false);
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
		add(Box.createHorizontalStrut(10));

		// ===== LÝ DO =====
		txtLyDo = new JTextField("Lý do huỷ (không bắt buộc)");
		txtLyDo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
		txtLyDo.setForeground(Color.GRAY);
		txtLyDo.setMaximumSize(new Dimension(220, 26));

		// Hiển thị lý do có sẵn từ ItemHuyHang (nếu có)
		String lyDoCoSan = item.getLyDo();
		if (lyDoCoSan != null && !lyDoCoSan.isEmpty()) {
			txtLyDo.setText(lyDoCoSan);
			txtLyDo.setForeground(Color.BLACK);
			txtLyDo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		}

		add(txtLyDo);
		add(Box.createHorizontalStrut(10));

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

	private String formatTien(double t) {
		return DF.format(t) + " đ";
	}

	private void addEvents() {

		// Tăng
		btnTang.addActionListener(e -> {
			int sl = item.getSoLuongHuy();
			int slTonHienThi = getSoLuongTonTheoQuyCachHienTai();
			if (sl < slTonHienThi) {
				item.setSoLuongHuy(sl + 1);
				updateUIValue();
				if (listener != null) {
					listener.onUpdate(item);
				}
			}
		});

		// Giảm
		btnGiam.addActionListener(e -> {
			int sl = item.getSoLuongHuy();
			if (sl > 1) {
				item.setSoLuongHuy(sl - 1);
				updateUIValue();
				if (listener != null) {
					listener.onUpdate(item);
				}
			}
		});

		// Nhập tay số lượng
		txtSLHuy.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				int soMoi;
				try {
					soMoi = Integer.parseInt(txtSLHuy.getText().trim());
				} catch (Exception ex) {
					soMoi = 1;
				}
				if (soMoi < 1)
					soMoi = 1;
				int slTonHienThi = getSoLuongTonTheoQuyCachHienTai();
				if (soMoi > slTonHienThi)
					soMoi = slTonHienThi;

				item.setSoLuongHuy(soMoi);
				updateUIValue();
				if (listener != null) {
					listener.onUpdate(item);
				}
			}
		});

		// Thay đổi đơn vị tính
		cboDonVi.addActionListener(e -> {
			int idx = cboDonVi.getSelectedIndex();
			if (idx >= 0 && idx < danhSachQuyCach.size()) {
				QuyCachDongGoi qcChon = danhSachQuyCach.get(idx);
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
				return;
			}

			// Lưu số lượng tồn gốc từ item
			soLuongTonGoc = item.getSoLuongTon();

			// Load vào combo và tìm quy cách gốc
			for (QuyCachDongGoi qc : danhSachQuyCach) {
				cboDonVi.addItem(qc.getDonViTinh().getTenDonViTinh());

				if (qc.isDonViGoc()) {
					item.setQuyCachGoc(qc);
					item.setQuyCachHienTai(qc);
				}
			}

			// Chọn đơn vị gốc mặc định
			for (int i = 0; i < danhSachQuyCach.size(); i++) {
				if (danhSachQuyCach.get(i).isDonViGoc()) {
					cboDonVi.setSelectedIndex(i);
					break;
				}
			}
		} else {
			cboDonVi.addItem("Lỗi");
			cboDonVi.setEnabled(false);
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
