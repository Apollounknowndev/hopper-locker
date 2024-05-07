package dev.worldgen.hopper.locker.mixin;

import dev.worldgen.hopper.locker.HopperInputSlot;
import dev.worldgen.hopper.locker.HopperLockerUtils;
import dev.worldgen.hopper.locker.access.HopperHandlerAccessor;
import dev.worldgen.hopper.locker.access.ScreenAccessor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HopperScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

    @Inject(
        method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V",
        at = @At("HEAD")
    )
    private void hopperLocker$toggleSlot(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        HandledScreen<?> handledScreen = ((HandledScreen<?>)(Object)this);
        if (handledScreen instanceof HopperScreen screen) {
            ScreenAccessor screenAccessor = (ScreenAccessor)screen;
            HopperScreenHandler handler = screen.getScreenHandler();
            HopperHandlerAccessor handlerAccessor = (HopperHandlerAccessor)handler;
            PlayerEntity player = screenAccessor.hopperLocker$getPlayer();

            if (slot instanceof HopperInputSlot && !slot.hasStack() && !player.isSpectator()) {
                switch (actionType) {
                    case PICKUP: {
                        if (handlerAccessor.hopperLocker$isSlotDisabled(slotId)) {
                            screenAccessor.hopperLocker$setSlotEnabled(slotId, true);
                            break;
                        }
                        if (!handler.getCursorStack().isEmpty()) break;
                        screenAccessor.hopperLocker$setSlotEnabled(slotId, false);
                        break;
                    }
                    case SWAP: {
                        ItemStack itemStack = player.getInventory().getStack(button);
                        if (!handlerAccessor.hopperLocker$isSlotDisabled(slotId) || itemStack.isEmpty()) break;
                        screenAccessor.hopperLocker$setSlotEnabled(slotId, true);
                    }
                }
            }
        }

    }

    @Inject(
        method = "drawSlot",
        at = @At("HEAD")
    )
    private void hopperLocker$drawLockedHopperSlots(DrawContext context, Slot slot, CallbackInfo ci) {
        if (slot instanceof HopperInputSlot hopperSlot && hopperSlot.getHandlerAccessor().hopperLocker$isSlotDisabled(slot.id)) {
            context.drawGuiTexture(HopperLockerUtils.DISABLED_SLOT_TEXTURE, slot.x - 1, slot.y - 1, 18, 18);
        }
    }
}
