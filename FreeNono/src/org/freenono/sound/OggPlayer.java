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
import javax.sound.sampled.FloatControl;
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
	private FloatControl volumeCtrl = null;
	
	// some lock somewhere...
	private Object lock = new Object();
	private volatile boolean playbackPaused = true;
	private Thread playThread = null;
	
	
	public OggPlayer(URL oggFile, int volume) {

		setVolume(volume);
		openFile(oggFile);
	}
	
	
	public void openFile(URL soundFile) {
		
		this.soundFile = soundFile;
		//openFile();
	}
	

	private void prepareLine() {

		closeFile();
		
		try {
			
			// get AudioInputStream from given file.
			in = AudioSystem.getAudioInputStream(soundFile);
			din = null;
			
			if (in != null) {
				
				AudioFormat baseFormat = in.getFormat();
				decodedFormat = new AudioFormat(
						AudioFormat.Encoding.PCM_SIGNED,
						baseFormat.getSampleRate(), 16,
						baseFormat.getChannels(), baseFormat.getChannels() * 2,
						baseFormat.getSampleRate(), false);
				
				// get AudioInputStream that will be decoded by underlying VorbisSPI
				din = AudioSystem.getAudioInputStream(decodedFormat, in);

				// get open line for ogg output
				DataLine.Info info = new DataLine.Info(SourceDataLine.class,
						decodedFormat);
				line = (SourceDataLine) AudioSystem.getLine(info);
				line.open(decodedFormat);

				// set volume for line
				volumeCtrl = (FloatControl) line
						.getControl(FloatControl.Type.VOLUME);
				volumeCtrl.setValue(volume*256);

				logger.info("Setting volume for playback of " + soundFile
						+ " to " + volume);
			}
		} catch (UnsupportedAudioFileException e) {

			logger.error("Could not open audio file because its format is not supported.");
		} catch (IOException e) {

			logger.error("Unable to access ogg file for background music.");

		} catch (LineUnavailableException e) {

			logger.error("No audio line available for playback of background music.");
		}
		
		// create thread for audio output
		createPlayThread();
	}
	
	
	private void closeFile() {

		try {
			if (in != null)
				in.close();
			if (din != null)
				din.close();
		} catch (IOException e) {

			logger.warn("A problem occurred during closing of audio file.");
		}
		
		//din = null;
		//in = null;
	}

	private void createPlayThread() {
		
		if (playThread == null) {
			
			playThread = new Thread() {
				
				public void run() {
					try {
						streamToLine(din);
					} catch (IOException e) {

						logger.error("Could not read audio file!");
					} catch (LineUnavailableException e) {

						logger.error("No line with neccesary line format available!");
					}
				}
			};
			// mark thread as daemon so the VM exits when this thread still runs!  
			playThread.setDaemon(true);
			playThread.start();
		}
	}

	@Override
	public void play() {
		
		// if thread is not already running open file and d
		if (playThread == null) {
			prepareLine();
		}
		
		synchronized(lock) {
			
			playbackPaused = false;
			lock.notifyAll();
		}
	}

	@Override
	public void stop() {

		playbackPaused = true;
		
		closePlayer();
		
		if (playThread != null) {
			playThread.interrupt();
			playThread = null;
		}
		
		closeFile();
	}
	
	@Override
	public void pause() {
		
		playbackPaused = true;
	}
	

	private void streamToLine(AudioInputStream din)
			throws LineUnavailableException, IOException {
		
		byte[] data = new byte[4096];
		int nBytesRead = 0, nBytesWritten = 0;
		
		logger.debug("Playback thread started and waiting...");

		if (line != null) {
			
			// Start
			line.start();
			
			// following code taken from: http://www.javalobby.org/java/forums/t18465.html
			synchronized (lock) {
				
				while ((nBytesRead = din.read(data, 0, data.length)) != -1) {
					
					while (playbackPaused) {						

						if(line.isRunning()) {
							line.stop();
						}
						try {
							lock.wait();
						}
						catch(InterruptedException e) {
						}
					}
						
					if(line.isOpen() && !line.isRunning()) {
						line.start();
					}
					nBytesWritten = line.write(data, 0, nBytesRead);
				}
			}
			
			// Stop
			line.stop();
			line.flush();
			line.close();
		}
	}
	
	@Override
	public void closePlayer() {

		closeFile();
		line.close();
	}

}
