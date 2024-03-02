package com.jazzkuh.airliteadditions.common.utils.lighting.bulb;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class BulbRegistry {
	private static final List<Bulb> bulbs = new ArrayList<>();

	static {
		bulbs.add(new Bulb("studio_right", "192.168.1.87", "studio", "right", "purple", "indigo"));
		bulbs.add(new Bulb("studio_left", "192.168.1.77", "studio", "left", "green", "indigo"));
		bulbs.add(new Bulb("studio_led_strip", "192.168.1.92", "studio", "led_strip", "green", "magenta"));
		bulbs.add(new Bulb("studio_led_strip2", "192.168.1.83"));
		bulbs.add(new Bulb("studio_back", "192.168.1.81", "studio", "back", "green", "indigo"));

		bulbs.add(new Bulb("living_hanging_one", "192.168.1.138", "living", "hanging", "scarlet"));
		bulbs.add(new Bulb("living_hanging_two", "192.168.1.137", "living", "hanging", "scarlet"));
		bulbs.add(new Bulb("living_hanging_three", "192.168.1.139", "living", "hanging", "scarlet"));

		bulbs.add(new Bulb("living_table_one", "192.168.1.140", "living", "hanging", "scarlet"));
		bulbs.add(new Bulb("living_table_two", "192.168.1.141", "living", "hanging", "scarlet"));

		bulbs.add(new Bulb("living_yellow_lamp", "192.168.1.91", "living", "yellow_lamp", "scarlet"));
		bulbs.add(new Bulb("living_standing", "192.168.1.88", "living", "standing", "scarlet"));

		bulbs.add(new Bulb("kitchen_one", "192.168.1.89", "kitchen", "one", "warm_white"));
		bulbs.add(new Bulb("kitchen_two", "192.168.1.90", "kitchen", "two", "warm_white"));
		bulbs.add(new Bulb("kitchen_three", "192.168.1.80", "kitchen", "three", "warm_white"));

		bulbs.add(new Bulb("cabinet_one", "192.168.1.85", "living", "cabinet", "warm_white"));
		bulbs.add(new Bulb("cabinet_two", "192.168.1.82", "living", "cabinet", "warm_white"));
	}

	public static List<Bulb> getAllBulbs() {
		return bulbs;
	}

	public static Bulb getBulbByName(String name) {
		for (Bulb bulb : BulbRegistry.bulbs) {
			if (bulb.getName().equals(name)) {
				return bulb;
			}
		}
		return null;
	}

	public static List<Bulb> getBulbsByGroup(String group) {
		List<Bulb> bulbs = new ArrayList<>();
		for (Bulb bulb : BulbRegistry.bulbs) {
			for (String bulbGroup : bulb.getGroups()) {
				if (bulbGroup.equals(group)) {
					bulbs.add(bulb);
				}
			}
		}
		return bulbs;
	}

	public static List<Bulb> getBulbsByGroups(String... groups) {
		List<Bulb> bulbs = new ArrayList<>();

		for (Bulb bulb : BulbRegistry.bulbs) {
			boolean hasAllGroups = true;

			for (String group : groups) {
				if (!Arrays.stream(bulb.getGroups()).toList().contains(group)) {
					hasAllGroups = false;
					break;
				}
			}

			if (hasAllGroups) {
				bulbs.add(bulb);
			}
		}

		return bulbs;
	}
}
