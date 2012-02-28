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
package de.ichmann.markusw.java.apps.freenono.serializer.nonogram;

import java.io.File;
import java.io.IOException;

import de.ichmann.markusw.java.apps.freenono.exception.InvalidFormatException;
import de.ichmann.markusw.java.apps.freenono.exception.ParameterException;
import de.ichmann.markusw.java.apps.freenono.model.Course;
import de.ichmann.markusw.java.apps.freenono.model.Nonogram;

public interface NonogramSerializer {

	Course loadNonogramCource(File f) throws NullPointerException, InvalidFormatException, IOException;

	void saveNonogramCourse(File f, Course c) throws IOException, ParameterException;
	
	Nonogram loadNonogram(File f) throws InvalidFormatException, IOException;

	void saveNonogram(File f, Nonogram n) throws IOException, ParameterException;
}
