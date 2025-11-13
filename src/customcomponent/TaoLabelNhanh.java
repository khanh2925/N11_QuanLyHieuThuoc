/**
 * @author Quốc Khánh cute
 * @version 1.0
 * @since Nov 12, 2025
 *
 * Mô tả: Lớp tiện ích tạo JLabel nhanh với cấu hình phổ biến.
 * Dùng trong code thuần hoặc kết hợp với WindowBuilder (cần gán biến + getter).
 */
package customcomponent;

import javax.swing.*;
import java.awt.*;

public class TaoLabelNhanh {

    /**
     * Tạo JLabel tiêu chuẩn cho form (trái, font 18, kích thước cố định)
     */
    public static JLabel tieuDe(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lbl.setPreferredSize(new Dimension(200, 24));
        lbl.setMaximumSize(new Dimension(200, 24));
        return lbl;
    }

    /**
     * Tạo JLabel với font và màu tùy chỉnh (cho tiêu đề nổi bật)
     */
    public static JLabel tuyChinh(String text, Font font, Color foreground) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(foreground);
        lbl.setPreferredSize(new Dimension(200, 30));
        return lbl;
    }
}