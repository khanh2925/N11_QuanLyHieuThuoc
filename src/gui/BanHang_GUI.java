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

import component.input.TaoJtextNhanh;
import component.label.TaoLabelNhanh;
import component.button.TaoButtonNhanh;

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
import enums.HinhThucKM;
import gui.panel.DonHangItemPanel;
import gui.dialog.HoaDonPreviewDialog;

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
	private NhanVien nhanVienHienTai = tk.getNhanVien();
	private KhuyenMai kmHoaDonDangApDung;
	
	private final String PLACEHOLDER_TIM_KH = "Nhập số điện thoại khách hàng";
	private final String PLACEHOLDER_TIM_THUOC = "Nhập mã sản phẩm hoặc số đăng ký";
	private final String PLACEHOLDER_TIEN_KHACH = "Nhập tiền khách đưa";
	private JTextField textField;

	private JButton btnApDungKMHD;
	private KhuyenMai kmHoaDonGoiY; // Lưu tạm KM ngon nhất tìm được
	private boolean cheDoUuTienHoaDon = false; // Mặc định là False (Ưu tiên KM Sản phẩm)

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
		JPanel pnHeader = new JPanel();
		pnHeader.setLayout(null); 
		pnHeader.setPreferredSize(new Dimension(1073, 88));
		pnHeader.setBackground(new Color(0xE3F2F5));
		
		txtTimThuoc = TaoJtextNhanh.timKiem();
		txtTimThuoc.setBorder(new LineBorder(new Color(0x00C0E2), 3, true));
		txtTimThuoc.setBounds(25, 17, 480, 60);
		txtTimThuoc.addActionListener(this);
		txtTimThuoc.setText(PLACEHOLDER_TIM_THUOC);
		txtTimThuoc.setForeground(Color.GRAY); // Màu xám cho placeholder

		txtTimThuoc.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				// Khi click vào: Nếu đang là placeholder thì xóa đi, đổi màu chữ đen
				if (txtTimThuoc.getText().equals(PLACEHOLDER_TIM_THUOC)) {
					txtTimThuoc.setText("");
					txtTimThuoc.setForeground(Color.BLACK);
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				// Khi click ra ngoài: Nếu rỗng thì trả lại placeholder màu xám
				if (txtTimThuoc.getText().trim().isEmpty()) {
					txtTimThuoc.setText(PLACEHOLDER_TIM_THUOC);
					txtTimThuoc.setForeground(Color.GRAY);
				}
			}
		});
		pnHeader.add(txtTimThuoc);

		return pnHeader;
	}

	private JPanel createCenterPanel() {
		JPanel pnCenter = new JPanel(new BorderLayout());
		pnCenter.setBackground(Color.WHITE);
		pnCenter.setPreferredSize(new Dimension(1087, 1080));
		pnCenter.setBorder(
				new CompoundBorder(new LineBorder(new Color(0x00C853), 3, true), new EmptyBorder(10, 10, 10, 10)));

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
	
	private JPanel createRightPanel() {
		JPanel pnRight = new JPanel();
		pnRight.setPreferredSize(new Dimension(1920 - 383 - 1073, 1080));
		pnRight.setBackground(Color.WHITE);
		pnRight.setBorder(new EmptyBorder(25, 25, 25, 25));
		pnRight.setLayout(new BoxLayout(pnRight, BoxLayout.Y_AXIS));

		// ==== TÌM KHÁCH HÀNG ====
		Box boxTimKhachHang = Box.createHorizontalBox();
		txtTimKH = TaoJtextNhanh.nhapLieu(PLACEHOLDER_TIM_KH);
		ckThuocTheoDon = new JCheckBox("Thuốc theo đơn:");
		ckThuocTheoDon.setBackground(null);
		boxTimKhachHang.add(txtTimKH);
		boxTimKhachHang.add(ckThuocTheoDon);
		pnRight.add(boxTimKhachHang);
		pnRight.add(Box.createVerticalStrut(10));

		txtTimKH.addActionListener(e -> xuLyTimKhach(true));
		txtTimKH.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (txtTimKH.getText().equals(PLACEHOLDER_TIM_KH)) {
					txtTimKH.setText("");
					txtTimKH.setForeground(Color.BLACK); // Đổi màu chữ khi nhập
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				String s = txtTimKH.getText().trim();
				
				// Nếu trống hoặc vẫn là placeholder -> Về vãng lai
				if (s.isEmpty() || s.equals(PLACEHOLDER_TIM_KH)) {
					txtTimKH.setText(PLACEHOLDER_TIM_KH);
					txtTimKH.setForeground(Color.GRAY); // Màu placeholder (tùy thư viện bạn dùng)
					troVeKhachVangLai();
					return;
				}
				
				// Nếu có nhập gì đó, thử tìm (nhưng false = không hiện popup lỗi nếu sai)
				xuLyTimKhach(false);
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
		
		// === [MỚI] NÚT GỢI Ý ÁP DỤNG KM HÓA ĐƠN ===
	    btnApDungKMHD = new JButton("Áp dụng KM Hóa Đơn");
	    btnApDungKMHD.setBackground(new Color(0xFF9800)); // Màu cam nổi bật
	    btnApDungKMHD.setForeground(Color.WHITE);
	    btnApDungKMHD.setFont(new Font("Segoe UI", Font.BOLD, 14));
	    btnApDungKMHD.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    btnApDungKMHD.setAlignmentX(Component.CENTER_ALIGNMENT);
	    // Mặc định ẩn, chỉ hiện khi có kèo thơm
	    btnApDungKMHD.setVisible(false); 
	    
	    // Sự kiện bấm nút
	    btnApDungKMHD.addActionListener(e -> xuLyApDungKMHoaDon());
	    
	    pnRight.add(btnApDungKMHD);
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
        txtTienKhach = TaoJtextNhanh.nhapLieu(PLACEHOLDER_TIEN_KHACH);
        txtTienKhach.setForeground(Color.GRAY); // Mặc định màu xám
        boxTienKhach.add(txtTienKhach);
        pnRight.add(boxTienKhach);
        pnRight.add(Box.createVerticalStrut(10));

        txtTienKhach.addActionListener(e -> {
            capNhatTienThua();
            // Sau khi enter, nếu rỗng thì trả về placeholder (tuỳ chọn)
            pnRight.requestFocus(); // Bỏ focus để kích hoạt sự kiện focusLost bên dưới
        });

        txtTienKhach.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                // Click vào: Xóa placeholder, chữ đen
                if (txtTienKhach.getText().equals(PLACEHOLDER_TIEN_KHACH)) {
                    txtTienKhach.setText("");
                    txtTienKhach.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                // Tính toán tiền thừa ngay khi click ra ngoài
                capNhatTienThua();

                // Click ra ngoài: Nếu rỗng -> Hiện placeholder màu xám
                if (txtTienKhach.getText().trim().isEmpty()) {
                    txtTienKhach.setText(PLACEHOLDER_TIEN_KHACH);
                    txtTienKhach.setForeground(Color.GRAY);
                }
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
				if (val <= 0)
					return;
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

	private void xuLyApDungKMHoaDon() {
	    if (!cheDoUuTienHoaDon) {
	        // === TRƯỜNG HỢP 1: CHUYỂN SANG KM HÓA ĐƠN ===
	        if (kmHoaDonGoiY == null) return;

	        int confirm = JOptionPane.showConfirmDialog(this, 
	            "Bạn muốn bỏ khuyến mãi sản phẩm để áp dụng:\n" +
	            "➤ " + kmHoaDonGoiY.getTenKM() + "\n\n" +
	            "Xác nhận chuyển đổi?",
	            "Áp dụng KM Hóa Đơn",
	            JOptionPane.YES_NO_OPTION);

	        if (confirm == JOptionPane.YES_OPTION) {
	            cheDoUuTienHoaDon = true;
	            
	            // 1. Xóa KM trong dữ liệu (Entity)
	            for (ItemDonHang item : dsItem) {
	                item.setKhuyenMai(null);
	            }
	            
	            // 2. Bắt giao diện vẽ lại ngay lập tức (UI)
	            capNhatGiaoDienDanhSachItem(); // <--- Gọi hàm helper mới (xem bên dưới)

	            capNhatTongTien();
	            JOptionPane.showMessageDialog(this, "Đã chuyển sang ưu tiên KM Hóa Đơn!");
	        }

	    } else {
	        // === TRƯỜNG HỢP 2: QUAY VỀ KM SẢN PHẨM ===
	        int confirm = JOptionPane.showConfirmDialog(this, 
	            "Hủy KM Hóa Đơn và tính lại khuyến mãi theo từng sản phẩm?\n",
	            "Quay lại KM Sản Phẩm",
	            JOptionPane.YES_NO_OPTION);

	        if (confirm == JOptionPane.YES_OPTION) {
	            cheDoUuTienHoaDon = false; 
	            
	            // 1. Khôi phục dữ liệu
	            khoiPhucKMSanPham();
	            
	            // 2. Bắt giao diện vẽ lại ngay lập tức
	            capNhatGiaoDienDanhSachItem(); // <--- Gọi hàm helper mới

	            capNhatTongTien();
	            JOptionPane.showMessageDialog(this, "Đã quay lại tính khuyến mãi theo sản phẩm!");
	        }
	    }
	}

	// === HÀM HELPER ĐỂ QUÉT VÀ VẼ LẠI UI ===
	// Bạn copy hàm này để dưới cùng file BanHang_GUI
	private void capNhatGiaoDienDanhSachItem() {
	    // Duyệt qua tất cả các component con trong panel danh sách
	    for (Component comp : pnDanhSachDon.getComponents()) {
	        if (comp instanceof DonHangItemPanel) {
	            // Ép kiểu về DonHangItemPanel và gọi hàm cập nhật
	            DonHangItemPanel panel = (DonHangItemPanel) comp;
	            panel.capNhatGiaoDien();
	        }
	    }
	    // Vẽ lại khung chứa (đề phòng layout bị lệch)
	    pnDanhSachDon.revalidate();
	    pnDanhSachDon.repaint();
	}

	/**
	 * Tạo 1 dòng sản phẩm trong panel từ ItemDonHang
	 */
	private void themSanPham(ItemDonHang item, int stt, String[] donViArr, int[] heSoArr, double[] giaArr,
			String anhPath) {
		DonHangItemPanel panel = new DonHangItemPanel(item, stt, donViArr, heSoArr, giaArr, anhPath,
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
				}, this, dsItem, loSanPhamDao, quyCachDongGoiDao);

		pnDanhSachDon.add(panel);
		pnDanhSachDon.add(Box.createVerticalStrut(5));
		pnDanhSachDon.revalidate();
		pnDanhSachDon.repaint();
		capNhatSTT();
	}

	/**
	 * Cho DonHangItemPanel gọi ngược khi tự tạo ItemDonHang mới (nhân dòng, lô mới)
	 */
	public void themSanPhamTuPanel(ItemDonHang itemMoi, String[] donViArr, int[] heSoArr, double[] giaArr,
			String anhPath) {
		int sttMoi = dsItem.size(); // đơn giản: theo thứ tự trong dsItem
		themSanPham(itemMoi, sttMoi, donViArr, heSoArr, giaArr, anhPath);
	}

	private String formatTien(double tien) {
		DecimalFormat df = new DecimalFormat("#,##0");
		return df.format(tien) + " đ";
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
		} else if (e.getSource() == btnBanHang) {
			xuLyBanHang();
		}
	}

	private void xuLyBanHang() {
		// 1. Kiểm tra giỏ hàng
		if (dsItem == null || dsItem.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Chưa có sản phẩm nào trong đơn !", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		// 2. Ràng buộc: Thuốc kê đơn bắt buộc có khách hàng
		boolean thuocKeDon = ckThuocTheoDon.isSelected();
		if (thuocKeDon && khachHangHienTai == null) {
			JOptionPane.showMessageDialog(this, "Đơn thuốc kê đơn bắt buộc phải chọn khách hàng.", "Thiếu khách hàng",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		// 3. Lấy khách cho hóa đơn (Nếu không chọn thì lấy khách Vãng Lai)
		KhachHang khForHD = khachHangHienTai;
		if (khForHD == null) {
			khForHD = khachHangDao.timKhachHangTheoMa("KH-00000000-0000");
		}

		// 4. Kiểm tra tiền khách đưa
		double tienKhach = parseTienTuTextField(txtTienKhach);
		
		// Nếu chưa nhập hoặc nhập 0
		if (tienKhach <= 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền khách đưa!", "Thiếu tiền khách đưa",
					JOptionPane.WARNING_MESSAGE);
			txtTienKhach.requestFocus();
			txtTienKhach.selectAll();
			return;
		}

		// Nếu tiền đưa ít hơn tổng hóa đơn (đã trừ KM) -> Chặn
		if (tienKhach < tongHoaDon) {
			JOptionPane.showMessageDialog(this,
					"Tiền khách đưa (" + formatTien(tienKhach) + ") ít hơn tổng hóa đơn (" + formatTien(tongHoaDon)
							+ ").\n" + "Vui lòng thu đủ tiền trước khi lập hóa đơn!",
					"Tiền không đủ", JOptionPane.WARNING_MESSAGE);
			txtTienKhach.requestFocus();
			txtTienKhach.selectAll();
			return;
		}

		// 5. Chuẩn bị dữ liệu Hóa Đơn
		String maHD = hoaDonDao.taoMaHoaDon();
		LocalDate ngayLap = LocalDate.now();
		List<ChiTietHoaDon> dsChiTiet = new ArrayList<>();

		// --- VÒNG LẶP TẠO CHI TIẾT ---
		for (ItemDonHang it : dsItem) {
			LoSanPham lo = it.getLoSanPham();
			int soLuong = it.getSoLuongMua();
			
			// Lấy giá bán niêm yết của Đơn Vị Hiện Tại (đã tính tỉ lệ giảm của quy cách nếu có)
			double giaBan = it.getDonGiaGoc(); 

			// Xử lý Đơn Vị Tính
			DonViTinh donViTinh = null;
			try {
				if (it.getQuyCachHienTai() != null) {
					donViTinh = it.getQuyCachHienTai().getDonViTinh();
				}
			} catch (Exception ex) {}

			if (donViTinh == null) {
				JOptionPane.showMessageDialog(this, "Lỗi: Không xác định được ĐVT cho sản phẩm " + it.getTenSanPham());
				return;
			}

			// === XỬ LÝ LOGIC KHUYẾN MÃI (QUAN TRỌNG) ===
			KhuyenMai kmForDetail = null;
			try {
				if (it.getKhuyenMai() != null) {
					KhuyenMai kmGoc = it.getKhuyenMai().getKhuyenMai();

					// ⚠️ Tạo bản sao của Khuyến Mãi để chỉnh sửa giá trị (tránh sửa vào cache chung)
					kmForDetail = new KhuyenMai(
							kmGoc.getMaKM(), 
							kmGoc.getTenKM(), 
							kmGoc.getNgayBatDau(), 
							kmGoc.getNgayKetThuc(),
							kmGoc.isTrangThai(), 
							kmGoc.isKhuyenMaiHoaDon(), 
							kmGoc.getHinhThuc(),
							kmGoc.getGiaTri(), // Giá trị ban đầu
							kmGoc.getDieuKienApDungHoaDon(), 
							kmGoc.getSoLuongKhuyenMai()
					);

					// ⚠️ LOGIC QUY ĐỔI TIỀN MẶT:
					// Nếu là GIAM_GIA_TIEN -> Phải nhân với Hệ Số Quy Đổi của đơn vị đang bán
					// Ví dụ: DB lưu giảm 500đ/viên. Bán Hộp (100 viên) -> Giá trị KM gửi đi phải là 50.000
					if (kmGoc.getHinhThuc() == HinhThucKM.GIAM_GIA_TIEN) {
						int heSo = it.getHeSoQuyCach(); // Lấy từ ItemDonHang (đã map với DAO)
						kmForDetail.setGiaTri(kmGoc.getGiaTri() * heSo);
					}
					// Nếu là GIAM_GIA_PHAN_TRAM -> Giữ nguyên giá trị (vì 10% của hộp tự động to hơn 10% của viên)
				}
			} catch (Exception ignore) {}

			// Tạo chi tiết hóa đơn tạm
			HoaDon hdTmp = new HoaDon();
			hdTmp.setMaHoaDon(maHD);

			// ✅ Tạo ChiTietHoaDon (Đúng thứ tự Constructor mới)
			// (HoaDon, LoSanPham, SoLuong, DonViTinh, GiaBan, KhuyenMai)
			ChiTietHoaDon cthd = new ChiTietHoaDon(hdTmp, lo, soLuong, donViTinh, giaBan, kmForDetail);
			dsChiTiet.add(cthd);
		}

		// 6. Tạo Hóa Đơn Chính
		// Entity HoaDon sẽ tự động tính toán lại tiền nong dựa trên dsChiTiet và kmHoaDonDangApDung
		HoaDon hd = new HoaDon(maHD, nhanVienHienTai, khForHD, ngayLap, kmHoaDonDangApDung, dsChiTiet, thuocKeDon);

		// 7. Lưu xuống CSDL
		boolean ok = hoaDonDao.themHoaDon(hd);
		if (!ok) {
			JOptionPane.showMessageDialog(this, "Lưu hóa đơn thất bại!\nVui lòng thử lại.", "Lỗi Hệ Thống",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// 8. Cập nhật số lượng KM Hóa Đơn (nếu có dùng)
		// Kiểm tra lại từ entity xem KM Hóa Đơn có bị hủy (do ưu tiên KM SP) hay không
		if (hd.getKhuyenMai() != null) {
			khuyenMaiDao.giamSoLuong(hd.getKhuyenMai().getMaKM());
		}

		// 9. Hoàn tất
		JOptionPane.showMessageDialog(this, "Lập hóa đơn thành công!\nMã hóa đơn: " + maHD, "Thành công",
				JOptionPane.INFORMATION_MESSAGE);

		// Sau khi lưu thành công và trước khi gọi lamMoiSauKhiBanThanhCong()
		int confirmPrint = JOptionPane.showConfirmDialog(this, 
		    "Lập hóa đơn thành công! Bạn có muốn xem/in hóa đơn không?", 
		    "Thành công", 
		    JOptionPane.YES_NO_OPTION);

		if (confirmPrint == JOptionPane.YES_OPTION) {
		    new HoaDonPreviewDialog(SwingUtilities.getWindowAncestor(this), hd).setVisible(true);
		}

		lamMoiSauKhiBanThanhCong();
	}

	private void lamMoiSauKhiBanThanhCong() {
		cheDoUuTienHoaDon = false; // <--- Thêm dòng này
	    btnApDungKMHD.setVisible(false); // <--- Ẩn nút đi
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
        
        // ✅ SỬA 1: Reset ô Tiền Khách về Placeholder màu xám
        txtTienKhach.setText(PLACEHOLDER_TIEN_KHACH);
        txtTienKhach.setForeground(Color.GRAY);
        
        txtTienThua.setText("0 đ");

        // Trả lại khách vãng lai
        khachHangHienTai = null;
        
        // ✅ SỬA 2: Reset ô Tìm Khách về Placeholder màu xám
        txtTimKH.setText(PLACEHOLDER_TIM_KH);
        txtTimKH.setForeground(Color.GRAY); // 
        
        txtTenKhachHang.setText("Vãng lai");
        
        // ✅ SỬA 3: Reset ô Tìm Thuốc (nếu chưa có)
        txtTimThuoc.setText(PLACEHOLDER_TIM_THUOC);
        txtTimThuoc.setForeground(Color.GRAY);

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
        
        // Nếu là placeholder -> coi như chưa nhập (0 đồng)
        if (raw.equals(PLACEHOLDER_TIEN_KHACH)) {
            return 0;
        }

        raw = raw.replace(".", "").replace(",", "").replace("đ", "").replace("Đ", "").replace("k", "").replace("K", "")
                .trim();
        if (raw.isEmpty())
            return 0;
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
		if (sp == null)
			sp = sanPhamDao.laySanPhamTheoMa(tuKhoa);

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
			JOptionPane.showMessageDialog(this, "Sản phẩm này không còn lô nào đang tồn kho!", "Lỗi tồn kho",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// ===== Quy cách =====
		List<QuyCachDongGoi> dsQuyCach = quyCachDongGoiDao.layDanhSachQuyCachTheoSanPham(sp.getMaSanPham());
		QuyCachDongGoi quyCachGoc = dsQuyCach.stream().filter(QuyCachDongGoi::isDonViGoc).findFirst().orElse(null);

		if (quyCachGoc == null) {
			JOptionPane.showMessageDialog(this, "Sản phẩm chưa có quy cách gốc!", "Lỗi cấu hình",
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
			anhPath = "/resources/images/default_medicine.png";
		}

		// Lô gần nhất
		LoSanPham loDauTien = dsLo.get(0);
		int tonThucTe = loSanPhamDao.tinhSoLuongTonThucTe(loDauTien.getMaLo());
		loDauTien.setSoLuongTon(tonThucTe);
		if (tonThucTe <= 0) {
			JOptionPane.showMessageDialog(this, "Lô gần hết hạn đã hết hàng (tồn khả dụng = 0)!", "Hết hàng",
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

		ItemDonHang item = new ItemDonHang(sp, loDauTien, kmSP, mapQC, tenDonViMacDinh, giaMacDinh);
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
				if (nested != null)
					return nested;
			}
		}
		return null;
	}

	private boolean congDonNeuTrungSanPham(SanPham sp) {
		Component[] comps = pnDanhSachDon.getComponents();

		for (int i = comps.length - 1; i >= 0; i--) {
			Component comp = comps[i];
			if (!(comp instanceof JComponent))
				continue;
			JComponent row = (JComponent) comp;

			Object obj = row.getClientProperty("item");
			if (!(obj instanceof ItemDonHang))
				continue;
			ItemDonHang item = (ItemDonHang) obj;

			if (!item.getSanPham().getMaSanPham().equals(sp.getMaSanPham())) {
				continue;
			}

			if (item.isKhoaChinhSua()) {
				continue;
			}

			JButton btnTang = timBtnTangTrongRow(row);
			if (btnTang == null)
				continue;

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

	    // 1. Tính toán bình thường theo hiện trạng giỏ hàng
	    for (ItemDonHang item : dsItem) {
	        tongTienHang += item.getThanhTienSauKM() + item.getTongGiamGiaSP(); // Tổng gốc
	        tongGiamSP += item.getTongGiamGiaSP();
	        
	        if (item.getKhuyenMai() != null) {
	            coKmSanPham = true;
	        }
	    }

	    // 2. Logic tính toán KM Hóa Đơn hiện tại (như code cũ của bạn)
	    double tienSauKmSP = tongTienHang - tongGiamSP;
	    if (tienSauKmSP < 0) tienSauKmSP = 0;

	    tongGiamHD = 0;
	    kmHoaDonDangApDung = null;

	    // Chỉ áp dụng KM HĐ nếu KHÔNG CÓ KM SP nào (Logic ưu tiên SP mặc định)
	    if (!coKmSanPham && !dsItem.isEmpty()) {
	        // Tìm KM hóa đơn tốt nhất cho số tiền hiện tại
	        KhuyenMai kmTotNhat = timKMHoaDonTotNhat(tienSauKmSP);
	        if (kmTotNhat != null) {
	            kmHoaDonDangApDung = kmTotNhat;
	            tongGiamHD = tinhTienGiam(tienSauKmSP, kmTotNhat);
	        }
	    }

	    // 3. Tổng thanh toán cuối cùng
	    tongHoaDon = tienSauKmSP - tongGiamHD;
	    if (tongHoaDon < 0) tongHoaDon = 0;

	    // 4. Update UI Text
	    txtTongTienHang.setText(formatTien(tongTienHang));
	    txtGiamSPValue.setText(formatTien(tongGiamSP));
	    txtGiamHDValue.setText(formatTien(tongGiamHD));
	    txtTongHDValue.setText(formatTien(tongHoaDon));
	    capNhatTienThua();
	    capNhatGoiYTien();

	    // ============================================================
	    // 5. LOGIC GỢI Ý (Cái button thông minh nằm ở đây)
	    // ============================================================
	    checkVaHienNutGoiY(tongTienHang, tongGiamSP, tongGiamHD);
	}
	// Hàm kiểm tra xem có nên hiện nút gợi ý không
	private void checkVaHienNutGoiY(double tongTienGoc, double giamSPHienTai, double giamHDHienTai) {
	    // TRƯỜNG HỢP 1: Đang bật chế độ ưu tiên Hóa Đơn
	    // Nút sẽ đóng vai trò là nút "HUỶ / QUAY LẠI"
	    if (cheDoUuTienHoaDon) {
	        btnApDungKMHD.setText("<html><center style='color:red'>✖ Hủy KM Hóa Đơn<br>(Quay lại KM SP)</center></html>");
	        btnApDungKMHD.setBackground(new Color(0xFFEBEE)); // Màu đỏ nhạt cảnh báo
	        btnApDungKMHD.setForeground(Color.RED);
	        btnApDungKMHD.setVisible(true);
	        return;
	    }

	    // TRƯỜNG HỢP 2: Đang dùng KM SP (Mặc định) -> Tìm kèo thơm
	    KhuyenMai kmCandidate = timKMHoaDonTotNhat(tongTienGoc);
	    
	    if (kmCandidate != null) {
	        double tienGiamDuKien = tinhTienGiam(tongTienGoc, kmCandidate);
	        
	        // Chỉ gợi ý nếu KM Hóa đơn ngon hơn tổng KM SP hiện tại
	        if (tienGiamDuKien > giamSPHienTai) {
	            kmHoaDonGoiY = kmCandidate;
	            
	            // Nút đóng vai trò "GỢI Ý ÁP DỤNG"
	            btnApDungKMHD.setText("<html><center>Dùng " + kmCandidate.getTenKM() + "<br>(Giảm " + formatTienShort((long)tienGiamDuKien) + ")</center></html>");
	            btnApDungKMHD.setBackground(new Color(0xFF9800)); // Màu cam nổi bật
	            btnApDungKMHD.setForeground(Color.WHITE);
	            btnApDungKMHD.setVisible(true);
	        } else {
	            btnApDungKMHD.setVisible(false);
	        }
	    } else {
	        btnApDungKMHD.setVisible(false);
	    }
	}

	// Hàm tìm KM hóa đơn tốt nhất (Helper)
	private KhuyenMai timKMHoaDonTotNhat(double tongTien) {
	    List<KhuyenMai> dsKm = khuyenMaiDao.layKhuyenMaiDangHoatDong();
	    double maxGiam = 0;
	    KhuyenMai kmChon = null;
	    
	    for (KhuyenMai km : dsKm) {
	        if (!km.isKhuyenMaiHoaDon()) continue;
	        if (tongTien < km.getDieuKienApDungHoaDon()) continue;
	        
	        double giam = tinhTienGiam(tongTien, km);
	        if (giam > maxGiam) {
	            maxGiam = giam;
	            kmChon = km;
	        }
	    }
	    return kmChon;
	}

	// Hàm tính tiền giảm (Helper)
	private double tinhTienGiam(double tongTien, KhuyenMai km) {
	    if (km.getHinhThuc() == HinhThucKM.GIAM_GIA_PHAN_TRAM) {
	        return tongTien * (km.getGiaTri() / 100.0);
	    } else {
	        return km.getGiaTri();
	    }
	}

	private void capNhatTienThua() {
		String raw = txtTienKhach.getText().trim();

		raw = raw.replace(".", "").replace(",", "").replace("đ", "").replace("Đ", "").replace("k", "").replace("K", "")
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
		if (tienThua < 0)
			tienThua = 0;

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
	private void xuLyTimKhach(boolean baoLoi) {
		String sdt = txtTimKH.getText().trim();

		// 1. Nếu ô trống hoặc là placeholder -> Về Vãng lai
		if (sdt.isEmpty() || sdt.equals(PLACEHOLDER_TIM_KH)) {
			troVeKhachVangLai();
			return;
		}

		// 2. Kiểm tra định dạng
		if (!sdt.matches("^0\\d{9}$")) {
			if (baoLoi) {
				JOptionPane.showMessageDialog(this, "SĐT không hợp lệ (10 chữ số, bắt đầu bằng 0).", "Lỗi",
						JOptionPane.WARNING_MESSAGE);
				txtTimKH.requestFocus();
			}
			// Nếu không báo lỗi (click ra ngoài), thì cứ để nguyên text đó cho họ sửa, không làm gì cả
			return;
		}

		// 3. Tìm trong DB
		KhachHang kh = khachHangDao.timKhachHangTheoSoDienThoai(sdt);

		if (kh == null) {
			// Không tìm thấy
			if (baoLoi) {
				int choice = JOptionPane.showConfirmDialog(this,
						"Không tìm thấy khách có SĐT: " + sdt + ".\n" + "Bạn muốn giữ khách 'Vãng lai' không?",
						"Không tìm thấy khách", JOptionPane.YES_NO_OPTION);

				if (choice == JOptionPane.YES_OPTION) {
					troVeKhachVangLai();
					// Reset lại ô nhập liệu về placeholder cho đẹp
					txtTimKH.setText(PLACEHOLDER_TIM_KH);
					txtTimKH.setForeground(Color.GRAY); 
				} else {
					// Chọn No -> Giữ nguyên số điện thoại để họ nhập lại hoặc đăng ký mới
					txtTimKH.requestFocus();
				}
			} else {
				// Nếu click ra ngoài mà không thấy khách -> Tự động về Vãng lai (hoặc giữ nguyên tùy logic bạn thích)
				// Ở đây tôi chọn giải pháp an toàn: Giữ nguyên SĐT đó nhưng Tên Khách vẫn là Vãng lai
				// Để họ biết là SĐT này chưa có trong hệ thống.
				khachHangHienTai = null;
				txtTenKhachHang.setText("Vãng lai (SĐT chưa lưu)");
			}
			return;
		}

		// 4. Tìm thấy -> Set khách hàng
		khachHangHienTai = kh;
		txtTenKhachHang.setText(kh.getTenKhachHang());
	}
	
	/** Helper để reset về vãng lai nhanh */
	private void troVeKhachVangLai() {
		khachHangHienTai = null;
		txtTenKhachHang.setText("Vãng lai");
	}
	private double tinhTienGiamHoaDon(double tongSauGiamSP, KhuyenMai km) {
		if (km == null || km.getHinhThuc() == null)
			return 0;

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

		if (giam < 0)
			giam = 0;
		if (giam > tongSauGiamSP)
			giam = tongSauGiamSP;
		return giam;
	}
	private void khoiPhucKMSanPham() {
	    for (ItemDonHang item : dsItem) {
	        // Lấy lại KM từ CSDL dựa vào mã sản phẩm
	        SanPham sp = item.getSanPham();
	        List<ChiTietKhuyenMaiSanPham> dsKMSP = ctKMSPDao.layChiTietKhuyenMaiDangHoatDongTheoMaSP(sp.getMaSanPham());
	        
	        if (!dsKMSP.isEmpty()) {
	            // Tìm thấy KM -> Set lại vào item
	            item.setKhuyenMai(dsKMSP.get(0)); 
	        } else {
	            // Không có KM -> Đảm bảo là null
	            item.setKhuyenMai(null);
	        }
	    }
	}
}