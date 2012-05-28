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
package org.freenono.quiz;

public class QuizQuestion {

	private String question = null;
	private String answer = null;
	
	public QuizQuestion(String question) {
		
		this.question = question;
	}
	
	public QuizQuestion(String question, String answer) {
		
		this.question = question;
		this.answer = answer;
	}

	public String getQuestion() {
		
		return question;
	}

	public void setQuestion(String question) {
		
		this.question = question;
	}

	public String getAnswer() {
		
		return answer;
	}

	public void setAnswer(String answer) {
		
		this.answer = answer;
	}
	
	public String toString() {
		
		return question;
	}
}
