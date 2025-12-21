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
		// (Tùy chọn) Cài đặt Look and Feel cho đẹp hơn
		try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		SwingUtilities.invokeLater(() -> {
			// Khởi chạy màn hình loading thay vì màn hình đăng nhập
			new DangNhap_GUI(); 
		});
	};
}