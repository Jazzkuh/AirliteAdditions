package com.jazzkuh.airliteadditions.common.triggers.fader.music;

import com.jazzkuh.airliteadditions.AirliteAdditions;
import com.jazzkuh.airliteadditions.common.framework.trigger.TriggerAction;
import com.jazzkuh.airliteadditions.utils.music.MusicEngine;
import lombok.SneakyThrows;

public class CueOffTrigger extends TriggerAction {
	@Override
	@SneakyThrows
	public void process() {
		MusicEngine musicEngine = AirliteAdditions.getInstance().getMusicEngine();
		if (musicEngine.isPlaying()) {
			musicEngine.playPause();

			if (musicEngine.getSpotifyAPI().getPosition() < 3000) return;
			musicEngine.previous();
		}
	}
}
