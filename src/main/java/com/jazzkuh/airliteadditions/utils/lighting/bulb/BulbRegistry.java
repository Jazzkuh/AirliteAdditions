package com.jazzkuh.airliteadditions.utils.lighting.bulb;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class BulbRegistry {
	private static final List<Bulb> bulbs = new ArrayList<>();

	static {
		bulbs.add(new Bulb("studio_right", "192.168.178.171", "studio", "right", "purple", "12482140", "indigo"));
		bulbs.add(new Bulb("studio_left", "192.168.178.115", "studio", "left", "green", "12482140", "indigo"));
		bulbs.add(new Bulb("studio_led_strip", "192.168.178.178", "studio", "led_strip", "green", "12482140", "magenta"));
		bulbs.add(new Bulb("living_table", "192.168.178.38", "living", "table", "warm_white", "11711492"));
		bulbs.add(new Bulb("living_hanging", "192.168.178.81", "living", "hanging", "warm_white", "11711492"));
		bulbs.add(new Bulb("living_standing", "192.168.178.80", "living", "standing", "warm_white", "11711492"));
		bulbs.add(new Bulb("kitchen_one", "192.168.178.109", "kitchen", "one", "warm_white", "11711485"));
		bulbs.add(new Bulb("kitchen_two", "192.168.178.101", "kitchen", "two", "warm_white", "11711485"));
		bulbs.add(new Bulb("kitchen_three", "192.168.178.249", "kitchen", "three", "warm_white", "11711485"));
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
