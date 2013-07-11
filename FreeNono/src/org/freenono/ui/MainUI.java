/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2013 by FreeNono Development Team
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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.plaf.FontUIResource;

import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.freenono.RunUI;
import org.freenono.board.BoardPanel;
import org.freenono.board.BoardPreview;
import org.freenono.board.StatusComponent;
import org.freenono.event.GameAdapter;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.QuizEvent;
import org.freenono.event.ProgramControlEvent.ProgramControlType;
import org.freenono.event.GameEventHelper;
import org.freenono.event.StateChangeEvent;
import org.freenono.model.game_modes.GameMode_Quiz;
import org.freenono.provider.CollectionProvider;
import org.freenono.provider.NonogramProvider;
import org.freenono.quiz.Question;
import org.freenono.ui.common.Tools;
import org.freenono.controller.Settings;

/**
 * Shows the main window for the GUI.
 * 
 * @author Christian Wichmann, Markus Wichmann
 */
public class MainUI extends JFrame {

    private static final long serialVersionUID = 3834029197472615118L;

    private static Logger logger = Logger.getLogger(MainUI.class);

    public static final String DEFAULT_THUMBNAILS_PATH = System
            .getProperty("user.home")
            + Tools.FILE_SEPARATOR
            + ".FreeNono"
            + Tools.FILE_SEPARATOR + "thumbnails";

    private GameAdapter gameAdapter = new GameAdapter() {

        public void optionsChanged(final ProgramControlEvent e) {

            repaint();
        }

        public void stateChanged(final StateChangeEvent e) {

            final boolean isSolved;

            /*
             * Calling of method handleGameEnding is done by the awt event
             * dispatch thread. It handles the game end by showing game over
             * dialog when possible.
             * 
             * After the event thread has received the Runnable the remaining
             * event listeners waiting for game end are called.
             */
            switch (e.getNewState()) {
            case GAME_OVER:
                isSolved = false;
                gameRunning = false;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        handleGameEnding(isSolved);
                    }
                });
                break;

