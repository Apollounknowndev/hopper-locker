package dev.worldgen.hopper.locker.access;

import net.minecraft.entity.player.PlayerEntity;

public interface ScreenAccessor {
    PlayerEntity hopperLocker$getPlayer();

    void hopperLocker$setSlotEnabled(int slotId, boolean enabled);
}
