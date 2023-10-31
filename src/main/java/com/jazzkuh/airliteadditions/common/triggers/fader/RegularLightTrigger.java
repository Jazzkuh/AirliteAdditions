package com.jazzkuh.airliteadditions.common.triggers.fader;

import com.jazzkuh.airliteadditions.common.framework.trigger.TriggerAction;
import com.jazzkuh.airliteadditions.common.utils.lighting.PhilipsWizLightController;
import com.jazzkuh.airliteadditions.common.utils.lighting.bulb.Bulb;
import com.jazzkuh.airliteadditions.common.utils.lighting.bulb.BulbRegistry;
import lombok.SneakyThrows;

public class RegularLightTrigger extends TriggerAction {
	@Override
	@SneakyThrows
	public void process() {
		for (Bulb bulb : BulbRegistry.getBulbsByGroups("studio", "magenta")) {
			//PhilipsWizLightController.setRGBColor(bulb, 255, 0, 93, 100);
			PhilipsWizLightController.setRGBColor(bulb, 255, 79, 0, 100);
		}

		for (Bulb bulb : BulbRegistry.getBulbsByGroups("studio", "indigo")) {
			//PhilipsWizLightController.setRGBColor(bulb, 111, 0, 255, 100);
			PhilipsWizLightController.setRGBColor(bulb, 	255, 36, 0, 100);
		}
	}
}
