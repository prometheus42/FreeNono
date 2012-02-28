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
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.freenono.event.GameEventHelper;
import org.freenono.exception.InvalidArgumentException;
import org.freenono.exception.InvalidFormatException;
import org.freenono.serializer.NonogramFormatException;
import org.freenono.serializer.NonogramSerializer;
import org.freenono.serializer.SettingsFormatException;
import org.freenono.serializer.SettingsSerializer;
import org.freenono.serializer.XMLNonogramSerializer;
import org.freenono.serializer.XMLSettingsSerializer;


// TODO: replace nonogramList with nonogramHash!
public class Manager {

	private static Logger logger = Logger.getLogger(Manager.class);

	public static final String DEFAULT_NONOGRAM_PATH = "./nonogram";
	public static final String DEFAULT_SETTINGS_FILE = System
			.getProperty("user.home")
			+ Tools.FILE_SEPARATOR
			+ ".FreeNono"
			+ Tools.FILE_SEPARATOR
			+ "freenono.xml";

	private GameEventHelper eventHelper = null;
	
	private NonogramSerializer nonoSerializer = new XMLNonogramSerializer();
	private SettingsSerializer settingsSerializer = new XMLSettingsSerializer();
	private List<Nonogram> nonogramList = null;
	private Hashtable<String, List<Nonogram>> nonogramHash = null;
	private List<String> nonogramDirList = null;
	private Settings settings = null;
	private String settingsFile = null;
	private String nonogramPath = null;

	public Manager(GameEventHelper eventHelper) throws InvalidArgumentException, FileNotFoundException,
			IOException {
		this(eventHelper, DEFAULT_NONOGRAM_PATH, DEFAULT_SETTINGS_FILE);
	}

	public Manager(GameEventHelper eventHelper, String nonogramPath, String settingsFile)
			throws InvalidArgumentException, FileNotFoundException, IOException {

		this.eventHelper = eventHelper;
		
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

		Hashtable<String, List<Nonogram>> ht = new Hashtable<String, List<Nonogram>>();
		List<String> dirlst = new ArrayList<String>();

		for (File file : files) {
			try {
				Nonogram[] n = nonoSerializer.load(file);
				if (n != null) {
					for (int i = 0; i < n.length; i++) {
						lst.add(n[i]);
					}
				}

				// TODO: replace with better solution
				String[] pathtmp = file.getAbsolutePath().split(Tools.FILE_SEPARATOR);
				String coursename = pathtmp[pathtmp.length - 2];

				if (ht.get(coursename) == null) {
					ht.put(coursename, new ArrayList<Nonogram>());
					dirlst.add(coursename);
				}
				
				if (n != null) {
					for (int i = 0; i < n.length; i++) {
						ht.get(coursename).add(n[i]);
					}
				}

			} catch (IOException e) {
				// TODO add log message here
			} catch (NonogramFormatException e) {
				// TODO add log message here
			}
		}

		this.nonogramList = lst;
		this.nonogramHash = ht;
		this.nonogramDirList = dirlst;
	}

	private void loadSettings(File file) {

		try {
			settings = settingsSerializer.load(file);
		} catch (SettingsFormatException e) {
			logger.error("InvalidFormatException when loading settings file.");
			//e.printStackTrace();
			// TODO check whether the old corrupt file should be deleted

		} catch (IOException e) {
			logger.error("IOException when loading settings file.");
			//e.printStackTrace();
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
			// TODO add log message here
			// TODO check whether the old corrupt file should be deleted
		}
	}

	public Settings getSettings() {
		return settings;
	}

	public String getSettingsFile() {
		return settingsFile;
	}

	public void setSettingsFile(String settingsFile) {
		this.settingsFile = settingsFile;
	}

	public String getNonogramPath() {
		return nonogramPath;
	}

	public void setNonogramPath(String nonogramPath) {
		this.nonogramPath = nonogramPath;
	}

	public Game createGame(Nonogram n) {
		Game g = new Game(n, settings.getMaxFailCount(), settings.getMaxTime());
		return g;
	}

	public void quitProgram() {
		saveSettings(new File(settingsFile));
	}

	public void setEventHelper(GameEventHelper eventHelper) {
		this.eventHelper = eventHelper;
	}

	public Collection<Nonogram> getNonogramList() {
		return Collections.unmodifiableCollection(nonogramList);
	}

	/**
	 * Get list of nongrams in the dir/course with the given name
	 * 
	 * @param dir
	 *            selected course
	 * @return List<Nonogram>
	 */
	public Collection<Nonogram> getNonogramList(String dir) {
		return nonogramHash.get(dir);
	}

	public List<String> getNonogramDirList() {
		return nonogramDirList;
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
