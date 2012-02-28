package de.ichmann.markusw.java.apps.freenono.board;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import de.ichmann.christianw.java.components.dotmatrix.DotMatrix;
import de.ichmann.christianw.java.components.dotmatrix.Emblem;
import de.ichmann.markusw.java.apps.freenono.model.Game;

public class StatusComponent extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1283871798919081849L;

	private DotMatrix displayTime;
	private Emblem remainingTime;
	
	private Game game;

	private final SimpleDateFormat timeFormatter = new SimpleDateFormat("mm:ss");
	String timeLeft = "00:00";
	DecimalFormat df;
	private int minutes = 30;
	private int seconds = 0;

	private Timer t;

	public StatusComponent(Game game) {
		this.game = game;
		
		// set layout
		//GridLayout layout = new GridLayout(3, 1);
		FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 40, 15);
		this.setLayout(layout);
		
		// set border
		// Border border = new BevelBorder(BevelBorder.RAISED);
		Border border = new EtchedBorder(EtchedBorder.RAISED);
		this.setBorder(border);

		// format and display time
		game.getTimeLeft();
		df = new DecimalFormat("00");
		remainingTime = new Emblem(df.format(minutes) + ":"
				+ df.format(seconds), 1, 0);
		displayTime = new DotMatrix(36, 8);
		displayTime.addEmblem(remainingTime);
		this.add(displayTime);

		// build statusComponent
		JLabel jlabel = new JLabel();
		jlabel.setText("Fehler: " + Integer.toString(42));
		jlabel.setFont(new Font("FreeSans", Font.PLAIN, 18));
		this.add(jlabel);

		// set timer
		t = new Timer(1000, this);
		t.start();
	}

//	public void refreshTime() {
//		if (game.usesMaxTime()) {
//			timeLeft = timeFormatter.format(game.getTimeLeft());
//		} else {
//			timeLeft = timeFormatter.format(game.getElapsedTime());
//		}
//	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (seconds == 0) {
			seconds = 59;
			minutes -= 1;
		} else {
			seconds -= 1;
		}
		remainingTime.setText(df.format(minutes) + ":" + df.format(seconds));
		displayTime.refresh();
	}

}
