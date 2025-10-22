/**
 * @author Thanh Kha
 * @version 1.0
 * @since Oct 16, 2025
 *
 * Mô tả: Giao diện quản lý sản phẩm
 */

package gui;

import entity.*;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.jfree.chart.block.CenterArrangement;

import customcomponent.ClipTooltipRenderer;
import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;

import java.net.URL;
import java.text.DecimalFormat;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;

import java.util.ArrayList;
import java.util.List;

public class SanPham_GUI extends JPanel {

	private JPanel pnCenter; // vùng trung tâm
	private JPanel pnHeader; // vùng đầu trang
	private DefaultTableModel modelSP;
	private JTable tblSP;
	private JScrollPane scrSP;

	DecimalFormat df = new DecimalFormat("#,000.#đ");

	private Color blueMint = new Color(180, 220, 240);
	private Color pinkPastel = new Color(255, 200, 220);
	private JTextField txtSearch;
	private PillButton btnThem;
	private PillButton btnCapNhat;
	private JComboBox<String> cboLoaiHang;
	private JPanel pnLoc;

	private LoaiSanPham lspKeDon;
	private LoaiSanPham lspKhongKeDon;
	private LoaiSanPham lspTPCN;
	private LoaiSanPham lspDungCuYTe;

	private DonViTinh dvtVien;
	private DonViTinh dvtHop;
	private DonViTinh dvtLo;
	private DonViTinh dvtTuyp;
	private List<SanPham> dssp;

	public SanPham_GUI() {
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(1537, 1168));

		UIManager.put("ComboBox.background", Color.WHITE);
		UIManager.put("ComboBox.selectionBackground", blueMint);
		UIManager.put("ComboBox.selectionForeground", Color.BLACK);
		UIManager.put("ComboBox.foreground", Color.BLACK);
		UIManager.put("ComboBox.disabledBackground", Color.WHITE);
		
		// ===== HEADER =====
		pnHeader = new JPanel();
		pnHeader.setPreferredSize(new Dimension(1073, 88));
		pnHeader.setBackground(new Color(0xE3F2F5));
		pnHeader.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
		add(pnHeader, BorderLayout.NORTH);

		txtSearch = new JTextField("");
		PlaceholderSupport.addPlaceholder(txtSearch, "Tìm kiếm sản phẩm");
		txtSearch.setForeground(Color.GRAY);
		txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txtSearch.setPreferredSize(new Dimension(420, 44));
		txtSearch.setBorder(new RoundedBorder(20));

		pnLoc = new JPanel();
		pnLoc.setBorder(BorderFactory.createTitledBorder(new RoundedBorder(20), "Lọc và sắp xếp"));
		pnLoc.setBackground(new Color(0, 0, 0, 0)); // Màu nền trong suốt
		pnLoc.setPreferredSize(new Dimension(560, 66));
		pnLoc.setLayout(null);

		cboLoaiHang = new JComboBox<String>();
		cboLoaiHang.setLocation(20, 18);
		cboLoaiHang.setSize(150, 30);
		cboLoaiHang.setBackground(Color.white);
		
		pnLoc.add(cboLoaiHang);

		btnThem = new PillButton("Thêm");
		btnThem.setSize(100, 30);

		btnCapNhat = new PillButton("Cập nhật");
		btnThem.setSize(100, 30);
		
		pnHeader.add(txtSearch);
		pnHeader.add(pnLoc);
		pnHeader.add(btnThem);
		pnHeader.add(btnCapNhat);
		// ===== CENTER =====
		pnCenter = new JPanel();
		pnCenter.setLayout(new BorderLayout());
		add(pnCenter, BorderLayout.CENTER);
		initTable();
		KhoiTaoEntityMau();
		LoadSanPham();
		
