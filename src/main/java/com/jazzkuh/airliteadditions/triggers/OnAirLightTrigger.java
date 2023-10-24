package com.jazzkuh.airliteadditions.triggers;

import com.jazzkuh.airliteadditions.framework.AirliteFaderStatus;
import com.jazzkuh.airliteadditions.framework.trigger.ChannelTriggerAction;
import com.jazzkuh.airliteadditions.utils.lighting.PhilipsWizLightController;
import com.jazzkuh.airliteadditions.utils.lighting.bulb.Bulb;
import com.jazzkuh.airliteadditions.utils.lighting.bulb.BulbRegistry;
import lombok.SneakyThrows;

public class OnAirLightTrigger extends ChannelTriggerAction {
	@Override
	@SneakyThrows
	public void process(AirliteFaderStatus airliteFaderStatus) {
		for (Bulb bulb : BulbRegistry.getBulbsByGroup("studio")) {
			PhilipsWizLightController.setRGBColor(bulb, 255, 0, 0, 100);
		}
	}
}
