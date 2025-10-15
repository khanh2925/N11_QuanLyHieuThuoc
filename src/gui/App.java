package gui;

import javax.swing.SwingUtilities;

public class App {
	public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
//            new DangNhap_GUI().setVisible(true);
        	new NhanVien_GUI().setVisible(true);
        });
	};
}
