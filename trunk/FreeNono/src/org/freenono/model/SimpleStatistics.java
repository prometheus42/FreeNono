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

    private List<Integer> occupyCounts = new ArrayList<Integer>();

    private GameAdapter gameAdapter = new GameAdapter() {

        @Override
        public void fieldOccupied(final FieldControlEvent e) {

            fieldsCorrectlyOccupied++;
            occupyCount++;
        }

        @Override
        public void fieldMarked(final FieldControlEvent e) {

            fieldsMarked++;
        }

        @Override
        public void fieldUnmarked(final FieldControlEvent e) {

        }

        @Override
        public void wrongFieldOccupied(final FieldControlEvent e) {

            fieldsWronglyOccupied++;
        }

        @Override
        public void stateChanged(final StateChangeEvent e) {

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
        public void timerElapsed(final StateChangeEvent e) {

            if (secondsCount >= 10) {
                occupyCounts.add(occupyCount);
                occupyCount = 0;
                secondsCount = 0;
            } else {
                secondsCount++;
            }
        }

        @Override
        public void optionsChanged(final ProgramControlEvent e) {

        }

        @Override
        public void programControl(final ProgramControlEvent e) {

        }

    };

    /**
     * Initializes a simple statistics class.
     */
    public SimpleStatistics() {

    }

    @Override
    public final void setEventHelper(final GameEventHelper eventHelper) {

        this.eventHelper = eventHelper;
        eventHelper.addGameListener(gameAdapter);
    }

    @Override
    public final void removeEventHelper() {

        eventHelper.removeGameListener(gameAdapter);
        this.eventHelper = null;
    }

    private int calculateOccupyPerformance() {

        occupiesPerSlot = 0;

        for (Integer i : occupyCounts)
            occupiesPerSlot += i;

        return occupiesPerSlot / Math.max(1, occupyCounts.size()) * 6;

    }

    public void outputStatistics() {

        /*
         * TODO change output to use Messages.getString("Statistics.???")
         */

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

    @Override
    public final Object getValue(final String property) {

        // TODO Auto-generated method stub
        return null;
    }
}
