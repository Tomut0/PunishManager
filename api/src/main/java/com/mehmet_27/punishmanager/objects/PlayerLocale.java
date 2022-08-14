package com.mehmet_27.punishmanager.objects;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.UUID;

public class PlayerLocale {
    private final ConfigManager configManager = PunishManager.getInstance().getConfigManager();
    private final @Nullable UUID uuid;

    public PlayerLocale(@Nullable UUID uuid) {
        this.uuid = uuid;
    }

    public Locale getLocale() {
        if (uuid != null) {
            return PunishManager.getInstance().getOfflinePlayers().get(uuid).getLocale();
        } else {
            return configManager.getDefaultLocale();
        }
    }
}
