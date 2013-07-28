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
package org.freenono.board;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.apache.log4j.Logger;
import org.freenono.controller.Settings;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.ProgramControlEvent.ProgramControlType;
import org.freenono.event.StateChangeEvent;
import org.freenono.model.game_modes.GameModeType;
import org.freenono.model.game_modes.GameTime;
import org.freenono.ui.Messages;
import org.freenono.ui.common.FontFactory;

/**
 * Displays a information box showing the game mode of the current game and more
 * information depending on this mode.
 * 
 * @author Christian Wichmann
 */
public class StatusComponent extends JPanel {

    private static final long serialVersionUID = 1283871798919081849L;

    private static Logger logger = Logger.getLogger(StatusComponent.class);

    private GameEventHelper eventHelper = null;
    private Settings settings = null;

    private static final Color LCD_COLOR = new Color(110, 95, 154);
    private JLabel failCountLabel;
    private JLabel failCountDisplay;
    private JLabel timeLabel;
    private JLabel timeDisplay;
    private JLabel nonogramNameLabel;
    private JLabel nonogramNameDisplay;

    private GameAdapter gameAdapter = new GameAdapter() {

        @Override
        public void setFailCount(final StateChangeEvent e) {

            if (settings.getGameMode() == GameModeType.MAX_FAIL) {
                refreshFailCount(e.getFailCount());
            }
        }

        @Override
        public void timerElapsed(final StateChangeEvent e) {

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    refreshTime(e.getGameTime());
                }
            });
        }

        @Override
        public void setTime(final StateChangeEvent e) {

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    refreshTime(e.getGameTime());
                }
            });
        }

        @Override
        public void stateChanging(final StateChangeEvent e) {

            switch (e.getNewState()) {
            case GAME_OVER:
                break;
            case SOLVED:
                break;
            case PAUSED:
                break;
            case RUNNING:
                break;
            case NONE:
                break;
            case USER_STOP:
                break;
            default:
                assert false : e.getNewState();
                break;
            }

        }

        @Override
        public void programControl(final ProgramControlEvent e) {

            // if game is started, show name of nonogram
            if (settings.isShowNonogramName()) {

                if (e.getPct() == ProgramControlType.START_GAME
                        || e.getPct() == ProgramControlType.RESTART_GAME) {

                    nonogramNameDisplay.setText(e.getPattern().getName());
                }
            }
        }

        @Override
        public void optionsChanged(final ProgramControlEvent e) {

            repaint();
        }
    };

    /**
     * Constructor that stores the settings, loads fonts and initializes the
     * status component.
     * 
     * @param settings
     *            Settings
     */
    public StatusComponent(final Settings settings) {

        if (settings == null) {
            throw new NullPointerException("Parameter should not be null.");
        }

        this.settings = settings;

        initialize();
    }

    /**
     * Initialize this component.
     */
    private void initialize() {

        // set GridBagLayout as layout manager
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints;
        setLayout(layout);

        final int size = 300;
        setMinimumSize(new Dimension(size, size));

        // get constraints for GridBagLayout
        final int inset = 15;
        constraints = new GridBagConstraints();
        constraints.insets = new Insets(inset, inset, inset, inset);
        int currentRow = 0;

        // set border
        Border border = new EtchedBorder(EtchedBorder.RAISED);
        setBorder(border);

        // add nonogram name if settings allow it
        if (settings.isShowNonogramName()) {

            nonogramNameLabel = new JLabel(
                    Messages.getString("StatusComponent.NonogramNameLabel"));
            nonogramNameLabel.setFont(FontFactory.createTextFont());
            constraints.gridheight = 1;
            constraints.gridwidth = 2;
            constraints.weightx = 1.0;
            constraints.weighty = 1.0;
            constraints.gridx = 0;
            constraints.gridy = currentRow++;
            constraints.anchor = GridBagConstraints.WEST;
            add(nonogramNameLabel, constraints);

            nonogramNameDisplay = new JLabel("");
            nonogramNameDisplay.setFont(FontFactory.createLcdFont());
            nonogramNameDisplay.setForeground(LCD_COLOR);
            constraints.gridheight = 1;
            constraints.gridwidth = 2;
            constraints.gridx = 1;
            constraints.gridy = currentRow++;
            constraints.anchor = GridBagConstraints.EAST;
            add(nonogramNameDisplay, constraints);
        }

        // add game mode description
        JLabel gameModeLabel = new JLabel(
                Messages.getString("StatusComponent.GameModeLabel"));
        gameModeLabel.setFont(FontFactory.createTextFont());
        constraints.gridheight = 1;
        constraints.gridwidth = 2;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.gridx = 0;
        constraints.gridy = currentRow++;
        constraints.anchor = GridBagConstraints.WEST;
        add(gameModeLabel, constraints);

        JLabel gameModeDisplay = new JLabel(settings.getGameMode().toString());
        gameModeDisplay.setFont(FontFactory.createLcdFont());
        gameModeDisplay.setForeground(LCD_COLOR);
        constraints.gridheight = 1;
        constraints.gridwidth = 2;
        constraints.gridx = 1;
        constraints.gridy = currentRow++;
        constraints.anchor = GridBagConstraints.EAST;
        add(gameModeDisplay, constraints);

        if (settings.getGameMode() == GameModeType.COUNT_TIME
                || settings.getGameMode() == GameModeType.MAX_TIME
                || settings.getGameMode() == GameModeType.PENALTY) {

            /*
             * add time to component
             */

            timeLabel = new JLabel(
                    Messages.getString("StatusComponent.TimeLabel"));
            timeLabel.setFont(FontFactory.createTextFont());
            constraints.gridheight = 1;
            constraints.gridwidth = 2;
            constraints.gridx = 0;
            constraints.gridy = currentRow++;
            constraints.anchor = GridBagConstraints.WEST;
            add(timeLabel, constraints);

            timeDisplay = new JLabel();
            timeDisplay.setFont(FontFactory.createLcdFont());
            timeDisplay.setForeground(LCD_COLOR);
            constraints.gridheight = 1;
            constraints.gridwidth = 2;
            constraints.gridx = 1;
            constraints.gridy = currentRow++;
            constraints.anchor = GridBagConstraints.EAST;
            add(timeDisplay, constraints);

        } else if (settings.getGameMode() == GameModeType.MAX_FAIL) {

            /*
             * set fail count label
             */

            failCountLabel = new JLabel(
                    Messages.getString("StatusComponent.FailCountLabel"));
            failCountLabel.setFont(FontFactory.createTextFont());
            constraints.gridheight = 1;
            constraints.gridwidth = 2;
            constraints.gridx = 0;
            constraints.gridy = currentRow++;
            constraints.anchor = GridBagConstraints.WEST;
            add(failCountLabel, constraints);

            failCountDisplay = new JLabel();
            failCountDisplay.setFont(FontFactory.createLcdFont());
            failCountDisplay.setForeground(LCD_COLOR);
            refreshFailCount(settings.getMaxFailCount());
            constraints.gridheight = 1;
            constraints.gridwidth = 2;
            constraints.gridx = 1;
            constraints.gridy = currentRow++;
            constraints.anchor = GridBagConstraints.EAST;
            add(failCountDisplay, constraints);
        }

        validate();
    }

    /**
     * Setter event helper.
     * @param eventHelper
     *            Event helper
     */
    public final void setEventHelper(final GameEventHelper eventHelper) {

        this.eventHelper = eventHelper;
        eventHelper.addGameListener(gameAdapter);
    }

    /**
     * Remove event helper.
     */
    public final void removeEventHelper() {

        eventHelper.removeGameListener(gameAdapter);
        this.eventHelper = null;
    }

    /**
     * Set time in status.
     * @param gameTime
     *            New gametime
     */
    private void refreshTime(final GameTime gameTime) {

        timeDisplay.setText(gameTime.toString());
    }

    /**
     * Set fail count.
     * @param failCount
     *            New fail count.
     */
    private void refreshFailCount(final int failCount) {

        logger.debug("Refreshing fail count.");

        if (failCount >= 0) {
            failCountDisplay.setText(Integer.toString(failCount)
                    + Messages.getString("StatusComponent.ErrorsLeft"));
        }
    }

    /**
     * Paint the component.
     * 
     * paints an gradient over the statusComponent (source by:
     * http://weblogs.java.net/blog/gfx/archive/2006/09/java2d_gradient.html)
     * 
     * @param g
     *            Graphics object to draw to.
     */
    @Override
    protected final void paintComponent(final Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        BufferedImage cache = null;
        if (cache == null || cache.getHeight() != getHeight()) {
            cache = new BufferedImage(2, getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = cache.createGraphics();

            GradientPaint paint = new GradientPaint(0, 0, Color.WHITE, 0,
                    getHeight(), settings.getColorModel().getCharmColor());
            g2d.setPaint(paint);
            g2d.fillRect(0, 0, 2, getHeight());
            g2d.dispose();
        }
        g2.drawImage(cache, 0, 0, getWidth(), getHeight(), null);
    }

}
