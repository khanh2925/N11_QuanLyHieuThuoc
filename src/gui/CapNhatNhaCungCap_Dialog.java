package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder; // 💡 THÊM: Dùng TitledBorder

import dao.NhaCungCap_DAO;
import entity.NhaCungCap;

@SuppressWarnings("serial")
public class CapNhatNhaCungCap_Dialog extends JDialog implements ActionListener {

    private final NhaCungCap_DAO nccDAO = new NhaCungCap_DAO();

    private NhaCungCap nccCapNhat;

    private JTextField txtMa, txtTen, txtSdt, txtEmail;
    private JTextArea txtDiaChi;
    private JCheckBox chkHoatDong;
    private JButton btnLuu, btnThoat;

    // 💡 GIỮ LẠI: Các hằng số màu sắc và font chữ
    private static final Color COLOR_XANH_LA = new Color(0x3B82F6); // Xanh dương
    private static final Color COLOR_XAM = new Color(0x6B7280);    // Xám
    private static final Color COLOR_BORDER = new Color(0x00C0E2);  // Viền
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_TIEU_DE = new Font("Segoe UI", Font.BOLD, 22);

    public CapNhatNhaCungCap_Dialog(Frame owner, NhaCungCap ncc) {
        super(owner, "Cập nhật nhà cung cấp", true);
        if (ncc == null)
            throw new IllegalArgumentException("NhaCungCap không được null.");
        
        this.nccCapNhat = new NhaCungCap(ncc); 
        
        initUI();
        napDuLieu(nccCapNhat);
    }

