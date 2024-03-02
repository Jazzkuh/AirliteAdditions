package com.jazzkuh.airliteadditions.common.framework;

import lombok.Getter;
import lombok.Setter;

@Getter
public class AirliteFaderStatus {
    private final int channelId;
    private @Setter boolean faderActive;
    private @Setter boolean channelOn;
    private @Setter boolean cueActive = false;
    private @Setter byte module;

    public AirliteFaderStatus(int channelId, byte faderData, byte channelData, byte module) {
        this.channelId = channelId;
        this.faderActive = faderData >= 1;
        this.channelOn = channelData >= 1;
        this.module = module;
    }

    public String toString() {
        return "Fader active: " + faderActive + ", channel on: " + channelOn;
    }

    public int getAirliteIndex() {
        return channelId + 3;
    }
}
