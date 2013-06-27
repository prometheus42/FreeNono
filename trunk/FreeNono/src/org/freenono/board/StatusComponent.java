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
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JPanel;
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

    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private static final Color LCD_COLOR = new Color(110, 95, 154);
    private JLabel failCountLabel;
    private JLabel failCountDisplay;
    private JLabel timeLabel;
    private JLabel timeDisplay;
    private JLabel gameModeDisplay;
    private JLabel gameModeLabel;
    private JLabel nonogramNameLabel;
    private JLabel nonogramNameDisplay;

    private static final int FONT_LCD_SIZE = 28;
    private static final String FONT_LCD_FONT = "LCDMono2";
    private static final int FONT_LCD_STYLE = Font.PLAIN;
    private static final int FONT_TEXT_SIZE = 16;
    private static final String FONT_TEXT_FONT = "FreeSans";
    private static final int FONT_TEXT_STYLE = Font.PLAIN;
    private Font fontLCD = null;
    private Font fontText = null;

    private int failCountLeft = 0;

    private GameAdapter gameAdapter = new GameAdapter() {

        @Override
        public void setFailCount(final StateChangeEvent e) {

            if (settings.getGameMode() == GameModeType.MAX_FAIL) {
                refreshFailCount(e.getFailCount());
            }
        }

        @Override
        public void timerElapsed(final StateChangeEvent e) {

            refreshTime(e.getGameTime());
        }

        @Override
        public void setTime(final StateChangeEvent e) {

            refreshTime(e.getGameTime());
        }

        @Override
        public void stateChanging(final StateChangeEvent e) {

            switch (e.getNewState()) {
            case gameOver:
                // TODO Handle error where time don't get to zero in maximum
                // time game mode.
                break;

            case solved:
                break;

            case paused:
                break;

            case running:
                break;

            default:
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
    };

    /**
     * Constructor that stores the settings, loads fonts and inits the status
     * component.
     * @param settings
     *            Settings
     */
    public StatusComponent(final Settings settings) {

        this.settings = settings;

        loadFonts();

        initialize();
    }

    /**
     * Load fonts.
     */
    private void loadFonts() {

        fontLCD = new Font(FONT_LCD_FONT, FONT_LCD_STYLE, FONT_LCD_SIZE);
        fontText = new Font(FONT_TEXT_FONT, FONT_TEXT_STYLE, FONT_TEXT_SIZE);
    }

    /**
     * Initialize this component.
     */
    private void initialize() {

        // set GridBagLayout as layout manager
        layout = new GridBagLayout();
        this.setLayout(layout);
        this.setMinimumSize(new Dimension(300, 300));

        // get constraints for GridBagLayout
        constraints = new GridBagConstraints();
        constraints.insets = new Insets(15, 15, 15, 15);

        // set border
        Border border = new EtchedBorder(EtchedBorder.RAISED);
        this.setBorder(border);

        // add nonogram name if settings allow it
        if (settings.isShowNonogramName()) {

            nonogramNameLabel = new JLabel(
                    Messages.getString("StatusComponent.NonogramNameLabel"));
            nonogramNameLabel.setFont(fontText);
            constraints.gridheight = 1;
            constraints.gridwidth = 2;
            constraints.weightx = 1.0;
            constraints.weighty = 1.0;
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.anchor = GridBagConstraints.WEST;
            this.add(nonogramNameLabel, constraints);

            nonogramNameDisplay = new JLabel("");
            nonogramNameDisplay.setFont(fontLCD);
            nonogramNameDisplay.setForeground(LCD_COLOR);
            constraints.gridheight = 1;
            constraints.gridwidth = 2;
            constraints.gridx = 1;
            constraints.gridy = 1;
            constraints.anchor = GridBagConstraints.EAST;
            this.add(nonogramNameDisplay, constraints);
        }

        // add game mode description
        gameModeLabel = new JLabel(
                Messages.getString("StatusComponent.GameModeLabel"));
        gameModeLabel.setFont(fontText);
        constraints.gridheight = 1;
        constraints.gridwidth = 2;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.WEST;
        this.add(gameModeLabel, constraints);

        gameModeDisplay = new JLabel(settings.getGameMode().toString());
        gameModeDisplay.setFont(fontLCD);
        gameModeDisplay.setForeground(LCD_COLOR);
        constraints.gridheight = 1;
        constraints.gridwidth = 2;
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.EAST;
        this.add(gameModeDisplay, constraints);

        // add time to component
        if (settings.getGameMode() == GameModeType.COUNT_TIME
                || settings.getGameMode() == GameModeType.MAX_TIME
                || settings.getGameMode() == GameModeType.PENALTY) {

            timeLabel = new JLabel(
                    Messages.getString("StatusComponent.TimeLabel"));
            timeLabel.setFont(fontText);
            constraints.gridheight = 1;
            constraints.gridwidth = 2;
            constraints.gridx = 0;
            constraints.gridy = 4;
            constraints.anchor = GridBagConstraints.WEST;
            this.add(timeLabel, constraints);

            timeDisplay = new JLabel();
            timeDisplay.setFont(fontLCD);
            timeDisplay.setForeground(LCD_COLOR);
            constraints.gridheight = 1;
            constraints.gridwidth = 2;
            constraints.gridx = 1;
            constraints.gridy = 5;
            constraints.anchor = GridBagConstraints.EAST;
            this.add(timeDisplay, constraints);
        }

        // set fail count label
        else if (settings.getGameMode() == GameModeType.MAX_FAIL) {

            failCountLabel = new JLabel(
                    Messages.getString("StatusComponent.FailCountLabel"));
            failCountLabel.setFont(fontText);
            constraints.gridheight = 1;
            constraints.gridwidth = 2;
            constraints.gridx = 0;
            constraints.gridy = 4;
            constraints.anchor = GridBagConstraints.WEST;
            this.add(failCountLabel, constraints);

            failCountDisplay = new JLabel();
            failCountDisplay.setFont(fontLCD);
            failCountDisplay.setForeground(LCD_COLOR);
            refreshFailCount(settings.getMaxFailCount());
            constraints.gridheight = 1;
            constraints.gridwidth = 2;
            constraints.gridx = 1;
            constraints.gridy = 5;
            constraints.anchor = GridBagConstraints.EAST;
            this.add(failCountDisplay, constraints);
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
     * @param gameTime New gametime
     */
    private void refreshTime(final GameTime gameTime) {

        timeDisplay.setText(gameTime.toString());
    }

    /**
     * Set fail count.
     * @param failCount New fail count.
     */
    private void refreshFailCount(final int failCount) {

        logger.debug("Refreshing fail count.");

        failCountLeft = failCount;

        if (failCountLeft >= 0) {
            failCountDisplay.setText(Integer.toString(failCountLeft)
                    + Messages.getString("StatusComponent.ErrorsLeft"));
        }
    }

    /**
     * Paint the component.
     * 
     * paints an gradient over the statusComponent (source by:
     * http://weblogs.java.net/blog/gfx/archive/2006/09/java2d_gradient.html)
     * 
     * @param g Graphics object to draw to.
     */
    protected final void paintComponent(final Graphics g) {

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
