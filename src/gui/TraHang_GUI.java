package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import customcomponent.PillButton;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.JLabel;

public class TraHang_GUI extends JPanel {
	private JTextField thanhTimKiem;
	private JTextField khoangNgayTimKiem;
	
	private JButton btnThem;
	private JButton btnXuat;
	private DefaultTableModel modelTblPhieuTra;
	private JTable tblPhieuTra;
	private JScrollPane scrDSPhieuTra;
	
	private DefaultTableModel modelTblCTPhieuTra;
	private JTable tblCTPhieuTra;
	private JScrollPane scrCTPhieuTra;
	
	
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                JFrame frame = new JFrame("Test NhapHang");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setContentPane(new TraHang_GUI());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public TraHang_GUI() {
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1280, 800)); // kích thước mong muốn
        
        
        JPanel cacChucNang = new JPanel(new BorderLayout());

        JPanel pnTren = new JPanel() {
            Image backgroundImage;
            {
                try {
                    backgroundImage = ImageIO.read(new File("src/images/thanhchucnang.png"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        cacChucNang.add(pnTren, BorderLayout.NORTH);

        // 3) Panel ngay dưới thanhChucNang
        JPanel thanhChucNang = new JPanel();                    
        thanhChucNang.setPreferredSize(new Dimension(0, 60));   
        thanhChucNang.setBackground(new Color(227,242,245));
        
        add(cacChucNang, BorderLayout.NORTH);

        
 
        


       
        // cac button cua thanh chuc nang
 
        pnTren.setPreferredSize(new Dimension(0, 60));
        pnTren.setLayout(null);
        
        JButton btnTongQuan = new JButton("Tổng quan");
        btnTongQuan.setBounds(35, 20, 100, 25);
        pnTren.add(btnTongQuan);
        btnTongQuan.setOpaque(false);                
        btnTongQuan.setContentAreaFilled(false);     
        btnTongQuan.setBorderPainted(false);         
        btnTongQuan.setFocusPainted(false); 
        btnTongQuan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JButton btnHoaDon = new JButton("Hóa đơn");
        btnHoaDon.setBounds(150, 20, 90, 25);
        pnTren.add(btnHoaDon);
        btnHoaDon.setOpaque(false);                
        btnHoaDon.setContentAreaFilled(false);     
        btnHoaDon.setBorderPainted(false);         
        btnHoaDon.setFocusPainted(false); 
        btnHoaDon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JButton btnSanPham = new PillButton("Sản phẩm");
        btnSanPham.setBounds(265, 20, 125, 25);
        pnTren.add(btnSanPham);

             
        JButton btnKhuyenMai = new JButton("Khuyến mãi");
        btnKhuyenMai.setBounds(410, 20, 110, 25);
        pnTren.add(btnKhuyenMai);
        btnKhuyenMai.setOpaque(false);                
        btnKhuyenMai.setContentAreaFilled(false);     
        btnKhuyenMai.setBorderPainted(false);         
        btnKhuyenMai.setFocusPainted(false); 
        btnKhuyenMai.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JButton btnNhanVien = new JButton("Nhân viên");
        btnNhanVien.setBounds(525, 20, 100, 25);
        pnTren.add(btnNhanVien);
        btnNhanVien.setOpaque(false);                
        btnNhanVien.setContentAreaFilled(false);     
        btnNhanVien.setBorderPainted(false);         
        btnNhanVien.setFocusPainted(false); 
        btnNhanVien.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JButton btnBaoCaoThuChi = new JButton("Báo cáo thu chi");
        btnBaoCaoThuChi.setBounds(640, 20, 150, 25);
        pnTren.add(btnBaoCaoThuChi);
        btnBaoCaoThuChi.setOpaque(false);                
        btnBaoCaoThuChi.setContentAreaFilled(false);     
        btnBaoCaoThuChi.setBorderPainted(false);         
        btnBaoCaoThuChi.setFocusPainted(false); 
        btnBaoCaoThuChi.setFont(new Font("Segoe UI", Font.BOLD, 13));

        
        // cac component trong thanh chuc nang
        cacChucNang.add(thanhChucNang, BorderLayout.SOUTH);
        thanhChucNang.setLayout(null);
        
        thanhTimKiem = new JTextField("Tìm phiếu trả");
        thanhTimKiem.setBounds(23, 10, 190, 40);
        thanhChucNang.add(thanhTimKiem);
        thanhTimKiem.setColumns(10);
        
        
        khoangNgayTimKiem = new JTextField();
        khoangNgayTimKiem.setBounds(242, 19, 140, 21);
        thanhChucNang.add(khoangNgayTimKiem);
        khoangNgayTimKiem.setColumns(10);
        
        btnThem = new PillButton("Thêm");
        btnThem.setBounds(425, 13, 86, 28);
        thanhChucNang.add(btnThem);      
        
        btnXuat = new PillButton("Xuất");
        btnXuat.setBounds(542, 13, 79, 28);
        thanhChucNang.add(btnXuat);
        
        JLabel lblChiTietPhieuTra = new JLabel("Chi tiết phiếu trả");
        lblChiTietPhieuTra.setBounds(853, 10, 198, 27);
        thanhChucNang.add(lblChiTietPhieuTra);
        lblChiTietPhieuTra.setFont(new Font("Segoe UI", Font.BOLD, 23));
        
        
        // centre
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.X_AXIS));
        center.setBorder(javax.swing.BorderFactory.createEmptyBorder(12,12,12,12)); // lề
        add(center, BorderLayout.CENTER);

        // 2 panel con
        JPanel pnlDSPhieuTra  = new JPanel(new BorderLayout());
        JPanel pnlCTPhieuTra = new JPanel(new BorderLayout());
        pnlDSPhieuTra.setBackground(new java.awt.Color(245,245,245));
        pnlCTPhieuTra.setBackground(new java.awt.Color(238,238,238));

        center.add(pnlDSPhieuTra);
        center.add(pnlCTPhieuTra);
        pnlDSPhieuTra.setPreferredSize(new Dimension(0, 0));
        pnlDSPhieuTra.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        pnlCTPhieuTra.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        pnlCTPhieuTra.setPreferredSize(new Dimension(0, 0));
        
        
        
        
       
        // bảng danh sách phiếu trả
        String cols1[] = { "Mã PT", "Người trả", "Mã HD", "Thời gian", "Khách hàng", "Tổng tiền", "Trạng thái" };
		modelTblPhieuTra = new DefaultTableModel(cols1, 0);

		tblPhieuTra = new JTable(modelTblPhieuTra);
		tblPhieuTra.setFont(new Font("Tahoma", Font.PLAIN, 18));
		tblPhieuTra.setOpaque(false);
		tblPhieuTra.setBackground(new Color(0, 0, 0, 0));
		tblPhieuTra.setShowGrid(false);
		tblPhieuTra.setRowHeight(30);
		tblPhieuTra.setRowMargin(13);
		tblPhieuTra.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        
		scrDSPhieuTra = new JScrollPane(tblPhieuTra);
		scrDSPhieuTra.setBounds(41, 480, 962, 234);
		scrDSPhieuTra.setOpaque(false);
		scrDSPhieuTra.getViewport().setOpaque(false);
		scrDSPhieuTra.setBorder(null);
		
		pnlDSPhieuTra.add(scrDSPhieuTra);
        
        // bảng chi tiết phiếu trả
        
		String cols2[] = { "Ngày duyệt phiếu", "Tên hàng", "Số lượng", "Thành tiền", "Lí do", "Trạng thái", };
		modelTblCTPhieuTra = new DefaultTableModel(cols2, 0);

		tblCTPhieuTra = new JTable(modelTblCTPhieuTra);
		tblCTPhieuTra.setFont(new Font("Tahoma", Font.PLAIN, 18));
		tblCTPhieuTra.setOpaque(false);
		tblCTPhieuTra.setBackground(new Color(0, 0, 0, 0));
		tblCTPhieuTra.setShowGrid(false);
		tblCTPhieuTra.setRowHeight(30);
		tblCTPhieuTra.setRowMargin(13);
		tblCTPhieuTra.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        
		scrCTPhieuTra = new JScrollPane(tblCTPhieuTra);
		scrCTPhieuTra.setBounds(41, 480, 962, 234);
		scrCTPhieuTra.setOpaque(false);
		scrCTPhieuTra.getViewport().setOpaque(false);
		scrCTPhieuTra.setBorder(null);
		
		pnlCTPhieuTra.add(scrCTPhieuTra);
		
		// du lieu giả
		
		modelTblPhieuTra.addRow(new Object[]{"PT000001", "Chu Anh Khôi", "HD-20251003-0001", "2025-10-15", "Công ty Minh Tâm", "1.250.000", "Đã xử lý"});
		modelTblPhieuTra.addRow(new Object[]{"PT000002", "Chu Anh Khôi", "HD-20251003-0002", "2025-10-15", "Nguyễn Thị Hoa",   "820.000",   "Chờ duyệt"});
		modelTblPhieuTra.addRow(new Object[]{"PT000003", "Chu Anh Khôi", "HD-20251003-0003", "2025-10-16", "Phạm Anh Khoa",    "560.000",   "Đã xử lý"});

		
		
		modelTblCTPhieuTra.addRow(new Object[]{"2025-10-16", "Thuốc ho", 2, "300.000", "Sai thuốc", "Đã xử lý"});
		modelTblCTPhieuTra.addRow(new Object[]{"2025-10-16", "Băng cá nhân",  1, "10.000", "Thiếu 1 băng", "Đã xử lý"});


    }
}
