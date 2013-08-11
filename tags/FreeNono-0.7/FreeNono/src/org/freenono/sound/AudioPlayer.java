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
package org.freenono.sound;

import java.net.URL;

/**
 * 
 * @author Christian Wichmann
 */
public abstract class AudioPlayer {

    private URL soundFile = null;

    /**
     * Default value for volume.
     */
    public static final int VOLUME_DEFAULT = 127;

    /**
     * Maximum value for volume.
     */
    public static final int VOLUME_MAX = 255;
    
    private int volume = VOLUME_DEFAULT;

    /**
     * Plays the given sound file. If this method is invoked when the audio file
     * is already played, it starts again from the beginning.
     */
    public abstract void play();

    /**
     * Stops play back of sound file. After this call the play back starts at
     * the beginning of the file.
     */
    public abstract void stop();

    /**
     * Pauses play back of sound file.
     */
    public abstract void pause();

    /**
     * Closes the player and all open streams.
     */
    public abstract void closePlayer();

    /**
     * Returns the given sound file.
     * 
     * @return Sound file, that was given for this AudioPlayer.
     */
    public final URL getSoundFile() {

        return soundFile;
    }

    /**
     * Sets the sound file for this AudioPlayer.
     * 
     * @param wavFile
     *            File, that should be played by AudioPlayer.
     */
    public final void setSoundFile(final URL wavFile) {
        this.soundFile = wavFile;
    }

    /**
     * Get the volume for this AudioPlayer.
     * 
     * @return the volume as integer between 0 and 255
     */
    public final int getVolume() {

        return volume;
    }

    /**
     * Set the volume for this AudioPlayer. It is up to the subclasses of this
     * class to decide when volume is actually set for the audio output!
     * 
     * @param volume
     *            the volume to set as integer between 0 and 255
     */
    public final void setVolume(final int volume) {

        if (volume < 0 || volume > VOLUME_MAX) {

            throw new IndexOutOfBoundsException(
                    "Volume has to be between 0 and 255.");
        }

        this.volume = volume;
    }

}
