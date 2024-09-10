package com.jazzkuh.airliteadditions.utils.music;

import com.jazzkuh.airliteadditions.AirliteAdditions;
import com.jazzkuh.airliteadditions.common.framework.button.ControlButton;
import com.jazzkuh.airliteadditions.common.framework.button.ControlLedColor;
import de.labystudio.spotifyapi.SpotifyListener;
import de.labystudio.spotifyapi.model.Track;

public class SpotifyEventListener implements SpotifyListener {
    @Override
    public void onConnect() {
    }

    @Override
    public void onTrackChanged(Track track) {
    }

    @Override
    public void onPositionChanged(int position) {
    }

    @Override
    public void onPlayBackChanged(boolean isPlaying) {
        if (isPlaying) {
            AirliteAdditions.getUdpServer().writeStaticLed(ControlButton.LED_1A, ControlLedColor.GREEN);
        } else {
            AirliteAdditions.getUdpServer().writeStaticLed(ControlButton.LED_1A, ControlLedColor.RED);
        }
    }

    @Override
    public void onSync() {
    }

    @Override
    public void onDisconnect(Exception exception) {
    }
}
