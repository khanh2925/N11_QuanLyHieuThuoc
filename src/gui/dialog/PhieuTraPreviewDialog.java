package gui.dialog;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

import entity.PhieuTra;
import entity.ChiTietPhieuTra;

@SuppressWarnings("serial")
public class PhieuTraPreviewDialog extends JDialog {

    public PhieuTraPreviewDialog(Window owner, PhieuTra pt, List<ChiTietPhieuTra> dsCT) {
        super(owner, "Xem phiếu trả hàng", ModalityType.APPLICATION_MODAL);

        setSize(500, 650);
        setLocationRelativeTo(owner);
        setResizable(false);

        JTextPane tp = new JTextPane();
        tp.setContentType("text/html");
        tp.setEditable(false);
        tp.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tp.setText(buildHTML(pt, dsCT));

        JScrollPane scroll = new JScrollPane(tp);
        scroll.setBorder(null);

        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel bottom = new JPanel();
        bottom.add(btnClose);

        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private String buildHTML(PhieuTra pt, List<ChiTietPhieuTra> dsCT) {
        StringBuilder html = new StringBuilder();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        html.append("""
        <html>
        <body style='font-family: Segoe UI; padding: 10px;'>
        <h2 style='text-align: center;'>HIỆU THUỐC HOÀ AN</h2>
        <h3 style='text-align: center;'>PHIẾU TRẢ HÀNG</h3>
        <p style='text-align:center;'>Mã phiếu: <b>%s</b></p>
        <hr>
        <p><b>Khách hàng:</b> %s</p>
        <p><b>SĐT:</b> %s</p>
        <p><b>Ngày trả:</b> %s</p>
        <p><b>Nhân viên lập:</b> %s</p>
        <hr>
        <table style='width:100%%; border-collapse: collapse;'>
           <tr>
              <th style='text-align:left;'>Sản phẩm</th>
              <th>SL</th>
              <th>Giá</th>
              <th>Thành tiền</th>
           </tr>
        """.formatted(
                pt.getMaPhieuTra(),
                pt.getKhachHang().getTenKhachHang(),
                pt.getKhachHang().getSoDienThoai(),
                pt.getNgayLap().format(fmt),
                pt.getNhanVien().getTenNhanVien()
        ));

        // Dòng chi tiết
        for (ChiTietPhieuTra ct : dsCT) {
            String tenSP = ct.getChiTietHoaDon().getSanPham().getTenSanPham();
            int sl = (int) ct.getSoLuong();
            double gia = ct.getChiTietHoaDon().getGiaBan();
            double thanhTien = ct.getThanhTienHoan();

            html.append("""
            <tr>
               <td>%s</td>
               <td style='text-align:center;'>%d</td>
               <td style='text-align:right;'>%,.0f</td>
               <td style='text-align:right;'>%,.0f</td>
            </tr>
            """.formatted(tenSP, sl, gia, thanhTien));
        }

        html.append("""
        </table>
        <hr>
        <h3 style='text-align:right;'>Tổng tiền hoàn: %,.0f đ</h3>
        <hr>
        <p style='text-align:center;'>Cảm ơn quý khách!</p>
        </body></html>
        """.formatted(pt.getTongTienHoan()));

        return html.toString();
    }
}
