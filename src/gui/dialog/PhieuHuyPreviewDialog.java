package gui.dialog;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

import entity.PhieuHuy;
import entity.ChiTietPhieuHuy;
import entity.LoSanPham;
import entity.SanPham;
import entity.NhanVien;

public class PhieuHuyPreviewDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PhieuHuyPreviewDialog(Window owner, PhieuHuy ph) {
        super(owner, "Xem phiếu huỷ hàng", ModalityType.APPLICATION_MODAL);

        setSize(500, 700);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());

        JTextPane tp = new JTextPane();
        tp.setContentType("text/html");
        tp.setEditable(false);
        tp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tp.setText(buildHTML(ph));
        tp.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(tp);
        scroll.setBorder(null);

        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.setBackground(new Color(240, 240, 240));
        btnClose.addActionListener(e -> dispose());

        JPanel bottom = new JPanel();
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        bottom.add(btnClose);

        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private String buildHTML(PhieuHuy ph) {

        // Lấy thông tin chung
        String maPhieu = ph.getMaPhieuHuy();
        String ngayLap = ph.getNgayLapPhieu() != null ? ph.getNgayLapPhieu().format(FMT) : "";
        NhanVien nv = ph.getNhanVien();
        String tenNV = nv != null ? nv.getTenNhanVien() : "";
        String sdtNV = nv != null ? nv.getSoDienThoai() : "";
        String trangThai = ph.isTrangThai() ? "ĐÃ DUYỆT" : "CHỜ DUYỆT";

        double tongTien = ph.getTongTien();

        StringBuilder html = new StringBuilder();

        // HEADER
        html.append("""
        <html>
        <body style='font-family: Segoe UI; padding: 15px;'>
        <h2 style='text-align: center;'>HIỆU THUỐC HOÀ AN</h2>
        <h3 style='text-align: center;'>PHIẾU HUỶ HÀNG</h3>
        <p style='text-align:center;'>Mã phiếu: <b>%s</b></p>
        <hr>

        <p><b>Ngày lập:</b> %s</p>
        <p><b>Nhân viên lập:</b> %s</p>
        <p><b>SĐT NV:</b> %s</p>
        <p><b>Trạng thái:</b> %s</p>

        <hr>

        <table style='width:100%%; border-collapse: collapse;'>
           <tr style='background-color: #f2f2f2;'>
              <th style='padding: 5px; border-bottom: 1px solid #ccc;'>Mã lô</th>
              <th style='padding: 5px; border-bottom: 1px solid #ccc;'>Tên SP</th>
              <th style='padding: 5px; border-bottom: 1px solid #ccc;'>SL huỷ</th>
              <th style='padding: 5px; border-bottom: 1px solid #ccc;'>Đơn giá</th>
              <th style='padding: 5px; border-bottom: 1px solid #ccc;'>T.Tiền</th>
           </tr>
        """.formatted(
                maPhieu, ngayLap, tenNV, sdtNV, trangThai
        ));

        // BODY – lặp chi tiết phiếu hủy
        for (ChiTietPhieuHuy ct : ph.getChiTietPhieuHuyList()) {

            LoSanPham lo = ct.getLoSanPham();
            SanPham sp = lo != null ? lo.getSanPham() : null;

            String maLo = lo != null ? lo.getMaLo() : "";
            String tenSP = sp != null ? sp.getTenSanPham() : "";

            int sl = ct.getSoLuongHuy();
            double donGia = ct.getDonGiaNhap();
            double thanhTien = ct.getThanhTien();

            html.append("""
            <tr>
               <td style='padding: 5px; border-bottom: 1px solid #eee;'>%s</td>
               <td style='padding: 5px; border-bottom: 1px solid #eee;'>%s</td>
               <td style='text-align:center; padding: 5px; border-bottom: 1px solid #eee;'>%d</td>
               <td style='text-align:right; padding: 5px; border-bottom: 1px solid #eee;'>%,.0f</td>
               <td style='text-align:right; padding: 5px; border-bottom: 1px solid #eee;'>%,.0f</td>
            </tr>
            """.formatted(maLo, tenSP, sl, donGia, thanhTien));
        }

        html.append("</table>");

        // FOOTER
        html.append("""
        <div style='text-align:right; margin-top: 15px;'>
            <p>Tổng tiền huỷ: <b>%,.0f đ</b></p>
        </div>

        <hr>
        <p style='text-align:center; font-style:italic;'>Báo cáo hủy hàng – Không có giá trị thanh toán</p>

        </body></html>
        """.formatted(tongTien));

        return html.toString();
    }
}
