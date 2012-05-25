/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2010 Markus Wichmann, Christian Wichmann
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

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

public class Nonogram {

	public static final Comparator<Nonogram> NAME_ASCENDING_ORDER = new Comparator<Nonogram>() {

		@Override
		public int compare(Nonogram n1, Nonogram n2) {

			if (n1 == null && n2 == null) {
				return 0;
			} else if (n1 == null) {
				return -1;
			} else if (n2 == null) {
				return 1;
			} else {
				return n1.getName().compareTo(n2.getName());
			}

		}
	};

	public static final Comparator<Nonogram> NAME_DESCENDING_ORDER = new Comparator<Nonogram>() {

		@Override
		public int compare(Nonogram n1, Nonogram n2) {

			if (n1 == null && n2 == null) {
				return 0;
			} else if (n1 == null) {
				return -1;
			} else if (n2 == null) {
				return 1;
			} else {
				return n1.getName().compareTo(n2.getName());
			}

		}
	};

	public static final Comparator<Nonogram> HASH_ASCENDING_ORDER = new Comparator<Nonogram>() {

		@Override
		public int compare(Nonogram n1, Nonogram n2) {

			if (n1 == null && n2 == null) {
				return 0;
			} else if (n1 == null) {
				return -1;
			} else if (n2 == null) {
				return 1;
			} else {
				return n1.getHash().compareTo(n2.getHash());
			}

		}
	};

	public static final Comparator<Nonogram> HASH_DESCENDING_ORDER = new Comparator<Nonogram>() {

		@Override
		public int compare(Nonogram n1, Nonogram n2) {

			if (n1 == null && n2 == null) {
				return 0;
			} else if (n1 == null) {
				return -1;
			} else if (n2 == null) {
				return 1;
			} else {
				return n1.getHash().compareTo(n2.getHash());
			}

		}
	};
	
	public static final Comparator<Nonogram> LEVEL_ASCENDING_ORDER = new Comparator<Nonogram>() {

		@Override
		public int compare(Nonogram n1, Nonogram n2) {
			
			if (n1 == null && n2 == null) {
				
				return 0;
				
			} else if (n1 == null) {
				
				return -1;
				
			} else if (n2 == null) {
				
				return 1;
				
			} else {

				// if both parameters are not null, compare levels
				if (n1.getLevel() != 0 && n2.getLevel() != 0) {
					
					if (n1.getLevel() < n2.getLevel())
						return -1;
					else if (n1.getLevel() > n2.getLevel())
						return 1;
					else
						return 0;
				
				// compare reasonably if some levels are zero...
				} else if (n1.getLevel() == 0 && n2.getLevel() != 0) {
					
					return 1;
					
				} else if (n1.getLevel() != 0 && n2.getLevel() == 0) {
					
					return -1;
					
				// ...or just use the names
				} else {
					
					return n1.getName().compareTo(n2.getName());
				}
			}
		}
	};
	
	public static final Comparator<Nonogram> LEVEL_DESCENDING_ORDER = new Comparator<Nonogram>() {

		@Override
		public int compare(Nonogram n1, Nonogram n2) {
			
			if (n1 == null && n2 == null) {
				
				return 0;
				
			} else if (n1 == null) {
				
				return -1;
				
			} else if (n2 == null) {
				
				return 1;
				
			} else {

				// if both parameters are not null, compare levels
				if (n1.getLevel() != 0 && n2.getLevel() != 0) {
					
					if (n1.getLevel() < n2.getLevel())
						return 1;
					else if (n1.getLevel() > n2.getLevel())
						return -1;
					else
						return 0;
				
				// compare reasonably if some levels are zero...
				} else if (n1.getLevel() == 0 && n2.getLevel() != 0) {
					
					return 1;
					
				} else if (n1.getLevel() != 0 && n2.getLevel() == 0) {
					
					return -1;
					
				// ...or just use the names
				} else {
					
					return n1.getName().compareTo(n2.getName());
				}
			}
		}
	};


	private static Logger logger = Logger.getLogger(Nonogram.class);

	private String name;
	private String desc;
	private String author;
	private int level;
	private DifficultyLevel difficulty;
	private String hash = null;
	private long duration;

	private int width;
	private int height;

	private boolean[][] field;

	private List<int[]> lineNumbers;
	private List<int[]> columnNumbers;

	private URL originPath;

	public Nonogram() {

		setName("");
		setDescription("");
		setDifficulty(DifficultyLevel.undefined);
		setAuthor("");
		setLevel(0);

		setSize(0, 0);

		this.lineNumbers = new ArrayList<int[]>();
		this.columnNumbers = new ArrayList<int[]>();
	}

	public Nonogram(String name, DifficultyLevel difficulty, boolean[][] field)
			throws NullPointerException {

		if (name == null) {
			throw new NullPointerException("Parameter name is null");
		}

		if (field == null) {
			throw new NullPointerException("Parameter field is null");
		}

		setName(name);
		setDescription(desc);
		setDifficulty(difficulty);
		setAuthor("");
		setLevel(0);

		int height = field.length;
		int width = Integer.MAX_VALUE;
		for (int i = 0; i < field.length; i++) {
			if (field[i].length < width) {
				// TODO what if there are different array lengths?
				width = field[i].length;
			}
		}
		if (width == Integer.MAX_VALUE) {
			width = 0;
		}

		// create internal field array and copy received values
		setSize(width, height);
		for (int i = 0; i < height(); i++) {
			for (int j = 0; j < width(); j++) {
				this.field[i][j] = field[i][j];
			}
		}

		calculateCaptions();
	}

