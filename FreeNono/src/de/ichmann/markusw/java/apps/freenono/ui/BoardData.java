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
package de.ichmann.markusw.java.apps.freenono.ui;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import de.ichmann.markusw.java.apps.freenono.model.Game;
import de.ichmann.markusw.java.apps.freenono.model.Nonogram;
import de.ichmann.markusw.java.apps.freenono.model.Token;

class BoardData {

	private static Logger logger = Logger.getLogger("de.ichmann.markusw.java.apps.nonogram");
	
	private Game game;

	private int topBorder = 5;
	private int bottomBorder = 5;
	private int leftBorder = 5;
	private int rightBorder = 5;
	private int horizontalPadding = 10;
	private int verticalPadding = 10;
	
	private int fieldWidth = 20;
	private int fieldHeight = 20;
	
	private double maxPreviewFactor = 2;
	
	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	
	
	public int getTopBorder() {
		return topBorder;
	}

	public void setTopBorder(int topBorder) {
		this.topBorder = topBorder;
	}

	
	
	public int getBottomBorder() {
		return bottomBorder;
	}

	public void setBottomBorder(int bottomBorder) {
		this.bottomBorder = bottomBorder;
	}

	
	
	public int getLeftBorder() {
		return leftBorder;
	}

	public void setLeftBorder(int leftBorder) {
		this.leftBorder = leftBorder;
	}

	
	
	public int getRightBorder() {
		return rightBorder;
	}

	public void setRightBorder(int rightBorder) {
		this.rightBorder = rightBorder;
	}

	
	
	public int getHorizontalPadding() {
		return horizontalPadding;
	}

	public void setHorizontalPadding(int horizontalPadding) {
		this.horizontalPadding = horizontalPadding;
	}

	
	
	public int getVerticalPadding() {
		return verticalPadding;
	}

	public void setVerticalPadding(int verticalPadding) {
		this.verticalPadding = verticalPadding;
	}

	
	
	public int getFieldWidth() {
		return fieldWidth;
	}

	public void setFieldWidth(int fieldWidth) {
		this.fieldWidth = fieldWidth;
	}

	
	
	public int getFieldHeight() {
		return fieldHeight;
	}

	public void setFieldHeight(int fieldHeight) {
		this.fieldHeight = fieldHeight;
	}
	
	
	
	public double getMaxPreviewFactor() {
		return maxPreviewFactor;
	}

	public void setMaxPreviewFactor(double maxPreviewFactor) {
		this.maxPreviewFactor = maxPreviewFactor;
	}
	
	
	
	/* refresh */
	
	int rowCount = 0;
	int columnCount = 0;
	int columnCaptionCount = 0;
	int rowCaptionCount = 0;
	
	public void refresh(boolean gameChanged) {

		clearFields(gameChanged);

		if (game != null) {
			
			calculateBaseValues();
			calculateRowNumberArea(gameChanged);
			calculateColumnNumberArea(gameChanged);
			calculateBoardArea();
			calculatePreviewArea();
			createImage();
			calculateTime();
			calculateFailCountLeft();
		}
	}
	
	private void clearFields(boolean gameChanged) {
		
		/* base values */
		if (gameChanged) {
			rowCount = 0;
			columnCount = 0;
			columnCaptionCount = 0;
			rowCaptionCount = 0;
		}

		/* column number area */
		columnNumberArea = null;
		columnNumberXCoords = null;
		columnNumberYCoords = null;
		if (gameChanged) {
			columnNumbers = null;
		}

		/* row number area */
		rowNumberArea = null;
		rowNumberXCoords = null;
		rowNumberYCoords = null;
		if (gameChanged) {
			rowNumbers = null;
		}

		/* board area */
		boardArea = null;
		boardXCoords = null;
		boardYCoords = null;
		boardValues = null;

		/* preview image */
		previewImage = null;
		previewArea = null;
	}

