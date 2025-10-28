package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

import dao.NhaCungCap_DAO;
import entity.NhaCungCap;

@SuppressWarnings("serial")
public class CapNhatNhaCungCap_Dialog extends JDialog implements ActionListener {

	private final NhaCungCap_DAO nccDAO = new NhaCungCap_DAO();

	private final NhaCungCap nccBanDau; // đối tượng gốc (để hiển thị & so sánh)
	private NhaCungCap nccCapNhat; // kết quả trả về sau khi Lưu

	private JTextField txtMa;
	private JTextField txtTen;
	private JTextField txtSdt;
	private JTextArea txtDiaChi;
	private JButton btnCapNhat, btnThoat;

	public CapNhatNhaCungCap_Dialog(Frame owner, NhaCungCap ncc) {
		super(owner, "Cập nhật nhà cung cấp", true);
		if (ncc == null)
			throw new IllegalArgumentException("NhaCungCap truyền vào không được null.");
		this.nccBanDau = ncc;
		initUI();
		fillData(ncc);
	}

	private void initUI() {
		setSize(560, 540);
		setLocationRelativeTo(getParent());
		getContentPane().setLayout(new BorderLayout(12, 12));
		getContentPane().setBackground(Color.WHITE);

		JPanel pnTitle = new JPanel(new BorderLayout());
		pnTitle.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
		JLabel lbl = new JLabel("Thông tin nhà cung cấp", SwingConstants.LEFT);
		lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
		pnTitle.add(lbl, BorderLayout.CENTER);
		pnTitle.setBackground(Color.WHITE);
		getContentPane().add(pnTitle, BorderLayout.NORTH);

		JPanel pnForm = new JPanel(null);
		pnForm.setBackground(Color.WHITE);

		JLabel lbMa = new JLabel("Mã nhà cung cấp:");
		lbMa.setBounds(12, 10, 180, 24);
		lbMa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		pnForm.add(lbMa);

		txtMa = new JTextField();
		txtMa.setBounds(12, 36, 510, 36);
		txtMa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txtMa.setBorder(new LineBorder(new Color(0x00C0E2), 1, true));
		txtMa.setEditable(false); // không cho sửa mã
		pnForm.add(txtMa);

		JLabel lbTen = new JLabel("Tên nhà cung cấp:");
		lbTen.setBounds(12, 84, 180, 24);
		lbTen.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		pnForm.add(lbTen);

		txtTen = new JTextField();
		txtTen.setBounds(12, 110, 510, 36);
		txtTen.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txtTen.setBorder(new LineBorder(new Color(0x00C0E2), 1, true));
		pnForm.add(txtTen);

		JLabel lbSdt = new JLabel("Số điện thoại:");
		lbSdt.setBounds(12, 158, 180, 24);
		lbSdt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		pnForm.add(lbSdt);

		txtSdt = new JTextField();
		txtSdt.setBounds(12, 184, 510, 36);
		txtSdt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txtSdt.setBorder(new LineBorder(new Color(0x00C0E2), 1, true));
		pnForm.add(txtSdt);

		JLabel lbDiaChi = new JLabel("Địa chỉ:");
		lbDiaChi.setBounds(12, 232, 180, 24);
		lbDiaChi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		pnForm.add(lbDiaChi);

		txtDiaChi = new JTextArea(4, 20);
		txtDiaChi.setLineWrap(true);
		txtDiaChi.setWrapStyleWord(true);
		txtDiaChi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		JScrollPane sp = new JScrollPane(txtDiaChi);
		sp.setBounds(12, 258, 510, 110);
		sp.setBorder(new LineBorder(new Color(0x00C0E2), 1, true));
		pnForm.add(sp);

		getContentPane().add(pnForm, BorderLayout.CENTER);

		JPanel pnButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
		pnButtons.setBackground(Color.WHITE);
		btnThoat = new JButton("Thoát");
		btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnThoat.setBackground(new Color(0x3B82F6));
		btnThoat.setForeground(Color.WHITE);
		btnThoat.setBorder(null);
		btnThoat.setFocusPainted(false);
		btnThoat.setPreferredSize(new Dimension(110, 35));

		btnCapNhat = new JButton("Cập nhật");
		btnCapNhat.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnCapNhat.setBackground(Color.WHITE);
		btnCapNhat.setBorder(new LineBorder(Color.GRAY));
		btnCapNhat.setPreferredSize(new Dimension(110, 35));

		pnButtons.add(btnThoat);
		pnButtons.add(btnCapNhat);
		getContentPane().add(pnButtons, BorderLayout.SOUTH);

		getRootPane().setDefaultButton(btnCapNhat);
		
		btnCapNhat.addActionListener(this);
		btnThoat.addActionListener(this);
		
		KeyAdapter enterToSave = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					capNhatNCC();
			}
		};
		txtTen.addKeyListener(enterToSave);
		txtSdt.addKeyListener(enterToSave);
		txtDiaChi.addKeyListener(enterToSave);
		
	}

	private void fillData(NhaCungCap n) {
		txtMa.setText(n.getMaNhaCungCap());
		txtTen.setText(n.getTenNhaCungCap());
		txtSdt.setText(n.getSoDienThoai());
		txtDiaChi.setText(n.getDiaChi());
	}
	

	private void capNhatNCC() {
		String ma = txtMa.getText().trim();
		String ten = txtTen.getText().trim();
		String sdt = txtSdt.getText().trim();
		String diachi = txtDiaChi.getText().trim();

		// Validate
		if (ten.isEmpty()) {
			warn("Vui lòng nhập tên nhà cung cấp.");
			txtTen.requestFocus();
			return;
		}
		if (!sdt.matches("^0\\d{9}$")) {
			warn("Số điện thoại không hợp lệ. Vui lòng nhập 10–11 số và bắt đầu bằng 0.");
			txtSdt.requestFocus();
			return;
		}
		if (diachi.length() < 5) {
			warn("Địa chỉ quá ngắn. Vui lòng nhập chi tiết hơn.");
			txtDiaChi.requestFocus();
			return;
		}

		// Lưu DB
		NhaCungCap nccNew = new NhaCungCap(ma, ten, sdt, diachi);
		boolean ok = nccDAO.updateNhaCungCap(nccNew);
		if (!ok) {
			warn("Cập nhật thất bại. Vui lòng thử lại.");
			return;
		}

		nccCapNhat = nccNew;
		JOptionPane.showMessageDialog(this, "Đã cập nhật nhà cung cấp.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
		dispose();
	}

	private void warn(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
	}

	/** Trả về NCC đã cập nhật, hoặc null nếu Hủy */
	public NhaCungCap getNhaCungCapCapNhat() {
		return nccCapNhat;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if(o.equals(btnCapNhat)) {
			capNhatNCC();
			return;
		}
		
		if(o.equals(btnThoat)) {
			dispose();
			return;
		}
		
	}
}