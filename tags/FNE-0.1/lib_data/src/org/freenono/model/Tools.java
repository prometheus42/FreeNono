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

import java.awt.FontMetrics;
import java.text.BreakIterator;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public final class Tools {

	public static final String NEW_LINE = System.getProperty("line.separator");

	public static final String FILE_SEPARATOR = System
			.getProperty("file.separator");

	/**
	 * Wraps text for labels by calculating the size and breaking the text with
	 * html elements according to the available size of its parent.
	 * (see http://fauzilhaqqi.net/2010/01/java-tutorial-wrap-text-into-jlabel/)
	 * 
	 * @param label
	 * @param text
	 */
	public static void wrapTextToLabel(JLabel label, String[] text) {
		// measure the length of font in pixel
		FontMetrics fm = label.getFontMetrics(label.getFont());
		// get container width, you must set the fixed width of
		// the container, i.e. JPanel
		int contWidth = label.getParent().getWidth();
		// to find the word separation
		BreakIterator boundary = BreakIterator.getWordInstance();
		// main string to be added
		StringBuffer m = new StringBuffer("<html>");
		// loop each index of array
		for (String str : text) {
			boundary.setText(str);
			// save each line
			StringBuffer line = new StringBuffer();
			// save each paragraph
			StringBuffer par = new StringBuffer();
			int start = boundary.first();
			// wrap loop
			for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary
					.next()) {
				String word = str.substring(start, end);
				line.append(word);
				// compare width with font metrics
				int trialWidth = SwingUtilities.computeStringWidth(fm,
						line.toString());
				// if bigger, add new line
				if (trialWidth > contWidth) {
					line = new StringBuffer(word);
					par.append("<br />");
				}
				// add new word to paragraphs
				par.append(word);
			}
			// add new line each paragraph
			par.append("<br />");
			// add paragraph into main string
			m.append(par);
		}
		// closed tag
		m.append("</html>");
		label.setText(m.toString());
	}

	/**
	 * Wrap a String into a label
	 * 
	 * @param label
	 * @param text
	 */
	public static void wrapTextToLabel(JLabel label, String text) {
		String[] newText = new String[] { text };
		wrapTextToLabel(label, newText);
	}

}
