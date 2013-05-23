/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2013 Christian Wichmann
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

import org.apache.log4j.Logger;
import org.freenono.controller.Settings;
import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;


public class GameMode_PenAndPaper extends GameMode {

	private static Logger logger = Logger.getLogger(GameMode_Quiz.class);

	private Token[][] field = null;
	
	private GameAdapter gameAdapter = new GameAdapter() {

		public void WrongFieldOccupied(FieldControlEvent e) {
			
		}
		
		public void MarkField(FieldControlEvent e) {

			if (field[e.getFieldRow()][e.getFieldColumn()] == Token.FREE) {
				
				field[e.getFieldRow()][e.getFieldColumn()] = Token.MARKED;
				eventHelper.fireFieldMarkedEvent(new FieldControlEvent(
						this, e.getFieldColumn(), e.getFieldRow()));
			}
			else if (field[e.getFieldRow()][e.getFieldColumn()] == Token.MARKED) {
				
				field[e.getFieldRow()][e.getFieldColumn()] = Token.FREE;
				eventHelper.fireFieldUnmarkedEvent(new FieldControlEvent(
						this, e.getFieldColumn(), e.getFieldRow()));
			}
		}

		public void OccupyField(FieldControlEvent e) {

			if (field[e.getFieldRow()][e.getFieldColumn()] == Token.FREE) {
				
				field[e.getFieldRow()][e.getFieldColumn()] = Token.OCCUPIED;
				eventHelper.fireFieldOccupiedEvent(new FieldControlEvent(
						this, e.getFieldColumn(), e.getFieldRow()));
			}
			else if (field[e.getFieldRow()][e.getFieldColumn()] == Token.OCCUPIED) {
				
				field[e.getFieldRow()][e.getFieldColumn()] = Token.FREE;
				eventHelper.fireFieldUnoccupiedEvent(new FieldControlEvent(this,
						e.getFieldColumn(), e.getFieldRow()));
			}
		}
	};
	
	
	public GameMode_PenAndPaper(GameEventHelper eventHelper, Nonogram nonogram,
			Settings settings) {

		super(eventHelper, nonogram, settings);

		eventHelper.addGameListener(gameAdapter);

		setGameModeType(GameModeType.PEN_AND_PAPER);
		
		// deactivate marking of wrongly occupied fields
		markInvalid = false;
		
		this.field = new Token[nonogram.height()][nonogram.width()];
		
		for (int i = 0; i < this.field.length; i++) {
			
			for (int j = 0; j < this.field[i].length; j++) {
				
				this.field[i][j] = Token.FREE;
			}
		}
	}

	
	@Override
	public boolean isSolved() {
		
		int y, x;
		boolean patternValue;
		Token fieldValue;

		for (y = 0; y < nonogram.height(); y++) {
			
			for (x = 0; x < nonogram.width(); x++) {

				patternValue = nonogram.getFieldValue(x, y);
				fieldValue = field[y][x];

				if (patternValue && fieldValue != Token.OCCUPIED) {
					return false;
				}
				
				if (!patternValue && fieldValue == Token.OCCUPIED) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public boolean isLost() {
		
		return false;
	}

	@Override
	protected void solveGame() {
		
	}

	@Override
	protected void pauseGame() {
		
	}

	@Override
	protected void resumeGame() {
		
	}

	@Override
	protected void stopGame() {
		
	}

	@Override
	protected Integer getGameScore() {
		
		// score is always zero!
		return new Integer(0);
	}
}
