package dev.worldgen.hopper.locker.mixin;

import dev.worldgen.hopper.locker.HopperInputSlot;
import dev.worldgen.hopper.locker.access.HopperHandlerAccessor;
import dev.worldgen.hopper.locker.access.PropertyDelegateAccessor;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.worldgen.hopper.locker.HopperLockerUtils.validSlot;

@Mixin(HopperScreenHandler.class)
public abstract class HopperScreenHandlerMixin extends ScreenHandler implements PropertyDelegateAccessor, HopperHandlerAccessor {
    protected HopperScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Unique
    protected PropertyDelegate propertyDelegate;

    @Override
    public PropertyDelegate hopperLocker$getPropertyDelegate() {
        return this.propertyDelegate;
    }

    @Override
    public void hopperLocker$setPropertyDelegate(PropertyDelegate propertyDelegate) {
        this.propertyDelegate = propertyDelegate;
        this.addProperties(this.propertyDelegate);
    }

    @Override
    public void hopperLocker$setSlot(int slot, boolean enabled) {
        HopperInputSlot hopperInputSlot = (HopperInputSlot)this.getSlot(slot);
        this.propertyDelegate.set(hopperInputSlot.id, enabled ? 0 : 1);
        this.sendContentUpdates();
    }

    @Override
    public boolean hopperLocker$isSlotDisabled(int slot) {
        if (validSlot(slot)) {
            return this.propertyDelegate.get(slot) == 1;
        } else {
            return false;
        }
    }

    /**
     * Mixins
     */

    @Inject(
            method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;)V",
            at = @At("TAIL")
    )
    private void hopperLocker$addPropertyDelegate(int syncId, PlayerInventory playerInventory, CallbackInfo ci) {
        this.hopperLocker$setPropertyDelegate(new ArrayPropertyDelegate(5));
    }

    @ModifyArg(
        method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/screen/HopperScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;",
            ordinal = 0
        )
    )
    private Slot hopperLocker$addHopperSlots(Slot slot) {
        return new HopperInputSlot(slot, ((HopperScreenHandler)(Object)this));
    }
}
