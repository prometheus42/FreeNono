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
package org.freenono.controller;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SplashScreen;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;
import org.freenono.RunUI;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.model.Game;
import org.freenono.model.data.Nonogram;
import org.freenono.provider.CollectionFromFilesystem;
import org.freenono.provider.CollectionFromJar;
import org.freenono.provider.CollectionFromSeed;
import org.freenono.provider.CollectionProvider;
import org.freenono.serializer.SettingsFormatException;
import org.freenono.serializer.SettingsSerializer;
import org.freenono.serializer.XMLSettingsSerializer;
import org.freenono.sound.AudioProvider;
import org.freenono.ui.FontFactory;
import org.freenono.ui.MainUI;
import org.freenono.ui.Messages;
import org.freenono.ui.common.Tools;

/**
 * Manager loads settings from file and instantiates all components of FreeNono
 * like the audio subsystem, highscore manager, ... Nonograms are loaded through
 * collection provider and at the end UI is made visible.
 * 
 * @author Christian Wichmann
 */
public class Manager {

    private static Logger logger = Logger.getLogger(Manager.class);

    public static final String DEFAULT_NONOGRAM_PATH = "./nonograms";
    public static final String DEFAULT_NONOGRAM_PATH_WINDOWS = System
            .getProperty("user.dir") + Tools.FILE_SEPARATOR + "nonograms";
    public static final String DEFAULT_NONOGRAM_PATH_LINUX = "/usr/share/freenono/nonograms";

    public static final String USER_NONOGRAM_PATH = System
            .getProperty("user.home")
            + Tools.FILE_SEPARATOR
            + ".FreeNono"
            + Tools.FILE_SEPARATOR + "nonograms";
    public static final String DEFAULT_SETTINGS_FILE = System
            .getProperty("user.home")
            + Tools.FILE_SEPARATOR
            + ".FreeNono"
            + Tools.FILE_SEPARATOR + "freenono.xml";

    public static final String DEFAULT_NONO_SERVER = "http://127.0.0.1";

    public static final Locale[] SUPPORTED_LANGUAGES = {Locale.GERMAN,
            Locale.ENGLISH, Locale.JAPANESE, Locale.ROOT};

    // TODO make directory hidden under windows
    // with Java 7:
    // Path path = FileSystems.getDefault().getPath("/j", "sa");
    // Files.setAttribute(path, "dos:hidden", true);
    // with Java 5:
    // Process process =
    // Runtime.getRuntime().exec("cmd.exe /C attrib -s -h -r your_path");

    private final SplashScreen splash;
    private Graphics2D splashGraphics = null;

    private GameEventHelper eventHelper = null;
    private MainUI mainUI = null;
    private AudioProvider audioProvider = null;
    // private HighscoreManager highscoreManager = null;
    private Game currentGame = null;
    private Nonogram currentPattern = null;
    private Settings settings = null;
    private String settingsFile = null;
    private SettingsSerializer settingsSerializer = new XMLSettingsSerializer();
    private List<CollectionProvider> nonogramProvider = new ArrayList<CollectionProvider>();

    private GameAdapter gameAdapter = new GameAdapter() {
        @Override
        public void optionsChanged(final ProgramControlEvent e) {

            // When an options is changed, save config file.
            saveSettings(new File(settingsFile));
        }

        @Override
        public void programControl(final ProgramControlEvent e) {

            switch (e.getPct()) {
            case START_GAME:
                break;

            case STOP_GAME:
                break;

            case RESTART_GAME:
                break;

            case PAUSE_GAME:
                break;

            case RESUME_GAME:
                break;

            case NONOGRAM_CHOSEN:
                // If new nonogram was chosen remove old event helper from
                // game object and instantiate new game object!
                // TODO should the nonogram be distributed this way?!
                if (currentGame != null) {
                    currentGame.removeEventHelper();
                }
                currentGame = createGame(e.getPattern());
                break;

            case QUIT_PROGRAMM:
                quitProgram();
                break;

            case OPTIONS_CHANGED:
                break;

            case SHOW_ABOUT:
                break;

            case SHOW_OPTIONS:
                break;

            default:
                break;
            }
        }
    };

