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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
 * Loads and plays an ogg file.
 * 
 * @author Christian Wichmann
 */
public class OggPlayer extends AudioPlayer {

    private static Logger logger = Logger.getLogger(OggPlayer.class);

    private AudioInputStream din = null;
    private AudioInputStream in = null;
    private AudioFormat decodedFormat = null;
    private SourceDataLine line = null;
    private FloatControl volumeCtrl = null;
    private byte[] rawData;
    private final int bytesPerCycle = 32768;

    private volatile boolean playbackPaused = true;
    private volatile boolean playbackStopped = true;
    private volatile boolean doLoop = true;
    private Runnable audioDataConsumer = null;
    private Runnable audioDataProducer = null;

    private static final ExecutorService AUDIO_EXECUTOR = Executors
            .newFixedThreadPool(4);
    private final BlockingQueue<byte[]> playingQueue = new ArrayBlockingQueue<byte[]>(
            4);

    private final Lock lock = new ReentrantLock();
    private final Condition doPlay = lock.newCondition();

    /**
     * Instantiates a class to play a specific audio file with the given volume.
     * 
     * @param oggFile
     *            File that should be played.
     * @param volume
     *            Volume for playing this file.
     * @param loop
     *            If play back should be looped when end of file is reached.
     * @throws UnsupportedAudioFileException
     *             if given file has wrong file format
     */
    public OggPlayer(final URL oggFile, final int volume, final boolean loop)
            throws UnsupportedAudioFileException {

        this.doLoop = loop;
        setVolume(volume);

        if (isCorrectFileFormat(oggFile)) {
            openFile(oggFile);
        } else {
            throw new UnsupportedAudioFileException("Can not load " + oggFile
                    + " because file format not supported.");
        }

        createPlayThread();
    }

    /**
     * Checks whether given file name has the correct type. On factor to check
     * is the file extension, another the mime type of the file.
     * 
     * @param pathToFile
     *            path of sound file
     * @return true, if file has the correct file format.
     */
    private boolean isCorrectFileFormat(final URL pathToFile) {

        // TODO use mime type with Java 7: Files.probeContentType(path)

        File file = new File(pathToFile.getFile());
        String extension = "";
        String filename = file.getName();

        int i = filename.lastIndexOf('.');

        if (i > 0 && i < filename.length() - 1) {
            extension = filename.substring(i + 1).toLowerCase();
        }

        // logger.debug("Mime Type of " + filename + " is "
        // + new MimetypesFileTypeMap().getContentType(file));

        return extension.equals("ogg");
    }

    /**
     * Opens sound file, prepares a line for its audio format and loads audio
     * data into byte array.
     * 
     * @param soundFile
     *            File that should be loaded.
     */
    private void openFile(final URL soundFile) {

        setSoundFile(soundFile);

        try {
            prepareLine();
            readAudioData();

        } catch (LineUnavailableException e) {
            logger.error("No audio line available for playback of background music.");

        } catch (UnsupportedAudioFileException e) {
            logger.error("Could not open audio file because its format is not supported.");

        } catch (IOException e) {
            logger.error("Unable to access ogg file for background music.");

        } finally {
            closeFile();
        }
    }

