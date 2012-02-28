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

import org.freenono.event.GameAdapter;
import org.freenono.event.GameEvent;
import org.freenono.event.GameEventHelper;
import org.freenono.ui.GameOverUI;
import org.freenono.ui.Messages;

public class GameStatistics {

	private Nonogram nonogram = null;
	private GameEventHelper eventHelper = null;
	
	private int fieldsCorrectlyOccupied = 0;
	private int fieldsWronglyOccupied = 0;
	private int fieldsMarked = 0;
	private int occupiesPerMinute = 0;
	private int secondsCount = 0;
	private int occupyCount = 0;
	private int highscore = 0;
	
	private List<Integer> occupyCounts = new ArrayList<Integer>();

	public GameAdapter gameAdapter = new GameAdapter() {

		@Override
		public void OccupyField(GameEvent e) {
			fieldsCorrectlyOccupied++;
			occupyCount++;
		}

		@Override
		public void MarkField(GameEvent e) {
			fieldsMarked++;
		}

		@Override
		public void ChangeActiveField(GameEvent e) {
		}

		@Override
		public void FieldOccupied(GameEvent e) {
		}

		@Override
		public void FieldMarked(GameEvent e) {
		}

		@Override
		public void FieldUnmarked(GameEvent e) {
		}

		@Override
		public void WrongFieldOccupied(GameEvent e) {
			fieldsWronglyOccupied++;
		}

		@Override
		public void StateChanged(GameEvent e) {
			switch (e.getNewState()) {
			case gameOver:
			case solved:
				printStatistics();
				break;

			default:
				break;
			}
		}

		@Override
		public void Timer(GameEvent e) {
			if (secondsCount >= 10) {
				occupyCounts.add(occupyCount);
				occupyCount = 0;
				secondsCount = 0;
			} else {
				secondsCount++;
			}
		}

		@Override
		public void OptionsChanged(GameEvent e) {
		}

		@Override
		public void ProgramControl(GameEvent e) {			
		}
		
	};
	
	
	public GameStatistics(Nonogram nonogram, GameEventHelper eventHelper) {
		
		this.nonogram = nonogram;
		this.eventHelper = eventHelper;
		eventHelper.addGameListener(gameAdapter);
		
	}
	
	private void calculateHighscore() {
		
	}
	
	private int calculateOccupyPerformance() {

		occupiesPerMinute = 0;
		
		for (Integer i : occupyCounts)
			occupiesPerMinute += i;
		
		return occupiesPerMinute / occupyCounts.size() * 6;

	}
	
	public void printStatistics() {
		 
		System.out.printf("***** Game Statistics **************************************\n");
		System.out.printf("*                                                          *\n");
		System.out.printf("* Nonogram: %s", nonogram.getName());
		for (int i = 0; i < Math.max(0, 47-nonogram.getName().length()); i++)
			System.out.printf(" ");
		System.out.printf("*\n");
		System.out.printf("*                                                          *\n");
		System.out.printf("* fields occupied:                      %4d fields        *\n", fieldsCorrectlyOccupied);
		System.out.printf("* fields marked:                        %4d fields        *\n", fieldsMarked);
		System.out.printf("* fields wrongly occupied:              %4d fields        *\n", fieldsWronglyOccupied);
		System.out.printf("*                                                          *\n");
		System.out.printf("* fields occupied per minute:           %4d fields        *\n", calculateOccupyPerformance());
		System.out.printf("*                                                          *\n");
		System.out.printf("************************************************************\n");
		
	}

	/**
	 * @return the highscore calculated with the data collected so far
	 */
	public int getHighscore() {
		
		calculateHighscore();
		
		return highscore;
		
	}

}
