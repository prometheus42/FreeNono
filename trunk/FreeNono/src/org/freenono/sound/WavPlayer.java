/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2010 Markus Wichmann
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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.Logger;

public class WavPlayer {

	private static Logger logger = Logger.getLogger(WavPlayer.class);

	private URL wavFile = null;
	private AudioInputStream audioInputStream = null;
	private AudioFormat audioFormat = null;
	private SourceDataLine sourceDataLine = null;
	private float volume = 1;
	
	private static boolean continuePlaying = false;
	private Thread playThread = null;

	
	public WavPlayer(URL wavFile, int volume) {

		this.wavFile = wavFile;
		setVolume(volume);
		openWAV();
	}

	
	public void openWAV() {

		try {

			audioInputStream = AudioSystem.getAudioInputStream(wavFile);
			audioFormat = audioInputStream.getFormat();
			
			// define line information based on line type,
			// encoding and frame sizes of audio file
			DataLine.Info dataLineInfo = new DataLine.Info(
					SourceDataLine.class, audioFormat, 
					audioFormat.getFrameSize() * 2);
					//audioInputStream.getFrameLength()));


			// make sure sound system supports data line
			if (!AudioSystem.isLineSupported(dataLineInfo)) {
				logger.error("Unsupported audio line format!");
				return;
			}
			
			// get source data line for playback of sound effect
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(audioFormat);
			
			// adjust the volume on the output line.
			if (sourceDataLine
					.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
				FloatControl volumeControl = (FloatControl) sourceDataLine
						.getControl(FloatControl.Type.MASTER_GAIN);
				volumeControl.setValue(volume);
			}
			sourceDataLine.start();

		} catch (LineUnavailableException e) {
			
			logger.error("No line with neccesary line format available!");
			
		} catch (UnsupportedAudioFileException e) {
			
			logger.error("Unsupported audio line format!");
			
		} catch (IOException e) {
			
			logger.error("Could not read audio file!");
		}

	}


	public void playWAV() {

		// reset audio system to start
		stopWAV();
		
		try {

			audioInputStream = AudioSystem.getAudioInputStream(wavFile);

		} catch (IOException e) {

			logger.error("Could not read audio file!");
			return;

		} catch (UnsupportedAudioFileException e) {

			logger.error("Unsupported audio format!");
			return;

		}
		
		playThread = new Thread() {
			public void run() {
				try {
					writeAudioStream();
				} catch (IOException e) {

					logger.error("Could not read audio file!");
				}
			}
		};
		playThread.start();
		
		continuePlaying = true;
	}
	
	private void stopWAV() {
		
		continuePlaying = false;
		
		// if (playThread != null)
		// playThread.stop();
	}

	private void writeAudioStream() throws IOException {
		
		int cnt;
		byte[] tempBuffer = new byte[64];

		while ((cnt = audioInputStream.read(tempBuffer, 0,
				tempBuffer.length)) != -1) {
			
			if (cnt > 0) {
				// Write data to the internal buffer of the data line
				// where it will be delivered to the speaker.
				sourceDataLine.write(tempBuffer, 0, cnt);
			}

			// stop writing to audio stream if variable is false
			if (!continuePlaying) {
				sourceDataLine.flush();
				return;
			}
		}
	}

	public void closeLines() {

		try {
			
			audioInputStream.close();
			
		} catch (Exception e) {
			
			logger.error("Could not read audio file!");
		}
	}

	
	@Override
	protected void finalize() throws Throwable {
		closeLines();
		super.finalize();
	}

	
	public URL getWavFile() {
		return wavFile;
	}

	public void setWavFile(URL wavFile) {
		this.wavFile = wavFile;
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