    /**
     * Opens the audio file and prepares a line for the data formats in it.
     * 
     * @throws LineUnavailableException
     *             when no appropriated line is available.
     * @throws UnsupportedAudioFileException
     *             when audio file has wrong file format.
     * @throws IOException
     *             when audio file could not be read.
     */
    private void prepareLine() throws LineUnavailableException,
            UnsupportedAudioFileException, IOException {

        final int sampleSizeInBits = 16;

        // get AudioInputStream from given file.
        in = AudioSystem.getAudioInputStream(getSoundFile());

        if (in != null) {
            AudioFormat baseFormat = in.getFormat();

            decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(), sampleSizeInBits,
                    baseFormat.getChannels(), baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(), false);

            // get AudioInputStream that will be decoded by underlying
            // VorbisSPI
            din = AudioSystem.getAudioInputStream(decodedFormat, in);

            // get open line for ogg output
            DataLine.Info info = new DataLine.Info(SourceDataLine.class,
                    decodedFormat);
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(decodedFormat);

            // set volume for line
            if (line.isControlSupported(FloatControl.Type.VOLUME)) {
                volumeCtrl = (FloatControl) line
                        .getControl(FloatControl.Type.VOLUME);
                volumeCtrl.setValue(getVolume() * VOLUME_MAX);
            }

            logger.info("Setting volume for playback of " + getSoundFile()
                    + " to " + getVolume());
        }
    }

    /**
     * Reads all data from audio file and stores it in a byte array.
     * 
     * @throws IOException
     *             when audio file can not be read from.
     */
    private void readAudioData() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        boolean readFurther = true;
        final int blockSize = 1024;

        byte[] tempBuffer = new byte[blockSize];

        /*
         * Read bytes from audio input stream and store it in byte array.
         * Do-while necessary because it is not guaranteed that read() does read
         * all data from stream.
         */
        do {
            int n = din.read(tempBuffer);

            if (n < 0) {
                readFurther = false;
            } else {
                baos.write(tempBuffer, 0, n);
            }
        } while (readFurther);

        // store data in final byte array
        rawData = baos.toByteArray();
    }

    /**
     * Closes file streams of audio file.
     */
    private void closeFile() {

        try {
            if (din != null) {
                din.close();
            }
        } catch (IOException e) {

            logger.warn("A problem occurred during closing of audio file.");
        }
    }

    /**
     * Creates a new thread to stream data to audio line if it does not already
     * exist.
     */
    private void createPlayThread() {

        audioDataConsumer = new Runnable() {
            @Override
            public void run() {
                logger.debug("Playback thread started...");
                streamToLine();
            }
        };

        audioDataProducer = new Runnable() {
            @Override
            public void run() {
                logger.debug("Audio data producer thread started...");
                provideAudioForStreaming();
            }
        };

        AUDIO_EXECUTOR.execute(audioDataConsumer);
        AUDIO_EXECUTOR.execute(audioDataProducer);
    }

    @Override
    public final void play() {

        playbackPaused = false;
        playbackStopped = false;

        lock.lock();
        try {
            doPlay.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final void stop() {

        playbackStopped = true;
        playbackPaused = true;
    }

    @Override
    public final void pause() {

        playbackPaused = true;
    }

    /**
     * Streams audio data to line when playback is not paused or stopped. Used
     * by play thread.
     */
    private void streamToLine() {

        line.stop();
        line.flush();
        line.start();
        while (true) {
            byte[] tmp = null;
            try {
                tmp = playingQueue.take();
            } catch (InterruptedException e) {
                logger.warn("Waiting on playing queue was interrupted.");
            }
            if (tmp != null) {
                line.write(tmp, 0, tmp.length);
            }
        }
    }

    /**
     * Provides audio data that is put into the blocking queue between this
     * producer thread and the consumer thread.
     */
    private void provideAudioForStreaming() {

        int audioDataPosition = 0;
        while (true) {
            while (playbackStopped) {
                audioDataPosition = 0;
                lock.lock();
                try {
                    doPlay.await();
                } catch (InterruptedException e) {
                    logger.warn("Interrupted!");
                } finally {
                    lock.unlock();
                }
            }

            while (playbackPaused) {
                lock.lock();
                try {
                    doPlay.await();
                } catch (InterruptedException e) {
                    logger.warn("Interrupted!");
                } finally {
                    lock.unlock();
                }
            }

            /*
             * Calculate how much bytes can still be send to audio line and
             * write byte block to it.
             */
            byte[] tmp;
            int residualBytes = rawData.length - audioDataPosition;
            if (residualBytes > bytesPerCycle) {
                tmp = Arrays.copyOfRange(rawData, audioDataPosition,
                        audioDataPosition + bytesPerCycle);
                audioDataPosition += bytesPerCycle;
            } else {
                tmp = Arrays.copyOfRange(rawData, audioDataPosition,
                        audioDataPosition + residualBytes);
                audioDataPosition += residualBytes;
            }

            /*
             * Put byte array into blocking queue to be read by playing thread.
             */
            try {
                playingQueue.put(tmp);
            } catch (InterruptedException e) {
                logger.warn("Waiting on playing queue was interrupted.");
            }

            /*
             * Find if data has ended and playback should either be stopped or
             * started again at the beginning.
             */
            if (audioDataPosition >= rawData.length) {
                if (doLoop) {
                    audioDataPosition = 0;
                } else {
                    playbackPaused = true;
                }
            }
        }
    }

    @Override
    public final void closePlayer() {

        stop();

        if (line != null) {
            line.stop();
            line.flush();
            line.close();
            line = null;
        }

        AUDIO_EXECUTOR.shutdown();
    }
}
