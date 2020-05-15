package eutros.jeiautos.helper;

import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.recipe.IFocus;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class JeiHelper {

    private static Logger LOGGER = LogManager.getLogger();

    @Nullable
    public static ItemStack getFocusedStack(Item target, @Nonnull IGuiItemStackGroup stacks) {
        ItemStack patternStack = null;
        try {
            Object focuz = FieldUtils.readField(stacks, "focus", true);
            if(focuz instanceof IFocus) {
                //noinspection unchecked
                patternStack = ((IFocus<ItemStack>) focuz).getValue();
                if(patternStack.getItem() != target) {
                    patternStack = null;
                }
            }
        } catch(IllegalAccessException | ClassCastException | IllegalArgumentException | SecurityException ignored) {
            LOGGER.debug("Failed to obtain focus through reflection");
        }
        return patternStack;
    }

}
