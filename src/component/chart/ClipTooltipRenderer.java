package component.chart;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

@SuppressWarnings("serial")
public class ClipTooltipRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // chỉ xét chuỗi
        String text = (value == null) ? "" : String.valueOf(value);
        setText(text);

        // Tính bề rộng hiển thị thực sự của ô
        int colWidth = table.getColumnModel().getColumn(column).getWidth();
        Insets ins = getInsets();              // padding của renderer
        int avail = colWidth - ins.left - ins.right - 4; // trừ thêm chút đệm

        // Đo bề rộng chữ
        FontMetrics fm = getFontMetrics(getFont());
        int textWidth = fm.stringWidth(text);

        // Nếu bị cắt -> gán tooltip = full text, ngược lại bỏ tooltip
        if (textWidth > avail) setToolTipText(text);
        else setToolTipText(null);

        return c;
    }
}
