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
import java.awt.Font;
import java.awt.FontFormatException;
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
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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
import org.freenono.interfaces.CollectionProvider;
import org.freenono.model.Nonogram;
import org.freenono.model.GameMode_Quiz;
import org.freenono.model.Tools;
import org.freenono.quiz.Question;
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

        public void optionsChanged(ProgramControlEvent e) {

            repaint();
        }

        public void stateChanged(StateChangeEvent e) {

            boolean isSolved = true;

            switch (e.getNewState()) {
            case gameOver:
                gameRunning = false;
                isSolved = false;

            case solved:
                gameRunning = false;
                handleGameEnding(isSolved);
                break;

            case paused:
                gameRunning = false;
                statusBarText.setText(Messages
                        .getString("MainUI.StatusBarPause"));
                break;

            case running:
                gameRunning = true;
                statusBarText.setText(Messages
                        .getString("MainUI.StatusBarRunning"));
                break;

            case userStop:
                gameRunning = false;
                statusBarText.setText(Messages
                        .getString("MainUI.StatusBarStopped"));
                break;

            default:
                break;
            }
        }

        public void askQuestion(QuizEvent e) {

            Question question = e.getQuestion();

            AskQuestionDialog aqd = new AskQuestionDialog(question, settings);

            // set answer to "0" if cancel button was pushed
            String answer = aqd.getAnswer();
            if (answer == null)
                answer = "0";

            ((GameMode_Quiz) e.getSource()).checkAnswer(question, answer);
        }

    };

    private GameEventHelper eventHelper = null;
    private Settings settings = null;
    private List<CollectionProvider> nonogramProvider = null;
    private Nonogram currentNonogram = null;
    private boolean gameRunning = false;
    
    private GraphicsDevice currentScreenDevice = null;

    private AboutDialog2 aboutDialog;
    private AboutDialog2 helpDialog;

    private JPanel jContentPane = null;
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
     * This is the default constructor
     */
    public MainUI(GameEventHelper geh, Settings s, List<CollectionProvider> np) {

        super();

        this.eventHelper = geh;
        this.settings = s;
        this.nonogramProvider = np;

        eventHelper.addGameListener(gameAdapter);

        
        // find screen on which MainUI is shown...
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        currentScreenDevice = getGraphicsConfiguration().getDevice();
        GraphicsDevice gs[] = ge.getScreenDevices();
        for (GraphicsDevice screen : gs) {
            logger.debug(screen.getDefaultConfiguration().getBounds());
        }
        logger.debug("MainUI on screen: " + currentScreenDevice);

        
        registerFonts();

        initialize();

        addListener();

        addKeyBindings();
    }

    /**
     * Register all fonts included in FreeNono to be used in the frames and dialogs.
     */
    private void registerFonts() {

        // add new font
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, getClass()
                    .getResourceAsStream("/resources/fonts/LCDMono.TTF")); //$NON-NLS-1$
            // font = font.deriveFont(36);
            GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .registerFont(font);
        } catch (FontFormatException e) {

            logger.error("Unable to load font file because of a wrong font file format!");
        } catch (IOException e) {

            logger.error("Could not load font file from filesystem.");
        }
    }

    /**
     * Initializes MainUI with its program icon, window sizes, etc. and gets
     * content pane with all components of MainUI.
     */
    private void initialize() {

        setSize(new Dimension(960, 780));
        setMinimumSize(new Dimension(800, 640));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        // setUndecorated(true);
        // setAlwaysOnTop(true);
        setIconImage(new ImageIcon(getClass().getResource(
                "/resources/icon/icon_freenono.png")).getImage());
        setLocationRelativeTo(null);
        setName("mainUI");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setTitle(Messages.getString("MainUI.Title"));

        setContentPane(getJContentPane());
        validate();

        // so that MainUI can receive key-events
        setFocusable(true);
        requestFocus();
    }

    /**
     * Add listeners for this window to pause game when frame is minimized and
     * handle the exit.
     */
    private void addListener() {

        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {

                if (gameRunning) {

                    performPause();
                }
            }

            @Override
            public void windowDeiconified(WindowEvent e) {

                // TODO resume game, if it was paused when iconifying window
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {

                performExit();
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
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

            public void actionPerformed(ActionEvent e) {
                performStart();
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("F2"), "Restart");
        rootPane.getActionMap().put("Restart", new AbstractAction() {

            private static final long serialVersionUID = 2909922464716273283L;

            public void actionPerformed(ActionEvent e) {
                if (restartButton.isEnabled())
                    performRestart();
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("F3"), "Pause");
        rootPane.getActionMap().put("Pause", new AbstractAction() {

            private static final long serialVersionUID = -3429023602787303442L;

            public void actionPerformed(ActionEvent e) {
                if (pauseButton.isEnabled())
                    performPause();
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("F4"), "Stop");
        rootPane.getActionMap().put("Stop", new AbstractAction() {

            private static final long serialVersionUID = -4991874644955600912L;

            public void actionPerformed(ActionEvent e) {
                if (stopButton.isEnabled())
                    performStop();
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("F5"), "ShowOptions");
        rootPane.getActionMap().put("ShowOptions", new AbstractAction() {

            private static final long serialVersionUID = 4520522172894740522L;

            public void actionPerformed(ActionEvent e) {
                showOptions();
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("F6"), "ShowStatistics");
        rootPane.getActionMap().put("ShowStatistics", new AbstractAction() {

            private static final long serialVersionUID = 7842336013574876417L;

            public void actionPerformed(ActionEvent e) {
                showStatistics();
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("F7"), "ShowHelp");
        rootPane.getActionMap().put("ShowHelp", new AbstractAction() {

            private static final long serialVersionUID = -5662170020301495368L;

            public void actionPerformed(ActionEvent e) {
                showHelp();
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("F8"), "ShowEdit");
        rootPane.getActionMap().put("ShowEdit", new AbstractAction() {

            private static final long serialVersionUID = 1578736838902924356L;

            public void actionPerformed(ActionEvent e) {
                showEdit();
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("F9"), "ShowAbout");
        rootPane.getActionMap().put("ShowAbout", new AbstractAction() {

            private static final long serialVersionUID = -5782569581091699423L;

            public void actionPerformed(ActionEvent e) {
                showAbout();
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("F10"), "Exit");
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("ESCAPE"), "Exit");
        rootPane.getActionMap().put("Exit", new AbstractAction() {

            private static final long serialVersionUID = 7710250349322747098L;

            public void actionPerformed(ActionEvent e) {
                performExit();
            }
        });
    }

    /**
     * Builds content pane with all components, like icon bar and status bar.
     * 
     * @return content pane with all components.
     */
    private JPanel getJContentPane() {

        if (jContentPane == null) {
            jContentPane = new JPanel() {

                private static final long serialVersionUID = -375905655173204523L;

                protected void paintComponent(Graphics g) {
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
            jContentPane.setLayout(layout);

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
            jContentPane.add(getJJToolBarBar(), constraints);

            // add status bar
            constraints.gridx = 0;
            constraints.gridy = 2;
            constraints.gridwidth = 2;
            constraints.gridheight = 1;
            constraints.weightx = 0;
            constraints.weighty = 0;
            constraints.anchor = GridBagConstraints.SOUTH;
            jContentPane.add(getStatusBar(), constraints);

            // add dummy panel
            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.weightx = 1;
            constraints.weighty = 1;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.anchor = GridBagConstraints.CENTER;
            jContentPane.add(Box.createVerticalGlue(), constraints);
        }
        return jContentPane;
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
                public void paintComponent(Graphics g) {
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
            jContentPane.remove(statusField);
        }

        if (boardPanel != null) {

            boardPanel.removeEventHelper();
            jContentPane.remove(boardPanel);
        }

        // add status component
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0;
        constraints.weighty = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        statusField = new StatusComponent(settings);
        jContentPane.add(statusField, constraints);

        // add board panel
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 10;
        constraints.weighty = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        boardPanel = new BoardPanel(eventHelper, currentNonogram, settings);
        jContentPane.add(boardPanel, constraints);

        // validate and layout MainUI ...
        jContentPane.validate();

        // ... and let boardPanel do its layout based upon available space
        boardPanel.layoutBoard();

        // set event helper for children
        statusField.setEventHelper(eventHelper);
        boardPanel.setEventHelper(eventHelper);

        // get focus for play field
        boardPanel.focusPlayfield();
    }

    private void setCurrentNonogram(Nonogram currentNonogram) {

        this.currentNonogram = currentNonogram;
    }

    
    /**
     * Functions controlling the game flow
     */

    private void performStart() {

        Nonogram chosenNonogram = null;

        if (gameRunning)
            performStop();

        setPauseButtonToPause();

        // set busy mouse cursor
        setCursor(new Cursor(Cursor.WAIT_CURSOR));

        // get NonogramChooserUI and show it
        NonogramChooserUI nonoChooser = new NonogramChooserUI(nonogramProvider);
        nonoChooser.setVisible(true);
        chosenNonogram = nonoChooser.getChosenNonogram();
        nonoChooser.dispose();
        // NonogramExplorer nexp = new NonogramExplorer(nonogramProvider,
        // settings.getColorModel());
        // nexp.setVisible(true);

        // reset mouse cursor
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        // if no nonogram was played before...
        if (currentNonogram == null) {

            // ...disable all buttons when no new nonogram was selected...
            if (chosenNonogram == null) {

                pauseButton.setEnabled(false);
                stopButton.setEnabled(false);
                restartButton.setEnabled(false);
            }
            // ...or start nonogram chosen by user.
            else {

                pauseButton.setEnabled(true);
                stopButton.setEnabled(true);
                restartButton.setEnabled(true);

                setCurrentNonogram(chosenNonogram);
                logger.debug("Nonogram chosen by user: " + chosenNonogram);

                buildBoard();

                eventHelper.fireProgramControlEvent(new ProgramControlEvent(
                        this, ProgramControlType.NONOGRAM_CHOSEN,
                        this.currentNonogram));

                eventHelper.fireProgramControlEvent(new ProgramControlEvent(
                        this, ProgramControlType.START_GAME,
                        this.currentNonogram));
            }
        }
        // if a specific nonogram was played before...
        else {

            // ...and user chose no new nonogram, resume old one...
            if (chosenNonogram == null) {

                performStop();
            }
            // ...or a new nonogram should be started.
            else {

                pauseButton.setEnabled(true);
                stopButton.setEnabled(true);
                restartButton.setEnabled(true);

                setCurrentNonogram(chosenNonogram);
                logger.debug("Nonogram chosen by user: " + chosenNonogram);

                buildBoard();

                eventHelper.fireProgramControlEvent(new ProgramControlEvent(
                        this, ProgramControlType.NONOGRAM_CHOSEN,
                        this.currentNonogram));

                eventHelper.fireProgramControlEvent(new ProgramControlEvent(
                        this, ProgramControlType.START_GAME,
                        this.currentNonogram));
            }
        }
    }

    private void performRestart() {

        performStop();

        if (currentNonogram != null) {

            pauseButton.setEnabled(true);
            stopButton.setEnabled(true);
            restartButton.setEnabled(true);

            buildBoard();

            eventHelper.fireProgramControlEvent(new ProgramControlEvent(this,
                    ProgramControlType.RESTART_GAME, this.currentNonogram));
        }
    }

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

    private void setPauseButtonToPause() {

        pauseButton.setIcon(new ImageIcon(getClass().getResource(
                "/resources/icon/button_pause.png")));
        pauseButton.setToolTipText(Messages.getString("MainUI.PauseTooltip"));
    }

    private void setPauseButtonToResume() {

        pauseButton.setIcon(new ImageIcon(getClass().getResource(
                "/resources/icon/button_resume.png")));
        pauseButton.setToolTipText(Messages.getString("MainUI.ResumeTooltip"));
    }

    private void performStop() {

        eventHelper.fireProgramControlEvent(new ProgramControlEvent(this,
                ProgramControlType.STOP_GAME));

        setPauseButtonToPause();

        pauseButton.setEnabled(false);
        restartButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    private void performExit() {

        int answer = JOptionPane.OK_OPTION;

        if (gameRunning) {
            answer = JOptionPane.showConfirmDialog(this,
                    Messages.getString("MainUI.QuestionQuitProgramm"),
                    Messages.getString("MainUI.QuestionQuitProgrammTitle"),
                    JOptionPane.YES_NO_OPTION);
        }

        if (answer == JOptionPane.OK_OPTION) {
            eventHelper.fireProgramControlEvent(new ProgramControlEvent(this,
                    ProgramControlType.QUIT_PROGRAMM));

            this.setVisible(false);
            this.dispose();
        }
    }

    private void handleCoop() {

        CoopStartDialog csd = new CoopStartDialog(settings);

        csd.setVisible(true);
    }

    /**
     * Functions providing organizational and statistical dialogs
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

    private void showEdit() {

        if (currentNonogram != null)
            logger.debug("Open editor frame with nonogram: "
                    + currentNonogram.getOriginPath());
        // TODO Add call of FNE
    }

    private void showStatistics() {

        // TODO implement statistics dialog
    }

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

    /**
     * Functions providing gui elements
     */

    /**
     * This method initializes jJToolBarBar
     * 
     * @return javax.swing.JToolBar
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
            // toolBar.add(getCoopButton());
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
     * This method initializes startButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getStartButton() {
        if (startButton == null) {
            startButton = new JButton();
            startButton.setText(""); //$NON-NLS-1$
            startButton.setFocusable(false);
            startButton.setIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_start.png"))); //$NON-NLS-1$
            startButton.setToolTipText(Messages
                    .getString("MainUI.StartTooltip")); //$NON-NLS-1$
            startButton.setDisabledIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_start2.png"))); //$NON-NLS-1$
            startButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    performStart();
                }
            });
        }
        return startButton;
    }

    /**
     * This method initializes coopButton
     * 
     * @return javax.swing.JButton
     */
    @SuppressWarnings("unused")
    private JButton getCoopButton() {

        if (coopButton == null) {
            coopButton = new JButton();
            coopButton.setText(""); //$NON-NLS-1$
            coopButton.setFocusable(false);
            coopButton.setIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_coop.png"))); //$NON-NLS-1$
            coopButton
                    .setToolTipText(Messages.getString("MainUI.StartTooltip")); //$NON-NLS-1$
            coopButton.setDisabledIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_coop2.png"))); //$NON-NLS-1$
            coopButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleCoop();
                }
            });
        }
        return coopButton;
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
                    "/resources/icon/button_pause.png"))); //$NON-NLS-1$
            pauseButton.setText(""); //$NON-NLS-1$
            pauseButton.setEnabled(false);
            pauseButton.setFocusable(false);
            pauseButton.setDisabledIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_pause2.png"))); //$NON-NLS-1$
            pauseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    performPause();
                }
            });
        }
        return pauseButton;
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
                    "/resources/icon/button_stop.png"))); //$NON-NLS-1$
            stopButton.setText(""); //$NON-NLS-1$
            stopButton.setEnabled(false);
            stopButton.setFocusable(false);
            stopButton.setDisabledIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_stop2.png"))); //$NON-NLS-1$
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
                    "/resources/icon/button_restart.png"))); //$NON-NLS-1$
            restartButton.setText(""); //$NON-NLS-1$
            restartButton.setEnabled(false);
            restartButton.setFocusable(false);
            restartButton.setDisabledIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_restart2.png"))); //$NON-NLS-1$
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
                    "/resources/icon/button_exit.png"))); //$NON-NLS-1$
            exitButton.setEnabled(true);
            exitButton.setDisabledIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_exit2.png"))); //$NON-NLS-1$
            exitButton.setText(""); //$NON-NLS-1$
            exitButton.setFocusable(false);
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
                    "/resources/icon/button_about2.png"))); //$NON-NLS-1$
            aboutButton.setIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_about.png"))); //$NON-NLS-1$
            aboutButton.setText(""); //$NON-NLS-1$
            aboutButton.setFocusable(false);
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
                    "/resources/icon/button_config2.png"))); //$NON-NLS-1$
            optionsButton.setIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_config.png"))); //$NON-NLS-1$
            optionsButton.setText(""); //$NON-NLS-1$
            optionsButton.setEnabled(true);
            optionsButton.setFocusable(false);
            optionsButton
                    .addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            showOptions();
                        }
                    });
        }
        return optionsButton;
    }

    /**
     * This method initializes helpButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getHelpButton() {
        if (helpButton == null) {
            helpButton = new JButton();
            helpButton.setComponentOrientation(ComponentOrientation.UNKNOWN);
            helpButton.setToolTipText(Messages.getString("MainUI.HelpTooltip")); //$NON-NLS-1$
            helpButton.setDisabledIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_help2.png"))); //$NON-NLS-1$
            helpButton.setIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_help.png"))); //$NON-NLS-1$
            helpButton.setText(""); //$NON-NLS-1$
            helpButton.setEnabled(true);
            helpButton.setFocusable(false);
            helpButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showHelp();
                }
            });
        }
        return helpButton;
    }

    /**
     * This method initializes editButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getEditButton() {
        if (editButton == null) {
            editButton = new JButton();
            editButton.setComponentOrientation(ComponentOrientation.UNKNOWN);
            editButton.setToolTipText(Messages.getString("MainUI.EditTooltip")); //$NON-NLS-1$
            editButton.setDisabledIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_edit2.png"))); //$NON-NLS-1$
            editButton.setIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_edit.png"))); //$NON-NLS-1$
            editButton.setText(""); //$NON-NLS-1$
            editButton.setEnabled(false);
            editButton.setFocusable(false);
            editButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showEdit();
                }
            });
        }
        return editButton;
    }

    /**
     * This method initializes statisticsButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getStatisticsButton() {
        if (statisticsButton == null) {
            statisticsButton = new JButton();
            statisticsButton
                    .setComponentOrientation(ComponentOrientation.UNKNOWN);
            statisticsButton.setToolTipText(Messages
                    .getString("MainUI.StatisticsTooltip")); //$NON-NLS-1$
            statisticsButton.setDisabledIcon(new ImageIcon(getClass()
                    .getResource("/resources/icon/button_statistics2.png"))); //$NON-NLS-1$
            statisticsButton.setIcon(new ImageIcon(getClass().getResource(
                    "/resources/icon/button_statistics.png"))); //$NON-NLS-1$
            statisticsButton.setText(""); //$NON-NLS-1$
            statisticsButton.setFocusable(false);
            statisticsButton.setEnabled(false);
            statisticsButton
                    .addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            showStatistics();
                        }
                    });
        }
        return statisticsButton;
    }

    /**
     * Miscellaneous functions
     */

    private void handleGameEnding(boolean isSolved) {

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
        BoardPreview preview = boardPanel.getPreviewArea();

        if (isSolved)
            saveThumbnail(preview.getPreviewImage());

        // show GameOver dialog
        GameOverUI ui = new GameOverUI(currentNonogram, preview, isSolved,
                settings);
        ui.setVisible(true);

        // TODO: handle highscore entry
    }

    private void saveThumbnail(BufferedImage preview) {

        File thumbDir = new File(DEFAULT_THUMBNAILS_PATH);

        if (!thumbDir.exists())
            thumbDir.mkdirs();

        // TODO save file only if it not exist
        File thumbFile = new File(thumbDir, currentNonogram.getHash());

        try {

            ImageIO.write((RenderedImage) preview, "png", thumbFile);

        } catch (IOException e) {

            logger.warn("Could not write preview image to file " + thumbFile);
        }

        logger.info("Preview image written to file " + thumbFile);
    }

}
