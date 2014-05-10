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
package org.freenono.controller;

import java.awt.Color;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.ProgramControlEvent.ProgramControlType;
import org.freenono.model.game_modes.GameModeType;
import org.freenono.ui.colormodel.ColorModel;
import org.freenono.ui.colormodel.ColorModelAnalogous;
import org.freenono.ui.colormodel.ColorModelEvenlySpaced;

/**
 * Stores all settings and provides getter and setter for them. For all settings
 * a default is defined.
 * 
 * @author Christian Wichmann, Markus Wichmann, Martin Wichmann
 */
public class Settings {

    /*
     * To add a new option:
     * 
     * 1) add field and default constant,
     * 
     * 2) implement getter and setter equal to the existing options,
     * 
     * 3) add option to resetSettings() method.
     * 
     * 4) add option to copy constructor.
     */

    private static Logger logger = Logger.getLogger(Settings.class);

    private GameEventHelper eventHelper;
    private ControlSettings controlSettings;
    private ColorModel currentColorModel;

    private static final boolean USE_MAX_FAIL_COUNT_DEFAULT = true;
    private boolean useMaxFailCount = true;

    private static final int MAX_FAIL_COUNT_DEFAULT = 5;
    private int maxFailCount = MAX_FAIL_COUNT_DEFAULT;

    private static final boolean USE_MAX_TIME_DEFAULT = true;
    private boolean useMaxTime = true;

    private static final long MAX_TIME_DEFAULT = 1800000;
    private long maxTime = MAX_TIME_DEFAULT;

    private static final boolean MARK_INVALID_DEFAULT = true;
    private boolean markInvalid = MARK_INVALID_DEFAULT;

    private static final boolean COUNT_MARKED_DEFAULT = false;
    private boolean countMarked = COUNT_MARKED_DEFAULT;

    private static final boolean PLAY_AUDIO_DEFAULT = false;
    private boolean playAudio = PLAY_AUDIO_DEFAULT;

    private static final boolean PLAY_MUSIC_DEFAULT = true;
    private boolean playMusic = PLAY_MUSIC_DEFAULT;

    private static final boolean PLAY_EFFECTS_DEFAULT = false;
    private boolean playEffects = PLAY_EFFECTS_DEFAULT;

    private static final boolean HIDE_PLAYFIELD_DEFAULT = true;
    private boolean hidePlayfield = HIDE_PLAYFIELD_DEFAULT;

    private static final boolean SEARCH_FOR_UPDATES_DEFAULT = false;
    private boolean searchForUpdates = SEARCH_FOR_UPDATES_DEFAULT;

    private static final boolean ACTIVATE_CHAT_DEFAULT = true;
    private boolean activateChat = ACTIVATE_CHAT_DEFAULT;

    private static final boolean CROSS_CAPTIONS_DEFAULT = false;
    private boolean crossCaptions = CROSS_CAPTIONS_DEFAULT;

    private static final boolean MARK_COMPLETE_ROWS_COLUMNS_DEFAULT = false;
    private boolean markCompleteRowsColumns = MARK_COMPLETE_ROWS_COLUMNS_DEFAULT;

    private static final boolean SHOW_NONOGRAM_NAME_DEFAULT = false;
    private boolean showNonogramName = SHOW_NONOGRAM_NAME_DEFAULT;

    private static final GameModeType GAME_MODE_DEFAULT = GameModeType.PENALTY;
    private GameModeType gameMode = GAME_MODE_DEFAULT;

    private static final Color BASE_COLOR_DEFAULT = new Color(153, 255, 153);
    private Color baseColor = BASE_COLOR_DEFAULT;

    private static final Color TEXT_COLOR_DEFAULT = Color.BLACK;
    private Color textColor = TEXT_COLOR_DEFAULT;

    private static final Locale GAME_LOCALE_DEFAULT = Locale.ROOT;
    private Locale gameLocale = GAME_LOCALE_DEFAULT;

    private static final String PLAYER_NAME_DEFAULT = System
            .getProperty("user.name");
    private String playerName = PLAYER_NAME_DEFAULT;

