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
import org.freenono.provider.NonogramsFromFilesystem;
import org.freenono.provider.NonogramsFromSeed;
import org.freenono.provider.NonogramsFromServer;
import org.freenono.serializer.SettingsFormatException;
import org.freenono.serializer.SettingsSerializer;
import org.freenono.serializer.XMLSettingsSerializer;
import org.freenono.sound.AudioProvider;
import org.freenono.ui.MainUI;
import org.freenono.ui.Messages;

public class Manager {

	private static Logger logger = Logger.getLogger(Manager.class);

	public static final String DEFAULT_NONOGRAM_PATH = "./nonograms";
	public static final String DEFAULT_NONO_SERVER = "http://127.0.0.1";
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

	private GameEventHelper eventHelper = null;
	private MainUI mainUI = null;
	private AudioProvider audioProvider = null;
	private Game currentGame = null;
	private Statistics currentStatistics = null;
	private Nonogram currentPattern = null;
	private Settings settings = null;
	private String settingsFile = null;
	private SettingsSerializer settingsSerializer = new XMLSettingsSerializer();
	private List<CollectionProvider> nonogramProvider = null;

	private GameAdapter gameAdapter = new GameAdapter() {

		@Override
		public void OptionsChanged(ProgramControlEvent e) {

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
				currentGame = createGame(e.getPattern());
				break;

			case QUIT_PROGRAMM:
				logger.debug("program exited by user.");
				break;
			}
		}

	};

	public Manager() throws NullPointerException, FileNotFoundException,
			IOException {

		this(DEFAULT_SETTINGS_FILE);
	}

	public Manager(String settingsFile) throws FileNotFoundException,
			IOException {

		// instantiate GameEventHelper and add own gameAdapter
		eventHelper = new GameEventHelper();
		eventHelper.addGameListener(gameAdapter);

		// load settings from file
		loadSettings(settingsFile);

		// instantiate nonogramProvider in background
		instantiateProvider();

		// instantiate mainUI
		mainUI = new MainUI(eventHelper, settings, nonogramProvider);
		mainUI.setVisible(true);

		// instantiate audio provider for game sounds
		audioProvider = new AudioProvider(eventHelper, settings);
	}

	private void instantiateProvider() {

		nonogramProvider = new ArrayList<CollectionProvider>();

		// get nonograms from distribution
		try {
			nonogramProvider.add(new NonogramsFromFilesystem(
					DEFAULT_NONOGRAM_PATH, Messages
							.getString("Manager.LocalNonogramsProvider")));
		} catch (FileNotFoundException e) {
			logger.warn("No nonograms found at default nonogram directory!");
		}

		// get users nonograms from home directory
		File nonogramDirectory = new File(USER_NONOGRAM_PATH);
		if (!nonogramDirectory.exists()) {
			nonogramDirectory.mkdir();
		}
		try {
			nonogramProvider.add(new NonogramsFromFilesystem(
					USER_NONOGRAM_PATH, Messages
							.getString("Manager.UserNonogramsProvider")));
		} catch (FileNotFoundException e) {
			logger.warn("No nonograms found at users home directory!");
		}

		// get nonograms by seed provider
		nonogramProvider.add(new NonogramsFromSeed(Messages
				.getString("Manager.SeedNonogramProvider")));

		// // TODO load from server in background to start FreeNono faster
		// // get nonograms from NonoServer
		// try {
		// nonogramProvider.add(new NonogramsFromServer(DEFAULT_NONO_SERVER,
		// "NonoServer"));
		// } catch (MalformedURLException e) {
		// logger.error("Invalid server URL.");
		// } catch (NullPointerException e) {
		// logger.error("Invalid server URL.");
		// }
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
			// e.printStackTrace();
			// TODO check whether the old corrupt file should be deleted

		} catch (IOException e) {
			logger.error("IOException when loading settings file.");
			// e.printStackTrace();
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
		currentStatistics = new SimpleStatistics(n, eventHelper);

		return g;
	}

}
