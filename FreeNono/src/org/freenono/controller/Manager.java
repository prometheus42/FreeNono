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
package org.freenono.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.Logger;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.interfaces.CollectionProvider;
import org.freenono.model.Game;
import org.freenono.interfaces.Statistics;
import org.freenono.model.Nonogram;
import org.freenono.model.SimpleStatistics;
import org.freenono.model.Tools;
import org.freenono.provider.CollectionFromFilesystem;
import org.freenono.provider.CollectionFromSeed;
import org.freenono.provider.CollectionFromServer;
import org.freenono.serializer.SettingsFormatException;
import org.freenono.serializer.SettingsSerializer;
import org.freenono.serializer.XMLSettingsSerializer;
import org.freenono.sound.AudioProvider;
import org.freenono.ui.MainUI;
import org.freenono.ui.Messages;

public class Manager {

	private static Logger logger = Logger.getLogger(Manager.class);

	public static final String DEFAULT_NONOGRAM_PATH = "./nonograms";
	public static final String DEFAULT_NONOGRAM_PATH_WINDOWS = System
			.getProperty("user.dir") + Tools.FILE_SEPARATOR + "nonograms";
	public static final String DEFAULT_NONOGRAM_PATH_LINUX = "/usr/share/freenono/nonograms";
	public static final String DEFAULT_NONO_SERVER = "http://127.0.0.1";
	public static final String USER_NONOGRAM_PATH = System
			.getProperty("user.home") + Tools.FILE_SEPARATOR
			+ ".FreeNono" + Tools.FILE_SEPARATOR + "nonograms";
	public static final String DEFAULT_SETTINGS_FILE = System
			.getProperty("user.home") + Tools.FILE_SEPARATOR
			+ ".FreeNono" + Tools.FILE_SEPARATOR + "freenono.xml";

	private GameEventHelper eventHelper = null;
	private MainUI mainUI = null;
	private AudioProvider audioProvider = null;
	private HighscoreManager highscoreManager = null;
	private Game currentGame = null;
	private Statistics currentStatistics = null;
	private Nonogram currentPattern = null;
	private Settings settings = null;
	private String settingsFile = null;
	private SettingsSerializer settingsSerializer = new XMLSettingsSerializer();
	private List<CollectionProvider> nonogramProvider = new ArrayList<CollectionProvider>();

	private GameAdapter gameAdapter = new GameAdapter() {

		@Override
		public void OptionsChanged(ProgramControlEvent e) {

			// When an options is changed, save config file.
			saveSettings(new File(settingsFile));
		}

		@Override
		public void ProgramControl(ProgramControlEvent e) {

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
				if (currentGame != null)
					currentGame.removeEventHelper();
				currentGame = createGame(e.getPattern());
				break;

			case QUIT_PROGRAMM:
				quitProgram();
				break;
			}
		}

	};

	public Manager() throws NullPointerException, FileNotFoundException,
			IOException {

		this(DEFAULT_SETTINGS_FILE);
	}

	public Manager(String settingsFile) throws FileNotFoundException {

		// instantiate GameEventHelper and add own gameAdapter
		eventHelper = new GameEventHelper();
		eventHelper.addGameListener(gameAdapter);

		
		// load settings from file
		loadSettings(settingsFile);
		
		
		// instantiate nonogramProvider in background
		instantiateProvider();
				
				
		// set look and feel to new (since Java SE 6 Update 10 release
		// standard and instantiate mainUI
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e1) {
			logger.warn("Needed java look and feel not available. FreeNono requires Java SE 6 Update 10 or later.");
		}
		mainUI = new MainUI(eventHelper, settings, nonogramProvider);
		mainUI.setVisible(true);

				
		// instantiate audio provider for game sounds
		audioProvider = new AudioProvider(eventHelper, settings);
		
		
		// instantiate highscore manager
		highscoreManager = new HighscoreManager(eventHelper);
	}

	
	private void instantiateProvider() {

		// get nonograms from distribution
		nonogramProvider.add(new CollectionFromFilesystem(getNonogramPath(),
				Messages.getString("Manager.LocalNonogramsProvider")));
		
		
		// get users nonograms from home directory
		nonogramProvider.add(new CollectionFromFilesystem(USER_NONOGRAM_PATH,
				Messages.getString("Manager.UserNonogramsProvider")));

		
		// get nonograms by seed provider
		nonogramProvider.add(new CollectionFromSeed(Messages
				.getString("Manager.SeedNonogramProvider")));

		
		// get nonograms from NonoServer
		// nonogramProvider.add(new CollectionFromServer(DEFAULT_NONO_SERVER,
		// "NonoServer"));
	}
	
	private String getNonogramPath() {
		
		String os = System.getProperty("os.name");
		
		if (os.equals("Linux")) {
			File f = new File(DEFAULT_NONOGRAM_PATH_LINUX);
			if (f.isDirectory())
				return DEFAULT_NONOGRAM_PATH_LINUX;
			else 
				return DEFAULT_NONOGRAM_PATH;
		}
		else if (os.startsWith("Windows"))
			return DEFAULT_NONOGRAM_PATH_WINDOWS;
		else 
			return DEFAULT_NONOGRAM_PATH;
	}

	
	private void loadSettings(String settingsFile) throws FileNotFoundException {

		if (settingsFile == null) {
			throw new NullPointerException("Parameter settingsFile is null");
		}

		this.settingsFile = settingsFile;
		loadSettings(new File(settingsFile));
	}

	private void loadSettings(File file) {

		try {
			
			settings = settingsSerializer.load(file);
			
		} catch (SettingsFormatException e) {
			
			logger.error("InvalidFormatException when loading settings file.");
			// TODO check whether the old corrupt file should be deleted

		} catch (IOException e) {
			
			logger.error("IOException when loading settings file.");
			// TODO check whether the old corrupt file should be deleted
		}

		if (settings == null) {
			
			settings = new Settings();
			logger.warn("Settings file not found. Using default settings!");
		}

		settings.setEventHelper(eventHelper);
	}

	private void saveSettings(File file) {

		try {
			settingsSerializer.save(this.settings, file);

		} catch (IOException e) {
			logger.warn("Settings file could not be saved. An IO error occured!");
			// TODO check whether the old corrupt file should be deleted
		}
	}

	public Game createGame(Nonogram n) {

		currentPattern = n;

		// create new Game instance
		Game g = new Game(eventHelper, currentPattern, settings);

		// create Statistics instance on an per Game basis
		if (currentStatistics != null)
			currentStatistics.removeEventHelper();
		currentStatistics = new SimpleStatistics(n);
		currentStatistics.setEventHelper(eventHelper);

		return g;
	}
	
	public void quitProgram() {
		
		logger.debug("program exited by user.");
		
		audioProvider.closeAudio();
		audioProvider.removeEventHelper();

		// TODO Why is this call necessary?
		// System.gc();
		// try {
		// Thread.sleep(2000);
		// } catch (InterruptedException ex) {
		// Thread.currentThread().interrupt();
		// }
		System.exit(0);
	}

}
