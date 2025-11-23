package gui;

import entity.ItemHuyHang;

import javax.swing.*;
import javax.swing.border.*;

import customcomponent.TaoJtextNhanh;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;

public class HuyHangItemPanel extends JPanel {

    private static final DecimalFormat DF = new DecimalFormat("#,##0");

    private ItemHuyHang item;

    private JLabel lblSTT;
    private JLabel lblAnh;

    private JTextField txtTenSP;
    private JTextField txtLo;
    private JTextField txtTon;

    private JButton btnGiam;
    private JButton btnTang;
    private JTextField txtSLHuy;

    private JTextField txtDonGia;
    private JTextField txtThanhTien;

    private JTextField txtLyDo;
    private JButton btnXoa;

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
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));
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
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/" + anhPath));
            lblAnh.setIcon(new ImageIcon(icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
        } catch (Exception e) {
            lblAnh.setText("Ảnh");
        }
        add(lblAnh);
        add(Box.createHorizontalStrut(8));

        // ==== INFO BOX (Tên – Lô – Tồn) ====
        Box infoBox = Box.createVerticalBox();
        infoBox.setBorder(new LineBorder(Color.BLACK));
        
        txtTenSP = TaoJtextNhanh.taoTextDonHang(
                item.getTenSanPham(), new Font("Segoe UI", Font.BOLD, 16), new Color(0x00796B), 300);
        infoBox.add(txtTenSP);

        Box loBox = Box.createHorizontalBox();
        loBox.setMaximumSize(new Dimension(300, 30));

        txtLo = TaoJtextNhanh.taoTextDonHang(
                "Lô: " + item.getMaLo(), new Font("Segoe UI", Font.BOLD, 16), new Color(0x00796B), 150);
        loBox.add(txtLo);
        loBox.add(Box.createHorizontalStrut(10));

        txtTon = taoText("Tồn: " + item.getSoLuongTon(), new Font("Segoe UI", Font.PLAIN, 14), 100);
        loBox.add(txtTon);

        txtTon = TaoJtextNhanh.taoTextDonHang(
                "Tồn: " + item.getSoLuongTon(), new Font("Segoe UI", Font.BOLD, 16), new Color(0x00796B), 150);
        loBox.add(txtTon);
        
        infoBox.add(loBox);
        add(infoBox);
        add(Box.createHorizontalStrut(5));

        // ===== SỐ LƯỢNG HUỶ =====
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

        txtSLHuy = TaoJtextNhanh.hienThi(
                String.valueOf(item.getSoLuongHuy()), new Font("Segoe UI", Font.PLAIN, 16), Color.BLACK);
        txtSLHuy.setMaximumSize(new Dimension(600, 30));
        txtSLHuy.setHorizontalAlignment(SwingConstants.CENTER);
        txtSLHuy.setEditable(true);
        soLuongBox.add(txtSLHuy);

        btnTang = new JButton("+");
        btnTang.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnTang.setPreferredSize(new Dimension(40, 30));
        btnTang.setMargin(new Insets(0, 0, 0, 0));
        btnTang.setFocusPainted(false);
        btnTang.setName("btnTang");
        soLuongBox.add(btnTang);

        add(soLuongBox);
        add(Box.createHorizontalStrut(5));

        // ===== ĐƠN GIÁ NHẬP =====
        txtDonGia = TaoJtextNhanh.taoTextDonHang(
                formatTien(item.getDonGiaNhap()), new Font("Segoe UI", Font.PLAIN, 16), Color.BLACK, 100);
        txtDonGia.setHorizontalAlignment(SwingConstants.RIGHT);
        add(txtDonGia);
        add(Box.createHorizontalStrut(5));

        // ===== THÀNH TIỀN =====
        txtThanhTien = TaoJtextNhanh.taoTextDonHang(
                formatTien(item.getThanhTien()), new Font("Segoe UI", Font.BOLD, 16), new Color(0xD32F2F), 120);
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

        // ===== LÝ DO =====
        txtLyDo = new JTextField("Lý do huỷ (không bắt buộc)");
        txtLyDo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        txtLyDo.setForeground(Color.GRAY);
        txtLyDo.setMaximumSize(new Dimension(220, 26));
        add(Box.createHorizontalStrut(10));
        add(txtLyDo);

        addEvents();
    }

    private JTextField taoText(String s, Font f, int w) {
        JTextField txt = new JTextField(s);
        txt.setFont(f);
        txt.setForeground(Color.BLACK);
        txt.setHorizontalAlignment(SwingConstants.LEFT);
        txt.setEditable(false);
        txt.setBorder(null);
        txt.setMaximumSize(new Dimension(w, 26));
        return txt;
    }

    private String formatTien(double t) {
        return DF.format(t) + " đ";
    }

    private void addEvents() {

        // Tăng
        btnTang.addActionListener(e -> {
            int sl = item.getSoLuongHuy();
            if (sl < item.getSoLuongTon()) {
                item.setSoLuongHuy(sl + 1);
                updateUIValue();
                listener.onUpdate(item);
            }
        });

        // Giảm
        btnGiam.addActionListener(e -> {
            int sl = item.getSoLuongHuy();
            if (sl > 1) {
                item.setSoLuongHuy(sl - 1);
                updateUIValue();
                listener.onUpdate(item);
            }
        });

        // Nhập tay
        txtSLHuy.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                int soMoi;
                try {
                    soMoi = Integer.parseInt(txtSLHuy.getText().trim());
                } catch (Exception ex) {
                    soMoi = 1;
                }
                if (soMoi < 1) soMoi = 1;
                if (soMoi > item.getSoLuongTon()) soMoi = item.getSoLuongTon();

                item.setSoLuongHuy(soMoi);
                updateUIValue();
                listener.onUpdate(item);
            }
        });

        // Xóa
        btnXoa.addActionListener(e -> {
            if (listener != null)
                listener.onDelete(item, this);
        });

        // Lý do
        txtLyDo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                item.setLyDo(txtLyDo.getText().trim());
            }
        });
    }

    public void updateUIValue() {
        txtSLHuy.setText(String.valueOf(item.getSoLuongHuy()));
        txtThanhTien.setText(formatTien(item.getThanhTien()));
    }

    public void setSTT(int stt) {
        lblSTT.setText(String.valueOf(stt));
    }
}
