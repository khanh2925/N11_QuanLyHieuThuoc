package gui.dialog;

import dao.LoSanPham_DAO;
import dao.SanPham_DAO;
import entity.LoSanPham;
import entity.SanPham;
import enums.LoaiSanPham;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DialogChonLo extends JDialog {

    private JTextField txtTim;
    private JTable tblLo;
    private DefaultTableModel model;
	private JComboBox<LoaiSanPham> cboLoaiHang;
	private JPanel pnTop;
    private LoSanPham selectedLo = null;
    private ArrayList<LoSanPham> dsLoHSD;

    private final LoSanPham_DAO loDAO = new LoSanPham_DAO();
    private final SanPham_DAO spDAO = new SanPham_DAO();

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String keyword;
    private String loaiTim; // "MASP" , "TENSP" , "HSD"
    

    public DialogChonLo(String keyword, String loaiTim) {
        this.keyword = keyword.trim();
        this.loaiTim = loaiTim;

        setTitle("Chọn lô sản phẩm");
        setModal(true);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI();
        loadInitialData();
    }

    private void initUI() {

        pnTop = new JPanel(new BorderLayout(10, 10));
        pnTop.setBorder(new EmptyBorder(10, 10, 10, 10));

        txtTim = new JTextField(keyword);
        txtTim.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtTim.addActionListener(e -> loc());
        pnTop.add(txtTim, BorderLayout.CENTER);

        /*
         * xử lý theo 2 cách là tìm theo hsd và từ khóa
         */
        
        JButton btnTim = new JButton("Tìm");
        btnTim.addActionListener(e -> {
            if ("HSD".equals(loaiTim)) {
                // Đang ở mode gần hết hạn → dùng combobox loại hàng
                timGanHetHanTheoLoai();
            } else {
                // Các mode khác (MASP, TENSP) dùng logic cũ
                loc();
            }
        });
        pnTop.add(btnTim, BorderLayout.EAST);


        add(pnTop, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"Mã lô", "Tên sản phẩm", "HSD", "Tồn", "Giá nhập"},
                0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblLo = new JTable(model);
        tblLo.setRowHeight(28);
        add(new JScrollPane(tblLo), BorderLayout.CENTER);

        JPanel pnBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnChon = new JButton("Chọn");
        JButton btnDong = new JButton("Đóng");

        btnChon.addActionListener(e -> chonLo());
        btnDong.addActionListener(e -> dispose());

        pnBottom.add(btnChon);
        pnBottom.add(btnDong);

        add(pnBottom, BorderLayout.SOUTH);
    }
    private void ThemComboBox() {
    	// combo loai hàng   	 
    	cboLoaiHang = new JComboBox<>(LoaiSanPham.values());
	    cboLoaiHang.setFont(new Font("Tahoma", Font.PLAIN, 18));
	    cboLoaiHang.setBounds(550, 25, 250, 40);
	    pnTop.remove(txtTim);
    	pnTop.add(cboLoaiHang, BorderLayout.CENTER);
    
	}
    // =====================================================
    // =============== LOAD DỮ LIỆU BAN ĐẦU ================
    // =====================================================

    private void loadInitialData() {
        ArrayList<LoSanPham> dsLo = loDAO.layTatCaLoSanPham(); // CHỈ CÓ HÀM NÀY  :contentReference[oaicite:2]{index=2}
        ArrayList<LoSanPham> ketQua = new ArrayList<>();

        switch (loaiTim) {

            case "MASP" -> {
                ArrayList<SanPham> dsSP = spDAO.timKiemSanPham(keyword); // DAO có hàm này  :contentReference[oaicite:3]{index=3}
                for (SanPham sp : dsSP) {
                    for (LoSanPham lo : dsLo) {
                        if (lo.getSanPham() != null &&
                                lo.getSanPham().getMaSanPham().equals(sp.getMaSanPham()))
                            ketQua.add(taiDayDuSanPham(lo));
                    }
                }
            }

            case "TENSP" -> {
                ArrayList<SanPham> dsSP = spDAO.timKiemSanPham(keyword);
                for (SanPham sp : dsSP) {
                    for (LoSanPham lo : dsLo) {
                        if (lo.getSanPham() != null &&
                                lo.getSanPham().getMaSanPham().equals(sp.getMaSanPham()))
                            ketQua.add(taiDayDuSanPham(lo));
                    }
                }
            }

            case "HSD" -> {
                // Đổi ô nhập text thành combobox loại hàng
                ThemComboBox();
                // Lần đầu mở dialog -> tự load theo loại đang chọn
                timGanHetHanTheoLoai();
            }

        }

        fill(ketQua);
    }

    // =====================================================
    // ========================= LỌC ========================
    // =====================================================

    private void loc() {
        
        if ("HSD".equals(loaiTim)) {
            timGanHetHanTheoLoai();
            return;
        }

        String text = txtTim.getText().trim();
        if (text.isEmpty()) return;

        ArrayList<LoSanPham> dsLo = loDAO.layTatCaLoSanPham();
        ArrayList<LoSanPham> ketQua = new ArrayList<>();

        // 1. Nhập MÃ LÔ
        if (text.matches("^LO-\\d{6}$")) {
            LoSanPham lo = loDAO.timLoTheoMa(text);
            if (lo != null) ketQua.add(taiDayDuSanPham(lo));
            fill(ketQua);
            return;
        }

        // 2. Nhập HSD
        LocalDate hsd = parseDate(text);
        if (hsd != null) {
            for (LoSanPham lo : dsLo) {
                if (lo.getHanSuDung().equals(hsd))
                    ketQua.add(taiDayDuSanPham(lo));
            }
            fill(ketQua);
            return;
        }

        // 3. Tìm theo tên / mã SP
        ArrayList<SanPham> dsSP = spDAO.timKiemSanPham(text);
        for (SanPham sp : dsSP) {
            for (LoSanPham lo : dsLo) {
                if (lo.getSanPham() != null &&
                        lo.getSanPham().getMaSanPham().equals(sp.getMaSanPham()))
                    ketQua.add(taiDayDuSanPham(lo));
            }
        }

        fill(ketQua);
    }


    // =====================================================
    // ====================== CHỌN LÔ ======================
    // =====================================================
    
    private void timGanHetHanTheoLoai() {
        if (cboLoaiHang == null) return;

        LoaiSanPham loai = (LoaiSanPham) cboLoaiHang.getSelectedItem();
        if (loai == null) return;

        ArrayList<LoSanPham> ketQua = new ArrayList<>();

        // Gọi DAO: dùng ngày hiện tại + số ngày cảnh báo
        for (LoSanPham lo : loDAO.timLoGanHetHanTheoLoai(loai)) {
            ketQua.add(taiDayDuSanPham(lo)); // nạp đủ thông tin sản phẩm
        }

        fill(ketQua);
    }
    
    private void chonLo() {
        int row = tblLo.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 lô.");
            return;
        }

        String maLo = model.getValueAt(row, 0).toString();
        selectedLo = loDAO.timLoTheoMa(maLo); // Lấy lại bản đầy đủ
        dispose();
    }

    // =====================================================
    // ==================== HỖ TRỢ =========================
    // =====================================================

    private LoSanPham taiDayDuSanPham(LoSanPham lo) {
        // DAO trả về SanPham chỉ có mã → kéo đầy đủ theo DAO
        if (lo.getSanPham() != null) {
            SanPham sp = spDAO.laySanPhamTheoMa(lo.getSanPham().getMaSanPham());
            lo.setSanPham(sp);
        }
        return lo;
    }

    private void fill(ArrayList<LoSanPham> ds) {
        model.setRowCount(0);

        for (LoSanPham lo : ds) {
            SanPham sp = lo.getSanPham();
            model.addRow(new Object[]{
                    lo.getMaLo(),
                    sp != null ? sp.getTenSanPham() : "N/A",
                    lo.getHanSuDung().format(fmt),
                    lo.getSoLuongTon(),
                    sp != null ? String.format("%,.0f", sp.getGiaNhap()) : "0"
            });
        }
    }

    private LocalDate parseDate(String s) {
        try {
            if (s.matches("^\\d{1,2}/\\d{1,2}/\\d{4}$"))
                return LocalDate.parse(s, fmt);

            if (s.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$"))
                return LocalDate.parse(s);
        } catch (Exception ignored) {}
        return null;
    }

    // =====================================================
    // =============== GETTER TRẢ LÔ CHỌN ==================
    // =====================================================

    public LoSanPham getSelectedLo() {
        return selectedLo;
    }
}
