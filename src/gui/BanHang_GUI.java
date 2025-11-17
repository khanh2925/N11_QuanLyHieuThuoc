package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.*;

import customcomponent.TaoJtextNhanh;
import customcomponent.TaoLabelNhanh;
import customcomponent.TaoButtonNhanh;

import dao.ChiTietKhuyenMaiSanPham_DAO;
import dao.HoaDon_DAO;
import dao.LoSanPham_DAO;
import dao.QuyCachDongGoi_DAO;
import dao.SanPham_DAO;
import dao.KhachHang_DAO;
import entity.ChiTietHoaDon;
import entity.ChiTietKhuyenMaiSanPham;
import entity.DonViTinh;
import entity.HoaDon;
import entity.ItemDonHang;
import entity.KhachHang;
import entity.KhuyenMai;
import entity.LoSanPham;
import entity.NhanVien;
import entity.QuyCachDongGoi;
import entity.SanPham;
import entity.Session;
import entity.TaiKhoan;
import dao.KhuyenMai_DAO;
import entity.KhuyenMai;
import enums.HinhThucKM;

/**
 * Giao diện Bán Hàng
 */
public class BanHang_GUI extends JPanel implements ActionListener {

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

    private SanPham_DAO sanPhamDao;
    private LoSanPham_DAO loSanPhamDao;
    private QuyCachDongGoi_DAO quyCachDongGoiDao;
    private ChiTietKhuyenMaiSanPham_DAO ctKMSPDao;
    private KhachHang_DAO khachHangDao;
    private HoaDon_DAO hoaDonDao;
    private KhuyenMai_DAO khuyenMaiDao;

    private List<ItemDonHang> dsItem = new ArrayList<>();

    // Tổng tiền
    private double tongTienHang = 0;
    private double tongGiamSP = 0;
    private double tongGiamHD = 0;
    private double tongHoaDon = 0;

    // Gợi ý tiền khách
    private JButton[] btnGoiY = new JButton[6];
    private long[] goiYValues = new long[6];
    private KhachHang khachHangHienTai;
	private JCheckBox ckThuocTheoDon;
	
	// Lấy nhân viên
	private TaiKhoan tk = Session.getInstance().getTaiKhoanDangNhap();
	private NhanVien nhanVienHienTai =  tk.getNhanVien();

	private KhuyenMai kmHoaDonDangApDung;
	
    public BanHang_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        initialize();

        sanPhamDao = new SanPham_DAO();
        loSanPhamDao = new LoSanPham_DAO();
        quyCachDongGoiDao = new QuyCachDongGoi_DAO();
        ctKMSPDao = new ChiTietKhuyenMaiSanPham_DAO();
        khachHangDao = new KhachHang_DAO();
        dsItem = new ArrayList<>();
        khachHangHienTai = null;
        khuyenMaiDao = new KhuyenMai_DAO();
        hoaDonDao = new HoaDon_DAO();


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
        txtTimThuoc.addActionListener(this);
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

        JScrollPane scrollPane = new JScrollPane(pnDanhSachDon);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        pnCenter.add(scrollPane, BorderLayout.CENTER);

