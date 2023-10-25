package com.jazzkuh.airliteadditions.common.triggers.button;

import com.jazzkuh.airliteadditions.AirliteAdditions;
import com.jazzkuh.airliteadditions.common.framework.button.ControlButton;
import com.jazzkuh.airliteadditions.common.framework.button.ControlLedBlinkSpeed;
import com.jazzkuh.airliteadditions.common.framework.button.ControlLedColor;
import com.jazzkuh.airliteadditions.common.framework.trigger.TriggerAction;
import lombok.SneakyThrows;

public class FaderSkipToggleTrigger extends TriggerAction {
	@Override
	@SneakyThrows
	public void process() {
		if (AirliteAdditions.getInstance().getShouldSkipOnStart()) {
			AirliteAdditions.getUdpServer().writeBlinkingLed(ControlButton.LED_8B, ControlLedColor.RED, ControlLedColor.OFF, ControlLedBlinkSpeed.SLOW);
			AirliteAdditions.getInstance().setShouldSkipOnStart(false);
		} else {
			AirliteAdditions.getUdpServer().writeStaticLed(ControlButton.LED_8B, ControlLedColor.GREEN);
			AirliteAdditions.getInstance().setShouldSkipOnStart(true);
		}
	}

	@Override
	public void startActions() {
		if (AirliteAdditions.getInstance().getShouldSkipOnStart()) {
			AirliteAdditions.getUdpServer().writeStaticLed(ControlButton.LED_8B, ControlLedColor.GREEN);
		} else {
			AirliteAdditions.getUdpServer().writeBlinkingLed(ControlButton.LED_8B, ControlLedColor.RED, ControlLedColor.OFF, ControlLedBlinkSpeed.SLOW);
		}
	}
}
