/**
 * @author Quốc Khánh cute
 * @version 1.0
 * @since Nov 12, 2025
 */
package component.button;

import java.awt.*;

public class TaoButtonNhanh {
    public static PillButton goiY(String text) {
        PillButton btn = new PillButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setMaximumSize(new Dimension(150, 40));
        return btn;
    }

    public static PillButton banHang() {
        PillButton btn = new PillButton("Bán hàng");
        btn.setMaximumSize(new Dimension(300, 70));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 23));
        return btn;
    }
}