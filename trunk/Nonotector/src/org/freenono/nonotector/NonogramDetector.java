/*****************************************************************************
 * Nonotector - Detector to import nonograms from scanned images
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
package org.freenono.nonotector;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;


public class NonogramDetector {

	static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

	public static void main(String[] args) {

		NonogramDetector nd = new NonogramDetector();
		
		nd.testOpenCV();
	}

	private void testOpenCV() {

		// Load image from file
		Mat image = Highgui.imread("/home/christian/Desktop/Nonotector/Nonogramme1b.png");
		
		// Save the resulting image
	    String filename = "/home/christian/Desktop/Nonotector/result.png";
	    Highgui.imwrite(filename, image);
	}
}
