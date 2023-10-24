package com.jazzkuh.airliteadditions.triggers;

import com.jazzkuh.airliteadditions.framework.AirliteFaderStatus;
import com.jazzkuh.airliteadditions.framework.trigger.ChannelTriggerAction;
import com.jazzkuh.airliteadditions.utils.lighting.PhilipsWizLightController;
import com.jazzkuh.airliteadditions.utils.lighting.bulb.Bulb;
import com.jazzkuh.airliteadditions.utils.lighting.bulb.BulbRegistry;
import lombok.SneakyThrows;

public class RegularLightTrigger extends ChannelTriggerAction {
	@Override
	@SneakyThrows
	public void process(AirliteFaderStatus airliteFaderStatus) {
		for (Bulb bulb : BulbRegistry.getBulbsByGroups("studio", "magenta")) {
			PhilipsWizLightController.setRGBColor(bulb, 255, 0, 93, 100);
		}

		for (Bulb bulb : BulbRegistry.getBulbsByGroups("studio", "indigo")) {
			PhilipsWizLightController.setRGBColor(bulb, 111, 0, 255, 100);
		}
	}
}
