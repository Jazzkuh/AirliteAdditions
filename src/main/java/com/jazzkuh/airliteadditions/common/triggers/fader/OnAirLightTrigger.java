package com.jazzkuh.airliteadditions.common.triggers.fader;

import com.jazzkuh.airliteadditions.common.framework.trigger.TriggerAction;
import com.jazzkuh.airliteadditions.common.utils.lighting.PhilipsWizLightController;
import com.jazzkuh.airliteadditions.common.utils.lighting.bulb.Bulb;
import com.jazzkuh.airliteadditions.common.utils.lighting.bulb.BulbRegistry;
import lombok.SneakyThrows;

public class OnAirLightTrigger extends TriggerAction {
	@Override
	@SneakyThrows
	public void process() {
		PhilipsWizLightController.setRGBColor(BulbRegistry.getBulbByName("studio_led_strip2"), 255, 0, 0, 100);
		PhilipsWizLightController.setState(BulbRegistry.getBulbByName("studio_led_strip2"), true);
		/*for (Bulb bulb : BulbRegistry.getBulbsByGroup("studio")) {
			PhilipsWizLightController.setRGBColor(bulb, 255, 0, 0, 100);
		}*/
	}
}
