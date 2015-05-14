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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Loads a specific file from a network connection and provides an easy access to all properties
 * defined in the given file. The file is only loaded once and all properties are stored forever or
 * as long as the VM is alive.
 *
 * @author Christian Wichmann
 */
public class PropertiesLoader {

    private static Logger logger = Logger.getLogger(PropertiesLoader.class);

    private static Map<String, Properties> listOfLoadedProperties = new HashMap<String, Properties>();
    private String propertyFileUrl = "";

    /**
     * Initializes this loader class for accessing properties from a file on the network. The
     * property file is only read from network if its URL is not already in the static list of
     * loaded properties.
     *
     * @param url
     *            URL of the file to be accessed
     */
    public PropertiesLoader(final String url) {

        propertyFileUrl = url;

        if (!listOfLoadedProperties.containsKey(propertyFileUrl)) {
            loadFileFromNetwork();
        }
    }

    /**
     * Loads file from network, parses it and stores all properties for later access.
     */
    private void loadFileFromNetwork() {

        InputStream is = null;
        try {
            // open file on network...
            final URL propertyFile = new URL(propertyFileUrl);
            is = propertyFile.openStream();

            // ...and load its properties.
            final Properties newProperties = new Properties();
            newProperties.load(is);

            listOfLoadedProperties.put(propertyFileUrl, newProperties);

        } catch (final MalformedURLException e) {
            logger.warn("Address of property file (" + propertyFileUrl + ") is not correct.");
        } catch (final IOException e) {
            logger.warn("Property file (" + propertyFileUrl + ") could not be read.");
        }
    }

    /**
     * Returns the value of a property in the defined network resource.
     *
     * @param property
     *            property for which to return the value
     * @return string representing the value for the given property, if the given property could not
     *         be found an empty string is returned
     */
    public final String getValueOfProperty(final String property) {

        final Properties properties = listOfLoadedProperties.get(propertyFileUrl);
        if (properties == null) {
            return new String();
        } else {
            return listOfLoadedProperties.get(propertyFileUrl).getProperty(property);
        }
    }
}
