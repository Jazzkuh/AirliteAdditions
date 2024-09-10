package com.jazzkuh.airliteadditions.utils.lighting.bulb;

import lombok.Getter;

@Getter
public class Bulb {
	private final String name;
	private final String ip;
	private final String[] groups;

	Bulb(String name, String ip, String... groups) {
		this.name = name;
		this.ip = ip;
		this.groups = groups;
	}
}
