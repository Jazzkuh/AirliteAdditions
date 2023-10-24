package com.jazzkuh.airliteadditions;

import com.jazzkuh.airliteadditions.framework.trigger.ChannelTriggerAction;
import com.jazzkuh.airliteadditions.framework.trigger.Trigger;
import com.jazzkuh.airliteadditions.framework.trigger.TriggerType;
import com.jazzkuh.airliteadditions.triggers.OnAirLightTrigger;
import com.jazzkuh.airliteadditions.triggers.RegularLightTrigger;
import com.jazzkuh.airliteadditions.triggers.music.MusicPauseTrigger;
import com.jazzkuh.airliteadditions.triggers.music.MusicPlayPauseTrigger;
import com.jazzkuh.airliteadditions.triggers.music.MusicSkipStartTrigger;

import java.util.HashMap;
import java.util.Map;

public class ChannelTriggerRegistry {
	private static Map<Trigger, Class<? extends ChannelTriggerAction>> triggers = new HashMap<>();

	static {
		registerAction(new Trigger(1, TriggerType.FADER_AND_CHANNEL_ON), OnAirLightTrigger.class);
		registerAction(new Trigger(1, TriggerType.FADER_OFF), RegularLightTrigger.class);
		registerAction(new Trigger(1, TriggerType.CHANNEL_OFF), RegularLightTrigger.class);
		registerAction(new Trigger(8, TriggerType.FADER_AND_CHANNEL_ON), MusicSkipStartTrigger.class);
		registerAction(new Trigger(8, TriggerType.CHANNEL_OFF), MusicPauseTrigger.class);
		registerAction(new Trigger(8, TriggerType.FADER_OFF), MusicPauseTrigger.class);
	}

	public static void registerAction(Trigger trigger, Class<? extends ChannelTriggerAction> triggerClass) {
		triggers.put(trigger, triggerClass);
	}

	public static ChannelTriggerAction getAction(Trigger trigger) {
		Class<? extends ChannelTriggerAction> triggerClass = triggers.keySet().stream().filter(trigger1 -> equals(trigger1, trigger)).map(triggers::get).findFirst().orElse(null);
		if (triggerClass == null) return null;

		try {
			return triggerClass.getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static boolean equals(Trigger trigger, Trigger trigger1) {
		return trigger.getChannelId() == trigger1.getChannelId() && trigger.getTriggerType() == trigger1.getTriggerType();
	}
}
