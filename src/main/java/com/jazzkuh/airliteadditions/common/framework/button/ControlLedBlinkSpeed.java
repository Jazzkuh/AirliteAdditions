package com.jazzkuh.airliteadditions.common.framework.button;

import lombok.Getter;

public enum ControlLedBlinkSpeed {
    SLOW((byte) 0x00),
    NORMAL((byte) 0x01),
    FAST((byte) 0x02);

    private final @Getter byte data;

    ControlLedBlinkSpeed(byte data) {
        this.data = data;
    }
}