    /**
     * Default constructor of Manager. Uses DEFAULT_SETTINGS_FILE as settings
     * file.
     * @throws IOException
     *             ???
     */
    public Manager() throws IOException {
        this(DEFAULT_SETTINGS_FILE);
    }

    /**
     * Constructor of Manager that uses 'settingsFile' as settings file.
     * @param settingsFile
     *            Settings file to use.
     */
    public Manager(final String settingsFile) {

        splash = SplashScreen.getSplashScreen();
        createSplashscreen();

        // load settings from file
        loadSettings(settingsFile);
        if (!settings.getGameLocale().equals(Locale.ROOT)) {
            Locale.setDefault(settings.getGameLocale());
        }

        updateSplashscreen(Messages.getString("Splashscreen.Building"));

        // instantiate GameEventHelper and add own gameAdapter
        eventHelper = new GameEventHelper();
        eventHelper.addGameListener(gameAdapter);

        settings.setEventHelper(eventHelper);

        // instantiate audio provider for game sounds
        audioProvider = new AudioProvider(eventHelper, settings);

        // instantiate highscore manager
        // highscoreManager = new HighscoreManager(eventHelper);

        // set game event helper for statistics manager
        SimpleStatistics.getInstance().setEventHelper(eventHelper);

        updateSplashscreen(Messages.getString("Splashscreen.Loading"));

        // instantiate collection provider for all nonogram sources
        instantiateProvider();

        updateSplashscreen(Messages.getString("Splashscreen.Starting"));
    }

    /**
     * Start the swing UI of FreeNono and close the splash screen.
     */
    public final void startSwingUI() {

        // set look and feel to new (since Java SE 6 Update 10 release
        // standard and instantiate mainUI
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException e) {
            logger.warn("Could not set look and feel.");
        } catch (InstantiationException e) {
            logger.warn("Could not set look and feel.");
        } catch (IllegalAccessException e) {
            logger.warn("Could not set look and feel.");
        } catch (UnsupportedLookAndFeelException e1) {
            logger.warn("Could not set look and feel.");
        }

