package com.jazzkuh.airliteadditions.triggers.music;

import com.jazzkuh.airliteadditions.AirliteAdditions;
import com.jazzkuh.airliteadditions.framework.AirliteFaderStatus;
import com.jazzkuh.airliteadditions.framework.trigger.ChannelTriggerAction;
import com.jazzkuh.airliteadditions.utils.music.MusicEngine;
import lombok.SneakyThrows;

public class MusicPlayPauseTrigger extends ChannelTriggerAction {
	@Override
	@SneakyThrows
	public void process(AirliteFaderStatus airliteFaderStatus) {
		MusicEngine musicEngine = AirliteAdditions.getInstance().getMusicEngine();
		musicEngine.playPause();
	}
}
