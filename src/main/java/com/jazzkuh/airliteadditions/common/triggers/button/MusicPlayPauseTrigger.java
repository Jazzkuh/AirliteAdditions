package com.jazzkuh.airliteadditions.common.triggers.button;

import com.jazzkuh.airliteadditions.AirliteAdditions;
import com.jazzkuh.airliteadditions.common.framework.button.ControlButton;
import com.jazzkuh.airliteadditions.common.framework.button.ControlLedColor;
import com.jazzkuh.airliteadditions.common.framework.trigger.TriggerAction;
import com.jazzkuh.airliteadditions.utils.music.MusicEngine;
import lombok.SneakyThrows;

public class MusicPlayPauseTrigger extends TriggerAction {
	private final MusicEngine musicEngine = AirliteAdditions.getInstance().getMusicEngine();

	@Override
	@SneakyThrows
	public void process() {
		if (musicEngine.isPlaying()) {
			//AirliteAdditions.getUdpServer().writeStaticLed(ControlButton.LED_1A, ControlLedColor.RED);
			musicEngine.playPause();
		} else {
			//AirliteAdditions.getUdpServer().writeStaticLed(ControlButton.LED_1A, ControlLedColor.GREEN);
			musicEngine.playPause();
		}
	}

	@Override
	public void startActions() {
		if (musicEngine.isPlaying()) {
			AirliteAdditions.getUdpServer().writeStaticLed(ControlButton.LED_1A, ControlLedColor.GREEN);
		} else {
			AirliteAdditions.getUdpServer().writeStaticLed(ControlButton.LED_1A, ControlLedColor.RED);
		}
	}
}
