package com.jazzkuh.airliteadditions.common.triggers.button;

import com.jazzkuh.airliteadditions.AirliteAdditions;
import com.jazzkuh.airliteadditions.common.framework.button.ControlButton;
import com.jazzkuh.airliteadditions.common.framework.button.ControlLedColor;
import com.jazzkuh.airliteadditions.common.framework.trigger.TriggerAction;
import com.jazzkuh.airliteadditions.utils.lighting.PhilipsWizLightController;
import com.jazzkuh.airliteadditions.utils.lighting.bulb.BulbRegistry;
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
		}
	}

	@Override
	public void startActions() {
		if (AirliteAdditions.getInstance().getBackLightEnabled()) {
			PhilipsWizLightController.setState(BulbRegistry.getBulbByName("studio_back"), true);
			AirliteAdditions.getUdpServer().writeStaticLed(ControlButton.LED_7A, ControlLedColor.GREEN);
		} else {
			PhilipsWizLightController.setState(BulbRegistry.getBulbByName("studio_back"), false);
			AirliteAdditions.getUdpServer().writeStaticLed(ControlButton.LED_7A, ControlLedColor.RED);
		}
	}
}
