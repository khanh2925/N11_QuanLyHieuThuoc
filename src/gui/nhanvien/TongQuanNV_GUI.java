/**

 *
 * Mô tả: Dashboard tổng quan cho nghiệp vụ quản lý hiệu thuốc
 * - 6 KPI Cards (2 hàng x 3 cột):
 *   + Lợi nhuận của ngày hiện tại
 *   + Số đơn hàng trong ngày hiện tại
 *   + Số SP đã bán hiện tại
 *   + Số lô sản phẩm sắt hết hạn
 *   + Số Phiếu hủy đã tạo
 *   + Số Phiếu trả đã tạo
 * - Biểu đồ đường hiển thị số lô SP tới hạn cần hủy
 * - Tích hợp DAO để lấy dữ liệu thực từ database
 */
package gui.nhanvien;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Map;

import component.border.RoundedBorder;
import component.chart.BieuDoCotJFreeChart;
import component.chart.DuLieuBieuDoCot;
import dao.ChiTietHoaDon_DAO;
import dao.HoaDon_DAO;
import dao.LoSanPham_DAO;
import dao.ThongKe_DAO;
import dao.ThongKe_DAO.ThongKeHoaDonNgay;
import entity.Session;
import enums.LoaiSanPham;
import dao.PhieuTra_DAO;
import dao.PhieuHuy_DAO;

public class TongQuanNV_GUI extends JPanel implements ActionListener, MouseListener {

	private JPanel pnCenter;
	private JPanel pnHeader;

	// KPIs Top Row (2 hàng x 3 cột = 5 thẻ + 1 ô trống)
	private JTextField txtSoDon;
	private JTextField txtTongTienDaBan;
	private JTextField txtSPDaBan;
	private JTextField txtLoSPSapHetHan;
	private JTextField txtSoPhieuHuyDaTao;
	private JTextField txtSoPhieuTraDaTao;

	// Panel chứa Biểu đồ
	private JPanel pnChartArea;
	private BieuDoCotJFreeChart bieuDoCot;

	// DAOs
	private HoaDon_DAO hoaDonDAO;
	private ThongKe_DAO thongKeDAO;
	private PhieuTra_DAO phieuTraDAO;
	private PhieuHuy_DAO phieuHuyDAO;
	private ChiTietHoaDon_DAO chiTietHoaDonDAO;
	private LoSanPham_DAO loSanPhamDao;

	// Main GUI reference để chuyển card
	private gui.Main_GUI mainGUI;

	private Color mauChu = new Color(0, 51, 102);
	private String maNV = Session.getInstance().getTaiKhoanDangNhap().getNhanVien().getMaNhanVien();
	// Formatter
	private DecimalFormat formatter = new DecimalFormat("#,###");

	/**
	 * Constructor với Main_GUI reference để hỗ trợ chuyển tab
	 * 
	 * @param mainGUI Tham chiếu đến Main_GUI
	 */
	public TongQuanNV_GUI(gui.Main_GUI mainGUI) {
		this.mainGUI = mainGUI;
		this.setPreferredSize(new Dimension(1537, 850));

		// Khởi tạo DAOs
		hoaDonDAO = new HoaDon_DAO();
		thongKeDAO = new ThongKe_DAO();
		phieuTraDAO = new PhieuTra_DAO();
		phieuHuyDAO = new PhieuHuy_DAO();
		chiTietHoaDonDAO = new ChiTietHoaDon_DAO();
		loSanPhamDao = new LoSanPham_DAO();

		initialize();
		// Load dữ liệu thật từ database
		loadRealData();
		loadChartLoaiHSD();
	}