    private static final boolean ASK_FOR_PLAYER_NAME_DEFAULT = true;
    private boolean askForPlayerName = ASK_FOR_PLAYER_NAME_DEFAULT;

    /**
     * Default constructor building a <code>Settings</code> object with default
     * values.
     */
    public Settings() {

        currentColorModel = new ColorModelAnalogous(baseColor);
        controlSettings = new ControlSettings();
    }

    /**
     * Constructs a new copy of a given <code>Settings</code> object with all
     * values from the existing object. Only the game event helper will
     * <strong>not</strong> be copied, because only <strong>one</strong>
     * instance should be allowed to fire options changed events.
     * 
     * @param oldSettings
     *            settings to be copied into the new object.
     */
    public Settings(final Settings oldSettings) {

        /*
         * Set all options from old settings object. Because many classes
         * (String, Integer, Color, Locale) are immutable they don't have to be
         * copied themselves!
         */
        setBaseColor(oldSettings.getBaseColor());
        setTextColor(oldSettings.getTextColor());
        setCountMarked(oldSettings.getCountMarked());
        setCrossCaptions(oldSettings.getCrossCaptions());
        setGameLocale(oldSettings.getGameLocale());
        setGameMode(oldSettings.getGameMode());
        setHidePlayfield(oldSettings.getHidePlayfield());
        setMarkCompleteRowsColumns(oldSettings.getMarkCompleteRowsColumns());
        setMarkInvalid(oldSettings.getMarkInvalid());
        setMaxFailCount(oldSettings.getMaxFailCount());
        setMaxTime(oldSettings.getMaxTime());
        setPlayAudio(oldSettings.getPlayAudio());
        setPlayEffects(oldSettings.isPlayEffects());
        setPlayMusic(oldSettings.isPlayMusic());
        setShowNonogramName(oldSettings.isShowNonogramName());
        setUseMaxFailCount(oldSettings.getUseMaxFailCount());
        setUseMaxTime(oldSettings.getUseMaxTime());
        setPlayerName(oldSettings.getPlayerName());
        setAskForPlayerName(oldSettings.shouldAskForPlayerName());
        setSearchForUpdates(oldSettings.shouldSearchForUpdates());
        setActivateChat(oldSettings.shouldActivateChat());

        /*
         * Set all objects included in settings for storing color and control
         * settings.
         */
        controlSettings = new ControlSettings();
        for (Control c : Control.values()) {
            setControl(c, oldSettings.getKeyCodeForControl(c));
        }
        currentColorModel = oldSettings.getColorModel();
    }

    /**
     * Reset all settings to defaults.
     */
    public final void resetSettings() {

        logger.debug("Resetting settings to default.");

        setBaseColor(BASE_COLOR_DEFAULT);
        setTextColor(TEXT_COLOR_DEFAULT);
        setCountMarked(COUNT_MARKED_DEFAULT);
        setCrossCaptions(CROSS_CAPTIONS_DEFAULT);
        setGameLocale(GAME_LOCALE_DEFAULT);
        setGameMode(GAME_MODE_DEFAULT);
        setHidePlayfield(HIDE_PLAYFIELD_DEFAULT);
        setMarkCompleteRowsColumns(MARK_COMPLETE_ROWS_COLUMNS_DEFAULT);
        setMarkInvalid(MARK_INVALID_DEFAULT);
        setMaxFailCount(MAX_FAIL_COUNT_DEFAULT);
        setMaxTime(MAX_TIME_DEFAULT);
        setPlayAudio(PLAY_AUDIO_DEFAULT);
        setPlayEffects(PLAY_EFFECTS_DEFAULT);
        setPlayMusic(PLAY_MUSIC_DEFAULT);
        setShowNonogramName(SHOW_NONOGRAM_NAME_DEFAULT);
        setUseMaxFailCount(USE_MAX_FAIL_COUNT_DEFAULT);
        setUseMaxTime(USE_MAX_TIME_DEFAULT);
        setPlayerName(PLAYER_NAME_DEFAULT);
        setAskForPlayerName(ASK_FOR_PLAYER_NAME_DEFAULT);
        setSearchForUpdates(SEARCH_FOR_UPDATES_DEFAULT);
        setActivateChat(ACTIVATE_CHAT_DEFAULT);

        currentColorModel.setBaseColor(BASE_COLOR_DEFAULT);
        controlSettings.resetSettings();
    }

