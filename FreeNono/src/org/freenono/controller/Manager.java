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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.LogManager;

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
import org.freenono.provider.CollectionListener;
import org.freenono.provider.CollectionProvider;
import org.freenono.serializer.SettingsFormatException;
import org.freenono.serializer.SettingsSerializer;
import org.freenono.serializer.XMLSettingsSerializer;
import org.freenono.sound.AudioProvider;
import org.freenono.ui.MainUI;
import org.freenono.ui.Messages;
import org.freenono.ui.common.FontFactory;
import org.freenono.ui.common.Tools;

/**
 * Manager loads settings from file and instantiates all components of FreeNono
 * like the audio subsystem, highscore manager, ... Nonograms are loaded through
 * collection provider and at the end UI is made visible.
 * 
 * @author Christian Wichmann
 */
public final class Manager {

    private static Logger logger = Logger.getLogger(Manager.class);

    private static final String DEFAULT_NONOGRAM_PATH = "./nonograms";
    private static final String DEFAULT_NONOGRAM_PATH_WINDOWS = System
            .getProperty("user.dir") + Tools.FILE_SEPARATOR + "nonograms";
    private static final String DEFAULT_NONOGRAM_PATH_LINUX = "/usr/share/freenono/nonograms";
    private static final String DEFAULT_NONOGRAM_PATH_DEBIAN = "/usr/share/games/freenono/nonograms";

    private static final String USER_NONOGRAM_PATH = System
            .getProperty("user.home")
            + Tools.FILE_SEPARATOR
            + ".FreeNono"
            + Tools.FILE_SEPARATOR + "nonograms";
    private static final String DEFAULT_SETTINGS_FILE = System
            .getProperty("user.home")
            + Tools.FILE_SEPARATOR
            + ".FreeNono"
            + Tools.FILE_SEPARATOR + "freenono.xml";

    @SuppressWarnings("unused")
    private static final String DEFAULT_NONO_SERVER = "http://127.0.0.1";

    /**
     * Gives path to save thumbnail images for already solved nonograms.
     */
    public static final String DEFAULT_THUMBNAILS_PATH = System
            .getProperty("user.home")
            + Tools.FILE_SEPARATOR
            + ".FreeNono"
            + Tools.FILE_SEPARATOR + "thumbnails";

    /**
     * Defines locales for all supported languages. The neutral locale
     * <code>Locale.ROOT</code> indicates to use the systems default locale.
     */
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
    private int alreadyLoadedCourses = 0;
    private int numberOfCourses = 100;

    private MainUI mainUI = null;
    private GameEventHelper eventHelper = null;
    private AudioProvider audioProvider = null;
    private Game currentGame = null;
    private Settings settings = null;
    private String settingsFile = DEFAULT_SETTINGS_FILE;
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
     * Initializes manager and all subsystems using the default settings file.
     */
    public Manager() {

        splash = SplashScreen.getSplashScreen();

        initialize();
    }

    /**
     * Initializes manager and all subsystems.
     */
    private void initialize() {

        /*
         * Disable java's own logging facility (java.util.logging) to prevent
         * third party libraries to clutter the console output. All error in
         * libraries should be handled and logged in FreeNono itself.
         */
        LogManager.getLogManager().reset();
        // java.util.logging.Logger.getLogger(
        // java.util.logging.Logger.GLOBAL_LOGGER_NAME).setLevel(
        // java.util.logging.Level.OFF);

        // load settings from file
        loadSettings(settingsFile);
        if (!settings.getGameLocale().equals(Locale.ROOT)) {
            Locale.setDefault(settings.getGameLocale());
        }

        createSplashscreen();

        updateSplashscreen(Messages.getString("Splashscreen.Building"), false);

        // instantiate GameEventHelper and add own gameAdapter
        eventHelper = new GameEventHelper();
        eventHelper.addGameListener(gameAdapter);

        settings.setEventHelper(eventHelper);

        // instantiate audio provider for game sounds
        audioProvider = new AudioProvider(eventHelper, settings);

        // instantiate highscore manager
        HighscoreManager.getInstance(settings).setEventHelper(eventHelper);

        // set game event helper for statistics manager
        SimpleStatistics.getInstance().setEventHelper(eventHelper);

        preloadLibraries();

        updateSplashscreen(Messages.getString("Splashscreen.Loading"), true);

        // instantiate collection provider for all nonogram sources
        instantiateProvider();

        updateSplashscreen(Messages.getString("Splashscreen.Starting"), false);
    }

