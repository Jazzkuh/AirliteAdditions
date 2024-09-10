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

public class BrightLightsTrigger extends TriggerAction {
	@Override
	@SneakyThrows
	public void process() {
		if (AirliteAdditions.getInstance().getBrightLightsEnabled()) {
			AirliteAdditions.getUdpServer().writeStaticLed(ControlButton.LED_8A, ControlLedColor.GREEN);
			AirliteAdditions.getInstance().setBrightLightsEnabled(false);

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
		} else {
			AirliteAdditions.getUdpServer().writeBlinkingLed(ControlButton.LED_8A, ControlLedColor.RED, ControlLedColor.OFF, ControlLedBlinkSpeed.SLOW);
			AirliteAdditions.getInstance().setBrightLightsEnabled(true);
			for (Bulb bulb : BulbRegistry.getAllBulbs()) {
				PhilipsWizLightController.setColorTemperature(bulb, 6500, 100);
			}
		}
	}

	@Override
	public void startActions() {
		if (AirliteAdditions.getInstance().getBrightLightsEnabled()) {
			AirliteAdditions.getUdpServer().writeBlinkingLed(ControlButton.LED_8A, ControlLedColor.RED, ControlLedColor.OFF, ControlLedBlinkSpeed.SLOW);
		} else {
			AirliteAdditions.getUdpServer().writeStaticLed(ControlButton.LED_8A, ControlLedColor.GREEN);
		}
	}
}
