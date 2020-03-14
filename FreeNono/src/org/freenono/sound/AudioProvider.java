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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.Logger;
import org.freenono.controller.Settings;
import org.freenono.event.FieldControlEvent;
import org.freenono.event.GameAdapter;
import org.freenono.event.GameEventHelper;
import org.freenono.event.ProgramControlEvent;
import org.freenono.event.ProgramControlEvent.ProgramControlType;
import org.freenono.event.StateChangeEvent;

/**
 * Provides audio services based on events fired by the game model.
 *
 * @author Christian Wichmann
 */
public class AudioProvider {

	private static Logger logger = Logger.getLogger(AudioProvider.class);

	private static final boolean PLAY_SFX_DEFAULT = false;
	private boolean playSFX = PLAY_SFX_DEFAULT;
	private static final boolean PLAY_MUSIC_DEFAULT = false;
	private boolean playMusic = PLAY_MUSIC_DEFAULT;

	private static final int VOLUME_SFX_DEFAULT = 200;
	private int volumeSFX = VOLUME_SFX_DEFAULT;
	private static final int VOLUME_MUSIC_DEFAULT = 200;
	private int volumeMusic = VOLUME_MUSIC_DEFAULT;

	private Settings settings = null;
	private List<String> bgMusicFiles = null;

	/**
	 * Defines all available types of sound effects. It is used as hash key to
	 * store file names and AudioPlayer instances in Maps.
	 */
	public enum SFXType {
		OCCUPY_SFX, FIELD_CHANGED_SFX, WRONGLY_OCCUPIED_SFX, GAME_OVER_SFX, GAME_WON_SFX
	};

	private final Map<SFXType, AudioPlayer> sfxPlayer = new HashMap<SFXType, AudioPlayer>();
	private final Map<SFXType, String> sfxFiles = new HashMap<SFXType, String>();
	private OggPlayer bgMusic = null;

	private GameEventHelper eventHelper = null;

	private final GameAdapter gameAdapter = new GameAdapter() {

		@Override
		public void occupyField(final FieldControlEvent e) {
			if (playSFX) {
				sfxPlayer.get(SFXType.OCCUPY_SFX).play();
			}
		}

		@Override
		public void markField(final FieldControlEvent e) {
		}

		@Override
		public void wrongFieldOccupied(final FieldControlEvent e) {
			if (playSFX) {
				sfxPlayer.get(SFXType.WRONGLY_OCCUPIED_SFX).play();
			}
		}

		@Override
		public void stateChanging(final StateChangeEvent e) {

			switch (e.getNewState()) {
			case GAME_OVER:
				if (playSFX) {
					sfxPlayer.get(SFXType.GAME_OVER_SFX).play();
				}
				if (playMusic) {
					stopBGMusic();
				}
				break;
			case SOLVED:
				if (playSFX) {
					sfxPlayer.get(SFXType.GAME_WON_SFX).play();
				}
				if (playMusic) {
					stopBGMusic();
				}
				break;
			case RUNNING:
				if (playMusic) {
					startBGMusic();
				}
				break;
			case USER_STOP:
				if (playMusic) {
					stopBGMusic();
				}
				break;
			case PAUSED:
				if (playMusic) {
					pauseBGMusic();
				}
				break;
			case NONE:
				break;
			default:
				assert false : e.getNewState();
				break;
			}

		}

		@Override
		public void programControl(final ProgramControlEvent e) {

			if (e.getPct() == ProgramControlType.QUIT_PROGRAMM) {
				closeAudio();
			}
		}

		@Override
		public void optionsChanged(final ProgramControlEvent e) {

			if (settings.isPlayMusic() != playMusic) {
				playMusic = settings.isPlayMusic();

				if (playMusic) {
					initAudio();

				} else {
					stopBGMusic();
				}
			}

			if (settings.isPlayEffects() != playSFX) {
				playSFX = settings.isPlayEffects();

				if (playSFX) {
					initAudio();

				} else {
					// stop all player for sound effects and clear list of
					// players
					for (final SFXType x : SFXType.values()) {
						if (sfxFiles.containsKey(x)) {
							sfxPlayer.get(x).closePlayer();
						}
					}
					sfxPlayer.clear();
				}
			}
		}
	};

	/**
	 * Instantiates the AudioProvider which initializes the audio system and
	 * listens to all events fired by the game to play appropriate sound.
	 *
	 * @param eventHelper Event helper to register GameAdapter.
	 * @param settings Settings for deciding whether to play sounds or not.
	 */
	public AudioProvider(final GameEventHelper eventHelper, final Settings settings) {

		if (eventHelper == null || settings == null) {
			throw new NullPointerException("At least one parameter not valid.");
		}

		setEventHelper(eventHelper);
		this.settings = settings;

		setPlaySFX(settings.isPlayEffects());
		setPlayMusic(settings.isPlayMusic());

		initData();
		initAudio();
	}

	/**
	 * Sets the current event helper and registers GameAdapter from this class.
	 *
	 * @param eventHelper Event helper to register GameAdapter.
	 */
	public final void setEventHelper(final GameEventHelper eventHelper) {

		this.eventHelper = eventHelper;
		eventHelper.addGameListener(gameAdapter);
	}

