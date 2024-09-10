package com.jazzkuh.airliteadditions.common.triggers.button;

import com.jazzkuh.airliteadditions.AirliteAdditions;
import com.jazzkuh.airliteadditions.common.framework.button.ControlButton;
import com.jazzkuh.airliteadditions.common.framework.button.ControlLedBlinkSpeed;
import com.jazzkuh.airliteadditions.common.framework.button.ControlLedColor;
import com.jazzkuh.airliteadditions.common.framework.trigger.TriggerAction;
import com.jazzkuh.airliteadditions.utils.lighting.PhilipsWizLightController;
import com.jazzkuh.airliteadditions.utils.lighting.bulb.Bulb;
import com.jazzkuh.airliteadditions.utils.lighting.bulb.BulbRegistry;
import lombok.SneakyThrows;

import java.util.Timer;
import java.util.TimerTask;

public class PartyLightTrigger extends TriggerAction {
	@Override
	@SneakyThrows
	public void process() {
		if (AirliteAdditions.getInstance().getPartyLightEnabled()) {
			AirliteAdditions.getUdpServer().writeStaticLed(ControlButton.LED_7B, ControlLedColor.GREEN);

			try {
				sendRequest("http://localhost:8888/dance-to-spotify/abort");
			} catch (Exception ignored) {}

			AirliteAdditions.getInstance().setPartyLightEnabled(false);
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					PhilipsWizLightController.setState(BulbRegistry.getBulbByName("studio_led_strip2"), false);
					for (Bulb bulb : BulbRegistry.getBulbsByGroups("scarlet")) {
						PhilipsWizLightController.setScene(bulb, PhilipsWizLightController.Scene.Sunset, 100);
					}

					for (Bulb bulb : BulbRegistry.getBulbsByGroups("studio")) {
						PhilipsWizLightController.setScene(bulb, PhilipsWizLightController.Scene.Sunset, 100);
					}

					for (Bulb bulb : BulbRegistry.getBulbsByGroups("warm_white")) {
						PhilipsWizLightController.setColorTemperature(bulb, 2200, 100);
					}
					System.out.println("Party light disabled");
				}
			}, 1500);
		} else {
			for (Bulb bulb : BulbRegistry.getBulbsByGroups("cabinet")) {
				PhilipsWizLightController.setState(bulb, false);
			}
			AirliteAdditions.getUdpServer().writeBlinkingLed(ControlButton.LED_7B, ControlLedColor.RED, ControlLedColor.OFF, ControlLedBlinkSpeed.SLOW);

			try {
				sendRequest("http://localhost:8888/dance-to-spotify?mode=party&roomIds=12482140,11711485,11711492");
			} catch (Exception ignored) {}

			AirliteAdditions.getInstance().setPartyLightEnabled(true);
		}
	}

	@Override
	public void startActions() {
		if (AirliteAdditions.getInstance().getPartyLightEnabled()) {
			AirliteAdditions.getUdpServer().writeBlinkingLed(ControlButton.LED_7B, ControlLedColor.RED, ControlLedColor.OFF, ControlLedBlinkSpeed.SLOW);
		} else {
			AirliteAdditions.getUdpServer().writeStaticLed(ControlButton.LED_7B, ControlLedColor.GREEN);
		}
	}
}