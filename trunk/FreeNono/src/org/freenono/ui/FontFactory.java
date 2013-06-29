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
package org.freenono.ui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * Factory class for vending standard <code>Font</code> objects. Wherever
 * possible, this factory will hand out references to shared <code>Font</code>
 * instances.
 * <p>
 * Create methods return a reference to a shared <code>Font</code> object which
 * is ok because Font is immutable. The given object can then be adjusted by the
 * <code>deriveFont()</code> method.
 * 
 * @author Christian Wichmann
 */
public final class FontFactory {

    private static Logger logger = Logger.getLogger(FontFactory.class);

    private static final int FONT_LCD_SIZE = 28;
    private static final String FONT_LCD_FONT = "LCDMono2";
    private static final int FONT_LCD_STYLE = Font.PLAIN;
    private static final int FONT_TEXT_SIZE = 16;
    private static final String FONT_TEXT_FONT = "Ubuntu";
    private static final int FONT_TEXT_STYLE = Font.PLAIN;
    private static Font sharedLcdFont = new Font(FONT_LCD_FONT, FONT_LCD_STYLE,
            FONT_LCD_SIZE);
    private static Font sharedTextFont = new Font(FONT_TEXT_FONT,
            FONT_TEXT_STYLE, FONT_TEXT_SIZE);
    private static Font sharedDefaultFont = new Font(FONT_TEXT_FONT,
            FONT_TEXT_STYLE, FONT_TEXT_SIZE);

    /**
     * Don't let anyone instantiate this class. Private constructor initializing
     * all necessary fonts.
     */
    private FontFactory() {

        registerFonts();
    }

    /**
     * Register all fonts included in FreeNono to be used in the frames and
     * dialogs.
     */
    private void registerFonts() {

        // add new font
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, getClass()
                    .getResourceAsStream("/resources/fonts/LCDMono.TTF")); //$NON-NLS-1$
            // font = font.deriveFont(36);
            GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .registerFont(font);
        } catch (FontFormatException e) {

            logger.error("Unable to load font file because of a wrong font file format!");
        } catch (IOException e) {

            logger.error("Could not load font file from filesystem.");
        }
    }

    /**
     * Creates a lcd like font for displaying game information like game time or
     * fail count.
     * 
     * @return lcd like font
     */
    public static Font createLcdFont() {

        return sharedLcdFont;
    }

    /**
     * Creates a default font for all other purposes.
     * 
     * @return default font
     */
    public static Font createDefaultFont() {

        return sharedDefaultFont;
    }

    /**
     * Creates a font for displaying textual information, e.g. in dialog boxes
     * etc.
     * 
     * @return text font
     */
    public static Font createTextFont() {

        return sharedTextFont;
    }
}
