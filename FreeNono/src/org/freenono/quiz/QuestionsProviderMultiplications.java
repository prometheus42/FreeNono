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

import java.util.Random;

public class QuestionsProviderMultiplications extends QuestionsProvider {

	private Random rng = null;

	public QuestionsProviderMultiplications() {
		
		rng = new Random();
	}

	@Override
	public QuizQuestion getNextQuestion() {

		int a = rng.nextInt(20) + 1;
		int b = rng.nextInt(20) + 1;

		QuizQuestion q = new QuestionMultiplication(new String(
				"Multiplizieren Sie " + a + " mit " + b + "!"), new String(
				Integer.toString(a * b)));

		return q;
	}

}