package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connectDB {
	private static Connection con = null;
	private static connectDB instance = new connectDB();

	public static connectDB getInstance() {
		return instance;
	}

	public void connect() throws SQLException {
		// Prevent re-connecting if already connected
		if (con != null && !con.isClosed()) {
			return;
		}

		try {
			String url = "jdbc:sqlserver://localhost:1433;databaseName=QuanLyHieuThuoc;";
			String user = "sa";
			String password = "sapassword";
			con = DriverManager.getConnection(url, user, password);
			// System.out.println("Kết nối thành công!"); // Silenced to reduce noise
		} catch (SQLException e) {
			System.err.println("Lỗi kết nối SQL Server: " + e.getMessage());
			throw e;
		}
	}

	public void disconnect() {
		if (con != null) {
			try {
				con.close();
				System.out.println("Ngắt kết nối thành công!");
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				con = null; // Ensure reference is cleared
			}
		}
	}

	public static Connection getConnection() {
		try {
			if (con == null || con.isClosed()) {
				getInstance().connect();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}
}