	/**
	 * Constructor mặc định (dùng cho test)
	 */
	public TongQuanNV_GUI() {
		this(null);
	}

	
	private void initialize() {
		setLayout(new BorderLayout());

		// ===== HEADER (NORTH) =====
		pnHeader = new JPanel();
		pnHeader.setPreferredSize(new Dimension(1073, 80));
		pnHeader.setBackground(new Color(227, 242, 245));
		add(pnHeader, BorderLayout.NORTH);
		pnHeader.setLayout(new BorderLayout(0, 0));

		JLabel lblTongQuan = new JLabel("Tổng Quan");
		lblTongQuan.setHorizontalAlignment(SwingConstants.CENTER);
		lblTongQuan.setForeground(new Color(0, 51, 102));
		lblTongQuan.setFont(new Font("Segoe UI", Font.BOLD, 36));
		pnHeader.add(lblTongQuan);
		
		pnCenter = new JPanel();
		pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));
		pnCenter.setBackground(Color.WHITE);
		add(pnCenter, BorderLayout.CENTER);
		pnCenter.setLayout(new BoxLayout(pnCenter, BoxLayout.Y_AXIS));
		
		createKPIpn();
		createChart();
	}

	// --- KHU VỰC KPI ---
	private void createKPIpn() {

		JPanel pnlKPIs = new JPanel(new GridLayout(2, 3, 15, 15)); // 2 hàng, 3 cột
		pnlKPIs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));
		pnlKPIs.setOpaque(false);

		// Hàng 1
		// Thẻ 1: Lợi nhuận tháng này
		txtSoDon = new JTextField();
		pnlKPIs.add(taoTheKPI("Số đơn hôm nay", mauChu, txtSoDon, null, null));

		// Thẻ 2: Lợi nhuận tháng trước
		txtTongTienDaBan = new JTextField();
		pnlKPIs.add(taoTheKPI("Tổng tiền hóa đơn", mauChu, txtTongTienDaBan, null, null));

		// Thẻ 3: Tổng tiền nhập hàng tháng này
		txtSPDaBan = new JTextField();
		pnlKPIs.add(taoTheKPI("Số SP đã bán hôm nay", mauChu, txtSPDaBan, null, null));

		// Hàng 2
		// Thẻ 4: Khách hàng mới
		txtLoSPSapHetHan = new JTextField();
		pnlKPIs.add(taoTheKPI("Lô SP sắp hết hạn", mauChu, txtLoSPSapHetHan, null, null));

		// Thẻ 5: Đơn trả cần duyệt (có thể click) - Border đỏ
		txtSoPhieuHuyDaTao = new JTextField();
		pnlKPIs.add(taoTheKPI("Số phiếu hủy đã tạo hôm nay", mauChu, txtSoPhieuHuyDaTao, null, null));

		// Thẻ 6: Đơn hủy cần duyệt (có thể click) - Border đỏ
		txtSoPhieuTraDaTao = new JTextField();
		pnlKPIs.add(taoTheKPI("Số phiếu trả đã tạo hôm nay", mauChu, txtSoPhieuTraDaTao, null, null));

		pnCenter.add(pnlKPIs);
		pnCenter.add(Box.createVerticalStrut(40));
	}
	
	/**
	 * Phương thức helper tạo thẻ KPI - Chức năng: chỉ thiết lập giao diện cho
	 * JTextField đã được tạo.
	 */
	private JPanel taoTheKPI(String title, Color mauChu, JTextField textField, String actionType, Color mauBorder) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(new Color(227, 242, 245));
		panel.setBorder(new RoundedBorder(15));
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.setPreferredSize(new Dimension(100, 100));

		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		lblTitle.setForeground(mauChu);
		lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblTitle.setBorder(new EmptyBorder(10, 10, 0, 10));

		// Setup JTextField đã được truyền vào
		textField.setColumns(10);
		textField.setOpaque(false);
		textField.setBorder(null);
		textField.setForeground(mauChu);
		textField.setFont(new Font("Segoe UI", Font.BOLD, 40));
		textField.setEditable(false);
		textField.setAlignmentX(Component.LEFT_ALIGNMENT);
		textField.setBorder(new EmptyBorder(5, 10, 10, 10));

		panel.add(lblTitle);
		panel.add(textField);

		return panel;
	}
	
	// ===== Biểu đồ =====
	private void createChart() {		


		pnChartArea = new JPanel();
		pnChartArea.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 51, 102)),
				"Biểu đồ số lượng lô SP gần hết hạn", TitledBorder.LEFT, TitledBorder.TOP,
				new Font("Segoe UI", Font.BOLD, 20), mauChu));
		pnChartArea.setBackground(Color.WHITE);
		pnChartArea.setLayout(new BorderLayout());
		pnChartArea.setPreferredSize(new Dimension(0, 500));
		pnChartArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));

		bieuDoCot = new BieuDoCotJFreeChart();
		bieuDoCot.setTieuDeBieuDo("Số lô cần hủy theo hạn sử dụng");
		bieuDoCot.setTieuDeTrucX("Loại sản phẩm");
		bieuDoCot.setTieuDeTrucY("Số lô");
		
		pnChartArea.removeAll();
		pnChartArea.setLayout(new BorderLayout());
		pnChartArea.add(bieuDoCot, BorderLayout.CENTER);
		pnCenter.add(pnChartArea);
		pnCenter.add(Box.createVerticalStrut(10));

		pnChartArea.revalidate();
		pnChartArea.repaint();
	}

	// biểu đố hiện thị số lô tới hạn cần hủy theo loaisanpham
	private void loadChartLoaiHSD() {
		bieuDoCot.xoaToanBoDuLieu();

		Map<LoaiSanPham, Integer> data = loSanPhamDao.thongKeSoLoCanHuyTheoHSDTheoLoai();

		int max = 0;

		for (var entry : data.entrySet()) {
			LoaiSanPham loai = entry.getKey();
			int soLo = entry.getValue();
			max = Math.max(max, soLo);

			Color mau;
			if (soLo >= 10)
				mau = new Color(220, 53, 69);
			else if (soLo >= 5)
				mau = new Color(255, 193, 7);
			else
				mau = new Color(25, 135, 84);

			bieuDoCot.themDuLieu(new DuLieuBieuDoCot(loai.getTenLoai(), "Cần hủy theo HSD", soLo, mau));
		}

		bieuDoCot.setDaiTrucY(0, max + 3);
	}
	
	/**
	 * Load dữ liệu thật từ database
	 */
	private void loadRealData() {

		try {

			ThongKeHoaDonNgay tk = thongKeDAO.thongKeHoaDonHomNayCuaNhanVien(maNV);

			int soHoaDon = tk.getSoHoaDon();
			double tongTienBanHang = tk.getTongTien();
			// 1. số đơn hôm nay
			txtSoDon.setText(String.valueOf(soHoaDon));
			
			// 2. Lợi nhuận tháng trước
			txtTongTienDaBan.setText(formatter.format(tongTienBanHang) + " ₫");

			// 3. số SP đã bán trong hôm nay
			int soSPDaBan = chiTietHoaDonDAO.demSoSanPhamBanHomNay(maNV);
			txtSPDaBan.setText(String.valueOf(soSPDaBan));

			// 4. số lô sp gần hết hạn
			int soLoToiHanSD = loSanPhamDao.layDanhSachLoSPToiHanSuDung().size();
			txtLoSPSapHetHan.setText(String.valueOf(soLoToiHanSD));

			int soPhieuHuy = phieuHuyDAO.demSoPhieuHuyHomNayCuaNhanVien(maNV);
			txtSoPhieuHuyDaTao.setText(String.valueOf(soPhieuHuy));

			// 6. Phiếu hủy chưa duyệt
			int soPhieuTra = phieuTraDAO.demSoPhieuTraHomNayCuaNhanVien(maNV);
			txtSoPhieuTraDaTao.setText(String.valueOf(soPhieuTra));

		} catch (Exception e) {
			System.err.println("❌ Lỗi load dữ liệu dashboard: " + e.getMessage());
			e.printStackTrace();
		}
	}

	

	/**
	 * Refresh lại toàn bộ dữ liệu dashboard Gọi method này khi cần cập nhật dữ liệu
	 * (VD: sau khi thêm hóa đơn mới)
	 */
	public void refreshDashboard() {

		// Load lại dữ liệu mới
		loadRealData();
		loadChartLoaiHSD();
	}

	// --- MAIN VÀ INTERFACE METHODS ---
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Dashboard Tổng Quan NV");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1280, 850);
			frame.setLocationRelativeTo(null);
			frame.setContentPane(new TongQuanNV_GUI());
			frame.setVisible(true);
		});
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}
}