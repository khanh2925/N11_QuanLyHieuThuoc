package gui.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import dao.DonViTinh_DAO;
import dao.QuyCachDongGoi_DAO;
import entity.DonViTinh;
import entity.QuyCachDongGoi;
import entity.SanPham;
import gui.quanly.QuanLySanPham_GUI;

@SuppressWarnings("serial")
public class QuyCachDongGoi_Dialog extends JDialog implements ActionListener {
    
    private JTextField txtMaQC, txtHeSo, txtTiLeGiam;
    private JComboBox<String> cboDVT;
    private JCheckBox chkDonViGoc;
    private JButton btnLuu, btnHuy;
    
    private String maSanPham;
    private String maQuyCachEdit;
    private QuanLySanPham_GUI parentGUI;
    
    private DonViTinh_DAO donViTinhDAO;
    private QuyCachDongGoi_DAO quyCachDAO;
    private List<DonViTinh> listDVT;

    public QuyCachDongGoi_Dialog(QuanLySanPham_GUI parent, String maSanPham, String maQuyCachEdit) {
        super((Frame) SwingUtilities.getWindowAncestor(parent), true);
        this.parentGUI = parent;
        this.maSanPham = maSanPham;
        this.maQuyCachEdit = maQuyCachEdit;
        
        this.donViTinhDAO = new DonViTinh_DAO();
        this.quyCachDAO = new QuyCachDongGoi_DAO();

        khoiTaoGiaoDien();
        taiDuLieuDonViTinh();
        khoiTaoDuLieu();
    }

    private void khoiTaoGiaoDien() {
        setTitle(maQuyCachEdit == null ? "Thêm quy cách đóng gói" : "Cập nhật quy cách");
        setSize(450, 350);
        setLocationRelativeTo(parentGUI);
        setLayout(new BorderLayout());

        JPanel pnForm = new JPanel(null);
        pnForm.setBackground(Color.WHITE);
        
        int x = 30, y = 20, wLbl = 100, wTxt = 250, h = 30, gap = 20;
        
        JLabel lblMa = new JLabel("Mã quy cách:");
        lblMa.setBounds(x, y, wLbl, h);
        pnForm.add(lblMa);
        txtMaQC = new JTextField();
        txtMaQC.setEditable(false);
        txtMaQC.setBounds(x + wLbl, y, wTxt, h);
        pnForm.add(txtMaQC);
        
        y += h + gap;
        JLabel lblDVT = new JLabel("Đơn vị tính:");
        lblDVT.setBounds(x, y, wLbl, h);
        pnForm.add(lblDVT);
        cboDVT = new JComboBox<>();
        cboDVT.setBounds(x + wLbl, y, wTxt, h);
        pnForm.add(cboDVT);
        
        y += h + gap;
        JLabel lblHS = new JLabel("Hệ số quy đổi:");
        lblHS.setBounds(x, y, wLbl, h);
        pnForm.add(lblHS);
        txtHeSo = new JTextField();
        txtHeSo.setBounds(x + wLbl, y, wTxt, h);
        pnForm.add(txtHeSo);
        
        y += h + gap;
        JLabel lblTL = new JLabel("Tỉ lệ giảm (%):");
        lblTL.setBounds(x, y, wLbl, h);
        pnForm.add(lblTL);
        txtTiLeGiam = new JTextField("0");
        txtTiLeGiam.setBounds(x + wLbl, y, wTxt, h);
        pnForm.add(txtTiLeGiam);
        
        y += h + gap;
        chkDonViGoc = new JCheckBox("Là đơn vị gốc (Cơ bản)");
        chkDonViGoc.setBackground(Color.WHITE);
        chkDonViGoc.setBounds(x + wLbl, y, wTxt, h);
        chkDonViGoc.addActionListener(e -> xuLyChonDonViGoc());
        pnForm.add(chkDonViGoc);

        add(pnForm, BorderLayout.CENTER);

        JPanel pnBtn = new JPanel();
        pnBtn.setBackground(Color.WHITE);
        btnLuu = new JButton("Lưu");
        btnLuu.addActionListener(this);
        btnHuy = new JButton("Hủy");
        btnHuy.addActionListener(this);
        pnBtn.add(btnLuu);
        pnBtn.add(btnHuy);
        add(pnBtn, BorderLayout.SOUTH);
    }

