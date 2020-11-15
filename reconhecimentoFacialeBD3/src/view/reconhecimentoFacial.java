package view;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imencode;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.FONT_HERSHEY_PLAIN;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.putText;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;

import java.awt.BorderLayout;
import java.awt.EventQueue;
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
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
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
import org.bytedeco.opencv.opencv_face.EigenFaceRecognizer;
import org.bytedeco.opencv.opencv_face.FisherFaceRecognizer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

import bean.Usuario;
import connection.ConnectionFactory;
import dao.UsuarioDAO;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class reconhecimentoFacial extends JFrame {

	private JPanel contentPane;

	UsuarioDAO dao = new UsuarioDAO();

	private reconhecimentoFacial.DaemonThread myThread = null;
	VideoCapture webcam = null;
	Mat cameraImagem = new Mat();
	CascadeClassifier cascade = new CascadeClassifier("src\\recursos\\haarcascade_frontalface_alt.xml");

	// EigenFaceRecognizer recognizer = EigenFaceRecognizer.create();
	// FisherFaceRecognizer recognizer = FisherFaceRecognizer.create();
	LBPHFaceRecognizer recognizer = LBPHFaceRecognizer.create();

	BytePointer mem = new BytePointer();
	RectVector detectarFace = new RectVector();

	String login;
	JButton btnConectar = new JButton("Conectar");

	JLabel label_Rec = new JLabel("");

	public reconhecimentoFacial() {
		iniciaComp();
		// recognizer.read("src\\fotos\\classificadorEigenFaces.yml");
		// recognizer.read("src\\fotos\\classificadorFisherFaces.yml");
		recognizer.read("src\\fotos\\classificadorLBPH.yml");
		recognizer.setThreshold(80);
		startCamera();
	}

	public void iniciaComp() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 420);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		label_Rec.setBounds(0, 0, 285, 350);
		contentPane.add(label_Rec);
		btnConectar.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				new telaCli().setVisible(true);
			}
		});

		btnConectar.setBounds(86, 358, 89, 23);
		contentPane.add(btnConectar);
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
							Graphics g = label_Rec.getGraphics();
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

								IntPointer rotulo = new IntPointer(1);
								DoublePointer confianca = new DoublePointer(1);
								recognizer.predict(face, rotulo, confianca);
								int predicao = rotulo.get(0);
								String nome;
								
								if (predicao == -1) {	
									rectangle(cameraImagem, dadosFace, new Scalar(0, 0, 255, 3), 3, 0, 0);
									nome = "desconhecido";
									btnConectar.setEnabled(false);
									
								}else {
									nome = TelaLogin.txtLogin.getText() + " - " + confianca.get(0);
									btnConectar.setEnabled(true);
								}

								int x = Math.max(dadosFace.tl().x() - 10, 0);
								int y = Math.max(dadosFace.tl().y() - 10, 0);
								putText(cameraImagem, nome, new Point(x, y), FONT_HERSHEY_PLAIN, 1.4,
										new Scalar(0, 255, 0, 0));
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

	public void startCamera() {
		webcam = new VideoCapture(0);
		myThread = new reconhecimentoFacial.DaemonThread();
		Thread t = new Thread(myThread);
		t.setDaemon(true);
		myThread.runnable = true;
		t.start();

	}

	public void pararCamera() {
		myThread.runnable = false;
		webcam.release();
		dispose();
	}
}