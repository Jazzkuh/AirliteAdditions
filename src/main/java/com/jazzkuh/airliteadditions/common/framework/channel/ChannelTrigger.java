package com.jazzkuh.airliteadditions.common.framework.channel;

import com.jazzkuh.airliteadditions.common.framework.trigger.TriggerType;
import lombok.Getter;
import lombok.Setter;

public class ChannelTrigger {
    public final @Getter int channelId;
    public @Getter @Setter TriggerType triggerType;

    public ChannelTrigger(int channelId, TriggerType triggerType) {
        this.channelId = channelId;
        this.triggerType = triggerType;
    }
}