    private void xuLyChonDonViGoc() {
        if(chkDonViGoc.isSelected()) {
            txtHeSo.setText("1");
            txtHeSo.setEditable(false);
        } else {
            txtHeSo.setEditable(true);
        }
    }

    private void taiDuLieuDonViTinh() {
        listDVT = donViTinhDAO.layTatCaDonViTinh();
        for(DonViTinh dvt : listDVT) {
            cboDVT.addItem(dvt.getTenDonViTinh());
        }
    }

    private void khoiTaoDuLieu() {
        if (maQuyCachEdit == null) {
            txtMaQC.setText(quyCachDAO.taoMaQuyCach());
        } else {
            // ⚠️ KHI SỬA: KHÔNG cho phép thay đổi thuộc tính "là đơn vị gốc"
            chkDonViGoc.setEnabled(false);
            chkDonViGoc.setToolTipText("Không thể thay đổi thuộc tính 'đơn vị gốc' khi sửa. Chỉ có thể thay đổi đơn vị tính.");
            
            List<QuyCachDongGoi> list = quyCachDAO.layDanhSachQuyCachTheoSanPham(maSanPham);
            for(QuyCachDongGoi qc : list) {
                if(qc.getMaQuyCach().equals(maQuyCachEdit)) {
                    txtMaQC.setText(qc.getMaQuyCach());
                    cboDVT.setSelectedItem(qc.getDonViTinh().getTenDonViTinh());
                    txtHeSo.setText(String.valueOf(qc.getHeSoQuyDoi()));
                    txtTiLeGiam.setText(String.valueOf((int)(qc.getTiLeGiam() * 100)));
                    chkDonViGoc.setSelected(qc.isDonViGoc());
                    if(qc.isDonViGoc()) txtHeSo.setEditable(false);
                    break;
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnHuy) {
            dispose();
        } else if (e.getSource() == btnLuu) {
            luuQuyCach();
        }
    }

    private void luuQuyCach() {
        try {
            int heSo = Integer.parseInt(txtHeSo.getText().trim());
            double tiLe = Double.parseDouble(txtTiLeGiam.getText().trim()) / 100.0;
            
            if(heSo <= 0) throw new NumberFormatException();
            if(tiLe < 0 || tiLe > 1) {
                JOptionPane.showMessageDialog(this, "Tỉ lệ giảm phải từ 0 đến 100%");
                return;
            }
            
            boolean isGoc = chkDonViGoc.isSelected();
            if(isGoc && heSo != 1) {
                JOptionPane.showMessageDialog(this, "Đơn vị gốc phải có hệ số quy đổi là 1!");
                return;
            }
            if(!isGoc && heSo == 1) {
                JOptionPane.showMessageDialog(this, "Đơn vị quy đổi phải có hệ số > 1. Nếu là 1, hãy chọn 'Là đơn vị gốc'.");
                return;
            }

            int idx = cboDVT.getSelectedIndex();
            if(idx < 0) return;
            DonViTinh dvtChon = listDVT.get(idx);

            // ===== KIỂM TRA TRÙNG ĐỠN VỊ TÍNH =====
            // Kiểm tra xem đơn vị tính đã được sử dụng cho quy cách khác chưa
            QuyCachDongGoi qcTrungDVT = quyCachDAO.timQuyCachTheoSanPhamVaDonVi(maSanPham, dvtChon.getMaDonViTinh());
            if(qcTrungDVT != null && !qcTrungDVT.getMaQuyCach().equals(maQuyCachEdit)) {
                JOptionPane.showMessageDialog(this,
                        "❌ ĐƠN VỊ TÍNH ĐÃ TỒN TẠI!\n\n" +
                        "Đơn vị tính '" + dvtChon.getTenDonViTinh() + "' đã được sử dụng cho quy cách: " + qcTrungDVT.getMaQuyCach() + "\n\n" +
                        "Mỗi sản phẩm không thể có 2 quy cách cùng đơn vị tính.\n" +
                        "Vui lòng chọn đơn vị tính khác.",
                        "Lỗi: Trùng đơn vị tính",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ===== XỬ LÝ ĐƠN VỊ GỐC =====
            if (isGoc && maQuyCachEdit == null) {
                // Trường hợp THÊM MỚI quy cách là đơn vị gốc
                QuyCachDongGoi qcGocCu = quyCachDAO.timQuyCachGocTheoSanPham(maSanPham);
                
                if(qcGocCu != null) {
                    // Đã có đơn vị gốc → Hỏi có muốn THAY THẾ không
                    int choice = JOptionPane.showConfirmDialog(this,
                            "⚠️ SẢN PHẨM ĐÃ CÓ ĐƠN VỊ GỐC\n\n" +
                            "Đơn vị gốc hiện tại: " + qcGocCu.getDonViTinh().getTenDonViTinh() + "\n" +
                            "Đơn vị gốc mới: " + dvtChon.getTenDonViTinh() + "\n\n" +
                            "Bạn có muốn THAY THẾ đơn vị gốc cũ bằng đơn vị mới không?\n\n" +
                            "  • YES: Xóa đơn vị gốc cũ, thêm đơn vị gốc mới\n" +
                            "  • NO: Hủy bỏ, giữ nguyên đơn vị gốc cũ",
                            "Xác nhận thay thế đơn vị gốc",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    
                    if(choice == JOptionPane.YES_OPTION) {
                        // Người dùng chọn THAY THẾ → Xóa đơn vị gốc cũ
                        boolean xoaOK = quyCachDAO.xoaQuyCachDongGoi(qcGocCu.getMaQuyCach());
                        if(!xoaOK) {
                            JOptionPane.showMessageDialog(this, 
                                "❌ Không thể xóa đơn vị gốc cũ! Vui lòng thử lại.", 
                                "Lỗi", 
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } else {
                        // Người dùng chọn HỦY → Không thêm, giữ nguyên đơn vị gốc cũ
                        return;
                    }
                }
            }

            // Sử dụng constructor đầy đủ tham số để tránh lỗi validation khi set từng thuộc tính
            SanPham sp = new SanPham(maSanPham);
            QuyCachDongGoi qc = new QuyCachDongGoi(
                txtMaQC.getText(),  // maQuyCach
                dvtChon,            // donViTinh
                sp,                 // sanPham
                heSo,               // heSoQuyDoi
                tiLe,               // tiLeGiam
                isGoc,              // isDonViGoc
                true                // trangThai (mặc định là Hoạt động)
            );

            boolean kq;
            if (maQuyCachEdit == null) {
                QuyCachDongGoi check = quyCachDAO.timQuyCachTheoSanPhamVaDonVi(maSanPham, dvtChon.getMaDonViTinh());
                if(check != null) {
                    JOptionPane.showMessageDialog(this, "Sản phẩm này đã có quy cách cho đơn vị: " + dvtChon.getTenDonViTinh());
                    return;
                }
                kq = quyCachDAO.themQuyCachDongGoi(qc);
            } else {
                kq = quyCachDAO.capNhatQuyCachDongGoi(qc);
            }

            if (kq) {
                JOptionPane.showMessageDialog(this, "Lưu thành công!");
                parentGUI.taiDuLieuQuyCach(maSanPham);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lưu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Hệ số và tỉ lệ phải là số hợp lệ!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}