		LoadCboLoaiSanPham();
	}

	private void initTable() {
		// Bảng phiếu huỷ
		String[] phieuHuyCols = { "Hình ảnh", "Mã sản phẩm", "Tên sản phẩm", "Loại sản phẩm", "Số đăng ký", "Hoạt chất",
				"Hàm lượng", "Hãng sản xuất", "Xuất xứ", "Đơn vị tính", "Đường dùng", "Giá nhập", "Giá bán",
				"Quy cách đóng gói", "Kệ bán", "Trạng thái" };

		modelSP = new DefaultTableModel(phieuHuyCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblSP = new JTable(modelSP);
		scrSP = new JScrollPane(tblSP);
		pnCenter.add(scrSP);
		formatTable(tblSP);
		tblSP.setSelectionBackground(pinkPastel);
		tblSP.getTableHeader().setBackground(blueMint);

		// Render ảnh trên table
		tblSP.setRowHeight(55); // chiều cao hàng để đủ chỗ cho ảnh
		tblSP.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
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
		for (int col = 1; col < tblSP.getColumnCount(); col++) {
		    tblSP.getColumnModel().getColumn(col).setCellRenderer(new ClipTooltipRenderer());
		}
	}

	private void formatTable(JTable table) {
		table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
		table.getTableHeader().setBorder(null);

		table.setRowHeight(28);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
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
			}
			else
				m.getColumn(i).setCellRenderer(leftRenderer);
		}

		// Không cho reorder header
		table.getTableHeader().setReorderingAllowed(false);
	}

	private void KhoiTaoEntityMau() {
		lspKeDon = new LoaiSanPham("LSP001", "Thuốc kê đơn", "Chỉ bán khi có đơn");
		lspKhongKeDon = new LoaiSanPham("LSP002", "Thuốc không kê đơn", "Bán tự do");
		lspTPCN = new LoaiSanPham("LSP003", "Thực phẩm chức năng", "Hỗ trợ sức khỏe");
		lspDungCuYTe = new LoaiSanPham("LSP004", "Dụng cụ y tế", "Dụng cụ hỗ trợ");

		dvtVien = new DonViTinh("DVT-001", "Viên", "Thuốc dạng viên nén hoặc nang");
		dvtHop = new DonViTinh("DVT-002", "Hộp", "Hộp chứa nhiều vỉ thuốc");
		dvtLo = new DonViTinh("DVT-003", "Lọ", "Dung dịch hoặc viên trong lọ");
		dvtTuyp = new DonViTinh("DVT-004", "Tuýp", "Thuốc bôi ngoài da dạng kem/gel");
	}

	private void LoadSanPham() {
		// ====== 2. Tạo danh sách sản phẩm ======
		dssp = new ArrayList<>();

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

		dssp.forEach(sp -> {
			String imagePath = "/images/" + sp.getHinhAnh();
			ImageIcon icon = null;

			URL url = getClass().getResource(imagePath);

			if (url == null) {
				String errorImagePath = "/images/icon_anh_sp_null.png";
				url = getClass().getResource(errorImagePath);
				if (url == null) {
					System.err.println("Không tìm thấy ảnh tại" + imagePath + ", ảnh thay thế bị lỗi!");
				} else {
					System.err.println("Không tìm thấy ảnh tại" + imagePath + ", dùng " + errorImagePath + " thay thế");
				}
			}

			icon = new ImageIcon(url);
			// Scale kích thước icon
			Image img = icon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
			icon = new ImageIcon(img);

			modelSP.addRow(new Object[] { icon, sp.getMaSanPham(), sp.getTenSanPham(),
					sp.getLoaiSanPham().getTenLoaiSanPham(), sp.getSoDangKy(), sp.getHoatChat(), sp.getHamLuong(),
					sp.getHangSanXuat(), sp.getXuatXu(), sp.getDonViTinh().getTenDonViTinh(), sp.getDuongDung(),
					df.format(sp.getGiaNhap()), df.format(sp.getGiaBan()), sp.getQuyCachDongGoi(), sp.getKeBanSanPham(),
					sp.isHoatDong() ? "Đang kinh doanh" : "Ngừng kinh doanh" });
		});
	}

	private void LoadCboLoaiSanPham() {
		cboLoaiHang.addItem("Chọn loại sản phẩm");
		cboLoaiHang.addItem(lspKeDon.getTenLoaiSanPham());
		cboLoaiHang.addItem(lspKhongKeDon.getTenLoaiSanPham());
		cboLoaiHang.addItem(lspDungCuYTe.getTenLoaiSanPham());
		cboLoaiHang.addItem(lspTPCN.getTenLoaiSanPham());
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Khung trống - clone base");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1280, 800);
			frame.setLocationRelativeTo(null);
			frame.setContentPane(new SanPham_GUI());
			frame.setVisible(true);
		});
	}
}