            case SOLVED:
                isSolved = true;
                gameRunning = false;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        handleGameEnding(isSolved);
                    }
                });
                break;

            case PAUSED:
                gameRunning = false;
                statusBarText.setText(Messages
                        .getString("MainUI.StatusBarPause"));
                break;

            case RUNNING:
                gameRunning = true;
                statusBarText.setText(Messages
                        .getString("MainUI.StatusBarRunning"));
                break;

            case USER_STOP:
                gameRunning = false;
                statusBarText.setText(Messages
                        .getString("MainUI.StatusBarStopped"));
                break;

            case NONE:
                break;

            default:
                assert false : e.getNewState();
                break;
            }
        }

        public void askQuestion(final QuizEvent e) {

            Question question = e.getQuestion();
            AskQuestionDialog aqd = new AskQuestionDialog(question, settings);

            // set answer to "0" if cancel button was pushed
            String answer = aqd.getAnswer();
            if (answer == null) {
                answer = "0";
            }
            ((GameMode_Quiz) e.getSource()).checkAnswer(question, answer);
        }
    };

    private GameEventHelper eventHelper = null;
    private Settings settings = null;
    private List<CollectionProvider> nonogramProvider = null;
    private NonogramProvider lastChosenNonogram = null;
    private boolean gameRunning = false;
    private boolean windowMinimized = false;

    private GraphicsDevice currentScreenDevice = null;

    private AboutDialog2 aboutDialog;
    private AboutDialog2 helpDialog;

    private JPanel contentPane = null;
    private JToolBar statusBar = null;
    private JMenuItem statusBarText = null;
    private JToolBar toolBar = null;

    private BoardPanel boardPanel = null;
    private StatusComponent statusField;

    private GridBagLayout layout;
    private GridBagConstraints constraints;

    private JButton startButton = null;
    private JButton coopButton = null;
    private JButton pauseButton = null;
    private JButton stopButton = null;
    private JButton restartButton = null;
    private JButton exitButton = null;
    private JButton aboutButton = null;
    private JButton helpButton = null;
    private JButton editButton = null;
    private JButton optionsButton = null;
    private JButton statisticsButton = null;

    /**
     * Initializes the main graphical user interface of FreeNono.
     * 
     * @param geh
     *            Game event helper to fire and receive events.
     * @param s
     *            Settings object.
     * @param np
     *            List of all available nonogram collections.
     */
    public MainUI(final GameEventHelper geh, final Settings s,
            final List<CollectionProvider> np) {

        super();

        this.eventHelper = geh;
        this.settings = s;
        this.nonogramProvider = np;

        eventHelper.addGameListener(gameAdapter);

        // find screen on which MainUI is shown...
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        currentScreenDevice = getGraphicsConfiguration().getDevice();
        GraphicsDevice[] gs = ge.getScreenDevices();
        for (GraphicsDevice screen : gs) {
            logger.debug(screen.getDefaultConfiguration().getBounds());
        }
        logger.debug("MainUI on screen: " + currentScreenDevice);

        setUIOptions();

        initialize();

        addListener();

        addKeyBindings();
    }

    /**
     * Initializes MainUI with its program icon, window sizes, etc. and gets
     * content pane with all components of MainUI.
     */
    private void initialize() {

        final Dimension normalSize = new Dimension(960, 780);
        final Dimension minimumSize = new Dimension(800, 640);

        setSize(normalSize);
        setMinimumSize(minimumSize);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        // setUndecorated(true);
        // setAlwaysOnTop(true);
        setIconImage(new ImageIcon(getClass().getResource(
                "/resources/icon/icon_freenono.png")).getImage());
        setLocationRelativeTo(null);
        setName("mainUI");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setTitle(Messages.getString("MainUI.Title"));

        setContentPane(buildContentPane());
        validate();

        // so that MainUI can receive key-events
        setFocusable(true);
        requestFocus();
    }

    /**
     * Sets all fonts of the current look-and-feel to a given font name and font
     * size. Font style for all keys will stay the same.
     * <p>
     * Source for 'setting of all fonts':
     * http://stackoverflow.com/questions/12730230/set-the-same-font-for-
     * all-component-java
     */
    private void setUIOptions() {

        // JDialog.setDefaultLookAndFeelDecorated(true);

        /*
         * Set font for all components.
         */
        Enumeration<Object> keys = UIManager.getDefaults().keys();

        while (keys.hasMoreElements()) {

            Object key = keys.nextElement();
            Object value = UIManager.get(key);

            if (value instanceof FontUIResource) {

                FontUIResource orig = (FontUIResource) value;
                UIManager.put(key, new FontUIResource(FontFactory
                        .createDefaultFont().deriveFont(orig.getStyle())));
            }
        }

        /*
         * Set background for all panels.
         */
        UIManager.put("Panel.background", settings.getColorModel()
                .getTopColor());
        UIManager.put("RootPane.background", settings.getColorModel()
                .getTopColor());
        UIManager.put("background", settings.getColorModel().getTopColor());
    }

    /**
     * Add listeners for this window to pause game when frame is minimized and
     * handle the exit.
     */
    private void addListener() {

        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(final WindowEvent e) {
            }

            @Override
            public void windowIconified(final WindowEvent e) {
                if (gameRunning) {
                    windowMinimized = true;
                    performPause();
                }
            }

            @Override
            public void windowDeiconified(final WindowEvent e) {
                if (windowMinimized) {
                    performPause();
                    windowMinimized = false;
                }
            }

            @Override
            public void windowDeactivated(final WindowEvent e) {
            }

            @Override
            public void windowClosing(final WindowEvent e) {
                performExit();
            }

            @Override
            public void windowClosed(final WindowEvent e) {
            }

            @Override
            public void windowActivated(final WindowEvent e) {
            }
        });
    }

    /**
     * Add key bindings for all control buttons on the top of the window.
     */
    private void addKeyBindings() {

        JComponent rootPane = this.getRootPane();

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("F1"), "Start");
        rootPane.getActionMap().put("Start", new AbstractAction() {

            private static final long serialVersionUID = 653149778238948695L;

            public void actionPerformed(final ActionEvent e) {

                performStart();
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("F2"), "Restart");
        rootPane.getActionMap().put("Restart", new AbstractAction() {

            private static final long serialVersionUID = 2909922464716273283L;

            public void actionPerformed(final ActionEvent e) {

                if (restartButton.isEnabled()) {

                    performRestart();
                }
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("F3"), "Pause");
        rootPane.getActionMap().put("Pause", new AbstractAction() {

            private static final long serialVersionUID = -3429023602787303442L;

            public void actionPerformed(final ActionEvent e) {

                if (pauseButton.isEnabled()) {

                    performPause();
                }
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("F4"), "Stop");
        rootPane.getActionMap().put("Stop", new AbstractAction() {

            private static final long serialVersionUID = -4991874644955600912L;

            public void actionPerformed(final ActionEvent e) {

                if (stopButton.isEnabled()) {

                    performStop();
                }
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("F5"), "ShowOptions");
        rootPane.getActionMap().put("ShowOptions", new AbstractAction() {

            private static final long serialVersionUID = 4520522172894740522L;

            public void actionPerformed(final ActionEvent e) {

                showOptions();
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("F6"), "ShowStatistics");
        rootPane.getActionMap().put("ShowStatistics", new AbstractAction() {

            private static final long serialVersionUID = 7842336013574876417L;

            public void actionPerformed(final ActionEvent e) {

                showStatistics();
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("F7"), "ShowHelp");
        rootPane.getActionMap().put("ShowHelp", new AbstractAction() {

            private static final long serialVersionUID = -5662170020301495368L;

            public void actionPerformed(final ActionEvent e) {

                showHelp();
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("F8"), "ShowEdit");
        rootPane.getActionMap().put("ShowEdit", new AbstractAction() {

            private static final long serialVersionUID = 1578736838902924356L;

            public void actionPerformed(final ActionEvent e) {

                showEdit();
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("F9"), "ShowAbout");
        rootPane.getActionMap().put("ShowAbout", new AbstractAction() {

            private static final long serialVersionUID = -5782569581091699423L;

            public void actionPerformed(final ActionEvent e) {

                showAbout();
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("F10"), "Exit");
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("ESCAPE"), "Exit");
        rootPane.getActionMap().put("Exit", new AbstractAction() {

            private static final long serialVersionUID = 7710250349322747098L;

            public void actionPerformed(final ActionEvent e) {

                performExit();
            }
        });
    }

    /**
     * Builds content pane with all components, like icon bar and status bar.
     * 
     * @return content pane with all components.
     */
    private JPanel buildContentPane() {

        if (contentPane == null) {
            contentPane = new JPanel() {

                private static final long serialVersionUID = -375905655173204523L;

                protected void paintComponent(final Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    BufferedImage cache = null;
                    if (cache == null || cache.getHeight() != getHeight()) {
                        cache = new BufferedImage(2, getHeight(),
                                BufferedImage.TYPE_INT_RGB);
                        Graphics2D g2d = cache.createGraphics();

                        GradientPaint paint = new GradientPaint(0, 0, settings
                                .getColorModel().getTopColor(), 0, getHeight(),
                                Color.WHITE);
                        g2d.setPaint(paint);
                        g2d.fillRect(0, 0, 2, getHeight());
                        g2d.dispose();
                    }
                    g2.drawImage(cache, 0, 0, getWidth(), getHeight(), null);
                }
            };

            // use GridBagLayout as layout manager
            layout = new GridBagLayout();
            constraints = new GridBagConstraints();
            contentPane.setLayout(layout);

            // add tool bar
            // constraints.insets = new Insets(0, 25, 0, 25);
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 2;
            constraints.gridheight = 1;
            constraints.weightx = 0;
            constraints.weighty = 0;
            constraints.anchor = GridBagConstraints.NORTH;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            contentPane.add(getJJToolBarBar(), constraints);

            // add status bar
            constraints.gridx = 0;
            constraints.gridy = 2;
            constraints.gridwidth = 2;
            constraints.gridheight = 1;
            constraints.weightx = 0;
            constraints.weighty = 0;
            constraints.anchor = GridBagConstraints.SOUTH;
            contentPane.add(getStatusBar(), constraints);

            // add dummy panel
            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.weightx = 1;
            constraints.weighty = 1;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.anchor = GridBagConstraints.CENTER;
            contentPane.add(Box.createVerticalGlue(), constraints);
        }
        return contentPane;
    }

    /**
     * Builds and initializes the status bar at the bottom of the window.
     * 
     * @return Status bar component.
     */
    private JToolBar getStatusBar() {
        if (statusBar == null) {
            statusBar = new JToolBar() {

                private static final long serialVersionUID = -3717090949953624554L;

                @Override
                public void paintComponent(final Graphics g) {
                    g.setColor(settings.getColorModel().getTopColor());
                    g.fillRect(0, 0, getSize().width, getSize().height);
                    super.paintComponent(g);
                }
            };
            statusBar.setOpaque(false);
            statusBar.setBorder(BorderFactory.createEmptyBorder());
            statusBar.setBorderPainted(false);
            statusBar.setFocusable(false);
            statusBar.setFloatable(false);
            statusBar.add(getStatusBarText());
        }
        return statusBar;
    }

    /**
     * Initializes the text label within the status bar.
     * 
     * @return Text label for the status bar.
     */
    private JMenuItem getStatusBarText() {
        if (statusBarText == null) {
            statusBarText = new JMenuItem();
            statusBarText.setText("FreeNono...");
        }
        return statusBarText;
    }

    /**
     * Instantiates the board panel itself and the status component.
     */
    private void buildBoard() {

        if (statusField != null) {

            statusField.removeEventHelper();
            contentPane.remove(statusField);
        }

        if (boardPanel != null) {

            boardPanel.removeEventHelper();
            contentPane.remove(boardPanel);
        }

        // add status component
        final int inset = 10;
        constraints.insets = new Insets(inset, inset, inset, inset);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0;
        constraints.weighty = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        statusField = new StatusComponent(settings);
        contentPane.add(statusField, constraints);

        // add board panel
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 10;
        constraints.weighty = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        boardPanel = new BoardPanel(eventHelper,
                lastChosenNonogram.fetchNonogram(), settings);
        contentPane.add(boardPanel, constraints);

        // validate and layout MainUI ...
        contentPane.validate();

        // ... and let boardPanel do its layout based upon available space
        boardPanel.layoutBoard();

        // set event helper for children
        statusField.setEventHelper(eventHelper);
        boardPanel.setEventHelper(eventHelper);
    }

    /*
     * Functions controlling the game flow
     */

    /**
     * Performs a start of a new game.
     */
    private void performStart() {

        NonogramProvider newlyChosenNonogram = null;

        boolean resumeAfter = false;

        if (gameRunning) {

            performPause();
            resumeAfter = true;
        }

        // set busy mouse cursor
        setCursor(new Cursor(Cursor.WAIT_CURSOR));

        // get NonogramChooserUI and show it
        NonogramChooserUI nonoChooser = new NonogramChooserUI(nonogramProvider);
        nonoChooser.setVisible(true);
        newlyChosenNonogram = nonoChooser.getChosenNonogram();
        nonoChooser.dispose();
        // NonogramExplorer nexp = new NonogramExplorer(nonogramProvider,
        // settings.getColorModel());
        // nexp.setVisible(true);

        // reset mouse cursor
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        if (lastChosenNonogram == null && newlyChosenNonogram == null) {

            /*
             * If no nonogram was played before and no nonogram was chosen,
             * disable all buttons when no new nonogram was selected.
             */

            pauseButton.setEnabled(false);
            stopButton.setEnabled(false);
            restartButton.setEnabled(false);

        } else if (newlyChosenNonogram != null) {

            /*
             * Or if some new nonogram was chosen save it and start the new
             * game.
             */

            performStop();

            pauseButton.setEnabled(true);
            stopButton.setEnabled(true);
            restartButton.setEnabled(true);
            statisticsButton.setEnabled(true);

            lastChosenNonogram = newlyChosenNonogram;
            logger.debug("Nonogram chosen by user: " + newlyChosenNonogram);

            buildBoard();

            eventHelper.fireProgramControlEvent(new ProgramControlEvent(this,
                    ProgramControlType.NONOGRAM_CHOSEN, lastChosenNonogram
                            .fetchNonogram()));

            eventHelper.fireProgramControlEvent(new ProgramControlEvent(this,
                    ProgramControlType.START_GAME, lastChosenNonogram
                            .fetchNonogram()));
        } else {

            /*
             * If no new nonogram was chosen and an old nonogram was set, resume
             * game if it was paused when clicking the button.
             */

            if (resumeAfter) {

                performPause();
            }
        }
    }

    /**
     * Performs a restart of the last played nonogram.
     */
    private void performRestart() {

        performStop();

        if (lastChosenNonogram != null) {

            pauseButton.setEnabled(true);
            stopButton.setEnabled(true);
            restartButton.setEnabled(true);

            buildBoard();

            eventHelper.fireProgramControlEvent(new ProgramControlEvent(this,
                    ProgramControlType.RESTART_GAME, lastChosenNonogram
                            .fetchNonogram()));
        }
    }

    /**
     * Performs a pause of running game.
     */
    private void performPause() {

        if (gameRunning) {

            eventHelper.fireProgramControlEvent(new ProgramControlEvent(this,
                    ProgramControlType.PAUSE_GAME));

            setPauseButtonToResume();

        } else {

            eventHelper.fireProgramControlEvent(new ProgramControlEvent(this,
                    ProgramControlType.RESUME_GAME));

            setPauseButtonToPause();
        }
    }

    /**
     * Changes icon of pause button to pause.
     */
    private void setPauseButtonToPause() {

        pauseButton.setIcon(new ImageIcon(getClass().getResource(
                "/resources/icon/button_pause.png")));
        pauseButton.setToolTipText(Messages.getString("MainUI.PauseTooltip"));
    }

    /**
     * Changes icon of pause button to resume.
     */
    private void setPauseButtonToResume() {

        pauseButton.setIcon(new ImageIcon(getClass().getResource(
                "/resources/icon/button_resume.png")));
        pauseButton.setToolTipText(Messages.getString("MainUI.ResumeTooltip"));
    }

    /**
     * Stops the running game.
     */
    private void performStop() {

        eventHelper.fireProgramControlEvent(new ProgramControlEvent(this,
                ProgramControlType.STOP_GAME));

        setPauseButtonToPause();

        pauseButton.setEnabled(false);
        restartButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    /**
     * Exits the program.
     */
    private void performExit() {

        boolean doExit = true;

        if (gameRunning) {

            /**
             * Shows a dialog to ask user if program should really be exited.
             * 
             * @author Christian Wichmann
             */
            class ReallyExitDialog extends JDialog {

                private static final long serialVersionUID = -3791896670433960168L;

                private boolean exit = false;
                private final int borderWidth = 20;

                /**
                 * Initializes a dialog to ask user if program should really be
                 * exited.
                 */
                public ReallyExitDialog() {

                    initialize();
                }

                /**
                 * Initializes a dialog to ask user if program should really be
                 * exited.
                 */
                private void initialize() {

                    setTitle(Messages
                            .getString("MainUI.QuestionQuitProgrammTitle"));
                    getContentPane().setBackground(
                            settings.getColorModel().getTopColor());
                    getContentPane().setForeground(
                            settings.getColorModel().getBottomColor());
                    setUndecorated(true);
                    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    setModalityType(ModalityType.APPLICATION_MODAL);

                    add(buildContentPane());

                    pack();
                    setLocationRelativeTo(null);
                }

                /**
                 * Builds a panel including the localized question for the user.
                 * 
                 * @return panel with text
                 */
                private JPanel buildContentPane() {

                    JPanel content = new JPanel();

                    content.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createEtchedBorder(), BorderFactory
                                    .createEmptyBorder(borderWidth,
                                            borderWidth, borderWidth,
                                            borderWidth)));
                    content.setOpaque(false);

                    content.add(new JLabel(Messages
                            .getString("MainUI.QuestionQuitProgramm")));
                    content.add(buildButtonPane());

                    return content;
                }

                /**
                 * This method builds the panel which includes two buttons to
                 * chose whether to exit program or not. By clicking a button
                 * the field <code>exit</code> will be set.
                 * 
                 * @return button panel
                 */
                private JPanel buildButtonPane() {

                    JPanel buttonPane = new JPanel();
                    buttonPane.setOpaque(false);
                    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
                    buttonPane.setBorder(BorderFactory.createEmptyBorder(0,
                            borderWidth, 0, 0));

                    JButton yesButton = new JButton(Messages.getString("Yes"));
                    yesButton.addActionListener(new ActionListener() {

                        public void actionPerformed(final ActionEvent arg0) {

                            exit = true;
                            dispose();
                        }
                    });
                    yesButton.setActionCommand("Yes");
                    buttonPane.add(yesButton);

                    JButton noButton = new JButton(Messages.getString("No"));
                    noButton.addActionListener(new ActionListener() {

                        public void actionPerformed(final ActionEvent arg0) {

                            exit = false;
                            dispose();
                        }
                    });
                    noButton.setActionCommand("No");
                    buttonPane.add(noButton);

                    return buttonPane;
                }

                /**
                 * Returns whether the user want to really exit.
                 * 
                 * @return true, if user wants to exit
                 */
                public boolean doExit() {

                    return exit;
                }
            }

            ReallyExitDialog exitDialog = new ReallyExitDialog();
            exitDialog.setVisible(true);
            doExit = exitDialog.doExit();
        }

        if (doExit) {

            eventHelper.fireProgramControlEvent(new ProgramControlEvent(this,
                    ProgramControlType.QUIT_PROGRAMM));

            setVisible(false);
            dispose();
        }
    }

    /**
     * Handle coop mode and show coop dialog.
     */
    private void handleCoop() {

        boolean resumeAfter = false;

        if (gameRunning) {

            performPause();
            resumeAfter = true;
        }

        CoopStartDialog csd = new CoopStartDialog(settings);
        csd.setVisible(true);

        if (resumeAfter) {

            performPause();
        }
    }

    /*
     * Functions providing organizational and statistical dialogs
     */

    /**
     * Shows a about box.
     */
    private void showAbout() {

        boolean resumeAfter = false;

        if (gameRunning) {

            performPause();
            resumeAfter = true;
        }

        eventHelper.fireProgramControlEvent(new ProgramControlEvent(this,
                ProgramControlType.SHOW_ABOUT));

        // set path to about dialog and build it
        if (aboutDialog == null) {

            logger.debug("Building about dialog.");

            URL pathToText = null, pathToIcon = null;

            String path = "/about/about_" + Locale.getDefault().getLanguage()
                    + ".html";
            pathToText = getClass().getResource(path);

            if (pathToText == null) {

                pathToText = getClass().getResource("/about/about_en.html");
            }

            // set path to FreeNono icon
            pathToIcon = getClass().getResource(
                    "/resources/icon/icon_freenono_big.png");

            if (pathToIcon != null && pathToText != null) {

                aboutDialog = new AboutDialog2(
                        Messages.getString("MainUI.Title"), RunUI.class
                                .getPackage().getImplementationVersion(),
                        pathToText, pathToIcon, settings.getColorModel()
                                .getTopColor());
            }
        }

        if (aboutDialog != null) {

            aboutDialog.setVisible(true);
        }

        if (resumeAfter) {

            performPause();
        }
    }

    /**
     * Shows a splash screen for a given time.
     * 
     * @param timerDelay
     *            Time to show splash screen for.
     */
    @SuppressWarnings("unused")
    private void showSplashscreen(final int timerDelay) {

        // show splash screen
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SplashScreen splash = new SplashScreen(timerDelay);
                splash.setVisible(true);
            }
        });
    }

    /**
     * Opens current nonogram in editor.
     */
    private void showEdit() {

        if (lastChosenNonogram != null) {
            logger.debug("Open editor frame with nonogram: "
                    + lastChosenNonogram.fetchNonogram().getOriginPath());
        }

        // TODO Add call of FNE
    }

    /**
     * Shows a statistic window.
     */
    private void showStatistics() {

        boolean resumeAfter = false;

        if (gameRunning) {

            performPause();
            resumeAfter = true;
        }

        StatisticsViewDialog svd = new StatisticsViewDialog(settings);
        svd.setVisible(true);

        if (resumeAfter) {

            performPause();
        }
    }

    /**
     * Shows a help dialog.
     */
    private void showHelp() {

        boolean resumeAfter = false;

        if (gameRunning) {

            performPause();
            resumeAfter = true;
        }

        eventHelper.fireProgramControlEvent(new ProgramControlEvent(this,
                ProgramControlType.SHOW_ABOUT));

        // set path to about dialog and build it
        if (helpDialog == null) {

            logger.debug("Building help dialog.");

            URL pathToText = null;

            String path = "/help/help_" + Locale.getDefault().getLanguage()
                    + ".html";
            pathToText = getClass().getResource(path);

            if (pathToText == null) {

                pathToText = getClass().getResource("/help/help_en.html");
            }

            if (pathToText != null) {

                helpDialog = new AboutDialog2(
                        Messages.getString("HelpDialog.Help"), null,
                        pathToText, null, settings.getColorModel()
                                .getTopColor());
            }
        }

        if (helpDialog != null) {

            helpDialog.setVisible(true);
        }

        if (resumeAfter) {

            performPause();
        }
    }

    /**
     * Shows the options dialog.
     */
    private void showOptions() {

        boolean resumeAfter = false;

        if (gameRunning) {

            performPause();
            resumeAfter = true;
        }

        eventHelper.fireProgramControlEvent(new ProgramControlEvent(this,
                ProgramControlType.SHOW_OPTIONS));
        OptionsUI ui = new OptionsUI(this, settings);
        ui.setVisible(true);

        if (resumeAfter) {

            performPause();
        }
    }

    /*
     * Functions providing gui elements
     */

    /**
     * Initializes the icon bar on top of the window.
     * 
     * @return Icon bar with all buttons in it.
     */
    private JToolBar getJJToolBarBar() {
        if (toolBar == null) {
            toolBar = new JToolBar();
            toolBar.setFloatable(false);
            toolBar.setFocusable(false);
            toolBar.setRollover(true);
            toolBar.setLayout(new FlowLayout(FlowLayout.CENTER));
            toolBar.setBorder(BorderFactory.createEmptyBorder());

            toolBar.add(getStartButton());
            toolBar.add(getCoopButton());
            toolBar.add(getRestartButton());
            toolBar.add(getPauseButton());
            toolBar.add(getStopButton());
            toolBar.add(getOptionsButton());
            toolBar.add(getStatisticsButton());
            toolBar.add(getHelpButton());
            toolBar.add(getEditButton());
            toolBar.add(getAboutButton());
            toolBar.add(getExitButton());
        }
        return toolBar;
    }

    /**
     * Initializes button to start a game.
     * 
     * @return Button to start game.
     */
    private JButton getStartButton() {
        if (startButton == null) {
            startButton = new JButton();
            startButton.setText("");
            startButton.setFocusable(false);
            startButton.setIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_start.png")));
            startButton.setToolTipText(Messages
                    .getString("MainUI.StartTooltip"));
            startButton.setDisabledIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_start2.png")));
            startButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    performStart();
                }
            });
        }
        return startButton;
    }

    /**
     * Initializes a button for the coop mode.
     * 
     * @return Button for coop mode.
     */
    private JButton getCoopButton() {

        if (coopButton == null) {
            coopButton = new JButton();
            coopButton.setText("");
            coopButton.setFocusable(false);
            coopButton.setIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_coop.png")));
            coopButton.setToolTipText(Messages.getString("MainUI.CoopTooltip"));
            coopButton.setDisabledIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_coop2.png")));
            coopButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    handleCoop();
                }
            });
        }
        return coopButton;
    }

    /**
     * Initializes button to pause game.
     * 
     * @return Button to pause game.
     */
    private JButton getPauseButton() {
        if (pauseButton == null) {
            pauseButton = new JButton();
            pauseButton.setToolTipText(Messages
                    .getString("MainUI.PauseTooltip"));
            pauseButton.setIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_pause.png")));
            pauseButton.setText("");
            pauseButton.setEnabled(false);
            pauseButton.setFocusable(false);
            pauseButton.setDisabledIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_pause2.png")));
            pauseButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    performPause();
                }
            });
        }
        return pauseButton;
    }

    /**
     * Initializes button to stop the game.
     * 
     * @return Button to stop game.
     */
    private JButton getStopButton() {
        if (stopButton == null) {
            stopButton = new JButton();
            stopButton.setToolTipText(Messages.getString("MainUI.StopTooltip"));
            stopButton.setIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_stop.png")));
            stopButton.setText("");
            stopButton.setEnabled(false);
            stopButton.setFocusable(false);
            stopButton.setDisabledIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_stop2.png")));
            stopButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    performStop();
                }
            });
        }
        return stopButton;
    }

    /**
     * Initializes a button to restart the game.
     * 
     * @return Button to restart game.
     */
    private JButton getRestartButton() {
        if (restartButton == null) {
            restartButton = new JButton();
            restartButton.setToolTipText(Messages
                    .getString("MainUI.RestartTooltip"));
            restartButton.setIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_restart.png")));
            restartButton.setText("");
            restartButton.setEnabled(false);
            restartButton.setFocusable(false);
            restartButton.setDisabledIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_restart2.png")));
            restartButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    performRestart();
                }
            });
        }
        return restartButton;
    }

    /**
     * Initializes a button to exit the game.
     * 
     * @return Button to exit game.
     */
    private JButton getExitButton() {
        if (exitButton == null) {
            exitButton = new JButton();
            exitButton.setIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_exit.png")));
            exitButton.setEnabled(true);
            exitButton.setDisabledIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_exit2.png")));
            exitButton.setText("");
            exitButton.setFocusable(false);
            exitButton.setToolTipText(Messages.getString("MainUI.ExitTooltip"));
            exitButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    performExit();
                }
            });
        }
        return exitButton;
    }

    /**
     * Initializes a button for showing an about box.
     * 
     * @return Button showing an about box.
     */
    private JButton getAboutButton() {
        if (aboutButton == null) {
            aboutButton = new JButton();
            aboutButton.setEnabled(true);
            aboutButton.setDisabledIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_about2.png")));
            aboutButton.setIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_about.png")));
            aboutButton.setText("");
            aboutButton.setFocusable(false);
            aboutButton.setComponentOrientation(ComponentOrientation.UNKNOWN);
            aboutButton.setToolTipText(Messages
                    .getString("MainUI.AboutTooltip"));
            aboutButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    showAbout();
                }
            });
        }
        return aboutButton;
    }

    /**
     * Initializes a button to show the options dialog.
     * 
     * @return Button that shows the options dialog.
     */
    private JButton getOptionsButton() {
        if (optionsButton == null) {
            optionsButton = new JButton();
            optionsButton.setComponentOrientation(ComponentOrientation.UNKNOWN);
            optionsButton.setToolTipText(Messages
                    .getString("MainUI.OptionsTooltip"));
            optionsButton.setDisabledIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_config2.png")));
            optionsButton.setIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_config.png")));
            optionsButton.setText("");
            optionsButton.setEnabled(true);
            optionsButton.setFocusable(false);
            optionsButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    showOptions();
                }
            });
        }
        return optionsButton;
    }

    /**
     * Initializes a button to get a help dialog.
     * 
     * @return Button for help dialog.
     */
    private JButton getHelpButton() {
        if (helpButton == null) {
            helpButton = new JButton();
            helpButton.setComponentOrientation(ComponentOrientation.UNKNOWN);
            helpButton.setToolTipText(Messages.getString("MainUI.HelpTooltip"));
            helpButton.setDisabledIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_help2.png")));
            helpButton.setIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_help.png")));
            helpButton.setText("");
            helpButton.setEnabled(true);
            helpButton.setFocusable(false);
            helpButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    showHelp();
                }
            });
        }
        return helpButton;
    }

    /**
     * Initializes button to call editor.
     * 
     * @return Button to call editor.
     */
    private JButton getEditButton() {
        if (editButton == null) {
            editButton = new JButton();
            editButton.setComponentOrientation(ComponentOrientation.UNKNOWN);
            editButton.setToolTipText(Messages.getString("MainUI.EditTooltip"));
            editButton.setDisabledIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_edit2.png")));
            editButton.setIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_edit.png")));
            editButton.setText("");
            editButton.setEnabled(false);
            editButton.setFocusable(false);
            editButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    showEdit();
                }
            });
        }
        return editButton;
    }

    /**
     * Initializes button to call a statistics window.
     * 
     * @return Button to call statistics window.
     */
    private JButton getStatisticsButton() {
        if (statisticsButton == null) {
            statisticsButton = new JButton();
            statisticsButton
                    .setComponentOrientation(ComponentOrientation.UNKNOWN);
            statisticsButton.setToolTipText(Messages
                    .getString("MainUI.StatisticsTooltip"));
            statisticsButton.setDisabledIcon(new ImageIcon(getClass()
                    .getResource("/resources/icon/button_statistics2.png")));
            statisticsButton.setIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_statistics.png")));
            statisticsButton.setText("");
            statisticsButton.setFocusable(false);
            statisticsButton.setEnabled(false);
            statisticsButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    showStatistics();
                }
            });
        }
        return statisticsButton;
    }

    /*
     * Miscellaneous functions
     */

    /**
     * Starts a new game when player chose next nonogram pattern in GameOverUI.
     * 
     * @param nextNonogramToPlay
     *            nonogram that should be played next
     */
    private void performStartFromDialog(
            final NonogramProvider nextNonogramToPlay) {

        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
        restartButton.setEnabled(true);
        statisticsButton.setEnabled(true);

        lastChosenNonogram = nextNonogramToPlay;
        logger.debug("Nonogram chosen by user: " + nextNonogramToPlay);

        buildBoard();

        eventHelper.fireProgramControlEvent(new ProgramControlEvent(this,
                ProgramControlType.NONOGRAM_CHOSEN, lastChosenNonogram
                        .fetchNonogram()));

        eventHelper.fireProgramControlEvent(new ProgramControlEvent(this,
                ProgramControlType.START_GAME, lastChosenNonogram
                        .fetchNonogram()));
    }

    /**
     * Handles the game ending by setting status bar text and showing a game end
     * dialog.
     * 
     * @param isSolved
     *            Whether the game was won or lost.
     */
    private void handleGameEnding(final boolean isSolved) {

        // set text for status bar
        if (isSolved) {
            statusBarText.setText(Messages.getString("MainUI.StatusBarWon"));
        } else {
            statusBarText.setText(Messages.getString("MainUI.StatusBarLost"));
        }

        // set buttons
        stopButton.setEnabled(false);
        pauseButton.setEnabled(false);

        // get previewImage and save it as file
        final BoardPreview preview = boardPanel.getPreviewArea();
        if (isSolved) {
            saveThumbnail(preview.getPreviewImage());
        }

        // show GameOver dialog and start new game if user chose new nonogram
        final GameOverUI gameOverDialog = new GameOverUI(lastChosenNonogram,
                isSolved, settings);
        final NonogramProvider nextNonogram = gameOverDialog
                .getNextNonogramToPlay();

        logger.debug("Next nonogram from game over dialog: " + nextNonogram);

        if (nextNonogram != null) {

            performStartFromDialog(nextNonogram);
        }
    }

    /**
     * Save preview of currently played nonogram as thumbnail on disk.
     * 
     * @param preview
     *            Preview of current nonogram.
     */
    private void saveThumbnail(final BufferedImage preview) {

        File thumbDir = new File(DEFAULT_THUMBNAILS_PATH);

        if (!thumbDir.exists()) {

            thumbDir.mkdirs();
        }

        File thumbFile = new File(thumbDir, lastChosenNonogram.fetchNonogram()
                .getHash());

        if (!thumbFile.exists()) {

            try {

                ImageIO.write((RenderedImage) preview, "png", thumbFile);

            } catch (IOException e) {

                logger.warn("Could not write preview image to file "
                        + thumbFile);
            }

            logger.info("Preview image written to file " + thumbFile);
        }
    }
}
