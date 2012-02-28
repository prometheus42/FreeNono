/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2010 Markus Wichmann
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 *****************************************************************************/
package de.ichmann.markusw.java.apps.freenono.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import de.ichmann.markusw.java.apps.freenono.board.BoardComponent;
import de.ichmann.markusw.java.apps.freenono.event.GameAdapter;
import de.ichmann.markusw.java.apps.freenono.event.GameEvent;
import de.ichmann.markusw.java.apps.freenono.event.GameEventHelper;
import de.ichmann.markusw.java.apps.freenono.exception.InvalidArgumentException;
import de.ichmann.markusw.java.apps.freenono.model.Game;
import de.ichmann.markusw.java.apps.freenono.model.Manager;
import de.ichmann.markusw.java.apps.freenono.model.Nonogram;
import de.ichmann.markusw.java.apps.freenono.model.RandomNonogram;
import de.ichmann.markusw.java.apps.freenono.model.Settings;
import de.ichmann.markusw.java.apps.freenono.sound.AudioProvider;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import java.awt.ComponentOrientation;

import org.apache.log4j.Logger;

public class MainUI extends JFrame {

	private static final long serialVersionUID = 3834029197472615118L;

	private static Logger logger = Logger.getLogger(MainUI.class);

	private GameAdapter gameAdapter = new GameAdapter() {

		public void OptionsChanged(GameEvent e) {
			System.out.println("optionChange");
		}
		
		public void StateChanged(GameEvent e) {

			boolean isSolved = true;
			switch (e.getNewState()) {
			case gameOver:
				isSolved = false;
				
			case solved:
				// set text for status bar
				if (isSolved)
					statusBarText.setText("Spiel gewonnen!");
				else
					statusBarText.setText("Spiel verloren!");
				
				stopButton.setEnabled(false);
				pauseButton.setEnabled(false);
				getCurrentGame().solveGame();
				boardComponent.solveGame();
				GameOverUI ui = new GameOverUI(getCurrentGame(),
						boardComponent.getPreviewArea(), isSolved);
				ui.setVisible(true);
				break;
				
			case paused:
				statusBarText.setText("Spiel pausiert...");
				break;
				
			case running:
				statusBarText.setText("Spiel läuft...");
				break;
				
			default:
				break;
			}
			
		}
		
	};

	private GameEventHelper eventHelper = new GameEventHelper();
	private Manager manager = null;
	private Game currentGame = null;
	private Nonogram currentNonogram = null;
	private AudioProvider audioProvider = null;

	private JPanel jContentPane = null;
	private JToolBar statusBar = null;
	private JMenuItem statusBarText = null;
	private JToolBar jJToolBarBar = null;
	private BoardComponent boardComponent = null;
	private JPanel boardPanel = null;

	private JButton startButton = null;
	private JButton pauseButton = null;
	private JButton resumeButton = null;
	private JButton stopButton = null;
	private JButton restartButton = null;
	private JButton exitButton = null;
	private JButton aboutButton = null;
	private JButton optionsButton = null;

