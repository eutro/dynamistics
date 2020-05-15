package client.jei.categories.pauto.processing;

import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import javax.annotation.Nonnull;

import static client.helper.JeiHelper.getFocusedStack;

public class RecipeHolderProcessingRecipe extends PackageRecipeProvider {

    private final int index;
    private final ItemStack stack;

    public RecipeHolderProcessingRecipe(Item item, int index) {
        this.stack = new ItemStack(item);
        this.index = index;
    }

    @Nonnull
    @Override
    public NBTTagCompound getPackageNBT(IGuiItemStackGroup group) {
        return getNBT(group, stack.getItem(), index);
    }

    public static NBTTagCompound getNBT(IGuiItemStackGroup group, Item item, int index) {
        ItemStack stack = getFocusedStack(item, group);
        if(stack != null && stack.hasTagCompound()) {
            NBTTagCompound tag = stack.getTagCompound();
            if(tag == null) return new NBTTagCompound();

            NBTTagList recipes = tag.getTagList("Recipes", 10);
            return recipes.getCompoundTagAt(index);
        }
        return new NBTTagCompound();
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
        super.getIngredients(ingredients);
        ingredients.setInput(VanillaTypes.ITEM, stack);
    }

    @Override
    public void drawInfo(@Nonnull Minecraft mc, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        super.drawInfo(mc, recipeWidth, recipeHeight, mouseX, mouseY);
        mc.fontRenderer.drawString(I18n.format("jeiautos.recipe.text.package_processing_extra", index + 1),
                24, 2, 0xFF000000);
    }

}
