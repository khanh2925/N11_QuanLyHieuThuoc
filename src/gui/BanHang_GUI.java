package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.*;

import customcomponent.TaoJtextNhanh;
import customcomponent.TaoLabelNhanh;
import entity.SanPham;
import customcomponent.TaoButtonNhanh;

/**
 * Giao diện Bán Hàng - KHUNG LAYOUT TRỐNG HOÀN TOÀN
 * Dùng class tiện ích: TaoJtextNhanh, TaoLabelNhanh, TaoButtonNhanh
 */
public class BanHang_GUI extends JPanel implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtTimThuoc;
    private JPanel pnDanhSachDon;
    private JTextField txtTimKH;
    private JTextField txtTienKhach;
    private JTextField txtTongTienHang;
    private JTextField txtTongHDValue;
    private JTextField txtTienThua;
    private JTextField txtTenKhachHang;
    private JButton btnThemDon;
    private JButton btnBanHang;
    private JTextField txtGiamSPValue;
    private JTextField txtGiamHDValue;

    public BanHang_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createRightPanel(), BorderLayout.EAST);
    }

    private JPanel createHeaderPanel() {
        JPanel pnHeader = new JPanel(null);
        pnHeader.setPreferredSize(new Dimension(1073, 88));
        pnHeader.setBackground(new Color(0xE3F2F5));
        txtTimThuoc = TaoJtextNhanh.timKiem();
        txtTimThuoc.setBounds(25, 17, 480, 60);
        pnHeader.add(txtTimThuoc);
        btnThemDon = new JButton("Thêm đơn");
        btnThemDon.setBounds(530, 30, 130, 45);
        pnHeader.add(btnThemDon);
        return pnHeader;
    }

    private JPanel createCenterPanel() {
        JPanel pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new CompoundBorder(
                new LineBorder(new Color(0x00C853), 3, true),
                new EmptyBorder(10, 10, 10, 10)));

        pnDanhSachDon = new JPanel();
        pnDanhSachDon.setLayout(new BoxLayout(pnDanhSachDon, BoxLayout.Y_AXIS));
        pnDanhSachDon.setBackground(Color.WHITE);
        themSanPham(
        	    1,
        	    "Paracetamol 500mg con pho quố dấn alalalallaa",
        	    new String[]{"2025A", "2025B"},           // Mảng lô
        	    "2025",
        	    new String[]{"Hộp", "Vỉ", "Lọ"},          // Mảng đơn vị
        	    2,
        	    "KM: Giảm 20%",
        	    "15.000.000 đ",
        	    "30.000 đ",
        	    "/images/paracetamol.jpg"
        	);
        JScrollPane scrollPane = new JScrollPane(pnDanhSachDon);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        pnCenter.add(scrollPane, BorderLayout.CENTER);

        return pnCenter;
    }

	    private void themSanPham(
	    int stt,
	    String tenThuoc,
	    String[] loArr,         // Mảng lô
	    String ton,
	    String[] donViArr,      // Mảng đơn vị
	    int soLuong,
	    String khuyenMai,
	    String donGia,
	    String thanhTien,
	    String anhPath
	) {
	    Box row = Box.createHorizontalBox();
	    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
	    row.setBorder(new CompoundBorder(
	        new LineBorder(new Color(0xDDDDDD), 1),
	        new EmptyBorder(8, 10, 8, 10)
	    ));
	    row.setBackground(new Color(0xFAFAFA));
	    row.setOpaque(true);
	
	    // STT
	    JLabel lblSTT = new JLabel(String.valueOf(stt));
	    lblSTT.setFont(new Font("Segoe UI", Font.BOLD, 16));
	    lblSTT.setPreferredSize(new Dimension(40, 30));
	    row.add(lblSTT);
	    row.add(Box.createHorizontalStrut(10));
	
	    // Hình ảnh
	    JLabel lblAnh = new JLabel();
	    lblAnh.setPreferredSize(new Dimension(80, 80));
	    lblAnh.setBorder(new LineBorder(Color.LIGHT_GRAY));
	    try {
	        ImageIcon icon = new ImageIcon(getClass().getResource(anhPath));
	        lblAnh.setIcon(new ImageIcon(icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
	    } catch (Exception e) {
	        lblAnh.setText("Ảnh");
	    }
	    row.add(lblAnh);
	    row.add(Box.createHorizontalStrut(10));
	
	    // === THÔNG TIN THUỐC ===
	    Box infoBox = Box.createVerticalBox();
	
	    // Tên thuốc
	    JTextField txtTenThuoc = TaoJtextNhanh.taoTextDonHang(
	        tenThuoc, new Font("Segoe UI", Font.BOLD, 16), new Color(0x00796B), 300);
	    infoBox.add(txtTenThuoc);
	
	    // Lô + tồn
	    Box loBox = Box.createHorizontalBox();
	    loBox.setMaximumSize(new Dimension(300, 30));
	    loBox.setPreferredSize(new Dimension(300, 30));
	
	    // Hiển thị lô: "Lô: A, B, C"
	    String loText = "Lô: " + String.join(", ", loArr);
	    JTextField txtLo = TaoJtextNhanh.taoTextDonHang(
	        loText, new Font("Segoe UI", Font.BOLD, 16), new Color(0x00796B), 150);
	    loBox.add(txtLo);
	    loBox.add(Box.createHorizontalStrut(8));
	
	    JTextField txtTon = TaoJtextNhanh.taoTextDonHang(
	        "Tồn: " + ton, new Font("Segoe UI", Font.BOLD, 16), new Color(0x00796B), 150);
	    loBox.add(txtTon);
	    infoBox.add(loBox);
	
	    row.add(infoBox);
	    row.add(Box.createHorizontalStrut(20));
	
	    // === ĐƠN VỊ TÍNH (ComboBox từ mảng) ===
	    JComboBox<String> cbDonVi = new JComboBox<>(donViArr);
	    cbDonVi.setPreferredSize(new Dimension(70, 30));
	    cbDonVi.setMaximumSize(new Dimension(70, 30));
	    cbDonVi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
	    cbDonVi.setSelectedIndex(0); // Chọn mặc định
	    row.add(cbDonVi);
	    row.add(Box.createHorizontalStrut(10));
	
	    // === SỐ LƯỢNG + / - ===
	    Box soLuongBox = Box.createHorizontalBox();
	    soLuongBox.setMaximumSize(new Dimension(140, 30));
	    soLuongBox.setPreferredSize(new Dimension(140, 30));
	    soLuongBox.setBackground(new Color(0xFAFAFA));
	    soLuongBox.setOpaque(true);
	    soLuongBox.setBorder(new LineBorder(new Color(0xDDDDDD), 1, true));
	
	    JButton btnGiam = new JButton("-");
	    btnGiam.setFont(new Font("Segoe UI", Font.BOLD, 16));
	    btnGiam.setPreferredSize(new Dimension(40, 30));
	    btnGiam.setMargin(new Insets(0, 0, 0, 0));
	    btnGiam.setFocusPainted(false);
	    soLuongBox.add(btnGiam);
	
	    JTextField txtSoLuong = TaoJtextNhanh.hienThi(
	        String.valueOf(soLuong), new Font("Segoe UI", Font.PLAIN, 16), Color.BLACK);
	    txtSoLuong.setMaximumSize(new Dimension(60, 30));
	    txtSoLuong.setPreferredSize(new Dimension(60, 30));
	    txtSoLuong.setHorizontalAlignment(SwingConstants.CENTER);
	    txtSoLuong.setEditable(true);
	    soLuongBox.add(txtSoLuong);
	
	    JButton btnTang = new JButton("+");
	    btnTang.setFont(new Font("Segoe UI", Font.BOLD, 16));
	    btnTang.setPreferredSize(new Dimension(40, 30));
	    btnTang.setMargin(new Insets(0, 0, 0, 0));
	    btnTang.setFocusPainted(false);
	    soLuongBox.add(btnTang);
	
	    row.add(soLuongBox);
	    row.add(Box.createHorizontalStrut(10));
	
	    // === KHUYẾN MÃI ===
	    JTextField txtKM = TaoJtextNhanh.taoTextDonHang(
	        khuyenMai, new Font("Segoe UI", Font.PLAIN, 16), Color.BLACK, 110);
	    txtKM.setHorizontalAlignment(SwingConstants.LEFT);
	    row.add(txtKM);
	    row.add(Box.createHorizontalStrut(10));
	
	    // === ĐƠN GIÁ ===
	    JTextField txtDonGia = TaoJtextNhanh.taoTextDonHang(
	        donGia, new Font("Segoe UI", Font.PLAIN, 16), Color.BLACK, 100);
	    txtDonGia.setHorizontalAlignment(SwingConstants.RIGHT);
	    row.add(txtDonGia);
	    row.add(Box.createHorizontalStrut(10));
	
	    // === THÀNH TIỀN ===
	    JTextField txtThanhTien = TaoJtextNhanh.taoTextDonHang(
	        thanhTien, new Font("Segoe UI", Font.BOLD, 16), new Color(0xD32F2F), 120);
	    txtThanhTien.setHorizontalAlignment(SwingConstants.RIGHT);
	    row.add(txtThanhTien);
	    row.add(Box.createHorizontalGlue());
	
	    // === NÚT XÓA ===
	    JButton btnXoa = new JButton();
	    btnXoa.setPreferredSize(new Dimension(40, 40));
	    btnXoa.setForeground(Color.RED);
	    btnXoa.setBorderPainted(false);
	    btnXoa.setContentAreaFilled(false);
	    btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    try {
	        ImageIcon icon = new ImageIcon(getClass().getResource("/images/bin.png"));
	        btnXoa.setIcon(new ImageIcon(icon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH)));
	    } catch (Exception ignored) {}
	    row.add(btnXoa);
	
	    // Thêm vào danh sách
	    pnDanhSachDon.add(row);
	    pnDanhSachDon.add(Box.createVerticalStrut(5));
	}

	private JPanel createRightPanel() {
        JPanel pnRight = new JPanel();
        pnRight.setPreferredSize(new Dimension(450, 1080));
        pnRight.setBackground(Color.WHITE);
        pnRight.setBorder(new EmptyBorder(25, 25, 25, 25));
        pnRight.setLayout(new BoxLayout(pnRight, BoxLayout.Y_AXIS));

        // === TÌM KHÁCH HÀNG ===
        Box boxTimKhachHang = Box.createHorizontalBox();
        txtTimKH = TaoJtextNhanh.nhapLieu("Nhập SĐT khách hàng");
        boxTimKhachHang.add(txtTimKH);
        pnRight.add(boxTimKhachHang);
        pnRight.add(Box.createVerticalStrut(10));

        // === TÊN KHÁCH HÀNG ===
        Box boxTenKhachHang = Box.createHorizontalBox();
        boxTenKhachHang.add(TaoLabelNhanh.tieuDe("Tên khách hàng:"));
        txtTenKhachHang = TaoJtextNhanh.hienThi("Vãng lai", new Font("Segoe UI", Font.BOLD, 20), new Color(0x00796B));
        boxTenKhachHang.add(txtTenKhachHang);
        pnRight.add(boxTenKhachHang);
        pnRight.add(Box.createVerticalStrut(10));

        // === TỔNG TIỀN HÀNG ===
        Box boxTongTienHang = Box.createHorizontalBox();
        boxTongTienHang.add(TaoLabelNhanh.tieuDe("Tổng tiền hàng:"));
        txtTongTienHang = TaoJtextNhanh.hienThi("0 đ", new Font("Segoe UI", Font.BOLD, 20), Color.BLACK);
        boxTongTienHang.add(txtTongTienHang);
        pnRight.add(boxTongTienHang);
        pnRight.add(Box.createVerticalStrut(10));

        // === GIẢM GIÁ SẢN PHẨM ===
        Box boxGiamSP = Box.createHorizontalBox();
        boxGiamSP.add(TaoLabelNhanh.tieuDe("Giảm giá sản phẩm:"));
        txtGiamSPValue = TaoJtextNhanh.hienThi("0 đ", new Font("Segoe UI", Font.BOLD, 20), Color.BLACK);
        boxGiamSP.add(txtGiamSPValue);
        pnRight.add(boxGiamSP);
        pnRight.add(Box.createVerticalStrut(10));

        // === GIẢM GIÁ HÓA ĐƠN ===
        Box boxGiamHD = Box.createHorizontalBox();
        boxGiamHD.add(TaoLabelNhanh.tieuDe("Giảm giá hóa đơn:"));
        txtGiamHDValue = TaoJtextNhanh.hienThi("0 đ", new Font("Segoe UI", Font.BOLD, 20), Color.BLACK);
        boxGiamHD.add(txtGiamHDValue);
        pnRight.add(boxGiamHD);
        pnRight.add(Box.createVerticalStrut(10));

        // === TỔNG HÓA ĐƠN ===
        Box boxTongHD = Box.createHorizontalBox();
        boxTongHD.add(TaoLabelNhanh.tieuDe("Tổng hóa đơn:"));
        txtTongHDValue = TaoJtextNhanh.hienThi("0 đ", new Font("Segoe UI", Font.BOLD, 20), new Color(0xD32F2F));
        boxTongHD.add(txtTongHDValue);
        pnRight.add(boxTongHD);
        pnRight.add(Box.createVerticalStrut(10));

        // === TIỀN KHÁCH ĐƯA ===
        Box boxTienKhach = Box.createHorizontalBox();
        txtTienKhach = TaoJtextNhanh.nhapLieu("Nhập tiền khách đưa");
        boxTienKhach.add(txtTienKhach);
        pnRight.add(boxTienKhach);
        pnRight.add(Box.createVerticalStrut(10));

        // === GỢI Ý TIỀN ===
        Box goiYTien = Box.createVerticalBox();
        Box row1 = Box.createHorizontalBox();
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        row1.setAlignmentX(Component.CENTER_ALIGNMENT);
        row1.add(TaoButtonNhanh.goiY("50k"));
        row1.add(Box.createHorizontalStrut(5));
        row1.add(TaoButtonNhanh.goiY("100k"));
        row1.add(Box.createHorizontalStrut(5));
        row1.add(TaoButtonNhanh.goiY("200k"));

        Box row2 = Box.createHorizontalBox();
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        row2.setAlignmentX(Component.CENTER_ALIGNMENT);
        row2.add(TaoButtonNhanh.goiY("300k"));
        row2.add(Box.createHorizontalStrut(5));
        row2.add(TaoButtonNhanh.goiY("500k"));
        row2.add(Box.createHorizontalStrut(5));
        row2.add(TaoButtonNhanh.goiY("1000k"));

        goiYTien.add(row1);
        goiYTien.add(Box.createVerticalStrut(5));
        goiYTien.add(row2);
        pnRight.add(goiYTien);
        pnRight.add(Box.createVerticalStrut(10));

        // === TIỀN THỪA ===
        Box boxTienThua = Box.createHorizontalBox();
        boxTienThua.add(TaoLabelNhanh.tieuDe("Tiền thừa:"));
        txtTienThua = TaoJtextNhanh.hienThi("0 đ", new Font("Segoe UI", Font.BOLD, 20), new Color(0x00796B));
        boxTienThua.add(txtTienThua);
        pnRight.add(boxTienThua);
        pnRight.add(Box.createVerticalStrut(10));

        // === NÚT BÁN HÀNG ===
        btnBanHang = TaoButtonNhanh.banHang();
        btnBanHang.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnRight.add(btnBanHang);

        return pnRight;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Bán Hàng - Dùng Class Tiện Ích");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(1600, 900);
            f.setLocationRelativeTo(null);
            f.setContentPane(new BanHang_GUI());
            f.setVisible(true);
        });
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	 private void timSanPhamTheoSoDangKy() {
	        String tuKhoa = txtTimThuoc.getText().trim();
	        if (tuKhoa.isEmpty()) {
	            JOptionPane.showMessageDialog(this, "Vui lòng nhập số đăng ký hoặc mã sản phẩm!");
	            return;
	        }

	        SanPham sp = sanPhamDAO.timSanPhamTheoSoDangKy(tuKhoa);
	        if (sp == null) sp = sanPhamDAO.laySanPhamTheoMa(tuKhoa);
	        if (sp == null) {
	            JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm với SĐK/Mã: " + tuKhoa);
	            return;
	        }
	        System.out.println(sp); // Kiểm tra log

	        LoSanPham lo = loDAO.timLoGanHetHanTheoSanPham(sp.getMaSanPham());
	        if (lo == null || loDAO.tinhSoLuongTonThucTe(lo.getMaLo()) <= 0) {
	            JOptionPane.showMessageDialog(this, "Sản phẩm này hiện đã hết hàng!");
	            return;
	        }
	        
	        // ===== LẤY TẤT CẢ QUY CÁCH ĐÓNG GÓI CỦA SẢN PHẨM NÀY =====
	        ArrayList<QuyCachDongGoi> dsQC = quyCachDAO.layQuyCachTheoSanPham(sp.getMaSanPham()); 
	        QuyCachDongGoi qcMacDinh = null;
	        
	        // Tìm đơn vị gốc (DonViGoc = true) để đặt mặc định
	        for (QuyCachDongGoi qc : dsQC) {
	            if (qc.isDonViGoc()) {
	                qcMacDinh = qc;
	                break;
	            }
	        }
	        // Nếu không có đơn vị gốc, chọn đơn vị đầu tiên
	        if (qcMacDinh == null && !dsQC.isEmpty()) {
	            qcMacDinh = dsQC.get(0);
	        }
	        
	        if (qcMacDinh == null) {
	              JOptionPane.showMessageDialog(this, "Sản phẩm này chưa có quy cách đóng gói nào được định nghĩa!", 
	                                           "Lỗi cấu hình", JOptionPane.ERROR_MESSAGE);
	              return;
	        }
	        // =========================================================
}