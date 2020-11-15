package view;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imencode;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.FONT_HERSHEY_PLAIN;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.putText;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

import connection.ConnectionFactory;
import dao.UsuarioDAO;

public class TelaLogin extends JFrame {

	private JPanel contentPane;
	public static JTextField txtLogin;
	private JPasswordField txtSenha;

	public static void main(String args[]) {

		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Windows".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				TelaLogin dialog = new TelaLogin();
				dialog.addWindowListener(new java.awt.event.WindowAdapter() {
					@Override
					public void windowClosing(java.awt.event.WindowEvent e) {
						System.exit(0);
					}
				});
				dialog.setVisible(true);
			}
		});
	}

	public TelaLogin() {
		iniciaComp();

	}

	public void iniciaComp() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 301, 370);
		contentPane = new JPanel();
		setResizable(false);
		setLocationRelativeTo(null);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblLogin = new JLabel("Login");
		lblLogin.setFont(new Font("Arial", Font.PLAIN, 13));
		lblLogin.setForeground(Color.WHITE);
		lblLogin.setBounds(125, 60, 50, 15);
		contentPane.add(lblLogin);

		txtLogin = new JTextField();
		txtLogin.setBounds(50, 90, 200, 20);
		contentPane.add(txtLogin);
		txtLogin.setColumns(25);

		JLabel lblSenha = new JLabel("Senha");
		lblSenha.setFont(new Font("Arial", Font.PLAIN, 13));
		lblSenha.setForeground(Color.WHITE);
		lblSenha.setBounds(125, 150, 50, 15);
		contentPane.add(lblSenha);

		txtSenha = new JPasswordField();
		txtSenha.setBounds(50, 180, 200, 20);
		contentPane.add(txtSenha);

		JButton btnEntrar = new JButton("Entrar");
		btnEntrar.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnEntrarActionPerformed(evt);
			}
		});
		btnEntrar.setBounds(50, 225, 70, 25);
		contentPane.add(btnEntrar);

		JButton btnCadastrar = new JButton("Cadastrar");
		btnCadastrar.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnCadastrarActionPerformed(evt);
			}
		});
		btnCadastrar.setBounds(160, 225, 109, 25);
		contentPane.add(btnCadastrar);

		JLabel label = new JLabel("");
		label.setIcon(new ImageIcon(
				"C:\\Users\\leona\\Desktop\\Danki code\\reconhecimentoFacialeBD3\\src\\recursos\\aps300x370.jpg"));
		label.setBounds(0, 0, 294, 341);
		contentPane.add(label);

	}

	private void btnEntrarActionPerformed(java.awt.event.ActionEvent evt) {

		UsuarioDAO dao = new UsuarioDAO();

		if (dao.checkLogin(txtLogin.getText(), new String(txtSenha.getPassword()))) {
			new reconhecimentoFacial().setVisible(true);
			this.dispose();
		} else {
			JOptionPane.showMessageDialog(null, "Login ou Senha invalidos.");
		}
	}

	private void btnCadastrarActionPerformed(java.awt.event.ActionEvent evt) {

		new TelaCadastro().setVisible(true);
		this.dispose();
	}

}
