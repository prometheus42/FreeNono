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
package org.freenono.serializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.freenono.model.Course;
import org.freenono.model.Nonogram;


/**
 * @author Markus Wichmann
 * 
 */
public class ZipCourseSerializer implements CourseSerializer {

	public static final String DEFAULT_FILE_EXTENSION = "nonopack";

	private static Logger logger = Logger.getLogger(ZipCourseSerializer.class);

	private XMLNonogramSerializer xmlNonogramSerializer = new XMLNonogramSerializer();

	private SimpleNonogramSerializer simpleNonogramSerializer = new SimpleNonogramSerializer();

	
	
	/* load methods */

	@Override
	public Course load(File f) throws NullPointerException, IOException,
			CourseFormatException, NonogramFormatException {

		// do some parameter checks
		if (f == null) {
			// unable to use a file that is null ;-)
			throw new NullPointerException("File parameter is null");
		}
		if (f.isDirectory()) {
			// unable to use a directory to load a course
			throw new IOException("unable to use a directory to load a course");
		}
		if (!f.exists()) {
			// unable to use a none existent directory
			throw new FileNotFoundException("specified directory doesn't exist");
		}

		Course c;
		ZipFile zip = new ZipFile(f);
		try {

			String name;
			List<Nonogram> nonograms = new ArrayList<Nonogram>();

			zip = new ZipFile(f);
			name = f.getName();
			int index = name.lastIndexOf(".");
			if (index >= 0) {
				name = name.substring(0, index);
			}
			
			int entryCount = 0;
			for (Enumeration<? extends ZipEntry> list = zip.entries(); list.hasMoreElements();) {

				entryCount++;
				
				Nonogram[] n = null;
				ZipEntry entry = list.nextElement();
				InputStream is = zip.getInputStream(entry);

				if (entry.getName().endsWith(
						"." + XMLNonogramSerializer.DEFAULT_FILE_EXTENSION)) {
					// load nonograms with the xml serializer
					n = xmlNonogramSerializer.load(is);
				} else if (entry.getName().endsWith(
						"." + SimpleNonogramSerializer.DEFAULT_FILE_EXTENSION)) {
					// load nonograms with the simple serializer
					n = simpleNonogramSerializer.load(is);
				}

				if (n != null) {
					for (int i = 0; i < n.length; i++) {
						nonograms.add(n[i]);
					}
				}
			}
			
			if (entryCount == 0) {
				throw new CourseFormatException("specified zip file is empty");
			}
			
			Collections.sort(nonograms, Nonogram.NAME_ORDER);
			c = new Course(name, nonograms.toArray(new Nonogram[0]));
		} finally {
			try {
				zip.close();
			} catch (Exception e) {
				logger.warn("Unable to close ZipFile");
			}
		}

		return c;
	}

	
	
	/* save methods */
	
	@Override
	public void save(File f, Course c) throws NullPointerException, IOException {

		// do some parameter checks
		if (f == null) {
			// unable to use a file that is null ;-)
			throw new NullPointerException("File parameter is null");
		}
		if (!f.isDirectory()) {
			// unable to use a file to save a course
			throw new IOException("unable to use a file to save a course");
		}
		if (c == null) {
			// there is no course to save
			throw new NullPointerException("Course parameter is null");
		}

		File courseFile = new File(f, c.getName());

		if (!courseFile.mkdirs()) {
			throw new IOException("Unable to create directories");
		}

		if (courseFile.exists()) {
			// at least trigger a log message, if the file already exists
			logger.warn("specified output directory already exists, some files may be overwritten");
		}

		ZipOutputStream zos = null;
		try {

			zos = new ZipOutputStream(new FileOutputStream(courseFile, false));
			zos.setLevel(0); // store only
			// zos.setLevel(9); // max compression

			for (Nonogram n : c.getNonograms()) {

				zos.putNextEntry(new ZipEntry(n.getName()));
				xmlNonogramSerializer.save(zos, n);
				zos.closeEntry();
			}
		} finally {
			try {
				zos.closeEntry();
				zos.close();
			} catch (Exception e) {
				logger.warn("Unable to close ZipFile");
			}
		}

	}

}
