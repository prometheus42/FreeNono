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
package de.ichmann.markusw.java.apps.freenono;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.ichmann.markusw.java.apps.freenono.model.Nonogram;
import de.ichmann.markusw.java.apps.freenono.serializer.nonogram.NonogramSerializer;
import de.ichmann.markusw.java.apps.freenono.serializer.nonogram.SimpleNonogramSerializer;
import de.ichmann.markusw.java.apps.freenono.serializer.nonogram.XMLNonogramSerializer;

public class ConvertNonogram {

	public static void main(String[] args) {
		if (args.length <= 0) {
			System.out.println("Please specify at least one nonogram file");
			return;
		}

		for (int i = 0; i < args.length; i++) {
			File f = new File(args[i]);
			if (f.isFile()) {
				convertNonogram(f);
			}
			else {
				List<File> files = getAllNonogramFiles(f);
				for (File file : files) {
					convertNonogram(file);
				}
			}
		}
	}

	private static List<File> getAllNonogramFiles(File dir) {

		List<File> lst = new ArrayList<File>();

		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				lst.addAll(getAllNonogramFiles(file));
			} else if (file.getName().endsWith(
					"." + SimpleNonogramSerializer.DEFAULT_FILE_EXTENSION)) {
				lst.add(file);
			}
		}
		return lst;
	}

	private static void convertNonogram(File input) {
		try {
			NonogramSerializer xmlNS = new XMLNonogramSerializer();
			NonogramSerializer simpleNS = new SimpleNonogramSerializer();
			
			File output = new File(input.getParentFile().getAbsolutePath(), "_" + input.getName());
			Nonogram n = simpleNS.loadNonogram(input);
			xmlNS.saveNonogram(output, n);

		} catch (Exception e) {
			System.out.println(input + ": " + e.getMessage());
		}
	}

}
