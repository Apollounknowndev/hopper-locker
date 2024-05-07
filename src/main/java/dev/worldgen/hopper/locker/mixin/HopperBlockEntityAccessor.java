package dev.worldgen.hopper.locker.mixin;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HopperBlockEntity.class)
public interface HopperBlockEntityAccessor {
    @Accessor("inventory")
    DefaultedList<ItemStack> inventory();
}
