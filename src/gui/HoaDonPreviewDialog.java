package gui;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import entity.HoaDon;
import entity.ChiTietHoaDon;

public class HoaDonPreviewDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public HoaDonPreviewDialog(Window owner, HoaDon hd) {
        super(owner, "Xem hóa đơn bán hàng", ModalityType.APPLICATION_MODAL);

        setSize(500, 700); // Chiều cao lớn hơn xíu để chứa đủ thông tin
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());

        // Vùng hiển thị nội dung HTML
        JTextPane tp = new JTextPane();
        tp.setContentType("text/html");
        tp.setEditable(false);
        tp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Tạo HTML từ đối tượng Hóa Đơn
        tp.setText(buildHTML(hd));
        
        // Fix lỗi scroll tự chạy xuống cuối
        tp.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(tp);
        scroll.setBorder(null);

        // Nút đóng
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.setBackground(new Color(240, 240, 240));

        JPanel bottom = new JPanel();
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        bottom.add(btnClose);

        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private String buildHTML(HoaDon hd) {
        StringBuilder html = new StringBuilder();
        
        // Thông tin khách hàng (Xử lý null nếu là khách lẻ)
        String tenKhach = (hd.getKhachHang() != null) ? hd.getKhachHang().getTenKhachHang() : "Khách lẻ";
        String sdtKhach = (hd.getKhachHang() != null) ? hd.getKhachHang().getSoDienThoai() : "";

        // 1. HEADER
        html.append("""
        <html>
        <body style='font-family: Segoe UI; padding: 15px;'>
        <h2 style='text-align: center;'>HIỆU THUỐC HOÀ AN</h2>
        <h3 style='text-align: center;'>HÓA ĐƠN BÁN HÀNG</h3>
        <p style='text-align:center;'>Mã HĐ: <b>%s</b></p>
        <hr>
        <p><b>Khách hàng:</b> %s</p>
        <p><b>SĐT:</b> %s</p>
        <p><b>Ngày lập:</b> %s</p>
        <p><b>Nhân viên:</b> %s</p>
        <hr>
        """.formatted(
                hd.getMaHoaDon(),
                tenKhach,
                sdtKhach,
                hd.getNgayLap().format(FMT),
                hd.getNhanVien().getTenNhanVien()
        ));

        // 2. TABLE HEADER
        html.append("""
        <table style='width:100%%; border-collapse: collapse;'>
           <tr style='background-color: #f2f2f2;'>
              <th style='text-align:left; padding: 5px; border-bottom: 1px solid #ccc;'>Tên thuốc</th>
              <th style='padding: 5px; border-bottom: 1px solid #ccc;'>ĐVT</th>
              <th style='padding: 5px; border-bottom: 1px solid #ccc;'>SL</th>
              <th style='padding: 5px; border-bottom: 1px solid #ccc;'>Đơn giá</th>
              <th style='padding: 5px; border-bottom: 1px solid #ccc;'>T.Tiền</th>
           </tr>
        """);

        // 3. TABLE BODY (Lặp qua danh sách chi tiết)
        for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
            String tenSP = ct.getSanPham().getTenSanPham();
            String dvt = (ct.getDonViTinh() != null) ? ct.getDonViTinh().getTenDonViTinh() : "";
            
            // Đơn giá này là đơn giá Gốc (như bạn đã sửa trong ItemDonHang)
            double donGia = ct.getGiaBan(); 
            
            // Thành tiền này là thực tế (đã trừ KM sản phẩm nếu có)
            double thanhTien = ct.getThanhTien(); 

            html.append("""
            <tr>
               <td style='padding: 5px; border-bottom: 1px solid #eee;'>%s</td>
               <td style='text-align:center; padding: 5px; border-bottom: 1px solid #eee; font-size: 12px;'>%s</td>
               <td style='text-align:center; padding: 5px; border-bottom: 1px solid #eee;'>%d</td>
               <td style='text-align:right; padding: 5px; border-bottom: 1px solid #eee;'>%,.0f</td>
               <td style='text-align:right; padding: 5px; border-bottom: 1px solid #eee;'>%,.0f</td>
            </tr>
            """.formatted(tenSP, dvt, (int)ct.getSoLuong(), donGia, thanhTien));
        }

        html.append("</table>");

        // 4. FOOTER (Tổng kết tiền)
        
        // Tính toán lại số tiền giảm giá (Tổng hợp cả giảm SP và giảm HĐ)
        // Công thức: Tổng giảm = Tổng tiền hàng gốc - Khách phải trả
        double tongGiamGia = hd.getTongTien() - hd.getTongThanhToan();

        html.append("<div style='text-align:right; margin-top: 15px;'>");
        
        // Dòng Tổng tiền hàng
        html.append(String.format("<p>Tổng tiền hàng: <b>%,.0f đ</b></p>", hd.getTongTien()));

        // Dòng Giảm giá (chỉ hiện nếu có giảm)
        if (tongGiamGia > 0) {
            html.append(String.format("<p>Giảm giá: -%,.0f đ</p>", tongGiamGia));
        }

        html.append("<hr>");
        
        // Dòng KHÁCH PHẢI TRẢ (To và Đỏ)
        html.append(String.format("<h3 style='color:#D32F2F; margin: 10px 0;'>TỔNG THANH TOÁN: %,.0f đ</h3>", hd.getTongThanhToan()));
        
        html.append("</div>");

        // Lời cảm ơn
        html.append("""
        <hr>
        <p style='text-align:center; font-style:italic;'>Cảm ơn quý khách & Hẹn gặp lại!</p>
        </body></html>
        """);

        return html.toString();
    }
}