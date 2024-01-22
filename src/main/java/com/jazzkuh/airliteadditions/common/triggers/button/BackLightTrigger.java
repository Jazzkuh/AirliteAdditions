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

public class BackLightTrigger extends TriggerAction {
	@Override
	@SneakyThrows
	public void process() {
		if (AirliteAdditions.getInstance().getBackLightEnabled()) {
			AirliteAdditions.getUdpServer().writeStaticLed(ControlButton.LED_7A, ControlLedColor.RED);
			AirliteAdditions.getInstance().setBackLightEnabled(false);
			PhilipsWizLightController.setState(BulbRegistry.getBulbByName("studio_back"), false);
		} else {
			AirliteAdditions.getUdpServer().writeStaticLed(ControlButton.LED_7A, ControlLedColor.GREEN);
			AirliteAdditions.getInstance().setBackLightEnabled(true);
			PhilipsWizLightController.setState(BulbRegistry.getBulbByName("studio_back"), true);
			PhilipsWizLightController.setRGBColor(BulbRegistry.getBulbByName("studio_back"), 255, 36, 0, 100);
		}
	}

	@Override
	public void startActions() {
		if (AirliteAdditions.getInstance().getBackLightEnabled()) {
			AirliteAdditions.getUdpServer().writeStaticLed(ControlButton.LED_7A, ControlLedColor.GREEN);
		} else {
			AirliteAdditions.getUdpServer().writeStaticLed(ControlButton.LED_7A, ControlLedColor.RED);
		}
	}
}
