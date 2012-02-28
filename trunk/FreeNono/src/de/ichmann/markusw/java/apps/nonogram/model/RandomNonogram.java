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
package de.ichmann.markusw.java.apps.nonogram.model;

import java.util.ArrayList;
import java.util.Random;

import de.ichmann.markusw.java.apps.nonogram.exception.ParameterException;

/**
 * To add new types of random nonograms, simply add it to the enum and 
 * the main which-statement
 */
public class RandomNonogram  {

	private int height = 5;
	private int width = 5;
	private Random rng = null;
	private int ranNonoCounter = 1;

	/*
	 * Add new types here
	 */
	public enum RandomTypes {
		RANDOM, HALFNHALF, FULLRANDOM
	}

	public RandomNonogram() {
		rng = new Random();
	}

		
	/**
	 * createRandomNonogram
	 * 
	 * @param height
	 *            Height of the to generated nonogram
	 * @param width
	 *            Width of the to generated nonogram
	 * @param type
	 *            Type of the random nonogram. Type 0 uses a random type.
	 * @return Nonogram, if one could be generated, else null.
	 */
	public Nonogram createRandomNonogram(int height, int width, RandomTypes type) {

		if (height <= 0) {
			this.height = 5;
		} else {
			this.height = height;
		}
		if (width <= 0) {
			width = 5;
		} else {
			this.width = width;
		}

		if (type == RandomTypes.RANDOM) {
			int tmp = RandomTypes.values().length;
			do {
				type = RandomTypes.values()[rng.nextInt(tmp)];
			} while (type == RandomTypes.RANDOM);
		}

		Nonogram ret = null;

		/*
		 * Add new types here
		 */
		switch (type) {
		case HALFNHALF:
			ret = halfnhalf();
			break;
		case FULLRANDOM:
			ret = fullRandomNono();
			break;
		default:
			ret = fullRandomNono();
			break;
		}

		return ret;
	}

	/**
	 * Generates random nonogram with one half marked
	 * 
	 * @return Nonogram, if generated, else null
	 */
	private Nonogram halfnhalf() {

		String id = "";
		String name = "random " + ranNonoCounter;
		String desc = "";
		int difficulty = 0;

		boolean field[][] = new boolean[height][width];

		int options = rng.nextInt(4);

		if (width == 1 || height == 1) {
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					field[j][i] = true;
				}
			}
		} else {
			switch (options) {
			case 0:
				for (int i = 0; i < Math.floor((width / 2)); i++) {
					for (int j = 0; j < height; j++) {
						field[j][i] = true;
					}
				}
				break;
			case 1:
				for (int i = (int) Math.floor((width / 2)); i < width; i++) {
					for (int j = 0; j < height; j++) {
						field[j][i] = true;
					}
				}
				break;
			case 2:
				for (int i = 0; i < width; i++) {
					for (int j = 0; j < (int) Math.floor((height / 2)); j++) {
						field[j][i] = true;
					}
				}
				break;
			case 3:
				for (int i = 0; i < width; i++) {
					for (int j = (int) Math.floor((height / 2)); j < height; j++) {
						field[j][i] = true;
					}
				}
				break;
			}

		}

		Nonogram ret = null;
		try {
			ret = new Nonogram(id, name, desc, difficulty, field);
		} catch (ParameterException e) {
			// e.printStackTrace(); // should not occur, since we use it correct
			// ;-)
		}

		return ret;
	}


	private Nonogram fullRandomNono() {
		String id = "";
		String name = "random " + ranNonoCounter;
		String desc = "";
		int difficulty = 0;

		boolean field[][] = new boolean[height][width];

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				field[j][i] = (rng.nextInt(2) == 0) ? true : false;
			}
		}

		Nonogram ret = null;
		try {
			ret = new Nonogram(id, name, desc, difficulty, field);
		} catch (ParameterException e) {
			// e.printStackTrace(); // should not occur, since we use it correct
			// ;-)
		}

		return ret;
	}
}