    /**
     * Loads libraries before they are used so that the about and help dialog
     * are shown faster.
     */
    private void preloadLibraries() {

        try {
            Class.forName("org.xhtmlrenderer.simple.XHTMLPanel");
            Class.forName("org.xhtmlrenderer.simple.FSScrollPane");
            Class.forName("com.kitfox.svg.app.beans.SVGPanel");
        } catch (ClassNotFoundException e) {
            logger.error("Could not preload libraries.");
        }
    }

    /**
     * Starts the swing UI of FreeNono and close the splash screen.
     */
    public void startSwingUI() {

        // start swing UI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // set look and feel to new (since Java SE 6 Update 10 release
                // standard and instantiate mainUI
                try {
                    for (LookAndFeelInfo info : UIManager
                            .getInstalledLookAndFeels()) {
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

                mainUI = new MainUI(eventHelper, settings, nonogramProvider);
                mainUI.setVisible(true);
            }
        });

        // close splashscreen
        closeSplashscreen();
    }

    /*
     * Methods concerning the splash screen.
     */

    /**
     * Creates and initializes a splash screen based on image shown by vm while
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
     * Updates splash screen with message.
     * 
     * @param message
     *            message to display in splash screen.
     * @param drawProgressBar
     *            whether to draw the progress bar or not
     */
    private void updateSplashscreen(final String message,
            final boolean drawProgressBar) {

        if (splashGraphics != null) {
            // update message
            final int splashWidth = 700;
            final int splashHeight = 250;
            final int splashStringX = 54;
            final int splashStringY = 400;
            splashGraphics.setComposite(AlphaComposite.Clear);
            splashGraphics.fillRect(0, splashHeight, splashWidth, splashHeight);
            splashGraphics.setPaintMode();
            splashGraphics.drawString(message, splashStringX, splashStringY);

            if (drawProgressBar) {
                // update progress bar
                final int progressBarX = 54;
                final int progressBarY = 404;
                final int progressBarHeight = 4;
                final int progressBarWidth = (int) (207. / numberOfCourses * alreadyLoadedCourses);
                // splashGraphics.setColor(Color.RED);
                splashGraphics.fillRect(progressBarX, progressBarY,
                        progressBarWidth, progressBarHeight);
            }
            splash.update();
        }
    }

    /**
     * Closes splash screen.
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

    /*
     * Methods concerning the loading and saving of data.
     */

    /**
     * Instantiate nonogram provider.
     */
    private void instantiateProvider() {

        if (Tools.isRunningJavaWebStart()) {
            /*
             * Get nonograms from jar file.
             */
            nonogramProvider.add(new CollectionFromJar(Messages
                    .getString("Manager.LocalNonogramsProvider")));

            /*
             * Get nonograms by seed provider.
             */
            nonogramProvider.add(new CollectionFromSeed(Messages
                    .getString("Manager.SeedNonogramProvider")));

        } else {
            /*
             * Get nonograms from distribution.
             */
            CollectionFromFilesystem collection1 = new CollectionFromFilesystem(
                    getNonogramPath(),
                    Messages.getString("Manager.LocalNonogramsProvider"), false);

            collection1.startLoading(new CollectionListener() {
                @Override
                public void collectionLoading(final CollectionEvent e) {
                    numberOfCourses = e.getCoursesInCollection();
                    alreadyLoadedCourses = e.getCoursesAlreadyLoaded();
                    updateSplashscreen(
                            Messages.getString("Splashscreen.LoadingLocal"),
                            true);
                }

                @Override
                public void collectionChanged(final CollectionEvent e) {
                }
            });
            nonogramProvider.add(collection1);

            /*
             * Get users nonograms from home directory.
             */
            numberOfCourses = 0;
            alreadyLoadedCourses = 0;
            CollectionFromFilesystem collection2 = new CollectionFromFilesystem(
                    USER_NONOGRAM_PATH,
                    Messages.getString("Manager.UserNonogramsProvider"), false);

            collection2.startLoading(new CollectionListener() {
                @Override
                public void collectionLoading(final CollectionEvent e) {
                    numberOfCourses = e.getCoursesInCollection();
                    alreadyLoadedCourses = e.getCoursesAlreadyLoaded();
                    updateSplashscreen(
                            Messages.getString("Splashscreen.LoadingUser"),
                            true);
                }

                @Override
                public void collectionChanged(final CollectionEvent e) {
                }
            });
            nonogramProvider.add(collection2);

            /*
             * Get nonograms by seed provider.
             */
            nonogramProvider.add(new CollectionFromSeed(Messages
                    .getString("Manager.SeedNonogramProvider")));
        }

        // get nonograms from NonoServer
        // nonogramProvider.add(new CollectionFromServer(DEFAULT_NONO_SERVER,
        // "NonoServer"));
    }

    /**
     * Get local nonogram path according to OS.
     * 
     * @return Local nonogram path.
     */
    private String getNonogramPath() {

        String os = System.getProperty("os.name");

        String nonogramPath = DEFAULT_NONOGRAM_PATH;

        if (os.equals("Linux")) {
            File f1 = new File(DEFAULT_NONOGRAM_PATH);
            File f2 = new File(DEFAULT_NONOGRAM_PATH_DEBIAN);
            if (f1.isDirectory()) {
                nonogramPath = DEFAULT_NONOGRAM_PATH;
            } else if (f2.isDirectory()) {
                nonogramPath = DEFAULT_NONOGRAM_PATH_DEBIAN;
            } else {
                nonogramPath = DEFAULT_NONOGRAM_PATH_LINUX;
            }
        } else if (os.startsWith("Windows")) {
            nonogramPath = DEFAULT_NONOGRAM_PATH_WINDOWS;
        }

        return nonogramPath;
    }

    /**
     * Load settings from given settings file.
     * 
     * @param settingsFile
     *            settings file to use
     */
    private void loadSettings(final String settingsFile) {

        if (settingsFile == null || "".equals(settingsFile)) {
            throw new IllegalArgumentException(
                    "Parameter settingsFile is invalid.");
        }

        this.settingsFile = settingsFile;
        File file = new File(settingsFile);

        try {
            settings = settingsSerializer.load(file);

        } catch (SettingsFormatException e) {
            // if SettingsFormatException was thrown file exists and can be
            // accessed
            file.delete();
            settings = new Settings();
            logger.error("SettingsFormatException when loading settings file.");
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

    /*
     * Methods concerning the program flow.
     */

    /**
     * Creates a new game using given nonogram.
     * 
     * @param nonogram
     *            nonogram to use for new game
     * @return created game model
     */
    private Game createGame(final Nonogram nonogram) {

        // create new Game instance
        Game g = new Game(eventHelper, nonogram, settings);

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

    /**
     * Restarts FreeNono. Not yet usable!
     */
    @SuppressWarnings("unused")
    private void restartProgram() {

        mainUI.removeEventHelper();
        mainUI.setVisible(false);
        mainUI.dispose();
        mainUI = null;

        Locale.setDefault(settings.getGameLocale());
        Messages.loadResourceBundle();
        FontFactory.resetFonts();

        // initialize();
        startSwingUI();
    }
}
