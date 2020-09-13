package eutros.dynamistics.jei.categories.pauto;

import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public interface IWrapperSupplier<T extends IRecipeWrapper> extends IRecipeCategory<T> {

    @Nonnull
    List<T> makeWrappers(ItemStack stack);

    @Nonnull
    ItemStack getFallbackStack();

}
