package dev.worldgen.hopper.locker.mixin;

import dev.worldgen.hopper.locker.access.PropertyDelegateAccessor;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public interface InventoryMixin {
    @Inject(
        method = "isValid",
        at = @At("HEAD"),
        cancellable = true
    )
    private void hopperLocker$invalidateLockedSlots(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof PropertyDelegateAccessor accessor && accessor.hopperLocker$getPropertyDelegate().get(slot) == 1) {
            cir.setReturnValue(false);
        }
    }
}
