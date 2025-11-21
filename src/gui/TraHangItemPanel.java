package gui;

import entity.ItemTraHang;

import javax.swing.*;
import javax.swing.border.*;

import customcomponent.TaoJtextNhanh;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;

public class TraHangItemPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final DecimalFormat DF = new DecimalFormat("#,##0");

	private ItemTraHang item;
	private String anhPath;

	// UI
	private JLabel lblSTT;
	private JTextField txtTenThuoc;
	private JTextField txtLo;
	private JTextField txtSoLuongMua;
	private JLabel lblAnh;
	private JLabel lblDonViTinh;
	private JButton btnGiam;
	private JButton btnTang;
	private JTextField txtSoLuongTra;
	private JTextField txtDonGia;
	private JTextField txtThanhTien;
	private JButton btnXoa;
	private JTextField txtLyDo;

	public interface Listener {
		void onUpdate(ItemTraHang item);

		void onDelete(ItemTraHang item, TraHangItemPanel panel);
	}

	private Listener listener;

	public TraHangItemPanel(ItemTraHang item, int stt, Listener listener, String anhPath) {
		this.item = item;
		this.listener = listener;
		this.anhPath = anhPath;

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
			ImageIcon icon = new ImageIcon(getClass().getResource("/images/" + anhPath));
			lblAnh.setIcon(new ImageIcon(icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
		} catch (Exception e) {
			lblAnh.setText("Ảnh");
		}
		add(lblAnh);
		add(Box.createHorizontalStrut(5));

		// ===== INFO (Tên + Lô + SL mua) =====
		Box infoBox = Box.createVerticalBox();
        infoBox.setBorder(new LineBorder(Color.BLACK));

		txtTenThuoc = TaoJtextNhanh.taoTextDonHang(item.getTenSanPham(), new Font("Segoe UI", Font.BOLD, 16),
				new Color(0x00796B), 300);
		infoBox.add(txtTenThuoc);

		Box loBox = Box.createHorizontalBox();
		loBox.setMaximumSize(new Dimension(300, 30));

		txtLo = TaoJtextNhanh.taoTextDonHang("Lô: " + item.getMaLo(), new Font("Segoe UI", Font.BOLD, 16),
				new Color(0x00796B), 150);
		loBox.add(txtLo);
		loBox.add(Box.createHorizontalStrut(8));

		txtSoLuongMua = TaoJtextNhanh.taoTextDonHang(
                "Đã mua: " + item.getSoLuongMua(), new Font("Segoe UI", Font.BOLD, 16), new Color(0x00796B), 150);
        loBox.add(txtSoLuongMua);
		
		infoBox.add(loBox);
		add(infoBox);
		add(Box.createHorizontalStrut(15));

		// ===== ĐƠN VỊ (readonly – giống bán hàng) =====
		lblDonViTinh = new JLabel(item.getDonViTinh());
		lblDonViTinh.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblDonViTinh.setPreferredSize(new Dimension(70, 30));
		add(lblDonViTinh);
		add(Box.createHorizontalStrut(15));

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
        
        txtSoLuongTra = TaoJtextNhanh.hienThi(
                String.valueOf(item.getSoLuongTra()), new Font("Segoe UI", Font.PLAIN, 16), Color.BLACK);
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
        txtDonGia = TaoJtextNhanh.taoTextDonHang(
                formatTien(item.getDonGia()), new Font("Segoe UI", Font.PLAIN, 16), Color.BLACK, 100);
        txtDonGia.setHorizontalAlignment(SwingConstants.RIGHT);
        add(txtDonGia);
        add(Box.createHorizontalStrut(5));
		
		// ===== THÀNH TIỀN =====
        txtThanhTien = TaoJtextNhanh.taoTextDonHang(
                formatTien(0), new Font("Segoe UI", Font.BOLD, 16), new Color(0xD32F2F), 120);
        txtThanhTien.setHorizontalAlignment(SwingConstants.RIGHT);
        add(txtThanhTien);
        add(Box.createHorizontalGlue());

		// ===== XÓA =====
        btnXoa = new JButton();
        btnXoa.setPreferredSize(new Dimension(40, 40));
        btnXoa.setBorderPainted(false);
        btnXoa.setContentAreaFilled(false);
        btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/bin.png"));
            btnXoa.setIcon(new ImageIcon(icon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH)));
        } catch (Exception ignored) {}
        add(btnXoa);

		// ===== LÝ DO TRẢ (đặt ở dưới – không phá layout gốc) =====
		txtLyDo = new JTextField("Lý do trả (không bắt buộc)");
		txtLyDo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
		txtLyDo.setForeground(Color.GRAY);
		txtLyDo.setMaximumSize(new Dimension(200, 26));

		add(Box.createHorizontalStrut(15));
		add(txtLyDo);

		addEvents();
	}

//	private JTextField taoTxt(String txt, Color c) {
//		JTextField t = new JTextField(txt);
//		t.setEditable(false);
//		t.setBorder(null);
//		t.setBackground(new Color(0xFAFAFA));
//		t.setFont(new Font("Segoe UI", Font.BOLD, 15));
//		t.setForeground(c);
//		return t;
//	}

	private void addEvents() {

		// ===== NÚT TĂNG =====
		btnTang.addActionListener(e -> {
			int sl = item.getSoLuongTra();
			if (sl < item.getSoLuongMua()) {
				item.setSoLuongTra(sl + 1);
				updateUIValue();
				listener.onUpdate(item);
			}
		});

		// ===== NÚT GIẢM =====
		btnGiam.addActionListener(e -> {
			int sl = item.getSoLuongTra();
			if (sl > 1) {
				item.setSoLuongTra(sl - 1);
				updateUIValue();
				listener.onUpdate(item);
			}
		});

		// ===== NHẬP TAY =====
		txtSoLuongTra.addActionListener(e -> nhapSL());
		txtSoLuongTra.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				nhapSL();
			}
		});

		// ===== XÓA =====
		btnXoa.addActionListener(e -> listener.onDelete(item, this));

		// ===== LÝ DO =====
		txtLyDo.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				item.setLyDo(txtLyDo.getText().trim());
			}
		});
	}

	private void nhapSL() {
		try {
			int slMoi = Integer.parseInt(txtSoLuongTra.getText().trim());
			item.setSoLuongTra(slMoi);
		} catch (Exception ex) {
			// reset nếu lỗi
		}
		updateUIValue();
		listener.onUpdate(item);
	}

	private void updateUIValue() {
		txtSoLuongTra.setText(String.valueOf(item.getSoLuongTra()));
		txtThanhTien.setText(DF.format(item.getThanhTien()));
	}

	public void setSTT(int stt) {
		lblSTT.setText(String.valueOf(stt));
	}

    private String formatTien(double t) {
        return DF.format(t) + " đ";
    }
}
