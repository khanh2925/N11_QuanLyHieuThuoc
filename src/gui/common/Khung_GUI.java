/**
 * @author Quốc Khánh
 * @version 3.0
 * @since Oct 16, 2025
 *
 * Mô tả: Khung giao diện trống - giữ lại bố cục chính để clone trang khác.
 */

package gui.common;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Khung_GUI extends JPanel {

    private JPanel pnCenter;   // vùng trung tâm
    private JPanel pnHeader;   // vùng đầu trang
    private JPanel pnRight;    // vùng cột phải
    private JTextField textField;

    public Khung_GUI() {
        this.setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1537, 1168));

        // ===== HEADER =====
        pnHeader = new JPanel();
        pnHeader.setPreferredSize(new Dimension(1073, 88));
        pnHeader.setBackground(new Color(0xE3F2F5));
        pnHeader.setLayout(null);
        add(pnHeader, BorderLayout.NORTH);
        
        JButton btnNewButton = new JButton("New button");
        btnNewButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	}
        });
        btnNewButton.setBounds(426, 11, 89, 66);
        pnHeader.add(btnNewButton);
        
        JButton btnNewButton_1 = new JButton("New button");
        btnNewButton_1.setBounds(542, 11, 89, 66);
        pnHeader.add(btnNewButton_1);
        
        textField = new JTextField();
        textField.setBounds(53, 34, 196, 20);
        pnHeader.add(textField);
        textField.setColumns(10);

        // ===== CENTER =====
        pnCenter = new JPanel();
        pnCenter.setBackground(new Color(255, 128, 192));
        pnCenter.setLayout(new BorderLayout());
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(pnCenter, BorderLayout.CENTER);

        // ===== RIGHT =====
        pnRight = new JPanel();
        pnRight.setPreferredSize(new Dimension(300, 1080));
        pnRight.setBackground(new Color(0, 128, 255));
        pnRight.setLayout(new BoxLayout(pnRight, BoxLayout.Y_AXIS));
        pnRight.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(pnRight, BorderLayout.EAST);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Khung trống - clone base");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new Khung_GUI());
            frame.setVisible(true);
        });
    }
}
