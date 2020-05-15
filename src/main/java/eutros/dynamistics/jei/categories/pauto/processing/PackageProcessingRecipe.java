package eutros.dynamistics.jei.categories.pauto.processing;

import eutros.dynamistics.helper.JeiHelper;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.util.Optional;

public class PackageProcessingRecipe extends PackageRecipeProvider {

    private ItemStack stack;

    public PackageProcessingRecipe(Item item) {
        this.stack = new ItemStack(item);
    }

    @Nonnull
    @Override
    public NBTTagCompound getPackageNBT(IGuiItemStackGroup group) {
        return Optional.ofNullable(JeiHelper.getFocusedStack(stack.getItem(), group))
                .filter(ItemStack::hasTagCompound)
                .map(ItemStack::getTagCompound)
                .orElseGet(NBTTagCompound::new);
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
        super.getIngredients(ingredients);
        ingredients.setInput(VanillaTypes.ITEM, stack);
    }

}
