package com.jazzkuh.airliteadditions.common.triggers.button;

import com.jazzkuh.airliteadditions.AirliteAdditions;
import com.jazzkuh.airliteadditions.common.framework.button.ControlButton;
import com.jazzkuh.airliteadditions.common.framework.button.ControlLedBlinkSpeed;
import com.jazzkuh.airliteadditions.common.framework.button.ControlLedColor;
import com.jazzkuh.airliteadditions.common.framework.trigger.TriggerAction;
import com.jazzkuh.airliteadditions.common.utils.lighting.PhilipsWizLightController;
import com.jazzkuh.airliteadditions.common.utils.lighting.bulb.Bulb;
import com.jazzkuh.airliteadditions.common.utils.lighting.bulb.BulbRegistry;
import lombok.SneakyThrows;

import java.util.Timer;
import java.util.TimerTask;

public class PartyLightTrigger extends TriggerAction {
	@Override
	@SneakyThrows
	public void process() {
		if (AirliteAdditions.getInstance().getPartyLightEnabled()) {
			AirliteAdditions.getUdpServer().writeStaticLed(ControlButton.LED_8A, ControlLedColor.GREEN);

			try {
				sendRequest("http://localhost:8888/dance-to-spotify/abort");
			} catch (Exception ignored) {}

			AirliteAdditions.getInstance().setPartyLightEnabled(false);
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					for (Bulb bulb : BulbRegistry.getBulbsByGroups("warm_white")) {
						PhilipsWizLightController.setColorTemperature(bulb, 100);
					}
					for (Bulb bulb : BulbRegistry.getBulbsByGroups("studio", "magenta")) {
						PhilipsWizLightController.setRGBColor(bulb, 255, 0, 93, 100);
					}

					for (Bulb bulb : BulbRegistry.getBulbsByGroups("studio", "indigo")) {
						PhilipsWizLightController.setRGBColor(bulb, 111, 0, 255, 100);
					}
					System.out.println("Party light disabled");
				}
			}, 20);
		} else {
			AirliteAdditions.getUdpServer().writeBlinkingLed(ControlButton.LED_8A, ControlLedColor.RED, ControlLedColor.OFF, ControlLedBlinkSpeed.SLOW);

			try {
				sendRequest("http://localhost:8888/dance-to-spotify?mode=party");
			} catch (Exception ignored) {}

			AirliteAdditions.getInstance().setPartyLightEnabled(true);
		}
	}

	@Override
	public void startActions() {
		if (AirliteAdditions.getInstance().getPartyLightEnabled()) {
			AirliteAdditions.getUdpServer().writeBlinkingLed(ControlButton.LED_8A, ControlLedColor.RED, ControlLedColor.OFF, ControlLedBlinkSpeed.SLOW);
		} else {
			AirliteAdditions.getUdpServer().writeStaticLed(ControlButton.LED_8A, ControlLedColor.GREEN);
		}
	}
}
