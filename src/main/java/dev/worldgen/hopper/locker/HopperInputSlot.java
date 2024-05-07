package dev.worldgen.hopper.locker;

import dev.worldgen.hopper.locker.access.HopperHandlerAccessor;
import dev.worldgen.hopper.locker.access.PropertyDelegateAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.slot.Slot;

public class HopperInputSlot extends Slot {
    private final HopperScreenHandler hopperHandler;

    public HopperInputSlot(Slot slot, HopperScreenHandler hopperHandler) {
        super(slot.inventory, slot.getIndex(), slot.x, slot.y);
        this.hopperHandler = hopperHandler;
    }
    @Override
    public boolean canInsert(ItemStack stack) {
        return !(((PropertyDelegateAccessor)this.hopperHandler).hopperLocker$getPropertyDelegate().get(this.id) == 1) && super.canInsert(stack);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        this.hopperHandler.onContentChanged(this.inventory);
    }

    public HopperHandlerAccessor getHandlerAccessor() {
        return (HopperHandlerAccessor)this.hopperHandler;
    }
}

