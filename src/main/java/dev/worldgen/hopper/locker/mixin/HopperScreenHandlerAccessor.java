package dev.worldgen.hopper.locker.mixin;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.HopperScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HopperScreenHandler.class)
public interface HopperScreenHandlerAccessor {
    @Accessor("inventory")
    Inventory getInventory();
}
