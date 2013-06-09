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
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.freenono.model.Course;
import org.freenono.model.Nonogram;


/**
 * Serializes a whole course of nonograms into or from a zip file. A course can
 * be loaded either by File object or through a InputStream.
 * 
 * @author Markus Wichmann, Christian Wichmann
 * 
 */
public class ZipCourseSerializer implements CourseSerializer {

	public static final String DEFAULT_FILE_EXTENSION = "nonopack";

	private static Logger logger = Logger.getLogger(ZipCourseSerializer.class);

	private XMLNonogramSerializer xmlNonogramSerializer = new XMLNonogramSerializer();

	private SimpleNonogramSerializer simpleNonogramSerializer = new SimpleNonogramSerializer();

	
	/**
	 * Extension of ZipInputStream to prevent xml classes used in
	 * XMLNonogramSerializer from closing the stream. The stream includes all
	 * files in a given jar file which is read by CollectionFromJar.
	 * 
	 * The alternative would be: Get resource from jar file by URL and use
	 * ZipFile in CollectionFromJar to retrieve separate input streams
	 * (ZipFile.getInputStream()).
	 * 
	 * Source: http://stackoverflow.com/questions/12975532/how-to-prevent-zipinputstream-from-being-closed-by-after-a-xslt-transform
	 * 
	 * @author Christian Wichmann
	 * 
	 */
	private class ZipInputStreamSuper extends ZipInputStream {

	    public ZipInputStreamSuper(InputStream in) {
	    	
	        super(in);
	    }

	    @Override
	    public void close() throws IOException {
	    	
	    	// do NOTHING!
	    }

	    @SuppressWarnings("unused")
		public void fuckingClose() throws IOException {
	    	
	        super.close();
	    }
	}			
	
	
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
		String name;
		ZipFile zip = new ZipFile(f);
		List<Nonogram> nonograms = new ArrayList<Nonogram>();

		try {

			zip = new ZipFile(f);
			name = f.getName();
			
			int index = name.lastIndexOf(".");
			if (index >= 0) {
				name = name.substring(0, index);
			}
			
			for (Enumeration<? extends ZipEntry> list = zip.entries();
					list.hasMoreElements(); ) {
				
				ZipEntry entry = list.nextElement();
				InputStream is = zip.getInputStream(entry);
				
				nonograms.addAll(loadFileFromZIP(entry, is));
			}
			
			if (nonograms.isEmpty()) {
				
				throw new CourseFormatException("Specified zip file contains no nonograms.");
			}
			
			Collections.sort(nonograms, Nonogram.NAME_ASCENDING_ORDER);
			c = new Course(name, nonograms);
			
		} finally {
			
			try {
				zip.close();
				
			} catch (Exception e) {
				
				logger.warn("Unable to close ZipFile");
			}
		}

		return c;
	}
	
	public Course load(InputStream is, String courseName) 
			throws NullPointerException, IOException, NonogramFormatException {
		
		if (is == null) {

			throw new NullPointerException("Input stream is null.");
		}

		Course c;
		List<Nonogram> nonograms = new ArrayList<Nonogram>();
		ZipInputStreamSuper zis = new ZipInputStreamSuper(is);
        ZipEntry entry;

        try {
        
            // while there are entries to process...
			while ((entry = zis.getNextEntry()) != null)
			{
				nonograms.addAll(loadFileFromZIP(entry, zis));
			}
			
		} finally {
			
		}
		
		Collections.sort(nonograms, Nonogram.NAME_ASCENDING_ORDER);
		c = new Course(courseName, nonograms);
		
		return c;
	}
	
	private List<Nonogram> loadFileFromZIP(ZipEntry entry,
			InputStream is) throws IOException, NonogramFormatException {
		
		List<Nonogram> nonograms = new ArrayList<Nonogram>();
		Nonogram[] n = null;

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
		
		return nonograms;
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
			throw new IOException("Unable to use a file to save a course. Please give a directory to save the course in!");
		}
		if (c == null) {
			// there is no course to save
			throw new NullPointerException("Course parameter is null");
		}

		// if (!courseFile.mkdirs()) {
		// throw new IOException("Unable to create directories");
		// }
		
		File courseFile = new File(f, c.getName()+".nonopack");

		if (courseFile.exists()) {
			// at least trigger a log message, if the file already exists
			logger.warn("specified output file already exists, it may be overwritten");
		}

		ZipOutputStream zos = null;
		try {

			zos = new ZipOutputStream(new FileOutputStream(courseFile, false));
			zos.setLevel(0); // store only
			// zos.setLevel(9); // max compression

			for (Nonogram n : c.getNonograms()) {

				zos.putNextEntry(new ZipEntry(n.getName()+".nonogram"));
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
