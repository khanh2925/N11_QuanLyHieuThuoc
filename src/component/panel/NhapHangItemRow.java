package component.panel;

import javax.swing.*;
import javax.swing.border.*;

import gui.NhapHang_GUI;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.time.LocalDate;

public class NhapHangItemRow extends JPanel {
	// model tối giản cho ví dụ (bạn có thể thay bằng SanPham/LoSanPham thật)
	public static class RowModel {
		public final String tenSP, hamLuong, donVi;
		public final String maLo;
		public final LocalDate hsd; // hạn dùng
		public int soLuong;
		public double donGia;

		public RowModel(String tenSP, String hamLuong, String donVi, String maLo, LocalDate hsd, int soLuong,
				double donGia) {
			this.tenSP = tenSP;
			this.hamLuong = hamLuong;
			this.donVi = donVi;
			this.maLo = maLo;
			this.hsd = hsd;
			this.soLuong = soLuong;
			this.donGia = donGia;
		}
	}

	// UI parts
	private final JLabel lbTen = new JLabel();
	private final JLabel lbHamLuong = new JLabel();
	private final JLabel lbDonVi = new JLabel();
	private final JLabel lbLoHang = new JLabel();
	private final JButton btnMinus = new JButton("−");
	private final JButton btnPlus = new JButton("+");
	private final JTextField txtQty = new JTextField(3);
	private final JLabel lbDonGia = new JLabel();
	private final JLabel lbThanhTien = new JLabel();
	private final JButton btnTrash = new JButton(); // 🗑
	private final DecimalFormat money = new DecimalFormat("#,##0.#'đ'");

	// callback
	public interface QtyListener {
		void onChange(int newQty, double thanhTien);
	}

	public interface RemoveListener {
		void onRemove(NhapHangItemRow row);
	}

	private QtyListener qtyListener;
	private RemoveListener removeListener;

	private RowModel data;