	private void calculateBaseValues() {
		
		Nonogram n = game.getPattern();

		rowCount = game.height();
		columnCount = game.width();
		columnCaptionCount = n.getColumnCaptionHeight();
		rowCaptionCount = n.getLineCaptionWidth();
	}
	
	
	/* column number area */

	Rectangle columnNumberArea = null;
	int[] columnNumberXCoords = null;
	int[] columnNumberYCoords = null;
	String[][] columnNumbers = null;
	
	private void calculateColumnNumberArea(boolean gameChanged) {

		Nonogram n = game.getPattern();
		
		int columnCaptionHeight = (int) (fieldHeight * columnCaptionCount);
		int rowCaptionWidth = (int) (fieldWidth * rowCaptionCount);

		
		// calculate area
		columnNumberArea = new Rectangle(
				leftBorder + rowCaptionWidth + horizontalPadding,
				topBorder,
				fieldWidth * columnCount,
				columnCaptionHeight);

		
		// initialize coordinate arrays
		columnNumberXCoords = new int[columnCount+1];
		columnNumberYCoords = new int[columnCaptionCount +1];
		
		// fill x coordinates
		double tmpX = columnNumberArea.x;
		for (int x = 0; x <= columnCount; x++) {
			columnNumberXCoords[x] = (int)tmpX;
			tmpX += this.fieldWidth;
		}
		
		// fill y coordinates
		double tmpY = columnNumberArea.y;
		for (int y = 0; y <= columnCaptionCount; y++) {
			columnNumberYCoords[y] = (int)tmpY;
			tmpY += this.fieldHeight;
		}
		
		if (gameChanged) {
			// initialize column numbers
			columnNumbers = new String[columnCount][columnCaptionCount];
			for (int x = 0; x < columnCount; x++) {
				int len = n.getColumnNumbersCount(x);
				for (int i = 0; i < columnCaptionCount; i++) {
					int number = n.getColumnNumber(x, i);
					
					columnNumbers[x][(i + columnCaptionCount - len) % columnCaptionCount] = number >= 0 ? Integer
							.toString(number)
							: "";
					
					//columnNumbers[x][columnCaptionCount - i - 1]
				}
			}
		}
	}
	
	
	/* row number area */

	Rectangle rowNumberArea = null;
	int[] rowNumberXCoords = null;
	int[] rowNumberYCoords = null;
	String[][] rowNumbers = null;
	
	private void calculateRowNumberArea(boolean gameChanged) {

		Nonogram n = game.getPattern();

		int columnCaptionHeight = (int) (fieldHeight * columnCaptionCount);
		int rowCaptionWidth = (int) (fieldWidth * rowCaptionCount);
		
		// calculate area
		rowNumberArea = new Rectangle(
				leftBorder,
				topBorder + columnCaptionHeight + verticalPadding,
				rowCaptionWidth,
				fieldHeight * rowCount);

		
		// initialize coordinate arrays
		rowNumberXCoords = new int[rowCaptionCount+1];
		rowNumberYCoords = new int[rowCount +1];
		
		// fill x coordinates
		double tmpX = rowNumberArea.x;
		for (int x = 0; x <= rowCaptionCount; x++) {
			rowNumberXCoords[x] = (int)tmpX;
			tmpX += this.fieldWidth;
		}
		
		// fill y coordinates
		double tmpY = rowNumberArea.y;
		for (int y = 0; y <= rowCount; y++) {
			rowNumberYCoords[y] = (int)tmpY;
			tmpY += this.fieldHeight;
		}
		
		if (gameChanged) {
			// initialize row numbers
			rowNumbers = new String[rowCount][rowCaptionCount];
			for (int y = 0; y < rowCount; y++) {
				int len = n.getLineNumberCount(y);
				for (int i = 0; i < rowCaptionCount; i++) {
					int number = n.getLineNumber(y, i);
					
					rowNumbers[y][(i + rowCaptionCount - len) % rowCaptionCount]  = number >= 0 ? Integer
							.toString(number)
							: "";
					
					//rowNumbers[y][rowCaptionCount - i - 1]
				}
			}
		}
	}


	
	/* board area */
	
