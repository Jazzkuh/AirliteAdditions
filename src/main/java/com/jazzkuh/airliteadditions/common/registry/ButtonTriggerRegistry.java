package com.jazzkuh.airliteadditions.common.registry;

import com.jazzkuh.airliteadditions.common.framework.button.ButtonTrigger;
import com.jazzkuh.airliteadditions.common.framework.button.ControlButton;
import com.jazzkuh.airliteadditions.common.framework.trigger.TriggerAction;
import com.jazzkuh.airliteadditions.common.framework.trigger.TriggerType;
import com.jazzkuh.airliteadditions.common.triggers.button.*;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class ButtonTriggerRegistry {
	private static @Getter Map<ButtonTrigger, Class<? extends TriggerAction>> triggers = new HashMap<>();

	static {
		registerAction(new ButtonTrigger(ControlButton.LED_1A, TriggerType.BUTTON_PRESSED), MusicPlayPauseTrigger.class);
		registerAction(new ButtonTrigger(ControlButton.LED_2A, TriggerType.BUTTON_PRESSED), MusicSkipTrigger.class);
		registerAction(new ButtonTrigger(ControlButton.LED_3A, TriggerType.BUTTON_PRESSED), MusicPreviousTrigger.class);

		registerAction(new ButtonTrigger(ControlButton.LED_6A, TriggerType.BUTTON_PRESSED), SceneTrigger.class);

		registerAction(new ButtonTrigger(ControlButton.LED_7A, TriggerType.BUTTON_PRESSED), BackLightTrigger.class);
		registerAction(new ButtonTrigger(ControlButton.LED_7B, TriggerType.BUTTON_PRESSED), PartyLightTrigger.class);
		registerAction(new ButtonTrigger(ControlButton.LED_8A, TriggerType.BUTTON_PRESSED), BrightLightsTrigger.class);
		registerAction(new ButtonTrigger(ControlButton.LED_8B, TriggerType.BUTTON_PRESSED), FaderSkipToggleTrigger.class);
	}

	public static void registerAction(ButtonTrigger buttonTrigger, Class<? extends TriggerAction> triggerClass) {
		triggers.put(buttonTrigger, triggerClass);
	}

	public static TriggerAction getAction(ButtonTrigger buttonTrigger) {
		Class<? extends TriggerAction> triggerClass = triggers.keySet().stream().filter(buttonTrigger1 -> equals(buttonTrigger1, buttonTrigger)).map(triggers::get).findFirst().orElse(null);
		if (triggerClass == null) return null;

		try {
			return triggerClass.getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean isKnownButton(ControlButton controlButton) {
		return triggers.keySet().stream().anyMatch(buttonTrigger -> buttonTrigger.getControlButton() == controlButton);
	}

	private static boolean equals(ButtonTrigger buttonTrigger, ButtonTrigger buttonTrigger1) {
		return buttonTrigger1.getControlButton() == buttonTrigger.getControlButton() && buttonTrigger1.getTriggerType() == buttonTrigger.getTriggerType();
	}
}
