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
package org.freenono.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.StateChangeEvent;
import org.freenono.interfaces.CollectionProvider;
import org.freenono.interfaces.Statistics;
import org.freenono.provider.NonogramsFromFilesystem;
import org.freenono.provider.NonogramsFromSeed;
import org.freenono.serializer.SettingsFormatException;
import org.freenono.serializer.SettingsSerializer;
import org.freenono.serializer.XMLSettingsSerializer;
import org.freenono.sound.AudioProvider;
import org.freenono.ui.MainUI;
import org.freenono.ui.Messages;

public class Manager {

	private static Logger logger = Logger.getLogger(Manager.class);

	public static final String DEFAULT_NONOGRAM_PATH = "./nonograms";
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

		@Override
		public void StateChanged(StateChangeEvent e) {

		}
	};

	private Settings settings = null;
	private String settingsFile = null;
	private SettingsSerializer settingsSerializer = new XMLSettingsSerializer();

	public Manager() throws NullPointerException, FileNotFoundException,
			IOException {

		this(DEFAULT_SETTINGS_FILE);

	}

	public Manager(String settingsFile) throws NullPointerException,
			FileNotFoundException, IOException {

		// instantiate GameEventHelper and add own gameAdapter
		eventHelper = new GameEventHelper();
		eventHelper.addGameListener(gameAdapter);

		// load necessary files: settings, courses
		loadSettings(settingsFile);
		instantiateProvider();

		// instantiate mainUI
		mainUI = new MainUI(eventHelper, settings, nonogramProvider);
		mainUI.setVisible(true);

		// instantiate audio provider for game sounds
		audioProvider = new AudioProvider(getSettings().getPlayAudio());
		audioProvider.setEventHelper(eventHelper);

	}

	private void instantiateProvider() {

		nonogramProvider = new ArrayList<CollectionProvider>();
		try {
			nonogramProvider.add(new NonogramsFromFilesystem(
					DEFAULT_NONOGRAM_PATH, Messages
							.getString("Manager.LocalNonogramsProvider")));
		} catch (FileNotFoundException e) {
			logger.warn("No nonograms found at default nonogram directory!");
		}

		nonogramProvider.add(new NonogramsFromSeed(Messages
				.getString("Manager.SeedNonogramProvider")));

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

	public Settings getSettings() {
		return this.settings;
	}

	public Game createGame(Nonogram n) {

		currentPattern = n;

		Game g = new Game(currentPattern, settings);
		g.setEventHelper(eventHelper);

		// create Statistics instance on an per Game basis
		currentStatistics = new SimpleStatistics(n, eventHelper);

		return g;
	}

	public GameEventHelper getEventHelper() {
		return eventHelper;
	}

	public Nonogram getCurrentPattern() {
		return currentPattern;
	}

	public void setCurrentPattern(Nonogram currentPattern) {
		this.currentPattern = currentPattern;
	}

}
