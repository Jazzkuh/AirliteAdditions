package com.jazzkuh.airliteadditions.common.utils.lighting.bulb;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class BulbRegistry {
	private static final List<Bulb> bulbs = new ArrayList<>();

	static {
		bulbs.add(new Bulb("studio_right", "192.168.178.192", "studio", "right", "purple", "12482140", "indigo"));
		bulbs.add(new Bulb("studio_left", "192.168.178.111", "studio", "left", "green", "12482140", "indigo"));
		bulbs.add(new Bulb("studio_led_strip", "192.168.178.205", "studio", "led_strip", "green", "12482140", "magenta"));

		bulbs.add(new Bulb("living_hanging_one", "192.168.178.134", "living", "hanging", "warm_white", "11711492"));
		bulbs.add(new Bulb("living_hanging_two", "192.168.178.141", "living", "hanging", "warm_white", "11711492"));
		bulbs.add(new Bulb("living_hanging_three", "192.168.178.185", "living", "hanging", "warm_white", "11711492"));

		bulbs.add(new Bulb("living_table_one", "192.168.178.68", "living", "hanging", "warm_white", "11711492"));
		bulbs.add(new Bulb("living_table_two", "192.168.178.135", "living", "hanging", "warm_white", "11711492"));

		bulbs.add(new Bulb("living_yellow_lamp", "192.168.178.55", "living", "yellow_lamp", "warm_white", "11711492"));
		bulbs.add(new Bulb("living_standing", "192.168.178.18", "living", "standing", "warm_white", "11711492"));

		bulbs.add(new Bulb("kitchen_one", "192.168.178.32", "kitchen", "one", "warm_white", "11711485"));
		bulbs.add(new Bulb("kitchen_two", "192.168.178.61", "kitchen", "two", "warm_white", "11711485"));
		bulbs.add(new Bulb("kitchen_three", "192.168.178.173", "kitchen", "three", "warm_white", "11711485"));

		bulbs.add(new Bulb("cabinet_one", "192.168.178.51", "living", "cabinet", "warm_white", "11711485"));
		bulbs.add(new Bulb("cabinet_two", "192.168.178.97", "living", "cabinet", "warm_white", "11711485"));
	}

	public static List<Bulb> getAllBulbs() {
		return bulbs;
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