	@Override
	public String toString() {
		return getName();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return desc;
	}

	public void setDescription(String desc) {
		this.desc = desc;
	}

	public DifficultyLevel getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(DifficultyLevel difficulty) {
		this.difficulty = difficulty;
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	
	public URL getOriginPath() {
		return originPath;
	}

	public void setOriginPath(URL originPath) {
		this.originPath = originPath;
	}

	private String generateHash() {

		// add all information to string
		StringBuilder strb = new StringBuilder();
		strb.append(name);
		strb.append(author);
		strb.append(desc);
		strb.append(difficulty);
		strb.append(duration);
		strb.append(width);
		strb.append(height);
		for (int i = 0; i < height(); i++) {
			for (int j = 0; j < width(); j++) {
				strb.append(field[i][j]);
			}
		}

		// generate hash value
		MessageDigest md = null;
		String hashFunction = "MD5"; // TODO use SHA-1?
		try {
			md = MessageDigest.getInstance(hashFunction);
		} catch (NoSuchAlgorithmException e) {
			logger.warn("Hash " + hashFunction
					+ " not available on this system.");
		}
		// TODO add standard encoding for all information in nonogram class and
		// use it here ->
		byte[] thedigest = md.digest(strb.toString().getBytes());

		// return the string containing the hash value as hex numbers
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < thedigest.length; ++i) {
			sb.append(Integer.toHexString((thedigest[i] & 0xFF) | 0x100)
					.toLowerCase().substring(1, 3));
		}

		return sb.toString();
	}

	/**
	 * @return the hash
	 */
	public String getHash() {

		if (hash == null) {
			hash = generateHash();
		}
		return hash;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		this.field = new boolean[height][width];
	}

	public int getLineCaptionWidth() {

		int maxLineNumbers = 0;

		for (int i = 0; i < height(); i++) {
			maxLineNumbers = Math.max(maxLineNumbers, getLineNumberCount(i));
		}

		return maxLineNumbers;
	}

	public int getColumnCaptionHeight() {

		int maxColumnNumbers = 0;

		for (int i = 0; i < width(); i++) {
			maxColumnNumbers = Math.max(maxColumnNumbers,
					getColumnNumbersCount(i));
		}

		return maxColumnNumbers;
	}

	public boolean getFieldValue(int x, int y) {

		if (x < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (x >= width()) {
			throw new IndexOutOfBoundsException();
		}
		if (y < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (y >= height()) {
			throw new IndexOutOfBoundsException();
		}

		return this.field[y][x];
	}

	public void setFieldValue(boolean b, int x, int y) {

		if (x < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (x >= width()) {
			throw new IndexOutOfBoundsException();
		}
		if (y < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (y >= height()) {
			throw new IndexOutOfBoundsException();
		}

		this.field[y][x] = b;
	}

	public int[] getLineNumbers(int y) {

		if (y < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (y >= height()) {
			throw new IndexOutOfBoundsException();
		}

		return lineNumbers.get(y);
	}

	public int[] getColumnNumbers(int x) {

		if (x < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (x >= width()) {
			throw new IndexOutOfBoundsException();
		}

		return columnNumbers.get(x);
	}

	public int getLineNumberCount(int y) {

		if (y < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (y >= height()) {
			throw new IndexOutOfBoundsException();
		}

		return lineNumbers.get(y).length;
	}

	public int getColumnNumbersCount(int x) {

		if (x < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (x >= width()) {
			throw new IndexOutOfBoundsException();
		}

		return columnNumbers.get(x).length;
	}

	public int getLineNumber(int y, int index) {

		if (y < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (y >= height()) {
			throw new IndexOutOfBoundsException();
		}

		if (index < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (index >= getLineCaptionWidth()) {
			throw new IndexOutOfBoundsException();
		}

		int[] tmp = lineNumbers.get(y);
		if (index < tmp.length) {
			return tmp[index];
		} else {
			return -1;
		}

	}

	public int getColumnNumber(int x, int index)
			throws IndexOutOfBoundsException {

		if (x < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (x >= width()) {
			throw new IndexOutOfBoundsException();
		}

		if (index < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (index >= getColumnCaptionHeight()) {
			throw new IndexOutOfBoundsException();
		}

		int[] tmp = columnNumbers.get(x);
		if (index < tmp.length) {
			return tmp[index];
		} else {
			return -1;
		}

	}

	void calculateCaptions() {

		// calculate line numbers
		lineNumbers = new ArrayList<int[]>();
		for (int i = 0; i < height(); i++) {
			int[] tmp = calculateNumbers(i, true);
			lineNumbers.add(tmp);
		}

		// calculate column numbers
		columnNumbers = new ArrayList<int[]>();
		for (int i = 0; i < width(); i++) {
			int[] tmp = calculateNumbers(i, false);
			columnNumbers.add(tmp);
		}
	}

	private int[] calculateNumbers(int index, boolean horizontal) {
		List<Integer> list = new ArrayList<Integer>();

		int tmp = 0;
		for (int i = 0; i < (horizontal ? width() : height()); i++) {
			if (horizontal ? field[index][i] : field[i][index]) {
				tmp++;
			} else if (tmp > 0) {
				list.add(tmp);
				tmp = 0;
			}
		}

		// handle the last set of fields, if available
		if (tmp > 0) {
			list.add(tmp);
			tmp = 0;
		}

		// if there haven't been any occupied fields, so add at least a zero
		if (list.size() <= 0) {
			list.add(0);
		}

		int[] array = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}

		return array;
	}

}
