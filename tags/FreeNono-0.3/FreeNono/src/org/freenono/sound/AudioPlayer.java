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
	public float volume = 1;

	public abstract void playSoundFile();

	public abstract void stopSoundFile();

	public abstract void closeLine();

	public URL getSoundFile() {
		return soundFile;
	}

	public void setSoundFile(URL wavFile) {
		this.soundFile = wavFile;
	}

	/**
	 * @return the volume
	 */
	public int getVolume() {
		return (int) (volume * 255);
	}

	/**
	 * @param volume
	 *            the volume to set as int between 0 and 255
	 */
	public void setVolume(int volume) {
		this.volume = (float) volume / 255;
	}

}