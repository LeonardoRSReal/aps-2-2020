package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionFactory {

	private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String URL = "jdbc:mysql://localhost:3306/apsrecfacial?useTimezone=true&serverTimezone=America/Sao_Paulo";
	private static final String USER = "root";
	private static final String PASS = "";

	public Statement stmt;
	public ResultSet rs;

	public Connection con;

	public static Connection getConnection() {

		try {
			Class.forName(DRIVER);

			return DriverManager.getConnection(URL, USER, PASS);

		} catch (ClassNotFoundException | SQLException ex) {
			throw new RuntimeException("Erro na conexão: ", ex);
		}
	}

	public static void closeConnection(Connection con) {

		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException ex) {
			Logger.getLogger(ConnectionFactory.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static void closeConnection(Connection con, PreparedStatement stmt) {

		closeConnection(con);

		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException ex) {
			Logger.getLogger(ConnectionFactory.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static void closeConnection(Connection con, PreparedStatement stmt, ResultSet rs) {

		closeConnection(con, stmt);

		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException ex) {
			Logger.getLogger(ConnectionFactory.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void executaSQL(String SQL) {
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(SQL);
		} catch (Exception ex) {
			System.out.println("Erro: " + ex);
		}
	}

}
