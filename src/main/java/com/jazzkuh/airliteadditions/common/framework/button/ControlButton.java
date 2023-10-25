package com.jazzkuh.airliteadditions.common.framework.button;

import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

@ToString
public enum ControlButton {
    LED_1A((byte) 0x00, "A", 1, -45, false),
    LED_2A((byte) 0x01, "A", 2, -40),
    LED_3A((byte) 0x02, "A", 4, -35),
    LED_4A((byte) 0x03, "A", 8, -30),
    LED_5A((byte) 0x04, "A", 16, -25),
    LED_6A((byte) 0x05, "A", 32, -20),
    LED_7A((byte) 0x06, "A", 64, -15),
    LED_8A((byte) 0x07, "A", -128, -10, false),
    LED_1B((byte) 0x08, "B", 1, -5),
    LED_2B((byte) 0x09, "B", 2, 0),
    LED_3B((byte) 0x0A, "B", 4, 5),
    LED_4B((byte) 0x0B, "B", 8, 10),
    LED_5B((byte) 0x0C, "B", 16, 15),
    LED_6B((byte) 0x0D, "B", 32, 20),
    LED_7B((byte) 0x0E, "B", 64, 25),
    LED_8B((byte) 0x0F, "B", -128, 30, false),
    ALL_LEDS((byte) 0xFF, "ALL", 0, 0);

    private final @Getter byte buttonId;
    private final @Getter String row;
    private final @Getter int pressedValue;
    private final @Getter int buttonValue;
    private final @Getter boolean hasPressedColor;

    ControlButton(byte buttonId, String row, int pressedValue, int buttonValue) {
        this.buttonId = buttonId;
        this.row = row;
        this.pressedValue = pressedValue;
        this.buttonValue = buttonValue;
        this.hasPressedColor = true;
    }

    ControlButton(byte buttonId, String row, int pressedValue, int buttonValue, boolean hasPressedColor) {
        this.buttonId = buttonId;
        this.row = row;
        this.pressedValue = pressedValue;
        this.buttonValue = buttonValue;
        this.hasPressedColor = hasPressedColor;
    }

    @Nullable
    public static ControlButton getButton(int buttonValue) {
        for (ControlButton controlButton : ControlButton.values()) {
            if (controlButton.getButtonValue() == buttonValue) {
                return controlButton;
            }
        }

        return null;
    }
}
