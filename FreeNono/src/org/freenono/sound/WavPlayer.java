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

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.Logger;


/**
 * Loads and plays a wav file.
 * 
 * @author Christian Wichmann
 */
public class WavPlayer extends AudioPlayer {

	private static Logger logger = Logger.getLogger(WavPlayer.class);

	private URL soundFile = null;
	private AudioInputStream audioInputStream = null;
	private AudioFormat audioFormat = null;
	private SourceDataLine sourceDataLine = null;
	private float volume = 1;
	
	private static boolean continuePlaying = false;
	private Thread playThread = null;

	
	public WavPlayer(URL wavFile, int volume) {

		setVolume(volume);
		
		openSoundFile(wavFile);
	}

	public void openSoundFile(URL soundFile) {

		this.soundFile = soundFile;
		openFile();
	}

	private void openFile() {

		try {

			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
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


	@Override
	public void play() {

		// reset audio system to start
		stop();
		
		try {

			audioInputStream = AudioSystem.getAudioInputStream(soundFile);

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
		// mark thread as daemon so the VM exits when this thread still runs!  
		playThread.setDaemon(true);
		playThread.start();
		
		continuePlaying = true;
	}
	
	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		
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
				if (sourceDataLine != null)
					sourceDataLine.write(tempBuffer, 0, cnt);
			}

			// stop writing to audio stream if variable is false
			if (!continuePlaying) {
				sourceDataLine.flush();
				return;
			}
		}
	}

	
	public void closePlayer() {

		try {
			
			audioInputStream.close();
			
		} catch (Exception e) {
			
			logger.error("Could not close audio file!");
		}
	}
	
	protected void finalize() throws Throwable {
		
		closePlayer();
		super.finalize();
	}

}
