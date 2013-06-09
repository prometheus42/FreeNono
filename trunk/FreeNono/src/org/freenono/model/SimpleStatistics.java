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
package org.freenono.model;

import java.util.ArrayList;
import java.util.List;

import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.StateChangeEvent;
import org.freenono.interfaces.Statistics;


/**
 * Calculates and outputs a simple statistic about field moves like marking,
 * occupying.
 * 
 * @author Christian Wichmann
 */
public class SimpleStatistics implements Statistics {

	private Nonogram nonogram = null;
	private GameEventHelper eventHelper = null;

	private int fieldsCorrectlyOccupied = 0;
	private int fieldsWronglyOccupied = 0;
	private int fieldsMarked = 0;
	private int occupiesPerSlot = 0;
	private int secondsCount = 0;
	private int occupyCount = 0;
	private int highscore = 0;

	private List<Integer> occupyCounts = new ArrayList<Integer>();

	public GameAdapter gameAdapter = new GameAdapter() {

		@Override
		public void FieldOccupied(FieldControlEvent e) {
			fieldsCorrectlyOccupied++;
			occupyCount++;
		}

		@Override
		public void FieldMarked(FieldControlEvent e) {
			fieldsMarked++;
		}

		@Override
		public void FieldUnmarked(FieldControlEvent e) {
		}

		@Override
		public void WrongFieldOccupied(FieldControlEvent e) {
			fieldsWronglyOccupied++;
		}

		@Override
		public void StateChanged(StateChangeEvent e) {
			switch (e.getNewState()) {
			case gameOver:
			case solved:
				outputStatistics();
				break;

			default:
				break;
			}
		}

		@Override
		public void Timer(StateChangeEvent e) {
			if (secondsCount >= 10) {
				occupyCounts.add(occupyCount);
				occupyCount = 0;
				secondsCount = 0;
			} else {
				secondsCount++;
			}
		}

		@Override
		public void OptionsChanged(ProgramControlEvent e) {
		}

		@Override
		public void ProgramControl(ProgramControlEvent e) {
		}

	};

	
	public SimpleStatistics(Nonogram nonogram) {

		this.nonogram = nonogram;
	}
	
	
	public void setEventHelper(GameEventHelper eventHelper) {
		
		this.eventHelper = eventHelper;
		eventHelper.addGameListener(gameAdapter);
	}
	
	public void removeEventHelper() {
		
		eventHelper.removeGameListener(gameAdapter);
		this.eventHelper = null;
	}
	

	private void calculateHighscore() {

	}

	private int calculateOccupyPerformance() {

		occupiesPerSlot = 0;

		for (Integer i : occupyCounts)
			occupiesPerSlot += i;

		return occupiesPerSlot / Math.max(1, occupyCounts.size()) * 6;

	}

	/*
	 * TODO: change output to use Messages.getString("Statistics.???")
	 */
	public void outputStatistics() {

		System.out
				.printf("***** Game Statistics **************************************\n");
		System.out
				.printf("*                                                          *\n");
		System.out.printf("* Nonogram: %s", nonogram.getName());
		for (int i = 0; i < Math.max(0, 47 - nonogram.getName().length()); i++)
			System.out.printf(" ");
		System.out.printf("*\n");
		System.out
				.printf("*                                                          *\n");
		System.out
				.printf("* fields occupied:                      %4d fields        *\n",
						fieldsCorrectlyOccupied);
		System.out
				.printf("* fields marked:                        %4d fields        *\n",
						fieldsMarked);
		System.out
				.printf("* fields wrongly occupied:              %4d fields        *\n",
						fieldsWronglyOccupied);
		System.out
				.printf("*                                                          *\n");
		System.out
				.printf("* fields occupied per minute:           %4d fields        *\n",
						calculateOccupyPerformance());
		System.out
				.printf("*                                                          *\n");
		System.out
				.printf("************************************************************\n");

	}

	/**
	 * @return the highscore calculated with the data collected so far
	 */
	public int getHighscore() {

		calculateHighscore();

		return highscore;

	}

	// /**
	// * Calculates a Score for the current state of the game.
	// *
	// * @return
	// */
	// public int getScore() {
	//
	// // TODO please implement me
	// // TODO add some kind of "successfullyDone" variable to the game
	// return (int) (this.getSuccessCount() - this.getFailCount() - this
	// .getUnmarkCount() * 0.2);
	//
	// }

}
