package dao;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.omg.PortableServer.ID_ASSIGNMENT_POLICY_ID;

import bean.Usuario;
import connection.ConnectionFactory;
import view.TelaCadastro;

public class UsuarioDAO {

	public boolean checkLogin(String login, String senha) {

		Connection con = ConnectionFactory.getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		boolean check = false;

		try {
			stmt = con.prepareStatement("SELECT * FROM usuario WHERE login = ? and senha = ?");
			stmt.setString(1, login);
			stmt.setString(2, senha);
			rs = stmt.executeQuery();

			if (rs.next()) {
				check = true;
			}
		} catch (SQLException ex) {
			Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}
		return check;
	}
	

	private static String SQL_CHECK = "SELECT login FROM usuario WHERE login = ?;";
	private static String SQL_INSERT = "INSERT INTO usuario (login,senha) VALUES (?, ?);";
	
	public void check(Usuario usuario) {

		Connection con = ConnectionFactory.getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = con.prepareStatement(SQL_CHECK);
			stmt.setString(1, usuario.getLogin());
			rs = stmt.executeQuery();
			if (rs.next()) {
				String login = rs.getString("login");
				JOptionPane.showMessageDialog(null, "Usuario já existe");
			} else {
				stmt = con.prepareStatement(SQL_INSERT);
				stmt.setString(1, usuario.getLogin());
				stmt.setString(2, usuario.getSenha());
				stmt.execute();
				stmt.close();
				JOptionPane.showMessageDialog(null, "Login " + TelaCadastro.textLogin.getText() + " adicionado");
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
}
