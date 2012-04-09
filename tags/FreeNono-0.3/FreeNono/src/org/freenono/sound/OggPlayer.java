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

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.Logger;

public class OggPlayer extends AudioPlayer {

	private static Logger logger = Logger.getLogger(OggPlayer.class);

	private AudioInputStream in = null;
	private AudioInputStream din = null;
	private AudioFormat decodedFormat = null;
	private SourceDataLine line = null;
	
	private static boolean continuePlaying = false;
	private Thread playThread = null;
	
	
	public OggPlayer(URL oggFile, int volume) {

		setVolume(volume);

		openSoundFile(oggFile);
	}
	
	
	public void openSoundFile(URL soundFile) {
		
		this.soundFile = soundFile;
		openFile();
	}
	
	private void openFile() {

		try {
			// Get AudioInputStream from given file.
			in = AudioSystem.getAudioInputStream(soundFile);
			din = null;
			if (in != null) {
				AudioFormat baseFormat = in.getFormat();
				decodedFormat = new AudioFormat(
						AudioFormat.Encoding.PCM_SIGNED,
						baseFormat.getSampleRate(), 16,
						baseFormat.getChannels(), baseFormat.getChannels() * 2,
						baseFormat.getSampleRate(), false);
				// Get AudioInputStream that will be decoded by underlying
				// VorbisSPI
				din = AudioSystem.getAudioInputStream(decodedFormat, in);

				// get line for ogg output
				line = getLine(decodedFormat);
			}
		} catch (UnsupportedAudioFileException e) {

			logger.error("Could not open audio file because its format is not supported.");
		} catch (IOException e) {

			logger.error("Unable to access ogg file for background music.");

		} catch (LineUnavailableException e) {

			logger.error("No audio line available for playback of background music.");
		}
	}

	private void rawplay(AudioInputStream din)
			throws LineUnavailableException, IOException {
		
		byte[] data = new byte[4096];
		if (line != null) {
			// Start
			line.start();
			int nBytesRead = 0, nBytesWritten = 0;
			while (nBytesRead != -1) {
				nBytesRead = din.read(data, 0, data.length);
				if (nBytesRead != -1)
					nBytesWritten = line.write(data, 0, nBytesRead);
				
				// stop writing to audio stream if variable is false
				if (!continuePlaying) {
					line.stop();
					line.flush();
					break;
				}
			}	
			line.close();
		}
	}

	private SourceDataLine getLine(AudioFormat audioFormat)
			throws LineUnavailableException {
		
		SourceDataLine res = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class,
				audioFormat);
		res = (SourceDataLine) AudioSystem.getLine(info);
		res.open(audioFormat);
		return res;
	}

	@Override
	public void playSoundFile() {
		
		openFile();

		if (playThread == null) {
			playThread = new Thread() {
				public void run() {
					try {
						rawplay(din);
					} catch (IOException e) {

						logger.error("Could not read audio file!");
					} catch (LineUnavailableException e) {

						logger.error("No line with neccesary line format available!");
					}
				}
			};
			playThread.start();
		}

		continuePlaying = true;
	}

	@Override
	public void stopSoundFile() {

		continuePlaying = false;
		closeLine();
		
		playThread = null;
		din = null;
		in = null;
	}
	
	public void pauseSoundFile() {
		continuePlaying = false;
	}

	public void resumeSoundFile() {
		continuePlaying = true;
	}
	
	@Override
	public void closeLine() {

		continuePlaying = false;

		try {
			if (in != null)
				in.close();
			if (din != null)
				din.close();
		} catch (IOException e) {

			logger.warn("A problem occurred during closing of audio file.");
		}
		
		line.close();
	}

}