    /**
     * Sets all options in this instance according to the values of another
     * instance of <code>Settings</code>. The given object will not be changed
     * and all options will be copied so that no reference will remain. Remember
     * that only one instance can/should hold the game event helper to fire
     * OPTIONS_CHANGED events.
     * 
     * @param newSettings
     *            settings object with new options
     */
    public final void setAllOptions(final Settings newSettings) {

        setBaseColor(newSettings.getBaseColor());
        setTextColor(newSettings.getTextColor());
        setCountMarked(newSettings.getCountMarked());
        setCrossCaptions(newSettings.getCrossCaptions());
        setGameLocale(newSettings.getGameLocale());
        setGameMode(newSettings.getGameMode());
        setHidePlayfield(newSettings.getHidePlayfield());
        setMarkCompleteRowsColumns(newSettings.getMarkCompleteRowsColumns());
        setMarkInvalid(newSettings.getMarkInvalid());
        setMaxFailCount(newSettings.getMaxFailCount());
        setMaxTime(newSettings.getMaxTime());
        setPlayAudio(newSettings.getPlayAudio());
        setPlayEffects(newSettings.isPlayEffects());
        setPlayMusic(newSettings.isPlayMusic());
        setShowNonogramName(newSettings.isShowNonogramName());
        setUseMaxFailCount(newSettings.getUseMaxFailCount());
        setUseMaxTime(newSettings.getUseMaxTime());
        setPlayerName(newSettings.getPlayerName());
        setAskForPlayerName(newSettings.shouldAskForPlayerName());
        setSearchForUpdates(newSettings.shouldSearchForUpdates());
        setActivateChat(newSettings.shouldActivateChat());

        for (Control c : Control.values()) {
            setControl(c, newSettings.getKeyCodeForControl(c));
        }
        currentColorModel = newSettings.getColorModel();
    }

    /**
     * Gets maximum fail count. It is used by game modes who count invalid moves
     * and game is lost when maximum is reached.
     * 
     * @return maximum fail count
     */
    public final int getMaxFailCount() {

        return maxFailCount;
    }

