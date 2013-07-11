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

/**
 * Provides all serializer to store data (settings, highscores and seed data)
 * in files or transmit them over network. Included are:  
 *  - nonogram serializer,
 *  - course serializer,
 *  - settings serializer (only for the FreeNono project),
 *  - seed serializer (only for CollectionFromSeed nonogram provider),
 *  - highscore serializer.
 * 
 * @author Markus Wichmann, Christian Wichmann
 */
package org.freenono.serializer;

