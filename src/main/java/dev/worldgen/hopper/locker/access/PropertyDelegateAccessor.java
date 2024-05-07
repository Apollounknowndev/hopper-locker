package dev.worldgen.hopper.locker.access;

import net.minecraft.screen.PropertyDelegate;

public interface PropertyDelegateAccessor {
    PropertyDelegate hopperLocker$getPropertyDelegate();
    default void hopperLocker$setPropertyDelegate(PropertyDelegate propertyDelegate) {
        throw new AssertionError();
    };
}
