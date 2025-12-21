package component.button;

import java.awt.*;
import javax.swing.*;

/**
 * Nút dạng viên thuốc hai nửa màu, hiệu ứng hover + click đầy đủ.
 * Giữ nguyên cách vẽ giống bản gốc liền mạch, không bo ở giữa.
 */
public class PillButton extends JButton {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private Color leftColor = new Color(220, 220, 220);
    @SuppressWarnings("unused")
	private Color rightColor = new Color(250, 220, 220);
    @SuppressWarnings("unused")
	private Color borderColor = new Color(170, 170, 170);

    public PillButton(String text) {
        super(text);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setForeground(Color.DARK_GRAY);
        setRolloverEnabled(true);
    }

    public void setColors(Color left, Color right) {
        this.leftColor = left;
        this.rightColor = right;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int arc = height;

        // Khi bấm, làm tối màu 10%
        float darken = getModel().isPressed() ? 0.9f : 1.0f;

        // Hai nửa màu
        Color left = new Color((int)(220 * darken), (int)(220 * darken), (int)(220 * darken));
        Color right = new Color((int)(250 * darken), (int)(220 * darken), (int)(220 * darken));

        // Nền viên thuốc
        g2.setColor(left);
        g2.fillRoundRect(0, 0, width, height, arc, arc);

        g2.setColor(right);
        g2.fillRoundRect(width / 2, 0, width / 2, height, arc, arc);

        // Viền nhẹ
        g2.setColor(new Color(170, 170, 170));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(0, 0, width - 1, height - 1, arc, arc);

        // Hover sáng nhẹ
        if (getModel().isRollover() && !getModel().isPressed()) {
            g2.setColor(new Color(255, 255, 255, 40));
            g2.fillRoundRect(0, 0, width, height, arc, arc);
        }

        g2.dispose();

        // Gọi paintComponent để JButton vẫn có hiệu ứng bấm thật
        super.paintComponent(g);
} }