	public NhapHangItemRow(RowModel data) {
		this.data = data;
		setOpaque(true);
		setBackground(new Color(255, 240, 240)); // pastel hồng nhạt
		setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, new Color(235, 215, 215)),
				new EmptyBorder(10, 12, 10, 12)));
		buildUI();
		bind(data);
		wire();
	}

	private void buildUI() {
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 100));
		GridBagConstraints g = new GridBagConstraints();
		g.insets = new Insets(2, 6, 2, 6);
		g.gridy = 0;

		// cột 1: Tên SP / thông tin
		JPanel pInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
		pInfo.setOpaque(false);
		styleLabel(lbTen, Font.BOLD);
		styleLabel(lbHamLuong, Font.PLAIN);
		styleLabel(lbDonVi, Font.PLAIN);
		pInfo.add(lbTen);
		pInfo.add(lbHamLuong);
		pInfo.add(lbDonVi);

		g.gridx = 0;
		g.weightx = 1;
		g.fill = GridBagConstraints.HORIZONTAL;
		add(pInfo, g);

		// cột 2: số lượng với nút − 1 +
		JPanel pQty = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
		pQty.setOpaque(false);
		styleButton(btnMinus);
		styleButton(btnPlus);
		txtQty.setHorizontalAlignment(JTextField.CENTER);
		txtQty.setPreferredSize(new Dimension(40, 28));
		pQty.add(btnMinus);
		pQty.add(txtQty);
		pQty.add(btnPlus);

		g.gridx = 1;
		g.weightx = 0;
		g.fill = GridBagConstraints.NONE;
		add(pQty, g);

		// cột 3: đơn giá
		styleMoney(lbDonGia, Font.PLAIN);
		g.gridx = 2;
		add(lbDonGia, g);

		// cột 4: thành tiền (đậm)
		styleMoney(lbThanhTien, Font.BOLD);
		g.gridx = 3;
		add(lbThanhTien, g);

		// cột 5: trash
		styleIconButton(btnTrash);
		g.gridx = 4;
		add(btnTrash, g);

		// hàng 2: nhãn Lô
		g.gridy = 1;
		g.gridx = 0;
		g.gridwidth = 5;
		g.anchor = GridBagConstraints.WEST;
		lbLoHang.setOpaque(true);
		lbLoHang.setBackground(new Color(220, 225, 228)); // badge xám-mint
		lbLoHang.setBorder(new EmptyBorder(3, 8, 3, 8));
		lbLoHang.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		add(lbLoHang, g);
	}

	private void styleLabel(JLabel lb, int style) {
		lb.setFont(new Font("Segoe UI", style, 14));
		lb.setForeground(new Color(40, 40, 40));
	}

	private void styleMoney(JLabel lb, int style) {
		lb.setFont(new Font("Segoe UI", style, 14));
		lb.setForeground(new Color(20, 20, 20));
		lb.setHorizontalAlignment(SwingConstants.RIGHT);
		lb.setPreferredSize(new Dimension(110, 26));
	}

	private void styleButton(JButton b) {
		b.setFont(new Font("Segoe UI", Font.BOLD, 16));
		b.setFocusPainted(false);
		b.setBorder(new LineBorder(new Color(200, 200, 200)));
		b.setBackground(Color.WHITE);
		b.setPreferredSize(new Dimension(36, 28));
	}

	private void styleIconButton(JButton b) {
		b.setFocusPainted(false);
		b.setBorder(new EmptyBorder(2, 6, 2, 6));
		b.setBackground(new Color(255, 240, 240));
		b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	private void bind(RowModel m) {
		lbTen.setText(m.tenSP);
		lbHamLuong.setText(m.hamLuong);
		lbDonVi.setText(m.donVi);
		txtQty.setText(String.valueOf(m.soLuong));
		lbDonGia.setText(money.format(m.donGia));
		lbThanhTien.setText(money.format(m.soLuong * m.donGia));
		String loTxt = String.format("Lô hàng   %s - %s • SL: %d", m.maLo, (m.hsd != null ? m.hsd : ""), m.soLuong);
		lbLoHang.setText(loTxt);
		lbLoHang.setForeground(new Color(60, 60, 60));
	}

	private void wire() {
		btnMinus.addActionListener(e -> changeQty(-1));
		btnPlus.addActionListener(e -> changeQty(+1));
		txtQty.addActionListener(e -> applyQtyFromText());
		txtQty.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				applyQtyFromText();
			}
		});
		btnTrash.addActionListener(e -> {
			if (removeListener != null)
				removeListener.onRemove(this);
		});
		// phím tắt: +/-
		registerKeyboardAction(e -> changeQty(+1), KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0),
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		registerKeyboardAction(e -> changeQty(-1), KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0),
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	private void applyQtyFromText() {
		try {
			int q = Integer.parseInt(txtQty.getText().trim());
			if (q < 1)
				q = 1;
			data.soLuong = q;
			txtQty.setText(String.valueOf(q));
			updateTotal();
		} catch (NumberFormatException ex) {
			txtQty.setText(String.valueOf(data.soLuong));
		}
	}

	private void changeQty(int delta) {
		int q = Math.max(1, data.soLuong + delta);
		data.soLuong = q;
		txtQty.setText(String.valueOf(q));
		updateTotal();
	}

	private void updateTotal() {
		double tt = data.soLuong * data.donGia;
		lbThanhTien.setText(money.format(tt));
		if (qtyListener != null)
			qtyListener.onChange(data.soLuong, tt);
	}

	// ==== API công khai ====
	public void setQtyListener(QtyListener l) {
		this.qtyListener = l;
	}

	public void setRemoveListener(RemoveListener l) {
		this.removeListener = l;
	}

	public int getSoLuong() {
		return data.soLuong;
	}

	public double getThanhTien() {
		return data.soLuong * data.donGia;
	}

	public RowModel getData() {
		return data;
	}
	
	public JButton getBtnTrash() {
		return btnTrash;
	}
	
	public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Demo Item Row");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel list = new JPanel();
            list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

            // Dùng class hàng bạn đã tạo (ví dụ ItemRowPanel hoặc NhapHangItemRow)
            NhapHangItemRow.RowModel m1 = new NhapHangItemRow.RowModel(
                    "Paracetamol", "500 mg", "Hộp",
                    "LO0001", LocalDate.of(2025, 12, 10),
                    1, 100_000
            );
            list.add(new NhapHangItemRow(m1));

            JScrollPane sp = new JScrollPane(list);
            sp.setBorder(BorderFactory.createEmptyBorder());
            f.setContentPane(sp);

            f.setSize(1000, 130);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
