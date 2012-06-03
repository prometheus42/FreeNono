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
package org.freenono.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import java.awt.ComponentOrientation;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;
import org.freenono.board.BoardComponent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEvent;
import org.freenono.event.GameEventHelper;
import org.freenono.model.Course;
import org.freenono.model.Game;
import org.freenono.model.GameStatistics;
import org.freenono.model.Manager;
import org.freenono.model.Nonogram;
import org.freenono.model.RandomNonogram;
import org.freenono.model.Settings;
import org.freenono.serializer.CourseFormatException;
import org.freenono.sound.AudioProvider;

public class MainUI extends JFrame {

	private static final long serialVersionUID = 3834029197472615118L;

	private static Logger logger = Logger.getLogger(MainUI.class);

	private GameAdapter gameAdapter = new GameAdapter() {

		public void StateChanged(GameEvent e) {

			boolean isSolved = true;
			switch (e.getNewState()) {
			case gameOver:
				isSolved = false;

			case solved:
				// set text for status bar
				if (isSolved)
					statusBarText.setText(Messages.getString("MainUI.StatusBarWon"));
				else
					statusBarText.setText(Messages.getString("MainUI.StatusBarLost"));

				stopButton.setEnabled(false);
				pauseButton.setEnabled(false);
				getCurrentGame().solveGame();
				boardComponent.solveGame();
				GameOverUI ui = new GameOverUI(getCurrentGame(), boardComponent.getPreviewArea(), isSolved);
				ui.setVisible(true);
				break;

			case paused:
				statusBarText.setText(Messages.getString("MainUI.StatusBarPause"));
				break;

			case running:
				statusBarText.setText(Messages.getString("MainUI.StatusBarRunning"));
				break;

			default:
				break;
			}

		}

		public void ProgramControl(GameEvent e) {

			switch (e.getPct()) {
			case START_GAME:
				performStart();
				break;
			case STOP_GAME:
				performStop();
				break;
			case RESUME_GAME:
				performResume();
				break;
			case PAUSE_GAME:
				performPause();
				break;
			case RESTART_GAME:
				performRestart();
				break;
			case SHOW_OPTIONS:
				showOptions();
				break;
			case SHOW_ABOUT:
				showAbout();
				break;
			case QUIT_PROGRAMM:
				performExit();
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
	private GameStatistics currentStatistics = null;

	private JPanel jContentPane = null;
	// TODO: Should the statusBar be a separate class which inherits JLabel???
	private JToolBar statusBar = null;
	private JMenuItem statusBarText = null;
	private JToolBar toolBar = null;
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
		} catch (NullPointerException e) {
			// TODO handle exception correct
			// TODO add log or user message
			logger.error("Manager could not be instantiated because of an invalid argument. " + e.getMessage());
			manager = null;
			System.exit(1);
		} catch (FileNotFoundException e) {
			// TODO handle exception correct
			// TODO add log or user message
			logger.error("Manager could not be instantiated because an needed file was not found. " + e.getMessage());
			manager = null;
			System.exit(1);
		} catch (IOException e) {
			// TODO handle exception correct
			// TODO add log or user message
			logger.error("Manager could not be instantiated because of an IO exception. " + e.getMessage());
			manager = null;
			System.exit(1);
		}

		// instantiate audio provider for game sounds
		audioProvider = new AudioProvider(manager.getSettings().getPlayAudio());
		audioProvider.setEventHelper(eventHelper);

		// initialize MainUI
		initialize();

		// add component Listener for handling the resize operation
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				Component c = (Component) e.getSource();
				handleResize(c.getSize());
			}
		});
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(900, 900);
		this.setLocationRelativeTo(null);
		this.setName("mainUI");
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}
			@Override
			public void windowClosing(WindowEvent e) {
				performExit();
			}		
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
		});
		this.setContentPane(getJContentPane());
		this.setTitle(Messages.getString("MainUI.Title"));
	}

	private void handleResize(Dimension newSize) {
		if (boardComponent != null) {
			boardComponent.handleResize(new Dimension((int) newSize.getWidth(), (int) (newSize.height - toolBar.getHeight() - statusBar.getHeight())));
		}
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
			boardPanel = new JPanel() {
				private static final long serialVersionUID = -5144877072997396393L;

				protected void paintComponent(Graphics g) {
					Graphics2D g2 = (Graphics2D) g;
					BufferedImage cache = null;
					if (cache == null || cache.getHeight() != getHeight()) {
						cache = new BufferedImage(2, getHeight(), BufferedImage.TYPE_INT_RGB);
						Graphics2D g2d = cache.createGraphics();

						GradientPaint paint = new GradientPaint(0, 0, new Color(143, 231, 200), 0, getHeight(), Color.WHITE);
						g2d.setPaint(paint);
						g2d.fillRect(0, 0, 2, getHeight());
						g2d.dispose();
					}
					g2.drawImage(cache, 0, 0, getWidth(), getHeight(), null);
				}
			};
		} else {
			boardPanel.remove(boardComponent);
		}

		// calculating maximum size for boardComponent
		int boardHeight = this.getHeight() - toolBar.getHeight() - statusBar.getHeight();
		int boardWidth = this.getWidth();

		boardComponent = new BoardComponent(currentGame, manager.getSettings().getHidePlayfield(), new Dimension(boardWidth, boardHeight));
		boardComponent.setEventHelper(eventHelper);
		boardPanel.add(boardComponent);
		jContentPane.add(boardPanel, BorderLayout.CENTER);

		boardComponent.focusPlayfield();

		this.validate();

	}

	public Nonogram getCurrentNonogram() {
		return currentNonogram;
	}

	public void setCurrentNonogram(Nonogram currentNonogram) {

		this.currentNonogram = currentNonogram;
		restartButton.setEnabled(currentNonogram != null);
	}

	private void performStart() {

		NonogramChooserUI nonoChooser = new NonogramChooserUI(manager);
		nonoChooser.setVisible(true);
		Nonogram choosenNonogram = nonoChooser.getResult();

		// set chosen Nonogram and get new Game to play
		setCurrentNonogram(choosenNonogram);
		if (choosenNonogram != null) {
			setCurrentGame(manager.createGame(getCurrentNonogram()));
			currentStatistics = new GameStatistics(choosenNonogram, eventHelper);
		} else {
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
		if (toolBar == null) {
			toolBar = new JToolBar();
			toolBar.setFloatable(false);
			toolBar.add(getStartButton());
			toolBar.add(getRestartButton());
			toolBar.add(getPauseButton());
			toolBar.add(getResumeButton());
			toolBar.add(getStopButton());
			toolBar.add(getOptionsButton());
			toolBar.add(getAboutButton());
			toolBar.add(getExitButton());
		}
		return toolBar;
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
			startButton.setIcon(new ImageIcon(getClass().getResource("/icon/button_start.png"))); //$NON-NLS-1$
			startButton.setToolTipText(Messages.getString("MainUI.StartTooltip")); //$NON-NLS-1$
			startButton.setDisabledIcon(new ImageIcon(getClass().getResource("/icon/button_start2.png"))); //$NON-NLS-1$
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
			pauseButton.setToolTipText(Messages.getString("MainUI.PauseTooltip")); //$NON-NLS-1$
			pauseButton.setIcon(new ImageIcon(getClass().getResource("/icon/button_pause.png"))); //$NON-NLS-1$
			pauseButton.setText(""); //$NON-NLS-1$
			pauseButton.setEnabled(false);
			pauseButton.setDisabledIcon(new ImageIcon(getClass().getResource("/icon/button_pause2.png"))); //$NON-NLS-1$
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
			resumeButton.setToolTipText(Messages.getString("MainUI.ResumeTooltip")); //$NON-NLS-1$
			resumeButton.setIcon(new ImageIcon(getClass().getResource("/icon/button_resume.png"))); //$NON-NLS-1$
			resumeButton.setText(""); //$NON-NLS-1$
			resumeButton.setEnabled(false);
			resumeButton.setDisabledIcon(new ImageIcon(getClass().getResource("/icon/button_resume2.png"))); //$NON-NLS-1$
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
			stopButton.setIcon(new ImageIcon(getClass().getResource("/icon/button_stop.png"))); //$NON-NLS-1$
			stopButton.setText(""); //$NON-NLS-1$
			stopButton.setEnabled(false);
			stopButton.setDisabledIcon(new ImageIcon(getClass().getResource("/icon/button_stop2.png"))); //$NON-NLS-1$
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
			restartButton.setToolTipText(Messages.getString("MainUI.RestartTooltip")); //$NON-NLS-1$
			restartButton.setIcon(new ImageIcon(getClass().getResource("/icon/button_restart.png"))); //$NON-NLS-1$
			restartButton.setText(""); //$NON-NLS-1$
			restartButton.setEnabled(false);
			restartButton.setDisabledIcon(new ImageIcon(getClass().getResource("/icon/button_restart2.png"))); //$NON-NLS-1$
			restartButton.addActionListener(new java.awt.event.ActionListener() {
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
			exitButton.setIcon(new ImageIcon(getClass().getResource("/icon/button_exit.png"))); //$NON-NLS-1$
			exitButton.setEnabled(true);
			exitButton.setDisabledIcon(new ImageIcon(getClass().getResource("/icon/button_exit2.png"))); //$NON-NLS-1$
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
			aboutButton.setDisabledIcon(new ImageIcon(getClass().getResource("/icon/button_about2.png"))); //$NON-NLS-1$
			aboutButton.setIcon(new ImageIcon(getClass().getResource("/icon/button_about.png"))); //$NON-NLS-1$
			aboutButton.setText(""); //$NON-NLS-1$
			aboutButton.setComponentOrientation(ComponentOrientation.UNKNOWN);
			aboutButton.setToolTipText(Messages.getString("MainUI.AboutTooltip")); //$NON-NLS-1$
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
			optionsButton.setToolTipText(Messages.getString("MainUI.OptionsTooltip")); //$NON-NLS-1$
			optionsButton.setDisabledIcon(new ImageIcon(getClass().getResource("/icon/button_config2.png"))); //$NON-NLS-1$
			optionsButton.setIcon(new ImageIcon(getClass().getResource("/icon/button_config.png"))); //$NON-NLS-1$
			optionsButton.setText(""); //$NON-NLS-1$
			optionsButton.setEnabled(true);
			optionsButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					showOptions();
				}
			});
		}
		return optionsButton;
	}

}