	Rectangle boardArea = null;
	int[] boardXCoords = null;
	int[] boardYCoords = null;
	Token[][] boardValues = null;
	
	private void calculateBoardArea() {
		
		// calculate area
		boardArea = new Rectangle(
				columnNumberArea.x,
				rowNumberArea.y,
				columnNumberArea.width,
				rowNumberArea.height);

		
		// initialize coordinate arrays
		boardXCoords = new int[columnCount+1];
		boardYCoords = new int[rowCount+1];

		// fill x coordinates
		double tmpX = boardArea.x;
		for (int x = 0; x <= columnCount; x++) {
			boardXCoords[x] = (int)tmpX;
			tmpX += this.fieldWidth;
		}
		
		// fill y coordinates
		double tmpY = boardArea.y;
		for (int y = 0; y <= rowCount; y++) {
			boardYCoords[y] = (int)tmpY;
			tmpY += this.fieldHeight;
		}
		
		
		// initialize row numbers
		boardValues = new Token[columnCount][rowCount];
		for (int y = 0; y < rowCount; y++) {
			for (int x = 0; x < columnCount; x++) {
				boardValues[x][y] = game.getFieldValue(x, y);
			}
		}
	}
	
	
	/* preview image */
	
	Image previewImage = null;
	Rectangle previewArea = null;

	private void calculatePreviewArea() {
		
		// create Rectangle for the complete area
		Rectangle area = new Rectangle(
				leftBorder, topBorder,
				rowNumberArea.width, columnNumberArea.height);

		// calculate the maximum possible factor from horizontal, vertical and
		// maxPreview factors
		double factor = Math.min(
				maxPreviewFactor,
				Math.min(
						(area.width / (double)columnCount),
						(area.height / (double)rowCount)));
		
		// calculate actual size and position for the preview
		int width = (int) (columnCount * factor);
		int height = (int) (rowCount * factor);
		int x = area.x + area.width - width;
		int y = area.y + area.height - height;
		
		previewArea = new Rectangle(x,y, width, height);
	}
	
	private void createImage() {

		byte pixelsAsByte[] = new byte[columnCount * rowCount];

		for (int y = 0; y < rowCount; y++) {
			for (int x = 0; x < columnCount; x++) {

				pixelsAsByte[(y * columnCount) + x] = (byte) (game
						.getFieldValue(x, y) == Token.OCCUPIED ? 0 : 255);
			}
		}

		BufferedImage image = new BufferedImage(columnCount, rowCount,
				BufferedImage.TYPE_BYTE_GRAY);

		WritableRaster raster = image.getRaster();
		raster.setDataElements(0, 0, columnCount, rowCount, pixelsAsByte);

		previewImage = image;
	}

	/* time */
	
	private final SimpleDateFormat timeFormatter = new SimpleDateFormat("mm:ss");
	
	Rectangle timeArea = null;
	String timeLeft = "00:00";
	
	void calculateTime() {
	
		timeArea = new Rectangle(50, 50, 200, 50);
		
		if (game.usesMaxTime()) {
			timeLeft = timeFormatter.format(game.getTimeLeft());
		}
		else {
			timeLeft = timeFormatter.format(game.getElapsedTime());
		}
	}
	
	
	/* fail count */
	
	Rectangle failCountArea = null;
	String failCountLeft = "0";
	
	private void calculateFailCountLeft() {

		failCountArea = new Rectangle(50, 80, 200, 50);

		if (game.usesMaxFailCount()) {
			failCountLeft = Integer.toString(game.getFailCountLeft());
		} else {
			failCountLeft = "";
		}
	}
	
	
}
