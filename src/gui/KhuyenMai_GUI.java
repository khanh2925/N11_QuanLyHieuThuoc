
package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import customcomponent.ClipTooltipRenderer;
import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import entity.ChiTietKhuyenMaiSanPham;
import entity.DonViTinh;
import entity.KhuyenMai;
import entity.LoaiSanPham;
import entity.SanPham;
import enums.DuongDung;
import enums.HinhThucKM;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class KhuyenMai_GUI extends JPanel {

	private JPanel pnCenter; // vùng trung tâm
	private JPanel pnHeader; // vùng đầu trang
	private JPanel pnRight; // vùng cột phải
	private PillButton btnThem;
	private PillButton btnCapNhat;
	private DefaultTableModel modelKM;
	private JTable tblKM;
	private JScrollPane scrKM;
	private Color blueMint = new Color(33, 150, 243);
	private Color pinkPastel = new Color(255, 200, 220);

	DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	DecimalFormat df = new DecimalFormat("#,000.#đ");
	private JTextField textField;

	public KhuyenMai_GUI() {
		this.setPreferredSize(new Dimension(1537, 850));
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(1537, 1168));

		// ===== HEADER =====
		pnHeader = new JPanel();
		pnHeader.setPreferredSize(new Dimension(1073, 88));
		pnHeader.setBackground(new Color(0xE3F2F5));
		add(pnHeader, BorderLayout.NORTH);

		btnThem = new PillButton("Thêm");
		btnThem.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnThem.setLocation(374, 25);
		btnThem.setSize(120, 40);

		btnCapNhat = new PillButton("Cập nhật");
		btnCapNhat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnCapNhat.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnCapNhat.setLocation(549, 25);
		btnCapNhat.setSize(120, 40);
		pnHeader.setLayout(null);
		pnHeader.add(btnThem);
		pnHeader.add(btnCapNhat);
		
		textField = new JTextField("");
		textField.setForeground(Color.GRAY);
		textField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		textField.setBorder(new RoundedBorder(20));
		textField.setBounds(10, 17, 336, 60);
		pnHeader.add(textField);
		// ===== CENTER =====
		pnCenter = new JPanel();
		pnCenter.setBackground(new Color(255, 128, 192));
		add(pnCenter, BorderLayout.CENTER);
		initTable();
		LoadKhuyenMai();
	}

	private void initTable() {
		// Bảng khuyến mãi
		String[] khuyenMaiCols = { "Mã khuyến mãi", "Tên khuyến mãi", "Hình thức", "Giá trị", "Ngày bắt đầu",
				"Ngày kết thúc", "Loại khuyến mãi", "Điều kiện áp dụng", "Trạng thái" };
		modelKM = new DefaultTableModel(khuyenMaiCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		pnCenter.setLayout(new BorderLayout(0, 0));
		tblKM = new JTable(modelKM);
		scrKM = new JScrollPane(tblKM);
		pnCenter.add(scrKM);
		formatTable(tblKM);
		tblKM.setSelectionBackground(pinkPastel);
		tblKM.getTableHeader().setBackground(blueMint);

		// Render ảnh trên table
		tblKM.setRowHeight(55); // chiều cao hàng để đủ chỗ cho ảnh
		tblKM.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public void setValue(Object value) {
				if (value instanceof ImageIcon) {
					setIcon((ImageIcon) value);
					setText("");
				} else {
					setIcon(null);
					setText(value == null ? "" : value.toString());
				}
			}
		});

		// Bỏ qua cột Ảnh (index 0). Gắn tooltip cho tất cả các cột còn lại.
		for (int col = 1; col < tblKM.getColumnCount(); col++) {
			tblKM.getColumnModel().getColumn(col).setCellRenderer(new ClipTooltipRenderer());
		}
	}

	private void formatTable(JTable table) {
		table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 18));
		table.getTableHeader().setBorder(null);
		table.getTableHeader().setForeground(Color.WHITE);

		table.setRowHeight(28);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		table.setSelectionBackground(new Color(180, 205, 230));
		table.setShowGrid(false);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment(JLabel.LEFT);

		// Đặt renderer cho từng cột theo tên header
		TableColumnModel m = table.getColumnModel();
		for (int i = 0; i < m.getColumnCount(); i++) {
			String col = m.getColumn(i).getHeaderValue().toString().toLowerCase();

			if (col.contains("mã"))
				m.getColumn(i).setCellRenderer(centerRenderer);
			else if (col.contains("số lượng") || col.contains("sl"))
				m.getColumn(i).setCellRenderer(rightRenderer);
			else if (col.contains("giá") || col.contains("tiền"))
				m.getColumn(i).setCellRenderer(rightRenderer);
			else if (col.contains("ngày") || col.contains("ảnh")) {
				m.getColumn(i).setCellRenderer(centerRenderer);
			} else
				m.getColumn(i).setCellRenderer(leftRenderer);
		}

		// Không cho reorder header
		table.getTableHeader().setReorderingAllowed(false);
	}

	public void LoadKhuyenMai() {

		LoaiSanPham lspKeDon = new LoaiSanPham("LSP001", "Thuốc kê đơn", "Chỉ bán khi có đơn");
		LoaiSanPham lspKhongKeDon = new LoaiSanPham("LSP002", "Thuốc không kê đơn", "Bán tự do");
		LoaiSanPham lspTPCN = new LoaiSanPham("LSP003", "Thực phẩm chức năng", "Hỗ trợ sức khỏe");
		LoaiSanPham lspDungCuYTe = new LoaiSanPham("LSP004", "Dụng cụ y tế", "Dụng cụ hỗ trợ");

		DonViTinh dvtVien = new DonViTinh("DVT-001", "Viên", "Thuốc dạng viên nén hoặc nang");
		DonViTinh dvtHop = new DonViTinh("DVT-002", "Hộp", "Hộp chứa nhiều vỉ thuốc");
		DonViTinh dvtLo = new DonViTinh("DVT-003", "Lọ", "Dung dịch hoặc viên trong lọ");
		DonViTinh dvtTuyp = new DonViTinh("DVT-004", "Tuýp", "Thuốc bôi ngoài da dạng kem/gel");

		List<SanPham> dssp = new ArrayList<>();
		List<KhuyenMai> ds = new ArrayList<>();
		List<ChiTietKhuyenMaiSanPham> dsctkm = new ArrayList<>();
		dssp.add(new SanPham("SP000001", "Paracetamol 500mg", lspKhongKeDon, "SDK-001", "Paracetamol", "500mg",
				"Traphaco", "Việt Nam", dvtVien, DuongDung.UONG, 800, 1200, "paracetamol.png", "Hộp 10 vỉ x 10 viên",
				"Kệ A1", true));

		dssp.add(new SanPham("SP000002", "Amoxicillin 500mg", lspKeDon, "SDK-002", "Amoxicillin", "500mg", "DHG Pharma",
				"Việt Nam", dvtVien, DuongDung.UONG, 1000, 1600, "amoxicillin.png", "Hộp 10 vỉ x 10 viên", "Kệ A2",
				true));

		dssp.add(new SanPham("SP000003", "Cefuroxime 250mg", lspKeDon, "SDK-003", "Cefuroxime", "250mg", "GSK", "Anh",
				dvtVien, DuongDung.UONG, 2500, 4000, "cefuroxime.png", "Hộp 2 vỉ x 10 viên", "Kệ A3", true));

		dssp.add(new SanPham("SP000004", "Vitamin C 1000mg", lspTPCN, "SDK-004", "Ascorbic Acid", "1000mg",
				"Mega We Care", "Thái Lan", dvtVien, DuongDung.UONG, 900, 1500, "vitC.png", "Hộp 10 vỉ x 10 viên",
				"Kệ B1", true));

		dssp.add(new SanPham("SP000005", "Oresol", lspTPCN, "SDK-005", "Glucose, NaCl, KCl", "5g/gói", "Imexpharm",
				"Việt Nam", dvtHop, DuongDung.UONG, 2500, 4000, "oresol.png", "Hộp 10 gói", "Kệ B2", true));

		dssp.add(new SanPham("SP000006", "Betadine 10%", lspDungCuYTe, "SDK-006", "Povidone Iodine", "10%",
				"Mundipharma", "Singapore", dvtLo, DuongDung.BOI, 15000, 25000, "betadine.png", "Lọ 30ml", "Kệ C1",
				true));

		dssp.add(new SanPham("SP000007", "Panadol Extra", lspKhongKeDon, "SDK-007", "Paracetamol, Caffeine",
				"500mg/65mg", "GSK", "Anh", dvtVien, DuongDung.UONG, 1200, 2500, "panadol.png", "Hộp 10 vỉ x 10 viên",
				"Kệ A1", true));

		dssp.add(new SanPham("SP000008", "Salonpas", lspDungCuYTe, "SDK-008", "Methyl Salicylate, Menthol", "20mg/3mg",
				"Hisamitsu", "Nhật Bản", dvtHop, DuongDung.BOI, 10000, 15000, "salonpas.png", "Hộp 12 miếng", "Kệ C2",
				true));

		dssp.add(new SanPham("SP000009", "Omeprazole 20mg", lspKeDon, "SDK-009", "Omeprazole", "20mg", "Stada", "Đức",
				dvtVien, DuongDung.UONG, 1800, 2800, "omeprazole.png", "Hộp 2 vỉ x 14 viên", "Kệ D1", true));

		dssp.add(new SanPham("SP000010", "Diclofenac 75mg", lspKeDon, "SDK-010", "Diclofenac Sodium", "75mg", "Domesco",
				"Việt Nam", dvtHop, DuongDung.TIEM, 2000, 3000, "diclofenac.png", "Hộp 10 ống", "Kệ D2", true));

		dssp.add(new SanPham("SP000011", "Tetracycline 250mg", lspKeDon, "SDK-011", "Tetracycline", "250mg",
				"Dược Hà Tây", "Việt Nam", dvtVien, DuongDung.UONG, 600, 1000, "tetracycline.png",
				"Hộp 10 vỉ x 10 viên", "Kệ D3", true));

		dssp.add(new SanPham("SP000012", "Nước muối sinh lý 0.9%", lspDungCuYTe, "SDK-012", "NaCl", "0.9%", "Bidiphar",
				"Việt Nam", dvtLo, DuongDung.NHO, 2000, 4000, "nuocmuoi.png", "Lọ 500ml", "Kệ C3", true));

		dssp.add(new SanPham("SP000013", "Canxi D3 500mg", lspTPCN, "SDK-013", "Calcium carbonate + Vitamin D3",
				"500mg/200IU", "Imexpharm", "Việt Nam", dvtVien, DuongDung.UONG, 800, 1800, "calcid3.png",
				"Hộp 10 vỉ x 10 viên", "Kệ B3", true));

		dssp.add(new SanPham("SP000014", "Efferalgan 150mg", lspKhongKeDon, "SDK-014", "Paracetamol", "150mg", "UPSA",
				"Pháp", dvtHop, DuongDung.UONG, 700, 1400, "efferalgan.png", "Hộp 10 gói", "Kệ A2", true));

		dssp.add(new SanPham("SP000015", "Smecta", lspKhongKeDon, "SDK-015", "Diosmectite", "3g/gói", "Ipsen", "Pháp",
				dvtHop, DuongDung.UONG, 4000, 7000, "smecta.png", "Hộp 10 gói", "Kệ A3", true));

		dssp.add(new SanPham("SP000016", "Clorpheniramin 4mg", lspKeDon, "SDK-016", "Chlorpheniramine maleate", "4mg",
				"Mediplantex", "Việt Nam", dvtVien, DuongDung.UONG, 400, 900, "clorpheniramin.png",
				"Hộp 10 vỉ x 10 viên", "Kệ D4", true));

		dssp.add(new SanPham("SP000017", "Decolgen", lspKhongKeDon, "SDK-017",
				"Paracetamol, Pseudoephedrine, Chlorpheniramine", "500mg/30mg/2mg", "United Pharma", "Việt Nam",
				dvtVien, DuongDung.UONG, 800, 1600, "decolgen.png", "Hộp 10 vỉ x 10 viên", "Kệ A4", true));

		dssp.add(new SanPham("SP000018", "Albendazole 400mg", lspKhongKeDon, "SDK-018", "Albendazole", "400mg",
				"Dược Hà Nội", "Việt Nam", dvtVien, DuongDung.UONG, 500, 1200, "albendazole.png", "Hộp 2 viên", "Kệ A5",
				true));

		dssp.add(new SanPham("SP000019", "Erythromycin 250mg", lspKeDon, "SDK-019", "Erythromycin", "250mg",
				"Pymepharco", "Việt Nam", dvtVien, DuongDung.UONG, 900, 2000, "erythromycin.png", "Hộp 10 vỉ x 10 viên",
				"Kệ D5", true));

		dssp.add(new SanPham("SP000020", "Acyclovir cream 5%", lspKeDon, "SDK-020", "Acyclovir", "5%", "Stella",
				"Việt Nam", dvtTuyp, DuongDung.BOI, 6000, 12000, "acyclovir.png", "Tuýp 5g", "Kệ C4", true));

		// giả lập danh sách khuyến mãi

		// KM theo % trên HÓA ĐƠN
		ds.add(new KhuyenMai("KM-20251022-0001", "Giảm 10% toàn bộ hóa đơn", LocalDate.of(2025, 10, 22),
				LocalDate.of(2025, 11, 30), true, /* đang áp dụng */
				true, /* khuyến mãi hóa đơn */
				HinhThucKM.GIAM_GIA_PHAN_TRAM, 10.0, "Áp dụng mọi đơn hàng", 0, 0));

		ds.add(new KhuyenMai("KM-20251101-0002", "Giảm 5% cho đơn từ 300.000đ", LocalDate.of(2025, 11, 1),
				LocalDate.of(2025, 11, 30), true, true, HinhThucKM.GIAM_GIA_PHAN_TRAM, 5.0, "Đơn hàng >= 300000đ", 0,
				0));

		// KM GIẢM TIỀN mặt trên hóa đơn
		ds.add(new KhuyenMai("KM-20251001-0003", "Giảm 20.000đ cho đơn từ 200.000đ", LocalDate.of(2025, 10, 1),
				LocalDate.of(2025, 10, 31), false, true, /* đã hết hạn, ví dụ */
				HinhThucKM.GIAM_GIA_TIEN, 20000, "Đơn hàng >= 200000đ", 0, 0));

		// KM theo % NHÓM HÀNG (không phải hóa đơn)
		ds.add(new KhuyenMai("KM-20251201-0004", "Giảm 15% nhóm Vitamin", LocalDate.of(2025, 12, 1),
				LocalDate.of(2025, 12, 15), true, false, HinhThucKM.GIAM_GIA_PHAN_TRAM, 15.0,
				"Chỉ áp dụng nhóm Vitamin/TP chức năng", 0, 0));

		// KM TẶNG THÊM (MUA N TẶNG M) — dùng soLuongToiThieu & soLuongTangThem
		ds.add(new KhuyenMai("KM-20251025-0005", "Mua 2 tặng 1 – Paracetamol 500mg", LocalDate.of(2025, 10, 25),
				LocalDate.of(2025, 11, 25), true, false, HinhThucKM.TANG_THEM, 0, "Áp dụng riêng Paracetamol 500mg", 2,
				1));

		dsctkm.add(new ChiTietKhuyenMaiSanPham(
				dssp.stream().filter(sp -> sp.getMaSanPham().equals("SP000001")).findFirst().orElse(null),
				ds.stream().filter(km -> km.getMaKM().equals("KM-20251025-0005")).findFirst().orElse(null)));

		ds.add(new KhuyenMai("KM-20251105-0006", "Mua 5 tặng 2 – Salonpas hộp", LocalDate.of(2025, 11, 5),
				LocalDate.of(2025, 11, 20), true, false, HinhThucKM.GIAM_GIA_PHAN_TRAM, 0, "Áp dụng khi mua hộp Salonpas", 5,
				2));

		dsctkm.add(new ChiTietKhuyenMaiSanPham(
				dssp.stream().filter(sp -> sp.getMaSanPham().equals("SP000008")).findFirst().orElse(null),
				ds.stream().filter(km -> km.getMaKM().equals("KM-20251105-0006")).findFirst().orElse(null)));

		// KM GIẢM TIỀN theo sản phẩm
		ds.add(new KhuyenMai("KM-20251020-0007", "Giảm 5.000đ – Nước muối 0.9%", LocalDate.of(2025, 10, 20),
				LocalDate.of(2025, 11, 10), true, false, HinhThucKM.GIAM_GIA_TIEN, 5000,
				"Áp dụng Nước muối 0.9% chai 500ml", 0, 0));

		dsctkm.add(new ChiTietKhuyenMaiSanPham(
				dssp.stream().filter(sp -> sp.getMaSanPham().equals("SP000012")).findFirst().orElse(null),
				ds.stream().filter(km -> km.getMaKM().equals("KM-20251020-0007")).findFirst().orElse(null)));

		// Một số KM test cho nhiều trạng thái
		ds.add(new KhuyenMai("KM-20250901-0008", "Giảm 7% cho nhóm giảm đau", LocalDate.of(2025, 9, 1),
				LocalDate.of(2025, 9, 30), false, false, HinhThucKM.GIAM_GIA_PHAN_TRAM, 7.0, "Hết hạn", 0, 0));

		ds.add(new KhuyenMai("KM-20251210-0009", "Giảm 30.000đ đơn từ 500.000đ", LocalDate.of(2025, 12, 10),
				LocalDate.of(2026, 1, 10), true, true, HinhThucKM.GIAM_GIA_TIEN, 30000, "Đơn hàng >= 500000đ", 0, 0));

		for (KhuyenMai km : ds) {
			String giaTri = "";
			String hinhThuc = "";
			if (km.getHinhThuc() == HinhThucKM.GIAM_GIA_PHAN_TRAM) {
				hinhThuc = "Giảm giá phần trăm";
				giaTri = km.getGiaTri() + "%";
			} else if (km.getHinhThuc() == HinhThucKM.GIAM_GIA_TIEN) {
				hinhThuc = "Giảm giá tiền";
				giaTri = df.format(km.getGiaTri());
			} else {
				hinhThuc = "Tặng thêm";
				giaTri = "Mua " + km.getSoLuongToiThieu() + " tặng " + km.getSoLuongTangThem();
			}
			modelKM.addRow(
					new Object[] { km.getMaKM(), km.getTenKM(), hinhThuc, giaTri, km.getNgayBatDau().format(fmt),
							km.getNgayKetThuc().format(fmt), km.isKhuyenMaiHoaDon() ? "Khuyến mãi hoá đơn" : "Khuyến mãi sản phẩm",
							km.getDieuKienApDungHoaDon(), km.isTrangThai() ? "Đang áp dụng" : "Hết hạn" });
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Khung trống - clone base");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1280, 800);
			frame.setLocationRelativeTo(null);
			frame.setContentPane(new KhuyenMai_GUI());
			frame.setVisible(true);
		});
	}
}
