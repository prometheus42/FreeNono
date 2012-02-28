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
package de.ichmann.markusw.java.apps.nonogram.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import de.ichmann.markusw.java.apps.nonogram.exception.InvalidArgumentException;
import de.ichmann.markusw.java.apps.nonogram.exception.InvalidFormatException;
import de.ichmann.markusw.java.apps.nonogram.serializer.nonogram.NonogramSerializer;
import de.ichmann.markusw.java.apps.nonogram.serializer.nonogram.XMLNonogramSerializer;
import de.ichmann.markusw.java.apps.nonogram.serializer.settings.SettingsSerializer;
import de.ichmann.markusw.java.apps.nonogram.serializer.settings.XMLSettingsSerializer;

public class Manager {

	private static Logger logger = Logger.getLogger(Manager.class);

	public static final String DEFAULT_NONOGRAM_PATH = "./nonogram";
	public static final String DEFAULT_SETTINGS_FILE = "./freenono.xml";

	private NonogramSerializer nonoSerializer = new XMLNonogramSerializer();
	private SettingsSerializer settingsSerializer = new XMLSettingsSerializer();
	private List<Nonogram> nonogramList = null;
	private Settings settings = null;
	private String settingsFile = null;
	private String nonogramPath = null;

	public Manager() throws InvalidArgumentException, FileNotFoundException,
			IOException {
		this(DEFAULT_NONOGRAM_PATH, DEFAULT_SETTINGS_FILE);
	}

	public Manager(String nonogramPath, String settingsFile)
			throws InvalidArgumentException, FileNotFoundException, IOException {

		if (nonogramPath == null) {
			throw new InvalidArgumentException("Parameter nonogramPath is null");
		}
		if (settingsFile == null) {
			throw new InvalidArgumentException("Parameter settingsFile is null");
		}

		this.nonogramPath = nonogramPath;
		this.settingsFile = settingsFile;
		loadNonograms(new File(nonogramPath));
		loadSettings(new File(settingsFile));
	}

	private void loadNonograms(File dir) throws FileNotFoundException {

		if (!dir.isDirectory()) {
			throw new FileNotFoundException("Parameter is no directory");
		}
		if (!dir.exists()) {
			throw new FileNotFoundException("Specified directory not found");
		}

		List<Nonogram> lst = new ArrayList<Nonogram>();
		List<File> files = getAllNonogramFiles(dir);

		for (File file : files) {
			try {
				Nonogram n = nonoSerializer.loadNonogram(file);
				lst.add(n);
			} catch (IOException e) {
				// TODO add log message here
			} catch (InvalidFormatException e) {
				// TODO add log message here
			}
		}

		this.nonogramList = lst;
	}

	private void loadSettings(File file) {

		try {
			settings = settingsSerializer.loadSettings(file);
		} catch (InvalidFormatException e) {
			logger.error("InvalidFormatException when loading settings file.");
			e.printStackTrace();
			// TODO check whether the old corrupt file should be deleted

		} catch (IOException e) {
			logger.error("IOException when loading settings file.");
			e.printStackTrace();
			// TODO check whether the old corrupt file should be deleted
		}

		if (settings == null) {
			settings = new Settings();
			logger.warn("Settings file not found. Using default settings!");
		}
	}

	private void saveSettings(File file) {

		try {
			settingsSerializer.saveSettings(this.settings, file);

		} catch (IOException e) {
			// TODO add log message here
			// TODO check whether the old corrupt file should be deleted
		}
	}

	public Settings getSettings() {
		return settings;
	}
	
	public Game createGame(Nonogram n) {
		Game g = new Game(n, settings.getMaxFailCount(), settings.getMaxTime());
		return g;
	}

	public void quitProgram() {
		saveSettings(new File(settingsFile));
	}
	
	public Collection<Nonogram> getNonogramList() {
		return Collections.unmodifiableCollection(nonogramList);
	}

	private List<File> getAllNonogramFiles(File dir) {

		List<File> lst = new ArrayList<File>();

		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				lst.addAll(getAllNonogramFiles(file));
			} else if (file.getName().endsWith(
					"." + XMLNonogramSerializer.DEFAULT_FILE_EXTENSION)) {
				lst.add(file);
			}
		}
		return lst;
	}
	
}