    private void initUI() {
        setSize(650, 620); // 💡 SỬA: Điều chỉnh kích thước
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // 💡 SỬA: Giữ nguyên setLayout(null) theo yêu cầu
        getContentPane().setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        JLabel lblTieuDe = new JLabel("Cập nhật thông tin nhà cung cấp", SwingConstants.CENTER);
        lblTieuDe.setFont(FONT_TIEU_DE);
        // 💡 SỬA: Căn giữa, chiếm toàn bộ chiều rộng, có padding trên
        lblTieuDe.setBounds(0, 20, this.getWidth() - 15, 30); 
        add(lblTieuDe);
        
        // 💡 SỬA: Tạo một panel con để chứa form, dùng TitledBorder
        JPanel pnForm = new JPanel();
        pnForm.setLayout(null); // Panel con cũng dùng layout null
        pnForm.setBackground(Color.WHITE);
        pnForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                " Thông tin chi tiết ",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                FONT_LABEL
        ));
        
        // 💡 SỬA: Đặt vị trí cho panel form
        pnForm.setBounds(20, 70, 590, 440);
        add(pnForm);

        // --- Bắt đầu sắp xếp các thành phần bên trong pnForm ---
        // 💡 SỬA: Định nghĩa lề và khoảng cách
        int labelX = 25;
        int textX = 180;
        int labelWidth = 150;
        int textWidth = 380;
        int fieldHeight = 35;
        int vGap = 15; // Khoảng cách dọc
        int y = 40; // Vị trí y ban đầu bên trong panel

        // --- Hàng 1: Mã NCC ---
        pnForm.add(createLabel("Mã nhà cung cấp:", labelX, y, labelWidth, fieldHeight));
        txtMa = createTextField(false);
        txtMa.setBounds(textX, y, textWidth, fieldHeight);
        pnForm.add(txtMa);

        // --- Hàng 2: Tên NCC ---
        y += fieldHeight + vGap;
        pnForm.add(createLabel("Tên nhà cung cấp:", labelX, y, labelWidth, fieldHeight));
        txtTen = createTextField(true);
        txtTen.setBounds(textX, y, textWidth, fieldHeight);
        pnForm.add(txtTen);

        // --- Hàng 3: SDT ---
        y += fieldHeight + vGap;
        pnForm.add(createLabel("Số điện thoại:", labelX, y, labelWidth, fieldHeight));
        txtSdt = createTextField(true);
        txtSdt.setBounds(textX, y, textWidth, fieldHeight);
        pnForm.add(txtSdt);
        
        // --- Hàng 4: Email ---
        y += fieldHeight + vGap;
        pnForm.add(createLabel("Email:", labelX, y, labelWidth, fieldHeight));
        txtEmail = createTextField(true);
        txtEmail.setBounds(textX, y, textWidth, fieldHeight);
        pnForm.add(txtEmail);

        // --- Hàng 5: Địa chỉ (JTextArea) ---
        y += fieldHeight + vGap;
        pnForm.add(createLabel("Địa chỉ:", labelX, y, labelWidth, fieldHeight));
        
        txtDiaChi = new JTextArea();
        txtDiaChi.setFont(FONT_TEXT);
        txtDiaChi.setLineWrap(true);
        txtDiaChi.setWrapStyleWord(true);
        JScrollPane sp = new JScrollPane(txtDiaChi);
        sp.setBorder(new LineBorder(COLOR_BORDER, 1, true));
        sp.setBounds(textX, y, textWidth, 90); // 💡 SỬA: Tăng chiều cao
        pnForm.add(sp);

        // --- Hàng 6: Checkbox ---
        y += 90 + vGap; // 💡 SỬA: Căn chỉnh y sau JTextArea
        chkHoatDong = new JCheckBox("Đang hợp tác");
        chkHoatDong.setFont(FONT_LABEL);
        chkHoatDong.setBackground(Color.WHITE);
        chkHoatDong.setBounds(textX, y, textWidth, fieldHeight);
        pnForm.add(chkHoatDong);
        
        // --- Buttons (Đặt bên ngoài pnForm, trên contentPane) ---
        // 💡 SỬA: Căn chỉnh vị trí Y dựa trên pnForm
        int buttonY = pnForm.getY() + pnForm.getHeight() + 20; 
        int btnWidth = 140;
        int btnHeight = 40;
        
        btnThoat = createButton("Thoát", COLOR_XAM);
        // 💡 SỬA: Căn lề phải
        btnThoat.setBounds(pnForm.getX() + pnForm.getWidth() - btnWidth, buttonY, btnWidth, btnHeight);
        add(btnThoat);

        btnLuu = createButton("Lưu thay đổi", COLOR_XANH_LA);
        btnLuu.setBounds(btnThoat.getX() - 10 - btnWidth, buttonY, btnWidth, btnHeight);
        add(btnLuu);

        btnLuu.addActionListener(this);
        btnThoat.addActionListener(this);
    }
    
    // 💡 SỬA: Hàm tiện ích tạo JLabel với setBounds
    private JLabel createLabel(String text, int x, int y, int w, int h) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_LABEL);
        lbl.setBounds(x, y, w, h);
        return lbl;
    }

    // 💡 SỬA: Hàm tiện ích tạo JTextField (không setBounds)
    private JTextField createTextField(boolean editable) {
        JTextField tf = new JTextField();
        tf.setFont(FONT_TEXT);
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(5, 8, 5, 8) // Thêm padding
        ));
        tf.setEditable(editable);
        if (!editable) {
            tf.setBackground(new Color(0xF3F4F6)); // Màu xám nhạt
            tf.setForeground(Color.DARK_GRAY);
        }
        return tf;
    }
    
    // 💡 SỬA: Hàm tiện ích tạo JButton (không setBounds)
    private JButton createButton(String text, Color background) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(background);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    // (Các hàm logic không thay đổi)

    private void napDuLieu(NhaCungCap n) {
        txtMa.setText(n.getMaNhaCungCap());
        txtTen.setText(n.getTenNhaCungCap());
        txtSdt.setText(n.getSoDienThoai());
        txtEmail.setText(n.getEmail());
        txtDiaChi.setText(n.getDiaChi());
        chkHoatDong.setSelected(n.isHoatDong());
    }

    private void capNhat() {
        try {
            String ten = txtTen.getText().trim();
            String sdt = txtSdt.getText().trim();
            String email = txtEmail.getText().trim();
            String diachi = txtDiaChi.getText().trim();
            boolean hoatDong = chkHoatDong.isSelected();

            if (ten.isEmpty()) throw new IllegalArgumentException("Tên nhà cung cấp không được để trống.");
            if (!sdt.matches("^0\\d{9}$"))
                throw new IllegalArgumentException("Số điện thoại không hợp lệ (10 chữ số, bắt đầu bằng 0).");
            if (diachi.isEmpty()) throw new IllegalArgumentException("Địa chỉ không được trống.");
            if (!email.isEmpty() && !email.matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}$"))
                throw new IllegalArgumentException("Email không hợp lệ.");

            nccCapNhat.setTenNhaCungCap(ten);
            nccCapNhat.setSoDienThoai(sdt);
            nccCapNhat.setDiaChi(diachi);
            nccCapNhat.setEmail(email);
            nccCapNhat.setHoatDong(hoatDong);

            if (!nccDAO.capNhatNhaCungCap(nccCapNhat)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại. Vui lòng thử lại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            JOptionPane.showMessageDialog(this, "Đã cập nhật thông tin nhà cung cấp thành công!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnLuu) capNhat();
        else if (src == btnThoat) {
            nccCapNhat = null; 
            dispose();
        }
    }

    public NhaCungCap getNhaCungCapCapNhat() {
        return nccCapNhat;
    }
}