package dev.worldgen.hopper.locker;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HopperLockerUtils {
    public static final Logger LOGGER = LoggerFactory.getLogger("hopper-locker");
    public static final Identifier DISABLED_SLOT_TEXTURE = new Identifier("container/crafter/disabled_slot");
    public static final Text TOGGLEABLE_SLOT_TEXT = Text.translatable("gui.togglable_slot");
    public static boolean validSlot(int slot) {
        return slot > -1 && slot < 5;
    }
}
