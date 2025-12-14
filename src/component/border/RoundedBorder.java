package component.border;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D; // THÊM IMPORT
import java.awt.Insets;
import java.awt.RenderingHints; // THÊM IMPORT
import javax.swing.border.Border;

public class RoundedBorder implements Border {

    private int radius; // Bán kính bo góc

    public RoundedBorder(int radius) {
        this.radius = radius;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        // Tạo khoảng đệm bên trong để văn bản không bị dính vào viền
        return new Insets(this.radius / 2, this.radius / 2, this.radius / 2, this.radius / 2);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        // Ép kiểu Graphics sang Graphics2D để có nhiều tùy chọn vẽ hơn
        Graphics2D g2d = (Graphics2D) g;
        
        // Bật chế độ khử răng cưa để đường viền mượt hơn
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Thiết lập màu cho đường viền (lấy theo màu chữ của component)
        g2d.setColor(c.getForeground());
        
        // Vẽ hình chữ nhật bo góc
        // width-1 và height-1 để đường viền không bị cắt ở cạnh phải và dưới
        g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
    }
    
    // CÁC PHƯƠNG THỨC BỊ TRÙNG LẶP Ở ĐÂY ĐÃ ĐƯỢC XÓA BỎ
}