	/**
	 * Removes the GameAdapter from the event helper instance.
	 */
	public final void removeEventHelper() {

		if (eventHelper != null) {
			eventHelper.removeGameListener(gameAdapter);
			this.eventHelper = null;
		}
	}

	/**
	 * Sets all file names for background music as well as sound effects.
	 */
	private void initData() {

		// set filenames for all music and sound effect files
		bgMusicFiles = new ArrayList<String>();
		bgMusicFiles.add("/resources/music/theme_A.ogg");
		// bgMusicFiles.add("/resources/music/theme_B.ogg");

		sfxFiles.put(SFXType.OCCUPY_SFX, "/resources/sounds/occupy.ogg");
		sfxFiles.put(SFXType.WRONGLY_OCCUPIED_SFX, "/resources/sounds/wrongMove.ogg");
		sfxFiles.put(SFXType.GAME_OVER_SFX, "/resources/sounds/lose.ogg");
		sfxFiles.put(SFXType.GAME_WON_SFX, "/resources/sounds/applause.ogg");
	}

	/**
	 * Initializes AudioPlayer instances for background music as well as sound
	 * effects.
	 */
	private void initAudio() {

		// initialize WavPlayer for every effect in the game
		if (playSFX) {
			for (final SFXType x : SFXType.values()) {
				if (sfxFiles.containsKey(x)) {
					try {
						sfxPlayer.put(x, new OggPlayer(getClass().getResource(sfxFiles.get(x)), volumeSFX, false));

					} catch (final UnsupportedAudioFileException exception) {
						logger.debug(exception.getMessage());
					}
				}
			}
			assert !sfxPlayer.isEmpty();
		}

		// initialize background music as OggPLayer
		if (playMusic) {
			if (bgMusic == null) {
				Collections.shuffle(bgMusicFiles);
				final URL audioFile = getClass().getResource(bgMusicFiles.get(0));
				logger.debug("Try to instantiate ogg player with music file " + audioFile);
				try {
					bgMusic = new OggPlayer(audioFile, volumeMusic, true);

				} catch (final UnsupportedAudioFileException exception) {
					logger.debug(exception.getMessage());
				}
			}
			assert bgMusic != null;
		}
	}

	/**
	 * Starts playing background music.
	 */
	private void startBGMusic() {

		if (bgMusic != null) {
			bgMusic.play();
		}
	}

	/**
	 * Pauses background music.
	 */
	private void pauseBGMusic() {

		if (bgMusic != null) {
			bgMusic.pause();
		}
	}

	/**
	 * Stops background music.
	 */
	private void stopBGMusic() {

		if (bgMusic != null) {
			bgMusic.stop();
		}
	}

	/**
	 * Closes all AudioPlayer for sound effects and background music.
	 */
	public final void closeAudio() {

		// close all AudioPlayer for sound effects
		for (final AudioPlayer w : sfxPlayer.values()) {
			if (w != null) {
				w.closePlayer();
			}
		}

		// close AudioPlayer for background music
		if (bgMusic != null) {
			bgMusic.closePlayer();
		}
	}

	/**
	 * Finalizes this AudioProvider instance.
	 *
	 * @throws Throwable when super throws it.
	 */
	@Override
	protected final void finalize() throws Throwable {

		closeAudio();
		removeEventHelper();
	}

	/**
	 * Returns whether sound effects should be played.
	 *
	 * @return True, if sound effects should be played.
	 */
	public final boolean isPlaySFX() {

		return playSFX;
	}

	/**
	 * Sets if sound effects should be played.
	 *
	 * @param playSFX if sound effects should be played.
	 */
	public final void setPlaySFX(final boolean playSFX) {

		this.playSFX = playSFX;
	}

	/**
	 * Returns whether music should be played.
	 *
	 * @return True, if background music should be played.
	 */
	public final boolean isPlayMusic() {

		return playMusic;
	}

	/**
	 * Sets if background music should be player.
	 *
	 * @param playMusic If music should be played.
	 */
	public final void setPlayMusic(final boolean playMusic) {

		this.playMusic = playMusic;
	}

	/**
	 * Returns the current volume for sound effects.
	 *
	 * @return Volume for sound effects.
	 */
	public final int getVolumeSFX() {

		return volumeSFX;
	}

	/**
	 * Sets volume for sound effects.
	 *
	 * @param volumeSFX the volume to which the sound effects are set between 0
	 * and 255
	 */
	public final void setVolumeSFX(final int volumeSFX) {

		if (volumeSFX < 0 || volumeSFX > 255) {
			throw new IllegalArgumentException("Volume setting not valid.");
		}

		this.volumeSFX = volumeSFX;
	}

	/**
	 * Returns the current volume for background music.
	 *
	 * @return Volume for background music.
	 */
	public final int getVolumeMusic() {

		return volumeMusic;
	}

	/**
	 * Sets volume for background music.
	 *
	 * @param volumeMusic the volume to which the background music is set
	 * between 0 and 255
	 */
	public final void setVolumeMusic(final int volumeMusic) {

		if (volumeMusic < 0 || volumeMusic > 255) {
			throw new IllegalArgumentException("Volume setting not valid.");
		}

		this.volumeMusic = volumeMusic;
	}
}
