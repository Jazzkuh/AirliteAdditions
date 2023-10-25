package com.jazzkuh.airliteadditions.common.framework.trigger;

public interface TriggerActionImpl {
    public abstract void process();

    default void startActions() {
    }
}
