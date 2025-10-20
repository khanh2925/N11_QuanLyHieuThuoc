/**
 * @author Thanh Kha
 * @version 1.0
 * @since Oct 16, 2025
 *
 * Mô tả: Class App chứa main để chạy trương trình
 */
package gui;

import javax.swing.SwingUtilities;

public class App {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new Main_GUI().setVisible(true);
		});
	};
}
