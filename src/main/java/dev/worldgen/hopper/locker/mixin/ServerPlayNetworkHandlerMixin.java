package dev.worldgen.hopper.locker.mixin;

import dev.worldgen.hopper.locker.access.HopperHandlerAccessor;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.network.packet.c2s.play.SlotChangedStateC2SPacket;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Inject(
        method = "onSlotChangedState",
        at = @At("TAIL")
    )
    private void hopperLocker$updateHopperBlockEntity(SlotChangedStateC2SPacket packet, CallbackInfo ci) {
        ScreenHandler screenHandler = ((ServerPlayNetworkHandler)(Object)this).player.currentScreenHandler;
        if (screenHandler instanceof HopperScreenHandler hopperHandler && ((HopperScreenHandlerAccessor)hopperHandler).getInventory() instanceof HopperBlockEntity hopperBlock) {
            ((HopperHandlerAccessor)hopperBlock).hopperLocker$setSlot(packet.slotId(), packet.newState());
        }
    }
}