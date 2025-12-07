package component.input;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JTextField;

/**
 * Lớp tiện ích giúp thêm placeholder cho JTextField.
**/
public class PlaceholderSupport {

    /**
     * Thêm placeholder cho JTextField.
     *
     * @param textField        JTextField cần thêm placeholder
     * @param placeholderText  Chuỗi placeholder hiển thị khi trống
     */
    public static void addPlaceholder(JTextField textField, String placeholderText) {
        // Gán màu và nội dung ban đầu
        textField.setForeground(Color.GRAY);
        textField.setText(placeholderText);

        // Thêm listener để xử lý focus
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholderText)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholderText);
                }
            }
        });
    }
}
