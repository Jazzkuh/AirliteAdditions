package com.jazzkuh.airliteadditions.framework.trigger;

import lombok.Getter;
import lombok.Setter;

public class Trigger {
    public final @Getter int channelId;
    public @Getter @Setter TriggerType triggerType;

    public Trigger(int channelId, TriggerType triggerType) {
        this.channelId = channelId;
        this.triggerType = triggerType;
    }
}
