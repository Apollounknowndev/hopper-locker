package dev.worldgen.hopper.locker.mixin;

import dev.worldgen.hopper.locker.HopperInputSlot;
import dev.worldgen.hopper.locker.HopperLockerUtils;
import dev.worldgen.hopper.locker.access.HopperHandlerAccessor;
import dev.worldgen.hopper.locker.access.ScreenAccessor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HopperScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HopperScreen.class)
public abstract class HopperScreenMixin extends HandledScreen<HopperScreenHandler> implements ScreenAccessor {
    public HopperScreenMixin(HopperScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Unique
    private PlayerEntity player;

    @Override
    public PlayerEntity hopperLocker$getPlayer() {
        return this.player;
    }

    @Override
    public void hopperLocker$setSlotEnabled(int slotId, boolean enabled) {
        ((HopperHandlerAccessor)this.handler).hopperLocker$setSlot(slotId, enabled);
        super.onSlotChangedState(slotId, this.handler.syncId, enabled);
        float f = enabled ? 1.0f : 0.75f;
        this.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.4f, f);
    }

    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    private void hopperLocker$setPlayer(HopperScreenHandler handler, PlayerInventory inventory, Text title, CallbackInfo ci) {
        this.player = inventory.player;
    }

    @Inject(
        method = "render",
        at = @At("TAIL")
    )
    private void hopperLocker$showToggleableSlotTooltip(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.focusedSlot instanceof HopperInputSlot && !((HopperHandlerAccessor)this.handler).hopperLocker$isSlotDisabled(this.focusedSlot.id) && this.handler.getCursorStack().isEmpty() && !this.focusedSlot.hasStack() && !this.player.isSpectator()) {
            context.drawTooltip(this.textRenderer, HopperLockerUtils.TOGGLEABLE_SLOT_TEXT, mouseX, mouseY);
        }
    }
}