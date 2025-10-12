package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connectDB {
	public static Connection con = null;
	private static connectDB instance = new connectDB();

	public static connectDB getInstance() {
		return instance;
	}

	public void connect() throws SQLException {
		try {
			String url = "jdbc:sqlserver://localhost:1433;databaseName=QuanLyHieuThuoc;";
			String user = "sa";
			String password = "sapassword";
			con = DriverManager.getConnection(url, user, password);
			System.out.println("Kết nối thành công!");
		} catch (SQLException e) {
			System.err.println("Lỗi kết nối SQL Server: " + e.getMessage());
			throw e; // Ném lại ngoại lệ để lớp gọi xử lý
		}
	}

	public void disconnect() {
		if (con != null) {
			try {
				con.close();
				System.out.println("Ngắt kết nối thành công!");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static Connection getConnection() {
		try {
			// Kiểm tra nếu kết nối null hoặc đã đóng
			if (con == null || con.isClosed()) {
				getInstance().connect(); // Tạo kết nối mới
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}
}