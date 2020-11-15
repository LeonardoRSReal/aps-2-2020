package view;

import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imencode;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_face.EigenFaceRecognizer;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_face.FisherFaceRecognizer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

import bean.Usuario;
import dao.UsuarioDAO;

public class TelaCadastro extends JFrame {

	JPanel contentPane;
	public static JTextField textLogin;
	JPasswordField textSenha;
	JPasswordField textSenha2;
	JTextField textCadastrar;
	JLabel label_foto = new JLabel("");
	JLabel contagemLabel = new JLabel("");
	JButton btnCapturar = new JButton("Capturar");

	private TelaCadastro.DaemonThread myThread = null;
	VideoCapture webcam = null;
	Mat cameraImagem = new Mat();
	CascadeClassifier cascade = new CascadeClassifier("src\\recursos\\haarcascade_frontalface_alt.xml");
	LBPHFaceRecognizer recognizer = LBPHFaceRecognizer.create();
	BytePointer mem = new BytePointer();
	RectVector detectarFace = new RectVector();

	String root, PrNome, UltiNome, Profi, dataNasc;
	int numeroAmostras = 25, amostra = 1;

	public TelaCadastro() {
		initiComp();
		startCamera();
	}

	public void initiComp() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 400);
		JPanel contentPane = new JPanel();
		setResizable(false);
		setLocationRelativeTo(null);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblCaptureImagens = new JLabel("Capture 25 Fotos");
		lblCaptureImagens.setHorizontalAlignment(SwingConstants.CENTER);
		lblCaptureImagens.setFont(new Font("Arial", Font.PLAIN, 12));
		lblCaptureImagens.setForeground(Color.WHITE);
		lblCaptureImagens.setBounds(14, 204, 146, 23);
		contentPane.add(lblCaptureImagens);

		contagemLabel.setForeground(Color.WHITE);
		contagemLabel.setBounds(40, 238, 90, 50);
		contentPane.add(contagemLabel);

		JLabel lblLogin = new JLabel("Login");
		lblLogin.setForeground(Color.WHITE);
		lblLogin.setBounds(10, 37, 50, 15);
		lblLogin.setFont(new Font("Arial", Font.PLAIN, 14));
		contentPane.add(lblLogin);

		textLogin = new JTextField();
		textLogin.setBounds(10, 65, 150, 20);
		contentPane.add(textLogin);
		textLogin.setColumns(25);

		JLabel lblSenha = new JLabel("Senha");
		lblSenha.setForeground(Color.WHITE);
		lblSenha.setFont(new Font("Arial", Font.PLAIN, 14));
		lblSenha.setBounds(10, 94, 50, 15);
		contentPane.add(lblSenha);

		textSenha = new JPasswordField();
		textSenha.setBounds(10, 120, 150, 20);
		contentPane.add(textSenha);
		textSenha.setColumns(25);

		JLabel lblSenha2 = new JLabel("Confirmar Senha");
		lblSenha2.setForeground(Color.WHITE);
		lblSenha2.setFont(new Font("Arial", Font.PLAIN, 14));
		lblSenha2.setBounds(10, 147, 150, 15);
		contentPane.add(lblSenha2);

		textSenha2 = new JPasswordField();
		textSenha2.setBounds(10, 173, 150, 20);
		contentPane.add(textSenha2);
		textSenha2.setColumns(25);

		btnCapturar.setBounds(10, 299, 103, 25);
		contentPane.add(btnCapturar);

		JButton btnLimpar = new JButton("");
		btnLimpar.setIcon(new ImageIcon(
				"C:\\Users\\leona\\Desktop\\Danki code\\reconhecimentoFacialeBD3\\src\\recursos\\balde-de-lixo.png"));
		btnLimpar.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnLimparActionPerformed(evt);
			}
		});
		btnLimpar.setBounds(130, 299, 30, 25);
		contentPane.add(btnLimpar);

		JButton btnSair = new JButton("Sair");
		btnSair.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnSairActionPerformed(evt);
			}
		});
		btnSair.setBounds(40, 335, 90, 25);
		contentPane.add(btnSair);

		JLabel lblCadastrarUsuario = new JLabel("Cadastrar Login");
		lblCadastrarUsuario.setForeground(Color.WHITE);
		lblCadastrarUsuario.setFont(new Font("Arial", Font.PLAIN, 14));
		lblCadastrarUsuario.setBounds(20, 11, 140, 15);
		contentPane.add(lblCadastrarUsuario);

		JLabel label = new JLabel("");
		label.setIcon(new ImageIcon(
				"C:\\Users\\leona\\Desktop\\Danki code\\reconhecimentoFacialeBD3\\src\\recursos\\aps180x370.jpg"));
		label.setBounds(0, 0, 180, 370);
		contentPane.add(label);

		label_foto.setBounds(180, 0, 264, 370);
		contentPane.add(label_foto);

	}

	private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {
		textLogin.setText("");
		textSenha.setText("");
		textSenha2.setText("");
	}

	private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {

		new TelaLogin().setVisible(true);
		this.dispose();
	}

	class DaemonThread implements Runnable {
		protected volatile boolean runnable = false;

		@Override
		public void run() {
			synchronized (this) {
				while (runnable) {
					try {
						if (webcam.grab()) {
							webcam.retrieve(cameraImagem);
							Graphics g = label_foto.getGraphics();
							Mat imagemColor = new Mat();
							imagemColor = cameraImagem;

							Mat imagemCinza = new Mat();
							cvtColor(cameraImagem, imagemCinza, COLOR_BGRA2GRAY);

							RectVector facesDetectadas = new RectVector();
							cascade.detectMultiScale(imagemCinza, facesDetectadas, 1.1, 2, 0, new Size(160, 160),
									new Size(500, 500));

							for (int i = 0; i < facesDetectadas.size(); i++) {
								Rect dadosFace = facesDetectadas.get(0);
								rectangle(cameraImagem, dadosFace, new Scalar(0, 0, 255, 0));
								Mat face = new Mat(imagemCinza, dadosFace);
								opencv_imgproc.resize(face, face, new Size(160, 160));

								if (btnCapturar.getModel().isPressed()) {
									if (amostra <= numeroAmostras) {
										imwrite("src\\fotos\\" + textLogin.getText() + "." + amostra + ".jpg", face);

										contagemLabel.setText(String.valueOf(amostra) + "/25");
										amostra++;
									}

									if (amostra > 25) {
										trainFotos();
										inserirBanco();
										pararCamera();
									}
								}
							}

							imencode(".bmp", cameraImagem, mem);
							Image im = ImageIO.read(new ByteArrayInputStream(mem.getStringBytes()));
							BufferedImage buff = (BufferedImage) im;

							if (g.drawImage(buff, 0, 0, 365, 379, 0, 0, buff.getWidth(), buff.getHeight(), null)) {
								if (runnable == false) {
									System.out.println("Salve a foto");
									this.wait();
								}
							}
						}
					} catch (IOException ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(null, "Erro ao iniciar camera (IOEx) \n" + ex);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(null, "Erro ao iniciar camera (Interrupted) \n" + ex);
					}
				}
			}
		}
	}

	public void inserirBanco() {

		Usuario usuarios = new Usuario();

		usuarios.setLogin(textLogin.getText());
		usuarios.setSenha(new String(textSenha.getPassword()));
		usuarios.setSenha(new String(textSenha2.getPassword()));

		UsuarioDAO dao = new UsuarioDAO();

		if ((textLogin.getText().isEmpty()) || new String(textSenha.getPassword()).isEmpty()
				|| new String(textSenha2.getPassword()).isEmpty()) {

			JOptionPane.showMessageDialog(null, "Os campos não podem ficar vazios");

		} else if (new String(textSenha.getPassword()).equals(new String(textSenha2.getPassword()))) {

			dao.check(usuarios);

		} else {

			JOptionPane.showMessageDialog(null, "As senhas devem ser iguais");

		}

		textLogin.setText("");
		textSenha.setText("");
		textSenha2.setText("");
	}

	public void pararCamera() {
		myThread.runnable = false;
		webcam.release();
		dispose();
	}

	public void startCamera() {
		webcam = new VideoCapture(0);
		myThread = new TelaCadastro.DaemonThread();
		Thread t = new Thread(myThread);
		t.setDaemon(true);
		myThread.runnable = true;
		t.start();
	}

	public void trainFotos() {
		File diretorio = new File("src\\fotos\\");
		FilenameFilter filtro = (File dir, String nome) -> nome.endsWith(".jpg") || nome.endsWith(".png")
				|| nome.endsWith(".gif");

		File[] files = diretorio.listFiles(filtro);
		MatVector fotos = new MatVector(files.length); //precisa ter fotos nessa pasta \\src\\fotos\\
		//vou botar no c por enqaunto, dps vc muda
		Mat labels = new Mat(files.length, 1, CV_32SC1);
		IntBuffer labelsBuffer = labels.createBuffer();

		int contador = 0;
		for (File image : files) {
			Mat foto = imread(image.getAbsolutePath(), IMREAD_GRAYSCALE);
			int a = Integer.parseInt(image.getName().split("\\.")[1]);
			opencv_imgproc.resize(foto, foto, new Size(160, 160));

			fotos.put(contador, foto);
			labelsBuffer.put(contador, a);
			contador++;
		}

		FaceRecognizer eigenFaces = EigenFaceRecognizer.create(10, 0);
		FaceRecognizer fisherFaces = FisherFaceRecognizer.create();
		FaceRecognizer lbph = LBPHFaceRecognizer.create(1, 8, 8, 8, 12);
		
			eigenFaces.train(fotos, labels);
	        eigenFaces.save("src\\fotos\\classificadorEigenFaces.yml");
	        
	        fisherFaces.train(fotos, labels);
	        fisherFaces.save("src\\fotos\\classificadorFisherFaces.yml");
	        
	        lbph.train(fotos, labels);
	        lbph.save("src\\fotos\\classificadorLBPH.yml");
	}
}
