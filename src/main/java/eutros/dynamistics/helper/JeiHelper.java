package eutros.dynamistics.helper;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableStatic;
import net.minecraft.util.ResourceLocation;

public class JeiHelper {

    private static IDrawableStatic slotDrawable;

    public static void makeSlotDrawable(IGuiHelper guiHelper) {
        slotDrawable = guiHelper.drawableBuilder(new ResourceLocation(ModIds.SELF, "textures/gui/slot.png"), 0, 0, 18, 18)
                .setTextureSize(18, 18)
                .build();
    }

    public static IDrawableStatic getSlotDrawable() {
        return slotDrawable;
    }

}
