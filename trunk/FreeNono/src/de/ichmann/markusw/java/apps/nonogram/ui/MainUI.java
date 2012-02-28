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
package de.ichmann.markusw.java.apps.nonogram.ui;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import de.ichmann.markusw.java.apps.nonogram.event.GameListener;
import de.ichmann.markusw.java.apps.nonogram.exception.InvalidArgumentException;
import de.ichmann.markusw.java.apps.nonogram.model.Game;
import de.ichmann.markusw.java.apps.nonogram.model.GameState;
import de.ichmann.markusw.java.apps.nonogram.model.Manager;
import de.ichmann.markusw.java.apps.nonogram.model.Nonogram;
import de.ichmann.markusw.java.apps.nonogram.model.Settings;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.ComponentOrientation;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

public class MainUI extends JFrame {

	private static final long serialVersionUID = 3834029197472615118L;

	private static Logger logger = Logger.getLogger(MainUI.class); // @jve:decl-index=0:

	private GameListener gameListener = new GameListener() {

		@Override
		public void Timer() {
			boardControl.refreshTime();
			boardControl.repaint();
		}

		@Override
		public void StateChanged(GameState oldState, GameState newState) {
			boolean isSolved = true;

			switch (newState) {
			case gameOver:
				isSolved = false;
			case solved:
				stopButton.setEnabled(false);
				pauseButton.setEnabled(false);
				getCurrentGame().solveGame();
				boardControl.refresh();
				boardControl.repaint();
				GameOverUI ui = new GameOverUI(getCurrentGame(), boardControl
						.getPreviewImage(), isSolved);
				ui.setVisible(true);
				break;
			default:
				break;
			}

		}

		@Override
		public void FieldOccupied(int x, int y) {
		}

		@Override
		public void FieldMarked(int x, int y) {
		}
	};

	private Manager manager = null;
	private Game currentGame = null;
	private Nonogram currentNonogram = null;

	private JPanel jContentPane = null;
	private JToolBar statusBar = null;
	private JMenuItem statusBarText = null;
	private BoardControl boardControl = null;

	private JToolBar jJToolBarBar = null;

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

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SplashScreen splash = new SplashScreen();
				splash.setVisible(true);
			}
		});

		try {
			manager = new Manager();
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
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(800, 600);
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
			jContentPane.add(getBoardControl(), BorderLayout.CENTER);
			jContentPane.add(getStatusBar(), BorderLayout.SOUTH);
			jContentPane.add(getJJToolBarBar(), BorderLayout.NORTH);
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

		if (this.currentGame != null) {
			this.currentGame.removeGameListener(gameListener);
		}

		this.currentGame = game;

		if (this.currentGame != null) {
			currentGame.addGameListener(gameListener);
			pauseButton.setEnabled(true);
			stopButton.setEnabled(true);
			boardControl.startGame(game);
		} else {
			pauseButton.setEnabled(false);
			stopButton.setEnabled(false);
			boardControl.stopGame();
		}

	}

	public Nonogram getCurrentNonogram() {
		return currentNonogram;
	}

	public void setCurrentNonogram(Nonogram currentNonogram) {

		this.currentNonogram = currentNonogram;
		restartButton.setEnabled(currentNonogram != null);
	}

	private void performStart() {

		Object[] array = manager.getNonogramList().toArray();
		Object first = array != null & array.length > 0 ? array[0] : null;

		Object obj = JOptionPane.showInputDialog(this, "message", "title", //$NON-NLS-1$ //$NON-NLS-2$
				JOptionPane.QUESTION_MESSAGE, null, array, first);

		if (obj != null) {
			setCurrentNonogram((Nonogram) obj);
			setCurrentGame(manager.createGame(getCurrentNonogram()));

		} else {
			setCurrentNonogram(null);
			setCurrentGame(null);
		}
		// TODO check implementation
	}

	private void performRestart() {

		if (getCurrentNonogram() != null) {
			setCurrentGame(manager.createGame(getCurrentNonogram()));
		}
		// TODO check implementation
	}

	private void performPause() {

		boardControl.pauseGame();
		pauseButton.setEnabled(false);
		resumeButton.setEnabled(true);
		// TODO check implementation
	}

	private void performResume() {

		boardControl.resumeGame();
		pauseButton.setEnabled(true);
		resumeButton.setEnabled(false);
		// TODO check implementation
	}

	private void performStop() {

		setCurrentGame(null);
		setCurrentNonogram(null);
		// TODO check implementation
	}

	private void performExit() {
		// TODO implement additional user question
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
	 * This method initializes boardControl
	 * 
	 * @return javax.swing.JPanel
	 */
	private BoardControl getBoardControl() {
		if (boardControl == null) {
			boardControl = new BoardControl();
		}
		return boardControl;
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
			jJToolBarBar.add(getExitButton());
			jJToolBarBar.add(getAboutButton());
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

} // @jve:decl-index=0:visual-constraint="10,10"
