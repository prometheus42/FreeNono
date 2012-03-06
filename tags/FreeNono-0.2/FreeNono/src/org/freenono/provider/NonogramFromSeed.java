/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2012 Christian Wichmann
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
package org.freenono.provider;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.freenono.interfaces.NonogramProvider;
import org.freenono.model.DifficultyLevel;
import org.freenono.model.Nonogram;
import org.freenono.model.RandomNonogram;
import org.freenono.model.RandomNonogram.RandomTypes;

public class NonogramFromSeed implements NonogramProvider {

	private Nonogram currentNonogram = null;
	private String seed = "";

	@Override
	public Nonogram fetchNonogram() {
		return generateNonogramBySeed();
	}

	@Override
	public String getId() {
		return "Seed -> " + seed;
	}

	@Override
	public String getName() {
		return "Seed -> " + seed;
	}

	@Override
	public String getDescription() {
		return "Seed -> " + seed;
	}

	@Override
	public DifficultyLevel getDifficulty() {
		return DifficultyLevel.undefined;
	}
	
	@Override
	public int width() {
		return fetchNonogram().width();
	}

	@Override
	public int height() {
		return fetchNonogram().height();
	}

	/**
	 * Generates a new Nonogram by calculating a hash from the given text. The
	 * width and height of the new nonogram is calculated by moduloing the
	 * hashed value and the seed value for the random number generator results
	 * from the first 64 bit of the hash.
	 * 
	 * @return Nonogram generated by seed to play
	 */
	private Nonogram generateNonogramBySeed() {

		// get the text input by the user...
		byte[] bytesOfMessage = null;

		try {
			bytesOfMessage = seed.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		// ...digest byte array to hash...
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		byte[] thedigest = md.digest(bytesOfMessage);

		BigInteger bigintdigest = new BigInteger(thedigest);

		// ...generate long from byte array to use...
		long seedValue = bigintdigest.longValue();
		int height = (bigintdigest.intValue() % 19) + 2;
		int width = (bigintdigest.intValue() % 19) + 2;

		// ..in the constructing of a new Nonogram!
		RandomNonogram randomNono = new RandomNonogram(seedValue);
		currentNonogram = randomNono.createRandomNonogram(height, width,
				RandomTypes.FULLRANDOM);
		return currentNonogram;
	}

	/**
	 * @param seed
	 *            the seed to set
	 */
	public Nonogram plantSeed(String seed) {
		this.seed = seed;
		return generateNonogramBySeed();
	}

	public String toString() {
		return "Seed -> " + seed;
	}
}