    /**
     * Sets maximum fail count. It is used by game modes who count invalid moves
     * and game is lost when maximum is reached.
     * 
     * @param maxFailCount
     *            maximum fail count
     */
    public final void setMaxFailCount(final int maxFailCount) {

        if (this.maxFailCount != maxFailCount) {
            this.maxFailCount = maxFailCount;

            if (eventHelper != null) {
                eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
                        this, ProgramControlType.OPTIONS_CHANGED));
            }
        }
    }

    /**
     * Getter use max time.
     * 
     * @return Use max time
     */
    @Deprecated
    public final boolean getUseMaxTime() {

        return useMaxTime;
    }

    /**
     * Setter use max time.
     * 
     * @param useMaxTime
     *            Use max time
     */
    @Deprecated
    public final void setUseMaxTime(final boolean useMaxTime) {

        if (this.useMaxTime != useMaxTime) {
            this.useMaxTime = useMaxTime;

            if (eventHelper != null) {
                eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
                        this, ProgramControlType.OPTIONS_CHANGED));
            }
        }
    }

    /**
     * Gets maximum game time. This time is used by game modes to count down
     * from. It is saved as number of milliseconds.
     * 
     * @return maximum game time
     */
    public final long getMaxTime() {

        return maxTime;
    }

    /**
     * Sets maximum game time. This time is used by game modes to count down
     * from. It is saved as number of milliseconds.
     * 
     * @param maxTime
     *            maximum game time
     */
    public final void setMaxTime(final long maxTime) {

        if (this.maxTime != maxTime) {
            this.maxTime = maxTime;

            if (eventHelper != null) {
                eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
                        this, ProgramControlType.OPTIONS_CHANGED));
            }
        }
    }

    /**
     * Getter use max fail count.
     * 
     * @return Use max fail count.
     */
    @Deprecated
    public final boolean getUseMaxFailCount() {

        return useMaxFailCount;
    }

    /**
     * Setter use max fail count.
     * 
     * @param useMaxFailCount
     *            Use max fail count
     */
    @Deprecated
    public final void setUseMaxFailCount(final boolean useMaxFailCount) {

        if (this.useMaxFailCount != useMaxFailCount) {
            this.useMaxFailCount = useMaxFailCount;

            if (eventHelper != null) {
                eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
                        this, ProgramControlType.OPTIONS_CHANGED));
            }
        }
    }

    /**
     * Gets whether wrongly occupied fields should be marked.
     * 
     * @return true, if wrongly occupied fields should be marked
     */
    public final boolean getMarkInvalid() {

        return markInvalid;
    }

    /**
     * Sets whether wrongly occupied fields should be marked.
     * 
     * @param markInvalid
     *            if wrongly occupied fields should be marked
     */
    public final void setMarkInvalid(final boolean markInvalid) {

        if (this.markInvalid != markInvalid) {
            this.markInvalid = markInvalid;

            if (eventHelper != null) {
                eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
                        this, ProgramControlType.OPTIONS_CHANGED));
            }
        }
    }

    /**
     * Getter count marked.
     * 
     * @return Count marked
     */
    @Deprecated
    public final boolean getCountMarked() {

        return countMarked;
    }

    /**
     * Setter count marked.
     * 
     * @param countMarked
     *            Count marked
     */
    @Deprecated
    public final void setCountMarked(final boolean countMarked) {

        if (this.countMarked != countMarked) {
            this.countMarked = countMarked;

            if (eventHelper != null) {
                eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
                        this, ProgramControlType.OPTIONS_CHANGED));
            }
        }
    }

    /**
     * Gets whether to play audio.
     * 
     * @return true, if audio should be played
     */
    @Deprecated
    public final boolean getPlayAudio() {

        return playAudio;
    }

    /**
     * Sets whether to play audio.
     * 
     * @param playAudio
     *            if audio should be played
     */
    @Deprecated
    public final void setPlayAudio(final boolean playAudio) {

        if (this.playAudio != playAudio) {
            this.playAudio = playAudio;

            if (eventHelper != null) {
                eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
                        this, ProgramControlType.OPTIONS_CHANGED));
            }
        }
    }

    /**
     * Gets if music should be played.
     * 
     * @return true, if music should be played.
     */
    public final boolean isPlayMusic() {

        return playMusic;
    }

    /**
     * Sets whether music should be played.
     * 
     * @param playMusic
     *            if music should be played.
     */
    public final void setPlayMusic(final boolean playMusic) {

        if (this.playMusic != playMusic) {
            this.playMusic = playMusic;

            if (eventHelper != null) {
                eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
                        this, ProgramControlType.OPTIONS_CHANGED));
            }
        }
    }

    /**
     * Gets if sound effects should be played.
     * 
     * @return true, if sound effects should be played.
     */
    public final boolean isPlayEffects() {

        return playEffects;
    }

    /**
     * Sets whether sound effects should be played.
     * 
     * @param playEffects
     *            if sound effects should be played.
     */
    public final void setPlayEffects(final boolean playEffects) {

        if (this.playEffects != playEffects) {
            this.playEffects = playEffects;

            if (eventHelper != null) {
                eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
                        this, ProgramControlType.OPTIONS_CHANGED));
            }
        }

    }

    /**
     * Gets whether to hide the play field while playing.
     * 
     * @return true, if play field should be hidden
     */
    public final boolean getHidePlayfield() {

        return hidePlayfield;
    }

    /**
     * Sets whether to hide the play field while playing.
     * 
     * @param hidePlayfield
     *            whether to hide play field
     */
    public final void setHidePlayfield(final boolean hidePlayfield) {

        if (this.hidePlayfield != hidePlayfield) {
            this.hidePlayfield = hidePlayfield;

            if (eventHelper != null) {
                eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
                        this, ProgramControlType.OPTIONS_CHANGED));
            }
        }
    }

    /**
     * Gets whether to automatically search for updates.
     * 
     * @return true, if updates should automatically searched for
     */
    public final boolean shouldSearchForUpdates() {

        return searchForUpdates;
    }

    /**
     * Sets whether to automatically search for updates.
     * 
     * @param searchForUpdates
     *            whether to automatically search for updates
     */
    public final void setSearchForUpdates(final boolean searchForUpdates) {

        if (this.searchForUpdates != searchForUpdates) {
            this.searchForUpdates = searchForUpdates;

            if (eventHelper != null) {
                eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
                        this, ProgramControlType.OPTIONS_CHANGED));
            }
        }
    }

    /**
     * Gets whether to activate chat via NonoWeb.
     * 
     * @return true, if chat should be activated
     */
    public final boolean shouldActivateChat() {

        return activateChat;
    }

    /**
     * Sets whether to activate chat via NonoWeb.
     * 
     * @param activateChat
     *            whether to activate chat
     */
    public final void setActivateChat(final boolean activateChat) {

        if (this.activateChat != activateChat) {
            this.activateChat = activateChat;

            if (eventHelper != null) {
                eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
                        this, ProgramControlType.OPTIONS_CHANGED));
            }
        }
    }

    /**
     * Gets if caption hints should be crossed out while playing.
     * 
     * @return true, if caption hints should be crossed out
     */
    public final boolean getCrossCaptions() {

        return crossCaptions;
    }

    /**
     * Sets if caption hints should be crossed out while playing.
     * 
     * @param crossCaptions
     *            if caption hints should be crossed out
     * 
     */
    public final void setCrossCaptions(final boolean crossCaptions) {

        if (this.crossCaptions != crossCaptions) {
            this.crossCaptions = crossCaptions;

            if (eventHelper != null) {
                eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
                        this, ProgramControlType.OPTIONS_CHANGED));
            }
        }
    }

    /**
     * Gets if complete rows and columns on the board should be marked.
     * 
     * @return true, if complete rows and columns should be marked
     */
    public final boolean getMarkCompleteRowsColumns() {

        return markCompleteRowsColumns;
    }

    /**
     * Sets if complete rows and columns on the board should be marked.
     * 
     * @param markCompleteRowsColumns
     *            if complete rows and columns should be marked
     * 
     */
    public final void setMarkCompleteRowsColumns(
            final boolean markCompleteRowsColumns) {

        if (this.markCompleteRowsColumns != markCompleteRowsColumns) {
            this.markCompleteRowsColumns = markCompleteRowsColumns;

            if (eventHelper != null) {
                eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
                        this, ProgramControlType.OPTIONS_CHANGED));
            }
        }
    }

    /**
     * Gets whether to show nonogram name in StatusField.
     * 
     * @return true, if nonogram name should be shown
     */
    public final boolean isShowNonogramName() {

        return showNonogramName;
    }

    /**
     * Sets whether to show nonogram name in StatusField.
     * 
     * @param showNonogramName
     *            if nonogram name should be shown
     */
    public final void setShowNonogramName(final boolean showNonogramName) {

        if (this.showNonogramName != showNonogramName) {

            this.showNonogramName = showNonogramName;

            if (eventHelper != null) {
                eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
                        this, ProgramControlType.OPTIONS_CHANGED));
            }
        }
    }

    /**
     * Returns chosen game mode.
     * 
     * @return currently chosen game mode
     */
    public final GameModeType getGameMode() {

        return gameMode;
    }

    /**
     * Sets chosen game mode.
     * 
     * @param gameMode
     *            currently chosen game mode
     */
    public final void setGameMode(final GameModeType gameMode) {

        if (this.gameMode != gameMode) {
            this.gameMode = gameMode;

            if (eventHelper != null) {
                eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
                        this, ProgramControlType.OPTIONS_CHANGED));
            }
        }
    }

    /**
     * Gets base color from which all other colors are derived.
     * 
     * @return base color
     */
    public final Color getBaseColor() {

        return baseColor;
    }

    /**
     * Sets base color from which all other colors are derived.
     * 
     * @param baseColor
     *            base color to be set
     */
    public final void setBaseColor(final Color baseColor) {

        if (!this.baseColor.equals(baseColor)) {

            this.baseColor = baseColor;
            this.currentColorModel = new ColorModelEvenlySpaced(baseColor);

            if (eventHelper != null) {
                eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
                        this, ProgramControlType.OPTIONS_CHANGED));
            }
        }
    }

    /**
     * Gets text color for all text fields in the program.
     * 
     * @return text color for all text fields
     */
    public final Color getTextColor() {

        return textColor;
    }

    /**
     * Sets text color for all text fields in the program.
     * 
     * @param textColor
     *            text color to be set for all text fields
     */
    public final void setTextColor(final Color textColor) {

        if (!this.textColor.equals(textColor)) {

            this.textColor = textColor;

            if (eventHelper != null) {
                eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
                        this, ProgramControlType.OPTIONS_CHANGED));
            }
        }
    }

    /**
     * Gets a color model containing different colors derived from a given base
     * color.
     * 
     * @return color model including all colors that should be used.
     */
    public final ColorModel getColorModel() {

        return currentColorModel;
    }

    /**
     * Gets game locale.
     * 
     * @return game locale
     */
    public final Locale getGameLocale() {

        return gameLocale;
    }

    /**
     * Sets game locale.
     * 
     * @param gameLocale
     *            game locale
     */
    public final void setGameLocale(final Locale gameLocale) {

        if (!this.gameLocale.equals(gameLocale)) {
            this.gameLocale = gameLocale;

            if (eventHelper != null) {
                eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
                        this, ProgramControlType.OPTIONS_CHANGED));
            }
        }
    }

    /**
     * Returns the set player name that is used for example in the high score
     * table. Default value for player name is the system property "user.name"
     * by the system.
     * 
     * @return player name
     */
    public final String getPlayerName() {

        return playerName;
    }

    /**
     * Sets player name that is used for example in the high score table.
     * Default value for player name is the system property "user.name" by the
     * system.
     * 
     * @param playerName
     *            player name to be set
     */
    public final void setPlayerName(final String playerName) {

        if (!this.playerName.equals(playerName)) {
            this.playerName = playerName;

            if (eventHelper != null) {
                eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
                        this, ProgramControlType.OPTIONS_CHANGED));
            }
        }
    }

    /**
     * Returns whether the user should every time be asked for a player name.
     * 
     * @return true, if user should every time be asked for a player name
     */
    public final boolean shouldAskForPlayerName() {

        return askForPlayerName;
    }

    /**
     * Sets option to ask user every time for a player name.
     * 
     * @param askForPlayerName
     *            if every time user should be asked for a player name
     */
    public final void setAskForPlayerName(final boolean askForPlayerName) {

        if (this.askForPlayerName != askForPlayerName) {
            this.askForPlayerName = askForPlayerName;

            if (eventHelper != null) {
                eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
                        this, ProgramControlType.OPTIONS_CHANGED));
            }
        }
    }

    /**
     * Gets key code for specific control like "left" or "mark".
     * 
     * @param control
     *            control defining a specific function in the game
     * 
     * @return key code for given control
     * @see ControlSettings
     */
    public final Integer getKeyCodeForControl(final Control control) {

        return controlSettings.getControl(control);
    }

    /**
     * Set key code for specific control to a given value.
     * 
     * @param control
     *            control for which key code should be set
     * @param keyCode
     *            key code to be set
     */
    public final void setControl(final Control control, final Integer keyCode) {

        final int oldKeyCode = controlSettings.getControl(control);
        if (oldKeyCode != keyCode) {
            controlSettings.setControl(control, keyCode);

            if (eventHelper != null) {
                eventHelper.fireOptionsChangedEvent(new ProgramControlEvent(
                        this, ProgramControlType.OPTIONS_CHANGED));
            }
        }
    }

    /**
     * Sets event helper for firing events.
     * 
     * @param eventHelper
     *            game event helper
     */
    public final void setEventHelper(final GameEventHelper eventHelper) {

        this.eventHelper = eventHelper;
    }
}
