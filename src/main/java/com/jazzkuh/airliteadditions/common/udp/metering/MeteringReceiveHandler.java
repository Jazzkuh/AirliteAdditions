package com.jazzkuh.airliteadditions.common.udp.metering;

import com.jazzkuh.airliteadditions.AirliteAdditions;
import com.jazzkuh.airliteadditions.common.framework.AirliteFaderStatus;
import com.jazzkuh.airliteadditions.common.framework.button.ButtonTrigger;
import com.jazzkuh.airliteadditions.common.framework.button.ControlButton;
import com.jazzkuh.airliteadditions.common.framework.button.ControlLedColor;
import com.jazzkuh.airliteadditions.common.framework.channel.ChannelTrigger;
import com.jazzkuh.airliteadditions.common.framework.trigger.TriggerAction;
import com.jazzkuh.airliteadditions.common.framework.trigger.TriggerType;
import com.jazzkuh.airliteadditions.common.registry.ButtonTriggerRegistry;
import com.jazzkuh.airliteadditions.common.registry.ChannelTriggerRegistry;

import java.util.Map;
import java.util.logging.Logger;

public class MeteringReceiveHandler {
    private static final Logger LOGGER = Logger.getLogger(MeteringReceiveHandler.class.getName());
    private static final Map<Integer, Double> bits = Map.of(
            0, 0D,
            1, 0.25D,
            2, 0.5D,
            3, 0.75D
    );
    public static void process(byte[] data) {
        try {
            // byte 0 and 1 are the AirLite header

            byte size = data[2];
            byte cmd = data[3];

            if (size == (byte) 0x08 && cmd == (byte) 0xF0) {
                byte data0 = data[4];
                byte data1 = data[5];
                byte data2 = data[6];
                byte data3 = data[7];
                byte data4 = data[8];
                byte data5 = data[9];

                // Masks
                int valueMask = 0x3F;      // Mask for bits [5:0]
                int intPartMask = 0x30;     // Mask for bits [7:6]
                int decPartMask = 0x03;     // Mask for bits [1:0]

                // Extracting data and calculating metering values
                double progL = ((data0 & intPartMask) >> 8) * 10 + (data0 & valueMask) + (bits.get(data0 & decPartMask));
                double progR = ((data1 & intPartMask) >> 8) * 10 + (data1 & valueMask) + (bits.get(data1 & decPartMask));
                double phonesL = ((data2 & intPartMask) >> 8) * 10 + (data2 & valueMask) + (bits.get(data2 & decPartMask));
                double phonesR = ((data3 & intPartMask) >> 8) * 10 + (data3 & valueMask) + (bits.get(data3 & decPartMask));
                double crmL = ((data4 & intPartMask) >> 8) * 10 + (data4 & valueMask) + (bits.get(data4 & decPartMask));
                double crmR = ((data5 & intPartMask) >> 8) * 10 + (data5 & valueMask) + (bits.get(data5 & decPartMask));

                // Creating the map of metering values
                AirliteAdditions.getInstance().setMeteringValues(Map.of(
                        "PROG_L", progL,
                        "PROG_R", progR,
                        "PHONES_L", phonesL,
                        "PHONES_R", phonesR,
                        "CRM_L", crmL,
                        "CRM_R", crmR
                ));
            }

        } catch (Exception e) {
            LOGGER.warning("Error while receiving data: " + e.getMessage());
        }
    }
}