	/**
	 * This is the default constructor
	 */
	public MainUI() {
		super();

		// show splash screen
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SplashScreen splash = new SplashScreen();
				splash.setVisible(true);
			}
		});

		// instantiate GameEventHelper add own gameAdapter
		eventHelper = new GameEventHelper();
		eventHelper.addGameListener(gameAdapter);

		// instantiate game manager
		try {
			manager = new Manager(eventHelper);
		} catch (InvalidArgumentException e) {
			// TODO handle exception correct
			// TODO add log or user message
			manager = null;
		} catch (FileNotFoundException e) {
			// TODO handle exception correct
			// TODO add log or user message
			manager = null;
		} catch (IOException e) {
			// TODO handle exception correct
			// TODO add log or user message
			manager = null;
		}

		// instantiate audio provider for game sounds
		audioProvider = new AudioProvider(manager.getSettings().getPlayAudio());
		audioProvider.setEventHelper(eventHelper);

		// initialize MainUI
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(812, 941);
		// [width=712,height=841] at boardDimension(700,700)
		// [width=812,height=941] at boardDimension(800,800)
		this.setLocationRelativeTo(null);
		this.setName("mainUI"); //$NON-NLS-1$
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(getJContentPane());
		this.setTitle(Messages.getString("MainUI.Title")); //$NON-NLS-1$
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJJToolBarBar(), BorderLayout.NORTH);
			jContentPane.add(getStatusBar(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes statusBar
	 * 
	 * @return javax.swing.JToolBar
	 */
	private JToolBar getStatusBar() {
		if (statusBar == null) {
			statusBar = new JToolBar();
			statusBar.add(getStatusBarText());
		}
		return statusBar;
	}

	/**
	 * This method initializes statusBarText
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getStatusBarText() {
		if (statusBarText == null) {
			statusBarText = new JMenuItem();
		}
		return statusBarText;
	}

	public Game getCurrentGame() {
		return currentGame;
	}

	public void setCurrentGame(Game game) {

		this.currentGame = game;

		if (this.currentGame != null) {

			currentGame.setEventHelper(eventHelper);
			pauseButton.setEnabled(true);
			stopButton.setEnabled(true);

			buildBoard();
			boardComponent.startGame();

		} else {

			pauseButton.setEnabled(false);
			stopButton.setEnabled(false);

			// TODO: stopGame() was called in the old Board UI, but that is not
			// possible any longer???
			// boardComponent.stopGame();
		}
	}

	private void buildBoard() {
		if (boardPanel == null) {
			boardPanel = new JPanel();
			// int boardHeight = this.getHeight() - jJToolBarBar.getHeight()
			// - statusBar.getHeight();
			// boardPanel.setSize(new Dimension(boardHeight, boardHeight));
			// boardPanel.setMinimumSize(new Dimension(400, 400));
			// boardPanel.setPreferredSize(new Dimension(400, 400));
		}
		if (boardComponent != null) {
			boardPanel.remove(boardComponent);
		}
		boardComponent = new BoardComponent(currentGame, manager.getSettings()
				.getHidePlayfield(), new Dimension(800, 800));
		boardComponent.setEventHelper(eventHelper);
		boardPanel.add(boardComponent);
		jContentPane.add(boardPanel, BorderLayout.WEST);

		boardComponent.focusPlayfield();

		this.pack();
	}

	public Nonogram getCurrentNonogram() {
		return currentNonogram;
	}

	public void setCurrentNonogram(Nonogram currentNonogram) {

		this.currentNonogram = currentNonogram;
		restartButton.setEnabled(currentNonogram != null);
	}

	private void performStart() {

		// create UI and fill tree
		NonogramChooserUI nonoChooser = new NonogramChooserUI();

		String[] dirs = manager.getNonogramDirList().toArray(new String[0]);
		for (int i = 0; i < dirs.length; i++) {
			Object[] array = manager.getNonogramList(dirs[i]).toArray();
			nonoChooser.addNonogramsToTree(dirs[i], array);
		}

		// show nonogramChooser UI
		nonoChooser.setVisible(true);

		// TODO: clean up these ifs and switchs...
		if (nonoChooser.isValidOptions()) {

			// evaluate chosen options
			Nonogram choosenNonogram = null;

			switch (nonoChooser.getType()) {
			case 0:
				// nonogram by file
				choosenNonogram = nonoChooser.getChoosenNono();
				break;
			case 1:
				// random nonogram
				RandomNonogram randomNonogram = new RandomNonogram();
				choosenNonogram = randomNonogram.createRandomNonogram(
						nonoChooser.getSliderHeight(),
						nonoChooser.getSliderWidth(),
						nonoChooser.getRandomType());
				break;
			case 2:
				// TODO: nonogram by seed
				break;
			default:
				break;
			}

			// start choosen nonogram
			setCurrentNonogram(choosenNonogram);
			if (choosenNonogram != null) {
				setCurrentGame(manager.createGame(getCurrentNonogram()));
			} else {
				setCurrentGame(null);
			}
		} else {
			setCurrentNonogram(null);
			setCurrentGame(null);
		}
	}

	private void performRestart() {

		if (getCurrentNonogram() != null) {
			setCurrentGame(manager.createGame(getCurrentNonogram()));
		}
		// TODO check implementation
	}

	private void performPause() {

		boardComponent.pauseGame();
		pauseButton.setEnabled(false);
		resumeButton.setEnabled(true);
		// TODO check implementation
	}

	private void performResume() {

		boardComponent.resumeGame();
		pauseButton.setEnabled(true);
		resumeButton.setEnabled(false);
		// TODO check implementation
	}

	private void performStop() {

		boardComponent.stopGame();
		resumeButton.setEnabled(false);
		restartButton.setEnabled(true);
		setCurrentGame(null);
		setCurrentNonogram(null);
		// TODO check implementation
	}

	private void performExit() {
		// TODO implement additional user question
		manager.quitProgram();
		audioProvider.quitProgram();
		this.setVisible(false);
		this.dispose();
	}

	private void showAbout() {
		AboutUI ui = new AboutUI(this);
		ui.setVisible(true);
	}

	private void showOptions() {
		Settings s = manager.getSettings();
		OptionsUI ui = new OptionsUI(this, s);
		ui.setVisible(true);
		s.toString();
	}

	/**
	 * This method initializes jJToolBarBar
	 * 
	 * @return javax.swing.JToolBar
	 */
	private JToolBar getJJToolBarBar() {
		if (jJToolBarBar == null) {
			jJToolBarBar = new JToolBar();
			jJToolBarBar.setFloatable(false);
			jJToolBarBar.add(getStartButton());
			jJToolBarBar.add(getRestartButton());
			jJToolBarBar.add(getPauseButton());
			jJToolBarBar.add(getResumeButton());
			jJToolBarBar.add(getStopButton());
			jJToolBarBar.add(getOptionsButton());
			jJToolBarBar.add(getAboutButton());
			jJToolBarBar.add(getExitButton());
		}
		return jJToolBarBar;
	}

	/**
	 * This method initializes startButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getStartButton() {
		if (startButton == null) {
			startButton = new JButton();
			startButton.setText(""); //$NON-NLS-1$
			startButton.setIcon(new ImageIcon(getClass().getResource(
					"/icon/button_start.png"))); //$NON-NLS-1$
			startButton.setToolTipText(Messages
					.getString("MainUI.StartTooltip")); //$NON-NLS-1$
			startButton.setDisabledIcon(new ImageIcon(getClass().getResource(
					"/icon/button_start2.png"))); //$NON-NLS-1$
			startButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					performStart();
				}
			});
		}
		return startButton;
	}

	/**
	 * This method initializes pauseButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getPauseButton() {
		if (pauseButton == null) {
			pauseButton = new JButton();
			pauseButton.setToolTipText(Messages
					.getString("MainUI.PauseTooltip")); //$NON-NLS-1$
			pauseButton.setIcon(new ImageIcon(getClass().getResource(
					"/icon/button_pause.png"))); //$NON-NLS-1$
			pauseButton.setText(""); //$NON-NLS-1$
			pauseButton.setEnabled(false);
			pauseButton.setDisabledIcon(new ImageIcon(getClass().getResource(
					"/icon/button_pause2.png"))); //$NON-NLS-1$
			pauseButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					performPause();
				}
			});
		}
		return pauseButton;
	}

	/**
	 * This method initializes resumeButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getResumeButton() {
		if (resumeButton == null) {
			resumeButton = new JButton();
			resumeButton.setToolTipText(Messages
					.getString("MainUI.ResumeTooltip")); //$NON-NLS-1$
			resumeButton.setIcon(new ImageIcon(getClass().getResource(
					"/icon/button_resume.png"))); //$NON-NLS-1$
			resumeButton.setText(""); //$NON-NLS-1$
			resumeButton.setEnabled(false);
			resumeButton.setDisabledIcon(new ImageIcon(getClass().getResource(
					"/icon/button_resume2.png"))); //$NON-NLS-1$
			resumeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					performResume();
				}
			});
		}
		return resumeButton;
	}

	/**
	 * This method initializes stopButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getStopButton() {
		if (stopButton == null) {
			stopButton = new JButton();
			stopButton.setToolTipText(Messages.getString("MainUI.StopTooltip")); //$NON-NLS-1$
			stopButton.setIcon(new ImageIcon(getClass().getResource(
					"/icon/button_stop.png"))); //$NON-NLS-1$
			stopButton.setText(""); //$NON-NLS-1$
			stopButton.setEnabled(false);
			stopButton.setDisabledIcon(new ImageIcon(getClass().getResource(
					"/icon/button_stop2.png"))); //$NON-NLS-1$
			stopButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					performStop();
				}
			});
		}
		return stopButton;
	}

	/**
	 * This method initializes restartButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRestartButton() {
		if (restartButton == null) {
			restartButton = new JButton();
			restartButton.setToolTipText(Messages
					.getString("MainUI.RestartTooltip")); //$NON-NLS-1$
			restartButton.setIcon(new ImageIcon(getClass().getResource(
					"/icon/button_restart.png"))); //$NON-NLS-1$
			restartButton.setText(""); //$NON-NLS-1$
			restartButton.setEnabled(false);
			restartButton.setDisabledIcon(new ImageIcon(getClass().getResource(
					"/icon/button_restart2.png"))); //$NON-NLS-1$
			restartButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							performRestart();
						}
					});
		}
		return restartButton;
	}

	/**
	 * This method initializes exitButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getExitButton() {
		if (exitButton == null) {
			exitButton = new JButton();
			exitButton.setIcon(new ImageIcon(getClass().getResource(
					"/icon/button_exit.png"))); //$NON-NLS-1$
			exitButton.setEnabled(true);
			exitButton.setDisabledIcon(new ImageIcon(getClass().getResource(
					"/icon/button_exit2.png"))); //$NON-NLS-1$
			exitButton.setText(""); //$NON-NLS-1$
			exitButton.setToolTipText(Messages.getString("MainUI.ExitTooltip")); //$NON-NLS-1$
			exitButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					performExit();
				}
			});
		}
		return exitButton;
	}

	/**
	 * This method initializes aboutButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAboutButton() {
		if (aboutButton == null) {
			aboutButton = new JButton();
			aboutButton.setEnabled(true);
			aboutButton.setDisabledIcon(new ImageIcon(getClass().getResource(
					"/icon/button_about2.png"))); //$NON-NLS-1$
			aboutButton.setIcon(new ImageIcon(getClass().getResource(
					"/icon/button_about.png"))); //$NON-NLS-1$
			aboutButton.setText(""); //$NON-NLS-1$
			aboutButton.setComponentOrientation(ComponentOrientation.UNKNOWN);
			aboutButton.setToolTipText(Messages
					.getString("MainUI.AboutTooltip")); //$NON-NLS-1$
			aboutButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					showAbout();
				}
			});
		}
		return aboutButton;
	}

	/**
	 * This method initializes optionsButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOptionsButton() {
		if (optionsButton == null) {
			optionsButton = new JButton();
			optionsButton.setComponentOrientation(ComponentOrientation.UNKNOWN);
			optionsButton.setToolTipText(Messages
					.getString("MainUI.OptionsTooltip")); //$NON-NLS-1$
			optionsButton.setDisabledIcon(new ImageIcon(getClass().getResource(
					"/icon/button_config2.png"))); //$NON-NLS-1$
			optionsButton.setIcon(new ImageIcon(getClass().getResource(
					"/icon/button_config.png"))); //$NON-NLS-1$
			optionsButton.setText(""); //$NON-NLS-1$
			optionsButton.setEnabled(true);
			optionsButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							showOptions();
						}
					});
		}
		return optionsButton;
	}

}
