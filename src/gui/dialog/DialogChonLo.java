package gui.dialog;

import dao.LoSanPham_DAO;
import dao.SanPham_DAO;
import entity.LoSanPham;
import entity.SanPham;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import component.input.PlaceholderSupport;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
//Class này dùng cho chọn lô trong huỷ hàng nhân viên
public class DialogChonLo extends JDialog {

    private JTextField txtTim;
    private JTable tblLo;
    private DefaultTableModel model;
	private JPanel pnTop;
    private LoSanPham selectedLo = null;
    private ArrayList<LoSanPham> dsLoHSD;
    private ArrayList<LoSanPham> currentDanhSach = new ArrayList<>(); // Lưu danh sách hiện tại
    private boolean selectedAll = false; // Flag cho chọn tất cả

    private final LoSanPham_DAO loDAO = new LoSanPham_DAO();
    private final SanPham_DAO spDAO = new SanPham_DAO();

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String keyword;
    private String loaiTim; // "MASP" , "TENSP" 
    

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
        PlaceholderSupport.addPlaceholder(txtTim, "Tìm theo mã lô, mã sản phẩm hoặc tên sản phẩm...");
        txtTim.addActionListener(e -> loc());
        pnTop.add(txtTim, BorderLayout.CENTER);

        /*
         * xử lý theo 2 cách là tìm theo hsd và từ khóa
         */
        
        JButton btnTim = new JButton("Tìm");
        btnTim.addActionListener(e -> loc());
        pnTop.add(btnTim, BorderLayout.EAST);


        add(pnTop, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"Mã lô", "Tên sản phẩm", "HSD", "Còn lại", "Tồn", "Giá nhập"},
                0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblLo = new JTable(model);
        tblLo.setRowHeight(28);
        tblLo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Tùy chỉnh độ rộng cột
        tblLo.getColumnModel().getColumn(0).setPreferredWidth(100); // Mã lô
        tblLo.getColumnModel().getColumn(1).setPreferredWidth(250); // Tên SP
        tblLo.getColumnModel().getColumn(2).setPreferredWidth(100); // HSD
        tblLo.getColumnModel().getColumn(3).setPreferredWidth(120); // Còn lại
        tblLo.getColumnModel().getColumn(4).setPreferredWidth(60);  // Tồn
        tblLo.getColumnModel().getColumn(5).setPreferredWidth(100); // Giá
        
        add(new JScrollPane(tblLo), BorderLayout.CENTER);

        JPanel pnBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnChon = new JButton("Chọn");
        JButton btnHuyTatCa = new JButton("Huỷ tất cả");
        JButton btnDong = new JButton("Đóng");

        btnChon.addActionListener(e -> chonLo());
        btnHuyTatCa.addActionListener(e -> huyTatCa());
        btnDong.addActionListener(e -> dispose());
        
        // Chỉ hiển thị nút "Huỷ tất cả" khi đang ở mode HSD
        if ("HSD".equals(loaiTim)) {
            pnBottom.add(btnHuyTatCa);
        }
        pnBottom.add(btnChon);
        pnBottom.add(btnDong);

        add(pnBottom, BorderLayout.SOUTH);
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
                // Load tất cả lô đã hết hạn
                dsLoHSD = new ArrayList<>(loDAO.layDanhSachLoSPDaHetHan());
                for (LoSanPham lo : dsLoHSD) {
                    ketQua.add(taiDayDuSanPham(lo));
                }
            }

        }

        fill(ketQua);
    }

    // =====================================================
    // ========================= LỌC ========================
    // =====================================================

    private void loc() {
        String text = txtTim.getText().trim();

        // Nếu mode HSD, lọc trong danh sách lô đã hết hạn
        if ("HSD".equals(loaiTim)) {
            if (text.isEmpty()) {
                // Nếu không nhập gì, hiển thị tất cả lô hết hạn
                ArrayList<LoSanPham> ketQua = new ArrayList<>();
                for (LoSanPham lo : dsLoHSD) {
                    ketQua.add(taiDayDuSanPham(lo));
                }
                fill(ketQua);
            } else {
                // Lọc theo keyword trong danh sách lô hết hạn
                ArrayList<LoSanPham> ketQua = new ArrayList<>();
                String lowerText = text.toLowerCase();
                for (LoSanPham lo : dsLoHSD) {
                    SanPham sp = lo.getSanPham();
                    boolean match = lo.getMaLo().toLowerCase().contains(lowerText)
                            || (sp != null && sp.getMaSanPham().toLowerCase().contains(lowerText))
                            || (sp != null && sp.getTenSanPham().toLowerCase().contains(lowerText));
                    if (match) {
                        ketQua.add(taiDayDuSanPham(lo));
                    }
                }
                fill(ketQua);
            }
            return;
        }

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


        // 2. Tìm theo tên / mã SP
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
    
    private void huyTatCa() {
        if (currentDanhSach == null || currentDanhSach.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Không có lô nào trong danh sách!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Tính tổng thống kê
        int tongSoLo = currentDanhSach.size();
        int tongSoLuong = 0;
        double tongGiaTri = 0;
        
        for (LoSanPham lo : currentDanhSach) {
            tongSoLuong += lo.getSoLuongTon();
            if (lo.getSanPham() != null) {
                tongGiaTri += lo.getSoLuongTon() * lo.getSanPham().getGiaNhap();
            }
        }
        
        // Xác nhận
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format(
                "Bạn muốn huỷ TẤT CẢ %d lô được tìm thấy?\n\n" +
                "📊 Thống kê:\n" +
                "   • Số lô: %d\n" +
                "   • Tổng số lượng: %,d\n" +
                "   • Giá trị ước tính: %,.0f đ\n\n" +
                "⚠️ Lưu ý: Tất cả các lô sẽ được thêm vào danh sách huỷ!",
                tongSoLo, tongSoLo, tongSoLuong, tongGiaTri
            ),
            "Xác nhận huỷ tất cả",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            selectedAll = true; // Đánh dấu là chọn tất cả
            dispose();
        }
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
        currentDanhSach = ds; // Lưu danh sách hiện tại
        LocalDate today = LocalDate.now();

        for (LoSanPham lo : ds) {
            SanPham sp = lo.getSanPham();
            
            // Tính số ngày còn lại đến HSD
            long soNgayConLai = ChronoUnit.DAYS.between(today, lo.getHanSuDung());
            String conLai;
            if (soNgayConLai > 0) {
                conLai = soNgayConLai + " ngày";
            } else if (soNgayConLai == 0) {
                conLai = "HÔM NAY";
            } else {
                conLai = "Quá hạn " + Math.abs(soNgayConLai) + " ngày";
            }
            
            model.addRow(new Object[]{
                    lo.getMaLo(),
                    sp != null ? sp.getTenSanPham() : "N/A",
                    lo.getHanSuDung().format(fmt),
                    conLai, // Cột mới
                    lo.getSoLuongTon(),
                    sp != null ? String.format("%,.0f", sp.getGiaNhap()) : "0"
            });
        }
    }



    // =====================================================
    // =============== GETTER TRẢ LÔ CHỌN ==================
    // =====================================================

    public LoSanPham getSelectedLo() {
        return selectedLo;
    }
    
    public boolean isSelectedAll() {
        return selectedAll;
    }
    
    public ArrayList<LoSanPham> getDanhSachLoChon() {
        return currentDanhSach;
    }
}
