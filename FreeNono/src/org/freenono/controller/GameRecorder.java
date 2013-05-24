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
package org.freenono.controller;

import java.util.HashMap;
import java.util.Map;

import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.QuizEvent;
import org.freenono.event.StateChangeEvent;


/**
 * Static class for all recording functions. A new recording can be started
 * under a new name and all game board events are recorded. ONLY the board
 * event, NOT the events fired by the UI. When a replay is started, first a
 * board clean event will be fired and then all board events with a separation
 * time given through the constructor or the {@link #setSeparationTime(int)}
 * method.
 * 
 * @author Christian Wichmann
 */
public class GameRecorder {

	private static GameEventHelper eventHelper;
	private static Map<String, GameRecord> gameRecords;
	private static boolean listening = false;
	private static boolean replayRunning = false;
	private static GameRecord currentRecord;
	private static int separationTime;
	
	private GameAdapter gameAdapter = new GameAdapter() {
		
		public void OptionsChanged(ProgramControlEvent e) {
		}
		
		public void StateChanged(StateChangeEvent e) {

			switch (e.getNewState()) {
			case gameOver:
				break;

			case solved:
				break;
				
			case userStop:
				break;

			case paused:
				break;

			case running:
				break;

			default:
				break;
			}

		}

		public void FieldOccupied(FieldControlEvent e) {
			
		}

		public void FieldMarked(FieldControlEvent e) {
			
		}

		public void FieldUnmarked(FieldControlEvent e) {
			
		}
		
		public void ChangeActiveField(FieldControlEvent e) {
			
		}
		
		public void AskQuestion(QuizEvent e) {
			
		}

	};
	
	
	public GameRecorder(GameEventHelper eventHelper) {
		
		GameRecorder.eventHelper = eventHelper;
		
		eventHelper.addGameListener(gameAdapter);
		
		gameRecords = new HashMap<String, GameRecord>();
		
		buildReplayThread();
	}
	
	private void buildReplayThread() {
		
		// TODO
	}

	public static void startRecording(String gameName) {
		
		if (gameRecords.containsKey(gameName)) {
			currentRecord = gameRecords.get(gameName);
		}
		else {
			currentRecord = new GameRecord();
			gameRecords.put(gameName, currentRecord);
		}
		listening = true;
	}
	
	public static void stopRecording() {
		
		listening = false;
	}
	
	public static void replayRecording(String gameName) {
		
		replayRunning = true;
	}
	
	public static void stopReplay() {
		
		replayRunning = false;
	}

	public static int getSeparationTime() {
		
		return separationTime;
	}

	public static void setSeparationTime(int separationTime) {
		
		GameRecorder.separationTime = separationTime;
	}
	
}
