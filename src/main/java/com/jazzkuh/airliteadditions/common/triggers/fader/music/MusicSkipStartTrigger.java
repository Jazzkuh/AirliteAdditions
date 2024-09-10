package com.jazzkuh.airliteadditions.common.triggers.fader.music;

import com.jazzkuh.airliteadditions.AirliteAdditions;
import com.jazzkuh.airliteadditions.common.framework.button.ControlButton;
import com.jazzkuh.airliteadditions.common.framework.button.ControlLedColor;
import com.jazzkuh.airliteadditions.common.framework.trigger.TriggerAction;
import com.jazzkuh.airliteadditions.utils.music.MusicEngine;
import lombok.SneakyThrows;

public class MusicSkipStartTrigger extends TriggerAction {
	@Override
	@SneakyThrows
	public void process() {
		MusicEngine musicEngine = AirliteAdditions.getInstance().getMusicEngine();
		if (musicEngine.isPlaying()) {
			AirliteAdditions.getUdpServer().writeStaticLed(ControlButton.LED_1A, ControlLedColor.RED);
			musicEngine.playPause();
		}

		AirliteAdditions.getUdpServer().writeStaticLed(ControlButton.LED_1A, ControlLedColor.GREEN);
		if (AirliteAdditions.getInstance().getShouldSkipOnStart()) {
			musicEngine.next();
		} else if (!musicEngine.isPlaying()) {
			musicEngine.playPause();
		}


		if (!musicEngine.isPlaying() && musicEngine.getProvider() == MusicEngine.MusicEngineProvider.APPLE_MUSIC) {
			musicEngine.playPause();
		}
	}
}
