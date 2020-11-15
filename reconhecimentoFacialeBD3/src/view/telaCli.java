package view;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;

public class telaCli extends JFrame {

	private JPanel contentPane;

	public telaCli() {
		iniComp();
	}

	public void iniComp() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 100);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblParabnsVocEntrou = new JLabel("Parab\u00E9ns voc\u00EA entrou!");
		lblParabnsVocEntrou.setFont(new Font("Arial", Font.BOLD, 25));
		lblParabnsVocEntrou.setHorizontalAlignment(SwingConstants.CENTER);
		lblParabnsVocEntrou.setBounds(0, 0, 434, 61);
		contentPane.add(lblParabnsVocEntrou);
	}
}
