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
package org.freenono.event;

/**
 * Abstract adapter class to prevent the necessity to implement every Listener
 * function in every GameListener.
 */
public class GameAdapter implements GameListener {

	
	@Override
	public void MarkField(FieldControlEvent e) {
	}

	@Override
	public void OccupyField(FieldControlEvent e) {
	}

	@Override
	public void ChangeActiveField(FieldControlEvent e) {
	}
	
	@Override
	public void WrongFieldOccupied(FieldControlEvent e) {
	}

	@Override
	public void FieldOccupied(FieldControlEvent e){
	}
	
	@Override
	public void FieldUnoccupied(FieldControlEvent e){
	}
	
	@Override
	public void FieldMarked(FieldControlEvent e){
	}
	
	@Override
	public void FieldUnmarked(FieldControlEvent e){
	}
	
	
	
	@Override
	public void StateChanged(StateChangeEvent e) {
	}

	@Override
	public void SetTime(StateChangeEvent e) {
	}
	
	@Override
	public void Timer(StateChangeEvent e) {
	}
	
	@Override
	public void SetFailCount(StateChangeEvent e) {
	}
	
	
	
	@Override
	public void OptionsChanged(ProgramControlEvent e) {
	}

	@Override
	public void ProgramControl(ProgramControlEvent e) {
	}
	
	
	
	@Override
	public void AskQuestion(QuizEvent e) {
	}

}