        // start swing UI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mainUI = new MainUI(eventHelper, settings, nonogramProvider);
                mainUI.setVisible(true);
            }
        });

        // close splashscreen
        closeSplashscreen();
    }

    /**
     * Create and initialize a splashscreen based on image shown by vm while
     * starting.
     */
    private void createSplashscreen() {

        // Create splash screen only when not yet happened.
        if (splash != null) {

            splashGraphics = splash.createGraphics();

            if (splashGraphics != null) {

                final Color splashscreenColor = new Color(190, 190, 190);
                final int versionX = 54;
                final int versionY = 145;
                final float versionFontSize = 18;
                final String versionString = RunUI.class.getPackage()
                        .getSpecificationVersion();

                splashGraphics.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                splashGraphics.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                splashGraphics.setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                splashGraphics.setRenderingHint(RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY);

                splashGraphics.setColor(splashscreenColor);
                splashGraphics.setFont(FontFactory.createSplashscreenFont()
                        .deriveFont(versionFontSize));

                splashGraphics.setPaintMode();
                splashGraphics.drawString("Version " + versionString, versionX,
                        versionY);
                splash.update();

                splashGraphics.setFont(FontFactory.createSplashscreenFont());
            }
        }
    }

    /**
     * Update splashscreen with message.
     * @param message
     *            Message to display in splashscreen.
     */
    private void updateSplashscreen(final String message) {
        if (splashGraphics != null) {
            final int splashWidth = 700;
            final int splashHeight = 250;
            final int splashStringX = 54;
            final int splashStringY = 405;

            splashGraphics.setComposite(AlphaComposite.Clear);
            splashGraphics.fillRect(0, splashHeight, splashWidth, splashHeight);
            splashGraphics.setPaintMode();
            splashGraphics.drawString(message, splashStringX, splashStringY);
            splash.update();
        }
    }

    /**
     * Close splashscreen.
     */
    private void closeSplashscreen() {
        if (splash != null) {
            try {
                splash.close();
            } catch (IllegalStateException e) {
                logger.warn("Could not close splashscreen.");
            }
        }
    }

    /**
     * Instantiate nonogram provider.
     */
    private void instantiateProvider() {
        
        if (isRunningJavaWebStart()) {
            // get nonograms from jar file
            nonogramProvider.add(new CollectionFromJar(Messages
                    .getString("Manager.LocalNonogramsProvider")));
        } else {
            // get nonograms from distribution
            nonogramProvider
                    .add(new CollectionFromFilesystem(
                            getNonogramPath(),
                            Messages.getString("Manager.LocalNonogramsProvider"),
                            false));

            // get users nonograms from home directory
            nonogramProvider
                    .add(new CollectionFromFilesystem(
                            USER_NONOGRAM_PATH,
                            Messages.getString("Manager.UserNonogramsProvider"),
                            false));
        }

        // get nonograms by seed provider
        nonogramProvider.add(new CollectionFromSeed(Messages
                .getString("Manager.SeedNonogramProvider")));

        // get nonograms from NonoServer
        // nonogramProvider.add(new CollectionFromServer(DEFAULT_NONO_SERVER,
        // "NonoServer"));
    }

    /**
     * Checks whether program runs under the normal VM or was started via Java
     * Web Start.
     * @return true, if program is running under Java Web Start and not under
     *         the normal VM.
     */
    private boolean isRunningJavaWebStart() {
        boolean hasJNLP = false;

        try {
            Class.forName("javax.jnlp.ServiceManager");
            hasJNLP = true;
        } catch (ClassNotFoundException ex) {
            hasJNLP = false;
        }
        return hasJNLP;
    }

    /**
     * Get local nonogram path according to OS.
     * @return Local nonogram path.
     */
    private String getNonogramPath() {
        String os = System.getProperty("os.name");

        String nonogramPath = DEFAULT_NONOGRAM_PATH;

        if (os.equals("Linux")) {
            File f = new File(DEFAULT_NONOGRAM_PATH_LINUX);
            if (f.isDirectory()) {
                nonogramPath = DEFAULT_NONOGRAM_PATH_LINUX;
            } else {
                nonogramPath = DEFAULT_NONOGRAM_PATH;
            }
        } else if (os.startsWith("Windows")) {
            nonogramPath = DEFAULT_NONOGRAM_PATH_WINDOWS;
        }

        return nonogramPath;
    }

    /**
     * Load settings from file defined by String.
     * @param settingsFile
     *            Settings file to use.
     */
    @Deprecated
    private void loadSettings(final String settingsFile) {
        if (settingsFile == null || settingsFile == "") {
            throw new IllegalArgumentException(
                    "Parameter settingsFile is invalid.");
        }

        this.settingsFile = settingsFile;
        loadSettings(new File(settingsFile));
    }

    /**
     * Load settings from file defined by a File object.
     * @param file
     *            Settings file to use.
     */
    private void loadSettings(final File file) {

        try {
            settings = settingsSerializer.load(file);
        } catch (SettingsFormatException e) {

            logger.error("InvalidFormatException when loading settings file.");
            // TODO check whether the old corrupt file should be deleted
        }

        if (settings == null) {

            settings = new Settings();
            logger.warn("Using default settings!");
        }

        settings.setEventHelper(eventHelper);
    }

    /**
     * Save settings to file.
     * @param file
     *            File to save the settings.
     */
    private void saveSettings(final File file) {

        settingsSerializer.save(this.settings, file);
    }

    /**
     * Create a new game using Nonogram n.
     * @param n
     *            Nonogram to use.
     * @return Created game
     */
    private Game createGame(final Nonogram n) {

        currentPattern = n;

        // create new Game instance
        Game g = new Game(eventHelper, currentPattern, settings);

        return g;
    }

    /**
     * Quit program.
     */
    private void quitProgram() {

        logger.debug("program exited by user.");

        audioProvider.closeAudio();
        audioProvider.removeEventHelper();

        // TODO Is this call necessary?
        System.exit(0);
    }

}
