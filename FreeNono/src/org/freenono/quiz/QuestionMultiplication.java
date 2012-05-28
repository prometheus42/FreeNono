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

import org.apache.log4j.Logger;


public class QuestionMultiplication extends QuizQuestion {

	private static Logger logger = Logger.getLogger(QuestionMultiplication.class);

	
	public QuestionMultiplication(String question, String answer) {
		
		super(question, answer);
	}

	public QuestionMultiplication(String question) {
		
		super(question);
	}

	public boolean checkAnswer(String answer) {

		logger.debug("numbers: " + getAnswer() + ", " + answer);
		logger.debug("numbers: " + Integer.valueOf(getAnswer()) + ", "
				+ Integer.valueOf(answer));
		if (Integer.valueOf(getAnswer()).compareTo(Integer.valueOf(answer)) != 0)
			return false;
		else
			return true;
	}
}
