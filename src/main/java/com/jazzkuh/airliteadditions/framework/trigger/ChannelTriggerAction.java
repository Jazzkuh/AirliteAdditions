package com.jazzkuh.airliteadditions.framework.trigger;

import com.jazzkuh.airliteadditions.framework.AirliteFaderStatus;

public abstract class ChannelTriggerAction implements ChannelTriggerActionImpl {
    public abstract void process(AirliteFaderStatus airliteFaderStatus);
}
