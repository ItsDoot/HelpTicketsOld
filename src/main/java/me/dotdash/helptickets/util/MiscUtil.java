package me.dotdash.helptickets.util;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.UUID;

public class MiscUtil {
    public static String uuidToName(UUID uuid) {
        return Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(uuid).get().getName();
    }

    public static String uuidToName(String uuid) {
        return Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(UUID.fromString(uuid)).get()
                .getName();
    }
}