        return pnCenter;
    }

    /**
     * Tạo 1 dòng sản phẩm trong panel từ ItemDonHang
     */
    private void themSanPham(
            ItemDonHang item,
            int stt,
            String[] donViArr,
            int[] heSoArr,
            double[] giaArr,
            String anhPath
    ) {
        DonHangItemPanel panel = new DonHangItemPanel(
                item,
                stt,
                donViArr,
                heSoArr,
                giaArr,
                anhPath,
                new DonHangItemPanel.ItemPanelListener() {
                    @Override
                    public void onItemUpdated(ItemDonHang it) {
                        capNhatTongTien();
                    }

                    @Override
                    public void onItemDeleted(ItemDonHang it, DonHangItemPanel p) {
                        pnDanhSachDon.remove(p);
                        pnDanhSachDon.revalidate();
                        pnDanhSachDon.repaint();
                        capNhatTongTien();
                        capNhatSTT();
                    }
                },
                this,
                dsItem,
                loSanPhamDao,
                quyCachDongGoiDao
        );

        pnDanhSachDon.add(panel);
        pnDanhSachDon.add(Box.createVerticalStrut(5));
        pnDanhSachDon.revalidate();
        pnDanhSachDon.repaint();
        capNhatSTT();
    }

    /**
     * Cho DonHangItemPanel gọi ngược khi tự tạo ItemDonHang mới (nhân dòng, lô mới)
     */
    public void themSanPhamTuPanel(
            ItemDonHang itemMoi,
            String[] donViArr,
            int[] heSoArr,
            double[] giaArr,
            String anhPath
    ) {
        int sttMoi = dsItem.size(); // đơn giản: theo thứ tự trong dsItem
        themSanPham(itemMoi, sttMoi, donViArr, heSoArr, giaArr, anhPath);
    }

    private String formatTien(double tien) {
        DecimalFormat df = new DecimalFormat("#,##0");
        return df.format(tien) + " đ";
    }

    private JPanel createRightPanel() {
        JPanel pnRight = new JPanel();
        pnRight.setPreferredSize(new Dimension(450, 1080));
        pnRight.setBackground(Color.WHITE);
        pnRight.setBorder(new EmptyBorder(25, 25, 25, 25));
        pnRight.setLayout(new BoxLayout(pnRight, BoxLayout.Y_AXIS));

        // ==== TÌM KHÁCH HÀNG ====
        Box boxTimKhachHang = Box.createHorizontalBox();
        txtTimKH = TaoJtextNhanh.nhapLieu("Nhập SĐT khách hàng");
        ckThuocTheoDon = new JCheckBox("Thuốc theo đơn:");
        boxTimKhachHang.add(txtTimKH);
        pnRight.add(boxTimKhachHang);
        pnRight.add(Box.createVerticalStrut(10));

        txtTimKH.addActionListener(e -> xuLyTimKhach());
        txtTimKH.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String s = txtTimKH.getText().trim();
                if (!s.isEmpty()) {
                    xuLyTimKhach();
                }
            }
        });

        // ==== TÊN KHÁCH ====
        Box boxTenKhachHang = Box.createHorizontalBox();
        boxTenKhachHang.add(TaoLabelNhanh.tieuDe("Tên khách hàng:"));
        txtTenKhachHang = TaoJtextNhanh.hienThi("Vãng lai", new Font("Segoe UI", Font.BOLD, 20), new Color(0x00796B));
        boxTenKhachHang.add(txtTenKhachHang);
        pnRight.add(boxTenKhachHang);
        pnRight.add(Box.createVerticalStrut(10));

        // ==== TỔNG TIỀN HÀNG ====
        Box boxTongTienHang = Box.createHorizontalBox();
        boxTongTienHang.add(TaoLabelNhanh.tieuDe("Tổng tiền hàng:"));
        txtTongTienHang = TaoJtextNhanh.hienThi("0 đ", new Font("Segoe UI", Font.BOLD, 20), Color.BLACK);
        boxTongTienHang.add(txtTongTienHang);
        pnRight.add(boxTongTienHang);
        pnRight.add(Box.createVerticalStrut(10));

        // ==== GIẢM GIÁ SẢN PHẨM ====
        Box boxGiamSP = Box.createHorizontalBox();
        boxGiamSP.add(TaoLabelNhanh.tieuDe("Giảm giá sản phẩm:"));
        txtGiamSPValue = TaoJtextNhanh.hienThi("0 đ", new Font("Segoe UI", Font.BOLD, 20), Color.BLACK);
        boxGiamSP.add(txtGiamSPValue);
        pnRight.add(boxGiamSP);
        pnRight.add(Box.createVerticalStrut(10));

        // ==== GIẢM GIÁ HÓA ĐƠN ====
        Box boxGiamHD = Box.createHorizontalBox();
        boxGiamHD.add(TaoLabelNhanh.tieuDe("Giảm giá hóa đơn:"));
        txtGiamHDValue = TaoJtextNhanh.hienThi("0 đ", new Font("Segoe UI", Font.BOLD, 20), Color.BLACK);
        boxGiamHD.add(txtGiamHDValue);
        pnRight.add(boxGiamHD);
        pnRight.add(Box.createVerticalStrut(10));

        // ==== TỔNG HÓA ĐƠN ====
        Box boxTongHD = Box.createHorizontalBox();
        boxTongHD.add(TaoLabelNhanh.tieuDe("Tổng hóa đơn:"));
        txtTongHDValue = TaoJtextNhanh.hienThi("0 đ", new Font("Segoe UI", Font.BOLD, 20), new Color(0xD32F2F));
        boxTongHD.add(txtTongHDValue);
        pnRight.add(boxTongHD);
        pnRight.add(Box.createVerticalStrut(10));

        // ==== TIỀN KHÁCH ĐƯA ====
        Box boxTienKhach = Box.createHorizontalBox();
        txtTienKhach = TaoJtextNhanh.nhapLieu("Nhập tiền khách đưa");
        boxTienKhach.add(txtTienKhach);
        pnRight.add(boxTienKhach);
        pnRight.add(Box.createVerticalStrut(10));

        txtTienKhach.addActionListener(e -> capNhatTienThua());
        txtTienKhach.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                capNhatTienThua();
            }
        });

        // ==== GỢI Ý TIỀN ====
        Box goiYTien = Box.createVerticalBox();
        Box row1 = Box.createHorizontalBox();
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        row1.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnGoiY[0] = TaoButtonNhanh.goiY("50k");
        btnGoiY[1] = TaoButtonNhanh.goiY("100k");
        btnGoiY[2] = TaoButtonNhanh.goiY("200k");

        row1.add(btnGoiY[0]);
        row1.add(Box.createHorizontalStrut(5));
        row1.add(btnGoiY[1]);
        row1.add(Box.createHorizontalStrut(5));
        row1.add(btnGoiY[2]);

        Box row2 = Box.createHorizontalBox();
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        row2.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnGoiY[3] = TaoButtonNhanh.goiY("300k");
        btnGoiY[4] = TaoButtonNhanh.goiY("500k");
        btnGoiY[5] = TaoButtonNhanh.goiY("1000k");

        row2.add(btnGoiY[3]);
        row2.add(Box.createHorizontalStrut(5));
        row2.add(btnGoiY[4]);
        row2.add(Box.createHorizontalStrut(5));
        row2.add(btnGoiY[5]);

        goiYTien.add(row1);
        goiYTien.add(Box.createVerticalStrut(5));
        goiYTien.add(row2);
        pnRight.add(goiYTien);
        pnRight.add(Box.createVerticalStrut(10));

        for (int i = 0; i < btnGoiY.length; i++) {
            final int idx = i;
            btnGoiY[i].addActionListener(e -> {
                long val = goiYValues[idx];
                if (val <= 0) return;
                txtTienKhach.setText(formatTien(val));
                capNhatTienThua();
            });
        }

        // ==== TIỀN THỪA ====
        Box boxTienThua = Box.createHorizontalBox();
        boxTienThua.add(TaoLabelNhanh.tieuDe("Tiền thừa:"));
        txtTienThua = TaoJtextNhanh.hienThi("0 đ", new Font("Segoe UI", Font.BOLD, 20), new Color(0x00796B));
        boxTienThua.add(txtTienThua);
        pnRight.add(boxTienThua);
        pnRight.add(Box.createVerticalStrut(10));

        // ==== NÚT BÁN HÀNG ====
        btnBanHang = TaoButtonNhanh.banHang();
        btnBanHang.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBanHang.addActionListener(this);
        pnRight.add(btnBanHang);

        return pnRight;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Bán Hàng");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(1600, 900);
            f.setLocationRelativeTo(null);
            f.setContentPane(new BanHang_GUI());
            f.setVisible(true);
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == txtTimThuoc) {
            xuLyTimThuoc();
        } else if( e.getSource() == btnBanHang) {
        		xuLyBanHang();
        }
    }

    private void xuLyBanHang() {
        // 1. Kiểm tra giỏ
        if (dsItem == null || dsItem.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Chưa có sản phẩm nào trong đơn !",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Ràng buộc: nếu kê đơn -> bắt buộc có khách (không Vãng lai)
        boolean thuocKeDon = ckThuocTheoDon.isSelected();
        if (thuocKeDon && khachHangHienTai == null) {
            JOptionPane.showMessageDialog(this,
                    "Đơn thuốc kê đơn bắt buộc phải chọn khách hàng.",
                    "Thiếu khách hàng",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Lấy khách cho hóa đơn
        KhachHang khForHD = khachHangHienTai;
        if (khForHD == null) {
            // chỉ cho Vãng lai khi không kê đơn
            khForHD = khachHangDao.timKhachHangTheoMa("KH-00000000-0000");
        }

	     // 4. Kiểm tra tiền khách đưa (dựa trên tongHoaDon đã tính sẵn + KM HĐ nếu có)
	        double tienKhach = parseTienTuTextField(txtTienKhach);
	
	        // Nếu chưa nhập hoặc nhập 0
	        if (tienKhach <= 0) {
	            JOptionPane.showMessageDialog(
	                    this,
	                    "Vui lòng nhập số tiền khách đưa!",
	                    "Thiếu tiền khách đưa",
	                    JOptionPane.WARNING_MESSAGE
	            );
	            txtTienKhach.requestFocus();
	            txtTienKhach.selectAll();
	            return;
	        }
	
	        // Nếu ít hơn tổng hóa đơn -> KHÔNG CHO TẠO HÓA ĐƠN
	        if (tienKhach < tongHoaDon) {
	            JOptionPane.showMessageDialog(
	                    this,
	                    "Tiền khách đưa (" + formatTien(tienKhach) + ") ít hơn tổng hóa đơn (" + formatTien(tongHoaDon) + ").\n"
	                            + "Vui lòng thu đủ tiền trước khi lập hóa đơn!",
	                    "Tiền không đủ",
	                    JOptionPane.WARNING_MESSAGE
	            );
	            txtTienKhach.requestFocus();
	            txtTienKhach.selectAll();
	            return;
	        }



        // 5. Chuẩn bị chi tiết hóa đơn
        String maHD = hoaDonDao.taoMaHoaDon();
        LocalDate ngayLap = LocalDate.now();

        List<ChiTietHoaDon> dsChiTiet = new ArrayList<>();

        for (ItemDonHang it : dsItem) {
            LoSanPham lo = it.getLoSanPham();
            int soLuong = it.getSoLuongMua();
            double giaBan = it.getDonGiaSauKM(); // đã trừ KM SP nếu có

            // KM SP nếu có -> lưu ở chi tiết
            KhuyenMai khuyenMaiSP = null;
            try {
                ChiTietKhuyenMaiSanPham ctkm = it.getKhuyenMai();
                if (ctkm != null) {
                    khuyenMaiSP = ctkm.getKhuyenMai();
                }
            } catch (Exception ignore) {}

            // Đơn vị tính
            DonViTinh donViTinh = null;
            try {
                QuyCachDongGoi qc = it.getQuyCachHienTai();
                if (qc != null) {
                    donViTinh = qc.getDonViTinh();
                }
            } catch (Exception ex) {}

            if (donViTinh == null) {
                JOptionPane.showMessageDialog(this,
                        "Không xác định được đơn vị tính cho sản phẩm: " + it.getTenSanPham(),
                        "Thiếu đơn vị tính",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            HoaDon hdTmp = new HoaDon();
            hdTmp.setMaHoaDon(maHD);

            ChiTietHoaDon cthd = new ChiTietHoaDon(
                    hdTmp,
                    lo,
                    soLuong,
                    giaBan,
                    khuyenMaiSP,
                    donViTinh
            );
            dsChiTiet.add(cthd);
        }

        // 6. Tạo hóa đơn
        HoaDon hd = new HoaDon(
                maHD,
                nhanVienHienTai,
                khForHD,
                ngayLap,
                kmHoaDonDangApDung,   // 👈 nếu có KM hóa đơn thì gắn vào đây, ngược lại là null
                dsChiTiet,
                thuocKeDon
        );

        // Lưu ý: HoaDon.getTongTien() nên trả về đúng số sau KM HĐ.  
        // Nếu constructor chỉ lưu raw, bạn có thể set thêm field tongTien = tongHoaDon.

        boolean ok = hoaDonDao.themHoaDon(hd);
        if (!ok) {
            JOptionPane.showMessageDialog(this,
                    "Lưu hóa đơn thất bại!\nVui lòng thử lại.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 7. Sau khi lưu thành công: giảm số lượng KM hóa đơn nếu đang dùng
        if (kmHoaDonDangApDung != null) {
            khuyenMaiDao.giamSoLuong(kmHoaDonDangApDung.getMaKM());
        }

        JOptionPane.showMessageDialog(this,
                "Lập hóa đơn thành công!\nMã hóa đơn: " + maHD,
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE);

        lamMoiSauKhiBanThanhCong();
    }



    private void lamMoiSauKhiBanThanhCong() {
        dsItem.clear();
        pnDanhSachDon.removeAll();
        pnDanhSachDon.revalidate();
        pnDanhSachDon.repaint();

        tongTienHang = 0;
        tongGiamSP = 0;
        tongGiamHD = 0;
        tongHoaDon = 0;

        txtTongTienHang.setText("0 đ");
        txtGiamSPValue.setText("0 đ");
        txtGiamHDValue.setText("0 đ");
        txtTongHDValue.setText("0 đ");
        txtTienKhach.setText("");
        txtTienThua.setText("0 đ");

        // Trả lại khách vãng lai
        khachHangHienTai = null;
        txtTimKH.setText("");
        txtTenKhachHang.setText("Vãng lai");

        // Xóa gợi ý tiền
        for (int i = 0; i < btnGoiY.length; i++) {
            if (btnGoiY[i] != null) {
                btnGoiY[i].setText("");
            }
            goiYValues[i] = 0;
        }
    }

    private double parseTienTuTextField(JTextField txt) {
        String raw = txt.getText().trim();
        raw = raw.replace(".", "")
                .replace(",", "")
                .replace("đ", "")
                .replace("Đ", "")
                .replace("k", "")
                .replace("K", "")
                .trim();
        if (raw.isEmpty()) return 0;
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

	// ================= XỬ LÝ TÌM THUỐC ==================
    private void xuLyTimThuoc() {
        String tuKhoa = txtTimThuoc.getText().trim();
        if (tuKhoa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số đăng ký hoặc mã sản phẩm!");
            return;
        }

        SanPham sp = sanPhamDao.timSanPhamTheoSoDangKy(tuKhoa);
        if (sp == null) sp = sanPhamDao.laySanPhamTheoMa(tuKhoa);

        if (sp == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm với SĐK/Mã: " + tuKhoa);
            return;
        }

        if (congDonNeuTrungSanPham(sp)) {
            txtTimThuoc.setText("");
            txtTimThuoc.requestFocus();
            return;
        }

        // ===== Lấy danh sách lô =====
        List<LoSanPham> dsLo = loSanPhamDao.layDanhSachLoTheoMaSanPham(sp.getMaSanPham());
        if (dsLo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Sản phẩm này không còn lô nào đang tồn kho!",
                    "Lỗi tồn kho",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ===== Quy cách =====
        List<QuyCachDongGoi> dsQuyCach = quyCachDongGoiDao.layDanhSachQuyCachTheoSanPham(sp.getMaSanPham());
        QuyCachDongGoi quyCachGoc = dsQuyCach.stream()
                .filter(QuyCachDongGoi::isDonViGoc)
                .findFirst()
                .orElse(null);

        if (quyCachGoc == null) {
            JOptionPane.showMessageDialog(this,
                    "Sản phẩm chưa có quy cách gốc!",
                    "Lỗi cấu hình",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] donViArr = new String[dsQuyCach.size()];
        double[] giaArr = new double[dsQuyCach.size()];
        int[] heSoArr = new int[dsQuyCach.size()];

        for (int i = 0; i < dsQuyCach.size(); i++) {
            QuyCachDongGoi qc = dsQuyCach.get(i);
            donViArr[i] = qc.getDonViTinh().getTenDonViTinh();
            double giaGoc = sp.getGiaBan() * qc.getHeSoQuyDoi();
            giaArr[i] = giaGoc - giaGoc * qc.getTiLeGiam();
            heSoArr[i] = qc.getHeSoQuyDoi();
        }

        // ===== KM theo SP =====
        List<ChiTietKhuyenMaiSanPham> dsKMSP = ctKMSPDao.layChiTietKhuyenMaiDangHoatDongTheoMaSP(sp.getMaSanPham());
        ChiTietKhuyenMaiSanPham kmSP = dsKMSP.isEmpty() ? null : dsKMSP.get(0);

        // ===== Ảnh =====
        String anhPath = sp.getHinhAnh();
        if (anhPath == null || anhPath.isEmpty()) {
            anhPath = "/images/default_medicine.png";
        }

        // Lô gần nhất
        LoSanPham loDauTien = dsLo.get(0);
        int tonThucTe = loSanPhamDao.tinhSoLuongTonThucTe(loDauTien.getMaLo());
        loDauTien.setSoLuongTon(tonThucTe);
        if (tonThucTe <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Lô gần hết hạn đã hết hàng (tồn khả dụng = 0)!",
                    "Hết hàng",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Map quy cách
        Map<String, QuyCachDongGoi> mapQC = new HashMap<>();
        for (QuyCachDongGoi qc : dsQuyCach) {
            String tenDV = qc.getDonViTinh().getTenDonViTinh();
            mapQC.put(tenDV, qc);
        }

        String tenDonViMacDinh = donViArr[0];
        double giaMacDinh = giaArr[0];

        ItemDonHang item = new ItemDonHang(
                sp,
                loDauTien,
                kmSP,
                mapQC,
                tenDonViMacDinh,
                giaMacDinh
        );
        dsItem.add(item);

        int stt = dsItem.size();
        themSanPham(item, stt, donViArr, heSoArr, giaArr, anhPath);

        pnDanhSachDon.revalidate();
        pnDanhSachDon.repaint();
        txtTimThuoc.setText("");
        txtTimThuoc.requestFocus();
        capNhatTongTien();
    }

    // ================= HỖ TRỢ CỘNG DỒN ==================
    private JButton timBtnTangTrongRow(JComponent row) {
        return timBtnTangTrongContainer(row);
    }

    private JButton timBtnTangTrongContainer(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JButton) {
                JButton b = (JButton) c;
                if ("btnTang".equals(b.getName())) {
                    return b;
                }
            } else if (c instanceof Container) {
                JButton nested = timBtnTangTrongContainer((Container) c);
                if (nested != null) return nested;
            }
        }
        return null;
    }

    private boolean congDonNeuTrungSanPham(SanPham sp) {
        Component[] comps = pnDanhSachDon.getComponents();

        for (int i = comps.length - 1; i >= 0; i--) {
            Component comp = comps[i];
            if (!(comp instanceof JComponent)) continue;
            JComponent row = (JComponent) comp;

            Object obj = row.getClientProperty("item");
            if (!(obj instanceof ItemDonHang)) continue;
            ItemDonHang item = (ItemDonHang) obj;

            if (!item.getSanPham().getMaSanPham().equals(sp.getMaSanPham())) {
                continue;
            }

            if (item.isKhoaChinhSua()) {
                continue;
            }

            JButton btnTang = timBtnTangTrongRow(row);
            if (btnTang == null) continue;

            btnTang.doClick();
            return true;
        }

        return false;
    }

    // ================= CẬP NHẬT TỔNG TIỀN ==================
    private void capNhatTongTien() {
        tongTienHang = 0;
        tongGiamSP = 0;

        boolean coKmSanPham = false;

        // 1. Tính tổng tiền hàng gốc + tổng giảm SP, check xem có KM SP không
        for (ItemDonHang item : dsItem) {
            double thanhTienSauKM = item.getThanhTienSauKM(); // đã trừ KM SP
            double giamSP = item.getTongGiamGiaSP();          // số tiền giảm cho item

            tongTienHang += thanhTienSauKM + giamSP; // cộng lại thành tiền gốc
            tongGiamSP += giamSP;

            if (item.getKhuyenMai() != null) {
                coKmSanPham = true;
            }
        }

        // 2. Tính số tiền sau KM SP
        double tienSauKmSP = tongTienHang - tongGiamSP;
        if (tienSauKmSP < 0) tienSauKmSP = 0;

        // 3. Tính KM hóa đơn (chỉ khi KHÔNG có KM sản phẩm)
        tongGiamHD = 0;
        kmHoaDonDangApDung = null;

        if (!coKmSanPham && !dsItem.isEmpty()) {
            List<KhuyenMai> dsKm = khuyenMaiDao.layKhuyenMaiDangHoatDong();
            double giamMax = 0;
            KhuyenMai kmChon = null;

            for (KhuyenMai km : dsKm) {
                if (!km.isKhuyenMaiHoaDon()) continue; // chỉ lấy KM hóa đơn

                double dieuKien = km.getDieuKienApDungHoaDon();
                if (tienSauKmSP < dieuKien) continue; // không đủ điều kiện

                double giam = tinhTienGiamHoaDon(tienSauKmSP, km);
                if (giam > giamMax) {
                    giamMax = giam;
                    kmChon = km;
                }
            }

            tongGiamHD = giamMax;
            kmHoaDonDangApDung = kmChon;
        }

        // 4. Tổng hóa đơn = sau KM SP - KM HĐ
        tongHoaDon = tienSauKmSP - tongGiamHD;
        if (tongHoaDon < 0) tongHoaDon = 0;

        // 5. Cập nhật UI
        txtTongTienHang.setText(formatTien(tongTienHang));
        txtGiamSPValue.setText(formatTien(tongGiamSP));
        txtGiamHDValue.setText(formatTien(tongGiamHD));
        txtTongHDValue.setText(formatTien(tongHoaDon));

        capNhatTienThua();
        capNhatGoiYTien();
    }


    private void capNhatTienThua() {
        String raw = txtTienKhach.getText().trim();

        raw = raw.replace(".", "")
                .replace(",", "")
                .replace("đ", "")
                .replace("Đ", "")
                .replace("k", "")
                .replace("K", "")
                .trim();

        double tienKhach = 0;
        if (!raw.isEmpty()) {
            try {
                tienKhach = Double.parseDouble(raw);
            } catch (NumberFormatException ex) {
                tienKhach = 0;
            }
        }

        double tienThua = tienKhach - tongHoaDon;
        if (tienThua < 0) tienThua = 0;

        txtTienThua.setText(formatTien(tienThua));
    }

    private String formatTienShort(long tien) {
        long nghin = Math.round(tien / 1000.0);
        return nghin + "k";
    }

    private void capNhatGoiYTien() {
        if (tongHoaDon <= 0) {
            return;
        }

        long bill = Math.round(tongHoaDon);
        long billK = (long) Math.ceil(bill / 1000.0);

        java.util.LinkedHashSet<Long> set = new java.util.LinkedHashSet<>();

        set.add(billK);
        set.add(billK + 1);

        long round5 = ((billK + 4) / 5) * 5;
        set.add(round5);

        long round10 = ((billK + 9) / 10) * 10;
        set.add(round10);

        long round50 = ((billK + 49) / 50) * 50;
        set.add(round50);

        long round100 = ((billK + 99) / 100) * 100;
        set.add(round100);

        if (set.size() < btnGoiY.length) {
            long round500 = ((billK + 499) / 500) * 500;
            set.add(round500);
        }

        java.util.List<Long> ds = new java.util.ArrayList<>(set);
        java.util.Collections.sort(ds);

        int max = Math.min(ds.size(), btnGoiY.length);

        for (int i = 0; i < max; i++) {
            long valK = ds.get(i);
            long val = valK * 1000;
            goiYValues[i] = val;
            if (btnGoiY[i] != null) {
                btnGoiY[i].setText(formatTienShort(val));
            }
        }
        for (int i = max; i < btnGoiY.length; i++) {
            if (btnGoiY[i] != null) {
                btnGoiY[i].setText("");
                goiYValues[i] = 0;
            }
        }
    }

    // ================= ĐÁNH LẠI STT ==================
    private void capNhatSTT() {
        Component[] comps = pnDanhSachDon.getComponents();
        int so = 1;
        for (Component comp : comps) {
            if (comp instanceof DonHangItemPanel) {
                DonHangItemPanel p = (DonHangItemPanel) comp;
                p.setStt(so++);
            }
        }
    }

    // ================= TÌM KHÁCH ==================
    private void xuLyTimKhach() {
        String sdt = txtTimKH.getText().trim();

        if (sdt.isEmpty()) {
            khachHangHienTai = null;
            txtTenKhachHang.setText("Vãng lai");
            return;
        }

        if (!sdt.matches("\\d{9,11}")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Số điện thoại không hợp lệ (chỉ nhận 9–11 chữ số).",
                    "Lỗi",
                    JOptionPane.WARNING_MESSAGE
            );
            txtTimKH.requestFocus();
            return;
        }

        KhachHang kh = khachHangDao.timKhachHangTheoSoDienThoai(sdt);

        if (kh == null) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Không tìm thấy khách có SĐT: " + sdt + ".\n"
                            + "Bạn muốn giữ khách 'Vãng lai' không?",
                    "Không tìm thấy khách",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                khachHangHienTai = null;
                txtTenKhachHang.setText("Vãng lai");
            }
            return;
        }

        khachHangHienTai = kh;
        txtTenKhachHang.setText(kh.getTenKhachHang());
    }
    private double tinhTienGiamHoaDon(double tongSauGiamSP, KhuyenMai km) {
        if (km == null || km.getHinhThuc() == null) return 0;

        double giam = 0;
        switch (km.getHinhThuc()) {

            case GIAM_GIA_PHAN_TRAM: // ví dụ GIAM_PHAN_TRAM
                giam = tongSauGiamSP * km.getGiaTri() / 100.0;
                break;
            case GIAM_GIA_TIEN: // ví dụ GIAM_TIEN
                giam = km.getGiaTri();
                break;
            default:
                break;
        }

        if (giam < 0) giam = 0;
        if (giam > tongSauGiamSP) giam = tongSauGiamSP;
        return giam;
    }

}
