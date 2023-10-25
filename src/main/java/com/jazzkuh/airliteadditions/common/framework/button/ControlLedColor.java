package com.jazzkuh.airliteadditions.common.framework.button;

import lombok.Getter;

public enum ControlLedColor {
    OFF((byte) 0x00),
    RED((byte) 0x01),
    GREEN((byte) 0x02);

    private final @Getter byte data;

    ControlLedColor(byte data) {
        this.data = data;
    }
}
