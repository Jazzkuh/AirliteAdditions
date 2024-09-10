package com.jazzkuh.airliteadditions.common.triggers.fader;

import com.jazzkuh.airliteadditions.AirliteAdditions;
import com.jazzkuh.airliteadditions.common.framework.trigger.TriggerAction;
import com.jazzkuh.airliteadditions.utils.lighting.PhilipsWizLightController;
import com.jazzkuh.airliteadditions.utils.lighting.bulb.BulbRegistry;
import lombok.SneakyThrows;

public class RegularLightTrigger extends TriggerAction {
	@Override
	@SneakyThrows
	public void process() {
		AirliteAdditions.getInstance().setMicOn(-1);
		PhilipsWizLightController.setRGBColor(BulbRegistry.getBulbByName("studio_led_strip2"), 255, 0, 0, 100);
		PhilipsWizLightController.setState(BulbRegistry.getBulbByName("studio_led_strip2"), false);
	}
}
