package eutros.dynamistics.jei.categories.pauto.processing;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import javax.annotation.Nonnull;

public class RecipeHolderProcessingRecipe extends PackageRecipeProvider {

    private final int index;

    public RecipeHolderProcessingRecipe(ItemStack stack, int index) {
        super(stack);
        this.index = index;
    }

    @Nonnull
    @Override
    public NBTTagCompound getPackageNBT() {
        return getNBT(stack, index);
    }

    public static NBTTagCompound getNBT(ItemStack stack, int index) {
        if(stack != null && stack.hasTagCompound()) {
            NBTTagCompound tag = stack.getTagCompound();
            if(tag == null) return new NBTTagCompound();

            NBTTagList recipes = tag.getTagList("Recipes", 10);
            return recipes.getCompoundTagAt(index);
        }
        return new NBTTagCompound();
    }

    @Override
    public void drawInfo(@Nonnull Minecraft mc, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        super.drawInfo(mc, recipeWidth, recipeHeight, mouseX, mouseY);
        mc.fontRenderer.drawString(I18n.format("dynamistics.recipe.text.package_processing_extra", index + 1),
                24, 2, 0xFF000000);
    }

}
