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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.freenono.exception.ParameterException;


public class Nonogram {
	
	private static Logger logger = Logger.getLogger(Nonogram.class);

	private String id;
	private String name;
	private String desc;
	private int difficulty;

	private int width;
	private int height;

	private boolean[][] field;

	private List<int[]> lineNumbers;
	private List<int[]> columnNumbers;

	public Nonogram() {

		setId("");
		setName("");
		setDescription("");
		setDifficulty(0);
		
		setSize(0, 0);

		this.lineNumbers = new ArrayList<int[]>();
		this.columnNumbers = new ArrayList<int[]>();
	}

	public Nonogram(String id,
			 String name,
			 String desc,
			 int difficulty,
			 boolean[][] field) throws ParameterException {

		if (name == null) {
			throw new ParameterException("Parameter name is null");
		}

		if (field == null) {
			throw new ParameterException("Parameter field is null");
		}

		setId(id);
		setName(name);
		setDescription(desc);
		setDifficulty(difficulty);
		
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
	
	public String getId() {
		return id;
	}

	void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return desc;
	}

	void setDescription(String desc) {
		this.desc = desc;
	}
	
	public int getDifficulty() {
		return difficulty;
	}

	void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		this.field = new boolean[height][width];
	}

	public int getLineCaptionWidth() {
		return (width() + 1) / 2;
	}

	public int getColumnCaptionHeight() {
		return (height() + 1) / 2;
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
	
	void setFieldValue(boolean b, int x, int y) {

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
