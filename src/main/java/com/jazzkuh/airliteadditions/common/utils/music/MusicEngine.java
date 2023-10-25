package com.jazzkuh.airliteadditions.common.utils.music;

import com.tagtraum.macos.music.Application;
import com.tagtraum.macos.music.Epls;
import de.labystudio.spotifyapi.SpotifyAPI;
import de.labystudio.spotifyapi.SpotifyAPIFactory;
import de.labystudio.spotifyapi.SpotifyListener;
import de.labystudio.spotifyapi.model.MediaKey;
import lombok.Getter;

public class MusicEngine {
	private @Getter SpotifyAPI spotifyAPI;
	private @Getter Application appleMusic;

	private @Getter MusicEngineProvider provider;

	public MusicEngine(MusicEngineProvider provider) {
		this.spotifyAPI = SpotifyAPIFactory.create();
		this.spotifyAPI.registerListener(new SpotifyEventListener());
		this.spotifyAPI.initialize();

		this.appleMusic = Application.getInstance();
		this.provider = provider;
	}

	public void playPause() {
		switch (this.provider) {
			case SPOTIFY -> spotifyAPI.pressMediaKey(MediaKey.PLAY_PAUSE);
			case APPLE_MUSIC -> appleMusic.playpause();
		}
	}

	public void next() {
		switch (this.provider) {
			case SPOTIFY -> spotifyAPI.pressMediaKey(MediaKey.NEXT);
			case APPLE_MUSIC -> appleMusic.nextTrack();
		}
	}

	public void previous() {
		switch (this.provider) {
			case SPOTIFY -> spotifyAPI.pressMediaKey(MediaKey.PREV);
			case APPLE_MUSIC -> appleMusic.previousTrack();
		}
	}

	public boolean isPlaying() {
		switch (this.provider) {
			case SPOTIFY -> {
				return spotifyAPI.isPlaying();
			}
			case APPLE_MUSIC -> {
				return appleMusic.getPlayerState() == Epls.PLAYING;
			}
		}

		return false;
	}

	public enum MusicEngineProvider {
		SPOTIFY,
		APPLE_MUSIC
	}
}
