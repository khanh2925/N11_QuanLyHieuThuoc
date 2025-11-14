/**
 * @author Quốc Khánh cute
 * @version 1.0
 * @since Nov 12, 2025
 *
 * Mô tả: Lớp tiện ích tạo JTextField nhanh với cấu hình phổ biến.
 * Dùng trong code thuần hoặc kết hợp với WindowBuilder (cần gán biến + getter).
 */
package customcomponent;

import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.*;

public class TaoJtextNhanh {

    /**
     * Tạo JTextField hiển thị (không chỉnh sửa) - dùng cho số tiền, tên, v.v.
     */
    public static JTextField hienThi(String text, Font font, Color foreground) {
        JTextField txt = new JTextField(text);
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txt.setFont(font);
        txt.setHorizontalAlignment(SwingConstants.RIGHT);
        txt.setEditable(false);
        txt.setForeground(foreground);
//        txt.setBorder(null);
//        txt.setBackground(Color.WHITE);
        return txt;
    }

    /**
     * Tạo JTextField nhập liệu (có placeholder, viền xanh)
     */
    public static JTextField nhapLieu(String placeholder) {
        JTextField txt = new JTextField();
        PlaceholderSupport.addPlaceholder(txt, placeholder);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        txt.setBorder(new LineBorder(new Color(0x00C0E2), 3, true));
        return txt;
    }

    /**
     * Tạo JTextField tìm kiếm (giống txtTimThuoc)
     */
    public static JTextField timKiem() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txt.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
        return txt;
    }
    public static JTextField taoTextDonHang(String text, Font font, Color foreground, int width) {
        JTextField txt = new JTextField(text);
        txt.setFont(font);
        txt.setForeground(foreground);
        txt.setEditable(false);
        txt.setBorder(null);
        txt.setBackground(new Color(0xFAFAFA));
        txt.setHorizontalAlignment(SwingConstants.LEFT);
        txt.setCaretPosition(0);
        txt.setToolTipText(text);
        txt.setMaximumSize(new Dimension(width, 30));
        txt.setPreferredSize(new Dimension(width, 30));
        return txt;
    }
}