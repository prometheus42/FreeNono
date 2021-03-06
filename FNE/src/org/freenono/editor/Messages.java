/*****************************************************************************
 * FreeNonoEditor - A editor for nonogram riddles
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
package org.freenono.editor;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Provides localized messages from a resource bundle for the current locale.
 * 
 * @author Christian Wichmann
 */
public final class Messages {

    private static final String BUNDLE_NAME = "resources.i18n.FNE";

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle(BUNDLE_NAME);

    /**
     * Private constructor for static tool class.
     */
    private Messages() {

    }

    /**
     * Get a localized message for a given key depending on default locale.
     * 
     * @param key
     *            Given key for which to get string.
     * @return String with message.
     */
    public static String getString(final String key) {

        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
