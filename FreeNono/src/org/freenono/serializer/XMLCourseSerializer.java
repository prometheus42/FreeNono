/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2011 Markus Wichmann
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.freenono.exception.InvalidFormatException;
import org.freenono.exception.ParameterException;
import org.freenono.model.Course;
import org.freenono.model.Nonogram;
import org.freenono.model.Tools;


/**
 * @author Markus Wichmann
 * 
 */
public class XMLCourseSerializer implements CourseSerializer {

	private static Logger logger = Logger.getLogger(XMLCourseSerializer.class);

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
		if (!f.isDirectory()) {
			// unable to use a file to load a course
			throw new IOException("unable to use a file to load a course");
		}
		if (!f.exists()) {
			// unable to use a none existent directory 
			throw new FileNotFoundException("specified directory doesn't exist");
		}

		Course c;
		String name;
		List<Nonogram> nonograms = new ArrayList<Nonogram>();

		name = f.getName();

		for (File file : f.listFiles()) {
			if (file.isDirectory()) {
				// directories will be spared
				continue;
			}
			
			Nonogram[] n = null;
			if (file.getName().endsWith("." + XMLNonogramSerializer.DEFAULT_FILE_EXTENSION)) {
				// load nonograms with the xml serializer
				n = xmlNonogramSerializer.load(file);
			}
			else if (file.getName().endsWith("." + SimpleNonogramSerializer.DEFAULT_FILE_EXTENSION)) {
				// load nonograms with the simple serializer
				n = simpleNonogramSerializer.load(file);
			}

			if (n != null) {
				for (int i = 0; i < n.length; i++) {
					nonograms.add(n[i]);
				}
			}
			
		}

		c = new Course(name, nonograms.toArray(new Nonogram[0]));

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

		if (f.exists()) {
			// at least trigger a log message, if the file already exists
			logger.warn("specified output directory already exists, some files may be overwritten");
		}

		File courseDir = new File(f, c.getName() + Tools.FILE_SEPARATOR);

		if (!courseDir.mkdirs()) {
			throw new IOException("Unable to create directories");
		}

		for (Nonogram n : c.getNonograms()) {
			File nonogramFile = new File(courseDir, n.getName());
			xmlNonogramSerializer.save(nonogramFile, n);
		}
	}
	
}
