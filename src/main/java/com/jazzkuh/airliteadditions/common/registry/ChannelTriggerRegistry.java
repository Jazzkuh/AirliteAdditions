package com.jazzkuh.airliteadditions.common.registry;

import com.jazzkuh.airliteadditions.common.framework.trigger.TriggerAction;
import com.jazzkuh.airliteadditions.common.framework.channel.ChannelTrigger;
import com.jazzkuh.airliteadditions.common.framework.trigger.TriggerType;
import com.jazzkuh.airliteadditions.common.triggers.fader.OnAirLightTrigger;
import com.jazzkuh.airliteadditions.common.triggers.fader.RegularLightTrigger;
import com.jazzkuh.airliteadditions.common.triggers.fader.music.MusicPauseTrigger;
import com.jazzkuh.airliteadditions.common.triggers.fader.music.MusicSkipStartTrigger;

import java.util.HashMap;
import java.util.Map;

public class ChannelTriggerRegistry {
	private static Map<ChannelTrigger, Class<? extends TriggerAction>> triggers = new HashMap<>();

	static {
		registerAction(new ChannelTrigger(-1, TriggerType.MICROPHONE_ON), OnAirLightTrigger.class);
		registerAction(new ChannelTrigger(-1, TriggerType.MICROPHONE_OFF), RegularLightTrigger.class);
		registerAction(new ChannelTrigger(8, TriggerType.FADER_AND_CHANNEL_ON), MusicSkipStartTrigger.class);
		registerAction(new ChannelTrigger(8, TriggerType.CHANNEL_OFF), MusicPauseTrigger.class);
		registerAction(new ChannelTrigger(8, TriggerType.FADER_OFF), MusicPauseTrigger.class);
	}

	public static void registerAction(ChannelTrigger channelTrigger, Class<? extends TriggerAction> triggerClass) {
		triggers.put(channelTrigger, triggerClass);
	}

	public static TriggerAction getAction(ChannelTrigger channelTrigger) {
		Class<? extends TriggerAction> triggerClass = triggers.keySet().stream().filter(channelTrigger1 -> equals(channelTrigger1, channelTrigger)).map(triggers::get).findFirst().orElse(null);
		if (triggerClass == null) return null;

		try {
			return triggerClass.getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static boolean equals(ChannelTrigger channelTrigger, ChannelTrigger channelTrigger1) {
		return channelTrigger.getChannelId() == channelTrigger1.getChannelId() && channelTrigger.getTriggerType() == channelTrigger1.getTriggerType();
	}
}
