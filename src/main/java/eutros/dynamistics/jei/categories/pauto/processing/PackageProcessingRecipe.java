package eutros.dynamistics.jei.categories.pauto.processing;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public class PackageProcessingRecipe extends PackageRecipeProvider {

    public PackageProcessingRecipe(ItemStack stack) {
        super(stack);
    }

    @Nonnull
    @Override
    public NBTTagCompound getPackageNBT() {
        return stack.getTagCompound() == null ? new NBTTagCompound() : stack.getTagCompound();
    }

}
