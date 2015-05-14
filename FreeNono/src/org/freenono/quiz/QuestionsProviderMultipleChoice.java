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
package org.freenono.quiz;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.freenono.ui.common.Tools;

/**
 * Provides multiple choice questions from sqlite file db.
 * 
 * (Libraries: https://bitbucket.org/xerial/sqlite-jdbc/ http://sqljet.com/tutorial.html)
 * 
 * @author Christian Wichmann
 */
public class QuestionsProviderMultipleChoice extends QuestionsProvider {

    private static Logger logger = Logger.getLogger(QuestionsProviderMultipleChoice.class);

    public static final String USER_QUESTIONS_PATH = System.getProperty("user.home") + Tools.FILE_SEPARATOR + ".FreeNono"
            + Tools.FILE_SEPARATOR + "quiz" + Tools.FILE_SEPARATOR + "german.db";

    private Connection connection = null;
    private Statement statement = null;

    /**
     * Initializes a question provider delivering multiple choice questions.
     */
    public QuestionsProviderMultipleChoice() {

        logger.debug("Connecting to quiz database...");

        // load the sqlite-JDBC driver using the current class loader
        try {
            Class.forName("org.sqlite.JDBC");

        } catch (ClassNotFoundException e) {
            logger.error("SQLite-JDBC Library not found.");
        }

        // create a database connection
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + USER_QUESTIONS_PATH);
            statement = connection.createStatement();
            statement.setQueryTimeout(30);

        } catch (SQLException e) {
            logger.error("Database connection failed.");
        }
    }

    @Override
    public final Question getNextQuestion(final int level) {

        if (level < 0 || level > 100) {
            throw new IllegalArgumentException("Level parameter should be between 0 and 100.");
        }

        ResultSet rs;
        final String[] answers = new String[4];
        String question = null;
        int correctAnswer = 0;

        try {

            // getting result set
            rs = statement.executeQuery("SELECT * FROM questions WHERE level = " + level + " ORDER BY RANDOM() LIMIT 1");

            // reading the result set
            rs.next();
            question = rs.getString("body");
            answers[0] = rs.getString("a");
            answers[1] = rs.getString("b");
            answers[2] = rs.getString("c");
            answers[3] = rs.getString("d");
            correctAnswer = rs.getInt("correct");

        } catch (SQLException e) {

            logger.warn("Could not read question from database.");
        }

        logger.debug("Generating new question of level " + level + ".");

        final Question q = new QuestionMultipleChoice(question, answers, correctAnswer);

        return q;
    }

}
