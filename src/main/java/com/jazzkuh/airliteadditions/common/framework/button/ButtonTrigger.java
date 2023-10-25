package com.jazzkuh.airliteadditions.common.framework.button;

import com.jazzkuh.airliteadditions.common.framework.trigger.TriggerType;
import lombok.Getter;
import lombok.Setter;

public class ButtonTrigger {
    public final @Getter ControlButton controlButton;
    public @Getter @Setter TriggerType triggerType;

    public ButtonTrigger(ControlButton controlButton, TriggerType triggerType) {
        this.controlButton = controlButton;
        this.triggerType = triggerType;
    }
}
