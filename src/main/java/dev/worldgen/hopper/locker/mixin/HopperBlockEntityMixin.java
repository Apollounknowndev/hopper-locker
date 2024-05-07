package dev.worldgen.hopper.locker.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.worldgen.hopper.locker.access.HopperHandlerAccessor;
import dev.worldgen.hopper.locker.access.PropertyDelegateAccessor;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.worldgen.hopper.locker.HopperLockerUtils.validSlot;

@Debug(export = true)
@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin extends BlockEntity implements HopperHandlerAccessor, PropertyDelegateAccessor {
	public HopperBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	/**
	 * Disabled slot field/methods
	 */

	@Unique
	protected PropertyDelegate propertyDelegate;

	@Override
	public PropertyDelegate hopperLocker$getPropertyDelegate() {
		return this.propertyDelegate;
	}

	@Override
	public void hopperLocker$setSlot(int slot, boolean enabled) {
		if (this.canToggleSlot(slot)) {
			this.propertyDelegate.set(slot, enabled ? 0 : 1);
			this.markDirty();
		}
	}

	@Override
	public boolean hopperLocker$isSlotDisabled(int slot) {
		if (validSlot(slot)) {
			return this.propertyDelegate.get(slot) == 1;
		} else {
			return false;
		}
	}

	@Unique
	private boolean canToggleSlot(int slot) {
		return validSlot(slot) && (((HopperBlockEntityAccessor)this).inventory().get(slot)).isEmpty();
	}

	/**
	 * Mixins
	 */

	@Inject(
		method = "<init>",
		at = @At("TAIL")
	)
	private void hopperLocker$setPropertyDelegator(BlockPos pos, BlockState state, CallbackInfo ci) {
		this.propertyDelegate = new ArrayPropertyDelegate(5);
	}

	@Inject(
		method = "setStack",
		at = @At("HEAD")
	)
	private void hopperLocker$enableSlotIfDisabled(int slot, ItemStack stack, CallbackInfo ci) {
		if (this.hopperLocker$isSlotDisabled(slot)) {
			this.hopperLocker$setSlot(slot, true);
		}
	}

	@Inject(
		method = "readNbt",
		at = @At("TAIL")
	)
	private void hopperLocker$readDisabledSlotsNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
		for(int i = 0; i < 5; ++i) {
			this.propertyDelegate.set(i, 0);
		}

		int[] disabledSlots = nbt.getIntArray("disabled_slots");

		for (int slot : disabledSlots) {
            if (this.canToggleSlot(slot)) {
                this.propertyDelegate.set(slot, 1);
            }
        }
	}

	@Inject(
		method = "writeNbt",
		at = @At("TAIL")
	)
	private void hopperLocker$writeDisabledSlotsNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
		IntList disabledSlots = new IntArrayList();

		for(int i = 0; i < 5; ++i) {
			if (this.hopperLocker$isSlotDisabled(i)) {
				disabledSlots.add(i);
			}
		}

		nbt.putIntArray("disabled_slots", disabledSlots);
	}

	@ModifyReturnValue(
		method = "createScreenHandler",
		at = @At("RETURN")
	)
	private ScreenHandler hopperLocker$transferPropertyDelegate(ScreenHandler screenHandler) {
		((PropertyDelegateAccessor)screenHandler).hopperLocker$setPropertyDelegate(this.propertyDelegate);

		return screenHandler;
	}
}