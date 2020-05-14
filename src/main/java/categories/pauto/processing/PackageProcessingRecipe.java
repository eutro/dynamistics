package categories.pauto.processing;

import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

import java.util.Objects;

import static helper.JeiHelper.getFocusedStack;

public class PackageProcessingRecipe extends PackageRecipeProvider {

    private ItemStack stack;

    public PackageProcessingRecipe(Item item) {
        this.stack = new ItemStack(item);
    }

    @Nonnull
    @Override
    public NBTTagCompound getPackageNBT(IGuiItemStackGroup group) {
        ItemStack stack = getFocusedStack(this.stack.getItem(), group);
        if(stack != null && stack.hasTagCompound()) {
            return Objects.requireNonNull(stack.getTagCompound());
        }
        return new NBTTagCompound();
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
        super.getIngredients(ingredients);
        ingredients.setInput(VanillaTypes.ITEM, stack);
    }

}
