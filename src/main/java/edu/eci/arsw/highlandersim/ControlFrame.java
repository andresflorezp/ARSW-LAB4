package edu.eci.arsw.highlandersim;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Segment;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.JScrollBar;

public class ControlFrame extends JFrame {

	private static final int DEFAULT_IMMORTAL_HEALTH = 100;
	private static final int DEFAULT_DAMAGE_VALUE = 10;

	private JPanel contentPane;

	public volatile static List<Immortal> immortals;

	private JTextArea output;
	private JLabel statisticsLabel;
	private JScrollPane scrollPane;
	private JTextField numOfImmortals;
	public volatile static List<Immortal> immortalsLives = new ArrayList();;
	public static Object monitor = new Object();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ControlFrame frame = new ControlFrame();
					/*
					 * List<Immortal> immortals2 = new ArrayList<>(); for(Immortal
					 * im:immortalsLives) { if(im.getHealth()>0)immortals2.add(im); } immortals =
					 * immortalsLives;
					 */
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ControlFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 647, 248);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JToolBar toolBar = new JToolBar();
		contentPane.add(toolBar, BorderLayout.NORTH);

		final JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				immortals = setupInmortals();

				if (immortals != null) {
					for (Immortal im : immortals) {
						im.start();
					}
				}

				btnStart.setEnabled(false);

			}
		});
		toolBar.add(btnStart);

		JButton btnPauseAndCheck = new JButton("Pause and check");
		btnPauseAndCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				/*
				 * COMPLETAR
				 */

				int sum = 0;

				for (Immortal im : immortals) {
					im.detener();
				}

				for (Immortal im : immortals) {
					sum += im.getHealth();
				}
				statisticsLabel.setText("<html>" + immortals.toString() + "<br>Health sum:" + sum);

				// statisticsLabel.setText("<html>" + immortals.toString() + "<br>Health sum:" +
				// sum);

				for (Immortal im : immortals) {
					im.seguir();
				}

				synchronized (this) {

					this.notifyAll();
				}
				for (Immortal im : immortals)
					im.parar();

			}
		});
		toolBar.add(btnPauseAndCheck);

		JButton btnResume = new JButton("Resume");

		btnResume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (Immortal im : immortals)
					im.avanzar();
				synchronized (monitor) {
					monitor.notifyAll();
				}
			}
		});

		toolBar.add(btnResume);

		JLabel lblNumOfImmortals = new JLabel("num. of immortals:");
		toolBar.add(lblNumOfImmortals);

		numOfImmortals = new JTextField();
		numOfImmortals.setText("3");
		toolBar.add(numOfImmortals);
		numOfImmortals.setColumns(10);

		JButton btnStop = new JButton("STOP");

		btnStop.setForeground(Color.RED);
		toolBar.add(btnStop);
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (Immortal im : immortals)im.pararTodo();;
					
			}
		});
		scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		output = new JTextArea();
		output.setEditable(false);
		scrollPane.setViewportView(output);

		statisticsLabel = new JLabel("Immortals total health:");
		contentPane.add(statisticsLabel, BorderLayout.SOUTH);

	}

	public List<Immortal> setupInmortals() {

		ImmortalUpdateReportCallback ucb = new TextAreaUpdateReportCallback(output, scrollPane);

		try {
			int ni = Integer.parseInt(numOfImmortals.getText());

			List<Immortal> il = new LinkedList<Immortal>();

			for (int i = 0; i < ni; i++) {
				Immortal i1 = new Immortal("im" + i, il, DEFAULT_IMMORTAL_HEALTH, DEFAULT_DAMAGE_VALUE, ucb);
				il.add(i1);
			}
			return il;
		} catch (NumberFormatException e) {
			JOptionPane.showConfirmDialog(null, "Número inválido.");
			return null;
		}

	}

}

class TextAreaUpdateReportCallback implements ImmortalUpdateReportCallback {

	JTextArea ta;
	JScrollPane jsp;

	public TextAreaUpdateReportCallback(JTextArea ta, JScrollPane jsp) {
		this.ta = ta;
		this.jsp = jsp;
	}

	@Override
	public void processReport(String report) {
		ta.append(report);

		// move scrollbar to the bottom
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JScrollBar bar = jsp.getVerticalScrollBar();
				bar.setValue(bar.getMaximum());
			}
		});

	}

}