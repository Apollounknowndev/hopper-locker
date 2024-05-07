package dev.worldgen.hopper.locker.access;

public interface HopperHandlerAccessor {
    boolean hopperLocker$isSlotDisabled(int slot);
    void hopperLocker$setSlot(int slot, boolean enabled);
}
