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
package org.freenono.sound;

import java.net.URL;

public abstract class AudioPlayer {

	public URL soundFile = null;
	
	public int volume = 127;

	
	public abstract void play();

	public abstract void stop();
	
	public abstract void pause();

	public abstract void closePlayer();

	
	public URL getSoundFile() {
		return soundFile;
	}

	public void setSoundFile(URL wavFile) {
		this.soundFile = wavFile;
	}

	
	/**
	 * Get the volume for this AudioPlayer.
	 * 
	 * @return the volume as integer between 0 and 255 
	 */
	public int getVolume() {
		
		return volume;
	}

	/**
	 * Set the volume for this AudioPlayer. It is up to the subclasses of this
	 * class to decide when volume is actually set for the audio output! 
	 * 
	 * @param volume
	 *            the volume to set as integer between 0 and 255
	 */
	public void setVolume(int volume) {
		
		if (volume < 0 || volume > 255) {
			throw new IndexOutOfBoundsException("Volume has to be between 0 and 255.");
		}
		
		this.volume = volume;
	}

}
