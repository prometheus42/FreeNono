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
package org.freenono.ui.common;

import java.awt.Font;
import java.awt.FontMetrics;
import java.text.BreakIterator;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * Utility class providing tools for convenience.
 *
 * @author Christian Wichmann
 */
public final class Tools {

    public static final String NEW_LINE = System.getProperty("line.separator");

    public static final String FILE_SEPARATOR = System.getProperty("file.separator");

    /**
     * Private constructor should not be called.
     */
    private Tools() {
    }

    /**
     * Checks whether program runs under the normal VM or was started via Java Web Start.
     *
     * @return true, if program is running under Java Web Start and not under the normal VM.
     */
    public static boolean isRunningJavaWebStart() {

        boolean hasJNLP = false;

        try {
            Class.forName("javax.jnlp.ServiceManager");
            hasJNLP = true;
        } catch (final ClassNotFoundException ex) {
            hasJNLP = false;
        }
        return hasJNLP;
    }

    /**
     * Wraps text for labels by calculating the size and breaking the text with html elements
     * according to the available size of its parent. (see
     * http://fauzilhaqqi.net/2010/01/java-tutorial-wrap-text-into-jlabel/)
     * @param label
     *            label where wrapped text should be shown
     * @param text
     *            text to wrap
     */
    public static void wrapTextToLabel(final JLabel label, final String[] text) {

        // measure the length of font in pixel
        final FontMetrics fm = label.getFontMetrics(label.getFont());
        // get container width, you must set the fixed width of
        // the container, i.e. JPanel
        final int contWidth = label.getParent().getWidth();
        // to find the word separation
        final BreakIterator boundary = BreakIterator.getWordInstance();
        // main string to be added
        final StringBuffer m = new StringBuffer("<html>");
        // loop each index of array
        for (final String str : text) {
            boundary.setText(str);
            // save each line
            StringBuffer line = new StringBuffer();
            // save each paragraph
            final StringBuffer par = new StringBuffer();
            int start = boundary.first();
            // wrap loop
            for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) {
                final String word = str.substring(start, end);
                line.append(word);
                // compare width with font metrics
                final int trialWidth = SwingUtilities.computeStringWidth(fm, line.toString());
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
     * Wrap a String into a label.
     * @param label
     *            label where wrapped text should be shown
     * @param text
     *            text to wrap
     */
    public static void wrapTextToLabel(final JLabel label, final String text) {

        final String[] newText = new String[] {text};
        wrapTextToLabel(label, newText);
    }

    /**
     * Deletes all not displayable characters in a given font from a string and strips all leading
     * and trailing whitespaces.
     *
     * @param string
     *            string to be checked and stripped
     * @param font
     *            font for which to check if characters can be displayed
     * @return checked and stripped string
     */
    public static String stripNotPrintableChars(final String string, final Font font) {

        final StringBuilder sb = new StringBuilder(string);

        for (int i = 0; i < sb.length(); i++) {
            if (!font.canDisplay(sb.codePointAt(i))) {
                sb.deleteCharAt(i);
            }
        }

        return sb.toString().trim();
    }
}
